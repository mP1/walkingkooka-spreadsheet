
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
}
