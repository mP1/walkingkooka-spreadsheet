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
    public void testCheckValueWithCellRange() {
        this.checkValue(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("A1:B2")
                            .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                    )
                )
        );
    }

    @Test
    public void testCheckValueWithColumn() {
        this.checkValue(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumn("AB")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testCheckValueWithLabel() {
        this.checkValue(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetLabelName.labelName("Label123")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testCheckValueWithRow() {
        this.checkValue(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRow("1234")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testCheckValueWithInvalidFails3() {
        this.checkValueFails(
            "invalid",
            "Metadata viewport=\"invalid\", Expected SpreadsheetViewport"
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
            .setAnchoredSelection(
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

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameViewport> type() {
        return SpreadsheetMetadataPropertyNameViewport.class;
    }
}
