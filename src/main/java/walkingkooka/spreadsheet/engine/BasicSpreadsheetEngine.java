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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;

import java.util.Collection;
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
                                       final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore,
                                       final SpreadsheetLabelStore labelStore,
                                       final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore,
                                       final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore,
                                       final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRuleStore) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(cellReferencesStore, "cellReferencesStore");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(labelReferencesStore, "labelReferencesStore");
        Objects.requireNonNull(rangeToCellStore, "rangeToCellStore");
        Objects.requireNonNull(rangeToConditionalFormattingRuleStore, "rangeToConditionalFormattingRuleStore");

        return new BasicSpreadsheetEngine(id,
                cellStore,
                cellReferencesStore,
                labelStore,
                labelReferencesStore,
                rangeToCellStore,
                rangeToConditionalFormattingRuleStore);
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetId id,
                                   final SpreadsheetCellStore cellStore,
                                   final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore,
                                   final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore,
                                   final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore,
                                   final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRuleStore) {
        this.id = id;
        this.cellStore = cellStore;
        this.cellReferencesStore = cellReferencesStore;
        this.labelStore = labelStore;
        this.labelReferencesStore = labelReferencesStore;
        this.rangeToCellStore = rangeToCellStore;
        this.rangeToConditionalFormattingRuleStore = rangeToConditionalFormattingRuleStore;
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    private final SpreadsheetId id;

    // LOAD CELL........................................................................................................

    /**
     * Loads the cell honouring the {@link SpreadsheetEngineEvaluation} which may result in loading and evaluating other cells.
     */
    @Override
    public SpreadsheetDelta loadCell(final SpreadsheetCellReference reference,
                                     final SpreadsheetEngineEvaluation evaluation,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(evaluation, "evaluation");
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            final Optional<SpreadsheetCell> loaded = this.cellStore.load(reference);
            loaded.map(c -> {
                final SpreadsheetCell evaluated = this.maybeParseAndEvaluateAndFormat(c, evaluation, context);
                updated.onLoad(evaluated); // might have just loaded a cell without any updates but want to record cell.
                return evaluated;
            });
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
        }
    }

    // SAVE CELL........................................................................................................

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
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
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
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
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
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
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
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
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
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
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
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
        }
    }

    private static void checkCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetRange from,
                                      final SpreadsheetRange to,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.BATCH.createUpdatedCells(this, context)) {
            BasicSpreadsheetEngineFillCells.execute(cells, from, to, this, context);
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
        }
    }

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        checkMapping(mapping);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.labelStore.save(mapping);
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
        }
    }

    @Override
    public SpreadsheetDelta removeLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        checkLabel(label);
        checkContext(context);

        try (final BasicSpreadsheetEngineUpdatedCells updated = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE.createUpdatedCells(this, context)) {
            this.labelStore.delete(label);
            updated.refreshUpdated();
            return SpreadsheetDelta.with(updated.cells());
        }
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name) {
        return this.labelStore.load(name);
    }

    // cell eval........................................................................................................

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
                    .setExpression(updated.expression(context));
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
            result = formula.setValue(Optional.of(context.evaluate(formula.expression()
                    .orElseThrow(() -> new BasicSpreadsheetEngineException("Cell missing value and error and expression")))));
        } catch (final ExpressionEvaluationException cause) {
            result = this.setError(formula, cause.getMessage());
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

        // try and use the cells custom format otherwise use a default from the context.
        SpreadsheetFormatter formatter = context.defaultSpreadsheetFormatter();
        final Optional<SpreadsheetCellFormat> maybeFormat = cell.format();
        if (maybeFormat.isPresent()) {
            final SpreadsheetCellFormat format = this.parsePatternIfNecessary(maybeFormat.get(), context);
            result = cell.setFormat(Optional.of(format));

            formatter = format.formatter()
                    .orElseThrow(() -> new SpreadsheetEngineException("Invalid cell format " + format));
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
    private SpreadsheetCellFormat parsePatternIfNecessary(final SpreadsheetCellFormat format,
                                                          final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetFormatter> formatter = format.formatter();
        return formatter.isPresent() ?
                format :
                this.parsePattern(format, context);
    }

    /**
     * Returns an updated {@link SpreadsheetCellFormat} after parsing the pattern into a {@link SpreadsheetFormatter}.
     */
    private SpreadsheetCellFormat parsePattern(final SpreadsheetCellFormat format,
                                               final SpreadsheetEngineContext context) {
        return format.setFormatter(Optional.of(context.parsePattern(format.pattern())));
    }

    /**
     * Uses the formatter to format the value, merging the style and returns an updated {@link TextNode}.
     */
    private TextNode formatAndApplyStyle0(final Object value,
                                          final SpreadsheetFormatter formatter,
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
        rules.addAll(this.rangeToConditionalFormattingRuleStore.loadCellReferenceValues(cell.reference()));
        for (SpreadsheetConditionalFormattingRule rule : rules) {
            final Object test = context.evaluate(rule.formula().expression().get());
            final Boolean booleanResult = context.convertOrFail(test, Boolean.class);
            if (Boolean.TRUE.equals(booleanResult)) {
                final TextNode formatted = cell.formatted()
                        .orElseThrow(() -> new BasicSpreadsheetEngineException("Missing formatted cell=" + cell));
                result = cell.setFormatted(
                        Optional.of(
                                rule.style()
                                        .apply(cell)
                                        .replace(formatted)));
            }
        }
        return result;
    }

    /**
     * Provides the conditional format rules for each cell.
     */
    private final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRuleStore;

    // FORMAT ERROR ....................................................................................................

    /**
     * Handles apply style to the error if present or defaulting to empty {@link String}.
     * The error becomes the text and no formatting or color is applied.
     */
    private SpreadsheetCell formatAndApplyStyleValueAbsent(final SpreadsheetCell cell) {
        final Optional<SpreadsheetError> error = cell.formula().error();

        return cell.setFormatted(Optional.of(cell.style().replace(TextNode.text(error.get().value()))));
    }

    // max..............................................................................................................

    @Override
    public double maxColumnWidth(final SpreadsheetColumnReference column) {
        return this.cellStore.maxColumnWidth(column);
    }

    // checkers.........................................................................................................

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
