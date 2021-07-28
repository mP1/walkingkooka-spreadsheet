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

public final class SpreadsheetColumnReferenceRangeTest extends SpreadsheetColumnOrRowReferenceRangeTestCase<SpreadsheetColumnReferenceRange, SpreadsheetColumnReference> {

    @Test
    public void testWith() {
        final SpreadsheetColumnReference lower = SpreadsheetColumnReference.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetColumnReference.parseColumn("D");

        final Range<SpreadsheetColumnReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        final SpreadsheetColumnReferenceRange selection = SpreadsheetColumnReferenceRange.with(range);
        assertSame(range, selection.range(), "range");
        assertSame(lower, selection.begin(), "begin");
        assertSame(upper, selection.end(), "end");
    }

    @Test
    public void testTestBefore() {
        this.testFalse(SpreadsheetCellReference.parseCellReference("A1"));
    }

    @Test
    public void testTestLeft() {
        this.testTrue(SpreadsheetCellReference.parseCellReference("B1"));
    }

    @Test
    public void testTestRight() {
        this.testTrue(SpreadsheetCellReference.parseCellReference("D2"));
    }

    @Test
    public void testTestAfter() {
        this.testFalse(SpreadsheetCellReference.parseCellReference("E1"));
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnReferenceRange selection = this.createSelection();

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
            protected void visit(final SpreadsheetColumnReferenceRange s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        assertEquals("132", b.toString());
    }

    @Test
    public void testFromJson() {
        this.unmarshallAndCheck(JsonNode.string("B:D"), this.createSelection());
    }

    @Test
    public void testToJson() {
        this.marshallAndCheck(this.createSelection(), JsonNode.string("B:D"));
    }

    @Test
    public void testDifferentColumn() {
        this.checkNotEquals(SpreadsheetSelection.parseColumnRange("Y:Z"));
    }

    @Test
    public void testToString() {
        final SpreadsheetColumnReference lower = SpreadsheetColumnReference.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetColumnReference.parseColumn("D");

        final Range<SpreadsheetColumnReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        this.toStringAndCheck(SpreadsheetColumnReferenceRange.with(range), "B:D");
    }

    @Override
    final SpreadsheetColumnReferenceRange createSelection() {
        final SpreadsheetColumnReference lower = SpreadsheetColumnReference.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetColumnReference.parseColumn("D");

        return SpreadsheetColumnReferenceRange.with(
                Range.with(
                        RangeBound.inclusive(lower),
                        RangeBound.inclusive(upper)
                )
        );
    }

    @Override
    SpreadsheetColumnReferenceRange createSelection(final Range<SpreadsheetColumnReference> range) {
        return SpreadsheetColumnReferenceRange.with(range);
    }

    @Override
    public SpreadsheetColumnReferenceRange parseString(final String text) {
        return SpreadsheetSelection.parseColumnRange(text);
    }

    @Override
    public SpreadsheetColumnReferenceRange unmarshall(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.unmarshallColumnRange(node, context);
    }

    @Override
    public Class<SpreadsheetColumnReferenceRange> type() {
        return SpreadsheetColumnReferenceRange.class;
    }
}
