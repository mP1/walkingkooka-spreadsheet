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

import walkingkooka.NeverError;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

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
        return selection.isCell() ||
            selection.isCellRange() ?
            this.updateCellOrCellRange(
                selection,
                anchor
            ) :
            Optional.of(
                this.selection.setDefaultAnchor()
            );
    }

    private Optional<AnchoredSpreadsheetSelection> updateCellOrCellRange(final SpreadsheetSelection selection,
                                                                         final SpreadsheetViewportAnchor anchor) {
        final SpreadsheetCellReference newCell = this.selection;

        final SpreadsheetViewportAnchor columnAnchor;
        final SpreadsheetViewportAnchor rowAnchor;

        switch (anchor) {
            case NONE:
                columnAnchor = SpreadsheetViewportAnchor.NONE;
                rowAnchor = SpreadsheetViewportAnchor.NONE;
                break;
            case TOP_LEFT:
                columnAnchor = SpreadsheetViewportAnchor.LEFT;
                rowAnchor = SpreadsheetViewportAnchor.TOP;
                break;
            case TOP_RIGHT:
                columnAnchor = SpreadsheetViewportAnchor.RIGHT;
                rowAnchor = SpreadsheetViewportAnchor.TOP;
                break;
            case BOTTOM_LEFT:
                columnAnchor = SpreadsheetViewportAnchor.LEFT;
                rowAnchor = SpreadsheetViewportAnchor.BOTTOM;
                break;
            case BOTTOM_RIGHT:
                columnAnchor = SpreadsheetViewportAnchor.RIGHT;
                rowAnchor = SpreadsheetViewportAnchor.BOTTOM;
                break;
            default:
                columnAnchor = null;
                rowAnchor = null;
                NeverError.unhandledEnum(
                    anchor,
                    SpreadsheetViewportAnchor.NONE,
                    SpreadsheetViewportAnchor.TOP_LEFT,
                    SpreadsheetViewportAnchor.TOP_RIGHT,
                    SpreadsheetViewportAnchor.BOTTOM_LEFT,
                    SpreadsheetViewportAnchor.BOTTOM_RIGHT
                );
        }

        final AnchoredSpreadsheetSelection anchoredColumn = columnToAnchored(
            selection.isCell() ?
                selection.toColumn() :
                selection.isCellRange() ?
                    selection.toColumnRange() :
                    selection,
            columnAnchor,
            newCell.toColumn()
        );

        final AnchoredSpreadsheetSelection anchoredRow = rowToAnchored(
            selection.isCell() ?
                selection.toRow() :
                selection.isCellRange() ?
                    selection.toRowRange() :
                    selection,
            rowAnchor,
            newCell.toRow()
        );

        final AnchoredSpreadsheetSelection newAnchored;
        final SpreadsheetSelection newSelectionColumn = anchoredColumn.selection();
        final SpreadsheetSelection newSelectionRow = anchoredRow.selection();

        if (newSelectionColumn.count() == 1 && newSelectionRow.count() == 1) {
            newAnchored = newSelectionColumn.toColumn()
                .setRow(
                    newSelectionRow.toRow()
                ).setDefaultAnchor();
        } else {
            final SpreadsheetViewportAnchor newAnchor;

            final SpreadsheetViewportAnchor anchoredColumnAnchor = anchoredColumn.anchor();
            final SpreadsheetViewportAnchor anchoredRowAnchor = anchoredRow.anchor();

            switch (anchoredColumnAnchor) {
                case NONE:
                case LEFT:
                    switch (anchoredRowAnchor) {
                        case NONE:
                        case TOP:
                            newAnchor = SpreadsheetViewportAnchor.TOP_LEFT;
                            break;
                        case BOTTOM:
                            newAnchor = SpreadsheetViewportAnchor.BOTTOM_LEFT;
                            break;
                        default:
                            newAnchor = NeverError.unhandledEnum(
                                anchor,
                                SpreadsheetViewportAnchor.NONE,
                                SpreadsheetViewportAnchor.TOP,
                                SpreadsheetViewportAnchor.BOTTOM
                            );
                            break;
                    }
                    break;
                case RIGHT:
                    switch (anchoredRowAnchor) {
                        case NONE:
                        case TOP:
                            newAnchor = SpreadsheetViewportAnchor.TOP_RIGHT;
                            break;
                        case BOTTOM:
                            newAnchor = SpreadsheetViewportAnchor.BOTTOM_RIGHT;
                            break;
                        default:
                            newAnchor = NeverError.unhandledEnum(
                                anchor,
                                SpreadsheetViewportAnchor.NONE,
                                SpreadsheetViewportAnchor.TOP,
                                SpreadsheetViewportAnchor.BOTTOM
                            );
                            break;
                    }
                    break;
                default:
                    newAnchor = NeverError.unhandledEnum(
                        anchor,
                        SpreadsheetViewportAnchor.NONE,
                        SpreadsheetViewportAnchor.LEFT,
                        SpreadsheetViewportAnchor.RIGHT
                    );
                    break;
            }

            newAnchored = newSelectionColumn.toColumnRange()
                .setRowRange(
                    newSelectionRow.toRowRange()
                ).setAnchor(newAnchor);
        }

        return Optional.of(newAnchored);
    }

    @Override
    Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                  final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
            this.selection.toCell()
        );
    }
}
