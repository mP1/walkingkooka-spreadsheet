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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.currency.provider.CurrencyExchangeRaterName;
import walkingkooka.currency.provider.CurrencyExchangeRaterProviderTesting;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.math.HasMathContextTesting;
import walkingkooka.net.header.MediaTypeDetectorTesting;
import walkingkooka.plugin.FakeProviderContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.props.Properties;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.currency.SpreadsheetCurrencyExchangeRaters;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContexts;
import walkingkooka.storage.StorageEnvironmentContextTesting;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.storage.Storages;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.storage.convert.StorageConverterContexts;
import walkingkooka.storage.convert.StorageConverters;
import walkingkooka.tree.expression.HasExpressionNumberKindTesting;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCurrencyCurrencyExchangeRaterProviderTest implements CurrencyExchangeRaterProviderTesting<SpreadsheetCurrencyCurrencyExchangeRaterProvider>,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    HasExpressionNumberKindTesting,
    HasMathContextTesting,
    JsonNodeMarshallUnmarshallContextTesting,
    MediaTypeDetectorTesting,
    StorageEnvironmentContextTesting {

    private final static Function<String, Number> NUMBER_PARSER = Double::parseDouble;

    private final static Properties PROPERTIES = Properties.parse("AUD-NZD=1.1");

    private final static StoragePath PROPERTIES_STORAGE_PATH = StoragePath.parse("/storage1/currency.properties");

    private final static ProviderContext PROVIDER_CONTEXT = new FakeProviderContext() {

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(
                value,
                type,
                this.context
            );
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> type) {
            return this.converter.convert(
                value,
                type,
                this.context
            );
        }

        private final Converter<StorageConverterContext> converter = Converters.collection(
            Lists.of(
                Converters.simple(),
                Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                StorageConverters.textToStoragePath()
            )
        );

        private final StorageConverterContext context = StorageConverterContexts.basic(
            this.converter,
            HAS_USER_DIRECTORIES,
            MEDIA_TYPE_DETECTOR,
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ExpressionNumberBinaryNumberConverterFunctions.multiply(), // multiplier
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        ',', // valueSeparator
                        Converters.fake(),
                        BinaryNumberConverterFunctions.fake(), // multiplier
                        BINARY_TEXT_CONTEXT,
                        CURRENCY_LOCALE_CONTEXT,
                        DATE_TIME_CONTEXT,
                        DECIMAL_NUMBER_CONTEXT
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );

        @Override
        public Optional<StorageValue> loadStorage(final StoragePath path) {
            return this.storageContext.loadStorage(path);
        }

        @Override
        public Runnable addStorageWatcher(final StorageWatcher watcher) {
            return this.storageContext.addStorageWatcher(watcher);
        }

        private final StorageContext storageContext = StorageContexts.basic(
            ConverterContexts.fake(), // ConverterLike
            MEDIA_TYPE_DETECTOR,
            Storages.treeMapStore(),
            STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        {
            this.storageContext.saveStorage(
                StorageValue.with(PROPERTIES_STORAGE_PATH)
                    .setValue(
                        Optional.of(PROPERTIES)
                    )
            );
        }
    };

    @Test
    public void testWithNullNumberParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCurrencyCurrencyExchangeRaterProvider.with(null)
        );
    }

    @Test
    public void testCurrencyExchangeRaterWithPropertiesNameAndValues() {
        this.currencyExchangeRaterAndCheck(
            CurrencyExchangeRaterName.with("properties"),
            Lists.of(
                PROPERTIES
            ),
            PROVIDER_CONTEXT,
            SpreadsheetCurrencyExchangeRaters.properties(
                PROPERTIES,
                NUMBER_PARSER
            )
        );
    }

    @Test
    public void testCurrencyExchangeRaterWithStoragePathPropertiesNameAndValues() {
        this.currencyExchangeRaterAndCheck(
            CurrencyExchangeRaterName.with("storage-path-properties"),
            Lists.of(
                PROPERTIES_STORAGE_PATH
            ),
            PROVIDER_CONTEXT,
            SpreadsheetCurrencyExchangeRaters.storagePathProperties(
                PROPERTIES_STORAGE_PATH,
                NUMBER_PARSER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testCurrencyExchangeRaterInfos() {
        this.treePrintAndCheck(
            this.createCurrencyExchangeRaterProvider()
                .currencyExchangeRaterInfos(),
            "CurrencyExchangeRaterInfoSet\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/CurrencyExchangeRater/properties properties\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/CurrencyExchangeRater/storage-path-properties storage-path-properties\n"
        );
    }

    @Override
    public SpreadsheetCurrencyCurrencyExchangeRaterProvider createCurrencyExchangeRaterProvider() {
        return SpreadsheetCurrencyCurrencyExchangeRaterProvider.with(NUMBER_PARSER);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCurrencyCurrencyExchangeRaterProvider> type() {
        return SpreadsheetCurrencyCurrencyExchangeRaterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
