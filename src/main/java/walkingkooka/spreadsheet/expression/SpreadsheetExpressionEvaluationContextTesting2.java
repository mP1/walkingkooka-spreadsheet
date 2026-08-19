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
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextTesting2;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting2;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContextTesting2;
import walkingkooka.terminal.expression.TerminalExpressionEvaluationContextTesting2;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.json.expression.JsonNodeExpressionEvaluationContextTesting2;
import walkingkooka.validation.expression.ValidatorExpressionEvaluationContextTesting2;
import walkingkooka.validation.form.expression.FormHandlerExpressionEvaluationContextTesting2;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExpressionEvaluationContextTesting2<C extends SpreadsheetExpressionEvaluationContext> extends SpreadsheetExpressionEvaluationContextTesting,
    FormHandlerExpressionEvaluationContextTesting2<SpreadsheetValidationReference, SpreadsheetDelta, C>,
    JsonNodeExpressionEvaluationContextTesting2<C>,
    SpreadsheetEnvironmentContextTesting2<C>,
    SpreadsheetLabelNameResolverTesting2<C>,
    SpreadsheetMetadataContextTesting2<C>,
    StorageExpressionEvaluationContextTesting2<C>,
    TerminalExpressionEvaluationContextTesting2<C>,
    ValidatorExpressionEvaluationContextTesting2<SpreadsheetValidationReference, C>,
    SpreadsheetProviderContextTesting2<C> {

    // stringEqualsCaseSensitivity......................................................................................

    @Test
    default void testStringEqualsCaseSensitivity() {
        this.checkEquals(
            SpreadsheetStrings.CASE_SENSITIVITY,
            this.createContext()
                .stringEqualsCaseSensitivity()
        );
    }

    // evaluate.........................................................................................................

    @Test
    default void testEvaluateWithEmptyStringReturnsError() {
        this.evaluateAndCheck(
            "",
            SpreadsheetErrorKind.ERROR.setMessage("End of text, expected \"\\\'\", [STRING] | EQUALS_EXPRESSION | VALUE")
        );
    }

    @Test
    default void testEvaluateWithWhitespaceStringReturnsError() {
        this.evaluateAndCheck(
            " ",
            SpreadsheetErrorKind.ERROR.setMessage("Invalid character \' \' expected \"\\\'\", [STRING] | EQUALS_EXPRESSION | VALUE")
        );
    }

    // parseExpression..................................................................................................

    @Test
    default void testParseExpressionNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseExpression(null)
        );
    }

    @Test
    default void testParseExpressionWithEmptyStringFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseExpression(
                    TextCursors.charSequence("")
                )
        );
    }

    @Test
    default void testParseExpressionWithOnlyWhitespaceStringFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseExpression(
                    TextCursors.charSequence(" ")
                )
        );
    }

    @Test
    default void testParseExpressionWithOnlyWhitespaceStringFails2() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseExpression(
                    TextCursors.charSequence("  ")
                )
        );
    }

    default void parseExpressionAndCheck(final String formula,
                                         final SpreadsheetFormulaParserToken expected) {
        this.parseExpressionAndCheck(
            this.createContext(),
            formula,
            expected
        );
    }

    default void parseExpressionAndFail(final String expression,
                                        final String expected) {
        this.parseExpressionAndFail(
            this.createContext(),
            expression,
            expected
        );
    }

    // parseValueOrExpression...........................................................................................

    @Test
    default void testParseValueOrExpressionNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseValueOrExpression(null)
        );
    }

    @Test
    default void testParseValueOrExpressionWithEmptyStringFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseValueOrExpression(
                    TextCursors.charSequence("")
                )
        );
    }

    @Test
    default void testParseValueOrExpressionWithOnlyWhitespaceStringFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseValueOrExpression(
                    TextCursors.charSequence(" ")
                )
        );
    }

    @Test
    default void testParseValueOrExpressionWithOnlyWhitespaceStringFails2() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseValueOrExpression(
                    TextCursors.charSequence("  ")
                )
        );
    }
    
    default void parseValueOrExpressionAndCheck(final String valueOrExpression,
                                                final SpreadsheetFormulaParserToken expected) {
        this.parseValueOrExpressionAndCheck(
            this.createContext(),
            valueOrExpression,
            expected
        );
    }

    default void parseValueOrExpressionAndFail(final String valueOrExpression,
                                               final String expected) {
        this.parseValueOrExpressionAndFail(
            this.createContext(),
            valueOrExpression,
            expected
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

    // loadCellRange....................................................................................................

    @Test
    default void testLoadCellRangeWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadCellRange(null)
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

    // spreadsheetFormatterContext......................................................................................

    @Test
    default void testSpreadsheetFormatterContextWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .spreadsheetFormatterContext(null)
        );
    }

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
    default C createConverterLike() {
        return this.createContext();
    }
}
