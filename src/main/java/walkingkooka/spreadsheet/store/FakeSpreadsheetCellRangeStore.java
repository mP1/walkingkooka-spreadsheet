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
import walkingkooka.store.FakeStore;
import walkingkooka.test.Fake;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FakeSpreadsheetCellRangeStore extends FakeStore<SpreadsheetCellRangeReference, List<SpreadsheetCellReference>> implements SpreadsheetCellRangeStore, Fake {

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellReference> findValuesWithCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addValue(final SpreadsheetCellRangeReference range,
                         final SpreadsheetCellReference value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(final SpreadsheetCellRangeReference range,
                            final SpreadsheetCellReference value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesWithValue(final SpreadsheetCellReference value) {
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }
}
