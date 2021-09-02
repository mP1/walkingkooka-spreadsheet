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
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetCellRange>
        implements ParseStringTesting<SpreadsheetCellRange>,
        PredicateTesting2<SpreadsheetCellRange, SpreadsheetCellReference> {

    private final static int COLUMN1 = 10;
    private final static int ROW1 = 11;
    private final static int COLUMN2 = 20;
    private final static int ROW2 = 21;

    @Test
    public void testWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellRange.with(null));
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
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellRange.with(range));
    }

    @Test
    public void testWith() {
        final SpreadsheetCellReference begin = this.cellReference(1, 2);
        final SpreadsheetCellReference end = this.cellReference(3, 4);
        final Range<SpreadsheetCellReference> range = begin.range(end);

        final SpreadsheetCellRange spreadsheetCellRange = SpreadsheetCellRange.with(range);
        assertSame(range, spreadsheetCellRange.range(), "range");
        assertEquals(begin, spreadsheetCellRange.begin(), "begin");
        assertEquals(end, spreadsheetCellRange.end(), "end");
        this.checkIsSingleCell(spreadsheetCellRange, false);
    }

    @Test
    public void testWith2() {
        final int column1 = 99;
        final int row1 = 2;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetCellRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row1, column1, row2, 99 - 3 + 1, 4 - 2 + 1);
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith3() {
        final int column1 = 1;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetCellRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row2, column2, row1, 3 - 1 + 1, 99 - 4 + 1);
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith4() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetCellRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row2, column1, row1, 88 - 3 + 1, 99 - 4 + 1);
        this.checkIsSingleCell(range, false);
    }

    // isSingleCell...........................................................

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testIsSingleCellTrue() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = column1;
        final int row2 = row1;

        final SpreadsheetCellRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row1, column2, row2, 1, 1);
        this.checkIsSingleCell(range, true);
    }

    @Test
    public void testIsSingleCellFalse() {
        final int column1 = 66;
        final int row1 = 77;
        final int column2 = 88;
        final int row2 = 99;

        final SpreadsheetCellRange range = this.range(column1, row1, column2, row2);
        this.checkIsSingleCell(range, false);
    }

    // setRange.....................................................................................

    @Test
    public void testSetRangeWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> this.range().setRange(null));
    }

    @Test
    public void testSetRangeWithSame() {
        final SpreadsheetCellRange range = this.range();
        assertSame(range, range.setRange(this.begin().spreadsheetCellRange(this.end()).range()));
    }

    @Test
    public void testSetRangeWithDifferent() {
        final SpreadsheetCellRange range = this.range();
        final SpreadsheetCellReference differentBegin = this.cellReference(1, 2);
        final SpreadsheetCellRange different = range.setRange(differentBegin.range(this.end()));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetRangeWithDifferent2() {
        final SpreadsheetCellRange range = this.range();
        final SpreadsheetCellRange different = range.setRange(this.end().range(this.cellReference(1, 2)));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetRangeWithDifferent3() {
        final SpreadsheetCellRange range = this.range();
        final SpreadsheetCellRange different = range.setRange(this.begin().range(this.cellReference(88, 99)));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent4() {
        final SpreadsheetCellRange range = this.range();
        final SpreadsheetCellRange different = range.setRange(this.cellReference(88, 99).range(this.begin()));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent5() {
        final SpreadsheetCellRange range = this.range();
        final SpreadsheetCellRange different = range.setRange(this.cellReference(1, 2).range(this.cellReference(88, 99)));
        this.check(different, 1, 2, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent6() {
        final SpreadsheetCellRange range = this.range();
        final SpreadsheetCellRange different = range.setRange(this.cellReference(88, 99).range(this.cellReference(1, 2)));
        this.check(different, 1, 2, 88, 99);
    }

    // columnReferenceRange.............................................................................................

    @Test
    public void testColumnReferenceRange() {
        this.columnReferenceRangeAndCheck("B2:D4", "B:D");
    }

    @Test
    public void testColumnReferenceRangeSingleton() {
        this.columnReferenceRangeAndCheck("B2:B4", "B");
    }

    private void columnReferenceRangeAndCheck(final String cell,
                                              final String column) {
        assertEquals(
                SpreadsheetSelection.parseColumnRange(column),
                SpreadsheetSelection.parseCellRange(cell).columnReferenceRange(),
                () -> cell + ".columnReferenceRange()"
        );
    }

    // SetColumnReferenceRange.............................................................................................

    @Test
    public void testSetColumnReferenceRangeNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setColumnReferenceRange(null));
    }

    @Test
    public void testSetColumnReferenceRangeSame() {
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:D4");
        assertSame(range, range.setColumnReferenceRange(SpreadsheetSelection.parseColumnRange("B:D")));
    }

    @Test
    public void testSetColumnReferenceRangeDifferent() {
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:D4");
        final SpreadsheetColumnReferenceRange columns = SpreadsheetSelection.parseColumnRange("F:G");
        final SpreadsheetCellRange different = range.setColumnReferenceRange(columns);

        assertNotSame(range, different);
        assertEquals(
                SpreadsheetSelection.parseCellRange("F2:G4"),
                different
        );

        assertEquals(columns, different.columnReferenceRange());
    }

    // rowReferenceRange.............................................................................................

    @Test
    public void testRowReferenceRange() {
        this.rowReferenceRangeAndCheck("B2:D4", "2:4");
    }

    @Test
    public void testRowReferenceRangeSingleton() {
        this.rowReferenceRangeAndCheck("B2:D2", "2");
    }

    private void rowReferenceRangeAndCheck(final String cell,
                                           final String row) {
        assertEquals(
                SpreadsheetSelection.parseRowRange(row),
                SpreadsheetSelection.parseCellRange(cell).rowReferenceRange(),
                () -> cell + ".rowReferenceRange()"
        );
    }

    // SetRowReferenceRange.............................................................................................

    @Test
    public void testSetRowReferenceRangeNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setRowReferenceRange(null));
    }

    @Test
    public void testSetRowReferenceRangeSame() {
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:D4");
        assertSame(range, range.setRowReferenceRange(SpreadsheetSelection.parseRowRange("2:4")));
    }

    @Test
    public void testSetRowReferenceRangeDifferent() {
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:D4");
        final SpreadsheetRowReferenceRange rows = SpreadsheetSelection.parseRowRange("6:7");
        final SpreadsheetCellRange different = range.setRowReferenceRange(rows);

        assertNotSame(range, different);
        assertEquals(
                SpreadsheetSelection.parseCellRange("B6:D7"),
                different
        );

        assertEquals(rows, different.rowReferenceRange());
    }

    // test.............................................................................................................

    @Test
    public void testTestSingletonTopLeft() {
        this.testFalse("C3", "B2");
    }

    @Test
    public void testTestSingletonTop() {
        this.testFalse("C3", "B3");
    }

    @Test
    public void testTestSingletonTopRight() {
        this.testFalse("C3", "B4");
    }

    @Test
    public void testTestSingletonLeft() {
        this.testFalse("C3", "B3");
    }

    @Test
    public void testTestSingleton() {
        this.testTrue("C3", "C3");
    }

    @Test
    public void testTestSingletonRight() {
        this.testFalse("C3", "D3");
    }

    @Test
    public void testTestSingletonBottomLeft() {
        this.testFalse("C3", "D2");
    }

    @Test
    public void testTestSingletonBottom() {
        this.testFalse("C3", "D3");
    }

    @Test
    public void testTestSingletonBottomRight() {
        this.testFalse("C3", "D4");
    }

    @Test
    public void testTestTopLeft() {
        this.testFalse("C3:E5", "B2");
    }

    @Test
    public void testTestTop() {
        this.testFalse("C3:E5", "B2");
    }

    @Test
    public void testTestTopRight() {
        this.testFalse("C3:E5", "B6");
    }

    @Test
    public void testTestLeft() {
        this.testFalse("C3:E5", "B4");
    }

    @Test
    public void testTest() {
        this.testTrue("C3:E5", "C3");
    }

    @Test
    public void testTest2() {
        this.testTrue("C3:E5", "D3");
    }

    @Test
    public void testTest3() {
        this.testTrue("C3:E5", "E3");
    }

    @Test
    public void testTest4() {
        this.testTrue("C3:E5", "C4");
    }

    @Test
    public void testTest5() {
        this.testTrue("C3:E5", "D4");
    }

    @Test
    public void testTest6() {
        this.testTrue("C3:E5", "E4");
    }

    @Test
    public void testTest7() {
        this.testTrue("C3:E5", "C5");
    }

    @Test
    public void testTest8() {
        this.testTrue("C3:E5", "D5");
    }

    @Test
    public void testTest9() {
        this.testTrue("C3:E5", "E5");
    }

    @Test
    public void testTestRight() {
        this.testFalse("C3:E5", "D6");
    }

    @Test
    public void testTestBottomLeft() {
        this.testFalse("C3:E5", "F2");
    }

    @Test
    public void testTestBottom() {
        this.testFalse("C3:E5", "F4");
    }

    @Test
    public void testTestBottomRight() {
        this.testFalse("C3:E5", "F6");
    }

    private void testTrue(final String range,
                          final String cell) {
        this.testTrue(
                SpreadsheetCellRange.parseCellRange(range),
                SpreadsheetExpressionReference.parseCell(cell)
        );
    }

    private void testFalse(final String range,
                           final String cell) {
        this.testFalse(
                SpreadsheetCellRange.parseCellRange(range),
                SpreadsheetExpressionReference.parseCell(cell)
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testCellRangeLeft() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "A3:B3",
                false
        );
    }

    @Test
    public void testCellRangeRight() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "C5:C6",
                false
        );
    }

    @Test
    public void testCellRangeAboveLeft() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "A1:B2",
                false
        );
    }

    @Test
    public void testCellRangePartialLeft() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "C1:C3",
                true
        );
    }

    @Test
    public void testCellRangePartialRight() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "D4:F4",
                true
        );
    }

    @Test
    public void testCellRangePartialAbove() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "C1:C3",
                true
        );
    }

    @Test
    public void testCellRangePartialBelow() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "D4:D6",
                true
        );
    }

    @Test
    public void testCellRangePartialLeftAbove() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "A1:C3",
                true
        );
    }

    @Test
    public void testCellRangePartialRightBelow() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "D4:E5",
                true
        );
    }

    @Test
    public void testCellRangePartialInside() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "C3:C3",
                true
        );
    }

    @Test
    public void testCellRangePartialInside2() {
        this.testCellRangeAndCheck2(
                "C3:D4",
                "C3:D4",
                true
        );
    }

    private void testCellRangeAndCheck2(final String range, final String testRange, final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetSelection.parseCellRange(range),
                SpreadsheetSelection.parseCellRange(testRange),
                expected
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
        assertEquals(
                expected,
                SpreadsheetSelection.parseCellRange(range).testColumn(SpreadsheetSelection.parseColumn(column)),
                range + " testColumn " + column
        );
    }

    // testTestRow.....................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testTestRowAndCheck2(
                "C3:D4",
                "2",
                false
        );
    }

    @Test
    public void testTestRowBelow() {
        this.testTestRowAndCheck2(
                "C3:D4",
                "5",
                false
        );
    }

    @Test
    public void testTestRowTop() {
        this.testTestRowAndCheck2(
                "C3:D4",
                "3",
                true
        );
    }

    @Test
    public void testTestRowBottom() {
        this.testTestRowAndCheck2(
                "C3:D4",
                "4",
                true
        );
    }

    @Test
    public void testTestRowInside() {
        this.testTestRowAndCheck2(
                "C3:E5",
                "4",
                true
        );
    }

    private void testTestRowAndCheck2(final String range,
                                      final String row,
                                      final boolean expected) {
        assertEquals(
                expected,
                SpreadsheetSelection.parseCellRange(range).testRow(SpreadsheetSelection.parseRow(row)),
                range + " testRow " + row
        );
    }

    // stream.................................................................................................

    @Test
    public void testColumnStream() {
        final SpreadsheetCellRange range = this.range(5, 10, 8, 10);

        this.checkStream(range,
                range.columnStream(),
                this.column(5), this.column(6), this.column(7));
    }

    @Test
    public void testColumnStreamFilterAndMapAndCollect() {
        final SpreadsheetCellRange range = this.range(5, 10, 8, 10);
        this.checkStream(range,
                range.columnStream()
                        .map(SpreadsheetColumnOrRowReference::value)
                        .filter(c -> c >= 6),
                6, 7);
    }

    @Test
    public void testRowStream() {
        final SpreadsheetCellRange range = this.range(10, 5, 10, 8);

        this.checkStream(range,
                range.rowStream(),
                this.row(5), this.row(6), this.row(7));
    }

    @Test
    public void testRowStreamFilterAndMapAndCollect() {
        final SpreadsheetCellRange range = this.range(5, 10, 8, 20);
        this.checkStream(range,
                range.rowStream()
                        .map(SpreadsheetColumnOrRowReference::value)
                        .filter(r -> r < 13),
                10, 11, 12);
    }

    @Test
    public void testCellStream() {
        final SpreadsheetCellRange range = this.range(3, 7, 5, 10);

        this.checkStream(
                range,
                range.cellStream(),
                this.cellReference(3, 7), this.cellReference(4, 7), this.cellReference(5, 7),
                this.cellReference(3, 8), this.cellReference(4, 8), this.cellReference(5, 8),
                this.cellReference(3, 9), this.cellReference(4, 9), this.cellReference(5, 9),
                this.cellReference(3, 10), this.cellReference(4, 10), this.cellReference(5, 10));
    }

    @Test
    public void testCellStreamFilterAndMapAndCollect() {
        final SpreadsheetCellRange range = this.range(5, 10, 8, 20);
        this.checkStream(range,
                range.cellStream()
                        .filter(cell -> cell.column().value() == 5 && cell.row().value() < 13),
                this.cellReference(5, 10), this.cellReference(5, 11), this.cellReference(5, 12));
    }

    private <T> void checkStream(final SpreadsheetCellRange range, final Stream<?> stream, final Object... expected) {
        final List<Object> actual = stream.collect(Collectors.toList());
        assertEquals(Lists.of(expected), actual, range::toString);
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
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final List<SpreadsheetCellReference> absent = Lists.array();
        range.cells(Lists.empty(), this::cellsPresent, absent::add);

        assertEquals(range.cellStream().collect(Collectors.toList()), absent, "absent");
    }

    @Test
    public void testCellsFull() {
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

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

        assertEquals(Lists.of(b1, c1, b2, c2, b3, c3), present, "present");
    }

    @Test
    public void testCellsMixed() {
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCellReference b3 = this.cellReference("B3");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCell c3 = this.c3();

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1, b2, c3), consumed::add, consumed::add);

        assertEquals(Lists.of(b1,
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
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCellReference b1 = this.cellReference("B1");
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCell b3 = this.b3();
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCell c3 = this.c3();

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b3, c3), consumed::add, consumed::add);

        assertEquals(Lists.of(b1,
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
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCellReference b3 = this.cellReference("B3");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCellReference c3 = this.cellReference("C3");

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1), consumed::add, consumed::add);

        assertEquals(Lists.of(b1,
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
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("$B$1:$C$3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCellReference b3 = this.cellReference("B3");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");
        final SpreadsheetCellReference c3 = this.cellReference("C3");

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1), consumed::add, consumed::add);

        assertEquals(Lists.of(b1,
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
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("B1:C2"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("B2");
        final SpreadsheetCellReference c1 = this.cellReference("C1");
        final SpreadsheetCellReference c2 = this.cellReference("C2");

        @SuppressWarnings("unused") final SpreadsheetCell z99 = this.cell("Z99", "99+0");

        final List<Object> consumed = Lists.array();

        range.cells(Lists.of(b1), consumed::add, consumed::add);

        assertEquals(Lists.of(b1,
                c1,
                b2,
                c2),
                consumed,
                "consumed");
    }

    private SpreadsheetCell cell(final String reference, final String formula) {
        return SpreadsheetCell.with(this.cellReference(reference), SpreadsheetFormula.with(formula));
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

    // equalsIgnoreReferenceKind........................................................................................

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

    private void equalsIgnoreReferenceKindAndCheck(final String range1,
                                                   final String range2,
                                                   final boolean expected) {
        this.equalsIgnoreReferenceKindAndCheck(SpreadsheetCellRange.parseCellRange(range1),
                SpreadsheetCellRange.parseCellRange(range2),
                expected);
    }

    // toRelative.......................................................................................................

    @Test
    public void testToRelative() {
        final SpreadsheetCellRange range = this.createSelection();
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
        final SpreadsheetCellRange actual = SpreadsheetCellRange.parseCellRange(start).toRelative();
        assertEquals(SpreadsheetCellRange.parseCellRange(expected),
                actual,
                () -> start + " toRelative");
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellRange selection = this.createSelection();

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
            protected void visit(final SpreadsheetCellRange s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        assertEquals("132", b.toString());
    }

    // SpreadsheetExpressionReferenceVisitor.............................................................................

    @Test
    public void testSpreadsheetExpressionReferenceVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellRange reference = this.createSelection();

        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference r) {
                assertSame(reference, r);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference r) {
                assertSame(reference, r);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetExpressionReference r) {
                assertSame(reference, r);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetExpressionReference r) {
                assertSame(reference, r);
                b.append("4");
            }

            @Override
            protected void visit(final SpreadsheetCellRange r) {
                assertSame(reference, r);
                b.append("5");
            }
        }.accept(reference);
        assertEquals("13542", b.toString());
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

    // toString.........................................................................................................

    @Test
    public void testToStringSingleton() {
        this.toStringAndCheck(SpreadsheetCellRange.parseCellRange("Z9"), "Z9");
    }

    @Test
    public void testString() {
        this.toStringAndCheck(SpreadsheetCellRange.parseCellRange("C3:D4"), "C3:D4");
    }

    // helpers .........................................................................................................

    @Override
    SpreadsheetCellRange createSelection() {
        return this.range(COLUMN1, ROW1, COLUMN2, ROW2);
    }

    // fromCells.......................................................................................................

    @Test
    public void testFromCellsWithNullCellsFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellRange.fromCells(null));
    }

    @Test
    public void testFromCellsEmptyCellsFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetCellRange.fromCells(Lists.empty()));
    }

    @Test
    public void testFromCellOne() {
        final int column = 2;
        final int row = 3;

        final SpreadsheetCellReference a = this.cellReference(column, row);

        final SpreadsheetCellRange range = SpreadsheetCellRange.fromCells(Lists.of(a));
        this.check(range, column, row, column, row);
        assertEquals(Range.singleton(a), range.range(), "range");
    }

    @Test
    public void testFromCells() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);
        final SpreadsheetCellReference b = this.cellReference(112, 12);
        final SpreadsheetCellReference c = this.cellReference(113, 20);
        final SpreadsheetCellReference d = this.cellReference(114, 24);
        final SpreadsheetCellReference e = this.cellReference(115, 24);

        final SpreadsheetCellRange range = SpreadsheetCellRange.fromCells(Lists.of(a, b, c, d, e));
        this.check(range, 111, 11, 115, 24);
    }

    @Test
    public void testFromCells2() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);
        final SpreadsheetCellReference b = this.cellReference(112, 12);
        final SpreadsheetCellReference c = this.cellReference(113, 20);
        final SpreadsheetCellReference d = this.cellReference(114, 24);
        final SpreadsheetCellReference e = this.cellReference(115, 24);

        final SpreadsheetCellRange range = SpreadsheetCellRange.fromCells(Lists.of(e, d, c, b, a));
        this.check(range, 111, 11, 115, 24);
    }

    @Test
    public void testFromCells3() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);

        final SpreadsheetCellRange range = SpreadsheetCellRange.fromCells(Lists.of(a));
        this.check(range, 111, 11, 111, 11);
    }

    // ParseStringTesting.................................................................................

    @Test
    public void testParseMissingSeparatorSingleton() {
        this.parseStringAndCheck(
                "A1",
                SpreadsheetCellRange.with(Range.singleton(SpreadsheetExpressionReference.parseCell("A1")))
        );
    }

    @Test
    public void testParseMissingBeginFails() {
        this.parseStringFails(":A2", new IllegalArgumentException("Missing begin in \":A2\""));
    }

    @Test
    public void testParseMissingEndFails() {
        this.parseStringFails("A2:", new IllegalArgumentException("Missing end in \"A2:\""));
    }

    @Test
    public void testParseInvalidBeginFails() {
        this.parseStringFails("##:A2", new IllegalArgumentException("Invalid begin in \"##:A2\""));
    }

    @Test
    public void testParseInvalidEndFails() {
        this.parseStringFails("A1:##", new IllegalArgumentException("Invalid end in \"A1:##\""));
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("A2:C4",
                SpreadsheetCellRange.with(SpreadsheetExpressionReference.parseCell("A2")
                        .range(SpreadsheetExpressionReference.parseCell("C4"))));
    }

    @Test
    public void testParseAbsoluteBegin() {
        this.parseStringAndCheck("$A$2:C4",
                SpreadsheetCellRange.with(SpreadsheetExpressionReference.parseCell("$A$2")
                        .range(SpreadsheetExpressionReference.parseCell("C4"))));
    }

    @Test
    public void testParseAbsoluteBegin2() {
        this.parseStringAndCheck("$A2:C4",
                SpreadsheetCellRange.with(SpreadsheetExpressionReference.parseCell("$A2")
                        .range(SpreadsheetExpressionReference.parseCell("C4"))));
    }

    @Test
    public void testParseAbsoluteEnd() {
        this.parseStringAndCheck("A2:$C4",
                SpreadsheetCellRange.with(SpreadsheetExpressionReference.parseCell("A2")
                        .range(SpreadsheetExpressionReference.parseCell("$C4"))));
    }

    @Test
    public void testParseAbsoluteEnd2() {
        this.parseStringAndCheck("A2:$C$4",
                SpreadsheetCellRange.with(SpreadsheetExpressionReference.parseCell("A2")
                        .range(SpreadsheetExpressionReference.parseCell("$C$4"))));
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("A1:A2"), SpreadsheetCellRange.parseCellRange("A1:A2"));
    }

    @Test
    public void testJsonNodeMarshall2() {
        this.marshallAndCheck(SpreadsheetCellRange.parseCellRange("A1:A2"), JsonNode.string("A1:A2"));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetCellRange.parseCellRange("A1:A2"));
    }

    //helper.................................................................................................

    private SpreadsheetCellRange range() {
        return this.range(this.begin(), this.end());
    }

    private SpreadsheetCellReference begin() {
        return this.cellReference(COLUMN1, ROW1);
    }

    private SpreadsheetCellReference end() {
        return this.cellReference(COLUMN2, ROW2);
    }

    private SpreadsheetCellRange range(final int column1, final int row1, final int column2, final int row2) {
        return this.range(this.cellReference(column1, row1), this.cellReference(column2, row2));
    }

    private SpreadsheetCellRange range(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        return SpreadsheetCellRange.with(begin.range(end));
    }

    private SpreadsheetCellReference cellReference(final String text) {
        return SpreadsheetExpressionReference.parseCell(text);
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

    private void check(final SpreadsheetCellRange range,
                       final int column1,
                       final int row1,
                       final int column2,
                       final int row2) {
        this.checkBegin(range, column1, row1);
        this.checkEnd(range, column2, row2);
    }

    private void check(final SpreadsheetCellRange range,
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

    private void checkBegin(final SpreadsheetCellRange range, final int column, final int row) {
        this.checkBegin(range, this.cellReference(column, row));
    }

    private void checkBegin(final SpreadsheetCellRange range, final SpreadsheetCellReference begin) {
        assertEquals(begin, range.begin(), () -> "range begin=" + range);
    }

    private void checkEnd(final SpreadsheetCellRange range, final int column, final int row) {
        this.checkEnd(range, this.cellReference(column, row));
    }

    private void checkEnd(final SpreadsheetCellRange range, final SpreadsheetCellReference end) {
        assertEquals(end, range.end(), () -> "range end=" + range);
    }

    private void checkWidth(final SpreadsheetCellRange range, final int width) {
        assertEquals(width, range.width(), () -> "range width=" + range);
    }

    private void checkHeight(final SpreadsheetCellRange range, final int height) {
        assertEquals(height, range.height(), () -> "range height=" + range);
    }

    private void checkIsSingleCell(final SpreadsheetCellRange range, final boolean expected) {
        assertEquals(expected, range.isSingleCell(), () -> "range=" + range + " isSingleCell");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellRange> type() {
        return SpreadsheetCellRange.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetCellRange unmarshall(final JsonNode node,
                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellRange.unmarshallCellRange(node, context);
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetCellRange parseString(final String text) {
        return SpreadsheetCellRange.parseCellRange(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> classs) {
        return classs;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
