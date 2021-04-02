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

public final class SpreadsheetColumnReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetColumnReference> {

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(null));
    }

    @Test
    public void testSetRow() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = column.setRow(row);
        assertEquals(column, cell.column(), "column");
        assertEquals(row, cell.row(), "row");
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
        final SpreadsheetColumnReference column1 = SpreadsheetColumnOrRowReference.parseColumn("A");
        final SpreadsheetColumnReference column2 = SpreadsheetColumnOrRowReference.parseColumn("B");
        final SpreadsheetColumnReference column3 = SpreadsheetColumnOrRowReference.parseColumn("C");
        final SpreadsheetColumnReference column4 = SpreadsheetColumnOrRowReference.parseColumn("$D");

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

    // parseColumnRange....................................................................................................

    @Test
    public void testParseRange() {
        assertEquals(Range.greaterThanEquals(SpreadsheetColumnOrRowReference.parseColumn("B"))
                        .and(Range.lessThanEquals(SpreadsheetColumnOrRowReference.parseColumn("D"))),
                SpreadsheetColumnOrRowReference.parseColumnRange("B:D"));
    }

    @Test
    public void testParseRange2() {
        assertEquals(Range.greaterThanEquals(SpreadsheetColumnOrRowReference.parseColumn("$B"))
                        .and(Range.lessThanEquals(SpreadsheetColumnOrRowReference.parseColumn("$D"))),
                SpreadsheetColumnOrRowReference.parseColumnRange("$B:$D"));
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
        assertEquals(
                SpreadsheetColumnReference.parseColumn("M"),
                SpreadsheetColumnReference.parseColumn("K").add(2)
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
        assertThrows(NullPointerException.class, () -> this.createReference().max(null));
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
        assertEquals(left ? reference : other,
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

    @Override
    SpreadsheetColumnReference createReference(final int value, final SpreadsheetReferenceKind kind) {
        return SpreadsheetColumnOrRowReference.column(value, kind);
    }

    @Override
    int maxValue() {
        return SpreadsheetColumnReference.MAX;
    }

    @Override
    public Class<SpreadsheetColumnReference> type() {
        return SpreadsheetColumnReference.class;
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetColumnReference parseString(final String text) {
        return SpreadsheetColumnOrRowReference.parseColumn(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetColumnReference unmarshall(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumnReference.unmarshallColumn(from, context);
    }
}
