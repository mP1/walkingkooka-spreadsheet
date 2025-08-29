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

package walkingkooka.spreadsheet.viewport;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportRectangleTest implements ClassTesting2<SpreadsheetViewportRectangle>,
    HashCodeEqualsDefinedTesting2<SpreadsheetViewportRectangle>,
    JsonNodeMarshallingTesting<SpreadsheetViewportRectangle>,
    ParseStringTesting<SpreadsheetViewportRectangle>,
    ToStringTesting<SpreadsheetViewportRectangle>,
    TreePrintableTesting,
    HasUrlFragmentTesting {

    private final static SpreadsheetCellReference HOME = SpreadsheetSelection.A1;
    private final static double WIDTH = 50;
    private final static double HEIGHT = 30;

    @Test
    public void testWithNullHomeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetViewportRectangle.with(
                null,
                WIDTH,
                HEIGHT
            )
        );
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetViewportRectangle.with(
                HOME,
                0,
                HEIGHT
            )
        );
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetViewportRectangle.with(
                HOME,
                -1,
                HEIGHT
            )
        );
    }

    @Test
    public void testWithInvalidHeightFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetViewportRectangle.with(
                HOME,
                WIDTH,
                0
            )
        );
    }

    @Test
    public void testWithInvalidHeightFails2() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetViewportRectangle.with(
                HOME,
                WIDTH,
                -1
            )
        );
    }

    @Test
    public void testWith() {
        this.check(
            SpreadsheetViewportRectangle.with(
                HOME,
                WIDTH,
                HEIGHT
            ),
            HOME,
            WIDTH,
            HEIGHT
        );
    }

    @Test
    public void testWithAbsoluteSpreadsheetCellReference() {
        this.check(
            SpreadsheetViewportRectangle.with(
                HOME
                    .toAbsolute(),
                WIDTH,
                HEIGHT
            ),
            HOME,
            WIDTH,
            HEIGHT
        );
    }

    @Test
    public void testWithCellReference() {
        this.check(
            SpreadsheetViewportRectangle.with(
                HOME,
                WIDTH,
                HEIGHT
            ),
            HOME,
            WIDTH,
            HEIGHT
        );
    }

    @Test
    public void testWithAbsoluteReference() {
        this.check(
            SpreadsheetViewportRectangle.with(
                HOME
                    .toAbsolute(),
                WIDTH,
                HEIGHT
            )
        );
    }

    // setHome..........................................................................................................

    @Test
    public void testSetHomeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setHome(null)
        );
    }

    @Test
    public void testSetHomeSame() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();

        assertSame(
            rectangle,
            rectangle.setHome(HOME)
        );
    }

    @Test
    public void testSetHomeDifferentReferenceKind() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();
        final SpreadsheetCellReference different = SpreadsheetSelection.parseCell("$B$9");

        this.checkEquals(
            SpreadsheetViewportRectangle.with(
                different,
                WIDTH,
                HEIGHT
            ),
            rectangle.setHome(different)
        );
    }

    @Test
    public void testSetHomeDifferentCell() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();
        final SpreadsheetCellReference different = SpreadsheetSelection.parseCell("Z1");

        this.checkEquals(
            SpreadsheetViewportRectangle.with(
                different,
                WIDTH,
                HEIGHT
            ),
            rectangle.setHome(different)
        );
    }

    // setWidth.........................................................................................................

    @Test
    public void testSetWidthInvalidFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .setWidth(0)
        );
    }

    @Test
    public void testSetWidthSame() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();
        assertSame(
            rectangle,
            rectangle.setWidth(rectangle.width())
        );
    }

    @Test
    public void testSetWidthDifferent() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();
        final double width = WIDTH * 2;

        final SpreadsheetViewportRectangle different = rectangle.setWidth(width);
        this.homeAndCheck(different);
        this.widthAndCheck(different, width);
        this.heightAndCheck(different);
    }

    // setHeight........................................................................................................

    @Test
    public void testSetHeightInvalidFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .setHeight(0)
        );
    }

    @Test
    public void testSetHeightSame() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();
        assertSame(
            rectangle,
            rectangle.setHeight(rectangle.height())
        );
    }

    @Test
    public void testSetHeightDifferent() {
        final SpreadsheetViewportRectangle rectangle = this.createObject();
        final double height = HEIGHT * 2;

        final SpreadsheetViewportRectangle different = rectangle.setHeight(height);
        this.homeAndCheck(different);
        this.widthAndCheck(different);
        this.heightAndCheck(different, height);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentHome() {
        this.checkNotEquals(
            SpreadsheetViewportRectangle.with(
                SpreadsheetSelection.parseCell("B2"),
                WIDTH,
                HEIGHT
            )
        );
    }

    @Test
    public void testEqualsDifferentWidth() {
        this.checkNotEquals(
            SpreadsheetViewportRectangle.with(
                HOME,
                1000 + WIDTH,
                HEIGHT
            )
        );
    }

    @Test
    public void testEqualsDifferentHeight() {
        this.checkNotEquals(
            SpreadsheetViewportRectangle.with(
                HOME,
                WIDTH,
                1000 + HEIGHT
            )
        );
    }

    @Override
    public SpreadsheetViewportRectangle createObject() {
        return SpreadsheetViewportRectangle.with(HOME, WIDTH, HEIGHT);
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseMissingHeightFails() {
        this.parseStringFails2(
            "A1",
            "Expected 3 tokens in \"A1\""
        );
    }

    private void parseStringFails2(final String text, final String expectedMessage) {
        this.parseStringFails(
            text,
            new IllegalArgumentException(expectedMessage)
        );
    }

    @Test
    public void testParseCellRangeFails() {
        this.parseStringFails2(
            "A1:B2:300:400",
            "Expected 3 tokens in \"A1:B2:300:400\""
        );
    }

    @Test
    public void testParseLabelFails() {
        this.parseStringFails(
            "Label123:300:400",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseCellReference() {
        this.parseStringAndCheck(
            "A1:300:400",
            SpreadsheetViewportRectangle.with(
                HOME,
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
            "A1:300.5:400.5",
            SpreadsheetViewportRectangle.with(
                HOME,
                300.5,
                400.5
            )
        );
    }

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

    // parseUrlFragment.................................................................................................

    @Test
    public void testFromUrlFragmentWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetViewportRectangle.fromUrlFragment(null)
        );
    }

    @Test
    public void testFromUrlFragmentWithEmptyFails() {
        this.fromUrlFragmentFails(
            "",
            "End of text, expected \"/\""
        );
    }

    @Test
    public void testFromUrlFragmentWithMissingLeadingSlashFails() {
        this.fromUrlFragmentFails(
            "home/A1",
            "Invalid character 'h' at 0 expected \"/\""
        );
    }

    @Test
    public void testFromUrlFragmentMissingHomeFails() {
        this.fromUrlFragmentFails(
            "/missing-home",
            "Missing home"
        );
    }

    @Test
    public void testFromUrlFragmentWithInvalidHomeFails() {
        this.fromUrlFragmentFails(
            "/home/!InvalidHome",
            "Missing home"
        );
    }

    @Test
    public void testFromUrlFragmentWithOnlyHomeFails() {
        this.fromUrlFragmentFails(
            "/home/A1",
            "End of text, expected \"/\""
        );
    }

    @Test
    public void testFromUrlFragmentMissingWidthFails() {
        this.fromUrlFragmentFails(
            "/home/A1/",
            "Missing width"
        );
    }

    @Test
    public void testFromUrlFragmentMissingWidth2Fails() {
        this.fromUrlFragmentFails(
            "/home/A1/not-width",
            "Missing width"
        );
    }

    @Test
    public void testFromUrlFragmentWithInvalidWidthFails() {
        this.fromUrlFragmentFails(
            "/home/A1/width/!InvalidHome",
            "Missing width"
        );
    }

    @Test
    public void testFromUrlFragmentWithOnlyHomeAndWidthFails() {
        this.fromUrlFragmentFails(
            "/home/A1/width/200",
            "End of text, expected \"/\""
        );
    }

    @Test
    public void testFromUrlFragmentMissingHeightFails() {
        this.fromUrlFragmentFails(
            "/home/A1/width/200/",
            "Missing height"
        );
    }

    @Test
    public void testFromUrlFragmentMissingHeight2Fails() {
        this.fromUrlFragmentFails(
            "/home/A1/width/200/not-height",
            "Missing height"
        );
    }

    @Test
    public void testFromUrlFragmentWithInvalidHeightFails() {
        this.fromUrlFragmentFails(
            "/home/A1/width/200/height/!InvalidHome",
            "Missing height"
        );
    }

    @Test
    public void testFromUrlFragmentWithHomeWidthHeight() {
        this.fromUrlFragmentAndCheck(
            "/home/A1/width/200/height/300",
            "A1:200:300"
        );
    }


    private void fromUrlFragmentFails(final String urlFragment,
                                      final String expected) {
        this.fromUrlFragmentFails(
            UrlFragment.parse(urlFragment),
            new IllegalArgumentException(expected)
        );
    }

    private void fromUrlFragmentFails(final UrlFragment urlFragment,
                                      final IllegalArgumentException expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetViewportRectangle.fromUrlFragment(urlFragment)
        );

        this.checkEquals(
            expected.getMessage(),
            thrown.getMessage()
        );
    }

    private void fromUrlFragmentAndCheck(final String urlFragment,
                                         final String expected) {
        this.fromUrlFragmentAndCheck(
            UrlFragment.parse(urlFragment),
            SpreadsheetViewportRectangle.parse(expected)
        );
    }

    private void fromUrlFragmentAndCheck(final UrlFragment urlFragment,
                                         final SpreadsheetViewportRectangle expected) {
        this.checkEquals(
            expected,
            SpreadsheetViewportRectangle.fromUrlFragment(urlFragment)
        );
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testMarshall2() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"A1:50.0:30.0\""
        );
    }

    @Test
    public void testMarshall3() {
        this.marshallAndCheck(
            SpreadsheetViewportRectangle.with(
                HOME,
                30,
                40
            ),
            JsonNode.string("A1:30.0:40.0")
        );
    }

    @Test
    public void testMarshall4() {
        this.marshallAndCheck(
            SpreadsheetViewportRectangle.with(
                HOME,
                30.5,
                40.5
            ),
            JsonNode.string("A1:30.5:40.5")
        );
    }

    @Test
    public void testMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetViewportRectangle.with(
                HOME,
                30.5,
                40.5
            )
        );
    }

    @Test
    @Override
    public void testMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createJsonNodeMarshallingValue());
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            JsonNode.string("A1:30:40"),
            SpreadsheetViewportRectangle.with(
                HOME,
                30,
                40
            )
        );
    }

    @Test
    public void testUnmarshall2() {
        this.unmarshallAndCheck(
            JsonNode.string("A1:30.5:40.5"),
            SpreadsheetViewportRectangle.with(
                HOME,
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

    @Override
    public SpreadsheetViewportRectangle unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewportRectangle.unmarshall(node, context);
    }

    @Override
    public SpreadsheetViewportRectangle createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createObject(),
            "SpreadsheetViewportRectangle\n" +
                "  home: A1\n" +
                "  width: 50.0\n" +
                "  height: 30.0\n"
        );
    }

    // UrlFragment......................................................................................................

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            this.createObject(),
            "/home/A1/width/50/height/30"
        );
    }

    @Test
    public void testUrlFragment2() {
        this.urlFragmentAndCheck(
            SpreadsheetViewportRectangle.with(
                HOME,
                30.5,
                40.5
            ),
            "/home/A1/width/30.5/height/40.5"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetViewportRectangle.with(
                HOME,
                30,
                40
            ),
            "home: A1 width: 30.0 height: 40.0"
        );
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(
            SpreadsheetViewportRectangle.with(
                HOME,
                30.5,
                40.5
            ),
            "home: A1 width: 30.5 height: 40.5"
        );
    }

    //helper............................................................................................................

    private void check(final SpreadsheetViewportRectangle viewport) {
        this.check(
            viewport,
            HOME,
            WIDTH,
            HEIGHT
        );
    }

    private void check(final SpreadsheetViewportRectangle viewport,
                       final SpreadsheetExpressionReference home,
                       final double width,
                       final double height) {
        this.homeAndCheck(viewport, home);
        this.widthAndCheck(viewport, width);
        this.heightAndCheck(viewport, height);
    }

    private void homeAndCheck(final SpreadsheetViewportRectangle viewport) {
        this.homeAndCheck(
            viewport,
            HOME
        );
    }

    private void homeAndCheck(final SpreadsheetViewportRectangle viewport,
                              final SpreadsheetExpressionReference home) {
        this.checkEquals(
            home,
            HOME,
            () -> "viewportRectangle: " + viewport
        );
    }

    private void widthAndCheck(final SpreadsheetViewportRectangle viewport) {
        this.widthAndCheck(
            viewport,
            WIDTH
        );
    }

    private void widthAndCheck(final SpreadsheetViewportRectangle viewport,
                               final double width) {
        this.checkEquals(
            width,
            viewport.width(),
            () -> "viewportRectangle width=" + viewport
        );
    }

    private void heightAndCheck(final SpreadsheetViewportRectangle viewport) {
        this.heightAndCheck(
            viewport,
            HEIGHT
        );
    }

    private void heightAndCheck(final SpreadsheetViewportRectangle viewport,
                                final double height) {
        this.checkEquals(
            height,
            viewport.height(),
            () -> "viewportRectangle height=" + viewport
        );
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
}
