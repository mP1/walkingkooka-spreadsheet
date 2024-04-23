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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporterException;
import walkingkooka.tree.expression.ExpressionEvaluationContextTesting;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExpressionEvaluationContextTesting<C extends SpreadsheetExpressionEvaluationContext> extends ExpressionEvaluationContextTesting<C>,
        ExpressionFunctionProviderTesting<C> {

    // parseExpression......................................................................................................

    @Test
    default void testParseFormulaNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .parseFormula(null)
        );
    }

    default void parseFormulaAndCheck(final String formula,
                                      final SpreadsheetParserToken expected) {
        this.parseFormulaAndCheck(
                this.createContext(),
                formula,
                expected
        );
    }

    default void parseFormulaAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                      final String formula,
                                      final SpreadsheetParserToken expected) {
        this.checkEquals(
                expected,
                context.parseFormula(
                        TextCursors.charSequence(formula)
                ),
                () -> "parseFormula " + formula + " with context " + context);
    }

    default void parseFormulaAndFail(final String formula,
                                     final String expected) {
        this.parseFormulaAndFail(
                this.createContext(),
                formula,
                expected
        );
    }

    default void parseFormulaAndFail(final SpreadsheetExpressionEvaluationContext context,
                                     final String formula,
                                     final String expected) {
        final ParserReporterException thrown = assertThrows(
                ParserReporterException.class,
                () -> context.parseFormula(
                        TextCursors.charSequence(formula)
                )
        );
        this.checkEquals(
                expected,
                thrown.getMessage(),
                "message"
        );
    }

    // loadCell.........................................................................................................

    default void loadCellAndCheck(final C context,
                                  final SpreadsheetCellReference cellReference,
                                  final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
                expected,
                context.loadCell(cellReference),
                () -> "loadCell " + cellReference
        );
    }

    // resolveIfLabel...................................................................................................

    @Test
    default void testResolveIfLabelNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext().resolveIfLabel(null)
        );
    }

    default void resolveIfLabel(final C context,
                                final SpreadsheetSelection selection,
                                final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                context.resolveIfLabel(selection),
                () -> "resolveIfLabel " + selection
        );
    }

    // ExpressionFunctionProvider.......................................................................................

    @Override
    default C createExpressionFunctionProvider() {
        return this.createContext();
    }
}
