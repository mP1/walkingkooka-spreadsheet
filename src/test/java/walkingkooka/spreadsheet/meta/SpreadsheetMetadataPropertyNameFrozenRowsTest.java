

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
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetMetadataPropertyNameFrozenRowsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameFrozenRows, SpreadsheetRowRangeReference> {

    @Test
    public void testCheckValueWithInvalidSpreadsheetRowRangeReferenceFails() {
        this.checkValueFails(
            SpreadsheetSelection.parseRowRange("2:3"),
            "Metadata frozenRows=2:3, Row range must begin at '1'"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Test
    public void testParseUrlFragmentSaveValueFrozenRows() {
        this.checkEquals(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetMetadataPropertyName.FROZEN_ROWS
                .parseUrlFragmentSaveValue("1:2")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameFrozenRows.instance(),
            "frozenRows"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameFrozenRows createName() {
        return SpreadsheetMetadataPropertyNameFrozenRows.instance();
    }

    @Override
    SpreadsheetRowRangeReference propertyValue() {
        return SpreadsheetSelection.parseRowRange("1:2");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetRowRangeReference.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameFrozenRows> type() {
        return SpreadsheetMetadataPropertyNameFrozenRows.class;
    }
}
