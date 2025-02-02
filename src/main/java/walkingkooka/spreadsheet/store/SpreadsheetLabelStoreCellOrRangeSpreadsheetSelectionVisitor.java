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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.text.CharSequences;
import walkingkooka.visit.Visiting;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Accepts a {@link SpreadsheetLabelName} and returns a {@link SpreadsheetCellReferenceOrRange} or {@link Optional#empty()},
 * using a {@link SpreadsheetLabelStore} to resolve labels.
 */
final class SpreadsheetLabelStoreCellOrRangeSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Optional<SpreadsheetCellReferenceOrRange> cellOrRange(final SpreadsheetExpressionReference reference,
                                                                 final SpreadsheetLabelStore store) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(store, "store");

        final SpreadsheetLabelStoreCellOrRangeSpreadsheetSelectionVisitor visitor = new SpreadsheetLabelStoreCellOrRangeSpreadsheetSelectionVisitor(store);
        visitor.accept(reference);
        return Optional.ofNullable(
                visitor.cellReferenceOrRange
        );
    }

    SpreadsheetLabelStoreCellOrRangeSpreadsheetSelectionVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetSelection selection) {
        final Set<SpreadsheetSelection> visited = this.visited;
        if (false == visited.add(selection)) {
            throw new IllegalStateException(
                    "Cycle detected for " + visited.stream()
                            .map(SpreadsheetLabelStoreCellOrRangeSpreadsheetSelectionVisitor::quote)
                            .collect(Collectors.joining(
                                            " -> ",
                                            "",
                                            " -> " + quote(selection)
                                    )
                            )
            );
        }

        return Visiting.CONTINUE;
    }

    private static CharSequence quote(final SpreadsheetSelection selection) {
        return CharSequences.quoteAndEscape(
                selection.toString()
        );
    }

    private final Set<SpreadsheetSelection> visited = Sets.ordered();

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.cellReferenceOrRange = reference;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        final Optional<SpreadsheetLabelMapping> mapping = this.store.load(label);
        if (mapping.isPresent()) {
            this.accept(mapping.get().target());
        }
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference range) {
        this.cellReferenceOrRange = range;
    }

    @Override
    public String toString() {
        return String.valueOf(this.cellReferenceOrRange);
    }

    private final SpreadsheetLabelStore store;

    private SpreadsheetCellReferenceOrRange cellReferenceOrRange = null;
}
