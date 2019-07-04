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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetStoreDeleteHateosHandlerTest extends SpreadsheetStoreHateosHandlerTestCase2<SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference, SpreadsheetCell>,
        SpreadsheetCellReference, SpreadsheetCell> {

    @Test
    public void testDelete() {
        this.deleted = false;

        final SpreadsheetCellReference id = this.id();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetCellStore() {
                    @Override
                    public void delete(final SpreadsheetCellReference i) {
                        assertSame(id, id, "id");
                        SpreadsheetStoreDeleteHateosHandlerTest.this.deleted = true;
                    }
                }),
                id,
                Optional.empty(),
                HateosHandler.NO_PARAMETERS,
                Optional.empty());

        assertEquals(true, this.deleted);
    }

    private boolean deleted;

    @Test
    public void testDeleteWithResourceFails() {
        final SpreadsheetCellReference id = this.id();
        this.handleFails(id,
                Optional.of(SpreadsheetCell.with(id, SpreadsheetFormula.with("1+2"))),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Override
    SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference, SpreadsheetCell> createHandler(SpreadsheetStore<SpreadsheetCellReference, SpreadsheetCell> store) {
        return SpreadsheetStoreDeleteHateosHandler.with(store);
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
        return Optional.empty();
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return this.id().range(this.id());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference, SpreadsheetCell>> type() {
        return Cast.to(SpreadsheetStoreDeleteHateosHandler.class);
    }
}
