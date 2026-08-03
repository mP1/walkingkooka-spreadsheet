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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} sorting the {@link Locale} of given {@link SpreadsheetCell}.
 */
final class SpreadsheetComparatorLocale implements SpreadsheetComparator<Locale> {

    static SpreadsheetComparatorLocale INSTANCE = new SpreadsheetComparatorLocale();

    private SpreadsheetComparatorLocale() {
        super();
    }

    @Override
    public Optional<Locale> extractValue(final SpreadsheetCell cell,
                                           final SpreadsheetComparatorContext context) {
        Objects.requireNonNull(context, "context");

        return null != cell ?
            cell.locale() :
            Optional.empty();
    }

    // Comparator.......................................................................................................

    @Override
    public int compare(final Locale left,
                       final Locale right) {
        return CaseSensitivity.INSENSITIVE.comparator()
            .compare(
                nullSafeLocaleLanguageTag(left),
                nullSafeLocaleLanguageTag(right)
            );
    }

    private static String nullSafeLocaleLanguageTag(final Locale locale) {
        return null != locale ?
            locale.toLanguageTag() :
            "";
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetComparatorName name() {
        return SpreadsheetComparatorName.LOCALE;
    }

    // Object...................................................................,.......................................

    @Override
    public String toString() {
        return Locale.class.getSimpleName();
    }
}
