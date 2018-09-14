package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The default or basic implementation of {@link SpreadsheetEngine} that includes support for evaluating nodes,
 * when they are refreshed and not when they are set.
 */
final class BasicSpreadsheetEngine implements SpreadsheetEngine{

    /**
     * Factory that creates a new {@link BasicSpreadsheetEngine}
     */
    static BasicSpreadsheetEngine with(final SpreadsheetId id,
                                       final SpreadsheetCellStore cellStore,
                                       final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                       final SpreadsheetParserContext parserContext,
                                       final Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(parserContext, "parserContext");
        Objects.requireNonNull(evaluationContextFactory, "evaluationContextFactory");

        return new BasicSpreadsheetEngine(id, cellStore, parser, parserContext, evaluationContextFactory);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final SpreadsheetCellStore cellStore,
                                   final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                   final SpreadsheetParserContext parserContext,
                                   final Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory) {
        this.id = id;
        this.cellStore = cellStore;
        this.parser = parser;
        this.parserContext = parserContext;
        this.evaluationContext = evaluationContextFactory.apply(this);
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    private SpreadsheetId id;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading) {
        Objects.requireNonNull(reference, "references");
        Objects.requireNonNull(loading, "loading");

        final Optional<SpreadsheetCell> cell = this.cellStore.load(reference);
        return cell.map(c -> this.maybeParseAndEvaluate(c, loading));
    }

    private final SpreadsheetCellStore cellStore;

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineLoading}
     */
    private SpreadsheetCell maybeParseAndEvaluate(final SpreadsheetCell cell, final SpreadsheetEngineLoading loading) {
        final SpreadsheetCell result = loading.process(cell, this);
        this.cellStore.save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    /**
     * If an expression is not present, parse the formula.
     */
    final SpreadsheetCell parseIfNecessary(final SpreadsheetCell cell) {
        return cell.expression().isPresent() ?
               cell :
               this.parse(cell);
    }

    /**
     * Parsers the formula for this cell, and sets its expression or error if parsing fails.
     */
    private SpreadsheetCell parse(final SpreadsheetCell cell) {
        SpreadsheetCell result = cell;

        try {
            final TextCursor formula = TextCursors.charSequence(cell.formula().value());
            final Optional<SpreadsheetParserToken> token = this.parser.parse(formula, this.parserContext);
            if(token.isPresent()) {
                result = result.setExpression(token.get().expressionNode());
            } else {
                // generic error message.
                final TextCursorSavePoint save = formula.save();
                formula.end();

                result = this.setError(cell, "Unable to parse entire expression=" + CharSequences.quoteAndEscape(save.textBetween()));
            }
        } catch (final ParserException failed) {
            // parsing failed set the error message
            result = this.setError(cell, failed.getMessage());
        }

        return result;
    }

    /**
     * If a value is available try and re-use or if an expression is present evaluate it.
     */
    final SpreadsheetCell evaluateIfPossible(final SpreadsheetCell cell) {
        return cell.value().isPresent() || cell.error().isPresent() ?
               cell : // value present - using cached.
               this.evaluate(cell);
    }

    private SpreadsheetCell evaluate(final SpreadsheetCell cell) {
        SpreadsheetCell result;
        try {
            result = cell.setValue(Optional.of(cell.expression().get().toValue(this.evaluationContext)));
        } catch (final ExpressionEvaluationException cause) {
            result = this.setError(cell, cause.getMessage());
        } catch (final NoSuchElementException cause) {
            throw new BasicSpreadsheetEngineException("Cell missing value and error and expression: " + cause.getMessage(), cause);
        }
        return result;
    }

    /**
     * Sets the error upon the cell.
     */
    private SpreadsheetCell setError(final SpreadsheetCell cell, final String message) {
        return cell.setError(Optional.of(SpreadsheetError.with(message)));
    }

    /**
     * The {@link Parser} that turns {@link SpreadsheetFormula} into a {@link ExpressionNode}.
     */
    private final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser;

    /**
     * Used during the parsing of expressions.
     */
    private final SpreadsheetParserContext parserContext;

    /**
     * The context used to evaluate {@link ExpressionNode} for each {@link SpreadsheetCell}.
     */
    private final ExpressionEvaluationContext evaluationContext;

    /**
     * Sets or replaces a label to reference mapping. It is acceptable to set to an non existing {@link SpreadsheetCellReference}
     */
    @Override
    public void setLabel(final SpreadsheetLabelName label, final SpreadsheetCellReference reference) {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(reference, "reference");

        this.labelToReference.put(label, reference);
    }

    /**
     * Removes the given label if it exists. Invalid or unknown names are ignored.
     */
    @Override
    public void removeLabel(SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        this.labelToReference.remove(label);
    }

    /**
     * Retrieves the {@link SpreadsheetCellReference} if one is present for the given {@link SpreadsheetLabelName}
     */
    @Override
    public Optional<SpreadsheetCellReference> label(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
        return Optional.ofNullable(this.labelToReference.get(label));
    }

    private final Map<SpreadsheetLabelName, SpreadsheetCellReference> labelToReference = Maps.sorted();

    @Override
    public String toString() {
        return this.cellStore.toString();
    }
}
