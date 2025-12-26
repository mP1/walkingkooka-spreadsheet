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
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

abstract class SpreadsheetExpressionEvaluationContextShared implements SpreadsheetExpressionEvaluationContext,
    EnvironmentContextDelegator,
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
    public final SpreadsheetExpressionEvaluationContext exitTerminal() {
        this.terminalContext.exitTerminal();
        return this;
    }

    @Override
    public final TerminalContext terminalContext() {
        return this.terminalContext;
    }

    final TerminalContext terminalContext;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public final <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                                final T value) {
        this.environmentContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    public final SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    public final LineEnding lineEnding() {
        return this.environmentContext()
            .lineEnding();
    }

    @Override
    public final SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext()
            .setLineEnding(lineEnding);
        return this;
    }

    @Override
    public final Locale locale() {
        return this.environmentContext()
            .locale();
    }

    @Override
    public final SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
        this.environmentContext()
            .setLocale(locale);
        return this;
    }

    @Override
    public final LocalDateTime now() {
        return this.environmentContext()
            .now(); // inherit unrelated defaults
    }

    @Override
    public final AbsoluteUrl serverUrl() {
        return this.environmentContext()
            .serverUrl();
    }

    @Override
    public final SpreadsheetId spreadsheetId() {
        return this.environmentContext()
            .spreadsheetId();
    }

    @Override
    public final SpreadsheetExpressionEvaluationContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.environmentContext()
            .setSpreadsheetId(spreadsheetId);
        return this;
    }

    @Override
    public final Optional<EmailAddress> user() {
        return this.environmentContext()
            .user();
    }

    @Override
    public final SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext()
            .setUser(user);
        return this;
    }

    @Override
    public abstract SpreadsheetEnvironmentContext environmentContext();
}
