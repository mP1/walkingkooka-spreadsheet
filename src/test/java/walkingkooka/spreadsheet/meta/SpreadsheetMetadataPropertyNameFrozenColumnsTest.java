
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
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetMetadataPropertyNameFrozenColumnsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameFrozenColumns, SpreadsheetColumnRangeReference> {

    @Test
    public void testCheckValueWithInvalidSpreadsheetColumnRangeReferenceFails() {
        this.checkValueFails(
            SpreadsheetSelection.parseColumnRange("C:D"),
            "Metadata frozenColumns=C:D, Column range must begin at 'A'"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Test
    public void testParseUrlFragmentSaveValueQuery() {
        this.parseUrlFragmentSaveValueAndCheck(
            "A:B",
            SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameFrozenColumns.instance(),
            "frozenColumns"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameFrozenColumns createName() {
        return SpreadsheetMetadataPropertyNameFrozenColumns.instance();
    }

    @Override
    SpreadsheetColumnRangeReference propertyValue() {
        return SpreadsheetSelection.parseColumnRange("A:B");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetColumnRangeReference.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameFrozenColumns> type() {
        return SpreadsheetMetadataPropertyNameFrozenColumns.class;
    }
}
