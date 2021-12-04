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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportTest implements ClassTesting2<SpreadsheetViewport>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewport>,
        JsonNodeMarshallingTesting<SpreadsheetViewport>,
        ParseStringTesting<SpreadsheetViewport>,
        ToStringTesting<SpreadsheetViewport> {

    private final static double X_OFFSET = 1;
    private final static double Y_OFFSET = 2;
    private final static double WIDTH = 50;
    private final static double HEIGHT = 30;

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetViewport.with(null, X_OFFSET, Y_OFFSET, WIDTH, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(reference(), X_OFFSET, Y_OFFSET, -1, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(reference(), X_OFFSET, Y_OFFSET, -2, HEIGHT));
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(reference(), X_OFFSET, Y_OFFSET, WIDTH, -1));
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewport.with(reference(), X_OFFSET, Y_OFFSET, WIDTH, -2));
    }

    @Test
    public void testWith() {
        this.check(
                SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT),
                this.reference(),
                X_OFFSET,
                Y_OFFSET,
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithNegativeXOffset() {
        final double x = -1;
        this.check(
                SpreadsheetViewport.with(this.reference(), x, Y_OFFSET, WIDTH, HEIGHT),
                this.reference(),
                x,
                Y_OFFSET,
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithNegativeYOffset() {
        final double y = -1;
        this.check(
                SpreadsheetViewport.with(this.reference(), X_OFFSET, y, WIDTH, HEIGHT),
                this.reference(),
                X_OFFSET,
                y,
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithAbsoluteSpreadsheetCellReference() {
        this.check(
                SpreadsheetViewport.with(this.reference().toAbsolute(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT),
                this.reference(),
                X_OFFSET,
                Y_OFFSET,
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithCellReference() {
        this.check(
                SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT),
                this.reference(),
                X_OFFSET,
                Y_OFFSET,
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithLabel() {
        this.check(
                SpreadsheetViewport.with(this.label(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT),
                this.label(),
                X_OFFSET,
                Y_OFFSET,
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithAbsoluteReference() {
        this.check(SpreadsheetViewport.with(this.reference().toAbsolute(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT));
    }

    @Test
    public void testWithZeroWidth() {
        this.check(
                SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, 0, HEIGHT),
                this.reference(),
                X_OFFSET,
                Y_OFFSET,
                0,
                HEIGHT
        );
    }

    @Test
    public void testWithZeroHeight() {
        this.check(
                SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, WIDTH, 0),
                this.reference(),
                X_OFFSET,
                Y_OFFSET,
                WIDTH,
                0
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsWithLabel() {
        this.checkEquals(
                SpreadsheetViewport.with(this.label(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT),
                SpreadsheetViewport.with(this.label(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT)
        );
    }

    @Test
    public void testEqualsDifferentCellReference() {
        this.checkNotEquals(SpreadsheetViewport.with(SpreadsheetCellReference.parseCell("a1"), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(SpreadsheetViewport.with(label(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT));
    }

    @Test
    public void testDifferentXOffsetEquals() {
        this.checkNotEquals(SpreadsheetViewport.with(reference(), 1000 + X_OFFSET, Y_OFFSET, WIDTH, HEIGHT));
    }

    @Test
    public void testDifferentYOffsetEquals() {
        this.checkNotEquals(SpreadsheetViewport.with(reference(), X_OFFSET, 1000 + Y_OFFSET, WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentWidth() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, 1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentHeight() {
        this.checkNotEquals(SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, WIDTH, 1000 + HEIGHT));
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseMissingHeightFails() {
        this.parseStringFails2("B9", "Expected 5 tokens in \"B9\"");
    }

    private void parseStringFails2(final String text, final String expectedMessage) {
        this.parseStringFails(text, new IllegalArgumentException(expectedMessage));
    }

    @Test
    public void testParseRangeFails() {
        this.parseStringFails2("A1:B2:100:200:300:400", "Expected 5 tokens in \"A1:B2:100:200:300:400\"");
    }

    @Test
    public void testParseCellReference() {
        this.parseStringAndCheck("B9:100:200:300:400", SpreadsheetViewport.with(this.reference(), 100, 200, 300, 400));
    }

    @Test
    public void testParseCellReferenceAbsoluteColumnAbsoluteRow() {
        this.parseStringAndCheck("$B$9:100:200:300:400", SpreadsheetViewport.with(SpreadsheetCellReference.parseCell("$B$9"), 100, 200, 300, 400));
    }

    @Test
    public void testParseCellReference2() {
        this.parseStringAndCheck("B9:100.5:200.5:300.5:400.5", SpreadsheetViewport.with(this.reference(), 100.5, 200.5, 300.5, 400.5));
    }

    @Test
    public void testParseLabel() {
        this.parseStringAndCheck("Label123:100.5:200.5:300.5:400.5", SpreadsheetViewport.with(this.label(), 100.5, 200.5, 300.5, 400.5));
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNode() {
        this.marshallAndCheck(this.createJsonNodeMarshallingValue(),
                "\"B9:1:2:50:30\"");
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createJsonNodeMarshallingValue());
    }

    @Test
    public void testJsonNodeUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("B9:10:20:30:40"), SpreadsheetViewport.with(this.reference(), 10, 20, 30, 40));
    }

    @Test
    public void testJsonNodeUnmarshall2() {
        this.unmarshallAndCheck(JsonNode.string("B9:10.5:20.5:30.5:40.5"), SpreadsheetViewport.with(this.reference(), 10.5, 20.5, 30.5, 40.5));
    }

    @Test
    public void testJsonNodeUnmarshall3() {
        this.unmarshallAndCheck(JsonNode.string("$B$9:10:20:30:40"), SpreadsheetViewport.with(SpreadsheetCellReference.parseCell("$B$9"), 10, 20, 30, 40));
    }

    @Test
    public void testJsonNodeMarshall2() {
        this.marshallAndCheck(SpreadsheetViewport.with(this.reference(), 10, 20, 30, 40), JsonNode.string("B9:10:20:30:40"));
    }

    @Test
    public void testJsonNodeMarshall3() {
        this.marshallAndCheck(SpreadsheetViewport.with(this.reference(), 10.5, 20.5, 30.5, 40.5), JsonNode.string("B9:10.5:20.5:30.5:40.5"));
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetViewport.with(this.reference(), 10.5, 20.5, 30.5, 40.5));
    }

    @Test
    public void testJsonNodeMarshallRoundtripLabel() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetViewport.with(this.label(), 10.5, 20.5, 30.5, 40.5));
    }

    // toString.........................................................................................................

    @Test
    public void testToStringCell() {
        this.toStringAndCheck(SpreadsheetViewport.with(this.reference(), 10, 20, 30, 40), "B9:10:20:30:40");
    }

    @Test
    public void testToStringCell2() {
        this.toStringAndCheck(SpreadsheetViewport.with(this.reference(), 10.5, 20.5, 30.5, 40.5), "B9:10.5:20.5:30.5:40.5");
    }

    @Test
    public void testToStringLabel() {
        this.toStringAndCheck(SpreadsheetViewport.with(this.label(), 10, 20, 30, 40), "Label123:10:20:30:40");
    }

    //helper............................................................................................................

    private SpreadsheetCellReference reference() {
        return SpreadsheetCellReference.parseCell("B9");
    }

    private SpreadsheetLabelName label() {
        return SpreadsheetExpressionReference.labelName("Label123");
    }

    private void check(final SpreadsheetViewport viewport) {
        this.check(
                viewport,
                this.reference(),
                X_OFFSET,
                Y_OFFSET,
                WIDTH,
                HEIGHT
        );
    }

    private void check(final SpreadsheetViewport viewport,
                       final SpreadsheetCellReferenceOrLabelName reference,
                       final double xOffset,
                       final double yOffset,
                       final double width,
                       final double height) {
        this.checkReference(viewport, reference);
        this.checkXOffset(viewport, xOffset);
        this.checkYOffset(viewport, yOffset);
        this.checkWidth(viewport, width);
        this.checkHeight(viewport, height);
    }

    private void checkReference(final SpreadsheetViewport viewport,
                                final SpreadsheetCellReferenceOrLabelName reference) {
        this.checkEquals(reference, viewport.cellOrLabel(), () -> "viewport width=" + viewport);
    }

    private void checkXOffset(final SpreadsheetViewport viewport,
                              final double xOffset) {
        this.checkEquals(xOffset, viewport.xOffset(), () -> "viewport xOffset=" + viewport);
    }

    private void checkYOffset(final SpreadsheetViewport viewport,
                              final double yOffset) {
        this.checkEquals(yOffset, viewport.yOffset(), () -> "viewport yOffset=" + viewport);
    }

    private void checkWidth(final SpreadsheetViewport viewport,
                            final double width) {
        this.checkEquals(width, viewport.width(), () -> "viewport width=" + viewport);
    }

    private void checkHeight(final SpreadsheetViewport viewport,
                             final double height) {
        this.checkEquals(height, viewport.height(), () -> "viewport height=" + viewport);
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

    @Override
    public SpreadsheetViewport createObject() {
        return SpreadsheetViewport.with(this.reference(), X_OFFSET, Y_OFFSET, WIDTH, HEIGHT);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetViewport unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewport.unmarshall(node, context);
    }

    @Override
    public SpreadsheetViewport createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetViewport parseString(final String text) {
        return SpreadsheetViewport.parse(text);
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
