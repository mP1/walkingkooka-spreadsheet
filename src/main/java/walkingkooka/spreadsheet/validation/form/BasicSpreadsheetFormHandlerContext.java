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

import walkingkooka.collect.set.SortedSets;
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.CanConvertDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContexts;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.validation.ValidatorContexts;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;
import walkingkooka.validation.form.FormFieldList;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormHandlerContext} that mixes some custom logic and calls to load and save cells from the provided
 * dependencies.
 */
final class BasicSpreadsheetFormHandlerContext implements SpreadsheetFormHandlerContext,
    CanConvertDelegator,
    EnvironmentContextDelegator {

    static BasicSpreadsheetFormHandlerContext with(final Form<SpreadsheetExpressionReference> form,
                                                   final SpreadsheetExpressionReferenceLoader loader,
                                                   final Function<Set<SpreadsheetCell>, SpreadsheetDelta> cellsSaver,
                                                   final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetFormHandlerContext(
            Objects.requireNonNull(form, "form"),
            Objects.requireNonNull(loader, "loader"),
            Objects.requireNonNull(cellsSaver, "cellsSaver"),
            Objects.requireNonNull(context, "context")
        );
    }

    private BasicSpreadsheetFormHandlerContext(final Form<SpreadsheetExpressionReference> form,
                                               final SpreadsheetExpressionReferenceLoader loader,
                                               final Function<Set<SpreadsheetCell>, SpreadsheetDelta> cellsSaver,
                                               final SpreadsheetEngineContext context) {
        this.form = form;
        this.loader = loader;
        this.cellsSaver = cellsSaver;
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

        final Optional<SpreadsheetCell> cell = this.loadCell(reference);
        final SpreadsheetEngineContext context = this.context;

        // Dont think a ValidationContext should ever need to load another cell.
        return SpreadsheetValidatorContexts.basic(
            ValidatorContexts.basic(
                reference,
                (final ValidatorSelector validatorSelector) -> context.validator(
                    validatorSelector,
                    context.providerContext()
                ),
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

        return this.loadCell(reference)
            .flatMap((SpreadsheetCell cell) -> cell.formula().value());
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
        final SpreadsheetEngineContext context = this.context;

        // build a map of cell or label to cell
        final Map<SpreadsheetExpressionReference, SpreadsheetCellReference> cellOrLabelToCell = SpreadsheetSelectionMaps.spreadsheetExpressionReference();
        final Map<SpreadsheetCellReference, SpreadsheetCell> loadedCellToSpreadsheetCell = SpreadsheetSelectionMaps.cell();

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

            loadedCellToSpreadsheetCell.put(
                cell,
                this.loadCell(cell)
                    .orElse(null)
            );
        }

        // try and merge $loadedCellToSpreadsheetCell with *NEW* values from fields.
        final Set<SpreadsheetCell> saving = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        for (final FormField<SpreadsheetExpressionReference> field : fields) {
            final SpreadsheetCellReference cell = cellOrLabelToCell.get(
                field.reference()
            ).toCell();

            // cell might be missing.
            SpreadsheetCell spreadsheetCell = loadedCellToSpreadsheetCell.get(cell);
            if (null == spreadsheetCell) {
                spreadsheetCell = cell.setFormula(SpreadsheetFormula.EMPTY);
            }

            saving.add(
                spreadsheetCell.setFormula(
                    spreadsheetCell.formula()
                        .setValue(field.value())
                )
            );
        }

        return this.cellsSaver.apply(saving);
    }

    /**
     * Attempts to load single cell for the given {@link SpreadsheetExpressionReference}, which includes resolving labels.
     */
    private Optional<SpreadsheetCell> loadCell(final SpreadsheetExpressionReference reference) {
        final SpreadsheetExpressionReferenceLoader loader = this.loader;
        final SpreadsheetEngineContext context = this.context;

        final SpreadsheetCellReference cellReference = context.resolveIfLabel((ExpressionReference) reference)
            .map(SpreadsheetSelection::toCell)
            .orElse(null);

        return null == cellReference ?
            Optional.empty() :
            loader.loadCell(
                cellReference,
                context.spreadsheetExpressionEvaluationContext(
                    SpreadsheetEngineContext.NO_CELL,
                    loader
                )
            );
    }

    private final SpreadsheetExpressionReferenceLoader loader;

    private final Function<Set<SpreadsheetCell>, SpreadsheetDelta> cellsSaver;

    // CanConvertDelegator..............................................................................................

    @Override
    public CanConvert canConvert() {
        return this.context;
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetFormHandlerContext cloneEnvironment() {
        final SpreadsheetEngineContext context = this.context;
        final SpreadsheetEngineContext clone = context.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return context == clone ?
            this :
            new BasicSpreadsheetFormHandlerContext(
                this.form,
                this.loader,
                this.cellsSaver,
                clone
            );
    }

    @Override
    public SpreadsheetFormHandlerContext setLineEnding(final LineEnding lineEnding) {
        this.context.setLineEnding(lineEnding);
        return this;
    }
    
    @Override
    public SpreadsheetFormHandlerContext setLocale(final Locale locale) {
        this.context.setLocale(locale);
        return this;
    }

    @Override
    public SpreadsheetFormHandlerContext setUser(final Optional<EmailAddress> user) {
        this.context.setUser(user);
        return this;
    }

    @Override
    public <T> SpreadsheetFormHandlerContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                 final T value) {
        this.context.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetFormHandlerContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.context.removeEnvironmentValue(name);
        return this;
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.context;
    }

    private final SpreadsheetEngineContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.loader + " " + this.cellsSaver + " " + this.context;
    }
}
