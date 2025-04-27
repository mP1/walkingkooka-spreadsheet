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

package walkingkooka.spreadsheet.expression;

import walkingkooka.convert.Converter;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContextDelegator;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContextDelegator;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Delegates all {@link ExpressionEvaluationContextDelegator} and most {@link SpreadsheetExpressionEvaluationContext}
 * except for cell getter and loaders:
 * <ul>
 *     <li>{@link #cell()}</li>
 *     <li>{@link #loadCell(SpreadsheetCellReference)}</li>
 *     <li>{@link #loadCellRange(SpreadsheetCellRangeReference)}</li>
 * </ul>
 */
public interface SpreadsheetExpressionEvaluationContextDelegator extends SpreadsheetExpressionEvaluationContext,
        StorageExpressionEvaluationContextDelegator {

    @Override
    default StorageExpressionEvaluationContext storageExpressionEvaluationContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    // SpreadsheetExpressionEvaluationContext...........................................................................

    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetExpressionEvaluationContext().converter();
    }

    @Override
    default <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.spreadsheetExpressionEvaluationContext()
                .environmentValue(name);
    }

    @Override
    default Set<EnvironmentValueName<?>> environmentValueNames() {
        return this.spreadsheetExpressionEvaluationContext()
                .environmentValueNames();
    }

    @Override
    default boolean isText(final Object value) {
        return this.spreadsheetExpressionEvaluationContext()
                .isText(value);
    }

    @Override
    default Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetExpressionEvaluationContext()
                .loadLabel(labelName);
    }

    @Override
    default Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return this.spreadsheetExpressionEvaluationContext()
                .nextEmptyColumn(row);
    }

    @Override
    default Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return this.spreadsheetExpressionEvaluationContext()
                .nextEmptyRow(column);
    }

    @Override
    default SpreadsheetFormulaParserToken parseFormula(final TextCursor formula) {
        return this.spreadsheetExpressionEvaluationContext()
                .parseFormula(formula);
    }

    @Override
    default AbsoluteUrl serverUrl() {
        return this.spreadsheetExpressionEvaluationContext()
                .serverUrl();
    }

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetExpressionEvaluationContext()
                .spreadsheetMetadata();
    }

    @Override
    default void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        this.spreadsheetExpressionEvaluationContext()
                .setSpreadsheetMetadata(metadata);
    }

    @Override
    default Optional<EmailAddress> user() {
        return this.spreadsheetExpressionEvaluationContext()
                .user();
    }

    // FormHandlerContext...............................................................................................

    @Override
    default Form<SpreadsheetExpressionReference> form() {
        return this.spreadsheetExpressionEvaluationContext()
                .form();
    }

    @Override
    default SpreadsheetDelta saveFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
        return this.spreadsheetExpressionEvaluationContext()
                .saveFieldValues(fields);
    }

    @Override
    default Optional<?> loadFieldValue(final SpreadsheetExpressionReference reference) {
        return this.spreadsheetExpressionEvaluationContext()
                .loadFieldValue(reference);
    }

    @Override
    default SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return this.spreadsheetExpressionEvaluationContext()
                .validatorContext(reference);
    }

    @Override
    default Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        return this.spreadsheetExpressionEvaluationContext()
                .formFieldReferenceComparator();
    }


    // ExpressionEvaluationContextDelegator.............................................................................

    SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext();
}
