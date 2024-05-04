
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

import walkingkooka.collect.Range;

import java.util.Optional;

/**
 * Base class for any navigation involving a cell, column or row, such as a MOUSE SHIFT click to extend an existing selection.
 */
abstract class SpreadsheetViewportNavigationSelectionExtend<T extends SpreadsheetSelection> extends SpreadsheetViewportNavigationSelection<T> {

    SpreadsheetViewportNavigationSelectionExtend(final T selection) {
        super(selection);
    }

    @Override
    final SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                      final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = viewport;

        final T selection = this.selection;

        // do nothing if new extend selection is hidden.
        if (false == selection.isHidden(context)) {
            final Optional<AnchoredSpreadsheetSelection> maybeAnchored = viewport.anchoredSelection();
            if (maybeAnchored.isPresent()) {
                // selection present try and move it.
                final AnchoredSpreadsheetSelection anchoredSelection = maybeAnchored.get();
                final Optional<AnchoredSpreadsheetSelection> maybeMovedSelection = this.updateSelection(
                        anchoredSelection.selection(),
                        anchoredSelection.anchor(),
                        context
                );

                if (maybeMovedSelection.isPresent()) {
                    final AnchoredSpreadsheetSelection movedSelection = maybeMovedSelection.get();
                    result = updateViewport(
                            movedSelection,
                            viewport,
                            context
                    );
                }
            } else {
                result = this.updateViewport(
                        selection.setDefaultAnchor(),
                        viewport,
                        context
                );
            }
        }

        return result;
    }

    @Override
    final String textPrefix() {
        return EXTEND_SPACE;
    }

    static AnchoredSpreadsheetSelection columnToAnchored(final SpreadsheetSelection selection,
                                                         final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetColumnReference newColumn) {
        final AnchoredSpreadsheetSelection anchored;

        if (selection.isColumnReference() || selection.isColumnRangeReference()) {
            if (selection.count() == 1) {
                final SpreadsheetColumnReference column = selection.toColumn();
                final int compare = newColumn.compareTo(column);
                if (0 == compare) {
                    anchored = selection.setAnchor(anchor);
                } else {
                    if (compare < 0) {
                        anchored = columnRange(
                                newColumn,
                                column
                        ).setAnchor(SpreadsheetViewportAnchor.RIGHT);
                    } else {
                        anchored = columnRange(
                                column,
                                newColumn
                        ).setAnchor(SpreadsheetViewportAnchor.LEFT);
                    }
                }
            } else {
                final SpreadsheetColumnRangeReference range = selection.toColumnRange();

                final SpreadsheetColumnReference left = range.begin();
                final SpreadsheetColumnReference right = range.end();

                final SpreadsheetColumnReference newLeft = newColumn.min(left);
                final SpreadsheetColumnReference newRight = newColumn.max(right);

                SpreadsheetViewportAnchor newAnchor;

                // try to compute the anchor for the furthest column
                if (diff(newColumn, left) <= diff(newColumn, right)) {
                    newAnchor = SpreadsheetViewportAnchor.RIGHT;
                } else {
                    newAnchor = SpreadsheetViewportAnchor.LEFT;
                }

                anchored = range.setRange(
                        range(
                                newLeft,
                                newRight
                        )
                ).setAnchor(newAnchor);
            }
        } else {
            // previous selection was not a column/column-range
            anchored = newColumn.setDefaultAnchor();
        }
        return anchored;
    }

    static <TT extends SpreadsheetColumnOrRowReference> int diff(final TT a,
                                                                 final TT b) {
        return Math.abs(a.value - b.value);
    }

    static <TT extends SpreadsheetColumnOrRowReference & Comparable<TT>> Range<TT> range(final TT left,
                                                                                         final TT right) {
        return Range.greaterThanEquals(left)
                .and(
                        Range.lessThanEquals(right)
                );
    }

    private static SpreadsheetColumnRangeReference columnRange(final SpreadsheetColumnReference left,
                                                               final SpreadsheetColumnReference right) {
        return SpreadsheetSelection.columnRange(
                range(
                        left,
                        right
                )
        );
    }

    static SpreadsheetRowRangeReference rowRange(final SpreadsheetRowReference left,
                                                 final SpreadsheetRowReference right) {
        return SpreadsheetSelection.rowRange(
                range(
                        left,
                        right
                )
        );
    }
}
