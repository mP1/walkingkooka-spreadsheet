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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

final class BasicSpreadsheetViewportSelectionNavigationContext implements SpreadsheetViewportSelectionNavigationContext {

    static BasicSpreadsheetViewportSelectionNavigationContext with(final Predicate<SpreadsheetColumnReference> columnHidden,
                                                                   final Predicate<SpreadsheetRowReference> rowHidden) {
        return new BasicSpreadsheetViewportSelectionNavigationContext(
                Objects.requireNonNull(columnHidden, "columnHidden"),
                Objects.requireNonNull(rowHidden, "rowHidden")
        );
    }

    private BasicSpreadsheetViewportSelectionNavigationContext(final Predicate<SpreadsheetColumnReference> columnHidden,
                                                               final Predicate<SpreadsheetRowReference> rowHidden) {
        this.columnHidden = columnHidden;
        this.rowHidden = rowHidden;
    }

    @Override
    public boolean isColumnHidden(final SpreadsheetColumnReference column) {
        checkColumn(column);

        return this.columnHidden.test(column);
    }

    private Predicate<SpreadsheetColumnReference> columnHidden;

    @Override
    public boolean isRowHidden(final SpreadsheetRowReference row) {
        checkRow(row);

        return this.rowHidden.test(row);
    }

    private Predicate<SpreadsheetRowReference> rowHidden;

    @Override
    public Optional<SpreadsheetColumnReference> leftColumn(final SpreadsheetColumnReference column) {
        checkColumn(column);

        return move(
                column,
                SpreadsheetColumnReference::isFirst,
                this.columnHidden,
                -1
        );
    }

    @Override
    public Optional<SpreadsheetColumnReference> rightColumn(final SpreadsheetColumnReference column) {
        checkColumn(column);

        return move(
                column,
                SpreadsheetColumnReference::isLast,
                this.columnHidden,
                +1
        );
    }

    @Override
    public Optional<SpreadsheetRowReference> upRow(final SpreadsheetRowReference row) {
        checkRow(row);

        return move(
                row,
                SpreadsheetRowReference::isFirst,
                this.rowHidden,
                -1
        );
    }

    @Override
    public Optional<SpreadsheetRowReference> downRow(final SpreadsheetRowReference row) {
        checkRow(row);

        return move(
                row,
                SpreadsheetRowReference::isLast,
                this.rowHidden,
                +1
        );
    }

    private static <T extends SpreadsheetColumnOrRowReference> Optional<T> move(final T start,
                                                                                final Predicate<T> stop,
                                                                                final Predicate<T> hidden,
                                                                                final int delta) {
        T result = start;

        do {
            if (stop.test(result)) {
                result = hidden.test(start) ?
                        null :
                        start;
                break;
            }

            result = (T) result.addSaturated(delta);

        } while (hidden.test(result));

        return Optional.ofNullable(result);
    }

    private static void checkColumn(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    private static void checkRow(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    @Override
    public String toString() {
        return this.columnHidden + " " + this.rowHidden;
    }
}
