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
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Base class for a range that holds a column or row range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
abstract class SpreadsheetColumnOrRowRangeReference<T extends SpreadsheetColumnOrRowReference & Comparable<T>> extends SpreadsheetSelection
        implements HasRange<T>,
        HasRangeBounds<T>,
        Iterable<T> {

    /**
     * Package private ctor
     */
    SpreadsheetColumnOrRowRangeReference(final Range<T> range) {
        super();
        this.range = range;
    }

    /**
     * Returns the top left column/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public final T begin() {
        return this.range.lowerBound().value().get(); // must exist
    }

    /**
     * Returns the bottom right column/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public final T end() {
        return this.range.upperBound().value().get(); // must exist
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    final Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection movedTo) {
        return this.toScalar()
                .replaceReferencesMapper0(movedTo);
    }

    // count............................................................................................................

    /**
     * Returns the number of columns or rows in this range.
     */
    @Override
    public final long count() {
        return this.end()
                .value()
                - this.begin()
                .value()
                + 1;
    }

    @Override
    public final boolean isFirst() {
        return this.begin().isFirst() && this.isSingle();
    }

    @Override
    public final boolean isLast() {
        return this.begin().isLast() && this.isSingle();
    }

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the top left column/row reference.
     */
    @Override
    public final Range<T> range() {
        return this.range;
    }

    final Range<T> range;

    /**
     * Would be setter that accepts a pair of column/row, and returns a range with those values,
     * creating a new instance if necessary.
     */
    final <R extends SpreadsheetColumnOrRowRangeReference<?>> R setRange0(final Range<T> range) {
        return Cast.to(
                this.range.equals(range) ?
                        this :
                        this.replace(range)
        );
    }

    abstract SpreadsheetColumnOrRowRangeReference<?> replace(final Range<T> range);

    // add..............................................................................................................

    final SpreadsheetColumnOrRowRangeReference<?> add0(final int value) {
        return 0 == value ?
                this :
                this.addNonZero(value);
    }

    abstract SpreadsheetColumnOrRowRangeReference<?> addNonZero(final int value);

    // addSaturated.....................................................................................................

    /**
     * Adds a delta to the value and returns an instance with the result.
     */
    @Override
    public abstract SpreadsheetColumnOrRowRangeReference<?> addSaturated(final int value);

    final SpreadsheetColumnOrRowRangeReference<?> addSaturated0(final int value) {
        return 0 == value ?
                this :
                this.addSaturatedNonZero(value);
    }

    abstract SpreadsheetColumnOrRowRangeReference<?> addSaturatedNonZero(final int value);

    // addIfRelative....................................................................................................

    /**
     * If this column or row is a relative reference add the given delta or return this if absolute.
     */
    public abstract SpreadsheetColumnOrRowRangeReference<?> addIfRelative(final int delta);

    // isSingle.........................................................................................................

    /**
     * Returns true only if this range covers a single column/row.
     */
    public final boolean isSingle() {
        return this.begin().equalsIgnoreReferenceKind(this.end());
    }

    @Override
    public SpreadsheetSelection toScalar() {
        return this.begin();
    }

    @Override
    SpreadsheetSelection toScalarIfUnit() {
        return this.isSingle() ?
                this.begin() :
                this;
    }

    @Override
    public final SpreadsheetCellReference toCell() {
        return this.toScalar()
                .toCell();
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
     * Mapping expression that returns a relative {@link SpreadsheetColumnOrRowReference} given the value.
     */
    abstract T iteratorIntToReference(int value);

    // HasParserToken...................................................................................................

    @Override
    public final SpreadsheetFormulaParserToken toParserToken() {
        throw new UnsupportedOperationException();
    }

    // HashCodeEqualsDefined.......................................................................................

    @Override
    public final int hashCode() {
        return this.range.hashCode();
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
                (SpreadsheetColumnOrRowRangeReference<?>) other,
                includeKind
        );
    }

    private boolean equals1(final SpreadsheetColumnOrRowRangeReference<?> other,
                            final boolean includeKind) {
        return this.begin().equalsNotSameAndNotNull(other.begin(), includeKind) &&
                this.end().equalsNotSameAndNotNull(other.end(), includeKind);
    }

    // toString........................................................................................................

    @Override
    public final String toString() {
        return this.isSingle() ?
                this.begin().toString() :
                this.begin() + SEPARATOR.string() + this.end();
    }
}
