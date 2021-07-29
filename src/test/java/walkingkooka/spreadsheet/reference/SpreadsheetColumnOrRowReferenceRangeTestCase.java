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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.Range;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.test.ParseStringTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetColumnOrRowReferenceRangeTestCase<S extends SpreadsheetColumnOrRowReferenceRange & Comparable<S>,
        R extends SpreadsheetColumnOrRowReference & Comparable<R>>
        extends SpreadsheetSelectionTestCase<S>
        implements ComparableTesting2<S>,
        ParseStringTesting<S> {

    SpreadsheetColumnOrRowReferenceRangeTestCase() {
        super();
    }

    @Test
    public final void testWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection(null));
    }

    @Override
    public final void testCompareToNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void testCompareToSelfGivesZero() {
        throw new UnsupportedOperationException();
    }

    abstract S createSelection(final Range<R> range);

    @Override
    public final S createComparable() {
        return this.createSelection();
    }

    @Override
    public final boolean compareAndEqualsMatch() {
        return false;
    }

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
