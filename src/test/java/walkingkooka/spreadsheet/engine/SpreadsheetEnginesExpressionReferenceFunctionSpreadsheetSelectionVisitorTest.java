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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;
import walkingkooka.spreadsheet.store.FakeSpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Optional;
import java.util.Set;

public final class SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitorTest
        implements SpreadsheetSelectionVisitorTesting<SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitor> {

    @Test
    public void testCellNonNullValue() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");
        final String value = "B2Value";

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                reference,
                                selection,
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
                Optional.of(
                        Optional.of(value)
                )
        );
    }

    @Test
    public void testCellNullValue() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                reference,
                                selection,
                                "loadCell"
                        );

                        return SpreadsheetDelta.EMPTY
                                .setCells(
                                        Sets.of(
                                                reference.setFormula(
                                                        SpreadsheetFormula.EMPTY
                                                                .setText("=1+2")
                                                )
                                        )
                                );
                    }
                },
                SpreadsheetEngineContexts.fake(),
                reference,
                Optional.of(
                        Optional.empty()
                )
        );
    }

    @Test
    public void testCellNotFound() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                reference,
                                selection,
                                "loadCell"
                        );

                        return SpreadsheetDelta.EMPTY;
                    }
                },
                SpreadsheetEngineContexts.fake(),
                reference,
                Optional.empty()
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
                    public SpreadsheetDelta loadCells(final SpreadsheetSelection c,
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
                                        return Optional.of(
                                                l.setLabelMappingTarget(cell)
                                        );
                                    }
                                };
                            }
                        };
                    }
                },
                label,
                Optional.of(
                        Optional.of(value)
                )
        );
    }

    @Test
    public void testCellRange() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:B3");
        final String b2Value = "B2Value";
        final Integer b3Value = 123;

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                range,
                                selection,
                                "loadCells"
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
                        Optional.of(
                                Lists.of(b2Value, b3Value)
                        )
                )
        );
    }

    @Test
    public void testCellRangeMissingCells() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:C3");
        final String b2Value = "B2Value";
        final Integer c3Value = 123;

        this.valuesAndCheck(
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
                        checkEquals(
                                range,
                                selection,
                                "loadCells"
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
                        Optional.of(
                                Lists.of(
                                        b2Value, // B2
                                        SpreadsheetError.selectionNotFound(
                                                SpreadsheetSelection.parseCell("C2")
                                        ), // C2
                                        SpreadsheetError.selectionNotFound(
                                                SpreadsheetSelection.parseCell("B3")
                                        ), // B3
                                        c3Value // C3
                                )
                        )
                )
        );
    }

    private void valuesAndCheck(final SpreadsheetEngine engine,
                                final SpreadsheetEngineContext context,
                                final SpreadsheetExpressionReference reference,
                                final Optional<Optional<Object>> value) {
        this.checkEquals(
                value,
                SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitor.values(
                        reference,
                        engine,
                        context
                ),
                () -> "values " + reference
        );
    }

    @Override
    public SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitor createVisitor() {
        return new SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitor(
                null,
                null
        );
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetEnginesExpressionReferenceFunction.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitor> type() {
        return SpreadsheetEnginesExpressionReferenceFunctionSpreadsheetSelectionVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
