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
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.HasDecimalNumberSymbolsTesting;


public final class SpreadsheetMetadataPropertyNameDecimalNumberSymbolsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameDecimalNumberSymbols, DecimalNumberSymbols>
    implements HasDecimalNumberSymbolsTesting {

    @Test
    public void testCheckValueWithInvalidDecimalNumberSymbolsFails() {
        this.checkValueFails(
            "invalid",
            "Metadata decimalNumberSymbols=\"invalid\", Expected DecimalNumberSymbols"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
            LOCALE_CONTEXT,
            this.propertyValue()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameDecimalNumberSymbols.instance(),
            "decimalNumberSymbols"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameDecimalNumberSymbols createName() {
        return SpreadsheetMetadataPropertyNameDecimalNumberSymbols.instance();
    }

    @Override
    DecimalNumberSymbols propertyValue() {
        return DECIMAL_NUMBER_SYMBOLS;
    }

    @Override
    String propertyValueType() {
        return DecimalNumberSymbols.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameDecimalNumberSymbols> type() {
        return SpreadsheetMetadataPropertyNameDecimalNumberSymbols.class;
    }
}
