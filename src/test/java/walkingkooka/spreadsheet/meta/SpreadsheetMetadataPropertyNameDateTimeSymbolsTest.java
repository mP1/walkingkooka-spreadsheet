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
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContexts;

import java.text.DateFormatSymbols;
import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameDateTimeSymbolsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameDateTimeSymbols, DateTimeSymbols> {

    @Test
    public void testCheckValueWithInvalidDateTimeSymbolsFails() {
        this.checkValueFails(
                "invalid",
                "Metadata dateTimeSymbols=\"invalid\", Expected DateTimeSymbols"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
                LocaleContexts.jre(Locale.ENGLISH),
                this.propertyValue()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameDateTimeSymbols.instance(),
                "dateTimeSymbols"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameDateTimeSymbols createName() {
        return SpreadsheetMetadataPropertyNameDateTimeSymbols.instance();
    }

    @Override
    DateTimeSymbols propertyValue() {
        return DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(Locale.ENGLISH)
        );
    }

    @Override
    String propertyValueType() {
        return DateTimeSymbols.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameDateTimeSymbols> type() {
        return SpreadsheetMetadataPropertyNameDateTimeSymbols.class;
    }
}
