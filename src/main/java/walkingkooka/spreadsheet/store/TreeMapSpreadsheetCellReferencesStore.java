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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

final class TreeMapSpreadsheetCellReferencesStore implements SpreadsheetCellReferencesStore {

    static TreeMapSpreadsheetCellReferencesStore empty() {
        return new TreeMapSpreadsheetCellReferencesStore();
    }

    private TreeMapSpreadsheetCellReferencesStore() {
        this.store = SpreadsheetExpressionReferenceStores.treeMap();
    }

    @Override
    public Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> cells) {
        return this.store.save(cells);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<Set<SpreadsheetCellReference>> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void saveCells(final SpreadsheetCellReference reference, 
                          final Set<SpreadsheetCellReference> cells) {
        this.store.saveCells(reference, cells);
    }

    @Override
    public void addCell(final ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference> referenceAndCell) {
        this.store.addCell(referenceAndCell);
    }

    @Override
    public Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference>> watcher) {
        return this.store.addAddCellWatcher(watcher);
    }

    @Override
    public void removeCell(final ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference> referenceAndCell) {
        this.store.removeCell(referenceAndCell);
    }

    @Override
    public Set<SpreadsheetCellReference> findCellsWithReference(final SpreadsheetCellReference reference, 
                                                                final int offset,
                                                                final int count) {
        return this.store.findCellsWithReference(
                reference,
                offset,
                count
        );
    }

    @Override
    public int countCellsWithReference(final SpreadsheetCellReference reference) {
        return this.store.countCellsWithReference(reference);
    }

    @Override
    public Runnable addRemoveCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference>> watcher) {
        return this.store.addRemoveCellWatcher(watcher);
    }

    @Override
    public Set<SpreadsheetCellReference> findReferencesWithCell(final SpreadsheetCellReference cell,
                                                                final int offset,
                                                                final int count) {
        return this.store.findReferencesWithCell(
                cell,
                offset,
                count
        );
    }

    @Override
    public void removeReferencesWithCell(final SpreadsheetCellReference cell) {
        this.store.removeReferencesWithCell(cell);
    }

    // Store............................................................................................................

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final SpreadsheetCellReference reference) {
        return this.store.load(reference);
    }

    @Override
    public void delete(final SpreadsheetCellReference reference) {
        this.store.delete(reference);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetCellReference> consumer) {
        return this.store.addDeleteWatcher(consumer);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetCellReference> ids(final int i,
                                             final int i1) {
        return this.store.ids(i, i1);
    }

    @Override
    public Optional<SpreadsheetCellReference> firstId() {
        return this.store.firstId();
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final int from, 
                                                      final int to) {
        return this.store.values(
                from,
                to
        );
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> firstValue() {
        return this.store.firstValue();
    }

    @Override
    public List<Set<SpreadsheetCellReference>> all() {
        return this.store.all();
    }

    @Override
    public List<Set<SpreadsheetCellReference>> between(final SpreadsheetCellReference from, 
                                                       final SpreadsheetCellReference to) {
        return this.store.between(
                from,
                to
        );
    }

    private final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.store.toString();
    }
}
