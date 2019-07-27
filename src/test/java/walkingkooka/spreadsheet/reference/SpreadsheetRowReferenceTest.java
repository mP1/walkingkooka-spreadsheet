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

public final class SpreadsheetRowReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetRowReference> {

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetReferenceKind.ABSOLUTE.row(23).setColumn(null);
        });
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
        final SpreadsheetRowReference row1 = SpreadsheetRowReference.parse("1");
        final SpreadsheetRowReference row2 = SpreadsheetRowReference.parse("2");
        final SpreadsheetRowReference row3 = SpreadsheetRowReference.parse("3");
        final SpreadsheetRowReference row4 = SpreadsheetRowReference.parse("$4");

        this.compareToArraySortAndCheck(row3, row1, row4, row2,
                row1, row2, row3, row4);
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
        this.parseAndCheck("$1", SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseAndCheck("$2", SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testParseRelative() {
        this.parseAndCheck("1", SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseAndCheck("2", SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // parseRange....................................................................................................

    @Test
    public void testParseRange() {
        assertEquals(Range.greaterThanEquals(SpreadsheetRowReference.parse("2"))
                        .and(Range.lessThanEquals(SpreadsheetRowReference.parse("4"))),
                SpreadsheetRowReference.parseRange("2:4"));
    }

    @Test
    public void testParseRange2() {
        assertEquals(Range.greaterThanEquals(SpreadsheetRowReference.parse("$2"))
                        .and(Range.lessThanEquals(SpreadsheetRowReference.parse("$5"))),
                SpreadsheetRowReference.parseRange("$2:$5"));
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testFromJsonNodeStringInvalidFails() {
        this.fromJsonNodeFails(JsonNode.string("!9"), IllegalArgumentException.class);
    }

    @Test
    public void testFromJsonNodeStringAbsolute() {
        this.fromJsonNodeAndCheck(JsonNode.string("$1"), SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testFromJsonNodeStringAbsolute2() {
        this.fromJsonNodeAndCheck(JsonNode.string("$2"), SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testFromJsonNodeStringRelative() {
        this.fromJsonNodeAndCheck(JsonNode.string("1"), SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testFromJsonNodeStringRelative2() {
        this.fromJsonNodeAndCheck(JsonNode.string("2"), SpreadsheetReferenceKind.RELATIVE.row(1));
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
        return SpreadsheetRowReference.with(value, kind);
    }

    @Override
    int maxValue() {
        return SpreadsheetRowReference.MAX;
    }

    @Override
    public Class<SpreadsheetRowReference> type() {
        return SpreadsheetRowReference.class;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public SpreadsheetRowReference fromJsonNode(final JsonNode from) {
        return SpreadsheetRowReference.fromJsonNode(from);
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetRowReference parse(final String text) {
        return SpreadsheetRowReference.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
