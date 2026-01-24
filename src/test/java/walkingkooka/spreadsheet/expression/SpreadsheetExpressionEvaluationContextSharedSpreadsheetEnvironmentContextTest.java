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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.FakeSpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContextSuppliers;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.FakeSpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
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
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextTest extends SpreadsheetExpressionEvaluationContextSharedTestCase<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext> {

    private final static Storage<SpreadsheetStorageContext> STORAGE = Storages.fake();

    private final static SpreadsheetContextSupplier SPREADSHEET_CONTEXT_SUPPLIER = SpreadsheetContextSuppliers.fake();

    private final static int DECIMAL_NUMBER_DIGIT_COUNT = 6;

    static {
        SpreadsheetEnvironmentContext context = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        for (final EnvironmentValueName<?> name : SpreadsheetEnvironmentContextFactory.ENVIRONMENT_VALUE_NAMES) {
            if (name.equals(SpreadsheetEnvironmentContextFactory.CONVERTER)) {
                continue;
            }

            context.setEnvironmentValue(
                name,
                Cast.to(
                    METADATA_EN_AU.getOrFail(
                        SpreadsheetMetadataPropertyName.fromEnvironmentValueName(name)
                    )
                )
            );
        }

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.CONVERTER,
            METADATA_EN_AU.getOrFail(
                SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
            )
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.DECIMAL_NUMBER_DIGIT_COUNT,
            DECIMAL_NUMBER_DIGIT_COUNT
        );

        SPREADSHEET_ENVIRONMENT_CONTEXT = context;
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

    private final static SpreadsheetMetadataContext SPREADSHEET_METADATA_CONTEXT = SpreadsheetMetadataContexts.fake();

    private final static TerminalContext TERMINAL_CONTEXT = TerminalContexts.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetContextSupplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                null,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                null,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                null,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetMetadataContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                null,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTerminalContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                null,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        final EnvironmentContext differentEnvironmentContext = EnvironmentContexts.empty(
            INDENTATION,
            lineEnding,
            LOCALE,
            HAS_NOW,
            EnvironmentContext.ANONYMOUS
        );

        final SpreadsheetExpressionEvaluationContext afterSet = context.setEnvironmentContext(differentEnvironmentContext);
        this.checkNotEquals(
            context,
            afterSet
        );
    }

    // HasLineEndings...................................................................................................

    @Test
    public void testLineEnding() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.lineEndingAndCheck(
            this.createContext(context),
            context.lineEnding()
        );
    }

    @Test
    public void testSetLineEnding() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    INDENTATION,
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        this.setLineEndingAndCheck(
            context,
            lineEnding
        );
    }

    @Test
    public void testLocale() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                Locale.FRANCE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.localeAndCheck(
            this.createContext(context),
            context.locale()
        );
    }

    @Test
    public void testSetLocale() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    INDENTATION,
                    LINE_ENDING,
                    Locale.FRANCE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    @Test
    public void testEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            SPREADSHEET_ENVIRONMENT_CONTEXT
        );

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
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.map(
                SPREADSHEET_ENVIRONMENT_CONTEXT
            )
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);
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
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Test
    public void testUser() {
        final EmailAddress user = EmailAddress.parse("user123@example.com");

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                Optional.of(user)
            )
        );

        this.userAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            user
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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(
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

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullParametersFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    // cell.............................................................................................................

    @Test
    public void testCell() {
        this.cellAndCheck(
            this.createContext()
        );
    }

    // setCell..........................................................................................................

    @Override
    public void testSetCellWithSame() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testSetCellFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setCell(SpreadsheetExpressionEvaluationContext.NO_CELL)
        );
    }

    // loadCell.........................................................................................................

    @Test
    public void testLoadCellMissingSpreadsheetId() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        this.loadCellAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testLoadCellWithSpreadsheetIdEnvironmentContextValue() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(123);

        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final SpreadsheetCellStore cells = SpreadsheetCellStores.treeMap();

        final SpreadsheetCell spreadsheetCell = cells.save(
            cellReference.setFormula(
                SpreadsheetFormula.EMPTY.setValue(
                    Optional.of("text")
                )
            )
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);

        this.loadCellAndCheck(
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            spreadsheetId.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return new FakeSpreadsheetStoreRepository() {

                                            @Override
                                            public SpreadsheetCellStore cells() {
                                                return cells;
                                            }
                                        };
                                    }
                                } :
                                null
                        );
                    }
                },
                spreadsheetEnvironmentContext
            ),
            cellReference,
            spreadsheetCell
        );
    }

    // loadCellRange....................................................................................................

    @Test
    public void testLoadCellRangeMissingSpreadsheetId() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        this.loadCellRangeAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            SpreadsheetSelection.A1.toCellRange()
        );
    }

    @Test
    public void testLoadCellRangeWithSpreadsheetIdEnvironmentContextValue() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(123);

        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final SpreadsheetCellStore cells = SpreadsheetCellStores.treeMap();

        final SpreadsheetCell spreadsheetCell = cells.save(
            cellReference.setFormula(
                SpreadsheetFormula.EMPTY.setValue(
                    Optional.of("text")
                )
            )
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);

        this.loadCellRangeAndCheck(
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            spreadsheetId.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return new FakeSpreadsheetStoreRepository() {

                                            @Override
                                            public SpreadsheetCellStore cells() {
                                                return cells;
                                            }
                                        };
                                    }
                                } :
                                null
                        );
                    }
                },
                spreadsheetEnvironmentContext
            ),
            SpreadsheetSelection.ALL_CELLS,
            spreadsheetCell
        );
    }

    // loadLabel........................................................................................................

    @Test
    public void testLoadLabelMissingSpreadsheetId() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        this.loadLabelAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            SpreadsheetSelection.labelName("UnknownLabel")
        );
    }

    @Test
    public void testLoadLabelWithSpreadsheetIdEnvironmentContextValue() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(123);

        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final SpreadsheetLabelStore labels = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        final SpreadsheetLabelMapping mapping = labels.save(
            label.setLabelMappingReference(SpreadsheetSelection.A1)
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);

        this.loadLabelAndCheck(
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            spreadsheetId.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return new FakeSpreadsheetStoreRepository() {

                                            @Override
                                            public SpreadsheetLabelStore labels() {
                                                return labels;
                                            }
                                        };
                                    }
                                } :
                                null
                        );
                    }
                },
                spreadsheetEnvironmentContext
            ),
            label,
            mapping
        );
    }

    // spreadsheetMetadata..............................................................................................

    @Test
    public void testSpreadsheetMetadataMissingSpreadsheetId() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createContext(spreadsheetEnvironmentContext)
                .spreadsheetMetadata()
        );
    }

    @Test
    public void testSpreadsheetMetadataWithSpreadsheetIdEnvironmentContextValue() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(123);

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            spreadsheetId
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);

        this.checkEquals(
            metadata,
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            spreadsheetId.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public SpreadsheetMetadata spreadsheetMetadata() {
                                        return metadata;
                                    }
                                } :
                                null
                        );
                    }
                },
                spreadsheetEnvironmentContext
            ).spreadsheetMetadata()
        );
    }

    // nextEmptyColumn..................................................................................................

    @Test
    public void testNextEmptyColumnFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .nextEmptyColumn(
                    SpreadsheetSelection.A1.toRow()
                )
        );
    }

    // nextEmptyRow.....................................................................................................

    @Test
    public void testNextEmptyRowFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .nextEmptyRow(
                    SpreadsheetSelection.A1.toColumn()
                )
        );
    }

    // resolveLabel.....................................................................................................

    @Test
    public void testResolveLabelWhenMissingSpreadsheetId() {
        this.resolveIfLabelAndCheck(
            this.createContext(),
            SpreadsheetSelection.labelName("HelloLabel")
        );
    }

    @Test
    public void testResolveLabelWhenSpreadsheetIdAvailable() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("HelloLabel");
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(999);

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);

        this.resolveIfLabelAndCheck(
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            spreadsheetId.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return new FakeSpreadsheetStoreRepository() {
                                            @Override
                                            public SpreadsheetLabelStore labels() {
                                                final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
                                                store.save(
                                                    label.setLabelMappingReference(cell)
                                                );
                                                return store;
                                            }
                                        };
                                    }

                                } :
                                null
                        );
                    }
                },
                spreadsheetEnvironmentContext
            ),
            label,
            cell
        );
    }

    // setSpreadsheetId.................................................................................................

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    // spreadsheetFormatterContext......................................................................................

    @Test
    public void testSpreadsheetFormatterContextFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .spreadsheetFormatterContext(
                    SpreadsheetExpressionEvaluationContext.NO_CELL
                )
        );
    }

    // EnvironmentValueName.............................................................................................

    @Test
    public void testFireEnvironmentValueNameChangeWithRoundingMode() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final DecimalNumberContext decimalNumberContext = context.decimalNumberContext();

        RoundingMode roundingMode = RoundingMode.UP;
        if (roundingMode == decimalNumberContext.mathContext()
            .getRoundingMode()) {
            roundingMode = RoundingMode.CEILING;
        }

        this.setEnvironmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContextFactory.ROUNDING_MODE,
            roundingMode
        );

        final DecimalNumberContext decimalNumberContext2 = context.decimalNumberContext();

        assertNotSame(
            decimalNumberContext,
            decimalNumberContext2,
            "DecimalNumberCoontext should have been recreated with new RoundingMode"
        );

        this.checkEquals(
            roundingMode,
            decimalNumberContext2.mathContext()
                .getRoundingMode(),
            "DecimalNumberContext.roundingMode"
        );
    }

    // StorageContext...................................................................................................

    @Test
    public void testSaveStorageAndLoadStorage() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/path1/file2");

        final StorageValue value = StorageValue.with(
            path,
            Optional.of(111)
        );

        this.saveStorageAndCheck(
            context,
            value,
            value
        );

        this.loadStorageAndCheck(
            context,
            path,
            value
        );
    }

    @Test
    public void testSaveStorageDeleteStorageAndLoadStorage() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/path1/file2");

        final StorageValue value = StorageValue.with(
            path,
            Optional.of(111)
        );

        this.saveStorageAndCheck(
            context,
            value,
            value
        );

        context.deleteStorage(path);

        this.loadStorageAndCheck(
            context,
            path
        );
    }

    @Test
    public void testSaveStorageAndListStorage() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/file1"),
                Optional.of(111)
            )
        );

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/file2"),
                Optional.of(222)
            )
        );

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/file3"),
                Optional.of(333)
            )
        );

        this.listStorageAndCheck(
            context,
            StoragePath.ROOT,
            1, // skip first
            1,
            StorageValueInfo.with(
                StoragePath.parse("/file2"),
                context.createdAuditInfo()
            )
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext() {
        return this.createContext(
            SpreadsheetEnvironmentContexts.basic(
                Storages.tree(),
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            )
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
        );

        return createContext(
            spreadsheetEnvironmentContext,
            expressionFunctionProvider,
            ProviderContexts.fake()
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return createContext(
            spreadsheetEnvironmentContext,
            EXPRESSION_FUNCTION_PROVIDER,
            ProviderContexts.fake()
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                                    final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                                    final ProviderContext providerContext) {
        return this.createContext(
            SPREADSHEET_CONTEXT_SUPPLIER,
            spreadsheetEnvironmentContext,
            expressionFunctionProvider,
            providerContext
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                                                    final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            spreadsheetContextSupplier,
            spreadsheetEnvironmentContext,
            EXPRESSION_FUNCTION_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                                                    final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                                    final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                                    final ProviderContext providerContext) {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
            spreadsheetContextSupplier,
            LOCALE_CONTEXT,
            spreadsheetEnvironmentContext,
            new FakeSpreadsheetMetadataContext() {
                @Override
                public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
                    return this.store.load(id);
                }

                @Override
                public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
                    return this.store.save(metadata);
                }

                @Override
                public void deleteMetadata(final SpreadsheetId id) {
                    this.store.delete(id);
                }

                @Override
                public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                               final int offset,
                                                                               final int count) {
                    return this.store.findByName(
                        name,
                        offset,
                        count
                    );
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            TERMINAL_CONTEXT,
            SpreadsheetProviders.basic(
                CONVERTER_PROVIDER,
                expressionFunctionProvider,
                SpreadsheetComparatorProviders.fake(),
                SpreadsheetExporterProviders.fake(),
                SpreadsheetFormatterProviders.fake(),
                FormHandlerProviders.fake(),
                SpreadsheetImporterProviders.fake(),
                SPREADSHEET_PARSER_PROVIDER,
                ValidatorProviders.fake()
            ),
            providerContext
        );
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverter() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final ExpressionNumber from = context.expressionNumberKind()
            .create(123);
        final String to = context.convertOrFail(from, String.class);

        this.checkEquals(
            to,
            context.converter()
                .convertOrFail(
                    from,
                    String.class,
                    context
                ),
            () -> "converter with context and context convertOrFail should return the same"
        );
    }

    @Test
    public void testConverterConvertExpressionNumberToString() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            String.class,
            "123"
        );
    }


    @Test
    public void testConverterConvertStringToExpressionNumber() {
        this.convertAndCheck(
            "123",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testConverterConvertStringToExpressionNumber2() {
        this.convertAndCheck(
            "123.5",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_DIGIT_COUNT;
    }
    // Class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext> type() {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.class;
    }
}
