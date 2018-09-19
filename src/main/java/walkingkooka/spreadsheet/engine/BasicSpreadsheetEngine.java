package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
                                       final SpreadsheetCellStore cellStore,
                                       final SpreadsheetLabelStore labelStore,
                                       final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                       final SpreadsheetParserContext parserContext,
                                       final Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(parserContext, "parserContext");
        Objects.requireNonNull(evaluationContextFactory, "evaluationContextFactory");

        return new BasicSpreadsheetEngine(id,
                cellStore,
                labelStore,
                parser,
                parserContext,
                evaluationContextFactory);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final SpreadsheetCellStore cellStore,
                                   final SpreadsheetLabelStore labelStore,
                                   final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                   final SpreadsheetParserContext parserContext,
                                   final Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory) {
        this.id = id;
        this.cellStore = cellStore;
        this.labelStore = labelStore;
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

    final SpreadsheetCellStore cellStore;

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineLoading}
     */
    private SpreadsheetCell maybeParseAndEvaluate(final SpreadsheetCell cell, final SpreadsheetEngineLoading loading) {
        final SpreadsheetCell result = cell.setFormula(loading.process(cell.formula(), this));
        this.cellStore.save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    /**
     * If an expression is not present, parse the formula.
     */
    SpreadsheetFormula parseIfNecessary(final SpreadsheetFormula formula) {
        return formula.expression().isPresent() ?
               formula :
               this.parse(formula, Function.identity());
    }

    /**
     * Parsers the formula for this cell, and sets its expression or error if parsing fails.
     */
    final SpreadsheetFormula parse(final SpreadsheetFormula formula,
                                   final Function<SpreadsheetParserToken, SpreadsheetParserToken> parsed) {
        SpreadsheetFormula result = formula;

        try {
            final TextCursor text = TextCursors.charSequence(formula.text());
            final Optional<SpreadsheetParserToken> token = this.parser.parse(text, this.parserContext);
            if(token.isPresent()) {
                final SpreadsheetParserToken updatedToken = parsed.apply(token.get());
                result = result.setText(updatedToken.text())
                        .setExpression(updatedToken.expressionNode());
            } else {
                // generic error message.
                final TextCursorSavePoint save = text.save();
                text.end();

                result = this.setError(formula, "Unable to parse entire expression=" + CharSequences.quoteAndEscape(save.textBetween()));
            }
        } catch (final ParserException failed) {
            // parsing failed set the error message
            result = this.setError(formula, failed.getMessage());
        }

        return result;
    }

    /**
     * If a value is available try and re-use or if an expression is present evaluate it.
     */
    final SpreadsheetFormula evaluateIfPossible(final SpreadsheetFormula formula) {
        return formula.value().isPresent() || formula.error().isPresent() ?
               formula : // value present - using cached.
               this.evaluate(formula);
    }

    private SpreadsheetFormula evaluate(final SpreadsheetFormula formula) {
        SpreadsheetFormula result;
        try {
            result = formula.setValue(Optional.of(formula.expression().get().toValue(this.evaluationContext)));
        } catch (final ExpressionEvaluationException cause) {
            result = this.setError(formula, cause.getMessage());
        } catch (final NoSuchElementException cause) {
            throw new BasicSpreadsheetEngineException("Cell missing value and error and expression: " + cause.getMessage(), cause);
        }
        return result;
    }

    /**
     * Sets the error upon the formula.
     */
    private SpreadsheetFormula setError(final SpreadsheetFormula formula, final String message) {
        return formula.setError(Optional.of(SpreadsheetError.with(message)));
    }

    @Override
    public void deleteColumns(final SpreadsheetColumnReference column, final int count) {
        Objects.requireNonNull(column, "column");
        checkCount(count);

        if(count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count, this)
                    .delete();
        }
    }

    @Override
    public void deleteRows(final SpreadsheetRowReference row, final int count) {
        Objects.requireNonNull(row, "row");
        checkCount(count);

        if(count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this)
                    .delete();
        }
    }

    @Override
    public void insertColumns(final SpreadsheetColumnReference column, final int count) {
        Objects.requireNonNull(column, "column");
        checkCount(count);

        if(count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count, this)
                    .insert();
        }
    }

    @Override
    public void insertRows(final SpreadsheetRowReference row, final int count) {
        Objects.requireNonNull(row, "row");
        checkCount(count);

        if(count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this)
                    .insert();
        }
    }

    private static void checkCount(final int count) {
        if(count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public void copy(final Collection<SpreadsheetCell> from, final SpreadsheetRange to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        if(!from.isEmpty()) {
            this.copy0(from, to);
        }
    }

    private void copy0(final Collection<SpreadsheetCell> from, final SpreadsheetRange to) {
        final SpreadsheetRange fromRange = SpreadsheetRange.from(from.stream()
                .map(c -> c.reference())
                .collect(Collectors.toList()));

        final int fromWidth = fromRange.width();
        final int fromHeight = fromRange.height();

        final int toWidth = to.width();
        final int toHeight = to.height();

        final int widthMultiple = fromWidth >= toWidth ?
                1 :
                toWidth / fromWidth;
        final int heightMultiple = fromHeight >= toHeight ?
                1 :
                toHeight / fromHeight;

        final SpreadsheetCellReference fromBegin = fromRange.begin();
        final SpreadsheetCellReference toBegin = to.begin();

        final int xOffset = toBegin.column().value() - fromBegin.column().value();
        final int yOffset = toBegin.row().value() - fromBegin.row().value();

        for(int h = 0; h < heightMultiple; h++) {
            final int y = yOffset + h * fromHeight;

            for(int w = 0; w < widthMultiple; w++) {
                final int x = xOffset + w * fromWidth;
                from.stream()
                        .forEach(c -> this.copyCell(c, x, y));
            }
        }
    }

    /**
     * Fixes any relative references within the formula belonging to the cell's expression. Absolute references are
     * ignored and left unmodified.
     */
    private void copyCell(final SpreadsheetCell cell, final int xOffset, final int yOffset) {
        final SpreadsheetCell updatedReference = cell.setReference(cell.reference().add(xOffset, yOffset));
        final SpreadsheetFormula formula = updatedReference.formula();

        final SpreadsheetCell save = updatedReference.setFormula(this.parse(formula,
                token-> BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
                        xOffset,
                        yOffset)
        ));
        this.cellStore.save(save);
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

    final SpreadsheetLabelStore labelStore;

    @Override
    public String toString() {
        return this.cellStore.toString();
    }
}
