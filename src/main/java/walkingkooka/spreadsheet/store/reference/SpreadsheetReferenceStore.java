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

package walkingkooka.spreadsheet.store.reference;

import walkingkooka.spreadsheet.parser.SpreadsheetCellReference;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link Store} that holds one or more references for every {@link SpreadsheetCellReference}.
 */
public interface SpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> extends Store<T, Set<SpreadsheetCellReference>> {

    @Override
    default Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> value) {
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    /**
     * Saves many references to the given id. Note any {@link #addAddReferenceWatcher(Consumer)} and {@link #addRemoveReferenceWatcher(Consumer)}
     * will be fired for all targets.
     */
    void saveReferences(final T id, final Set<SpreadsheetCellReference> targets);

    /**
     * Adds a reference to the given id.
     */
    void addReference(final TargetAndSpreadsheetCellReference<T> targetAndReference);

    /**
     * Adds a {@link Consumer watcher} which receives all added reference events.
     */
    Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher);

    /**
     * Removes a reference from the given id.
     */
    void removeReference(final TargetAndSpreadsheetCellReference<T> targetAndReference);

    /**
     * Adds a {@link Consumer watcher} which receives all removed reference events.
     */
    Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher);

    /**
     * Loads the referred id given a {@link SpreadsheetCellReference}.
     */
    Set<T> loadReferred(final SpreadsheetCellReference reference);
}
