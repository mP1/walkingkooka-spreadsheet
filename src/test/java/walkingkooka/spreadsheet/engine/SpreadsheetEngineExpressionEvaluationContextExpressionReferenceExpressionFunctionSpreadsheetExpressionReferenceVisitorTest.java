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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitorTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.store.FakeSpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Optional;
import java.util.Set;

public final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitorTest
        implements SpreadsheetExpressionReferenceVisitorTesting<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor> {

    @Test
    public void testCell() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");
        final String value = "B2Value";

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCell(final SpreadsheetCellReference cell,
                                                     final SpreadsheetEngineEvaluation evaluation,
                                                     final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                     final SpreadsheetEngineContext context) {
                        checkEquals(
                                reference,
                                cell,
                                "loadCell"
                        );

                        return SpreadsheetDelta.EMPTY
                                .setCells(
                                        Sets.of(
                                                reference.setFormula(
                                                        SpreadsheetFormula.EMPTY
                                                                .setText("=1+2")
                                                                .setValue(
                                                                        Optional.of(
                                                                                value
                                                                        )
                                                                )
                                                )
                                        )
                                );
                    }
                },
                SpreadsheetEngineContexts.fake(),
                reference,
                Optional.of(value)
        );
    }

    @Test
    public void testLabelToCell() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        final String value = "B2Value";

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCell(final SpreadsheetCellReference c,
                                                     final SpreadsheetEngineEvaluation evaluation,
                                                     final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                     final SpreadsheetEngineContext context) {
                        checkEquals(
                                cell,
                                c,
                                "loadCell"
                        );

                        return SpreadsheetDelta.EMPTY
                                .setCells(
                                        Sets.of(
                                                cell.setFormula(
                                                        SpreadsheetFormula.EMPTY
                                                                .setText("=1+2")
                                                                .setValue(
                                                                        Optional.of(
                                                                                value
                                                                        )
                                                                )
                                                )
                                        )
                                );
                    }
                },
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public SpreadsheetStoreRepository storeRepository() {
                        return new FakeSpreadsheetStoreRepository() {
                            @Override
                            public SpreadsheetLabelStore labels() {
                                return new FakeSpreadsheetLabelStore() {

                                    @Override
                                    public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName l) {
                                        checkEquals(label, l);
                                        return Optional.of(l.mapping(cell));
                                    }
                                };
                            }
                        };
                    }
                },
                label,
                Optional.of(value)
        );
    }

    @Test
    public void testCellRange() {
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:B3");
        final String b2Value = "B2Value";
        final Integer b3Value = 123;

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final Set<SpreadsheetCellRange> ranges,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                Sets.of(range),
                                ranges,
                                "loadCells ranges"
                        );

                        return SpreadsheetDelta.EMPTY
                                .setCells(
                                        Sets.of(
                                                SpreadsheetSelection.parseCell("B2")
                                                        .setFormula(
                                                                SpreadsheetFormula.EMPTY
                                                                        .setText("=1")
                                                                        .setValue(
                                                                                Optional.of(
                                                                                        b2Value
                                                                                )
                                                                        )
                                                        ),
                                                SpreadsheetSelection.parseCell("B3")
                                                        .setFormula(
                                                                SpreadsheetFormula.EMPTY
                                                                        .setText("=2")
                                                                        .setValue(
                                                                                Optional.of(
                                                                                        b3Value
                                                                                )
                                                                        )
                                                        )
                                        )
                                );
                    }
                },
                SpreadsheetEngineContexts.fake(),
                range,
                Optional.of(
                        Lists.of(b2Value, b3Value)
                )
        );
    }

    @Test
    public void testCellRangeMissingCells() {
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:C3");
        final String b2Value = "B2Value";
        final Integer c3Value = 123;

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final Set<SpreadsheetCellRange> ranges,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                Sets.of(range),
                                ranges,
                                "loadCells ranges"
                        );

                        return SpreadsheetDelta.EMPTY
                                .setCells(
                                        Sets.of(
                                                SpreadsheetSelection.parseCell("B2")
                                                        .setFormula(
                                                                SpreadsheetFormula.EMPTY
                                                                        .setText("=1")
                                                                        .setValue(
                                                                                Optional.of(
                                                                                        b2Value
                                                                                )
                                                                        )
                                                        ),
                                                SpreadsheetSelection.parseCell("C3")
                                                        .setFormula(
                                                                SpreadsheetFormula.EMPTY
                                                                        .setText("=2")
                                                                        .setValue(
                                                                                Optional.of(
                                                                                        c3Value
                                                                                )
                                                                        )
                                                        )
                                        )
                                );
                    }
                },
                SpreadsheetEngineContexts.fake(),
                range,
                Optional.of(
                        Lists.of(
                                b2Value, // B2
                                null, // B3
                                null, // C2
                                c3Value // C3
                        )
                )
        );
    }

    private void valuesAndCheck(final SpreadsheetEngine engine,
                                final SpreadsheetEngineContext context,
                                final SpreadsheetExpressionReference reference,
                                final Optional<Object> value) {
        this.checkEquals(
                value,
                SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor.values(
                        reference,
                        engine,
                        context
                ),
                () -> "values " + reference
        );
    }

    @Override
    public SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor(
                null,
                null
        );
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunction.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor> type() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
