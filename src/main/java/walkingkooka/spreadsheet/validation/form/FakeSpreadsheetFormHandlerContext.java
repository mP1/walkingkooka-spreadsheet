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

import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.validation.form.FakeFormHandlerContext;

import java.util.Locale;
import java.util.Optional;

public class FakeSpreadsheetFormHandlerContext extends FakeFormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta>
    implements SpreadsheetFormHandlerContext {

    public FakeSpreadsheetFormHandlerContext() {
        super();
    }

    @Override
    public SpreadsheetFormHandlerContext setLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormHandlerContext setUser(final Optional<EmailAddress> user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormHandlerContext cloneEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> SpreadsheetFormHandlerContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                 final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormHandlerContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }
}
