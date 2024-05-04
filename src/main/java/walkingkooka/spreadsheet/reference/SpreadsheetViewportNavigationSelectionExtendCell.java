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
 * Represents a cell being SHIFT CLICKED in the viewport.
 * <br>
 * <pre>
 * extend cell A1
 * </pre>
 */
final class SpreadsheetViewportNavigationSelectionExtendCell extends SpreadsheetViewportNavigationSelectionExtend<SpreadsheetCellReference> {

    static SpreadsheetViewportNavigationSelectionExtendCell with(final SpreadsheetCellReference selection) {
        return new SpreadsheetViewportNavigationSelectionExtendCell(selection);
    }

    private SpreadsheetViewportNavigationSelectionExtendCell(final SpreadsheetCellReference selection) {
        super(selection);
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                           final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context) {
        final AnchoredSpreadsheetSelection anchored;

        final SpreadsheetCellReference newCell = this.selection;

        if (selection.isCellReference() || selection.isCellRangeReference()) {
            if (newCell.equalsIgnoreReferenceKind(selection) && selection.count() == 1) {
                anchored = selection.setAnchor(anchor);
            } else {
                final SpreadsheetCellRangeReference range = selection.toCellRange();

                final SpreadsheetCellReference topLeft = range.begin();
                final SpreadsheetCellReference bottomRight = range.end();

                SpreadsheetColumnReference left = topLeft.column();
                SpreadsheetRowReference top = topLeft.row();
                SpreadsheetColumnReference right = bottomRight.column();
                SpreadsheetRowReference bottom = bottomRight.row();

                final SpreadsheetColumnReference column = newCell.column();
                final SpreadsheetRowReference row = newCell.row();

                final SpreadsheetColumnReference c;
                final SpreadsheetRowReference r;

                switch (anchor) {
                    case NONE:
                    case TOP_LEFT:
                        c = left;
                        r = top;
                        break;
                    case TOP_RIGHT:
                        c = right;
                        r = top;
                        break;
                    case BOTTOM_LEFT:
                        c = left;
                        r = bottom;
                        break;
                    case BOTTOM_RIGHT:
                        c = right;
                        r = bottom;
                        break;
                    default:
                        c = null;
                        r = null;
                        NeverError.unhandledEnum(
                                anchor,
                                SpreadsheetViewportAnchor.NONE,
                                SpreadsheetViewportAnchor.TOP_LEFT,
                                SpreadsheetViewportAnchor.TOP_RIGHT,
                                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                                SpreadsheetViewportAnchor.BOTTOM_RIGHT
                        );
                }

                left = c.min(column);
                right = c.max(column);
                top = r.min(row);
                bottom = r.max(row);

                final SpreadsheetViewportAnchor newAnchor;

                if (left.equalsIgnoreReferenceKind(topLeft.column())) {
                    // TOP_LEFT | BOTTOM_LEFT
                    if (top.equalsIgnoreReferenceKind(topLeft.row())) {
                        newAnchor = SpreadsheetViewportAnchor.TOP_LEFT;
                    } else {
                        newAnchor = SpreadsheetViewportAnchor.BOTTOM_LEFT;
                    }
                } else {
                    // TOP_RIGHT | BOTTOM_RIGHT
                    if (top.equalsIgnoreReferenceKind(topLeft.row())) {
                        newAnchor = SpreadsheetViewportAnchor.TOP_RIGHT;
                    } else {
                        newAnchor = SpreadsheetViewportAnchor.BOTTOM_RIGHT;
                    }
                }

                anchored = setRange(
                        range,
                        left.setRow(top),
                        right.setRow(bottom),
                        newAnchor
                );
            }
        } else {
            // previous selection was not a cell/cell-range
            anchored = newCell.setDefaultAnchor();
        }

        return Optional.of(anchored);
    }

    private static AnchoredSpreadsheetSelection setRange(final SpreadsheetCellRangeReference range,
                                                         final SpreadsheetCellReference topLeft,
                                                         final SpreadsheetCellReference bottomRight,
                                                         final SpreadsheetViewportAnchor anchor) {
        return range.setRange(
                Range.greaterThanEquals(topLeft)
                        .and(
                                Range.lessThanEquals(bottomRight)
                        )
        ).setAnchor(anchor);
    }

    @Override
    Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                  final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
                this.selection.toCell()
        );
    }
}
