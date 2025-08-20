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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameViewportHomeTest extends SpreadsheetMetadataPropertyNameTestCase<
    SpreadsheetMetadataPropertyNameViewportHome,
    SpreadsheetCellReference> {

    @Test
    public void testCheckValueWithCell() {
        this.checkValue(
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testCheckValueWithCellRangeFails() {
        this.checkValueFails(
            SpreadsheetSelection.parseCellRange("B2:C3"),
            "Metadata viewportHome=B2:C3, Expected SpreadsheetCellReference"
        );
    }

    @Test
    public void testCheckValueWithColumnFails() {
        this.checkValueFails(
            SpreadsheetSelection.A1.column(),
            "Metadata viewportHome=A, Expected SpreadsheetCellReference"
        );
    }

    @Test
    public void testCheckValueWithLabel() {
        this.checkValueFails(
            SpreadsheetSelection.labelName("Label123"),
            "Metadata viewportHome=Label123, Expected SpreadsheetCellReference"
        );
    }

    @Test
    public void testCheckValueWithRowFails() {
        this.checkValueFails(
            SpreadsheetSelection.A1.row(),
            "Metadata viewportHome=1, Expected SpreadsheetCellReference"
        );
    }

    @Test
    public void testCheckValueWithRowRangeFails() {
        this.checkValueFails(
            SpreadsheetSelection.parseRowRange("3:4"),
            "Metadata viewportHome=3:4, Expected SpreadsheetCellReference"
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
            SpreadsheetMetadataPropertyNameViewportHome.instance(),
            "viewportHome"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameViewportHome createName() {
        return SpreadsheetMetadataPropertyNameViewportHome.instance();
    }

    @Override
    SpreadsheetCellReference propertyValue() {
        return SpreadsheetSelection.A1;
    }

    @Override
    String propertyValueType() {
        return SpreadsheetCellReference.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameViewportHome> type() {
        return SpreadsheetMetadataPropertyNameViewportHome.class;
    }
}
