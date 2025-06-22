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

import walkingkooka.Cast;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;
import walkingkooka.validation.form.FormName;

import java.util.List;

/**
 * Type safe factory methods for creating forms and fields.
 */
public final class SpreadsheetForms implements PublicStaticHelper {

    /**
     * A {@link Form} with a type parameter of {@link SpreadsheetExpressionReference}.
     */
    public final static Class<Form<SpreadsheetExpressionReference>> FORM_CLASS = Cast.to(Form.class);

    /**
     * {@see ValidationError}
     */
    public static ValidationError<SpreadsheetExpressionReference> error(final SpreadsheetExpressionReference reference,
                                                                        final String message) {
        return ValidationError.with(
                reference,
                message
        );
    }

    /**
     * {@see ValidationError}
     */
    public static ValidationErrorList<SpreadsheetExpressionReference> errorList(final List<ValidationError<SpreadsheetExpressionReference>> errors) {
        return ValidationErrorList.with(errors);
    }

    /**
     * {@see FormField}.
     */
    public static FormField<SpreadsheetExpressionReference> field(final SpreadsheetExpressionReference reference) {
        return FormField.with(reference);
    }

    /**
     * {@see Form}.
     */
    public static Form<SpreadsheetExpressionReference> form(final FormName name) {
        return Form.with(name);
    }

    /**
     * Stop creation
     */
    private SpreadsheetForms() {
        throw new UnsupportedOperationException();
    }
}
