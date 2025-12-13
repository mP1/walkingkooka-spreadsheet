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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.validation.ValueType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class BasicSpreadsheetEngineFilterCellsPredicateTest implements PredicateTesting2<BasicSpreadsheetEngineFilterCellsPredicate, SpreadsheetCell>,
    SpreadsheetMetadataTesting {

    private final static String CONTEXT_TO_STRING = "FakeSpreadsheetEngineContext123";

    private final static String LOADER_TO_STRING = "LoaderToString";

    // test.............................................................................................................

    @Test
    public void testTestWithNullFalse() {
        this.testFalse(null);
    }

    @Test
    public void testTestFalseEmptyFormulaNoValueWithAny() {
        this.testFalse(
            this.createPredicate(SpreadsheetValueType.ANY),
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testTestTrueAnyValueTypeWithNotEmptyFormula() {
        this.testTrue(
            this.createPredicate(SpreadsheetValueType.ANY),
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
            )
        );
    }

    @Test
    public void testTestTrueSameValueType() {
        this.testTrue(
            this.createPredicate(SpreadsheetValueType.TEXT),
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=\"hello\"")
                    .setValue(
                        Optional.of("hello")
                    )
            )
        );
    }

    @Test
    public void testTestFalseAnyValueType() {
        this.testFalse(
            this.createPredicate(SpreadsheetValueType.ANY),
            SpreadsheetSelection.parseCell("B2")
                .setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testTestFalseWrongValueType() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=\"Hello\"")
                .setValue(
                    Optional.of("Hello")
                )
        );

        this.testTrue(
            this.createPredicate(SpreadsheetValueType.ANY),
            cell
        );
        this.testFalse(
            this.createPredicate(SpreadsheetValueType.NUMBER),
            cell
        );
    }

    @Override
    public BasicSpreadsheetEngineFilterCellsPredicate createPredicate() {
        return this.createPredicate(
            SpreadsheetValueType.ANY
        );
    }

    private BasicSpreadsheetEngineFilterCellsPredicate createPredicate(final ValueType valueType) {
        final Expression expression = Expression.call(
            Expression.namedFunction(
                SpreadsheetExpressionFunctions.name("Test123")
            ),
            Expression.NO_CHILDREN
        );

        return BasicSpreadsheetEngineFilterCellsPredicate.with(
            valueType,
            expression,
            new FakeSpreadsheetEngineContext() {

                @Override
                public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                     final SpreadsheetExpressionReferenceLoader loader) {
                    Objects.requireNonNull(cell, "cell");

                    return new FakeSpreadsheetExpressionEvaluationContext() {
                        @Override
                        public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
                            Objects.requireNonNull(name, "name");

                            return Cast.to(
                                new FakeExpressionFunction<Object, SpreadsheetExpressionEvaluationContext>() {

                                    @Override
                                    public Optional<ExpressionFunctionName> name() {
                                        return Optional.of(name);
                                    }

                                    @Override
                                    public Object apply(final List<Object> parameters,
                                                        final SpreadsheetExpressionEvaluationContext context) {
                                        assertEquals(
                                            Lists.empty(),
                                            parameters,
                                            "parameters"
                                        );

                                        return true;
                                    }

                                    @Override
                                    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                        return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters")
                                                .variable(Object.class)
                                        );
                                    }

                                    @Override
                                    public boolean isPure(final ExpressionPurityContext context) {
                                        return true;
                                    }
                                }
                            );
                        }

                        @Override
                        public <T> Either<T, String> convert(final Object value,
                                                             final Class<T> target) {
                            return this.successfulConversion(
                                target.cast(value),
                                target
                            );
                        }
                    };
                }

                @Override
                public <C extends ConverterContext> Converter<C> converter(final ConverterName name,
                                                                           final List<?> values,
                                                                           final ProviderContext context) {
                    return CONVERTER_PROVIDER.converter(
                        name,
                        values,
                        context
                    );
                }

                @Override
                public SpreadsheetMetadata spreadsheetMetadata() {
                    return METADATA_EN_AU;
                }

                @Override
                public String toString() {
                    return CONTEXT_TO_STRING;
                }
            },
            new FakeSpreadsheetExpressionReferenceLoader() {
                @Override
                public String toString() {
                    return LOADER_TO_STRING;
                }
            } ///
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createPredicate(),
            "* Test123() " + CONTEXT_TO_STRING + " " + LOADER_TO_STRING
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineFilterCellsPredicate> type() {
        return BasicSpreadsheetEngineFilterCellsPredicate.class;
    }
}
