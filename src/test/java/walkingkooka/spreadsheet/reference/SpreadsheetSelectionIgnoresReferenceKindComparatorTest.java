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
    public void testCompareColumnAndCell() {
        this.compareAndCheckLess(
                SpreadsheetSelection.parseColumn("A"),
                SpreadsheetSelection.A1
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
                a1,
                b,
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
