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

package walkingkooka.spreadsheet.template;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A Context that leverages Spreadsheet components to parse and render templates.
 */
public interface SpreadsheetTemplateContext extends SpreadsheetParserContext,
        SpreadsheetExpressionEvaluationContext {

    /**
     * {@see TemplateContext#templateValue}
     */
    String templateValue(final TemplateValueName name);

    @Override
    default SpreadsheetTemplateContext setCell(final Optional<SpreadsheetCell> cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        throw new UnsupportedOperationException();
    }

    @Override
    default void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");
        throw new UnsupportedOperationException();
    }

    // Validation.......................................................................................................

    /**
     * A template never needs the {@link SpreadsheetExpressionReference}.
     */
    @Override
    default SpreadsheetExpressionReference validationReference() {
        throw new UnsupportedOperationException();
    }

    // ValidationExpressionEvaluationContext............................................................................

    @Override
    default Optional<Object> validationValue() {
        throw new UnsupportedOperationException();
    }

    // HasForm..........................................................................................................

    @Override
    default Form<SpreadsheetExpressionReference> form() {
        throw new UnsupportedOperationException();
    }

    // FormHandlerContext...............................................................................................

    @Override
    default Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    default SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    default SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    default List<ValidationError<SpreadsheetExpressionReference>> validateFormFields(final List<FormField<SpreadsheetExpressionReference>> fields) {
        throw new UnsupportedOperationException();
    }
}
