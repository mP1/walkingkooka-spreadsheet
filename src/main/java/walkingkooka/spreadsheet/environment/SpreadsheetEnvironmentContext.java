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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.LineEnding;

import java.util.Locale;
import java.util.Optional;

/**
 * A {@link EnvironmentContext} with a few extra spreadsheet standard {@link walkingkooka.environment.EnvironmentValueName}.
 */
public interface SpreadsheetEnvironmentContext extends EnvironmentContext {

    EnvironmentValueName<AbsoluteUrl> SERVER_URL = EnvironmentValueName.with("serverUrl");

    /**
     * Getter that returns the server {@link AbsoluteUrl}
     */
    AbsoluteUrl serverUrl();

    EnvironmentValueName<SpreadsheetId> SPREADSHEET_ID = EnvironmentValueName.with("spreadsheetId");

    /**
     * The {@link SpreadsheetId} that identifies this spreadsheet.
     */
    SpreadsheetId spreadsheetId();

    /**
     * Returns a {@link SpreadsheetEnvironmentContext} with the given {@link SpreadsheetId}
     */
    SpreadsheetEnvironmentContext setSpreadsheetId(final SpreadsheetId spreadsheetId);

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetEnvironmentContext cloneEnvironment();


    @Override
    SpreadsheetEnvironmentContext removeEnvironmentValue(final EnvironmentValueName<?> environmentValueName);

    @Override
    <T> SpreadsheetEnvironmentContext setEnvironmentValue(final EnvironmentValueName<T> environmentValueName,
                                                          final T reference);

    @Override
    SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext);

    @Override
    SpreadsheetEnvironmentContext setLineEnding(final LineEnding lineEnding);

    @Override
    SpreadsheetEnvironmentContext setLocale(final Locale locale);

    @Override
    SpreadsheetEnvironmentContext setUser(final Optional<EmailAddress> user);
}
