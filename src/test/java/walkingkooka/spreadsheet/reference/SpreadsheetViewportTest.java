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

    private static final SpreadsheetCellRange CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
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
    public void testSetAnchoredSelectionNullFails() {
        final SpreadsheetViewport viewport = this.createObject();
        assertThrows(
                NullPointerException.class,
                () -> viewport.setAnchoredSelection(null)
        );
    }

    @Test
    public void testSetAnchoredSelectionSame() {
        final SpreadsheetViewport viewport = this.createObject();
        assertSame(
                viewport,
                viewport.setAnchoredSelection(viewport.anchoredSelection())
        );
    }

    @Test
    public void testSetAnchoredSelectionDifferent() {
        final SpreadsheetViewport viewport = this.createObject();

        final Optional<AnchoredSpreadsheetSelection> differentAnchoredSelection = Optional.of(
                SpreadsheetSelection.parseCell("B2")
                        .setDefaultAnchor()
        );

        this.checkNotEquals(
                viewport.anchoredSelection(),
                differentAnchoredSelection,
                "different anchoredSelection"
        );

        final SpreadsheetViewport different = viewport.setAnchoredSelection(differentAnchoredSelection);
        assertNotSame(
                viewport,
                different
        );
        this.checkAnchoredSelection(
                different,
                differentAnchoredSelection
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
        final SpreadsheetViewport viewport = this.createObject();
        final List<SpreadsheetViewportNavigation> navigations = Lists.of(
                SpreadsheetViewportNavigation.extendRightColumn()
        );
        this.checkNotEquals(
                NAVIGATIONS,
                navigations,
                "different navigations"
        );

        final SpreadsheetViewport differentViewport = viewport.setNavigations(navigations);
        assertNotSame(
                viewport,
                differentViewport
        );
        this.checkAnchoredSelection(
                differentViewport,
                viewport.anchoredSelection()
        );
        this.checkNavigations(
                differentViewport,
                navigations
        );
    }

    @Test
    public void testSetNavigationsDifferentCopied() {
        final SpreadsheetViewport selection = this.createObject();
        final List<SpreadsheetViewportNavigation> navigations = Lists.array();
        navigations.add(
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
        this.checkAnchoredSelection(
                differentSelection,
                selection.anchoredSelection()
        );

        navigations.clear();

        this.checkNavigations(
                differentSelection,
                Lists.of(
                        SpreadsheetViewportNavigation.extendRightColumn()
                )
        );
    }

    private void checkAnchoredSelection(final SpreadsheetViewport viewport,
                                        final Optional<AnchoredSpreadsheetSelection> anchoredSelection) {
        this.checkEquals(
                anchoredSelection,
                viewport.anchoredSelection(),
                "anchoredSelection"
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
    public void testEqualsDifferentRectangle() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        SpreadsheetSelection.parseCell("Z99")
                                .viewportRectangle(99, 999),
                        SpreadsheetViewport.NO_ANCHORED_SELECTION,
                        NAVIGATIONS
                )
        );
    }

    @Test
    public void testEqualsDifferentSelection() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        Optional.of(
                                SpreadsheetSelection.parseCell("Z9")
                                        .setDefaultAnchor()
                        ),
                        NAVIGATIONS
                )
        );
    }

    @Test
    public void testEqualsDifferentNavigations() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        RECTANGLE,
                        SpreadsheetViewport.NO_ANCHORED_SELECTION,
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
                        .setDefaultAnchor(),
                "cell A1" + EOL
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRow("12")
                        .setDefaultAnchor(),
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
                        SpreadsheetViewport.NO_ANCHORED_SELECTION,
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
                        SpreadsheetViewport.NO_ANCHORED_SELECTION,
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
    public void testTreePrintRectangleAnchoredSelectionRowRangeNavigations() {
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
                        "anchoredSelection: row-range 12:34 TOP" + EOL +
                        "navigations:" + EOL +
                        "  left column" + EOL
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentCell() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.A1.setDefaultAnchor(),
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
                        .setDefaultAnchor(),
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
                        .setDefaultAnchor(),
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
                        .setAnchoredSelection(
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
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCell("B2")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallCellRange() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
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
                        .setAnchoredSelection(
                                Optional.of(
                                        COLUMN.setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallColumnRange() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
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
                        .setAnchoredSelection(
                                Optional.of(
                                        ROW.setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testJsonMarshallRowRange() {
        this.marshallRoundTripTwiceAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
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
                        .setAnchoredSelection(
                                SpreadsheetViewport.NO_ANCHORED_SELECTION
                        ),
                RECTANGLE.toString()
        );
    }

    @Test
    public void testToStringWithSelectionCell() {
        this.toStringAndCheck(
                HOME.viewportRectangle(WIDTH, HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        CELL.setDefaultAnchor()
                                )
                        ),
                RECTANGLE + " anchoredSelection: " + CELL
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
                RECTANGLE + " anchoredSelection: " + CELL_RANGE + " " + ANCHOR + " navigations: " + NAVIGATIONS.iterator().next()
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
