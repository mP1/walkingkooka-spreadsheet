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
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContexts;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.store.HasNotFoundTextTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class SpreadsheetSelectionTestCase<S extends SpreadsheetSelection> implements ClassTesting2<S>,
    HashCodeEqualsDefinedTesting2<S>,
    HasUrlFragmentTesting,
    HasTextTesting,
    HasNotFoundTextTesting,
    JsonNodeMarshallingTesting<S>,
    IsMethodTesting<S>,
    ParseStringTesting<S>,
    PredicateTesting2<S, SpreadsheetSelection>,
    ToStringTesting<S>,
    TreePrintableTesting {

    private final static Function<SpreadsheetViewport, SpreadsheetViewportWindows> WINDOWS_FUNCTION = (SpreadsheetViewport v) -> {
        throw new UnsupportedOperationException();
    };

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

    // add..............................................................................................................

    final void addAndCheck(final SpreadsheetSelection selection,
                           final int delta,
                           final SpreadsheetSelection expected) {
        this.checkEquals(
            expected,
            selection.add(delta),
            () -> selection + " add " + delta
        );
    }

    // addSaturated.....................................................................................................

    final void addSaturatedAndCheck(final SpreadsheetSelection selection,
                                    final int delta,
                                    final SpreadsheetSelection expected) {
        this.checkEquals(
            expected,
            selection.addSaturated(delta),
            () -> selection + " addSaturated " + delta
        );
    }

    // add column/row...................................................................................................

    @Test
    public final void testAddColumnAndRowZeroZero() {
        final SpreadsheetSelection selection = this.createSelection();
        if (false == selection.isLabelName()) {
            assertSame(
                selection,
                selection.add(0, 0)
            );
        }
    }

    final void addColumnRowAndCheck(final S selection,
                                    final int column,
                                    final int row,
                                    final S expected) {
        this.checkEquals(
            expected,
            selection.add(
                column,
                row
            ),
            () -> selection + "add " + column + " " + row
        );
    }

    @Test
    public final void testAddSaturatedColumnAndRowZeroZero() {
        final SpreadsheetSelection selection = this.createSelection();
        if (false == selection.isLabelName()) {
            assertSame(
                selection,
                selection.addSaturated(0, 0)
            );
        }
    }

    final void addSaturatedColumnRowAndCheck(final S selection,
                                             final int column,
                                             final int row,
                                             final S expected) {
        this.checkEquals(
            expected,
            selection.addSaturated(
                column,
                row
            ),
            () -> selection + "addSaturated " + column + " " + row
        );
    }

    // addIfRelative....................................................................................................

    final void addIfRelativeAndCheck(final S selection,
                                     final int delta) {
        assertSame(
            selection,
            selection.addIfRelative(delta)
        );
    }

    final void addIfRelativeAndCheck(final S selection,
                                     final int delta,
                                     final S expected) {
        this.checkEquals(
            expected,
            selection.addIfRelative(delta),
            () -> selection + " addIfRelative " + delta
        );
    }

    // count.............................................................................................................

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

    final void toCellAndCheck(final String selection,
                              final String expected) {
        this.toCellAndCheck(
            this.parseString(selection),
            expected
        );
    }

    final void toCellAndCheck(final SpreadsheetSelection selection,
                              final String expected) {
        this.toCellAndCheck(
            selection,
            SpreadsheetSelection.parseCell(expected)
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

    // toCellOrCellRange................................................................................................

    final void toCellOrCellRangeFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .toCellOrCellRange()
        );
    }

    // toCellRange......................................................................................................

    final void toCellRangeAndCheck(final String selection,
                                   final String expected) {
        this.toCellRangeAndCheck(
            this.parseString(selection),
            SpreadsheetSelection.parseCellRange(expected)
        );
    }

    final void toCellRangeAndCheck(final SpreadsheetSelection selection,
                                   final SpreadsheetCellRangeReference expected) {
        this.checkEquals(
            expected,
            selection.toCellRange(),
            selection::toString
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
                                     final SpreadsheetColumnRangeReference expected) {
        this.checkEquals(
            expected,
            selection.toColumnRange(),
            () -> selection + " toColumnRange"
        );
    }

    // toColumnOrColumnRange............................................................................................

    final void toColumnOrColumnRangeAndCheck(final S selection,
                                             final SpreadsheetSelection expected) {
        this.checkEquals(
            expected,
            selection.toColumnOrColumnRange()
        );
    }

    final void toColumnOrColumnRangeFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .toColumnOrColumnRange()
        );
    }

    // toExpressionReference............................................................................................

    final void toExpressionReferenceAndCheck() {
        final S selection = this.createSelection();
        assertSame(
            selection,
            selection.toExpressionReference()
        );
    }

    final void toExpressionReferenceFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .toExpressionReference()
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
                                  final SpreadsheetRowRangeReference expected) {
        this.checkEquals(
            expected,
            selection.toRowRange(),
            () -> selection + " toRowRange"
        );
    }

    // toRowOrRowRange..................................................................................................

    final void toRowOrRowRangeAndCheck(final S selection,
                                       final SpreadsheetSelection expected) {
        this.checkEquals(
            expected,
            selection.toRowOrRowRange()
        );
    }

    final void toRowOrRowRangeFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .toRowOrRowRange()
        );
    }

    // ReplaceReferencesMapper..........................................................................................

    @Test
    public final void testReplaceReferencesMapperNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.A1.replaceReferencesMapper(null)
        );
    }

    @Test
    public final void testReplaceReferencesMapperLabelFails() {
        this.replaceReferencesMapperFails(
            SpreadsheetSelection.labelName("Label123"),
            "Expected non label but got Label123"
        );
    }

    final void replaceReferencesMapperFails(final SpreadsheetSelection moveTo,
                                            final String message) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createSelection()
                .replaceReferencesMapper(moveTo)
        );

        this.checkEquals(
            message,
            thrown.getMessage(),
            "message"
        );
    }

    final void replaceReferencesMapperAndCheck(final String selection,
                                               final SpreadsheetSelection moveTo,
                                               final int deltaX,
                                               final int deltaY) {
        this.replaceReferencesMapperAndCheck(
            this.parseString(selection),
            moveTo,
            deltaX,
            deltaY
        );
    }

    final void replaceReferencesMapperAndCheck(final SpreadsheetSelection selection,
                                               final SpreadsheetSelection moveTo,
                                               final int deltaX,
                                               final int deltaY) {
        final Optional<SpreadsheetSelectionReplaceReferencesMapperFunction> maybeMapper = Cast.to(
            selection.replaceReferencesMapper(moveTo)
        );

        if (0 != deltaX || 0 != deltaY) {
            final SpreadsheetSelectionReplaceReferencesMapperFunction mapper = maybeMapper.get();
            this.checkEquals(
                deltaX,
                mapper.deltaX,
                "deltaX"
            );
            this.checkEquals(
                deltaY,
                mapper.deltaY,
                "deltaY"
            );
        } else {
            this.checkEquals(
                Optional.empty(),
                maybeMapper
            );
        }
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public final void testComparatorNamesBoundsCheckWithNullComparatorsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSelection()
                .comparatorNamesBoundsCheck(null)
        );
    }

    void comparatorNamesBoundsCheckAndCheck(final String selection,
                                            final String comparatorsNameList) {
        this.comparatorNamesBoundsCheckAndCheck(
            this.parseString(selection),
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(comparatorsNameList)
        );
    }

    void comparatorNamesBoundsCheckAndCheck(final SpreadsheetSelection selection,
                                            final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList comparatorsNameList) {
        selection.comparatorNamesBoundsCheck(comparatorsNameList);
    }

    void comparatorNamesBoundsCheckAndCheckFails(final String selection,
                                                 final String comparatorsNameList,
                                                 final String expected) {
        this.comparatorNamesBoundsCheckAndCheckFails(
            this.parseString(selection),
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(comparatorsNameList),
            expected
        );
    }

    void comparatorNamesBoundsCheckAndCheckFails(final SpreadsheetSelection selection,
                                                 final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList comparatorsNameList,
                                                 final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> selection.comparatorNamesBoundsCheck(comparatorsNameList)
        );

        this.checkEquals(
            expected,
            thrown.getMessage(),
            () -> selection + " comparatorNamesBoundsCheck " + comparatorsNameList
        );
    }

    // containsAll.......................................................................................................

    @Test
    public final void testContainsAllWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSelection()
                .containsAll(null)
        );
    }

    final void containsAllAndCheck(final SpreadsheetSelection selection,
                                   final SpreadsheetViewportWindows windows,
                                   final boolean expected) {
        this.checkEquals(
            expected,
            selection.containsAll(windows),
            () -> selection + " containsAll " + windows
        );
    }

    // testXXX..........................................................................................................

    @Test
    public final void testTestWithNullFalse() {
        this.testFalse(null);
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

    @Test final void testTestCellWithNullFails() {
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

    @Test final void testTestCellRangeWithNullFails() {
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
                                     final SpreadsheetCellRangeReference range,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            selection.testCellRange(range),
            () -> selection + " testCellRange " + range
        );

        this.testAndCheck(
            selection,
            range,
            expected
        );
    }

    @Test final void testTestColumnWithNullFails() {
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

    @Test final void testTestRowWithNullFails() {
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

    @Test final public void testDefaultAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportAnchor anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");
    }

    @Test final public void testDefaultAnchorThenSetAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportAnchor anchor = selection.defaultAnchor();
        this.checkNotEquals(null, anchor, "anchor");

        final AnchoredSpreadsheetSelection anchored = selection.setAnchor(anchor);
        this.checkEquals(anchor, anchored.anchor(), "anchor");
        this.checkEquals(selection, anchored.selection(), "selection");
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
            for (final SpreadsheetViewportAnchor anchor : SpreadsheetViewportAnchor.values()) {
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

    // setDefaultAnchor.................................................................................................

    @Test
    public final void testSetDefaultAnchor() {
        final S selection = this.createSelection();
        final SpreadsheetViewportAnchor anchor = selection.defaultAnchor();

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
                                  final SpreadsheetViewportAnchor anchor,
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
                                  final SpreadsheetViewportAnchor anchor,
                                  final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                  final Predicate<SpreadsheetRowReference> hiddenRows,
                                  final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.leftColumn(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate leftColumn"
        );
    }

    // leftPixels.......................................................................................................

    final void leftPixelsAndCheck(final String selection,
                                  final SpreadsheetViewportAnchor anchor,
                                  final int count,
                                  final String hiddenColumns,
                                  final Map<String, Double> columnWidths,
                                  final String hiddenRows,
                                  final Map<String, Double> rowHeights,
                                  final String expected) {
        this.leftPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expected)
        );
    }

    final void leftPixelsAndCheck(final S selection,
                                  final SpreadsheetViewportAnchor anchor,
                                  final int count,
                                  final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                  final Function<SpreadsheetColumnReference, Double> columnWidths,
                                  final Predicate<SpreadsheetRowReference> hiddenRows,
                                  final Function<SpreadsheetRowReference, Double> rowHeight,
                                  final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.leftPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnWidths,
                    hiddenRows,
                    rowHeight,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate " + count + " leftPixels"
        );
    }

    // upRow............................................................................................................

    final void upRowAndCheck(final String selection,
                             final SpreadsheetViewportAnchor anchor,
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
                             final SpreadsheetViewportAnchor anchor,
                             final Predicate<SpreadsheetColumnReference> hiddenColumns,
                             final Predicate<SpreadsheetRowReference> hiddenRows,
                             final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.upRow(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate upRow"
        );
    }

    // upPixels.......................................................................................................

    final void upPixelsAndCheck(final String selection,
                                final SpreadsheetViewportAnchor anchor,
                                final int count,
                                final String hiddenColumns,
                                final Map<String, Double> columnWidths,
                                final String hiddenRows,
                                final Map<String, Double> rowHeights,
                                final String expected) {
        this.upPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expected)
        );
    }

    final void upPixelsAndCheck(final S selection,
                                final SpreadsheetViewportAnchor anchor,
                                final int count,
                                final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                final Function<SpreadsheetColumnReference, Double> columnWidths,
                                final Predicate<SpreadsheetRowReference> hiddenRows,
                                final Function<SpreadsheetRowReference, Double> rowHeight,
                                final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.upPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnWidths,
                    hiddenRows,
                    rowHeight,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate " + count + " upPixels"
        );
    }

    // rightColumn.......................................................................................................

    final void rightColumnAndCheck(final String selection,
                                   final SpreadsheetViewportAnchor anchor,
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
                                   final SpreadsheetViewportAnchor anchor,
                                   final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                   final Predicate<SpreadsheetRowReference> hiddenRows,
                                   final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.rightColumn(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate rightColumn"
        );
    }

    // rightPixels.....................................................................................................

    final void rightPixelsAndCheck(final String selection,
                                   final SpreadsheetViewportAnchor anchor,
                                   final int count,
                                   final String hiddenColumns,
                                   final Map<String, Double> columnWidths,
                                   final String hiddenRows,
                                   final Map<String, Double> rowHeights,
                                   final String expected) {
        this.rightPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expected)
        );
    }

    final void rightPixelsAndCheck(final S selection,
                                   final SpreadsheetViewportAnchor anchor,
                                   final int count,
                                   final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                   final Function<SpreadsheetColumnReference, Double> columnWidths,
                                   final Predicate<SpreadsheetRowReference> hiddenRows,
                                   final Function<SpreadsheetRowReference, Double> rowHeight,
                                   final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.rightPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnWidths,
                    hiddenRows,
                    rowHeight,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate " + count + " rightPixels"
        );
    }

    // downRow..........................................................................................................

    final void downRowAndCheck(final String selection,
                               final SpreadsheetViewportAnchor anchor,
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
                               final SpreadsheetViewportAnchor anchor,
                               final Predicate<SpreadsheetColumnReference> hiddenColumns,
                               final Predicate<SpreadsheetRowReference> hiddenRows,
                               final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.downRow(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate downRow"
        );
    }

    // downPixels......................................................................................................

    final void downPixelsAndCheck(final String selection,
                                  final SpreadsheetViewportAnchor anchor,
                                  final int count,
                                  final String hiddenColumns,
                                  final Map<String, Double> columnWidths,
                                  final String hiddenRows,
                                  final Map<String, Double> rowHeights,
                                  final String expected) {
        this.downPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expected)
        );
    }

    final void downPixelsAndCheck(final S selection,
                                  final SpreadsheetViewportAnchor anchor,
                                  final int count,
                                  final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                  final Function<SpreadsheetColumnReference, Double> columnWidths,
                                  final Predicate<SpreadsheetRowReference> hiddenRows,
                                  final Function<SpreadsheetRowReference, Double> rowHeight,
                                  final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.downPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnWidths,
                    hiddenRows,
                    rowHeight,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate " + count + " downPixels"
        );
    }

    // extendRange......................................................................................................

    final void extendRangeAndCheck(final String selection,
                                   final String moved) {
        this.extendRangeAndCheck(
            selection,
            moved,
            SpreadsheetViewportAnchor.NONE
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final SpreadsheetViewportAnchor anchor) {
        final S parsed = this.parseString(selection);

        this.extendRangeAndCheck(
            parsed,
            this.parseString(moved)
                .toScalarIfUnit(),
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
            SpreadsheetViewportAnchor.NONE,
            expected
        );
    }

    final void extendRangeAndCheck(final String selection,
                                   final String moved,
                                   final SpreadsheetViewportAnchor anchor,
                                   final String expected) {
        this.extendRangeAndCheck(
            this.parseString(selection),
            this.parseString(moved)
                .toScalarIfUnit(),
            anchor,
            this.parseRange(expected)
        );
    }

    final void extendRangeAndCheck(final S selection,
                                   final SpreadsheetSelection moved,
                                   final SpreadsheetViewportAnchor anchor,
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
                                   final SpreadsheetViewportAnchor anchor,
                                   final Optional<SpreadsheetSelection> expected) {
        if (moved.isPresent()) {
            this.checkEquals(
                true,
                moved.map(m -> m instanceof SpreadsheetCellReference || m instanceof SpreadsheetColumnReference || m instanceof SpreadsheetRowReference).get(),
                () -> moved + " must be either cell/column/row"
            );
        }
        this.checkEquals(
            expected.map(SpreadsheetSelection::toScalarIfUnit),
            selection.extendRange(Cast.to(moved), anchor),
            () -> selection + " extendRange " + moved + " " + anchor
        );
    }

    abstract SpreadsheetSelection parseRange(final String range);

    // extendLeftColumn.................................................................................................

    final void extendLeftColumnAndCheck(final String selection,
                                        final SpreadsheetViewportAnchor anchor,
                                        final String hiddenColumns,
                                        final String hiddenRows,
                                        final String expectedSelection,
                                        final SpreadsheetViewportAnchor expectedAnchor) {
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
                                        final SpreadsheetViewportAnchor anchor,
                                        final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                        final Predicate<SpreadsheetRowReference> hiddenRows,
                                        final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendLeftColumn(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendLeftColumn"
        );
    }

    // extendLeftPixels.................................................................................................

    final void extendLeftPixelsAndCheck(final String selection,
                                        final SpreadsheetViewportAnchor anchor,
                                        final int count,
                                        final String hiddenColumns,
                                        final Map<String, Double> columnWidths,
                                        final String hiddenRows,
                                        final Map<String, Double> rowHeights,
                                        final String expectedSelection,
                                        final SpreadsheetViewportAnchor expectedAnchor) {
        this.extendLeftPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expectedSelection).map(
                s -> s.setAnchor(expectedAnchor)
            )
        );
    }

    final void extendLeftPixelsAndCheck(final S selection,
                                        final SpreadsheetViewportAnchor anchor,
                                        final int count,
                                        final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                        final Function<SpreadsheetColumnReference, Double> columnToWidths,
                                        final Predicate<SpreadsheetRowReference> hiddenRows,
                                        final Function<SpreadsheetRowReference, Double> rowToHeights,
                                        final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendLeftPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnToWidths,
                    hiddenRows,
                    rowToHeights,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendLeftPixels"
        );
    }

    // extendUpRow......................................................................................................

    final void extendUpRowAndCheck(final String selection,
                                   final SpreadsheetViewportAnchor anchor,
                                   final String hiddenColumns,
                                   final String hiddenRows,
                                   final String expectedSelection,
                                   final SpreadsheetViewportAnchor expectedAnchor) {
        this.extendUpRowAndCheck(
            this.parseString(selection),
            anchor,
            this.hiddenColumns(hiddenColumns),
            this.hiddenRows(hiddenRows),
            this.parseStringOrEmpty(expectedSelection).map(
                s -> s.toScalarIfUnit()
                    .setAnchor(expectedAnchor)
            )
        );
    }

    final void extendUpRowAndCheck(final S selection,
                                   final SpreadsheetViewportAnchor anchor,
                                   final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                   final Predicate<SpreadsheetRowReference> hiddenRows,
                                   final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendUpRow(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendUpRow"
        );
    }

    // extendUpPixels..................................................................................................

    final void extendUpPixelsAndCheck(final String selection,
                                      final SpreadsheetViewportAnchor anchor,
                                      final int count,
                                      final String hiddenColumns,
                                      final Map<String, Double> columnWidths,
                                      final String hiddenRows,
                                      final Map<String, Double> rowHeights,
                                      final String expectedSelection,
                                      final SpreadsheetViewportAnchor expectedAnchor) {
        this.extendUpPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expectedSelection).map(
                s -> s.setAnchor(expectedAnchor)
            )
        );
    }

    final void extendUpPixelsAndCheck(final S selection,
                                      final SpreadsheetViewportAnchor anchor,
                                      final int count,
                                      final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                      final Function<SpreadsheetColumnReference, Double> columnToWidths,
                                      final Predicate<SpreadsheetRowReference> hiddenRows,
                                      final Function<SpreadsheetRowReference, Double> rowToHeights,
                                      final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendUpPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnToWidths,
                    hiddenRows,
                    rowToHeights,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendUpPixels"
        );
    }

    // extendRightColumn................................................................................................

    final void extendRightColumnAndCheck(final String selection,
                                         final SpreadsheetViewportAnchor anchor,
                                         final String hiddenColumns,
                                         final String hiddenRows,
                                         final String expectedSelection,
                                         final SpreadsheetViewportAnchor expectedAnchor) {
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
                                         final SpreadsheetViewportAnchor anchor,
                                         final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                         final Predicate<SpreadsheetRowReference> hiddenRows,
                                         final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendRightColumn(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendRightColumn"
        );
    }

    // extendRightPixels.................................................................................................

    final void extendRightPixelsAndCheck(final String selection,
                                         final SpreadsheetViewportAnchor anchor,
                                         final int count,
                                         final String hiddenColumns,
                                         final Map<String, Double> columnWidths,
                                         final String hiddenRows,
                                         final Map<String, Double> rowHeights,
                                         final String expectedSelection,
                                         final SpreadsheetViewportAnchor expectedAnchor) {
        this.extendRightPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expectedSelection).map(
                s -> s.setAnchor(expectedAnchor)
            )
        );
    }

    final void extendRightPixelsAndCheck(final S selection,
                                         final SpreadsheetViewportAnchor anchor,
                                         final int count,
                                         final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                         final Function<SpreadsheetColumnReference, Double> columnToWidths,
                                         final Predicate<SpreadsheetRowReference> hiddenRows,
                                         final Function<SpreadsheetRowReference, Double> rowToHeights,
                                         final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendRightPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnToWidths,
                    hiddenRows,
                    rowToHeights,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendRightPixels"
        );
    }

    // extendDownRow...................................................................................................

    final void extendDownRowAndCheck(final String selection,
                                     final SpreadsheetViewportAnchor anchor,
                                     final String hiddenColumns,
                                     final String hiddenRows,
                                     final String expectedSelection,
                                     final SpreadsheetViewportAnchor expectedAnchor) {
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
                                     final SpreadsheetViewportAnchor anchor,
                                     final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                     final Predicate<SpreadsheetRowReference> hiddenRows,
                                     final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendDownRow(
                anchor,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendDownRow"
        );
    }

    // extendDownPixels.................................................................................................

    final void extendDownPixelsAndCheck(final String selection,
                                        final SpreadsheetViewportAnchor anchor,
                                        final int count,
                                        final String hiddenColumns,
                                        final Map<String, Double> columnWidths,
                                        final String hiddenRows,
                                        final Map<String, Double> rowHeights,
                                        final String expectedSelection,
                                        final SpreadsheetViewportAnchor expectedAnchor) {
        this.extendDownPixelsAndCheck(
            this.parseString(selection),
            anchor,
            count,
            this.hiddenColumns(hiddenColumns),
            this.columnToWidth(columnWidths),
            this.hiddenRows(hiddenRows),
            this.rowToHeight(rowHeights),
            this.parseStringOrEmpty(expectedSelection).map(
                s -> s.setAnchor(expectedAnchor)
            )
        );
    }

    final void extendDownPixelsAndCheck(final S selection,
                                        final SpreadsheetViewportAnchor anchor,
                                        final int count,
                                        final Predicate<SpreadsheetColumnReference> hiddenColumns,
                                        final Function<SpreadsheetColumnReference, Double> columnToWidths,
                                        final Predicate<SpreadsheetRowReference> hiddenRows,
                                        final Function<SpreadsheetRowReference, Double> rowToHeights,
                                        final Optional<AnchoredSpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            selection.extendDownPixels(
                anchor,
                count,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    columnToWidths,
                    hiddenRows,
                    rowToHeights,
                    WINDOWS_FUNCTION
                )
            ),
            () -> selection + " anchor=" + anchor + " navigate extendDownPixels"
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
                this.parseRange(text)
                    .toScalarIfUnit()
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

    private static <T extends SpreadsheetSelection> Predicate<T> hiddenPredicate(final String columnOrRows,
                                                                                 final Function<String, T> parser) {
        return (columnOrRow) -> CharacterConstant.COMMA.parse(
            columnOrRows,
            parser
        ).contains(columnOrRow);
    }

    private Function<SpreadsheetColumnReference, Double> columnToWidth(final Map<String, Double> columnToWidths) {
        return columnOrRowToWidthOrHeight(
            columnToWidths,
            SpreadsheetSelection::parseColumn
        );
    }

    private Function<SpreadsheetRowReference, Double> rowToHeight(final Map<String, Double> rowToHeights) {
        return columnOrRowToWidthOrHeight(
            rowToHeights,
            SpreadsheetSelection::parseRow
        );
    }

    private <T extends SpreadsheetSelection> Function<T, Double> columnOrRowToWidthOrHeight(final Map<String, Double> columnOrRowToWidthOrHeight,
                                                                                            final Function<String, T> parser) {
        final Map<T, Double> map = columnOrRowToWidthOrHeight.entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    e -> parser.apply(e.getKey()),
                    Map.Entry::getValue
                )
            );
        return (columnOrRow) -> {
            final Double length = map.get(columnOrRow);
            this.checkNotEquals(
                null,
                length,
                () -> "Missing " + columnOrRow + " parse " + columnOrRowToWidthOrHeight
            );
            return length;
        };
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
                               final SpreadsheetViewportAnchor anchor,
                               final String expected) {
        this.focusedAndCheck(
            this.parseString(selection),
            anchor,
            this.parseString(expected)
                .toScalarIfUnit()
        );
    }

    final void focusedAndCheck(final S selection,
                               final SpreadsheetViewportAnchor anchor,
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

    // toScalar.........................................................................................................

    final void toScalarAndCheck(final String selection) {
        this.toScalarAndCheck(
            this.parseString(selection)
        );
    }

    final void toScalarAndCheck(final S selection) {
        this.toScalarAndCheck(
            selection,
            selection
        );
    }

    final void toScalarAndCheck(final String selection,
                                final SpreadsheetSelection expected) {
        this.toScalarAndCheck(
            this.parseString(selection),
            expected
        );
    }

    final void toScalarAndCheck(final S selection,
                                final SpreadsheetSelection expected) {
        final SpreadsheetSelection scalar = selection.toScalar();

        if (scalar.isCellRange() || scalar.isColumnRange() || scalar.isRowRange()) {
            throw new IllegalStateException("Scalar " + scalar + " of " + selection + " must not be a range");
        }

        this.checkEquals(
            expected,
            scalar,
            () -> "toScalar " + selection
        );
    }

    // toRange..........................................................................................................

    final void toRangeAndCheck(final String selection) {
        this.toRangeAndCheck(
            this.parseString(selection)
        );
    }

    final void toRangeAndCheck(final S selection) {
        this.toRangeAndCheck(
            selection,
            selection
        );
    }

    final void toRangeAndCheck(final String selection,
                               final SpreadsheetSelection expected) {
        this.toRangeAndCheck(
            this.parseString(selection),
            expected
        );
    }

    final void toRangeAndCheck(final S selection,
                               final SpreadsheetSelection expected) {
        final SpreadsheetSelection range = selection.toRange();

        if (range.isCell() || range.isColumn() || range.isRow()) {
            throw new IllegalStateException("Range " + range + " of " + selection + " must not be a scalar");
        }

        this.checkEquals(
            expected,
            range,
            () -> "toRange " + selection
        );
    }

    // ifDifferentColumnOrRowTypeFail...................................................................................

    final void ifDifferentColumnOrRowTypeFail(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow,
                                              final SpreadsheetColumnOrRowReferenceOrRange other) {
        columnOrRow.ifDifferentColumnOrRowTypeFail(other);
    }

    final void ifDifferentColumnOrRowTypeFail(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow,
                                              final SpreadsheetColumnOrRowReferenceOrRange other,
                                              final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> columnOrRow.ifDifferentColumnOrRowTypeFail(other)
        );
        this.checkEquals(
            expected,
            thrown.getMessage(),
            () -> columnOrRow + " ifDifferentColumnOrRowTypeFail " + other
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public final void testHasUrlFragment() {
        final S selection = this.createSelection();

        this.urlFragmentAndCheck(
            selection,
            UrlFragment.with(
                selection.toStringMaybeStar()
            )
        );
    }

    // HasParserToken...................................................................................................

    final void toParserTokenAndCheck(final SpreadsheetSelection selection,
                                     final SpreadsheetFormulaParserToken expected) {
        this.checkEquals(
            expected,
            selection.toParserToken(),
            selection::toString
        );
    }

    final void toParserTokenAndCheck(final SpreadsheetSelection selection,
                                     final SpreadsheetFormulaParserToken expected,
                                     final Parser<SpreadsheetParserContext> parser) {
        this.toParserTokenAndCheck(
            selection,
            expected
        );

        this.checkEquals(
            Optional.of(
                expected
            ),
            parser.orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(
                    TextCursors.charSequence(
                        expected.text()
                    ),
                    SpreadsheetParserContexts.fake()
                ),
            () -> "parse " + expected.text()
        );
    }

    // toStringMaybeStar................................................................................................

    final void toStringMaybeStarAndCheck(final SpreadsheetSelection selection) {
        this.toStringMaybeStarAndCheck(
            selection,
            selection.toString()
        );
    }

    final void toStringMaybeStarAndCheck(final SpreadsheetSelection selection,
                                         final String expected) {
        this.checkEquals(
            selection.toStringMaybeStar(),
            expected,
            selection::toString
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

    @Test
    public void testIsCellOrCellRange() {
        this.checkEquals(
            this instanceof SpreadsheetCellReferenceTest ||
                this instanceof SpreadsheetCellRangeReferenceTest,
            this.createSelection()
                .isCellOrCellRange()
        );
    }

    @Test
    public void testIsColumnOrColumnRange() {
        this.checkEquals(
            this instanceof SpreadsheetColumnReferenceTest ||
                this instanceof SpreadsheetColumnRangeReferenceTest,
            this.createSelection()
                .isColumnOrColumnRange()
        );
    }

    @Test
    public void testIsRowOrRowRange() {
        this.checkEquals(
            this instanceof SpreadsheetRowReferenceTest ||
                this instanceof SpreadsheetRowRangeReferenceTest,
            this.createSelection()
                .isRowOrRowRange()
        );
    }

    @Override
    public final S createIsMethodObject() {
        return this.createSelection();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.setContains(
            Sets.of(
                "isAll",
                "isFirst",
                "isNotFirst",
                "isLast",
                "isNotLast",
                "isHidden",
                "isScalar",
                "isCellOrCellRange",
                "isColumnOrColumnRange",
                "isExternalReference",
                "isRowOrRowRange",
                "isUnit"
            )
        );
    }

    @Override
    public final String toIsMethodName(final String typeName) {
        final String clean = typeName.replace("Spreadsheet", "")
            .replace("Reference", "");
        return "is" +
            clean.subSequence(0, 1)
                .toString()
                .toUpperCase() +
            clean.subSequence(
                1,
                clean.length()
            );
    }

    // isUnit...........................................................................................................

    final void isUnitAndCheck(final String selection,
                              final boolean expected) {
        this.isUnitAndCheck(
            this.parseString(selection),
            expected
        );
    }

    final void isUnitAndCheck(final S selection,
                              final boolean expected) {
        this.checkEquals(
            expected,
            selection.isUnit(),
            () -> selection + "  isUnit"
        );
    }

    // JsonNodeTesting..................................................................................................

    @Override
    public final S createJsonNodeMarshallingValue() {
        return this.createSelection();
    }

    // NamingTesting....................................................................................................

    @Override
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
            expected.getName() + " not a subclass of " + IllegalArgumentException.class
        );
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        assertTrue(
            expected instanceof IllegalArgumentException,
            expected.getClass().getName() + "=" + expected + " not a subclass of " + IllegalArgumentException.class
        );
        return expected;
    }

    // PredicateTesting.................................................................................................

    @Override
    public final S createPredicate() {
        return this.createSelection();
    }
}
