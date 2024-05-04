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
 * Represents a column being SHIFT CLICKED in the viewport.
 * <br>
 * <pre>
 * extend column A
 * extend column BC
 * </pre>
 */
final class SpreadsheetViewportNavigationSelectionExtendColumn extends SpreadsheetViewportNavigationSelectionExtend<SpreadsheetColumnReference> {

    static SpreadsheetViewportNavigationSelectionExtendColumn with(final SpreadsheetColumnReference selection) {
        return new SpreadsheetViewportNavigationSelectionExtendColumn(selection);
    }

    private SpreadsheetViewportNavigationSelectionExtendColumn(final SpreadsheetColumnReference selection) {
        super(selection);
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                           final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context) {
        final AnchoredSpreadsheetSelection anchored;

        final SpreadsheetColumnReference newColumn = this.selection;

        if (selection.isColumnReference() || selection.isColumnRangeReference()) {
            if (newColumn.equalsIgnoreReferenceKind(selection.toColumn()) && selection.count() == 1) {
                anchored = selection.setAnchor(anchor);
            } else {
                final SpreadsheetColumnRangeReference range = selection.toColumnRange();

                final SpreadsheetColumnReference left = range.begin();
                final SpreadsheetColumnReference right = range.end();

                final SpreadsheetColumnReference newLeft = newColumn.min(left);
                final SpreadsheetColumnReference newRight = newColumn.max(right);

                SpreadsheetViewportAnchor newAnchor;

                // try to compute the anchor for the furthest column
                if (newColumn.compareTo(left) <= 0) {
                    newAnchor = SpreadsheetViewportAnchor.RIGHT;
                } else {
                    newAnchor = SpreadsheetViewportAnchor.LEFT;
                }

                anchored = range.setRange(
                        Range.greaterThanEquals(newLeft)
                                .and(
                                        Range.lessThanEquals(newRight)
                                )
                ).setAnchor(newAnchor);
            }
        } else {
            // previous selection was not a column/column-range
            anchored = newColumn.setDefaultAnchor();
        }

        return Optional.ofNullable(anchored);
    }

    @Override
    Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                  final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
                this.selection.setRow(home.row())
        );
    }
}
