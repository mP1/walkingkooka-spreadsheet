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
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.type.JavaVisibility;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

walkingkooka.reflect.*;

public final class SpreadsheetRangeTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetRange>
        implements ParseStringTesting<SpreadsheetRange>,
        PredicateTesting2<SpreadsheetRange, SpreadsheetCellReference> {

    private final static int COLUMN1 = 10;
    private final static int ROW1 = 11;
    private final static int COLUMN2 = 20;
    private final static int ROW2 = 21;

    @Test
    public void testWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetRange.with(null));
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
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetRange.with(range));
    }

    @Test
    public void testWith() {
        final SpreadsheetCellReference begin = this.cellReference(1, 2);
        final SpreadsheetCellReference end = this.cellReference(3, 4);
        final Range<SpreadsheetCellReference> range = begin.range(end);

        final SpreadsheetRange spreadsheetRange = SpreadsheetRange.with(range);
        assertSame(range, spreadsheetRange.range(), "range");
        assertEquals(begin, spreadsheetRange.begin(), "begin");
        assertEquals(end, spreadsheetRange.end(), "end");
        this.checkIsSingleCell(spreadsheetRange, false);
    }

    @Test
    public void testWith2() {
        final int column1 = 99;
        final int row1 = 2;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row1, column1, row2, 99 - 3 + 1, 4 - 2 + 1);
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith3() {
        final int column1 = 1;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row2, column2, row1, 3 - 1 + 1, 99 - 4 + 1);
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith4() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
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

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row1, column2, row2, 1, 1);
        this.checkIsSingleCell(range, true);
    }

    @Test
    public void testIsSingleCellFalse() {
        final int column1 = 66;
        final int row1 = 77;
        final int column2 = 88;
        final int row2 = 99;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.checkIsSingleCell(range, false);
    }

    // setRange.....................................................................................

    @Test
    public void testSetRangeWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> this.range().setRange(null));
    }

    @Test
    public void testSetRangeWithSame() {
        final SpreadsheetRange range = this.range();
        assertSame(range, range.setRange(this.begin().spreadsheetRange(this.end()).range()));
    }

    @Test
    public void testSetRangeWithDifferent() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetCellReference differentBegin = this.cellReference(1, 2);
        final SpreadsheetRange different = range.setRange(differentBegin.range(this.end()));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetRangeWithDifferent2() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setRange(this.end().range(this.cellReference(1, 2)));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetRangeWithDifferent3() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setRange(this.begin().range(this.cellReference(88, 99)));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent4() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setRange(this.cellReference(88, 99).range(this.begin()));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent5() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setRange(this.cellReference(1, 2).range(this.cellReference(88, 99)));
        this.check(different, 1, 2, 88, 99);
    }

    @Test
    public void testSetRangeWithDifferent6() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setRange(this.cellReference(88, 99).range(this.cellReference(1, 2)));
        this.check(different, 1, 2, 88, 99);
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
        this.testTrue(SpreadsheetRange.parseRange0(range),
                SpreadsheetExpressionReference.parseCellReference(cell));
    }

    private void testFalse(final String range,
                           final String cell) {
        this.testFalse(SpreadsheetRange.parseRange0(range),
                SpreadsheetExpressionReference.parseCellReference(cell));
    }

    // stream.................................................................................................

    @Test
    public void testColumnStream() {
        final SpreadsheetRange range = this.range(5, 10, 8, 10);

        this.checkStream(range,
                range.columnStream(),
                this.column(5), this.column(6), this.column(7));
    }

    @Test
    public void testColumnStreamFilterAndMapAndCollect() {
        final SpreadsheetRange range = this.range(5, 10, 8, 10);
        this.checkStream(range,
                range.columnStream()
                        .map(SpreadsheetColumnOrRowReference::value)
                        .filter(c -> c >= 6),
                6, 7);
    }

    @Test
    public void testRowStream() {
        final SpreadsheetRange range = this.range(10, 5, 10, 8);

        this.checkStream(range,
                range.rowStream(),
                this.row(5), this.row(6), this.row(7));
    }

    @Test
    public void testRowStreamFilterAndMapAndCollect() {
        final SpreadsheetRange range = this.range(5, 10, 8, 20);
        this.checkStream(range,
                range.rowStream()
                        .map(SpreadsheetColumnOrRowReference::value)
                        .filter(r -> r < 13),
                10, 11, 12);
    }

    @Test
    public void testCellStream() {
        final SpreadsheetRange range = this.range(3, 7, 5, 10);

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
        final SpreadsheetRange range = this.range(5, 10, 8, 20);
        this.checkStream(range,
                range.cellStream()
                        .filter(cell -> cell.column().value() == 5 && cell.row().value() < 13),
                this.cellReference(5, 10), this.cellReference(5, 11), this.cellReference(5, 12));
    }

    private <T> void checkStream(final SpreadsheetRange range, final Stream<?> stream, final Object... expected) {
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
        assertThrows(NullPointerException.class, () -> this.createReference().cells(cells, present, absent));
    }

    @Test
    public void testCellsEmpty() {
        final SpreadsheetRange range = SpreadsheetRange.parseRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final List<SpreadsheetCellReference> absent = Lists.array();
        range.cells(Lists.empty(), this::cellsPresent, absent::add);

        assertEquals(range.cellStream().collect(Collectors.toList()), absent, "absent");
    }

    @Test
    public void testCellsFull() {
        final SpreadsheetRange range = SpreadsheetRange.parseRange("B1:C3"); // B1, B2, B3, C1, C2, C3

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
        final SpreadsheetRange range = SpreadsheetRange.parseRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCellReference b3 = this.cellReference("$B$3");
        final SpreadsheetCellReference c1 = this.cellReference("$C$1");
        final SpreadsheetCellReference c2 = this.cellReference("$C$2");
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
        final SpreadsheetRange range = SpreadsheetRange.parseRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCellReference b1 = this.cellReference("$B$1");
        final SpreadsheetCellReference b2 = this.cellReference("$B$2");
        final SpreadsheetCell b3 = this.b3();
        final SpreadsheetCellReference c1 = this.cellReference("$C$1");
        final SpreadsheetCellReference c2 = this.cellReference("$C$2");
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
        final SpreadsheetRange range = SpreadsheetRange.parseRange("B1:C3"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("$B$2");
        final SpreadsheetCellReference b3 = this.cellReference("$B$3");
        final SpreadsheetCellReference c1 = this.cellReference("$C$1");
        final SpreadsheetCellReference c2 = this.cellReference("$C$2");
        final SpreadsheetCellReference c3 = this.cellReference("$C$3");

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
        final SpreadsheetRange range = SpreadsheetRange.parseRange("B1:C2"); // B1, B2, B3, C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCellReference b2 = this.cellReference("$B$2");
        final SpreadsheetCellReference c1 = this.cellReference("$C$1");
        final SpreadsheetCellReference c2 = this.cellReference("$C$2");

        @SuppressWarnings("unused")
        final SpreadsheetCell z99 = this.cell("Z99", "99+0");

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

    // SpreadsheetExpressionReferenceVisitor.............................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetRange reference = this.createReference();

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
            protected void visit(final SpreadsheetRange r) {
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
        this.toStringAndCheck(SpreadsheetRange.parseRange0("Z9"), "Z9");
    }

    @Test
    public void testString() {
        this.toStringAndCheck(SpreadsheetRange.parseRange0("C3:D4"), "C3:D4");
    }

    // disabled..........................................................................................................

    @Override
    public void testTypeNaming() {
    }

    // helpers .........................................................................................................

    @Override
    SpreadsheetRange createReference() {
        return this.range(COLUMN1, ROW1, COLUMN2, ROW2);
    }

    // fromCells.......................................................................................................

    @Test
    public void testFromCellsWithNullCellsFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetRange.fromCells(null));
    }

    @Test
    public void testFromCellsEmptyCellsFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetRange.fromCells(Lists.empty()));
    }

    @Test
    public void testFromCellOne() {
        final int column = 2;
        final int row = 3;

        final SpreadsheetCellReference a = this.cellReference(column, row);

        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(a));
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

        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(a, b, c, d, e));
        this.check(range, 111, 11, 115, 24);
    }

    @Test
    public void testFromCells2() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);
        final SpreadsheetCellReference b = this.cellReference(112, 12);
        final SpreadsheetCellReference c = this.cellReference(113, 20);
        final SpreadsheetCellReference d = this.cellReference(114, 24);
        final SpreadsheetCellReference e = this.cellReference(115, 24);

        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(e, d, c, b, a));
        this.check(range, 111, 11, 115, 24);
    }

    @Test
    public void testFromCells3() {
        final SpreadsheetCellReference a = this.cellReference(111, 11);

        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(a));
        this.check(range, 111, 11, 111, 11);
    }

    // ParseStringTesting.................................................................................

    @Test
    public void testParseMissingSeparatorSingleton() {
        this.parseStringAndCheck("A1", SpreadsheetRange.cellToRange(SpreadsheetExpressionReference.parseCellReference("A1")));
    }

    @Test
    public void testParseMissingBeginFails() {
        this.parseStringFails(":A2", IllegalArgumentException.class);
    }

    @Test
    public void testParseMissingEndFails() {
        this.parseStringFails("A2:", IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidBeginFails() {
        this.parseStringFails("##..A2", IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidEndFails() {
        this.parseStringFails("A1:##", IllegalArgumentException.class);
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("A1:A2", SpreadsheetRange.with(SpreadsheetExpressionReference.parseCellReference("A1").range(SpreadsheetExpressionReference.parseCellReference("A2"))));
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("A1:A2"), SpreadsheetRange.parseRange0("A1:A2"));
    }

    @Test
    public void testJsonNodeMarshall2() {
        this.marshallAndCheck(SpreadsheetRange.parseRange0("A1:A2"), JsonNode.string("A1:A2"));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetRange.parseRange0("A1:A2"));
    }

    //compare...........................................................................................................

    @Test
    public void testCompareCellFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.range().compare(this.begin()));
    }

    @Test
    public void testCompareRangeFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.range().compare(this.range()));
    }

    //helper.................................................................................................

    private SpreadsheetRange range() {
        return this.range(this.begin(), this.end());
    }

    private SpreadsheetCellReference begin() {
        return this.cellReference(COLUMN1, ROW1);
    }

    private SpreadsheetCellReference end() {
        return this.cellReference(COLUMN2, ROW2);
    }

    private SpreadsheetRange range(final int column1, final int row1, final int column2, final int row2) {
        return this.range(this.cellReference(column1, row1), this.cellReference(column2, row2));
    }

    private SpreadsheetRange range(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        return SpreadsheetRange.with(begin.range(end));
    }

    private SpreadsheetCellReference cellReference(final String text) {
        return SpreadsheetExpressionReference.parseCellReference(text);
    }

    private SpreadsheetCellReference cellReference(final int column, final int row) {
        return this.column(column)
                .setRow(this.row(row));
    }

    private SpreadsheetColumnReference column(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column);
    }

    private SpreadsheetRowReference row(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }

    private void check(final SpreadsheetRange range,
                       final int column1,
                       final int row1,
                       final int column2,
                       final int row2) {
        this.checkBegin(range, column1, row1);
        this.checkEnd(range, column2, row2);
    }

    private void check(final SpreadsheetRange range,
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

    private void checkBegin(final SpreadsheetRange range, final int column, final int row) {
        this.checkBegin(range, this.cellReference(column, row));
    }

    private void checkBegin(final SpreadsheetRange range, final SpreadsheetCellReference begin) {
        assertEquals(begin, range.begin(), () -> "range begin=" + range);
    }

    private void checkEnd(final SpreadsheetRange range, final int column, final int row) {
        this.checkEnd(range, this.cellReference(column, row));
    }

    private void checkEnd(final SpreadsheetRange range, final SpreadsheetCellReference end) {
        assertEquals(end, range.end(), () -> "range end=" + range);
    }

    private void checkWidth(final SpreadsheetRange range, final int width) {
        assertEquals(width, range.width(), () -> "range width=" + range);
    }

    private void checkHeight(final SpreadsheetRange range, final int height) {
        assertEquals(height, range.height(), () -> "range height=" + range);
    }

    private void checkIsSingleCell(final SpreadsheetRange range, final boolean expected) {
        assertEquals(expected, range.isSingleCell(), () -> "range=" + range + " isSingleCell");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetRange> type() {
        return SpreadsheetRange.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetRange unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetRange.unmarshallRange(node, context);
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetRange parseString(final String text) {
        return SpreadsheetRange.parseRange0(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> classs) {
        return classs;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // PredicateTesting..................................................................................................

    @Override
    public SpreadsheetRange createPredicate() {
        return this.range();
    }
}
