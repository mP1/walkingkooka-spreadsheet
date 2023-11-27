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
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.expression.Expression;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting<C extends SpreadsheetEngineContext> extends ContextTesting<C>,
        ParserTesting,
        HasLocaleTesting {

    // resolveIfLabel...............................................................................................

    @Test
    default void testResolveIfLabelNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .resolveIfLabel(null)
        );
    }

    @Test
    default void testResolveIfLabelCell() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    default void testResolveIfLabelColumn() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseColumn("Z")
        );
    }

    @Test
    default void testResolveIfLabelColumnRange() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseColumnRange("X:Y")
        );
    }

    @Test
    default void testResolveIfLabelRow() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    default void testResolveIfLabelRowRange() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseRowRange("3:4")
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetSelection selection) {
        this.resolveIfLabelAndCheck(
                selection,
                selection
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetSelection selection,
                                        final SpreadsheetSelection expected) {
        this.resolveIfLabelAndCheck(
                this.createContext(),
                selection,
                expected
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetEngineContext context,
                                        final SpreadsheetSelection selection,
                                        final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                context.resolveIfLabel(selection),
                () -> "resolveIfLabel " + selection
        );
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

    // format...........................................................................................................

    @Test
    default void testFormatNullFormatterFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().format("1", null));
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetFormatter formatter,
                                final Optional<SpreadsheetText> expected) {
        this.formatAndCheck(this.createContext(),
                value,
                formatter,
                expected);
    }

    default void formatAndCheck(final SpreadsheetEngineContext context,
                                final Object value,
                                final SpreadsheetFormatter formatter,
                                final Optional<SpreadsheetText> expected) {
        this.checkEquals(expected,
                context.format(value, formatter),
                () -> "format " + CharSequences.quoteIfChars(value) + " " + formatter);
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }
}
