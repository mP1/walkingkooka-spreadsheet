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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

final class BasicSpreadsheetViewportNavigationContext implements SpreadsheetViewportNavigationContext {

    static BasicSpreadsheetViewportNavigationContext with(final Predicate<SpreadsheetColumnReference> columnHidden,
                                                          final Function<SpreadsheetColumnReference, Double> columnWidths,
                                                          final Predicate<SpreadsheetRowReference> rowHidden,
                                                          final Function<SpreadsheetRowReference, Double> rowHeights,
                                                          final SpreadsheetViewportWindowsFunction windows) {
        return new BasicSpreadsheetViewportNavigationContext(
            Objects.requireNonNull(columnHidden, "columnHidden"),
            Objects.requireNonNull(columnWidths, "columnWidths"),
            Objects.requireNonNull(rowHidden, "rowHidden"),
            Objects.requireNonNull(rowHeights, "rowHeights"),
            Objects.requireNonNull(windows, "windows")
        );
    }

    private BasicSpreadsheetViewportNavigationContext(final Predicate<SpreadsheetColumnReference> columnHidden,
                                                      final Function<SpreadsheetColumnReference, Double> columnWidths,
                                                      final Predicate<SpreadsheetRowReference> rowHidden,
                                                      final Function<SpreadsheetRowReference, Double> rowHeights,
                                                      final SpreadsheetViewportWindowsFunction windows) {
        this.columnHidden = columnHidden;
        this.columnWidths = columnWidths;
        this.rowHidden = rowHidden;
        this.rowHeights = rowHeights;
        this.windows = windows;
    }

    @Override
    public boolean isColumnHidden(final SpreadsheetColumnReference column) {
        checkColumn(column);

        return this.columnHidden.test(column);
    }

    private final Predicate<SpreadsheetColumnReference> columnHidden;

    @Override
    public boolean isRowHidden(final SpreadsheetRowReference row) {
        checkRow(row);

        return this.rowHidden.test(row);
    }

    private final Predicate<SpreadsheetRowReference> rowHidden;

    @Override
    public Optional<SpreadsheetColumnReference> leftColumn(final SpreadsheetColumnReference column) {
        checkColumn(column);

        return move(
            column,
            SpreadsheetColumnReference::isFirst,
            this.columnHidden,
            -1
        );
    }

    @Override
    public Optional<SpreadsheetColumnReference> rightColumn(final SpreadsheetColumnReference column) {
        checkColumn(column);

        return move(
            column,
            SpreadsheetColumnReference::isLast,
            this.columnHidden,
            +1
        );
    }

    @Override
    public Optional<SpreadsheetRowReference> upRow(final SpreadsheetRowReference row) {
        checkRow(row);

        return move(
            row,
            SpreadsheetRowReference::isFirst,
            this.rowHidden,
            -1
        );
    }

    @Override
    public Optional<SpreadsheetRowReference> downRow(final SpreadsheetRowReference row) {
        checkRow(row);

        return move(
            row,
            SpreadsheetRowReference::isLast,
            this.rowHidden,
            +1
        );
    }

    private static <T extends SpreadsheetSelection> Optional<T> move(final T start,
                                                                     final Predicate<T> stop,
                                                                     final Predicate<T> hidden,
                                                                     final int delta) {
        T result = start;

        do {
            if (stop.test(result)) {
                result = hidden.test(start) ?
                    null :
                    start;
                break;
            }

            result = (T) result.addSaturated(delta);

        } while (hidden.test(result));

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<SpreadsheetColumnReference> leftPixels(final SpreadsheetColumnReference column,
                                                           final int pixels) {
        checkColumn(column);

        return movePixels(
            column,
            SpreadsheetColumnReference::isFirst,
            this.columnHidden,
            -1,
            this.columnWidths,
            pixels
        );
    }

    @Override
    public Optional<SpreadsheetColumnReference> rightPixels(final SpreadsheetColumnReference column,
                                                            final int pixels) {
        checkColumn(column);

        return movePixels(
            column,
            SpreadsheetColumnReference::isLast,
            this.columnHidden,
            +1,
            this.columnWidths,
            pixels
        );
    }

    private final Function<SpreadsheetColumnReference, Double> columnWidths;

    @Override
    public Optional<SpreadsheetRowReference> upPixels(final SpreadsheetRowReference row,
                                                      final int pixels) {
        checkRow(row);

        return movePixels(
            row,
            SpreadsheetRowReference::isFirst,
            this.rowHidden,
            -1,
            this.rowHeights,
            pixels
        );
    }

    @Override
    public Optional<SpreadsheetRowReference> downPixels(final SpreadsheetRowReference row,
                                                        final int pixels) {
        checkRow(row);

        return movePixels(
            row,
            SpreadsheetRowReference::isLast,
            this.rowHidden,
            +1,
            this.rowHeights,
            pixels
        );
    }

    private final Function<SpreadsheetRowReference, Double> rowHeights;


    private static <T extends SpreadsheetSelection> Optional<T> movePixels(final T start,
                                                                           final Predicate<T> stop,
                                                                           final Predicate<T> hidden,
                                                                           final int delta,
                                                                           final Function<T, Double> widthOrHeight,
                                                                           final int pixels) {
        if (pixels < 0) {
            throw new IllegalArgumentException("Invalid pixels " + pixels + " <= 0");
        }

        T moved = start;
        T lastNotHidden = start;

        double pixelCountdown = pixels;

        do {
            // might have reached first or last
            if (stop.test(moved)) {
                lastNotHidden = moved.equals(start) && hidden.test(moved) ?
                    null : // cant move and is hidden return nothing.
                    moved; // must have already been hidden tested so return it.
                break;
            }

            // advance moved
            moved = (T) moved.addSaturated(delta);
            if (hidden.test(moved)) {
                if (stop.test(moved)) {
                    break;
                }
                continue; // skip hidden result
            }
            lastNotHidden = moved;

            final double length = widthOrHeight.apply(moved);
            pixelCountdown = pixelCountdown - length;
            if (pixelCountdown < 0) {
                break; // moved enuff
            }

        } while (true);

        return Optional.ofNullable(lastNotHidden);
    }

    private static void checkColumn(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    private static void checkRow(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    @Override
    public SpreadsheetViewportWindows windows(final SpreadsheetViewportRectangle viewportRectangle,
                                              final boolean includeFrozenColumnsRows,
                                              final Optional<SpreadsheetSelection> selection) {
        return this.windows.windows(
            viewportRectangle,
            includeFrozenColumnsRows,
            selection
        );
    }

    private final SpreadsheetViewportWindowsFunction windows;

    @Override
    public String toString() {
        return this.columnHidden + " " + this.columnWidths + " " + this.rowHidden + " " + this.rowHeights + " " + this.windows;
    }
}
