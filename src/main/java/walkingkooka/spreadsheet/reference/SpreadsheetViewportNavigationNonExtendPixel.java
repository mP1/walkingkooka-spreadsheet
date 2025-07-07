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

abstract class SpreadsheetViewportNavigationNonExtendPixel extends SpreadsheetViewportNavigationPixel {

    SpreadsheetViewportNavigationNonExtendPixel(final int value) {
        super(value);
    }

    @Override
    final Optional<AnchoredSpreadsheetSelection> updateViewportSelection(final AnchoredSpreadsheetSelection anchoredSelection,
                                                                         final SpreadsheetViewportRectangle rectangle,
                                                                         final SpreadsheetViewportNavigationContext context) {
        // check if original selection is within the moved viewport
        final SpreadsheetViewportWindows windows = context.windows(
            rectangle,
            true, //includeFrozenColumnsRows
            SpreadsheetViewportWindowsFunction.NO_SELECTION
        );

        return windows.test(
            anchoredSelection.anchor()
                .opposite()
                .selection(
                    anchoredSelection.selection()
                )
        ) ?
            Optional.of(anchoredSelection) :
            SpreadsheetViewport.NO_ANCHORED_SELECTION;
    }
}
