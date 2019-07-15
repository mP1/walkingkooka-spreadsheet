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

package walkingkooka.spreadsheet;

import walkingkooka.compare.Range;
import walkingkooka.compare.RangeVisitor;

import java.util.Objects;

/**
 * A {@link RangeVisitor} used to validate the range is both bounds and they are inclusive. All other forms will
 * throw a {@link IllegalArgumentException}.
 */
final class SpreadsheetRangeRangeVisitor extends RangeVisitor<SpreadsheetCellReference> {

    // called by SpreadsheetRange
    static SpreadsheetRangeRangeVisitor check(final Range<SpreadsheetCellReference> range) {
        Objects.requireNonNull(range, "range");

        final SpreadsheetRangeRangeVisitor visitor = new SpreadsheetRangeRangeVisitor();
        visitor.accept(range);
        return visitor;
    }

    private SpreadsheetRangeRangeVisitor() {
        super();
    }

    @Override
    protected void all() {
        throw new IllegalArgumentException("Invalid range value " + Range.all());
    }

    @Override
    protected void singleton(final SpreadsheetCellReference value) {
        this.topLeft = value;
        this.bottomRight = value;
    }

    @Override
    protected void lowerBoundAll() {
        throw new IllegalArgumentException("Range lower bounds must be inclusive: " + Range.all().lowerBound());
    }

    @Override
    protected void lowerBoundExclusive(final SpreadsheetCellReference value) {
        throw new IllegalArgumentException("Range lower bounds must be inclusive: " + value);
    }

    @Override
    protected void lowerBoundInclusive(final SpreadsheetCellReference value) {
        this.topLeft = value;
    }

    @Override
    protected void upperBoundAll() {
        throw new IllegalArgumentException("Range upper bounds must be inclusive: " + Range.all().upperBound());
    }

    @Override
    protected void upperBoundExclusive(final SpreadsheetCellReference value) {
        throw new IllegalArgumentException("Range upper bounds must be inclusive: " + value);
    }

    @Override
    protected void upperBoundInclusive(final SpreadsheetCellReference value) {
        this.bottomRight = value;
    }

    SpreadsheetCellReference topLeft;
    SpreadsheetCellReference bottomRight;
}
