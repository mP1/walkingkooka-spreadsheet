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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.FakeCurrencyContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetConverterPropertiesToSpreadsheetMetadataTest extends SpreadsheetConverterTestCase<SpreadsheetConverterPropertiesToSpreadsheetMetadata> {

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static CurrencyLocaleContext CURRENCY_LOCALE_CONTEXT = new FakeCurrencyContext() {
        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            return Optional.of(
                Currency.getInstance(locale)
            );
        }
    }.setLocaleContext(
        LocaleContexts.jre(LOCALE)
    );

    @Test
    public void testConvertPropertiesToSpreadsheetMetadataWithEmpty() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY;

        this.convertAndCheck(
            metadata.properties(),
            SpreadsheetMetadata.class,
            metadata
        );
    }

    @Test
    public void testConvertPropertiesToSpreadsheetMetadataWithEmpty2() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY;

        this.convertAndCheck(
            metadata.properties(),
            metadata
        );
    }

    @Test
    public void testConvertPropertiesToSpreadsheetMetadataWithNotEmpty() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            LOCALE
        ).loadFromLocale(CURRENCY_LOCALE_CONTEXT);

        this.convertAndCheck(
            metadata.properties(),
            metadata
        );
    }

    @Test
    public void testConvertStringPropertiesToSpreadsheetMetadataWithNotEmpty() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            LOCALE
        ).loadFromLocale(CURRENCY_LOCALE_CONTEXT);

        this.convertAndCheck(
            metadata.properties()
                .text(),
            metadata
        );
    }

    @Override
    public SpreadsheetConverterPropertiesToSpreadsheetMetadata createConverter() {
        return SpreadsheetConverterPropertiesToSpreadsheetMetadata.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return CONVERTER.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> type) {
                return CONVERTER.convert(
                    value,
                    type,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> CONVERTER = SpreadsheetConverters.collection(
                Lists.of(
                    Converters.simple(),
                    SpreadsheetConverters.textToProperties()
                )
            );

            @Override
            public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
                return Optional.of(
                    Currency.getInstance(
                        currencyCode.value()
                    )
                );
            }

            @Override
            public Optional<Locale> localeForLanguageTag(final String languageTag) {
                return Optional.of(
                    Locale.forLanguageTag(languageTag)
                );
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterPropertiesToSpreadsheetMetadata> type() {
        return SpreadsheetConverterPropertiesToSpreadsheetMetadata.class;
    }
}
