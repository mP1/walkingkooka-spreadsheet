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

package walkingkooka.spreadsheet.store.repo;

import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.storage.StorageStore;
import walkingkooka.store.Store;

import java.util.Objects;

/**
 * A {@link SpreadsheetStoreRepository} that exposes the {@link Store stores} given to it.
 */
final class BasicSpreadsheetStoreRepository implements SpreadsheetStoreRepository {

    static BasicSpreadsheetStoreRepository with(final SpreadsheetCellStore cells,
                                                final SpreadsheetCellReferencesStore cellReferences,
                                                final SpreadsheetColumnStore columns,
                                                final SpreadsheetGroupStore groups,
                                                final SpreadsheetLabelStore labels,
                                                final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences,
                                                final SpreadsheetMetadataStore metadatas,
                                                final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells,
                                                final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                                final SpreadsheetRowStore rows,
                                                final StorageStore storage,
                                                final SpreadsheetUserStore users) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(cellReferences, "cellReferences");
        Objects.requireNonNull(columns, "columns");
        Objects.requireNonNull(groups, "groups");
        Objects.requireNonNull(labels, "labels");
        Objects.requireNonNull(labelReferences, "labelReferences");
        Objects.requireNonNull(metadatas, "metadatas");
        Objects.requireNonNull(rangeToCells, "rangeToCells");
        Objects.requireNonNull(rangeToConditionalFormattingRules, "rangeToConditionalFormattingRules");
        Objects.requireNonNull(rows, "rows");
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(users, "users");

        return new BasicSpreadsheetStoreRepository(
                cells,
                cellReferences,
                columns,
                groups,
                labels,
                labelReferences,
                metadatas,
                rangeToCells,
                rangeToConditionalFormattingRules,
                rows,
                storage,
                users
        );
    }

    private BasicSpreadsheetStoreRepository(final SpreadsheetCellStore cells,
                                            final SpreadsheetCellReferencesStore cellReferences,
                                            final SpreadsheetColumnStore columns,
                                            final SpreadsheetGroupStore groups,
                                            final SpreadsheetLabelStore labels,
                                            final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences,
                                            final SpreadsheetMetadataStore metadatas,
                                            final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells,
                                            final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                            final SpreadsheetRowStore rows,
                                            final StorageStore storage,
                                            final SpreadsheetUserStore users) {
        this.cells = cells;
        this.cellReferences = cellReferences;
        this.columns = columns;
        this.groups = groups;
        this.labels = labels;
        this.labelReferences = labelReferences;
        this.metadatas = metadatas;
        this.rangeToCells = rangeToCells;
        this.rangeToConditionalFormattingRules = rangeToConditionalFormattingRules;
        this.rows = rows;
        this.storage = storage;
        this.users = users;
    }

    @Override
    public SpreadsheetCellStore cells() {
        return this.cells;
    }

    private final SpreadsheetCellStore cells;

    @Override
    public SpreadsheetCellReferencesStore cellReferences() {
        return this.cellReferences;
    }

    private final SpreadsheetCellReferencesStore cellReferences;

    @Override
    public SpreadsheetColumnStore columns() {
        return this.columns;
    }

    private final SpreadsheetColumnStore columns;

    @Override
    public SpreadsheetGroupStore groups() {
        return this.groups;
    }

    private final SpreadsheetGroupStore groups;

    @Override
    public SpreadsheetLabelStore labels() {
        return this.labels;
    }

    private final SpreadsheetLabelStore labels;

    @Override
    public SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences() {
        return this.labelReferences;
    }

    private final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences;

    @Override
    public SpreadsheetMetadataStore metadatas() {
        return this.metadatas;
    }

    private final SpreadsheetMetadataStore metadatas;

    @Override
    public SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells() {
        return this.rangeToCells;
    }

    private final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells;

    @Override
    public SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        return this.rangeToConditionalFormattingRules;
    }

    private final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules;

    @Override
    public SpreadsheetRowStore rows() {
        return this.rows;
    }

    private final SpreadsheetRowStore rows;

    @Override
    public StorageStore storage() {
        return this.storage;
    }

    private final StorageStore storage;

    @Override
    public SpreadsheetUserStore users() {
        return this.users;
    }

    private final SpreadsheetUserStore users;

    @Override
    public String toString() {
        return this.cells + " " +
                this.cellReferences + " " +
                this.columns + " " +
                this.groups + " " +
                this.labels + " " +
                this.labelReferences + " " +
                this.metadatas + " " +
                this.rangeToCells + " " +
                this.rangeToConditionalFormattingRules + " " +
                this.rows + " " +
                this.storage + " " +
                this.users;
    }
}
