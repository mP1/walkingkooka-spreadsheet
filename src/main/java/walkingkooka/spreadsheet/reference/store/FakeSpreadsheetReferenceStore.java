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
import walkingkooka.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class FakeSpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> extends FakeStore<T, Set<SpreadsheetCellReference>> implements SpreadsheetReferenceStore<T>, Fake {

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> targets) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(targets, "targets");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        Objects.requireNonNull(targetAndReference, "targetAndReference");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        Objects.requireNonNull(targetAndReference, "targetAndReference");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        throw new UnsupportedOperationException();
    }
}
