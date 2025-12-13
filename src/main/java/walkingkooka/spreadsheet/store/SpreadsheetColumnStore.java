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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A store that contains {@link SpreadsheetColumn} including some methods that for frequent queries.
 */
public interface SpreadsheetColumnStore extends SpreadsheetColumnOrRowStore<SpreadsheetColumnReference, SpreadsheetColumn> {

    /**
     * Attempts to load all the columns in the given {@link SpreadsheetColumnRangeReference}.
     */
    default Set<SpreadsheetColumn> loadColumns(final SpreadsheetColumnRangeReference range) {
        Objects.requireNonNull(range, "ranges");

        final Set<SpreadsheetColumn> columns = SortedSets.tree(SpreadsheetColumn.REFERENCE_COMPARATOR);

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
}
