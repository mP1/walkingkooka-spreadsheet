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

package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Store} that holds one or more values for {@link SpreadsheetCellRangeReference}.
 * For parameter types {@link SpreadsheetCellReference} and {@link SpreadsheetCellRangeReference} the {@link walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind}
 * is ignored passing either variant must return the same results.
 * All returned {@link SpreadsheetCellReference} including those within a {@link SpreadsheetCellRangeReference} will be relative and never absolute.
 */
public interface SpreadsheetCellRangeStore extends SpreadsheetStore<SpreadsheetCellRangeReference, List<SpreadsheetCellReference>> {

    /**
     * Values dont include the actual range, therefore this {@link Store} method is invalid and throws {@link UnsupportedOperationException}.
     */
    @Override
    default List<SpreadsheetCellReference> save(final List<SpreadsheetCellReference> value) {
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    /**
     * Finds all {@link SpreadsheetCellRangeReference} that include the given {@link SpreadsheetCellReference}.
     */
    Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellReference cell);

    /**
     * Load all the values for a single cell.
     */
    Set<SpreadsheetCellReference> findValuesWithCell(final SpreadsheetCellReference cell);

    /**
     * Add a single value to the given {@link SpreadsheetCellRangeReference}. If the mapping exists nothing happens.
     */
    void addValue(final SpreadsheetCellRangeReference range,
                  final SpreadsheetCellReference value);

    /**
     * Removes a single value if it exists for the given {@link SpreadsheetCellRangeReference}
     */
    void removeValue(final SpreadsheetCellRangeReference range,
                     final SpreadsheetCellReference value);

    /**
     * Returns all the {@link SpreadsheetCellRangeReference ranges} containing the given value.
     */
    Set<SpreadsheetCellRangeReference> findCellRangesWithValue(final SpreadsheetCellReference value);
}
