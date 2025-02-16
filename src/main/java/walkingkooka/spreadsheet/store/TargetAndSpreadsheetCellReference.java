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

package walkingkooka.spreadsheet.store;

import walkingkooka.Cast;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;

import java.util.Objects;

/**
 * Holds a {@link SpreadsheetExpressionReference source} to a {@link SpreadsheetCellReference}.
 */
public final class TargetAndSpreadsheetCellReference<T extends SpreadsheetExpressionReference> {

    public static <T extends SpreadsheetExpressionReference> TargetAndSpreadsheetCellReference<T> with(final T target,
                                                                                                       final SpreadsheetCellReference cell) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(cell, "cell");

        return new TargetAndSpreadsheetCellReference<>(
                target,
                cell
        );
    }

    private TargetAndSpreadsheetCellReference(final T target,
                                              final SpreadsheetCellReference cell) {
        this.target = target;
        this.cell = cell;
    }

    public T target() {
        return this.target;
    }

    private final T target;

    public SpreadsheetCellReference cell() {
        return this.cell;
    }

    private final SpreadsheetCellReference cell;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.target,
                this.cell
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof TargetAndSpreadsheetCellReference &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final TargetAndSpreadsheetCellReference<?> other) {
        return this.target.equals(other.target) &&
                this.cell.equalsIgnoreReferenceKind(other.cell); // not strictly necessary SpreadsheetReferenceKind should be the same
    }


    @Override
    public String toString() {
        return this.target + "->" + this.cell;
    }
}
