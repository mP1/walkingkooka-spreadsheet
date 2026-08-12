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

package walkingkooka.spreadsheet.provider;

import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;

public final class SpreadsheetProviderContexts implements PublicStaticHelper {

    /**
     * {@see SpreadsheetProviderContext}
     */
    public static ProviderContext spreadsheet(final MediaTypeDetector mediaTypeDetector,
                                              final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                              final PluginStore pluginStore,
                                              final Storage<StorageContext> storage,
                                              final CurrencyLocaleContext currencyLocaleContext,
                                              final StorageEnvironmentContext storageEnvironmentContext,
                                              final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        return SpreadsheetProviderContext.with(
            mediaTypeDetector,
            multiplier,
            pluginStore,
            storage,
            currencyLocaleContext,
            storageEnvironmentContext,
            jsonNodeMarshallUnmarshallContext
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetProviderContexts() {
        throw new UnsupportedOperationException();
    }
}
