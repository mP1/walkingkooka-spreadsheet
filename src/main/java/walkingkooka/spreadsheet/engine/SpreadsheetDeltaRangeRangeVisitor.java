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

package walkingkooka.spreadsheet.engine;

import walkingkooka.ToStringBuilder;
import walkingkooka.compare.Range;
import walkingkooka.compare.RangeVisitor;
import walkingkooka.spreadsheet.SpreadsheetRange;

/**
 * A {@link RangeVisitor} used to format a {@link Range} so a COLON appears between bounds rather than double dot.
 */
final class SpreadsheetDeltaRangeRangeVisitor<I extends Comparable<I>> extends RangeVisitor<I> {

    // called by SpreadsheetRange
    static <I extends Comparable<I>> String rangeToString(final Range<I> range) {
        final SpreadsheetDeltaRangeRangeVisitor visitor = new SpreadsheetDeltaRangeRangeVisitor();
        visitor.accept(range);
        return visitor.toString;
    }

    SpreadsheetDeltaRangeRangeVisitor() {
        super();
    }

    @Override
    protected void all() {
        throw new IllegalArgumentException("Invalid range value " + Range.all());
    }

    @Override
    protected void singleton(final I value) {
        this.toString = value.toString();
    }

    @Override
    protected void lowerBoundAll() {
        throw new IllegalArgumentException("Range lower bounds must be inclusive: " + Range.all().lowerBound());
    }

    @Override
    protected void lowerBoundExclusive(final I value) {
        throw new IllegalArgumentException("Range lower bounds must be inclusive: " + value);
    }

    @Override
    protected void lowerBoundInclusive(final I value) {
        this.toString = value.toString();
    }

    @Override
    protected void upperBoundAll() {
        throw new IllegalArgumentException("Range upper bounds must be inclusive: " + Range.all().upperBound());
    }

    @Override
    protected void upperBoundExclusive(final I value) {
        throw new IllegalArgumentException("Range upper bounds must be inclusive: " + value);
    }

    @Override
    protected void upperBoundInclusive(final I value) {
        this.toString = this.toString + SpreadsheetRange.SEPARATOR + value;
    }

    String toString;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.toString)
                .build();
    }
}
