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
import walkingkooka.ContextTesting;
import walkingkooka.locale.HasLocaleTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviderTesting;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderTesting;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting<C extends SpreadsheetEngineContext> extends ContextTesting<C>,
        ParserTesting,
        HasLocaleTesting,
        ExpressionFunctionProviderTesting<C>,
        SpreadsheetComparatorProviderTesting<C>,
        SpreadsheetLabelNameResolverTesting {

    // SpreadsheetLabelNameResolverTesting..............................................................................

    @Override
    default SpreadsheetLabelNameResolver spreadsheetLabelNameResolver() {
        return this.createContext();
    }


    // parseFormula......................................................................................................

    @Test
    default void testParseFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().parseFormula(null));
    }

    default void parseFormulaAndCheck(final String expression,
                                      final SpreadsheetParserToken expected) {
        this.parseFormulaAndCheck(this.createContext(),
                expression,
                expected);
    }

    default void parseFormulaAndCheck(final SpreadsheetEngineContext context,
                                      final String formula,
                                      final SpreadsheetParserToken expected) {
        this.checkEquals(
                expected,
                context.parseFormula(
                        TextCursors.charSequence(formula)
                ),
                () -> "parseFormula " + formula + " with context " + context);
    }

    // toExpression....................................................................................................

    @Test
    default void testToExpressionWithNullTokenFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .toExpression(null)
        );
    }

    default void toExpressionAndCheck(final SpreadsheetEngineContext context,
                                      final SpreadsheetParserToken token) {
        this.toExpressionAndCheck(
                context,
                token,
                Optional.empty()
        );
    }

    default void toExpressionAndCheck(final SpreadsheetEngineContext context,
                                      final SpreadsheetParserToken token,
                                      final Expression expected) {
        this.toExpressionAndCheck(
                context,
                token,
                Optional.of(expected)
        );
    }

    default void toExpressionAndCheck(final SpreadsheetEngineContext context,
                                      final SpreadsheetParserToken token,
                                      final Optional<Expression> expected) {
        this.checkEquals(
                expected,
                context.toExpression(token),
                () -> token.toString()
        );
    }

    // evaluate.........................................................................................................

    @Test
    default void testEvaluateNullExpressionFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().evaluate(null, Optional.empty()));
    }

    @Test
    default void testEvaluateNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .evaluate(
                                Expression.value("required expression"),
                                null
                        )
        );
    }

    default void evaluateAndCheck(final Expression expression,
                                  final Object expected) {
        this.evaluateAndCheck(
                this.createContext(),
                expression,
                expected
        );
    }

    default void evaluateAndCheck(final SpreadsheetEngineContext context,
                                  final Expression expression,
                                  final Object expected) {
        this.evaluateAndCheck(
                context,
                expression,
                Optional.empty(),
                expected
        );
    }

    default void evaluateAndCheck(final SpreadsheetEngineContext context,
                                  final Expression expression,
                                  final Optional<SpreadsheetCell> cell,
                                  final Object expected) {
        this.checkEquals(
                expected,
                context.evaluate(expression, cell),
                () -> "evaluate " + expression + cell.map(c -> " " + c).orElse("") + " with context " + context
        );
    }

    // evaluateAsBoolean................................................................................................

    default void evaluateAsBooleanAndCheck(final SpreadsheetEngineContext context,
                                           final Expression expression,
                                           final boolean expected) {
        this.evaluateAsBooleanAndCheck(
                context,
                expression,
                Optional.empty(),
                expected
        );
    }

    default void evaluateAsBooleanAndCheck(final SpreadsheetEngineContext context,
                                           final Expression expression,
                                           final Optional<SpreadsheetCell> cell,
                                           final boolean expected) {
        this.checkEquals(
                expected,
                context.evaluateAsBoolean(
                        expression,
                        cell
                ),
                () -> "evaluateAsBoolean " + expression + cell.map(c -> " " + c).orElse("") + " with context " + context
        );
    }

    // formatValue......................................................................................................

    @Test
    default void testFormatValueNullFormatterFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .formatValue(
                                "1",
                                null
                        )
        );
    }

    default void formatValueAndCheck(final Object value,
                                     final SpreadsheetFormatter formatter,
                                     final SpreadsheetText expected) {
        this.formatValueAndCheck(
                value,
                formatter,
                expected.toTextNode()
        );
    }

    default void formatValueAndCheck(final Object value,
                                     final SpreadsheetFormatter formatter,
                                     final TextNode expected) {
        this.formatValueAndCheck(
                value,
                formatter,
                Optional.of(expected)
        );
    }

    default void formatValueAndCheck(final Object value,
                                     final SpreadsheetFormatter formatter,
                                     final Optional<TextNode> expected) {
        this.formatValueAndCheck(
                this.createContext(),
                value,
                formatter,
                expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final Object value,
                                     final SpreadsheetFormatter formatter,
                                     final TextNode expected) {
        this.formatValueAndCheck(
                context,
                value,
                formatter,
                Optional.of(expected)
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final Object value,
                                     final SpreadsheetFormatter formatter,
                                     final Optional<TextNode> expected) {
        this.checkEquals(
                expected,
                context.formatValue(value, formatter),
                () -> "formatValue " + CharSequences.quoteIfChars(value) + " " + formatter
        );
    }

    // format...........................................................................................................

    @Test
    default void testFormatAndStyleNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .formatValueAndStyle(
                                null,
                                Optional.empty() // no formatter
                        )
        );
    }

    @Test
    default void testFormatAndStyleNullFormatterFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .formatValueAndStyle(
                                SpreadsheetSelection.A1.setFormula(
                                        SpreadsheetFormula.EMPTY
                                ),
                                null
                        )
        );
    }

    default void formatAndStyleAndCheck(final SpreadsheetCell cell,
                                        final SpreadsheetFormatter formatter,
                                        final SpreadsheetCell expected) {
        this.formatAndStyleAndCheck(
                this.createContext(),
                cell,
                formatter,
                expected
        );
    }

    default void formatAndStyleAndCheck(final SpreadsheetEngineContext context,
                                        final SpreadsheetCell cell,
                                        final SpreadsheetFormatter formatter,
                                        final SpreadsheetCell expected) {
        this.formatAndStyleAndCheck(
                context,
                cell,
                Optional.of(
                        formatter
                ),
                expected
        );
    }

    default void formatAndStyleAndCheck(final SpreadsheetEngineContext context,
                                        final SpreadsheetCell cell,
                                        final Optional<SpreadsheetFormatter> formatter,
                                        final SpreadsheetCell expected) {
        this.checkEquals(
                expected,
                context.formatValueAndStyle(
                        cell,
                        formatter
                ),
                () -> "formatValueAndStyle " + cell + " " + formatter);
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }

    // SpreadsheetComparatorProvider....................................................................................

    @Override
    default C createSpreadsheetComparatorProvider() {
        return this.createContext();
    }

    // ExpressionFunctionProvider.......................................................................................

    @Override
    default C createExpressionFunctionProvider() {
        return this.createContext();
    }
}
