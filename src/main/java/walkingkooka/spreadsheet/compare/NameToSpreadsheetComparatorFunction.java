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

import walkingkooka.text.CharSequences;

import java.util.function.Function;

/**
 * A function that maps names to {@link SpreadsheetComparator}.
 */
final class NameToSpreadsheetComparatorFunction implements Function<String, SpreadsheetComparator<?>> {

    final static NameToSpreadsheetComparatorFunction INSTANCE = new NameToSpreadsheetComparatorFunction();

    private NameToSpreadsheetComparatorFunction() {
        super();
    }

    @Override
    public SpreadsheetComparator<?> apply(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        final SpreadsheetComparator<?> comparator;

        switch (name) {
            case "date":
                comparator = SpreadsheetComparators.date();
                break;
            case "date-time":
                comparator = SpreadsheetComparators.dateTime();
                break;
            case "day-of-month":
                comparator = SpreadsheetComparators.dayOfMonth();
                break;
            case "expression-number":
                comparator = SpreadsheetComparators.expressionNumber();
                break;
            case "hour-of-am-pm":
                comparator = SpreadsheetComparators.hourOfAmPm();
                break;
            case "hour-of-day":
                comparator = SpreadsheetComparators.hourOfDay();
                break;
            case "minute-of-hour":
                comparator = SpreadsheetComparators.minuteOfHour();
                break;
            case "month-of-year":
                comparator = SpreadsheetComparators.monthOfYear();
                break;
            case "nano-of-second":
                comparator = SpreadsheetComparators.nanoOfSecond();
                break;
            case "seconds-of-minute":
                comparator = SpreadsheetComparators.secondsOfMinute();
                break;
            case "string":
                comparator = SpreadsheetComparators.string();
                break;
            case "string-case-insensitive":
                comparator = SpreadsheetComparators.stringCaseInsensitive();
                break;
            case "time":
                comparator = SpreadsheetComparators.time();
                break;
            case "year":
                comparator = SpreadsheetComparators.year();
                break;
            default:
                comparator = null;
                throw new IllegalArgumentException("Unknown SpreadsheetComparator " + CharSequences.quoteAndEscape(name));
        }

        return comparator;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
