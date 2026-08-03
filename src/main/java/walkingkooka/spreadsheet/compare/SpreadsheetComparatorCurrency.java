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
import walkingkooka.text.CaseSensitivity;

import java.util.Comparator;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} sorting the {@link java.util.Currency} of given {@link SpreadsheetCell}.
 */
final class SpreadsheetComparatorCurrency implements SpreadsheetComparator<Currency> {

    static SpreadsheetComparatorCurrency INSTANCE = new SpreadsheetComparatorCurrency();

    private SpreadsheetComparatorCurrency() {
        super();
    }

    @Override
    public Optional<Currency> extractValue(final SpreadsheetCell cell,
                                           final SpreadsheetComparatorContext context) {
        Objects.requireNonNull(context, "context");

        return null != cell ?
            cell.currency() :
            Optional.empty();
    }

    // Comparator.......................................................................................................

    @Override
    public int compare(final Currency left,
                       final Currency right) {
        return CaseSensitivity.INSENSITIVE.comparator()
            .compare(
                nullSafeCurrencyCode(left),
                nullSafeCurrencyCode(right)
            );
    }

    private static String nullSafeCurrencyCode(final Currency currency) {
        return null != currency ?
            currency.getCurrencyCode() :
            "";
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetComparatorName name() {
        return SpreadsheetComparatorName.CURRENCY;
    }

    // Object...................................................................,.......................................

    @Override
    public String toString() {
        return Currency.class.getSimpleName();
    }
}
