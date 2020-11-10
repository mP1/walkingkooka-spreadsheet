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
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(this.reference(), 0, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(this.reference(), -1, HEIGHT));
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(this.reference(), WIDTH, 0));
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetPixelRectangle.with(this.reference(), WIDTH, -1));
    }

    @Test
    public void testWith() {
        this.check(SpreadsheetPixelRectangle.with(this.reference(), WIDTH, HEIGHT));
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
    public void testEqualsDifferentReference() {
        this.checkNotEquals(SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("a1"), 1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentWidth() {
        this.checkNotEquals(SpreadsheetPixelRectangle.with(this.reference(), 1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentHeight() {
        this.checkNotEquals(SpreadsheetPixelRectangle.with(this.reference(), WIDTH, 1000 + HEIGHT));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 40, 50), "B9/40/50");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 40.5, 50.75), "B9/40.5/50.75");
    }

    @Test
    public void testToString3() {
        this.toStringAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 40, 50.75), "B9/40/50.75");
    }

    @Test
    public void testToString4() {
        this.toStringAndCheck(SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("$C$3"), 40, 50.75), "$C$3/40/50.75");
    }

    // helpers .........................................................................................................

    @Override
    SpreadsheetPixelRectangle createReference() {
        return this.rectangle();
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseMissingWidthFails() {
        this.parseStringFails2("B9", "Missing width & height in \"B9\"");
    }

    @Test
    public void testParseMissingWidthFails2() {
        this.parseStringFails2("B9/", "Missing width & height in \"B9/\"");
    }

    @Test
    public void testParseMissingHeightFails() {
        this.parseStringFails2("B9/400", "Missing height in \"B9/400\"");
    }

    @Test
    public void testParseInvalidWidthFails() {
        this.parseStringFails2("B9/abc/400", "Invalid width in \"B9/abc/400\"");
    }

    @Test
    public void testParseInvalidWidthFails2() {
        this.parseStringFails2("B9/-1/400", "Invalid width -1.0 <= 0");
    }

    @Test
    public void testParseInvalidHeightFails() {
        this.parseStringFails2("B9/400/XYZ", "Invalid height in \"B9/400/XYZ\"");
    }

    @Test
    public void testParseInvalidHeightFails2() {
        this.parseStringFails2("B9/400/-1", "Invalid height 400.0 <= 0");
    }

    private void parseStringFails2(final String text, final String expectedMessage) {
        this.parseStringFails(text, new IllegalArgumentException(expectedMessage));
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("B9/400/500", SpreadsheetPixelRectangle.with(this.reference(), 400, 500));
    }

    @Test
    public void testParse2() {
        this.parseStringAndCheck("$B$9/400/500", SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("$B$9"), 400, 500));
    }

    @Test
    public void testParse3() {
        this.parseStringAndCheck("B9/400.5/500.5", SpreadsheetPixelRectangle.with(this.reference(), 400.5, 500.5));
    }

    // test.............................................................................................................

    @Test
    public void testTestLeft() {
        this.testAndCheck(100, 50, -1, 20, false);
    }

    @Test
    public void testTestRight() {
        this.testAndCheck(100, 50, 101, 20, false);
    }

    @Test
    public void testTestUp() {
        this.testAndCheck(100, 50, 1, -1, false);
    }

    @Test
    public void testTestDown() {
        this.testAndCheck(100, 50, 1, 51, false);
    }

    @Test
    public void testTestTopLeft() {
        this.testAndCheck(100, 50, 0, 0, true);
    }

    @Test
    public void testTestTopRight() {
        this.testAndCheck(100, 50, 100, 0, true);
    }

    @Test
    public void testTestBottomLeft() {
        this.testAndCheck(100, 50, 0, 50, true);
    }

    @Test
    public void testTestBottomRight() {
        this.testAndCheck(100, 50, 100, 50, true);
    }

    @Test
    public void testTestCenter() {
        this.testAndCheck(100, 50, 20, 20, true);
    }

    private void testAndCheck(final double width,
                              final double height,
                              final double x,
                              final double y,
                              final boolean expected) {
        final SpreadsheetPixelRectangle rectangle = SpreadsheetPixelRectangle.with(reference(), width, height);
        assertEquals(expected,
                rectangle.test(x, y),
                () -> "test " + x + ", " + y + " in " + rectangle);
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("B9/50/75"), SpreadsheetPixelRectangle.with(this.reference(), 50, 75));
    }

    @Test
    public void testJsonNodeUnmarshall2() {
        this.unmarshallAndCheck(JsonNode.string("B9/50.5/75.5"), SpreadsheetPixelRectangle.with(this.reference(), 50.5, 75.5));
    }

    @Test
    public void testJsonNodeUnmarshall3() {
        this.unmarshallAndCheck(JsonNode.string("$B$9/50.5/75.5"), SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("$B$9"), 50.5, 75.5));
    }

    @Test
    public void testJsonNodeMarshall2() {
        this.marshallAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 50, 75), JsonNode.string("B9/50/75"));
    }

    @Test
    public void testJsonNodeMarshall3() {
        this.marshallAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 50.5, 75.5), JsonNode.string("B9/50.5/75.5"));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 50, 75));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip2() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetPixelRectangle.with(this.reference(), 50.5, 75.75));
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
        return SpreadsheetPixelRectangle.with(this.reference(), WIDTH, HEIGHT);
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetCellReference.parseCellReference("B9");
    }

    private void check(final SpreadsheetPixelRectangle rectangle) {
        this.check(rectangle,
                this.reference(),
                WIDTH,
                HEIGHT);
    }

    private void check(final SpreadsheetPixelRectangle rectangle,
                       final SpreadsheetCellReference reference,
                       final double width,
                       final double height) {
        this.checkReference(rectangle, reference);
        this.checkWidth(rectangle, width);
        this.checkHeight(rectangle, height);
    }

    private void checkReference(final SpreadsheetPixelRectangle rectangle,
                                final SpreadsheetCellReference reference) {
        assertEquals(reference, rectangle.reference(), () -> "rectangle width=" + rectangle);
    }

    private void checkWidth(final SpreadsheetPixelRectangle rectangle,
                            final double width) {
        assertEquals(width, rectangle.width(), () -> "rectangle width=" + rectangle);
    }

    private void checkHeight(final SpreadsheetPixelRectangle rectangle,
                             final double height) {
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
