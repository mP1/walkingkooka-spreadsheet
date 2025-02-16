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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStores;
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
                            public SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences() {
                                return SpreadsheetExpressionReferenceStores.treeMap();
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
                            public SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences() {
                                return SpreadsheetExpressionReferenceStores.treeMap();
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
                },
                SpreadsheetDeltaProperties.ALL,
                BasicSpreadsheetEngineChangesMode.IMMEDIATE
        );

        changes.onCellSavedImmediate(
                SpreadsheetSelection.A1
                        .setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("1+2")
                        )
        );
        changes.onCellSavedImmediate(
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("3+4")
                        )
        );

        changes.onColumnSavedImmediate(
                SpreadsheetSelection.parseColumn("M")
                        .column()
        );
        changes.onColumnSavedImmediate(
                SpreadsheetSelection.parseColumn("N")
                        .column()
                        .setHidden(true)
        );

        changes.onRowSavedImmediate(
                SpreadsheetSelection.parseRow("6")
                        .row()
        );
        changes.onRowSavedImmediate(
                SpreadsheetSelection.parseRow("7")
                        .row()
                        .setHidden(true)
        );

        this.toStringAndCheck(
                changes,
                "{A1=A1 A1 1+2 status=SAVE committed=true, B2=B2 B2 3+4 status=SAVE committed=true} {} {M=M M status=SAVE, N=N N hidden=true status=SAVE} {6=6 6 status=SAVE, 7=7 7 hidden=true status=SAVE}"
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
