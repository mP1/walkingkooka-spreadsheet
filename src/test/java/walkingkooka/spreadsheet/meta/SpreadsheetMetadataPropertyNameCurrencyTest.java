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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.locale.LocaleContexts;

import java.util.Currency;
import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameCurrencyTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameCurrency, Currency> {

    @Test
    public void testExtractLocaleAwareValueWithLocaleEnglish() {
        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(
                Locale.forLanguageTag("en-AU")
            ),
            Currency.getInstance("AUD")
        );
    }

    @Test
    public void testExtractLocaleAwareValueWithLocaleEnglishAustralia() {
        final Locale locale = Locale.forLanguageTag("en-AU");

        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(locale),
            Currency.getInstance(locale)
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameCurrency.instance(),
            "currency"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameCurrency createName() {
        return SpreadsheetMetadataPropertyNameCurrency.instance();
    }

    @Override
    Currency propertyValue() {
        return Currency.getInstance("AUD");
    }

    @Override
    String propertyValueType() {
        return Currency.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameCurrency> type() {
        return SpreadsheetMetadataPropertyNameCurrency.class;
    }
}
