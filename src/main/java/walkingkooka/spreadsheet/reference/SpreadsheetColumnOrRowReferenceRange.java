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

import walkingkooka.Cast;
import walkingkooka.collect.HasRange;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;

import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Base class for a range that holds a column or row range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
abstract class SpreadsheetColumnOrRowReferenceRange<T extends SpreadsheetColumnOrRowReference & Comparable<T>> extends SpreadsheetSelection
        implements HasRange<T>,
        HasRangeBounds<T>,
        Iterable<T> {

    /**
     * Package private ctor
     */
    SpreadsheetColumnOrRowReferenceRange(final Range<T> range) {
        super();
        this.range = range;
    }

    /**
     * Returns the top left column/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final T begin() {
        return this.range.lowerBound().value().get(); // must exist
    }

    /**
     * Returns the bottom right column/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final T end() {
        return this.range.upperBound().value().get(); // must exist
    }

    /**
     * Returns the number of columns or rows in this range.
     */
    public final int count() {
        return this.end()
                .value()
                - this.begin()
                .value()
                + 1;
    }

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the top left column/row reference.
     */
    public final Range<T> range() {
        return this.range;
    }

    final Range<T> range;

    /**
     * Would be setter that accepts a pair of column/row, and returns a range with those values,
     * creating a new instance if necessary.
     */
    final <R extends SpreadsheetColumnOrRowReferenceRange<?>> R setRange0(final Range<T> range) {
        return Cast.to(
                this.range.equals(range) ?
                        this :
                        this.replace(range)
        );
    }

    abstract SpreadsheetColumnOrRowReferenceRange<?> replace(final Range<T> range);

    /**
     * Returns true only if this range covers a single column/row.
     */
    public final boolean isSingle() {
        return this.begin().equals(this.end());
    }

    // Iterable.........................................................................................................

    @Override
    public final Iterator<T> iterator() {
        return IntStream.rangeClosed(
                        this.begin().value(),
                        this.end().value()
                )
                .boxed()
                .map(this::iteratorIntToReference)
                .iterator();
    }

    /**
     * Mapping function that returns a relative {@link SpreadsheetColumnOrRowReference} from the given value.
     */
    abstract T iteratorIntToReference(int value);

    // HashCodeEqualsDefined.......................................................................................

    @Override
    public final int hashCode() {
        return this.range.hashCode();
    }

    @Override
    final boolean equals0(final Object other) {
        return this.equals1(Cast.to(other));
    }

    private boolean equals1(final SpreadsheetColumnOrRowReferenceRange<?> other) {
        return this.range.equals(other.range);
    }

    // toString........................................................................................................

    @Override
    public final String toString() {
        return this.isSingle() ?
                this.begin().toString() :
                this.begin() + SEPARATOR.string() + this.end();
    }
}
