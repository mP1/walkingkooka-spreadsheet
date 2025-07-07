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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;

import java.util.Optional;
import java.util.Set;

public final class BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitorTest
    implements SpreadsheetSelectionVisitorTesting<BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor> {

    @Test
    public void testValuesWithCellNonNullValue() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");
        final String value = "B2Value";

        this.valuesAndCheck(
            reference,
            new FakeSpreadsheetExpressionReferenceLoader() {
                @Override
                public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                          final SpreadsheetExpressionEvaluationContext context) {
                    checkEquals(
                        reference,
                        cell,
                        "loadCell"
                    );

                    return Optional.of(
                        cell.setFormula(
                            SpreadsheetFormula.EMPTY
                                .setText("=1+2")
                                .setValue(
                                    Optional.of(
                                        value
                                    )
                                )
                        )
                    );
                }
            },
            Optional.of(
                Optional.of(value)
            )
        );
    }

    @Test
    public void testValuesWithCellNullValue() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        this.valuesAndCheck(
            reference,
            new FakeSpreadsheetExpressionReferenceLoader() {
                @Override
                public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                          final SpreadsheetExpressionEvaluationContext context) {
                    checkEquals(
                        reference,
                        cell,
                        "loadCell"
                    );

                    return Optional.of(
                        cell.setFormula(
                            SpreadsheetFormula.EMPTY
                                .setText("=1+2")
                                .setValue(Optional.empty())
                        )
                    );
                }
            },
            SpreadsheetExpressionEvaluationContext.REFERENCE_NULL_VALUE
        );
    }

    @Test
    public void testValuesWithCellNotFound() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.A1;

        this.valuesAndCheck(
            reference,
            new FakeSpreadsheetExpressionReferenceLoader() {

                @Override
                public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                          final SpreadsheetExpressionEvaluationContext context) {
                    checkEquals(
                        reference,
                        cell,
                        "loadCell"
                    );

                    return Optional.empty();
                }
            },
            SpreadsheetExpressionEvaluationContext.REFERENCE_NULL_VALUE
        );
    }

    @Test
    public void testValuesWithLabelToCell() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        final String value = "B2Value";

        this.valuesAndCheck(
            label,
            new FakeSpreadsheetExpressionReferenceLoader() {
                @Override
                public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference c,
                                                          final SpreadsheetExpressionEvaluationContext context) {
                    checkEquals(
                        cell,
                        c,
                        "loadCell"
                    );

                    return Optional.of(
                        cell.setFormula(
                            SpreadsheetFormula.EMPTY
                                .setText("=1+2")
                                .setValue(
                                    Optional.of(
                                        value
                                    )
                                )
                        )
                    );
                }

                @Override
                public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName l) {
                    checkEquals(label, l);
                    return Optional.of(
                        l.setLabelMappingReference(cell)
                    );
                }
            },
            Optional.of(
                Optional.of(value)
            )
        );
    }

    @Test
    public void testValuesWithCellRange() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:B3");
        final String b2Value = "B2Value";
        final Integer b3Value = 123;

        this.valuesAndCheck(
            range,
            new FakeSpreadsheetExpressionReferenceLoader() {

                @Override
                public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference r,
                                                          final SpreadsheetExpressionEvaluationContext context) {
                    checkEquals(
                        r,
                        range,
                        "loadCells"
                    );

                    return Sets.of(
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
                    );
                }
            },
            Optional.of(
                Optional.of(
                    Lists.of(
                        b2Value,
                        b3Value
                    )
                )
            )
        );
    }

    @Test
    public void testValuesWithCellRangeMissingCells() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:C3");
        final String b2Value = "B2Value";
        final Integer c3Value = 123;

        this.valuesAndCheck(
            range,
            new FakeSpreadsheetExpressionReferenceLoader() {
                @Override
                public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference r,
                                                          final SpreadsheetExpressionEvaluationContext context) {
                    checkEquals(
                        range,
                        r,
                        "loadCells"
                    );

                    return Sets.of(
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
                    );
                }
            },
            Optional.of(
                Optional.of(
                    Lists.of(
                        b2Value, // B2
                        null, // C2
                        null, // B3
                        c3Value// C3
                    )
                )
            )
        );
    }

    private void valuesAndCheck(final SpreadsheetExpressionReference reference,
                                final SpreadsheetExpressionReferenceLoader loader,
                                final Optional<Optional<Object>> value) {
        this.checkEquals(
            value,
            BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor.values(
                reference,
                loader,
                SpreadsheetExpressionEvaluationContexts.fake()
            ),
            () -> "values " + reference
        );
    }

    @Override
    public BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor createVisitor() {
        return new BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor(
            null, // loader
            null // context
        );
    }

    // class ...........................................................................................................

    @Override
    public String typeNamePrefix() {
        return BasicSpreadsheetExpressionEvaluationContext.class.getSimpleName() + "Reference";
    }

    @Override
    public Class<BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor> type() {
        return BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
