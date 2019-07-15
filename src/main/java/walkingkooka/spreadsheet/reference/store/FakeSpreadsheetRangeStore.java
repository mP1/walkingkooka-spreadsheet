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

package walkingkooka.spreadsheet.reference.store;

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.store.FakeSpreadsheetStore;
import walkingkooka.test.Fake;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FakeSpreadsheetRangeStore<V> extends FakeSpreadsheetStore<SpreadsheetRange, List<V>> implements SpreadsheetRangeStore<V>, Fake {

    @Override
    public Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addValue(final SpreadsheetRange range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replaceValue(final SpreadsheetRange range, final V newValue, final V oldValue) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(newValue, "newValue");
        Objects.requireNonNull(oldValue, "oldValue");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(final SpreadsheetRange range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetRange> rangesWithValue(final V value) {
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }
}
