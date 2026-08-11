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

import walkingkooka.spreadsheet.SpreadsheetContextTesting;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextTesting;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

public interface SpreadsheetEngineContextTesting extends SpreadsheetContextTesting,
    ParserTesting,
    SpreadsheetStorageContextTesting {

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

    // evaluate.........................................................................................................

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
}
