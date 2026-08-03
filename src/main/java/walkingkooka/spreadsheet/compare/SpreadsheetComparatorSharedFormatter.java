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

import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} sorting the {@link SpreadsheetFormatterSelector} of given {@link SpreadsheetCell}.
 */
final class SpreadsheetComparatorSharedFormatter extends SpreadsheetComparatorShared<SpreadsheetFormatterSelector> {

    /**
     * Singleton
     */
    final static SpreadsheetComparatorSharedFormatter INSTANCE = new SpreadsheetComparatorSharedFormatter();

    private SpreadsheetComparatorSharedFormatter() {
        super(SpreadsheetComparatorName.FORMATTER);
    }

    @Override
    Optional<SpreadsheetFormatterSelector> extractValueNonNull(final SpreadsheetCell cell,
                                                               final SpreadsheetComparatorContext context) {
        return cell.formatter();
    }

    @Override
    int compareNonNull(final SpreadsheetFormatterSelector left,
                       final SpreadsheetFormatterSelector right) {
        return left.name()
            .compareTo(
                right.name()
            );
    }

    // Object...................................................................,.......................................

    @Override
    public String toString() {
        return SpreadsheetFormatterSelector.class.getSimpleName();
    }
}
