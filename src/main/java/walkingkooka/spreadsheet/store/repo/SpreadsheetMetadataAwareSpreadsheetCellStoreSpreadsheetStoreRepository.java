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

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;

import java.util.Objects;

/**
 * A {@link SpreadsheetStoreRepository} that rewraps the {@link SpreadsheetCellStore} each time a {@link SpreadsheetMetadata}
 * with the same id is saved.
 */
final class SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository implements SpreadsheetStoreRepository {

    static SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository with(final SpreadsheetId id,
                                                                                       final SpreadsheetStoreRepository repository,
                                                                                       final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                                       final ProviderContext providerContext) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(repository, "repository");
        Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository(
                id,
                repository,
                spreadsheetParserProvider,
                providerContext
        );
    }

    private SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository(final SpreadsheetId id,
                                                                                   final SpreadsheetStoreRepository repository,
                                                                                   final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                                   final ProviderContext providerContext) {
        this.id = id;
        this.repository = repository;
        this.spreadsheetParserProvider = spreadsheetParserProvider;
        this.providerContext = providerContext;

        repository.metadatas().addSaveWatcher(this::onSaveMetadata);
    }

    /**
     * Whenever the {@link SpreadsheetMetadata} with the same id is saved call {@link SpreadsheetCellStores#spreadsheetFormulaSpreadsheetMetadataAware}
     * again.
     */
    private void onSaveMetadata(final SpreadsheetMetadata metadata) {
        final SpreadsheetId id = metadata.id()
                .orElseThrow(() -> new IllegalArgumentException("Metadata missing id"));
        final SpreadsheetId expected = this.id;
        if (expected.equals(id)) {
            this.cells = SpreadsheetCellStores.spreadsheetFormulaSpreadsheetMetadataAware(
                    this.repository.cells(),
                    metadata,
                    this.spreadsheetParserProvider,
                    this.providerContext
            );
        }
    }

    @Override
    public SpreadsheetCellStore cells() {
        if (null == this.cells) {
            this.cells = SpreadsheetCellStores.spreadsheetFormulaSpreadsheetMetadataAware(
                    this.repository.cells(),
                    this.repository.metadatas().loadOrFail(this.id),
                    this.spreadsheetParserProvider,
                    this.providerContext
            );
        }
        return cells;
    }

    private final SpreadsheetParserProvider spreadsheetParserProvider;

    private final ProviderContext providerContext;

    /**
     * This will be updated each time a new {@link SpreadsheetMetadata} is saved.
     */
    private SpreadsheetCellStore cells; // TODO AtomicReference

    @Override
    public SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences() {
        return this.repository.cellReferences();
    }

    @Override
    public SpreadsheetColumnStore columns() {
        return this.repository.columns();
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
    public SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells() {
        return this.repository.rangeToCells();
    }

    @Override
    public SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        return this.repository.rangeToConditionalFormattingRules();
    }

    @Override
    public SpreadsheetRowStore rows() {
        return this.repository.rows();
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
        return this.id + " " + this.repository + " " + this.spreadsheetParserProvider + " " + this.providerContext;
    }
}
