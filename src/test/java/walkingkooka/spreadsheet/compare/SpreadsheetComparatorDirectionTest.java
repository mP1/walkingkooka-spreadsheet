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
        final SpreadsheetComparator<String> comparator = SpreadsheetComparators.string();

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
