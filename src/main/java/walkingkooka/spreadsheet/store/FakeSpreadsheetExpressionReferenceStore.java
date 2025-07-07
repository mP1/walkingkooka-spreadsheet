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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.store.FakeStore;
import walkingkooka.test.Fake;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class FakeSpreadsheetExpressionReferenceStore<T extends SpreadsheetExpressionReference>
    extends FakeStore<T, Set<SpreadsheetCellReference>> implements SpreadsheetExpressionReferenceStore<T>, Fake {

    @Override
    public void saveCells(final T reference,
                          final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cells, "cells");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        Objects.requireNonNull(referenceAndCell, "referenceAndCell");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        Objects.requireNonNull(referenceAndCell, "referenceAndCell");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addRemoveCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellReference> findCellsWithReference(final T reference,
                                                                final int offset,
                                                                final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int countCellsWithReference(final T reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> findReferencesWithCell(final SpreadsheetCellReference cell,
                                         final int offset,
                                         final int count) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReferencesWithCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }
}
