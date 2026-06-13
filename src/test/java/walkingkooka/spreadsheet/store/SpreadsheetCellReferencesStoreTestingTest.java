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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.Store;
import walkingkooka.store.StoreWatcher;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetCellReferencesStoreTestingTest implements SpreadsheetCellReferencesStoreTesting<SpreadsheetCellReferencesStoreTestingTest.TestSpreadsheetCellReferencesStore> {

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetCellReferencesStore createStore() {
        return new TestSpreadsheetCellReferencesStore();
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.A1;
    }

    @Override
    public Set<SpreadsheetCellReference> value() {
        return Set.of(
            SpreadsheetSelection.parseCell("B2"),
            SpreadsheetSelection.parseCell("C3")
        );
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetCellReferencesStore> type() {
        return TestSpreadsheetCellReferencesStore.class;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }

    static class TestSpreadsheetCellReferencesStore implements SpreadsheetCellReferencesStore {

        @Override
        public void addValue(final SpreadsheetCellReference cell,
                             final SpreadsheetCellReference value) {
            Objects.requireNonNull(cell, "cell");
            Objects.requireNonNull(value, "value");
        }

        @Override
        public void removeValue(final SpreadsheetCellReference reference,
                                final SpreadsheetCellReference cell) {
            Objects.requireNonNull(reference, "reference");
            Objects.requireNonNull(cell, "cell");
        }

        @Override
        public Set<SpreadsheetCellReference> findValuesById(final SpreadsheetCellReference reference,
                                                            final int offset,
                                                            final int count) {
            Objects.requireNonNull(reference, "reference");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Set.of();
        }

        @Override
        public Set<SpreadsheetCellReference> findCellsWithCellOrCellRange(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                                          final int offset,
                                                                          final int count) {
            Objects.requireNonNull(cellOrCellRange, "cellOrCellRange");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Set.of();
        }

        @Override
        public Set<SpreadsheetCellReference> findReferencesWithCell(final SpreadsheetCellReference cell,
                                                                    final int offset,
                                                                    final int count) {
            Objects.requireNonNull(cell, "cell");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Set.of();
        }

        @Override
        public void removeByValue(final SpreadsheetCellReference cell) {
            Objects.requireNonNull(cell, "cell");
        }

        @Override
        public Optional<Set<SpreadsheetCellReference>> load(final SpreadsheetCellReference reference) {
            Objects.requireNonNull(reference, "reference");
            return Optional.empty();
        }

        @Override
        public void delete(final SpreadsheetCellReference reference) {
            Objects.requireNonNull(reference, "reference");
        }

        @Override
        public int count() {
            return 0;
        }

        @Override
        public Set<SpreadsheetCellReference> ids(final int offset,
                                                 final int count) {
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Set.of();
        }

        @Override
        public List<Set<SpreadsheetCellReference>> values(final int offset,
                                                          final int count) {
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return List.of();
        }

        @Override
        public List<Set<SpreadsheetCellReference>> between(final SpreadsheetCellReference from,
                                                           final SpreadsheetCellReference to) {
            Store.checkBetween(
                from,
                to
            );
            return List.of();
        }

        @Override
        public Runnable addStoreWatcher(final StoreWatcher<Set<SpreadsheetCellReference>> watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
