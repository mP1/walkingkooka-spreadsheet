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
 * Holds a {@link SpreadsheetExpressionReference reference} to a {@link SpreadsheetCellReference}.
 */
public final class ReferenceAndSpreadsheetCellReference<T extends SpreadsheetExpressionReference> {

    public static <T extends SpreadsheetExpressionReference> ReferenceAndSpreadsheetCellReference<T> with(final T reference,
                                                                                                          final SpreadsheetCellReference cell) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cell, "cell");

        return new ReferenceAndSpreadsheetCellReference<>(
            reference,
            cell
        );
    }

    private ReferenceAndSpreadsheetCellReference(final T reference,
                                                 final SpreadsheetCellReference cell) {
        this.reference = reference;
        this.cell = cell;
    }

    public T reference() {
        return this.reference;
    }

    private final T reference;

    public ReferenceAndSpreadsheetCellReference<T> setReference(final T reference) {
        return this.reference.equals(reference) ?
            this :
            new ReferenceAndSpreadsheetCellReference<>(
                Objects.requireNonNull(reference, "reference"),
                this.cell
            );
    }

    public SpreadsheetCellReference cell() {
        return this.cell;
    }

    public ReferenceAndSpreadsheetCellReference<T> setCell(final SpreadsheetCellReference cell) {
        return this.cell.equals(cell) ?
            this :
            new ReferenceAndSpreadsheetCellReference<>(
                this.reference,
                Objects.requireNonNull(cell, "cell")
            );
    }

    private final SpreadsheetCellReference cell;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.reference,
            this.cell
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof ReferenceAndSpreadsheetCellReference &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final ReferenceAndSpreadsheetCellReference<?> other) {
        return this.reference.equals(other.reference) &&
            this.cell.equalsIgnoreReferenceKind(other.cell); // not strictly necessary SpreadsheetReferenceKind should be the same
    }


    @Override
    public String toString() {
        return this.reference + "->" + this.cell;
    }
}
