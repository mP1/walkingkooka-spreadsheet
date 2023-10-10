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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportRectangleTest implements ClassTesting2<SpreadsheetViewportRectangle>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewportRectangle>,
        JsonNodeMarshallingTesting<SpreadsheetViewportRectangle>,
        ParseStringTesting<SpreadsheetViewportRectangle>,
        ToStringTesting<SpreadsheetViewportRectangle> {

    private final static double WIDTH = 50;
    private final static double HEIGHT = 30;

    @Test
    public void testWithNullHomeFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetViewportRectangle.with(null, WIDTH, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewportRectangle.with(reference(), -1, HEIGHT));
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewportRectangle.with(reference(), -2, HEIGHT));
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewportRectangle.with(reference(), WIDTH, -1));
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetViewportRectangle.with(reference(), WIDTH, -2));
    }

    @Test
    public void testWith() {
        this.check(
                SpreadsheetViewportRectangle.with(this.reference(), WIDTH, HEIGHT),
                this.reference(),
                WIDTH,
                HEIGHT
        );
    }
    @Test
    public void testWithAbsoluteSpreadsheetCellReference() {
        this.check(
                SpreadsheetViewportRectangle.with(this.reference().toAbsolute(), WIDTH, HEIGHT),
                this.reference(),
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithCellReference() {
        this.check(
                SpreadsheetViewportRectangle.with(this.reference(), WIDTH, HEIGHT),
                this.reference(),
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithLabel() {
        this.check(
                SpreadsheetViewportRectangle.with(this.label(), WIDTH, HEIGHT),
                this.label(),
                WIDTH,
                HEIGHT
        );
    }

    @Test
    public void testWithAbsoluteReference() {
        this.check(SpreadsheetViewportRectangle.with(this.reference().toAbsolute(), WIDTH, HEIGHT));
    }

    @Test
    public void testWithZeroWidth() {
        this.check(
                SpreadsheetViewportRectangle.with(this.reference(), 0, HEIGHT),
                this.reference(),
                0,
                HEIGHT
        );
    }

    @Test
    public void testWithZeroHeight() {
        this.check(
                SpreadsheetViewportRectangle.with(this.reference(), WIDTH, 0),
                this.reference(),
                WIDTH,
                0
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsWithLabel() {
        this.checkEquals(
                SpreadsheetViewportRectangle.with(this.label(), WIDTH, HEIGHT),
                SpreadsheetViewportRectangle.with(this.label(), WIDTH, HEIGHT)
        );
    }

    @Test
    public void testEqualsDifferentHome() {
        this.checkNotEquals(SpreadsheetViewportRectangle.with(SpreadsheetSelection.parseCell("a1"), WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(SpreadsheetViewportRectangle.with(label(), WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentWidth() {
        this.checkNotEquals(SpreadsheetViewportRectangle.with(this.reference(), 1000 + WIDTH, HEIGHT));
    }

    @Test
    public void testEqualsDifferentHeight() {
        this.checkNotEquals(SpreadsheetViewportRectangle.with(this.reference(), WIDTH, 1000 + HEIGHT));
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseMissingHeightFails() {
        this.parseStringFails2(
                "B9",
                "Expected 3 tokens in \"B9\""
        );
    }

    private void parseStringFails2(final String text, final String expectedMessage) {
        this.parseStringFails(
                text,
                new IllegalArgumentException(expectedMessage)
        );
    }

    @Test
    public void testParseRangeFails() {
        this.parseStringFails2(
                "A1:B2:300:400",
                "Expected 3 tokens in \"A1:B2:300:400\""
        );
    }

    @Test
    public void testParseCellReference() {
        this.parseStringAndCheck(
                "B9:300:400",
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        300,
                        400
                )
        );
    }

    @Test
    public void testParseCellReferenceAbsoluteColumnAbsoluteRow() {
        this.parseStringAndCheck(
                "$B$9:300:400",
                SpreadsheetViewportRectangle.with(
                        SpreadsheetSelection.parseCell("$B$9"),
                        300,
                        400
                )
        );
    }

    @Test
    public void testParseCellReference2() {
        this.parseStringAndCheck(
                "B9:300.5:400.5",
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        300.5,
                        400.5
                )
        );
    }

    @Test
    public void testParseLabel() {
        this.parseStringAndCheck(
                "Label123:300.5:400.5",
                SpreadsheetViewportRectangle.with(
                        this.label(),
                        300.5,
                        400.5
                )
        );
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testMarshall2() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                "\"B9:50:30\""
        );
    }

    @Test
    public void testMarshall3() {
        this.marshallAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30,
                        40
                ),
                JsonNode.string("B9:30:40")
        );
    }

    @Test
    public void testMarshall4() {
        this.marshallAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30.5,
                        40.5
                ),
                JsonNode.string("B9:30.5:40.5")
        );
    }

    @Test
    public void testMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30.5,
                        40.5
                )
        );
    }

    @Test
    public void testMarshallRoundtripLabel() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.label(),
                        30.5,
                        40.5
                )
        );
    }

    @Test
    public void testMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createJsonNodeMarshallingValue());
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                JsonNode.string("B9:30:40"),
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30,
                        40
                )
        );
    }

    @Test
    public void testUnmarshall2() {
        this.unmarshallAndCheck(
                JsonNode.string("B9:30.5:40.5"),
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30.5,
                        40.5
                )
        );
    }

    @Test
    public void testUnmarshall3() {
        this.unmarshallAndCheck(
                JsonNode.string("$B$9:30:40"),
                SpreadsheetViewportRectangle.with(
                        SpreadsheetSelection.parseCell("$B$9"),
                        30,
                        40
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringCell() {
        this.toStringAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30,
                        40
                ),
                "B9:30:40"
        );
    }

    @Test
    public void testToStringCell2() {
        this.toStringAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.reference(),
                        30.5,
                        40.5
                ),
                "B9:30.5:40.5"
        );
    }

    @Test
    public void testToStringLabel() {
        this.toStringAndCheck(
                SpreadsheetViewportRectangle.with(
                        this.label(),
                        30,
                        40
                ),
                "Label123:30:40"
        );
    }

    //helper............................................................................................................

    private SpreadsheetCellReference reference() {
        return SpreadsheetSelection.parseCell("B9");
    }

    private SpreadsheetLabelName label() {
        return SpreadsheetSelection.labelName("Label123");
    }

    private void check(final SpreadsheetViewportRectangle viewport) {
        this.check(
                viewport,
                this.reference(),
                WIDTH,
                HEIGHT
        );
    }

    private void check(final SpreadsheetViewportRectangle viewport,
                       final SpreadsheetExpressionReference home,
                       final double width,
                       final double height) {
        this.checkHome(viewport, home);
        this.checkWidth(viewport, width);
        this.checkHeight(viewport, height);
    }

    private void checkHome(final SpreadsheetViewportRectangle viewport,
                           final SpreadsheetExpressionReference home) {
        this.checkEquals(
                home,
                viewport.home(),
                () -> "viewportRectangle: " + viewport
        );
    }

    private void checkWidth(final SpreadsheetViewportRectangle viewport,
                            final double width) {
        this.checkEquals(width, viewport.width(), () -> "viewportRectangle width=" + viewport);
    }

    private void checkHeight(final SpreadsheetViewportRectangle viewport,
                             final double height) {
        this.checkEquals(height, viewport.height(), () -> "viewportRectangle height=" + viewport);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportRectangle> type() {
        return SpreadsheetViewportRectangle.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetViewportRectangle createObject() {
        return SpreadsheetViewportRectangle.with(this.reference(), WIDTH, HEIGHT);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetViewportRectangle unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewportRectangle.unmarshall(node, context);
    }

    @Override
    public SpreadsheetViewportRectangle createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ParseStringTesting..................................................................................................

    @Override
    public SpreadsheetViewportRectangle parseString(final String text) {
        return SpreadsheetViewportRectangle.parse(text);
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
