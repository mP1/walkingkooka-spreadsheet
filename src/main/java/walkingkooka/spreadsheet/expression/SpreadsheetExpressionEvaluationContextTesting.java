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
import walkingkooka.tree.expression.ExpressionEvaluationContextTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExpressionEvaluationContextTesting<C extends SpreadsheetExpressionEvaluationContext> extends ExpressionEvaluationContextTesting<C> {

    // parseExpression......................................................................................................

    @Test
    default void testParseExpressionNullFails() {
        assertThrows(NullPointerException.class, () -> this.createContext().parseExpression(null));
    }

    default void parseExpressionAndCheck(final String expression,
                                         final SpreadsheetParserToken expected) {
        this.parseExpressionAndCheck(
                this.createContext(),
                expression,
                expected
        );
    }

    default void parseExpressionAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final String expression,
                                         final SpreadsheetParserToken expected) {
        this.checkEquals(
                expected,
                context.parseExpression(
                        TextCursors.charSequence(expression)
                ),
                () -> "parseExpression " + expression + " with context " + context);
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
}
