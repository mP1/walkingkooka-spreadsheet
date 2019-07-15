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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Comparator;

final class TreeMapSpreadsheetRangeStoreBottomRightEntry<V> extends TreeMapSpreadsheetRangeStoreEntry<V> {

    static <V> TreeMapSpreadsheetRangeStoreBottomRightEntry<V> with(final SpreadsheetRange range, final V value) {
        return new TreeMapSpreadsheetRangeStoreBottomRightEntry<V>(range, value);
    }

    private TreeMapSpreadsheetRangeStoreBottomRightEntry(final SpreadsheetRange range, final V value) {
        super(range, value);
    }

    @Override
    Comparator<SpreadsheetCellReference> comparator() {
        return Comparator.<SpreadsheetCellReference>naturalOrder().reversed();
    }

    @Override
    SpreadsheetCellReference primaryCellReference(final SpreadsheetRange range) {
        return range.end();
    }

    @Override
    SpreadsheetCellReference secondaryCellReference(SpreadsheetRange range) {
        return range.begin();
    }
}
