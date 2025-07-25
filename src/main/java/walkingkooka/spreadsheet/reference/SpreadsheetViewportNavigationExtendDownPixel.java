

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

final class SpreadsheetViewportNavigationExtendDownPixel extends SpreadsheetViewportNavigationExtendPixel {

    static SpreadsheetViewportNavigationExtendDownPixel with(final int value) {
        return new SpreadsheetViewportNavigationExtendDownPixel(value);
    }

    private SpreadsheetViewportNavigationExtendDownPixel(final int value) {
        super(value);
    }

    @Override
    Optional<SpreadsheetCellReference> updateHome(final SpreadsheetCellReference home,
                                                  final SpreadsheetViewportNavigationContext context) {
        return home.downPixels(
            SpreadsheetViewportAnchor.CELL,
            this.value,
            context
        ).map(SpreadsheetSelection::toCell);
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                           final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context) {
        return selection.extendDownPixels(
            anchor,
            this.value,
            context
        );
    }

    @Override
    public String text() {
        return "extend-down " + this.value + "px";
    }
}
