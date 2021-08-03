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
import walkingkooka.collect.RangeBound;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowReferenceRangeTest extends SpreadsheetColumnOrRowReferenceRangeTestCase<SpreadsheetRowReferenceRange, SpreadsheetRowReference> {

    @Test
    public void testWith() {
        final SpreadsheetRowReference lower = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetRowReference.parseRow("4");

        final Range<SpreadsheetRowReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        final SpreadsheetRowReferenceRange selection = SpreadsheetRowReferenceRange.with(range);
        assertSame(range, selection.range(), "range");
        assertSame(lower, selection.begin(), "begin");
        assertSame(upper, selection.end(), "end");
    }

    @Test
    public void testTestAbove() {
        this.testFalse(SpreadsheetCellReference.parseCell("A1"));
    }

    @Test
    public void testTestLeft() {
        this.testTrue(SpreadsheetCellReference.parseCell("B2"));
    }

    @Test
    public void testTestRight() {
        this.testTrue(SpreadsheetCellReference.parseCell("D4"));
    }

    @Test
    public void testTestBelow() {
        this.testFalse(SpreadsheetCellReference.parseCell("E5"));
    }

    // testCellRange....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck2(
                "1:1",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeBefore2() {
        this.testCellRangeAndCheck2(
                "1:2",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftOverlap() {
        this.testCellRangeAndCheck2(
                "1:3",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeInside() {
        this.testCellRangeAndCheck2(
                "4:5",
                "C3:F6",
                true
        );
    }

    @Test
    public void testTestCellRangeRightOverlap() {
        this.testCellRangeAndCheck2(
                "4:5",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAll() {
        this.testCellRangeAndCheck2(
                "1:5",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck2(
                "5:5",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeAfter2() {
        this.testCellRangeAndCheck2(
                "5:6",
                "C3:D4",
                false
        );
    }

    private void testCellRangeAndCheck2(final String rowRange,
                                        final String cellRange,
                                        final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetSelection.parseRowRange(rowRange),
                SpreadsheetSelection.parseCellRange(cellRange),
                expected
        );
    }

    // setColumnReferenceRange............................................................................................

    @Test
    public void testSetColumnReferenceRangeNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setColumnReferenceRange(null));
    }

    @Test
    public void testSetColumnReferenceRange() {
        this.setColumnReferenceRangeAndCheck("2:4", "B:D", "B2:D4");
    }

    @Test
    public void testSetColumnReferenceRange2() {
        this.setColumnReferenceRangeAndCheck("2", "B", "B2");
    }

    @Test
    public void testSetColumnReferenceRange3() {
        this.setColumnReferenceRangeAndCheck("2", "B:D", "B2:D2");
    }

    private void setColumnReferenceRangeAndCheck(final String row,
                                                 final String column,
                                                 final String range) {
        assertEquals(
                SpreadsheetSelection.parseCellRange(range),
                SpreadsheetSelection.parseColumnRange(column).setRowReferenceRange(SpreadsheetSelection.parseRowRange(row)),
                () -> column + " setRowReferenceRange " + row
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetRowReferenceRange selection = this.createSelection();

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
            protected void visit(final SpreadsheetRowReferenceRange s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        assertEquals("132", b.toString());
    }

    @Test
    public void testFromJson() {
        this.unmarshallAndCheck(JsonNode.string("2:4"), this.createSelection());
    }

    @Test
    public void testToJson() {
        this.marshallAndCheck(this.createSelection(), JsonNode.string("2:4"));
    }

    @Test
    public void testDifferentColumn() {
        this.checkNotEquals(SpreadsheetSelection.parseRowRange("55:66"));
    }

    @Test
    public void testToString() {
        final SpreadsheetRowReference lower = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetRowReference.parseRow("4");

        final Range<SpreadsheetRowReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        this.toStringAndCheck(SpreadsheetRowReferenceRange.with(range), "2:4");
    }

    @Override
    final SpreadsheetRowReferenceRange createSelection() {
        final SpreadsheetRowReference lower = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetRowReference.parseRow("4");

        return SpreadsheetRowReferenceRange.with(
                Range.with(
                        RangeBound.inclusive(lower),
                        RangeBound.inclusive(upper)
                )
        );
    }

    @Override
    SpreadsheetRowReferenceRange createSelection(final Range<SpreadsheetRowReference> range) {
        return SpreadsheetRowReferenceRange.with(range);
    }

    @Override
    public SpreadsheetRowReferenceRange parseString(final String text) {
        return SpreadsheetSelection.parseRowRange(text);
    }

    @Override
    public SpreadsheetRowReferenceRange unmarshall(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.unmarshallRowRange(node, context);
    }

    @Override
    public Class<SpreadsheetRowReferenceRange> type() {
        return SpreadsheetRowReferenceRange.class;
    }
}
