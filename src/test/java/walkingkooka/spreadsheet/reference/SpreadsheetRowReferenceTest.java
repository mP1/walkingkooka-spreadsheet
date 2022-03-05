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
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetRowReference> {

    @Test
    public void testMin() {
        final SpreadsheetRowReference min = SpreadsheetRowReference.MIN;
        this.checkEquals(0, min.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, min.referenceKind(), "referenceKind");
    }

    @Test
    public void testMax() {
        final SpreadsheetRowReference max = SpreadsheetRowReference.MAX;
        this.checkEquals(SpreadsheetRowReference.MAX_VALUE, max.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, max.referenceKind(), "referenceKind");
    }

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.row(23).setColumn(null));
    }

    @Test
    public void testSetColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = row.setColumn(column);
        this.checkEquals(column, cell.column(), "column");
        this.checkEquals(row, cell.row(), "row");
    }

    @Test
    public void testRow() {
        final SpreadsheetRowReference reference = SpreadsheetReferenceKind.ABSOLUTE.row(123);

        this.checkEquals(
                SpreadsheetRow.with(reference),
                reference.row()
        );
    }

    // Predicate........................................................................................................

    @Test
    public void testTestDifferentRowFalse() {
        final SpreadsheetRowReference selection = this.createSelection();
        this.testFalse(selection
                .add(1)
                .setColumn(this.columnReference())
        );
    }

    @Test
    public void testTestDifferentRowKindTrue() {
        final SpreadsheetRowReference selection = this.createSelection();
        this.testTrue(selection
                .setReferenceKind(selection.referenceKind().flip())
                .setColumn(this.columnReference())
        );
    }

    private SpreadsheetColumnReference columnReference() {
        return SpreadsheetColumnReference.parseColumn("A");
    }

    // range............................................................................................................

    @Test
    public void testRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        this.checkEquals(
                Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)),
                lower.range(upper)
        );
    }

    // rowRange..............................................................................................

    @Test
    public void testSpreadsheetRowRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        this.checkEquals(
                SpreadsheetSelection.parseRowRange("2:4"),
                lower.rowRange(upper)
        );
    }

    // toSpreadsheetRowRange..............................................................................................

    @Test
    public void testToSpreadsheetRowRange() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("2");

        this.checkEquals(
                SpreadsheetSelection.parseRowRange("2"),
                row.rowRange()
        );
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAbsolute() {
        final int value = 123;
        this.toRelativeAndCheck(SpreadsheetReferenceKind.ABSOLUTE.row(value), SpreadsheetReferenceKind.RELATIVE.row(value));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck(SpreadsheetReferenceKind.RELATIVE.row(123));
    }

    @Test
    public void testEqualReferenceKindIgnored() {
        this.compareToAndCheckEquals(
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
                SpreadsheetReferenceKind.RELATIVE.row(VALUE));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE + 999));
    }

    @Test
    public void testLess2() {
        this.compareToAndCheckLess(
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
                SpreadsheetReferenceKind.RELATIVE.row(VALUE + 999));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetRowReference row1 = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference row3 = SpreadsheetSelection.parseRow("3");
        final SpreadsheetRowReference row4 = SpreadsheetSelection.parseRow("$4");

        this.compareToArraySortAndCheck(row3, row1, row4, row2,
                row1, row2, row3, row4);
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
        this.parseStringAndCheck("$1", SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseStringAndCheck("$2", SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testParseRelative() {
        this.parseStringAndCheck("1", SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseStringAndCheck("2", SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // parseRowRange....................................................................................................

    @Test
    public void testParseRange() {
        this.checkEquals(
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseRow("2"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseRow("4")))
                ),
                SpreadsheetSelection.parseRowRange("2:4"));
    }

    @Test
    public void testParseRange2() {
        this.checkEquals(
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseRow("$2"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseRow("$5")))
                ),
                SpreadsheetSelection.parseRowRange("$2:$5"));
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.checkEquals(
                SpreadsheetRowReference.parseRow("9"),
                SpreadsheetRowReference.parseRow("7").add(2)
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        this.checkEquals(
                SpreadsheetRowReference.parseRow("9"),
                SpreadsheetRowReference.parseRow("7").addSaturated(2)
        );
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
        this.maxAndCheck("5", "6", RIGHT);
    }

    @Test
    public void testMaxLess2() {
        this.maxAndCheck("$5", "6", RIGHT);
    }

    @Test
    public void testMaxLess3() {
        this.maxAndCheck("5", "$6", RIGHT);
    }

    @Test
    public void testMaxLess4() {
        this.maxAndCheck("$5", "$6", RIGHT);
    }

    @Test
    public void testMaxEqual() {
        this.maxAndCheck("5", "5", LEFT);
    }

    @Test
    public void testMaxEqual2() {
        this.maxAndCheck("$5", "5", LEFT);
    }

    @Test
    public void testMaxEqual3() {
        this.maxAndCheck("5", "$5", LEFT);
    }

    @Test
    public void testMaxEqual4() {
        this.maxAndCheck("$5", "$5", LEFT);
    }

    @Test
    public void testMaxMore() {
        this.maxAndCheck("6", "5", LEFT);
    }

    @Test
    public void testMaxMore2() {
        this.maxAndCheck("$6", "5", LEFT);
    }

    @Test
    public void testMaxMore3() {
        this.maxAndCheck("6", "$5", LEFT);
    }

    @Test
    public void testMaxMore4() {
        this.maxAndCheck("$6", "$5", LEFT);
    }

    private void maxAndCheck(final String reference,
                             final String other,
                             final boolean RIGHT) {
        this.maxAndCheck(SpreadsheetRowReference.parseRow(reference),
                SpreadsheetRowReference.parseRow(other),
                RIGHT);
    }

    private void maxAndCheck(final SpreadsheetRowReference reference,
                             final SpreadsheetRowReference other,
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
        this.minAndCheck("5", "6", LEFT);
    }

    @Test
    public void testMinLess2() {
        this.minAndCheck("$5", "6", LEFT);
    }

    @Test
    public void testMinLess3() {
        this.minAndCheck("5", "$6", LEFT);
    }

    @Test
    public void testMinLess4() {
        this.minAndCheck("$5", "$6", LEFT);
    }

    @Test
    public void testMinEqual() {
        this.minAndCheck("5", "5", LEFT);
    }

    @Test
    public void testMinEqual2() {
        this.minAndCheck("$5", "5", LEFT);
    }

    @Test
    public void testMinEqual3() {
        this.minAndCheck("5", "$5", LEFT);
    }

    @Test
    public void testMinEqual4() {
        this.minAndCheck("$5", "$5", LEFT);
    }

    @Test
    public void testMinRight() {
        this.minAndCheck("6", "5", RIGHT);
    }

    @Test
    public void testMinRight2() {
        this.minAndCheck("$6", "5", RIGHT);
    }

    @Test
    public void testMinRight3() {
        this.minAndCheck("6", "$5", RIGHT);
    }

    @Test
    public void testMinRight4() {
        this.minAndCheck("$6", "$5", RIGHT);
    }

    private void minAndCheck(final String reference,
                             final String other,
                             final boolean left) {
        this.minAndCheck(SpreadsheetRowReference.parseRow(reference),
                SpreadsheetRowReference.parseRow(other),
                left);
    }

    private void minAndCheck(final SpreadsheetRowReference reference,
                             final SpreadsheetRowReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
                reference.min(other),
                () -> "min of " + reference + " and " + other);
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testRangeCheck2(
                "2",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftEdge() {
        this.testRangeCheck2(
                "3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeCenter() {
        this.testRangeCheck2(
                "4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeRightEdge() {
        this.testRangeCheck2(
                "5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testRangeCheck2(
                "6",
                "C3:E5",
                false
        );
    }

    private void testRangeCheck2(final String row,
                                 final String range,
                                 final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetRowReference.parseRow(row),
                SpreadsheetCellRange.parseCellRange(range),
                expected
        );
    }

    // rowRange...................................................................................................,,,

    @Test
    public void testRowRangeSpreadsheetRowRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("2");

        this.checkEquals(
                SpreadsheetRowReferenceRange.with(Range.greaterThanEquals(upper)),
                lower.rowRange(upper),
                () -> lower + " rowRange " + upper
        );
    }

    @Test
    public void testRowRange() {
        final SpreadsheetRowReference row = this.createSelection();

        this.checkEquals(
                SpreadsheetRowReferenceRange.with(Range.singleton(row)),
                row.rowRange(),
                () -> row + ".rowRange"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRow("12"),
                "row 12" + EOL
        );
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testJsonNodeUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string("!9"));
    }

    @Test
    public void testJsonNodeUnmarshallStringAbsolute() {
        this.unmarshallAndCheck(JsonNode.string("$1"), SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testJsonNodeUnmarshallStringAbsolute2() {
        this.unmarshallAndCheck(JsonNode.string("$2"), SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testJsonNodeUnmarshallStringRelative() {
        this.unmarshallAndCheck(JsonNode.string("1"), SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testJsonNodeUnmarshallStringRelative2() {
        this.unmarshallAndCheck(JsonNode.string("2"), SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // toString........................................................................

    @Test
    public void testToStringRelative() {
        this.checkToString(0, SpreadsheetReferenceKind.RELATIVE, "1");
    }

    @Test
    public void testToStringRelative2() {
        this.checkToString(123, SpreadsheetReferenceKind.RELATIVE, "124");
    }

    @Test
    public void testToStringAbsolute() {
        this.checkToString(0, SpreadsheetReferenceKind.ABSOLUTE, "$1");
    }

    @Override
    SpreadsheetRowReference createReference(final int value, final SpreadsheetReferenceKind kind) {
        return SpreadsheetColumnOrRowReference.row(value, kind);
    }

    @Override
    int maxValue() {
        return SpreadsheetRowReference.MAX_VALUE;
    }

    @Override
    public Class<SpreadsheetRowReference> type() {
        return SpreadsheetRowReference.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetRowReference unmarshall(final JsonNode from,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetRowReference.unmarshallRow(from, context);
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetRowReference parseString(final String text) {
        return SpreadsheetSelection.parseRow(text);
    }
}
