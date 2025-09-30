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

import walkingkooka.convert.CanConvert;
import walkingkooka.convert.CanConvertDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.text.TextNode;

import java.util.Locale;
import java.util.Optional;

/**
 * A {@link SpreadsheetEngineContext} used by {@link BasicSpreadsheetEngine} delegating all methods except for the evaluateXXX
 * methods using an existing
 */
final class BasicSpreadsheetEngineSpreadsheetEngineContext implements SpreadsheetEngineContext,
    SpreadsheetContextDelegator,
    CanConvertDelegator {

    static BasicSpreadsheetEngineSpreadsheetEngineContext with(final SpreadsheetEngineContext spreadsheetEngineContext,
                                                               final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext) {
        return new BasicSpreadsheetEngineSpreadsheetEngineContext(
            spreadsheetEngineContext,
            spreadsheetExpressionEvaluationContext
        );
    }

    private BasicSpreadsheetEngineSpreadsheetEngineContext(final SpreadsheetEngineContext spreadsheetEngineContext,
                                                           final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext) {
        this.spreadsheetEngineContext = spreadsheetEngineContext;
        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext;
    }

    // SpreadsheetEngineContext.........................................................................................

    @Override
    public AbsoluteUrl serverUrl() {
        return this.spreadsheetEngineContext.serverUrl();
    }

    @Override
    public SpreadsheetEngineContext setSpreadsheetEngineContextMode(final SpreadsheetEngineContextMode mode) {
        return with(
            this.spreadsheetEngineContext.setSpreadsheetEngineContextMode(mode),
            this.spreadsheetExpressionEvaluationContext
        );
    }

    private final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext;

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                                      final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetEngineContext.parseFormula(
            formula,
            cell
        );
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
        return this.spreadsheetEngineContext.toExpression(token);
    }

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
        return this.spreadsheetEngineContext.spreadsheetExpressionEvaluationContext(
            cell,
            loader
        );
    }

    @Override
    public Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                          final Optional<Object> value,
                                          final Optional<SpreadsheetFormatterSelector> formatter) {
        return this.spreadsheetEngineContext.formatValue(
            cell,
            value,
            formatter
        );
    }

    @Override
    public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                               final Optional<SpreadsheetFormatterSelector> formatter) {
        return this.spreadsheetEngineContext.formatValueAndStyle(
            cell,
            formatter
        );
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetEngineContext.spreadsheetMetadata();
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetEngineContext.resolveLabel(labelName);
    }

    @Override
    public boolean isPure(final ExpressionFunctionName expressionFunctionName) {
        return this.spreadsheetEngineContext.isPure(expressionFunctionName);
    }

    // CanConvertDelegator..............................................................................................

    @Override
    public CanConvert canConvert() {
        return this.spreadsheetEngineContext;
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetEngineContext setUser(final Optional<EmailAddress> user) {
        this.spreadsheetEngineContext.setUser(user);
        return this;
    }

    @Override
    public SpreadsheetEngineContext cloneEnvironment() {
        final SpreadsheetEngineContext spreadsheetEngineContext = this.spreadsheetEngineContext;
        final SpreadsheetEngineContext spreadsheetEngineContextClone = spreadsheetEngineContext.cloneEnvironment();

        final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext = this.spreadsheetExpressionEvaluationContext;
        final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContextClone = spreadsheetExpressionEvaluationContext.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return spreadsheetEngineContext == spreadsheetEngineContextClone &&
            spreadsheetExpressionEvaluationContext == spreadsheetExpressionEvaluationContextClone ?
            this :
            with(
                spreadsheetEngineContextClone,
                spreadsheetExpressionEvaluationContextClone
            );
    }

    @Override
    public <T> SpreadsheetEngineContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                            final T value) {
        this.spreadsheetEngineContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetEngineContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.spreadsheetEngineContext.removeEnvironmentValue(name);
        return this;
    }

    // SpreadsheetContextDelegator......................................................................................

    @Override
    public SpreadsheetEngineContext setLocale(final Locale locale) {
        this.spreadsheetEngineContext.setLocale(locale);
        return this;
    }

    @Override
    public SpreadsheetContext spreadsheetContext() {
        return this.spreadsheetEngineContext;
    }

    private final SpreadsheetEngineContext spreadsheetEngineContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetEngineContext.toString();
    }
}
