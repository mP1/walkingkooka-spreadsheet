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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.compare.LowerOrUpperTesting;
import walkingkooka.compare.Range;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetCellReference>
        implements ComparableTesting<SpreadsheetCellReference>,
        LowerOrUpperTesting<SpreadsheetCellReference>,
        ParseStringTesting<SpreadsheetCellReference> {

    private final static int COLUMN = 123;
    private final static int ROW = 456;

    @Test
    public void testWithNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetCellReference.with(null, row());
        });
    }

    @Test
    public void testWithNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetCellReference.with(column(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnReference column = this.column();
        final SpreadsheetRowReference row = this.row();
        final SpreadsheetCellReference cell = SpreadsheetCellReference.with(column, row);
        this.checkColumn(cell, column);
        this.checkRow(cell, row);
    }

    // setColumn..................................................................................................

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createReference().setColumn(null);
        });
    }

    @Test
    public void testSetColumnSame() {
        final SpreadsheetCellReference cell = this.createReference();
        assertSame(cell, cell.setColumn(this.column(COLUMN)));
    }

    @Test
    public void testSetColumnDifferent() {
        final SpreadsheetCellReference cell = this.createReference();
        final SpreadsheetColumnReference differentColumn = this.column(99);
        final SpreadsheetCellReference different = cell.setColumn(differentColumn);
        this.checkRow(different, this.row());
        this.checkColumn(different, differentColumn);
    }

    // setRow..................................................................................................

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createReference().setRow(null);
        });
    }

    @Test
    public void testSetRowSame() {
        final SpreadsheetCellReference cell = this.createReference();
        assertSame(cell, cell.setRow(this.row(ROW)));
    }

    @Test
    public void testSetRowDifferent() {
        final SpreadsheetCellReference cell = this.createReference();
        final SpreadsheetRowReference differentRow = this.row(99);
        final SpreadsheetCellReference different = cell.setRow(differentRow);
        this.checkColumn(different, this.column());
        this.checkRow(different, differentRow);
    }

    // addColumn .............................................................................................

    @Test
    public void testAddColumnZero() {
        final SpreadsheetCellReference cell = this.createReference();
        assertSame(cell, cell.addColumn(0));
    }

    @Test
    public void testAddColumnNonZero() {
        final SpreadsheetCellReference cell = this.createReference();
        final int delta = 10;

        final SpreadsheetCellReference different = cell.addColumn(delta);
        this.checkColumn(different, this.column().setValue(COLUMN + delta));
        this.checkRow(different, this.row());
    }

    // addRow .............................................................................................

    @Test
    public void testAddRowZero() {
        final SpreadsheetCellReference cell = this.createReference();
        assertSame(cell, cell.addRow(0));
    }

    @Test
    public void testAddRowNonZero() {
        final SpreadsheetCellReference cell = this.createReference();
        final int delta = 10;

        final SpreadsheetCellReference different = cell.addRow(delta);
        this.checkRow(different, this.row().setValue(ROW + delta));
        this.checkColumn(different, this.column());
    }

    // add .............................................................................................

    @Test
    public void testAddColumnRowColumnZeroAndRowZero() {
        final SpreadsheetCellReference cell = this.createReference();
        assertSame(cell, cell.add(0, 0));
    }

    @Test
    public void testAddColumnRowColumnNonZeroAndRowNonZero() {
        final SpreadsheetCellReference cell = this.createReference();
        final int column = 10;
        final int row = 100;

        final SpreadsheetCellReference different = cell.add(column, row);
        this.checkColumn(different, this.column().setValue(COLUMN + column));
        this.checkRow(different, this.row().setValue(ROW + row));
    }

    @Test
    public void testAddColumnRowColumnNonZero() {
        final SpreadsheetCellReference cell = this.createReference();
        final int column = 10;

        final SpreadsheetCellReference different = cell.add(column, 0);
        this.checkColumn(different, this.column().setValue(COLUMN + column));
        this.checkRow(different, this.row());
    }

    @Test
    public void testAddColumnRowRowNonZero() {
        final SpreadsheetCellReference cell = this.createReference();
        final int row = 100;

        final SpreadsheetCellReference different = cell.add(0, row);
        this.checkColumn(different, this.column());
        this.checkRow(different, this.row().setValue(ROW + row));
    }

    // lower...........................................................................

    @Test
    public void testLowerSame() {
        final SpreadsheetCellReference reference = this.createReference();
        assertSame(reference, reference.lower(reference));
    }

    @Test
    public void testLowerOtherLessColumnSameRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference lower = this.createReference(COLUMN - 99, ROW);

        this.lowerAndCheck(reference, lower, lower);
    }

    @Test
    public void testLowerOtherGreaterColumnSameRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference higher = this.createReference(COLUMN + 99, ROW);

        this.lowerAndCheck(reference, higher, reference);
    }

    @Test
    public void testLowerOtherSameColumnLessRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference lower = this.createReference(COLUMN, ROW - 99);

        this.lowerAndCheck(reference, lower, lower);
    }

    @Test
    public void testLowerOtherSameColumnGreaterRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference higher = this.createReference(COLUMN, ROW + 99);

        this.lowerAndCheck(reference, higher, reference);
    }

    @Test
    public void testLowerOtherLessColumnGreaterRow() {
        this.lowerAndCheck(this.createReference(COLUMN - 99, ROW + 99), COLUMN - 99, ROW);
    }

    @Test
    public void testLowerOtherGreaterColumnLessRow() {
        this.lowerAndCheck(this.createReference(COLUMN + 99, ROW - 99), COLUMN, ROW - 99);
    }

    private void lowerAndCheck(final SpreadsheetCellReference other,
                               final int column,
                               final int row) {
        this.lowerAndCheck(this.createReference(), other, this.createReference(column, row));
    }

    private void lowerAndCheck(final SpreadsheetCellReference reference,
                               final SpreadsheetCellReference other,
                               final SpreadsheetCellReference lower) {
        assertEquals(lower,
                reference.lower(other),
                reference + " lower " + other + " expected " + lower);
    }

    // upper..........................................................................................

    @Test
    public void testUpperSame() {
        final SpreadsheetCellReference reference = this.createReference();
        assertSame(reference, reference.lower(reference));
    }

    @Test
    public void testUpperOtherLessColumnSameRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference lower = this.createReference(COLUMN - 99, ROW);

        this.upperAndCheck(reference, lower, reference);
    }

    @Test
    public void testUpperOtherGreaterColumnSameRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference higher = this.createReference(COLUMN + 99, ROW);

        this.upperAndCheck(reference, higher, higher);
    }

    @Test
    public void testUpperOtherSameColumnLessRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference lower = this.createReference(COLUMN, ROW - 99);

        this.upperAndCheck(reference, lower, reference);
    }

    @Test
    public void testUpperOtherSameColumnGreaterRow() {
        final SpreadsheetCellReference reference = this.createReference();
        final SpreadsheetCellReference higher = this.createReference(COLUMN, ROW + 99);

        this.upperAndCheck(reference, higher, higher);
    }

    @Test
    public void testUpperOtherLessColumnGreaterRow() {
        this.upperAndCheck(this.createReference(COLUMN - 99, ROW + 99), COLUMN, ROW + 99);
    }

    @Test
    public void testUpperOtherGreaterColumnLessRow() {
        this.upperAndCheck(this.createReference(COLUMN + 99, ROW - 99), COLUMN + 99, ROW);
    }

    private void upperAndCheck(final SpreadsheetCellReference other,
                               final int column,
                               final int row) {
        this.upperAndCheck(this.createReference(), other, this.createReference(column, row));
    }

    private void upperAndCheck(final SpreadsheetCellReference reference,
                               final SpreadsheetCellReference other,
                               final SpreadsheetCellReference upper) {
        assertEquals(upper, reference.upper(other), () -> reference + " upper " + other + " expected " + upper);
    }

    @Test
    public void testSameColumnSameRowDifferentReferenceKinds() {
        this.compareToAndCheckEqual(
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
                this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN, SpreadsheetReferenceKind.RELATIVE, ROW));
    }

    @Test
    public void testSameColumnDifferentRow() {
        this.compareToAndCheckLess(
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
                this.cell(COLUMN, ROW + 10));
    }

    @Test
    public void testSameColumnDifferentReferenceKindDifferentRow() {
        this.compareToAndCheckLess(
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
                this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW + 10));
    }

    @Test
    public void testDifferentColumnSameRow() {
        this.compareToAndCheckLess(this.cell(COLUMN + 10, ROW));
    }

    @Test
    public void testDifferentColumnDifferentReferenceKindDifferentRow() {
        this.compareToAndCheckLess(this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN + 10, SpreadsheetReferenceKind.ABSOLUTE, ROW));
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

    // range.............................................................................................................

    @Test
    public void testRangeNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.cell(1, 1).spreadsheetRange(null);
        });
    }

    @Test
    public void testRange() {
        final SpreadsheetCellReference lower = this.cell(1, 1);
        final SpreadsheetCellReference upper = this.cell(2, 2);
        this.rangeAndCheck(lower,
                upper,
                Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)));
    }

    @Test
    public void testRangeSwapped() {
        final SpreadsheetCellReference lower = this.cell(1, 1);
        final SpreadsheetCellReference upper = this.cell(2, 2);
        final SpreadsheetRange range = upper.spreadsheetRange(lower);
        this.checkEquals(Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)), range.range());
    }

    @Test
    public void testRangeOne() {
        final SpreadsheetCellReference lower = this.cell(1, 1);
        final SpreadsheetRange range = lower.spreadsheetRange(lower);
        this.checkEquals(Range.singleton(lower), range.range());
    }

    private void rangeAndCheck(final SpreadsheetCellReference cell,
                               final SpreadsheetCellReference other,
                               final Range<SpreadsheetCellReference> expected) {
        final Range<SpreadsheetCellReference> range = cell.range(other);
        assertEquals(expected,
                range,
                () -> cell + " range " + other);



        assertEquals(SpreadsheetRange.with(expected),
                cell.spreadsheetRange(other),
                () -> cell + " spreadsheetRange " + other);
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseInvalidCellReferenceFails() {
        this.parseFails("Invalid",
                IllegalArgumentException.class);
    }

    @Test
    public void testParseCellReferenceRelative() {
        this.parseAndCheck("A98",
                SpreadsheetColumnReference.with(0, SpreadsheetReferenceKind.RELATIVE)
                        .setRow(SpreadsheetRowReference.with(97, SpreadsheetReferenceKind.RELATIVE)));
    }

    @Test
    public void testParseCellReferenceAbsolute() {
        this.parseAndCheck("$A$98",
                SpreadsheetColumnReference.with(0, SpreadsheetReferenceKind.ABSOLUTE)
                        .setRow(SpreadsheetRowReference.with(97, SpreadsheetReferenceKind.ABSOLUTE)));
    }

    // parseCellReferenceRange..........................................................................................

    @Test
    public void testParseRange() {
        assertEquals(Range.greaterThanEquals(SpreadsheetExpressionReference.parseCellReference("B2"))
                        .and(Range.lessThanEquals(SpreadsheetExpressionReference.parseCellReference("D4"))),
                SpreadsheetCellReference.parseCellReferenceRange("B2:D4"));
    }

    @Test
    public void testParseRange2() {
        assertEquals(Range.greaterThanEquals(SpreadsheetExpressionReference.parseCellReference("$B$2"))
                        .and(Range.lessThanEquals(SpreadsheetExpressionReference.parseCellReference("$D$4"))),
                SpreadsheetCellReference.parseCellReferenceRange("$B$2:$D$4"));
    }

    @Test
    public void testParseRange3() {
        assertEquals(Range.greaterThanEquals(SpreadsheetExpressionReference.parseCellReference("$B2"))
                        .and(Range.lessThanEquals(SpreadsheetExpressionReference.parseCellReference("D$4"))),
                SpreadsheetCellReference.parseCellReferenceRange("$B2:D$4"));
    }

    // HasJsonNode..................................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testFromJsonNodeNullFails() {
        this.fromJsonNodeFails(JsonNode.nullNode());
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(123));
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array());
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object());
    }

    @Test
    public void testFromJsonNodeString() {
        this.fromJsonNodeAndCheck(JsonNode.string("$A$1"),
                SpreadsheetExpressionReference.parseCellReference("$A$1"));
    }

    // toString..................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createReference(), "$DT$457");
    }

    @Override
    SpreadsheetCellReference createReference() {
        return SpreadsheetCellReference.with(column(), row());
    }

    private SpreadsheetCellReference createReference(final int column, final int row) {
        return this.column(column).setRow(this.row(row));
    }

    private SpreadsheetColumnReference column() {
        return this.column(COLUMN);
    }

    private SpreadsheetColumnReference column(final int value) {
        return SpreadsheetColumnReference.with(value, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private SpreadsheetRowReference row() {
        return this.row(ROW);
    }

    private SpreadsheetRowReference row(final int value) {
        return SpreadsheetRowReference.with(value, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private void checkColumn(final SpreadsheetCellReference cell, final SpreadsheetColumnReference column) {
        assertEquals(column, cell.column(), "column");
    }

    private void checkRow(final SpreadsheetCellReference cell, final SpreadsheetRowReference row) {
        assertEquals(row, cell.row(), "row");
    }

    @Override
    public SpreadsheetCellReference createComparable() {
        return this.createReference();
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return false;
    }

    @Override
    public SpreadsheetCellReference createLowerOrUpper() {
        return this.createComparable();
    }

    @Override
    public Class<SpreadsheetCellReference> type() {
        return SpreadsheetCellReference.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting.........................................................................................


    @Override
    public SpreadsheetCellReference parse(final String text) {
        return SpreadsheetExpressionReference.parseCellReference(text);
    }

    @Override
    public Class<? extends RuntimeException> parseFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseFailedExpected(final RuntimeException expected) {
        return expected;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public SpreadsheetCellReference fromJsonNode(final JsonNode from) {
        return SpreadsheetCellReference.fromJsonNodeCellReference(from);
    }
}
