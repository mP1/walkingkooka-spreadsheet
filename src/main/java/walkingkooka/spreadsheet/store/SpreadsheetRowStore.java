
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
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.value.SpreadsheetRow;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A store that contains {@link SpreadsheetRow} including some methods that for frequent queries.
 */
public interface SpreadsheetRowStore extends SpreadsheetColumnOrRowStore<SpreadsheetRowReference, SpreadsheetRow> {

    /**
     * Attempts to load all the rows in the given {@link SpreadsheetRowRangeReference}.
     */
    default Set<SpreadsheetRow> loadRows(final SpreadsheetRowRangeReference range) {
        Objects.requireNonNull(range, "ranges");

        final Set<SpreadsheetRow> rows = SortedSets.tree(SpreadsheetRow.REFERENCE_COMPARATOR);

        for (final SpreadsheetRowReference rowReference : range) {
            final Optional<SpreadsheetRow> row = this.load(rowReference);
            if (row.isPresent()) {
                rows.add(row.get());
            }
        }

        return Sets.readOnly(rows);
    }

    /**
     * Attempts to save all the rows.
     */
    default void saveRows(final Set<SpreadsheetRow> rows) {
        Objects.requireNonNull(rows, "rows");

        for (final SpreadsheetRow row : rows) {
            this.save(row);
        }
    }
}
