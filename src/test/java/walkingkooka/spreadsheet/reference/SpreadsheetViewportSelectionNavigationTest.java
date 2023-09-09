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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.test.ParseStringTesting;

import java.util.List;
import java.util.Optional;

public final class SpreadsheetViewportSelectionNavigationTest implements ParseStringTesting<List<SpreadsheetViewportSelectionNavigation>>,
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
        this.parseStringFails(
                "EXTEND-RIGHT",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseLeft() {
        this.parseStringAndCheck(
                "left",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.LEFT
                )
        );
    }

    @Test
    public void testParseExtendRight() {
        this.parseStringAndCheck(
                "extend-right",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT
                )
        );
    }

    @Test
    public void testParseLeftRightUpExtendDown() {
        this.parseStringAndCheck(
                "left,right,up,extend-down",
                Lists.of(
                        SpreadsheetViewportSelectionNavigation.LEFT,
                        SpreadsheetViewportSelectionNavigation.RIGHT,
                        SpreadsheetViewportSelectionNavigation.UP,
                        SpreadsheetViewportSelectionNavigation.EXTEND_DOWN
                )
        );
    }

    // update..........................................................................................................

    @Test
    public void testUpdateColumnLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetSelection.parseColumn("B")

        );
    }

    @Test
    public void testUpdateRowLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseRow("12")
        );
    }

    @Test
    public void testUpdateRowDown() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.DOWN,
                SpreadsheetSelection.parseRow("12"),
                SpreadsheetSelection.parseRow("13")

        );
    }

    @Test
    public void testUpdateCellUp() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.UP,
                SpreadsheetSelection.parseCell("C3"),
                SpreadsheetSelection.parseCell("C2")

        );
    }

    @Test
    public void testUpdateColumnRangeUp() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.UP,
                SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorLeftNavigateLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
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
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                columnStore,
                SpreadsheetSelection.parseColumnRange("B:E").setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorRightNavigateLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.RIGHT,
                SpreadsheetSelection.parseColumn("A").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testUpdateColumnRangeAnchorLeftNavigateRight() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.RIGHT,
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
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                columnStore,
                SpreadsheetSelection.parseColumnRange("B:F").setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testUpdateColumnExtendLeft() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_LEFT,
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumnRange("B:C").setAnchor(SpreadsheetViewportSelectionAnchor.RIGHT)
        );
    }

    @Test
    public void testUpdateColumnExtendUp() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_UP,
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testUpdateRowRangeExtendUp() {
        final SpreadsheetRowStore rowStore = SpreadsheetRowStores.treeMap();
        rowStore.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));
        rowStore.save(SpreadsheetSelection.parseRow("4").row().setHidden(true));

        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_UP,
                SpreadsheetSelection.parseRowRange("5:6"),
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                rowStore,
                SpreadsheetSelection.parseRowRange("2:6").setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM)
        );
    }

    @Test
    public void testUpdateRowExtendRight() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT,
                SpreadsheetSelection.parseRow("3")
        );
    }

    @Test
    public void testUpdateRowRangeExtendDown() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_DOWN,
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
                SpreadsheetViewportSelectionNavigation.EXTEND_DOWN,
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
