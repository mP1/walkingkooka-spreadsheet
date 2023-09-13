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

import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;

import java.util.Optional;

final class SpreadsheetViewportSelectionNavigationLeftColumn extends SpreadsheetViewportSelectionNavigation {

    final static SpreadsheetViewportSelectionNavigationLeftColumn INSTANCE = new SpreadsheetViewportSelectionNavigationLeftColumn();

    private SpreadsheetViewportSelectionNavigationLeftColumn() {
        super();
    }

    @Override
    public String text() {
        return "left column";
    }

    @Override
    boolean isOpposite(final SpreadsheetViewportSelectionNavigation other) {
        return other instanceof SpreadsheetViewportSelectionNavigationRightColumn;
    }

    @Override
    public Optional<SpreadsheetViewportSelection> update(final SpreadsheetSelection selection,
                                                         final SpreadsheetViewportSelectionAnchor anchor,
                                                         final SpreadsheetColumnStore columnStore,
                                                         final SpreadsheetRowStore rowStore) {
        return selection.leftColumn(
                anchor,
                columnStore,
                rowStore
        ).map(s -> s.setAnchorOrDefault(anchor));
    }
}
