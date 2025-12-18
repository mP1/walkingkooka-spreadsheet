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

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.CanConvertDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

abstract class SpreadsheetEngineContextShared implements SpreadsheetEngineContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    CanConvertDelegator,
    SpreadsheetProviderDelegator {

    SpreadsheetEngineContextShared() {
        super();
    }

    @Override
    public final SpreadsheetEngineContext spreadsheetEngineContext() {
        return this;
    }

    @Override
    public final Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
        Objects.requireNonNull(token, "token");

        return token.toExpression(
            this.spreadsheetExpressionEvaluationContext(
                NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake() // toExpression never loads references
            )
        );
    }

    @Override
    public final boolean isPure(final ExpressionFunctionName function) {
        return this.expressionFunction(
            function,
            Lists.empty(),
            this.providerContext()
        ).isPure(this);
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public final SpreadsheetEngineContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.environmentContext()
                .cloneEnvironment()
        );
    }

    @Override
    public final <T> SpreadsheetEngineContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                  final T value) {
        this.environmentContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    public final SpreadsheetEngineContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    public final AbsoluteUrl serverUrl() {
        return this.environmentContext()
            .environmentValueOrFail(SERVER_URL);
    }

    @Override
    public final LineEnding lineEnding() {
        return this.environmentContext()
            .lineEnding();
    }

    @Override
    public final SpreadsheetEngineContext setLineEnding(final LineEnding lineEnding) {
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
    public final SpreadsheetEngineContext setLocale(final Locale locale) {
        this.environmentContext()
            .setLocale(locale);
        return this;
    }

    @Override
    public final SpreadsheetEngineContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext()
            .setUser(user);
        return this;
    }

    @Override
    abstract public EnvironmentContext environmentContext();
}
