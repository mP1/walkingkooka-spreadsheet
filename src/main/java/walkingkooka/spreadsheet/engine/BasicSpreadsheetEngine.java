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
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
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
                                       final SpreadsheetLabelStore labelStore,
                                       final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(conditionalFormattingRules, "conditionalFormattingRules");

        return new BasicSpreadsheetEngine(id, cellStore, labelStore, conditionalFormattingRules);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final SpreadsheetCellStore cellStore,
                                   final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules) {
        this.id = id;
        this.cellStore = cellStore;
        this.labelStore = labelStore;
        this.conditionalFormattingRules = conditionalFormattingRules;
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
        return cell.map(c -> this.maybeParseAndEvaluateAndFormat(c, loading, context));
    }

    final SpreadsheetCellStore cellStore;

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineLoading}
     */
    private SpreadsheetCell maybeParseAndEvaluateAndFormat(final SpreadsheetCell cell,
                                                           final SpreadsheetEngineLoading loading,
                                                           final SpreadsheetEngineContext context) {
        final SpreadsheetCell result = loading.formulaEvaluateAndStyle(cell, this, context);
        this.cellStore.save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    final SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                  final SpreadsheetEngineContext context) {
        return this.formatAndApplyStyle(
                cell.setFormula(this.parseFormulaAndEvaluate(cell.formula(), context)),
                context);
    }

    private SpreadsheetFormula parseFormulaAndEvaluate(final SpreadsheetFormula formula, final SpreadsheetEngineContext context) {
        return this.evaluateIfPossible(this.parseIfNecessary(formula, context), context);
    }

    // PARSE .........................................................................................................

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

    // EVAL .........................................................................................................

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

    // ERROR HANDLING..............................................................................................

    /**
     * Sets the error upon the formula.
     */
    private SpreadsheetFormula setError(final SpreadsheetFormula formula, final String message) {
        return formula.setError(Optional.of(SpreadsheetError.with(message)));
    }

    // FORMAT .........................................................................................................

    /**
     * If a value is present use the pattern to format and apply the styling.
     */
    private SpreadsheetCell formatAndApplyStyle(final SpreadsheetCell cell, final SpreadsheetEngineContext context) {
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
                this.formatAndApplyStyleValueAbsent(result, context);

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
        rules.addAll(this.conditionalFormattingRules.loadCellReference(cell.reference()));
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
    private SpreadsheetCell formatAndApplyStyleValueAbsent(final SpreadsheetCell cell,
                                                           final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetError> error = cell.formula().error();

        return cell.setFormatted(Optional.of(cell.style().setCellFormattedText(error.isPresent() ?
                error.get().value() :
                "")));
    }

    // DELETE / INSERT / COLUMN / ROW ..................................................................................

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
