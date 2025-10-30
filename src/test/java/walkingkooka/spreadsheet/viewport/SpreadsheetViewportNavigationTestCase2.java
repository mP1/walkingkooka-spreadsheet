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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SpreadsheetViewportNavigationTestCase2<T extends SpreadsheetViewportNavigation> extends
    SpreadsheetViewportNavigationTestCase<T> implements ParseStringTesting<List<T>>,
    HasTextTesting,
    TreePrintableTesting {

    SpreadsheetViewportNavigationTestCase2() {
        super();
    }

    // parse............................................................................................................

    @Override
    public final void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public final void testParseToStringRoundtrip() {
        final T navigation = this.createSpreadsheetViewportNavigation();

        this.parseStringAndCheck(
            navigation.text(),
            Lists.of(
                navigation
            )
        );
    }

    @Override
    public final List<T> parseString(final String string) {
        return Cast.to(
            SpreadsheetViewportNavigationList.parse(string)
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // isExtend.........................................................................................................

    final void isExtendAndCheck(final T navigation,
                                final boolean expected) {
        this.checkEquals(
            expected,
            navigation.isExtend(),
            navigation::toString
        );
    }

    // isScroll.........................................................................................................

    @Test
    public final void testIsScroll() {
        this.checkEquals(
            this.type()
                .getName()
                .contains("Scroll"),
            this.createSpreadsheetViewportNavigation()
                .isScroll()
        );
    }

    // update...........................................................................................................

    final static SpreadsheetCellReference HOME = SpreadsheetCellReference.A1;

    final static int COLUMN_WIDTH = 100;

    final static int ROW_HEIGHT = 30;

    final static int COLUMNS_ACROSS = 5;

    final static int VIEWPORT_WIDTH = COLUMN_WIDTH * COLUMNS_ACROSS;

    final static int ROWS_DOWN = 5;
    final static int VIEWPORT_HEIGHT = ROW_HEIGHT * ROWS_DOWN;

    final static SpreadsheetViewportRectangle HOME_VIEWPORT_RECTANGLE = viewportRectangle(HOME);

    static SpreadsheetViewportRectangle viewportRectangle(final SpreadsheetCellReference home) {
        return home.viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    final void updateAndCheck(final AnchoredSpreadsheetSelection selection,
                              final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
            Optional.of(selection),
            Optional.of(expected)
        );
    }

    final void updateAndCheck(final SpreadsheetViewportNavigation navigation,
                              final AnchoredSpreadsheetSelection selection,
                              final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
            navigation,
            Optional.of(selection),
            Optional.of(expected)
        );
    }

    final void updateAndCheck(final Optional<AnchoredSpreadsheetSelection> anchoredSelection,
                              final Optional<AnchoredSpreadsheetSelection> expected) {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(),
            HOME_VIEWPORT_RECTANGLE.viewport()
                .setAnchoredSelection(anchoredSelection),
            HOME_VIEWPORT_RECTANGLE.viewport()
                .setAnchoredSelection(expected)
        );
    }

    final void updateAndCheck(final SpreadsheetViewportNavigation navigation,
                              final Optional<AnchoredSpreadsheetSelection> anchoredSelection,
                              final Optional<AnchoredSpreadsheetSelection> expected) {
        this.updateAndCheck(
            navigation,
            HOME_VIEWPORT_RECTANGLE.viewport()
                .setAnchoredSelection(anchoredSelection),
            HOME_VIEWPORT_RECTANGLE.viewport()
                .setAnchoredSelection(expected)
        );
    }

    final void updateAndCheck(final SpreadsheetViewport viewport,
                              final SpreadsheetViewport expected) {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(),
            viewport,
            expected
        );
    }

    final void updateAndCheck(final SpreadsheetViewportNavigation navigation,
                              final SpreadsheetViewport viewport,
                              final SpreadsheetViewport expected) {
        this.updateAndCheck(
            navigation,
            viewport,
            Predicates.never(), // hiddenColumns
            Predicates.never(), // hiddenRows
            expected
        );
    }

    final void updateAndCheck(final SpreadsheetViewportNavigation navigation,
                              final SpreadsheetViewport viewport,
                              final Predicate<SpreadsheetColumnReference> hiddenColumns,
                              final Predicate<SpreadsheetRowReference> hiddenRows,
                              final SpreadsheetViewport expected) {
        final SpreadsheetCellReference home = viewport.rectangle()
            .home();

        SpreadsheetCellReference bottomRight = home;
        for (int i = 0; i < COLUMNS_ACROSS; i++) {
            if (hiddenColumns.test(bottomRight.column())) {
                i--;
            }
            bottomRight = bottomRight.addColumn(1);
        }

        for (int i = 0; i < ROWS_DOWN; i++) {
            if (hiddenRows.test(bottomRight.row())) {
                i--;
            }
            bottomRight = bottomRight.addRow(1);
        }

        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(
            Sets.of(
                home.cellRange(bottomRight)
            )
        );

        this.checkEquals(
            expected,
            navigation.update(
                viewport,
                SpreadsheetViewportNavigationContexts.basic(
                    hiddenColumns,
                    COLUMN_TO_WIDTH,
                    hiddenRows,
                    ROW_TO_HEIGHT,
                    (SpreadsheetViewport v) -> {
                        checkEquals(
                            true,
                            v.includeFrozenColumnsRows(),
                            "includeFrozenColumnsRows"
                        );
                        checkEquals(
                            SpreadsheetViewport.NO_ANCHORED_SELECTION,
                            v.anchoredSelection(),
                            "selection"
                        );
                        return windows;
                    }
                )
            ),
            () -> navigation + " update " + viewport + " windows: " + windows
        );
    }

    final static Function<SpreadsheetColumnReference, Double> COLUMN_TO_WIDTH = (c) -> 1.0 * COLUMN_WIDTH;

    final static Function<SpreadsheetRowReference, Double> ROW_TO_HEIGHT = (c) -> 1.0 * ROW_HEIGHT;

    final Predicate<SpreadsheetColumnReference> hiddenColumns(final String columns) {
        return hiddenPredicate(
            columns,
            SpreadsheetSelection::parseColumn
        );
    }

    final Predicate<SpreadsheetRowReference> hiddenRows(final String rows) {
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

    abstract T createSpreadsheetViewportNavigation();
}
