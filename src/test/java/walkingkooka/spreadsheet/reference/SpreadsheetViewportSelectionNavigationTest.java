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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportSelectionNavigationTest implements ParseStringTesting<List<SpreadsheetViewportSelectionNavigation>>,
        TreePrintableTesting,
        ClassTesting<SpreadsheetViewportSelectionNavigation> {

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseUnknownFails() {
        this.parseStringFails(
                "!invalid",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseUnknownFails2() {
        final String text = "EXTEND-RIGHT";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        0
                )
        );
    }

    @Test
    public void testParseUnknownFails3() {
        final String text = "right row";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        "right ".length()
                )
        );
    }

    @Test
    public void testParseInvalidValue() {
        final String text = "right 123A";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        "right 123".length()
                )
        );
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                Lists.empty()
        );
    }

    @Test
    public void testParseLeftColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.leftColumn()
        );
    }

    @Test
    public void testParseRightColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.rightColumn()
        );
    }

    @Test
    public void testParseUpRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.upRow()
        );
    }

    @Test
    public void testParseDownRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.downRow()
        );
    }

    @Test
    public void testParseExtendLeftColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.extendLeftColumn()
        );
    }

    @Test
    public void testParseExtendRightColumn() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.extendRightColumn()
        );
    }

    @Test
    public void testParseExtendUpRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.extendUpRow()
        );
    }

    @Test
    public void testParseExtendDownRow() {
        this.parseStringAndCheck0(
                SpreadsheetViewportSelectionNavigation.extendDownRow()
        );
    }

    private void parseStringAndCheck0(final SpreadsheetViewportSelectionNavigation navigation) {
        this.parseStringAndCheck(
                navigation.text(),
                Lists.of(
                        navigation
                )
        );
    }

    @Test
    public void testParseLeftColumnRightColumnUpRowDownRow() {
        this.parseStringAndCheck(
                "left column,right column,up row,down row",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.upRow(),
                        SpreadsheetViewportSelectionNavigation.downRow()
                )
        );
    }

    @Test
    public void testParseExtendLeftColumnExtendRightColumnExtendUpRowExtendDownRow() {
        this.parseStringAndCheck(
                "extend-left column,extend-right column,extend-up row,extend-down row",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                        SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                        SpreadsheetViewportSelectionNavigation.extendUpRow(),
                        SpreadsheetViewportSelectionNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testParseLeftPixelRightPixelUpPixelDownPixel() {
        this.parseStringAndCheck(
                "left 10,right 20,up 30,down 40",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftPixel(10),
                        SpreadsheetViewportSelectionNavigation.rightPixel(20),
                        SpreadsheetViewportSelectionNavigation.upPixel(30),
                        SpreadsheetViewportSelectionNavigation.downPixel(40)
                )
        );
    }

    @Test
    public void testParseExtendLeftPixelExtendRightPixelExtendUpPixelExtendDownPixel() {
        this.parseStringAndCheck(
                "extend-left 10,extend-right 20,extend-up 30,extend-down 40",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftPixel(10),
                        SpreadsheetViewportSelectionNavigation.extendRightPixel(20),
                        SpreadsheetViewportSelectionNavigation.extendUpPixel(30),
                        SpreadsheetViewportSelectionNavigation.extendDownPixel(40)
                )
        );
    }

    // update..........................................................................................................

    @Test
    public void testUpdateColumnLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.leftColumn(),
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetSelection.parseColumn("B")

        );
    }

    @Test
    public void testUpdateRowLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.leftColumn(),
                SpreadsheetSelection.parseRow("12")
        );
    }

    @Test
    public void testUpdateRowDown() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.downRow(),
                SpreadsheetSelection.parseRow("12"),
                SpreadsheetSelection.parseRow("13")

        );
    }

    @Test
    public void testUpdateCellUp() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.upRow(),
                SpreadsheetSelection.parseCell("C3"),
                SpreadsheetSelection.parseCell("C2")

        );
    }

    @Test
    public void testUpdateColumnRangeUp() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.upRow(),
                SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorLeftNavigateLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.leftColumn(),
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumn("B").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorExtendRightNavigateLeftSkipHidden() {
        final SpreadsheetColumnStore columnStore = SpreadsheetColumnStores.treeMap();
        columnStore.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));
        columnStore.save(SpreadsheetSelection.parseColumn("D").column().setHidden(true));

        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                columnStore,
                SpreadsheetSelection.parseColumnRange("B:E").setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorRightNavigateLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.leftColumn(),
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.RIGHT,
                SpreadsheetSelection.parseColumn("A").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorLeftNavigateRight() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.rightColumn(),
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumn("D").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorLeftNavigateExtendRightSkipHidden() {
        final SpreadsheetColumnStore columnStore = SpreadsheetColumnStores.treeMap();
        columnStore.save(SpreadsheetSelection.parseColumn("D").column().setHidden(true));
        columnStore.save(SpreadsheetSelection.parseColumn("E").column().setHidden(true));

        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                columnStore,
                SpreadsheetSelection.parseColumnRange("B:F").setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testUpdateColumnExtendLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumnRange("B:C").setAnchor(SpreadsheetViewportSelectionAnchor.RIGHT)
        );
    }

    @Test
    public void testUpdateColumnExtendUp() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendUpRow(),
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testUpdateRowRangeExtendUp() {
        final SpreadsheetRowStore rowStore = SpreadsheetRowStores.treeMap();
        rowStore.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));
        rowStore.save(SpreadsheetSelection.parseRow("4").row().setHidden(true));

        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendUpRow(),
                SpreadsheetSelection.parseRowRange("5:6"),
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                rowStore,
                SpreadsheetSelection.parseRowRange("2:6").setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM)
        );
    }

    @Test
    public void testUpdateRowExtendRight() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                SpreadsheetSelection.parseRow("3")
        );
    }

    @Test
    public void testUpdateRowRangeExtendDown() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendDownRow(),
                SpreadsheetSelection.parseRowRange("3:4"),
                SpreadsheetViewportSelectionAnchor.TOP,
                SpreadsheetSelection.parseRowRange("3:5").setAnchor(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    @Test
    public void testUpdateRowRangeExtendDownSkipsHiddenRows() {
        final SpreadsheetRowStore rowStore = SpreadsheetRowStores.treeMap();
        rowStore.save(SpreadsheetSelection.parseRow("5").row().setHidden(true));
        rowStore.save(SpreadsheetSelection.parseRow("6").row().setHidden(true));

        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendDownRow(),
                SpreadsheetSelection.parseRowRange("3:4"),
                SpreadsheetViewportSelectionAnchor.TOP,
                rowStore,
                SpreadsheetSelection.parseRowRange("3:7").setAnchor(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection) {
        this.updateAndCheck(
                navigation,
                selection,
                selection
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                expected.setAnchor(expected.defaultAnchor())
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetViewportSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetViewportSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                anchor,
                SpreadsheetColumnStores.treeMap(),
                SpreadsheetRowStores.treeMap(),
                expected
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetViewportSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                anchor,
                columnStore,
                SpreadsheetRowStores.fake(),
                expected
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetRowStore rowStore,
                                final SpreadsheetViewportSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                anchor,
                SpreadsheetColumnStores.fake(),
                rowStore,
                expected
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore,
                                final SpreadsheetViewportSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    private void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                                final SpreadsheetSelection selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore,
                                final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                navigation.update(selection, anchor, columnStore, rowStore),
                () -> navigation + " update " + selection + " " + anchor
        );
    }

    // compact..........................................................................................................

    @Test
    public void testCompactNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelectionNavigation.compact(null)
        );
    }

    @Test
    public void testCompactEmpty() {
        this.compactAndCheck(
                Lists.empty()
        );
    }

    @Test
    public void testCompactOne() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.upRow()
        );
    }

    @Test
    public void testCompactOne2() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.leftColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites2() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.rightColumn(),
                SpreadsheetViewportSelectionNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites3() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.upRow(),
                SpreadsheetViewportSelectionNavigation.extendUpRow()
        );
    }

    @Test
    public void testCompactManyNoOpposites4() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.downRow(),
                SpreadsheetViewportSelectionNavigation.extendUpRow()
        );
    }

    @Test
    public void testCompactManyNoOpposites5() {
        this.compactAndCheck(
                SpreadsheetViewportSelectionNavigation.leftColumn(),
                SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                SpreadsheetViewportSelectionNavigation.upRow(),
                SpreadsheetViewportSelectionNavigation.extendDownRow()
        );
    }

    @Test
    public void testCompactLeftRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn()
                )
        );
    }

    @Test
    public void testCompactUpDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.upRow(),
                        SpreadsheetViewportSelectionNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactRightLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.leftColumn()
                )
        );
    }

    @Test
    public void testCompactDownUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.downRow(),
                        SpreadsheetViewportSelectionNavigation.upRow()
                )
        );
    }


    @Test
    public void testCompactExtendLeftExtendRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                        SpreadsheetViewportSelectionNavigation.extendRightColumn()
                )
        );
    }

    @Test
    public void testCompactExtendUpExtendDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendUpRow(),
                        SpreadsheetViewportSelectionNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testCompactExtendRightExtendLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn()
                )
        );
    }

    @Test
    public void testCompactExtendDownExtendUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendDownRow(),
                        SpreadsheetViewportSelectionNavigation.extendUpRow()
                )
        );
    }

    @Test
    public void testCompactLeftRightLeftRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn()
                )
        );
    }

    @Test
    public void testCompactLeftRightLeftRightLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.leftColumn()
                ),
                SpreadsheetViewportSelectionNavigation.leftColumn()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn()
                ),
                SpreadsheetViewportSelectionNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.upRow()
                ),
                SpreadsheetViewportSelectionNavigation.upRow()
        );
    }

    @Test
    public void testCompactLeftUpRightDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.leftColumn(),
                        SpreadsheetViewportSelectionNavigation.upRow(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftExtendUpExtendRightExtendDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                        SpreadsheetViewportSelectionNavigation.extendUpRow(),
                        SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                        SpreadsheetViewportSelectionNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                        SpreadsheetViewportSelectionNavigation.upRow(),
                        SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                        SpreadsheetViewportSelectionNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                        SpreadsheetViewportSelectionNavigation.upRow(),
                        SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                        SpreadsheetViewportSelectionNavigation.downRow(),
                        SpreadsheetViewportSelectionNavigation.downRow()
                ),
                SpreadsheetViewportSelectionNavigation.downRow()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                        SpreadsheetViewportSelectionNavigation.upRow(),
                        SpreadsheetViewportSelectionNavigation.extendRightColumn(),
                        SpreadsheetViewportSelectionNavigation.downRow(),
                        SpreadsheetViewportSelectionNavigation.rightColumn(),
                        SpreadsheetViewportSelectionNavigation.extendLeftColumn()
                ),
                SpreadsheetViewportSelectionNavigation.rightColumn(),
                SpreadsheetViewportSelectionNavigation.extendLeftColumn()
        );
    }

    private void compactAndCheck(final SpreadsheetViewportSelectionNavigation... expected) {
        this.compactAndCheck(
                Lists.of(expected),
                expected
        );
    }


    private void compactAndCheck(final List<SpreadsheetViewportSelectionNavigation> in,
                                 final SpreadsheetViewportSelectionNavigation... expected) {
        this.compactAndCheck(
                in,
                Lists.of(expected)
        );
    }

    private void compactAndCheck(final List<SpreadsheetViewportSelectionNavigation> in,
                                 final List<SpreadsheetViewportSelectionNavigation> expected) {
        this.checkEquals(
                expected,
                SpreadsheetViewportSelectionNavigation.compact(in),
                () -> "compact " + in
        );
    }

    // ParseStringTesting...............................................................................................

    @Override
    public List<SpreadsheetViewportSelectionNavigation> parseString(final String text) {
        return SpreadsheetViewportSelectionNavigation.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportSelectionNavigation> type() {
        return SpreadsheetViewportSelectionNavigation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
