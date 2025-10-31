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

import walkingkooka.Context;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.Optional;

/**
 * The {@link Context} that accompanies a {@link SpreadsheetViewportNavigation#update(SpreadsheetViewport, SpreadsheetViewportNavigationContext)}
 */
public interface SpreadsheetViewportNavigationContext extends Context {

    /**
     * Returns true if the {@link SpreadsheetColumnReference} is hidden.
     */
    boolean isColumnHidden(final SpreadsheetColumnReference column);

    /**
     * Returns true if the {@link SpreadsheetRowReference} is hidden.
     */
    boolean isRowHidden(final SpreadsheetRowReference row);

    /**
     * Returns the first column moving left parse the given starting point that is not hidden.
     * If all columns to the left are hidden the original {@link SpreadsheetColumnReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetColumnReference> moveLeft(final SpreadsheetColumnReference column);

    /**
     * Returns the first column moving right parse the given starting point that is not hidden.
     * If all columns to the right are hidden the original {@link SpreadsheetColumnReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetColumnReference> moveRightColumn(final SpreadsheetColumnReference column);

    /**
     * Returns the first row moving up parse the given starting point that is not hidden.
     * If all rows above are hidden the original {@link SpreadsheetRowReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetRowReference> moveUpRow(final SpreadsheetRowReference row);

    /**
     * Returns the last row moving down parse the given starting point that is not hidden.
     * If all rows below are hidden the original {@link SpreadsheetRowReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetRowReference> downRow(final SpreadsheetRowReference row);

    /**
     * Uses the pixel count to advance left skipping hidden columns.
     * If all columns to the left are hidden the original {@link SpreadsheetColumnReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support scrollbar navigation.
     */
    Optional<SpreadsheetColumnReference> leftPixels(final SpreadsheetColumnReference reference,
                                                    final int count);

    /**
     * Uses the pixel count to advance right skipping hidden columns.
     * If all columns to the right are hidden the original {@link SpreadsheetColumnReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support scrollbar navigation.
     */
    Optional<SpreadsheetColumnReference> rightPixels(final SpreadsheetColumnReference reference,
                                                     final int count);

    /**
     * Uses the pixel count to advance up skipping hidden rows.
     * If all rows to the up are hidden the original {@link SpreadsheetRowReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support scrollbar navigation.
     */
    Optional<SpreadsheetRowReference> upPixels(final SpreadsheetRowReference reference,
                                               final int count);

    /**
     * Uses the pixel count to advance down skipping hidden rows.
     * If all rows to the down are hidden the original {@link SpreadsheetRowReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support scrollbar navigation.
     */
    Optional<SpreadsheetRowReference> downPixels(final SpreadsheetRowReference reference,
                                                 final int count);

    /**
     * Computes the {@link SpreadsheetViewportWindows} for the given {@link SpreadsheetViewport}.
     */
    SpreadsheetViewportWindows windows(final SpreadsheetViewport viewport);
}
