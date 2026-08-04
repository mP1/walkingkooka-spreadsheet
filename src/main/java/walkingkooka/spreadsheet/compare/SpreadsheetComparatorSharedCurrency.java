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

import walkingkooka.currency.HasCurrency;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;
import java.util.Currency;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} sorting the {@link java.util.Currency} of given {@link SpreadsheetCell}.
 */
final class SpreadsheetComparatorSharedCurrency extends SpreadsheetComparatorShared<Currency> {

    static SpreadsheetComparatorSharedCurrency INSTANCE = new SpreadsheetComparatorSharedCurrency();

    private SpreadsheetComparatorSharedCurrency() {
        super(SpreadsheetComparatorName.CURRENCY);
    }

    @Override
    Optional<Currency> extractValueNonNull(final SpreadsheetCell cell,
                                           final SpreadsheetComparatorContext context) {
        return cell.currency();
    }

    @Override
    int compareNonNull(final Currency left,
                       final Currency right) {
        return HasCurrency.CURRENCY_CASE_SENSITIVITY.comparator()
            .compare(
                left.getCurrencyCode(),
                right.getCurrencyCode()
            );
    }

    // Object...................................................................,.......................................

    @Override
    public String toString() {
        return Currency.class.getSimpleName();
    }
}
