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
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportSelectionTest implements ClassTesting<SpreadsheetViewportSelection>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewportSelection>,
        JsonNodeMarshallingTesting<SpreadsheetViewportSelection>,
        ToStringTesting<SpreadsheetViewportSelection>,
        TreePrintableTesting {

    private static final SpreadsheetColumnReference COLUMN = SpreadsheetSelection.parseColumn("B");
    private static final SpreadsheetCellReference CELL = SpreadsheetSelection.parseCell("B2");
    private static final SpreadsheetRowReference ROW = SpreadsheetSelection.parseRow("2");

    private static final SpreadsheetColumnReferenceRange COLUMN_RANGE = SpreadsheetSelection.parseColumnRange("B:C");
    private static final SpreadsheetCellRange CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
    private static final SpreadsheetRowReferenceRange ROW_RANGE = SpreadsheetSelection.parseRowRange("2:3");

    private static final SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("Label123");

    private static final SpreadsheetSelection SELECTION = CELL_RANGE;
    private static final SpreadsheetViewportSelectionAnchor ANCHOR = SpreadsheetViewportSelectionAnchor.TOP_LEFT;
    private static final Optional<SpreadsheetViewportSelectionNavigation> NAVIGATION = Optional.of(
            SpreadsheetViewportSelectionNavigation.LEFT
    );

    @Test
    public void testWithNullSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelection.with(
                        null,
                        SpreadsheetViewportSelectionAnchor.NONE,
                        SpreadsheetViewportSelection.NO_NAVIGATION
                )
        );
    }

    @Test
    public void testWithNullAnchorFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelection.with(
                        CELL,
                        null,
                        SpreadsheetViewportSelection.NO_NAVIGATION
                )
        );
    }

    @Test
    public void testWithNullNavigationFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelection.with(
                        CELL,
                        SpreadsheetViewportSelectionAnchor.NONE,
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
        for (final SpreadsheetViewportSelectionAnchor anchor : SpreadsheetViewportSelectionAnchor.values()) {
            if (anchor != SpreadsheetViewportSelectionAnchor.NONE) {
                this.withFails(
                        selection,
                        anchor,
                        "Invalid anchor " +
                                anchor +
                                " for " +
                                selection +
                                ", valid anchors: " +
                                SpreadsheetViewportSelectionAnchor.NONE
                );
            }
        }
    }

    private void withNonRangeAndCheck(final SpreadsheetSelection selection) {
        final SpreadsheetViewportSelectionAnchor anchor = SpreadsheetViewportSelectionAnchor.NONE;
        final Optional<SpreadsheetViewportSelectionNavigation> navigation = Optional.of(
                SpreadsheetViewportSelectionNavigation.LEFT
        );

        final SpreadsheetViewportSelection viewportSelection = SpreadsheetViewportSelection.with(
                selection,
                anchor,
                navigation
        );
        this.checkSelection(
                viewportSelection,
                selection
        );
        this.checkAnchor(
                viewportSelection,
                anchor
        );
        this.checkNavigation(
                viewportSelection,
                navigation
        );
    }

    // cellRange.........................................................................................................

    @Test
    public void testWithCellRangeAndLeftAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportSelectionAnchor.LEFT);
    }

    @Test
    public void testWithCellRangeAndRightAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportSelectionAnchor.RIGHT);
    }

    @Test
    public void testWithCellRangeAndTopAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportSelectionAnchor.TOP);
    }

    @Test
    public void testWithCellRangeAndBottomAnchorFails() {
        this.withFails(CELL_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM);
    }

    @Test
    public void testWithCellRangeAndTopLeftAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportSelectionAnchor.TOP_LEFT);
    }

    @Test
    public void testWithCellRangeAndTopRightAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportSelectionAnchor.TOP_RIGHT);
    }

    @Test
    public void testWithCellRangeAndBottomLeftAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT);
    }

    @Test
    public void testWithCellRangeAndBottomRightAnchor() {
        this.withAndCheck(CELL_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT);
    }

    // columnRange.........................................................................................................

    @Test
    public void testWithColumnRangeAndTopAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.TOP);
    }

    @Test
    public void testWithColumnRangeAndBottomAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM);
    }

    @Test
    public void testWithColumnRangeAndTopLeftAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.TOP_LEFT);
    }

    @Test
    public void testWithColumnRangeAndTopRightAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.TOP_RIGHT);
    }

    @Test
    public void testWithColumnRangeAndBottomLeftAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT);
    }

    @Test
    public void testWithColumnRangeAndBottomRightAnchorFails() {
        this.withFails(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT);
    }

    @Test
    public void testWithColumnRangeAndLeftAnchor() {
        this.withAndCheck(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.LEFT);
    }

    @Test
    public void testWithColumnRangeAndRightAnchor() {
        this.withAndCheck(COLUMN_RANGE, SpreadsheetViewportSelectionAnchor.RIGHT);
    }

    // rowRange.........................................................................................................

    @Test
    public void testWithRowRangeAndLeftAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportSelectionAnchor.LEFT);
    }

    @Test
    public void testWithRowRangeAndRightAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportSelectionAnchor.RIGHT);
    }

    @Test
    public void testWithRowRangeAndTopLeftAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportSelectionAnchor.TOP_LEFT);
    }

    @Test
    public void testWithRowRangeAndTopRightAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportSelectionAnchor.TOP_RIGHT);
    }

    @Test
    public void testWithRowRangeAndBottomLeftAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT);
    }

    @Test
    public void testWithRowRangeAndBottomRightAnchorFails() {
        this.withFails(ROW_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT);
    }

    @Test
    public void testWithRowRangeAndTopAnchor() {
        this.withAndCheck(ROW_RANGE, SpreadsheetViewportSelectionAnchor.TOP);
    }

    @Test
    public void testWithRowRangeAndBottomAnchor() {
        this.withAndCheck(ROW_RANGE, SpreadsheetViewportSelectionAnchor.BOTTOM);
    }

    // label............................................................................................................

    @Test
    public void testWithLabelWithoutAnchor() {
        this.withNonRangeAndCheck(LABEL);
    }

    @Test
    public void testWithLabelAnyAnchor() {
        for (final SpreadsheetViewportSelectionAnchor anchor : SpreadsheetViewportSelectionAnchor.values()) {
            this.withAndCheck(LABEL, anchor);
        }
    }

    // helpers..........................................................................................................

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportSelectionAnchor anchor) {
        this.withFails(selection, anchor, null);
    }

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportSelectionAnchor anchor,
                           final String message) {
        this.withFails(
                selection,
                anchor,
                SpreadsheetViewportSelection.NO_NAVIGATION,
                message
        );
    }

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportSelectionAnchor anchor,
                           final Optional<SpreadsheetViewportSelectionNavigation> navigation,
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
                () -> SpreadsheetViewportSelection.with(
                        selection,
                        anchor,
                        navigation
                )
        );
        if (null != message) {
            this.checkEquals(message, thrown2.getMessage(), "message");
        }
    }

    private void withAndCheck(final SpreadsheetSelection selection,
                              final SpreadsheetViewportSelectionAnchor anchor) {
        final SpreadsheetViewportSelection viewportSelection = selection.setAnchor(
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
        this.checkNavigation(
                viewportSelection,
                SpreadsheetViewportSelection.NO_NAVIGATION
        );
    }

    // setSelection.....................................................................................................

    @Test
    public void testSetSelectionNullFails() {
        final SpreadsheetViewportSelection viewportSelection = this.createObject();
        assertThrows(
                NullPointerException.class,
                () -> viewportSelection.setSelection(null)
        );
    }

    @Test
    public void testSetSelectionSame() {
        final SpreadsheetViewportSelection viewportSelection = this.createObject();
        assertSame(
                viewportSelection,
                viewportSelection.setSelection(viewportSelection.selection())
        );
    }

    @Test
    public void testSetSelectionDifferent() {
        final SpreadsheetViewportSelection viewportSelection = this.createObject();

        final SpreadsheetSelection selection = SpreadsheetSelection.parseCellRange("ZZ99");
        this.checkNotEquals(
                SELECTION,
                selection,
                "different selection"
        );

        final SpreadsheetViewportSelection different = viewportSelection.setSelection(selection);
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
        this.checkNavigation(
                different,
                viewportSelection.navigation()
        );
    }

    // setNavigation.....................................................................................................

    @Test
    public void testSetNavigationNullFails() {
        final SpreadsheetViewportSelection selection = this.createObject();
        assertThrows(
                NullPointerException.class,
                () -> selection.setNavigation(null)
        );
    }

    @Test
    public void testSetNavigationSame() {
        final SpreadsheetViewportSelection selection = this.createObject();
        assertSame(
                selection,
                selection.setNavigation(selection.navigation())
        );
    }

    @Test
    public void testSetNavigationDifferent() {
        final SpreadsheetViewportSelection selection = this.createObject();
        final Optional<SpreadsheetViewportSelectionNavigation> navigation = Optional.of(
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT
        );
        this.checkNotEquals(
                NAVIGATION,
                navigation,
                "different navigation"
        );

        final SpreadsheetViewportSelection differentSelection = selection.setNavigation(navigation);
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
        this.checkNavigation(
                differentSelection,
                navigation
        );
    }

    private void checkSelection(final SpreadsheetViewportSelection viewportSelection,
                                final SpreadsheetSelection selection) {
        this.checkEquals(
                selection,
                viewportSelection.selection(),
                "selection"
        );
    }

    private void checkAnchor(final SpreadsheetViewportSelection viewportSelection,
                             final SpreadsheetViewportSelectionAnchor anchor) {
        this.checkEquals(
                anchor,
                viewportSelection.anchor(),
                "anchor"
        );
    }

    private void checkNavigation(final SpreadsheetViewportSelection viewportSelection,
                                 final Optional<SpreadsheetViewportSelectionNavigation> navigation) {
        this.checkEquals(
                navigation,
                viewportSelection.navigation(),
                "navigation"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testDifferentSelection() {
        this.checkNotEquals(
                SpreadsheetViewportSelection.with(
                        SpreadsheetSelection.parseCellRange("X1:Y99"),
                        ANCHOR,
                        NAVIGATION
                )
        );
    }

    @Test
    public void testDifferentAnchor() {
        this.checkNotEquals(
                SpreadsheetViewportSelection.with(
                        SELECTION,
                        SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT,
                        NAVIGATION
                )
        );
    }

    @Test
    public void testDifferentNavigation() {
        this.checkNotEquals(
                SpreadsheetViewportSelection.with(
                        SELECTION,
                        SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT,
                        Optional.of(
                                SpreadsheetViewportSelectionNavigation.RIGHT
                        )
                )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.A1
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                "cell A1" + EOL
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRow("12")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                "row 12" + EOL
        );
    }

    @Test
    public void testTreePrintWithAnchor() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP),
                "row-range 12:34 TOP" + EOL
        );
    }

    @Test
    public void testTreePrintNonRangeAndNavigation() {
        this.treePrintAndCheck(
                SpreadsheetViewportSelection.with(
                        SpreadsheetSelection.A1,
                        SpreadsheetViewportSelectionAnchor.NONE,
                        Optional.of(
                                SpreadsheetViewportSelectionNavigation.LEFT
                        )
                ),
                "cell A1 LEFT" + EOL
        );
    }

    @Test
    public void testTreePrintRangeWithAnchorAndNavigation() {
        this.treePrintAndCheck(
                SpreadsheetViewportSelection.with(
                        SpreadsheetSelection.parseRowRange("12:34"),
                        SpreadsheetViewportSelectionAnchor.TOP,
                        Optional.of(
                                SpreadsheetViewportSelectionNavigation.LEFT
                        )
                ),
                "row-range 12:34 TOP LEFT" + EOL
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragmentCell() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.A1
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                "/cell/A1"
        );
    }

    @Test
    public void testUrlFragmentCellRangeTopLeft() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP_LEFT),
                "/cell/B2:C3/top-left"
        );
    }

    @Test
    public void testUrlFragmentCellRangeTopRight() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP_RIGHT),
                "/cell/B2:C3/top-right"
        );
    }

    @Test
    public void testUrlFragmentColumnNone() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumn("Z")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                "/column/Z"
        );
    }

    @Test
    public void testUrlFragmentColumnRangeLeft() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumnRange("X:Y")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.LEFT),
                "/column/X:Y/left"
        );
    }

    @Test
    public void testUrlFragmentColumnRangeRight() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumnRange("X:Y")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.RIGHT),
                "/column/X:Y/right"
        );
    }

    @Test
    public void testUrlFragmentLabelNone() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellOrLabel("Label123")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                "/cell/Label123"
        );
    }

    @Test
    public void testUrlFragmentLabelBottomRight() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellOrLabel("Label123")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT),
                "/cell/Label123/bottom-right"
        );
    }

    private void urlFragmentAndCheck(final SpreadsheetViewportSelection selection,
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
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testJsonMarshallCellRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP_LEFT)
        );
    }

    @Test
    public void testJsonMarshallColumn() {
        this.marshallRoundTripTwiceAndCheck(
                COLUMN.setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testJsonMarshallColumnRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseColumnRange("B:C")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testJsonMarshallRow() {
        this.marshallRoundTripTwiceAndCheck(
                COLUMN.setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testJsonMarshallRowRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToStringCell() {
        this.toStringAndCheck(
                CELL.setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                CELL.toString()
        );
    }

    @Test
    public void testToStringCellRangeWithAnchor() {
        this.toStringAndCheck(
                CELL_RANGE.setAnchor(SpreadsheetViewportSelectionAnchor.TOP_LEFT),
                CELL_RANGE + " " + SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testToStringWithNavigation() {
        this.toStringAndCheck(
                SpreadsheetViewportSelection.with(
                        CELL_RANGE,
                        ANCHOR,
                        NAVIGATION
                ),
                CELL_RANGE + " " + ANCHOR + " " + NAVIGATION.get()
        );
    }

    // helpers..........................................................................................................

    @Override
    public Class<SpreadsheetViewportSelection> type() {
        return SpreadsheetViewportSelection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetViewportSelection createObject() {
        return SpreadsheetViewportSelection.with(
                SELECTION,
                ANCHOR,
                NAVIGATION
        );
    }

    @Override
    public SpreadsheetViewportSelection unmarshall(final JsonNode from,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewportSelection.unmarshall(from, context);
    }

    @Override
    public SpreadsheetViewportSelection createJsonNodeMarshallingValue() {
        return this.createObject();
    }
}
