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

import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStore;
import walkingkooka.storage.Storage;
import walkingkooka.store.Store;

import java.util.Objects;

/**
 * A {@link SpreadsheetStoreRepository} that exposes the {@link Store stores} given to it.
 */
final class BasicSpreadsheetStoreRepository implements SpreadsheetStoreRepository {

    static BasicSpreadsheetStoreRepository with(final SpreadsheetCellStore cells,
                                                final SpreadsheetCellReferencesStore cellReferences,
                                                final SpreadsheetColumnStore columns,
                                                final SpreadsheetFormStore forms,
                                                final SpreadsheetGroupStore groups,
                                                final SpreadsheetLabelStore labels,
                                                final SpreadsheetLabelReferencesStore labelReferences,
                                                final SpreadsheetMetadataStore metadatas,
                                                final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells,
                                                final SpreadsheetRowStore rows,
                                                final Storage storage,
                                                final SpreadsheetUserStore users) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(cellReferences, "cellReferences");
        Objects.requireNonNull(columns, "columns");
        Objects.requireNonNull(forms, "forms");
        Objects.requireNonNull(groups, "groups");
        Objects.requireNonNull(labels, "labels");
        Objects.requireNonNull(labelReferences, "labelReferences");
        Objects.requireNonNull(metadatas, "metadatas");
        Objects.requireNonNull(rangeToCells, "rangeToCells");
        Objects.requireNonNull(rows, "rows");
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(users, "users");

        return new BasicSpreadsheetStoreRepository(
            cells,
            cellReferences,
            columns,
            forms,
            groups,
            labels,
            labelReferences,
            metadatas,
            rangeToCells,
            rows,
            storage,
            users
        );
    }

    private BasicSpreadsheetStoreRepository(final SpreadsheetCellStore cells,
                                            final SpreadsheetCellReferencesStore cellReferences,
                                            final SpreadsheetColumnStore columns,
                                            final SpreadsheetFormStore forms,
                                            final SpreadsheetGroupStore groups,
                                            final SpreadsheetLabelStore labels,
                                            final SpreadsheetLabelReferencesStore labelReferences,
                                            final SpreadsheetMetadataStore metadatas,
                                            final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells,
                                            final SpreadsheetRowStore rows,
                                            final Storage storage,
                                            final SpreadsheetUserStore users) {
        this.cells = cells;
        this.cellReferences = cellReferences;
        this.columns = columns;
        this.forms = forms;
        this.groups = groups;
        this.labels = labels;
        this.labelReferences = labelReferences;
        this.metadatas = metadatas;
        this.rangeToCells = rangeToCells;
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
    public SpreadsheetFormStore forms() {
        return this.forms;
    }

    private final SpreadsheetFormStore forms;

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
    public SpreadsheetLabelReferencesStore labelReferences() {
        return this.labelReferences;
    }

    private final SpreadsheetLabelReferencesStore labelReferences;

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
    public SpreadsheetRowStore rows() {
        return this.rows;
    }

    private final SpreadsheetRowStore rows;

    @Override
    public Storage storage() {
        return this.storage;
    }

    private final Storage storage;

    @Override
    public SpreadsheetUserStore users() {
        return this.users;
    }

    private final SpreadsheetUserStore users;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.cells,
            this.cellReferences,
            this.columns,
            this.forms,
            this.groups,
            this.labels,
            this.labelReferences,
            this.metadatas,
            this.rangeToCells,
            this.rows,
            this.storage,
            this.users
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicSpreadsheetStoreRepository &&
                this.equals0((BasicSpreadsheetStoreRepository) other));
    }

    private boolean equals0(final BasicSpreadsheetStoreRepository other) {
        return this.cells.equals(other.cells) &&
            this.cellReferences.equals(other.cellReferences) &&
            this.columns.equals(other.columns) &&
            this.forms.equals(other.forms) &&
            this.groups.equals(other.groups) &&
            this.labels.equals(other.labels) &&
            this.labelReferences.equals(other.labelReferences) &&
            this.metadatas.equals(other.metadatas) &&
            this.rangeToCells.equals(other.rangeToCells) &&
            this.rows.equals(other.rows) &&
            this.storage.equals(other.storage) &&
            this.users.equals(other.users);
    }

    @Override
    public String toString() {
        return this.cells + " " +
            this.cellReferences + " " +
            this.columns + " " +
            this.forms + " " +
            this.groups + " " +
            this.labels + " " +
            this.labelReferences + " " +
            this.metadatas + " " +
            this.rangeToCells + " " +
            this.rows + " " +
            this.storage + " " +
            this.users;
    }
}
