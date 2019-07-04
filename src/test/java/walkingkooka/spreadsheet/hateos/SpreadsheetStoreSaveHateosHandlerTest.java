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

package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetStoreSaveHateosHandlerTest extends SpreadsheetStoreHateosHandlerTestCase2<SpreadsheetStoreSaveHateosHandler<SpreadsheetCellReference, SpreadsheetCell>,
        SpreadsheetCellReference, SpreadsheetCell> {

    @Test
    public void testSave() {
        final SpreadsheetCellReference id = this.id();
        final SpreadsheetCell before = SpreadsheetCell.with(id, SpreadsheetFormula.with("1+2"));
        final SpreadsheetCell after = SpreadsheetCell.with(id, SpreadsheetFormula.with("3+4"));

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetCellStore() {
                    @Override
                    public SpreadsheetCell save(final SpreadsheetCell c) {
                        assertSame(before, c, "saving");
                        return after;
                    }
                }),
                id,
                Optional.of(before),
                HateosHandler.NO_PARAMETERS,
                Optional.of(after));
    }

    @Test
    public void testSaveWithoutResourceFails() {
        final SpreadsheetCellReference id = this.id();
        this.handleFails(id,
                Optional.empty(),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Override
    SpreadsheetStoreSaveHateosHandler<SpreadsheetCellReference, SpreadsheetCell> createHandler(SpreadsheetStore<SpreadsheetCellReference, SpreadsheetCell> store) {
        return SpreadsheetStoreSaveHateosHandler.with(store);
    }

    @Override
    SpreadsheetStore<SpreadsheetCellReference, SpreadsheetCell> store() {
        return SpreadsheetCellStores.fake();
    }

    @Override
    public final SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parseCellReference("A1");
    }

    @Override
    public Optional<SpreadsheetCell> resource() {
        return Optional.of(SpreadsheetCell.with(this.id(), SpreadsheetFormula.with("99")));
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return this.id().range(this.id());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetStoreSaveHateosHandler<SpreadsheetCellReference, SpreadsheetCell>> type() {
        return Cast.to(SpreadsheetStoreSaveHateosHandler.class);
    }
}
