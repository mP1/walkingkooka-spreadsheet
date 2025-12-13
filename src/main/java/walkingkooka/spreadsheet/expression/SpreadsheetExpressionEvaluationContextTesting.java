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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.value.HasSpreadsheetCellTesting;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.validation.expression.ValidatorExpressionEvaluationContextTesting;
import walkingkooka.validation.form.expression.FormHandlerExpressionEvaluationContextTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExpressionEvaluationContextTesting<C extends SpreadsheetExpressionEvaluationContext> extends FormHandlerExpressionEvaluationContextTesting<SpreadsheetExpressionReference, SpreadsheetDelta, C>,
    HasSpreadsheetCellTesting<C>,
    SpreadsheetEnvironmentContextTesting2<C>,
    SpreadsheetLabelNameResolverTesting<C>,
    ValidatorExpressionEvaluationContextTesting<SpreadsheetExpressionReference, C>,
    SpreadsheetProviderContextTesting<C> {

    // stringEqualsCaseSensitivity......................................................................................

    @Test
    default void testStringEqualsCaseSensitivity() {
        this.checkEquals(
            SpreadsheetStrings.CASE_SENSITIVITY,
            this.createContext()
                .stringEqualsCaseSensitivity()
        );
    }

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
                                      final SpreadsheetFormulaParserToken expected) {
        this.parseFormulaAndCheck(
            this.createContext(),
            formula,
            expected
        );
    }

    default void parseFormulaAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                      final String formula,
                                      final SpreadsheetFormulaParserToken expected) {
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
        final RuntimeException thrown = assertThrows(
            RuntimeException.class,
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

    @Test
    default void testLoadCellWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().loadCell(null)
        );
    }

    default void loadCellAndCheck(final C context,
                                  final SpreadsheetCellReference cellReference) {
        this.loadCellAndCheck(
            context,
            cellReference,
            SpreadsheetExpressionEvaluationContext.NO_CELL
        );
    }

    default void loadCellAndCheck(final C context,
                                  final SpreadsheetCellReference cellReference,
                                  final SpreadsheetCell expected) {
        this.loadCellAndCheck(
            context,
            cellReference,
            Optional.of(expected)
        );
    }

    default void loadCellAndCheck(final C context,
                                  final SpreadsheetCellReference cellReference,
                                  final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadCell(cellReference),
            () -> "loadCell " + cellReference
        );
    }

    // loadCellRange....................................................................................................

    @Test
    default void testLoadCellRangeWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadCellRange(null)
        );
    }

    default void loadCellRangeAndCheck(final C context,
                                       final SpreadsheetCellRangeReference range,
                                       final SpreadsheetCell... expected) {
        this.loadCellRangeAndCheck(
            context,
            range,
            Sets.of(expected)
        );
    }

    default void loadCellRangeAndCheck(final C context,
                                       final SpreadsheetCellRangeReference range,
                                       final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadCellRange(range),
            () -> "loadCellRange " + range
        );
    }

    // loadLabel........................................................................................................

    @Test
    default void testLoadLabelWithNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext().loadLabel(null)
        );
    }

    default void loadLabelAndCheck(final C context,
                                   final SpreadsheetLabelName labelName) {
        this.loadLabelAndCheck(
            context,
            labelName,
            Optional.empty()
        );
    }

    default void loadLabelAndCheck(final C context,
                                   final SpreadsheetLabelName labelName,
                                   final SpreadsheetLabelMapping expected) {
        this.loadLabelAndCheck(
            context,
            labelName,
            Optional.of(expected)
        );
    }

    default void loadLabelAndCheck(final C context,
                                   final SpreadsheetLabelName labelName,
                                   final Optional<SpreadsheetLabelMapping> expected) {
        this.checkEquals(
            expected,
            context.loadLabel(labelName),
            () -> "loadLabel " + labelName
        );
    }

    // setCell..........................................................................................................

    @Test
    default void testSetCellWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setCell(null)
        );
    }

    @Test
    default void testSetCellWithSame() {
        final C context = this.createContext();

        assertSame(
            context,
            context.setCell(
                context.cell()
            )
        );
    }

    default void setCellAndCheck(final C context,
                                 final Optional<SpreadsheetCell> cell,
                                 final SpreadsheetExpressionEvaluationContext expected) {
        this.checkEquals(
            expected,
            context.setCell(cell)
        );
    }

    // setSpreadsheetMetadata...........................................................................................

    @Test
    default void testSetSpreadsheetMetadataWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setSpreadsheetMetadata(null)
        );
    }

    @Test
    default void testSetSpreadsheetMetadataWithDifferentIdFails() {
        final C context = this.createContext();
        final SpreadsheetMetadata metadata = context.spreadsheetMetadata();
        final SpreadsheetMetadata set = metadata.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(
                1L +
                    metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                        .value()
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> context.setSpreadsheetMetadata(set)
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

    // spreadsheetFormatterContext......................................................................................

    @Test
    default void testSpreadsheetFormatterContextWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .spreadsheetFormatterContext(null)
        );
    }

    // SpreadsheetLabelNameResolverTesting..............................................................................

    @Override
    default C createSpreadsheetLabelNameResolver() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetExpressionEvaluationContext.class.getSimpleName();
    }

    // SpreadsheetExpressionEvaluationContext...........................................................................

    @Override
    default C createCanConvert() {
        return this.createContext();
    }
}
