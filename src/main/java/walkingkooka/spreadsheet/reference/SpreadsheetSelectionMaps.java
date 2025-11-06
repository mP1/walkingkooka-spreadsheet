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

package walkingkooka.spreadsheet.reference;

import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.SortedMap;

/**
 * A collection of maps where the key is a {@link SpreadsheetSelection}.
 */
public final class SpreadsheetSelectionMaps implements PublicStaticHelper {

    public static <V> SortedMap<SpreadsheetCellReference, V> cell() {
        return Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
    }

    public static <V> SortedMap<SpreadsheetColumnReference, V> column() {
        return Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
    }

    public static <V> SortedMap<SpreadsheetLabelName, V> label() {
        return Maps.sorted();
    }

    public static <V> SortedMap<SpreadsheetRowReference, V> row() {
        return Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
    }

    public static <V> SortedMap<SpreadsheetExpressionReference, V> spreadsheetExpressionReference() {
        return Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
    }

    /**
     * Stop creation
     */
    private SpreadsheetSelectionMaps() {
        throw new UnsupportedOperationException();
    }
}
