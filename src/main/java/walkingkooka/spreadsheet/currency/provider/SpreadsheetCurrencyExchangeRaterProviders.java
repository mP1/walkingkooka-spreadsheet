
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

package walkingkooka.spreadsheet.currency.provider;

import walkingkooka.currency.CurrencyExchangeRater;
import walkingkooka.currency.CurrencyExchangeRaters;
import walkingkooka.currency.provider.CurrencyExchangeRaterProvider;
import walkingkooka.props.Properties;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.currency.SpreadsheetCurrencyExchangeRaterContext;
import walkingkooka.spreadsheet.currency.SpreadsheetCurrencyExchangeRaters;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.currency.StorageCurrencyExchangeRaters;

import java.util.function.Function;

/**
 * A {@link CurrencyExchangeRaterProvider} for {@link SpreadsheetCurrencyExchangeRaters}.
 */
public final class SpreadsheetCurrencyExchangeRaterProviders implements PublicStaticHelper {

    /**
     * {@link CurrencyExchangeRater}
     */
    public static CurrencyExchangeRater<SpreadsheetCurrencyExchangeRaterContext> properties(final Properties properties,
                                                                                            final Function<String, Number> numberParser) {
        return CurrencyExchangeRaters.properties(
            properties,
            numberParser
        );
    }

    /**
     * {@link StorageCurrencyExchangeRaters#storagePathProperties(StoragePath, Function, StorageContext)}
     */
    public static CurrencyExchangeRater<SpreadsheetCurrencyExchangeRaterContext> storagePath(final StoragePath storagePath,
                                                                                             final Function<String, Number> numberParser,
                                                                                             final StorageContext storageContext) {
        return StorageCurrencyExchangeRaters.storagePathProperties(
            storagePath,
            numberParser,
            storageContext
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetCurrencyExchangeRaterProviders() {
        throw new UnsupportedOperationException();
    }
}
