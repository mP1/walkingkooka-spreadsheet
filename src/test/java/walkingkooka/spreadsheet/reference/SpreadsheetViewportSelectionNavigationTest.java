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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportSelectionNavigationTest implements ClassTesting<SpreadsheetViewportSelectionNavigation> {

    // from............................................................................................................

    @Test
    public void testFromNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportSelectionNavigation.from(null)
        );
    }

    @Test
    public void testFromEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionNavigation.from("")
        );
    }

    @Test
    public void testFromUnknownFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionNavigation.from("!invalid")
        );
    }

    @Test
    public void testFromUnknownFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionNavigation.from("EXTEND-RIGHT")
        );
    }

    @Test
    public void testLeft() {
        this.fromAndCheck(
                "left",
                SpreadsheetViewportSelectionNavigation.LEFT
        );
    }

    @Test
    public void testExtendRight() {
        this.fromAndCheck(
                "extend-right",
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT
        );
    }

    private void fromAndCheck(final String text,
                              final SpreadsheetViewportSelectionNavigation expected) {
        this.checkEquals(
                expected,
                SpreadsheetViewportSelectionNavigation.from(text),
                () -> "from " + CharSequences.quoteAndEscape(text)
        );
    }

    // perform..........................................................................................................

    @Test
    public void testPerformColumnLeft() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetSelection.parseColumn("B")

        );
    }

    @Test
    public void testPerformRowLeft() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseRow("12")
        );
    }

    @Test
    public void testPerformRowDown() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.DOWN,
                SpreadsheetSelection.parseRow("12"),
                SpreadsheetSelection.parseRow("13")

        );
    }

    @Test
    public void testPerformCellUp() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.UP,
                SpreadsheetSelection.parseCell("C3"),
                SpreadsheetSelection.parseCell("C2")

        );
    }

    @Test
    public void testPerformColumnRangeUp() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.UP,
                SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testPerformColumnRangeAnchorLeftNavigateLeft() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumn("B").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testPerformColumnRangeAnchorExtendRightNavigateLeftSkipHidden() {
        final SpreadsheetColumnStore columnStore = SpreadsheetColumnStores.treeMap();
        columnStore.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));
        columnStore.save(SpreadsheetSelection.parseColumn("D").column().setHidden(true));

        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                columnStore,
                SpreadsheetSelection.parseColumnRange("B:E").setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testPerformColumnRangeAnchorRightNavigateLeft() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.LEFT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.RIGHT,
                SpreadsheetSelection.parseColumn("A").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testPerformColumnRangeAnchorLeftNavigateRight() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.RIGHT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumn("D").setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testPerformColumnRangeAnchorLeftNavigateExtendRightSkipHidden() {
        final SpreadsheetColumnStore columnStore = SpreadsheetColumnStores.treeMap();
        columnStore.save(SpreadsheetSelection.parseColumn("D").column().setHidden(true));
        columnStore.save(SpreadsheetSelection.parseColumn("E").column().setHidden(true));

        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT,
                SpreadsheetSelection.parseColumnRange("B:C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                columnStore,
                SpreadsheetSelection.parseColumnRange("B:F").setAnchor(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Test
    public void testPerformColumnExtendLeft() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_LEFT,
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetSelection.parseColumnRange("B:C").setAnchor(SpreadsheetViewportSelectionAnchor.RIGHT)
        );
    }

    @Test
    public void testPerformColumnExtendUp() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_UP,
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testPerformRowRangeExtendUp() {
        final SpreadsheetRowStore rowStore = SpreadsheetRowStores.treeMap();
        rowStore.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));
        rowStore.save(SpreadsheetSelection.parseRow("4").row().setHidden(true));

        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_UP,
                SpreadsheetSelection.parseRowRange("5:6"),
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                rowStore,
                SpreadsheetSelection.parseRowRange("2:6").setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM)
        );
    }

    @Test
    public void testPerformRowExtendRight() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_RIGHT,
                SpreadsheetSelection.parseRow("3")
        );
    }

    @Test
    public void testPerformRowRangeExtendDown() {
        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_DOWN,
                SpreadsheetSelection.parseRowRange("3:4"),
                SpreadsheetViewportSelectionAnchor.TOP,
                SpreadsheetSelection.parseRowRange("3:5").setAnchor(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    @Test
    public void testPerformRowRangeExtendDownSkipsHiddenRows() {
        final SpreadsheetRowStore rowStore = SpreadsheetRowStores.treeMap();
        rowStore.save(SpreadsheetSelection.parseRow("5").row().setHidden(true));
        rowStore.save(SpreadsheetSelection.parseRow("6").row().setHidden(true));

        this.performAndCheck(
                SpreadsheetViewportSelectionNavigation.EXTEND_DOWN,
                SpreadsheetSelection.parseRowRange("3:4"),
                SpreadsheetViewportSelectionAnchor.TOP,
                rowStore,
                SpreadsheetSelection.parseRowRange("3:7").setAnchor(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection) {
        this.performAndCheck(
                navigation,
                selection,
                selection
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection,
            final SpreadsheetSelection expected) {
        this.performAndCheck(
                navigation,
                selection,
                expected.setAnchor(expected.defaultAnchor())
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection,
            final SpreadsheetViewportSelection expected) {
        this.performAndCheck(
                navigation,
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection,
            final SpreadsheetViewportSelectionAnchor anchor,
            final SpreadsheetViewportSelection expected) {
        this.performAndCheck(
                navigation,
                selection,
                anchor,
                SpreadsheetColumnStores.treeMap(),
                SpreadsheetRowStores.treeMap(),
                expected
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection,
            final SpreadsheetViewportSelectionAnchor anchor,
            final SpreadsheetColumnStore columnStore,
            final SpreadsheetViewportSelection expected) {
        this.performAndCheck(
                navigation,
                selection,
                anchor,
                columnStore,
                SpreadsheetRowStores.fake(),
                expected
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection,
            final SpreadsheetViewportSelectionAnchor anchor,
            final SpreadsheetRowStore rowStore,
            final SpreadsheetViewportSelection expected) {
        this.performAndCheck(
               navigation,
                selection,
                anchor,
                SpreadsheetColumnStores.fake(),
                rowStore,
                expected
        );
    }

    private void performAndCheck(
            final SpreadsheetViewportSelectionNavigation navigation,
            final SpreadsheetSelection selection,
            final SpreadsheetViewportSelectionAnchor anchor,
            final SpreadsheetColumnStore columnStore,
            final SpreadsheetRowStore rowStore,
            final SpreadsheetViewportSelection expected) {
        this.checkEquals(
                expected,
                navigation.perform(selection, anchor, columnStore, rowStore),
                () -> navigation + " perform " + selection + " " + anchor
        );
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
