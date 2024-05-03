
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
 * Represents a column being CLICKED in the viewport.
 * <br>
 * <pre>
 * select column A
 * select column BC
 * </pre>
 */
final class SpreadsheetViewportNavigationSelectionColumn extends SpreadsheetViewportNavigationSelection<SpreadsheetColumnReference> {

    static SpreadsheetViewportNavigationSelectionColumn with(final SpreadsheetColumnReference selection) {
        return new SpreadsheetViewportNavigationSelectionColumn(selection);
    }

    private SpreadsheetViewportNavigationSelectionColumn(final SpreadsheetColumnReference selection) {
        super(selection);
    }
}
