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
import walkingkooka.Cast;
import walkingkooka.ContextTesting;
import walkingkooka.locale.HasLocaleTesting;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.expression.Expression;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting<C extends SpreadsheetEngineContext> extends ContextTesting<C>,
        ParserTesting,
        HasLocaleTesting {

    // resolveCellReference...............................................................................................

    @Test
    default void testResolveCellReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().resolveCellReference(null));
    }

    @Test
    default void testResolveCellReferenceRange() {
        this.resolveCellReferenceAndCheck(
                SpreadsheetSelection.parseExpressionReference("B2:B3"),
                SpreadsheetSelection.parseCell("B2")
        );
    }

    default void resolveCellReferenceAndFail(final SpreadsheetExpressionReference reference) {
        this.resolveCellReferenceAndFail(
                this.createContext(),
                reference
        );
    }

    default void resolveCellReferenceAndFail(final SpreadsheetEngineContext context,
                                             final SpreadsheetExpressionReference reference) {
        assertThrows(RuntimeException.class, () -> context.resolveCellReference(reference),
                () -> "resolveCellReference " + reference
        );
    }

    default void resolveCellReferenceAndFail(final SpreadsheetExpressionReference reference,
                                             final String message) {
        this.resolveCellReferenceAndFail(
                this.createContext(),
                reference,
                message
        );
    }

    default void resolveCellReferenceAndFail(final SpreadsheetEngineContext context,
                                             final SpreadsheetExpressionReference reference,
                                             final String message) {
        final RuntimeException thrown = assertThrows(RuntimeException.class, () -> context.resolveCellReference(reference),
                () -> "resolveCellReference " + reference
        );
        assertEquals(
                message,
                thrown.getMessage(),
                () -> "resolveCellReference " + reference
        );
    }

    default void resolveCellReferenceAndCheck(final SpreadsheetExpressionReference reference,
                                              final SpreadsheetCellReference expected) {
        this.resolveCellReferenceAndCheck(
                this.createContext(),
                reference,
                expected
        );
    }

    default void resolveCellReferenceAndCheck(final SpreadsheetEngineContext context,
                                              final SpreadsheetExpressionReference reference,
                                              final SpreadsheetCellReference expected) {
        assertEquals(
                expected,
                context.resolveCellReference(reference),
                () -> "resolveCellReference " + reference
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
                context.parseFormula(formula),
                () -> "parseFormula " + formula + " with context " + context);
    }

    // evaluate.........................................................................................................

    @Test
    default void testEvaluateNullFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().evaluate(null));
    }

    default void evaluateAndCheck(final Expression expression,
                                  final Object expected) {
        this.evaluateAndCheck(this.createContext(),
                expression,
                expected);
    }

    default void evaluateAndCheck(final SpreadsheetEngineContext context,
                                  final Expression expression,
                                  final Object expected) {
        assertEquals(expected,
                context.evaluate(expression),
                () -> "evaluate " + expression + " with context " + context);
    }

    // parseFormat......................................................................................................

    @Test
    default void testParsePatternNullFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().parsePattern(null));
    }

    default void parsePatternAndCheck(final String pattern,
                                      final Object value,
                                      final SpreadsheetFormatterContext spreadsheetFormatContext,
                                      final Optional<SpreadsheetText> expected) {
        this.parsePatternAndCheck(this.createContext(),
                pattern,
                value,
                spreadsheetFormatContext,
                expected);
    }

    default void parsePatternAndCheck(final SpreadsheetEngineContext context,
                                      final String pattern,
                                      final Object value,
                                      final SpreadsheetFormatterContext spreadsheetFormatContext,
                                      final Optional<SpreadsheetText> expected) {
        final SpreadsheetFormatter formatter = context.parsePattern(pattern);
        assertEquals(expected,
                formatter.format(Cast.to(value), spreadsheetFormatContext),
                () -> "parsePattern " + pattern + " " + formatter + " then format " + CharSequences.quoteIfChars(value));
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
        assertEquals(expected,
                context.format(value, formatter),
                () -> "format " + CharSequences.quoteIfChars(value) + " " + formatter);
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }
}
