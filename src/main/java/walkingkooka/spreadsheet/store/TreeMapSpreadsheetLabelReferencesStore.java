
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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.Store;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link SpreadsheetLabelReferencesStore} that uses a {@link Map} to store a cell or label to its many {@link SpreadsheetLabelName}.
 */
final class TreeMapSpreadsheetLabelReferencesStore implements SpreadsheetLabelReferencesStore,
    SpreadsheetExpressionReferencesStoreDelegator<SpreadsheetLabelName> {

    static TreeMapSpreadsheetLabelReferencesStore empty() {
        return new TreeMapSpreadsheetLabelReferencesStore();
    }

    private TreeMapSpreadsheetLabelReferencesStore() {
        this.store = SpreadsheetExpressionReferencesStores.treeMap();
    }

    @Override
    public Set<SpreadsheetLabelName> findLabelsWithCellOrCellRange(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                                   final int offset,
                                                                   final int count) {
        Objects.requireNonNull(cellOrCellRange, "cellOrCellRange");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        return 0 == count ?
            Sets.empty() :
            this.findLabelsWithCellOrCellRangeNonZeroCount(
                cellOrCellRange,
                offset,
                count
            );
    }

    private Set<SpreadsheetLabelName> findLabelsWithCellOrCellRangeNonZeroCount(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                                                final int offset,
                                                                                final int count) {
        final Set<SpreadsheetLabelName> labels = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        // potentially slow for large ranges with gaps.
        for (final SpreadsheetCellReference reference : cellOrCellRange.toCellRange()) {
            labels.addAll(
                this.findIdsByValue(
                    reference,
                    0,
                    Integer.MAX_VALUE
                )
            );
        }

        return Sets.readOnly(
            labels.stream()
                .skip(offset)
                .limit(count)
                .collect(
                    ImmutableSortedSet.collector(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR)
                )
        );
    }

    // SpreadsheetExpressionReferencesStoreDelegator....................................................................

    @Override
    public SpreadsheetExpressionReferencesStore<SpreadsheetLabelName> spreadsheetExpressionReferencesStore() {
        return this.store;
    }

    private final SpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.store.toString();
    }
}
