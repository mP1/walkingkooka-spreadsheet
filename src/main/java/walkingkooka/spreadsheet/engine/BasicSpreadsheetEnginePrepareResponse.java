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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Handles creating a {@link SpreadsheetDelta} with all the requested fields in {@link SpreadsheetDeltaProperties}.
 */
final class BasicSpreadsheetEnginePrepareResponse {

    static SpreadsheetDelta prepare(final BasicSpreadsheetEngine engine,
                                    final BasicSpreadsheetEngineChanges changes,
                                    final SpreadsheetViewportWindows window,
                                    final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEnginePrepareResponse(
                engine,
                changes,
                window,
                context
        ).go();
    }

    private BasicSpreadsheetEnginePrepareResponse(final BasicSpreadsheetEngine engine,
                                                  final BasicSpreadsheetEngineChanges changes,
                                                  final SpreadsheetViewportWindows window,
                                                  final SpreadsheetEngineContext context) {
        this.engine = engine;
        this.changes = changes;
        this.window = window;
        this.context = context;

        final SpreadsheetStoreRepository repo = context.storeRepository();
        this.cellStore = repo.cells();
        this.columnStore = repo.columns();
        this.rowStore = repo.rows();
        this.labelStore = repo.labels();

        final Set<SpreadsheetDeltaProperties> deltaProperties = this.changes.deltaProperties;

        this.deltaProperties = deltaProperties;

        this.shouldSaveUpdateColumns = deltaProperties.contains(SpreadsheetDeltaProperties.COLUMNS);
        this.shouldDeleteColumns = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_COLUMNS);

