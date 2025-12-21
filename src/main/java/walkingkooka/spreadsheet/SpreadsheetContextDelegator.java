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

import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.LineEnding;

import java.util.Locale;
import java.util.Optional;

public interface SpreadsheetContextDelegator extends SpreadsheetContext,
    SpreadsheetEnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetMetadataContextDelegator,
    SpreadsheetProviderDelegator {

    @Override
    default SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContext()
            .storeRepository();
    }

    @Override
    default Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        return this.spreadsheetContext()
            .httpRouter();
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    default <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                       final T value) {
        this.spreadsheetEnvironmentContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    default SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.spreadsheetEnvironmentContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    default SpreadsheetId spreadsheetId() {
        return this.spreadsheetContext()
            .spreadsheetId();
    }

    @Override
    default SpreadsheetContext setSpreadsheetId(final SpreadsheetId id) {
        return this.spreadsheetContext()
            .setSpreadsheetId(id);
    }

    @Override
    default SpreadsheetContext setUser(final Optional<EmailAddress> user) {
        this.spreadsheetEnvironmentContext()
            .setUser(user);
        return this;
    }

    @Override
    default SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetContext();
    }

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetContext()
            .spreadsheetMetadata();
    }

    // HasLineEnding....................................................................................................

    @Override
    default LineEnding lineEnding() {
        return this.environmentContext()
            .lineEnding();
    }

    @Override
    default SpreadsheetContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext()
            .setLineEnding(lineEnding);
        return this;
    }
    
    // LocaleContext....................................................................................................

    @Override
    default Locale locale() {
        return this.environmentContext()
            .locale();
    }

    @Override
    default SpreadsheetContext setLocale(final Locale locale) {
        this.environmentContext()
            .setLocale(locale);
        return this;
    }

    @Override
    default LocaleContext localeContext() {
        return this.spreadsheetContext();
    }

    // SpreadsheetMetadataContext.......................................................................................

    @Override
    default ProviderContext providerContext() {
        return this.spreadsheetContext()
            .providerContext();
    }

    // SpreadsheetProvider..............................................................................................

    @Override
    default SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetContext();
    }

    @Override
    default SpreadsheetMetadata createMetadata(final EmailAddress user,
                                               final Optional<Locale> locale) {
        return SpreadsheetContext.super.createMetadata(
            user,
            locale
        );
    }
    
    @Override
    default SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetContext();
    }

    SpreadsheetContext spreadsheetContext();
}
