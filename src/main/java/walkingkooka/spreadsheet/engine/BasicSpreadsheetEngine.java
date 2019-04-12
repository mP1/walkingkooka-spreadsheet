package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormattedCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.text.spreadsheetformat.SpreadsheetFormattedText;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatter;
import walkingkooka.tree.expression.ExpressionEvaluationException;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
                                       final SpreadsheetLabelStore labelStore,
                                       final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules,
                                       final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore,
                                       final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore,
                                       final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(conditionalFormattingRules, "conditionalFormattingRules");
        Objects.requireNonNull(cellReferencesStore, "cellReferencesStore");
        Objects.requireNonNull(labelReferencesStore, "labelReferencesStore");
        Objects.requireNonNull(rangeToCellStore, "rangeToCellStore");

        return new BasicSpreadsheetEngine(id,
                cellStore,
                labelStore,
                conditionalFormattingRules,
                cellReferencesStore,
                labelReferencesStore,
                rangeToCellStore);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final SpreadsheetCellStore cellStore,
                                   final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules,
                                   final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore,
                                   final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore,
                                   final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        this.id = id;
        this.cellStore = cellStore;
        this.labelStore = labelStore;
        this.conditionalFormattingRules = conditionalFormattingRules;
        this.cellReferencesStore = cellReferencesStore;
        this.labelReferencesStore = labelReferencesStore;
        this.rangeToCellStore = rangeToCellStore;
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    private final SpreadsheetId id;

    // LOAD CELL, SAVE CELL..........................................................................................

    /**
     * Loads the requested cell, which may also involve re-evaluating the formula.
     */
    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference reference,
                                              final SpreadsheetEngineEvaluation evaluation,
                                              final SpreadsheetEngineContext context) {
        checkReference(reference);
        Objects.requireNonNull(evaluation, "evaluation");
        checkContext(context);

        final Optional<SpreadsheetCell> cell = this.cellStore.load(reference);
        return cell.map(c -> this.maybeParseAndEvaluateAndFormat(c, evaluation, context));
    }

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineEvaluation}
     */
    SpreadsheetCell maybeParseAndEvaluateAndFormat(final SpreadsheetCell cell,
                                                   final SpreadsheetEngineEvaluation evaluation,
                                                   final SpreadsheetEngineContext context) {
        final SpreadsheetCell result = evaluation.formulaEvaluateAndStyle(cell, this, context);
        this.cellStore.save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    final SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                  final SpreadsheetEngineContext context) {
        return this.formatAndApplyStyle(
                cell.setFormula(this.parseFormulaAndEvaluate(cell.formula(), context)),
                context);
    }

    private SpreadsheetFormula parseFormulaAndEvaluate(final SpreadsheetFormula formula,
                                                       final SpreadsheetEngineContext context) {
        return this.evaluateIfPossible(this.parseIfNecessary(formula, context), context);
    }

    // PARSE .........................................................................................................

    /**
     * If an expression is not present, parse the formula.
     */
    private SpreadsheetFormula parseIfNecessary(final SpreadsheetFormula formula,
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

    // EVAL .........................................................................................................

    /**
     * If a value is available try and re-use or if an expression is present evaluate it.
     */
    private SpreadsheetFormula evaluateIfPossible(final SpreadsheetFormula formula,
                                                  final SpreadsheetEngineContext context) {
        return formula.error().isPresent() ?
                formula : // value present - using cached.
                this.evaluate(formula, context);
    }

    private SpreadsheetFormula evaluate(final SpreadsheetFormula formula,
                                        final SpreadsheetEngineContext context) {
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

    // ERROR HANDLING..............................................................................................

    /**
     * Sets the error upon the formula.
     */
    private SpreadsheetFormula setError(final SpreadsheetFormula formula,
                                        final String message) {
        return formula.setError(Optional.of(SpreadsheetError.with(message)));
    }

    // FORMAT .........................................................................................................

    /**
     * If a value is present use the pattern to format and apply the styling.
     */
    private SpreadsheetCell formatAndApplyStyle(final SpreadsheetCell cell,
                                                final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;

        SpreadsheetTextFormatter<?> formatter = context.defaultSpreadsheetTextFormatter();
        final Optional<SpreadsheetCellFormat> maybeFormat = cell.format();
        if (maybeFormat.isPresent()) {
            final SpreadsheetCellFormat format = this.parseFormatPatternIfNecessary(maybeFormat.get(), context);
            result = cell.setFormat(Optional.of(format));
            final Optional<SpreadsheetTextFormatter<?>> maybeFormatter = format.formatter();
            if (!maybeFormatter.isPresent()) {
                throw new SpreadsheetEngineException("Failed to make " + SpreadsheetTextFormatter.class.getSimpleName() + " from " + format);
            }
            formatter = format.formatter().get();
        }

        final SpreadsheetFormula formula = cell.formula();
        final Optional<Object> value = formula.value();
        final SpreadsheetCell beforeConditionalRules = value.isPresent() ?
                result.setFormatted(Optional.of(this.formatAndApplyStyle0(value.get(), formatter, result.style(), context))) :
                this.formatAndApplyStyleValueAbsent(result);

        return this.locateAndApplyConditionalFormattingRule(beforeConditionalRules, context);
    }

    /**
     * Returns a {@link SpreadsheetCellFormat} parsing the pattern if necessary.
     */
    private SpreadsheetCellFormat parseFormatPatternIfNecessary(final SpreadsheetCellFormat format,
                                                                final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetTextFormatter<?>> formatter = format.formatter();
        return formatter.isPresent() ?
                format :
                this.parseFormatPattern(format, context);
    }

    /**
     * Returns an updated {@link SpreadsheetCellFormat} after parsing the pattern into a {@link SpreadsheetTextFormatter}.
     */
    private SpreadsheetCellFormat parseFormatPattern(final SpreadsheetCellFormat format,
                                                     final SpreadsheetEngineContext context) {
        return format.setFormatter(Optional.of(context.parseFormatPattern(format.pattern())));
    }

    /**
     * Uses the formatter to format the value, merging the style and returns an updated {@link SpreadsheetFormattedCell}.
     */
    private SpreadsheetFormattedCell formatAndApplyStyle0(final Object value,
                                                          final SpreadsheetTextFormatter<?> formatter,
                                                          final SpreadsheetCellStyle style,
                                                          final SpreadsheetEngineContext context) {
        String text = "";
        Optional<Color> color = SpreadsheetFormattedText.WITHOUT_COLOR;

        final Optional<SpreadsheetFormattedText> maybeFormattedText = context.format(value, formatter);
        if (maybeFormattedText.isPresent()) {
            final SpreadsheetFormattedText formattedText = maybeFormattedText.get();
            text = formattedText.text();
            color = formattedText.color();
        }
        SpreadsheetFormattedCell formattedCell = style.setCellFormattedText(text);

        if (color.isPresent()) {
            formattedCell.setTextColor(color.get());
        }

        return formattedCell;
    }

    /**
     * Locates and returns the first matching conditional rule style.
     */
    private SpreadsheetCell locateAndApplyConditionalFormattingRule(final SpreadsheetCell cell,
                                                                    final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;

        final Set<SpreadsheetConditionalFormattingRule> rules = Sets.sorted(SpreadsheetConditionalFormattingRule.PRIORITY_COMPARATOR);
        rules.addAll(this.conditionalFormattingRules.loadCellReferenceValues(cell.reference()));
        for (SpreadsheetConditionalFormattingRule rule : rules) {
            final Object test = context.evaluate(rule.formula().expression().get());
            final Boolean booleanResult = context.convert(test, Boolean.class);
            if (Boolean.TRUE.equals(booleanResult)) {
                final Optional<SpreadsheetFormattedCell> formatted = cell.formatted();
                if (!formatted.isPresent()) {
                    throw new BasicSpreadsheetEngineException("Missing formatted cell=" + cell);
                }

                result = cell.setFormatted(Optional.of(formatted.get().setStyle(rule.style().apply(cell))));
                break;
            }
        }
        return result;
    }

    /**
     * Provides the conditional format rules for each cell.
     */
    private final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules;

    // FORMAT ERROR ....................................................................................................

    /**
     * Handles apply style to the error if present or defaulting to empty {@link String}.
     * The error becomes the text and no formatting or color is applied.
     */
    private SpreadsheetCell formatAndApplyStyleValueAbsent(final SpreadsheetCell cell) {
        final Optional<SpreadsheetError> error = cell.formula().error();

        return cell.setFormatted(Optional.of(cell.style().setCellFormattedText(error.isPresent() ?
                error.get().value() :
                "")));
    }

    // SAVE CELL....................................................................................................

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public Set<SpreadsheetCell> saveCell(final SpreadsheetCell cell,
                                         final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.maybeParseAndEvaluateAndFormat(cell,
                    SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                    context);
            return updated.refreshUpdated();
        }
    }

    // DELETE CELL....................................................................................................

    /**
     * DELETE the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public Set<SpreadsheetCell> deleteCell(final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineContext context) {
        checkReference(reference);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.cellStore.delete(reference);
            return updated.refreshUpdated();
        }
    }

    // DELETE / INSERT / COLUMN / ROW ..................................................................................

    @Override
    public Set<SpreadsheetCell> deleteColumns(final SpreadsheetColumnReference column,
                                              final int count,
                                              final SpreadsheetEngineContext context) {
        checkColumn(column);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count, this, context)
                    .delete();
            return updated.refreshUpdated();
        }
    }

    @Override
    public Set<SpreadsheetCell> deleteRows(final SpreadsheetRowReference row,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
        checkRow(row);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this, context)
                    .delete();
            return updated.refreshUpdated();
        }
    }

    @Override
    public Set<SpreadsheetCell> insertColumns(final SpreadsheetColumnReference column,
                                              final int count,
                                              final SpreadsheetEngineContext context) {
        checkColumn(column);
        checkCount(count);
        checkContext(context);

        if (count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count,
                    this,
                    context)
                    .insert();
        }
        return Sets.empty();
    }

    @Override
    public Set<SpreadsheetCell> insertRows(final SpreadsheetRowReference row,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
        checkRow(row);
        checkCount(count);
        checkContext(context);

        if (count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this, context)
                    .insert();
        }
        return Sets.empty();
    }

    private static void checkCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public Set<SpreadsheetCell> copyCells(final Collection<SpreadsheetCell> from,
                                          final SpreadsheetRange to,
                                          final SpreadsheetEngineContext context) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineCopyCells.execute(from, to, this, context);
            return updated.refreshUpdated();
        }
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private static void checkColumn(SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    private static void checkRow(SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    private static void checkContext(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");
    }

    @Override
    public String toString() {
        return this.cellStore.toString();
    }

    final SpreadsheetCellStore cellStore;
    final SpreadsheetLabelStore labelStore;

    /**
     * Tracks all references to a single cell.
     */
    final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore;
    final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore;

    /**
     * Used to track ranges to cells references.
     */
    final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore;
}
