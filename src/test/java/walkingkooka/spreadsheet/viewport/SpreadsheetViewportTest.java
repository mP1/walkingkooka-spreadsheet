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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
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
    HasUrlFragmentTesting,
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

    private static final boolean INCLUDE_FROZEN_COLUMNS_ROWS = SpreadsheetViewport.DEFAULT_INCLUDE_FROZEN_COLUMNS_ROWS;

    private static final SpreadsheetCellRangeReference CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
    private static final SpreadsheetSelection SELECTION = CELL_RANGE;
    private static final SpreadsheetViewportAnchor ANCHOR = SpreadsheetViewportAnchor.TOP_LEFT;

    private static final SpreadsheetViewportNavigationList NAVIGATIONS = SpreadsheetViewportNavigationList.EMPTY
        .concat(
            SpreadsheetViewportNavigation.leftColumn()
        );

    @Test
    public void testWithNullRectangleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetViewport.with(null)
        );
    }

    // setIncludeFrozenColumnsRows......................................................................................

    @Test
    public void testSetIncludeFrozenColumnsRowsSame() {
        final SpreadsheetViewport viewport = this.createObject();
        assertSame(
            viewport,
            viewport.setIncludeFrozenColumnsRows(viewport.includeFrozenColumnsRows())
        );
    }

    @Test
    public void testSetIncludeFrozenColumnsRowsDifferent() {
        final SpreadsheetViewport viewport = this.createObject();

        final boolean differentIncludeFrozenColumnsRows = false == INCLUDE_FROZEN_COLUMNS_ROWS;

        this.checkNotEquals(
            viewport.includeFrozenColumnsRows(),
            differentIncludeFrozenColumnsRows,
            "different includeFrozenColumnsRows"
        );

        final SpreadsheetViewport different = viewport.setIncludeFrozenColumnsRows(differentIncludeFrozenColumnsRows);
        assertNotSame(
            viewport,
            different
        );
        this.includeFrozenColumnsRowsAndCheck(
            different,
            differentIncludeFrozenColumnsRows
        );
        this.anchoredSelectionAndCheck(
            different,
            viewport.anchoredSelection()
        );
        this.navigationsAndCheck(
            different,
            viewport.navigations()
        );
    }

    // setAnchoredSelection.............................................................................................

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
        this.includeFrozenColumnsRowsAndCheck(
            different,
            INCLUDE_FROZEN_COLUMNS_ROWS
        );
        this.anchoredSelectionAndCheck(
            different,
            differentAnchoredSelection
        );
        this.navigationsAndCheck(
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
        final SpreadsheetViewportNavigationList navigations = SpreadsheetViewportNavigationList.EMPTY.concat(
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
        this.includeFrozenColumnsRowsAndCheck(
            differentViewport,
            INCLUDE_FROZEN_COLUMNS_ROWS
        );
        this.anchoredSelectionAndCheck(
            differentViewport,
            viewport.anchoredSelection()
        );
        this.navigationsAndCheck(
            differentViewport,
            navigations
        );
    }

    private void includeFrozenColumnsRowsAndCheck(final SpreadsheetViewport viewport,
                                                  final boolean includeFrozenColumnsRows) {
        this.checkEquals(
            includeFrozenColumnsRows,
            viewport.includeFrozenColumnsRows(),
            "includeFrozenColumnsRows"
        );
    }

    private void anchoredSelectionAndCheck(final SpreadsheetViewport viewport,
                                           final Optional<AnchoredSpreadsheetSelection> anchoredSelection) {
        this.checkEquals(
            anchoredSelection,
            viewport.anchoredSelection(),
            "anchoredSelection"
        );
    }

    private void navigationsAndCheck(final SpreadsheetViewport viewport,
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
                INCLUDE_FROZEN_COLUMNS_ROWS,
                SpreadsheetViewport.NO_ANCHORED_SELECTION,
                NAVIGATIONS
            )
        );
    }

    @Test
    public void testEqualsDifferentIncludeFrozenColumnsRows() {
        this.checkNotEquals(
            SpreadsheetViewport.with(
                RECTANGLE,
                false == INCLUDE_FROZEN_COLUMNS_ROWS,
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
                INCLUDE_FROZEN_COLUMNS_ROWS,
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
                INCLUDE_FROZEN_COLUMNS_ROWS,
                SpreadsheetViewport.NO_ANCHORED_SELECTION,
                SpreadsheetViewportNavigationList.EMPTY.concat(
                    SpreadsheetViewportNavigation.rightColumn()
                )
            )
        );
    }

    @Override
    public SpreadsheetViewport createObject() {
        return SpreadsheetViewport.with(
            RECTANGLE,
            INCLUDE_FROZEN_COLUMNS_ROWS,
            Optional.of(
                SELECTION.setAnchor(ANCHOR)
            ),
            NAVIGATIONS
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
    public void testTreePrintWithRectangle() {
        this.treePrintAndCheck(
            SpreadsheetViewport.with(
                RECTANGLE,
                INCLUDE_FROZEN_COLUMNS_ROWS,
                SpreadsheetViewport.NO_ANCHORED_SELECTION,
                SpreadsheetViewport.NO_NAVIGATION
            ),
            "SpreadsheetViewport\n" +
                "  rectangle:\n" +
                "    SpreadsheetViewportRectangle\n" +
                "      home: A1\n" +
                "      width: 100.0\n" +
                "      height: 50.0\n"
        );
    }

    @Test
    public void testTreePrintWithRectangleAndNavigations() {
        this.treePrintAndCheck(
            SpreadsheetViewport.with(
                RECTANGLE,
                INCLUDE_FROZEN_COLUMNS_ROWS,
                SpreadsheetViewport.NO_ANCHORED_SELECTION,
                SpreadsheetViewportNavigationList.EMPTY.setElements(
                    Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.upRow()
                    )
                )
            ),
            "SpreadsheetViewport\n" +
                "  rectangle:\n" +
                "    SpreadsheetViewportRectangle\n" +
                "      home: A1\n" +
                "      width: 100.0\n" +
                "      height: 50.0\n" +
                "  navigations:\n" +
                "    left column\n" +
                "    up row\n"
        );
    }

    @Test
    public void testTreePrintWithRectangleAnchoredSelectionRowRangeNavigations() {
        this.treePrintAndCheck(
            SpreadsheetViewport.with(
                RECTANGLE,
                INCLUDE_FROZEN_COLUMNS_ROWS,
                Optional.of(
                    SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(
                            SpreadsheetViewportAnchor.TOP
                        )
                ),
                SpreadsheetViewportNavigationList.EMPTY.setElements(
                    Lists.of(
                        SpreadsheetViewportNavigation.leftColumn()
                    )
                )
            ),
            "SpreadsheetViewport\n" +
                "  rectangle:\n" +
                "    SpreadsheetViewportRectangle\n" +
                "      home: A1\n" +
                "      width: 100.0\n" +
                "      height: 50.0\n" +
                "  anchoredSelection:row-range 12:34 TOP\n" +
                "  navigations:\n" +
                "    left column\n"
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentSpreadsheetViewportRectangleOnly() {
        this.urlFragmentAndCheck(
            SpreadsheetViewport.with(RECTANGLE),
            "/home/A1/width/100/height/50"
        );
    }

    @Test
    public void testUrlFragmentCell() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.A1.setDefaultAnchor(),
            "/home/A1/width/100/height/50/selection/A1"
        );
    }

    @Test
    public void testUrlFragmentCellRangeTopLeft() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3")
                .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT),
            "/home/A1/width/100/height/50/selection/B2:C3/top-left"
        );
    }

    @Test
    public void testUrlFragmentCellRangeTopRight() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3")
                .setAnchor(SpreadsheetViewportAnchor.TOP_RIGHT),
            "/home/A1/width/100/height/50/selection/B2:C3/top-right"
        );
    }

    @Test
    public void testUrlFragmentColumnNone() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseColumn("Z")
                .setDefaultAnchor(),
            "/home/A1/width/100/height/50/selection/Z"
        );
    }

    @Test
    public void testUrlFragmentColumnRangeLeft() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseColumnRange("X:Y")
                .setAnchor(SpreadsheetViewportAnchor.LEFT),
            "/home/A1/width/100/height/50/selection/X:Y/left"
        );
    }

    @Test
    public void testUrlFragmentColumnRangeRight() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseColumnRange("X:Y")
                .setAnchor(SpreadsheetViewportAnchor.RIGHT),
            "/home/A1/width/100/height/50/selection/X:Y/right"
        );
    }

    @Test
    public void testUrlFragmentLabelNone() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseCellOrLabel("Label123")
                .setDefaultAnchor(),
            "/home/A1/width/100/height/50/selection/Label123"
        );
    }

    @Test
    public void testUrlFragmentLabelBottomRight() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseCellOrLabel("Label123")
                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT),
            "/home/A1/width/100/height/50/selection/Label123/bottom-right"
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

    @Test
    public void testUrlFragmentIncludeFrozenColumnsRowsTrueAndWithoutSelection() {
        this.urlFragmentAndCheck(
            HOME.viewportRectangle(111, 222)
                .viewport()
                .setIncludeFrozenColumnsRows(true)
                .setNavigations(SpreadsheetViewportNavigationList.parse("right 99px,down 999px")),
            "/home/A1/width/111/height/222/includeFrozenColumnsRows/true/navigations/right%2099px,down%20999px"
        );
    }

    @Test
    public void testUrlFragmentMissingSelection() {
        this.urlFragmentAndCheck(
            HOME.viewportRectangle(111, 222)
                .viewport()
                .setNavigations(NAVIGATIONS),
            "/home/A1/width/111/height/222/navigations/left%20column"
        );
    }

    @Test
    public void testUrlFragmentAllProperties() {
        this.urlFragmentAndCheck(
            this.createObject(),
            "/home/A1/width/100/height/50/selection/B2:C3/top-left/navigations/left%20column"
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "{\n" +
                "  \"rectangle\": \"A1:100.0:50.0\",\n" +
                "  \"anchoredSelection\": {\n" +
                "    \"selection\": {\n" +
                "      \"type\": \"spreadsheet-cell-range-reference\",\n" +
                "      \"value\": \"B2:C3\"\n" +
                "    },\n" +
                "    \"anchor\": \"TOP_LEFT\"\n" +
                "  },\n" +
                "  \"navigations\": \"left column\"\n" +
                "}"
        );
    }

    @Test
    public void testJsonMarshallIncludeFrozenColumnsRowsTrue() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue()
                .setIncludeFrozenColumnsRows(true),
            "{\n" +
                "  \"rectangle\": \"A1:100.0:50.0\",\n" +
                "  \"includeFrozenColumnsRows\": true,\n" +
                "  \"anchoredSelection\": {\n" +
                "    \"selection\": {\n" +
                "      \"type\": \"spreadsheet-cell-range-reference\",\n" +
                "      \"value\": \"B2:C3\"\n" +
                "    },\n" +
                "    \"anchor\": \"TOP_LEFT\"\n" +
                "  },\n" +
                "  \"navigations\": \"left column\"\n" +
                "}"
        );
    }

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

    @Override
    public SpreadsheetViewport unmarshall(final JsonNode json,
                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewport.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetViewport createJsonNodeMarshallingValue() {
        return this.createObject();
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
            "home: A1 width: 100.0 height: 50.0"
        );
    }

    @Test
    public void testToStringIncludeFrozenColumnsRows() {
        this.toStringAndCheck(
            HOME.viewportRectangle(WIDTH, HEIGHT)
                .viewport()
                .setIncludeFrozenColumnsRows(true)
                .setAnchoredSelection(
                    SpreadsheetViewport.NO_ANCHORED_SELECTION
                ),
            "home: A1 width: 100.0 height: 50.0 includeFrozenColumnsRows: true"
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
            "home: A1 width: 100.0 height: 50.0 anchoredSelection: B2"
        );
    }

    @Test
    public void testToStringCellRangeSelectionAndNavigations() {
        this.toStringAndCheck(
            SpreadsheetViewport.with(
                RECTANGLE,
                INCLUDE_FROZEN_COLUMNS_ROWS,
                Optional.of(
                    CELL_RANGE.setAnchor(ANCHOR)
                ),
                NAVIGATIONS
            ),
            RECTANGLE + " anchoredSelection: " + CELL_RANGE + " " + ANCHOR + " navigations: " + NAVIGATIONS.iterator().next()
        );
    }

    // fromUrlFragment..................................................................................................

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndInvalidTokenFails() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentFails(
            rectangle.urlFragment() + "/abc",
            "Invalid character 'a' at 30 expected \"/\""
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangle() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment(),
            rectangle.viewport()
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndInvalidIncludesFrozenColumnsRowsFails() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentFails(
            rectangle.urlFragment() + "/includeFrozenColumnsRows/!invalid",
            "Invalid character '!' at 55"
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndIncludesFrozenColumnsRowsFalse() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/includeFrozenColumnsRows/false",
            rectangle.viewport()
                .setIncludeFrozenColumnsRows(false)
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndIncludesFrozenColumnsRowsTrue() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/includeFrozenColumnsRows/true",
            rectangle.viewport()
                .setIncludeFrozenColumnsRows(true)
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndInvalidSelectionFails() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentFails(
            rectangle.urlFragment() + "/selection/!Invalid",
            "Missing selection"
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndCell() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/selection/B2",
            rectangle.viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCell("B2")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndCellRange() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/selection/C3:D4",
            rectangle.viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("C3:D4")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndCellRangeAndTopLeft() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/selection/C3:D4/top-left",
            rectangle.viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("C3:D4")
                            .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                    )
                )
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndLabel() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/selection/Label123",
            rectangle.viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.labelName("Label123")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndCellAndNavigations() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/selection/B2/navigations/right 400px",
            rectangle.viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCell("B2")
                            .setDefaultAnchor()
                    )
                ).setNavigations(
                    SpreadsheetViewportNavigationList.parse("right 400px")
                )
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndCellRangeAnchorAndNavigations() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/selection/C3:D4/top-left/navigations/right 400px",
            rectangle.viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("C3:D4")
                            .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                    )
                ).setNavigations(
                    SpreadsheetViewportNavigationList.parse("right 400px")
                )
        );
    }

    @Test
    public void testFromUrlFragmentWithSpreadsheetViewportRectangleAndNavigations() {
        final SpreadsheetViewportRectangle rectangle = SpreadsheetViewportRectangle.with(
            SpreadsheetSelection.A1,
            200,
            300
        );
        this.fromUrlFragmentAndCheck(
            rectangle.urlFragment() + "/navigations/right 555px",
            rectangle.viewport()
                .setNavigations(
                    SpreadsheetViewportNavigationList.parse("right 555px")
                )
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
            () -> SpreadsheetViewport.fromUrlFragment(urlFragment)
        );

        this.checkEquals(
            expected.getMessage(),
            thrown.getMessage()
        );
    }

    private void fromUrlFragmentAndCheck(final String urlFragment,
                                         final SpreadsheetViewport expected) {
        this.fromUrlFragmentAndCheck(
            UrlFragment.parse(urlFragment),
            expected
        );
    }

    private void fromUrlFragmentAndCheck(final UrlFragment urlFragment,
                                         final SpreadsheetViewport expected) {
        this.checkEquals(
            expected,
            SpreadsheetViewport.fromUrlFragment(urlFragment)
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
}
