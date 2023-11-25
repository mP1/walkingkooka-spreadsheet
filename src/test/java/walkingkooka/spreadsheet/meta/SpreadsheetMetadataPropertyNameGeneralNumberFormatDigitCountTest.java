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

public final class SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCountTest extends SpreadsheetMetadataPropertyNameIntegerTestCase<SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount> {

    @Test
    public void testNegativeValueFails() {
        this.checkValueFails(-1, "Expected value >= 0, but got -1 for \"general-number-format-digit-count\"");
    }

    @Test
    public void testZeroValue() {
        this.checkValue(0);
    }

    @Test
    public void testEightValue() {
        this.checkValue(8);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount.instance(),
                "general-number-format-digit-count"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount createName() {
        return SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount.instance();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount> type() {
        return SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount.class;
    }
}
