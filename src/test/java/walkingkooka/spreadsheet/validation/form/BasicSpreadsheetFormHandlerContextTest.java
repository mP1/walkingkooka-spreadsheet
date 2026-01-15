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

package walkingkooka.spreadsheet.validation.form;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.LabelNotFoundException;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.ReferenceExpression;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetFormHandlerContextTest implements SpreadsheetFormHandlerContextTesting<BasicSpreadsheetFormHandlerContext>,
    HashCodeEqualsDefinedTesting2<BasicSpreadsheetFormHandlerContext> {

    private final static Form<SpreadsheetExpressionReference> FORM = Form.with(
        FormName.with("Form123")
    );

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    private static final Function<Set<SpreadsheetCell>, SpreadsheetDelta> CELLS_SAVER = (Set<SpreadsheetCell> cells) -> SpreadsheetDelta.EMPTY.setCells(cells);

    // with.............................................................................................................

    @Test
    public void testWithNullFormFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetFormHandlerContext.with(
                null,
                SpreadsheetExpressionReferenceLoaders.fake(),
                CELLS_SAVER,
                SpreadsheetEngineContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetExpressionReferenceLoaderFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                null,
                CELLS_SAVER,
                SpreadsheetEngineContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullCellsSaverFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                SpreadsheetExpressionReferenceLoaders.fake(),
                null,
                SpreadsheetEngineContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEngineContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                SpreadsheetExpressionReferenceLoaders.fake(),
                CELLS_SAVER,
                null
            )
        );
    }

    // validatorContext.................................................................................................

    @Test
    public void testValidationContext() {
        final BasicSpreadsheetFormHandlerContext context = this.createContext();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetValidatorContext validatorContext = context.validatorContext(cell);

        this.checkEquals(
            cell,
            validatorContext.validationReference(),
            "validationReference"
        );

        final String value = "*VALUE123*";

        final ExpressionEvaluationContext expressionEvaluationContext = validatorContext.expressionEvaluationContext(value);

        this.checkEquals(
            value,
            expressionEvaluationContext.evaluateExpression(
                ReferenceExpression.reference(SpreadsheetValidatorContext.VALUE)
            ),
            "VALUE"
        );

        this.checkEquals(
            EXPRESSION_NUMBER_KIND.create(1 + 23),
            expressionEvaluationContext.evaluateExpression(
                Expression.add(
                    Expression.value(EXPRESSION_NUMBER_KIND.create(1)),
                    Expression.value(EXPRESSION_NUMBER_KIND.create(23))
                )
            ),
            "expressionEvaluationContext.evaluateExpression(1+23)"
        );
    }

    // loadFormFieldValue...............................................................................................

    @Test
    public void testLoadFormFieldValueCellMissing() {
        this.loadFormFieldValueAndCheck(
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                new FakeSpreadsheetExpressionReferenceLoader() {
                    @Override
                    public Optional<SpreadsheetCell> loadCell(SpreadsheetCellReference cell, SpreadsheetExpressionEvaluationContext context) {
                        return Optional.empty();
                    }
                },
                CELLS_SAVER,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
                        return SpreadsheetExpressionEvaluationContexts.fake();
                    }
                }
            ),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testLoadFormFieldValueCellMissingValue() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.loadFormFieldValueAndCheck(
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                new FakeSpreadsheetExpressionReferenceLoader() {
                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.of(
                            cell.setFormula(SpreadsheetFormula.EMPTY)
                        );
                    }
                },
                CELLS_SAVER,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
                        return SpreadsheetExpressionEvaluationContexts.fake();
                    }
                }
            ),
            cell
        );
    }

    @Test
    public void testLoadFormFieldValueCellWithValue() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String value = "*VALUE123*";

        this.loadFormFieldValueAndCheck(
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                new FakeSpreadsheetExpressionReferenceLoader() {

                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.of(
                            cell.setFormula(
                                SpreadsheetFormula.EMPTY.setValue(
                                    Optional.of(value)
                                )
                            )
                        );
                    }
                },
                CELLS_SAVER,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
                        return SpreadsheetExpressionEvaluationContexts.fake();
                    }
                }
            ),
            cell,
            value
        );
    }

    @Test
    public void testLoadFormFieldValueCellWithUnknownLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("UnknownLabel");

        this.loadFormFieldValueAndCheck(
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                new FakeSpreadsheetExpressionReferenceLoader() {
                    @Override
                    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference cellRange,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Sets.empty();
                    }
                },
                CELLS_SAVER,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
                        return Optional.empty();
                    }
                }
            ),
            label
        );
    }

    @Test
    public void testLoadFormFieldValueCellWithLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String value = "*VALUE123*";

        this.loadFormFieldValueAndCheck(
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(FormName.with("Form123")),
                new FakeSpreadsheetExpressionReferenceLoader() {
                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.of(
                            cell.setFormula(
                                SpreadsheetFormula.EMPTY.setValue(
                                    Optional.of(value)
                                )
                            )
                        );
                    }
                },
                CELLS_SAVER,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
                        return Optional.of(cell);
                    }

                    @Override
                    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
                        return SpreadsheetExpressionEvaluationContexts.fake();
                    }
                }
            ),
            label,
            value
        );
    }

    // saveFormFieldValues..............................................................................................

    @Test
    public void testSaveFormFieldValuesWithFormFieldWithMissingCell() {
        final BasicSpreadsheetFormHandlerContext context = this.createContext();

        final SpreadsheetCellReference missingCell = SpreadsheetSelection.parseCell("Z99");
        final String value = "Value111";

        this.saveFormFieldValuesAndCheck(
            context,
            Lists.of(
                SpreadsheetForms.field(missingCell)
                    .setValue(
                        Optional.of(value)
                    )
            ),
            SpreadsheetDelta.EMPTY.setCells(
                Sets.of(
                    missingCell.setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                            Optional.of(value)
                        )
                    )
                )
            )
        );
    }

    @Test
    public void testSaveFormFieldValuesWithFormFieldWithCell() {
        final BasicSpreadsheetFormHandlerContext context = this.createContext();

        final SpreadsheetCellReference a1Cell = SpreadsheetSelection.A1;
        final String a1Value = "A1Value111";

        this.saveFormFieldValuesAndCheck(
            context,
            Lists.of(
                SpreadsheetForms.field(a1Cell)
                    .setValue(
                        Optional.of(a1Value)
                    )
            ),
            SpreadsheetDelta.EMPTY.setCells(
                Sets.of(
                    a1Cell.setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                            Optional.of(a1Value)
                        )
                    )
                )
            )
        );
    }

    private final static SpreadsheetCellReference A1_CELL = SpreadsheetSelection.A1;
    private static final FakeSpreadsheetExpressionReferenceLoader SPREADSHEET_EXPRESSION_REFERENCE_LOADER = new FakeSpreadsheetExpressionReferenceLoader() {
        @Override
        public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                  final SpreadsheetExpressionEvaluationContext context) {
            return Optional.ofNullable(
                A1_CELL.equalsIgnoreReferenceKind(cell) ?
                    cell.setFormula(SpreadsheetFormula.EMPTY) :
                    null
            );
        }

        @Override
        public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference cellRange,
                                                  final SpreadsheetExpressionEvaluationContext context) {
            final SpreadsheetCell cell = this.loadCell(
                cellRange.toCell(),
                context
            ).orElse(null);
            return null == cell ?
                Sets.empty() :
                Sets.of(cell);
        }
    };

    private final static SpreadsheetLabelName A1LABEL = SpreadsheetSelection.labelName("A1LABEL");

    @Test
    public void testSaveFormFieldValuesWithFormFieldWithLabel() {
        final BasicSpreadsheetFormHandlerContext context = this.createContext();

        final String a1Value = "A1Value111";

        this.saveFormFieldValuesAndCheck(
            context,
            Lists.of(
                SpreadsheetForms.field(A1LABEL)
                    .setValue(
                        Optional.of(a1Value)
                    )
            ),
            SpreadsheetDelta.EMPTY.setCells(
                Sets.of(
                    A1_CELL.setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                            Optional.of(a1Value)
                        )
                    )
                )
            )
        );
    }

    @Test
    public void testSaveFormFieldValuesWithFormFieldWithUnknownLabel() {
        final BasicSpreadsheetFormHandlerContext context = this.createContext();

        final SpreadsheetCellReference a1Cell = SpreadsheetSelection.A1;
        final String a1Value = "A1Value111";

        assertThrows(
            LabelNotFoundException.class,
            () -> context.saveFormFieldValues(
                Lists.of(
                    SpreadsheetForms.field(
                            SpreadsheetSelection.labelName("UNKNOWNLABEL")
                        )
                        .setValue(
                            Optional.of(a1Value)
                        )
                )
            )
        );
    }

    // helper...........................................................................................................

    @Override
    public BasicSpreadsheetFormHandlerContext createContext() {
        return BasicSpreadsheetFormHandlerContext.with(
            FORM,
            SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
            CELLS_SAVER,
            new FakeSpreadsheetEngineContext() {
                @Override
                public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                     final SpreadsheetExpressionReferenceLoader loader) {
                    return new FakeSpreadsheetExpressionEvaluationContext() {

                        @Override
                        public SpreadsheetExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
                            return new FakeSpreadsheetExpressionEvaluationContext() {

                                @Override
                                public boolean isText(final Object value) {
                                    return value instanceof CharSequence;
                                }

                                @Override
                                public Optional<Optional<Object>> reference(final ExpressionReference reference) {
                                    return scoped.apply(reference);
                                }

                                @Override
                                public <T> Either<T, String> convert(final Object value,
                                                                     final Class<T> target) {
                                    return Converters.simple()
                                        .convert(
                                            value,
                                            target,
                                            ConverterContexts.fake()
                                        );
                                }

                                @Override
                                public MathContext mathContext() {
                                    return MathContext.DECIMAL32;
                                }
                            };
                        }
                    };
                }

                @Override
                public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
                    return Optional.ofNullable(
                        A1LABEL.equals(labelName) ?
                            SpreadsheetSelection.A1 :
                            null
                    );
                }

                @Override
                public SpreadsheetEngineContext setEnvironmentContext(final EnvironmentContext environmentContext) {
                    Objects.requireNonNull(environmentContext, "environmentContext");

                    return SpreadsheetEngineContexts.fake();
                }

                @Override
                public LineEnding lineEnding() {
                    return this.environmentContext.lineEnding();
                }

                @Override
                public SpreadsheetEngineContext setLineEnding(final LineEnding lineEnding) {
                    this.environmentContext.setLineEnding(lineEnding);
                    return this;
                }

                @Override
                public Locale locale() {
                    return this.environmentContext.locale();
                }

                @Override
                public void setLocale(final Locale locale) {
                    this.environmentContext.setLocale(locale);
                }

                @Override
                public LocalDateTime now() {
                    return this.environmentContext.now();
                }

                @Override
                public Optional<EmailAddress> user() {
                    return this.environmentContext.user();
                }

                @Override
                public SpreadsheetEngineContext setUser(final Optional<EmailAddress> user) {
                    this.environmentContext.setUser(user);
                    return this;
                }

                @Override
                public <T> Optional<T> environmentValue(final EnvironmentValueName<T> environmentValueName) {
                    return this.environmentContext.environmentValue(environmentValueName);
                }

                @Override
                public SpreadsheetEngineContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
                    this.environmentContext.removeEnvironmentValue(name);
                    return this;
                }

                @Override
                public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
                    return this.environmentContext.addEventValueWatcher(watcher);
                }

                @Override
                public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
                    return this.environmentContext.addEventValueWatcherOnce(watcher);
                }

                private final EnvironmentContext environmentContext = EnvironmentContexts.map(
                    EnvironmentContexts.empty(
                        LineEnding.NL,
                        Locale.ENGLISH,
                        () -> LocalDateTime.MIN,
                        EnvironmentContext.ANONYMOUS
                    )
                );
            }
        );
    }

    @Override
    public void testEnvironmentValueWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNullValueFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testUserNotNull() {
        throw new UnsupportedOperationException();
    }

    // hashCode/equals..................................................................................................

    @Test
    @Override
    public void testEquals() {
        final SpreadsheetEngineContext engineContext = SpreadsheetEngineContexts.fake();

        this.checkEquals(
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                engineContext
            ),
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                engineContext
            )
        );
    }

    @Test
    public void testEqualsDifferentForm() {
        final SpreadsheetEngineContext engineContext = SpreadsheetEngineContexts.fake();

        this.checkNotEquals(
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(
                    FormName.with("formName")
                ),
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                engineContext
            ),
            BasicSpreadsheetFormHandlerContext.with(
                Form.with(
                    FormName.with("differentFormName")
                ),
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                engineContext
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetExpressionReferenceLoader() {
        final SpreadsheetEngineContext engineContext = SpreadsheetEngineContexts.fake();

        this.checkNotEquals(
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SpreadsheetExpressionReferenceLoaders.fake(),
                CELLS_SAVER,
                engineContext
            ),
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SpreadsheetExpressionReferenceLoaders.fake(),
                CELLS_SAVER,
                engineContext
            )
        );
    }

    @Test
    public void testEqualsDifferentCellSaver() {
        final SpreadsheetEngineContext engineContext = SpreadsheetEngineContexts.fake();

        this.checkNotEquals(
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                engineContext
            ),
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                (c) -> {
                    throw new UnsupportedOperationException();
                },
                engineContext
            )
        );
    }

    @Test
    public void testEqualsDifferentEngineContext() {
        this.checkNotEquals(
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                SpreadsheetEngineContexts.fake()
            ),
            BasicSpreadsheetFormHandlerContext.with(
                FORM,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                CELLS_SAVER,
                SpreadsheetEngineContexts.fake()
            )
        );
    }

    @Override
    public BasicSpreadsheetFormHandlerContext createObject() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetFormHandlerContext> type() {
        return BasicSpreadsheetFormHandlerContext.class;
    }
}
