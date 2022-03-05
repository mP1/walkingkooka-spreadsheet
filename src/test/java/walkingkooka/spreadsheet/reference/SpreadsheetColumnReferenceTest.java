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
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetColumnReference> {

    @Test
    public void testMin() {
        final SpreadsheetColumnReference min = SpreadsheetColumnReference.MIN;
        this.checkEquals(0, min.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, min.referenceKind(), "referenceKind");
    }

    @Test
    public void testMax() {
        final SpreadsheetColumnReference max = SpreadsheetColumnReference.MAX;
        this.checkEquals(SpreadsheetColumnReference.MAX_VALUE, max.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, max.referenceKind(), "referenceKind");
    }

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(null));
    }

    @Test
    public void testSetRow() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = column.setRow(row);
        this.checkEquals(column, cell.column(), "column");
        this.checkEquals(row, cell.row(), "row");
    }

    @Test
    public void testColumn() {
        final SpreadsheetColumnReference reference = SpreadsheetReferenceKind.ABSOLUTE.column(123);

        this.checkEquals(
                SpreadsheetColumn.with(reference),
                reference.column()
        );
    }

    // Predicate........................................................................................................

    @Test
    public void testTestDifferentColumnFalse() {
        final SpreadsheetColumnReference selection = this.createSelection();
        this.testFalse(selection
                .add(1)
                .setRow(this.row())
        );
    }

    @Test
    public void testTestDifferentColumnKindTrue() {
        final SpreadsheetColumnReference selection = this.createSelection();
        this.testTrue(selection
                .setReferenceKind(selection.referenceKind().flip())
                .setRow(this.row()));
    }

    private SpreadsheetRowReference row() {
        return SpreadsheetRowReference.parseRow("1");
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testRangeCheck2(
                "B",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftEdge() {
        this.testRangeCheck2(
                "C",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeCenter() {
        this.testRangeCheck2(
                "D",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeRightEdge() {
        this.testRangeCheck2(
                "E",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testRangeCheck2(
                "F",
                "C3:E5",
                false
        );
    }

    private void testRangeCheck2(final String column,
                                 final String range,
                                 final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetColumnReference.parseColumn(column),
                SpreadsheetCellRange.parseCellRange(range),
                expected
        );
    }

    // range............................................................................................................

    @Test
    public void testRange() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

        this.checkEquals(
                Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)),
                lower.range(upper)
        );
    }

    // columnRange..............................................................................................

    @Test
    public void testSpreadsheetColumnRange() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

        this.checkEquals(
                SpreadsheetSelection.parseColumnRange("B:D"),
                lower.columnRange(upper)
        );
    }

    // toSpreadsheetColumnRange..............................................................................................

    @Test
    public void testToSpreadsheetColumnRange() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.checkEquals(
                SpreadsheetSelection.parseColumnRange("C"),
                column.columnRange()
        );
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAbsolute() {
        final int value = 123;
        this.toRelativeAndCheck(SpreadsheetReferenceKind.ABSOLUTE.column(value), SpreadsheetReferenceKind.RELATIVE.column(value));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck(SpreadsheetReferenceKind.RELATIVE.column(123));
    }

    @Test
    public void testEqualReferenceKindIgnored() {
        this.compareToAndCheckEquals(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
                SpreadsheetReferenceKind.RELATIVE.column(VALUE));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
                SpreadsheetReferenceKind.ABSOLUTE.column(VALUE + 999));
    }

    @Test
    public void testLess2() {
        this.compareToAndCheckLess(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
                SpreadsheetReferenceKind.RELATIVE.column(VALUE + 999));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetColumnReference column1 = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnReference column2 = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference column3 = SpreadsheetSelection.parseColumn("C");
        final SpreadsheetColumnReference column4 = SpreadsheetSelection.parseColumn("$D");

        this.compareToArraySortAndCheck(column3, column1, column4, column2,
                column1, column2, column3, column4);
    }

    // parseColumn.......................................................................................................

    @Test
    public void testParseColumnUpperCased() {
        this.parseStringAndCheck("A", SpreadsheetColumnReference.with(0, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnUpperCased2() {
        this.parseStringAndCheck("B", SpreadsheetColumnReference.with(1, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnUpperCasedAbsolute() {
        this.parseStringAndCheck("$C", SpreadsheetColumnReference.with(2, SpreadsheetReferenceKind.ABSOLUTE));
    }

    @Test
    public void testParseColumnLowerCased() {
        this.parseStringAndCheck("d", SpreadsheetColumnReference.with(3, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnLowerCasedAbsolute() {
        this.parseStringAndCheck("$e", SpreadsheetColumnReference.with(4, SpreadsheetReferenceKind.ABSOLUTE));
    }

    @Test
    public void testParseColumnAA() {
        this.parseStringAndCheck("AA", SpreadsheetColumnReference.with(26, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnAAB() {
        this.parseStringAndCheck("AAB", SpreadsheetColumnReference.with(703, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnXFD() {
        this.parseStringAndCheck("XFD", SpreadsheetColumnReference.with(16383, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnXFEFails() {
        this.parseStringFails("XFE", IllegalArgumentException.class);
    }

    // parseColumnRange....................................................................................................

    @Test
    public void testParseRange() {
        this.checkEquals(
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseColumn("B"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseColumn("D")))
                ),
                SpreadsheetSelection.parseColumnRange("B:D"));
    }

    @Test
    public void testParseRange2() {
        this.checkEquals(
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseColumn("$B"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseColumn("$D")))
                ),
                SpreadsheetSelection.parseColumnRange("$B:$D"));
    }

    // parseString.....................................................................................................

    @Test
    public void testParseEmptyFails() {
        this.parseStringFails("", IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidFails() {
        this.parseStringFails("!9", IllegalArgumentException.class);
    }

    @Test
    public void testParseAbsolute() {
        this.parseStringAndCheck("$A", SpreadsheetReferenceKind.ABSOLUTE.column(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseStringAndCheck("$B", SpreadsheetReferenceKind.ABSOLUTE.column(1));
    }

    @Test
    public void testParseRelative() {
        this.parseStringAndCheck("A", SpreadsheetReferenceKind.RELATIVE.column(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseStringAndCheck("B", SpreadsheetReferenceKind.RELATIVE.column(1));
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.checkEquals(
                SpreadsheetColumnReference.parseColumn("M"),
                SpreadsheetColumnReference.parseColumn("K").add(2)
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        this.checkEquals(
                SpreadsheetColumnReference.parseColumn("M"),
                SpreadsheetColumnReference.parseColumn("K").addSaturated(2)
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnReference selection = this.createSelection();

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
            protected void visit(final SpreadsheetColumnReference s) {
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
                SpreadsheetSelection.parseColumn("AB"),
                "column AB" + EOL
        );
    }

    // columnRange...................................................................................................,,,

    @Test
    public void testColumnRangeSpreadsheetColumnRange() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("B");

        this.checkEquals(
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(lower)
                                .and(
                                        Range.lessThanEquals(upper)
                                )
                ),
                lower.columnRange(upper),
                () -> lower + " columnRange " + upper
        );
    }

    @Test
    public void testColumnRange() {
        final SpreadsheetColumnReference column = this.createSelection();
        this.checkEquals(
                SpreadsheetColumnReferenceRange.with(Range.singleton(column)),
                column.columnRange(),
                () -> column + ".columnRange"
        );
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testJsonNodeUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string("!9"));
    }

    @Test
    public void testJsonNodeUnmarshallStringAbsolute() {
        this.unmarshallAndCheck(JsonNode.string("$A"), SpreadsheetReferenceKind.ABSOLUTE.column(0));
    }

    @Test
    public void testJsonNodeUnmarshallStringAbsolute2() {
        this.unmarshallAndCheck(JsonNode.string("$B"), SpreadsheetReferenceKind.ABSOLUTE.column(1));
    }

    @Test
    public void testJsonNodeUnmarshallStringRelative() {
        this.unmarshallAndCheck(JsonNode.string("A"), SpreadsheetReferenceKind.RELATIVE.column(0));
    }

    @Test
    public void testJsonNodeUnmarshallStringRelative2() {
        this.unmarshallAndCheck(JsonNode.string("B"), SpreadsheetReferenceKind.RELATIVE.column(1));
    }

    // max.............................................................................................................

    private final static boolean LEFT = true;
    private final static boolean RIGHT = !LEFT;

    @Test
    public void testMaxNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().max(null));
    }

    @Test
    public void testMaxLess() {
        this.maxAndCheck("A", "B", RIGHT);
    }

    @Test
    public void testMaxLess2() {
        this.maxAndCheck("$A", "B", RIGHT);
    }

    @Test
    public void testMaxLess3() {
        this.maxAndCheck("A", "$B", RIGHT);
    }

    @Test
    public void testMaxLess4() {
        this.maxAndCheck("$A", "$B", RIGHT);
    }

    @Test
    public void testMaxEqual() {
        this.maxAndCheck("A", "A", LEFT);
    }

    @Test
    public void testMaxEqual2() {
        this.maxAndCheck("$A", "A", LEFT);
    }

    @Test
    public void testMaxEqual3() {
        this.maxAndCheck("A", "$A", LEFT);
    }

    @Test
    public void testMaxEqual4() {
        this.maxAndCheck("$A", "$A", LEFT);
    }

    @Test
    public void testMaxMore() {
        this.maxAndCheck("B", "A", LEFT);
    }

    @Test
    public void testMaxMore2() {
        this.maxAndCheck("$B", "A", LEFT);
    }

    @Test
    public void testMaxMore3() {
        this.maxAndCheck("B", "$A", LEFT);
    }

    @Test
    public void testMaxMore4() {
        this.maxAndCheck("$B", "$A", LEFT);
    }

    private void maxAndCheck(final String reference,
                             final String other,
                             final boolean RIGHT) {
        this.maxAndCheck(SpreadsheetColumnReference.parseColumn(reference),
                SpreadsheetColumnReference.parseColumn(other),
                RIGHT);
    }

    private void maxAndCheck(final SpreadsheetColumnReference reference,
                             final SpreadsheetColumnReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
                reference.max(other),
                () -> "max of " + reference + " and " + other);
    }
    // min.............................................................................................................

    @Test
    public void testMinNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().min(null));
    }

    @Test
    public void testMinLess() {
        this.minAndCheck("A", "B", LEFT);
    }

    @Test
    public void testMinLess2() {
        this.minAndCheck("$A", "B", LEFT);
    }

    @Test
    public void testMinLess3() {
        this.minAndCheck("A", "$B", LEFT);
    }

    @Test
    public void testMinLess4() {
        this.minAndCheck("$A", "$B", LEFT);
    }

    @Test
    public void testMinEqual() {
        this.minAndCheck("A", "A", LEFT);
    }

    @Test
    public void testMinEqual2() {
        this.minAndCheck("$A", "A", LEFT);
    }

    @Test
    public void testMinEqual3() {
        this.minAndCheck("A", "$A", LEFT);
    }

    @Test
    public void testMinEqual4() {
        this.minAndCheck("$A", "$A", LEFT);
    }

    @Test
    public void testMinRight() {
        this.minAndCheck("B", "A", RIGHT);
    }

    @Test
    public void testMinRight2() {
        this.minAndCheck("$B", "A", RIGHT);
    }

    @Test
    public void testMinRight3() {
        this.minAndCheck("B", "$A", RIGHT);
    }

    @Test
    public void testMinRight4() {
        this.minAndCheck("$B", "$A", RIGHT);
    }

    private void minAndCheck(final String reference,
                             final String other,
                             final boolean left) {
        this.minAndCheck(SpreadsheetColumnReference.parseColumn(reference),
                SpreadsheetColumnReference.parseColumn(other),
                left);
    }

    private void minAndCheck(final SpreadsheetColumnReference reference,
                             final SpreadsheetColumnReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
                reference.min(other),
                () -> "min of " + reference + " and " + other);
    }

    // toString.....................................................................................................

    @Test
    public void testToStringRelative() {
        this.checkToString(0, SpreadsheetReferenceKind.RELATIVE, "A");
    }

    @Test
    public void testToStringRelative2() {
        this.checkToString(25, SpreadsheetReferenceKind.RELATIVE, "Z");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testToStringRelative3() {
        this.checkToString((1 * 26) + 0, SpreadsheetReferenceKind.RELATIVE, "AA");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testToStringRelative4() {
        this.checkToString((1 * 26) + 3, SpreadsheetReferenceKind.RELATIVE, "AD");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testToStringRelative5() {
        this.checkToString((1 * 26 * 26) + (25 * 26) + 12, SpreadsheetReferenceKind.RELATIVE, "AYM");
    }

    @Test
    public void testToStringAbsolute() {
        this.checkToString(0, SpreadsheetReferenceKind.ABSOLUTE, "$A");
    }

    @Test
    public void testToStringAbsolute2() {
        this.checkToString(2, SpreadsheetReferenceKind.ABSOLUTE, "$C");
    }

    @Test
    public void testToStringMaxValue() {
        this.checkToString(SpreadsheetColumnReference.MAX_VALUE, SpreadsheetReferenceKind.RELATIVE, "XFD");
    }

    @Override
    SpreadsheetColumnReference createReference(final int value, final SpreadsheetReferenceKind kind) {
        return SpreadsheetColumnOrRowReference.column(value, kind);
    }

    @Override
    int maxValue() {
        return SpreadsheetColumnReference.MAX_VALUE;
    }

    @Override
    public Class<SpreadsheetColumnReference> type() {
        return SpreadsheetColumnReference.class;
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetColumnReference parseString(final String text) {
        return SpreadsheetSelection.parseColumn(text);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetColumnReference unmarshall(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumnReference.unmarshallColumn(from, context);
    }
}
