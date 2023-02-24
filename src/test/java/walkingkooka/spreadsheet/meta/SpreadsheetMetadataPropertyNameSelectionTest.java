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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelectionAnchor;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameSelectionTest extends SpreadsheetMetadataPropertyNameTestCase<
        SpreadsheetMetadataPropertyNameSelection,
        SpreadsheetViewportSelection> {

    @Test
    public void testCheckCellRange() {
        this.checkValue(
                SpreadsheetSelection.parseCellRange("A1:B2")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT)
        );
    }

    @Test
    public void testCheckColumn() {
        this.checkValue(
                SpreadsheetSelection.parseColumn("AB")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testCheckLabel() {
        this.checkValue(
                SpreadsheetLabelName.labelName("Label123")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testCheckRow() {
        this.checkValue(
                SpreadsheetSelection.parseRow("1234")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testInvalidValueFails() {
        this.checkValueFails("invalid", "Expected SpreadsheetSelection, but got \"invalid\" for \"selection\"");
    }

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSelection.instance(), "selection");
    }

    @Override
    SpreadsheetMetadataPropertyNameSelection createName() {
        return SpreadsheetMetadataPropertyNameSelection.instance();
    }

    @Override
    SpreadsheetViewportSelection propertyValue() {
        return SpreadsheetSelection.parseCell("B99")
                .setAnchor(SpreadsheetViewportSelectionAnchor.NONE);
    }

    @Override
    String propertyValueType() {
        return SpreadsheetSelection.class.getSimpleName();
    }

    @Override
    String urlFragment() {
        return "/selection";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSelection> type() {
        return SpreadsheetMetadataPropertyNameSelection.class;
    }
}
