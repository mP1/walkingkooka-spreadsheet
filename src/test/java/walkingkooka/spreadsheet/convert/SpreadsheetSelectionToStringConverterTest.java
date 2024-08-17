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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetSelectionToStringConverterTest implements ConverterTesting2<SpreadsheetSelectionToStringConverter, SpreadsheetConverterContext>,
        ToStringTesting<SpreadsheetSelectionToStringConverter> {

    @Test
    public void testConvertSpreadsheetCellToString() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("$A$1"),
                "$A$1"
        );
    }

    @Test
    public void testConvertSpreadsheetCellRangeToString() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                "A1:B2"
        );
    }

    @Test
    public void testConvertSpreadsheetLabelNameToString() {
        this.convertAndCheck(
                SpreadsheetSelection.labelName("Label123"),
                "Label123"
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellFails() {
        this.convertFails(
                "A1",
                SpreadsheetCell.class
        );
    }

    @Override
    public SpreadsheetSelectionToStringConverter createConverter() {
        return SpreadsheetSelectionToStringConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetSelectionToStringConverter.INSTANCE,
                "Selection to String"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetSelectionToStringConverter> type() {
        return SpreadsheetSelectionToStringConverter.class;
    }
}
