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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetColumnOrRow;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparators;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigation;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigationContexts;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.validation.form.SpreadsheetFormHandlerContext;
import walkingkooka.spreadsheet.validation.form.SpreadsheetFormHandlerContexts;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.Validator;
import walkingkooka.validation.form.DuplicateFormFieldReferencesException;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;
import walkingkooka.validation.form.FormHandler;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The default or basic implementation of {@link SpreadsheetEngine} that includes support for evaluating cells.
 */
final class BasicSpreadsheetEngine implements SpreadsheetEngine {

    /**
     * Singleton
     */
    final static BasicSpreadsheetEngine INSTANCE = new BasicSpreadsheetEngine();

    /**
     * A safe maximum to query labels for a {@link walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference}.
     * Maybe this should be moved into context or made a variable somewhere
     */
    final static int FIND_LABELS_WITH_REFERENCE_COUNT = Integer.MAX_VALUE;

    /**
     * A safe maximum to limit finding of references when satisfying {@link SpreadsheetDeltaProperties#REFERENCES}.
     */
    final static int FIND_REFERENCES_COUNT = Integer.MAX_VALUE;

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine() {
        super();
    }

    // LOAD CELL........................................................................................................

    /**
     * Loads the selected {@link SpreadsheetSelection cells} honouring the {@link SpreadsheetEngineEvaluation} which may
     * result in loading and evaluating other cells. Note if the cells were not found and labels are requested the
     * labels will be loaded and present in the {@link SpreadsheetDelta}.
     */
    @Override
    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(evaluation, "evaluation");
        Objects.requireNonNull(deltaProperties, "deltaProperties");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                evaluation,
                deltaProperties,
                context
        );

        try {
            if (selection.isLabelName()) {
                changes.getOrCreateLabelCache(
                        selection.toLabelName(),
                        BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED
                );
            }

            this.loadCellRange(
                    context.resolveIfLabelOrFail(selection)
                            .toCellRange(),
                    changes,
                    context
            );

            // finish evaluating loaded cells
            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // LOAD MULTIPLE CELL RANGES........................................................................................

    @Override
    public SpreadsheetDelta loadMultipleCellRanges(final Set<SpreadsheetCellRangeReference> cellRanges,
                                                   final SpreadsheetEngineEvaluation evaluation,
                                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                   final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cellRanges, "cellRanges");
        Objects.requireNonNull(evaluation, "evaluation");
        Objects.requireNonNull(deltaProperties, "deltaProperties");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                evaluation,
                deltaProperties,
                context
        );

        try {
            for (final SpreadsheetCellRangeReference cellRange : cellRanges) {
                this.loadCellRange(
                        cellRange,
                        changes,
                        context
                );
            }

            // finish evaluating loaded cells
            changes.commit();

            final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.with(cellRanges);

            return this.prepareResponse(
                    changes,
                    window,
                    context
            ).setWindow(window);
        } finally {
            changes.close();
        }
    }

    private void loadCellRange(final SpreadsheetCellRangeReference cellRange,
                               final BasicSpreadsheetEngineChanges changes,
                               final SpreadsheetEngineContext context) {
        if(cellRange.count() == 1) {
            this.loadCell(
                    cellRange.toCell(),
                    changes,
                    context
            );
        } else {
            final SpreadsheetCellStore store = context.storeRepository()
                    .cells();

            for (final SpreadsheetCellReference cell : cellRange) {
                changes.getOrCreateCellCache(
                        cell,
                        BasicSpreadsheetEngineChangesCacheStatusCell.DELETED
                );
            }

            final Set<SpreadsheetCell> spreadsheetCells = store.loadCellRange(cellRange);
            for (final SpreadsheetCell spreadsheetCell : spreadsheetCells) {
                final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = changes.getOrCreateCellCache(
                        spreadsheetCell.reference(),
                        BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED
                );
                cache.loading(spreadsheetCell);
            }
        }
    }

    private void loadCell(final SpreadsheetCellReference cell,
                          final BasicSpreadsheetEngineChanges changes,
                          final SpreadsheetEngineContext context) {
        final SpreadsheetCellStore store = context.storeRepository()
                .cells();

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = changes.getOrCreateCellCache(
                cell,
                BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED
        );
        cache.loadingOrMissing(
                store.load(cell)
                        .orElse(null)
        );
    }

    // SAVE CELL........................................................................................................

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = changes.getOrCreateCellCache(
                    cell.reference(),
                    BasicSpreadsheetEngineChangesCacheStatusCell.SAVING
            );
            cache.saving(cell);
            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // SAVE CELLS.......................................................................................................

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta saveCells(final Set<SpreadsheetCell> cells,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(context, "context");

        // save all cells.
        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            for (final SpreadsheetCell cell : cells) {
                final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = changes.getOrCreateCellCache(
                        cell.reference(),
                        BasicSpreadsheetEngineChangesCacheStatusCell.SAVING
                );
                cache.saving(cell);
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // DELETE CELL....................................................................................................

    /**
     * DELETE the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta deleteCells(final SpreadsheetSelection selection,
                                        final SpreadsheetEngineContext context) {
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            context.storeRepository()
                    .cells()
                    .deleteCells(
                            context.resolveIfLabelOrFail(selection)
                                    .toCellRange()
                    );

            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // FILL CELLS.......................................................................................................

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetCellRangeReference from,
                                      final SpreadsheetCellRangeReference to,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(from, "parse");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            BasicSpreadsheetEngineFillCells.execute(
                    cells,
                    from,
                    to,
                    this,
                    changes,
                    context
            );

            changes.commit();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    // FILTER CELLS....................................................................................................

    @Override
    public Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final String valueType,
                                            final Expression expression,
                                            final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        CharSequences.failIfNullOrEmpty(valueType, "valueType");
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            return cells.stream()
                    .filter(
                            BasicSpreadsheetEngineFilterCellsPredicate.with(
                                    valueType,
                                    expression,
                                    context.spreadsheetEngineContext(
                                            SpreadsheetMetadataPropertyName.FIND_FUNCTIONS
                                    ),
                                    changes
                            )
                    ).collect(Collectors.toCollection(Sets::ordered));
        } finally {
            changes.close();
        }
    }

    // FIND CELLS.......................................................................................................

    @Override
    public SpreadsheetDelta findCells(final SpreadsheetCellRangeReference cellRange,
                                      final SpreadsheetCellRangeReferencePath path,
                                      final int offset,
                                      final int count,
                                      final String valueType,
                                      final Expression expression,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cellRange, "cellRange");
        Objects.requireNonNull(path, "path");
        SpreadsheetEngine.checkOffsetAndCount(
                offset,
                count
        );
        CharSequences.failIfNullOrEmpty(valueType, "valueType");
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(deltaProperties, "deltaProperties");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                deltaProperties,
                context
        );

        try {
            // this will be used to filter individual cells matching the find range and type.
            final Predicate<SpreadsheetCell> filterPredicate = BasicSpreadsheetEngineFilterCellsPredicate.with(
                    valueType,
                    expression,
                    context.spreadsheetEngineContext(
                            SpreadsheetMetadataPropertyName.FIND_FUNCTIONS
                    ),
                    changes
            );

            final Set<SpreadsheetCell> found = SortedSets.tree(
                    SpreadsheetCellReference.cellComparator(
                            path.comparator()
                    )
            );

            final SpreadsheetCellStore store = context.storeRepository()
                    .cells();
            int loadOffset = 0;
            int skipOffset = 0;

            for (; ; ) {
                final int maxLeft = count - found.size();
                if (maxLeft <= 0) {
                    break;
                }

                final Collection<SpreadsheetCell> loaded = store.loadCellRange(
                        cellRange,
                        path,
                        loadOffset,
                        maxLeft
                );
                if (loaded.isEmpty()) {
                    break;
                }

                for (final SpreadsheetCell possible : loaded) {
                    loadOffset++;

                    final SpreadsheetCell loadedAndEval = this.evaluateValidateFormatAndStyle(
                            possible,
                            SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                            changes, // SpreadsheetExpressionReferenceLoader
                            context
                    );

                    if (filterPredicate.test(loadedAndEval)) {
                        if (skipOffset >= offset) {
                            found.add(loadedAndEval);
                            changes.onCellLoading(loadedAndEval);
                        }
                        skipOffset++;
                    }
                }
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // FIND CELLS WITH REFERENCE........................................................................................

    @Override
    public SpreadsheetDelta findCellsWithReference(final SpreadsheetExpressionReference reference,
                                                   final int offset,
                                                   final int count,
                                                   final SpreadsheetEngineContext context) {
        Objects.requireNonNull(reference, "reference");
        SpreadsheetEngine.checkOffsetAndCount(
                offset,
                count
        );
        Objects.requireNonNull(context, "context");

        return this.findCellsWithReferenceWithCellOrCellRange(
                context.resolveIfLabelOrFail(reference)
                        .toCellOrCellRange(),
                offset,
                count,
                context
        );
    }

    private SpreadsheetDelta findCellsWithReferenceWithCellOrCellRange(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                                       final int offset,
                                                                       final int count,
                                                                       final SpreadsheetEngineContext context) {
        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                FIND_CELLS_WITH_REFERENCE_DELTA_PROPERTIES,
                context
        );

        try {
            for (final SpreadsheetCellReference cell : context.storeRepository()
                    .cellReferences()
                    .findCellsWithCellOrCellRange(
                            cellOrCellRange,
                            offset,
                            count
                    )) {
                changes.getOrCreateCellCache(
                        cell,
                        BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED
                );
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    private final static Set<SpreadsheetDeltaProperties> FIND_CELLS_WITH_REFERENCE_DELTA_PROPERTIES = Sets.of(
            SpreadsheetDeltaProperties.CELLS,
            SpreadsheetDeltaProperties.REFERENCES
    );

    // findFormulaReferences............................................................................................

    @Override
    public SpreadsheetDelta findFormulaReferences(final SpreadsheetCellReference cell,
                                                  final int offset,
                                                  final int count,
                                                  final Set<SpreadsheetDeltaProperties> properties,
                                                  final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        SpreadsheetEngine.checkOffsetAndCount(
                offset,
                count
        );
        Objects.requireNonNull(properties, "properties");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                properties,
                context
        );

        try {
            final SpreadsheetStoreRepository repository = context.storeRepository();
            final SpreadsheetCellReferencesStore cellReferencesStore = repository.cellReferences();

            int skipCount = 0;
            int loadedCount = 0;

            // https://github.com/mP1/walkingkooka-spreadsheet/issues/5634 SpreadsheetExpressionReferenceStore.loadReferences(SpreadsheetCellReference, int offset, int count)
            for (final SpreadsheetCellReference reference : cellReferencesStore.findCellsWithReference(
                    cell,
                    0, // offset
                    FIND_REFERENCES_COUNT // count
            )) {
                if (skipCount < offset) {
                    skipCount++;
                    continue;
                }

                if (loadedCount >= count) {
                    break;
                }

                changes.getOrCreateCellCache(
                        reference.toCell(),
                        BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED
                );

                loadedCount++;
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // SORT CELLS.......................................................................................................

    @Override
    public SpreadsheetDelta sortCells(final SpreadsheetCellRangeReference cellRange,
                                      final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cellRange, "cellRange");
        Objects.requireNonNull(comparators, "comparators");
        Objects.requireNonNull(deltaProperties, "deltaProperties");
        Objects.requireNonNull(context, "context");

        return this.sortCells0(
                cellRange,
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(comparators),
                deltaProperties,
                context
        );
    }

    private SpreadsheetDelta sortCells0(final SpreadsheetCellRangeReference cellRange,
                                        final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList comparators,
                                        final Set<SpreadsheetDeltaProperties> deltaProperties,
                                        final SpreadsheetEngineContext context) {
        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                deltaProperties,
                context
        );
        try {
            final Set<SpreadsheetCell> loaded = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
            final SpreadsheetCellStore cellStore = context.storeRepository()
                    .cells();

            for (final SpreadsheetCell cell : cellStore.loadCellRange(cellRange)) {

                final SpreadsheetCell evaluated = this.parseFormulaEvaluateValidateFormatStyleAndSave(
                        cell,
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        changes, // SpreadsheetExpressionReferenceLoader
                        context
                );
                loaded.add(evaluated);
            }

            final SpreadsheetCellRange range = cellRange.setValue(loaded);

            final Map<SpreadsheetCell, SpreadsheetCell> movedFromTo = Maps.sorted(SpreadsheetCell.REFERENCE_COMPARATOR);

            this.sortCells1(
                    range,
                    SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(comparators),
                    movedFromTo::put,
                    context
            );

            // delete old cells...
            for (final SpreadsheetCell cell : movedFromTo.keySet()) {
                cellStore.delete(cell.reference());
            }

            // save moved cells
            for (final SpreadsheetCell to : movedFromTo.values()) {
                final SpreadsheetCell saved = this.parseFormulaEvaluateValidateFormatStyleAndSave(
                        to,
                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                        changes, // SpreadsheetExpressionReferenceLoader
                        context
                );
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    private void sortCells1(final SpreadsheetCellRange cells,
                            final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList columnOrRowAndComparatorNames,
                            final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedFromTo,
                            final SpreadsheetEngineContext context) {

        final SpreadsheetMetadata metadata = context.spreadsheetMetadata();
        final SpreadsheetComparatorNameList sortComparators = metadata.getOrFail(SpreadsheetMetadataPropertyName.SORT_COMPARATORS);

        final Set<SpreadsheetComparatorName> requiredNames = columnOrRowAndComparatorNames.names();

        final String missing = requiredNames.stream()
                .filter(n -> false == sortComparators.contains(n))
                .map(SpreadsheetComparatorName::toString)
                .collect(Collectors.joining(","));
        if (false == missing.isEmpty()) {
            throw new IllegalArgumentException("Invalid comparators: " + missing);
        }

        final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators = columnOrRowAndComparatorNames.stream()
                .map(n -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                                n.columnOrRow(),
                                n.comparatorNameAndDirections()
                                        .stream()
                                        .map(
                                                nad -> nad.direction()
                                                        .apply(
                                                                context.spreadsheetComparator(
                                                                        nad.name(),
                                                                        Lists.empty(),
                                                                        context // ProviderContext
                                                                )
                                                        )
                                        ).collect(Collectors.toList())
                        )
                ).collect(Collectors.toList());

        cells.sort(
                comparators,
                movedFromTo, // moved cells
                metadata.sortSpreadsheetComparatorContext(
                        context, // ConverterProvider
                        context, // SpreadsheetLabelNameResolver
                        context // ProviderContext
                )
        );
    }

    // LOAD COLUMNS.....................................................................................................

    @Override
    public SpreadsheetDelta loadColumn(final SpreadsheetColumnReference column,
                                       final SpreadsheetEngineContext context) {
        return SpreadsheetDelta.EMPTY
                .setColumns(
                        toSet(
                                context.storeRepository()
                                        .columns()
                                        .load(column)
                        )
                );
    }

    // SAVE COLUMN.....................................................................................................

    /**
     * Saves the {@link SpreadsheetColumn} and then loads and saves all the cells in that column.
     */
    @Override
    public SpreadsheetDelta saveColumn(final SpreadsheetColumn column,
                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(column, "column");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            final SpreadsheetStoreRepository repo = context.storeRepository();
            repo.columns()
                    .save(column);

            // load cells in column and save them again, this will re-evaluate as necessary.
            final SpreadsheetCellStore cells = repo.cells();
            for (final SpreadsheetCell cell : cells.column(column.reference())) {
                final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = changes.getOrCreateCellCache(
                        cell.reference(),
                        BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED // Maybe should be LOADING so cell appears in response
                );
                cache.loading(cell);
            }

            changes.commit();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    // LOAD ROW.........................................................................................................

    @Override
    public SpreadsheetDelta loadRow(final SpreadsheetRowReference row,
                                    final SpreadsheetEngineContext context) {
        return SpreadsheetDelta.EMPTY
                .setRows(
                        toSet(
                                context.storeRepository()
                                        .rows()
                                        .load(row)
                        )
                );
    }

    // SAVE ROW.........................................................................................................

    /**
     * Saves the {@link SpreadsheetRow} and then loads and saves all the cells in that row.
     */
    @Override
    public SpreadsheetDelta saveRow(final SpreadsheetRow row,
                                    final SpreadsheetEngineContext context) {
        Objects.requireNonNull(row, "row");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            final SpreadsheetStoreRepository repo = context.storeRepository();
            repo.rows()
                    .save(row);

            // load cells in row and save them again, this will re-evaluate as necessary.
            final SpreadsheetCellStore cells = repo.cells();
            for (final SpreadsheetCell cell : cells.row(row.reference())) {
                final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = changes.getOrCreateCellCache(
                        cell.reference(),
                        BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED // Maybe should be LOADING so cell appears in response
                );
                cache.loading(cell);
            }

            changes.commit();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    // DELETE / INSERT / COLUMN / ROW ..................................................................................

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        Objects.requireNonNull(column, "column");
        SpreadsheetEngine.checkCount(count);
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn.with(
                    column.value(),
                    count,
                    this,
                    context
            ).delete();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(row, "row");
        SpreadsheetEngine.checkCount(count);
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow.with(
                            row.value(),
                            count,
                            this,
                            context
                    )
                    .delete();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        Objects.requireNonNull(column, "column");
        SpreadsheetEngine.checkCount(count);
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn.with(
                            column.value(),
                            count,
                            this,
                            context
                    )
                    .insert();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(row, "row");
        SpreadsheetEngine.checkCount(count);
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow.with(
                            row.value(),
                            count,
                            this,
                            context
                    )
                    .insert();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    // FORM.............................................................................................................

    @Override
    public SpreadsheetDelta loadForm(final FormName name,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            Form<SpreadsheetExpressionReference> form = context.storeRepository()
                    .forms()
                    .load(name)
                    .orElse(null);

            SpreadsheetDelta delta = this.prepareResponse(
                    changes,
                    context
            );

            // form loaded, add to SpreadsheetDelta#forms
            if (null != form) {
                delta = delta.setForms(
                        Sets.of(form)
                );
            }

            return delta;
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta saveForm(final Form<SpreadsheetExpressionReference> form,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            final Form<SpreadsheetExpressionReference> savedForm = context.storeRepository()
                    .forms()
                    .save(form);

            for (final FormField<SpreadsheetExpressionReference> field : savedForm.fields()) {
                BasicSpreadsheetEngineLoadFormSpreadsheetSelectionVisitor.acceptFormField(
                        field,
                        changes
                );
            }

            final Map<SpreadsheetExpressionReference, Integer> cellOrLabelToCount = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

            for (final FormField<SpreadsheetExpressionReference> field : form.fields()) {
                final SpreadsheetExpressionReference cellOrLabel = field.reference();

                Integer count = cellOrLabelToCount.get(cellOrLabel);
                if(null == count) {
                    count = 0;
                }
                count++;

                cellOrLabelToCount.put(
                        cellOrLabel,
                        count
                );
            }

            final Set<SpreadsheetExpressionReference> duplicates = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

            for(final Map.Entry<SpreadsheetExpressionReference, Integer> cellOrLabelAndCount : cellOrLabelToCount.entrySet()) {
                final SpreadsheetExpressionReference cellOrLabel = cellOrLabelAndCount.getKey();
                int count = cellOrLabelAndCount.getValue();

                if(cellOrLabel.isLabelName()) {
                    final SpreadsheetLabelName labelName = cellOrLabel.toLabelName();
                    final SpreadsheetSelection labelNameTarget = context.resolveLabel(labelName)
                            .orElse(null);
                    if(null != labelNameTarget) {
                        final Integer cellCount = cellOrLabelToCount.get(labelNameTarget);
                        if(null != cellCount) {
                            count = count + cellCount;

                            if(count > 1) {
                                duplicates.add(
                                        labelNameTarget.toExpressionReference()
                                );
                            }
                        }
                    }
                }

                if(count > 1) {
                    duplicates.add(cellOrLabel);
                }
            }

            if(false == duplicates.isEmpty()) {
                throw new DuplicateFormFieldReferencesException(duplicates);
            }

            return this.prepareResponse(
                    changes,
                    context
            ).setForms(
                    Sets.of(savedForm)
            );
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta deleteForm(final FormName name,
                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            context.storeRepository()
                    .forms()
                    .delete(name);

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta loadForms(final int offset,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
        SpreadsheetEngine.checkOffsetAndCount(
                offset,
                count
        );
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            final List<Form<SpreadsheetExpressionReference>> forms = context.storeRepository()
                    .forms()
                    .values(
                            offset,
                            count
                    );

            return this.prepareResponse(
                    changes,
                    context
            ).setForms(
                    new HashSet<>(forms)
            );
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta prepareForm(final FormName name,
                                        final SpreadsheetExpressionReference selection,
                                        final SpreadsheetEngineContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            Form<SpreadsheetExpressionReference> form = context.storeRepository()
                    .forms()
                    .load(name)
                    .orElseThrow(() -> new IllegalArgumentException("Form not found"));

            final SpreadsheetCellRangeReference cellRange = context.resolveIfLabelOrFail(selection)
                    .toCellRange();

            // verify the selected range is a column or row.
            final int width = cellRange.width();
            final int height = cellRange.height();
            if (1 != width && 1 != height) {
                throw new IllegalArgumentException("Form cell range must be either a column or row");
            }

            SpreadsheetCellReference cell = cellRange.toCell();
            final int columnDelta = width == 1 ?
                    0 :
                    1;
            final int rowDelta = height == 1 ?
                    0 :
                    1;

            {
                // fix the reference for each field.
                final List<FormField<SpreadsheetExpressionReference>> fields = Lists.array();

                for (final FormField<SpreadsheetExpressionReference> field : form.fields()) {
                    fields.add(
                            field.setReference(cell)
                    );

                    cell = cell.add(
                            columnDelta,
                            rowDelta
                    );
                }

                form = form.setFields(fields);
            }

            // Create FormHandlerContext and prepare fields.
            final FormHandler<SpreadsheetExpressionReference, SpreadsheetDelta, SpreadsheetFormHandlerContext> handler = context.formHandler(
                    form.handler()
                            .orElseGet(
                                    () -> context.spreadsheetMetadata()
                                            .getOrFail(SpreadsheetMetadataPropertyName.DEFAULT_FORM_HANDLER)
                            ),
                    context // ProviderContext
            );

            final SpreadsheetFormHandlerContext formHandlerContext = SpreadsheetFormHandlerContexts.spreadsheetEngine(
                    form,
                    this,
                    context
            );

            form = handler.prepareForm(
                    form,
                    formHandlerContext
            );

            form = form.setErrors(
                    handler.validateForm(
                            form,
                            formHandlerContext
                    )
            );

            return this.prepareResponse(
                    changes,
                    context
            ).setForms(
                    Sets.of(form)
            );
        } finally {
            changes.close();
        }
    }

    @Override
    public SpreadsheetDelta submitForm(final Form<SpreadsheetExpressionReference> form,
                                       final SpreadsheetExpressionReference selection,
                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );
        try {
            Form<SpreadsheetExpressionReference> loaded = context.storeRepository()
                    .forms()
                    .load(form.name())
                    .orElseThrow(() -> new IllegalArgumentException("Form not found"));

            final SpreadsheetCellRangeReference cellRange = context.resolveIfLabelOrFail(selection)
                    .toCellRange();

            // verify the selected range is a column or row.
            final int width = cellRange.width();
            final int height = cellRange.height();
            if (1 != width && 1 != height) {
                throw new IllegalArgumentException("Form cell range must be either a column or row");
            }

            {
                SpreadsheetCellReference cell = cellRange.toCell();
                final int columnDelta = width == 1 ?
                        0 :
                        1;
                final int rowDelta = height == 1 ?
                        0 :
                        1;

                // build a map of submitted form fields.
                final Map<SpreadsheetExpressionReference, FormField<SpreadsheetExpressionReference>> referenceToFields = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

                for (final FormField<SpreadsheetExpressionReference> field : form.fields()) {
                    referenceToFields.put(
                            field.reference(),
                            field
                    );
                }

                // fix the reference for each field & copy the values from the submitted form.
                final List<FormField<SpreadsheetExpressionReference>> fields = Lists.array();

                for (FormField<SpreadsheetExpressionReference> field : loaded.fields()) {
                    field = field.setReference(cell);

                    final FormField<SpreadsheetExpressionReference> submittedField = referenceToFields.get(cell);
                    if (null != submittedField) {
                        field = field.setValue(
                                submittedField.value()
                        );
                    }

                    fields.add(field);

                    cell = cell.add(
                            columnDelta,
                            rowDelta
                    );
                }

                loaded = loaded.setFields(fields);
            }

            // Create FormHandlerContext and prepare fields.
            final FormHandler<SpreadsheetExpressionReference, SpreadsheetDelta, SpreadsheetFormHandlerContext> handler = context.formHandler(
                    form.handler()
                            .orElseGet(
                                    () -> context.spreadsheetMetadata()
                                            .getOrFail(SpreadsheetMetadataPropertyName.DEFAULT_FORM_HANDLER)
                            ),
                    context // ProviderContext
            );

            final SpreadsheetFormHandlerContext formHandlerContext = SpreadsheetFormHandlerContexts.spreadsheetEngine(
                    form,
                    this,
                    context
            );

            final List<ValidationError<SpreadsheetExpressionReference>> errors = handler.validateForm(
                    form,
                    formHandlerContext
            );
            loaded = loaded.setErrors(errors);

            return handler.submitForm(
                    loaded,
                    formHandlerContext
            );
        } finally {
            changes.close();
        }
    }

    // SAVE LABEL.......................................................................................................

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(mapping, "mapping");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            final SpreadsheetLabelMapping saved = context.storeRepository()
                    .labels()
                    .save(mapping);

            changes.onLabelSaved(saved);
            changes.commit();

            return this.prepareResponse(
                    changes,
                    context
            );
        } finally {
            changes.close();
        }
    }

    // DELETE LABEL.....................................................................................................

    @Override
    public SpreadsheetDelta deleteLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            context.storeRepository()
                    .labels()
                    .delete(label);

            changes.commit();

            return this.prepareResponse(changes, context);
        } finally {
            changes.close();
        }
    }

    // LOAD LABEL.......................................................................................................

    @Override
    public SpreadsheetDelta loadLabel(final SpreadsheetLabelName label,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            final SpreadsheetLabelMapping loaded = context.storeRepository()
                    .labels()
                    .load(label)
                    .orElse(null);

            if (null != loaded) {
                changes.getOrCreateLabelCache(
                        loaded.label(),
                        BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED
                );
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    SpreadsheetViewportWindows.EMPTY, // dont want to load any *extra* labels
                    context
            );
        } finally {
            changes.close();
        }
    }

    // LOAD LABELS......................................................................................................

    @Override
    public SpreadsheetDelta loadLabels(final int offset,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        SpreadsheetEngine.checkOffsetAndCount(
                offset,
                count
        );
        Objects.requireNonNull(context, "context");

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.changes(
                this,
                context
        );

        try {
            final List<SpreadsheetLabelMapping> mappings = context.storeRepository()
                    .labels()
                    .values(
                            offset,
                            count
                    );
            for (final SpreadsheetLabelMapping mapping : mappings) {
                changes.getOrCreateLabelCache(
                        mapping.label(),
                        BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED
                );
            }

            changes.commit();

            return this.prepareResponse(
                    changes,
                    SpreadsheetViewportWindows.EMPTY, // dont want to load any *extra* labels
                    context
            );
        } finally {
            changes.close();
        }
    }

    // FIND LABELS WITH REFERENCE.......................................................................................

    @Override
    public SpreadsheetDelta findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                    final int offset,
                                                    final int count,
                                                    final SpreadsheetEngineContext context) {
        Objects.requireNonNull(reference, "reference");
        SpreadsheetEngine.checkOffsetAndCount(
                offset,
                count
        );
        Objects.requireNonNull(context, "context");

        return SpreadsheetDelta.EMPTY.setLabels(
                context.storeRepository()
                        .labels()
                        .findLabelsWithReference(
                                reference,
                                offset,
                                count
                        )
        );
    }

    // cell eval........................................................................................................

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineEvaluation}
     */
    SpreadsheetCell parseFormulaEvaluateValidateFormatStyleAndSave(final SpreadsheetCell cell,
                                                                   final SpreadsheetEngineEvaluation evaluation,
                                                                   final SpreadsheetExpressionReferenceLoader loader,
                                                                   final SpreadsheetEngineContext context) {
        SpreadsheetCell styled = evaluation.parseFormulaEvaluateAndStyle(
                cell,
                this,
                loader,
                context
        );
        if(cell.equals(styled)) {
            styled = cell; // identity could have changed if evaluation=FORCE_RECOMPUTE returning an equal cell
        }

        context.storeRepository()
                .cells()
                .save(styled); // update cells enabling caching of parsing and value and errors.

        return styled;
    }

    // Visible for SpreadsheetEngineEvaluation only called by COMPUTE_IF_NECESSARY & FORCE_RECOMPUTE
    SpreadsheetCell parseFormulaEvaluateValidateFormatAndStyle(final SpreadsheetCell cell,
                                                               final SpreadsheetEngineEvaluation evaluation,
                                                               final SpreadsheetExpressionReferenceLoader loader,
                                                               final SpreadsheetEngineContext context) {
        final SpreadsheetCell afterParse = this.parseFormulaIfNecessary(
                cell,
                Function.identity(),
                context
        );

        return afterParse.formula().error().isPresent() ?
                afterParse :
                this.evaluateValidateFormatAndStyle(
                        afterParse,
                        evaluation,
                        loader,
                        context
                );
    }

    // PARSE FORMULA....................................................................................................

    /**
     * Parsers the formula for this cell, and sets its expression or error if parsing fails.
     */
    SpreadsheetCell parseFormulaIfNecessary(final SpreadsheetCell cell,
                                            final Function<SpreadsheetFormulaParserToken, SpreadsheetFormulaParserToken> parsed,
                                            final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;
        SpreadsheetFormula formula = cell.formula();

        try {
            final String formulaText = formula.text();
            if(false == formulaText.isEmpty()) {

                // if a token is NOT present parse the formula text
                SpreadsheetFormulaParserToken token = formula.token()
                        .orElse(null);
                if (null == token) {
                    final SpreadsheetMetadata metadata = context.spreadsheetMetadata();

                    formula = SpreadsheetFormula.parse(
                            TextCursors.charSequence(formulaText),
                            cell.parser()
                                    .map(p -> context.spreadsheetParser(
                                                    p,
                                                    context
                                            )
                                    ).orElseGet(
                                            () -> SpreadsheetFormulaParsers.valueOrExpression(
                                                    metadata.spreadsheetParser(
                                                            context, // SpreadsheetParserProvider
                                                            context // ProviderContext
                                                    ) // SpreadsheetEngineContext implements SpreadsheetParserProvider
                                            )
                                    ),
                            metadata.spreadsheetParserContext(
                                    Optional.of(cell),
                                    context
                            )
                    );

                    token = formula.token()
                            .orElse(null);
                }
                if (null != token) {
                    token = parsed.apply(token);
                    formula = formula.setToken(
                            Optional.of(token)
                    );
                }
                // if expression is absent, convert token into expression
                if (null != token && false == formula.expression().isPresent()) {
                    formula = formula.setExpression(
                            context.toExpression(token)
                    );
                }

                result = cell.setFormula(
                        formula
                );
            }

            //if error formatValueAndStyle
            if (formula.error().isPresent()) {
                result = context.formatValueAndStyle(
                        result,
                        Optional.empty()
                );
            }

        } catch (final Exception failed) {
            result = context.formatThrowableAndStyle(
                    failed,
                    cell.setFormula(formula)
            );
        }

        return result;
    }

    // EVAL ............................................................................................................

    /**
     * This is only called if the formula was parsed an {@link Expression} exists ready for evaluation.
     */
    private SpreadsheetCell evaluateValidateFormatAndStyle(final SpreadsheetCell cell,
                                                           final SpreadsheetEngineEvaluation evaluation,
                                                           final SpreadsheetExpressionReferenceLoader loader,
                                                           final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;

        try {
            // special case formula.text is empty but value is present
            {
                SpreadsheetFormula formula = cell.formula();
                if (false == formula.text().isEmpty()) {

                    // ask enum to dispatch
                    final Optional<Expression> maybeExpression = formula.expression();
                    if (maybeExpression.isPresent()) {
                        result = cell.setFormula(
                                formula.setValue(
                                        evaluation.evaluate(
                                                this,
                                                cell,
                                                loader,
                                                context
                                        )
                                ).setValueIfError(context)
                        );
                    }
                }
            }

            result = this.validate(
                    result,
                    loader,
                    context
            );

            result = context.formatValueAndStyle(
                    result,
                    cell.formatter()
                            .map(f -> context.spreadsheetFormatter(
                                            f,
                                            context // ProviderContext
                                    )
                            )
            );
        } catch (final Exception cause) {
            result = context.formatThrowableAndStyle(
                    cause,
                    cell
            );
        }

        return result;
    }

    /**
     * If the current cell has a validator and has no error, validate the value.
     */
    private SpreadsheetCell validate(final SpreadsheetCell cell,
                                     final SpreadsheetExpressionReferenceLoader loader,
                                     final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;

        final ValidatorSelector validatorSelector = cell.validator()
                .orElse(null);
        if (null != validatorSelector) {
            final SpreadsheetFormula formula = cell.formula();
            if (false == formula.error().isPresent()) {
                result = validate0(
                        cell,
                        validatorSelector,
                        loader,
                        context.spreadsheetEngineContext(SpreadsheetMetadataPropertyName.VALIDATION_FUNCTIONS)
                );
            }
        }

        return result;
    }

    private static SpreadsheetCell validate0(final SpreadsheetCell cell,
                                             final ValidatorSelector validatorSelector,
                                             final SpreadsheetExpressionReferenceLoader loader,
                                             final SpreadsheetEngineContext context) {
        final Validator<SpreadsheetExpressionReference, SpreadsheetValidatorContext> validator = context.validator(
                validatorSelector,
                context // providerContext
        );

        final SpreadsheetFormula formula = cell.formula();
        final Optional<Object> value = formula.errorOrValue();

        final SpreadsheetMetadata metadata = context.spreadsheetMetadata();

        final BiFunction<Object, SpreadsheetExpressionReference, SpreadsheetExpressionEvaluationContext> referenceToExpressionEvaluationContext =
                (final Object v,
                 final SpreadsheetExpressionReference cellOrLabel) -> context.spreadsheetEngineContext(SpreadsheetMetadataPropertyName.VALIDATION_FUNCTIONS)
                        .spreadsheetExpressionEvaluationContext(
                                Optional.of(cell),
                                loader
                        ).addLocalVariable(
                                SpreadsheetValidatorContext.VALUE,
                                value
                        );

        return cell.setFormula(
                formula.setError(
                        SpreadsheetError.validationErrors(
                                validator.validate(
                                        value.orElse(null), // unwrap Optionals
                                        metadata.spreadsheetValidatorContext(
                                                cell.reference(), // reference
                                                (final ValidatorSelector v) -> context.validator(v, context),
                                                referenceToExpressionEvaluationContext,
                                                context, // SpreadsheetLabelNameResolver
                                                context, // ConverterProvider
                                                context // ProviderContext
                                        )
                                )
                        )
                )
        );
    }

    // SpreadsheetEngineEvaluation #evaluateXXX.........................................................................

    /**
     * If a formatted value is present and the {@link Expression#isPure(ExpressionPurityContext)} then return
     * the current {@link SpreadsheetFormula#value()} otherwise evaluate the expression again.
     */
    // SpreadsheetEngineEvaluation#COMPUTE_IF_NECESSARY
    Optional<Object> evaluateIfNecessary(final SpreadsheetCell cell,
                                         final SpreadsheetExpressionReferenceLoader loader,
                                         final SpreadsheetEngineContext context) {
        return cell.formattedValue().isPresent() && expressionRequired(cell).isPure(context) ?
                cell.formula()
                        .value() :
                this.evaluate(
                        cell,
                        loader,
                        context
                );
    }

    /**
     * Unconditionally evaluate the {@link Expression} returning the value.
     */
    // SpreadsheetEngineEvaluation#FORCE_RECOMPUTE
    Optional<Object> evaluate(final SpreadsheetCell cell,
                              final SpreadsheetExpressionReferenceLoader loader,
                              final SpreadsheetEngineContext context) {

        return Optional.ofNullable(
                expressionRequired(cell)
                        .toValue(
                                context.spreadsheetExpressionEvaluationContext(
                                        Optional.of(cell),
                                        loader
                                )
                        )
        );
    }

    private static Expression expressionRequired(final SpreadsheetCell cell) {
        return cell.formula()
                .expression()
                .orElseThrow(() -> new IllegalStateException(
                                "Formula of " +
                                        CharSequences.quoteAndEscape(cell.reference().toString()) +
                                        " missing expected expression"
                        )
                );
    }

    // prepareResponse..................................................................................................

    /**
     * Creates a {@link SpreadsheetDelta} to hold the given cells and then queries to fetch the labels for those cells.
     */
    private SpreadsheetDelta prepareResponse(final BasicSpreadsheetEngineChanges changes,
                                             final SpreadsheetEngineContext context) {
        changes.commit();

        return this.prepareResponse(
                changes,
                changes.changesCellRange()
                        .map(
                                r -> SpreadsheetViewportWindows.with(
                                        Sets.of(r)
                                )
                        ).orElse(SpreadsheetViewportWindows.EMPTY),
                context
        );
    }

    /**
     * Creates a {@link SpreadsheetDelta} to hold the given cells and then queries to fetch the labels for the
     * given {@link SpreadsheetViewportWindows}. Labels must be loaded for the entire {@link SpreadsheetViewportWindows},
     * because {@link SpreadsheetLabelMapping} may exist for missing/empty cells which are not present in either
     * {@link SpreadsheetDelta#cells} or {@link SpreadsheetDelta#deletedColumns}.
     */
    private SpreadsheetDelta prepareResponse(final BasicSpreadsheetEngineChanges changes,
                                             final SpreadsheetViewportWindows window,
                                             final SpreadsheetEngineContext context) {
        return BasicSpreadsheetEnginePrepareResponse.prepare(
                this,
                changes,
                window,
                context
        );
    }

    // columnWidth......................................................................................................

    @Override
    public double columnWidth(final SpreadsheetColumnReference columnReference,
                              final SpreadsheetEngineContext context) {
        Objects.requireNonNull(columnReference, "column");
        Objects.requireNonNull(context, "context");

        double columnWidth = 0;

        final SpreadsheetStoreRepository repo = context.storeRepository();
        final Optional<SpreadsheetColumn> column = repo.columns()
                .load(columnReference);

        if (!column.isPresent() || !column.get().hidden()) {
            columnWidth = context.storeRepository()
                    .cells()
                    .maxColumnWidth(columnReference);
            if (0 == columnWidth) {
                columnWidth = columnWidthOrRowHeight(
                        TextStylePropertyName.WIDTH,
                        context
                );
            }
        }

        return columnWidth;
    }

    @Override
    public double rowHeight(final SpreadsheetRowReference rowReference,
                            final SpreadsheetEngineContext context) {
        Objects.requireNonNull(rowReference, "row");
        Objects.requireNonNull(context, "context");

        double rowHeight = 0;

        final SpreadsheetStoreRepository repo = context.storeRepository();
        final Optional<SpreadsheetRow> row = repo.rows()
                .load(rowReference);

        if (!row.isPresent() || !row.get().hidden()) {
            rowHeight = context.storeRepository()
                    .cells()
                    .maxRowHeight(rowReference);
            if (0 == rowHeight) {
                rowHeight = columnWidthOrRowHeight(
                        TextStylePropertyName.HEIGHT,
                        context
                );
            }
        }

        return rowHeight;
    }

    /**
     * Gets the double value for the given {@link TextStylePropertyName} which is either WIDTH or HEIGHT>
     */
    private double columnWidthOrRowHeight(final TextStylePropertyName<Length<?>> propertyName,
                                          final SpreadsheetEngineContext context) {
        return context.spreadsheetMetadata()
                .getEffectiveStylePropertyOrFail(propertyName)
                .pixelValue();
    }

    @Override
    public int columnCount(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        return context.storeRepository()
                .cells()
                .columnCount();
    }

    @Override
    public int rowCount(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        return context.storeRepository()
                .cells()
                .rowCount();
    }

    // WINDOW...........................................................................................................

    @Override
    public SpreadsheetViewportWindows window(final SpreadsheetViewportRectangle viewportRectangle,
                                             final boolean includeFrozenColumnsRows,
                                             final Optional<SpreadsheetSelection> selection,
                                             final SpreadsheetEngineContext context) {
        Objects.requireNonNull(viewportRectangle, "viewportRectangle");
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(context, "context");

        return this.windowNonLabelSelection(
                viewportRectangle,
                includeFrozenColumnsRows,
                selection.map(context::resolveIfLabelOrFail),
                context
        );
    }

    private SpreadsheetViewportWindows windowNonLabelSelection(final SpreadsheetViewportRectangle viewportRectangle,
                                                               final boolean includeFrozenColumnsRows,
                                                               final Optional<SpreadsheetSelection> selection,
                                                               final SpreadsheetEngineContext context) {
        double width = viewportRectangle.width();
        double height = viewportRectangle.height();

        SpreadsheetColumnRangeReference frozenColumns = null;
        SpreadsheetRowRangeReference frozenRows = null;

        if (includeFrozenColumnsRows) {
            final SpreadsheetMetadata metadata = context.spreadsheetMetadata();

            // compute actual frozenColumns, metadata.FROZEN_COLUMNS might be higher than requested width...............

            final Optional<SpreadsheetColumnRangeReference> maybeFrozenColumns = metadata.get(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS);
            if (maybeFrozenColumns.isPresent()) {
                final SpreadsheetColumnReference lastFrozenColumn = maybeFrozenColumns.get()
                        .end();

                SpreadsheetColumnReference leftColumn = SpreadsheetReferenceKind.RELATIVE.firstColumn();
                SpreadsheetColumnReference rightColumn = leftColumn;

                do {
                    final double columnWidth = this.columnWidth(rightColumn, context);
                    width = width - columnWidth;
                    if (width <= 0) {
                        break;
                    }
                    if (rightColumn.equalsIgnoreReferenceKind(lastFrozenColumn)) {
                        break;
                    }
                    rightColumn = rightColumn.addSaturated(1);
                } while (!rightColumn.isLast());

                frozenColumns = leftColumn.columnRange(rightColumn);
            }

            // compute actual frozenRows, metadata.FROZEN_ROWS might be higher than requested height....................
            final Optional<SpreadsheetRowRangeReference> maybeFrozenRows = metadata.get(SpreadsheetMetadataPropertyName.FROZEN_ROWS);

            if (maybeFrozenRows.isPresent()) {

                final SpreadsheetRowReference lastFrozenRow = maybeFrozenRows.get()
                        .end();

                SpreadsheetRowReference topRow = SpreadsheetReferenceKind.RELATIVE.firstRow();
                SpreadsheetRowReference bottomRow = topRow;

                do {
                    final double rowHeight = this.rowHeight(bottomRow, context);
                    height = height - rowHeight;
                    if (height <= 0) {
                        break;
                    }
                    if (bottomRow.equalsIgnoreReferenceKind(lastFrozenRow)) {
                        break;
                    }
                    bottomRow = bottomRow.addSaturated(1);
                } while (!bottomRow.isLast());

                frozenRows = topRow.rowRange(bottomRow);
            }
        }

        // non frozen viewport
        SpreadsheetCellReference nonFrozenHome = context.resolveIfLabelOrFail(
                viewportRectangle.home()
        ).toCell();
        if (null != frozenColumns) {
            final SpreadsheetColumnReference right = frozenColumns.end();
            if (right.compareTo(nonFrozenHome.column()) >= 0) {
                nonFrozenHome = nonFrozenHome.setColumn(right.addSaturated(+1));
            }
        }
        if (null != frozenRows) {
            final SpreadsheetRowReference right = frozenRows.end();
            if (right.compareTo(nonFrozenHome.row()) >= 0) {
                nonFrozenHome = nonFrozenHome.setRow(right.addSaturated(+1));
            }
        }

        final SpreadsheetColumnRangeReference nonFrozenColumns = width > 0 ?
                this.columnRange(
                        nonFrozenHome.column(),
                        0,
                        width,
                        selection,
                        context
                ) : null;

        final SpreadsheetRowRangeReference nonFrozenRows = height > 0 ?
                this.rowRange(
                        nonFrozenHome.row(),
                        0,
                        height,
                        selection,
                        context
                ) : null;

        final Set<SpreadsheetCellRangeReference> window = Sets.ordered();

        SpreadsheetCellRangeReference nonFrozenCells = null;
        if (null != nonFrozenColumns && null != nonFrozenRows) {
            nonFrozenCells = nonFrozenColumns.setRowRange(nonFrozenRows);
        }

        // compute other ranges parse frozenColumns/frozenRows .........................................................

        boolean skipPan = false;

        if (null != frozenColumns && null != frozenRows) {
            // FCR fr fr fr
            // fc  n  n  n
            // fc  n  n  n
            final SpreadsheetCellRangeReference frozenColumnsRowsCells = frozenColumns.setRowRange(frozenRows);

            window.add(frozenColumnsRowsCells);

            skipPan = null != frozenColumnsRowsCells &&
                    selection.map(s -> s.testCellRange(frozenColumnsRowsCells)).orElse(false);
        }

        if (null != frozenRows && null != nonFrozenColumns) {
            // fcr FR FR FR
            // fc  n  n  n
            // fc  n  n  n
            final SpreadsheetCellRangeReference frozenRowsCells = frozenRows.setColumnRange(nonFrozenColumns);

            window.add(frozenRowsCells);

            skipPan = skipPan ||
                    selection.map(s -> s.testCellRange(frozenRowsCells)).orElse(false);
        }

        if (null != frozenColumns && null != nonFrozenRows) {
            // fcr fr fr fr
            // FC  n  n  n
            // FC  n  n  n
            final SpreadsheetCellRangeReference frozenColumnCells = frozenColumns.setRowRange(nonFrozenRows);
            window.add(frozenColumnCells);

            skipPan = skipPan ||
                    selection.map(s -> s.testCellRange(frozenColumnCells)).orElse(false);
        }

        if (null != nonFrozenCells) {
            // selection is not in any of the frozen ranges therefore nonFrozenCells may need panning.
            if (selection.isPresent() && !skipPan) {
                final SpreadsheetSelection spreadsheetSelection = selection.get();

                if (!spreadsheetSelection.testCellRange(nonFrozenCells)) {
                    nonFrozenCells = BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor.pan(
                            nonFrozenCells,
                            nonFrozenHome.viewportRectangle(
                                    width,
                                    height
                            ),
                            spreadsheetSelection,
                            this,
                            context
                    );
                }
            }
            window.add(nonFrozenCells);
        }

        return SpreadsheetViewportWindows.with(window);
    }

    /**
     * Uses the given home cell of the viewport and a X offset and width to compute the start and end columns.
     * Note if the selection matches the left or right columns incompletely then that will advance left/right.
     */
    SpreadsheetColumnRangeReference columnRange(final SpreadsheetColumnReference column,
                                                final double xOffset,
                                                final double width,
                                                final Optional<SpreadsheetSelection> selection,
                                                final SpreadsheetEngineContext context) {
        // columns
        double x = xOffset;
        SpreadsheetColumnReference leftColumn = column;

        // consume xOffset
        if (0 != xOffset) {
            if (xOffset < 0) {
                for (; ; ) {
                    if (leftColumn.isFirst()) {
                        x = 0;
                        break;
                    }
                    leftColumn = leftColumn.addSaturated(-1);
                    x = x + this.columnWidth(leftColumn, context);
                    if (x >= 0) {
                        break;
                    }
                }

                x = x + 0;
            } else {
                for (; ; ) {
                    final double columnWidth = this.columnWidth(leftColumn, context);
                    if (x - columnWidth < 0) {
                        break;
                    }
                    x = x - columnWidth;
                    leftColumn = leftColumn.addSaturated(+1);
                }
            }
        }

        x = x + width;
        SpreadsheetColumnReference rightColumn = leftColumn;

        for (; ; ) {
            if (rightColumn.isLast()) {
                x = width + (xOffset < 0 ? +xOffset : 0);
                leftColumn = rightColumn;

                for (; ; ) {
                    x = x - this.columnWidth(leftColumn, context);
                    if (0 == x) {
                        break;
                    }
                    if (x < 0) {
                        if (!selection.isPresent() || !selection.get().testColumn(leftColumn)) {
                            break;
                        }

                        x = x + this.columnWidth(rightColumn, context);
                        rightColumn = rightColumn.addSaturated(-1);
                    }

                    leftColumn = leftColumn.addSaturated(-1);
                }

                if (xOffset < 0) {
                    if (!leftColumn.isFirst()) {
                        x = xOffset;
                        for (; ; ) {
                            leftColumn = leftColumn.addSaturated(-1);
                            x = x + this.columnWidth(leftColumn, context);
                            if (x >= 0) {
                                break;
                            }
                        }
                    }
                }
                break;
            }

            x = x - this.columnWidth(rightColumn, context);
            if (0 == x) {
                break;
            }
            if (x < 0) {
                if (!selection.isPresent() || !selection.get().testColumn(rightColumn)) {
                    break;
                }

                x = x + this.columnWidth(leftColumn, context);
                leftColumn = leftColumn.addSaturated(+1);
            }
            rightColumn = rightColumn.addSaturated(+1);
        }

        return leftColumn.columnRange(rightColumn);
    }

    /**
     * Uses the given home cell of the viewport and a Y offset and height to compute the start and end rows.
     * Note if the selection matches the top or bottom rows incompletely then that will advance up/down.
     */
    SpreadsheetRowRangeReference rowRange(final SpreadsheetRowReference row,
                                          final double yOffset,
                                          final double height,
                                          final Optional<SpreadsheetSelection> selection,
                                          final SpreadsheetEngineContext context) {
        // rows
        double y = yOffset;
        SpreadsheetRowReference topRow = row;

        // consume yOffset
        if (0 != yOffset) {
            if (yOffset < 0) {
                for (; ; ) {
                    if (topRow.isFirst()) {
                        y = 0;
                        break;
                    }
                    topRow = topRow.addSaturated(-1);
                    y = y + this.rowHeight(topRow, context);
                    if (y >= 0) {
                        break;
                    }
                }

                y = y + 0;
            } else {
                for (; ; ) {
                    final double rowHeight = this.rowHeight(topRow, context);
                    if (y - rowHeight < 0) {
                        break;
                    }
                    y = y - rowHeight;
                    topRow = topRow.addSaturated(+1);
                }
            }
        }

        y = y + height;
        SpreadsheetRowReference bottomRow = topRow;

        for (; ; ) {
            if (bottomRow.isLast()) {
                y = height + (yOffset < 0 ? +yOffset : 0);
                topRow = bottomRow;

                for (; ; ) {
                    y = y - this.rowHeight(topRow, context);
                    if (0 == y) {
                        break;
                    }
                    if (y < 0) {
                        if (!selection.isPresent() || !selection.get().testRow(topRow)) {
                            break;
                        }

                        y = y + this.rowHeight(bottomRow, context);
                        bottomRow = bottomRow.addSaturated(-1);
                    }

                    topRow = topRow.addSaturated(-1);
                }

                if (yOffset < 0) {
                    if (!topRow.isFirst()) {
                        y = yOffset;
                        for (; ; ) {
                            topRow = topRow.addSaturated(-1);
                            y = y + this.rowHeight(topRow, context);
                            if (y >= 0) {
                                break;
                            }
                        }
                    }
                }
                break;
            }
            y = y - this.rowHeight(bottomRow, context);
            if (0 == y) {
                break;
            }
            if (y < 0) {
                if (!selection.isPresent() || !selection.get().testRow(bottomRow)) {
                    break;
                }

                y = y + this.rowHeight(topRow, context);
                topRow = topRow.addSaturated(+1);
            }
            bottomRow = bottomRow.addSaturated(+1);
        }

        return topRow.rowRange(bottomRow);
    }

    double sumColumnWidths(final SpreadsheetColumnReference start,
                           final SpreadsheetColumnReference end,
                           final SpreadsheetEngineContext context) {
        double sum = 0;
        SpreadsheetColumnReference column = start;

        do {
            sum += this.columnWidth(column, context);
            column = column.addSaturated(1);
        } while (!column.isLast() && column.compareTo(end) <= 0);

        return sum;
    }

    double sumRowHeights(final SpreadsheetRowReference start,
                         final SpreadsheetRowReference end,
                         final SpreadsheetEngineContext context) {
        double sum = 0;
        SpreadsheetRowReference row = start;

        do {
            sum += this.rowHeight(row, context);
            row = row.addSaturated(1);
        } while (!row.isLast() && row.compareTo(end) <= 0);

        return sum;
    }

    // navigate.........................................................................................................

    @Override
    public Optional<SpreadsheetViewport> navigate(final SpreadsheetViewport viewport,
                                                  final SpreadsheetEngineContext context) {
        Objects.requireNonNull(viewport, "viewport");
        Objects.requireNonNull(context, "context");

        Optional<SpreadsheetViewport> result;

        SpreadsheetViewport notLabelViewport = viewport;

        final Optional<AnchoredSpreadsheetSelection> maybeAnchored = viewport.anchoredSelection();
        if (maybeAnchored.isPresent()) {
            final AnchoredSpreadsheetSelection anchoredBefore = maybeAnchored.get();
            final SpreadsheetSelection selection = anchoredBefore.selection();

            if (selection.isLabelName()) {
                final SpreadsheetSelection selectionNotLabel = context.resolveLabelOrFail(selection.toLabelName());
                notLabelViewport = notLabelViewport.setAnchoredSelection(
                        Optional.of(
                                selectionNotLabel.setAnchor(
                                        anchoredBefore.anchor()
                                )
                        )
                );

                result = this.navigateNonLabelSelection(
                        notLabelViewport,
                        context
                );

                if (result.isPresent()) {
                    SpreadsheetViewport viewportResult = result.get();
                    final Optional<AnchoredSpreadsheetSelection> resultMaybeAnchored = viewportResult.anchoredSelection();
                    if (resultMaybeAnchored.isPresent()) {
                        final AnchoredSpreadsheetSelection resultAnchored = resultMaybeAnchored.get();
                        final SpreadsheetSelection resultSelection = resultAnchored.selection();
                        if (resultSelection.equalsIgnoreReferenceKind(selectionNotLabel)) {
                            result = Optional.of(
                                    // restore the original label
                                    viewportResult.setAnchoredSelection(maybeAnchored)
                            );
                        }
                    }
                }
            } else {
                result = this.navigateNonLabelSelection(
                        notLabelViewport,
                        context
                );
            }
        } else {
            result = this.navigateNonLabelSelection(
                    notLabelViewport,
                    context
            );
        }

        return result;
    }

    private Optional<SpreadsheetViewport> navigateNonLabelSelection(final SpreadsheetViewport viewport,
                                                                    final SpreadsheetEngineContext context) {
        final SpreadsheetStoreRepository repository = context.storeRepository();

        return this.navigateNonLabelSelection0(
                viewport,
                SpreadsheetViewportNavigationContexts.basic(
                        repository.columns()::isHidden,
                        (c) -> this.columnWidth(c, context),
                        repository.rows()::isHidden,
                        (r) -> this.rowHeight(r, context),
                        (r, i, s) -> this.window(r, i, s, context)
                )
        );
    }

    private Optional<SpreadsheetViewport> navigateNonLabelSelection0(final SpreadsheetViewport viewport,
                                                                     final SpreadsheetViewportNavigationContext context) {
        Optional<SpreadsheetViewport> result;

        final List<SpreadsheetViewportNavigation> navigations = viewport.navigations()
                .compact();
        if (navigations.isEmpty()) {
            result = this.navigateWithoutNavigation(
                    viewport,
                    context
            );
        } else {
            SpreadsheetViewport navigating = viewport;

            for (final SpreadsheetViewportNavigation navigation : navigations) {
                navigating = navigation.update(
                        navigating,
                        context
                );
            }

            result = Optional.of(
                    navigating.setNavigations(SpreadsheetViewport.NO_NAVIGATION)
            );
        }

        return result;
    }

    /**
     * Tests if the home is hidden, returning {@link SpreadsheetViewport#NO_NAVIGATION} then also tests the selection
     * and if that is hidden clears it.
     */
    private Optional<SpreadsheetViewport> navigateWithoutNavigation(final SpreadsheetViewport viewport,
                                                                    final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = null;

        final SpreadsheetCellReference home = viewport.rectangle()
                .home();
        if (context.isColumnHidden(home.column()) || context.isRowHidden(home.row())) {
            // home is hidden clear viewport
            result = null;
        } else {
            final Optional<AnchoredSpreadsheetSelection> maybeAnchored = viewport.anchoredSelection();
            if (maybeAnchored.isPresent()) {
                final AnchoredSpreadsheetSelection anchored = maybeAnchored.get();
                final SpreadsheetSelection selection = anchored.selection();
                if (selection.isHidden(context::isColumnHidden, context::isRowHidden)) {
                    // selection is hidden clear it.
                    result = viewport.setAnchoredSelection(SpreadsheetViewport.NO_ANCHORED_SELECTION);
                } else {
                    result = viewport;
                }
            }
        }
        return Optional.ofNullable(result);
    }

    // j2cl helpers....................................................................................................

    // The J2CL Optional does not support map.
    static <V extends SpreadsheetColumnOrRow<R>, R extends SpreadsheetSelection & Comparable<R>> Set<V> toSet(final Optional<V> columnOrRow) {
        return columnOrRow.isPresent() ?
                Sets.of(columnOrRow.get()) :
                Sets.empty();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
