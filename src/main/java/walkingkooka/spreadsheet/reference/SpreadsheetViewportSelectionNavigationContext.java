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

import walkingkooka.Context;

import java.util.Optional;

/**
 * The {@link Context} that accompanies a {@link SpreadsheetViewportSelectionNavigation#update(SpreadsheetSelection, SpreadsheetViewportSelectionAnchor, SpreadsheetViewportSelectionNavigationContext)}
 */
public interface SpreadsheetViewportSelectionNavigationContext extends Context {

    /**
     * Returns true if the {@link SpreadsheetColumnReference} is hidden.
     */
    boolean isColumnHidden(SpreadsheetColumnReference column);

    /**
     * Returns true if the {@link SpreadsheetRowReference} is hidden.
     */
    boolean isRowHidden(SpreadsheetRowReference row);

    /**
     * Returns the first column moving left from the given starting point that is not hidden.
     * If all columns to the left are hidden the original {@link SpreadsheetColumnReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetColumnReference> leftColumnSkipHidden(final SpreadsheetColumnReference reference);
}
