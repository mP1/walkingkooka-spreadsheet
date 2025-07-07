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

import java.time.temporal.Temporal;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetComparatorTest implements SpreadsheetComparatorTesting<BasicSpreadsheetComparator<String>, String>,
    HashCodeEqualsDefinedTesting2<BasicSpreadsheetComparator<String>>,
    ToStringTesting<BasicSpreadsheetComparator<String>> {

    private final static SpreadsheetComparatorDirection DIRECTION = SpreadsheetComparatorDirection.DOWN;

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("name");

    @Test
    public void testWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparator.with(
                null,
                String.CASE_INSENSITIVE_ORDER,
                DIRECTION,
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
                DIRECTION,
                NAME
            )
        );
    }

    @Test
    public void testWithNullDirectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
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
                SpreadsheetComparatorDirection.UP,
                null
            )
        );
    }

    @Test
    public void testWithUp() {
        final SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.UP;

        final BasicSpreadsheetComparator<Temporal> comparator = BasicSpreadsheetComparator.with(
            Temporal.class,
            DateTimeComparators.dayOfMonth(),
            direction,
            NAME
        );

        this.directionAndCheck(
            comparator,
            direction
        );
    }

    @Test
    public void testWithDown() {
        final SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.DOWN;

        final BasicSpreadsheetComparator<Temporal> comparator = BasicSpreadsheetComparator.with(
            Temporal.class,
            DateTimeComparators.dayOfMonth(),
            direction,
            NAME
        );

        this.directionAndCheck(
            comparator,
            direction
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
                DIRECTION,
                NAME
            ),
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                DIRECTION,
                NAME
            )
        );
    }

    @Test
    public void testEqualsDifferentDirection() {
        this.checkNotEquals(
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                DIRECTION,
                NAME
            ),
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                DIRECTION.flip(),
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
                DIRECTION,
                NAME
            ),
            BasicSpreadsheetComparator.with(
                String.class,
                String.CASE_INSENSITIVE_ORDER,
                DIRECTION,
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
    public void tesToStringDefault() {
        final Comparator<String> comparator = Comparators.fake();
        final SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.DEFAULT;

        this.toStringAndCheck(
            BasicSpreadsheetComparator.with(
                String.class,
                comparator,
                direction,
                NAME
            ),
            NAME.toString()
        );
    }

    @Test
    public void tesToStringUp() {
        final Comparator<String> comparator = Comparators.fake();
        final SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.UP;

        this.toStringAndCheck(
            BasicSpreadsheetComparator.with(
                String.class,
                comparator,
                direction,
                NAME
            ),
            NAME + " " + direction
        );
    }

    @Test
    public void tesToStringDown() {
        final Comparator<String> comparator = Comparators.fake();
        final SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.DOWN;

        this.toStringAndCheck(
            BasicSpreadsheetComparator.with(
                String.class,
                comparator,
                direction,
                NAME
            ),
            NAME + " " + direction
        );
    }

    // Comparator.......................................................................................................
    @Override
    public BasicSpreadsheetComparator<String> createComparator() {
        return BasicSpreadsheetComparator.with(
            String.class,
            String.CASE_INSENSITIVE_ORDER,
            DIRECTION,
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
