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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A store that contains {@link SpreadsheetColumn} including some methods that for frequent queries.
 */
public interface SpreadsheetColumnStore extends SpreadsheetColumnOrRowStore<SpreadsheetColumnReference, SpreadsheetColumn> {

    /**
     * Attempts to load all the columns in the given {@link SpreadsheetColumnReferenceRange}.
     */
    default Set<SpreadsheetColumn> loadColumns(final SpreadsheetColumnReferenceRange range) {
        Objects.requireNonNull(range, "ranges");

        final Set<SpreadsheetColumn> columns = Sets.sorted();

        for (final SpreadsheetColumnReference columnReference : range) {
            final Optional<SpreadsheetColumn> column = this.load(columnReference);
            if (column.isPresent()) {
                columns.add(column.get());
            }
        }

        return Sets.readOnly(columns);
    }

    /**
     * Attempts to save all the columns.
     */
    default void saveColumns(final Set<SpreadsheetColumn> columns) {
        Objects.requireNonNull(columns, "columns");

        for (final SpreadsheetColumn column : columns) {
            this.save(column);
        }
    }

    /**
     * Returns the first column moving left from the given starting point that is not hidden.
     * If all columns to the left are hidden, the original {@link SpreadsheetColumnReference}, if this is hidden an
     * {@link Optional#empty()}.
     */
    Optional<SpreadsheetColumnReference> leftColumnSkipHidden(final SpreadsheetColumnReference reference);

    /**
     * Returns the first column moving right from the given starting point that is not hidden.
     * If all columns to the right are hidden, the original {@link SpreadsheetColumnReference}, if this is hidden an
     * {@link Optional#empty()}.
     */
    Optional<SpreadsheetColumnReference> rightSkipHidden(final SpreadsheetColumnReference reference);
}
