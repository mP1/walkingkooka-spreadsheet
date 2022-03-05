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
    private static final Optional<SpreadsheetViewportSelectionAnchor> ANCHOR = Optional.of(
            SpreadsheetViewportSelectionAnchor.TOP_LEFT
    );
    private static final Optional<SpreadsheetViewportSelectionNavigation> NAVIGATION = Optional.of(
            SpreadsheetViewportSelectionNavigation.LEFT
    );

    @Test
    public void testWithNullSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelection.with(
                        null,
                        SpreadsheetViewportSelection.NO_ANCHOR,
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
                        SpreadsheetViewportSelection.NO_ANCHOR,
                        null
                )
        );
    }

    // cell.............................................................................................................

    @Test
    public void testWithCellAndAnchorFails() {
        this.withAnyAnchorFails(CELL);
    }

    @Test
    public void testWithCellAndNoAnchor() {
        this.withAndCheck(CELL);
    }

    @Test
    public void testWithColumnAndAnchorFails() {
        this.withAnyAnchorFails(COLUMN);
    }

    @Test
    public void testWithColumnAndNoAnchor() {
        this.withAndCheck(COLUMN);
    }

    @Test
    public void testWithRowAndAnchorFails() {
        this.withAnyAnchorFails(ROW);
    }

    @Test
    public void testWithRowAndNoAnchor() {
        this.withAndCheck(ROW);
    }

    private void withAndCheck(final SpreadsheetSelection selection) {
        final Optional<SpreadsheetViewportSelectionAnchor> anchor = SpreadsheetViewportSelection.NO_ANCHOR;
        final Optional<SpreadsheetViewportSelectionNavigation> navigation = Optional.of(
                SpreadsheetViewportSelectionNavigation.LEFT
        );

        final SpreadsheetViewportSelection viewportSelection = SpreadsheetViewportSelection.with(
                selection,
                anchor,
                navigation
        );
        assertSame(selection, viewportSelection.selection(), "selection");
        this.checkEquals(anchor, viewportSelection.anchor(), "anchor");
        this.checkEquals(navigation, viewportSelection.navigation(), "navigation");
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
        this.withAndCheck(LABEL);
    }

    @Test
    public void testWithLabelAnyAnchor() {
        for (final SpreadsheetViewportSelectionAnchor anchor : SpreadsheetViewportSelectionAnchor.values()) {
            this.withAndCheck(LABEL, anchor);
        }
    }

    // helpers..........................................................................................................

    private void withAnyAnchorFails(final SpreadsheetSelection selection) {
        for (final SpreadsheetViewportSelectionAnchor anchor : SpreadsheetViewportSelectionAnchor.values()) {
            this.withFails(selection, anchor, selection + " must not have an anchor got " + anchor);
        }
    }

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportSelectionAnchor anchor) {
        this.withFails(selection, anchor, null);
    }

    private void withFails(final SpreadsheetSelection selection,
                           final SpreadsheetViewportSelectionAnchor anchor,
                           final String message) {
        this.withFails(
                selection,
                Optional.of(anchor),
                SpreadsheetViewportSelection.NO_NAVIGATION,
                message
        );
    }

    private void withFails(final SpreadsheetSelection selection,
                           final Optional<SpreadsheetViewportSelectionAnchor> anchor,
                           final Optional<SpreadsheetViewportSelectionNavigation> navigation,
                           final String message) {
        if (anchor.isPresent()) {
            final IllegalArgumentException thrown = assertThrows(
                    IllegalArgumentException.class,
                    () -> selection.setAnchor(anchor)
            );
            if (null != message) {
                this.checkEquals(message, thrown.getMessage(), "message");
            }

        }
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelection.with(
                        selection,
                        anchor,
                        navigation
                )
        );
        if (null != message) {
            this.checkEquals(message, thrown.getMessage(), "message");
        }
    }

    private void withAndCheck(final SpreadsheetSelection selection,
                              final SpreadsheetViewportSelectionAnchor anchor) {
        final SpreadsheetViewportSelection viewportSelection = selection.setAnchor(
                Optional.of(anchor)
        );
        assertSame(selection, viewportSelection.selection(), "selection");

        this.checkEquals(
                Optional.of(anchor),
                viewportSelection.anchor(),
                "anchor"
        );
        this.checkEquals(
                SpreadsheetViewportSelection.NO_NAVIGATION,
                viewportSelection.navigation(),
                "navigation"
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
        this.checkEquals(
                selection.selection(),
                differentSelection.selection(),
                "selection"
        );
        this.checkEquals(
                selection.anchor(),
                differentSelection.anchor(),
                "anchor"
        );
        this.checkEquals(
                navigation,
                differentSelection.navigation(),
                "navigation("
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
                        Optional.of(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT),
                        NAVIGATION
                )
        );
    }

    @Test
    public void testDifferentNavigation() {
        this.checkNotEquals(
                SpreadsheetViewportSelection.with(
                        SELECTION,
                        Optional.of(
                                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
                        ),
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
                SpreadsheetSelection.parseCell("A1")
                        .setAnchor(SpreadsheetViewportSelection.NO_ANCHOR),
                "cell A1" + EOL
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRow("12")
                        .setAnchor(SpreadsheetViewportSelection.NO_ANCHOR),
                "row 12" + EOL
        );
    }

    @Test
    public void testTreePrintWithAnchor() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(Optional.of(SpreadsheetViewportSelectionAnchor.TOP)),
                "row-range 12:34 TOP" + EOL
        );
    }

    @Test
    public void testTreePrintNonRangeAndNavigation() {
        this.treePrintAndCheck(
                SpreadsheetViewportSelection.with(
                        SpreadsheetSelection.parseCell("A1"),
                        SpreadsheetViewportSelection.NO_ANCHOR,
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
                        Optional.of(
                                SpreadsheetViewportSelectionAnchor.TOP
                        ),
                        Optional.of(
                                SpreadsheetViewportSelectionNavigation.LEFT
                        )
                ),
                "row-range 12:34 TOP LEFT" + EOL
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonMarshallCell() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCell("B2")
                        .setAnchor(SpreadsheetViewportSelection.NO_ANCHOR)
        );
    }

    @Test
    public void testJsonMarshallCellRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3")
                        .setAnchor(
                                Optional.of(SpreadsheetViewportSelectionAnchor.TOP_LEFT)
                        )
        );
    }

    @Test
    public void testJsonMarshallColumn() {
        this.marshallRoundTripTwiceAndCheck(
                COLUMN.setAnchor(SpreadsheetViewportSelection.NO_ANCHOR)
        );
    }

    @Test
    public void testJsonMarshallColumnRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseColumnRange("B:C")
                        .setAnchor(
                                Optional.of(SpreadsheetViewportSelectionAnchor.LEFT)
                        )
        );
    }

    @Test
    public void testJsonMarshallRow() {
        this.marshallRoundTripTwiceAndCheck(
                COLUMN.setAnchor(SpreadsheetViewportSelection.NO_ANCHOR)
        );
    }

    @Test
    public void testJsonMarshallRowRange() {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetSelection.parseRowRange("12:34")
                        .setAnchor(
                                Optional.of(
                                        SpreadsheetViewportSelectionAnchor.TOP
                                )
                        )
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToStringNoAnchor() {
        this.toStringAndCheck(CELL.setAnchor(SpreadsheetViewportSelection.NO_ANCHOR), CELL.toString());
    }

    @Test
    public void testToStringWithAnchor() {
        this.toStringAndCheck(
                CELL_RANGE.setAnchor(Optional.of(SpreadsheetViewportSelectionAnchor.TOP_LEFT)),
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
                CELL_RANGE + " " + ANCHOR.get() + " " + NAVIGATION.get()
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
