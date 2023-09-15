
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
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A store that contains {@link SpreadsheetRow} including some methods that for frequent queries.
 */
public interface SpreadsheetRowStore extends SpreadsheetColumnOrRowStore<SpreadsheetRowReference, SpreadsheetRow> {

    /**
     * Attempts to load all the rows in the given {@link SpreadsheetRowReferenceRange}.
     */
    default Set<SpreadsheetRow> loadRows(final SpreadsheetRowReferenceRange range) {
        Objects.requireNonNull(range, "ranges");

        final Set<SpreadsheetRow> rows = Sets.sorted();

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

    /**
     * Returns the first row moving up from the given starting point that is not hidden.
     * If all rows above are hidden the original {@link SpreadsheetRowReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetRowReference> upRowSkipHidden(final SpreadsheetRowReference reference);

    /**
     * Returns the last row moving down from the given starting point that is not hidden.
     * If all rows below are hidden the original {@link SpreadsheetRowReference} is returned,
     * providing it is also not hidden or {@link Optional#empty()}.
     * <br>
     * This method is used to support keyboard navigation.
     */
    Optional<SpreadsheetRowReference> downRowSkipHidden(final SpreadsheetRowReference reference);
}
