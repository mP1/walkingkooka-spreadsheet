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

package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import walkingkooka.Either;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.parser.AdditionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.AmPmSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.BooleanLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.BooleanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.CellRangeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DayNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DecimalSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DivisionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.EqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ErrorSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ExpressionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.FunctionParametersSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GreaterThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GreaterThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GroupSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.HourSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LabelSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LambdaFunctionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LessThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LessThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MillisecondSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MinuteSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MultiplicationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NamedFunctionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NotEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ParentSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.PowerSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SecondsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SubtractionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.YearSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.LocalDateParserToken;
import walkingkooka.text.cursor.parser.LocalDateTimeParserToken;
import walkingkooka.text.cursor.parser.LocalTimeParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;

import java.lang.reflect.Method;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class SpreadsheetFormulaParsersTest implements PublicStaticHelperTesting<SpreadsheetFormulaParsers>,
    ParserTesting2<Parser<SpreadsheetParserContext>, SpreadsheetParserContext> {

    private final static int TWO_DIGIT_YEAR = 20;
    private ExpressionNumberKind expressionNumberKind;
    private final static char VALUE_SEPARATOR = ';'; // deliberate choice picking something different other than comma

    /**
     * The name of the test (test method) is important and is used to select a {@link ExpressionNumberKind}.
     */
    @BeforeEach
    public void setupExpressionNumberKind(final TestInfo testInfo) {
        final String testName = testInfo.getDisplayName();
        this.expressionNumberKind = testName.endsWith("BigDecimal()") ?
            ExpressionNumberKind.BIG_DECIMAL :
            testName.endsWith("Double()") ?
                ExpressionNumberKind.DOUBLE :
                null;
    }

    // conditionRight...................................................................................................

    @Test
    public void testConditionRightParserParseEmpty() {
        this.conditionRightParserParseFails(
            ""
        );
    }

    @Test
    public void testConditionRightParserParseNonConditionSymbol() {
        this.conditionRightParserParseFails(
            "123"
        );
    }

    private void conditionRightParserParseFails(final String text) {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.conditionRight(
                valueOrExpressionParser()
            ),
            text
        );
    }

    @Test
    public void testConditionRightParserParseEqualsSignMissingValueOrExpressionFails() {
        this.conditionRightParserParseThrows(
            "<",
            "End of text, expected VALUE | EXPRESSION"
        );
    }

    private void conditionRightParserParseThrows(final String text,
                                                 final String message) {
        this.parseThrows(
            SpreadsheetFormulaParsers.conditionRight(
                valueOrExpressionParser()
            ),
            text,
            message
        );
    }

    @Test
    public void testConditionRightParserParseEqualsNumber() {
        this.conditionRightParserParseAndCheck(
            "=123",
            conditionRightEquals(
                equalsSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseNotEqualsNumber() {
        this.conditionRightParserParseAndCheck(
            "<>123",
            conditionRightNotEquals(
                notEqualsSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseLessThanNumber() {
        this.conditionRightParserParseAndCheck(
            "<123",
            conditionRightLessThan(
                lessThanSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseLessThanEqualsNumber() {
        this.conditionRightParserParseAndCheck(
            "<=123",
            conditionRightLessThanEquals(
                lessThanEqualsSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseGreaterThanNumber() {
        this.conditionRightParserParseAndCheck(
            ">123",
            conditionRightGreaterThan(
                greaterThanSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseGreaterThanEquals() {
        this.conditionRightParserParseAndCheck(
            ">=123",
            conditionRightGreaterThanEquals(
                greaterThanEqualsSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseGreaterThanEqualsNumber() {
        this.conditionRightParserParseAndCheck(
            ">=123",
            conditionRightGreaterThanEquals(
                greaterThanEqualsSymbol(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseGreaterThanEqualsNumberWithExtraSpaces() {
        this.conditionRightParserParseAndCheck(
            ">= 123",
            conditionRightGreaterThanEquals(
                greaterThanEqualsSymbol(),
                whitespace1(),
                number(123)
            )
        );
    }

    @Test
    public void testConditionRightParserParseEqualsString() {
        this.conditionRightParserParseAndCheck(
            "=\"Hello\"",
            conditionRightEquals(
                equalsSymbol(),
                SpreadsheetFormulaParserToken.text(
                    Lists.of(
                        doubleQuotesSymbol(),
                        textLiteral("Hello"),
                        doubleQuotesSymbol()
                    ),
                    "\"Hello\""
                )
            )
        );
    }

    @Test
    public void testConditionRightParserParseEqualsFunction() {
        this.conditionRightParserParseAndCheck(
            "=xyz()",
            conditionRightEquals(
                equalsSymbol(),
                namedFunction(
                    functionName("xyz"),
                    functionParameters(
                        parenthesisOpen(),
                        parenthesisClose()
                    )
                )
            )
        );
    }

    @Test
    public void testConditionRightParserParseEqualsFunctionWithArguments() {
        this.conditionRightParserParseAndCheck(
            "=def(123)",
            conditionRightEquals(
                equalsSymbol(),
                namedFunction(
                    functionName("def"),
                    functionParameters(
                        parenthesisOpen(),
                        number(123),
                        parenthesisClose()
                    )
                )
            )
        );
    }

    @Test
    public void testConditionRightParserParseEqualsFunctionTrailingSpace() {
        this.conditionRightParserParseAndCheck(
            "=abc() ",
            conditionRightEquals(
                equalsSymbol(),
                expression(
                    namedFunction(
                        functionName("abc"),
                        functionParameters(
                            parenthesisOpen(),
                            parenthesisClose()
                        )
                    ),
                    whitespace1()
                )
            )
        );
    }

    private ConditionRightSpreadsheetFormulaParserToken conditionRightEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::conditionRightEquals,
            tokens
        );
    }

    private ConditionRightSpreadsheetFormulaParserToken conditionRightGreaterThan(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::conditionRightGreaterThan,
            tokens
        );
    }

    private ConditionRightSpreadsheetFormulaParserToken conditionRightGreaterThanEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::conditionRightGreaterThanEquals,
            tokens
        );
    }

    private ConditionRightSpreadsheetFormulaParserToken conditionRightLessThan(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::conditionRightLessThan,
            tokens
        );
    }

    private ConditionRightSpreadsheetFormulaParserToken conditionRightLessThanEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::conditionRightLessThanEquals,
            tokens
        );
    }

    private ConditionRightSpreadsheetFormulaParserToken conditionRightNotEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::conditionRightNotEquals,
            tokens
        );
    }

    private void conditionRightParserParseAndCheck(final String text,
                                                   final SpreadsheetFormulaParserToken expected) {
        this.conditionRightParserParseAndCheck(
            text,
            expected,
            text
        );
    }

    private void conditionRightParserParseAndCheck(final String text,
                                                   final SpreadsheetFormulaParserToken expected,
                                                   final String consumed) {
        this.conditionRightParserParseAndCheck(
            text,
            expected,
            consumed,
            ""
        );
    }

    private void conditionRightParserParseAndCheck(final String text,
                                                   final SpreadsheetFormulaParserToken expected,
                                                   final String textConsumed,
                                                   final String textAfter) {
        this.parseAndCheck(
            SpreadsheetFormulaParsers.conditionRight(
                valueOrExpressionParser()
            ),
            text,
            expected,
            textConsumed,
            textAfter
        );
    }

    // expression.......................................................................................................

    @Test
    public void testExpressionWithApostropheStringFails() {
        this.expressionParserParseFails(
            "'Apostrophe String",
            "Invalid character '\\'' at 0 expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testExpressionWithFunction() {
        final String text = "z(123)";

        this.expressionParserParseStringAndCheck(
            text,
            namedFunction(
                functionName("z"),
                functionParameters(
                    parenthesisOpen(),
                    number(123),
                    parenthesisClose()
                )
            )
        );
    }

    @Test
    public void testExpressionWithTrue() {
        final String text = "true";

        this.expressionParserParseStringAndCheck(
            text,
            booleanValue(
                booleanLiteral(true)
            )
        );
    }

    @Test
    public void testExpressionWithFalse() {
        final String text = "false";

        this.expressionParserParseStringAndCheck(
            text,
            booleanValue(
                booleanLiteral(false)
            )
        );
    }

    @Test
    public void testExpressionWithNumber() {
        final String text = "123";

        this.expressionParserParseStringAndCheck(
            text,
            number(123)
        );
    }

    @Test
    public void testExpressionWithAdditionNegativeNumberPlusNumber() {
        final String text = "-1+2";

        this.expressionParserParseStringAndCheck(
            text,
            addition(
                negative(
                    minusSymbol(),
                    number(1)
                ),
                plusSymbol(),
                number(2)
            )
        );
    }

    @Test
    public void testExpressionWithAdditionNumberPlusNumber() {
        final String text = "1+2";

        this.expressionParserParseStringAndCheck(
            text,
            addition(
                number(1),
                plusSymbol(),
                number(2)
            )
        );
    }

    private void expressionParserParseFails(final String text,
                                            final String expected) {
        this.parseThrows(
            SpreadsheetFormulaParsers.expression(),
            text,
            expected
        );
    }

    private void expressionParserParseStringAndCheck(final String text,
                                                     final SpreadsheetFormulaParserToken expected) {
        this.parseAndCheck(
            expressionParser(),
            text,
            expected,
            text
        );
    }

    @Test
    public void testExpressionToString() {
        this.checkEquals(
            "EXPRESSION",
            SpreadsheetFormulaParsers.expression()
                .toString()
        );
    }

    // templateExpression...............................................................................................

    @Test
    public void testTemplateExpressionApostropheStringFails() {
        this.templateExpressionParserParseFails(
            "'Apostrophe String",
            "Invalid character '\\'' at 0 expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | TEMPLATE_VALUE_NAME | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testTemplateExpressionFunction() {
        final String text = "z(123)";

        this.templateExpressionParserParseStringAndCheck(
            text,
            namedFunction(
                functionName("z"),
                functionParameters(
                    parenthesisOpen(),
                    number(123),
                    parenthesisClose()
                )
            )
        );
    }

    @Test
    public void testTemplateExpressionNumber() {
        final String text = "123";

        this.templateExpressionParserParseStringAndCheck(
            text,
            number(123)
        );
    }

    @Test
    public void testTemplateExpressionWithTemplateValueName() {
        final String text = "Template-value-name-123";

        this.templateExpressionParserParseStringAndCheck(
            text,
            SpreadsheetFormulaParserToken.templateValueName(
                TemplateValueName.with(text),
                text
            )
        );
    }

    @Test
    public void testTemplateExpressionAdditionNegativeNumberPlusNumber() {
        final String text = "-1+2";

        this.templateExpressionParserParseStringAndCheck(
            text,
            addition(
                negative(
                    minusSymbol(),
                    number(1)
                ),
                plusSymbol(),
                number(2)
            )
        );
    }

    @Test
    public void testTemplateExpressionAdditionNumberPlusNumber() {
        final String text = "1+2";

        this.templateExpressionParserParseStringAndCheck(
            text,
            addition(
                number(1),
                plusSymbol(),
                number(2)
            )
        );
    }

    private void templateExpressionParserParseFails(final String text,
                                                    final String expected) {
        this.parseThrows(
            SpreadsheetFormulaParsers.templateExpression(),
            text,
            expected
        );
    }

    private void templateExpressionParserParseStringAndCheck(final String text,
                                                             final SpreadsheetFormulaParserToken expected) {
        this.parseAndCheck(
            SpreadsheetFormulaParsers.templateExpression(),
            text,
            expected,
            text
        );
    }

    @Test
    public void testTemplateExpressionToString() {
        this.checkEquals(
            "EXPRESSION",
            SpreadsheetFormulaParsers.expression()
                .toString()
        );
    }

    // apostrophe string values.........................................................................................

    @Test
    public void testValueOrExpressionApostropheStringEmpty() {
        this.valueOrExpressionParserParseStringAndCheck("");
    }

    @Test
    public void testValueOrExpressionApostropheString() {
        this.valueOrExpressionParserParseStringAndCheck("abc-123");
    }

    @Test
    public void testValueOrExpressionApostropheStringIncludesSingleAndDoubleQuote() {

        this.valueOrExpressionParserParseStringAndCheck("1abc-'123\"456");
    }

    @Test
    public void testValueOrExpressionApostropheStringIgnoresBackslashEscaping() {
        this.valueOrExpressionParserParseStringAndCheck("new-line\\ntab\\t");
    }

    private void valueOrExpressionParserParseStringAndCheck(final String text) {
        final String apostropheText = '\'' + text;

        this.parseAndCheck(
            valueOrExpressionParser(),
            apostropheText,
            TextSpreadsheetFormulaParserToken.text(
                Lists.of(
                        SpreadsheetFormulaParserToken.apostropheSymbol("'", "'"),
                        SpreadsheetFormulaParserToken.textLiteral(text, text)
                    ).stream()
                    .filter(t -> !t.text().isEmpty())
                    .collect(Collectors.toList()),
                apostropheText),
            apostropheText,
            ""
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testValueOrExpressionParserWithTrue() {
        final String text = "true";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.booleanValue(
                Lists.of(
                    booleanLiteral(true)
                ),
                text
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserWithFalse() {
        final String text = "false";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.booleanValue(
                Lists.of(
                    booleanLiteral(false)
                ),
                text
            ),
            text
        );
    }

    // date values.......................................................................................................

    @Test
    public void testValueOrExpressionParserWithDateParsePatternParserWithExtraTextFails() {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.valueOrExpression(SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").parser()),
            "2000/12/31 Extra"
        );
    }

    @Test
    public void testValueOrExpressionParserWithDateParsePatternParserWithDayMonthYear2000() {
        this.dateParserPatternPatternParseParseAndCheck(
            "31/12/2000",
            "dd/mm/yyyy",
            day31(),
            slash(),
            month12(),
            slash(),
            year2000()
        );
    }

    @Test
    public void testValueOrExpressionParserWithDateParsePatternParserWithDayMonthYear99() {
        this.dateParserPatternPatternParseParseAndCheck(
            "31/12/99",
            "dd/mm/yy",
            day31(),
            slash(),
            month12(),
            slash(),
            year99()
        );
    }

    @Test
    public void testValueOrExpressionParserWithDateParsePatternParserWithYear2000MonthDay() {
        this.dateParserPatternPatternParseParseAndCheck(
            "2000/12/31",
            "yyyy/mm/dd",
            year2000(),
            slash(),
            month12(),
            slash(),
            day31()
        );
    }

    private void dateParserPatternPatternParseParseAndCheck(final String text,
                                                            final String pattern,
                                                            final SpreadsheetFormulaParserToken... tokens) {
        this.parseValueAndCheck(
            text,
            SpreadsheetPattern.parseDateParsePattern(pattern).parser(),
            SpreadsheetFormulaParserToken::date,
            tokens
        );
    }

    // date/time values.................................................................................................

    @Test
    public void testValueOrExpressionParserWithDateTimeParsePatternParserWithExtraTextFails() {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.valueOrExpression(SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:m").parser()),
            "2000/12/31 12:58 Extra"
        );
    }

    @Test
    public void testValueOrExpressionParserWithDateTimeParsePatternParserWithDayMonthYearHourMinutes() {
        this.parseDateTimeAndCheck(
            "31/12/2000 11:58:59.123 pm",
            "dd/mm/yyyy hh:mm:ss.000 a/p",
            day31(),
            slash(),
            month12(),
            slash(),
            year2000(),
            whitespace(" "),
            hour11(),
            colon(),
            minute58(),
            colon(),
            seconds59(),
            decimal(),
            millis123(),
            whitespace(" "),
            pm()
        );
    }

    private void parseDateTimeAndCheck(final String text,
                                       final String pattern,
                                       final SpreadsheetFormulaParserToken... tokens) {
        this.parseValueAndCheck(
            text,
            SpreadsheetPattern.parseDateTimeParsePattern(pattern).parser(),
            SpreadsheetFormulaParserToken::dateTime,
            tokens
        );
    }

    // time values.......................................................................................................

    @Test
    public void testValueOrExpressionParserWithTimeParsePatternParserWithExtraTextFails() {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.valueOrExpression(SpreadsheetPattern.parseTimeParsePattern("hh:mm").parser()),
            "12:58 Extra"
        );
    }

    @Test
    public void testValueOrExpressionParserWithTimeParsePatternParserWithHourMinutes() {
        this.parseTimeAndCheck(
            "11:58",
            "hh:mm",
            hour11(),
            colon(),
            minute58()
        );
    }

    @Test
    public void testValueOrExpressionParserWithTimeParsePatternParserWithHourMinutesSeconds() {
        this.parseTimeAndCheck(
            "11:58:59",
            "hh:mm:ss",
            hour11(),
            colon(),
            minute58(),
            colon(),
            seconds59()
        );
    }

    @Test
    public void testValueOrExpressionParserWithTimeParsePatternParserWithHourMinutesSecondsMillis() {
        this.parseTimeAndCheck(
            "11:58:59.123",
            "hh:mm:ss.000",
            hour11(),
            colon(),
            minute58(),
            colon(),
            seconds59(),
            decimal(),
            millis123()
        );
    }

    private void parseTimeAndCheck(final String text,
                                   final String pattern,
                                   final SpreadsheetFormulaParserToken... tokens) {
        this.parseValueAndCheck(
            text,
            SpreadsheetPattern.parseTimeParsePattern(pattern).parser(),
            SpreadsheetFormulaParserToken::time,
            tokens
        );
    }

    private void parseValueAndCheck(final String text,
                                    final Parser<SpreadsheetParserContext> parser,
                                    final BiFunction<List<ParserToken>, String, ParentSpreadsheetFormulaParserToken> factory,
                                    final SpreadsheetFormulaParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        this.parseAndCheck(
            SpreadsheetFormulaParsers.valueOrExpression(parser),
            text,
            factory.apply(
                list,
                ParserToken.text(list)
            ),
            text,
            ""
        );
    }

    // values...........................................................................................................

    @Test
    public void testValueOrExpressionParserParseText() {
        final String text = "\"abc-123\"";

        this.valueOrExpressionParserParseAndCheck(text,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    doubleQuotesSymbol(),
                    textLiteral("abc-123"),
                    doubleQuotesSymbol()
                ),
                text
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseTextWithEscapedDoubleQuote() {
        final String text = "\"abc-\"\"-123\"";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    doubleQuotesSymbol(),
                    SpreadsheetFormulaParserToken.textLiteral("abc-\"-123", "abc-\"\"-123"),
                    doubleQuotesSymbol()
                ),
                text
            ),
            text,
            "\"abc-\\\"-123\""
        );
    }

    // NUMBER...........................................................................................................

    @Test
    public void testValueOrExpressionParserParseNumberWithExtraTextFails() {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.valueOrExpression(SpreadsheetPattern.parseNumberParsePattern("#").parser().andEmptyTextCursor()),
            "12 Extra"
        );
    }

    @Test
    public void testValueOrExpressionParserParseNumber() {
        final String text = "1";

        this.valueOrExpressionParserParseAndCheck(text,
            number(1),
            text);
    }

    @Test
    public void testValueOrExpressionParserParseNumberPercent() {
        final String text = "100%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(100),
                percentSymbol()
            ),
            text,
            "1"
        );
    }

    @Test
    public void testValueOrExpressionParserNumberPercent2() {
        final String text = "123%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(123),
                percentSymbol()
            ),
            text,
            "1.23"
        );
    }

    @Test
    public void testValueOrExpressionParserNumberDecimalPercent() {
        final String text = "100.%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(100),
                decimal(),
                percentSymbol()
            ),
            text,
            "1"
        );
    }

    @Test
    public void testValueOrExpressionParserNumberDecimalPercent2() {
        final String text = "123.%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(123),
                decimal(),
                percentSymbol()
            ),
            text,
            "1.23"
        );
    }

    @Test
    public void testValueOrExpressionParserNumberDecimalDigitPercent() {
        final String text = "100.0%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(100),
                decimal(),
                digits(0),
                percentSymbol()
            ),
            text,
            "1"
        );
    }

    @Test
    public void testValueOrExpressionParserNumberDecimalDigitPercent2() {
        final String text = "123.5%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(123),
                decimal(),
                digits(5),
                percentSymbol()
            ),
            text,
            "1.235"
        );
    }

    // CELL,............................................................................................................

    @Test
    public void testCellParserParseColumnFails() {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.cell(),
            "A"
        );
    }

    @Test
    public void testCellParserParseLabelFails() {
        this.parseThrows(
            SpreadsheetFormulaParsers.cell(),
            "LABEL123",
            "Invalid column \"LABEL\" not between \"A\" and \"XFE\""
        );
    }

    @Test
    public void testCellParserParseCell() {
        final String text = "A1";
        final CellSpreadsheetFormulaParserToken cell = cell(0, "A", 0);

        this.cellParserParseAndCheck(text, cell, text);
    }

    @Test
    public void testCellParserParseCell2() {
        final String text = "AA678";
        final CellSpreadsheetFormulaParserToken cell = this.cell(26, "AA", 678 - 1);

        this.cellParserParseAndCheck(text, cell, text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetFormulaParsers#cell()}} and then repeat again with
     * {@link SpreadsheetFormulaParsers#expression()}.
     */
    private void cellParserParseAndCheck(final String from,
                                         final SpreadsheetFormulaParserToken expected,
                                         final String text) {
        this.parseAndCheck(
            SpreadsheetFormulaParsers.cellOrCellRangeOrLabel(),
            from,
            expected,
            text
        );

        this.cellOrCellRangeOrLabelParseAndCheck(
            from,
            expected,
            text
        );
    }

    @Test
    public void testCellParserToString() {
        this.checkEquals(
            "CELL",
            SpreadsheetFormulaParsers.cell()
                .toString()
        );
    }

    // CELL, CELL RANGE & LABEL ........................................................................................

    @Test
    public void testCellOrCellRangeOrLabelParserWithExpressionFails() {
        this.parseFailAndCheck(
            SpreadsheetFormulaParsers.cellOrCellRangeOrLabel(),
            "1+2"
        );
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithCell() {
        final String text = "A1";
        final CellSpreadsheetFormulaParserToken cell = cell(0, "A", 0);

        this.cellOrCellRangeOrLabelParseAndCheck(text, cell, text);
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithCell2() {
        final String text = "AA678";
        final CellSpreadsheetFormulaParserToken cell = this.cell(26, "AA", 678 - 1);

        this.cellOrCellRangeOrLabelParseAndCheck(text, cell, text);
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithCellRange() {
        final String text = "A1:B2";

        final CellRangeSpreadsheetFormulaParserToken range = SpreadsheetFormulaParserToken.cellRange(
            Lists.of(
                this.cell(0, "A", 0),
                betweenSymbol(),
                this.cell(1, "B", 1)
            ),
            text
        );

        this.cellOrCellRangeOrLabelParseAndCheck(
            text,
            range,
            text
        );
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithLabel() {
        final String text = "Hello";

        this.cellOrCellRangeOrLabelParseAndCheck(text,
            SpreadsheetFormulaParserToken.label(SpreadsheetSelection.labelName(text), text),
            text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetFormulaParsers#cellOrCellRangeOrLabel()}} and then repeat again with
     * {@link SpreadsheetFormulaParsers#expression()}.
     */
    private void cellOrCellRangeOrLabelParseAndCheck(final String from,
                                                     final SpreadsheetFormulaParserToken expected,
                                                     final String text) {
        this.parseAndCheck(
            SpreadsheetFormulaParsers.cellOrCellRangeOrLabel(),
            from,
            expected,
            text
        );
        this.valueOrExpressionParserParseAndCheck(from, expected, text);
    }

    @Test
    public void testCellOrCellRangeOrLabelParserToString() {
        this.checkEquals(
            "LABEL | CELL_RANGE | CELL",
            SpreadsheetFormulaParsers.cellOrCellRangeOrLabel()
                .toString()
        );
    }

    // RANGE............................................................................................................

    @Test
    public void testCellRangeParserParseNumberExpressionFails() {
        this.parseFailAndCheck(SpreadsheetFormulaParsers.cellRange(), "1+2");
    }

    @Test
    public void testCellRangeParserParseCellToCell() {
        final CellSpreadsheetFormulaParserToken from = this.cell(0, "A", 0);
        final CellSpreadsheetFormulaParserToken to = this.cell(1, "B", 1);

        final CellRangeSpreadsheetFormulaParserToken range = range(from, to);
        final String text = range.text();

        this.cellRangeParserParseAndCheck(text, range, text);
    }

    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/2197 SpreadsheetCellRangeReference only allowing begin/end cells")
    public void testCellRangeParserParseLabelToLabel() {
        final LabelSpreadsheetFormulaParserToken from = this.label("parse");
        final LabelSpreadsheetFormulaParserToken to = this.label("to");

        final CellRangeSpreadsheetFormulaParserToken range = range(from, to);
        final String text = range.text();

        this.cellRangeParserParseAndCheck(text, range, text);
    }

    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/2197 SpreadsheetCellRangeReference only allowing begin/end cells")
    public void testCellRangeParserParseCellToLabel() {
        final CellSpreadsheetFormulaParserToken from = this.cell(0, "A", 0);
        final LabelSpreadsheetFormulaParserToken to = this.label("to");

        final CellRangeSpreadsheetFormulaParserToken range = range(from, to);
        final String text = range.text();

        this.cellRangeParserParseAndCheck(text, range, text);
    }

    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/2197 SpreadsheetCellRangeReference only allowing begin/end cells")
    public void testCellRangeParserParseLabelToCell() {
        final LabelSpreadsheetFormulaParserToken from = this.label("to");
        final CellSpreadsheetFormulaParserToken to = this.cell(0, "A", 0);

        final CellRangeSpreadsheetFormulaParserToken range = range(from, to);
        final String text = range.text();

        this.cellRangeParserParseAndCheck(text, range, text);
    }

    @Test
    public void testCellRangeParserParseWhitespace() {
        final CellSpreadsheetFormulaParserToken from = this.cell(0, "A", 0);
        final CellSpreadsheetFormulaParserToken to = this.cell(1, "B", 1);

        final String text = from.text() + "  " + betweenSymbol() + "  " + to.text();
        final CellRangeSpreadsheetFormulaParserToken range = SpreadsheetFormulaParserToken.cellRange(
            Lists.of(
                from,
                whitespace2(),
                betweenSymbol(),
                whitespace2(),
                to
            ),
            text
        );

        this.cellRangeParserParseAndCheck(
            text,
            range,
            text,
            text.replace(" ", "")
        );
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetFormulaParsers#cellRange()} and then repeat again with {@link SpreadsheetFormulaParsers#expression()}.
     */
    private void cellRangeParserParseAndCheck(final String from,
                                              final CellRangeSpreadsheetFormulaParserToken expected,
                                              final String text) {
        this.cellRangeParserParseAndCheck(
            from,
            expected,
            text,
            text
        );
    }

    private void cellRangeParserParseAndCheck(final String from,
                                              final CellRangeSpreadsheetFormulaParserToken expected,
                                              final String text,
                                              final String expressionToString) {
        this.parseAndCheck(
            SpreadsheetFormulaParsers.cellRange(),
            from,
            expected,
            text
        );
        this.valueOrExpressionParserParseAndCheck(
            from,
            expected,
            text,
            expressionToString
        );
    }

    @Test
    public void testCellRangeParserToString() {
        this.checkEquals(
            "CELL_RANGE",
            SpreadsheetFormulaParsers.cellRange()
                .toString()
        );
    }

    // error...........................................................................................................

    @Test
    public void testErrorParserParseErrorDiv0() {
        this.errorParserParseAndCheck(SpreadsheetErrorKind.DIV0);
    }

    @Test
    public void testErrorParserParseErrorName() {
        this.errorParserParseAndCheck(SpreadsheetErrorKind.NAME);
    }

    @Test
    public void testErrorParserParseErrorRef() {
        this.errorParserParseAndCheck(SpreadsheetErrorKind.REF);
    }

    private void errorParserParseAndCheck(final SpreadsheetErrorKind kind) {
        this.errorParserParseAndCheck(
            kind.text(),
            SpreadsheetFormulaParserToken.error(
                kind.toError(),
                kind.text()
            )
        );
    }

    private void errorParserParseAndCheck(final String from,
                                          final ErrorSpreadsheetFormulaParserToken expected) {
        this.parseAndCheck(
            SpreadsheetFormulaParsers.error(),
            from,
            expected,
            from
        );
    }

    // Expression cell..................................................................................................

    @Test
    public void testValueOrExpressionParserParseStarFails() {
        this.valueOrExpressionParserParseFails(
            "1+*",
            "Invalid character '*' at 2 expected LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testValueOrExpressionParserParseCellReference() {
        final String text = "A1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            cell(
                columnReference("A"),
                rowReference("1")
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseCellReferenceAbsolute() {
        final String text = "$A$1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            cell(
                columnReference("$A"),
                rowReference("$1")
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseCellReferenceMixed() {
        final String text = "$A1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            cell(
                columnReference("$A"),
                rowReference("1")
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseCellReferenceMixed2() {
        final String text = "A$1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            cell(
                columnReference("A"),
                rowReference("$1")
            ),
            text
        );
    }

    // Expression cell range............................................................................................

    @Test
    public void testValueOrExpressionParserParseCellRange() {
        final String text = "A1:B2";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.cellRange(
                Lists.of(
                    SpreadsheetFormulaParserToken.cell(
                        Lists.of(
                            SpreadsheetFormulaParserToken.column(
                                SpreadsheetSelection.parseColumn("A"),
                                "A"
                            ),
                            SpreadsheetFormulaParserToken.row(
                                SpreadsheetSelection.parseRow("1"),
                                "1"
                            )
                        ),
                        "A1"
                    ),
                    betweenSymbol(),
                    SpreadsheetFormulaParserToken.cell(
                        Lists.of(
                            SpreadsheetFormulaParserToken.column(
                                SpreadsheetSelection.parseColumn("B"),
                                "B"
                            ),
                            SpreadsheetFormulaParserToken.row(
                                SpreadsheetSelection.parseRow("2"),
                                "2"
                            )
                        ),
                        "B2"
                    )
                ),
                text
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseCellRangeAbsolute() {
        final String text = "$A$1:$B$2";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.cellRange(
                Lists.of(
                    SpreadsheetFormulaParserToken.cell(
                        Lists.of(
                            SpreadsheetFormulaParserToken.column(
                                SpreadsheetSelection.parseColumn("$A"),
                                "$A"
                            ),
                            SpreadsheetFormulaParserToken.row(
                                SpreadsheetSelection.parseRow("$1"),
                                "$1"
                            )
                        ),
                        "$A$1"
                    ),
                    betweenSymbol(),
                    SpreadsheetFormulaParserToken.cell(
                        Lists.of(
                            columnReference("$B"),
                            rowReference("$2")
                        ),
                        "$B$2"
                    )
                ),
                text
            ),
            text
        );
    }

    // Error............................................................................................................

    @Test
    public void testValueOrExpressionParserParseErrorDiv() {
        final String text = "#DIV/0!";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.error(
                SpreadsheetErrorKind.DIV0.toError(),
                text
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseErrorRef() {
        final String text = "#REF!";

        this.valueOrExpressionParserParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.error(
                SpreadsheetErrorKind.REF.toError(),
                text
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseAdditionLeftErrorRef() {
        final String text = "#REF!+456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                SpreadsheetFormulaParserToken.error(
                    SpreadsheetErrorKind.REF.toError(),
                    "#REF!"
                ),
                plusSymbol(),
                number(456)
            ),
            text
        );
    }

    // Negative........................................................................................................

    @Test
    public void testValueOrExpressionParserParseNegativeBigDecimal() {
        final String text = "-1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                number(1)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeWhitespaceBigDecimal() {
        final String text = "-  1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                whitespace2(),
                number(1)
            ),
            text,
            "-1"
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeNumberBigDecimal() {
        this.valueOrExpressionParserParseNegativeNumber();
    }

    @Test
    public void testValueOrExpressionParserParseNegativeNumberDouble() {
        this.valueOrExpressionParserParseNegativeNumber();
    }

    private void valueOrExpressionParserParseNegativeNumber() {
        final String text = "-1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                number(1)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeCellBigDecimal() {
        this.valueOrExpressionParserParseNegativeCell();
    }

    @Test
    public void testValueOrExpressionParserParseNegativeCellDouble() {
        this.valueOrExpressionParserParseNegativeCell();
    }

    private void valueOrExpressionParserParseNegativeCell() {
        final String text = "-A1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                cell(0, "A", 0)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeLabel() {
        final String text = "-LabelABC";

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                label("LabelABC")
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNumberBigDecimal() {
        this.valueOrExpressionParserParseNumber();
    }

    @Test
    public void testValueOrExpressionParserParseNumberDouble() {
        this.valueOrExpressionParserParseNumber();
    }

    private void valueOrExpressionParserParseNumber() {
        final String text = "1";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(1)
            ),
            text,
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNumberWithDigitsBigDecimal() {
        this.valueOrExpressionParserParseNumberWithDigits();
    }

    @Test
    public void testValueOrExpressionParserParseNumberWithDigitsDouble() {
        this.valueOrExpressionParserParseNumberWithDigits();
    }

    private void valueOrExpressionParserParseNumberWithDigits() {
        final String text = "1.75";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(1),
                decimalSymbols(),
                digits(75)
            ),
            text,
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNumberPercentageBigDecimal() {
        this.valueOrExpressionParserParseNumberPercentage();
    }

    @Test
    public void testValueOrExpressionParserParseNumberPercentageDouble() {
        this.valueOrExpressionParserParseNumberPercentage();
    }

    private void valueOrExpressionParserParseNumberPercentage() {
        final String text = "1%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            number(
                digits(1),
                percentSymbol()
            ),
            "1%",
            "0.01" // expression without percent
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeNumberPercentageBigDecimal() {
        this.valueOrExpressionParserParseNegativeNumberPercentage();
    }

    @Test
    public void testValueOrExpressionParserParseNegativeNumberPercentageDouble() {
        this.valueOrExpressionParserParseNegativeNumberPercentage();
    }

    private void valueOrExpressionParserParseNegativeNumberPercentage() {
        final String text = "-1%";

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                number(
                    digits(1),
                    percentSymbol()
                )
            ),
            text,
            "-0.01"
        );
    }

    @Test
    public void testValueOrExpressionParserParseGroupLabel() {
        final String labelText = "Hello";

        final String groupText = "(" + labelText + ")";

        this.valueOrExpressionParserParseAndCheck(
            groupText,
            group(
                parenthesisOpen(),
                label(labelText),
                parenthesisClose()
            ),
            groupText,
            labelText
        );
    }

    @Test
    public void testValueOrExpressionParserParseGroupWhitespaceLabelWhitespace() {
        final String labelText = "Hello";
        final String groupText = "(  " + labelText + "  )";

        final GroupSpreadsheetFormulaParserToken group = group(
            parenthesisOpen(),
            whitespace2(),
            label(labelText),
            whitespace2(),
            parenthesisClose()
        );

        this.valueOrExpressionParserParseAndCheck(
            groupText,
            group,
            groupText,
            labelText
        );
    }

    @Test
    public void testValueOrExpressionParserParseGroupNegativeNumberBigDecimal() {
        this.valueOrExpressionParserParseGroupNegativeNumber();
    }

    @Test
    public void testValueOrExpressionParserParseGroupNegativeNumberDouble() {
        this.valueOrExpressionParserParseGroupNegativeNumber();
    }

    private void valueOrExpressionParserParseGroupNegativeNumber() {
        final String groupText = "(-123)";

        this.valueOrExpressionParserParseAndCheck(
            groupText,
            group(
                parenthesisOpen(),
                negative(
                    minusSymbol(),
                    number(123)
                ),
                parenthesisClose()
            ),
            groupText,
            groupText.replace(
                    "(", "")
                .replace(")", "")
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeGroupNumberBigDecimal() {
        this.valueOrExpressionParserParseNegativeGroupNumber();
    }

    @Test
    public void testValueOrExpressionParserParseNegativeGroupNumberDouble() {
        this.valueOrExpressionParserParseNegativeGroupNumber();
    }

    private void valueOrExpressionParserParseNegativeGroupNumber() {
        final String text = "-" + group(
            parenthesisOpen(),
            number(123),
            parenthesisClose()
        ).text();

        this.valueOrExpressionParserParseAndCheck(
            text,
            negative(
                minusSymbol(),
                group(
                    parenthesisOpen(),
                    number(123),
                    parenthesisClose()
                )
            ),
            text,
            text.replace("(", "")
                .replace(")", "")
        );
    }

    @Test
    public void testValueOrExpressionParserParseAddBigDecimal() {
        this.valueOrExpressionParserParseAdd();
    }

    @Test
    public void testValueOrExpressionParserParseAddDouble() {
        this.valueOrExpressionParserParseAdd();
    }

    private void valueOrExpressionParserParseAdd() {
        final String text = "123+456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                number(123),
                plusSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseAdd2BigDecimal() {
        this.valueOrExpressionParserParseAdd2();
    }

    @Test
    public void testValueOrExpressionParserParseAdd2Double() {
        this.valueOrExpressionParserParseAdd2();
    }

    private void valueOrExpressionParserParseAdd2() {
        final String text = "123+456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                addition(
                    number(123),
                    plusSymbol(),
                    number(456)
                ),
                plusSymbol(),
                number(789)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseAddNegativeBigDecimal() {
        this.valueOrExpressionParserParseAddNegative();
    }

    @Test
    public void testValueOrExpressionParserParseAddNegativeDouble() {
        this.valueOrExpressionParserParseAddNegative();
    }

    private void valueOrExpressionParserParseAddNegative() {
        // 123+-456+789
        final String text = "123+-456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                addition(
                    number(123),
                    plusSymbol(),
                    negative(
                        minusSymbol(),
                        number(456)
                    )
                ),
                plusSymbol(),
                number(789)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseSubtractBigDecimal() {
        this.valueOrExpressionParserParseSubtract();
    }

    @Test
    public void testValueOrExpressionParserParseSubtractDouble() {
        this.valueOrExpressionParserParseSubtract();
    }

    private void valueOrExpressionParserParseSubtract() {
        final String text = "123-456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            subtraction(
                number(123),
                minusSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseSubtract2BigDecimal() {
        this.valueOrExpressionParserParseSubtract2();
    }

    @Test
    public void testValueOrExpressionParserParseSubtract2Double() {
        this.valueOrExpressionParserParseSubtract2();
    }

    private void valueOrExpressionParserParseSubtract2() {
        final String text = "123-456" + "-789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            subtraction(
                subtraction(
                    number(123),
                    minusSymbol(),
                    number(456)
                ),
                minusSymbol(),
                number(789)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseSubtractNegativeBigDecimal() {
        this.subtractNegative();
    }

    @Test
    public void testValueOrExpressionParserParseSubtractNegativeDouble() {
        this.subtractNegative();
    }

    private void subtractNegative() {
        final String text = "123--456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            subtraction(
                number(123),
                minusSymbol(),
                negative(
                    minusSymbol(),
                    number(456)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseSubtractAddBigDecimal() {
        this.subtractAdd();
    }

    @Test
    public void testValueOrExpressionParserParseSubtractAddDouble() {
        this.subtractAdd();
    }

    private void subtractAdd() {
        final String text = "123-456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                subtraction(
                    number(123),
                    minusSymbol(),
                    number(456)
                ),
                plusSymbol(),
                number(789)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseSubtractWhitespaceAroundMinusSignBigDecimal() {
        this.subtractWhitespaceAroundMinusSign();
    }

    @Test
    public void testValueOrExpressionParserParseSubtractWhitespaceAroundMinusSignDouble() {
        this.subtractWhitespaceAroundMinusSign();
    }

    private void subtractWhitespaceAroundMinusSign() {
        final String text = "123  -  456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                subtraction(
                    number(123),
                    whitespace2(),
                    minusSymbol(),
                    whitespace2(),
                    number(456)
                ),
                plusSymbol(),
                number(789)
            ),
            text,
            "123-456+789"
        );
    }

    @Test
    public void testValueOrExpressionParserParseMultiplyBigDecimal() {
        this.valueOrExpressionParserParseMultiply1();
    }

    @Test
    public void testValueOrExpressionParserParseMultiplyDouble() {
        this.valueOrExpressionParserParseMultiply1();
    }

    private void valueOrExpressionParserParseMultiply1() {
        final String text = "123*456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            multiplication(
                number(123),
                multiplySymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseMultiply2BigDecimal() {
        this.valueOrExpressionParserParseMultiply2();
    }

    @Test
    public void testValueOrExpressionParserParseMultiply2Double() {
        this.valueOrExpressionParserParseMultiply2();
    }

    private void valueOrExpressionParserParseMultiply2() {
        final String text = "111+222*333";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                number(111),
                plusSymbol(),
                multiplication(
                    number(222),
                    multiplySymbol(),
                    number(333)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeMultiplyNegativeBigDecimal() {
        this.negativeMultiplyNegative();
    }

    @Test
    public void testValueOrExpressionParserParseNegativeMultiplyNegativeDouble() {
        this.negativeMultiplyNegative();
    }

    private void negativeMultiplyNegative() {
        final String text = "-123*-456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            multiplication(
                negative(
                    minusSymbol(),
                    number(123)
                ),
                multiplySymbol(),
                negative(
                    minusSymbol(),
                    number(456)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseDivideBigDecimal() {
        this.valueOrExpressionParserParseDivide();
    }

    @Test
    public void testValueOrExpressionParserParseDivideDouble() {
        this.valueOrExpressionParserParseDivide();
    }

    private void valueOrExpressionParserParseDivide() {
        final String text = "123/456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            division(
                number(123),
                divideSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseDivide2BigDecimal() {
        this.valueOrExpressionParserParseDivide2();
    }

    @Test
    public void testValueOrExpressionParserParseDivide2Double() {
        this.valueOrExpressionParserParseDivide2();
    }

    private void valueOrExpressionParserParseDivide2() {
        final String text = "111+222/333";

        this.valueOrExpressionParserParseAndCheck(
            text,
            addition(
                number(111),
                plusSymbol(),
                division(
                    number(222),
                    divideSymbol(),
                    number(333)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNegativeDivideNegativeBigDecimal() {
        this.valueOrExpressionParserParseNegativeDivideNegative();
    }

    @Test
    public void testValueOrExpressionParserParseNegativeDivideNegativeDouble() {
        this.valueOrExpressionParserParseNegativeDivideNegative();
    }

    private void valueOrExpressionParserParseNegativeDivideNegative() {
        final String text = "-123/-456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            division(
                negative(
                    minusSymbol(),
                    number(123)
                ),
                divideSymbol(),
                negative(
                    minusSymbol(),
                    number(456)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParsePowerBigDecimal() {
        this.valueOrExpressionParserParsePower();
    }

    @Test
    public void testValueOrExpressionParserParsePowerDouble() {
        this.valueOrExpressionParserParsePower();
    }

    private void valueOrExpressionParserParsePower() {
        final String text = "123^456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            power(
                number(123),
                powerSymbol(),
                number(456)
            ),
            text,
            text.replace("^", "^^")
        );
    }

    @Test
    public void testValueOrExpressionParserParsePower2BigDecimal() {
        this.valueOrExpressionParserParsePower2();
    }

    @Test
    public void testValueOrExpressionParserParsePower2Double() {
        this.valueOrExpressionParserParsePower2();
    }

    private void valueOrExpressionParserParsePower2() {
        final String text = "111*222^333";

        this.valueOrExpressionParserParseAndCheck(
            text,
            multiplication(
                number(111),
                multiplySymbol(),
                power(
                    (SpreadsheetFormulaParserToken) number(222),
                    powerSymbol(),
                    (SpreadsheetFormulaParserToken) number(333)
                )
            ),
            text,
            text.replace("^", "^^")
        );
    }

    @Test
    public void testValueOrExpressionParserParseEqualsBigDecimal() {
        this.valueOrExpressionParserParseEquals();
    }

    @Test
    public void testValueOrExpressionParserParseEqualsDouble() {
        this.valueOrExpressionParserParseEquals();
    }

    private void valueOrExpressionParserParseEquals() {
        final String text = "123=456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            equals(
                number(123),
                equalsSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseEqualsAddBigDecimal() {
        this.valueOrExpressionParserParseEqualsAdd();
    }

    @Test
    public void testValueOrExpressionParserParseEqualsAddDouble() {
        this.valueOrExpressionParserParseEqualsAdd();
    }

    private void valueOrExpressionParserParseEqualsAdd() {
        final String text = "123=456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            equals(
                number(123),
                equalsSymbol(),
                addition(
                    number(456),
                    plusSymbol(),
                    number(789)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseNotEqualsBigDecimal() {
        this.valueOrExpressionParserParseNotEquals();
    }

    @Test
    public void testValueOrExpressionParserParseNotEqualsDouble() {
        this.valueOrExpressionParserParseNotEquals();
    }

    private void valueOrExpressionParserParseNotEquals() {
        final String text = "123<>456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            notEquals(
                number(123),
                notEqualsSymbol(),
                number(456)
            ),
            text,
            text.replace("<>", "!=")
        );
    }

    @Test
    public void testValueOrExpressionParserParseNotEqualsAddBigDecimal() {
        this.valueOrExpressionParserParseNotEqualsAdd();
    }

    @Test
    public void testValueOrExpressionParserParseNotEqualsAddDouble() {
        this.valueOrExpressionParserParseNotEqualsAdd();
    }

    private void valueOrExpressionParserParseNotEqualsAdd() {
        final String addText = "456+789";
        final AdditionSpreadsheetFormulaParserToken add = addition(
            number(456),
            plusSymbol(),
            number(789)
        );

        final String text = "123<>" + addText;
        final NotEqualsSpreadsheetFormulaParserToken ne = notEquals(
            number(123),
            notEqualsSymbol(),
            add
        );

        this.valueOrExpressionParserParseAndCheck(
            text,
            ne,
            text,
            text.replace("<>", "!=")
        );
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanBigDecimal() {
        this.valueOrExpressionParserParseGreaterThan();
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanDouble() {
        this.valueOrExpressionParserParseGreaterThan();
    }

    private void valueOrExpressionParserParseGreaterThan() {
        final String text = "123>456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            greaterThan(
                number(123),
                greaterThanSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanAddBigDecimal() {
        this.valueOrExpressionParserParseGreaterThanAdd();
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanAddDouble() {
        this.valueOrExpressionParserParseGreaterThanAdd();
    }

    private void valueOrExpressionParserParseGreaterThanAdd() {
        final String text = "123>456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            greaterThan(
                number(123),
                greaterThanSymbol(),
                addition(
                    number(456),
                    plusSymbol(),
                    number(789)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanEqualsBigDecimal() {
        this.valueOrExpressionParserParseGreaterThanEquals();
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanEqualsDouble() {
        this.valueOrExpressionParserParseGreaterThanEquals();
    }

    private void valueOrExpressionParserParseGreaterThanEquals() {
        final String text = "123>=456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            greaterThanEquals(
                number(123),
                greaterThanEqualsSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanEqualsAddBigDecimal() {
        this.valueOrExpressionParserParseGreaterThanEqualsAdd();
    }

    @Test
    public void testValueOrExpressionParserParseGreaterThanEqualsAddDouble() {
        this.valueOrExpressionParserParseGreaterThanEqualsAdd();
    }

    private void valueOrExpressionParserParseGreaterThanEqualsAdd() {
        final String text = "123>=456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            greaterThanEquals(
                number(123),
                greaterThanEqualsSymbol(),
                addition(
                    number(456),
                    plusSymbol(),
                    number(789)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseLessThanBigDecimal() {
        this.valueOrExpressionParserParseLessThan();
    }

    @Test
    public void testValueOrExpressionParserParseLessThanDouble() {
        this.valueOrExpressionParserParseLessThan();
    }

    private void valueOrExpressionParserParseLessThan() {
        final String text = "123<456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            lessThan(
                number(123),
                lessThanSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseLessThanAddBigDecimal() {
        this.valueOrExpressionParserParseLessThanAdd();
    }

    @Test
    public void testValueOrExpressionParserParseLessThanAddDouble() {
        this.valueOrExpressionParserParseLessThanAdd();
    }

    private void valueOrExpressionParserParseLessThanAdd() {
        final String text = "123<456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            lessThan(
                number(123),
                lessThanSymbol(),
                addition(
                    number(456),
                    plusSymbol(),
                    number(789)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseLessThanEqualsBigDecimal() {
        this.valueOrExpressionParserParseLessThanEquals();
    }

    @Test
    public void testValueOrExpressionParserParseLessThanEqualsDouble() {
        this.valueOrExpressionParserParseLessThanEquals();
    }

    private void valueOrExpressionParserParseLessThanEquals() {
        final String text = "123<=456";

        this.valueOrExpressionParserParseAndCheck(
            text,
            lessThanEquals(
                number(123),
                lessThanEqualsSymbol(),
                number(456)
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseLessThanEqualsAddBigDecimal() {
        this.valueOrExpressionParserParseLessThanEqualsAdd();
    }

    @Test
    public void testValueOrExpressionParserParseLessThanEqualsAddDouble() {
        this.valueOrExpressionParserParseLessThanEqualsAdd();
    }

    private void valueOrExpressionParserParseLessThanEqualsAdd() {
        final String text = "123<=456+789";

        this.valueOrExpressionParserParseAndCheck(
            text,
            lessThanEquals(
                number(123),
                lessThanEqualsSymbol(),
                addition(
                    number(456),
                    plusSymbol(),
                    number(789)
                )
            ),
            text
        );
    }

    @Test
    public void testValueOrExpressionParserParseComplexExpressionBigDecimal() {
        this.valueOrExpressionParserParseComplexExpression();
    }

    @Test
    public void testValueOrExpressionParserParseComplexExpressionDouble() {
        this.valueOrExpressionParserParseComplexExpression();
    }

    private void valueOrExpressionParserParseComplexExpression() {
        //111+222+(-333)-444*555
        final String addText = "111+222";
        final AdditionSpreadsheetFormulaParserToken add = addition(
            number(111),
            plusSymbol(),
            number(222)
        );

        final GroupSpreadsheetFormulaParserToken group = group(
            parenthesisOpen(),
            negative(
                minusSymbol(),
                number(333)
            ),
            parenthesisClose()
        );

        final String addText2 = add + "+" + group.text();
        final AdditionSpreadsheetFormulaParserToken add2 = addition(
            add,
            plusSymbol(),
            group
        );

        final String multiplyText = "444*555";
        final MultiplicationSpreadsheetFormulaParserToken multiply = multiplication(
            number(444),
            multiplySymbol(),
            number(555)
        );

        final String subText = addText2 + "-" + multiplyText;
        final SubtractionSpreadsheetFormulaParserToken sub = subtraction(
            add2,
            minusSymbol(),
            multiply
        );

        this.valueOrExpressionParserParseAndCheck(
            subText,
            sub,
            subText,
            subText.replace("(", "").replace(")", "")
        );
    }

    // FunctionParameters...............................................................................................

    @Test
    public void testFunctionParametersParserParseFunctionParametersEmpty() {
        this.functionParametersParserParseAndCheck(
            "()",
            functionParameters(
                parenthesisOpen(),
                parenthesisClose()
            )
        );
    }

    @Test
    public void testFunctionParametersParserParseFunctionParametersEmptyOnlySpaces() {
        this.functionParametersParserParseAndCheck(
            "(  )",
            functionParameters(
                parenthesisOpen(),
                whitespace2(),
                parenthesisClose()
            )
        );
    }

    @Test
    public void testFunctionParametersParserParseFunctionParametersOneNumberParameter() {
        this.functionParametersParserParseAndCheck(
            "(1)",
            functionParameters(
                parenthesisOpen(),
                number(1),
                parenthesisClose()
            )
        );
    }

    @Test
    public void testFunctionParametersParserParseFunctionParametersOneStringParameter() {
        this.functionParametersParserParseAndCheck(
            "(\"abc\")",
            functionParameters(
                parenthesisOpen(),
                text(
                    doubleQuotesSymbol(),
                    textLiteral("abc"),
                    doubleQuotesSymbol()
                ),
                parenthesisClose()
            )
        );
    }

    @Test
    public void testFunctionParametersParserParseFunctionParametersTwoNumberParameters() {
        this.functionParametersParserParseAndCheck(
            "(1;2)",
            functionParameters(
                parenthesisOpen(),
                number(1),
                valueSeparatorSymbol(),
                number(2),
                parenthesisClose()
            )
        );
    }

    @Test
    public void testFunctionParametersParserParseFunctionParametersThreeNumberParameters() {
        this.functionParametersParserParseAndCheck(
            "(1;2;345)",
            functionParameters(
                parenthesisOpen(),
                number(1),
                valueSeparatorSymbol(),
                number(2),
                valueSeparatorSymbol(),
                number(345),
                parenthesisClose()
            )
        );
    }

    private void functionParametersParserParseAndCheck(final String text,
                                                       final ParserToken token) {
        this.functionParametersParserParseAndCheck(
            text,
            token,
            ""
        );
    }

    private void functionParametersParserParseAndCheck(final String text,
                                                       final ParserToken token,
                                                       final String textAfter) {
        this.parseAndCheck(
            functionParametersParser(),
            text,
            token,
            text,
            textAfter
        );
    }

    @Test
    public void testFunctionParametersParserToString() {
        this.checkEquals(
            "FUNCTION_PARAMETERS",
            SpreadsheetFormulaParsers.functionParameters()
                .toString()
        );
    }

    // LambdaFunction....................................................................................................

    @Test
    public void testLambdaFunctionOtherExpressionFails() {
        this.parseFailAndCheck(
            this.lambdaFunctionParser(),
            "1+2"
        );
    }

    @Test
    public void testLambdaFunctionParserParseLambdaFunctionNameWithNumberParameter() {
        final String text = "lambda(x;x+x)(1)";
        final LabelSpreadsheetFormulaParserToken x = this.label("x");

        final LambdaFunctionSpreadsheetFormulaParserToken lambda = lambdaFunction(
            functionName("lambda"),
            functionParameters(
                parenthesisOpen(),
                x,
                valueSeparatorSymbol(),
                addition(
                    x,
                    plusSymbol(),
                    x
                ),
                parenthesisClose()
            ),
            functionParameters(
                parenthesisOpen(),
                number(1),
                parenthesisClose()
            )
        );

        this.lambdaFunctionParserParseAndCheck(
            text,
            lambda,
            text
        );
    }

    @Test
    public void testLambdaFunctionParserParseLambdaFunctionNameWithStringParameter() {
        final String text = "lambda(x;x+x)(\"Hello\")";
        final LabelSpreadsheetFormulaParserToken x = this.label("x");

        final LambdaFunctionSpreadsheetFormulaParserToken lambda = lambdaFunction(
            functionName("lambda"),
            functionParameters(
                parenthesisOpen(),
                x,
                valueSeparatorSymbol(),
                addition(
                    x,
                    plusSymbol(),
                    x
                ),
                parenthesisClose()
            ),
            functionParameters(
                parenthesisOpen(),
                text(
                    doubleQuotesSymbol(),
                    textLiteral("Hello"),
                    doubleQuotesSymbol()
                ),
                parenthesisClose()
            )
        );

        this.lambdaFunctionParserParseAndCheck(
            text,
            lambda,
            text
        );
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetFormulaParsers#lambdaFunction()} and then repeat again with
     * {@link SpreadsheetFormulaParsers#expression()}. Both should give the same results.
     */
    private void lambdaFunctionParserParseAndCheck(final String from,
                                                   final LambdaFunctionSpreadsheetFormulaParserToken expected,
                                                   final String text) {
        this.lambdaFunctionParserParseAndCheck(
            from,
            expected,
            text,
            text
        );
    }

    private void lambdaFunctionParserParseAndCheck(final String from,
                                                   final LambdaFunctionSpreadsheetFormulaParserToken expected,
                                                   final String text,
                                                   final String expressionToString) {
        this.parseAndCheck(
            lambdaFunctionParser(),
            from,
            expected,
            text
        );
        this.valueOrExpressionParserParseAndCheck(
            from,
            expected,
            text,
            expressionToString
        );
    }

    @Test
    public void testLambdaFunctionParserToString() {
        this.checkEquals(
            "LAMBDA_FUNCTION",
            lambdaFunctionParser()
                .toString()
        );
    }

    private Parser<SpreadsheetParserContext> lambdaFunctionParser() {
        return SpreadsheetFormulaParsers.lambdaFunction();
    }

    // NamedFunction....................................................................................................

    @Test
    public void testNamedFunctionParserParseNumberExpressionFails() {
        this.parseFailAndCheck(
            this.namedFunctionParser(),
            "1+2"
        );
    }

    @Test
    public void testNamedFunctionParserParseWithoutArguments() {
        final String text = "xyz()";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text
        );
    }

    @Test
    public void testNamedFunctionNameWithDotWithoutArguments() {
        final String text = "Error.Type()";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("Error.Type"),
            functionParameters(
                parenthesisOpen(),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text
        );
    }

    @Test
    public void testNamedFunctionParserParseWithoutArgumentsWhitespace() {
        final String text = "xyz(  )";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                whitespace2(),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text,
            "xyz()"
        );
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgumentBigDecimal() {
        this.namedFunctionParserParseWithOneArgument();
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgumentDouble() {
        this.namedFunctionParserParseWithOneArgument();
    }

    private void namedFunctionParserParseWithOneArgument() {
        final String text = "xyz(123)";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                number(123),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text
        );
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgument2BigDecimal() {
        this.namedFunctionParserParseWithOneArgument2();
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgument2Double() {
        this.namedFunctionParserParseWithOneArgument2();
    }

    private void namedFunctionParserParseWithOneArgument2() {
        final String text = "xyz(  123)";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                whitespace2(),
                number(123),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text,
            "xyz(123)"
        );
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgument3BigDecimal() {
        this.namedFunctionParserParseWithOneArgument3();
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgument3Double() {
        this.namedFunctionParserParseWithOneArgument3();
    }

    private void namedFunctionParserParseWithOneArgument3() {
        final String text = "xyz(123  )";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                number(123),
                whitespace2(),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text,
            "xyz(123)"
        );
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgument4BigDecimal() {
        this.namedFunctionParserParseWithOneArgument4();
    }

    @Test
    public void testNamedFunctionParserParseWithOneArgument4Double() {
        this.namedFunctionParserParseWithOneArgument4();
    }

    private void namedFunctionParserParseWithOneArgument4() {
        final String text = "xyz(  123  )";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                whitespace2(),
                number(123),
                whitespace2(),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text,
            "xyz(123)"
        );
    }

    @Test
    public void testNamedFunctionParserParseTwoArgumentsBigDecimal() {
        this.namedFunctionParserParseTwoArguments();
    }

    @Test
    public void testNamedFunctionParserParseTwoArgumentsDouble() {
        this.namedFunctionParserParseTwoArguments();
    }

    private void namedFunctionParserParseTwoArguments() {
        final String text = "xyz(123;456)";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                number(123),
                valueSeparatorSymbol(),
                number(456),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text,
            text.replace(";", ",")
        );
    }

    @Test
    public void testNamedFunctionParserParseFourArgumentsBigDecimal() {
        this.namedFunctionParserParseFourArguments();
    }

    @Test
    public void testNamedFunctionParserParseFourArgumentsDouble() {
        this.namedFunctionParserParseFourArguments();
    }

    private void namedFunctionParserParseFourArguments() {
        final String text = "xyz(1;2;3;4)";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                number(1),
                valueSeparatorSymbol(),
                number(2),
                valueSeparatorSymbol(),
                number(3),
                valueSeparatorSymbol(),
                number(4),
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            text,
            f,
            text,
            "xyz(1,2,3,4)"
        );
    }

    @Test
    public void testNamedFunctionParserParseFunctionBigDecimal() {
        this.namedFunctionParserParseFunction();
    }

    @Test
    public void testNamedFunctionParserParseFunctionDouble() {
        this.namedFunctionParserParseFunction();
    }

    private void namedFunctionParserParseFunction() {
        final String yText = "y(123)";
        final NamedFunctionSpreadsheetFormulaParserToken y = namedFunction(
            functionName("y"),
            functionParameters(
                parenthesisOpen(),
                number(123),
                parenthesisClose()
            )
        );

        final String xText = "x(" + yText + ")";
        final NamedFunctionSpreadsheetFormulaParserToken x = namedFunction(
            functionName("x"),
            functionParameters(
                parenthesisOpen(),
                y,
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            xText,
            x,
            xText
        );
    }

    @Test
    public void testNamedFunctionParserParseFunctionWithFunctionBigDecimal() {
        this.namedFunctionParserParseFunctionWithFunction();
    }

    @Test
    public void testNamedFunctionParserParseFunctionWithFunctionDouble() {
        this.namedFunctionParserParseFunctionWithFunction();
    }

    private void namedFunctionParserParseFunctionWithFunction() {
        final String zText = "z(123)";
        final NamedFunctionSpreadsheetFormulaParserToken z = namedFunction(
            functionName("z"),
            functionParameters(
                parenthesisOpen(),
                number(123),
                parenthesisClose()
            )
        );

        final String yText = "y(" + zText + ")";
        final NamedFunctionSpreadsheetFormulaParserToken y = namedFunction(

            functionName("y"),
            functionParameters(
                parenthesisOpen(),
                z,
                parenthesisClose()
            )
        );

        final String xText = "x(" + yText + ")";
        final NamedFunctionSpreadsheetFormulaParserToken x = namedFunction(

            functionName("x"),
            functionParameters(
                parenthesisOpen(),
                y,
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(
            xText,
            x,
            xText
        );
    }

    @Test
    public void testNamedFunctionParserParseWithRangeArgumentBigDecimal() {
        this.namedFunctionParserParseWithRangeArgument();
    }

    @Test
    public void testNamedFunctionParserParseWithRangeArgumentDouble() {
        this.namedFunctionParserParseWithRangeArgument();
    }

    private void namedFunctionParserParseWithRangeArgument() {
        final CellSpreadsheetFormulaParserToken from = this.cell(0, "A", 0);
        final CellSpreadsheetFormulaParserToken to = this.cell(1, "B", 1);

        final CellRangeSpreadsheetFormulaParserToken range = range(from, to);
        final String rangeText = range.text();

        final String text = "xyz(" + rangeText + ")";
        final NamedFunctionSpreadsheetFormulaParserToken f = namedFunction(
            functionName("xyz"),
            functionParameters(
                parenthesisOpen(),
                range,
                parenthesisClose()
            )
        );

        this.namedFunctionParseAndCheck(text, f, text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetFormulaParsers#namedFunction()} and then repeat again with
     * {@link SpreadsheetFormulaParsers#expression()}. Both should give the same results.
     */
    private void namedFunctionParseAndCheck(final String from,
                                            final NamedFunctionSpreadsheetFormulaParserToken expected,
                                            final String text) {
        this.namedFunctionParseAndCheck(
            from,
            expected,
            text,
            text
        );
    }

    private void namedFunctionParseAndCheck(final String from,
                                            final NamedFunctionSpreadsheetFormulaParserToken expected,
                                            final String text,
                                            final String expressionToString) {
        this.parseAndCheck(
            namedFunctionParser(),
            from,
            expected,
            text
        );
        this.valueOrExpressionParserParseAndCheck(
            from,
            expected,
            text,
            expressionToString
        );
    }

    @Test
    public void testNamedFunctionParserToString() {
        this.checkEquals(
            "NAMED_FUNCTION",
            namedFunctionParser()
                .toString()
        );
    }

    private Parser<SpreadsheetParserContext> namedFunctionParser() {
        return SpreadsheetFormulaParsers.namedFunction();
    }

    // Group ...........................................................................................................

    @Test
    public void testValueOrExpressionParserParseGroupAndFurtherExpressionsBigDecimal() {
        this.valueOrExpressionParserParseGroupAndFurtherExpressions();
    }

    @Test
    public void testValueOrExpressionParserParseGroupAndFurtherExpressionsDouble() {
        this.valueOrExpressionParserParseGroupAndFurtherExpressions();
    }

    private void valueOrExpressionParserParseGroupAndFurtherExpressions() {
        final SpreadsheetFormulaParserToken left = number(1);
        final SpreadsheetFormulaParserToken right = number(2);
        final AdditionSpreadsheetFormulaParserToken add = addition(
            left,
            plusSymbol(),
            right
        );
        final GroupSpreadsheetFormulaParserToken group = group(
            this.parenthesisOpen(),
            add,
            this.parenthesisClose()
        );

        final SpreadsheetFormulaParserToken last = number(3);
        final MultiplicationSpreadsheetFormulaParserToken mul = multiplication(
            group,
            multiplySymbol(),
            last
        );

        this.valueOrExpressionParserParseAndCheck(
            mul.text(),
            mul,
            mul.text(),
            "1+2*3"
        );
    }

    @Test
    public void testValueOrExpressionParserParseNestedGroupAdditionBigDecimal() {
        this.valueOrExpressionParserParseNestedGroupAddition();
    }

    @Test
    public void testValueOrExpressionParserParseestedGroupAdditionDouble() {
        this.valueOrExpressionParserParseNestedGroupAddition();
    }

    private void valueOrExpressionParserParseNestedGroupAddition() {
        final SpreadsheetFormulaParserToken left1 = number(1);
        final SpreadsheetFormulaParserToken right1 = number(2);
        final AdditionSpreadsheetFormulaParserToken add1 = addition(
            left1,
            plusSymbol(),
            right1
        );
        final GroupSpreadsheetFormulaParserToken group1 = group(
            this.parenthesisOpen(),
            add1,
            this.parenthesisClose()
        );

        final SpreadsheetFormulaParserToken left2 = number(3);
        final SpreadsheetFormulaParserToken right2 = number(4);
        final AdditionSpreadsheetFormulaParserToken add2 = addition(
            left2,
            plusSymbol(),
            right2
        );
        final GroupSpreadsheetFormulaParserToken group2 = group(
            this.parenthesisOpen(),
            add2,
            this.parenthesisClose()
        );

        final MultiplicationSpreadsheetFormulaParserToken mul = multiplication(
            group1,
            multiplySymbol(),
            group2
        );

        final String text = mul.text();
        this.valueOrExpressionParserParseAndCheck(
            text,
            mul,
            text,
            text.replace("(", "").replace(")", "")
        );
    }

    @Test
    public void testValueOrExpressionParserParseInvalidTokenFails() {
        this.valueOrExpressionParserParseFails(
            "!!!",
            "Invalid character '!' at 0 expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testValueOrExpressionParserParseInvalidTokenFails2() {
        this.valueOrExpressionParserParseFails(
            "  !",
            "Invalid character '!' at 2 expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testValueOrExpressionParserParseInvalidBinaryTokenRightFailsBigDecimal() {
        this.valueOrExpressionParserParseFails();
    }

    @Test
    public void testValueOrExpressionParserParseInvalidBinaryTokenRightFailsDouble() {
        this.valueOrExpressionParserParseFails();
    }

    private void valueOrExpressionParserParseFails() {
        this.valueOrExpressionParserParseFails(
            "1+!",
            "Invalid character '!' at 2 expected LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testParseInvalidGroupTokenFails() {
        this.valueOrExpressionParserParseFails(
            "( !",
            "Invalid character '!' at 2 expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testParseAdditionBigDecimal() {
        this.testParseAddition();
    }

    @Test
    public void testParseAdditionDouble() {
        this.testParseAddition();
    }

    private void testParseAddition() {
        this.parseEvaluateAndCheck(
            "1+2",
            1 + 2
        );
    }

    @Test
    public void testParseAdditionLeadingWhitespaceBigDecimal() {
        this.testParseAdditionLeadingWhitespace();
    }

    @Test
    public void testParseAdditionLeadingWhitespaceDouble() {
        this.testParseAdditionLeadingWhitespace();
    }

    private void testParseAdditionLeadingWhitespace() {
        this.parseEvaluateAndCheck(
            " 1+2",
            1 + 2
        );
    }

    @Test
    public void testParseAdditionLeadingWhitespace2BigDecimal() {
        this.testParseAdditionLeadingWhitespace2();
    }

    @Test
    public void testParseAdditionLeadingWhitespace2Double() {
        this.testParseAdditionLeadingWhitespace2();
    }

    private void testParseAdditionLeadingWhitespace2() {
        this.parseEvaluateAndCheck(
            "  1+2",
            1 + 2
        );
    }

    @Test
    public void testParseAdditionTrailingWhitespaceBigDecimal() {
        this.testParseAdditionTrailingWhitespace();
    }

    @Test
    public void testParseAdditionTrailingWhitespaceDouble() {
        this.testParseAdditionTrailingWhitespace();
    }

    private void testParseAdditionTrailingWhitespace() {
        this.parseEvaluateAndCheck(
            "1+2 ",
            1 + 2
        );
    }

    @Test
    public void testParseAdditionTrailingWhitespace2BigDecimal() {
        this.testParseAdditionTrailingWhitespace2();
    }

    @Test
    public void testParseAdditionTrailingWhitespace2Double() {
        this.testParseAdditionTrailingWhitespace2();
    }

    private void testParseAdditionTrailingWhitespace2() {
        this.parseEvaluateAndCheck(
            "1+2  ",
            1 + 2
        );
    }

    @Test
    public void testParseAdditionSurroundedByWhitespaceBigDecimal() {
        this.testParseAdditionSurroundedByWhitespace();
    }

    @Test
    public void testParseAdditionSurroundedByWhitespaceDouble() {
        this.testParseAdditionSurroundedByWhitespace();
    }

    private void testParseAdditionSurroundedByWhitespace() {
        this.parseEvaluateAndCheck(
            " 1+2 ",
            1 + 2
        );
    }

    @Test
    public void testParseMultiplicationBigDecimal() {
        this.testParseMultiplication();
    }

    @Test
    public void testParseMultiplicationDouble() {
        this.testParseMultiplication();
    }

    private void testParseMultiplication() {
        this.parseEvaluateAndCheck(
            "3*4.5",
            3 * 4.5
        );
    }

    @Test
    public void testParseMathOperatorPriorityBigDecimal() {
        this.testParseMathOperatorPriority();
    }

    @Test
    public void testParseMathOperatorPriorityDouble() {
        this.testParseMathOperatorPriority();
    }

    private void testParseMathOperatorPriority() {
        this.parseEvaluateAndCheck(
            "1+2*3+4.5",
            1 + 2 * 3 + 4.5
        );
    }

    @Test
    public void testParseParenthesisBigDecimal() {
        this.testParseParenthesis();
    }

    @Test
    public void testParseParenthesisDouble() {
        this.testParseParenthesis();
    }

    private void testParseParenthesis() {
        this.parseEvaluateAndCheck(
            "((1+2))",
            ((1 + 2))
        );
    }

    @Test
    public void testParseParenthesis2BigDecimal() {
        this.testParseParenthesis2();
    }

    @Test
    public void testParseParenthesis2Double() {
        this.testParseParenthesis2();
    }

    private void testParseParenthesis2() {
        this.parseEvaluateAndCheck(
            "(1+2)*3.5",
            (1 + 2) * 3.5
        );
    }

    @Test
    public void testParseParenthesis3BigDecimal() {
        this.testParseParenthesis3();
    }

    @Test
    public void testParseParenthesis3Double() {
        this.testParseParenthesis3();
    }

    private void testParseParenthesis3() {
        this.parseEvaluateAndCheck(
            "((1+2)*3.5)",
            ((1 + 2) * 3.5)
        );
    }

    @Test
    public void testParseParenthesis4BigDecimal() {
        this.testParseParenthesis4();
    }

    @Test
    public void testParseParenthesis4Double() {
        this.testParseParenthesis4();
    }

    private void testParseParenthesis4() {
        this.parseEvaluateAndCheck("(1+2)+(3+4.5)", (1 + 2) + (3 + 4.5));
    }

    @Test
    public void testParseParenthesis5BigDecimal() {
        this.testParseParenthesis5();
    }

    @Test
    public void testParseParenthesis5Double() {
        this.testParseParenthesis5();
    }

    private void testParseParenthesis5() {
        assertEquals(-42.0, (1 + 2) + (3 + 4.5) * -6, 0.5);
        this.parseEvaluateAndCheck(
            "(1+2)+(3+4.5)*-6",
            -42
        );
    }

    @Test
    public void testParseParenthesis6BigDecimal() {
        this.testParseParenthesis6();
    }

    @Test
    public void testParseParenthesis6Double() {
        this.testParseParenthesis6();
    }

    private void testParseParenthesis6() {
        this.parseEvaluateAndCheck(
            "(1+2*(3+4*(5+6)*-7))",
            (1 + 2 * (3 + 4 * (5 + 6) * -7))
        );
    }

    @Test
    public void testParseParenthesis7BigDecimal() {
        this.testParseParenthesis7();
    }

    @Test
    public void testParseParenthesis7Double() {
        this.testParseParenthesis7();
    }

    private void testParseParenthesis7() {
        this.parseEvaluateAndCheck("-(1+2*(3+4*(5+6)*-7))", -(1 + 2 * (3 + 4 * (5 + 6) * -7)));
    }

    @Test
    public void testParseParenthesis8BigDecimal() {
        this.testParseParenthesis8();
    }

    @Test
    public void testParseParenthesis8Double() {
        this.testParseParenthesis8();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testParseParenthesis8() {
        this.parseEvaluateAndCheck(
            "-(1+2*(3+4*(5+6)*-(7*8+(9+0))))",
            -(1 + 2 * (3 + 4 * (5 + 6) * -(7 * 8 + (9 + 0))))
        );
    }

    @Test
    public void testParseParenthesis9BigDecimal() {
        this.testParseParenthesis9();
    }

    @Test
    public void testParseParenthesis9Double() {
        this.testParseParenthesis9();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testParseParenthesis9() {
        this.parseEvaluateAndCheck(
            "((((1+2))))",
            ((((1 + 2))))
        );
    }

    @Test
    public void testParseParenthesis10BigDecimal() {
        this.testParseParenthesis10();
    }

    @Test
    public void testParseParenthesis10Double() {
        this.testParseParenthesis10();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testParseParenthesis10() {
        this.parseEvaluateAndCheck(
            "-(-(-(-(1+2))))",
            -(-(-(-(1 + 2))))
        );
    }

    @Test
    public void testParseLongFormulaWithoutParensBigDecimal() {
        this.testParseLongFormulaWithoutParens();
    }

    @Test
    public void testParseLongFormulaWithoutParensDouble() {
        this.testParseLongFormulaWithoutParens();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testParseLongFormulaWithoutParens() {
        this.parseEvaluateAndCheck(
            "1+2-3+4-5*6+7-8*9",
            1 + 2 - 3 + 4 - 5 * 6 + 7 - 8 * 9
        );
    }

    @Test
    public void testParseLongFormulaWithoutParens2BigDecimal() {
        this.testParseLongFormulaWithoutParens2();
    }

    @Test
    public void testParseLongFormulaWithoutParens2Double() {
        this.testParseLongFormulaWithoutParens2();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testParseLongFormulaWithoutParens2() {
        this.parseEvaluateAndCheck(
            "-1+2-3+4-5*6+7-8*9",
            -1 + 2 - 3 + 4 - 5 * 6 + 7 - 8 * 9
        );
    }

    @Test
    public void testParseLocalDateSubtractionBigDecimal() {
        this.testParseLocalDateSubtraction();
    }

    @Test
    public void testParseLocalDateSubtractionDouble() {
        this.testParseLocalDateSubtraction();
    }

    private void testParseLocalDateSubtraction() {
        this.parseEvaluateAndCheck(
            "test.toDate(\"2000-01-03\")-test.toDate(\"1999-12-31\")",
            3
        );// days!
    }

    @Test
    public void testParseLocalDateTimeSubtractionBigDecimal() {
        this.testParseLocalDateTimeSubtraction();
    }

    @Test
    public void testParseLocalDateTimeSubtractionDouble() {
        this.testParseLocalDateTimeSubtraction();
    }

    private void testParseLocalDateTimeSubtraction() {
        this.parseEvaluateAndCheck("test.toDateTime(\"2000-02-01T12:00:00\")-test.toDateTime(\"2000-01-31T06:00:00\")", 1.25); //1 1/4days
    }

    @Test
    public void testParseLocalTimeSubtractionBigDecimal() {
        this.testParseLocalTimeSubtraction();
    }

    @Test
    public void testParseLocalTimeSubtractionDouble() {
        this.testParseLocalTimeSubtraction();
    }

    private void testParseLocalTimeSubtraction() {
        this.parseEvaluateAndCheck(
            "test.toTime(test.toTime(\"18:00:00\")-test.toTime(\"06:00:00\"))",
            "12:00"
        ); //1/2 a day or 12noon
    }

    @Test
    public void testParseErrorDotType() {
        this.parseEvaluateAndCheck(
            "Error.Type()",
            "Hello"
        );
    }

    // helpers..........................................................................................................

    private void valueOrExpressionParserParseFails(final String text,
                                                   final String message) {
        this.parseThrows(
            SpreadsheetFormulaParsers.expression(),
            text,
            message
        );

        final int at = message.indexOf("at ") + 3;
        final int spaceAfter = message.indexOf(" expected");

        final int position = Integer.parseInt(
            message.substring(at, spaceAfter)
        ) + 1;

        //+1 to the column number in the message
        this.parseThrows(
            valueOrExpressionParser(),
            "=" + text,
            message.substring(
                0,
                at
            ) + position +
                message.substring(spaceAfter)
        );
    }

    private void valueOrExpressionParserParseAndCheck(final String formula,
                                                      final ParserToken token,
                                                      final String text) {
        this.valueOrExpressionParserParseAndCheck(
            formula,
            token,
            text,
            text
        );
    }

    private void valueOrExpressionParserParseAndCheck(final String formula,
                                                      final ParserToken token,
                                                      final String text,
                                                      final String expressionToString) {
        this.parseAndCheck(
            expressionParser(),
            formula,
            token,
            text,
            ""
        );

        final String equalsFormula = "=" + formula;
        this.parseAndCheck(
            valueOrExpressionParser(),
            equalsFormula,
            expression(
                equalsSymbol(),
                token
            ),
            equalsFormula,
            ""
        );

        final SpreadsheetFormulaParserToken spreadsheetFormulaParserToken = token.cast(SpreadsheetFormulaParserToken.class);

        this.checkEquals(
            expressionToString.replace(';', ','),
            spreadsheetFormulaParserToken.toExpression(
                    ExpressionEvaluationContexts.basic(
                        ExpressionNumberKind.DEFAULT,
                        (n) -> {
                            throw new UnsupportedOperationException();
                        },
                        (n) -> {
                            throw new UnsupportedOperationException();
                        },
                        (r) -> {
                            throw new UnsupportedOperationException();
                        },
                        (r) -> {
                            throw new UnsupportedOperationException();
                        },
                        CaseSensitivity.SENSITIVE,
                        ConverterContexts.fake(),
                        LocaleContexts.fake()
                    )
                ).map(Object::toString)
                .orElse("")
        );
    }

    private void parseEvaluateAndCheck(final String formulaText,
                                       final Object expectedText) {
        this.parseEvaluateAndCheck(formulaText, String.valueOf(expectedText));
    }

    /**
     * Accepts a formula with an expression. Note the expression is assumed to NOT having the leading equals sign.
     * The second part of the test will prefix an equals sign and attempt to parse using the {@link SpreadsheetFormulaParsers#valueOrExpression} parser.
     */
    private void parseEvaluateAndCheck(final String formulaText,
                                       final String expectedText) {
        this.parseEvaluateAndCheck0(
            expressionParser(),
            formulaText,
            expectedText
        );

        this.parseEvaluateAndCheck0(
            valueOrExpressionParser(),
            "=" + formulaText,
            expectedText
        );

        this.parseEvaluateAndCheck0(
            valueOrExpressionParser(),
            "= " + formulaText,
            expectedText
        );
    }

    private void parseEvaluateAndCheck0(final Parser<SpreadsheetParserContext> parser,
                                        final String formulaText,
                                        final String expectedText) {
        this.parseExpressionEvaluateAndCheck1(
            parser,
            formulaText,
            ExpressionNumberKind.BIG_DECIMAL,
            TWO_DIGIT_YEAR,
            expectedText
        );
        this.parseExpressionEvaluateAndCheck1(
            parser,
            formulaText,
            ExpressionNumberKind.DOUBLE,
            TWO_DIGIT_YEAR,
            expectedText
        );
    }

    private void parseExpressionEvaluateAndCheck1(final Parser<SpreadsheetParserContext> parser,
                                                  final String formulaText,
                                                  final ExpressionNumberKind kind,
                                                  final int twoDigitYear,
                                                  final String expectedText) {
        final SpreadsheetFormulaParserToken formula = this.parse(parser, formulaText);
        final Optional<Expression> maybeExpression = formula.toExpression(
            new FakeExpressionEvaluationContext() {

                @Override
                public ExpressionNumberKind expressionNumberKind() {
                    return expressionNumberKind;
                }

                @Override
                public int twoDigitYear() {
                    return twoDigitYear;
                }
            }
        );
        if (!maybeExpression.isPresent()) {
            fail("Failed to convert spreadsheet formula to expression " + CharSequences.quoteAndEscape(formulaText));
        }
        final Expression expression = maybeExpression.get();
        final String value = expression.toString(this.expressionEvaluationContext(kind));
        this.checkEquals(expectedText, value, () -> "expression " + CharSequences.quoteAndEscape(formulaText) + " as text is");
    }

    private static Parser<SpreadsheetParserContext> expressionParser() {
        return SpreadsheetFormulaParsers.expression();
    }

    private static Parser<SpreadsheetParserContext> functionParametersParser() {
        return SpreadsheetFormulaParsers.functionParameters();
    }

    private static Parser<SpreadsheetParserContext> valueOrExpressionParser() {
        return SpreadsheetFormulaParsers.valueOrExpression(
            Parsers.alternatives(
                Lists.of(
                    SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").parser(),
                    SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").parser(),
                    SpreadsheetPattern.parseNumberParsePattern("#;#.#").parser(),
                    SpreadsheetPattern.parseTimeParsePattern("hh:mm").parser()
                )
            )
        );
    }

    private SpreadsheetFormulaParserToken parse(final Parser<SpreadsheetParserContext> parser,
                                                final String parse) {
        final TextCursor cursor = TextCursors.charSequence(parse);
        final Optional<ParserToken> spreadsheetFormula = parser.parse(cursor, this.createContext());
        if (!spreadsheetFormula.isPresent()) {
            fail("Parser failed to parse " + CharSequences.quoteAndEscape(parse));
        }

        final TextCursorSavePoint after = cursor.save();
        cursor.end();
        final String leftOver = after.textBetween().toString();
        if (!leftOver.isEmpty()) {
            fail("Parser left " + CharSequences.quoteAndEscape(leftOver) + " parse " + CharSequences.quoteAndEscape(parse));
        }
        return spreadsheetFormula.get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    private ExpressionEvaluationContext expressionEvaluationContext(final ExpressionNumberKind kind) {
        final Function<ConverterContext, ParserContext> parserContext = (c) -> ParserContexts.basic(
            false, // canNumbersHaveGroupSeparator
            InvalidCharacterExceptionFactory.POSITION,
            ',', // valueSeparator
            c,
            c
        );

        final Converter<ExpressionNumberConverterContext> stringDouble = Converters.parser(
            Double.class,
            Parsers.doubleParser(),
            parserContext,
            (t, c) -> t.cast(DoubleParserToken.class).value()
        ).cast(ExpressionNumberConverterContext.class);

        final Converter<ExpressionNumberConverterContext> stringLocalDate = Converters.parser(
            LocalDate.class,
            Parsers.localDate((c) -> DateTimeFormatter.ISO_LOCAL_DATE),
            parserContext,
            (t, c) -> t.cast(LocalDateParserToken.class).value()
        ).cast(ExpressionNumberConverterContext.class);

        final Converter<ExpressionNumberConverterContext> stringLocalDateTime = Converters.parser(
            LocalDateTime.class,
            Parsers.localDateTime((c) -> DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            parserContext,
            (t, c) -> t.cast(LocalDateTimeParserToken.class).value()
        ).cast(ExpressionNumberConverterContext.class);

        final Converter<ExpressionNumberConverterContext> stringLocalTime = Converters.parser(
            LocalTime.class,
            Parsers.localTime((c) -> DateTimeFormatter.ISO_LOCAL_TIME),
            parserContext,
            (t, c) -> t.cast(LocalTimeParserToken.class).value()
        ).cast(ExpressionNumberConverterContext.class);

        final Converter<ExpressionNumberConverterContext> converter = Converters.collection(
            Lists.of(
                Converters.object(),
                Converters.simple(),
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.numberToNumber()),
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localDateToNumber()),
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localDateTimeToNumber()),
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localTimeToNumber()),
                ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                    .to(
                        Number.class,
                        Converters.numberToLocalTime()
                    ),
                ExpressionNumberConverters.toNumberOrExpressionNumber(stringDouble),
                stringLocalDate,
                stringLocalDateTime,
                stringLocalTime,
                Converters.objectToString()
            )
        );

        return new FakeExpressionEvaluationContext() {

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return kind;
            }

            @Override
            public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                          final Object value) {
                return parameter.convertOrFail(value, this);
            }

            @Override
            public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
                switch (name.value()) {
                    case "Error.Type":
                        return function(
                            (p, c) -> "Hello"
                        );
                    case "test.toDate":
                        return function(
                            (p, c) -> convertStringParameter(p, LocalDate.class)
                        );
                    case "test.toDateTime":
                        return function(
                            (p, c) -> convertStringParameter(p, LocalDateTime.class)
                        );
                    case "test.toTime":
                        return function(
                            (p, c) -> convertStringParameter(p, LocalTime.class)
                        );
                    default:
                        throw new UnknownExpressionFunctionException(name);
                }
            }

            private FakeExpressionFunction<Object, ExpressionEvaluationContext> function(final BiFunction<List<Object>, ExpressionEvaluationContext, Object> apply) {
                return new FakeExpressionFunction<>() {
                    @Override
                    public Object apply(final List<Object> parameters,
                                        final ExpressionEvaluationContext context) {
                        return apply.apply(parameters, context);
                    }

                    @Override
                    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                        return Lists.of(
                            ExpressionFunctionParameterName.with("values")
                                .variable(Object.class)
                                .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES)
                        );
                    }
                };
            }

            private <T> T convertStringParameter(final List<Object> parameters, final Class<T> targetType) {
                if (parameters.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 parameter=" + parameters);
                }
                return this.convertOrFail(parameters.get(0), targetType);
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return converter.convert(
                    value,
                    target,
                    ExpressionNumberConverterContexts.basic(
                        Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            ',', // valueSeparator
                            Converters.fake(),
                            dateTimeContext(),
                            decimalNumberContext()
                        ),
                        this.expressionNumberKind()
                    )
                );
            }

            @Override
            public boolean isText(final Object value) {
                return SpreadsheetStrings.isText(value);
            }
        };
    }

    @Override
    public Parser<SpreadsheetParserContext> createParser() {
        return expressionParser();
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return this.createContext(this.expressionNumberKind);
    }

    private SpreadsheetParserContext createContext(final ExpressionNumberKind kind) {
        final DateTimeContext dateTimeContext = this.dateTimeContext();
        final DecimalNumberContext decimalNumberContext = this.decimalNumberContext();
        return new FakeSpreadsheetParserContext() {

            @Override
            public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                       final TextCursor cursor) {
                return InvalidCharacterExceptionFactory.POSITION_EXPECTED.apply(
                    parser,
                    cursor
                );
            }

            @Override
            public List<String> ampms() {
                return dateTimeContext.ampms();
            }

            @Override
            public int defaultYear() {
                return dateTimeContext.defaultYear();
            }

            @Override
            public List<String> monthNames() {
                return dateTimeContext.monthNames();
            }

            @Override
            public List<String> monthNameAbbreviations() {
                return dateTimeContext.monthNameAbbreviations();
            }

            @Override
            public int twoDigitYear() {
                return dateTimeContext.twoDigitYear();
            }

            @Override
            public List<String> weekDayNames() {
                return dateTimeContext.weekDayNames();
            }

            @Override
            public List<String> weekDayNameAbbreviations() {
                return dateTimeContext.weekDayNameAbbreviations();
            }

            @Override
            public String currencySymbol() {
                return decimalNumberContext.currencySymbol();
            }

            @Override
            public char decimalSeparator() {
                return decimalNumberContext.decimalSeparator();
            }

            @Override
            public String exponentSymbol() {
                return decimalNumberContext.exponentSymbol();
            }

            @Override
            public char groupSeparator() {
                return decimalNumberContext.groupSeparator();
            }

            @Override
            public char percentSymbol() {
                return decimalNumberContext.percentSymbol();
            }

            @Override
            public MathContext mathContext() {
                return decimalNumberContext.mathContext();
            }

            @Override
            public char negativeSign() {
                return decimalNumberContext.negativeSign();
            }

            @Override
            public char positiveSign() {
                return decimalNumberContext.positiveSign();
            }

            @Override
            public char zeroDigit() {
                return decimalNumberContext.zeroDigit();
            }

            @Override
            public Locale locale() {
                return decimalNumberContext.locale();
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return kind;
            }

            @Override
            public char valueSeparator() {
                return VALUE_SEPARATOR;
            }
        };
    }

    private AdditionSpreadsheetFormulaParserToken addition(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::addition,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken betweenSymbol() {
        return SpreadsheetFormulaParserToken.betweenSymbol(":", ":");
    }

    private BooleanSpreadsheetFormulaParserToken booleanValue(final ParserToken... tokens) {
        return SpreadsheetFormulaParserToken.booleanValue(
            Lists.of(tokens),
            ParserToken.text(
                Lists.of(tokens)
            )
        );
    }

    private BooleanLiteralSpreadsheetFormulaParserToken booleanLiteral(final boolean value) {
        return SpreadsheetFormulaParserToken.booleanLiteral(
            value,
            String.valueOf(value)
        );
    }

    private CellSpreadsheetFormulaParserToken cell(final ParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::cell,
            tokens
        );
    }

    private CellSpreadsheetFormulaParserToken cell(final int column,
                                                   final String columnText,
                                                   final int row) {
        return cell(
            SpreadsheetFormulaParserToken.column(
                SpreadsheetReferenceKind.RELATIVE.column(column),
                columnText
            ),
            SpreadsheetFormulaParserToken.row(
                SpreadsheetReferenceKind.RELATIVE.row(row),
                String.valueOf(1 + row)
            )
        );
    }

    private ColumnSpreadsheetFormulaParserToken columnReference(final String text) {
        return SpreadsheetFormulaParserToken.column(
            SpreadsheetSelection.parseColumn(text),
            text
        );
    }

    private TextLiteralSpreadsheetFormulaParserToken colon() {
        return textLiteral(":");
    }

    private DayNumberSpreadsheetFormulaParserToken day31() {
        return SpreadsheetFormulaParserToken.dayNumber(31, "31");
    }

    private DecimalSeparatorSymbolSpreadsheetFormulaParserToken decimal() {
        return SpreadsheetFormulaParserToken.decimalSeparatorSymbol(".", ".");
    }

    private SpreadsheetFormulaParserToken decimalSymbols() {
        return SpreadsheetFormulaParserToken.decimalSeparatorSymbol(".", ".");
    }

    private SpreadsheetFormulaParserToken digits(final Number number) {
        return SpreadsheetFormulaParserToken.digits(
            "" + number,
            "" + number
        );
    }

    private DivisionSpreadsheetFormulaParserToken division(final ParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::division,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken divideSymbol() {
        return SpreadsheetFormulaParserToken.divideSymbol(
            "/",
            "/"
        );
    }

    private SpreadsheetFormulaParserToken doubleQuotesSymbol() {
        return SpreadsheetFormulaParserToken.doubleQuoteSymbol(
            "\"",
            "\""
        );
    }

    private EqualsSpreadsheetFormulaParserToken equals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::equalsSpreadsheetFormulaParserToken,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken equalsSymbol() {
        return SpreadsheetFormulaParserToken.equalsSymbol("=", "=");
    }

    private ExpressionSpreadsheetFormulaParserToken expression(final ParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::expression,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken functionName(final String name) {
        return SpreadsheetFormulaParserToken.functionName(
            SpreadsheetFunctionName.with(name),
            name
        );
    }

    private FunctionParametersSpreadsheetFormulaParserToken functionParameters(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::functionParameters,
            tokens
        );
    }

    private GreaterThanSpreadsheetFormulaParserToken greaterThan(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::greaterThan,
            tokens
        );
    }

    private GreaterThanEqualsSpreadsheetFormulaParserToken greaterThanEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::greaterThanEquals,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken greaterThanSymbol() {
        return SpreadsheetFormulaParserToken.greaterThanSymbol(
            ">",
            ">"
        );
    }

    private SpreadsheetFormulaParserToken greaterThanEqualsSymbol() {
        return SpreadsheetFormulaParserToken.greaterThanEqualsSymbol(
            ">=",
            ">="
        );
    }

    private GroupSpreadsheetFormulaParserToken group(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::group,
            tokens
        );
    }

    private HourSpreadsheetFormulaParserToken hour11() {
        return SpreadsheetFormulaParserToken.hour(11, "11");
    }

    private MillisecondSpreadsheetFormulaParserToken millis123() {
        return SpreadsheetFormulaParserToken.millisecond(123_000_000, "123");
    }

    private MinuteSpreadsheetFormulaParserToken minute58() {
        return SpreadsheetFormulaParserToken.minute(58, "58");
    }

    private LabelSpreadsheetFormulaParserToken label(final String label) {
        return SpreadsheetFormulaParserToken.label(SpreadsheetSelection.labelName(label), label);
    }

    private LambdaFunctionSpreadsheetFormulaParserToken lambdaFunction(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::lambdaFunction,
            tokens
        );
    }

    private LessThanSpreadsheetFormulaParserToken lessThan(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::lessThan,
            tokens
        );
    }

    private LessThanEqualsSpreadsheetFormulaParserToken lessThanEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::lessThanEquals,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken lessThanSymbol() {
        return SpreadsheetFormulaParserToken.lessThanSymbol(
            "<",
            "<"
        );
    }

    private SpreadsheetFormulaParserToken lessThanEqualsSymbol() {
        return SpreadsheetFormulaParserToken.lessThanEqualsSymbol(
            "<=",
            "<="
        );
    }


    private SpreadsheetFormulaParserToken multiplySymbol() {
        return SpreadsheetFormulaParserToken.multiplySymbol(
            "*",
            "*"
        );
    }

    private MultiplicationSpreadsheetFormulaParserToken multiplication(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::multiplication,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken minusSymbol() {
        return SpreadsheetFormulaParserToken.minusSymbol(
            "-",
            "-"
        );
    }

    private MonthNumberSpreadsheetFormulaParserToken month12() {
        return SpreadsheetFormulaParserToken.monthNumber(12, "12");
    }

    private NamedFunctionSpreadsheetFormulaParserToken namedFunction(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::namedFunction,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken negative(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::negative,
            tokens
        );
    }

    private NotEqualsSpreadsheetFormulaParserToken notEquals(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::notEquals,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken notEqualsSymbol() {
        return SpreadsheetFormulaParserToken.notEqualsSymbol(
            "<>",
            "<>"
        );
    }

    private NumberSpreadsheetFormulaParserToken number(final Number number) {
        return number(
            digits(number)
        );
    }

    private NumberSpreadsheetFormulaParserToken number(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::number,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken parenthesisOpen() {
        return SpreadsheetFormulaParserToken.parenthesisOpenSymbol("(", "(");
    }

    private SpreadsheetFormulaParserToken parenthesisClose() {
        return SpreadsheetFormulaParserToken.parenthesisCloseSymbol(")", ")");
    }

    private SpreadsheetFormulaParserToken percentSymbol() {
        return SpreadsheetFormulaParserToken.percentSymbol(
            "%",
            "%"
        );
    }

    private SpreadsheetFormulaParserToken plusSymbol() {
        return SpreadsheetFormulaParserToken.plusSymbol(
            "+",
            "+"
        );
    }

    private AmPmSpreadsheetFormulaParserToken pm() {
        return SpreadsheetFormulaParserToken.amPm(12, "pm");
    }

    private PowerSpreadsheetFormulaParserToken power(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::power,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken powerSymbol() {
        return SpreadsheetFormulaParserToken.powerSymbol(
            "^",
            "^"
        );
    }

    private CellRangeSpreadsheetFormulaParserToken range(final SpreadsheetFormulaParserToken from,
                                                         final SpreadsheetFormulaParserToken to) {
        return parentToken(
            SpreadsheetFormulaParserToken::cellRange,
            from,
            betweenSymbol(),
            to
        );
    }

    private RowSpreadsheetFormulaParserToken rowReference(final String text) {
        return SpreadsheetFormulaParserToken.row(
            SpreadsheetSelection.parseRow(text),
            text
        );
    }

    private SecondsSpreadsheetFormulaParserToken seconds59() {
        return SpreadsheetFormulaParserToken.seconds(59, "59");
    }

    private TextLiteralSpreadsheetFormulaParserToken slash() {
        return textLiteral("/");
    }

    private SubtractionSpreadsheetFormulaParserToken subtraction(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::subtraction,
            tokens
        );
    }

    private SpreadsheetFormulaParserToken text(final SpreadsheetFormulaParserToken... tokens) {
        return parentToken(
            SpreadsheetFormulaParserToken::text,
            tokens
        );
    }

    private TextLiteralSpreadsheetFormulaParserToken textLiteral(final String text) {
        return SpreadsheetFormulaParserToken.textLiteral(text, text);
    }

    private SpreadsheetFormulaParserToken valueSeparatorSymbol() {
        return SpreadsheetFormulaParserToken.valueSeparatorSymbol(
            "" + VALUE_SEPARATOR,
            "" + VALUE_SEPARATOR
        );
    }

    private SpreadsheetFormulaParserToken whitespace1() {
        return whitespace(" ");
    }

    private SpreadsheetFormulaParserToken whitespace2() {
        return whitespace("  ");
    }

    private SpreadsheetFormulaParserToken whitespace(final String text) {
        return SpreadsheetFormulaParserToken.whitespace(
            text,
            text
        );
    }

    private YearSpreadsheetFormulaParserToken year99() {
        return SpreadsheetFormulaParserToken.year(99, "99");
    }

    private YearSpreadsheetFormulaParserToken year2000() {
        return SpreadsheetFormulaParserToken.year(2000, "2000");
    }

    private <T extends SpreadsheetFormulaParserToken> T parentToken(final BiFunction<List<ParserToken>, String, T> factory,
                                                                    final ParserToken... tokens) {
        return factory.apply(
            Lists.of(tokens),
            ParserToken.text(
                Lists.of(tokens)
            )
        );
    }

    // PublicStaticHelperTesting........................................................................................

    @Override
    public Class<SpreadsheetFormulaParsers> type() {
        return SpreadsheetFormulaParsers.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
