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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;

public final class SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPatternTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern, SpreadsheetNumberFormatPattern> {

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern.INSTANCE, "number-format-pattern");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern.INSTANCE;
    }

    @Override
    SpreadsheetNumberFormatPattern propertyValue() {
        return SpreadsheetNumberFormatPattern.parseNumberFormatPattern("#.## \"custom\"");
    }

    @Override
    String propertyValueType() {
        return "Number format pattern";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern.class;
    }
}
