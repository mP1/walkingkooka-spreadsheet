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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameCellTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameCell, SpreadsheetCellReferenceOrLabelName> {

    @Test
    public void testCheckLabel() {
        this.checkValue(SpreadsheetLabelName.labelName("Label123"));
    }

    @Test
    public final void testInvalidSpreadsheetCellReferenceFails() {
        this.checkValueFails("invalid", "Expected SpreadsheetCellReference, but got \"invalid\" for \"" + this.createName().value() + "\"");
    }

    @Test
    public final void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameCell.instance(), "cell");
    }

    @Override
    SpreadsheetMetadataPropertyNameCell createName() {
        return SpreadsheetMetadataPropertyNameCell.instance();
    }

    @Override
    final SpreadsheetCellReference propertyValue() {
        return SpreadsheetCellReference.parseCellReference("B99");
    }

    @Override
    final String propertyValueType() {
        return SpreadsheetCellReference.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameCell> type() {
        return SpreadsheetMetadataPropertyNameCell.class;
    }
}
