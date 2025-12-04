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

import walkingkooka.Context;
import walkingkooka.convert.CanConvert;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.HasMissingCellNumberValue;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.text.TextNode;

import java.util.Locale;
import java.util.Optional;

/**
 * A context that holds individual values tailored for an individual spreadsheet or user such as their locale,
 * formatting characters such as the selected decimal point and other similar data. The {@link SpreadsheetEngine} itself
 * will refer to this context for these values when it performs its spreadsheet operations such as evaluating a cell,
 * formatting etc. This supports the idea of using a single {@link SpreadsheetEngine} that is shared amongst users and
 * spreadsheets and only the {@link SpreadsheetEngineContext} is different, for circumstances such as those previously
 * mentioned.
 */
public interface SpreadsheetEngineContext extends Context,
    CanConvert,
    ExpressionPurityContext,
    SpreadsheetContext,
    SpreadsheetLabelNameResolver,
    HasMissingCellNumberValue {

    /**
     * Useful constant for some members that require a {@link SpreadsheetCell}.
     */
    Optional<SpreadsheetCell> NO_CELL = Optional.empty();

    /**
     * Returns a {@link SpreadsheetEngineContext} which will use the {@link SpreadsheetEngineContextMode} when fetching functions
     * and a converter.
     */
    SpreadsheetEngineContext setSpreadsheetEngineContextMode(final SpreadsheetEngineContextMode mode);

    // parseFormula.....................................................................................................

    /**
     * Parses the formula into an {@link SpreadsheetFormulaParserToken} which can then be transformed into an {@link Expression}.
     */
    SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                               final Optional<SpreadsheetCell> cell);

    // toExpression.....................................................................................................

    /**
     * Helps by converting the given {@link SpreadsheetFormulaParserToken} into its {@link Expression} equivalent.
     */
    Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token);

    // spreadsheetExpressionEvaluationContext...........................................................................

    /**
     * Returns a {@link SpreadsheetExpressionEvaluationContext} that may be used to evaluate an {@link Expression} using
     * the given {@link SpreadsheetCell} as the current cell.
     */
    SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                  final SpreadsheetExpressionReferenceLoader loader);

    // Formatting & SpreadsheetFormatterProvider........................................................................

    /**
     * Formats the given value using the provided formatter.
     */
    Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                   final Optional<Object> value,
                                   final Optional<SpreadsheetFormatterSelector> formatter);

    /**
     * Combines formatting of any present value along with possibly applying conditional rules.
     */
    SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                        final Optional<SpreadsheetFormatterSelector> formatter);

    // HasMissingCellNumberValue........................................................................................

    @Override
    default ExpressionNumber missingCellNumberValue() {
        return this.spreadsheetMetadata()
            .missingCellNumberValue();
    }

    // SpreadsheetContext...............................................................................................

    /**
     * {@see SpreadsheetContext#setSpreadsheetId}
     */
    @Override
    SpreadsheetEngineContext setSpreadsheetId(final SpreadsheetId spreadsheetId);

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetEngineContext cloneEnvironment();

    @Override
    SpreadsheetEngineContext setEnvironmentContext(final EnvironmentContext environmentContext);

    @Override
    SpreadsheetEngineContext setLineEnding(final LineEnding lineEnding);

    @Override
    SpreadsheetEngineContext setLocale(final Locale locale);

    @Override
    SpreadsheetEngineContext setUser(final Optional<EmailAddress> user);

    @Override
    <T> SpreadsheetEngineContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                     final T value);

    @Override
    SpreadsheetEngineContext removeEnvironmentValue(final EnvironmentValueName<?> name);
}
