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
 * Base class for any navigation involving a cell, column or row, such as a MOUSE click to select.
 */
abstract class SpreadsheetViewportNavigationSelectionSelect<T extends SpreadsheetSelection> extends SpreadsheetViewportNavigationSelection<T> {

    SpreadsheetViewportNavigationSelectionSelect(final T selection) {
        super(selection);
    }

    @Override final SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                                final SpreadsheetViewportNavigationContext context) {
        return this.updateViewport(
            this.selection.setDefaultAnchor(),
            viewport,
            context
        );
    }

    @Override final Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                                           final SpreadsheetViewportAnchor anchor,
                                                                           final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
            this.selection.setDefaultAnchor()
        );
    }

    @Override final Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                                  final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
            this.selection.toCell()
        );
    }

    @Override final String textPrefix() {
        return SELECT_SPACE;
    }
}
