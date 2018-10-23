package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionEvaluationException;

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
final class BasicSpreadsheetEngine implements SpreadsheetEngine {

    /**
     * Factory that creates a new {@link BasicSpreadsheetEngine}
     */
    static BasicSpreadsheetEngine with(final SpreadsheetId id,
                                       final SpreadsheetCellStore cellStore,
                                       final SpreadsheetLabelStore labelStore) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(labelStore, "labelStore");

        return new BasicSpreadsheetEngine(id, cellStore, labelStore);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final SpreadsheetCellStore cellStore,
                                   final SpreadsheetLabelStore labelStore) {
        this.id = id;
        this.cellStore = cellStore;
        this.labelStore = labelStore;
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    private SpreadsheetId id;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference reference,
                                              final SpreadsheetEngineLoading loading,
                                              final SpreadsheetEngineContext context) {
        Objects.requireNonNull(reference, "references");
        Objects.requireNonNull(loading, "loading");
        Objects.requireNonNull(context, "context");

        final Optional<SpreadsheetCell> cell = this.cellStore.load(reference);
        return cell.map(c -> this.maybeParseAndEvaluate(c, loading, context));
    }

    final SpreadsheetCellStore cellStore;

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineLoading}
     */
    private SpreadsheetCell maybeParseAndEvaluate(final SpreadsheetCell cell,
                                                  final SpreadsheetEngineLoading loading,
                                                  final SpreadsheetEngineContext context) {
        final SpreadsheetCell result = cell.setFormula(loading.process(cell.formula(), this, context));
        this.cellStore.save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    /**
     * If an expression is not present, parse the formula.
     */
    SpreadsheetFormula parseIfNecessary(final SpreadsheetFormula formula,
                                        final SpreadsheetEngineContext context) {
        return formula.expression().isPresent() ?
                formula :
                this.parse(formula, Function.identity(), context);
    }

    /**
     * Parsers the formula for this cell, and sets its expression or error if parsing fails.
     */
    final SpreadsheetFormula parse(final SpreadsheetFormula formula,
                                   final Function<SpreadsheetParserToken, SpreadsheetParserToken> parsed,
                                   final SpreadsheetEngineContext context) {
        SpreadsheetFormula result;

        try {
            final SpreadsheetParserToken updated = parsed.apply(context.parseFormula(formula.text()));
            result = formula.setText(updated.text())
                    .setExpression(updated.expressionNode());
        } catch (final ParserException failed) {
            // parsing failed set the error message
            result = this.setError(formula, failed.getMessage());
        }

        return result;
    }

    /**
     * If a value is available try and re-use or if an expression is present evaluate it.
     */
    final SpreadsheetFormula evaluateIfPossible(final SpreadsheetFormula formula, final SpreadsheetEngineContext context) {
        return formula.error().isPresent() ?
                formula : // value present - using cached.
                this.evaluate(formula, context);
    }

    private SpreadsheetFormula evaluate(final SpreadsheetFormula formula, final SpreadsheetEngineContext context) {
        SpreadsheetFormula result;
        try {
            result = formula.setValue(Optional.of(context.evaluate(formula.expression().get())));
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
    public void deleteColumns(final SpreadsheetColumnReference column,
                              final int count,
                              final SpreadsheetEngineContext context) {
        Objects.requireNonNull(column, "column");
        checkCount(count);
        checkContext(context);

        if (count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count, this, context)
                    .delete();
        }
    }

    @Override
    public void deleteRows(final SpreadsheetRowReference row,
                           final int count,
                           final SpreadsheetEngineContext context) {
        Objects.requireNonNull(row, "row");
        checkCount(count);
        checkContext(context);

        if (count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this, context)
                    .delete();
        }
    }

    @Override
    public void insertColumns(final SpreadsheetColumnReference column,
                              final int count,
                              final SpreadsheetEngineContext context) {
        Objects.requireNonNull(column, "column");
        checkCount(count);
        checkContext(context);

        if (count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count,
                    this,
                    context)
                    .insert();
        }
    }

    @Override
    public void insertRows(final SpreadsheetRowReference row,
                           final int count,
                           final SpreadsheetEngineContext context) {
        Objects.requireNonNull(row, "row");
        checkCount(count);
        checkContext(context);

        if (count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this, context)
                    .insert();
        }
    }

    private static void checkCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public void copy(final Collection<SpreadsheetCell> from,
                     final SpreadsheetRange to,
                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        checkContext(context);

        if (!from.isEmpty()) {
            this.copy0(from, to, context);
        }
    }

    private void copy0(final Collection<SpreadsheetCell> from,
                       final SpreadsheetRange to,
                       final SpreadsheetEngineContext context) {
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

        for (int h = 0; h < heightMultiple; h++) {
            final int y = yOffset + h * fromHeight;

            for (int w = 0; w < widthMultiple; w++) {
                final int x = xOffset + w * fromWidth;
                from.stream()
                        .forEach(c -> this.copyCell(c, x, y, context));
            }
        }
    }

    /**
     * Fixes any relative references within the formula belonging to the cell's expression. Absolute references are
     * ignored and left unmodified.
     */
    private void copyCell(final SpreadsheetCell cell,
                          final int xOffset,
                          final int yOffset,
                          final SpreadsheetEngineContext context) {
        final SpreadsheetCell updatedReference = cell.setReference(cell.reference().add(xOffset, yOffset));
        final SpreadsheetFormula formula = updatedReference.formula();

        final SpreadsheetCell save = updatedReference.setFormula(this.parse(formula,
                token -> BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
                        xOffset,
                        yOffset),
                context));
        this.cellStore.save(save);
    }

    final SpreadsheetLabelStore labelStore;

    private static void checkContext(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");
    }

    @Override
    public String toString() {
        return this.cellStore.toString();
    }
}
