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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

abstract class SpreadsheetExpressionEvaluationContextShared implements SpreadsheetExpressionEvaluationContext,
    SpreadsheetEnvironmentContextDelegator,
    SpreadsheetConverterContextDelegator,
    TerminalContextDelegator {

    SpreadsheetExpressionEvaluationContextShared(final TerminalContext terminalContext) {
        super();

        this.terminalContext = terminalContext;
    }

    @Override
    public final SpreadsheetFormulaParserToken parseExpression(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        return this.parseOrFail(
            expression,
            SpreadsheetFormulaParsers.expression()
        );
    }

    @Override
    public final SpreadsheetFormulaParserToken parseValueOrExpression(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        return this.parseOrFail(
            expression,
            SpreadsheetFormulaParsers.valueOrExpression(this.spreadsheetParser())
        );
    }

    private SpreadsheetFormulaParserToken parseOrFail(final TextCursor expression,
                                                      final SpreadsheetParser parser) {

        final SpreadsheetParserContext parserContext = this.spreadsheetParserContext();

        return parser.orReport(ParserReporters.basic())
            .parse(expression, parserContext)
            .get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    abstract SpreadsheetParser spreadsheetParser();

    abstract SpreadsheetParserContext spreadsheetParserContext();

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return Cast.to(
            this.expressionFunctionProvider()
                .expressionFunction(
                    name,
                    Lists.empty(),
                    this.providerContext() // ProviderContext
                )
        );
    }

    abstract ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider();

    abstract ProviderContext providerContext();

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        return this.expressionFunction(name)
            .isPure(this);
    }

    @Override
    public final <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                        final Object value) {
        return parameter.convertOrFail(
            value,
            this
        );
    }

    @Override
    public final Object handleException(final RuntimeException exception) {
        return SpreadsheetErrorKind.translate(exception);
    }

    /**
     * Resolves several types of {@link ExpressionReference} into values.
     * <ul>
     * <li>Resolves {@link SpreadsheetLabelName} to a {@link SpreadsheetCell} returning its value.</li>
     * <li>Loads a {@link SpreadsheetCell} to a {@link SpreadsheetCell} returning its value.</li>
     * <li>For {@link EnvironmentValueName} loads the {@link EnvironmentContext#environmentValue(EnvironmentValueName)}.</li>
     * </ul>
     */
    @Override
    public final Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        Optional<Optional<Object>> value = Optional.empty();

        final Set<Object> cycle = Sets.ordered();

        Object temp = reference;

        while (temp instanceof ExpressionReference) {
            // cycle detection
            if (false == cycle.add(temp)) {
                throw new IllegalArgumentException("Cycle detected from " + reference + " with " + temp);
            }

            if (temp instanceof SpreadsheetExpressionReference) {
                SpreadsheetExpressionReference spreadsheetExpressionReference = (SpreadsheetExpressionReference) temp;
                final SpreadsheetSelection selection = this.resolveIfLabel(
                    (ExpressionReference) temp
                ).orElse(null);

                if (null != selection) {
                    spreadsheetExpressionReference = selection.toExpressionReference();
                }
                if (spreadsheetExpressionReference instanceof SpreadsheetExpressionReference) {
                    value = this.handleSpreadsheetExpressionReference(spreadsheetExpressionReference);
                }
            } else {
                if (temp instanceof EnvironmentValueName) {
                    value = Optional.ofNullable(
                        Cast.to(
                            this.environmentValue(
                                (EnvironmentValueName<?>) temp
                            )
                        )
                    );
                }
            }

            temp = value.map(v -> v.orElse(null))
                .orElse(null);
        }

        return value;
    }

    abstract Optional<Optional<Object>> handleSpreadsheetExpressionReference(final SpreadsheetExpressionReference reference);

    // TerminalContextDelegator.........................................................................................

    @Override
    public final TerminalContext terminalContext() {
        return this.terminalContext;
    }

    final TerminalContext terminalContext;

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    public final Optional<StoragePath> currentWorkingDirectory() {
        return this.spreadsheetEnvironmentContext()
            .currentWorkingDirectory();
    }

    @Override
    public final void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        this.spreadsheetEnvironmentContext()
            .setCurrentWorkingDirectory(currentWorkingDirectory);
    }

    @Override
    public final Indentation indentation() {
        return this.spreadsheetEnvironmentContext()
            .indentation();
    }
    
    @Override
    public final LineEnding lineEnding() {
        return this.spreadsheetEnvironmentContext()
            .lineEnding();
    }

    @Override
    public final Locale locale() {
        return this.spreadsheetEnvironmentContext()
            .locale();
    }

    @Override
    public final void setLocale(final Locale locale) {
        this.spreadsheetEnvironmentContext()
            .setLocale(locale);
    }

    @Override
    public final LocalDateTime now() {
        return this.spreadsheetEnvironmentContext()
            .now(); // inherit unrelated defaults
    }

    @Override
    public final SpreadsheetEnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext();
    }

    @Override
    public abstract SpreadsheetEnvironmentContext spreadsheetEnvironmentContext();

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public final Optional<StorageValue> loadStorage(final StoragePath path) {
        return this.storage()
            .load(
                path,
                this.spreadsheetStorageContext()
            );
    }

    @Override
    public final StorageValue saveStorage(final StorageValue value) {
        return this.storage()
            .save(
                value,
                this.spreadsheetStorageContext()
            );
    }

    @Override
    public final void deleteStorage(final StoragePath path) {
        this.storage()
            .delete(
                path,
                this.spreadsheetStorageContext()
            );
    }

    @Override
    public final List<StorageValueInfo> listStorage(final StoragePath parent,
                                                    final int offset,
                                                    final int count) {
        return this.storage()
            .list(
                parent,
                offset,
                count,
                this.spreadsheetStorageContext()
            );
    }

    @Override
    public final Storage<SpreadsheetStorageContext> storage() {
        return this.environmentContext()
            .storage();
    }

    abstract SpreadsheetStorageContext spreadsheetStorageContext();
}
