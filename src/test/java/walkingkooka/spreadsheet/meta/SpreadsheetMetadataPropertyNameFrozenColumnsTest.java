
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
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameFrozenColumnsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameFrozenColumns, SpreadsheetColumnReferenceRange> {

    @Test
    public void testInvalidSpreadsheetColumnReferenceRangeFails() {
        this.checkValueFails(
                SpreadsheetSelection.parseColumnRange("C:D"),
                "Range must begin at 'A', but got C:D for \"frozen-columns\""
        );
    }

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameFrozenColumns.instance(),
                "frozen-columns"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameFrozenColumns createName() {
        return SpreadsheetMetadataPropertyNameFrozenColumns.instance();
    }

    @Override
    SpreadsheetColumnReferenceRange propertyValue() {
        return SpreadsheetSelection.parseColumnRange("A:B");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetColumnReferenceRange.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameFrozenColumns> type() {
        return SpreadsheetMetadataPropertyNameFrozenColumns.class;
    }
}
