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
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

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
        final SpreadsheetRowReference newRow = this.selection;

        return Optional.of(
            selection.isRow() || selection.isRowRange() ?
                rowToAnchored(
                    selection,
                    anchor,
                    newRow
                ) :
                newRow.setDefaultAnchor()
        );
    }

    @Override
    Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                  final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
            this.selection.setColumn(home.column())
        );
    }
}
