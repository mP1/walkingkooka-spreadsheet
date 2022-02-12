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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaNonWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaNonWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaNonWindowed withNonWindowed(final Optional<SpreadsheetViewportSelection> selection,
                                                       final Set<SpreadsheetCell> cells,
                                                       final Set<SpreadsheetLabelMapping> labels,
                                                       final Set<SpreadsheetCellReference> deletedCells,
                                                       final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                       final Map<SpreadsheetRowReference, Double> rowHeights) {
        return new SpreadsheetDeltaNonWindowed(
                selection,
                cells,
                labels,
                deletedCells,
                columnWidths,
                rowHeights
        );
    }

    private SpreadsheetDeltaNonWindowed(final Optional<SpreadsheetViewportSelection> selection,
                                        final Set<SpreadsheetCell> cells,
                                        final Set<SpreadsheetLabelMapping> labels,
                                        final Set<SpreadsheetCellReference> deletedCells,
                                        final Map<SpreadsheetColumnReference, Double> columnWidths,
                                        final Map<SpreadsheetRowReference, Double> rowHeights) {
        super(
                selection,
                cells,
                labels,
                deletedCells,
                columnWidths,
                rowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceSelection(final Optional<SpreadsheetViewportSelection> selection) {
        return new SpreadsheetDeltaNonWindowed(
                selection,
                this.cells,
                this.labels,
                this.deletedCells,
                this.columnWidths,
                this.rowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells) {
        // cells have already been filtered by window
        return new SpreadsheetDeltaNonWindowed(
                this.selection,
                cells,
                this.labels,
                this.deletedCells,
                this.columnWidths,
                this.rowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels) {
        return new SpreadsheetDeltaNonWindowed(
                this.selection,
                this.cells,
                labels,
                this.deletedCells,
                this.columnWidths,
                this.rowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return new SpreadsheetDeltaNonWindowed(
                this.selection,
                this.cells,
                this.labels,
                deletedCells,
                this.columnWidths,
                this.rowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return new SpreadsheetDeltaNonWindowed(
                this.selection,
                this.cells,
                this.labels,
                this.deletedCells,
                columnWidths,
                this.rowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return new SpreadsheetDeltaNonWindowed(
                this.selection,
                this.cells,
                this.labels,
                this.deletedCells,
                this.columnWidths,
                rowHeights
        );
    }

    /**
     * There is no window.
     */
    @Override
    public Optional<SpreadsheetCellRange> window() {
        return NO_WINDOW;
    }

    @Override
    Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells) {
        final Set<SpreadsheetCell> copy = Sets.sorted();
        copy.addAll(cells);
        return Sets.immutable(copy);
    }

    @Override
    Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return filter(
                deletedCells,
                Predicates.always(),
                SpreadsheetCellReference::toRelative
        );
    }

    @Override
    Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        final Map<SpreadsheetColumnReference, Double> copy = Maps.sorted();

        for(final Map.Entry<SpreadsheetColumnReference, Double> columnAndWidth : columnWidths.entrySet()) {
            copy.put(
                    columnAndWidth.getKey().toRelative(),
                    columnAndWidth.getValue()
            );
        }

        return Maps.immutable(copy);
    }

    @Override
    Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        final Map<SpreadsheetRowReference, Double> copy = Maps.sorted();

        for(final Map.Entry<SpreadsheetRowReference, Double> rowAndHeight : rowHeights.entrySet()) {
            copy.put(
                    rowAndHeight.getKey().toRelative(),
                    rowAndHeight.getValue()
            );
        }

        return Maps.immutable(copy);
    }

    // TreePrintable.....................................................................................................

    @Override
    void printWindow(final IndentingPrinter printer) {
        // nop
    }

    // Object...........................................................................................................

    @Override
    void toStringWindow(final ToStringBuilder b) {
    }
}
