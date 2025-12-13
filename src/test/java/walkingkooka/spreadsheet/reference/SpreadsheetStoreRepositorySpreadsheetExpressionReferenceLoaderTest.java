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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoaderTest implements SpreadsheetExpressionReferenceLoaderTesting<SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader> {

    private final static SpreadsheetCellReference CELL = SpreadsheetSelection.A1;

    @Test
    public void testWithNullSpreadsheetStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader.with(null)
        );
    }

    @Test
    public void testLoadCell() {
        final SpreadsheetStoreRepository repo = new FakeSpreadsheetStoreRepository() {

            @Override
            public SpreadsheetCellStore cells() {
                return this.store;
            }

            private final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        };

        final SpreadsheetCell cell = CELL.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(123)
            )
        );

        repo.cells()
            .save(cell);

        this.loadCellAndCheck(
            SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader.with(repo),
            CELL,
            this.createContext(),
            cell
        );
    }

    @Test
    public void testLoadCellRange() {
        final SpreadsheetStoreRepository repo = new FakeSpreadsheetStoreRepository() {

            @Override
            public SpreadsheetCellStore cells() {
                return this.store;
            }

            private final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        };

        final SpreadsheetCell a1 = CELL.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(111)
            )
        );

        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setValue(
                    Optional.of(222)
                )
            );

        repo.cells()
            .save(a1);
        repo.cells()
            .save(b2);

        this.loadCellRangeAndCheck(
            SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader.with(repo),
            SpreadsheetSelection.parseCellRange("A1:B2"),
            this.createContext(),
            a1,
            b2
        );
    }

    @Test
    public void testLoadLabel() {
        final SpreadsheetStoreRepository repo = new FakeSpreadsheetStoreRepository() {

            @Override
            public SpreadsheetLabelStore labels() {
                return this.store;
            }

            private final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        };

        final SpreadsheetLabelMapping label = SpreadsheetSelection.labelName("Label123")
            .setLabelMappingReference(CELL);

        repo.labels()
            .save(label);

        this.loadLabelAndCheck(
            SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader.with(repo),
            label.label(),
            label
        );
    }

    @Override
    public SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader createSpreadsheetExpressionReferenceLoader() {
        return SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader.with(SpreadsheetStoreRepositories.fake());
    }

    @Override
    public SpreadsheetExpressionEvaluationContext createContext() {
        return SpreadsheetExpressionEvaluationContexts.fake();
    }
}
