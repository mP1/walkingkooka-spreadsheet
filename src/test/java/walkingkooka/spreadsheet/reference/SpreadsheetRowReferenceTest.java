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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetRowReference> {

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.row(23).setColumn(null));
    }

    @Test
    public void testSetColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = row.setColumn(column);
        assertEquals(column, cell.column(), "column");
        assertEquals(row, cell.row(), "row");
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
        final SpreadsheetRowReference row1 = SpreadsheetColumnOrRowReference.parseRow("1");
        final SpreadsheetRowReference row2 = SpreadsheetColumnOrRowReference.parseRow("2");
        final SpreadsheetRowReference row3 = SpreadsheetColumnOrRowReference.parseRow("3");
        final SpreadsheetRowReference row4 = SpreadsheetColumnOrRowReference.parseRow("$4");

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

    // parseColumnRange....................................................................................................

    @Test
    public void testParseRange() {
        assertEquals(Range.greaterThanEquals(SpreadsheetColumnOrRowReference.parseRow("2"))
                        .and(Range.lessThanEquals(SpreadsheetColumnOrRowReference.parseRow("4"))),
                SpreadsheetColumnOrRowReference.parseRowRange("2:4"));
    }

    @Test
    public void testParseRange2() {
        assertEquals(Range.greaterThanEquals(SpreadsheetColumnOrRowReference.parseRow("$2"))
                        .and(Range.lessThanEquals(SpreadsheetColumnOrRowReference.parseRow("$5"))),
                SpreadsheetColumnOrRowReference.parseRowRange("$2:$5"));
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        assertEquals(
                SpreadsheetRowReference.parseRow("9"),
                SpreadsheetRowReference.parseRow("7").add(2)
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        assertEquals(
                SpreadsheetRowReference.parseRow("9"),
                SpreadsheetRowReference.parseRow("7").addSaturated(2)
        );
    }

    // max.............................................................................................................

    private final static boolean LEFT = true;
    private final static boolean RIGHT = !LEFT;

    @Test
    public void testMaxNullFails() {
        assertThrows(NullPointerException.class, () -> this.createReference().max(null));
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
        assertEquals(left ? reference : other,
                reference.max(other),
                () -> "max of " + reference + " and " + other);
    }

    // min.............................................................................................................

    @Test
    public void testMinNullFails() {
        assertThrows(NullPointerException.class, () -> this.createReference().min(null));
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
        assertEquals(left ? reference : other,
                reference.min(other),
                () -> "min of " + reference + " and " + other);
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
        return SpreadsheetColumnOrRowReference.parseRow(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
