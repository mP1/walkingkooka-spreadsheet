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
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

public final class BasicSpreadsheetEngineChangesTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineChanges>
        implements ToStringTesting<BasicSpreadsheetEngineChanges> {

    @Test
    public void testToString() {
        final BasicSpreadsheetEngine engine = BasicSpreadsheetEngine.with(SpreadsheetMetadata.EMPTY);

        final BasicSpreadsheetEngineChanges cells = BasicSpreadsheetEngineChanges.with(
                engine,
                new FakeSpreadsheetEngineContext() {
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
                        };
                    }
                },
                BasicSpreadsheetEngineChangesMode.IMMEDIATE
        );

        cells.onCellSavedImmediate(
                SpreadsheetCell.with(
                        SpreadsheetExpressionReference.parseCell("A1"),
                        SpreadsheetFormula.EMPTY
                                .setText("1+2")
                )
        );
        cells.onCellSavedImmediate(
                SpreadsheetCell.with(
                        SpreadsheetExpressionReference.parseCell("B2"),
                        SpreadsheetFormula.EMPTY
                                .setText("3+4")
                )
        );

        this.toStringAndCheck(cells, "{A1=A1=1+2, B2=B2=3+4}");
    }

    @Override
    public Class<BasicSpreadsheetEngineChanges> type() {
        return BasicSpreadsheetEngineChanges.class;
    }

    @Override
    public String typeNameSuffix() {
        return "Changes";
    }
}
