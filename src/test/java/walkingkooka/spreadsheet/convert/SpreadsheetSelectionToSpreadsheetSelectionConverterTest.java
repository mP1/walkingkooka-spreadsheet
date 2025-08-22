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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetSelectionToSpreadsheetSelectionConverterTest implements ConverterTesting2<SpreadsheetSelectionToSpreadsheetSelectionConverter, SpreadsheetConverterContext>,
    ToStringTesting<SpreadsheetSelectionToSpreadsheetSelectionConverter> {
    
    // cell.............................................................................................................

    private final static SpreadsheetCellReference CELL = SpreadsheetSelection.A1;

    @Test
    public void testConvertCellToSpreadsheetSelection() {
        this.convertAndCheck(
            CELL,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertCellToCell() {
        this.convertAndCheck(
            CELL
        );
    }

    @Test
    public void testConvertCellToCellOrCellRange() {
        this.convertAndCheck(
            CELL,
            SpreadsheetCellReferenceOrRange.class
        );
    }

    @Test
    public void testConvertCellToCellRange() {
        this.convertAndCheck(
            CELL,
            CELL.toCellRange()
        );
    }

    @Test
    public void testConvertCellToColumn() {
        this.convertAndCheck(
            CELL,
            CELL.toColumn()
        );
    }

    @Test
    public void testConvertCellToColumnRange() {
        this.convertAndCheck(
            CELL,
            CELL.toColumnRange()
        );
    }

    @Test
    public void testConvertCellToLabelFails() {
        this.convertFails(
            CELL,
            SpreadsheetLabelName.class
        );
    }

    @Test
    public void testConvertCellToRow() {
        this.convertAndCheck(
            CELL,
            CELL.toRow()
        );
    }

    @Test
    public void testConvertCellToRowRange() {
        this.convertAndCheck(
            CELL,
            CELL.toRowRange()
        );
    }

    // cellRange........................................................................................................

    private final static SpreadsheetCellRangeReference CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
    
    @Test
    public void testConvertCellRangeToCell() {
        this.convertAndCheck(
            CELL_RANGE,
            CELL_RANGE.toCell()
        );
    }

    @Test
    public void testConvertCellRangeToCellRange() {
        this.convertAndCheck(
            CELL_RANGE
        );
    }

    @Test
    public void testConvertCellRangeToCellOrCellRange() {
        this.convertAndCheck(
            CELL_RANGE,
            SpreadsheetCellReferenceOrRange.class
        );
    }

    @Test
    public void testConvertCellRangeToSpreadsheetSelection() {
        this.convertAndCheck(
            CELL_RANGE,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertCellRangeToExpressionReference() {
        this.convertAndCheck(
            CELL_RANGE,
            SpreadsheetExpressionReference.class
        );
    }

    @Test
    public void testConvertCellRangeToColumn() {
        this.convertAndCheck(
            CELL_RANGE,
            CELL_RANGE.toColumn()
        );
    }

    @Test
    public void testConvertCellRangeToColumnRange() {
        this.convertAndCheck(
            CELL_RANGE,
            CELL_RANGE.toColumnRange()
        );
    }

    @Test
    public void testConvertCellRangeToRow() {
        this.convertAndCheck(
            CELL_RANGE,
            CELL_RANGE.toRow()
        );
    }

    @Test
    public void testConvertCellRangeToRowRange() {
        this.convertAndCheck(
            CELL_RANGE,
            CELL_RANGE.toRowRange()
        );
    }

    // column...........................................................................................................

    private final static SpreadsheetColumnReference COLUMN = SpreadsheetSelection.parseColumn("B");

    @Test
    public void testConvertColumnToSpreadsheetSelection() {
        this.convertAndCheck(
            COLUMN,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertColumnToCellFails() {
        this.convertFails(
            COLUMN,
            SpreadsheetCellReference.class
        );
    }

    @Test
    public void testConvertColumnToCellOrCellRange() {
        this.convertAndCheck(
            COLUMN,
            COLUMN.toCellOrCellRange()
        );
    }

    @Test
    public void testConvertColumnToCellRange() {
        this.convertAndCheck(
            COLUMN,
            COLUMN.toCellRange()
        );
    }

    @Test
    public void testConvertColumnToColumn() {
        this.convertAndCheck(
            COLUMN,
            COLUMN.toColumn()
        );
    }

    @Test
    public void testConvertColumnToColumnRange() {
        this.convertAndCheck(
            COLUMN,
            COLUMN.toColumnRange()
        );
    }

    @Test
    public void testConvertColumnToLabelFails() {
        this.convertFails(
            COLUMN,
            SpreadsheetLabelName.class
        );
    }

    @Test
    public void testConvertColumnToRowFails() {
        this.convertFails(
            COLUMN,
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testConvertColumnToRowRangeFails() {
        this.convertFails(
            COLUMN,
            SpreadsheetRowRangeReference.class
        );
    }

    // columnRange......................................................................................................

    private final static SpreadsheetColumnRangeReference COLUMN_RANGE = SpreadsheetSelection.parseColumnRange("C:D");

    @Test
    public void testConvertColumnRangeToCellFails() {
        this.convertFails(
            COLUMN_RANGE,
            SpreadsheetCellReference.class
        );
    }

    @Test
    public void testConvertColumnRangeToCellRange() {
        this.convertAndCheck(
            COLUMN_RANGE,
            COLUMN_RANGE.toCellRange()
        );
    }

    @Test
    public void testConvertColumnRangeToCellOrCellRange() {
        this.convertAndCheck(
            COLUMN_RANGE,
            SpreadsheetCellReferenceOrRange.class,
            COLUMN_RANGE.toCellRange()
        );
    }

    @Test
    public void testConvertColumnRangeToSpreadsheetSelection() {
        this.convertAndCheck(
            COLUMN_RANGE,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertColumnRangeToExpressionReference() {
        this.convertAndCheck(
            COLUMN_RANGE,
            SpreadsheetExpressionReference.class,
            COLUMN_RANGE.toExpressionReference()
        );
    }

    @Test
    public void testConvertColumnRangeToColumn() {
        this.convertAndCheck(
            COLUMN_RANGE,
            COLUMN_RANGE.toColumn()
        );
    }

    @Test
    public void testConvertColumnRangeToColumnRange() {
        this.convertAndCheck(
            COLUMN_RANGE,
            COLUMN_RANGE.toColumnRange()
        );
    }

    @Test
    public void testConvertColumnRangeToRowFails() {
        this.convertFails(
            COLUMN_RANGE,
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testConvertColumnRangeToRowRangeFails() {
        this.convertFails(
            COLUMN_RANGE,
            SpreadsheetRowRangeReference.class
        );
    }

    @Test
    public void testConvertColumnRangeToRowOrRowRangeFails() {
        this.convertFails(
            COLUMN_RANGE,
            SpreadsheetRowReferenceOrRange.class
        );
    }

    // row..............................................................................................................

    private final static SpreadsheetRowReference ROW = SpreadsheetSelection.parseRow("4");

    @Test
    public void testConvertRowToSpreadsheetSelection() {
        this.convertAndCheck(
            ROW,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertRowToCellFails() {
        this.convertFails(
            ROW,
            SpreadsheetCellReference.class
        );
    }

    @Test
    public void testConvertRowToCellOrCellRange() {
        this.convertAndCheck(
            ROW,
            SpreadsheetCellReferenceOrRange.class,
            ROW.toCellOrCellRange()
        );
    }

    @Test
    public void testConvertRowToCellRange() {
        this.convertAndCheck(
            ROW,
            ROW.toCellRange()
        );
    }

    @Test
    public void testConvertRowToColumnFails() {
        this.convertFails(
            ROW,
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testConvertRowToColumnRangeFails() {
        this.convertFails(
            ROW,
            SpreadsheetColumnRangeReference.class
        );
    }

    @Test
    public void testConvertRowToColumnOrColumnRangeFails() {
        this.convertFails(
            ROW,
            SpreadsheetColumnReferenceOrRange.class
        );
    }

    @Test
    public void testConvertRowToLabelFails() {
        this.convertFails(
            ROW,
            SpreadsheetLabelName.class
        );
    }

    @Test
    public void testConvertRowToRow() {
        this.convertAndCheck(
            ROW,
            ROW.toRow()
        );
    }

    @Test
    public void testConvertRowToRowRange() {
        this.convertAndCheck(
            ROW,
            ROW.toRowRange()
        );
    }

    // rowRange.........................................................................................................

    private final static SpreadsheetRowRangeReference ROW_RANGE = SpreadsheetSelection.parseRowRange("5:6");

    @Test
    public void testConvertRowRangeToCellFails() {
        this.convertFails(
            ROW_RANGE,
            SpreadsheetCellReference.class
        );
    }

    @Test
    public void testConvertRowRangeToCellRange() {
        this.convertAndCheck(
            ROW_RANGE,
            ROW_RANGE.toCellRange()
        );
    }

    @Test
    public void testConvertRowRangeToCellOrCellRange() {
        this.convertAndCheck(
            ROW_RANGE,
            SpreadsheetCellReferenceOrRange.class,
            ROW_RANGE.toCellRange()
        );
    }

    @Test
    public void testConvertRowRangeToSpreadsheetSelection() {
        this.convertAndCheck(
            ROW_RANGE,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertRowRangeToExpressionReference() {
        this.convertAndCheck(
            ROW_RANGE,
            SpreadsheetExpressionReference.class,
            ROW_RANGE.toExpressionReference()
        );
    }

    @Test
    public void testConvertRowRangeToColumnFails() {
        this.convertFails(
            ROW_RANGE,
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testConvertRowRangeToColumnRangeFails() {
        this.convertFails(
            ROW_RANGE,
            SpreadsheetColumnRangeReference.class
        );
    }

    @Test
    public void testConvertRowRangeToColumnOrColumnRangeFails() {
        this.convertFails(
            ROW_RANGE,
            SpreadsheetColumnReferenceOrRange.class
        );
    }

    @Test
    public void testConvertRowRangeToRow() {
        this.convertAndCheck(
            ROW_RANGE,
            ROW_RANGE.toRow()
        );
    }

    @Test
    public void testConvertRowRangeToRowRange() {
        this.convertAndCheck(
            ROW_RANGE,
            ROW_RANGE.toRowRange()
        );
    }


    // Converter........................................................................................................

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
