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

package walkingkooka.spreadsheet.compare;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A mapper function that can turn a {@link SpreadsheetComparatorName} into a {@link SpreadsheetComparator}.
 */
public interface SpreadsheetComparatorProvider {

    /**
     * Resolves the given {@link SpreadsheetComparatorName} to a {@link SpreadsheetComparatorName}.
     */
    Optional<SpreadsheetComparator<?>> spreadsheetComparator(final SpreadsheetComparatorName name);

    /**
     * Helper that invokes {@link #spreadsheetComparator(SpreadsheetComparatorName)} and throws a {@link IllegalArgumentException}
     * if none was found.
     */
    default SpreadsheetComparator<?> spreadsheetComparatorOrFail(final SpreadsheetComparatorName name) {
        return this.spreadsheetComparator(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown comparator " + name));
    }

    /**
     * Returns all available {@link SpreadsheetComparatorInfo}
     */
    Set<SpreadsheetComparatorInfo> spreadsheetComparatorInfos();

    /**
     * Helper that maps a {@link List} of {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} into a {@link List} of
     * {@link SpreadsheetColumnOrRowSpreadsheetComparators} including the name to comparator lookups.
     */
    default List<SpreadsheetColumnOrRowSpreadsheetComparators> toSpreadsheetColumnOrRowSpreadsheetComparators(final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> names) {
        return names.stream()
                .map(n -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        n.columnOrRow(),
                        n.comparatorNameAndDirections()
                                .stream()
                                .map(
                                        nad -> nad.direction()
                                                .apply(
                                                        this.spreadsheetComparatorOrFail(nad.name())
                                                )
                                ).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }
}
