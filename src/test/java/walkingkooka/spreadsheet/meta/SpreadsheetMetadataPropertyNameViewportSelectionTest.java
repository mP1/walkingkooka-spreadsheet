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
import walkingkooka.locale.LocaleContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameViewportSelectionTest extends SpreadsheetMetadataPropertyNameTestCase<
    SpreadsheetMetadataPropertyNameViewportSelection,
    AnchoredSpreadsheetSelection> {

    @Test
    public void testCheckValueWithCell() {
        this.checkValue(
            SpreadsheetSelection.A1.setDefaultAnchor()
        );
    }

    @Test
    public void testCheckValueWithCellRange() {
        this.checkValue(
            SpreadsheetSelection.parseCellRange("B2:C3")
                .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
        );
    }

    @Test
    public void testCheckValueWithColumn() {
        this.checkValue(
            SpreadsheetSelection.A1.column()
                .setDefaultAnchor()
        );
    }

    @Test
    public void testCheckValueWithColumnRange() {
        this.checkValue(
            SpreadsheetSelection.parseColumnRange("B:C")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testCheckValueWithLabel() {
        this.checkValue(
            SpreadsheetSelection.labelName("Label123")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testCheckValueWithRow() {
        this.checkValue(
            SpreadsheetSelection.A1.row()
                .setDefaultAnchor()
        );
    }

    @Test
    public void testCheckValueWithRowRange() {
        this.checkValue(
            SpreadsheetSelection.parseRowRange("4:5")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(Locale.ENGLISH),
            null
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameViewportSelection.instance(),
            "viewportSelection"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameViewportSelection createName() {
        return SpreadsheetMetadataPropertyNameViewportSelection.instance();
    }

    @Override
    AnchoredSpreadsheetSelection propertyValue() {
        return SpreadsheetSelection.A1.setDefaultAnchor();
    }

    @Override
    String propertyValueType() {
        return AnchoredSpreadsheetSelection.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameViewportSelection> type() {
        return SpreadsheetMetadataPropertyNameViewportSelection.class;
    }
}
