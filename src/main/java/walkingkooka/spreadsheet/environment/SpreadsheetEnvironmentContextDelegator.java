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

package walkingkooka.spreadsheet.environment;

import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.LineEnding;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public interface SpreadsheetEnvironmentContextDelegator extends SpreadsheetEnvironmentContext,
    EnvironmentContextDelegator {

    @Override
    default <T> SpreadsheetEnvironmentContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                  final T value) {
        this.environmentContext().setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    default SpreadsheetEnvironmentContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");
        this.environmentContext().removeEnvironmentValue(name);
        return this;
    }

    @Override
    default SpreadsheetEnvironmentContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext().setLineEnding(lineEnding);
        return this;
    }

    @Override
    default SpreadsheetEnvironmentContext setLocale(final Locale locale) {
        this.environmentContext().setLocale(locale);
        return this;
    }

    @Override
    default AbsoluteUrl serverUrl() {
        return this.spreadsheetEnvironmentContext()
            .serverUrl();
    }

    @Override
    default SpreadsheetId spreadsheetId() {
        return this.spreadsheetEnvironmentContext()
            .spreadsheetId();
    }

    @Override
    default SpreadsheetEnvironmentContext setUser(Optional<EmailAddress> user) {
        this.environmentContext().setUser(user);
        return this;
    }
    
    // EnvironmentContextDelegator......................................................................................

    @Override
    default SpreadsheetEnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext();
    }

    SpreadsheetEnvironmentContext spreadsheetEnvironmentContext();
}
