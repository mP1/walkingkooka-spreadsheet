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
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.format.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.format.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepositoryTest implements SpreadsheetStoreRepositoryTesting<SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository> {

    private final static SpreadsheetId ID = SpreadsheetId.with(0x1234);
    private final static SpreadsheetStoreRepository REPOSITORY = new FakeSpreadsheetStoreRepository() {
        @Override
        public SpreadsheetMetadataStore metadatas() {
            return SpreadsheetMetadataStores.treeMap();
        }
    };

    private final static SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern();

    private final static Supplier<LocalDateTime> NOW = LocalDateTime::now;

    @Test
    public void testWithNullIdFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                        null,
                        REPOSITORY,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
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
                        NOW
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
                        NOW
                )
        );
    }

    @Test
    public void testWithNullNowFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                        ID,
                        REPOSITORY,
                        SPREADSHEET_PARSER_PROVIDER,
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

        final SpreadsheetMetadata metadata = this.metadata();
        repository.metadatas()
                .save(metadata);

        assertNotSame(cellStore, repository.cells(), "SpreadsheetCellStore should have been recreated because of metadata save");
        assertSame(repository.cells(), repository.cells(), "SpreadsheetCellStore should have been cached, and not recreated");
    }

    @Test
    public void testCellStoreSaveDifferentMetadataCellStore() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        final SpreadsheetCellStore cellStore = repository.cells();
        assertSame(cellStore, repository.cells());

        final SpreadsheetMetadata metadata = this.metadata();
        repository.metadatas()
                .save(metadata.set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(99999)));

        assertSame(cellStore, repository.cells(), "SpreadsheetCellStore should have been cached, and not recreated");
    }

    @Test
    public void testSaveMetadataCellStoreSaveMetadataCellStore() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        final SpreadsheetMetadata metadata = this.metadata();
        repository.metadatas()
                .save(metadata);

        final SpreadsheetCellStore cellStore = repository.cells();

        // addSaveWatcher not fired if same metadata saved twice in a row
        repository.metadatas()
                .save(metadata.set(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME, SpreadsheetName.with("Different")));

        assertNotSame(cellStore, repository.cells(), "SpreadsheetCellStore should have been recreated because of metadata save");
        assertSame(repository.cells(), repository.cells(), "SpreadsheetCellStore should have been cached, and not recreated");
    }

    @Test
    public void testSaveMetadataThenLoadCell() {
        final SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository repository = this.createStoreRepository();

        final SpreadsheetMetadata metadata = this.metadata();

        repository.metadatas()
                .save(metadata);

        final SpreadsheetCell cell = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("1.5")
                );

        repository.cells().save(cell);

        repository.metadatas()
                .save(
                        metadata.set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '$')
                );

        final SpreadsheetCell reloaded = repository.cells().loadOrFail(cell.reference());
        final SpreadsheetFormula formula = reloaded.formula();
        this.checkEquals("1$5", reloaded.formula().text());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                        ID,
                        REPOSITORY,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                ),
                ID + " " + REPOSITORY + " " + SPREADSHEET_PARSER_PROVIDER
        );
    }

    @Override
    public SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository createStoreRepository() {
        final SpreadsheetMetadataStore metadatas = SpreadsheetMetadataStores.treeMap();
        metadatas.save(this.metadata());

        return SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
                ID,
                SpreadsheetStoreRepositories.basic(
                        SpreadsheetCellStores.treeMap(),
                        SpreadsheetExpressionReferenceStores.treeMap(),
                        SpreadsheetColumnStores.treeMap(),
                        SpreadsheetGroupStores.treeMap(),
                        SpreadsheetLabelStores.treeMap(),
                        SpreadsheetExpressionReferenceStores.treeMap(),
                        metadatas,
                        SpreadsheetCellRangeStores.treeMap(),
                        SpreadsheetCellRangeStores.treeMap(),
                        SpreadsheetRowStores.treeMap(),
                        SpreadsheetUserStores.treeMap()
                ),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, ID)
                .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
                .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"))
                .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now())
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.');
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
