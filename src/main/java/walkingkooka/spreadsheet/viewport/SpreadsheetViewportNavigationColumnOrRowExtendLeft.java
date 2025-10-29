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

import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

final class SpreadsheetViewportNavigationColumnOrRowExtendLeft extends SpreadsheetViewportNavigationColumnOrRow {

    final static SpreadsheetViewportNavigationColumnOrRowExtendLeft INSTANCE = new SpreadsheetViewportNavigationColumnOrRowExtendLeft();

    private SpreadsheetViewportNavigationColumnOrRowExtendLeft() {
        super();
    }

    @Override
    public String text() {
        return "extend-left column";
    }

    @Override
    boolean isOpposite(final SpreadsheetViewportNavigation other) {
        return other instanceof SpreadsheetViewportNavigationColumnOrRowExtendRight;
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                           final SpreadsheetViewportAnchor anchor,
                                                           final SpreadsheetViewportNavigationContext context) {
        return selection.extendLeftColumn(
            anchor,
            context
        );
    }
}
