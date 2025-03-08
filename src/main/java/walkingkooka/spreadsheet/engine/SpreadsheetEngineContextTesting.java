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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting<C extends SpreadsheetEngineContext> extends ContextTesting<C>,
        ParserTesting,
        HasLocaleTesting,
        SpreadsheetProviderTesting<C>,
        SpreadsheetLabelNameResolverTesting<C> {

    // SpreadsheetLabelNameResolverTesting..............................................................................

    @Override
    default C createSpreadsheetLabelNameResolver() {
        return this.createContext();
    }

    // serverUrl........................................................................................................

    default void serverUrlAndCheck(final C context,
                                   final AbsoluteUrl expected) {
        this.checkEquals(
                expected,
                context.serverUrl()
        );
    }

    // parseFormula......................................................................................................

    @Test
    default void testParseFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().parseFormula(null));
    }

    default void parseFormulaAndCheck(final String expression,
                                      final SpreadsheetFormulaParserToken expected) {
        this.parseFormulaAndCheck(this.createContext(),
                expression,
                expected);
    }

    default void parseFormulaAndCheck(final SpreadsheetEngineContext context,
                                      final String formula,
                                      final SpreadsheetFormulaParserToken expected) {
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
                                      final SpreadsheetFormulaParserToken token) {
        this.toExpressionAndCheck(
                context,
                token,
                Optional.empty()
        );
    }

    default void toExpressionAndCheck(final SpreadsheetEngineContext context,
                                      final SpreadsheetFormulaParserToken token,
                                      final Expression expected) {
        this.toExpressionAndCheck(
                context,
                token,
                Optional.of(expected)
        );
    }

    default void toExpressionAndCheck(final SpreadsheetEngineContext context,
                                      final SpreadsheetFormulaParserToken token,
                                      final Optional<Expression> expected) {
        this.checkEquals(
                expected,
                context.toExpression(token),
                token::toString
        );
    }

    // SpreadsheetEngineContext.........................................................................................

    @Test
    default void testSpreadsheetEngineContextWithNullFunctionAliasesFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .spreadsheetEngineContext(null)
        );
    }

    default void spreadsheetEngineContextAndCheck(final SpreadsheetEngineContext context,
                                                  final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> aliases,
                                                  final SpreadsheetEngineContext expected) {
        this.checkEquals(
                expected,
                context.spreadsheetEngineContext(aliases),
                () -> "expressionEvaluationContext " + aliases
        );
    }

    // spreadsheetExpressionEvaluationContext...........................................................................

    @Test
    default void testSpreadsheetExpressionEvaluationContextWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .spreadsheetExpressionEvaluationContext(
                                null,
                                SpreadsheetExpressionReferenceLoaders.fake()
                        )
        );
    }

    @Test
    default void testSpreadsheetExpressionEvaluationContextWithNullSpreadsheetExpressionReferenceLoaderFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .spreadsheetExpressionEvaluationContext(
                                Optional.empty(), // cell
                                null
                        )
        );
    }

    // evaluate.........................................................................................................

    default void evaluateAndCheck(final Expression expression,
                                  final Object expected) {
        this.evaluateAndCheck(
                expression,
                SpreadsheetExpressionReferenceLoaders.fake(),
                expected
        );
    }

    default void evaluateAndCheck(final Expression expression,
                                  final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                  final Object expected) {
        this.evaluateAndCheck(
                this.createContext(),
                expression,
                spreadsheetExpressionReferenceLoader,
                expected
        );
    }

    default void evaluateAndCheck(final SpreadsheetEngineContext context,
                                  final Expression expression,
                                  final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                  final Object expected) {
        this.evaluateAndCheck(
                context,
                expression,
                Optional.empty(),
                spreadsheetExpressionReferenceLoader,
                expected
        );
    }

    default void evaluateAndCheck(final SpreadsheetEngineContext context,
                                  final SpreadsheetCell cell,
                                  final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                  final Object expected) {
        final Expression expression = cell.formula()
                .expression()
                .orElse(null);
        this.evaluateAndCheck(
                context,
                expression,
                Optional.of(cell),
                spreadsheetExpressionReferenceLoader,
                expected
        );
    }

    default void evaluateAndCheck(final SpreadsheetEngineContext context,
                                  final Expression expression,
                                  final Optional<SpreadsheetCell> cell,
                                  final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                  final Object expected) {
        this.checkEquals(
                expected,
                expression.toValue(
                        context.spreadsheetExpressionEvaluationContext(
                                cell,
                                spreadsheetExpressionReferenceLoader
                        )
                ),
                () -> "evaluate " + expression + cell.map(c -> " " + c).orElse("") + " with context " + context
        );
    }

    // formatValue......................................................................................................

    @Test
    default void testFormatValueNullFormatterFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .formatValue(
                                Optional.of("1"),
                                null
                        )
        );
    }

    default void formatValueAndCheck(final Object value,
                                     final SpreadsheetFormatter formatter,
                                     final SpreadsheetText expected) {
        this.formatValueAndCheck(
                Optional.of(value),
                formatter,
                expected
        );
    }

    default void formatValueAndCheck(final Optional<Object> value,
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
                Optional.of(value),
                formatter,
                expected
        );
    }

    default void formatValueAndCheck(final Optional<Object> value,
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
                Optional.of(value),
                formatter,
                expected
        );
    }

    default void formatValueAndCheck(final Optional<Object> value,
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
                Optional.of(value),
                formatter,
                expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final Optional<Object> value,
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
                                     final Optional<Object> value,
                                     final SpreadsheetFormatter formatter,
                                     final Optional<TextNode> expected) {
        this.checkEquals(
                expected,
                context.formatValue(
                        value,
                        formatter
                ),
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

    // TypeNameTesting .................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }

    // SpreadsheetComparatorProvider....................................................................................

    @Override
    default C createSpreadsheetComparatorProvider() {
        return this.createContext();
    }

    // SpreadsheetFormatterProvider.....................................................................................

    @Override
    default C createSpreadsheetFormatterProvider() {
        return this.createContext();
    }

    // ExpressionFunctionProvider.......................................................................................

    @Override
    default C createExpressionFunctionProvider() {
        return this.createContext();
    }

    // SpreadsheetParserProvider........................................................................................

    @Override
    default C createSpreadsheetParserProvider() {
        return this.createContext();
    }
}
