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
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetExpressionReferenceComparatorTest implements ClassTesting2<SpreadsheetExpressionReferenceComparator>,
        ComparatorTesting<SpreadsheetExpressionReferenceComparator, SpreadsheetExpressionReference> {

    // cell v cell .....................................................................................................

    @Test
    public void testCellEqual() {
        this.compareAndCheckEquals(this.cell(1, 2));
    }

    @Test
    public void testCellEqualRelativeColumn() {
        this.compareAndCheckEquals(this.cell(SpreadsheetReferenceKind.RELATIVE, 1, SpreadsheetReferenceKind.ABSOLUTE, 2));
    }

    @Test
    public void testCellEqualRelativeRow() {
        this.compareAndCheckEquals(this.cell(SpreadsheetReferenceKind.ABSOLUTE, 1, SpreadsheetReferenceKind.RELATIVE, 2));
    }

    @Test
    public void testCellLess() {
        final int column = 99;
        final int row = 99;
        this.compareAndCheckLess(this.cell(column, row), this.cell(column + 1, row));
    }

    @Test
    public void testCellLessSameColumn() {
        final int column = 1;
        final int row = 88;
        this.compareAndCheckLess(this.cell(column, row), this.cell(column, row + 11));
    }

    @Test
    public void testCellLessSameColumn2() {
        final int column = 99;
        final int row = 1;
        this.compareAndCheckLess(this.cell(column, row), this.cell(column, row + 11));
    }

    @Test
    public void testCellDifferentReferenceKind() {
        this.compareAndCheckEquals(SpreadsheetExpressionReference.parseCellReference("A1"), SpreadsheetExpressionReference.parseCellReference("$A1"));
    }

    @Test
    public void testCellDifferentReferenceKind2() {
        this.compareAndCheckEquals(SpreadsheetExpressionReference.parseCellReference("A1"), SpreadsheetExpressionReference.parseCellReference("$A$1"));
    }

    @Test
    public void testCellDifferentReferenceKind3() {
        this.compareAndCheckEquals(SpreadsheetExpressionReference.parseCellReference("A1"), SpreadsheetExpressionReference.parseCellReference("A$1"));
    }

    // label v label ....................................................................................................

    @Test
    public void testLabelEqual() {
        this.compareAndCheckEquals(this.label("abcdef"));
    }

    @Test
    public void testLabelEqualDifferentCase() {
        this.compareAndCheckEquals(this.label("ABcdef"), this.label("abCDef"));
    }

    @Test
    public void testLabelLess() {
        this.compareAndCheckLess(this.label("abcdef"), this.label("ghijk"));
    }

    @Test
    public void testLabelLessDifferentCase() {
        this.compareAndCheckLess(this.label("ABcdef"), this.label("mnOPq"));
    }

    // label v cell ....................................................................................................

    @Test
    public void testLabelAndCell() {
        this.compareAndCheckLess(this.label("abcdef"), this.cell(1, 1));
    }

    @Test
    public void testLabelAndCellRelativeColumn() {
        this.compareAndCheckLess(this.label("abcdef"),
                this.cell(SpreadsheetReferenceKind.RELATIVE, 1, SpreadsheetReferenceKind.ABSOLUTE, 1));
    }

    @Test
    public void testLabelAndCellRelativeRow() {
        this.compareAndCheckLess(this.label("abcdef"),
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, 1, SpreadsheetReferenceKind.RELATIVE, 1));
    }

    @Test
    public void testLabelAndCell2() {
        this.compareAndCheckLess(this.label("vwxyz"), this.cell(2, 2));
    }

    @Test
    public void testTreeSet() {
        final Set<SpreadsheetExpressionReference> ordered = Sets.sorted(this.createComparator());

        final SpreadsheetExpressionReference label1 = this.label("abcdef");
        final SpreadsheetExpressionReference label2 = this.label("GHIJKLMN");
        final SpreadsheetExpressionReference cell3 = this.cell(1, 55);
        final SpreadsheetExpressionReference cell4 = this.cell(2, 66);
        final SpreadsheetExpressionReference cell5 = this.cell(99, 3);

        ordered.add(label1);
        ordered.add(label2);
        ordered.add(cell3);
        ordered.add(cell4);
        ordered.add(cell5);

        final List<SpreadsheetExpressionReference> list = Lists.array();
        list.addAll(ordered);

        assertEquals(Lists.of(label1, label2, cell3, cell4, cell5), list);
    }

    @Test
    public void testTreeSet2() {
        final Set<SpreadsheetExpressionReference> ordered = Sets.sorted(this.createComparator());

        final SpreadsheetExpressionReference label1 = this.label("abcdef");
        final SpreadsheetExpressionReference label2 = this.label("GHIJKLM");
        final SpreadsheetExpressionReference cell3 = this.cell(1, 55);
        final SpreadsheetExpressionReference cell4 = this.cell(2, 66);
        final SpreadsheetExpressionReference cell5 = this.cell(99, 3);

        ordered.add(cell4);
        ordered.add(label1);
        ordered.add(cell3);
        ordered.add(cell5);
        ordered.add(label2);

        final List<SpreadsheetExpressionReference> list = Lists.array();
        list.addAll(ordered);

        assertEquals(Lists.of(label1, label2, cell3, cell4, cell5), list);
    }

    // toString .......................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComparator(), "SpreadsheetCellReference < SpreadsheetLabelName");
    }

    @Override
    public SpreadsheetExpressionReferenceComparator createComparator() {
        return SpreadsheetExpressionReferenceComparator.INSTANCE;
    }

    private SpreadsheetCellReference cell(final int column, final int row) {
        return this.cell(SpreadsheetReferenceKind.ABSOLUTE, column, SpreadsheetReferenceKind.ABSOLUTE, row);
    }

    private SpreadsheetCellReference cell(final SpreadsheetReferenceKind columnKind,
                                          final int column,
                                          final SpreadsheetReferenceKind rowKind,
                                          final int row) {
        return columnKind.column(column).setRow(rowKind.row(row));
    }

    private SpreadsheetLabelName label(final String label) {
        return SpreadsheetExpressionReference.labelName(label);
    }

    @Override
    public Class<SpreadsheetExpressionReferenceComparator> type() {
        return SpreadsheetExpressionReferenceComparator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
