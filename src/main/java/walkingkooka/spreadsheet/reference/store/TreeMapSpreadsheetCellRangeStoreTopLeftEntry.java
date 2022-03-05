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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.Comparator;
import java.util.Set;

final class TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V> extends TreeMapSpreadsheetCellRangeStoreEntry<V> {

    static <V> TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V> with(final SpreadsheetCellRange range, final V value) {
        return new TreeMapSpreadsheetCellRangeStoreTopLeftEntry<>(range, value);
    }

    private TreeMapSpreadsheetCellRangeStoreTopLeftEntry(final SpreadsheetCellRange range, final V value) {
        super(range, value);
    }

    @Override
    Comparator<SpreadsheetCellReference> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    SpreadsheetCellReference primaryCellReference(final SpreadsheetCellRange range) {
        return range.begin();
    }

    @Override
    SpreadsheetCellReference secondaryCellReference(SpreadsheetCellRange range) {
        return range.end();
    }

    /**
     * Rebuilds all the ranges within this entry.
     */
    Set<SpreadsheetCellRange> ranges() {
        final Set<SpreadsheetCellRange> ranges = Sets.ordered();

        final SpreadsheetCellReference topLeft = this.range.begin();

        for (SpreadsheetCellReference bottomRight : this.secondaryCellReferenceToValues.keySet()) {
            ranges.add(topLeft.cellRange(bottomRight));
        }
        return ranges;
    }
}
