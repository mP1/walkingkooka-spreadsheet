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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.convert.FakeSpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storages;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.FakeExpressionFunctionProvider;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetContextTest extends SpreadsheetExpressionEvaluationContextSharedTestCase<SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext> {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("Z9");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
        CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("'CurrentCell"))
    );

    private final static SpreadsheetExpressionReferenceLoader SPREADSHEET_EXPRESSION_REFERENCE_LOADER = SpreadsheetExpressionReferenceLoaders.fake();

    private final static SpreadsheetMetadataMode MODE = SpreadsheetMetadataMode.FORMULA;

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    ).set(
        SpreadsheetMetadataPropertyName.FUNCTIONS,
        SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
    ).set(
        SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
        SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
    );

    static {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(METADATA);

        SPREADSHEET_STORE_REPOSITORY = SpreadsheetStoreRepositories.treeMap(
            store,
            Storages.fake()
        );
    }

    private final static SpreadsheetStoreRepository SPREADSHEET_STORE_REPOSITORY;

    static {
        final SpreadsheetEnvironmentContext c = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        c.setSpreadsheetId(SPREADSHEET_ID);
        SPREADSHEET_ENVIRONMENT_CONTEXT = c;
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

    private final static SpreadsheetContext SPREADSHEET_CONTEXT = spreadsheetContext(
        SPREADSHEET_ENVIRONMENT_CONTEXT,
        EXPRESSION_FUNCTION_PROVIDER,
        PROVIDER_CONTEXT
    );

    private final static TerminalContext TERMINAL_CONTEXT = TerminalContexts.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullModeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                null,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                null,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetExpressionReferenceLoaderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                CELL,
                null,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetLabelNameResolverFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                null,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                null,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTerminalContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                null
            )
        );
    }

    // setCell..........................................................................................................

    @Test
    public void testSetCellDifferentCell() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext();

        final Optional<SpreadsheetCell> differentCell = Optional.of(
            SpreadsheetSelection.parseCell("B2")
                .setFormula(
                    SpreadsheetFormula.EMPTY.setText("Different")
                )
        );

        final SpreadsheetExpressionEvaluationContext different = context.setCell(differentCell);
        assertNotSame(
            context,
            different
        );
        this.checkEquals(
            differentCell,
            different.cell(),
            "serverUrl"
        );
    }

    // loadCell.........................................................................................................

    @Test
    public void testLoadCell() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+22+333")
        );
        cellStore.save(spreadsheetCell);

        this.loadCellAndCheck(
            this.createContext(
                new FakeSpreadsheetExpressionReferenceLoader() {
                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return cellStore.load(cell);
                    }
                }
            ),
            cell,
            spreadsheetCell
        );
    }

    // nextEmptyColumn..................................................................................................

    @Test
    public void testNextEmptyColumn() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );
        cellStore.save(spreadsheetCell);

        this.nextEmptyColumnAndCheck(
            SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SPREADSHEET_LABEL_NAME_RESOLVER,
                spreadsheetContext(cellStore),
                TERMINAL_CONTEXT
            ),
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    // nextEmptyRow.....................................................................................................

    @Test
    public void testNextEmptyRow() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );
        cellStore.save(spreadsheetCell);

        this.nextEmptyRowAndCheck(
            SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
                MODE,
                CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SPREADSHEET_LABEL_NAME_RESOLVER,
                spreadsheetContext(cellStore),
                TERMINAL_CONTEXT
            ),
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseRow("2")
        );
    }

    // evaluateExpression...............................................................................................

    @Test
    public void testEvaluateExpressionFunctionParameterValueConvertFails() {
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("HelloFunction");

        this.evaluateExpressionAndCheck(
            this.createContext(
                new FakeExpressionFunctionProvider<>() {
                    @Override
                    public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                            final List<?> values,
                                                                                                            final ProviderContext context) {
                        return new FakeExpressionFunction<>() {
                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(name);
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameter.NUMBER.setType(Void.class)
                                        .setKinds(
                                            ExpressionFunctionParameterKind.CONVERT_EVALUATE
                                        )
                                );
                            }

                            @Override
                            public Object apply(final List<Object> values,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                ExpressionFunctionParameter.NUMBER.getOrFail(values, 0);
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                    @Override
                    public ExpressionFunctionInfoSet expressionFunctionInfos() {
                        return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/HelloFunction HelloFunction");
                    }

                    @Override
                    public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                        return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                    }
                }
            ),
            Expression.call(
                Expression.namedFunction(functionName),
                Lists.of(
                    Expression.value("String1")
                )
            ),
            SpreadsheetErrorKind.VALUE.setMessage(
                "HelloFunction: number: Cannot convert \"String1\" to Void"
            )
        );
    }

    // evaluateFunction.................................................................................................

    @Test
    public void testEvaluateFunctionMissingParameters() {
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("HelloFunction");

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext(
            new FakeExpressionFunctionProvider<>() {

                @Override
                public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                        final List<?> values,
                                                                                                        final ProviderContext context) {
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Optional<ExpressionFunctionName> name() {
                            return Optional.of(functionName);
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(int count) {
                            return Lists.of(
                                ExpressionFunctionParameter.DATE,
                                ExpressionFunctionParameter.DATETIME,
                                ExpressionFunctionParameter.TIME
                            );
                        }

                        @Override
                        public Object apply(final List<Object> parameters,
                                            final SpreadsheetExpressionEvaluationContext context) {
                            this.checkParameterCount(parameters);
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public ExpressionFunctionInfoSet expressionFunctionInfos() {
                    return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/HelloFunction HelloFunction");
                }

                @Override
                public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                    return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                }
            }
        );

        final ExpressionFunction<?, ExpressionEvaluationContext> function = context.expressionFunction(
            functionName
        );

        this.evaluateFunctionAndCheck(
            context,
            Cast.to(
                function
            ),
            Lists.of(1),
            Cast.to(
                SpreadsheetErrorKind.VALUE.setMessage("HelloFunction: Missing parameter(s): date-time, time")
            )
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        final SpreadsheetEnvironmentContext differentEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final SpreadsheetExpressionEvaluationContext afterSet = context.setEnvironmentContext(differentEnvironmentContext);
        this.checkNotEquals(
            context,
            afterSet
        );
    }

    // HasLineEndings...................................................................................................

    @Test
    public void testLineEnding() {
        final SpreadsheetEnvironmentContext context = SPREADSHEET_ENVIRONMENT_CONTEXT;

        this.lineEndingAndCheck(
            this.createContext(context),
            context.lineEnding()
        );
    }

    @Test
    public void testSetLineEnding() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT;

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext(spreadsheetEnvironmentContext);

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        context.setLineEnding(lineEnding);

        this.lineEndingAndCheck(
            context,
            lineEnding
        );
    }

    @Test
    public void testLocale() {
        final SpreadsheetEnvironmentContext context = SPREADSHEET_ENVIRONMENT_CONTEXT;

        this.localeAndCheck(
            this.createContext(context),
            context.locale()
        );
    }

    @Test
    public void testSetLocale() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext(SPREADSHEET_ENVIRONMENT_CONTEXT);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    @Test
    public void testEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            name,
            value
        );
    }

    @Test
    public void testSetEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext(spreadsheetEnvironmentContext);
        context.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            context,
            name,
            value
        );
    }

    @Test
    public void testRemoveEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext(spreadsheetEnvironmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testUser() {
        final EmailAddress user = EmailAddress.parse("user123@example.com");

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setUser(
            Optional.of(user)
        );

        this.userAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            user
        );
    }

    @Test
    public void testReferenceWithEnvironmentValueName() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.referenceAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            name,
            value
        );
    }

    @Test
    public void testReferenceWithEnvironmentValueNameCycle() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<SpreadsheetLabelName> name = EnvironmentValueName.with(
            "Hello",
            SpreadsheetLabelName.class
        );
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            label
        );

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String value = "Hello World123";

        this.referenceFails(
            this.createContext(
                spreadsheetEnvironmentContext,
                new FakeSpreadsheetExpressionReferenceLoader() {

                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference c,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.ofNullable(
                            cell.equalsIgnoreReferenceKind(c) ?
                                cell.setFormula(
                                    SpreadsheetFormula.EMPTY.setValue(
                                        Optional.of(name)
                                    )
                                ) :
                                null
                        );
                    }
                },
                new FakeSpreadsheetLabelNameResolver() {
                    @Override
                    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName l) {
                        return Optional.ofNullable(
                            label.equals(l) ?
                                cell :
                                null
                        );
                    }
                }
            ),
            name,
            new IllegalArgumentException("Cycle detected from Hello with Hello")
        );
    }

    @Test
    public void testReferenceWithEnvironmentValueNameEqualsLabel() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<SpreadsheetLabelName> name = EnvironmentValueName.with(
            "Hello",
            SpreadsheetLabelName.class
        );
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            label
        );

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String value = "Hello World123";

        this.referenceAndCheck(
            this.createContext(
                spreadsheetEnvironmentContext,
                new FakeSpreadsheetExpressionReferenceLoader() {

                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference c,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.ofNullable(
                            cell.equalsIgnoreReferenceKind(c) ?
                                cell.setFormula(
                                    SpreadsheetFormula.EMPTY.setValue(
                                        Optional.of(value)
                                    )
                                ) :
                                null
                        );
                    }
                },
                new FakeSpreadsheetConverterContext() {
                    @Override
                    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName l) {
                        return Optional.ofNullable(
                            label.equals(l) ?
                                cell :
                                null
                        );
                    }
                }
            ),
            name,
            value
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext() {
        return this.createContext(SPREADSHEET_EXPRESSION_REFERENCE_LOADER);
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext(final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader) {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
            spreadsheetExpressionReferenceLoader,
            SPREADSHEET_LABEL_NAME_RESOLVER
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            spreadsheetEnvironmentContext,
            SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
            SPREADSHEET_LABEL_NAME_RESOLVER
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext(final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return this.createContext(
            SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            expressionFunctionProvider
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext(final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                                         final SpreadsheetLabelNameResolver labelNameResolver,
                                                                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
            spreadsheetExpressionReferenceLoader,
            labelNameResolver,
            expressionFunctionProvider,
            PROVIDER_CONTEXT
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                         final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                                         final SpreadsheetLabelNameResolver labelNameResolver) {
        return createContext(
            spreadsheetEnvironmentContext,
            spreadsheetExpressionReferenceLoader,
            labelNameResolver,
            EXPRESSION_FUNCTION_PROVIDER,
            ProviderContexts.fake()
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                         final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                                         final SpreadsheetLabelNameResolver labelNameResolver,
                                                                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                         final ProviderContext providerContext) {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.with(
            MODE,
            CELL,
            spreadsheetExpressionReferenceLoader,
            labelNameResolver,
            spreadsheetContext(
                spreadsheetEnvironmentContext,
                expressionFunctionProvider,
                providerContext
            ),
            TERMINAL_CONTEXT
        );
    }

    private static SpreadsheetContext spreadsheetContext(final SpreadsheetCellStore cellStore) {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(METADATA);

        return spreadsheetContext(
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetCellStore cells() {
                    return cellStore;
                }

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return store;
                }
            },
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
            EXPRESSION_FUNCTION_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private static SpreadsheetContext spreadsheetContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                         final ProviderContext providerContext) {
        return spreadsheetContext(
            SPREADSHEET_STORE_REPOSITORY,
            spreadsheetEnvironmentContext,
            expressionFunctionProvider,
            providerContext
        );
    }

    private static SpreadsheetContext spreadsheetContext(final SpreadsheetStoreRepository storeRepository,
                                                         final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                         final ProviderContext providerContext) {
        return SpreadsheetContexts.fixedSpreadsheetId(
            storeRepository,
            (c) -> {
                throw new UnsupportedOperationException();
            }, // Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory
            (c) -> {
                throw new UnsupportedOperationException();
            }, // Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            spreadsheetProvider(expressionFunctionProvider),
            providerContext
        );
    }

    private static SpreadsheetProvider spreadsheetProvider(final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return SpreadsheetProviders.basic(
            CONVERTER_PROVIDER,
            expressionFunctionProvider,
            SPREADSHEET_COMPARATOR_PROVIDER,
            SPREADSHEET_EXPORTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            FORM_HANDLER_PROVIDER,
            SPREADSHEET_IMPORTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER,
            VALIDATOR_PROVIDER
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverter() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext context = this.createContext();

        final ExpressionNumber from = context.expressionNumberKind().create(123);
        final String to = context.convertOrFail(from, String.class);

        this.checkEquals(
            to,
            context.converter().convertOrFail(from, String.class, context),
            () -> "converter with context and context convertOrFail should return the same"
        );
    }

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return METADATA.getOrFail(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT);
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext> type() {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetContext.class;
    }
}
