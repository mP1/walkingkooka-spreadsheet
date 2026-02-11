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
import walkingkooka.compare.Comparators;
import walkingkooka.datetime.compare.DateTimeComparators;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;

import java.time.temporal.Temporal;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetComparatorTest implements SpreadsheetComparatorTesting<BasicSpreadsheetComparator<String>, String>,
    HashCodeEqualsDefinedTesting2<BasicSpreadsheetComparator<String>>,
    ToStringTesting<BasicSpreadsheetComparator<String>> {

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.TEXT;

    @Test
    public void testWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparator.with(
                null,
                String.CASE_INSENSITIVE_ORDER,
                NAME
            )
        );
    }

    @Test
    public void testWithNullComparatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparator.with(
                String.class,
                null,
                NAME
            )
        );
    }

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final BasicSpreadsheetComparator<Temporal> comparator = BasicSpreadsheetComparator.with(
            Temporal.class,
            DateTimeComparators.dayOfMonth(),
            NAME
        );
    }

    @Test
    public void testType() {
        this.typeAndCheck(
            this.createComparator(),
            String.class
        );
    }

    @Test
    public void testCompareLess() {
        this.compareAndCheckLess(
            "apple",
            "Banana"
        );
    }

    @Test
    public void testCompareLess2() {
        this.compareAndCheckLess(
            "APPLE",
            "Banana"
        );
    }

    @Test
    public void testEqualsDifferentComparator() {
        this.checkNotEquals(
            BasicSpreadsheetComparator.with(
                String.class,
                Comparators.fake(),
                NAME
            ),
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                NAME
            )
        );
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                NAME
            ),
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                SpreadsheetComparatorName.with(
                    NAME.value() + "-different"
                )
            )
        );
    }

    @Override
    public BasicSpreadsheetComparator<String> createObject() {
        return this.createComparator();
    }

    @Test
    public void tesToString() {
        final Comparator<String> comparator = Comparators.fake();

        this.toStringAndCheck(
            BasicSpreadsheetComparator.with(
                String.class,
                comparator,
                NAME
            ),
            NAME.toString()
        );
    }

    // Comparator.......................................................................................................

    @Override
    public BasicSpreadsheetComparator<String> createComparator() {
        return BasicSpreadsheetComparator.with(
            String.class,
            String.CASE_INSENSITIVE_ORDER,
            NAME
        );
    }

    @Override
    public Class<BasicSpreadsheetComparator<String>> type() {
        return Cast.to(
            BasicSpreadsheetComparator.class
        );
    }
}
