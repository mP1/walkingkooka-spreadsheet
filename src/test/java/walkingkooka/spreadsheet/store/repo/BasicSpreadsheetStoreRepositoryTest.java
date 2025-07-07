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
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
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
import walkingkooka.storage.StorageStore;
import walkingkooka.storage.StorageStores;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetStoreRepositoryTest implements SpreadsheetStoreRepositoryTesting<BasicSpreadsheetStoreRepository> {

    @Test
    public void testWithNullCellsFails() {
        this.withFails(
            null,
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullCellReferencesFails() {
        this.withFails(
            this.cells(),
            null,
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users());
    }

    @Test
    public void testWithNullColumnsFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            null,
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users());
    }

    @Test
    public void testWithNullFormsFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            null,
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users());
    }

    @Test
    public void testWithNullGroupsFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            null,
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users());
    }

    @Test
    public void testWithNullLabelsFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            null,
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullLabelReferencesFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            null,
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullMetadatasFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            null,
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullRangeToCellsFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            null,
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullRangeToConditionalFormattingRulesFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            null,
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullRowsFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            null,
            this.storage(),
            this.users()
        );
    }

    @Test
    public void testWithNullStorageFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            null,
            this.users()
        );
    }

    @Test
    public void testWithNullUserFails() {
        this.withFails(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            null
        );
    }

    private void withFails(final SpreadsheetCellStore cells,
                           final SpreadsheetCellReferencesStore cellReferences,
                           final SpreadsheetColumnStore columns,
                           final SpreadsheetFormStore forms,
                           final SpreadsheetGroupStore groups,
                           final SpreadsheetLabelStore labels,
                           final SpreadsheetLabelReferencesStore labelReferences,
                           final SpreadsheetMetadataStore metadatas,
                           final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells,
                           final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                           final SpreadsheetRowStore rows,
                           final StorageStore storage,
                           final SpreadsheetUserStore users) {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetStoreRepository.with(
                cells,
                cellReferences,
                columns,
                forms,
                groups,
                labels,
                labelReferences,
                metadatas,
                rangeToCells,
                rangeToConditionalFormattingRules,
                rows,
                storage,
                users
            )
        );
    }

    @Test
    public void testToString() {
        final SpreadsheetCellStore cells = this.cells();
        final SpreadsheetCellReferencesStore cellReferences = this.cellReferences();
        final SpreadsheetColumnStore columns = this.columns();
        final SpreadsheetFormStore forms = this.forms();
        final SpreadsheetGroupStore groups = this.groups();
        final SpreadsheetLabelStore labels = this.labels();
        final SpreadsheetLabelReferencesStore labelReferences = this.labelReferences();
        final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells = this.rangeToCells();
        final SpreadsheetMetadataStore metadatas = this.metadatas();
        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules = this.rangeToConditionalFormattingRules();
        final SpreadsheetRowStore rows = this.rows();
        final StorageStore storage = this.storage();
        final SpreadsheetUserStore users = this.users();

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
                rangeToConditionalFormattingRules,
                rows,
                storage,
                users
            ),
            cells + " " + cellReferences + " " + columns + " " + forms + " " + groups + " " + labels + " " + labelReferences + " " + metadatas + " " + rangeToCells + " " + rangeToConditionalFormattingRules + " " + rows + " " + storage + " " + users);
    }

    @Override
    public BasicSpreadsheetStoreRepository createStoreRepository() {
        return BasicSpreadsheetStoreRepository.with(
            this.cells(),
            this.cellReferences(),
            this.columns(),
            this.forms(),
            this.groups(),
            this.labels(),
            this.labelReferences(),
            this.metadatas(),
            this.rangeToCells(),
            this.rangeToConditionalFormattingRules(),
            this.rows(),
            this.storage(),
            this.users()
        );
    }

    private SpreadsheetCellStore cells() {
        return SpreadsheetCellStores.fake();
    }

    private SpreadsheetCellReferencesStore cellReferences() {
        return SpreadsheetCellReferencesStores.fake();
    }

    private SpreadsheetColumnStore columns() {
        return SpreadsheetColumnStores.fake();
    }

    private SpreadsheetFormStore forms() {
        return SpreadsheetFormStores.fake();
    }

    private SpreadsheetGroupStore groups() {
        return SpreadsheetGroupStores.fake();
    }

    private SpreadsheetLabelStore labels() {
        return SpreadsheetLabelStores.fake();
    }

    private SpreadsheetLabelReferencesStore labelReferences() {
        return SpreadsheetLabelReferencesStores.fake();
    }

    private SpreadsheetMetadataStore metadatas() {
        return SpreadsheetMetadataStores.fake();
    }

    private SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells() {
        return SpreadsheetCellRangeStores.fake();
    }

    private SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        return SpreadsheetCellRangeStores.fake();
    }

    private SpreadsheetRowStore rows() {
        return SpreadsheetRowStores.fake();
    }

    private StorageStore storage() {
        return StorageStores.fake();
    }

    private SpreadsheetUserStore users() {
        return SpreadsheetUserStores.fake();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetStoreRepository> type() {
        return BasicSpreadsheetStoreRepository.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return "Basic";
    }
}
