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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Accepts a {@link SpreadsheetLabelName} fails if a cycle is detected.
 */
final class TreeMapSpreadsheetLabelStoreCycleSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static void cycleFreeTest(final SpreadsheetLabelMapping mapping,
                              final SpreadsheetLabelStore store) {
        Objects.requireNonNull(mapping, "mapping");
        Objects.requireNonNull(store, "store");

        final TreeMapSpreadsheetLabelStoreCycleSpreadsheetSelectionVisitor visitor = new TreeMapSpreadsheetLabelStoreCycleSpreadsheetSelectionVisitor(store);
        visitor.accept(
            mapping.label()
        );
        visitor.accept(
            mapping.reference()
        );
    }

    // @VisibleForTesting
    TreeMapSpreadsheetLabelStoreCycleSpreadsheetSelectionVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    private static CharSequence quote(final SpreadsheetSelection selection) {
        return CharSequences.quoteAndEscape(
            selection.toString()
        );
    }

    private final Set<SpreadsheetSelection> visited = Sets.ordered();

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        final Set<SpreadsheetSelection> visited = this.visited;
        if (false == visited.add(label)) {
            throw new IllegalArgumentException(
                "Cycle detected for " + visited.stream()
                    .map(TreeMapSpreadsheetLabelStoreCycleSpreadsheetSelectionVisitor::quote)
                    .collect(Collectors.joining(
                            " -> ",
                            "",
                            " -> " + quote(label)
                        )
                    )
            );
        }

        final Optional<SpreadsheetLabelMapping> mapping = this.store.load(label);
        if (mapping.isPresent()) {
            this.accept(mapping.get().reference());
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    private final SpreadsheetLabelStore store;
}
