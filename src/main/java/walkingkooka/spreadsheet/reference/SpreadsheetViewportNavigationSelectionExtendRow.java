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
 * Represents a row being SHIFT CLICKED in the viewport.
 * <br>
 * <pre>
 * extend row 1
 * extend row 12
 * </pre>
 */
final class SpreadsheetViewportNavigationSelectionExtendRow extends SpreadsheetViewportNavigationSelectionExtend<SpreadsheetRowReference> {

    static SpreadsheetViewportNavigationSelectionExtendRow with(final SpreadsheetRowReference selection) {
        return new SpreadsheetViewportNavigationSelectionExtendRow(selection);
    }

    private SpreadsheetViewportNavigationSelectionExtendRow(final SpreadsheetRowReference selection) {
        super(selection);
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                           final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context) {
        final AnchoredSpreadsheetSelection anchored;

        final SpreadsheetRowReference newRow = this.selection;

        if (selection.isRowReference() || selection.isRowRangeReference()) {
            if (newRow.equalsIgnoreReferenceKind(selection.toRow()) && selection.count() == 1) {
                anchored = selection.setAnchor(anchor);
            } else {
                final SpreadsheetRowRangeReference range = selection.toRowRange();

                final SpreadsheetRowReference top = range.begin();
                final SpreadsheetRowReference bottom = range.end();

                final SpreadsheetRowReference newTop = newRow.min(top);
                final SpreadsheetRowReference newBottom = newRow.max(bottom);

                SpreadsheetViewportAnchor newAnchor;

                // try to compute the anchor for the furthest row
                if (newRow.compareTo(top) <= 0) {
                    newAnchor = SpreadsheetViewportAnchor.BOTTOM;
                } else {
                    newAnchor = SpreadsheetViewportAnchor.TOP;
                }

                anchored = range.setRange(
                        Range.greaterThanEquals(newTop)
                                .and(
                                        Range.lessThanEquals(newBottom)
                                )
                ).setAnchor(newAnchor);
            }
        } else {
            // previous selection was not a row/row-range
            anchored = newRow.setDefaultAnchor();
        }

        return Optional.ofNullable(anchored);
    }

    @Override
    Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                  final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
                this.selection.setColumn(home.column())
        );
    }
}
