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

import walkingkooka.spreadsheet.SpreadsheetCell;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Consumer} used by {@link SpreadsheetCellRangeReference#cells(Collection, Consumer, Consumer)}.
 */
final class SpreadsheetCellRangeReferenceCellsConsumer implements Consumer<SpreadsheetCellReference> {

    static SpreadsheetCellRangeReferenceCellsConsumer with(final Collection<SpreadsheetCell> cells,
                                                           final Consumer<? super SpreadsheetCell> present,
                                                           final Consumer<? super SpreadsheetCellReference> absent) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(present, "present");
        Objects.requireNonNull(absent, "absent");

        return new SpreadsheetCellRangeReferenceCellsConsumer(cells, present, absent);
    }

    private SpreadsheetCellRangeReferenceCellsConsumer(final Collection<SpreadsheetCell> cells,
                                                       final Consumer<? super SpreadsheetCell> present,
                                                       final Consumer<? super SpreadsheetCellReference> absent) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(present, "present");
        Objects.requireNonNull(absent, "absent");

        cells
            .forEach(this::addCell);

        this.present = present;
        this.absent = absent;
    }

    private void addCell(final SpreadsheetCell cell) {
        this.referenceToCells.put(cell.reference(), cell);
    }

    @Override
    public void accept(final SpreadsheetCellReference spreadsheetCellReference) {
        final SpreadsheetCell cell = this.referenceToCells.get(spreadsheetCellReference);
        if (null != cell) {
            this.present.accept(cell);
        } else {
            this.absent.accept(spreadsheetCellReference);
        }
    }

    final Consumer<? super SpreadsheetCell> present;
    final Consumer<? super SpreadsheetCellReference> absent;
    final Map<SpreadsheetCellReference, SpreadsheetCell> referenceToCells = SpreadsheetSelectionMaps.cell();

    @Override
    public String toString() {
        return this.present + " " + this.absent;
    }
}
