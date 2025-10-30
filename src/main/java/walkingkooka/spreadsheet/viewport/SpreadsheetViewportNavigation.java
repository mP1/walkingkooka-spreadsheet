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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
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
     * {@see SpreadsheetViewportNavigationExtendPixelDown}
     */
    public static SpreadsheetViewportNavigation extendDownPixel(final int value) {
        return SpreadsheetViewportNavigationExtendPixelDown.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowMoveDown}
     */
    public static SpreadsheetViewportNavigation extendDownRow() {
        return SpreadsheetViewportNavigationColumnOrRowExtendDown.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowExtendLeft}
     */
    public static SpreadsheetViewportNavigation extendLeftColumn() {
        return SpreadsheetViewportNavigationColumnOrRowExtendLeft.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendPixelLeft}
     */
    public static SpreadsheetViewportNavigation extendLeftPixel(final int value) {
        return SpreadsheetViewportNavigationExtendPixelLeft.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowExtendRight}
     */
    public static SpreadsheetViewportNavigation extendRightColumn() {
        return SpreadsheetViewportNavigationColumnOrRowExtendRight.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendPixelRight}
     */
    public static SpreadsheetViewportNavigation extendRightPixel(final int value) {
        return SpreadsheetViewportNavigationExtendPixelRight.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationExtendPixelUp}
     */
    public static SpreadsheetViewportNavigation extendUpPixel(final int value) {
        return SpreadsheetViewportNavigationExtendPixelUp.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowExtendUp}
     */
    public static SpreadsheetViewportNavigation extendUpRow() {
        return SpreadsheetViewportNavigationColumnOrRowExtendUp.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationPixelScrollDown}
     */
    public static SpreadsheetViewportNavigation downPixel(final int value) {
        return SpreadsheetViewportNavigationPixelScrollDown.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowMoveDown}
     */
    public static SpreadsheetViewportNavigation downRow() {
        return SpreadsheetViewportNavigationColumnOrRowMoveDown.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowMoveLeft}
     */
    public static SpreadsheetViewportNavigation leftColumn() {
        return SpreadsheetViewportNavigationColumnOrRowMoveLeft.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationPixelScrollLeft}
     */
    public static SpreadsheetViewportNavigation leftPixel(final int value) {
        return SpreadsheetViewportNavigationPixelScrollLeft.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowMoveRight}
     */
    public static SpreadsheetViewportNavigation rightColumn() {
        return SpreadsheetViewportNavigationColumnOrRowMoveRight.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportNavigationPixelScrollRight}
     */
    public static SpreadsheetViewportNavigation rightPixel(final int value) {
        return SpreadsheetViewportNavigationPixelScrollRight.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationPixelScrollUp}
     */
    public static SpreadsheetViewportNavigation upPixel(final int value) {
        return SpreadsheetViewportNavigationPixelScrollUp.with(value);
    }

    /**
     * {@see SpreadsheetViewportNavigationColumnOrRowMoveUp}
     */
    public static SpreadsheetViewportNavigation upRow() {
        return SpreadsheetViewportNavigationColumnOrRowMoveUp.INSTANCE;
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
            SpreadsheetViewport.with(rectangle)
                .setIncludeFrozenColumnsRows(true)
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
                ).clearAnchoredSelection();
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
     * Returns true if this is an extend {@link SpreadsheetViewportNavigation}.
     */
    public final boolean isExtend() {
        return this instanceof SpreadsheetViewportNavigationColumnOrRow && this.getClass().getSimpleName().contains("Extend") ||
            this instanceof SpreadsheetViewportNavigationExtendPixel;
    }

    /**
     * Returns true if a navigation with a pixel argument
     */
    public final boolean isPixel() {
        return this instanceof SpreadsheetViewportNavigationPixel;
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.value()); // instances should never be added to a Set or Map
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetViewportNavigation &&
                this.equals0((SpreadsheetViewportNavigation) other);
    }

    private boolean equals0(final SpreadsheetViewportNavigation other) {
        return this.getClass().equals(other.getClass()) &&
            Objects.equals(
                this.value(),
                other.value()
            );
    }

    /**
     * All sub-classes have a value which may be null.
     */
    abstract Object value();

    @Override
    public final String toString() {
        return this.text();
    }
}
