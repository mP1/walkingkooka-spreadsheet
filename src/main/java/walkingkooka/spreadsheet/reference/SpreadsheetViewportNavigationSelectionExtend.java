
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

import walkingkooka.NeverError;
import walkingkooka.collect.Range;
import walkingkooka.compare.Comparators;

import java.util.Optional;

/**
 * Base class for any navigation involving a cell, column or row, such as a MOUSE SHIFT click to extend an existing selection.
 */
abstract class SpreadsheetViewportNavigationSelectionExtend<T extends SpreadsheetSelection> extends SpreadsheetViewportNavigationSelection<T> {

    SpreadsheetViewportNavigationSelectionExtend(final T selection) {
        super(selection);
    }

    @Override //
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

    @Override //
    final String textPrefix() {
        return EXTEND_SPACE;
    }

    static AnchoredSpreadsheetSelection columnToAnchored(final SpreadsheetSelection selection,
                                                         final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetColumnReference newColumn) {
        final AnchoredSpreadsheetSelection anchored;

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
            final SpreadsheetColumnReference other = anchor.column(range);

            final int leftCompare = Comparators.normalize(newColumn.compareTo(other));
            switch (leftCompare) {
                case -1:
                    anchored = columnRange(
                        newColumn,
                        other
                    ).setAnchor(SpreadsheetViewportAnchor.RIGHT);
                    break;
                case 0:
                    anchored = newColumn.setDefaultAnchor();
                    break;
                case +1:
                    anchored = columnRange(
                        other,
                        newColumn
                    ).setAnchor(SpreadsheetViewportAnchor.LEFT);
                    break;
                default:
                    anchored = null;
                    NeverError.unhandledCase(
                        leftCompare,
                        -1,
                        0,
                        +1
                    );
            }
        }
        return anchored;
    }

    static AnchoredSpreadsheetSelection rowToAnchored(final SpreadsheetSelection selection,
                                                      final SpreadsheetViewportAnchor anchor,
                                                      final SpreadsheetRowReference newRow) {
        final AnchoredSpreadsheetSelection anchored;

        if (selection.count() == 1) {
            final SpreadsheetRowReference row = selection.toRow();
            final int compare = newRow.compareTo(row);
            if (0 == compare) {
                anchored = selection.setAnchor(anchor);
            } else {
                if (compare < 0) {
                    anchored = rowRange(
                        newRow,
                        row
                    ).setAnchor(SpreadsheetViewportAnchor.BOTTOM);
                } else {
                    anchored = rowRange(
                        row,
                        newRow
                    ).setAnchor(SpreadsheetViewportAnchor.TOP);
                }
            }
        } else {
            final SpreadsheetRowRangeReference range = selection.toRowRange();
            final SpreadsheetRowReference other = anchor.row(range);

            final int topCompare = Comparators.normalize(newRow.compareTo(other));
            switch (topCompare) {
                case -1:
                    anchored = rowRange(
                        newRow,
                        other
                    ).setAnchor(SpreadsheetViewportAnchor.BOTTOM);
                    break;
                case 0:
                    anchored = newRow.setDefaultAnchor();
                    break;
                case +1:
                    anchored = rowRange(
                        other,
                        newRow
                    ).setAnchor(SpreadsheetViewportAnchor.TOP);
                    break;
                default:
                    anchored = null;
                    NeverError.unhandledCase(
                        topCompare,
                        -1,
                        0,
                        +1
                    );
            }
        }
        return anchored;
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

    private static SpreadsheetRowRangeReference rowRange(final SpreadsheetRowReference left,
                                                         final SpreadsheetRowReference right) {
        return SpreadsheetSelection.rowRange(
            range(
                left,
                right
            )
        );
    }

    private static <TT extends SpreadsheetSelection & Comparable<TT>> Range<TT> range(final TT left,
                                                                                      final TT right) {
        return Range.greaterThanEquals(left)
            .and(
                Range.lessThanEquals(right)
            );
    }
}
