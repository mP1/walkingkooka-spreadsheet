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
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.store.Store;

import java.util.Objects;

/**
 * A {@link SpreadsheetStoreRepository} that exposes the {@link Store stores} given to it.
 */
final class BasicSpreadsheetStoreRepository implements SpreadsheetStoreRepository {

    static BasicSpreadsheetStoreRepository with(final SpreadsheetCellStore cells,
                                                final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences,
                                                final SpreadsheetGroupStore groups,
                                                final SpreadsheetLabelStore labels,
                                                final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences,
                                                final SpreadsheetMetadataStore metadatas,
                                                final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells,
                                                final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                                final SpreadsheetUserStore users) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(cellReferences, "cellReferences");
        Objects.requireNonNull(groups, "groups");
        Objects.requireNonNull(labels, "labels");
        Objects.requireNonNull(labelReferences, "labelReferences");
        Objects.requireNonNull(metadatas, "metadatas");
        Objects.requireNonNull(rangeToCells, "rangeToCells");
        Objects.requireNonNull(rangeToConditionalFormattingRules, "rangeToConditionalFormattingRules");
        Objects.requireNonNull(users, "users");

        return new BasicSpreadsheetStoreRepository(cells,
                cellReferences,
                groups,
                labels,
                labelReferences,
                metadatas,
                rangeToCells,
                rangeToConditionalFormattingRules,
                users);
    }

    private BasicSpreadsheetStoreRepository(final SpreadsheetCellStore cells,
                                            final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences,
                                            final SpreadsheetGroupStore groups,
                                            final SpreadsheetLabelStore labels,
                                            final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences,
                                            final SpreadsheetMetadataStore metadatas,
                                            final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells,
                                            final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                            final SpreadsheetUserStore users) {
        this.cells = cells;
        this.cellReferences = cellReferences;
        this.groups = groups;
        this.labels = labels;
        this.labelReferences = labelReferences;
        this.metadatas = metadatas;
        this.rangeToCells = rangeToCells;
        this.rangeToConditionalFormattingRules = rangeToConditionalFormattingRules;
        this.users = users;
    }

    @Override
    public SpreadsheetCellStore cells() {
        return this.cells;
    }

    private final SpreadsheetCellStore cells;

    @Override
    public SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences() {
        return this.cellReferences;
    }

    private final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences;

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
    public SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells() {
        return this.rangeToCells;
    }

    private final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells;

    @Override
    public SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        return this.rangeToConditionalFormattingRules;
    }

    private final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules;

    @Override
    public SpreadsheetUserStore users() {
        return this.users;
    }

    private final SpreadsheetUserStore users;

    @Override
    public String toString() {
        return this.cells + " " +
                this.cellReferences + " " +
                this.groups + " " +
                this.labels + " " +
                this.labelReferences + " " +
                this.metadatas + " " +
                this.rangeToCells + " " +
                this.rangeToConditionalFormattingRules + " " +
                this.users;
    }
}
