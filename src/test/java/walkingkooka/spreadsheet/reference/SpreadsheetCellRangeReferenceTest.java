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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.Range;
import walkingkooka.collect.iterable.IterableTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeReferenceTest extends SpreadsheetCellReferenceOrRangeTestCase<SpreadsheetCellRangeReference>
    implements ComparableTesting2<SpreadsheetCellRangeReference>,
    CanReplaceReferencesTesting<SpreadsheetCellRangeReference>,
    IterableTesting<SpreadsheetCellRangeReference, SpreadsheetCellReference> {

    private final static int COLUMN1 = 10;
    private final static int ROW1 = 11;
    private final static int COLUMN2 = 20;
    private final static int ROW2 = 21;

    @Test
    public void testWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellRangeReference.with(null));
    }

    @Test
    public void testWithRangeAllFails() {
        this.withFails(Range.all());
    }

    @Test
    public void testWithRangeLessThanEqualsFails() {
        this.withFails(Range.lessThanEquals(this.cellReference(5, 5)));
    }

    @Test
    public void testWithRangeGreaterThanEqualsFails() {
        this.withFails(Range.greaterThanEquals(this.cellReference(1, 1)));
    }

    @Test
    public void testWithRangeLowerExclusiveFails() {
        this.withFails(Range.greaterThan(this.cellReference(1, 1))
            .and(Range.lessThanEquals(this.cellReference(5, 5))));
    }

    @Test
    public void testWithRangeUpperExclusiveFails() {
        this.withFails(Range.greaterThanEquals(this.cellReference(1, 1))
            .and(Range.lessThan(this.cellReference(5, 5))));
    }

    private void withFails(final Range<SpreadsheetCellReference> range) {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellRangeReference.with(range));
    }

    @Test
    public void testWith() {
        final SpreadsheetCellReference begin = this.cellReference(1, 2);
        final SpreadsheetCellReference end = this.cellReference(3, 4);
        final Range<SpreadsheetCellReference> range = begin.range(end);

        final SpreadsheetCellRangeReference spreadsheetCellRangeReference = SpreadsheetCellRangeReference.with(range);
        assertSame(range, spreadsheetCellRangeReference.range(), "range");
        this.checkEquals(begin, spreadsheetCellRangeReference.begin(), "begin");
        this.checkEquals(end, spreadsheetCellRangeReference.end(), "end");
        this.isUnitAndCheck(spreadsheetCellRangeReference, false);
    }

    @Test
    public void testWith2() {
        final int column1 = 99;
        final int row1 = 2;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetCellRangeReference range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row1, column1, row2, 99 - 3 + 1, 4 - 2 + 1);
        this.isUnitAndCheck(range, false);
    }

    @Test
    public void testWith3() {
        final int column1 = 1;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetCellRangeReference range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row2, column2, row1, 3 - 1 + 1, 99 - 4 + 1);
        this.isUnitAndCheck(range, false);
    }

    @Test
    public void testWith4() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetCellRangeReference range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row2, column1, row1, 88 - 3 + 1, 99 - 4 + 1);
        this.isUnitAndCheck(range, false);
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3"),
            "Cell Range not found: \"B2:C3\""
        );
    }

    // isUnit.....................................................................................................

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testIsUnitTrue() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = column1;
        final int row2 = row1;

        final SpreadsheetCellRangeReference range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row1, column2, row2, 1, 1);
        this.isUnitAndCheck(range, true);
    }

    @Test
    public void testIsUnitFalse() {
        final int column1 = 66;
        final int row1 = 77;
        final int column2 = 88;
        final int row2 = 99;

        final SpreadsheetCellRangeReference range = this.range(column1, row1, column2, row2);
        this.isUnitAndCheck(range, false);
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("A1:B2");
    }

    @Test
    public void testTextAll() {
        this.textAndCheck(
            SpreadsheetSelection.ALL_CELLS,
            "*"
        );
    }

    // count............................................................................................................

    @Test
    public void testCountA1() {
        this.countAndCheck("A1:A1", 1);
    }

    @Test
    public void testCountB2() {
        this.countAndCheck("B2:B2", 1);
    }

    @Test
    public void testCountC3D4() {
        this.countAndCheck("C3:D4", 4);
    }

    @Test
    public void testCountE5F5() {
        this.countAndCheck("E5:F5", 2);
    }

    @Test
    public void testCountAllCellsConstant() {
        this.countAndCheck(
            SpreadsheetSelection.ALL_CELLS,
            17179869184L
        );
    }

    // isAll..........................................................................................................

    @Test
    public void testIsAllA1() {
        this.isAllAndCheck("A1", false);
    }

    @Test
    public void testIsAll2() {
        this.isAllAndCheck(
            "A1:" + SpreadsheetReferenceKind.RELATIVE.lastColumn() + SpreadsheetReferenceKind.RELATIVE.lastRow(),
            true
        );
    }

    @Test
    public void testIsAll3() {
        this.isAllAndCheck(
            "B2:" + SpreadsheetReferenceKind.RELATIVE.lastColumn() + SpreadsheetReferenceKind.RELATIVE.lastRow(),
            false
        );
    }

    // isFirst..........................................................................................................

    @Test
    public void testIsFirstA1() {
        this.isFirstAndCheck("A1", true);
    }

    @Test
    public void testIsFirstA1Absolute() {
        this.isFirstAndCheck("$A$1", true);
    }

    @Test
    public void testIsFirstA2() {
        this.isFirstAndCheck("A2", false);
    }

    @Test
    public void testIsFirstA1A2() {
        this.isFirstAndCheck("A1:A2", false);
    }

    // isLast..........................................................................................................

    @Test
    public void testIsLastA1() {
        this.isLastAndCheck("A1", false);
    }

    @Test
    public void testIsLastA1Absolute() {
        this.isLastAndCheck("$A$1", false);
    }

    @Test
    public void testIsLastEndsLast() {
        this.isLastAndCheck(
            "A1:" + SpreadsheetReferenceKind.RELATIVE.lastColumn() + SpreadsheetReferenceKind.RELATIVE.lastRow(),
            false
        );
    }

    @Test
    public void testIsLast() {
        this.isLastAndCheck(
            "" + SpreadsheetReferenceKind.RELATIVE.lastColumn() + SpreadsheetReferenceKind.RELATIVE.lastRow(),
            true
        );
    }

    // setRange.....................................................................................

    @Test
    public void testSetRangeWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> this.range().setRange(null));
    }

    @Test
    public void testSetRangeWithSame() {
        final SpreadsheetCellRangeReference range = this.range();
        assertSame(range, range.setRange(this.begin().cellRange(this.end()).range()));
    }

    @Test
    public void testSetRangeWithDifferent() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetCellReference differentBegin = this.cellReference(1, 2);
        final SpreadsheetCellRangeReference different = range.setRange(differentBegin.range(this.end()));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetRangeWithDifferent2() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetCellRangeReference different = range.setRange(this.end().range(this.cellReference(1, 2)));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetRangeWithDifferent3() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetCellRangeReference different = range.setRange(this.begin().range(this.cellReference(88, 99)));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent4() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetCellRangeReference different = range.setRange(this.cellReference(88, 99).range(this.begin()));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent5() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetCellRangeReference different = range.setRange(this.cellReference(1, 2).range(this.cellReference(88, 99)));
        this.check(different, 1, 2, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent6() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetCellRangeReference different = range.setRange(this.cellReference(88, 99).range(this.cellReference(1, 2)));
        this.check(different, 1, 2, 88, 99);
    }

    // columnRange.....................................................................................................

    @Test
    public void testColumnRange() {
        this.columnRangeAndCheck("B2:D4", "B:D");
    }

    @Test
    public void testColumnRangeSingleton() {
        this.columnRangeAndCheck("B2:B4", "B");
    }

    private void columnRangeAndCheck(final String cell,
                                     final String column) {
        this.checkEquals(
            SpreadsheetSelection.parseColumnRange(column),
            SpreadsheetSelection.parseCellRange(cell).columnRange(),
            () -> cell + ".columnRange()"
        );
    }

    // SetColumnRange..................................................................................................

    @Test
    public void testSetColumnRangeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSelection().setColumnRange(null)
        );
    }

    @Test
    public void testSetColumnRangeSame() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:D4");
        assertSame(range, range.setColumnRange(SpreadsheetSelection.parseColumnRange("B:D")));
    }

    @Test
    public void testSetColumnRangeDifferent() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:D4");
        final SpreadsheetColumnRangeReference columns = SpreadsheetSelection.parseColumnRange("F:G");
        final SpreadsheetCellRangeReference different = range.setColumnRange(columns);

        assertNotSame(range, different);
        this.checkEquals(
            SpreadsheetSelection.parseCellRange("F2:G4"),
            different
        );

        this.checkEquals(columns, different.columnRange());
    }

    // rowRange.........................................................................................................

    @Test
    public void testRowRangeReference() {
        this.rowRangeAndCheck("B2:D4", "2:4");
    }

    @Test
    public void testRowRangeSingleton() {
        this.rowRangeAndCheck("B2:D2", "2");
    }

    private void rowRangeAndCheck(final String cell,
                                  final String row) {
        this.checkEquals(
            SpreadsheetSelection.parseRowRange(row),
            SpreadsheetSelection.parseCellRange(cell).rowRange(),
            () -> cell + ".rowRange()"
        );
    }

    // SetRowRange......................................................................................................

    @Test
    public void testSetRowRangeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSelection().setRowRange(null)
        );
    }

    @Test
    public void testSetRowRangeSame() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:D4");
        assertSame(range, range.setRowRange(SpreadsheetSelection.parseRowRange("2:4")));
    }

    @Test
    public void testSetRowRangeDifferent() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:D4");
        final SpreadsheetRowRangeReference rows = SpreadsheetSelection.parseRowRange("6:7");
        final SpreadsheetCellRangeReference different = range.setRowRange(rows);

        assertNotSame(range, different);
        this.checkEquals(
            SpreadsheetSelection.parseCellRange("B6:D7"),
            different
        );

        this.checkEquals(rows, different.rowRange());
    }

    // add column/row..................................................................................................

    @Test
    public void testAddColumnAndRow() {
        this.addColumnRowAndCheck(
            SpreadsheetSelection.parseCellRange("A1"),
            1,
            1,
            SpreadsheetSelection.parseCellRange("B2")
        );
    }

    @Test
    public void testAddColumnAndRow2() {
        this.addColumnRowAndCheck(
            SpreadsheetSelection.parseCellRange("B2"),
            1,
            0,
            SpreadsheetSelection.parseCellRange("C2")
        );
    }

    @Test
    public void testAddColumnAndRow3() {
        this.addColumnRowAndCheck(
            SpreadsheetSelection.parseCellRange("C3"),
            0,
            3,
            SpreadsheetSelection.parseCellRange("C6")
        );
    }

    @Test
    public void testAddSaturatedColumnAndRow() {
        this.addSaturatedColumnRowAndCheck(
            SpreadsheetSelection.parseCellRange("A1"),
            1,
            1,
            SpreadsheetSelection.parseCellRange("B2")
        );
    }

    @Test
    public void testAddSaturatedColumnAndRow2() {
        this.addSaturatedColumnRowAndCheck(
            SpreadsheetSelection.parseCellRange("B2"),
            1,
            0,
            SpreadsheetSelection.parseCellRange("C2")
        );
    }

    @Test
    public void testAddSaturatedColumnAndRow3() {
        this.addSaturatedColumnRowAndCheck(
            SpreadsheetSelection.parseCellRange("C3"),
            0,
            3,
            SpreadsheetSelection.parseCellRange("C6")
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeAbsolute() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCellRange("$A$2:$C$4"),
            1,
            2
        );
    }

    @Test
    public void testAddIfRelativeMixed() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCellRange("A$2:$C$4"),
            1,
            0,
            SpreadsheetSelection.parseCellRange("B$2:$C$4")
        );
    }

    @Test
    public void testAddIfRelativeMixed2() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCellRange("A2:$C$4"),
            1,
            1,
            SpreadsheetSelection.parseCellRange("B3:$C$4")
        );
    }

    @Test
    public void testAddIfRelativeMixed3() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCellRange("$A$2:C4"),
            1,
            2,
            SpreadsheetSelection.parseCellRange("$A$2:D6")
        );
    }

    @Test
    public void testAddIfRelativeAll() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCellRange("A2:D5"),
            1,
            2,
            SpreadsheetSelection.parseCellRange("B4:E7")
        );
    }

    // replaceReferencesMapper..........................................................................................

    @Test
    public void testReplaceReferencesMapperCell() {
        this.replaceReferencesMapperAndCheck(
            "A1:B2",
            SpreadsheetSelection.parseCell("B4"),
            1,
            3
        );
    }

    @Test
    public void testReplaceReferencesMapperCell2() {
        this.replaceReferencesMapperAndCheck(
            "B4:C5",
            SpreadsheetSelection.A1,
            -1,
            -3
        );
    }

    @Test
    public void testReplaceReferencesMapperCell3() {
        this.replaceReferencesMapperAndCheck(
            "A1:A1",
            SpreadsheetSelection.A1,
            0,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperColumn() {
        this.replaceReferencesMapperAndCheck(
            "B4:C5",
            SpreadsheetSelection.parseColumn("E"),
            3,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperRow() {
        this.replaceReferencesMapperAndCheck(
            "B4:C5",
            SpreadsheetSelection.parseRow("8"),
            0,
            4
        );
    }

    @Test
    public void testReplaceReferencesMapperCellRange() {
        this.replaceReferencesMapperAndCheck(
            "A1:C4",
            SpreadsheetSelection.parseCellRange("B4:D9"),
            1,
            3
        );
    }

    // CanReferenceReplace..............................................................................................

    @Test
    public void testReplaceReferenceEmptyBeginFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createReplaceReference()
                .replaceReferences(
                    (r) -> Optional.empty()
                )
        );
    }

    @Test
    public void testReplaceReferenceEmptyEndFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createReplaceReference()
                .replaceReferences(
                    (r) -> Optional.ofNullable(
                        r.toString().equals("B2") ?
                            r :
                            null
                    )
                )
        );
    }

    @Test
    public void testReplaceReferenceSame() {
        this.replaceReferencesAndCheck(
            this.createReplaceReference(),
            Optional::of
        );
    }

    @Test
    public void testReplaceReferenceChange() {
        this.replaceReferencesAndCheck(
            "B2:C3",
            "B2",
            "A1",
            "C3",
            "B2",
            "A1:B2"
        );
    }

    @Test
    public void testReplaceReferenceChange2() {
        this.replaceReferencesAndCheck(
            "B2:C3",
            "B2",
            "D4",
            "C3",
            "E5",
            "D4:E5"
        );
    }

    @Test
    public void testReplaceReferenceSwap() {
        this.replaceReferencesAndCheck(
            "B2:C3",
            "B2",
            "D4",
            "C3",
            "A1",
            "A1:D4"
        );
    }

    @Test
    public void testReplaceReferenceSwap2() {
        this.replaceReferencesAndCheck(
            "B2:C3",
            "B2",
            "D1",
            "C3",
            "A5",
            "A1:D5"
        );
    }

    private void replaceReferencesAndCheck(final String range,
                                           final String begin,
                                           final String beginReplacement,
                                           final String end,
                                           final String endReplacement,
                                           final String expected) {
        this.replaceReferencesAndCheck(
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCell(begin),
            SpreadsheetSelection.parseCell(beginReplacement),
            SpreadsheetSelection.parseCell(end),
            SpreadsheetSelection.parseCell(endReplacement),
            SpreadsheetSelection.parseCellRange(expected)
        );
    }

    private void replaceReferencesAndCheck(final SpreadsheetCellRangeReference range,
                                           final SpreadsheetCellReference begin,
                                           final SpreadsheetCellReference beginReplacement,
                                           final SpreadsheetCellReference end,
                                           final SpreadsheetCellReference endReplacement,
                                           final SpreadsheetCellRangeReference expected) {
        this.replaceReferencesAndCheck(
            range,
            (r) -> {
                if (r.equals(begin)) {
                    return Optional.of(
                        beginReplacement
                    );
                }
                if (r.equals(end)) {
                    return Optional.of(
                        endReplacement
                    );
                }
                throw new IllegalArgumentException(r.toString());
            },
            expected
        );
    }

    @Override
    public SpreadsheetCellRangeReference createReplaceReference() {
        return SpreadsheetCellRangeReference.parseCellRange("B2:C3");
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public void testComparatorNamesBoundsCheckWithColumnComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "A1:B2",
            "B=TEXT;C=TEXT;ZZ=TEXT",
            "Invalid column(s) C, ZZ are not within A1:B2"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRowComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "A1:B2",
            "2=TEXT;3=TEXT;99=TEXT",
            "Invalid row(s) 3, 99 are not within A1:B2"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithColumns() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A1:C3",
            "A=text UP;B=text DOWN"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRows() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A1:C3",
            "2=text UP;3=text DOWN"
        );
    }

    // toScalar.........................................................................................................

    @Test
    public void testToScalar() {
        this.toScalarAndCheck(
            "A1:B2",
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testToScalar2() {
        this.toScalarAndCheck(
            "B2:C3",
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testToScalar3() {
        this.toScalarAndCheck(
            "$C$3:D4",
            SpreadsheetSelection.parseCell("$C$3")
        );
    }

    // toCell...........................................................................................................

    @Test
    public void testToCell() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:C3");

        this.toCellAndCheck(
            range,
            SpreadsheetSelection.parseCell("B2")
        );
    }

    // toCellRangeResolvingLabels.......................................................................................

    @Test
    public void testToCellRange() {
        this.toCellRangeAndCheck(
            "A1:B2",
            "A1:B2"
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
            "C3",
            "C3"
        );
    }

    // toColumn.........................................................................................................

    @Test
    public void testToColumn() {
        this.toColumnAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseColumn("A")
        );
    }

    // toColumnRange....................................................................................................

    @Test
    public void testToColumnRange() {
        this.toColumnRangeAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    // toColumnOrColumnRange............................................................................................

    @Test
    public void testToColumnOrColumnRange() {
        final SpreadsheetCellRangeReference selection = this.createSelection();
        this.toColumnOrColumnRangeAndCheck(
            selection,
            selection.toColumnRange()
        );
    }

    // toRow............................................................................................................

    @Test
    public void testToRow() {
        this.toRowAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseRow("1")
        );
    }

    // toRowRange.......................................................................................................

    @Test
    public void testToRowRange() {
        this.toRowRangeAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseRowRange("1:2")
        );
    }

    @Test
    public void testToRowRange2() {
        this.toRowRangeAndCheck(
            SpreadsheetSelection.parseCellRange("C3:D4"),
            SpreadsheetSelection.parseRowRange("3:4")
        );
    }

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRange() {
        final SpreadsheetCellRangeReference selection = this.createSelection();
        this.toRowOrRowRangeAndCheck(
            selection,
            selection.toRowRange()
        );
    }

    // test.............................................................................................................

    @Test
    public void testTestWithCellOutOfRange() {
        this.testFalse(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCell("C3")
        );
    }

    @Test
    public void testTestWithCellInsideRange() {
        this.testTrue(
            SpreadsheetSelection.parseCellRange("C3:D4"),
            SpreadsheetSelection.parseCell("C3")
        );
    }

    @Test
    public void testtestEqualsDifferentColumn() {
        this.testFalse(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testtestEqualsDifferentRow() {
        this.testFalse(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseRow("3")
        );
    }

    // testCell.........................................................................................................

    @Test
    public void testTestCellSingletonTopLeft() {
        this.testCellAndCheckFalse("C3", "B2");
    }

    @Test
    public void testTestCellSingletonTop() {
        this.testCellAndCheckFalse("C3", "B3");
    }

    @Test
    public void testTestCellSingletonTopRight() {
        this.testCellAndCheckFalse("C3", "B4");
    }

    @Test
    public void testTestCellSingletonLeft() {
        this.testCellAndCheckFalse("C3", "B3");
    }

    @Test
    public void testTestCellSingleton() {
        this.testCellAndCheckTrue("C3", "C3");
    }

    @Test
    public void testTestCellSingletonRight() {
        this.testCellAndCheckFalse("C3", "D3");
    }

    @Test
    public void testTestCellSingletonBottomLeft() {
        this.testCellAndCheckFalse("C3", "D2");
    }

    @Test
    public void testTestCellSingletonBottom() {
        this.testCellAndCheckFalse("C3", "D3");
    }

    @Test
    public void testTestCellSingletonBottomRight() {
        this.testCellAndCheckFalse("C3", "D4");
    }

    @Test
    public void testTestCellTopLeft() {
        this.testCellAndCheckFalse("C3:E5", "B2");
    }

    @Test
    public void testTestCellTop() {
        this.testCellAndCheckFalse("C3:E5", "B2");
    }

    @Test
    public void testTestCellTopRight() {
        this.testCellAndCheckFalse("C3:E5", "B6");
    }

    @Test
    public void testTestCellLeft() {
        this.testCellAndCheckFalse("C3:E5", "B4");
    }

    @Test
    public void testTestCell() {
        this.testCellAndCheckTrue("C3:E5", "C3");
    }

    @Test
    public void testTestCell2() {
        this.testCellAndCheckTrue("C3:E5", "D3");
    }

    @Test
    public void testTestCell3() {
        this.testCellAndCheckTrue("C3:E5", "E3");
    }

    @Test
    public void testTestCell4() {
        this.testCellAndCheckTrue("C3:E5", "C4");
    }

    @Test
    public void testTestCell5() {
        this.testCellAndCheckTrue("C3:E5", "D4");
    }

    @Test
    public void testTestCell6() {
        this.testCellAndCheckTrue("C3:E5", "E4");
    }

    @Test
    public void testTestCell7() {
        this.testCellAndCheckTrue("C3:E5", "C5");
    }

    @Test
    public void testTestCell8() {
        this.testCellAndCheckTrue("C3:E5", "D5");
    }

    @Test
    public void testTestCell9() {
        this.testCellAndCheckTrue("C3:E5", "E5");
    }

    @Test
    public void testTestCellRight() {
        this.testCellAndCheckFalse("C3:E5", "D6");
    }

    @Test
    public void testTestCellBottomLeft() {
        this.testCellAndCheckFalse("C3:E5", "F2");
    }

    @Test
    public void testTestCellBottom() {
        this.testCellAndCheckFalse("C3:E5", "F4");
    }

    @Test
    public void testTestCellBottomRight() {
        this.testCellAndCheckFalse("C3:E5", "F6");
    }

    private void testCellAndCheckTrue(final String range,
                                      final String cell) {
        this.testCellAndCheck(
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCell(cell),
            true
        );
    }

    private void testCellAndCheckFalse(final String range,
                                       final String cell) {
        this.testCellAndCheck(
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCell(cell),
            false
        );
    }

    // testTestCellRange................................................................................................

    @Test
    public void testTestCellRangeLeft() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "A3:B3",
            false
        );
    }

    @Test
    public void testTestCellRangeRight() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "C5:C6",
            false
        );
    }

    @Test
    public void testTestCellRangeAboveLeft() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "A1:B2",
            false
        );
    }

    @Test
    public void testTestCellRangePartialLeft() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "C1:C3",
            true
        );
    }

    @Test
    public void testTestCellRangePartialRight() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "D4:F4",
            true
        );
    }

    @Test
    public void testTestCellRangePartialAbove() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "C1:C3",
            true
        );
    }

    @Test
    public void testTestCellRangePartialBelow() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "D4:D6",
            true
        );
    }

    @Test
    public void testTestCellRangePartialLeftAbove() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "A1:C3",
            true
        );
    }

    @Test
    public void testTestCellRangePartialRightBelow() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "D4:E5",
            true
        );
    }

    @Test
    public void testTestCellRangePartialInside() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "C3:C3",
            true
        );
    }

    @Test
    public void testTestCellRangePartialInside2() {
        this.testCellRangeAndCheck(
            "C3:D4",
            "C3:D4",
            true
        );
    }

    // testTestColumn.....................................................................................................

    @Test
    public void testTestColumnBefore() {
        this.testTestColumnAndCheck2(
            "C3:D4",
            "B",
            false
        );
    }

    @Test
    public void testTestColumnAfter() {
        this.testTestColumnAndCheck2(
            "C3:D4",
            "E",
            false
        );
    }

    @Test
    public void testTestColumnLeft() {
        this.testTestColumnAndCheck2(
            "C3:D4",
            "C",
            true
        );
    }

    @Test
    public void testTestColumnRight() {
        this.testTestColumnAndCheck2(
            "C3:D4",
            "D",
            true
        );
    }

    @Test
    public void testTestColumnWithin() {
        this.testTestColumnAndCheck2(
            "C3:E5",
            "D",
            true
        );
    }

    private void testTestColumnAndCheck2(final String range,
                                         final String column,
                                         final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetSelection.parseCellRange(range).testColumn(SpreadsheetSelection.parseColumn(column)),
            range + " testColumn " + column
        );
    }

    // testTestRow.....................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testRowAndCheck(
            "C3:D4",
            "2",
            false
        );
    }

    @Test
    public void testTestRowBelow() {
        this.testRowAndCheck(
            "C3:D4",
            "5",
            false
        );
    }

    @Test
    public void testTestRowTop() {
        this.testRowAndCheck(
            "C3:D4",
            "3",
            true
        );
    }

    @Test
    public void testTestRowBottom() {
        this.testRowAndCheck(
            "C3:D4",
            "4",
            true
        );
    }

    @Test
    public void testTestRowInside() {
        this.testRowAndCheck(
            "C3:E5",
            "4",
            true
        );
    }

    // containsAllSpreadsheetViewportWindows............................................................................

    @Test
    public void testContainsAllSpreadsheetViewportWindowsAllSpreadsheetViewportWindowsOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "A1:B2",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsTopLeftOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:C3",
            "A1:B2",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsBottomRightOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:C3",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:C3",
            "D4:E5,F6:G7",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsSame() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:C3",
            "B2:C3",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsSameInsideAndOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "A1:C3",
            "A1,B2:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsInside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:E5",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsInside2() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:E5",
            "C3",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsStarInside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "*",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsEmpty() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "A1",
            "",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsEmpty2() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "A1:B2",
            "",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsStarInside2() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "*",
            "C3:D4,E5:F6",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsStarWithStar() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "*",
            "*",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsAndSomeOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:C3",
            "A1:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetViewportWindowsAndSomeStarOutside() {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            "B2:C3",
            "*",
            false
        );
    }

    private void containsAllSpreadsheetViewportWindowsAndCheck(final String range,
                                                               final String windows,
                                                               final boolean expected) {
        this.containsAllSpreadsheetViewportWindowsAndCheck(
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetViewportWindows.parse(windows),
            expected
        );
    }

    private void containsAllSpreadsheetViewportWindowsAndCheck(final SpreadsheetCellRangeReference range,
                                                               final SpreadsheetViewportWindows windows,
                                                               final boolean expected) {
        this.containsAllAndCheck(
            range,
            windows,
            expected
        );
    }

    // containsAll......................................................................................................

    @Test
    public void testContainsAllSpreadsheetCellRangeAllOutside() {
        this.containsAllAndCheck(
            "A1:B2",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeTopLeftOutside() {
        this.containsAllAndCheck(
            "B2:C3",
            "A1:B2",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeBottomRightOutside() {
        this.containsAllAndCheck(
            "B2:C3",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeSame() {
        this.containsAllAndCheck(
            "B2:C3",
            "B2:C3",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeInside() {
        this.containsAllAndCheck(
            "B2:E5",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeInside2() {
        this.containsAllAndCheck(
            "B2:E5",
            "C3",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeInside3() {
        this.containsAllAndCheck(
            "*",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeAndSomeOutside() {
        this.containsAllAndCheck(
            "B2:C3",
            "A1:D4",
            false
        );
    }

    @Test
    public void testContainsAllSpreadsheetCellRangeAndSomeOutside2() {
        this.containsAllAndCheck(
            "B2:C3",
            "*",
            false
        );
    }

    private void containsAllAndCheck(final String range,
                                     final String test,
                                     final boolean expected) {
        this.containsAllAndCheck(
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCellRange(test),
            expected
        );
    }

    private void containsAllAndCheck(final SpreadsheetCellRangeReference range,
                                     final SpreadsheetCellRangeReference test,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            range.containsAll(test),
            () -> range + " contains " + test
        );
    }

    // stream...........................................................................................................

    @Test
    public void testColumnStream() {
        final SpreadsheetCellRangeReference range = this.range(5, 10, 8, 10);

        this.checkStream(range,
            range.columnStream(),
            this.column(5), this.column(6), this.column(7));
    }

    @Test
    public void testColumnStreamFilterAndMapAndCollect() {
        final SpreadsheetCellRangeReference range = this.range(5, 10, 8, 10);
        this.checkStream(range,
            range.columnStream()
                .map(SpreadsheetColumnReference::value)
                .filter(c -> c >= 6),
            6, 7);
    }

    @Test
    public void testRowStream() {
        final SpreadsheetCellRangeReference range = this.range(10, 5, 10, 8);

        this.checkStream(range,
            range.rowStream(),
            this.row(5), this.row(6), this.row(7));
    }

    @Test
    public void testRowStreamFilterAndMapAndCollect() {
        final SpreadsheetCellRangeReference range = this.range(5, 10, 8, 20);
        this.checkStream(range,
            range.rowStream()
                .map(SpreadsheetRowReference::value)
                .filter(r -> r < 13),
            10, 11, 12);
    }

    @Test
    public void testCellStream() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("A1:A2");

        this.checkStream(
            range,
            range.cellStream(),
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("A2")
        );
    }

    @Test
    public void testCellStream2() {
        final SpreadsheetCellRangeReference range = this.range(3, 7, 5, 10);

        this.checkStream(
            range,
            range.cellStream(),
            this.cellReference(3, 7), this.cellReference(4, 7), this.cellReference(5, 7),
            this.cellReference(3, 8), this.cellReference(4, 8), this.cellReference(5, 8),
            this.cellReference(3, 9), this.cellReference(4, 9), this.cellReference(5, 9),
            this.cellReference(3, 10), this.cellReference(4, 10), this.cellReference(5, 10));
    }

    @Test
    public void testCellStreamAllCells() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.ALL_CELLS;

        final long count = range.cellStream()
            .count();
        this.checkNotEquals(
            0,
            count
        );

        this.checkEquals(
            (long) range.width() * (long) range.height(),
            range.cellStream()
                .count()
        );
    }

    @Test
    public void testCellStreamFilterAndMapAndCollect() {
        final SpreadsheetCellRangeReference range = this.range(5, 10, 8, 20);
        this.checkStream(range,
            range.cellStream()
                .filter(cell -> cell.column().value() == 5 && cell.row().value() < 13),
            this.cellReference(5, 10), this.cellReference(5, 11), this.cellReference(5, 12));
    }

    private void checkStream(final SpreadsheetCellRangeReference range, final Stream<?> stream, final Object... expected) {
        final List<Object> actual = stream.collect(Collectors.toList());
        this.checkEquals(Lists.of(expected), actual, range::toString);
    }

    // cells............................................................................................................

    @Test
    public void testCellsNullCellsFails() {
        this.cellsFails(null,
            this::cellsPresent,
            this::cellsAbsent);
    }

    @Test
    public void testCellsNullPresentFails() {
        this.cellsFails(Lists.of((this.cell("A1", "1+2"))),
            null,
            this::cellsAbsent);
    }

    @Test
    public void testCellsNullAbsentFails() {
        this.cellsFails(Lists.of((this.cell("A1", "1+2"))),
            this::cellsPresent,
            null);
    }

    private void cellsFails(final List<SpreadsheetCell> cells,
                            final Consumer<SpreadsheetCell> present,
                            final Consumer<SpreadsheetCellReference> absent) {
        assertThrows(NullPointerException.class, () -> this.createSelection().cells(cells, present, absent));
    }

    @Test
    public void testCellsEmpty() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final List<SpreadsheetCellReference> absent = Lists.array();
        range.cells(Lists.empty(), this::cellsPresent, absent::add);

        this.checkEquals(range.cellStream().collect(Collectors.toList()), absent, "absent");
    }

    @Test
    public void testCellsFull() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final List<SpreadsheetCell> present = Lists.array();

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();
        final SpreadsheetCell c1 = this.c1();
        final SpreadsheetCell c2 = this.c2();
        final SpreadsheetCell c3 = this.c3();

        range.cells(Lists.of(b1, b2, b3, c1, c2, c3),
            present::add,
            this::cellsAbsent);

        this.checkEquals(Lists.of(b1, c1, b2, c2, b3, c3), present, "present");
    }

    @Test
    public void testCellsMixed() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCellReference b3 = this.cellReference("B3");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCell c3 = this.c3();

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1, b2, c3), consumed::add, consumed::add);

        this.checkEquals(Lists.of(b1,
                c1,
                b2,
                c2,
                b3,
                c3),
            consumed,
            "consumed");
    }

    @Test
    public void testCellsMixed2() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCellReference b1 = this.cellReference("B1");
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCell b3 = this.b3();
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCell c3 = this.c3();

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b3, c3), consumed::add, consumed::add);

        this.checkEquals(Lists.of(b1,
                c1,
                b2,
                c2,
                b3,
                c3),
            consumed,
            "consumed");
    }

    @Test
    public void testCellsMixed3() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCellReference b3 = this.cellReference("B3");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCellReference c3 = this.cellReference("C3");

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1), consumed::add, consumed::add);

        this.checkEquals(Lists.of(b1,
                c1,
                b2,
                c2,
                b3,
                c3),
            consumed,
            "consumed");
    }


    @Test
    public void testCellsMixedAbsoluteCellReferences() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("$B$1:$C$3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCellReference b3 = this.cellReference("B3");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCellReference c3 = this.cellReference("C3");

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1), consumed::add, consumed::add);

        this.checkEquals(Lists.of(b1,
                c1,
                b2,
                c2,
                b3,
                c3),
            consumed,
            "consumed");
    }

    @Test
    public void testCellsIgnoresOutOfRange() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B1:C2"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");

        @SuppressWarnings("unused") final SpreadsheetCell z99 = this.cell("Z99", "99+0");

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1), consumed::add, consumed::add);

        this.checkEquals(Lists.of(b1,
                c1,
                b2,
                c2),
            consumed,
            "consumed");
    }

    private SpreadsheetCell cell(final String reference,
                                 final String formula) {
        return this.cellReference(reference)
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText(formula)
            );
    }

    private void cellsPresent(final SpreadsheetCell cell) {
        throw new UnsupportedOperationException();
    }

    private void cellsAbsent(final SpreadsheetCellReference reference) {
        throw new UnsupportedOperationException();
    }

    private SpreadsheetCell b1() {
        return this.cell("B1", "1");
    }

    private SpreadsheetCell b2() {
        return this.cell("B2", "2");
    }

    private SpreadsheetCell b3() {
        return this.cell("B3", "3");
    }

    private SpreadsheetCell c1() {
        return this.cell("C1", "4");
    }

    private SpreadsheetCell c2() {
        return this.cell("C2", "5");
    }

    private SpreadsheetCell c3() {
        return this.cell("C3", "6");
    }

    @Test
    public void testTestCellRangeOrFail() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("C3:Z99");

        assertSame(
            range.toCellRange(),
            range
        );
    }

    // isUnit.....................................................................................................

    @Test
    public void testIsUnitSame() {
        this.isUnitAndCheck(
            "A1:A1",
            true
        );
    }

    @Test
    public void testIsUnitDifferentReferenceKind() {
        this.isUnitAndCheck(
            "A1:$A$1",
            true
        );
    }

    @Test
    public void testIsUnitDifferentReferenceKind2() {
        this.isUnitAndCheck(
            "$A$1:A1",
            true
        );
    }

    @Test
    public void testIsUnitDifferent() {
        this.isUnitAndCheck(
            "A1:B2",
            false
        );
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenBeginHidden() {
        this.isHiddenAndCheck(
            "A1:B2",
            Predicates.is(SpreadsheetSelection.parseColumn("A")),
            Predicates.is(SpreadsheetSelection.parseRow("1")),
            true
        );
    }

    @Test
    public void testIsHiddenEndHidden() {
        this.isHiddenAndCheck(
            "A1:B2",
            Predicates.is(SpreadsheetSelection.parseColumn("B")),
            Predicates.is(SpreadsheetSelection.parseRow("2")),
            true
        );
    }

    @Test
    public void testIsHiddenNotHidden() {
        this.isHiddenAndCheck(
            "A1:B2",
            Predicates.never(),
            Predicates.never(),
            false
        );
    }

    @Test
    public void testIsHiddenSingleColumnHidden() {
        this.isHiddenAndCheck(
            "A1:A1",
            Predicates.is(SpreadsheetSelection.parseColumn("A")),
            Predicates.never(),
            true
        );
    }

    @Test
    public void testIsHiddenSingleRowHidden() {
        this.isHiddenAndCheck(
            "A1:A1",
            Predicates.never(),
            Predicates.is(SpreadsheetSelection.parseRow("1")),
            true
        );
    }

    @Test
    public void testIsHiddenSingleNeitherHidden() {
        this.isHiddenAndCheck(
            "A1:A1",
            Predicates.never(),
            Predicates.never(),
            false
        );
    }

    @Test
    public void testIsHiddenSingleNeitherHidden2() {
        this.isHiddenAndCheck(
            "A1:$A$1",
            Predicates.never(),
            Predicates.never(),
            false
        );
    }

    // navigate.........................................................................................................

    // B2 C2 D2
    // B3 C3 D3
    // B4 C4 D4

    @Test
    public void testLeftColumnAnchorTopLeft() {
        this.leftColumnAndCheck(
            "B2:D4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C4"
        );
    }

    @Test
    public void testLeftColumnAnchorTopRight() {
        this.leftColumnAndCheck(
            "B2:D4",
            SpreadsheetViewportAnchor.TOP_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A4"
        );
    }

    // A1 B1 C1
    // A2 B2 C2
    // A3 B3 C3
    @Test
    public void testLeftColumnAnchorBottomLeft() {
        this.leftColumnAndCheck(
            "A1:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A1"
        );
    }

    @Test
    public void testLeftPixels() {
        this.leftPixelsAndCheck(
            "D2:E2",
            SpreadsheetViewportAnchor.TOP_LEFT,
            50,
            "C",
            Maps.of("A", 5.0, "B", 50.0, "D", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B2"
        );
    }

    @Test
    public void testRightColumnAnchorTopLeft() {
        this.rightColumnAndCheck(
            "A1:C3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D3"
        );
    }

    @Test
    public void testRightColumnAnchorTopRight() {
        this.rightColumnAndCheck(
            "A1:C3",
            SpreadsheetViewportAnchor.TOP_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B3"
        );
    }

    @Test
    public void testRightColumnAnchorBottomRight() {
        this.rightColumnAndCheck(
            "A1:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B1"
        );
    }

    @Test
    public void testRightPixels() {
        this.rightPixelsAndCheck(
            "D1:E2",
            SpreadsheetViewportAnchor.TOP_LEFT,
            50,
            "F",
            Maps.of("E", 5.0, "G", 50.0, "H", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "H2"
        );
    }

    // B1 C1 D1
    // B2 C2 D2
    // B3 C3 D3

    @Test
    public void testUpRowAnchorBottomRight() {
        this.upRowAndCheck(
            "B1:D3",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D1"
        );
    }

    @Test
    public void testUpPixels() {
        this.upPixelsAndCheck(
            "E5:F6",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Map.of("4", 50.0, "2", 50.0),
            "E2"
        );
    }

    @Test
    public void testDownRowAnchorBottomRight() {
        this.downRowAndCheck(
            "B1:D3",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D2"
        );
    }

    @Test
    public void testDownPixels() {
        this.downPixelsAndCheck(
            "B2:C3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "4",
            Map.of("5", 50.0, "6", 50.0),
            "C6"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRangeTopLeft() {
        this.extendRangeAndCheck(
            "B2:C3",
            "C4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            "B2:C4"
        );
    }

    @Test
    public void testExtendRangeTopLeft2() {
        this.extendRangeAndCheck(
            "B2:C3",
            "D4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            "B2:D4"
        );
    }

    //    B2 C2  <-- anchor
    // A3 B3 C3
    @Test
    public void testExtendRangeTopRight() {
        this.extendRangeAndCheck(
            "B2:C3",
            "A3",
            SpreadsheetViewportAnchor.TOP_RIGHT,
            "A2:C3"
        );
    }


    //     B2   C2  D2
    // --> B3   C3
    @Test
    public void testExtendRangeBottomLeft() {
        this.extendRangeAndCheck(
            "B2:C3",
            "D2",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            "B2:D3"
        );
    }

    // A2   B2   C2
    //      B3   C3 <-- anchor
    @Test
    public void testExtendRangeBottomRight() {
        this.extendRangeAndCheck(
            "B2:C3",
            "A2",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "A2:C3"
        );
    }

    @Test
    public void testExtendRangeBottomRightSame() {
        this.extendRangeAndCheck(
            "A1:B2",
            "A1",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "A1:B2"
        );
    }

    @Override
    SpreadsheetCellRangeReference parseRange(final String range) {
        return SpreadsheetSelection.parseCellRange(range);
    }

    // extendLeft.......................................................................................................

    @Test
    public void testExtendLeftColumnAnchorBottomRight() {
        this.extendLeftColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    // anchor -> C3 D3
    //           C4 D4

    @Test
    public void testExtendLeftColumnAnchorTopLeft() {
        this.extendLeftColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:C4",
            SpreadsheetViewportAnchor.TOP_LEFT
        ); // C4
    }

    @Test
    public void testExtendLeftColumnAnchorTopLeft2() {
        this.extendLeftColumnAndCheck(
            "C3:D3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendLeftColumnAnchorTopRightFirstColumn() {
        final String range = "A1:A2";
        final SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.TOP_RIGHT;

        this.extendLeftColumnAndCheck(
            range,
            anchor,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            range,
            anchor
        );
    }

    // [C3]         [B3] C3
    //  C4           B4  C4
    // bottom-left  bottom-right
    @Test
    public void testExtendLeftColumnSingleColumnBottomLeft() {
        this.extendLeftColumnAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B3:C4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    // [C3]         [B2] C3
    //  C4           B4  C4
    // bottom-right  bottom-right
    @Test
    public void testExtendLeftColumnSingleColumnBottomRight() {
        this.extendLeftColumnAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B3:C4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        ); // actual=c3:c4
    }

    @Test
    public void testExtendLeftColumnSkipsHidden() {
        this.extendLeftColumnAndCheck(
            "D4:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "C",
            NO_HIDDEN_ROWS,
            "B4:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendLeftColumnIgnoresHiddenRow() {
        this.extendLeftColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            "3",
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendLeftColumnIgnoresHiddenRow2() {
        this.extendLeftColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "",
            "4",
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // extendLeftPixels.................................................................................................

    @Test
    public void testExtendLeftPixels() {
        this.extendLeftPixelsAndCheck(
            "E5:F5",
            SpreadsheetViewportAnchor.TOP_RIGHT,
            50,
            "D",
            Maps.of("C", 50.0, "B", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B5:F5",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    // extendRight......................................................................................................

    @Test
    public void testExtendRightColumnAnchorTopLeft() {
        this.extendRightColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:E4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendRightColumnAnchorBottomRight() {
        this.extendRightColumnAndCheck(
            "C3:D3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D3",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // C3 D3
    // C4 D4 <-- anchor

    @Test
    public void testExtendRightColumnAnchorBottomRight2() {
        this.extendRightColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        ); // C4
    }

    @Test
    public void testExtendRightColumnAnchorBottomLeftLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();
        final String cell = column.add(-1) + "1:" + column + "1";
        final SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.BOTTOM_LEFT;

        this.extendRightColumnAndCheck(
            cell,
            anchor,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell,
            anchor
        );
    }

    @Test
    public void testExtendRightColumnAnchorTopRightLastColumn2() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.extendRightColumnAndCheck(
            column.add(-1) + "1:" + column + "1",
            SpreadsheetViewportAnchor.TOP_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column + "1",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // [C3]         C3 [D3]
    //  C4          C4 D4
    // bottom-right bottom-left
    @Test
    public void testExtendRightColumnAnchorFlipsAnchor() {
        this.extendRightColumnAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    // [C3]         C3 [D3]
    //  C4          C4  D4
    // bottom-left  bottom-left
    @Test
    public void testExtendRightColumnSingleColumnBottomLeft() {
        this.extendRightColumnAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    // [C3]         C3 [D3]
    //  C4          C4  D4
    // bottom-right bottom-left
    @Test
    public void testExtendRightColumnSingleColumnBottomRight() {
        this.extendRightColumnAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testExtendRightColumnSkipsHiddenColumn() {
        this.extendRightColumnAndCheck(
            "B2:C3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            "D",
            NO_HIDDEN_ROWS,
            "B2:E3",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendRightColumnIgnoresHiddenRow() {
        this.extendRightColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            "3",
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendRightColumnIgnoresHiddenRow2() {
        this.extendRightColumnAndCheck(
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "",
            "4",
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // extendRightPixels...............................................................................................

    @Test
    public void testExtendRightPixels() {
        this.extendRightPixelsAndCheck(
            "A2:B2",
            SpreadsheetViewportAnchor.TOP_LEFT,
            50,
            "C",
            Maps.of("D", 50.0, "E", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "A2:E2",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // extendUpRow....................................................................................................

    @Test
    public void testExtendUpRowAnchorTopLeft() {
        this.extendUpRowAndCheck(
            "C3:E5",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:E4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendUpRowAnchorTopLeft2() {
        this.extendUpRowAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // C3 D3 E3
    // C4 D4 E4
    // C5 D5 E5

    @Test
    public void testExtendUpRowAnchorBottomRight() {
        this.extendUpRowAndCheck(
            "C3:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C2:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    // anchor -> A1 B1
    //           A2 B2

    @Test
    public void testExtendUpRowAnchorTopLeftFirstRow() {
        this.extendUpRowAndCheck(
            "A1:B2",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A1:B1",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    //              [C2] D2
    // [C3] D3       C3  D3
    //
    // bottom-left  bottom-left
    @Test
    public void testExtendUpRowSingleColumnBottomLeft() {
        this.extendUpRowAndCheck(
            "C3:D3",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C2:D3",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    //           [C2] D2
    // [C3] D3   C3  D3
    //
    // top-left  bottom-left
    @Test
    public void testExtendUpRowSingleColumnTopLeft() {
        this.extendUpRowAndCheck(
            "C3:D3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C2:D3",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testExtendUpRowSkipsHiddenRow() {
        this.extendUpRowAndCheck(
            "D4:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            "3",
            "D2:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendUpRowIgnoresHiddenColumn() {
        this.extendUpRowAndCheck(
            "B3:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "B",
            NO_HIDDEN_ROWS,
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendUpRowIgnoresHiddenColumn2() {
        this.extendUpRowAndCheck(
            "B3:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "C",
            NO_HIDDEN_ROWS,
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // extendUpPixel...................................................................................................

    @Test
    public void testExtendUpPixels() {
        this.extendUpPixelsAndCheck(
            "E6:F6",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "5",
            Maps.of("4", 50.0, "3", 50.0),
            "E3:F6",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    // extendDownRow....................................................................................................

    @Test
    public void testExtendDownRowAnchorTopLeft() {
        this.extendDownRowAndCheck(
            "C3:E5",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:E6",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendDownRowAnchorBottomRight() {
        this.extendDownRowAndCheck(
            "C3:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C4:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendDownRowAnchorBottomRight2() {
        this.extendDownRowAndCheck(
            "C3:C4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C4",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendDownRowAnchorTopLeftLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();
        final String cell = "A" + row + ":B" + row;
        final SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.TOP_LEFT;

        this.extendDownRowAndCheck(
            cell,
            anchor,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell,
            anchor
        );
    }

    @Test
    public void testExtendDownRowAnchorBottomRightLastRow2() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.extendDownRowAndCheck(
            "A" + row.add(-1) + ":A" + row,
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A" + row,
            SpreadsheetViewportAnchor.NONE
        );
    }

    // [C3] D3       C3  D3
    //              [C4] D4
    //
    // bottom-left  top-left
    @Test
    public void testExtendDownRowSingleColumnBottomLeft() {
        this.extendDownRowAndCheck(
            "C3:D3",
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:D4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // [C3] D3   C3  D3
    //           C4  [D4]
    //
    // top-left  top-left
    @Test
    public void testExtendDownRowSingleColumnTopLeft() {
        this.extendDownRowAndCheck(
            "C3:D3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:D4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendDownRowSkipsHiddenRow() {
        this.extendDownRowAndCheck(
            "B2:C3",
            SpreadsheetViewportAnchor.TOP_LEFT,
            NO_HIDDEN_COLUMNS,
            "4",
            "B2:C5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendDownRowIgnoresHiddenColumn() {
        this.extendDownRowAndCheck(
            "B3:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "B",
            NO_HIDDEN_ROWS,
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendDownRowIgnoresHiddenColumn2() {
        this.extendDownRowAndCheck(
            "B3:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "C",
            NO_HIDDEN_ROWS,
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    // extendDownPixels.................................................................................................

    @Test
    public void testExtendDownPixels() {
        this.extendDownPixelsAndCheck(
            "B2:C2",
            SpreadsheetViewportAnchor.TOP_LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Maps.of("4", 50.0, "5", 50.0),
            "B2:C5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocusedBottomRight() {
        this.focusedAndCheck(
            "A1:B2",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT,
            "A1"
        );
    }

    @Test
    public void testFocusedTopLeft() {
        this.focusedAndCheck(
            "C3:$D$4",
            SpreadsheetViewportAnchor.TOP_LEFT,
            "$D$4"
        );
    }

    // toParserToken....................................................................................................

    @Test
    public void testToParserToken() {
        final String text = "A1:B2";

        this.toParserTokenAndCheck(
            SpreadsheetSelection.parseCellRange(text),
            SpreadsheetFormulaParserToken.cellRange(
                Lists.of(
                    SpreadsheetSelection.A1.toParserToken(),
                    SpreadsheetSelection.parseCell("B2").toParserToken()
                ),
                text
            )
        );
    }

    // equalsIgnoreReferenceKind..........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferentValuesFalse() {
        this.equalsIgnoreReferenceKindAndCheck("$A1",
            "$B2",
            false);
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferentValuesFalse2() {
        this.equalsIgnoreReferenceKindAndCheck("$A1:$Z99",
            "$B2:$Z99",
            false);
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferentReferenceKindSameValues() {
        this.equalsIgnoreReferenceKindAndCheck("$C3",
            "C3",
            true);
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferentReferenceKindSameValues2() {
        this.equalsIgnoreReferenceKindAndCheck("$C3:$D4",
            "C3:D4",
            true);
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferentReferenceKindSameValues3() {
        this.equalsIgnoreReferenceKindAndCheck("$C3:$D4",
            "C$3:D$4",
            true);
    }

    @Test
    public void testEqualsIgnoreReferenceKindSameReferenceKindDifferentValues() {
        this.equalsIgnoreReferenceKindAndCheck("$C3",
            "$C4",
            false);
    }

    @Test
    public void testEqualsIgnoreReferenceKindSameReferenceKindDifferentValues2() {
        this.equalsIgnoreReferenceKindAndCheck("$C3:$D4",
            "$C4:$D4",
            false);
    }

    // toRelative.......................................................................................................

    @Test
    public void testToRelative() {
        final SpreadsheetCellRangeReference range = this.createSelection();
        assertSame(range, range.toRelative());
    }

    @Test
    public void testToRelativeBeginAbsolute() {
        this.toRelativeAndCheck("$A1:B2", "A1:B2");
    }

    @Test
    public void testToRelativeBeginAbsoluteEndAbsolute() {
        this.toRelativeAndCheck("$A1:$B2", "A1:B2");
    }

    @Test
    public void testToRelativeEndAbsolute() {
        this.toRelativeAndCheck("A1:$B2", "A1:B2");
    }

    private void toRelativeAndCheck(final String start,
                                    final String expected) {
        final SpreadsheetCellRangeReference actual = SpreadsheetSelection.parseCellRange(start).toRelative();
        this.checkEquals(SpreadsheetSelection.parseCellRange(expected),
            actual,
            () -> start + " toRelative");
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellRangeReference selection = this.createSelection();

        new FakeSpreadsheetSelectionVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetCellRangeReference s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        this.checkEquals("132", b.toString());
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            "cell-range A1:B2" + EOL
        );
    }

    // equals...............................................................................

    @Test
    public void testEqualsDifferentBegin() {
        this.checkNotEquals(this.range(9, ROW1, COLUMN2, ROW2));
    }

    @Test
    public void testEqualsDifferentEnd() {
        this.checkNotEquals(this.range(COLUMN1, ROW1, COLUMN2, 99));
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindBeginDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A1:C3",
            "$A$1:C3",
            true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindBeginDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A1:C3",
            "B2:C3",
            false
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindEndDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A1:C3",
            "A1:$C$3",
            true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindEndDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A1:C3",
            "B2:C3",
            false
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringAllCells() {
        this.toStringAndCheck(
            SpreadsheetSelection.ALL_CELLS,
            "A1:XFD1048576"
        );
    }

    @Test
    public void testToStringSingleton() {
        this.toStringAndCheck(SpreadsheetSelection.parseCellRange("Z9"), "Z9");
    }

    @Test
    public void testString() {
        this.toStringAndCheck(SpreadsheetSelection.parseCellRange("C3:D4"), "C3:D4");
    }

    // toStringMaybeStar................................................................................................

    @Test
    public void testToStringMaybeStar() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2")
        );
    }

    @Test
    public void testToStringMaybeStarAllColumns() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.ALL_CELLS,
            "*"
        );
    }

    // helpers .........................................................................................................

    @Override
    SpreadsheetCellRangeReference createSelection() {
        return this.range(COLUMN1, ROW1, COLUMN2, ROW2);
    }

    // bounds...........................................................................................................

    @Test
    public void testBoundsWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellRangeReference.bounds(null)
        );
    }

    @Test
    public void testBoundsWithEmptyListFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetCellRangeReference.bounds(Lists.empty())
        );
    }

    @Test
    public void testBoundsWithListOneElement() {
        final int column = 2;
        final int row = 3;

        final SpreadsheetCellReference a = this.cellReference(column, row);

        final SpreadsheetCellRangeReference range = SpreadsheetCellRangeReference.bounds(Lists.of(a));
        this.check(
            range,
            column,
            row,
            column,
            row
        );
        this.checkEquals(
            Range.singleton(a),
            range.range(),
            "range"
        );
    }

    @Test
    public void testBounds() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);
        final SpreadsheetCellReference b = this.cellReference(112, 12);
        final SpreadsheetCellReference c = this.cellReference(113, 20);
        final SpreadsheetCellReference d = this.cellReference(114, 24);
        final SpreadsheetCellReference e = this.cellReference(115, 24);

        final SpreadsheetCellRangeReference range = SpreadsheetCellRangeReference.bounds(
            Lists.of(
                a,
                b,
                c,
                d,
                e
            )
        );
        this.check(
            range,
            111,
            11,
            115,
            24
        );
    }

    @Test
    public void testBounds2() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);
        final SpreadsheetCellReference b = this.cellReference(112, 12);
        final SpreadsheetCellReference c = this.cellReference(113, 20);
        final SpreadsheetCellReference d = this.cellReference(114, 24);
        final SpreadsheetCellReference e = this.cellReference(115, 24);

        final SpreadsheetCellRangeReference range = SpreadsheetCellRangeReference.bounds(
            Lists.of(
                e,
                d,
                c,
                b,
                a
            )
        );
        this.check(
            range,
            111,
            11,
            115,
            24
        );
    }

    @Test
    public void testBounds3() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);

        final SpreadsheetCellRangeReference range = SpreadsheetCellRangeReference.bounds(Lists.of(a));
        this.check(
            range,
            111,
            11,
            111,
            11
        );
    }

    // ParseStringTesting.................................................................................

    @Test
    public void testParseMissingSeparatorSingleton() {
        this.parseStringAndCheck(
            "A1",
            SpreadsheetCellRangeReference.with(Range.singleton(SpreadsheetSelection.A1))
        );
    }

    @Test
    public void testParseMissingBeginFails() {
        this.parseStringFails(
            ":A2",
            new IllegalArgumentException("Invalid character ':' at 0")
        );
    }

    @Test
    public void testParseMissingEndFails() {
        this.parseStringFails(
            "A2:",
            new IllegalArgumentException("Empty upper range in \"A2:\"")
        );
    }

    @Test
    public void testParseInvalidBeginFails() {
        this.parseStringFails(
            "##:A2",
            new IllegalArgumentException("Invalid character '#' at 0")
        );
    }

    @Test
    public void testParseInvalidEndFails() {
        this.parseStringFails(
            "A1:##",
            new IllegalArgumentException("Invalid character '#' at 3")
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "A2:C4",
            SpreadsheetCellRangeReference.with(SpreadsheetSelection.parseCell("A2")
                .range(SpreadsheetSelection.parseCell("C4")))
        );
    }

    @Test
    public void testParseEquivalent() {
        this.parseStringAndCheck(
            "A1:$A$1",
            SpreadsheetCellRangeReference.with(
                Range.singleton(
                    SpreadsheetSelection.A1
                )
            )
        );
    }

    @Test
    public void testParseEquivalent2() {
        this.parseStringAndCheck(
            "$A$1:A1",
            SpreadsheetCellRangeReference.with(
                Range.singleton(
                    SpreadsheetSelection.parseCell("$A$1")
                )
            )
        );
    }

    @Test
    public void testParseAbsoluteBegin() {
        this.parseStringAndCheck("$A$2:C4",
            SpreadsheetCellRangeReference.with(SpreadsheetSelection.parseCell("$A$2")
                .range(SpreadsheetSelection.parseCell("C4"))));
    }

    @Test
    public void testParseAbsoluteBegin2() {
        this.parseStringAndCheck("$A2:C4",
            SpreadsheetCellRangeReference.with(SpreadsheetSelection.parseCell("$A2")
                .range(SpreadsheetSelection.parseCell("C4"))));
    }

    @Test
    public void testParseAbsoluteEnd() {
        this.parseStringAndCheck("A2:$C4",
            SpreadsheetCellRangeReference.with(SpreadsheetSelection.parseCell("A2")
                .range(SpreadsheetSelection.parseCell("$C4"))));
    }

    @Test
    public void testParseAbsoluteEnd2() {
        this.parseStringAndCheck("A2:$C$4",
            SpreadsheetCellRangeReference.with(SpreadsheetSelection.parseCell("A2")
                .range(SpreadsheetSelection.parseCell("$C$4"))));
    }

    @Test
    public void testParseSwap() {
        this.parseStringAndCheck(
            "B2:A1",
            SpreadsheetCellRangeReference.with(
                SpreadsheetSelection.A1
                    .range(SpreadsheetSelection.parseCell("B2")
                    )
            )
        );
    }

    @Test
    public void testParseSwap2() {
        this.parseStringAndCheck(
            "B2:$A$1",
            SpreadsheetCellRangeReference.with(
                SpreadsheetSelection.parseCell("$A$1")
                    .range(SpreadsheetSelection.parseCell("B2")
                    )
            )
        );
    }

    @Test
    public void testParseStar() {
        this.parseStringAndCheck(
            "*",
            SpreadsheetSelection.ALL_CELLS
        );
    }

    // IterableTesting..................................................................................................

    @Test
    public void testIterable() {
        this.iterateAndCheck(
            this.createIterable().iterator(),
            this.b2().reference(),
            this.c2().reference(),
            this.b3().reference(),
            this.c3().reference()
        );
    }

    // ComparableTesting................................................................................................

    @Test
    public void testCompareEqualDifferentSpreadsheetReferenceKind() {
        this.compareToAndCheckEquals(
            SpreadsheetSelection.parseCellRange("$A$1:$B$2"),
            SpreadsheetSelection.parseCellRange("A1:B2")
        );
    }

    @Test
    public void testCompareEqualLess() {
        this.compareToAndCheckLess(
            SpreadsheetSelection.parseCellRange("$A$1:$B$2"),
            SpreadsheetSelection.parseCellRange("B2:C3")
        );
    }

    @Test
    public void testCompareSort() {
        final SpreadsheetCellRangeReference a1b2 = SpreadsheetSelection.parseCellRange("A1:B2");
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("B2:C3");
        final SpreadsheetCellRangeReference c3d4 = SpreadsheetSelection.parseCellRange("C3:D4");

        this.compareToArraySortAndCheck(
            c3d4,
            a1b2,
            b2c3,
            a1b2,
            b2c3,
            c3d4
        );
    }

    @Test
    public void testCompareSort2() {
        final SpreadsheetCellRangeReference a1b2 = SpreadsheetSelection.parseCellRange("A1:B2");
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("B2:C3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("B2:D4");

        this.compareToArraySortAndCheck(
            b2d4,
            a1b2,
            b2c3,
            a1b2,
            b2c3,
            b2d4
        );
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("A1:A2"), SpreadsheetSelection.parseCellRange("A1:A2"));
    }

    @Test
    public void testMarshall2() {
        this.marshallAndCheck(SpreadsheetSelection.parseCellRange("A1:A2"), JsonNode.string("A1:A2"));
    }

    @Test
    public void testMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetSelection.parseCellRange("A1:A2"));
    }

    //helper.................................................................................................

    private SpreadsheetCellRangeReference range() {
        return this.range(this.begin(), this.end());
    }

    private SpreadsheetCellReference begin() {
        return this.cellReference(COLUMN1, ROW1);
    }

    private SpreadsheetCellReference end() {
        return this.cellReference(COLUMN2, ROW2);
    }

    private SpreadsheetCellRangeReference range(final int column1, final int row1, final int column2, final int row2) {
        return this.range(this.cellReference(column1, row1), this.cellReference(column2, row2));
    }

    private SpreadsheetCellRangeReference range(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        return SpreadsheetCellRangeReference.with(begin.range(end));
    }

    private SpreadsheetCellReference cellReference(final String text) {
        return SpreadsheetSelection.parseCell(text);
    }

    private SpreadsheetCellReference cellReference(final int column, final int row) {
        return this.column(column)
            .setRow(this.row(row));
    }

    private SpreadsheetColumnReference column(final int column) {
        return SpreadsheetReferenceKind.RELATIVE.column(column);
    }

    private SpreadsheetRowReference row(final int row) {
        return SpreadsheetReferenceKind.RELATIVE.row(row);
    }

    private void check(final SpreadsheetCellRangeReference range,
                       final int column1,
                       final int row1,
                       final int column2,
                       final int row2) {
        this.checkBegin(range, column1, row1);
        this.checkEnd(range, column2, row2);
    }

    private void check(final SpreadsheetCellRangeReference range,
                       final int column1,
                       final int row1,
                       final int column2,
                       final int row2,
                       final int width,
                       final int height) {
        this.check(range, column1, row1, column2, row2);
        this.checkWidth(range, width);
        this.checkHeight(range, height);
    }

    private void checkBegin(final SpreadsheetCellRangeReference range, final int column, final int row) {
        this.checkBegin(range, this.cellReference(column, row));
    }

    private void checkBegin(final SpreadsheetCellRangeReference range, final SpreadsheetCellReference begin) {
        this.checkEquals(begin, range.begin(), () -> "range begin=" + range);
    }

    private void checkEnd(final SpreadsheetCellRangeReference range, final int column, final int row) {
        this.checkEnd(range, this.cellReference(column, row));
    }

    private void checkEnd(final SpreadsheetCellRangeReference range, final SpreadsheetCellReference end) {
        this.checkEquals(end, range.end(), () -> "range end=" + range);
    }

    private void checkWidth(final SpreadsheetCellRangeReference range, final int width) {
        this.checkEquals(width, range.width(), () -> "range width=" + range);
    }

    private void checkHeight(final SpreadsheetCellRangeReference range, final int height) {
        this.checkEquals(height, range.height(), () -> "range height=" + range);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellRangeReference> type() {
        return SpreadsheetCellRangeReference.class;
    }

    // ComparableTesting.................................................................................................

    @Override
    public SpreadsheetCellRangeReference createComparable() {
        return this.createSelection();
    }

    // IterableTesting.................................................................................................

    @Override
    public SpreadsheetCellRangeReference createIterable() {
        return SpreadsheetSelection.parseCellRange("B2:C3");
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetCellRangeReference unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellRangeReference.unmarshallCellRange(node, context);
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetCellRangeReference parseString(final String text) {
        return SpreadsheetSelection.parseCellRange(text);
    }
}
