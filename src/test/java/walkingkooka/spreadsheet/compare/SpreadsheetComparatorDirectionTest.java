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
import walkingkooka.compare.Comparators;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetComparatorDirectionTest implements ClassTesting2<SpreadsheetComparatorDirection> {

    // apply............................................................................................................

    @Test
    public void testApplyUp() {
        final SpreadsheetComparator<Void> comparator = SpreadsheetComparators.fake();
        assertSame(
            comparator,
            SpreadsheetComparatorDirection.UP.apply(comparator)
        );
    }

    @Test
    public void testApplyDown() {
        final SpreadsheetComparator<String> comparator = SpreadsheetComparators.text();

        this.checkEquals(
            SpreadsheetComparators.reverse(comparator),
            SpreadsheetComparatorDirection.DOWN.apply(comparator)
        );
    }

    @Test
    public void testApplyDownTwice() {
        final SpreadsheetComparator<Void> comparator = SpreadsheetComparators.fake();
        assertSame(
            comparator,
            SpreadsheetComparatorDirection.DOWN.apply(
                SpreadsheetComparatorDirection.DOWN.apply(comparator)
            )
        );
    }

    // flip.............................................................................................................

    @Test
    public void testFlipUp() {
        this.flipAndCheck(
            SpreadsheetComparatorDirection.UP,
            SpreadsheetComparatorDirection.DOWN
        );
    }

    @Test
    public void testFlipDown() {
        this.flipAndCheck(
            SpreadsheetComparatorDirection.DOWN,
            SpreadsheetComparatorDirection.UP
        );
    }

    @Test
    public void testFlipTwice() {
        this.flipAndCheck(
            SpreadsheetComparatorDirection.DOWN.flip(),
            SpreadsheetComparatorDirection.DOWN
        );
    }

    private void flipAndCheck(final SpreadsheetComparatorDirection in,
                              final SpreadsheetComparatorDirection expected) {
        assertSame(
            expected,
            in.flip()
        );
    }

    // fixCompareResult.................................................................................................

    @Test
    public void testFixCompareResultUpEquals() {
        this.fixCompareResultAndCheck(
            SpreadsheetComparatorDirection.UP,
            Comparators.EQUAL,
            Comparators.EQUAL
        );
    }

    @Test
    public void testFixCompareResultUpLess() {
        this.fixCompareResultAndCheck(
            SpreadsheetComparatorDirection.UP,
            Comparators.LESS,
            Comparators.LESS
        );
    }

    @Test
    public void testFixCompareResultUpMore() {
        this.fixCompareResultAndCheck(
            SpreadsheetComparatorDirection.UP,
            Comparators.MORE,
            Comparators.MORE
        );
    }

    @Test
    public void testFixCompareResultDownEquals() {
        this.fixCompareResultAndCheck(
            SpreadsheetComparatorDirection.DOWN,
            Comparators.EQUAL,
            Comparators.EQUAL
        );
    }

    @Test
    public void testFixCompareResultDownLess() {
        this.fixCompareResultAndCheck(
            SpreadsheetComparatorDirection.DOWN,
            -2,
            Comparators.MORE
        );
    }

    @Test
    public void testFixCompareResultDownMore() {
        this.fixCompareResultAndCheck(
            SpreadsheetComparatorDirection.DOWN,
            +2,
            Comparators.LESS
        );
    }

    private void fixCompareResultAndCheck(final SpreadsheetComparatorDirection direction,
                                          final int value,
                                          final int expected) {
        this.checkEquals(
            expected,
            direction.fixCompareResult(value),
            () -> direction + " " + value
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetComparatorDirection> type() {
        return SpreadsheetComparatorDirection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
