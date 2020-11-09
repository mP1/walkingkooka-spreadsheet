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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPixelRectangleTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetPixelRectangle>
        implements ParseStringTesting<SpreadsheetPixelRectangle> {

    private final static double WIDTH = 50;
    private final static double HEIGHT = 75;

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(0, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(-1, HEIGHT));
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(WIDTH, 0));
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(WIDTH, -1));
    }

    @Test
    public void testWith() {
        this.check(SpreadsheetPixelRectangle.with(WIDTH, HEIGHT));
    }

    // SpreadsheetExpressionReferenceVisitor.............................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetPixelRectangle reference = this.createReference();

        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference r) {
                assertSame(reference, r);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference r) {
                assertSame(reference, r);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetExpressionReference r) {
                assertSame(reference, r);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetExpressionReference r) {
                assertSame(reference, r);
                b.append("4");
            }

            @Override
            protected void visit(final SpreadsheetPixelRectangle r) {
                assertSame(reference, r);
                b.append("5");
            }
        }.accept(reference);
        assertEquals("13542", b.toString());
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentWidth() {
        this.checkNotEquals(SpreadsheetPixelRectangle.with(1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentHeight() {
        this.checkNotEquals(SpreadsheetPixelRectangle.with(WIDTH, 1000 + HEIGHT));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetPixelRectangle.with(40, 50), "40.0x50.0");
    }

    @Test
    public void testString2() {
        this.toStringAndCheck(SpreadsheetPixelRectangle.with(40.5, 50.75), "40.5x50.75");
    }

    // helpers .........................................................................................................

    @Override
    SpreadsheetPixelRectangle createReference() {
        return this.rectangle();
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseMissingSeparatorFails() {
        this.parseStringFails2("400", "Missing separator 'x' in \"400\"");
    }

    @Test
    public void testParseMissingWidthFails() {
        this.parseStringFails2("x400", "Missing width in \"x400\"");
    }

    @Test
    public void testParseMissingHeightFails() {
        this.parseStringFails2("400x", "Missing height in \"400x\"");
    }

    @Test
    public void testParseInvalidWidthFails() {
        this.parseStringFails2("abcx400", "Invalid width in \"abcx400\"");
    }

    @Test
    public void testParseInvalidWidthFails2() {
        this.parseStringFails2("-1x400", "Invalid width -1.0 <= 0");
    }

    @Test
    public void testParseInvalidHeightFails() {
        this.parseStringFails2("400xXYZ", "Invalid height in \"400xXYZ\"");
    }

    @Test
    public void testParseInvalidHeightFails2() {
        this.parseStringFails2("400x-1", "Invalid height 400.0 <= 0");
    }

    private void parseStringFails2(final String text, final String expectedMessage) {
        this.parseStringFails(text, new IllegalArgumentException(expectedMessage));
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("400x500", SpreadsheetPixelRectangle.with(400, 500));
    }

    @Test
    public void testParse2() {
        this.parseStringAndCheck("400.5x500.5", SpreadsheetPixelRectangle.with(400.5, 500.5));
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("50x75"), SpreadsheetPixelRectangle.with(50, 75));
    }

    @Test
    public void testJsonNodeMarshall2() {
        this.unmarshallAndCheck(JsonNode.string("50.5x75.5"), SpreadsheetPixelRectangle.with(50.5, 75.5));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetPixelRectangle.with(50, 75));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip2() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetPixelRectangle.with(50.5, 75.75));
    }

    //compare...........................................................................................................

    @Test
    public void testCompareCellFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.rectangle().compare(SpreadsheetCellReference.parseCellReference("A1")));
    }

    @Test
    public void testCompareRectangleFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.rectangle().compare(this.rectangle()));
    }

    //helper.................................................................................................

    private SpreadsheetPixelRectangle rectangle() {
        return SpreadsheetPixelRectangle.with(WIDTH, HEIGHT);
    }

    private void check(final SpreadsheetPixelRectangle rectangle) {
        this.check(rectangle, WIDTH, HEIGHT);
    }

    private void check(final SpreadsheetPixelRectangle rectangle,
                       final double width,
                       final double height) {
        this.checkWidth(rectangle, width);
        this.checkHeight(rectangle, height);
    }

    private void checkWidth(final SpreadsheetPixelRectangle rectangle, final double width) {
        assertEquals(width, rectangle.width(), () -> "rectangle width=" + rectangle);
    }

    private void checkHeight(final SpreadsheetPixelRectangle rectangle, final double height) {
        assertEquals(height, rectangle.height(), () -> "rectangle height=" + rectangle);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetPixelRectangle> type() {
        return SpreadsheetPixelRectangle.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetPixelRectangle unmarshall(final JsonNode node,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetPixelRectangle.unmarshallPixelRectangle(node, context);
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetPixelRectangle parseString(final String text) {
        return SpreadsheetPixelRectangle.parsePixelRectangle0(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> classs) {
        return classs;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
