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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextTest implements ClassTesting<SpreadsheetExpressionEvaluationContext> {

    @Test
    public void testCellOrFail() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<SpreadsheetCell> cell() {
                        return Optional.empty();
                    }
                }.cellOrFail()
        );

        this.checkEquals(
                "Missing cell",
                thrown.getMessage()
        );
    }

    @Test
    public void testReferenceOrFailPresentNotNull() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final Object value = "abc123";

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(label, r, "reference");
                        return Optional.of(
                                Optional.of(value)
                        );
                    }
                },
                label,
                value
        );
    }

    @Test
    public void testReferenceOrFailPresentNull() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(label, r, "reference");
                        return Optional.of(
                                Optional.empty()
                        );
                    }
                },
                label,
                null
        );
    }

    @Test
    public void testReferenceOrFailAbsent() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(label, r, "reference");
                        return Optional.empty();
                    }
                },
                label,
                SpreadsheetError.selectionNotFound(label)
        );
    }

    @Test
    public void testReferenceOrFailAbsentWhenNotSpreadsheetExpressionReference() {
        final TemplateValueName notSpreadsheetExpressionReference = TemplateValueName.with("TemplateValueName123");

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(notSpreadsheetExpressionReference, r, "reference");
                        return Optional.empty();
                    }
                },
                notSpreadsheetExpressionReference,
                SpreadsheetError.referenceNotFound(notSpreadsheetExpressionReference)
        );
    }

    private void referenceOrFailAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final ExpressionReference reference,
                                         final Object expected) {
        this.checkEquals(
                expected,
                context.referenceOrFail(reference),
                () -> "referenceOrFail " + reference
        );
    }

    // testCycleCheck...................................................................................................

    @Test
    public void testCellCycleCheckWhenCellEmpty() {
        this.cellCycleCheck(
                Optional.empty(),
                "B2"
        );
    }

    @Test
    public void testCellCycleCheckWhenCellDifferent() {
        this.cellCycleCheck(
                Optional.of("A1"),
                "B2"
        );
    }

    @Test
    public void testCellCycleCheckFails() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.cellCycleCheck(
                        Optional.of("A1"),
                        "A1"
                )
        );
    }

    @Test
    public void testCellCycleCheckDifferentReferenceKindFails() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.cellCycleCheck(
                        Optional.of("$A1"),
                        "A$1"
                )
        );
    }

    private void cellCycleCheck(final Optional<String> current,
                                final String load) {
        new FakeSpreadsheetExpressionEvaluationContext() {
            @Override
            public Optional<SpreadsheetCell> cell() {
                return current.map(
                        c -> SpreadsheetSelection.parseCell(c)
                                .setFormula(SpreadsheetFormula.EMPTY)
                );
            }
        }.cellCycleCheck(
                SpreadsheetSelection.parseCell(load)
        );
    }

    // cellRangeCycleCheck..............................................................................................

    @Test
    public void testCellRangeCycleCheckWhenEmpty() {
        this.cellRangeCycleCheck(
                Optional.empty(),
                "B2"
        );
    }

    @Test
    public void testCellRangeCycleCheckWhenOutside() {
        this.cellRangeCycleCheck(
                Optional.of("A1"),
                "B2"
        );
    }

    @Test
    public void testCellRangeCycleCheckFails() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.cellRangeCycleCheck(
                        Optional.of("A1"),
                        "A1"
                )
        );
    }

    @Test
    public void testCellRangeCycleCheckDifferentReferenceKindFails() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.cellRangeCycleCheck(
                        Optional.of("$A1"),
                        "A$1"
                )
        );
    }

    @Test
    public void testCellRangeCycleCheckContainsFails() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.cellRangeCycleCheck(
                        Optional.of("B2"),
                        "A1:C3"
                )
        );
    }

    private void cellRangeCycleCheck(final Optional<String> current,
                                     final String load) {
        new FakeSpreadsheetExpressionEvaluationContext() {
            @Override
            public Optional<SpreadsheetCell> cell() {
                return current.map(
                        c -> SpreadsheetSelection.parseCell(c)
                                .setFormula(SpreadsheetFormula.EMPTY)
                );
            }
        }.cellRangeCycleCheck(
                SpreadsheetSelection.parseCellRange(load)
        );
    }
    
    // class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContext> type() {
        return SpreadsheetExpressionEvaluationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
