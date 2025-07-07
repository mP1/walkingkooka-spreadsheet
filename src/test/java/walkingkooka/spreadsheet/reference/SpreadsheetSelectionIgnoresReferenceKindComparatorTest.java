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
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparatorTesting2;

public final class SpreadsheetSelectionIgnoresReferenceKindComparatorTest implements ComparatorTesting2<SpreadsheetSelectionIgnoresReferenceKindComparator, SpreadsheetSelection>,
    ToStringTesting<SpreadsheetSelectionIgnoresReferenceKindComparator> {

    @Test
    public void testCompareSameCellSelectionDifferentSpreadsheetReferenceKind() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("$A$1")
        );
    }

    @Test
    public void testCompareSameLabelDifferentCase() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.labelName("Hello"),
            SpreadsheetSelection.labelName("HELLO")
        );
    }

    @Test
    public void testCompareLabelAndLabelLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.labelName("A"),
            SpreadsheetSelection.labelName("B")
        );
    }

    @Test
    public void testCompareLabelAndLabelLessDifferentCase() {
        this.compareAndCheckLess(
            SpreadsheetSelection.labelName("a"),
            SpreadsheetSelection.labelName("B")
        );
    }

    @Test
    public void testCompareColumnAndColumnLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testCompareColumnAndColumnEqualDifferentKind() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.parseColumn("$C"),
            SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testCompareRowAndRowLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseRow("4"),
            SpreadsheetSelection.parseRow("5")
        );
    }

    @Test
    public void testCompareRowAndRowEqualDifferentKind() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseRow("$6"),
            SpreadsheetSelection.parseRow("7")
        );
    }

    @Test
    public void testCompareColumnAndRow() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testCompareColumnRangeAndColumnRangeLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testCompareColumnRangeAndColumnRangeEqualDifferentKind() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.parseColumnRange("$C:$D"),
            SpreadsheetSelection.parseColumnRange("C:D")
        );
    }

    @Test
    public void testCompareRowRangeAndRowRangeLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseRowRange("4:5"),
            SpreadsheetSelection.parseRowRange("5:6")
        );
    }

    @Test
    public void testCompareRowRangeAndRowRangeEqualDifferentKind() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.parseRowRange("$8:$9"),
            SpreadsheetSelection.parseRowRange("8:9")
        );
    }

    @Test
    public void testCompareColumnAndCell() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testCompareCellAndCellEqual() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testCompareCellAndCellEqualDifferentKind() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.A1.toAbsolute()
        );
    }

    @Test
    public void testCompareCellAndCellLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("A2")
        );
    }

    @Test
    public void testCompareCellAndCellLess2() {
        this.compareAndCheckLess(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("B1")
        );
    }

    @Test
    public void testCompareCellAndCellLess3() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseCell("D2"),
            SpreadsheetSelection.parseCell("C3")
        );
    }

    @Test
    public void testCompareCellRangeAndCellRangeEqualDifferentKind() {
        this.compareAndCheckEquals(
            SpreadsheetSelection.A1.toRange(),
            SpreadsheetSelection.A1.toAbsolute().toRange()
        );
    }

    @Test
    public void testCompareCellRangeAndCellRangeLess() {
        this.compareAndCheckLess(
            SpreadsheetSelection.A1.toRange(),
            SpreadsheetSelection.parseCellRange("A2")
        );
    }

    @Test
    public void testCompareCellRangeAndCellRangeLess2() {
        this.compareAndCheckLess(
            SpreadsheetSelection.A1.toRange(),
            SpreadsheetSelection.parseCellRange("B1")
        );
    }

    @Test
    public void testCompareCellRangeAndCellRangeLess3() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCellRange("A1:C2")
        );
    }

    @Test
    public void testCompareCellRangeAndCellRangeLess4() {
        this.compareAndCheckLess(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCellRange("A1:B3")
        );
    }

    @Test
    public void testCompareColumnsRowsThenColumns() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("A2");
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("B1");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        this.comparatorArraySortAndCheck(
            b1,
            b2,
            a1,
            a2, // expected below
            a1,
            b1,
            a2,
            b2
        );
    }

    @Test
    public void testCompareMixed() {
        final SpreadsheetColumnReference a = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetColumnReference b = SpreadsheetSelection.parseColumn("b");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");

        this.comparatorArraySortAndCheck(
            b2c3,
            b,
            b2,
            a,
            a1, // expected below
            a,
            b,
            a1,
            b2,
            b2c3
        );
    }

    @Override
    public SpreadsheetSelectionIgnoresReferenceKindComparator createComparator() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.INSTANCE;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetSelectionIgnoresReferenceKindComparator.INSTANCE,
            "Comparator(SpreadsheetSelection ignoring SpreadsheetReferenceKind)"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetSelectionIgnoresReferenceKindComparator> type() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.class;
    }
}
