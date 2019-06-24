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
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStores;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStoreRepositoryTest implements StoreRepositoryTesting<BasicStoreRepository> {

    @Test
    public void testWithNullCellsFails() {
        this.withFails(null,
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.ranges(),
                this.users());
    }

    @Test
    public void testWithNullCellReferencesFails() {
        this.withFails(this.cells(),
                null,
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.ranges(),
                this.users());
    }

    @Test
    public void testWithNullGroupsFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                null,
                this.labels(),
                this.labelReferences(),
                this.ranges(),
                this.users());
    }

    @Test
    public void testWithNullLabelsFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                null,
                this.labelReferences(),
                this.ranges(),
                this.users());
    }

    @Test
    public void testWithNullLabelReferencesFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                null,
                this.ranges(),
                this.users());
    }

    @Test
    public void testWithNullRangesFails() {
        this.withFails(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
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
                this.ranges(),
                null);
    }

    private void withFails(final SpreadsheetCellStore cells,
                           final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences,
                           final SpreadsheetGroupStore groups,
                           final SpreadsheetLabelStore labels,
                           final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences,
                           final SpreadsheetRangeStore ranges,
                           final SpreadsheetUserStore users) {
        assertThrows(NullPointerException.class, () -> {
            BasicStoreRepository.with(cells, cellReferences, groups, labels, labelReferences, ranges, users);
        });
    }

    @Test
    public void testToString() {
        final SpreadsheetCellStore cells = this.cells();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences = this.cellReferences();
        final SpreadsheetGroupStore groups = this.groups();
        final SpreadsheetLabelStore labels = this.labels();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences = this.labelReferences();
        final SpreadsheetRangeStore ranges = this.ranges();
        final SpreadsheetUserStore users = this.users();

        this.toStringAndCheck(BasicStoreRepository.with(cells,
                cellReferences,
                groups,
                labels,
                labelReferences,
                ranges,
                users),
                cells + " " + cellReferences + " " + groups + " " + labels + " " + labelReferences + " " + ranges + " " + users);
    }

    @Override
    public BasicStoreRepository createStoreRepository() {
        return BasicStoreRepository.with(this.cells(),
                this.cellReferences(),
                this.groups(),
                this.labels(),
                this.labelReferences(),
                this.ranges(),
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

    private SpreadsheetRangeStore ranges() {
        return SpreadsheetRangeStores.fake();
    }

    private SpreadsheetUserStore users() {
        return SpreadsheetUserStores.fake();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicStoreRepository> type() {
        return BasicStoreRepository.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return "Basic";
    }
}
