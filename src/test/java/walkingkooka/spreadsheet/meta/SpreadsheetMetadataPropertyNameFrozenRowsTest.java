

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
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameFrozenRowsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameFrozenRows, SpreadsheetRowReferenceRange> {

    @Test
    public void testInvalidSpreadsheetRowReferenceRangeFails() {
        this.checkValueFails(
                SpreadsheetSelection.parseRowRange("2:3"),
                "Range must begin at '1', but got 2:3 for \"frozen-rows\""
        );
    }

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameFrozenRows.instance(),
                "frozen-rows"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameFrozenRows createName() {
        return SpreadsheetMetadataPropertyNameFrozenRows.instance();
    }

    @Override
    SpreadsheetRowReferenceRange propertyValue() {
        return SpreadsheetSelection.parseRowRange("1:2");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetRowReferenceRange.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameFrozenRows> type() {
        return SpreadsheetMetadataPropertyNameFrozenRows.class;
    }
}
