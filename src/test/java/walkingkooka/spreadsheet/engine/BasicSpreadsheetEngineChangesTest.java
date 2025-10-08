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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
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
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Optional;

public final class BasicSpreadsheetEngineChangesTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineChanges>
    implements ToStringTesting<BasicSpreadsheetEngineChanges> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final BasicSpreadsheetEngine engine = BasicSpreadsheetEngine.INSTANCE;

        final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChanges.with(
            engine,
            SpreadsheetEngineEvaluation.SKIP_EVALUATE,
            SpreadsheetDeltaProperties.ALL,
            BasicSpreadsheetEngineChangesMode.IMMEDIATE, // IMMEDIATE is simpler than BATCH
            new FakeSpreadsheetEngineContext() {

                @Override
                public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                     final SpreadsheetExpressionReferenceLoader loader) {
                    return new FakeSpreadsheetExpressionEvaluationContext() {
                        @Override
                        public String toString() {
                            return this.getClass().getSimpleName();
                        }
                    };
                }

                @Override
                public SpreadsheetStoreRepository storeRepository() {
                    return new FakeSpreadsheetStoreRepository() {
                        @Override
                        public SpreadsheetCellStore cells() {
                            return SpreadsheetCellStores.treeMap();
                        }

                        @Override
                        public SpreadsheetCellReferencesStore cellReferences() {
                            return SpreadsheetCellReferencesStores.treeMap();
                        }

                        @Override
                        public SpreadsheetColumnStore columns() {
                            return SpreadsheetColumnStores.treeMap();
                        }

                        @Override
                        public SpreadsheetLabelStore labels() {
                            return SpreadsheetLabelStores.treeMap();
                        }

                        @Override
                        public SpreadsheetLabelReferencesStore labelReferences() {
                            return SpreadsheetLabelReferencesStores.treeMap();
                        }

                        @Override
                        public SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells() {
                            return SpreadsheetCellRangeStores.treeMap();
                        }

                        @Override
                        public SpreadsheetRowStore rows() {
                            return SpreadsheetRowStores.treeMap();
                        }
                    };
                }
            }
        );

        changes.onCellSaved(
            SpreadsheetSelection.A1
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("1+2")
                )
        );
        changes.onCellSaved(
            SpreadsheetSelection.parseCell("B2")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("3+4")
                )
        );

        changes.onColumnSaved(
            SpreadsheetSelection.parseColumn("M")
                .column()
        );
        changes.onColumnSaved(
            SpreadsheetSelection.parseColumn("N")
                .column()
                .setHidden(true)
        );

        changes.onRowSaved(
            SpreadsheetSelection.parseRow("6")
                .row()
        );
        changes.onRowSaved(
            SpreadsheetSelection.parseRow("7")
                .row()
                .setHidden(true)
        );

        this.toStringAndCheck(
            changes,
            "SKIP_EVALUATE cells: A1: A1 \"1+2\" status=SAVED, B2: B2 \"3+4\" status=SAVED columns: M: M status=SAVED, N: N hidden=true status=SAVED rows: 6: 6 status=SAVED, 7: 7 hidden=true status=SAVED"
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineChanges> type() {
        return BasicSpreadsheetEngineChanges.class;
    }

    @Override
    public String typeNameSuffix() {
        return "Changes";
    }
}
