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
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.store.Store;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link Store} that tracks cells and labels references for a single {@link SpreadsheetCellReference} within its formula.
 * Note that all operations ignore the {@link SpreadsheetReferenceKind} and treat
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
    void saveCells(final T reference,
                   final Set<SpreadsheetCellReference> cells);

    /**
     * Adds a reference to the given target.
     */
    void addCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell);

    /**
     * Adds a {@link Consumer watcher} which receives all added reference events.
     */
    @SuppressWarnings("UnusedReturnValue")
    Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher);

    /**
     * Removes a {@link SpreadsheetExpressionReference} from a {@link SpreadsheetCellReference}
     */
    void removeCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell);

    /**
     * Adds a {@link Consumer watcher} which receives all removed reference events.
     */
    Runnable addRemoveCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher);

    /**
     * Finds any {@link SpreadsheetCellReference} with the provided reference.
     */
    Set<SpreadsheetCellReference> findCellsWithReference(final T reference,
                                                         final int offset,
                                                         final int count);

    /**
     * Counts the number of cells with the given reference.
     */
    int countCellsWithReference(final T reference);

    /**
     * Loads ALL the targets (references too or mentions) for a given {@link SpreadsheetCellReference cell}.
     * <pre>
     * cell=references
     * A1=Z9+11
     * B2=Z9+22
     *
     * findReferencesWithCell(Z9) -> A1, B2
     *
     * // to find references within A1 (without walking the formula AST) try
     * load(A1) -> Z9
     * </pre>
     * <p>
     * This might be useful to display all references to a particular cell. To display references to a label try
     * {@link SpreadsheetLabelStore#loadCellOrCellRanges(SpreadsheetLabelName)}.
     */
    Set<T> findReferencesWithCell(final SpreadsheetCellReference cell,
                                  final int offset,
                                  final int count);

    /**
     * Removes any references for the provided {@link SpreadsheetCellReference}.
     * This is useful to remove references within a {@link SpreadsheetCell#formula()}.
     * <br>
     * This is equivalent to {@link #findReferencesWithCell(SpreadsheetCellReference, int, int) finding all references for a cell}
     * and then {@link #removeCell(ReferenceAndSpreadsheetCellReference) removing them one by one}.
     */
    void removeReferencesWithCell(final SpreadsheetCellReference cell);
}
