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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetSelectionToSpreadsheetSelectionConverterTest implements ConverterTesting2<SpreadsheetSelectionToSpreadsheetSelectionConverter, SpreadsheetConverterContext>,
    ToStringTesting<SpreadsheetSelectionToSpreadsheetSelectionConverter> {

    @Test
    public void testConvertCellToSpreadsheetSelection() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCell("Z99"),
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertCellToCellRange() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCell("Z99"),
            SpreadsheetSelection.parseCellRange("Z99")
        );
    }

    @Test
    public void testConvertCellRangeToCell() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3"),
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testConvertCellRangeToSpreadsheetSelection() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3"),
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertCellToExpressionReference() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCell("Z99"),
            SpreadsheetExpressionReference.class
        );
    }

    @Test
    public void testConvertCellRangeToExpressionReference() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3"),
            SpreadsheetExpressionReference.class
        );
    }

    @Override
    public SpreadsheetSelectionToSpreadsheetSelectionConverter createConverter() {
        return SpreadsheetSelectionToSpreadsheetSelectionConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetSelectionToSpreadsheetSelectionConverter.INSTANCE,
            "selection to selection"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetSelectionToSpreadsheetSelectionConverter> type() {
        return SpreadsheetSelectionToSpreadsheetSelectionConverter.class;
    }
}