        this.shouldSaveUpdateRows = deltaProperties.contains(SpreadsheetDeltaProperties.ROWS);
        this.shouldDeleteRows = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_ROWS);

        this.shouldSaveUpdateLabels = deltaProperties.contains(SpreadsheetDeltaProperties.LABELS);
        this.shouldDeleteLabels = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_LABELS);

        this.shouldSaveUpdateCells = deltaProperties.contains(SpreadsheetDeltaProperties.CELLS);
        this.shouldDeleteCells = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS);
    }

    private SpreadsheetDelta go() {
        this.changes.commit();

        this.columns();
        this.rows();
        this.labels();
        this.cells();

        this.extractedLabelsWithinWindow();

        // order is important because labels and cells for hidden columns/rows are filtered.
        SpreadsheetDelta delta = SpreadsheetDelta.EMPTY;
        if (this.shouldSaveUpdateColumns) {
            delta = delta.setColumns(
                    extractSavedOrUpdated(this.columns)
            );
        }
        if (this.shouldSaveUpdateRows) {
            delta = delta.setRows(
                    extractSavedOrUpdated(this.rows)
            );
        }

        if (this.shouldSaveUpdateCells) {
            delta = delta.setCells(
                    extractSavedOrUpdated(this.cells)
            );
        }

        if (this.shouldSaveUpdateLabels) {
            delta = delta.setLabels(
                    extractSavedOrUpdated(this.labels)
            );
        }

        if(this.deltaProperties.contains(SpreadsheetDeltaProperties.REFERENCES)) {
            delta = delta.setReferences(
                    this.changes.cellToReferences
            );
        }

        if (this.shouldDeleteCells) {
            delta = delta.setDeletedCells(
                    extractDeleted(this.cells)
            );
        }
        if (this.shouldDeleteColumns) {
            delta = delta.setDeletedColumns(
                    extractDeleted(this.columns)
            );
        }
        if (this.shouldDeleteRows) {
            delta = delta.setDeletedRows(
                    extractDeleted(this.rows)
            );
        }
        if (this.shouldDeleteLabels) {
            delta = delta.setDeletedLabels(
                    extractDeleted(this.labels)
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.COLUMN_WIDTHS)) {
            delta = delta.setColumnWidths(
                    this.columnsWidths()
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.ROW_HEIGHTS)) {
            delta = delta.setRowHeights(
                    this.rowHeights()
            );
        }

        final boolean hasColumnCount = deltaProperties.contains(SpreadsheetDeltaProperties.COLUMN_COUNT);
        final boolean hasRowCount = deltaProperties.contains(SpreadsheetDeltaProperties.ROW_COUNT);

        if (hasColumnCount || hasRowCount) {
            if (hasColumnCount) {
                delta = delta.setColumnCount(
                        OptionalInt.of(
                                this.engine.columnCount(this.context)
                        )
                );
            }
            if (hasRowCount) {
                delta = delta.setRowCount(
                        OptionalInt.of(
                                this.engine.rowCount(this.context)
                        )
                );
            }
        }

        return delta;
    }

    private void columns() {
        if (this.shouldSaveUpdateColumns || this.shouldDeleteColumns) {
            final Map<SpreadsheetColumnReference, SpreadsheetColumn> columns = this.columns;

            for (final BasicSpreadsheetEngineChangesCache<SpreadsheetColumnReference, SpreadsheetColumn> referenceAndColumn : this.changes.columns.values()) {
                final SpreadsheetColumnReference column = referenceAndColumn.reference;

                if (referenceAndColumn.isSave()) {
                    if (this.shouldSaveUpdateColumns) {
                        columns.put(
                                column,
                                //column
                                referenceAndColumn.value()
                        );
                    }
                } else {
                    if (referenceAndColumn.isDelete()) {
                        if (this.shouldDeleteColumns) {
                            columns.put(
                                    column,
                                    null
                            );
                        }
                    }
                }
            }
        }
    }

    private final Map<SpreadsheetColumnReference, SpreadsheetColumn> columns = Maps.sorted();

    private void rows() {
        if (this.shouldSaveUpdateRows || this.shouldDeleteRows) {
            final Map<SpreadsheetRowReference, SpreadsheetRow> rows = this.rows;

            for (final BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow> referenceAndRow : this.changes.rows.values()) {
                final SpreadsheetRowReference row = referenceAndRow.reference;

                if (referenceAndRow.isSave()) {
                    if (this.shouldSaveUpdateRows) {
                        rows.put(
                                row,
                                // row
                                referenceAndRow.value()
                        );
                    }
                } else {
                    if (referenceAndRow.isDelete()) {
                        if (this.shouldDeleteRows) {
                            rows.put(
                                    row,
                                    null
                            );
                        }
                    }
                }
            }
        }
    }

    private final Map<SpreadsheetRowReference, SpreadsheetRow> rows = Maps.sorted();

    private void labels() {
        if (this.shouldSaveUpdateLabels || this.shouldDeleteLabels || this.shouldSaveUpdateCells) {
            for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> nameAndMapping : this.changes.labels.values()) {
                final SpreadsheetLabelName labelName = nameAndMapping.reference;

                if (nameAndMapping.isLoadOrSave()) {
                    final SpreadsheetLabelMapping labelMapping = nameAndMapping.value();
                    if (this.shouldSaveUpdateLabels) {
                        this.labels.put(
                                labelName,
                                labelMapping
                        );
                    }
                    this.addLabelMappingCells(labelMapping);
                } else {
                    if (nameAndMapping.isDelete()) {
                        if (this.shouldDeleteLabels) {
                            this.labels.put(
                                    labelName,
                                    null
                            );
                        }
                    }
                }
            }
        }
    }

    private void extractedLabelsWithinWindow() {
        // add labels within the range of the given window.
        if (this.window.isNotEmpty()) {
            // if not adding columns/rows/labels theres no point looping over ranges for their cells
            if (this.shouldSaveUpdateCells && (this.shouldSaveUpdateColumns || this.shouldSaveUpdateRows || this.shouldSaveUpdateLabels)) {
                final Set<SpreadsheetCellReference> cells = Sets.hash();

                for (final SpreadsheetCellRangeReference cellRange : this.window.cellRanges()) {

                    // include all columns and rows within the window.
                    cellRange.cellStream()
                            .forEach(c -> {
                                        if (cells.add(c)) {
                                            addColumn(
                                                    c.column()
                                            );
                                            addRow(
                                                    c.row()
                                            );
                                        }
                                    }
                            );

                    if (this.shouldSaveUpdateLabels) {
                        for (final SpreadsheetLabelMapping labelMapping : this.labelStore.findLabelsWithReference(
                                cellRange,
                                0,
                                BasicSpreadsheetEngine.FIND_LABELS_WITH_REFERENCE_COUNT
                        )) {
                            this.labels.put(
                                    labelMapping.label(),
                                    labelMapping
                            );
                        }
                    }
                }
            }
        }
    }

    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> labels = Maps.ordered();

    private void cells() {
        if (this.shouldSaveUpdateCells || this.shouldDeleteCells || this.shouldSaveUpdateLabels || this.shouldDeleteLabels || this.shouldSaveUpdateColumns || this.shouldDeleteColumns || this.shouldSaveUpdateRows || this.shouldDeleteRows) {

            for (final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> referenceAndCell : this.changes.cells.values()) {
                final SpreadsheetCellReference cell = referenceAndCell.reference;

                // dont want to include cells loaded because they are referenced in a formula
                if (referenceAndCell.isLoadOrSave()) {
                    if (this.shouldSaveUpdateCells) {
                        this.cells.put(
                                cell,
                                referenceAndCell.value()
                        );
                    }

                    this.addLabelMappingCells(cell);
                } else {
                    if (referenceAndCell.isDelete() && this.shouldDeleteCells) {
                        this.cells.put(
                                cell,
                                null
                        );
                    }
                    this.addColumn(
                            cell.column()
                    );
                    this.addRow(
                            cell.row()
                    );
                }
            }

            // each cell might have additional labels...................................................................

            for (final Map.Entry<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell : this.cells.entrySet()) {
                final SpreadsheetCellReference cellReference = cellReferenceToCell.getKey();
                final SpreadsheetCell cell = cellReferenceToCell.getValue();

                if (null != cell) {
                    this.addLabelMappingCells(cellReference);
                }
            }
        }
    }

    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    private void addCell(final SpreadsheetCellReference cell) {
        if (this.window.test(cell)) {
            if (this.add(
                    cell,
                    this.cells,
                    this.cellStore
            )) {
                this.addColumn(
                        cell.column()
                );
                this.addRow(
                        cell.row()
                );
            }
        }
    }

    private void addColumn(final SpreadsheetColumnReference column) {
        if (this.shouldSaveUpdateColumns) {
            this.add(
                    column,
                    this.columns,
                    this.columnStore
            );
        }
    }

    private Map<SpreadsheetColumnReference, Double> columnsWidths() {
        final Map<SpreadsheetColumnReference, Double> columnsWidths = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        for (final SpreadsheetCellReference cell : this.cells.keySet()) {
            final SpreadsheetColumnReference column = cell.column().toRelative();

            if (false == columnsWidths.containsKey(column)) {
                final double width = this.engine.columnWidth(
                        column,
                        this.context
                );
                if (width > 0) {
                    columnsWidths.put(column, width);
                }
            }
        }
        return columnsWidths;
    }

    private void addLabelMappingCells(final SpreadsheetCellReference cell) {
        if (this.shouldSaveUpdateLabels) {
            for (final SpreadsheetLabelMapping labelMapping : this.labelStore.findLabelsWithReference(
                    cell,
                    0,
                    BasicSpreadsheetEngine.FIND_LABELS_WITH_REFERENCE_COUNT
            )) {

                final SpreadsheetLabelName labelName = labelMapping.label();
                if (false == this.labels.containsKey(labelName)) {
                    this.labels.put(
                            labelName,
                            labelMapping
                    );

                    this.addLabelMappingCells(labelMapping);
                }
            }
        }
    }

    private void addLabelMappingCells(final SpreadsheetLabelMapping labelMapping) {
        if (this.shouldSaveUpdateCells) {
            SpreadsheetExpressionReference reference;
            do {
                reference = labelMapping.reference();
                if (reference.isLabelName()) {
                    reference = this.labelStore.load(
                                    reference.toLabelName()
                            ).map(SpreadsheetLabelMapping::reference)
                            .orElse(null);
                }
            } while (null != reference && reference.isLabelName());

            if (null != reference && reference.isCell()) {
                this.addCell(
                        reference.toCell()
                );
            }
        }
    }

    private void addRow(final SpreadsheetRowReference row) {
        if (this.shouldSaveUpdateRows) {
            this.add(
                    row,
                    this.rows,
                    this.rowStore
            );
        }
    }

    private Map<SpreadsheetRowReference, Double> rowHeights() {
        final Map<SpreadsheetRowReference, Double> rowsHeights = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        for (final SpreadsheetCellReference cell : this.cells.keySet()) {
            final SpreadsheetRowReference row = cell.row()
                    .toRelative();
            if (false == rowsHeights.containsKey(row)) {
                final double height = this.engine.rowHeight(
                        row,
                        this.context
                );
                if (height > 0) {
                    rowsHeights.put(row, height);
                }
            }
        }
        return rowsHeights;
    }

    private <R extends SpreadsheetSelection & Comparable<R>, H extends HasSpreadsheetReference<R>> boolean add(final R reference,
                                                                                                               final Map<R, H> referenceToHas,
                                                                                                               final SpreadsheetStore<R, H> store) {
        boolean added = false;

        if (false == referenceToHas.containsKey(reference)) {
            final H columnOrRow = store.load(reference)
                    .orElse(null);
            if (null != columnOrRow) {
                referenceToHas.put(
                        reference,
                        columnOrRow
                );

                added = true;
            }
        }

        return added;
    }

    private static <T extends HateosResource<? extends SpreadsheetSelection>> Set<T> extractSavedOrUpdated(final Map<?, T> referenceToEntities) {
        final Set<T> saveOrUpdated = SortedSets.tree();

        for (final T value : referenceToEntities.values()) {
            if (null != value) {
                saveOrUpdated.add(value);
            }
        }

        return saveOrUpdated;
    }

    private static <T extends SpreadsheetSelection> Set<T> extractDeleted(final Map<T, ?> referenceToEntities) {
        final Set<T> deleted = SortedSets.tree();

        for (final Map.Entry<T, ?> referenceToColumnOrRow : referenceToEntities.entrySet()) {
            if (null == referenceToColumnOrRow.getValue()) {
                deleted.add(
                        referenceToColumnOrRow.getKey()
                );
            }
        }

        return deleted;
    }

    private final BasicSpreadsheetEngine engine;

    private final BasicSpreadsheetEngineChanges changes;

    private final SpreadsheetViewportWindows window;

    private final SpreadsheetEngineContext context;

    private final Set<SpreadsheetDeltaProperties> deltaProperties;

    private final boolean shouldSaveUpdateColumns;

    private final boolean shouldDeleteColumns;

    private final boolean shouldSaveUpdateRows;

    private final boolean shouldDeleteRows;

    private final boolean shouldSaveUpdateLabels;

    private final boolean shouldDeleteLabels;

    private final boolean shouldSaveUpdateCells;

    private final boolean shouldDeleteCells;

    private final SpreadsheetColumnStore columnStore;

    private final SpreadsheetRowStore rowStore;

    private final SpreadsheetCellStore cellStore;

    private final SpreadsheetLabelStore labelStore;
}
