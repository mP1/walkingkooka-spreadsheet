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

package walkingkooka.spreadsheet;

import walkingkooka.Context;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.HasProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Context} holding state including {@link EnvironmentContext} for a single user and the spreadsheet they are
 * viewing/editing.
 */
public interface SpreadsheetContext extends SpreadsheetProvider,
    EnvironmentContext,
    HasProviderContext,
    HasSpreadsheetMetadata,
    HasSpreadsheetServerUrl,
    LocaleContext,
    SpreadsheetMetadataContext {

    @Override
    default SpreadsheetMetadata createMetadata(final EmailAddress user,
                                               final Optional<Locale> locale) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(locale, "locale");
        throw new UnsupportedOperationException();
    }

    /**
     * The {@link SpreadsheetId} that identifies this spreadsheet.
     */
    SpreadsheetId spreadsheetId();

    /**
     * The {@link SpreadsheetStoreRepository} for the selected spreadsheet.
     */
    SpreadsheetStoreRepository storeRepository();

    /**
     * Getter that returns a cached {@link SpreadsheetEngineContext}, belonging to this {@link SpreadsheetId}.
     */
    SpreadsheetEngineContext spreadsheetEngineContext();

    /**
     * Returns the {@link Router} for this spreadsheet.
     */
    Router<HttpRequestAttribute<?>, HttpHandler> httpRouter();

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetContext cloneEnvironment();

    @Override
    <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                               final T value);

    @Override
    SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name);


    @Override
    SpreadsheetContext setLocale(final Locale locale);

    @Override
    SpreadsheetContext setUser(final Optional<EmailAddress> user);
}
