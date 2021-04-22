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

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;

import java.util.Objects;

/**
 * A {@link SpreadsheetStoreRepository} that rewraps the {@link SpreadsheetCellStore} each time a {@link SpreadsheetMetadata}
 * is saved.
 */
final class SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository implements SpreadsheetStoreRepository {

    static SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository with(final SpreadsheetId id,
                                                                                       final SpreadsheetStoreRepository repository) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(repository, "repository");

        return new SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository(id, repository);
    }

    private SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository(final SpreadsheetId id,
                                                                                   final SpreadsheetStoreRepository repository) {
        this.id = id;
        this.repository = repository;

        repository.metadatas().addSaveWatcher(this::onSaveMetadata);
    }

    /**
     * Whenever the {@link SpreadsheetMetadata} is saved call {@link SpreadsheetCellStores#spreadsheetFormulaSpreadsheetMetadataAware}
     * again.
     */
    private void onSaveMetadata(final SpreadsheetMetadata metadata) {
        final SpreadsheetId id = metadata.id()
                .orElseThrow(() -> new IllegalArgumentException("Metadata missing id"));
        final SpreadsheetId expected = this.id;
        if (!expected.equals(id)) {
            throw new IllegalArgumentException("Saved metadata has different id got " + id + " expected: " + expected);
        }

        this.cells = SpreadsheetCellStores.spreadsheetFormulaSpreadsheetMetadataAware(
                this.repository.cells(),
                metadata
        );
    }

    @Override
    public SpreadsheetCellStore cells() {
        if (null == this.cells) {
            this.cells = SpreadsheetCellStores.spreadsheetFormulaSpreadsheetMetadataAware(
                    this.repository.cells(),
                    this.repository.metadatas().loadOrFail(this.id)
            );
        }
        return cells;
    }

    /**
     * This will be updated each time a new {@link SpreadsheetMetadata} is saved.
     */
    private SpreadsheetCellStore cells; // TODO AtomicReference

    @Override
    public SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences() {
        return this.repository.cellReferences();
    }

    @Override
    public SpreadsheetGroupStore groups() {
        return this.repository.groups();
    }

    @Override
    public SpreadsheetLabelStore labels() {
        return this.repository.labels();
    }

    @Override
    public SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences() {
        return this.repository.labelReferences();
    }

    @Override
    public SpreadsheetMetadataStore metadatas() {
        return this.repository.metadatas();
    }

    @Override
    public SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells() {
        return this.repository.rangeToCells();
    }

    @Override
    public SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        return this.repository.rangeToConditionalFormattingRules();
    }

    @Override
    public SpreadsheetUserStore users() {
        return this.repository.users();
    }

    private final SpreadsheetId id;

    // @VisibleForTesting
    final SpreadsheetStoreRepository repository;

    @Override
    public String toString() {
        return this.id + " " + this.repository;
    }
}
