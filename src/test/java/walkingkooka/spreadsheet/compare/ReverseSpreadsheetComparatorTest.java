
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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReverseSpreadsheetComparatorTest implements SpreadsheetComparatorTesting<ReverseSpreadsheetComparator<String>, String>,
    HashCodeEqualsDefinedTesting2<ReverseSpreadsheetComparator<String>>,
    ToStringTesting<ReverseSpreadsheetComparator<String>> {

    @Test
    public void testWithNullComparatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> ReverseSpreadsheetComparator.with(
                null
            )
        );
    }

    @Test
    public void testWith() {
        ReverseSpreadsheetComparator.with(
            SpreadsheetComparators.text()

        );
    }

    @Test
    public void testWithUnwrapsReverseSpreadsheetConverter() {
        final SpreadsheetComparator<String> comparator = SpreadsheetComparators.text();
        assertSame(
            comparator,
            ReverseSpreadsheetComparator.with(
                ReverseSpreadsheetComparator.with(
                    comparator
                )
            )
        );
    }

    @Test
    public void testType() {
        final SpreadsheetComparator<LocalTime> comparator = SpreadsheetComparators.hourOfDay();

        this.typeAndCheck(
            SpreadsheetComparators.reverse(
                comparator
            ),
            LocalTime.class
        );
    }

    @Test
    public void testCompareMore() {
        this.compareAndCheckMore(
            "apple",
            "Banana"
        );
    }

    @Test
    public void testCompareEqual() {
        this.compareAndCheckEquals(
            "apple",
            "APPLE"
        );
    }

    @Test
    public void testEqualsDifferentComparator() {
        this.checkNotEquals(
            ReverseSpreadsheetComparator.with(
                SpreadsheetComparators.hourOfDay()
            ),
            ReverseSpreadsheetComparator.with(
                SpreadsheetComparators.text()
            )
        );
    }

    @Override
    public ReverseSpreadsheetComparator<String> createObject() {
        return this.createComparator();
    }

    @Test
    public void testToString() {
        final SpreadsheetComparator<String> comparator = SpreadsheetComparators.textCaseInsensitive();

        this.toStringAndCheck(
            ReverseSpreadsheetComparator.with(
                comparator
            ),
            "text-case-insensitive DOWN"
        );
    }

    // Comparator.......................................................................................................
    @Override
    public ReverseSpreadsheetComparator<String> createComparator() {
        return Cast.to(
            ReverseSpreadsheetComparator.with(
                SpreadsheetComparators.textCaseInsensitive()
            )
        );
    }

    @Override
    public Class<ReverseSpreadsheetComparator<String>> type() {
        return Cast.to(
            ReverseSpreadsheetComparator.class
        );
    }
}
