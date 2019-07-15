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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetStoreRepositoryTest implements SpreadsheetStoreRepositoryTesting<BasicSpreadsheetStoreRepository> {

    @Test
    public void testWithNullCellsFails() {
        this.withFails(null,
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullCellReferencesFails() {
        this.withFails(this.cells(),
                null,
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullGroupsFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                null,
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullLabelsFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                null,
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullLabelReferencesFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                null,
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullMetadatasFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                null,
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullRangeToCellsFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                null,
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    @Test
    public void testWithNullRangeToConditionalFormattingRulesFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                null,
                this.users());
    }

    @Test
    public void testWithNullUserFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                null);
    }

    private void withFails(final SpreadsheetCellStore cells,
                           final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences,
                           final SpreadsheetGroupStore groups,
                           final SpreadsheetLabelStore labels,
                           final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences,
                           final SpreadsheetMetadataStore metadatas,
                           final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells,
                           final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                           final SpreadsheetUserStore users) {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetStoreRepository.with(cells,
                    cellReferences,
                    groups,
                    labels,
                    labelReferences,
                    metadatas,
                    rangeToCells,
                    rangeToConditionalFormattingRules,
                    users);
        });
    }

    @Test
    public void testToString() {
        final SpreadsheetCellStore cells = this.cells();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences = this.cellReferences();
        final SpreadsheetGroupStore groups = this.groups();
        final SpreadsheetLabelStore labels = this.labels();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences = this.labelReferences();
        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells = this.rangeToCells();
        final SpreadsheetMetadataStore metadatas = this.metadatas();
        final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules = this.rangeToConditionalFormattingRules();
        final SpreadsheetUserStore users = this.users();

        this.toStringAndCheck(BasicSpreadsheetStoreRepository.with(cells,
                cellReferences,
                groups,
                labels,
                labelReferences,
                metadatas,
                rangeToCells,
                rangeToConditionalFormattingRules,
                users),
                cells + " " + cellReferences + " " + groups + " " + labels + " " + labelReferences + " " + metadatas + " " + rangeToCells + " " + rangeToConditionalFormattingRules + " " + users);
    }

    @Override
    public BasicSpreadsheetStoreRepository createStoreRepository() {
        return BasicSpreadsheetStoreRepository.with(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.metadatas(),
                this.rangeToCells(),
                this.rangeToConditionalFormattingRules(),
                this.users());
    }

    private SpreadsheetCellStore cells() {
        return SpreadsheetCellStores.fake();
    }

    private SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences() {
        return SpreadsheetReferenceStores.fake();
    }

    private SpreadsheetGroupStore groups() {
        return SpreadsheetGroupStores.fake();
    }

    private SpreadsheetLabelStore labels() {
        return SpreadsheetLabelStores.fake();
    }

    private SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences() {
        return SpreadsheetReferenceStores.fake();
    }

    private SpreadsheetMetadataStore metadatas() {
        return SpreadsheetMetadataStores.fake();
    }

    private SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells() {
        return SpreadsheetRangeStores.fake();
    }

    private SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        return SpreadsheetRangeStores.fake();
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
