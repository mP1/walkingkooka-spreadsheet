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
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStore;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStores;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetStoreRepositoryTest implements SpreadsheetStoreRepositoryTesting<BasicSpreadsheetStoreRepository> {

    private final static SpreadsheetCellStore CELLS = SpreadsheetCellStores.fake();

    private final static SpreadsheetCellReferencesStore CELL_REFERENCES = SpreadsheetCellReferencesStores.fake();

    private final static SpreadsheetColumnStore COLUMNS = SpreadsheetColumnStores.fake();

    private final static SpreadsheetFormStore FORMS = SpreadsheetFormStores.fake();

    private final static SpreadsheetGroupStore GROUPS = SpreadsheetGroupStores.fake();

    private final static SpreadsheetLabelStore LABELS = SpreadsheetLabelStores.fake();

    private final static SpreadsheetLabelReferencesStore LABEL_REFERENCES = SpreadsheetLabelReferencesStores.fake();

    private final static SpreadsheetMetadataStore METADATAS = SpreadsheetMetadataStores.fake();

    private final static SpreadsheetCellRangeStore<SpreadsheetCellReference> RANGE_TO_CELLS = SpreadsheetCellRangeStores.fake();

    private final static SpreadsheetRowStore ROWS = SpreadsheetRowStores.fake();

    private final static Storage<StorageExpressionEvaluationContext> STORAGES = Storages.fake();

    private final static SpreadsheetUserStore USERS = SpreadsheetUserStores.fake();

    @Test
    public void testWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                null,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullCellReferencesFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                null,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullColumnsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                null,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullFormsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                null,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullGroupsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                null,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                null,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullLabelReferencesFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                null,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullMetadatasFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                null,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullRangeToCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                null,
                ROWS,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullRowsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                null,
                STORAGES,
                USERS
            )
        );
    }

    @Test
    public void testWithNullStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                null,
                USERS
            )
        );
    }

    @Test
    public void testWithNullUserFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                CELLS,
                CELL_REFERENCES,
                COLUMNS,
                FORMS,
                GROUPS,
                LABELS,
                LABEL_REFERENCES,
                METADATAS,
                RANGE_TO_CELLS,
                ROWS,
                STORAGES,
                null
            )
        );
    }

    @Override
    public BasicSpreadsheetStoreRepository createStoreRepository() {
        return BasicSpreadsheetStoreRepository.with(
            CELLS,
            CELL_REFERENCES,
            COLUMNS,
            FORMS,
            GROUPS,
            LABELS,
            LABEL_REFERENCES,
            METADATAS,
            RANGE_TO_CELLS,
            ROWS,
            STORAGES,
            USERS
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetCellStore cells = CELLS;
        final SpreadsheetCellReferencesStore cellReferences = CELL_REFERENCES;
        final SpreadsheetColumnStore columns = COLUMNS;
        final SpreadsheetFormStore forms = FORMS;
        final SpreadsheetGroupStore groups = GROUPS;
        final SpreadsheetLabelStore labels = LABELS;
        final SpreadsheetLabelReferencesStore labelReferences = LABEL_REFERENCES;
        final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells = RANGE_TO_CELLS;
        final SpreadsheetMetadataStore metadatas = METADATAS;
        final SpreadsheetRowStore rows = ROWS;
        final Storage<StorageExpressionEvaluationContext> storage = STORAGES;
        final SpreadsheetUserStore users = USERS;

        this.toStringAndCheck(
            BasicSpreadsheetStoreRepository.with(
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
            ),
            cells + " " + cellReferences + " " + columns + " " + forms + " " + groups + " " + labels + " " + labelReferences + " " + metadatas + " " + rangeToCells + " " + rows + " " + storage + " " + users);
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetStoreRepository> type() {
        return BasicSpreadsheetStoreRepository.class;
    }

    @Override
    public String typeNamePrefix() {
        return "Basic";
    }
}
