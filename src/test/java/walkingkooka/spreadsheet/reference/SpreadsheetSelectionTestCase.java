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
import walkingkooka.net.UrlFragment;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class SpreadsheetSelectionTestCase<S extends SpreadsheetSelection> implements ClassTesting2<S>,
        HashCodeEqualsDefinedTesting2<S>,
        HasTextTesting,
        JsonNodeMarshallingTesting<S>,
        IsMethodTesting<S>,
        ParseStringTesting<S>,
        PredicateTesting2<S, SpreadsheetSelection>,
        ToStringTesting<S>,
        TreePrintableTesting {

    SpreadsheetSelectionTestCase() {
        super();
    }

    // text.............................................................................................................

    final void textAndCheck(final String text) {
        this.textAndCheck(
                this.parseString(text),
                text
        );
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
                             final long expected) {
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

    // toCell...........................................................................................................

    final void toCellFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().toCell()
        );
    }

    final void toCellAndCheck(final SpreadsheetSelection selection,
                              final SpreadsheetCellReference expected) {
        this.checkEquals(
                expected,
                selection.toCell(),
                () -> selection + " toCell"
        );
    }

    // toCellRange.....................................................................................................

    final void toCellRangeWithNullFunctionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().toCellRange(null)
        );
    }

    final void toCellRangeAndCheck(final String selection,
                                   final String expected) {
        this.toCellRangeAndCheck(
                this.parseString(selection),
                SpreadsheetSelection.parseCellRange(expected)
        );
    }

    final void toCellRangeAndCheck(final SpreadsheetSelection selection,
                                   final SpreadsheetCellRange expected) {
        this.toCellRangeAndCheck(
                selection,
                (l) -> {
                    throw new UnsupportedOperationException(l.toString());
                },
                expected
        );
    }

    final void toCellRangeAndCheck(final SpreadsheetSelection selection,
                                   final Function<SpreadsheetLabelName, Optional<SpreadsheetCellRange>> labelToCellRange,
                                   final SpreadsheetCellRange expected) {
        this.toCellRangeAndCheck(
                selection,
                labelToCellRange,
                Optional.of(expected)
        );
    }

    final void toCellRangeAndCheck(final SpreadsheetSelection selection,
                                   final Function<SpreadsheetLabelName, Optional<SpreadsheetCellRange>> labelToCellRange,
                                   final Optional<SpreadsheetCellRange> expected) {
        this.checkEquals(
                expected,
                selection.toCellRange(labelToCellRange),
                () -> selection + " toCellRange"
        );
    }

    // toColumn.........................................................................................................

    final void toColumnFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().toColumn()
        );
    }

    final void toColumnAndCheck(final SpreadsheetSelection selection,
                                final SpreadsheetColumnReference expected) {
        this.checkEquals(
                expected,
                selection.toColumn(),
                () -> selection + " toColumn"
        );
    }

    // toColumnRange.........................................................................................................

    final void toColumnRangeFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().toColumnRange()
        );
    }

    final void toColumnRangeAndCheck(final SpreadsheetSelection selection,
                                     final SpreadsheetColumnReferenceRange expected) {
        this.checkEquals(
                expected,
                selection.toColumnRange(),
                () -> selection + " toColumnRange"
        );
    }

    // toRow.........................................................................................................

    final void toRowFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().toRow()
        );
    }

    final void toRowAndCheck(final SpreadsheetSelection selection,
                             final SpreadsheetRowReference expected) {
        this.checkEquals(
                expected,
                selection.toRow(),
                () -> selection + " toRow"
        );
    }

    // toRowRange.........................................................................................................

    final void toRowRangeFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().toRowRange()
        );
    }

    final void toRowRangeAndCheck(final SpreadsheetSelection selection,
                                  final SpreadsheetRowReferenceRange expected) {
        this.checkEquals(
                expected,
                selection.toRowRange(),
                () -> selection + " toRowRange"
        );
    }

    // testXXX..........................................................................................................

    @Test
    public final void testTestWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().test(null)
        );
    }

    @Test
    public final void testTestWithCellRangeFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().test(SpreadsheetSelection.parseCellRange("A1:B2"))
        );
    }

    @Test
    public final void testTestWithColumnRangeFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().test(SpreadsheetSelection.parseColumnRange("B:C"))
        );
    }

    @Test
    public final void testTestWithLabelFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().test(SpreadsheetSelection.labelName("Label123"))
        );
    }

    @Test
    public final void testTestWithRowRangeFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createSelection().test(SpreadsheetSelection.parseRowRange("4:5"))
        );
    }

    @Test
    final void testTestCellWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().testCell(null)
        );
    }

    final void testCellAndCheck(final String selection,
                                final String cell,
                                final boolean expected) {
        this.testCellAndCheck(
                this.parseString(selection),
                SpreadsheetSelection.parseCell(cell),
                expected
        );
    }

    final void testCellAndCheck(final S selection,
                                final SpreadsheetCellReference cell,
                                final boolean expected) {
        this.checkEquals(
                expected,
                selection.testCell(cell),
                () -> selection + " testCell " + cell
        );
    }

    @Test
    final void testTestCellRangeWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().testCellRange(null)
        );
    }

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

    @Test
    final void testTestColumnWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().testColumn(null)
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

    @Test
    final void testTestRowWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSelection().testRow(null)
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

    // setAnchor........................................................................................................

    @Test
    public final void testSetAnchorNullFails() {
        final S selection = this.createSelection();

        assertThrows(
                NullPointerException.class,
                () -> selection.setAnchor(null)
        );
    }

    @Test
    public final void testSetAnchorInvalidFails() {
        final S selection = this.createSelection();

        if (false == selection.isLabelName()) {
            for (final SpreadsheetViewportSelectionAnchor anchor : SpreadsheetViewportSelectionAnchor.values()) {
                if (selection.anchors().contains(anchor)) {
                    continue;
                }
                final IllegalArgumentException thrown = assertThrows(
                        IllegalArgumentException.class,
                        () -> selection.setAnchor(anchor)
                );
                this.checkEquals(
                        "Invalid anchor " +
                                anchor +
                                " for " +
                                selection +
                                ", valid anchors: " +
                                selection.anchors()
                                        .stream()
                                        .map(Enum::toString)
                                        .collect(Collectors.joining(", ")),
                        thrown.getMessage()
                );
            }
        }
    }

    @Test
    public final void testSetAnchorTryAllValid() {
        final S selection = this.createSelection();

        for (final SpreadsheetViewportSelectionAnchor anchor : SpreadsheetViewportSelectionAnchor.values()) {
            if (selection.anchors().contains(anchor)) {
                this.checkEquals(
                        SpreadsheetViewportSelection.with(
                                selection,
                                anchor,
                                SpreadsheetViewportSelection.NO_NAVIGATION
                        ),
                        selection.setAnchor(anchor),
                        () -> selection + ".setAnchor " + anchor
                );
            }
        }
    }

    // setDefaultAnchor.................................................................................................

    @Test
    public final void testSetDefaultAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportSelectionAnchor anchor = selection.defaultAnchor();

        this.checkEquals(
                selection.setAnchor(anchor),
                selection.setDefaultAnchor()
        );
    }

    // cellColumnOrRowText..............................................................................................

    final void cellColumnOrRowTextAndCheck(final String text) {
        this.cellColumnOrRowTextAndCheck(
                this.createSelection(),
                text
        );
    }

    final void cellColumnOrRowTextAndCheck(final SpreadsheetSelection selection,
                                           final String text) {
        this.checkEquals(
                text,
                selection.cellColumnOrRowText(),
                selection + " cellColumnOrRowText"
        );
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

    // leftColumn.......................................................................................................

    final void leftColumnAndCheck(final String selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String hiddenColumns,
                                  final String hiddenRows,
                                  final String expected) {
        this.leftColumnAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expected)
        );
    }

    final void leftColumnAndCheck(final S selection,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                  final Predicate<SpreadsheetRowReference> hiddenRows,
                                  final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.leftColumn(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate leftColumn"
        );
    }

    // upRow............................................................................................................

    final void upRowAndCheck(final String selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final String hiddenColumns,
                             final String hiddenRows,
                             final String expected) {
        this.upRowAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expected)
        );
    }

    final void upRowAndCheck(final S selection,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final Predicate<SpreadsheetColumnReference> hiddenColumns,
                             final Predicate<SpreadsheetRowReference> hiddenRows,
                             final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.upRow(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate upRow"
        );
    }
    // rightColumn.......................................................................................................

    final void rightColumnAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String hiddenColumns,
                                   final String hiddenRows,
                                   final String expected) {
        this.rightColumnAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expected)
        );
    }

    final void rightColumnAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                   final Predicate<SpreadsheetRowReference> hiddenRows,
                                   final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.rightColumn(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate rightColumn"
        );
    }
    // downRow..........................................................................................................

    final void downRowAndCheck(final String selection,
                               final SpreadsheetViewportSelectionAnchor anchor,
                               final String hiddenColumns,
                               final String hiddenRows,
                               final String expected) {
        this.downRowAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expected)
        );
    }

    final void downRowAndCheck(final S selection,
                               final SpreadsheetViewportSelectionAnchor anchor,
                               final Predicate<SpreadsheetColumnReference> hiddenColumns,
                               final Predicate<SpreadsheetRowReference> hiddenRows,
                               final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
                expected.map(SpreadsheetSelection::simplify),
                selection.downRow(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate downRow"
        );
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
        if (moved.isPresent()) {
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

    // extendLeftColumn.................................................................................................

    final void extendLeftColumnAndCheck(final String selection,
                                        final SpreadsheetViewportSelectionAnchor anchor,
                                        final String hiddenColumns,
                                        final String hiddenRows,
                                        final String expectedSelection,
                                        final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendLeftColumnAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expectedSelection).map(
                        s -> s.setAnchor(expectedAnchor)
                )
        );
    }

    final void extendLeftColumnAndCheck(final S selection,
                                        final SpreadsheetViewportSelectionAnchor anchor,
                                        final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                        final Predicate<SpreadsheetRowReference> hiddenRows,
                                        final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendLeftColumn(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate extendLeftColumn"
        );
    }

    // extendUpRow......................................................................................................

    final void extendUpRowAndCheck(final String selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String hiddenColumns,
                                   final String hiddenRows,
                                   final String expectedSelection,
                                   final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendUpRowAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expectedSelection).map(
                        s -> s.simplify()
                                .setAnchor(expectedAnchor)
                )
        );
    }

    final void extendUpRowAndCheck(final S selection,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                   final Predicate<SpreadsheetRowReference> hiddenRows,
                                   final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendUpRow(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate extendUpRow"
        );
    }

    // extendRightColumn................................................................................................

    final void extendRightColumnAndCheck(final String selection,
                                         final SpreadsheetViewportSelectionAnchor anchor,
                                         final String hiddenColumns,
                                         final String hiddenRows,
                                         final String expectedSelection,
                                         final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendRightColumnAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expectedSelection).map(
                        s -> s.setAnchor(expectedAnchor)
                )
        );
    }

    final void extendRightColumnAndCheck(final S selection,
                                         final SpreadsheetViewportSelectionAnchor anchor,
                                         final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                         final Predicate<SpreadsheetRowReference> hiddenRows,
                                         final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendRightColumn(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate extendRightColumn"
        );
    }

    // extendDownRow...................................................................................................

    final void extendDownRowAndCheck(final String selection,
                                     final SpreadsheetViewportSelectionAnchor anchor,
                                     final String hiddenColumns,
                                     final String hiddenRows,
                                     final String expectedSelection,
                                     final SpreadsheetViewportSelectionAnchor expectedAnchor) {
        this.extendDownRowAndCheck(
                this.parseString(selection),
                anchor,
                this.hiddenColumns(hiddenColumns),
                this.hiddenRows(hiddenRows),
                this.parseStringOrEmpty(expectedSelection).map(
                        s -> s.setAnchor(expectedAnchor)
                )
        );
    }

    final void extendDownRowAndCheck(final S selection,
                                     final SpreadsheetViewportSelectionAnchor anchor,
                                     final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                     final Predicate<SpreadsheetRowReference> hiddenRows,
                                     final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                selection.extendDownRow(
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> selection + " anchor=" + anchor + " navigate extendDownRow"
        );
    }

    // helpers..........................................................................................................

    final static String NO_HIDDEN_COLUMNS = "";

    final static String NO_HIDDEN_ROWS = "";

    final static Predicate<SpreadsheetColumnReference> NO_HIDDEN_COLUMNS_PREDICATE = Predicates.never();

    final static Predicate<SpreadsheetRowReference> NO_HIDDEN_ROWS_PREDICATE = Predicates.never();

    private final static Function<SpreadsheetColumnReference, Double> COLUMN_TO_WIDTH = (c) -> {
        throw new UnsupportedOperationException();
    };

    private final static Function<SpreadsheetRowReference, Double> ROW_TO_HEIGHT = (c) -> {
        throw new UnsupportedOperationException();
    };

    private Optional<SpreadsheetSelection> parseStringOrEmpty(final String text) {
        return Optional.ofNullable(
                CharSequences.isNullOrEmpty(text) ?
                        null :
                        this.parseRange(text).simplify()
        );
    }

    private Predicate<SpreadsheetColumnReference> hiddenColumns(final String columns) {
        return hiddenPredicate(
                columns,
                SpreadsheetSelection::parseColumn
        );
    }

    private Predicate<SpreadsheetRowReference> hiddenRows(final String rows) {
        return hiddenPredicate(
                rows,
                SpreadsheetSelection::parseRow
        );
    }

    private static <T extends SpreadsheetColumnOrRowReference> Predicate<T> hiddenPredicate(final String columnOrRows,
                                                                                            final Function<String, T> parser) {
        return (columnOrRow) -> CharacterConstant.COMMA.parse(
                columnOrRows,
                parser
        ).contains(columnOrRow);
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
                1L, // count is long
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

    // HasUrlFragment...................................................................................................

    @Test
    public final void testHasUrlFragment() {
        final S selection = this.createSelection();
        final String toString = selection.toString();

        this.checkEquals(
                UrlFragment.with(
                        "/" +
                                (selection.isLabelName() ? "cell" : selection.selectionTypeName())
                                        .replace("-range", "") +
                                "/" +
                                toString
                ),
                selection.urlFragment()
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
    public final void testMarshall() {
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

    // NamingTesting....................................................................................................

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
}
