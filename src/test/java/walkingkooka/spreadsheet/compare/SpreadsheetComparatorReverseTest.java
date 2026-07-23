
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

public final class SpreadsheetComparatorReverseTest implements SpreadsheetComparatorTesting<SpreadsheetComparatorReverse<String>, String>,
    HashCodeEqualsDefinedTesting2<SpreadsheetComparatorReverse<String>>,
    ToStringTesting<SpreadsheetComparatorReverse<String>> {

    @Test
    public void testWithNullComparatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorReverse.with(
                null
            )
        );
    }

    @Test
    public void testWith() {
        SpreadsheetComparatorReverse.with(
            SpreadsheetComparators.text()

        );
    }

    @Test
    public void testWithUnwrapsReverseSpreadsheetConverter() {
        final SpreadsheetComparator<String> comparator = SpreadsheetComparators.text();
        assertSame(
            comparator,
            SpreadsheetComparatorReverse.with(
                SpreadsheetComparatorReverse.with(
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
            SpreadsheetComparatorReverse.with(
                SpreadsheetComparators.hourOfDay()
            ),
            SpreadsheetComparatorReverse.with(
                SpreadsheetComparators.text()
            )
        );
    }

    @Override
    public SpreadsheetComparatorReverse<String> createObject() {
        return this.createComparator();
    }

    @Test
    public void testToString() {
        final SpreadsheetComparator<String> comparator = SpreadsheetComparators.textCaseInsensitive();

        this.toStringAndCheck(
            SpreadsheetComparatorReverse.with(comparator),
            comparator.reversed()
                .toString()
        );
    }

    // Comparator.......................................................................................................
    @Override
    public SpreadsheetComparatorReverse<String> createComparator() {
        return Cast.to(
            SpreadsheetComparatorReverse.with(
                SpreadsheetComparators.textCaseInsensitive()
            )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorReverse<String>> type() {
        return Cast.to(
            SpreadsheetComparatorReverse.class
        );
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
