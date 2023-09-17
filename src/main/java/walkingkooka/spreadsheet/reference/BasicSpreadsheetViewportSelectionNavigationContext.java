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

        SpreadsheetColumnReference left = column;

        for (; ; ) {
            if (left.isFirst()) {
                left = this.isColumnHidden(column) ?
                        null :
                        column;
                break;
            }

            left = left.addSaturated(-1);

            if (!this.isColumnHidden(left)) {
                break;
            }
        }

        return Optional.ofNullable(left);
    }

    @Override
    public Optional<SpreadsheetColumnReference> rightColumn(final SpreadsheetColumnReference column) {
        checkColumn(column);

        SpreadsheetColumnReference right = column;

        for (; ; ) {
            if (right.isLast()) {
                right = this.isColumnHidden(column) ?
                        null :
                        column;
                break;
            }

            right = right.addSaturated(+1);

            if (!this.isColumnHidden(right)) {
                break;
            }
        }

        return Optional.ofNullable(right);
    }

    @Override
    public Optional<SpreadsheetRowReference> upRow(final SpreadsheetRowReference row) {
        checkRow(row);

        SpreadsheetRowReference up = row;

        for (; ; ) {
            if (up.isFirst()) {
                up = this.isRowHidden(row) ?
                        null :
                        row;
                break;
            }

            up = up.addSaturated(-1);

            if (!this.isRowHidden(up)) {
                break;
            }
        }

        return Optional.ofNullable(up);
    }

    @Override
    public Optional<SpreadsheetRowReference> downRow(final SpreadsheetRowReference row) {
        checkRow(row);

        SpreadsheetRowReference down = row;

        for (; ; ) {
            if (down.isLast()) {
                down = this.isRowHidden(row) ?
                        null :
                        row;
                break;
            }

            down = down.addSaturated(+1);

            if (!this.isRowHidden(down)) {
                break;
            }
        }

        return Optional.ofNullable(down);
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
