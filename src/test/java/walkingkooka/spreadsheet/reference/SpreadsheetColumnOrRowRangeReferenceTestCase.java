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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetColumnOrRowRangeReferenceTestCase<S extends SpreadsheetSelection & Comparable<S>,
    R extends SpreadsheetSelection & Comparable<R>>
    extends SpreadsheetSelectionTestCase<S>
    implements ComparableTesting2<S> {

    SpreadsheetColumnOrRowRangeReferenceTestCase() {
        super();
    }

    @Test
    public final void testWithNullRangeFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection(null));
    }

    // add..............................................................................................................

    @Test
    public final void testAddZero() {
        final S selection = this.createSelection();
        assertSame(
            selection,
            selection.add(0)
        );
    }

    // addSaturated.....................................................................................................

    @Test
    public final void testAddSaturatedZero() {
        final S selection = this.createSelection();
        assertSame(
            selection,
            selection.addSaturated(0)
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public final void testAddIfRelativeZero() {
        this.addIfRelativeAndCheck(
            this.createSelection(),
            0
        );
    }

    // toExpressionReference............................................................................................

    @Test
    public final void testToExpressionReferenceFails() {
        this.toExpressionReferenceFails();
    }

    // toRange.........................................................................................................

    @Test
    public final void testToRange() {
        this.toRangeAndCheck(
            this.createSelection()
        );
    }

    // toParserToken....................................................................................................

    @Test
    public final void testToParserTokenFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection().toParserToken()
        );
    }

    // compare.........................................................................................................

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
}
