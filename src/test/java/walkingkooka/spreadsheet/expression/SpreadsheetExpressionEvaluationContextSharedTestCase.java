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
import walkingkooka.collect.list.Lists;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;

import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class SpreadsheetExpressionEvaluationContextSharedTestCase<C extends SpreadsheetExpressionEvaluationContextShared> implements SpreadsheetExpressionEvaluationContextTesting<C>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    SpreadsheetExpressionEvaluationContextSharedTestCase() {
        super();
    }

    // parseExpression..................................................................................................

    @Test
    public final void testParseExpressionQuotedString() {
        final String text = "abc123";
        final String expression = '"' + text + '"';

        this.parseExpressionAndCheck(
            expression,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\""),
                    SpreadsheetFormulaParserToken.textLiteral(text, text),
                    SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\"")
                ),
                expression
            )
        );
    }

    @Test
    public final void testParseExpressionNumber() {
        final String text = "123";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits(text, text)
                ),
                text
            )
        );
    }

    private final static char DECIMAL = '.';

    @Test
    public final void testParseExpressionNumber2() {
        final String text = "1" + DECIMAL + "5";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits("1", "1"),
                    SpreadsheetFormulaParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                    SpreadsheetFormulaParserToken.digits("5", "5")
                ),
                text
            )
        );
    }

    @Test
    public final void testParseExpressionAdditionExpression() {
        final String text = "1+2";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.addition(
                Lists.of(
                    SpreadsheetFormulaParserToken.number(
                        Lists.of(
                            SpreadsheetFormulaParserToken.digits("1", "1")
                        ),
                        "1"
                    ),
                    SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                    SpreadsheetFormulaParserToken.number(
                        Lists.of(
                            SpreadsheetFormulaParserToken.digits("2", "2")
                        ),
                        "2"
                    )
                ),
                text
            )
        );
    }

    @Test
    public final void testParseExpressionEqualsAdditionExpressionFails() {
        final String text = "=1+2";

        this.parseExpressionAndFail(
            text,
            "Invalid character '=' at (1,1) expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    // parseValueOrExpression...........................................................................................

    @Test
    public final void testParseValueOrExpressionDoubleQuotedStringFails() {
        this.parseValueOrExpressionAndFail(
            "\"abc123\"",
            "Invalid character '\\\"' at (1,1) expected \"\\'\", [STRING] | EQUALS_EXPRESSION | VALUE"
        );
    }

    @Test
    public final void testParseValueOrExpressionDate() {
        final String text = "1999/12/31";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.date(
                Lists.of(
                    SpreadsheetFormulaParserToken.year(1999, "1999"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.dayNumber(31, "31")
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionDateTime() {
        final String text = "1999/12/31 12:58";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.dateTime(
                Lists.of(
                    SpreadsheetFormulaParserToken.year(1999, "1999"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.dayNumber(31, "31"),
                    SpreadsheetFormulaParserToken.whitespace(" ", " "),
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58")
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionNumber() {
        final String text = "123";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits(text, text)
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionNumber2() {
        final String text = "1" + DECIMAL + "5";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits("1", "1"),
                    SpreadsheetFormulaParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                    SpreadsheetFormulaParserToken.digits("5", "5")
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionApostropheString() {
        final String text = "'Hello";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.apostropheSymbol("'", "'"),
                    SpreadsheetFormulaParserToken.textLiteral("Hello", "Hello")
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionTime() {
        final String text = "12:58:59";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.time(
                Lists.of(
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.seconds(59, "59")
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionEqualsAdditionExpression() {
        final String text = "=1+2";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.expression(
                Lists.of(
                    SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                    SpreadsheetFormulaParserToken.addition(
                        Lists.of(
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits("1", "1")
                                ),
                                "1"
                            ),
                            SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits("2", "2")
                                ),
                                "2"
                            )
                        ),
                        "1+2"
                    )
                ),
                text
            )
        );
    }

    @Test
    public final void testParseValueOrExpressionAdditionExpressionFails() {
        final String text = "1+2";

        this.parseValueOrExpressionAndFail(
            text,
            "Invalid character '1' at (1,1) expected \"\\'\", [STRING] | EQUALS_EXPRESSION | VALUE"
        );
    }

    // evaluate.........................................................................................................

    @Test
    public final void testEvaluateIncompleteExpressionFails() {
        this.evaluateAndCheck(
            this.createContext(),
            "=1+",
            SpreadsheetErrorKind.ERROR.setMessage(
                "End of text, expected LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
            )
        );
    }

    @Test
    public final void testEvaluateApostrophe() {
        this.evaluateAndCheck(
            this.createContext(),
            "'Hello",
            "Hello"
        );
    }

    @Test
    public final void testEvaluateDate() {
        this.evaluateAndCheck(
            this.createContext(),
            "1999/12/31",
            LocalDate.of(
                1999,
                12,
                31
            )
        );
    }

    @Test
    public final void testEvaluateDateTime() {
        this.evaluateAndCheck(
            this.createContext(),
            "1999/12/31 12:58",
            LocalDateTime.of(
                1999,
                12,
                31,
                12,
                58
            )
        );
    }

    @Test
    public final void testEvaluateNumberValue() {
        this.evaluateAndCheck(
            this.createContext(),
            "123.5",
            EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    @Test
    public final void testEvaluateTime() {
        this.evaluateAndCheck(
            this.createContext(),
            "12:58:59",
            LocalTime.of(
                12,
                58,
                59
            )
        );
    }

    @Test
    public final void testEvaluateExpression() {
        this.evaluateAndCheck(
            this.createContext(),
            "=1+2",
            EXPRESSION_NUMBER_KIND.create(3)
        );
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public final MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public abstract int decimalNumberDigitCount();

    @Override
    public final DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = METADATA_EN_AU.decimalNumberContext(
        SpreadsheetMetadata.NO_CELL,
        LOCALE_CONTEXT
    );

    // Class............................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetExpressionEvaluationContextShared.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
