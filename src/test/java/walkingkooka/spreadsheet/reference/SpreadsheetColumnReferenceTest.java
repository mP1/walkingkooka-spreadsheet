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
import walkingkooka.compare.Range;
import walkingkooka.tree.json.JsonNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetColumnReference> {

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(null);
        });
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
        this.compareToAndCheckEqual(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
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
        final SpreadsheetColumnReference column1 = SpreadsheetColumnReference.parse("A");
        final SpreadsheetColumnReference column2 = SpreadsheetColumnReference.parse("B");
        final SpreadsheetColumnReference column3 = SpreadsheetColumnReference.parse("C");
        final SpreadsheetColumnReference column4 = SpreadsheetColumnReference.parse("$D");

        this.compareToArraySortAndCheck(column3, column1, column4, column2,
                column1, column2, column3, column4);
    }

    // parseRange....................................................................................................

    @Test
    public void testParseRange() {
        assertEquals(Range.greaterThanEquals(SpreadsheetColumnReference.parse("B"))
                        .and(Range.lessThanEquals(SpreadsheetColumnReference.parse("D"))),
                SpreadsheetColumnReference.parseRange("B:D"));
    }

    @Test
    public void testParseRange2() {
        assertEquals(Range.greaterThanEquals(SpreadsheetColumnReference.parse("$B"))
                        .and(Range.lessThanEquals(SpreadsheetColumnReference.parse("$D"))),
                SpreadsheetColumnReference.parseRange("$B:$D"));
    }

    // parseString.....................................................................................................

    @Test
    public void testParseEmptyFails() {
        this.parseFails("", IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidFails() {
        this.parseFails("!9", IllegalArgumentException.class);
    }

    @Test
    public void testParseAbsolute() {
        this.parseAndCheck("$A", SpreadsheetReferenceKind.ABSOLUTE.column(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseAndCheck("$B", SpreadsheetReferenceKind.ABSOLUTE.column(1));
    }

    @Test
    public void testParseRelative() {
        this.parseAndCheck("A", SpreadsheetReferenceKind.RELATIVE.column(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseAndCheck("B", SpreadsheetReferenceKind.RELATIVE.column(1));
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testFromJsonNodeStringInvalidFails() {
        this.fromJsonNodeFails(JsonNode.string("!9"), IllegalArgumentException.class);
    }

    @Test
    public void testFromJsonNodeStringAbsolute() {
        this.fromJsonNodeAndCheck(JsonNode.string("$A"), SpreadsheetReferenceKind.ABSOLUTE.column(0));
    }

    @Test
    public void testFromJsonNodeStringAbsolute2() {
        this.fromJsonNodeAndCheck(JsonNode.string("$B"), SpreadsheetReferenceKind.ABSOLUTE.column(1));
    }

    @Test
    public void testFromJsonNodeStringRelative() {
        this.fromJsonNodeAndCheck(JsonNode.string("A"), SpreadsheetReferenceKind.RELATIVE.column(0));
    }

    @Test
    public void testFromJsonNodeStringRelative2() {
        this.fromJsonNodeAndCheck(JsonNode.string("B"), SpreadsheetReferenceKind.RELATIVE.column(1));
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

    @Test
    public void testToStringRelative3() {
        this.checkToString((1 * 26) + 0, SpreadsheetReferenceKind.RELATIVE, "AA");
    }

    @Test
    public void testToStringRelative4() {
        this.checkToString((1 * 26) + 3, SpreadsheetReferenceKind.RELATIVE, "AD");
    }

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
        return SpreadsheetColumnReference.with(value, kind);
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
    public SpreadsheetColumnReference parse(final String text) {
        return SpreadsheetColumnReference.parse(text);
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
    public SpreadsheetColumnReference fromJsonNode(final JsonNode from) {
        return SpreadsheetColumnReference.fromJsonNode(from);
    }
}
