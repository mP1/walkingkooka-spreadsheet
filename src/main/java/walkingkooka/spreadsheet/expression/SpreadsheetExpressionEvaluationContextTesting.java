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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextTesting;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.value.HasSpreadsheetCellTesting;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContextTesting;
import walkingkooka.terminal.expression.TerminalExpressionEvaluationContextTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.json.expression.JsonNodeExpressionEvaluationContextTesting;
import walkingkooka.validation.expression.ValidatorExpressionEvaluationContextTesting;
import walkingkooka.validation.form.expression.FormHandlerExpressionEvaluationContextTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExpressionEvaluationContextTesting extends FormHandlerExpressionEvaluationContextTesting,
    HasSpreadsheetCellTesting,
    JsonNodeExpressionEvaluationContextTesting,
    SpreadsheetEnvironmentContextTesting,
    SpreadsheetLabelNameResolverTesting,
    SpreadsheetMetadataContextTesting,
    StorageExpressionEvaluationContextTesting,
    TerminalExpressionEvaluationContextTesting,
    ValidatorExpressionEvaluationContextTesting,
    SpreadsheetProviderContextTesting {

    // parseExpression..................................................................................................

    default void parseExpressionAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final String expression,
                                         final SpreadsheetFormulaParserToken expected) {
        this.checkEquals(
            expected,
            context.parseExpression(
                TextCursors.charSequence(expression)
            ),
            () -> "parseExpression " + CharSequences.quoteAndEscape(expression) + " with context " + context);
    }

    default void parseExpressionAndFail(final SpreadsheetExpressionEvaluationContext context,
                                        final String expression,
                                        final String expected) {
        final RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> context.parseExpression(
                TextCursors.charSequence(expression)
            )
        );
        this.getMessageAndCheck(
            thrown,
            expected
        );
    }

    // parseValueOrExpression...........................................................................................

    default void parseValueOrExpressionAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                                final String valueOrExpression,
                                                final SpreadsheetFormulaParserToken expected) {
        this.checkEquals(
            expected,
            context.parseValueOrExpression(
                TextCursors.charSequence(valueOrExpression)
            ),
            () -> "parseValueOrExpression " + CharSequences.quoteAndEscape(valueOrExpression) + " with context " + context);
    }

    default void parseValueOrExpressionAndFail(final SpreadsheetExpressionEvaluationContext context,
                                               final String valueOrExpression,
                                               final String expected) {
        final RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> context.parseValueOrExpression(
                TextCursors.charSequence(valueOrExpression)
            )
        );
        this.checkEquals(
            expected,
            thrown.getMessage(),
            "message"
        );
    }

    // loadCell.........................................................................................................

    default void loadCellAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                  final SpreadsheetCellReference cellReference) {
        this.loadCellAndCheck(
            context,
            cellReference,
            SpreadsheetExpressionEvaluationContext.NO_CELL
        );
    }

    default void loadCellAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                  final SpreadsheetCellReference cellReference,
                                  final SpreadsheetCell expected) {
        this.loadCellAndCheck(
            context,
            cellReference,
            Optional.of(expected)
        );
    }

    default void loadCellAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                  final SpreadsheetCellReference cellReference,
                                  final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadCell(cellReference),
            () -> "loadCell " + cellReference
        );
    }

    // loadCellRange....................................................................................................

    default void loadCellRangeAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                       final SpreadsheetCellRangeReference range,
                                       final SpreadsheetCell... expected) {
        this.loadCellRangeAndCheck(
            context,
            range,
            Sets.of(expected)
        );
    }

    default void loadCellRangeAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                       final SpreadsheetCellRangeReference range,
                                       final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadCellRange(range),
            () -> "loadCellRange " + range
        );
    }

    // loadLabel........................................................................................................

    default void loadLabelAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                   final SpreadsheetLabelName labelName) {
        this.loadLabelAndCheck(
            context,
            labelName,
            Optional.empty()
        );
    }

    default void loadLabelAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                   final SpreadsheetLabelName labelName,
                                   final SpreadsheetLabelMapping expected) {
        this.loadLabelAndCheck(
            context,
            labelName,
            Optional.of(expected)
        );
    }

    default void loadLabelAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                   final SpreadsheetLabelName labelName,
                                   final Optional<SpreadsheetLabelMapping> expected) {
        this.checkEquals(
            expected,
            context.loadLabel(labelName),
            () -> "loadLabel " + labelName
        );
    }

    // setCell..........................................................................................................

    default void setCellAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                 final Optional<SpreadsheetCell> cell,
                                 final SpreadsheetExpressionEvaluationContext expected) {
        this.checkEquals(
            expected,
            context.setCell(cell)
        );
    }

    // nextEmptyColumn.....................................................................................................

    default void nextEmptyColumnAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final SpreadsheetRowReference row) {
        this.nextEmptyColumnAndCheck(
            context,
            row,
            Optional.empty()
        );
    }

    default void nextEmptyColumnAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final SpreadsheetRowReference row,
                                         final SpreadsheetColumnReference expected) {
        this.nextEmptyColumnAndCheck(
            context,
            row,
            Optional.of(expected)
        );
    }

    default void nextEmptyColumnAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final SpreadsheetRowReference row,
                                         final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            context.nextEmptyColumn(row)
        );
    }

    // nextEmptyRow.....................................................................................................

    default void nextEmptyRowAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                      final SpreadsheetColumnReference column) {
        this.nextEmptyRowAndCheck(
            context,
            column,
            Optional.empty()
        );
    }

    default void nextEmptyRowAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                      final SpreadsheetColumnReference column,
                                      final SpreadsheetRowReference expected) {
        this.nextEmptyRowAndCheck(
            context,
            column,
            Optional.of(expected)
        );
    }

    default void nextEmptyRowAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                      final SpreadsheetColumnReference column,
                                      final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            context.nextEmptyRow(column)
        );
    }
}
