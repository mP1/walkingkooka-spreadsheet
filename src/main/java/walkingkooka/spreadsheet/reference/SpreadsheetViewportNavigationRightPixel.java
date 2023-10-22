

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

final class SpreadsheetViewportNavigationRightPixel extends SpreadsheetViewportNavigationNonExtendPixel {

    static SpreadsheetViewportNavigationRightPixel with(final int value) {
        return new SpreadsheetViewportNavigationRightPixel(value);
    }

    private SpreadsheetViewportNavigationRightPixel(final int value) {
        super(value);
    }

    @Override
    Optional<SpreadsheetSelection> updateHome(final SpreadsheetCellReference home,
                                              final SpreadsheetViewportAnchor anchor,
                                              final SpreadsheetViewportNavigationContext context) {
        return home.rightPixels(
                anchor,
                this.value,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                           final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context) {
        return selection.rightPixels(
                anchor,
                this.value,
                context
        ).map(s -> s.setAnchorOrDefault(anchor));
    }

    @Override
    public String text() {
        return "right " + this.value + "px";
    }
}
