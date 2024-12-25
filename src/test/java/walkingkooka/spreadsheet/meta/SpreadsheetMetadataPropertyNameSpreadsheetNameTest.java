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
import walkingkooka.spreadsheet.SpreadsheetName;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameSpreadsheetNameTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetName, SpreadsheetName> {

    @Test
    public void testCheckValueWithInvalidSpreadsheetNameFails() {
        this.checkValueFails(
                "\r",
                "Metadata spreadsheet-name=\"\\r\", Expected SpreadsheetName"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetName.instance(), "spreadsheet-name");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetName createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetName.instance();
    }

    @Override
    SpreadsheetName propertyValue() {
        return SpreadsheetName.with("Spreadsheet-name-123");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetName.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetName> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetName.class;
    }
}
