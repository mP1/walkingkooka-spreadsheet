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
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

public final class SpreadsheetContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetContext}
     */
    public static SpreadsheetContext basic(final AbsoluteUrl serverUrl,
                                           final SpreadsheetId spreadsheetId,
                                           final SpreadsheetStoreRepository storeRepository,
                                           final SpreadsheetProvider spreadsheetProvider,
                                           final EnvironmentContext environmentContext,
                                           final LocaleContext localeContext,
                                           final ProviderContext providerContext) {
        return BasicSpreadsheetContext.with(
            serverUrl,
            spreadsheetId,
            storeRepository,
            spreadsheetProvider,
            environmentContext,
            localeContext,
            providerContext
        );
    }

    /**
     * {@see FakeSpreadsheetContext}
     */
    public static SpreadsheetContext fake() {
        return new FakeSpreadsheetContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetContexts() {
        throw new UnsupportedOperationException();
    }
}
