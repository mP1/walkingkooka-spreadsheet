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
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportAnchor;

import java.util.Locale;
import java.util.Optional;


public final class SpreadsheetMetadataPropertyNameViewportTest extends SpreadsheetMetadataPropertyNameTestCase<
        SpreadsheetMetadataPropertyNameViewport,
        SpreadsheetViewport> {

    @Test
    public void testCheckCellRange() {
        this.checkValue(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                                )
                        )
        );
    }

    @Test
    public void testCheckColumn() {
        this.checkValue(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseColumn("AB")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testCheckLabel() {
        this.checkValue(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetLabelName.labelName("Label123")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testCheckRow() {
        this.checkValue(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseRow("1234")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testInvalidValueFails() {
        this.checkValueFails(
                "invalid",
                "Expected SpreadsheetViewport, but got \"invalid\" for \"viewport\""
        );
    }

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameViewport.instance(),
                "viewport"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameViewport createName() {
        return SpreadsheetMetadataPropertyNameViewport.instance();
    }

    @Override
    SpreadsheetViewport propertyValue() {
        return SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setSelection(
                        Optional.of(
                                SpreadsheetSelection.parseCell("B99")
                                        .setDefaultAnchor()
                        )
                );
    }

    @Override
    String propertyValueType() {
        return SpreadsheetViewport.class.getSimpleName();
    }

    @Override
    String urlFragment() {
        return "/viewport";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameViewport> type() {
        return SpreadsheetMetadataPropertyNameViewport.class;
    }
}
