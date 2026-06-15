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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.MultiValueStoreWatcher;
import walkingkooka.store.Store;
import walkingkooka.store.StoreWatcher;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class SpreadsheetLabelReferencesStoreTestingTest implements SpreadsheetLabelReferencesStoreTesting<SpreadsheetLabelReferencesStoreTestingTest.TestSpreadsheetLabelReferencesStore> {

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherAndSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherAndDelete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetLabelReferencesStore createStore() {
        return new TestSpreadsheetLabelReferencesStore();
    }

    @Override
    public SpreadsheetLabelName id() {
        return SpreadsheetSelection.labelName("Label123");
    }

    @Override
    public SpreadsheetCellReference value() {
        return SpreadsheetSelection.parseCell("B2");
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetLabelReferencesStore> type() {
        return TestSpreadsheetLabelReferencesStore.class;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }

    static class TestSpreadsheetLabelReferencesStore implements SpreadsheetLabelReferencesStore {

        @Override
        public Set<SpreadsheetLabelName> findLabelsWithCellOrCellRange(final SpreadsheetCellReferenceOrRange cellOrCellRange,
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
        public void addValue(final SpreadsheetLabelName label,
                             final SpreadsheetCellReference value) {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(value, "value");
        }

        @Override
        public void removeValue(final SpreadsheetLabelName label,
                                final SpreadsheetCellReference value) {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(value, "value");
        }

        @Override
        public List<SpreadsheetCellReference> findValuesById(final SpreadsheetLabelName label,
                                                            final int offset,
                                                            final int count) {
            Objects.requireNonNull(label, "label");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Lists.empty();
        }

        @Override
        public List<SpreadsheetLabelName> findIdsByValue(final SpreadsheetCellReference cell,
                                                        final int offset,
                                                        final int count) {
            Objects.requireNonNull(cell, "cell");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Lists.of();
        }

        @Override
        public void removeByValue(final SpreadsheetCellReference cell) {
            Objects.requireNonNull(cell, "cell");
        }

        @Override
        public void delete(final SpreadsheetLabelName label) {
            Objects.requireNonNull(label, "label");
        }

        @Override
        public int count() {
            return 0;
        }

        @Override
        public Set<SpreadsheetLabelName> ids(final int offset,
                                             final int count) {
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Set.of();
        }

        @Override
        public List<SpreadsheetCellReference> values(final int offset,
                                                          final int count) {
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return List.of();
        }

        @Override
        public List<SpreadsheetCellReference> between(final SpreadsheetLabelName from,
                                                           final SpreadsheetLabelName to) {
            Store.checkBetween(
                from,
                to
            );
            return List.of();
        }

        @Override
        public Runnable addStoreWatcher(final StoreWatcher<SpreadsheetCellReference> watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addStoreWatcher(final MultiValueStoreWatcher<SpreadsheetLabelName, SpreadsheetCellReference> watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
