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
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportTest implements ClassTesting<SpreadsheetViewport>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewport>,
        JsonNodeMarshallingTesting<SpreadsheetViewport>,
        ToStringTesting<SpreadsheetViewport>,
        TreePrintableTesting {

    private static final SpreadsheetColumnReference COLUMN = SpreadsheetSelection.parseColumn("B");
    private static final SpreadsheetCellReference CELL = SpreadsheetSelection.parseCell("B2");
    private static final SpreadsheetRowReference ROW = SpreadsheetSelection.parseRow("2");

    private static final SpreadsheetColumnReferenceRange COLUMN_RANGE = SpreadsheetSelection.parseColumnRange("B:C");
    private static final SpreadsheetCellRange CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
    private static final SpreadsheetRowReferenceRange ROW_RANGE = SpreadsheetSelection.parseRowRange("2:3");

    private static final SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("Label123");

    private static final SpreadsheetSelection SELECTION = CELL_RANGE;
    private static final SpreadsheetViewportAnchor ANCHOR = SpreadsheetViewportAnchor.TOP_LEFT;
    private static final List<SpreadsheetViewportSelectionNavigation> NAVIGATIONS = Lists.of(
            SpreadsheetViewportSelectionNavigation.leftColumn()
    );

    @Test
    public void testWithNullSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewport.with(
                        null,
                        SpreadsheetViewportAnchor.NONE,
                        SpreadsheetViewport.NO_NAVIGATION
                )
        );
    }

    @Test
    public void testWithNullAnchorFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewport.with(
                        CELL,
                        null,
                        SpreadsheetViewport.NO_NAVIGATION
                )
        );
    }

    @Test
    public void testWithNullNavigationsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewport.with(
                        CELL,
                        SpreadsheetViewportAnchor.NONE,
                        null
                )
        );
    }

    // cell.............................................................................................................

    @Test
    public void testWithCellAndAnchorFails() {
        this.withAnchorNotNonRangeFails(CELL);
    }

    @Test
    public void testWithCellAndNoAnchor() {
        this.withNonRangeAndCheck(CELL);
    }

    @Test
    public void testWithColumnAndAnchorFails() {
        this.withAnchorNotNonRangeFails(COLUMN);
    }

    @Test
    public void testWithColumnAndNoAnchor() {
        this.withNonRangeAndCheck(COLUMN);
    }

    @Test
    public void testWithRowAndAnchorFails() {
        this.withAnchorNotNonRangeFails(ROW);
    }

    @Test
    public void testWithRowAndNoAnchor() {
        this.withNonRangeAndCheck(ROW);
    }

    private void withAnchorNotNonRangeFails(final SpreadsheetSelection selection) {
        for (final SpreadsheetViewportAnchor anchor : SpreadsheetViewportAnchor.values()) {
            if (anchor != SpreadsheetViewportAnchor.NONE) {
                this.withFails(
                        selection,
                        anchor,
                        "Invalid anchor " +
                                anchor +
                                " for " +
                                selection +
                                ", valid anchors: " +
                                SpreadsheetViewportAnchor.NONE
                );
            }
        }
    }

    private void withNonRangeAndCheck(final SpreadsheetSelection selection) {
        final SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.NONE;
        final List<SpreadsheetViewportSelectionNavigation> navigations = Lists.of(
                SpreadsheetViewportSelectionNavigation.leftColumn()
        );

        final SpreadsheetViewport viewportSelection = SpreadsheetViewport.with(
                selection,
                anchor,
                navigations
        );
        this.checkSelection(
                viewportSelection,
                selection
        );
        this.checkAnchor(
                viewportSelection,
                anchor
        );
        this.checkNavigations(
                viewportSelection,
                navigations
        );
    }

    // cellRange.........................................................................................................

    @Test
    public void testWithCellRangeAndLeftAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportAnchor.LEFT);
    }

    @Test
    public void testWithCellRangeAndRightAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportAnchor.RIGHT);
    }

    @Test
    public void testWithCellRangeAndTopAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportAnchor.TOP);
    }

    @Test
    public void testWithCellRangeAndBottomAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportAnchor.BOTTOM);
    }

    @Test
    public void testWithCellRangeAndTopLeftAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportAnchor.TOP_LEFT);
    }

    @Test
    public void testWithCellRangeAndTopRightAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportAnchor.TOP_RIGHT);
    }

    @Test
    public void testWithCellRangeAndBottomLeftAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportAnchor.BOTTOM_LEFT);
    }

    @Test
    public void testWithCellRangeAndBottomRightAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportAnchor.BOTTOM_RIGHT);
    }

    // columnRange.........................................................................................................

    @Test
    public void testWithColumnRangeAndTopAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportAnchor.TOP);
    }

    @Test
    public void testWithColumnRangeAndBottomAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportAnchor.BOTTOM);
    }

    @Test
    public void testWithColumnRangeAndTopLeftAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportAnchor.TOP_LEFT);
    }

    @Test
    public void testWithColumnRangeAndTopRightAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportAnchor.TOP_RIGHT);
    }

    @Test
    public void testWithColumnRangeAndBottomLeftAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportAnchor.BOTTOM_LEFT);
    }

    @Test
    public void testWithColumnRangeAndBottomRightAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportAnchor.BOTTOM_RIGHT);
    }

    @Test
    public void testWithColumnRangeAndLeftAnchor() {
        this.withAndCheck(COLUMN_RANGE, SpreadsheetViewportAnchor.LEFT);
    }

    @Test
    public void testWithColumnRangeAndRightAnchor() {
        this.withAndCheck(COLUMN_RANGE, SpreadsheetViewportAnchor.RIGHT);
    }

    // rowRange.........................................................................................................

    @Test
    public void testWithRowRangeAndLeftAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportAnchor.LEFT);
    }

    @Test
    public void testWithRowRangeAndRightAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportAnchor.RIGHT);
    }

    @Test
    public void testWithRowRangeAndTopLeftAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportAnchor.TOP_LEFT);
    }

    @Test
    public void testWithRowRangeAndTopRightAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportAnchor.TOP_RIGHT);
    }

    @Test
    public void testWithRowRangeAndBottomLeftAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportAnchor.BOTTOM_LEFT);
    }

    @Test
    public void testWithRowRangeAndBottomRightAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportAnchor.BOTTOM_RIGHT);
    }

    @Test
    public void testWithRowRangeAndTopAnchor() {
        this.withAndCheck(ROW_RANGE, SpreadsheetViewportAnchor.TOP);
    }

    @Test
    public void testWithRowRangeAndBottomAnchor() {
        this.withAndCheck(ROW_RANGE, SpreadsheetViewportAnchor.BOTTOM);
    }

    // label............................................................................................................

    @Test
    public void testWithLabelWithoutAnchor() {
        this.withNonRangeAndCheck(LABEL);
    }

    @Test
    public void testWithLabelAnyAnchor() {
        for (final SpreadsheetViewportAnchor anchor : SpreadsheetViewportAnchor.values()) {
            this.withAndCheck(LABEL, anchor);
        }
    }

    // helpers..........................................................................................................

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportAnchor anchor) {
        this.withFails(selection, anchor, null);
    }

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportAnchor anchor,
                           final String message) {
        this.withFails(
                selection,
                anchor,
                SpreadsheetViewport.NO_NAVIGATION,
                message
        );
    }

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportAnchor anchor,
                           final List<SpreadsheetViewportSelectionNavigation> navigations,
                           final String message) {

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> selection.setAnchor(anchor)
        );
        if (null != message) {
            this.checkEquals(message, thrown.getMessage(), "message");
        }
        final IllegalArgumentException thrown2 = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewport.with(
                        selection,
                        anchor,
                        navigations
                )
        );
        if (null != message) {
            this.checkEquals(message, thrown2.getMessage(), "message");
        }
    }

    private void withAndCheck(final SpreadsheetSelection selection,
                              final SpreadsheetViewportAnchor anchor) {
        final SpreadsheetViewport viewportSelection = selection.setAnchor(
                anchor
        );
        this.checkSelection(
                viewportSelection,
                selection
        );

        this.checkAnchor(
                viewportSelection,
                anchor
        );
        this.checkNavigations(
                viewportSelection,
                SpreadsheetViewport.NO_NAVIGATION
        );
    }

    // setSelection.....................................................................................................

    @Test
    public void testSetSelectionNullFails() {
        final SpreadsheetViewport viewportSelection = this.createObject();
        assertThrows(
                NullPointerException.class,
                () -> viewportSelection.setSelection(null)
        );
    }

    @Test
    public void testSetSelectionSame() {
        final SpreadsheetViewport viewportSelection = this.createObject();
        assertSame(
                viewportSelection,
                viewportSelection.setSelection(viewportSelection.selection())
        );
    }

    @Test
    public void testSetSelectionDifferent() {
        final SpreadsheetViewport viewportSelection = this.createObject();

        final SpreadsheetSelection selection = SpreadsheetSelection.parseCellRange("ZZ99");
        this.checkNotEquals(
                SELECTION,
                selection,
                "different selection"
        );

        final SpreadsheetViewport different = viewportSelection.setSelection(selection);
        assertNotSame(
                viewportSelection,
                different
        );
        this.checkSelection(
                different,
                selection
        );
        this.checkAnchor(
                different,
                viewportSelection.anchor()
        );
        this.checkNavigations(
                different,
                viewportSelection.navigations()
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
        final List<SpreadsheetViewportSelectionNavigation> navigations = Lists.of(
                SpreadsheetViewportSelectionNavigation.extendRightColumn()
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
        this.checkAnchor(
                differentSelection,
                selection.anchor()
        );
        this.checkNavigations(
                differentSelection,
                navigations
        );
    }

    private void checkSelection(final SpreadsheetViewport viewportSelection,
                                final SpreadsheetSelection selection) {
        this.checkEquals(
                selection,
                viewportSelection.selection(),
                "selection"
        );
    }

    private void checkAnchor(final SpreadsheetViewport viewportSelection,
                             final SpreadsheetViewportAnchor anchor) {
        this.checkEquals(
                anchor,
                viewportSelection.anchor(),
                "anchor"
        );
    }

    private void checkNavigations(final SpreadsheetViewport viewportSelection,
                                  final List<SpreadsheetViewportSelectionNavigation> navigations) {
        this.checkEquals(
                navigations,
                viewportSelection.navigations(),
                "navigations"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testDifferentSelection() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        SpreadsheetSelection.parseCellRange("X1:Y99"),
                        ANCHOR,
                        NAVIGATIONS
                )
        );
    }

    @Test
    public void testDifferentAnchor() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        SELECTION,
                        SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                        NAVIGATIONS
                )
        );
    }

    @Test
    public void testDifferentNavigations() {
        this.checkNotEquals(
                SpreadsheetViewport.with(
                        SELECTION,
                        SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                        Lists.of(
                                SpreadsheetViewportSelectionNavigation.rightColumn()
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
    public void testTreePrintNonRangeAndNavigations() {
        this.treePrintAndCheck(
                SpreadsheetViewport.with(
                        SpreadsheetSelection.A1,
                        SpreadsheetViewportAnchor.NONE,
                        Lists.of(
                                SpreadsheetViewportSelectionNavigation.leftColumn()
                        )
                ),
                "cell A1 left column" + EOL
        );
    }

    @Test
    public void testTreePrintNonRangeAndNavigations2() {
        this.treePrintAndCheck(
                SpreadsheetViewport.with(
                        SpreadsheetSelection.A1,
                        SpreadsheetViewportAnchor.NONE,
                        Lists.of(
                                SpreadsheetViewportSelectionNavigation.leftColumn(),
                                SpreadsheetViewportSelectionNavigation.upRow()
                        )
                ),
                "cell A1 left column,up row" + EOL
        );
    }

    @Test
    public void testTreePrintRangeWithAnchorAndNavigations() {
        this.treePrintAndCheck(
                SpreadsheetViewport.with(
                        SpreadsheetSelection.parseRowRange("12:34"),
                        SpreadsheetViewportAnchor.TOP,
                        Lists.of(
                                SpreadsheetViewportSelectionNavigation.leftColumn()
                        )
                ),
                "row-range 12:34 TOP left column" + EOL
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentCell() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.A1
                        .setAnchor(SpreadsheetViewportAnchor.NONE),
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
                SpreadsheetSelection.parseCell("B2")
                        .setAnchor(SpreadsheetViewportAnchor.NONE)
        );
    }

    @Test
    public void testJsonMarshallCellRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
        );
    }

    @Test
    public void testJsonMarshallColumn() {
        this.marshallRoundTripTwiceAndCheck(
                COLUMN.setAnchor(SpreadsheetViewportAnchor.NONE)
        );
    }

    @Test
    public void testJsonMarshallColumnRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseColumnRange("B:C")
                        .setAnchor(SpreadsheetViewportAnchor.LEFT)
        );
    }

    @Test
    public void testJsonMarshallRow() {
        this.marshallRoundTripTwiceAndCheck(
                COLUMN.setAnchor(SpreadsheetViewportAnchor.NONE)
        );
    }

    @Test
    public void testJsonMarshallRowRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(SpreadsheetViewportAnchor.TOP)
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToStringCell() {
        this.toStringAndCheck(
                CELL.setAnchor(SpreadsheetViewportAnchor.NONE),
                CELL.toString()
        );
    }

    @Test
    public void testToStringCellRangeWithAnchor() {
        this.toStringAndCheck(
                CELL_RANGE.setAnchor(SpreadsheetViewportAnchor.TOP_LEFT),
                CELL_RANGE + " " + SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testToStringWithNavigations() {
        this.toStringAndCheck(
                SpreadsheetViewport.with(
                        CELL_RANGE,
                        ANCHOR,
                        NAVIGATIONS
                ),
                CELL_RANGE + " " + ANCHOR + " " + NAVIGATIONS.iterator().next()
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

    @Override
    public SpreadsheetViewport createObject() {
        return SpreadsheetViewport.with(
                SELECTION,
                ANCHOR,
                NAVIGATIONS
        );
    }

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
