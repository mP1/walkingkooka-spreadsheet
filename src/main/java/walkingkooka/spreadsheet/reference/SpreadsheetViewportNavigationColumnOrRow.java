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

import java.util.Optional;

/**
 * Base class for any navigation involving a column or row, such as a CURSOR DOWN or SHIFT CURSOR DOWN keyboard keys.
 */
abstract class SpreadsheetViewportNavigationColumnOrRow extends SpreadsheetViewportNavigation {

    SpreadsheetViewportNavigationColumnOrRow() {
        super();
    }

    @Override
    final SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                      final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = viewport;

        final Optional<AnchoredSpreadsheetSelection> maybeAnchored = viewport.anchoredSelection();
        if (maybeAnchored.isPresent()) {
            // selection present try and move it.
            final AnchoredSpreadsheetSelection selection = maybeAnchored.get();
            final Optional<AnchoredSpreadsheetSelection> maybeMovedSelection = this.updateSelection(
                    selection.selection(),
                    selection.anchor(),
                    context
            );

            if (maybeMovedSelection.isPresent()) {
                final AnchoredSpreadsheetSelection movedSelection = maybeMovedSelection.get();
                final SpreadsheetViewportRectangle rectangle = viewport.rectangle();

                // check if moved selection is within the original viewport
                final SpreadsheetViewportWindows windows = context.windows(
                        rectangle,
                        true, //includeFrozenColumnsRows
                        SpreadsheetViewportWindowsFunction.NO_SELECTION
                );

                if (
                        windows.test(
                                movedSelection.anchor()
                                        .opposite()
                                        .selection(
                                                movedSelection.selection()
                                        )
                        )
                ) {
                    // moved selection within windows leave home unmoved
                    result = viewport.setAnchoredSelection(maybeMovedSelection);
                } else {
                    // moved selection is outside viewport need to move home
                    final SpreadsheetCellReference home = rectangle.home();
                    final Optional<AnchoredSpreadsheetSelection> maybeMovedHome = this.updateSelection(
                            home,
                            SpreadsheetViewportAnchor.CELL,
                            context
                    );

                    if (maybeMovedHome.isPresent()) {
                        result = result.setRectangle(
                                rectangle.setHome(
                                        maybeMovedHome.get()
                                                .selection()
                                                .toCell()
                                )
                        );
                    } else {
                        result = result.setRectangle(
                                rectangle.setHome(home)
                        ).setAnchoredSelection(SpreadsheetViewport.NO_ANCHORED_SELECTION);
                    }
                }
            } else {
                result = viewport.setAnchoredSelection(SpreadsheetViewport.NO_ANCHORED_SELECTION);
            }
        }

        return result;
    }
}
