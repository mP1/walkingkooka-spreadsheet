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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public final class SpreadsheetLabelReferencesStoreTestingTest implements SpreadsheetLabelReferencesStoreTesting<SpreadsheetLabelReferencesStoreTestingTest.TestSpreadsheetLabelReferencesStore> {

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddCellWithWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testDeleteDoesntFireDeleteWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testDeleteWithRemoveCellWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveCellWithWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveLastCellAddDeleteWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSaveCellsAddCellWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSaveCellsDoesntFireDeleteWatchers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSaveCellsReplaceAddCellWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSaveCellsReplaceAddCellWatcher2() {
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
    public Set<SpreadsheetCellReference> value() {
        return Set.of(
            SpreadsheetSelection.parseCell("B2"),
            SpreadsheetSelection.parseCell("C3")
        );
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
        public void saveCells(final SpreadsheetLabelName label,
                              final Set<SpreadsheetCellReference> cells) {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(cells, "cells");
        }

        @Override
        public void addCell(final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> labelAndReference) {
            Objects.requireNonNull(labelAndReference, "labelAndReference");
        }

        @Override
        public Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName>> watcher) {
            Objects.requireNonNull(watcher, "watcher");
            return null;
        }

        @Override
        public void removeCell(final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> labelAndReference) {
            Objects.requireNonNull(labelAndReference, "labelAndReference");
        }

        @Override
        public Set<SpreadsheetCellReference> findCellsWithReference(final SpreadsheetLabelName label,
                                                                    final int offset,
                                                                    final int count) {
            Objects.requireNonNull(label, "label");
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return Set.of();
        }

        @Override
        public int countCellsWithReference(final SpreadsheetLabelName label) {
            Objects.requireNonNull(label, "label");
            return 0;
        }

        @Override
        public Runnable addRemoveCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName>> watcher) {
            Objects.requireNonNull(watcher, "watcher");
            return null;
        }

        @Override
        public Set<SpreadsheetLabelName> findReferencesWithCell(final SpreadsheetCellReference cell,
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
        public void removeReferencesWithCell(final SpreadsheetCellReference cell) {
            Objects.requireNonNull(cell, "cell");
        }

        @Override
        public Optional<Set<SpreadsheetCellReference>> load(final SpreadsheetLabelName label) {
            Objects.requireNonNull(label, "label");
            return Optional.empty();
        }

        @Override
        public void delete(final SpreadsheetLabelName label) {
            Objects.requireNonNull(label, "label");
        }

        @Override
        public Runnable addDeleteWatcher(final Consumer<SpreadsheetLabelName> watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
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
        public List<Set<SpreadsheetCellReference>> values(final int offset,
                                                          final int count) {
            Store.checkOffsetAndCount(
                offset,
                count
            );
            return List.of();
        }

        @Override
        public List<Set<SpreadsheetCellReference>> between(final SpreadsheetLabelName from,
                                                           final SpreadsheetLabelName to) {
            Store.checkBetween(
                from,
                to
            );
            return List.of();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
