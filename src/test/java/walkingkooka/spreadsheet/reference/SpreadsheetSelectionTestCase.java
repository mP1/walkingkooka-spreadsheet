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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class SpreadsheetSelectionTestCase<S extends SpreadsheetSelection> implements ClassTesting2<S>,
        HashCodeEqualsDefinedTesting2<S>,
        JsonNodeMarshallingTesting<S>,
        IsMethodTesting<S>,
        ParseStringTesting<S>,
        PredicateTesting2<S, SpreadsheetCellReference>,
        ToStringTesting<S>,
        TreePrintableTesting {

    SpreadsheetSelectionTestCase() {
        super();
    }

    // testCellRangeAndCheck............................................................................................

    final void countAndCheck(final String selection,
                             final int expected) {
        this.countAndCheck(
                this.parseString(selection),
                expected
        );
    }

    final void countAndCheck(final S selection,
                             final int expected) {
        this.checkEquals(
                expected,
                selection.count(),
                () -> selection + " count"
        );
    }

    // isAll............................................................................................................

    final void isAllAndCheck(final String selection,
                             final boolean expected) {
        this.isAllAndCheck(
                this.parseString(selection),
                expected
        );
    }

    final void isAllAndCheck(final S selection,
                             final boolean expected) {
        this.checkEquals(
                expected,
                selection.isAll(),
                () -> selection + ".isAll"
        );
    }

    // isFirst..........................................................................................................

    final void isFirstAndCheck(final String selection,
                               final boolean expected) {
        this.isFirstAndCheck(
                this.parseString(selection),
                expected
        );
    }

    final void isFirstAndCheck(final S selection,
                               final boolean expected) {
        this.checkEquals(
                expected,
                selection.isFirst(),
                () -> selection + ".isFirst"
        );
    }

    // isLast...........................................................................................................

    final void isLastAndCheck(final String selection,
                              final boolean expected) {
        this.isLastAndCheck(
                this.parseString(selection),
                expected
        );
    }

    final void isLastAndCheck(final S selection,
                              final boolean expected) {
        this.checkEquals(
                expected,
                selection.isLast(),
                () -> selection + ".isLast"
        );
    }

    // toCellOrFail....................................................................................................

    final void toCellOrFailFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().toCellOrFail()
        );
    }

    final void toCellOrFailAndCheck(final SpreadsheetSelection selection,
                                    final SpreadsheetCellReference expected) {
        this.checkEquals(
                expected,
                selection.toCellOrFail()
        );
    }

    // testCellRangeAndCheck............................................................................................

    final void testCellRangeAndCheck(final String selection,
                                     final String range,
                                     final boolean expected) {
        this.testCellRangeAndCheck(
                this.parseString(selection),
                SpreadsheetSelection.parseCellRange(range),
                expected
        );
    }

    final void testCellRangeAndCheck(final S selection,
                                     final SpreadsheetCellRange range,
                                     final boolean expected) {
        this.checkEquals(
                expected,
                selection.testCellRange(range),
                () -> selection + " testCellRange " + range
        );
    }

    final void testColumnAndCheck(final String selection,
                                  final String column,
                                  final boolean expected) {
        this.testColumnAndCheck(
                this.parseString(selection),
                column,
                expected
        );
    }

    final void testColumnAndCheck(final S selection,
                                  final String column,
                                  final boolean expected) {
        this.checkEquals(
                expected,
                selection.testColumn(SpreadsheetSelection.parseColumn(column)),
                selection + ".testColumn(" + column + ")"
        );
    }

    final void testRowAndCheck(final String selection,
                               final String row,
                               final boolean expected) {
        this.testRowAndCheck(
                this.parseString(selection),
                row,
                expected
        );
    }

    final void testRowAndCheck(final S selection,
                               final String row,
                               final boolean expected) {
        this.checkEquals(
                expected,
                selection.testRow(SpreadsheetSelection.parseRow(row)),
                selection + ".testRow(" + row + ")"
        );
    }

    // toRelative.......................................................................................................

    final void toRelativeAndCheck(final S selection) {
        this.toRelativeAndCheck(selection, selection);
    }

    final void toRelativeAndCheck(final S selection,
                                  final S expected) {
        if (expected.equals(selection)) {
            assertSame(
                    expected,
                    selection.toRelative(),
                    selection::toString
            );
        } else {
            this.checkEquals(
                    expected,
                    selection.toRelative(),
                    selection::toString
            );
        }
    }

    // defaultAnchor....................................................................................................

    @Test
    final public void testDefaultAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportSelectionAnchor anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");
    }

    @Test
    final public void testDefaultAnchorThenSetAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportSelectionAnchor anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");

        final SpreadsheetViewportSelection viewportSelection = selection.setAnchor(anchor);
        this.checkEquals(anchor, viewportSelection.anchor(), "anchor");
        this.checkEquals(selection, viewportSelection.selection(), "selection");
    }

    // isHidden.........................................................................................................

    final void isHiddenAndCheck(final String selection,
                                final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                final Predicate<SpreadsheetRowReference> hiddenRowTester,
                                final boolean expected) {
        this.isHiddenAndCheck(
                this.parseString(selection),
                hiddenColumnTester,
                hiddenRowTester,
                expected
        );
    }

    final void isHiddenAndCheck(final S selection,
                                final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                                final Predicate<SpreadsheetRowReference> hiddenRowTester,
                                final boolean expected) {
        this.checkEquals(
                expected,
                selection.isHidden(hiddenColumnTester, hiddenRowTester),
                () -> "isHidden " + selection
        );
    }

    // left.............................................................................................................

    final void leftAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore) {
        this.leftAndCheck(
                selection,
                columnStore,
                this.rowStore()
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetRowStore rowStore) {
        this.leftAndCheck(
                selection,
                this.columnStore(),
                rowStore
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore) {
        this.leftAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore) {
        this.leftAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void leftAndCheck(final String selection,
                             final String expected) {
        this.leftAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                this.parseString(expected)
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final String expected) {
        this.leftAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected)
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final String expected) {
        this.leftAndCheck(
                selection,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.leftAndCheck(
                selection,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.leftAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final String expected) {
        this.leftAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.leftAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void leftAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.leftAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                expected
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore) {
        this.leftAndCheck(
                selection,
                columnStore,
                Optional.empty()
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetRowStore rowStore) {
        this.leftAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                this.columnStore(),
                rowStore,
                Optional.empty()
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                columnStore,
                Optional.of(expected)
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                expected
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.leftAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                expected
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.leftAndCheck(
                selection,
                anchor,
                columnStore,
                Optional.of(expected)
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.leftAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void leftAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.left(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate left"
        );
    }

    // up.............................................................................................................
    final void upAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore) {
        this.upAndCheck(
                selection,
                columnStore,
                this.rowStore()
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetRowStore rowStore) {
        this.upAndCheck(
                selection,
                this.columnStore(),
                rowStore
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore) {
        this.upAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore) {
        this.upAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void upAndCheck(final String selection,
                             final String expected) {
        this.upAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                this.parseString(expected)
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final String expected) {
        this.upAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected)
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final String expected) {
        this.upAndCheck(
                selection,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.upAndCheck(
                selection,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.upAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final String expected) {
        this.upAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.upAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void upAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.upAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                expected
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore) {
        this.upAndCheck(
                selection,
                columnStore,
                Optional.empty()
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetRowStore rowStore) {
        this.upAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                this.columnStore(),
                rowStore,
                Optional.empty()
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                columnStore,
                Optional.of(expected)
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                expected
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.upAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                expected
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.upAndCheck(
                selection,
                anchor,
                columnStore,
                Optional.of(expected)
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.upAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void upAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.up(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate up"
        );
    }
    // right.............................................................................................................

    final void rightAndCheck(final String selection,
                            final SpreadsheetColumnStore columnStore) {
        this.rightAndCheck(
                selection,
                columnStore,
                this.rowStore()
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetRowStore rowStore) {
        this.rightAndCheck(
                selection,
                this.columnStore(),
                rowStore
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore) {
        this.rightAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore) {
        this.rightAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void rightAndCheck(final String selection,
                            final String expected) {
        this.rightAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                this.parseString(expected)
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final String expected) {
        this.rightAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected)
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetColumnStore columnStore,
                            final String expected) {
        this.rightAndCheck(
                selection,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetRowStore rowStore,
                            final String expected) {
        this.rightAndCheck(
                selection,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore,
                            final String expected) {
        this.rightAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final String expected) {
        this.rightAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetRowStore rowStore,
                            final String expected) {
        this.rightAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void rightAndCheck(final String selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore,
                            final String expected) {
        this.rightAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                expected
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetColumnStore columnStore) {
        this.rightAndCheck(
                selection,
                columnStore,
                Optional.empty()
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetRowStore rowStore) {
        this.rightAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                this.columnStore(),
                rowStore,
                Optional.empty()
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                columnStore,
                Optional.of(expected)
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore,
                            final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                expected
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore,
                            final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetColumnStore columnStore,
                            final Optional<SpreadsheetSelection> expected) {
        this.rightAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                expected
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetSelection expected) {
        this.rightAndCheck(
                selection,
                anchor,
                columnStore,
                Optional.of(expected)
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final Optional<SpreadsheetSelection> expected) {
        this.rightAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void rightAndCheck(final S selection,
                            final SpreadsheetViewportSelectionAnchor anchor,
                            final SpreadsheetColumnStore columnStore,
                            final SpreadsheetRowStore rowStore,
                            final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.right(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate right"
        );
    }
    // down.............................................................................................................
    final void downAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore) {
        this.downAndCheck(
                selection,
                columnStore,
                this.rowStore()
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetRowStore rowStore) {
        this.downAndCheck(
                selection,
                this.columnStore(),
                rowStore
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore) {
        this.downAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore) {
        this.downAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void downAndCheck(final String selection,
                             final String expected) {
        this.downAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                this.parseString(expected)
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final String expected) {
        this.downAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected)
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final String expected) {
        this.downAndCheck(
                selection,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.downAndCheck(
                selection,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.downAndCheck(
                this.parseString(selection),
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final String expected) {
        this.downAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.downAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore,
                expected
        );
    }

    final void downAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final String expected) {
        this.downAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expected)
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                expected
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore) {
        this.downAndCheck(
                selection,
                columnStore,
                Optional.empty()
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetRowStore rowStore) {
        this.downAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                this.columnStore(),
                rowStore,
                Optional.empty()
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                columnStore,
                Optional.of(expected)
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                expected
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetColumnStore columnStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.downAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                expected
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetSelection expected) {
        this.downAndCheck(
                selection,
                anchor,
                columnStore,
                Optional.of(expected)
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.downAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void downAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final SpreadsheetColumnStore columnStore,
                             final SpreadsheetRowStore rowStore,
                             final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.down(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate down"
        );
    }

    private SpreadsheetColumnStore columnStore() {
        return SpreadsheetColumnStores.treeMap();
    }

    private SpreadsheetRowStore rowStore() {
        return SpreadsheetRowStores.treeMap();
    }

    // extendRange......................................................................................................

    final void extendRangeAndCheck(final String selection,
                                   final String moved) {
        this.extendRangeAndCheck(
                selection,
                moved,
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final SpreadsheetViewportSelectionAnchor anchor) {
        final S parsed = this.parseString(selection);

        this.extendRangeAndCheck(
                parsed,
                this.parseString(moved).simplify(),
                anchor,
                parsed
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final String expected) {
        this.extendRangeAndCheck(
                selection,
                moved,
                SpreadsheetViewportSelectionAnchor.NONE,
                expected
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String expected) {
        this.extendRangeAndCheck(
                this.parseString(selection),
                this.parseString(moved).simplify(),
                anchor,
                this.parseRange(expected)
        );
    }

    final void extendRangeAndCheck(final S selection,
                                   final SpreadsheetSelection moved,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetSelection expected) {
        this.extendRangeAndCheck(
                selection,
                Optional.of(moved),
                anchor,
                Optional.of(expected)
        );
    }

    final void extendRangeAndCheck(final S selection,
                                   final Optional<SpreadsheetSelection> moved,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final Optional<SpreadsheetSelection> expected) {
        if(moved.isPresent()) {
            this.checkEquals(
                    true,
                    moved.map(m -> m instanceof SpreadsheetCellReference || m instanceof SpreadsheetColumnReference || m instanceof SpreadsheetRowReference).get(),
                    () -> moved + " must be either cell/column/row"
            );
        }
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.extendRange(Cast.to(moved), anchor),
                () -> selection + " extendRange " + moved + " " + anchor
        );
    }

    abstract SpreadsheetSelection parseRange(final String range);

    // extendLeft.......................................................................................................

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetRowStore rowStore) {
        this.extendLeftAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore) {
        this.extendLeftAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final String expectedSelection) {
        this.extendLeftAndCheck(
                selection,
                this.columnStore(),
                this.rowStore(),
                expectedSelection
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final String expectedSelection) {
        this.extendLeftAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                this.parseString(expectedSelection)
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendLeftAndCheck(
                selection,
                anchor,
                this.columnStore(),
                this.rowStore(),
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendLeftAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendLeftAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendLeftAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expectedSelection)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendLeftAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final SpreadsheetViewportSelection expected) {
        this.extendLeftAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void extendLeftAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendLeft(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate extendLeft"
        );
    }

    // extendUp.......................................................................................................

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore) {
        this.extendUpAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore) {
        this.extendUpAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore()
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore) {
        this.extendUpAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendUpAndCheck(final String selection,
                                final String expectedSelection) {
        this.extendUpAndCheck(
                selection,
                this.columnStore(),
                this.rowStore(),
                expectedSelection
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore,
                                final String expectedSelection) {
        this.extendUpAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                this.parseString(expectedSelection)
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final String expectedSelection,
                                final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendUpAndCheck(
                selection,
                anchor,
                this.columnStore(),
                this.rowStore(),
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetRowStore rowStore,
                                final String expectedSelection,
                                final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendUpAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore,
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendUpAndCheck(final String selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore,
                                final String expectedSelection,
                                final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendUpAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expectedSelection)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendUpAndCheck(final S selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore,
                                final SpreadsheetViewportSelection expected) {
        this.extendUpAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void extendUpAndCheck(final S selection,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetColumnStore columnStore,
                                final SpreadsheetRowStore rowStore,
                                final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendUp(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate extendUp"
        );
    }

    // extendRight.......................................................................................................

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetRowStore rowStore) {
        this.extendRightAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetRowStore rowStore) {
        this.extendRightAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetRowStore rowStore) {
        this.extendRightAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final String expectedSelection) {
        this.extendRightAndCheck(
                selection,
                this.columnStore(),
                this.rowStore(),
                expectedSelection
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetRowStore rowStore,
                                   final String expectedSelection) {
        this.extendRightAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                this.parseString(expectedSelection)
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String expectedSelection,
                                   final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendRightAndCheck(
                selection,
                anchor,
                this.columnStore(),
                this.rowStore(),
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final String expectedSelection,
                                   final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendRightAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendRightAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetRowStore rowStore,
                                   final String expectedSelection,
                                   final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendRightAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expectedSelection)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetSelection expectedSelection) {
        this.extendRightAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                this.columnStore(),
                this.rowStore(),
                expectedSelection.simplify()
                        .setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetViewportSelection expected) {
        this.extendRightAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetViewportSelection expected) {
        this.extendRightAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetRowStore rowStore,
                                   final SpreadsheetViewportSelection expected) {
        this.extendRightAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void extendRightAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetColumnStore columnStore,
                                   final SpreadsheetRowStore rowStore,
                                   final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendRight(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate extendRight"
        );
    }

    // extendDown.......................................................................................................

    final void extendDownAndCheck(final String selection) {
        this.extendDownAndCheck(
                selection,
                this.columnStore(),
                this.rowStore()
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore) {
        this.extendDownAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore) {
        this.extendDownAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore()
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore) {
        this.extendDownAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                Optional.empty()
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final String expectedSelection) {
        this.extendDownAndCheck(
                selection,
                this.columnStore(),
                this.rowStore(),
                expectedSelection
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final String expectedSelection) {
        this.extendDownAndCheck(
                this.parseString(selection),
                SpreadsheetViewportSelectionAnchor.NONE,
                columnStore,
                rowStore,
                this.parseString(expectedSelection)
                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendDownAndCheck(
                selection,
                anchor,
                this.columnStore(),
                this.rowStore(),
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetRowStore rowStore,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendDownAndCheck(
                selection,
                anchor,
                this.columnStore(),
                rowStore,
                expectedSelection,
                expectedAnchor
        );
    }

    final void extendDownAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final String expectedSelection,
                                  final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendDownAndCheck(
                this.parseString(selection),
                anchor,
                columnStore,
                rowStore,
                this.parseRange(expectedSelection)
                        .simplify()
                        .setAnchor(expectedAnchor)
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetSelection expectedSelection) {
        this.extendDownAndCheck(
                selection,
                SpreadsheetViewportSelectionAnchor.NONE,
                this.columnStore(),
                this.rowStore(),
                expectedSelection.simplify()
                        .setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetViewportSelection expected) {
        this.extendDownAndCheck(
                selection,
                anchor,
                columnStore(),
                expected
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetViewportSelection expected) {
        this.extendDownAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final SpreadsheetViewportSelection expected) {
        this.extendDownAndCheck(
                selection,
                anchor,
                columnStore,
                rowStore,
                Optional.of(expected)
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final Optional<SpreadsheetViewportSelection> expected) {
        this.extendDownAndCheck(
                selection,
                anchor,
                columnStore,
                this.rowStore(),
                expected
        );
    }

    final void extendDownAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetColumnStore columnStore,
                                  final SpreadsheetRowStore rowStore,
                                  final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendDown(anchor, columnStore, rowStore),
                () -> selection + " anchor=" + anchor + " navigate extendDown"
        );
    }

    // focused.........................................................................................................

    @Test
    public final void testFocusedNullAnchorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().focused(null)
        );
    }

    final void focusedAndCheck(final String selection,
                               final SpreadsheetViewportSelectionAnchor anchor,
                               final String expected) {
        this.focusedAndCheck(
                this.parseString(selection),
                anchor,
                this.parseString(expected).simplify()
        );
    }

    final void focusedAndCheck(final S selection,
                               final SpreadsheetViewportSelectionAnchor anchor,
                               final SpreadsheetSelection expected) {
        this.checkEquals(
                1,
                expected.count(),
                () -> "Expected " + expected + " count must be one"
        );
        this.checkEquals(
                expected,
                selection.focused(anchor),
                () -> selection + " anchor " + anchor + " focused"
        );
    }

    // simplify.........................................................................................................

    final void simplifyAndCheck(final String selection) {
        this.simplifyAndCheck(
                this.parseString(selection)
        );
    }

    final void simplifyAndCheck(final S selection) {
        this.simplifyAndCheck(
                selection,
                selection
        );
    }

    final void simplifyAndCheck(final String selection,
                                final SpreadsheetSelection expected) {
        this.simplifyAndCheck(
                this.parseString(selection),
                expected
        );
    }

    final void simplifyAndCheck(final S selection,
                                final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                selection.simplify(),
                () -> "simplify " + selection
        );
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindSame() {
        final S selection = this.createSelection();

        this.equalsIgnoreReferenceKindAndCheck(
                selection,
                selection,
                true
        );
    }

    final void equalsIgnoreReferenceKindAndCheck(final String left,
                                                 final String right,
                                                 final boolean expected) {
        this.equalsIgnoreReferenceKindAndCheck(
                this.parseString(left),
                this.parseString(right),
                expected
        );
    }

    final void equalsIgnoreReferenceKindAndCheck(final S left,
                                                 final S right,
                                                 final boolean expected) {
        this.checkEquals(
                expected,
                left.equalsIgnoreReferenceKind(right),
                () -> left + " equalsIgnoreReferenceKind " + right
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json..............................................................................................................

    @Test
    public final void testJsonNodeMarshall() {
        final S selection = this.createSelection();
        this.marshallAndCheck(selection, JsonNode.string(selection.toString()));
    }

    abstract S createSelection();

    // HashCodeEqualsDefinedTesting.....................................................................................

    @Override
    public final S createObject() {
        return this.createSelection();
    }

    // IsMethodTesting...................................................................................................

    @Override
    public final S createIsMethodObject() {
        return this.createSelection();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return "";//ExpressionReference.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.setContains(
                Sets.of("isAll", "isFirst", "isLast", "isHidden")
        );
    }

    // JsonNodeTesting..................................................................................................

    @Override
    public final S createJsonNodeMarshallingValue() {
        return this.createSelection();
    }

    // ParsingTesting...................................................................................................

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        assertTrue(
                IllegalArgumentException.class.isAssignableFrom(expected),
                expected.getName() + " not a sub class of " + IllegalArgumentException.class
        );
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        assertTrue(
                expected instanceof IllegalArgumentException,
                expected.getClass().getName() + "=" + expected + " not a sub class of " + IllegalArgumentException.class
        );
        return expected;
    }

    // PredicateTesting.................................................................................................

    @Override
    public final S createPredicate() {
        return this.createSelection();
    }

    public final void testTypeNaming() {
        // nop
    }

    @Override
    public final String typeNamePrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String typeNameSuffix() {
        throw new UnsupportedOperationException();
    }
}
