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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.validation.FakeValidatorContext;

import java.util.Locale;
import java.util.Optional;

public class FakeSpreadsheetValidatorContext extends FakeValidatorContext<SpreadsheetExpressionReference> implements SpreadsheetValidatorContext {

    public FakeSpreadsheetValidatorContext() {
        super();
    }

    @Override
    public SpreadsheetValidatorContext cloneEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext setLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext setUser(final Optional<EmailAddress> user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> SpreadsheetValidatorContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                               final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext setValidationReference(final SpreadsheetExpressionReference cellOrLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext expressionEvaluationContext(final Object value) {
        throw new UnsupportedOperationException();
    }
}
