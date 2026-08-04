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
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;

import java.util.Comparator;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} sorting the {@link walkingkooka.spreadsheet.value.SpreadsheetError} of given {@link SpreadsheetCell}.
 */
final class SpreadsheetComparatorSharedError extends SpreadsheetComparatorShared<SpreadsheetError> {

    static SpreadsheetComparatorSharedError INSTANCE = new SpreadsheetComparatorSharedError();

    private SpreadsheetComparatorSharedError() {
        super(SpreadsheetComparatorName.ERROR);
    }

    @Override
    Optional<SpreadsheetError> extractValueNonNull(final SpreadsheetCell cell,
                                                   final SpreadsheetComparatorContext context) {
        return cell.formula()
            .error();
    }

    @Override
    int compareNonNull(final SpreadsheetError left,
                       final SpreadsheetError right) {
        return left.compareTo(right);
    }

    // Object...................................................................,.......................................

    @Override
    public String toString() {
        return SpreadsheetError.class.getSimpleName();
    }
}
