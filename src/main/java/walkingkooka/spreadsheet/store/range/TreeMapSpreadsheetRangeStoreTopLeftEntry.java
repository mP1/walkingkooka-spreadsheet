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

package walkingkooka.spreadsheet.store.range;

import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetRange;

import java.util.Comparator;
import java.util.Set;

final class TreeMapSpreadsheetRangeStoreTopLeftEntry<V> extends TreeMapSpreadsheetRangeStoreEntry<V> {

    static <V> TreeMapSpreadsheetRangeStoreTopLeftEntry<V> with(final SpreadsheetRange range, final V value) {
        return new TreeMapSpreadsheetRangeStoreTopLeftEntry<V>(range, value);
    }

    private TreeMapSpreadsheetRangeStoreTopLeftEntry(final SpreadsheetRange range, final V value) {
        super(range, value);
    }

    @Override
    Comparator<SpreadsheetCellReference> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    SpreadsheetCellReference primaryCellReference(final SpreadsheetRange range) {
        return range.begin();
    }

    @Override
    SpreadsheetCellReference secondaryCellReference(SpreadsheetRange range) {
        return range.end();
    }

    /**
     * Rebuilds all the ranges within this entry.
     */
    Set<SpreadsheetRange> ranges() {
        final Set<SpreadsheetRange> ranges = Sets.ordered();

        final SpreadsheetCellReference topLeft = this.range.begin();

        for (SpreadsheetCellReference bottomRight : this.secondaryCellReferenceToValues.keySet()) {
            ranges.add(topLeft.spreadsheetRange(bottomRight));
        }
        return ranges;
    }
}
