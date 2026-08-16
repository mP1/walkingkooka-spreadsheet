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
import walkingkooka.spreadsheet.SpreadsheetContextTesting2;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextTesting2;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineContextTesting2<C extends SpreadsheetEngineContext> extends ContextTesting<C>,
    SpreadsheetEngineContextTesting,
    SpreadsheetContextTesting2<C>,
    SpreadsheetLabelNameResolverTesting2<C>,
    SpreadsheetProviderTesting<C>,
    SpreadsheetStorageContextTesting2<C> {

    // setLocale........................................................................................................

    @Test
    @Override
    default void testSetLocaleWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setLocale(null)
        );
    }

    // SpreadsheetLabelNameResolverTesting2..............................................................................

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

    // toExpression.....................................................................................................

    @Test
    default void testToExpressionWithNullTokenFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .toExpression(null)
        );
    }

    // setSpreadsheetMetadataMode.......................................................................................

    @Test
    default void testSetSpreadsheetMetadataModeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setSpreadsheetMetadataMode(null)
        );
    }

    default void setSpreadsheetMetadataModeAndCheck(final SpreadsheetEngineContext context,
                                                    final SpreadsheetMetadataMode mode,
                                                    final SpreadsheetEngineContext expected) {
        this.checkEquals(
            expected,
            context.setSpreadsheetMetadataMode(mode),
            () -> "setSpreadsheetMetadataMode " + mode
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

    // class.......... .................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }

    // MediaTypeDetector................................................................................................

    @Override
    default C createMediaTypeDetector() {
        return this.createContext();
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

    // SpreadsheetProvider..............................................................................................

    @Override
    default C createSpreadsheetProvider() {
        return this.createContext();
    }
}
