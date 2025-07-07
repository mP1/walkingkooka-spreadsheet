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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Wraps another store and presents a readonly view.
 */
final class ReadOnlySpreadsheetLabelStore implements SpreadsheetLabelStore {

    static ReadOnlySpreadsheetLabelStore with(final SpreadsheetLabelStore store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetLabelStore(store);
    }

    private ReadOnlySpreadsheetLabelStore(SpreadsheetLabelStore store) {
        this.store = store;
    }

    @Override
    public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetLabelMapping save(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetLabelMapping> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final SpreadsheetLabelName id) {
        Objects.requireNonNull(id, "id");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetLabelName> deleted) {
        Objects.requireNonNull(deleted, "deleted");
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetLabelName> ids(final int offset,
                                         final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetLabelMapping> values(final int offset,
                                                final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetLabelMapping> between(final SpreadsheetLabelName from,
                                                 final SpreadsheetLabelName to) {
        return this.store.between(
            from,
            to
        );
    }

    @Override
    public Set<SpreadsheetLabelMapping> findSimilar(final String text,
                                                    final int count) {
        return this.store.findSimilar(text, count);
    }

    @Override
    public Set<SpreadsheetCellReferenceOrRange> loadCellOrCellRanges(final SpreadsheetLabelName label) {
        return this.store.loadCellOrCellRanges(label);
    }

    @Override
    public Set<SpreadsheetLabelMapping> findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                                final int offset,
                                                                final int count) {
        return this.store.findLabelsWithReference(
            reference,
            offset,
            count
        );
    }

    private final SpreadsheetLabelStore store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
