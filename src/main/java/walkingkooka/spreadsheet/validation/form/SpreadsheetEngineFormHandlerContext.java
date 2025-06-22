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

package walkingkooka.spreadsheet.validation.form;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.CanConvertDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetDeltaProperties;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContexts;
import walkingkooka.text.CharSequences;
import walkingkooka.validation.ValidatorContexts;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;
import walkingkooka.validation.form.FormFieldList;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class SpreadsheetEngineFormHandlerContext implements SpreadsheetFormHandlerContext,
        CanConvertDelegator,
        EnvironmentContextDelegator {

    static SpreadsheetEngineFormHandlerContext with(final Form<SpreadsheetExpressionReference> form,
                                                    final SpreadsheetEngine engine,
                                                    final SpreadsheetEngineContext context) {
        return new SpreadsheetEngineFormHandlerContext(
                Objects.requireNonNull(form, "form"),
                Objects.requireNonNull(engine, "engine"),
                Objects.requireNonNull(context, "context")
        );
    }

    private SpreadsheetEngineFormHandlerContext(final Form<SpreadsheetExpressionReference> form,
                                                final SpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
        this.form = form;
        this.engine = engine;
        this.context = context;
    }

    @Override
    public Form<SpreadsheetExpressionReference> form() {
        return this.form;
    }

    /**
     * The current active form.
     */
    private final Form<SpreadsheetExpressionReference> form;

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");
        if (false == reference.isCell() && false == reference.isLabelName()) {
            throw new IllegalArgumentException("Invalid reference " + reference + " expected only cell or label");
        }

        final SpreadsheetEngineContext context = this.context;

        final Optional<SpreadsheetCell> cell = this.engine.loadCells(
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE, // DONT want to evaluate cell's formula working with input values.
                Sets.of(SpreadsheetDeltaProperties.CELLS),
                context
        ).cell(
                context.resolveIfLabelOrFail(reference).toCell()
        );

        // Dont think a ValidationContext should ever need to load another cell.
        return SpreadsheetValidatorContexts.basic(
                ValidatorContexts.basic(
                        reference,
                        (final ValidatorSelector validatorSelector) -> context.validator(validatorSelector, context),
                        (final Object validatingValue,
                         final SpreadsheetExpressionReference r) -> context.spreadsheetExpressionEvaluationContext(
                                cell,
                                SpreadsheetExpressionReferenceLoaders.fake() // SpreadsheetExpressionReferenceLoader
                        ).addLocalVariable(
                                SpreadsheetValidatorContext.VALUE,
                                Optional.ofNullable(validatingValue)
                        ), //BiFunction<Object, T, ExpressionEvaluationContext > referenceToExpressionEvaluationContext,
                        context, // CanConvert,
                        context // EnvironmentContext
                )
        );
    }

    @Override
    public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        if (reference.isCellRange()) {
            throw new IllegalArgumentException("Invalid reference " + reference + " expected only cell or label");
        }

        final SpreadsheetEngineContext context = this.context;

        final SpreadsheetDelta delta = this.engine.loadCells(
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE, // only interested in SpreadsheetCell#value
                Sets.of(
                        SpreadsheetDeltaProperties.CELLS
                ),
                context
        );

        final SpreadsheetSelection maybeCell = reference.isCell() ?
                reference :
                context.resolveLabel(reference.toLabelName())
                        .orElse(null);

        Object value = null;

        // cell may be present and may or may not have an value.
        if (null != maybeCell) {
            value = delta.cell(maybeCell.toCell())
                    .flatMap(c -> c.formula().value())
                    .orElse(null);
        }

        return Optional.ofNullable(value);
    }

    @Override
    public SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
        return this.saveFormFieldValues0(
                FormFieldList.with(
                        Objects.requireNonNull(fields, "fields")
                )
        );
    }

    private SpreadsheetDelta saveFormFieldValues0(final FormFieldList<SpreadsheetExpressionReference> fields) {
        final SpreadsheetEngine engine = this.engine;
        final SpreadsheetEngineContext context = this.context;

        // build a map of cell or label to cell
        final Map<SpreadsheetExpressionReference, SpreadsheetCellReference> cellOrLabelToCell = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        final Set<SpreadsheetCellRangeReference> loadCells = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        for (final FormField<SpreadsheetExpressionReference> field : fields) {
            SpreadsheetExpressionReference cellOrLabel = field.reference();

            SpreadsheetCellReference cell;
            if (cellOrLabel.isLabelName()) {
                // attempt to save a field to an unmapped label is a FAIL.
                cell = context.resolveLabelOrFail(
                        cellOrLabel.toLabelName()
                ).toCell();
            } else {
                // skip CellRange.
                if (cellOrLabel.isCell()) {
                    cell = cellOrLabel.toCell();
                } else {
                    continue;
                }
            }

            // complain if CELLRANGE only allow CELL or LABEL to a CELL
            if (cellOrLabel.isCellRange()) {
                throw new IllegalArgumentException("Field with cell-range " + CharSequences.quote(cellOrLabel.toString()) + " expected only cell or label");
            }

            final SpreadsheetCellReference duplicate = cellOrLabelToCell.put(
                    cellOrLabel,
                    cell
            );
            if (null != duplicate) {
                throw new IllegalArgumentException("Multiple fields with same name " + CharSequences.quoteAndEscape(cellOrLabel.text()));
            }

            loadCells.add(cell.toCellRange());
        }

        // try and load each of the cells in $loadCells
        final SpreadsheetDelta loaded = engine.loadMultipleCellRanges(
                loadCells,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE, // Only interested in SpreadsheetFormula#value not formula expression values
                Sets.of(SpreadsheetDeltaProperties.CELLS),
                context
        );
        final Map<SpreadsheetCellReference, SpreadsheetCell> loadedCellToSpreadsheetCell = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        for (final SpreadsheetCell cell : loaded.cells()) {
            loadedCellToSpreadsheetCell.put(
                    cell.reference(),
                    cell
            );
        }

        // try and merge $loadedCellToSpreadsheetCell with *NEW* values from fields.
        final Set<SpreadsheetCell> saving = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        for (final FormField<SpreadsheetExpressionReference> field : fields) {
            final SpreadsheetCellReference cell = cellOrLabelToCell.get(
                    field.reference()
            ).toCell();

            final SpreadsheetCell spreadsheetCell = loadedCellToSpreadsheetCell.get(cell);
            saving.add(
                    spreadsheetCell.setFormula(
                            spreadsheetCell.formula()
                                    .setValue(field.value())
                    )
            );
        }

        return engine.saveCells(
                saving,
                context
        );
    }

    // CanConvertDelegator..............................................................................................

    @Override
    public CanConvert canConvert() {
        return this.context;
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.context;
    }

    private final SpreadsheetEngine engine;

    private final SpreadsheetEngineContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.engine + " " + this.context;
    }
}
