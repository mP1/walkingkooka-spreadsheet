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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameViewportCellTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameViewportCell, SpreadsheetCellReference> {

    @Test
    public void testInvalidSpreadsheetCellReferenceFails() {
        this.checkValueFails("invalid", "Expected SpreadsheetCellReference, but got \"invalid\" for \"" + this.createName().value() + "\"");
    }

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameViewportCell.instance(), "viewport-cell");
    }

    @Override
    SpreadsheetMetadataPropertyNameViewportCell createName() {
        return SpreadsheetMetadataPropertyNameViewportCell.instance();
    }

    @Override
    SpreadsheetCellReference propertyValue() {
        return SpreadsheetSelection.parseCell("B99");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetCellReference.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameViewportCell> type() {
        return SpreadsheetMetadataPropertyNameViewportCell.class;
    }
}
