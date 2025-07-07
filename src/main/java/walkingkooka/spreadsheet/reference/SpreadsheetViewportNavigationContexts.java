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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetViewportWindowsFunction;

import java.util.function.Function;
import java.util.function.Predicate;

public final class SpreadsheetViewportNavigationContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetViewportNavigationContext}
     */
    public static SpreadsheetViewportNavigationContext basic(final Predicate<SpreadsheetColumnReference> columnHidden,
                                                             final Function<SpreadsheetColumnReference, Double> columnWidths,
                                                             final Predicate<SpreadsheetRowReference> rowHidden,
                                                             final Function<SpreadsheetRowReference, Double> rowHeights,
                                                             final SpreadsheetViewportWindowsFunction windows) {
        return BasicSpreadsheetViewportNavigationContext.with(
            columnHidden,
            columnWidths,
            rowHidden,
            rowHeights,
            windows
        );
    }

    /**
     * {@see FakeSpreadsheetViewportNavigationContext}
     */
    public static SpreadsheetViewportNavigationContext fake() {
        return new FakeSpreadsheetViewportNavigationContext();
    }

    /**
     * Stop creation.
     */
    private SpreadsheetViewportNavigationContexts() {
        throw new UnsupportedOperationException();
    }
}
