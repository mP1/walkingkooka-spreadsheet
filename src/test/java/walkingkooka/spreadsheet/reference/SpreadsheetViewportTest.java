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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportTest implements ClassTesting<SpreadsheetViewport>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewport>,
        JsonNodeMarshallingTesting<SpreadsheetViewport>,
        ToStringTesting<SpreadsheetViewport>,
        TreePrintableTesting {

    private final static SpreadsheetCellReference HOME = SpreadsheetSelection.A1;

    private final static int WIDTH = 100;

    private final static int HEIGHT = 50;

    private static final SpreadsheetViewportRectangle RECTANGLE = HOME.viewportRectangle(
            WIDTH,
            HEIGHT
    );

    private static final SpreadsheetColumnReference COLUMN = SpreadsheetSelection.parseColumn("B");
    private static final SpreadsheetCellReference CELL = SpreadsheetSelection.parseCell("B2");
    private static final SpreadsheetRowReference ROW = SpreadsheetSelection.parseRow("2");

    private static final SpreadsheetColumnReferenceRange COLUMN_RANGE = SpreadsheetSelection.parseColumnRange("B:C");
    private static final SpreadsheetCellRange CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
    private static final SpreadsheetRowReferenceRange ROW_RANGE = SpreadsheetSelection.parseRowRange("2:3");

    private static final SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("Label123");

    private static final SpreadsheetSelection SELECTION = CELL_RANGE;
    private static final SpreadsheetViewportAnchor ANCHOR = SpreadsheetViewportAnchor.TOP_LEFT;
    private static final List<SpreadsheetViewportNavigation> NAVIGATIONS = Lists.of(
            SpreadsheetViewportNavigation.leftColumn()
    );

    @Test
    public void testWithNullRectangleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewport.with(null)
        );
    }

    // setSelection.....................................................................................................

    @Test
    public void testSetSelectionNullFails() {
        final SpreadsheetViewport viewport = this.createObject();
        assertThrows(
                NullPointerException.class,
                () -> viewport.setSelection(null)
        );
    }

    @Test
    public void testSetSelectionSame() {
        final SpreadsheetViewport viewport = this.createObject();
        assertSame(
                viewport,
                viewport.setSelection(viewport.selection())
        );
    }

    @Test
    public void testSetSelectionDifferent() {
        final SpreadsheetViewport viewport = this.createObject();

        final Optional<AnchoredSpreadsheetSelection> differentSelection = Optional.of(
                SpreadsheetSelection.parseCell("B2")
                        .setAnchor(SpreadsheetViewportAnchor.NONE)
        );

        this.checkNotEquals(
                viewport.selection(),
                differentSelection,
                "different selection"
        );

        final SpreadsheetViewport different = viewport.setSelection(differentSelection);
        assertNotSame(
                viewport,
                different
        );
        this.checkSelection(
                different,
                differentSelection
        );
        this.checkNavigations(
                different,
                viewport.navigations()
        );
    }

    // setNavigations....................................................................................................

    @Test
    public void testSetNavigationsNullFails() {
        final SpreadsheetViewport selection = this.createObject();
        assertThrows(
                NullPointerException.class,
                () -> selection.setNavigations(null)
        );
    }

    @Test
    public void testSetNavigationsSame() {
        final SpreadsheetViewport selection = this.createObject();
        assertSame(
                selection,
                selection.setNavigations(selection.navigations())
        );
    }

    @Test
    public void testSetNavigationsDifferent() {
        final SpreadsheetViewport selection = this.createObject();
        final List<SpreadsheetViewportNavigation> navigations = Lists.of(
                SpreadsheetViewportNavigation.extendRightColumn()
        );
        this.checkNotEquals(
                NAVIGATIONS,
                navigations,
                "different navigations"
        );

        final SpreadsheetViewport differentSelection = selection.setNavigations(navigations);
        assertNotSame(
                selection,
                differentSelection
        );
        this.checkSelection(
                differentSelection,
                selection.selection()
        );
        this.checkNavigations(
                differentSelection,
                navigations
        );
    }

    private void checkSelection(final SpreadsheetViewport viewport,
                                final Optional<AnchoredSpreadsheetSelection> selection) {
        this.checkEquals(
                selection,
                viewport.selection(),
                "selection"
        );
    }

    private void checkNavigations(final SpreadsheetViewport viewport,
                                  final List<SpreadsheetViewportNavigation> navigations) {
        this.checkEquals(
                navigations,
                viewport.navigations(),
                "navigations"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testDifferentRectangle() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        SpreadsheetSelection.parseCell("Z99")
                                .viewportRectangle(99, 999),
                        SpreadsheetViewport.NO_SELECTION,
                        NAVIGATIONS
                )
        );
    }

    @Test
    public void testDifferentSelection() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        Optional.of(
                                SpreadsheetSelection.parseCell("Z9")
                                        .setAnchor(SpreadsheetViewportAnchor.NONE)
                        ),
                        NAVIGATIONS
                )
        );
    }

    @Test
    public void testDifferentNavigations() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        SpreadsheetViewport.NO_SELECTION,
                        Lists.of(
                                SpreadsheetViewportNavigation.rightColumn()
                        )
                )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.A1
                        .setAnchor(SpreadsheetViewportAnchor.NONE),
                "cell A1" + EOL
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRow("12")
                        .setAnchor(SpreadsheetViewportAnchor.NONE),
                "row 12" + EOL
        );
    }

    @Test
    public void testTreePrintWithAnchor() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(SpreadsheetViewportAnchor.TOP),
                "row-range 12:34 TOP" + EOL
        );
    }

    @Test
    public void testTreePrintRectangle() {
        this.treePrintAndCheck(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        SpreadsheetViewport.NO_SELECTION,
                        SpreadsheetViewport.NO_NAVIGATION
                ),
                "rectangle:" + EOL +
                        "  home: A1" + EOL +
                        "  width: 100.0" + EOL +
                        "  height: 50.0" + EOL
        );
    }

    @Test
    public void testTreePrintRectangleAndNavigations() {
        this.treePrintAndCheck(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        SpreadsheetViewport.NO_SELECTION,
                        Lists.of(
                                SpreadsheetViewportNavigation.leftColumn(),
                                SpreadsheetViewportNavigation.upRow()
                        )
                ),
                "rectangle:" + EOL +
                        "  home: A1" + EOL +
                        "  width: 100.0" + EOL +
                        "  height: 50.0" + EOL +
                        "navigations:" + EOL +
                        "  left column" + EOL +
                        "  up row" + EOL
        );
    }

    @Test
    public void testTreePrintRectangleSelectionRowRangeNavigations() {
        this.treePrintAndCheck(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        Optional.of(
                                SpreadsheetSelection.parseRowRange("12:34")
                                        .setAnchor(
                                                SpreadsheetViewportAnchor.TOP
                                        )
                        ),
                        Lists.of(
                                SpreadsheetViewportNavigation.leftColumn()
                        )
                ),
                "rectangle:" + EOL +
                        "  home: A1" + EOL +
                        "  width: 100.0" + EOL +
                        "  height: 50.0" + EOL +
                        "selection: row-range 12:34 TOP" + EOL +
                        "navigations:" + EOL +
                        "  left column" + EOL
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentCell() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.A1.setAnchor(SpreadsheetViewportAnchor.NONE),
                "/cell/A1"
        );
    }

    @Test
    public void testUrlFragmentCellRangeTopLeft() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT),
                "/cell/B2:C3/top-left"
        );
    }

    @Test
    public void testUrlFragmentCellRangeTopRight() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_RIGHT),
                "/cell/B2:C3/top-right"
        );
    }

    @Test
    public void testUrlFragmentColumnNone() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumn("Z")
                        .setAnchor(SpreadsheetViewportAnchor.NONE),
                "/column/Z"
        );
    }

    @Test
    public void testUrlFragmentColumnRangeLeft() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumnRange("X:Y")
                        .setAnchor(SpreadsheetViewportAnchor.LEFT),
                "/column/X:Y/left"
        );
    }

    @Test
    public void testUrlFragmentColumnRangeRight() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumnRange("X:Y")
                        .setAnchor(SpreadsheetViewportAnchor.RIGHT),
                "/column/X:Y/right"
        );
    }

    @Test
    public void testUrlFragmentLabelNone() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellOrLabel("Label123")
                        .setAnchor(SpreadsheetViewportAnchor.NONE),
                "/cell/Label123"
        );
    }

    @Test
    public void testUrlFragmentLabelBottomRight() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellOrLabel("Label123")
                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT),
                "/cell/Label123/bottom-right"
        );
    }

    private void urlFragmentAndCheck(final AnchoredSpreadsheetSelection anchoredSpreadsheetSelection,
                                     final String expected) {
        this.urlFragmentAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        anchoredSpreadsheetSelection
                                )
                        ),
                expected
        );
    }

    private void urlFragmentAndCheck(final SpreadsheetViewport selection,
                                     final String expected) {
        this.checkEquals(
                UrlFragment.with(expected),
                selection.urlFragment(),
                selection + " urlfragment"
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonMarshallCell() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCell("B2")
                                                .setAnchor(SpreadsheetViewportAnchor.NONE)
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallCellRange() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("B2:C3")
                                                .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallColumn() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        COLUMN.setAnchor(SpreadsheetViewportAnchor.NONE)
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallColumnRange() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseColumnRange("B:C")
                                                .setAnchor(SpreadsheetViewportAnchor.LEFT)
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallRow() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        ROW.setAnchor(SpreadsheetViewportAnchor.NONE)
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallRowRange() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseRowRange("12:34")
                                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                                )
                        )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                SpreadsheetViewport.NO_SELECTION
                        ),
                RECTANGLE.toString()
        );
    }

    @Test
    public void testToStringWithSelectionCell() {
        this.toStringAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setSelection(
                                Optional.of(
                                        CELL.setAnchor(SpreadsheetViewportAnchor.NONE)
                                )
                        ),
                RECTANGLE + " selection: " + CELL
        );
    }

    @Test
    public void testToStringCellRangeSelectionAndNavigations() {
        this.toStringAndCheck(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        Optional.of(
                                CELL_RANGE.setAnchor(ANCHOR)
                        ),
                        NAVIGATIONS
                ),
                RECTANGLE + " selection: " + CELL_RANGE + " " + ANCHOR + " navigations: " + NAVIGATIONS.iterator().next()
        );
    }

    // helpers..........................................................................................................

    @Override
    public Class<SpreadsheetViewport> type() {
        return SpreadsheetViewport.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // equals...........................................................................................................

    @Override
    public SpreadsheetViewport createObject() {
        return SpreadsheetViewport.with(
                RECTANGLE,
                Optional.of(
                        SELECTION.setAnchor(ANCHOR)
                ),
                NAVIGATIONS
        );
    }

    // Json.............................................................................................................

    @Override
    public SpreadsheetViewport unmarshall(final JsonNode from,
                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewport.unmarshall(from, context);
    }

    @Override
    public SpreadsheetViewport createJsonNodeMarshallingValue() {
        return this.createObject();
    }
}
