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

import walkingkooka.collect.Range;
import walkingkooka.collect.RangeVisitor;

import java.util.Objects;

/**
 * A {@link RangeVisitor} used to validate the range is both bounds and they are inclusive. All other forms will
 * throw a {@link IllegalArgumentException}.
 */
final class SpreadsheetSelectionRangeRangeVisitor<S extends SpreadsheetSelection & Comparable<S>> extends RangeVisitor<S> {

    // called by SpreadsheetCellRangeReference
    static <S extends SpreadsheetSelection & Comparable<S>> void check(final Range<S> range) {
        Objects.requireNonNull(range, "range");

        new SpreadsheetSelectionRangeRangeVisitor<S>()
            .accept(range);
    }

    private SpreadsheetSelectionRangeRangeVisitor() {
        super();
    }

    @Override
    protected void all() {
        throw new IllegalArgumentException("Invalid range value " + Range.all());
    }

    @Override
    protected void singleton(final S value) {
        // nop
    }

    @Override
    protected void lowerBoundAll() {
        throw new IllegalArgumentException("Range lower bounds must be inclusive: " + Range.all().lowerBound());
    }

    @Override
    protected void lowerBoundExclusive(final S value) {
        throw new IllegalArgumentException("Range lower bounds must be inclusive: " + value);
    }

    @Override
    protected void lowerBoundInclusive(final S value) {
        // nop
    }

    @Override
    protected void upperBoundAll() {
        throw new IllegalArgumentException("Range upper bounds must be inclusive: " + Range.all().upperBound());
    }

    @Override
    protected void upperBoundExclusive(final S value) {
        throw new IllegalArgumentException("Range upper bounds must be inclusive: " + value);
    }

    @Override
    protected void upperBoundInclusive(final S value) {
        // nop
    }
}
