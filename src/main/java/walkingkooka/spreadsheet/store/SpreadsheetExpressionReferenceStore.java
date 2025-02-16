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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.store.Store;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link Store} that tracks cells and labels references for a single {@link SpreadsheetCellReference} within its formula.
 * Note that all operations ignore the {@link walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceKind} and treat
 * absolute and relative references as equivalent.
 */
public interface SpreadsheetExpressionReferenceStore<T extends SpreadsheetExpressionReference> extends SpreadsheetStore<T, Set<SpreadsheetCellReference>> {

    @Override
    default Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");
        throw new UnsupportedOperationException();
    }

    @Override
    default Runnable addSaveWatcher(final Consumer<Set<SpreadsheetCellReference>> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    /**
     * Saves all the references for a cell or label.
     * Note any {@link #addAddCellWatcher(Consumer)} and {@link #addRemoveCellWatcher(Consumer)} will be fired for all targets.
     */
    void saveCells(final T target,
                   final Set<SpreadsheetCellReference> cells);

    /**
     * Adds a reference to the given target.
     */
    void addCell(final TargetAndSpreadsheetCellReference<T> targetAndCell);

    /**
     * Adds a {@link Consumer watcher} which receives all added reference events.
     */
    @SuppressWarnings("UnusedReturnValue")
    Runnable addAddCellWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher);

    /**
     * Removes a target from a {@link SpreadsheetCellReference}
     */
    void removeCell(final TargetAndSpreadsheetCellReference<T> targetAndCell);

    /**
     * Adds a {@link Consumer watcher} which receives all removed reference events.
     */
    Runnable addRemoveCellWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher);

    /**
     * Loads ALL the targets (references too or mentions) for a given {@link SpreadsheetCellReference cell}.
     * <pre>
     * target=references
     * A1=Z9+11
     * B2=Z9+22
     *
     * loadTargets(Z9) -> A1, B2
     *
     * // to find references within A1 (without walking the formula AST) try
     * load(A1) -> Z9
     * </pre>
     * <p>
     * This might be useful to display all references to a particular cell. To display references to a label try
     * {@link SpreadsheetLabelStore#loadCellOrRanges(SpreadsheetLabelName)}.
     */
    Set<T> loadTargets(final SpreadsheetCellReference cell);
}
