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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetContextTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting<C extends SpreadsheetEngineContext> extends ContextTesting<C>,
    SpreadsheetContextTesting<C>,
    ParserTesting,
    SpreadsheetLabelNameResolverTesting<C>,
    SpreadsheetProviderTesting<C> {

    // SpreadsheetLabelNameResolverTesting..............................................................................

    @Override
    default C createSpreadsheetLabelNameResolver() {
        return this.createContext();
    }

    // parseFormula......................................................................................................

    @Test
    default void testParseFormulaNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseFormula(
                    null,
                    SpreadsheetEngineContext.NO_CELL
                )
        );
    }

    @Test
    default void testParseFormulaNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseFormula(
                    TextCursors.fake(),
                    null
                )
        );
    }

    default void parseFormulaAndCheck(final String expression,
                                      final SpreadsheetFormulaParserToken expected) {
        this.parseFormulaAndCheck(
            expression,
            SpreadsheetEngineContext.NO_CELL,
            expected
        );
    }

    default void parseFormulaAndCheck(final String expression,
                                      final Optional<SpreadsheetCell> cell,
                                      final SpreadsheetFormulaParserToken expected) {
        this.parseFormulaAndCheck(
            this.createContext(),
            expression,
            cell,
            expected
        );
    }

    default void parseFormulaAndCheck(final SpreadsheetEngineContext context,
                                      final String formula,
                                      final SpreadsheetFormulaParserToken expected) {
        this.parseFormulaAndCheck(
            context,
            formula,
            SpreadsheetEngineContext.NO_CELL,
            expected
        );
    }

    default void parseFormulaAndCheck(final SpreadsheetEngineContext context,
                                      final String formula,
                                      final Optional<SpreadsheetCell> cell,
                                      final SpreadsheetFormulaParserToken expected) {
        this.checkEquals(
            expected,
            context.parseFormula(
                TextCursors.charSequence(formula),
                cell
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

    // setSpreadsheetEngineContextMode..................................................................................

    @Test
    default void testSetSpreadsheetEngineContextModeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setSpreadsheetEngineContextMode(null)
        );
    }

    default void setSpreadsheetEngineContextModeAndCheck(final SpreadsheetEngineContext context,
                                                         final SpreadsheetEngineContextMode mode,
                                                         final SpreadsheetEngineContext expected) {
        this.checkEquals(
            expected,
            context.setSpreadsheetEngineContextMode(mode),
            () -> "setSpreadsheetEngineContextMode " + mode
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
                    SpreadsheetEngineContext.NO_CELL,
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
    default void testFormatValueNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .formatValue(
                    null,
                    Optional.of("1"),
                    SpreadsheetEngineContext.NO_SPREADSHEET_FORMATTER_SELECTOR
                )
        );
    }

    @Test
    default void testFormatValueNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .formatValue(
                    SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                    null,
                    SpreadsheetEngineContext.NO_SPREADSHEET_FORMATTER_SELECTOR
                )
        );
    }

    @Test
    default void testFormatValueNullSpreadsheetFormatterSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .formatValue(
                    SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                    Optional.of("1"),
                    null
                )
        );
    }

    default void formatValueAndCheck(final SpreadsheetCell cell,
                                     final Object value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final SpreadsheetText expected) {
        this.formatValueAndCheck(
            cell,
            Optional.of(value),
            formatter,
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetCell cell,
                                     final Optional<Object> value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final SpreadsheetText expected) {
        this.formatValueAndCheck(
            cell,
            value,
            formatter,
            expected.textNode()
        );
    }

    default void formatValueAndCheck(final SpreadsheetCell cell,
                                     final Object value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final TextNode expected) {
        this.formatValueAndCheck(
            cell,
            Optional.of(value),
            formatter,
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetCell cell,
                                     final Optional<Object> value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final TextNode expected) {
        this.formatValueAndCheck(
            cell,
            value,
            formatter,
            Optional.of(expected)
        );
    }

    default void formatValueAndCheck(final SpreadsheetCell cell,
                                     final Object value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final Optional<TextNode> expected) {
        this.formatValueAndCheck(
            cell,
            Optional.of(value),
            formatter,
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetCell cell,
                                     final Optional<Object> value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final Optional<TextNode> expected) {
        this.formatValueAndCheck(
            this.createContext(),
            cell,
            value,
            formatter,
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final SpreadsheetCell cell,
                                     final Object value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final TextNode expected) {
        this.formatValueAndCheck(
            context,
            cell,
            Optional.of(value),
            formatter,
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final SpreadsheetCell cell,
                                     final Optional<Object> value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final TextNode expected) {
        this.formatValueAndCheck(
            context,
            cell,
            value,
            formatter,
            Optional.of(expected)
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final SpreadsheetCell cell,
                                     final Optional<Object> value,
                                     final SpreadsheetFormatterSelector formatter,
                                     final Optional<TextNode> expected) {
        this.formatValueAndCheck(
            context,
            cell,
            value,
            Optional.of(formatter),
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetEngineContext context,
                                     final SpreadsheetCell cell,
                                     final Optional<Object> value,
                                     final Optional<SpreadsheetFormatterSelector> formatter,
                                     final Optional<TextNode> expected) {
        this.checkEquals(
            expected,
            context.formatValue(
                cell,
                value,
                formatter
            ),
            () -> "formatValue " + cell + " " + CharSequences.quoteIfChars(value) + " " + formatter
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
                                        final SpreadsheetFormatterSelector formatter,
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
                                        final SpreadsheetFormatterSelector formatter,
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
                                        final Optional<SpreadsheetFormatterSelector> formatter,
                                        final SpreadsheetCell expected) {
        this.checkEquals(
            expected,
            context.formatValueAndStyle(
                cell,
                formatter
            ),
            () -> "formatValueAndStyle " + cell + " " + formatter
        );
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
