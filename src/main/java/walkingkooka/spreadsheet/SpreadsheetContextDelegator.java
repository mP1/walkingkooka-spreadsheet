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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Locale;

public interface SpreadsheetContextDelegator extends SpreadsheetContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetMetadataContextDelegator,
    SpreadsheetProviderDelegator {

    @Override
    default SpreadsheetId spreadsheetId() {
        return this.spreadsheetContext()
            .spreadsheetId();
    }

    @Override
    default SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContext()
            .storeRepository();
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    default <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                       final T value) {
        this.spreadsheetContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    default SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.spreadsheetContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    default EnvironmentContext environmentContext() {
        return this.spreadsheetContext();
    }

    // LocaleContext....................................................................................................

    @Override
    default Locale locale() {
        return this.environmentContext()
            .locale();
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
    default SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetContext();
    }

    SpreadsheetContext spreadsheetContext();
}
