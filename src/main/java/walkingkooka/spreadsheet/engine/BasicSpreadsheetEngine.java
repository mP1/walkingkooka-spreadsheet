/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;

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
     * Uses the formatter to format the value, merging the style and returns an updated {@link TextNode}.
     */
    private TextNode formatAndApplyStyle0(final Object value,
                                                   final SpreadsheetTextFormatter<?> formatter, 
                                                   final TextStyle style,
                                                   final SpreadsheetEngineContext context) {
        return context.format(value, formatter)
                .map(f -> style.replace(f.toTextNode()))
                .orElse(EMPTY_TEXT_NODE);
    }

    private final static TextNode EMPTY_TEXT_NODE = TextNode.text("");

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
                final Optional<TextNode> formatted = cell.formatted();
                if (!formatted.isPresent()) {
                    throw new BasicSpreadsheetEngineException("Missing formatted cell=" + cell);
                }
                result = cell.setFormatted(
                        Optional.of(
                                rule.style()
                                        .apply(cell)
                                        .replace(formatted.get())));
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

        return cell.setFormatted(Optional.of(cell.style().replace(TextNode.text(error.get().value()))));
    }

    // SAVE CELL....................................................................................................

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.maybeParseAndEvaluateAndFormat(cell,
                    SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                    context);
            return this.delta(updated.refreshUpdated());
        }
    }

    // DELETE CELL....................................................................................................

    /**
     * DELETE the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta deleteCell(final SpreadsheetCellReference reference,
                                       final SpreadsheetEngineContext context) {
        checkReference(reference);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.cellStore.delete(reference);
            return this.delta(updated.refreshUpdated());
        }
    }

    // DELETE / INSERT / COLUMN / ROW ..................................................................................

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        checkColumn(column);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count, this, context)
                    .delete();
            return this.delta(updated.refreshUpdated());
        }
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        checkRow(row);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this, context)
                    .delete();
            return this.delta(updated.refreshUpdated());
        }
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        checkColumn(column);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.with(column.value(), count,
                    this,
                    context)
                    .insert();
            return this.delta(updated.refreshUpdated());
        }
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        checkRow(row);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(row.value(), count, this, context)
                    .insert();
            return this.delta(updated.refreshUpdated());
        }
    }

    private static void checkCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public SpreadsheetDelta copyCells(final Collection<SpreadsheetCell> from,
                                      final SpreadsheetRange to,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineCopyCells.execute(from, to, this, context);
            return this.delta(updated.refreshUpdated());
        }
    }

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        checkMapping(mapping);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.labelStore.save(mapping);
            return this.delta(updated.refreshUpdated());
        }
    }

    @Override
    public SpreadsheetDelta removeLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        checkLabel(label);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.labelStore.delete(label);
            return this.delta(updated.refreshUpdated());
        }
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name) {
        return this.labelStore.load(name);
    }

    private SpreadsheetDelta delta(final Set<SpreadsheetCell> cells) {
        return SpreadsheetDelta.with(this.id(), cells);
    }

    private static void checkLabel(final SpreadsheetLabelName name) {
        Objects.requireNonNull(name, "name");
    }

    private static void checkMapping(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");
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
