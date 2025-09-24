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

import walkingkooka.locale.LocaleContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;

import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

public final class SpreadsheetGlobalContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetGlobalContext}
     */
    public static SpreadsheetGlobalContext basic(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                                 final SpreadsheetMetadataStore store,
                                                 final LocaleContext localeContext,
                                                 final ProviderContext providerContext) {
        return BasicSpreadsheetGlobalContext.with(
            createMetadata,
            store,
            localeContext,
            providerContext
        );
    }

    /**
     * {@see FakeSpreadsheetGlobalContext}
     */
    public static SpreadsheetGlobalContext fake() {
        return new FakeSpreadsheetGlobalContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetGlobalContexts() {
        throw new UnsupportedOperationException();
    }
}
