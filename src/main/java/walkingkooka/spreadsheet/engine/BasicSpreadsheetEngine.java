package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default or basic implementation of {@link SpreadsheetEngine} that includes support for evaluating nodes,
 * when they are refreshed and not when they are set.
 */
final class BasicSpreadsheetEngine implements SpreadsheetEngine{

    /**
     * Factory that creates a new {@link BasicSpreadsheetEngine}
     */
    static BasicSpreadsheetEngine with(final SpreadsheetId id,
                                   final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                   final SpreadsheetParserContext parserContext,
                                   final ExpressionEvaluationContext evaluationContext) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(parserContext, "parserContext");
        Objects.requireNonNull(evaluationContext, "evaluationContext");

        return new BasicSpreadsheetEngine(id, parser, parserContext, evaluationContext);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                   final SpreadsheetParserContext parserContext,
                                   final ExpressionEvaluationContext evaluationContext) {
        this.id = id;
        this.parser = parser;
        this.parserContext = parserContext;
        this.evaluationContext = evaluationContext;
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    private SpreadsheetId id;

    @Override
    public Set<SpreadsheetCell> load(final Set<SpreadsheetCellReference> references, final SpreadsheetEngineLoading loading) {
        Objects.requireNonNull(references, "references");
        Objects.requireNonNull(loading, "loading");

        final Set<SpreadsheetCellReference> copy = Sets.ordered();
        copy.addAll(references);
        if(copy.isEmpty()) {
            throw new IllegalArgumentException("References must not be empty");
        }

        return copy.stream()
                .map(r -> this.cells.get(r))
                .filter(c -> null != c)
                .map(c -> this.parseAndEvaluate(c, loading))
                .collect(Collectors.toCollection(Sets::sorted));
    }

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary.
     */
    private SpreadsheetCell parseAndEvaluate(final SpreadsheetCell cell, final SpreadsheetEngineLoading loading) {
        final SpreadsheetCell reply = this.evaluateIfPossible(this.parseIfNecessary(loading.prepare(cell)));
        this.cells.put(reply.reference(), reply); // update cells enabling caching of parsing and value and errors.
        return reply;
    }

    /**
     * If an expression is not present, parse the formula.
     */
    private SpreadsheetCell parseIfNecessary(final SpreadsheetCell cell) {
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
    private SpreadsheetCell evaluateIfPossible(final SpreadsheetCell cell) {
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
     * Accepts a potentially updated cell.
     */
    @Override
    public void set(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");
        this.cells.put(cell.reference(), cell);
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
     * All cells present in this spreadsheet
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    /**
     * The context used to evaluate {@link ExpressionNode} for each {@link SpreadsheetCell}.
     */
    private final ExpressionEvaluationContext evaluationContext;

    @Override
    public String toString() {
        return this.cells.toString();
    }
}
