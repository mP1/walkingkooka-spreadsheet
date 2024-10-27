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
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
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

    BasicSpreadsheetEnginePrepareResponse(final BasicSpreadsheetEngine engine,
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

    SpreadsheetDelta go() {
        this.changes.refreshUpdated();

        // columns......................................................................................................
        if (this.shouldSaveUpdateColumns || this.shouldDeleteColumns) {
            for (final Map.Entry<SpreadsheetColumnReference, SpreadsheetColumn> referenceToColumn : this.changes.updatedAndDeletedColumns.entrySet()) {
                final SpreadsheetColumnReference reference = referenceToColumn.getKey();
                final SpreadsheetColumn column = referenceToColumn.getValue();

                if (null != column) {
                    if (this.shouldSaveUpdateColumns) {
                        this.columns.put(
                                reference,
                                column
                        );
                    }
                } else {
                    if (this.shouldDeleteColumns) {
                        this.columns.put(
                                reference,
                                null
                        );
                    }
                }
            }
        }

        // rows.........................................................................................................

        if (this.shouldSaveUpdateRows || this.shouldDeleteRows) {
            for (final Map.Entry<SpreadsheetRowReference, SpreadsheetRow> referenceToRow : this.changes.updatedAndDeletedRows.entrySet()) {
                final SpreadsheetRowReference reference = referenceToRow.getKey();
                final SpreadsheetRow row = referenceToRow.getValue();

                if (null != row) {
                    if (this.shouldSaveUpdateRows) {
                        this.rows.put(
                                reference,
                                row
                        );
                    }
                } else {
                    if (this.shouldDeleteRows) {
                        this.rows.put(
                                reference,
                                null
                        );
                    }
                }
            }
        }

        // labels.......................................................................................................

        if (this.shouldSaveUpdateLabels || this.shouldDeleteLabels || this.shouldSaveUpdateCells) {
            for (final Map.Entry<SpreadsheetLabelName, SpreadsheetLabelMapping> labelNameAndMapping : this.changes.updatedAndDeletedLabels.entrySet()) {
                final SpreadsheetLabelName labelName = labelNameAndMapping.getKey();
                final SpreadsheetLabelMapping labelMapping = labelNameAndMapping.getValue();

                if (null != labelMapping) {
                    if (this.shouldSaveUpdateLabels) {
                        this.labels.put(
                                labelName,
                                labelMapping
                        );
                    }
                    this.addLabelMappingCells(labelMapping);
                } else {
                    if (this.shouldDeleteLabels) {
                        this.labels.put(
                                labelName,
                                null
                        );
                    }
                }
            }
        }

        // cells........................................................................................................

        if (this.shouldSaveUpdateCells || this.shouldDeleteCells || this.shouldSaveUpdateLabels || this.shouldDeleteLabels || this.shouldSaveUpdateColumns || this.shouldDeleteColumns || this.shouldSaveUpdateRows || this.shouldDeleteRows) {
            for (final Map.Entry<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell : this.changes.updatedAndDeletedCells.entrySet()) {
                final SpreadsheetCellReference cellReference = cellReferenceToCell.getKey();
                final SpreadsheetCell cell = cellReferenceToCell.getValue();

                if (null != cell) {
                    if (this.shouldSaveUpdateCells) {
                        this.cells.put(
                                cellReference,
                                cell
                        );
                    }
                    if (this.shouldSaveUpdateLabels) {
                        for (final SpreadsheetLabelMapping labelMapping : this.labelStore.labels(cellReference)) {

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

                    this.addColumnIfNecessary(
                            cellReference.column()
                    );
                    this.addRowIfNecessary(
                            cellReference.row()
                    );
                } else {
                    if (this.shouldDeleteCells) {
                        this.cells.put(
                                cellReference,
                                null
                        );
                    }
                    this.addColumnIfNecessary(
                            cellReference.column()
                    );
                    this.addRowIfNecessary(
                            cellReference.row()
                    );
                }
            }

            // each cell might have additional labels...................................................................

            for (final Map.Entry<SpreadsheetCellReference, SpreadsheetCell> cellReferenceToCell : this.cells.entrySet()) {
                final SpreadsheetCellReference cellReference = cellReferenceToCell.getKey();
                final SpreadsheetCell cell = cellReferenceToCell.getValue();

                if (null != cell) {
                    if (this.shouldSaveUpdateLabels) {
                        for (final SpreadsheetLabelMapping labelMapping : this.labelStore.labels(cellReference)) {
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
                    this.addColumnIfNecessary(
                            cellReference.column()
                    );
                    this.addRowIfNecessary(
                            cellReference.row()
                    );
                }
            }
        }

        // add labels within the range of the given window.
        if (this.window.isNotEmpty()) {
            // if not adding columns/rows/labels theres no point looping over ranges for their cells
            if (this.shouldSaveUpdateCells && (this.shouldSaveUpdateColumns || this.shouldSaveUpdateRows || this.shouldSaveUpdateLabels)) {
                final Set<SpreadsheetCellReference> cellReferences = Sets.hash();


                for (final SpreadsheetCellRangeReference range : this.window.cellRanges()) {

                    // include all columns and rows within the window.
                    range.cellStream()
                            .forEach(c -> {
                                        if (cellReferences.add(c)) {
                                            addColumnIfNecessary(
                                                    c.column()
                                            );
                                            addRowIfNecessary(
                                                    c.row()
                                            );
                                        }
                                    }
                            );

                    if (this.shouldSaveUpdateLabels) {
                        for (final SpreadsheetLabelMapping labelMapping : this.labelStore.labels(range)) {
                            this.labels.put(
                                    labelMapping.label(),
                                    labelMapping
                            );
                        }
                    }
                }
            }
        }

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
            final Map<SpreadsheetColumnReference, Double> columnsWidths = Maps.sorted(SpreadsheetRowReference.COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR);

            for (final SpreadsheetCellReference cell : this.cells.keySet()) {
                this.addColumnWidthIfNecessary(
                        cell.column()
                                .setReferenceKind(SpreadsheetReferenceKind.RELATIVE),
                        columnsWidths
                );
            }

            delta = delta.setColumnWidths(columnsWidths);
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.ROW_HEIGHTS)) {
            final Map<SpreadsheetRowReference, Double> rowsHeights = Maps.sorted(SpreadsheetRowReference.COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR);

            for (final SpreadsheetCellReference cell : this.cells.keySet()) {
                this.addRowHeightIfNecessary(
                        cell.row()
                                .setReferenceKind(SpreadsheetReferenceKind.RELATIVE),
                        rowsHeights
                );
            }

            delta = delta.setRowHeights(rowsHeights);
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

    private void addCellIfNecessary(final SpreadsheetCellReference cell) {
        this.addIfNecessary(
                cell,
                this.cells,
                this.cellStore
        );
    }

    private void addColumnIfNecessary(final SpreadsheetColumnReference column) {
        if (this.shouldSaveUpdateColumns) {
            this.addIfNecessary(
                    column,
                    this.columns,
                    this.columnStore
            );
        }
    }

    private void addColumnWidthIfNecessary(final SpreadsheetColumnReference column,
                                           final Map<SpreadsheetColumnReference, Double> columnsWidths) {
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

    private void addLabelMappingCells(final SpreadsheetLabelMapping labelMapping) {
        if (this.shouldSaveUpdateCells) {
            SpreadsheetExpressionReference target;
            do {
                target = labelMapping.target();
                if (target instanceof SpreadsheetLabelName) {
                    target = this.labelStore.load(
                                    target.toLabelName()
                            ).map(SpreadsheetLabelMapping::target)
                            .orElse(null);
                }
            } while (target instanceof SpreadsheetLabelName);

            if (target instanceof SpreadsheetCellReference) {
                this.addCellIfNecessary(
                        target.toCell()
                );
            }
        }
    }


    private void addRowIfNecessary(final SpreadsheetRowReference row) {
        if (this.shouldSaveUpdateRows) {
            this.addIfNecessary(
                    row,
                    this.rows,
                    this.rowStore
            );
        }
    }

    private void addRowHeightIfNecessary(final SpreadsheetRowReference row,
                                         final Map<SpreadsheetRowReference, Double> rowsHeights) {
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

    private <R extends SpreadsheetSelection & Comparable<R>,
            H extends HasSpreadsheetReference<R>>
    void addIfNecessary(final R reference,
                        final Map<R, H> referenceToHas,
                        final SpreadsheetStore<R, H> store) {
        if (false == referenceToHas.containsKey(reference)) {
            final H columnOrRow = store.load(reference)
                    .orElse(null);
            if (null != columnOrRow) {
                referenceToHas.put(
                        reference,
                        columnOrRow
                );
            }
        }
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

    private final Map<SpreadsheetColumnReference, SpreadsheetColumn> columns = Maps.sorted();

    private final SpreadsheetColumnStore columnStore;

    private final Map<SpreadsheetRowReference, SpreadsheetRow> rows = Maps.sorted();

    private final SpreadsheetRowStore rowStore;

    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    private final SpreadsheetCellStore cellStore;

    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> labels = Maps.ordered();

    private final SpreadsheetLabelStore labelStore;
}
