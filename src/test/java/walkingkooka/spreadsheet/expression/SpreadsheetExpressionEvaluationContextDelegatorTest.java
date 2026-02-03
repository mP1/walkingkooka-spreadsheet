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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.FakeSpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class SpreadsheetExpressionEvaluationContextDelegatorTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContextDelegatorTest.TestSpreadsheetExpressionEvaluationContextDelegator>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadCellWithNullCellFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadCellRangeWithNullRangeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadLabelWithNullLabelFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCellWithNullCellFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCellWithSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSpreadsheetFormatterContextWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetExpressionEvaluationContextDelegator createContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator();
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
            .decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
            .mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator();
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetExpressionEvaluationContextDelegator> type() {
        return TestSpreadsheetExpressionEvaluationContextDelegator.class;
    }

    final static class TestSpreadsheetExpressionEvaluationContextDelegator implements SpreadsheetExpressionEvaluationContextDelegator {

        @Override
        public SpreadsheetExpressionEvaluationContextDelegator setCell(final Optional<SpreadsheetCell> cell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetCell> cell() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Optional<Object>> reference(final ExpressionReference reference) {
            return this.expressionEvaluationContext().reference(reference);
        }

        @Override
        public SpreadsheetExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
            Objects.requireNonNull(scoped, "scoped");

            return new TestSpreadsheetExpressionEvaluationContextDelegator();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext() {
            final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
            spreadsheetEnvironmentContext.setSpreadsheetId(
                Optional.of(spreadsheetId)
            );

            return SpreadsheetExpressionEvaluationContexts.spreadsheetContext(
                SpreadsheetMetadataMode.FORMULA,
                SpreadsheetExpressionEvaluationContext.NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SpreadsheetContexts.fixedSpreadsheetId(
                    SpreadsheetEngines.basic(),
                    new FakeSpreadsheetStoreRepository() {
                        @Override
                        public SpreadsheetMetadataStore metadatas() {
                            return new FakeSpreadsheetMetadataStore() {
                                @Override
                                public Optional<SpreadsheetMetadata> load(final SpreadsheetId id) {
                                    return Optional.ofNullable(
                                        id.equals(spreadsheetId) ?
                                            METADATA_EN_AU.set(
                                                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                                                spreadsheetId
                                            ) :
                                            null
                                    );
                                }
                            };
                        }
                    },
                    (c) -> {
                        throw new UnsupportedOperationException();
                    }, // Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory
                    spreadsheetEnvironmentContext,
                    LOCALE_CONTEXT,
                    SPREADSHEET_PROVIDER,
                    PROVIDER_CONTEXT
                ),
                TERMINAL_CONTEXT
            );
        }

        @Override
        public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
            Objects.requireNonNull(labelName, "labelName");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContextDelegatorTest.TestSpreadsheetExpressionEvaluationContextDelegator cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestSpreadsheetExpressionEvaluationContextDelegator setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new TestSpreadsheetExpressionEvaluationContextDelegator();
        }

        @Override
        public Optional<StorageValue> loadStorage(final StoragePath path) {
            return this.storage.load(
                path,
                StorageContexts.fake()
            );
        }

        @Override
        public StorageValue saveStorage(final StorageValue value) {
            return this.storage.save(
                value,
                StorageContexts.fake()
            );
        }

        @Override
        public void deleteStorage(final StoragePath path) {
            this.storage.delete(
                path,
                StorageContexts.fake()
            );
        }

        @Override
        public List<StorageValueInfo> listStorage(final StoragePath parent,
                                                  final int offset,
                                                  final int count) {
            return this.storage.list(
                parent,
                offset,
                count,
                StorageContexts.fake()
            );
        }

        private final Storage<StorageContext> storage = Storages.tree();

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
