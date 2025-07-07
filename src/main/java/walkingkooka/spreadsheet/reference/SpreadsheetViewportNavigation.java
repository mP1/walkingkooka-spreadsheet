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

import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.SpreadsheetViewportWindowsFunction;
import walkingkooka.text.HasText;

import java.util.Objects;
import java.util.Optional;

/**
 * Captures a users input movement relative to another selection with a viewport
 */
public abstract class SpreadsheetViewportNavigation implements HasText {

    /**
     * {@see SpreadsheetViewportNavigationSelectionSelectCell}
     */
    public static SpreadsheetViewportNavigation cell(final SpreadsheetCellReference selection) {
        return SpreadsheetViewportNavigationSelectionSelectCell.with(selection);
    }

    /**
     * {@see SpreadsheetViewportNavigationSelectionExtendCell}
     */
    public static SpreadsheetViewportNavigation extendCell(final SpreadsheetCellReference selection) {
        return SpreadsheetViewportNavigationSelectionExtendCell.with(selection);
    }

    /**
     * {@see SpreadsheetViewportNavigationSelectionSelectColumn}
     */
    public static SpreadsheetViewportNavigation column(final SpreadsheetColumnReference selection) {
        return SpreadsheetViewportNavigationSelectionSelectColumn.with(selection);
    }

    /**
     * {@see SpreadsheetViewportNavigationSelectionExtendColumn}
     */
    public static SpreadsheetViewportNavigation extendColumn(final SpreadsheetColumnReference selection) {
        return SpreadsheetViewportNavigationSelectionExtendColumn.with(selection);
    }

    /**
     * {@see SpreadsheetViewportNavigationSelectionSelectRow}
     */
    public static SpreadsheetViewportNavigation row(final SpreadsheetRowReference row) {
        return SpreadsheetViewportNavigationSelectionSelectRow.with(row);
    }

    /**
     * {@see SpreadsheetViewportNavigationSelectionExtendRow}
     */
    public static SpreadsheetViewportNavigation extendRow(final SpreadsheetRowReference row) {
        return SpreadsheetViewportNavigationSelectionExtendRow.with(row);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendDownPixel}
     */
    public static SpreadsheetViewportNavigation extendDownPixel(final int value) {
        return SpreadsheetViewportNavigationExtendDownPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationDownRow}
     */
    public static SpreadsheetViewportNavigation extendDownRow() {
        return SpreadsheetViewportNavigationExtendDownRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendLeftColumn}
     */
    public static SpreadsheetViewportNavigation extendLeftColumn() {
        return SpreadsheetViewportNavigationExtendLeftColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendLeftPixel}
     */
    public static SpreadsheetViewportNavigation extendLeftPixel(final int value) {
        return SpreadsheetViewportNavigationExtendLeftPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendRightColumn}
     */
    public static SpreadsheetViewportNavigation extendRightColumn() {
        return SpreadsheetViewportNavigationExtendRightColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendRightPixel}
     */
    public static SpreadsheetViewportNavigation extendRightPixel(final int value) {
        return SpreadsheetViewportNavigationExtendRightPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendUpPixel}
     */
    public static SpreadsheetViewportNavigation extendUpPixel(final int value) {
        return SpreadsheetViewportNavigationExtendUpPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendUpRow}
     */
    public static SpreadsheetViewportNavigation extendUpRow() {
        return SpreadsheetViewportNavigationExtendUpRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationDownPixel}
     */
    public static SpreadsheetViewportNavigation downPixel(final int value) {
        return SpreadsheetViewportNavigationDownPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationDownRow}
     */
    public static SpreadsheetViewportNavigation downRow() {
        return SpreadsheetViewportNavigationDownRow.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationLeftColumn}
     */
    public static SpreadsheetViewportNavigation leftColumn() {
        return SpreadsheetViewportNavigationLeftColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationLeftPixel}
     */
    public static SpreadsheetViewportNavigation leftPixel(final int value) {
        return SpreadsheetViewportNavigationLeftPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationRightColumn}
     */
    public static SpreadsheetViewportNavigation rightColumn() {
        return SpreadsheetViewportNavigationRightColumn.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationRightPixel}
     */
    public static SpreadsheetViewportNavigation rightPixel(final int value) {
        return SpreadsheetViewportNavigationRightPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationUpPixel}
     */
    public static SpreadsheetViewportNavigation upPixel(final int value) {
        return SpreadsheetViewportNavigationUpPixel.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationUpRow}
     */
    public static SpreadsheetViewportNavigation upRow() {
        return SpreadsheetViewportNavigationUpRow.INSTANCE;
    }

    SpreadsheetViewportNavigation() {
    }

    /**
     * Executes this navigation on the given selection and anchor returning the updated result.
     */
    public final SpreadsheetViewport update(final SpreadsheetViewport viewport,
                                            final SpreadsheetViewportNavigationContext context) {
        Objects.requireNonNull(viewport, "viewport");
        Objects.requireNonNull(context, "context");

        return this.update0(
            viewport,
            context
        );
    }

    abstract SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                         final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                                    final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context);

    /**
     * Takes a updated {@link AnchoredSpreadsheetSelection} and also updates the {@link SpreadsheetViewport},
     * which may involve moving the home as necessary so the viewport includes the new selection.
     */
    final SpreadsheetViewport updateViewport(final AnchoredSpreadsheetSelection anchoredSelection,
                                             final SpreadsheetViewport viewport,
                                             final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = viewport;

        final SpreadsheetViewportRectangle rectangle = viewport.rectangle();

        // check if moved selection is within the original viewport
        final SpreadsheetViewportWindows windows = context.windows(
            rectangle,
            true, //includeFrozenColumnsRows
            SpreadsheetViewportWindowsFunction.NO_SELECTION
        );

        if (
            windows.test(
                anchoredSelection.anchor()
                    .opposite()
                    .selection(
                        anchoredSelection.selection()
                    )
            )
        ) {
            // moved selection within windows leave home unmoved
            result = viewport.setAnchoredSelection(
                Optional.of(anchoredSelection)
            );
        } else {
            // moved selection is outside viewport need to move home
            final SpreadsheetCellReference home = rectangle.home();
            final Optional<SpreadsheetCellReference> maybeMovedHome = this.updateHome(
                home,
                context
            );

            if (maybeMovedHome.isPresent()) {
                result = result.setRectangle(
                    rectangle.setHome(
                        maybeMovedHome.get()
                    )
                );
            } else {
                result = result.setRectangle(
                    rectangle.setHome(home)
                ).setAnchoredSelection(SpreadsheetViewport.NO_ANCHORED_SELECTION);
            }
        }
        return result;
    }

    abstract Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                           final SpreadsheetViewportNavigationContext context);

    /**
     * Any navigations before a previous should be ignored as the selection replaces them.
     */
    final boolean isClearPrevious() {
        return this instanceof SpreadsheetViewportNavigationSelectionSelect;
    }

    abstract boolean isOpposite(final SpreadsheetViewportNavigation other);

    /**
     * Returns true if an extend {@link SpreadsheetViewportNavigation}.
     */
    public final boolean isExtend() {
        return this.getClass().getSimpleName().contains("Extend");
    }

    /**
     * Returns true if a navigation with a pixel argument
     */
    public final boolean isPixel() {
        return this instanceof SpreadsheetViewportNavigationPixel;
    }

    @Override
    public final String toString() {
        return this.text();
    }
}
