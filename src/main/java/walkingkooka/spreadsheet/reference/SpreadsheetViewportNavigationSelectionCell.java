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

/**
 * Represents a cell being CLICKED in the viewport.
 * <br>
 * <pre>
 * select cell A1
 * </pre>
 */
final class SpreadsheetViewportNavigationSelectionCell extends SpreadsheetViewportNavigationSelection<SpreadsheetCellReference> {

    static SpreadsheetViewportNavigationSelectionCell with(final SpreadsheetCellReference selection) {
        return new SpreadsheetViewportNavigationSelectionCell(selection);
    }

    private SpreadsheetViewportNavigationSelectionCell(final SpreadsheetCellReference selection) {
        super(selection);
    }
}
