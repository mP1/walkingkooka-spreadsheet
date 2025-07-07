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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStoreTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStores;
import walkingkooka.storage.StorageStores;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepositoryTest implements SpreadsheetStoreRepositoryTesting<SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository>,
    SpreadsheetMetadataTesting {

    private final static SpreadsheetId ID = SpreadsheetId.with(0x1234);
    private final static SpreadsheetStoreRepository REPOSITORY = new FakeSpreadsheetStoreRepository() {
        @Override
        public SpreadsheetMetadataStore metadatas() {
            return createTreeMap();
        }
    };

    private final static SpreadsheetMetadata METADATA = METADATA_EN_AU.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID, ID
    );

    @Test
    public void testWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                null,
                REPOSITORY,
                SPREADSHEET_PARSER_PROVIDER,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                ID,
                null,
                SPREADSHEET_PARSER_PROVIDER,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                ID,
                REPOSITORY,
                null,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                ID,
                REPOSITORY,
                SPREADSHEET_PARSER_PROVIDER,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                ID,
                REPOSITORY,
                SPREADSHEET_PARSER_PROVIDER,
                LOCALE_CONTEXT,
                null
            )
        );
    }

    @Test
    public void testCellStoreBeforeSavingMetadata() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        assertNotSame(repository.repository.cells(), repository.cells(), "cells");

        cellStore.save(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY.setText("=4+5+6")
                )
        );
    }

    @Test
    public void testCellStoreSaveMetadataCellStore() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        final SpreadsheetCellStore cellStore = repository.cells();
        assertSame(cellStore, repository.cells());

        repository.metadatas()
            .save(
                METADATA.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                    SpreadsheetName.with("different")
                )
            );

        assertNotSame(cellStore, repository.cells(), "SpreadsheetCellStore should have been recreated because of metadata save");
        assertSame(repository.cells(), repository.cells(), "SpreadsheetCellStore should have been cached, and not recreated");
    }

    @Test
    public void testCellStoreSaveDifferentMetadataCellStore() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        final SpreadsheetCellStore cellStore = repository.cells();
        assertSame(cellStore, repository.cells());

        repository.metadatas()
            .save(METADATA.set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(99999)));

        assertSame(cellStore, repository.cells(), "SpreadsheetCellStore should have been cached, and not recreated");
    }

    @Test
    public void testSaveMetadataCellStoreSaveMetadataCellStore() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        repository.metadatas()
            .save(METADATA);

        final SpreadsheetCellStore cellStore = repository.cells();

        // addSaveWatcher not fired if same metadata saved twice in a row
        repository.metadatas()
            .save(METADATA.set(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME, SpreadsheetName.with("Different")));

        assertNotSame(cellStore, repository.cells(), "SpreadsheetCellStore should have been recreated because of metadata save");
        assertSame(repository.cells(), repository.cells(), "SpreadsheetCellStore should have been cached, and not recreated");
    }

    @Test
    public void testSaveMetadataThenLoadCell() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        repository.metadatas()
            .save(METADATA);

        final SpreadsheetCell cell = SpreadsheetSelection.A1
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("1.5")
            );

        repository.cells().save(cell);

        repository.metadatas()
            .save(
                METADATA.set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS.setDecimalSeparator('*'))
            );

        final SpreadsheetCell reloaded = repository.cells().loadOrFail(cell.reference());
        this.checkEquals(
            "1*5",
            reloaded.formula()
                .text()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                ID,
                REPOSITORY,
                SPREADSHEET_PARSER_PROVIDER,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            ),
            ID + " " + REPOSITORY + " " + SPREADSHEET_PARSER_PROVIDER + " " + LOCALE_CONTEXT + " " + PROVIDER_CONTEXT
        );
    }

    @Override
    public SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository createStoreRepository() {
        final SpreadsheetMetadataStore metadatas = createTreeMap();
        metadatas.save(METADATA);

        return SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
            ID,
            SpreadsheetStoreRepositories.basic(
                SpreadsheetCellStores.treeMap(),
                SpreadsheetCellReferencesStores.treeMap(),
                SpreadsheetColumnStores.treeMap(),
                SpreadsheetFormStores.treeMap(),
                SpreadsheetGroupStores.treeMap(),
                SpreadsheetLabelStores.treeMap(),
                SpreadsheetLabelReferencesStores.treeMap(),
                metadatas,
                SpreadsheetCellRangeStores.treeMap(),
                SpreadsheetCellRangeStores.treeMap(),
                SpreadsheetRowStores.treeMap(),
                StorageStores.tree(STORAGE_STORE_CONTEXT),
                SpreadsheetUserStores.treeMap()
            ),
            SPREADSHEET_PARSER_PROVIDER,
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    private static SpreadsheetMetadataStore createTreeMap() {
        return SpreadsheetMetadataStores.treeMap(
            SpreadsheetMetadataStoreTesting.CREATE_TEMPLATE,
            LocalDateTime::now
        );
    }

    @Override
    public String typeNamePrefix() {
        return "SpreadsheetMetadataAwareSpreadsheetCellStore";
    }

    @Override
    public Class<SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository> type() {
        return SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.class;
    }
}
