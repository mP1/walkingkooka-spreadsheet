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

import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.Store;

import java.util.Objects;
import java.util.Set;

final class TreeMapSpreadsheetCellReferencesStore implements SpreadsheetCellReferencesStore,
    SpreadsheetExpressionReferencesStoreDelegator<SpreadsheetCellReference> {

    static TreeMapSpreadsheetCellReferencesStore empty() {
        return new TreeMapSpreadsheetCellReferencesStore();
    }

    private TreeMapSpreadsheetCellReferencesStore() {
        this.store = SpreadsheetExpressionReferencesStores.treeMap();
    }

    @Override
    public Set<SpreadsheetCellReference> findCellsWithCellOrCellRange(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                                      final int offset,
                                                                      final int count) {
        Objects.requireNonNull(cellOrCellRange, "cellOrCellRange");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        return 0 == count ?
            Sets.empty() :
            this.findCellsWithCellOrCellRangeNonZeroCount(
                cellOrCellRange,
                offset,
                count
            );
    }

    private Set<SpreadsheetCellReference> findCellsWithCellOrCellRangeNonZeroCount(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                                                   final int offset,
                                                                                   final int count) {
        final Set<SpreadsheetCellReference> references = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        // potentially slow for large ranges with gaps.
        for (final SpreadsheetCellReference cell : cellOrCellRange.toCellRange()) {
            references.addAll(
                this.findValuesById(
                    cell,
                    0,
                    Integer.MAX_VALUE
                )
            );
        }

        return Sets.readOnly(
            references.stream()
                .skip(offset)
                .limit(count)
                .collect(
                    ImmutableSortedSet.collector(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR)
                )
        );
    }

    // SpreadsheetExpressionReferencesStoreDelegator....................................................................

    @Override
    public SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> spreadsheetExpressionReferencesStore() {
        return this.store;
    }

    private final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.store.toString();
    }
}
