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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.compare.Comparators;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetViewport>
        implements ParseStringTesting<SpreadsheetViewport>,
        ComparableTesting2<SpreadsheetViewport> {

    private final static double WIDTH = 50;
    private final static double HEIGHT = 75;

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(this.reference(), 0, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(this.reference(), -1, HEIGHT));
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(this.reference(), WIDTH, 0));
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(this.reference(), WIDTH, -1));
    }

    @Test
    public void testWith() {
        this.check(SpreadsheetViewport.with(this.reference(), WIDTH, HEIGHT));
    }

    @Test
    public void testWithAbsoluteReference() {
        this.check(SpreadsheetViewport.with(this.reference().toAbsolute(), WIDTH, HEIGHT));
    }

    // SpreadsheetExpressionReferenceVisitor.............................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetViewport reference = this.createReference();

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
            protected void visit(final SpreadsheetViewport r) {
                assertSame(reference, r);
                b.append("5");
            }
        }.accept(reference);
        assertEquals("13542", b.toString());
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentReference() {
        this.checkNotEquals(SpreadsheetViewport.with(SpreadsheetCellReference.parseCellReference("a1"), 1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentWidth() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference(), 1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentHeight() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference(), WIDTH, 1000 + HEIGHT));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetViewport.with(this.reference(), 40, 50), "B9:40:50");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetViewport.with(this.reference(), 40.5, 50.75), "B9:40.5:50.75");
    }

    @Test
    public void testToString3() {
        this.toStringAndCheck(SpreadsheetViewport.with(this.reference(), 40, 50.75), "B9:40:50.75");
    }

    // helpers .........................................................................................................

    @Override
    SpreadsheetViewport createReference() {
        return this.viewport();
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseMissingWidthFails() {
        this.parseStringFails2("B9", "Missing width & height in \"B9\"");
    }

    @Test
    public void testParseMissingWidthFails2() {
        this.parseStringFails2("B9:", "Missing width & height in \"B9:\"");
    }

    @Test
    public void testParseMissingHeightFails() {
        this.parseStringFails2("B9:400", "Missing height in \"B9:400\"");
    }

    @Test
    public void testParseInvalidWidthFails() {
        this.parseStringFails2("B9:abc:400", "Invalid width in \"B9:abc:400\"");
    }

    @Test
    public void testParseInvalidWidthFails2() {
        this.parseStringFails2("B9:-1:400", "Invalid width -1.0 <= 0");
    }

    @Test
    public void testParseInvalidHeightFails() {
        this.parseStringFails2("B9:400:XYZ", "Invalid height in \"B9:400:XYZ\"");
    }

    @Test
    public void testParseInvalidHeightFails2() {
        this.parseStringFails2("B9:400:-1", "Invalid height 400.0 <= 0");
    }

    private void parseStringFails2(final String text, final String expectedMessage) {
        this.parseStringFails(text, new IllegalArgumentException(expectedMessage));
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("B9:400:500", SpreadsheetViewport.with(this.reference(), 400, 500));
    }

    @Test
    public void testParse2() {
        this.parseStringAndCheck("$B$9:400:500", SpreadsheetViewport.with(SpreadsheetCellReference.parseCellReference("$B$9"), 400, 500));
    }

    @Test
    public void testParse3() {
        this.parseStringAndCheck("B9:400.5:500.5", SpreadsheetViewport.with(this.reference(), 400.5, 500.5));
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
        final SpreadsheetViewport viewport = SpreadsheetViewport.with(reference(), width, height);
        assertEquals(expected,
                viewport.test(x, y),
                () -> "test " + x + ", " + y + " in " + viewport);
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("B9:50:75"), SpreadsheetViewport.with(this.reference(), 50, 75));
    }

    @Test
    public void testJsonNodeUnmarshall2() {
        this.unmarshallAndCheck(JsonNode.string("B9:50.5:75.5"), SpreadsheetViewport.with(this.reference(), 50.5, 75.5));
    }

    @Test
    public void testJsonNodeUnmarshall3() {
        this.unmarshallAndCheck(JsonNode.string("$B$9:50.5:75.5"), SpreadsheetViewport.with(SpreadsheetCellReference.parseCellReference("$B$9"), 50.5, 75.5));
    }

    @Test
    public void testJsonNodeMarshall2() {
        this.marshallAndCheck(SpreadsheetViewport.with(this.reference(), 50, 75), JsonNode.string("B9:50:75"));
    }

    @Test
    public void testJsonNodeMarshall3() {
        this.marshallAndCheck(SpreadsheetViewport.with(this.reference(), 50.5, 75.5), JsonNode.string("B9:50.5:75.5"));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetViewport.with(this.reference(), 50, 75));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip2() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetViewport.with(this.reference(), 50.5, 75.75));
    }

    //compareTo0...........................................................................................................

    @Test
    public void testCompareCellFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.viewport().compareTo0(SpreadsheetCellReference.parseCellReference("A1")));
    }

    @Test
    public void testCompareRectangleFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.viewport().compareTo0(this.viewport()));
    }

    @Test
    public void testCompareReferenceColumnLeft() {
        this.compareToAndCheckMore(SpreadsheetViewport.with(this.reference().addColumn(-1), WIDTH, HEIGHT));
    }

    @Test
    public void testCompareReferenceColumnRight() {
        this.compareToAndCheckLess(SpreadsheetViewport.with(this.reference().addColumn(+1), WIDTH, HEIGHT));
    }

    @Test
    public void testCompareReferenceRowDown() {
        this.compareToAndCheckMore(SpreadsheetViewport.with(this.reference().addRow(-1), WIDTH, HEIGHT));
    }

    @Test
    public void testCompareReferenceRowUp() {
        this.compareToAndCheckLess(SpreadsheetViewport.with(this.reference().addRow(+1), WIDTH, HEIGHT));
    }

    @Test
    public void testCompareReferenceWidth() {
        this.compareToAndCheckEquals(SpreadsheetViewport.with(this.reference(), WIDTH + 1, HEIGHT));
    }

    @Test
    public void testCompareReferenceHeight() {
        this.compareToAndCheckEquals(SpreadsheetViewport.with(this.reference(), WIDTH, HEIGHT + 1));
    }

    @Override
    public void compareToAndCheckEquals(final SpreadsheetViewport viewport) {
        this.compareToAndCheck(viewport, Comparators.EQUAL);
    }

    @Override
    public void compareToAndCheck(final SpreadsheetViewport comparable, final int expected) {
        this.compareToAndCheck(this.createComparable(), comparable, expected);
    }

    //equals............................................................................................................

    @Test
    public void testDifferentReference() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference().add(1, 1), WIDTH, HEIGHT));
    }

    @Test
    public void testDifferentWidth() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference(), WIDTH + 1, HEIGHT));
    }

    @Test
    public void testDifferentHeight() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference(), WIDTH, HEIGHT + 1));
    }

    //helper............................................................................................................

    private SpreadsheetViewport viewport() {
        return SpreadsheetViewport.with(this.reference(), WIDTH, HEIGHT);
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetCellReference.parseCellReference("B9");
    }

    private void check(final SpreadsheetViewport viewport) {
        this.check(viewport,
                this.reference(),
                WIDTH,
                HEIGHT);
    }

    private void check(final SpreadsheetViewport viewport,
                       final SpreadsheetCellReference reference,
                       final double width,
                       final double height) {
        this.checkReference(viewport, reference);
        this.checkWidth(viewport, width);
        this.checkHeight(viewport, height);
    }

    private void checkReference(final SpreadsheetViewport viewport,
                                final SpreadsheetCellReference reference) {
        assertEquals(reference, viewport.reference(), () -> "viewport width=" + viewport);
    }

    private void checkWidth(final SpreadsheetViewport viewport,
                            final double width) {
        assertEquals(width, viewport.width(), () -> "viewport width=" + viewport);
    }

    private void checkHeight(final SpreadsheetViewport viewport,
                             final double height) {
        assertEquals(height, viewport.height(), () -> "viewport height=" + viewport);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewport> type() {
        return SpreadsheetViewport.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ComparableTesting................................................................................................

    @Override
    public SpreadsheetViewport createComparable() {
        return SpreadsheetViewport.with(this.reference(), WIDTH, HEIGHT);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetViewport unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewport.unmarshallViewport(node, context);
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetViewport parseString(final String text) {
        return SpreadsheetViewport.parseViewport0(text);
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
