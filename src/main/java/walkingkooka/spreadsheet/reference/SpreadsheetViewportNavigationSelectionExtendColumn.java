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

                SpreadsheetViewportAnchor newAnchor = anchor;
                if (SpreadsheetViewportAnchor.NONE == newAnchor) {
                    newAnchor =
                            selection.toColumn()
                                    .compareTo(newColumn) <= 0 ?
                                    SpreadsheetViewportAnchor.LEFT :
                                    SpreadsheetViewportAnchor.RIGHT;
                }

                switch (newAnchor) {
                    case LEFT:
                        anchored = range.setRange(
                                Range.greaterThanEquals(range.begin())
                                        .and(
                                                Range.lessThanEquals(newColumn)
                                        )
                        ).setAnchor(SpreadsheetViewportAnchor.LEFT);
                        break;
                    case RIGHT:
                        anchored = range.setRange(
                                Range.greaterThanEquals(newColumn)
                                        .and(
                                                Range.lessThanEquals(range.end())
                                        )
                        ).setAnchor(SpreadsheetViewportAnchor.RIGHT);
                        break;
                    default:
                        anchored = NeverError.unhandledEnum(
                                anchor,
                                SpreadsheetViewportAnchor.NONE, SpreadsheetViewportAnchor.LEFT, SpreadsheetViewportAnchor.RIGHT
                        );
                }
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
