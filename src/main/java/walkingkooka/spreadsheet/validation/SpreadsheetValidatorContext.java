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

package walkingkooka.spreadsheet.validation;

import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.Validator;
import walkingkooka.validation.ValidatorContext;

import java.util.Locale;

public interface SpreadsheetValidatorContext extends ValidatorContext<SpreadsheetExpressionReference> {

    @Override
    SpreadsheetValidatorContext cloneEnvironment();

    @Override
    SpreadsheetValidatorContext setLocale(final Locale locale);

    @Override
    <T> SpreadsheetValidatorContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                        final T value);

    @Override
    SpreadsheetValidatorContext removeEnvironmentValue(final EnvironmentValueName<?> name);

    /**
     * A named reference that may be used within {@link walkingkooka.tree.expression.Expression} executed within a {@link Validator}.
     */
    SpreadsheetLabelName VALUE = SpreadsheetSelection.labelName(ValidatorContext.VALIDATION_EXPRESSION_VALUE_REFERENCE_STRING);

    @Override
    SpreadsheetValidatorContext setValidationReference(final SpreadsheetExpressionReference cellOrLabel);

    /**
     * Creates a {@link SpreadsheetExpressionEvaluationContext} with the current {@link #validationReference()}
     * as the cell.
     */
    @Override
    SpreadsheetExpressionEvaluationContext expressionEvaluationContext(final Object value);
}
