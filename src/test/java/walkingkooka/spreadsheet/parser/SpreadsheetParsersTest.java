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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.function.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.DoubleParserToken;
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
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class SpreadsheetParsersTest implements PublicStaticHelperTesting<SpreadsheetParsers>,
        ParserTesting2<Parser<SpreadsheetParserContext>, SpreadsheetParserContext> {

    private ExpressionNumberKind expressionNumberKind;

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

        System.out.println(this.expressionNumberKind + " " + testInfo.getDisplayName());
    }

    // values.... ......................................................................................................

    @Test
    public void testApostropheStringEmpty() {
        this.parseStringAndCheck("");
    }

    @Test
    public void testApostropheString() {
        this.parseStringAndCheck("abc-123");
    }

    @Test
    public void testApostropheStringIncludesSingleAndDoubleQuote() {

        this.parseStringAndCheck("1abc-'123\"456");
    }

    @Test
    public void testApostropheStringIgnoresBackslashEscaping() {
        this.parseStringAndCheck("new-line\\ntab\\t");
    }

    private void parseStringAndCheck(final String text) {
        final String apostropheText = '\'' + text;

        this.parseAndCheck(
                SpreadsheetParsers.valueOrExpression(),
                apostropheText,
                SpreadsheetTextParserToken.text(
                        Lists.of(
                                SpreadsheetParserToken.apostropheSymbol("'", "'"),
                                SpreadsheetParserToken.textLiteral(text, text)
                        ).stream()
                                .filter(t -> !t.text().isEmpty())
                                .collect(Collectors.toList()),
                        apostropheText),
                apostropheText,
                ""
        );
    }

    // values...........................................................................................................

    @Test
    public void testText() {
        final String text = "\"abc-123\"";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.text(
                        Lists.of(
                                doubleQuotes(),
                                SpreadsheetParserToken.textLiteral("abc-123", "abc-123"),
                                doubleQuotes()
                        ),
                        text),
                text);
    }

    @Test
    public void testTextWithEscapedDoubleQuote() {
        final String text = "\"abc-\"\"-123\"";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.text(
                        Lists.of(
                                doubleQuotes(),
                                SpreadsheetParserToken.textLiteral("abc-\"-123", "abc-\"\"-123"),
                                doubleQuotes()
                        ),
                        text),
                text);
    }

    @Test
    public void testBigDecimal() {
        final String text = "1.5";

        this.parseExpressionAndCheck(text,
                number(1.5),
                text);
    }

    // CELL & LABEL ......................................................................................................

    @Test
    public void testCellReferencesParserOtherExpressionFails() {
        this.parseFailAndCheck(SpreadsheetParsers.cellReferences(), "1+2");
    }

    @Test
    public void testCell() {
        final String text = "A1";
        final SpreadsheetCellReferenceParserToken cell = cell(0, "A", 0);

        this.cellReferenceParseAndCheck(text, cell, text);
    }

    @Test
    public void testCell2() {
        final String text = "AA678";
        final SpreadsheetCellReferenceParserToken cell = this.cell(26, "AA", 678 - 1);

        this.cellReferenceParseAndCheck(text, cell, text);
    }

    @Test
    public void testLabel() {
        final String text = "Hello";

        this.cellReferenceParseAndCheck(text,
                SpreadsheetParserToken.labelName(SpreadsheetExpressionReference.labelName(text), text),
                text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#cellReferences()}} and then repeat again with
     * {@link SpreadsheetParsers#expression()}.
     */
    private void cellReferenceParseAndCheck(final String from,
                                            final SpreadsheetParserToken expected,
                                            final String text) {
        this.parseAndCheck(SpreadsheetParsers.cellReferences(), from, expected, text);
        this.parseExpressionAndCheck(from, expected, text);
    }

    // RANGE............................................................................................................

    @Test
    public void testRangeParserOtherExpressionFails() {
        this.parseFailAndCheck(SpreadsheetParsers.range(), "1+2");
    }

    @Test
    public void testRangeCellToCell() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetCellReferenceParserToken to = this.cell(1, "B", 1);

        final SpreadsheetRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    public void testRangeLabelToLabel() {
        final SpreadsheetLabelNameParserToken from = this.label("from");
        final SpreadsheetLabelNameParserToken to = this.label("to");

        final SpreadsheetRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    public void testRangeCellToLabel() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetLabelNameParserToken to = this.label("to");

        final SpreadsheetRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    public void testRangeLabelToCell() {
        final SpreadsheetLabelNameParserToken from = this.label("to");
        final SpreadsheetCellReferenceParserToken to = this.cell(0, "A", 0);

        final SpreadsheetRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    public void testRangeWhitespace() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetCellReferenceParserToken to = this.cell(1, "B", 1);

        final String text = from.text() + "  " + between() + "  " + to.text();
        final SpreadsheetRangeParserToken range = SpreadsheetParserToken.range(Lists.of(from, whitespace(), between(), whitespace(), to), text);

        this.rangeParseAndCheck(text, range, text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#range()} and then repeat again with {@link SpreadsheetParsers#expression()}.
     */
    private void rangeParseAndCheck(final String from, final SpreadsheetRangeParserToken expected, final String text) {
        this.parseAndCheck(SpreadsheetParsers.range(), from, expected, text);
        this.parseExpressionAndCheck(from, expected, text);
    }

    // Negative.........................................................................................

    @Test
    public void testExpressionNegativeBigDecimal() {
        final String text = "-1.5";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), number(1.5)), text),
                text);
    }

    @Test
    public void testExpressionNegativeWhitespaceBigDecimal() {
        final String text = "-  1.5";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), whitespace(), number(1.5)), text),
                text);
    }

    @Test
    public void testExpressionNegativeNumberBigDecimal() {
        this.testExpressionNegativeNumber();
    }

    @Test
    public void testExpressionNegativeNumberDouble() {
        this.testExpressionNegativeNumber();
    }

    private void testExpressionNegativeNumber() {

        final String text = "-1";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), number(1)), text),
                text);
    }

    @Test
    public void testExpressionNegativeCellBigDecimal() {
        this.testExpressionNegativeCell();
    }

    @Test
    public void testExpressionNegativeCellDouble() {
        this.testExpressionNegativeCell();
    }

    private void testExpressionNegativeCell() {
        final String text = "-A1";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), cell(0, "A", 0)), text),
                text);
    }

    @Test
    public void testExpressionNegativeLabel() {
        final String text = "-LabelABC";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), label("LabelABC")), text),
                text);
    }

    @Test
    public void testExpressionNumberPercentageBigDecimal() {
        this.testExpressionNumberPercentage();
    }

    @Test
    public void testExpressionNumberPercentageDouble() {
        this.testExpressionNumberPercentage();
    }

    private void testExpressionNumberPercentage() {
        final String text = "1%";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.percentage(Lists.of(number(1), percent()), text),
                text);
    }

    @Test
    public void testExpressionNegativeNumberPercentageBigDecimal() {
        this.testExpressionNegativeNumberPercentage();
    }

    @Test
    public void testExpressionNegativeNumberPercentageDouble() {
        this.testExpressionNegativeNumberPercentage();
    }

    private void testExpressionNegativeNumberPercentage() {
        final String text = "-1%";
        final SpreadsheetParserToken percent = SpreadsheetParserToken.percentage(Lists.of(number(1), percent()), "1%");

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), percent), text),
                text);
    }

    @Test
    public void testExpressionGroupLabel() {
        final String labelText = "Hello";

        final String groupText = "(" + labelText + ")";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), label(labelText), closeParenthesis()), groupText);

        this.parseExpressionAndCheck(groupText, group, groupText);
    }

    @Test
    public void testExpressionGroupWhitespaceLabelWhitespace() {
        final String labelText = "Hello";
        final String groupText = "(  " + labelText + "  )";

        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), whitespace(), label(labelText), whitespace(), closeParenthesis()), groupText);

        this.parseExpressionAndCheck(groupText, group, groupText);
    }

    @Test
    public void testExpressionGroupNegativeNumberBigDecimal() {
        this.testExpressionGroupNegativeNumber();
    }

    @Test
    public void testExpressionGroupNegativeNumberDouble() {
        this.testExpressionGroupNegativeNumber();
    }

    private void testExpressionGroupNegativeNumber() {
        final SpreadsheetParserToken negative = negative(number(123));

        final String groupText = "(-123)";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), negative, closeParenthesis()), groupText);

        this.parseExpressionAndCheck(groupText, group, groupText);
    }

    @Test
    public void testExpressionNegativeGroupNumberBigDecimal() {
        this.testExpressionNegativeGroupNumber();
    }

    @Test
    public void testExpressionNegativeGroupNumberDouble() {
        this.testExpressionNegativeGroupNumber();
    }

    private void testExpressionNegativeGroupNumber() {
        final String groupText = "(123)";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), number(123), closeParenthesis()), groupText);

        final String text = "-" + groupText;
        this.parseExpressionAndCheck(text, negative(group), text);
    }

    @Test
    public void testExpressionAddBigDecimal() {
        this.testExpressionAdd();
    }

    @Test
    public void testExpressionAddDouble() {
        this.testExpressionAdd();
    }

    private void testExpressionAdd() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123+456";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), text);

        this.parseExpressionAndCheck(text, add, text);
    }

    @Test
    public void testExpressionAdd2BigDecimal() {
        this.testExpressionAdd2();
    }

    @Test
    public void testExpressionAdd2Double() {
        this.testExpressionAdd2();
    }

    private void testExpressionAdd2() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123+456";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), number(789)), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionAddNegativeBigDecimal() {
        this.testExpressionAddNegative();
    }

    @Test
    public void testExpressionAddNegativeDouble() {
        this.testExpressionAddNegative();
    }

    private void testExpressionAddNegative() {
        // 123+-456+789
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = negative(number(456));
        final String text = "123+-456";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), number(789)), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionSubtractBigDecimal() {
        this.testExpressionSubtract();
    }

    @Test
    public void testExpressionSubtractDouble() {
        this.testExpressionSubtract();
    }

    private void testExpressionSubtract() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123-456";
        final SpreadsheetSubtractionParserToken add = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        this.parseExpressionAndCheck(text, add, text);
    }

    @Test
    public void testExpressionSubtract2BigDecimal() {
        this.testExpressionSubtract2();
    }

    @Test
    public void testExpressionSubtract2Double() {
        this.testExpressionSubtract2();
    }

    private void testExpressionSubtract2() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123-456";
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        final String text2 = text + "-789";
        final SpreadsheetSubtractionParserToken add2 = SpreadsheetParserToken.subtraction(Lists.of(sub, minus(), number(789)), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionSubtractNegativeBigDecimal() {
        this.subtractNegative();
    }

    @Test
    public void testExpressionSubtractNegativeDouble() {
        this.subtractNegative();
    }

    private void subtractNegative() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = negative(number(456));
        final String text = "123--456";
        final SpreadsheetSubtractionParserToken add = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        this.parseExpressionAndCheck(text, add, text);
    }

    @Test
    public void testExpressionSubtractAddBigDecimal() {
        this.subtractAdd();
    }

    @Test
    public void testExpressionSubtractAddDouble() {
        this.subtractAdd();
    }

    private void subtractAdd() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123-456";
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(sub, plus(), number(789)), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionSubtractWhitespaceAroundMinusSignBigDecimal() {
        this.subtractWhitespaceAroundMinusSign();
    }

    @Test
    public void testExpressionSubtractWhitespaceAroundMinusSignDouble() {
        this.subtractWhitespaceAroundMinusSign();
    }

    private void subtractWhitespaceAroundMinusSign() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123  -  456";
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(left, whitespace(), minus(), whitespace(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(sub, plus(), number(789)), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionMultiplyBigDecimal() {
        this.testExpressionMultiply1();
    }

    @Test
    public void testExpressionMultiplyDouble() {
        this.testExpressionMultiply1();
    }

    private void testExpressionMultiply1() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123*456";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(left, multiply(), right), text);

        this.parseExpressionAndCheck(text, multiply, text);
    }

    @Test
    public void testExpressionMultiply2BigDecimal() {
        this.testExpressionMultiply2();
    }

    @Test
    public void testExpressionMultiply2Double() {
        this.testExpressionMultiply2();
    }

    private void testExpressionMultiply2() {
        final SpreadsheetParserToken left = number(222);
        final SpreadsheetParserToken right = number(333);
        final String text = "222*333";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(left, multiply(), right), text);

        final String text2 = "111+" + text;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(number(111), plus(), multiply), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionNegativeMultiplyNegativeBigDecimal() {
        this.negativeMultiplyNegative();
    }

    @Test
    public void testExpressionNegativeMultiplyNegativeDouble() {
        this.negativeMultiplyNegative();
    }

    private void negativeMultiplyNegative() {
        final SpreadsheetParserToken left = negative(number(123));
        final SpreadsheetParserToken right = negative(number(456));
        final String text = "-123*-456";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(left, multiply(), right), text);

        this.parseExpressionAndCheck(text, multiply, text);
    }

    @Test
    public void testExpressionDivideBigDecimal() {
        this.testExpressionDivide();
    }

    @Test
    public void testExpressionDivideDouble() {
        this.testExpressionDivide();
    }

    private void testExpressionDivide() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123/456";
        final SpreadsheetDivisionParserToken divide = SpreadsheetParserToken.division(Lists.of(left, divide(), right), text);

        this.parseExpressionAndCheck(text, divide, text);
    }

    @Test
    public void testExpressionDivide2BigDecimal() {
        this.testExpressionDivide2();
    }

    @Test
    public void testExpressionDivide2Double() {
        this.testExpressionDivide2();
    }

    private void testExpressionDivide2() {
        final SpreadsheetParserToken left = number(222);
        final SpreadsheetParserToken right = number(333);
        final String text = "222/333";
        final SpreadsheetDivisionParserToken divide = SpreadsheetParserToken.division(Lists.of(left, divide(), right), text);

        final String text2 = "111+" + text;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(number(111), plus(), divide), text2);

        this.parseExpressionAndCheck(text2, add2, text2);
    }

    @Test
    public void testExpressionNegativeDivideNegativeBigDecimal() {
        this.testExpressionNegativeDivideNegative();
    }

    @Test
    public void testExpressionNegativeDivideNegativeDouble() {
        this.testExpressionNegativeDivideNegative();
    }

    private void testExpressionNegativeDivideNegative() {
        final SpreadsheetParserToken left = negative(number(123));
        final SpreadsheetParserToken right = negative(number(456));
        final String text = "-123/-456";
        final SpreadsheetDivisionParserToken divide = SpreadsheetParserToken.division(Lists.of(left, divide(), right), text);

        this.parseExpressionAndCheck(text, divide, text);
    }

    @Test
    public void testExpressionPowerBigDecimal() {
        this.testExpressionPower();
    }

    @Test
    public void testExpressionPowerDouble() {
        this.testExpressionPower();
    }

    private void testExpressionPower() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123^456";
        final SpreadsheetPowerParserToken power = SpreadsheetParserToken.power(Lists.of(left, power(), right), text);

        this.parseExpressionAndCheck(text, power, text);
    }

    @Test
    public void testExpressionPower2BigDecimal() {
        this.testExpressionPower2();
    }

    @Test
    public void testExpressionPower2Double() {
        this.testExpressionPower2();
    }

    private void testExpressionPower2() {
        final SpreadsheetParserToken left = number(222);
        final SpreadsheetParserToken right = number(333);
        final String text = "222^333";
        final SpreadsheetPowerParserToken power = SpreadsheetParserToken.power(Lists.of(left, power(), right), text);

        final String text2 = "111*" + text;
        final SpreadsheetMultiplicationParserToken multiply2 = SpreadsheetParserToken.multiplication(Lists.of(number(111), multiply(), power), text2);

        this.parseExpressionAndCheck(text2, multiply2, text2);
    }

    @Test
    public void testExpressionEqualsBigDecimal() {
        this.testExpressionEquals();
    }

    @Test
    public void testExpressionEqualsDouble() {
        this.testExpressionEquals();
    }

    private void testExpressionEquals() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123==456";
        final SpreadsheetEqualsParserToken equals = SpreadsheetParserToken.equalsParserToken(Lists.of(left, equals(), right), text);

        this.parseExpressionAndCheck(text, equals, text);
    }

    @Test
    public void testExpressionEqualsAddBigDecimal() {
        this.testExpressionEqualsAdd();
    }

    @Test
    public void testExpressionEqualsAddDouble() {
        this.testExpressionEqualsAdd();
    }

    private void testExpressionEqualsAdd() {
        final SpreadsheetParserToken middle = number(456);
        final SpreadsheetParserToken right = number(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = number(123);
        final String text = "123==" + addText;
        final SpreadsheetEqualsParserToken equals = SpreadsheetParserToken.equalsParserToken(Lists.of(left, equals(), add), text);

        this.parseExpressionAndCheck(text, equals, text);
    }

    @Test
    public void testExpressionNotEqualsBigDecimal() {
        this.testExpressionNotEquals();
    }

    @Test
    public void testExpressionNotEqualsDouble() {
        this.testExpressionNotEquals();
    }

    private void testExpressionNotEquals() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123!=456";
        final SpreadsheetNotEqualsParserToken ne = SpreadsheetParserToken.notEquals(Lists.of(left, notEquals(), right), text);

        this.parseExpressionAndCheck(text, ne, text);
    }

    @Test
    public void testExpressionNotEqualsAddBigDecimal() {
        this.testExpressionNotEqualsAdd();
    }

    @Test
    public void testExpressionNotEqualsAddDouble() {
        this.testExpressionNotEqualsAdd();
    }

    private void testExpressionNotEqualsAdd() {
        final SpreadsheetParserToken middle = number(456);
        final SpreadsheetParserToken right = number(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = number(123);
        final String text = "123!=" + addText;
        final SpreadsheetNotEqualsParserToken ne = SpreadsheetParserToken.notEquals(Lists.of(left, notEquals(), add), text);

        this.parseExpressionAndCheck(text, ne, text);
    }

    @Test
    public void testExpressionGreaterThanBigDecimal() {
        this.testExpressionGreaterThan();
    }

    @Test
    public void testExpressionGreaterThanDouble() {
        this.testExpressionGreaterThan();
    }

    private void testExpressionGreaterThan() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123>456";
        final SpreadsheetGreaterThanParserToken gt = SpreadsheetParserToken.greaterThan(Lists.of(left, greaterThan(), right), text);

        this.parseExpressionAndCheck(text, gt, text);
    }

    @Test
    public void testExpressionGreaterThanAddBigDecimal() {
        this.testExpressionGreaterThanAdd();
    }

    @Test
    public void testExpressionGreaterThanAddDouble() {
        this.testExpressionGreaterThanAdd();
    }

    private void testExpressionGreaterThanAdd() {
        final SpreadsheetParserToken middle = number(456);
        final SpreadsheetParserToken right = number(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = number(123);
        final String text = "123>" + addText;
        final SpreadsheetGreaterThanParserToken gt = SpreadsheetParserToken.greaterThan(Lists.of(left, greaterThan(), add), text);

        this.parseExpressionAndCheck(text, gt, text);
    }

    @Test
    public void testExpressionGreaterThanEqualsBigDecimal() {
        this.testExpressionGreaterThanEquals();
    }

    @Test
    public void testExpressionGreaterThanEqualsDouble() {
        this.testExpressionGreaterThanEquals();
    }

    private void testExpressionGreaterThanEquals() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123>=456";
        final SpreadsheetGreaterThanEqualsParserToken gte = SpreadsheetParserToken.greaterThanEquals(Lists.of(left, greaterThanEquals(), right), text);

        this.parseExpressionAndCheck(text, gte, text);
    }

    @Test
    public void testExpressionGreaterThanEqualsAddBigDecimal() {
        this.testExpressionGreaterThanEqualsAdd();
    }

    @Test
    public void testExpressionGreaterThanEqualsAddDouble() {
        this.testExpressionGreaterThanEqualsAdd();
    }

    private void testExpressionGreaterThanEqualsAdd() {
        final SpreadsheetParserToken middle = number(456);
        final SpreadsheetParserToken right = number(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = number(123);
        final String text = "123>=" + addText;
        final SpreadsheetGreaterThanEqualsParserToken gte = SpreadsheetParserToken.greaterThanEquals(Lists.of(left, greaterThanEquals(), add), text);

        this.parseExpressionAndCheck(text, gte, text);
    }

    @Test
    public void testExpressionLessThanBigDecimal() {
        this.testExpressionLessThan();
    }

    @Test
    public void testExpressionLessThanDouble() {
        this.testExpressionLessThan();
    }

    private void testExpressionLessThan() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123<456";
        final SpreadsheetLessThanParserToken lt = SpreadsheetParserToken.lessThan(Lists.of(left, lessThan(), right), text);

        this.parseExpressionAndCheck(text, lt, text);
    }

    @Test
    public void testExpressionLessThanAddBigDecimal() {
        this.testExpressionLessThanAdd();
    }

    @Test
    public void testExpressionLessThanAddDouble() {
        this.testExpressionLessThanAdd();
    }

    private void testExpressionLessThanAdd() {
        final SpreadsheetParserToken middle = number(456);
        final SpreadsheetParserToken right = number(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = number(123);
        final String text = "123<" + addText;
        final SpreadsheetLessThanParserToken lt = SpreadsheetParserToken.lessThan(Lists.of(left, lessThan(), add), text);

        this.parseExpressionAndCheck(text, lt, text);
    }

    @Test
    public void testExpressionLessThanEqualsBigDecimal() {
        this.testExpressionLessThanEquals();
    }

    @Test
    public void testExpressionLessThanEqualsDouble() {
        this.testExpressionLessThanEquals();
    }

    private void testExpressionLessThanEquals() {
        final SpreadsheetParserToken left = number(123);
        final SpreadsheetParserToken right = number(456);
        final String text = "123<=456";
        final SpreadsheetLessThanEqualsParserToken lte = SpreadsheetParserToken.lessThanEquals(Lists.of(left, lessThanEquals(), right), text);

        this.parseExpressionAndCheck(text, lte, text);
    }

    @Test
    public void testExpressionLessThanEqualsAddBigDecimal() {
        this.testExpressionLessThanEqualsAdd();
    }

    @Test
    public void testExpressionLessThanEqualsAddDouble() {
        this.testExpressionLessThanEqualsAdd();
    }

    private void testExpressionLessThanEqualsAdd() {
        final SpreadsheetParserToken middle = number(456);
        final SpreadsheetParserToken right = number(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = number(123);
        final String text = "123<=" + addText;
        final SpreadsheetLessThanEqualsParserToken lte = SpreadsheetParserToken.lessThanEquals(Lists.of(left, lessThanEquals(), add), text);

        this.parseExpressionAndCheck(text, lte, text);
    }

    @Test
    public void testExpressionComplexExpressionBigDecimal() {
        this.testExpressionComplexExpression();
    }

    @Test
    public void testExpressionComplexExpressionDouble() {
        this.testExpressionComplexExpression();
    }

    private void testExpressionComplexExpression() {
        //111+222+(-333)-444*555
        final String addText = "111+222";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(number(111), plus(), number(222)), addText);

        final String groupText = "(-333)";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), negative(number(333)), closeParenthesis()), groupText);

        final String addText2 = add + "+" + groupText;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), group), addText2);

        final String multiplyText = "444*555";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(number(444), multiply(), number(555)), multiplyText);

        final String subText = addText2 + "-" + multiplyText;
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(add2, minus(), multiply), subText);

        this.parseExpressionAndCheck(subText, sub, subText);
    }

    // Function.........................................................................................................

    @Test
    public void testFunctionOtherExpressionFails() {
        this.parseFailAndCheck(this.functionParser(), "1+2");
    }

    @Test
    public void testFunctionWithoutArguments() {
        final String text = "xyz()";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithoutArgumentsWhitespace() {
        final String text = "xyz(  )";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), whitespace(), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgumentBigDecimal() {
        this.testFunctionWithOneArgument();
    }

    @Test
    public void testFunctionWithOneArgumentDouble() {
        this.testFunctionWithOneArgument();
    }

    private void testFunctionWithOneArgument() {
        final String text = "xyz(123)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), number(123), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgument2BigDecimal() {
        this.testFunctionWithOneArgument2();
    }

    @Test
    public void testFunctionWithOneArgument2Double() {
        this.testFunctionWithOneArgument2();
    }

    private void testFunctionWithOneArgument2() {
        final String text = "xyz(  123)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), whitespace(), number(123), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgument3BigDecimal() {
        this.testFunctionWithOneArgument3();
    }

    @Test
    public void testFunctionWithOneArgument3Double() {
        this.testFunctionWithOneArgument3();
    }

    private void testFunctionWithOneArgument3() {
        final String text = "xyz(123  )";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), number(123), whitespace(), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgument4BigDecimal() {
        this.testFunctionWithOneArgument4();
    }

    @Test
    public void testFunctionWithOneArgument4Double() {
        this.testFunctionWithOneArgument4();
    }

    private void testFunctionWithOneArgument4() {
        final String text = "xyz(  123  )";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), whitespace(), number(123), whitespace(), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithTwoArgumentsBigDecimal() {
        this.testFunctionWithTwoArguments();
    }

    @Test
    public void testFunctionWithTwoArgumentsDouble() {
        this.testFunctionWithTwoArguments();
    }

    private void testFunctionWithTwoArguments() {
        final String text = "xyz(123,456)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), number(123), valueSeparator(), number(456), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithFourArgumentsBigDecimal() {
        this.testFunctionWithFourArguments();
    }

    @Test
    public void testFunctionWithFourArgumentsDouble() {
        this.testFunctionWithFourArguments();
    }

    private void testFunctionWithFourArguments() {
        final String text = "xyz(1,2,3,4)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), number(1), valueSeparator(), number(2), valueSeparator(), number(3), valueSeparator(), number(4), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithinFunctionBigDecimal() {
        this.testFunctionWithinFunction();
    }

    @Test
    public void testFunctionWithinFunctionDouble() {
        this.testFunctionWithinFunction();
    }

    private void testFunctionWithinFunction() {
        final String yText = "y(123)";
        final SpreadsheetFunctionParserToken y = SpreadsheetParserToken.function(Lists.of(functionName("y"), openParenthesis(), number(123), closeParenthesis()), yText);

        final String xText = "x(" + yText + ")";
        final SpreadsheetFunctionParserToken x = SpreadsheetParserToken.function(Lists.of(functionName("x"), openParenthesis(), y, closeParenthesis()), xText);

        this.functionParseAndCheck(xText, x, xText);
    }

    @Test
    public void testFunctionWithinFunctionWithinFunctionBigDecimal() {
        this.testFunctionWithinFunctionWithinFunction();
    }

    @Test
    public void testFunctionWithinFunctionWithinFunctionDouble() {
        this.testFunctionWithinFunctionWithinFunction();
    }

    private void testFunctionWithinFunctionWithinFunction() {
        final String zText = "z(123)";
        final SpreadsheetFunctionParserToken z = SpreadsheetParserToken.function(Lists.of(functionName("z"), openParenthesis(), number(123), closeParenthesis()), zText);

        final String yText = "y(" + zText + ")";
        final SpreadsheetFunctionParserToken y = SpreadsheetParserToken.function(Lists.of(functionName("y"), openParenthesis(), z, closeParenthesis()), yText);

        final String xText = "x(" + yText + ")";
        final SpreadsheetFunctionParserToken x = SpreadsheetParserToken.function(Lists.of(functionName("x"), openParenthesis(), y, closeParenthesis()), xText);

        this.functionParseAndCheck(xText, x, xText);
    }

    @Test
    public void testFunctionWithRangeArgumentBigDecimal() {
        this.testFunctionWithRangeArgument();
    }

    @Test
    public void testFunctionWithRangeArgumentDouble() {
        this.testFunctionWithRangeArgument();
    }

    private void testFunctionWithRangeArgument() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetCellReferenceParserToken to = this.cell(1, "B", 1);

        final SpreadsheetRangeParserToken range = range(from, to);
        final String rangeText = range.text();

        final String text = "xyz(" + rangeText + ")";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), range, closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#function()} and then repeat again with
     * {@link SpreadsheetParsers#expression()}. Both should give the same results.
     */
    private void functionParseAndCheck(final String from,
                                       final SpreadsheetFunctionParserToken expected,
                                       final String text) {
        this.parseAndCheck(functionParser(), from, expected, text);
        this.parseExpressionAndCheck(from, expected, text);
    }

    private Parser<SpreadsheetParserContext> functionParser() {
        return SpreadsheetParsers.function();
    }

    // Group ....................................................................................................

    @Test
    public void testExpressionGroupAndFurtherExpressionsBigDecimal() {
        this.testExpressionGroupAndFurtherExpressions();
    }

    @Test
    public void testExpressionGroupAndFurtherExpressionsDouble() {
        this.testExpressionGroupAndFurtherExpressions();
    }

    private void testExpressionGroupAndFurtherExpressions() {
        final SpreadsheetParserToken left = number(1);
        final SpreadsheetParserToken right = number(2);
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), "1+2");
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(this.openParenthesis(), add, this.closeParenthesis()), "(" + add.text() + ")");

        final SpreadsheetParserToken last = number(3);
        final SpreadsheetMultiplicationParserToken mul = SpreadsheetParserToken.multiplication(Lists.of(group, multiply(), last), group.text() + "*3");

        this.parseExpressionAndCheck(mul.text(), mul, mul.text());
    }

    @Test
    public void testExpressionNestedGroupAdditionBigDecimal() {
        this.testExpressionNestedGroupAddition();
    }

    @Test
    public void testExpressionNestedGroupAdditionDouble() {
        this.testExpressionNestedGroupAddition();
    }

    private void testExpressionNestedGroupAddition() {
        final SpreadsheetParserToken left1 = number(1);
        final SpreadsheetParserToken right1 = number(2);
        final SpreadsheetAdditionParserToken add1 = SpreadsheetParserToken.addition(Lists.of(left1, plus(), right1), "1+2");
        final SpreadsheetGroupParserToken group1 = SpreadsheetParserToken.group(Lists.of(this.openParenthesis(), add1, this.closeParenthesis()), "(" + add1.text() + ")");

        final SpreadsheetParserToken left2 = number(3);
        final SpreadsheetParserToken right2 = number(4);
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(left2, plus(), right2), "3+4");
        final SpreadsheetGroupParserToken group2 = SpreadsheetParserToken.group(Lists.of(this.openParenthesis(), add2, this.closeParenthesis()), "(" + add2.text() + ")");

        final SpreadsheetMultiplicationParserToken mul = SpreadsheetParserToken.multiplication(Lists.of(group1, multiply(), group2), group1.text() + "*" + group2.text());
        this.parseExpressionAndCheck(mul.text(), mul, mul.text());
    }

    @Test
    public void testExpressionInvalidTokenFails() {
        this.parseExpressionThrows("!", this.reporterMessage('!', 1, 1));
    }

    @Test
    public void testExpressionInvalidTokenFails2() {
        this.parseExpressionThrows("  !", this.reporterMessage('!', 3, 1));
    }

    @Test
    public void testExpressionInvalidBinaryTokenRightFailsBigDecimal() {
        this.testExpressionInvalidBinaryTokenRightFails();
    }

    @Test
    public void testExpressionInvalidBinaryTokenRightFailsDouble() {
        this.testExpressionInvalidBinaryTokenRightFails();
    }

    private void testExpressionInvalidBinaryTokenRightFails() {
        this.parseExpressionThrows("1+!", this.reporterMessage('!', 3, 1));
    }

    @Test
    public void testExpressionInvalidGroupTokenFails() {
        this.parseExpressionThrows("( !", this.reporterMessage('!', 3, 1));
    }

    private String reporterMessage(final char c, final int column, final int row) {
        return "Unrecognized character " + CharSequences.quoteIfChars(c) + " at (" + column + "," + row + ")";
    }

    @Test
    public void testExpressionAdditionBigDecimal() {
        this.testExpressionAddition();
    }

    @Test
    public void testExpressionAdditionDouble() {
        this.testExpressionAddition();
    }

    private void testExpressionAddition() {
        this.parseExpressionEvaluateAndCheck("1+2", 1 + 2);
    }

    @Test
    public void testExpressionAdditionLeadingWhitespaceBigDecimal() {
        this.testExpressionAdditionLeadingWhitespace();
    }

    @Test
    public void testExpressionAdditionLeadingWhitespaceDouble() {
        this.testExpressionAdditionLeadingWhitespace();
    }

    private void testExpressionAdditionLeadingWhitespace() {
        this.parseExpressionEvaluateAndCheck(" 1+2", 1 + 2);
    }

    @Test
    public void testExpressionAdditionLeadingWhitespace2BigDecimal() {
        this.testExpressionAdditionLeadingWhitespace2();
    }

    @Test
    public void testExpressionAdditionLeadingWhitespace2Double() {
        this.testExpressionAdditionLeadingWhitespace2();
    }

    private void testExpressionAdditionLeadingWhitespace2() {
        this.parseExpressionEvaluateAndCheck("  1+2", 1 + 2);
    }

    @Test
    public void testExpressionAdditionTrailingWhitespaceBigDecimal() {
        this.testExpressionAdditionTrailingWhitespace();
    }

    @Test
    public void testExpressionAdditionTrailingWhitespaceDouble() {
        this.testExpressionAdditionTrailingWhitespace();
    }

    private void testExpressionAdditionTrailingWhitespace() {
        this.parseExpressionEvaluateAndCheck("1+2 ", 1 + 2);
    }

    @Test
    public void testExpressionAdditionTrailingWhitespace2BigDecimal() {
        this.testExpressionAdditionTrailingWhitespace2();
    }

    @Test
    public void testExpressionAdditionTrailingWhitespace2Double() {
        this.testExpressionAdditionTrailingWhitespace2();
    }

    private void testExpressionAdditionTrailingWhitespace2() {
        this.parseExpressionEvaluateAndCheck("1+2  ", 1 + 2);
    }

    @Test
    public void testExpressionAdditionSurroundedByWhitespaceBigDecimal() {
        this.testExpressionAdditionSurroundedByWhitespace();
    }

    @Test
    public void testExpressionAdditionSurroundedByWhitespaceDouble() {
        this.testExpressionAdditionSurroundedByWhitespace();
    }

    private void testExpressionAdditionSurroundedByWhitespace() {
        this.parseExpressionEvaluateAndCheck(" 1+2 ", 1 + 2);
    }

    @Test
    public void testExpressionMultiplicationBigDecimal() {
        this.testExpressionMultiplication();
    }

    @Test
    public void testExpressionMultiplicationDouble() {
        this.testExpressionMultiplication();
    }

    private void testExpressionMultiplication() {
        this.parseExpressionEvaluateAndCheck("3*4.5", 3 * 4.5);
    }

    @Test
    public void testExpressionMathOperatorPriorityBigDecimal() {
        this.testExpressionMathOperatorPriority();
    }

    @Test
    public void testExpressionMathOperatorPriorityDouble() {
        this.testExpressionMathOperatorPriority();
    }

    private void testExpressionMathOperatorPriority() {
        this.parseExpressionEvaluateAndCheck("1+2*3+4.5", 1 + 2 * 3 + 4.5);
    }

    @Test
    public void testExpressionParenthesisBigDecimal() {
        this.testExpressionParenthesis();
    }

    @Test
    public void testExpressionParenthesisDouble() {
        this.testExpressionParenthesis();
    }

    private void testExpressionParenthesis() {
        this.parseExpressionEvaluateAndCheck("((1+2))", ((1 + 2)));
    }

// FIXME Spreadsheet parsing ignores everything after the first right parens.

    @Test
    public void testExpressionParenthesis2BigDecimal() {
        this.testExpressionParenthesis2();
    }

    @Test
    public void testExpressionParenthesis2Double() {
        this.testExpressionParenthesis2();
    }

    private void testExpressionParenthesis2() {
        this.parseExpressionEvaluateAndCheck("(1+2)*3.5", (1 + 2) * 3.5);
    }

    @Test
    public void testExpressionParenthesis3BigDecimal() {
        this.testExpressionParenthesis3();
    }

    @Test
    public void testExpressionParenthesis3Double() {
        this.testExpressionParenthesis3();
    }

    private void testExpressionParenthesis3() {
        this.parseExpressionEvaluateAndCheck("((1+2)*3.5)", ((1 + 2) * 3.5));
    }

    @Test
    public void testExpressionParenthesis4BigDecimal() {
        this.testExpressionParenthesis4();
    }

    @Test
    public void testExpressionParenthesis4Double() {
        this.testExpressionParenthesis4();
    }

    private void testExpressionParenthesis4() {
        this.parseExpressionEvaluateAndCheck("(1+2)+(3+4.5)", (1 + 2) + (3 + 4.5));
    }

    @Test
    public void testExpressionParenthesis5BigDecimal() {
        this.testExpressionParenthesis5();
    }

    @Test
    public void testExpressionParenthesis5Double() {
        this.testExpressionParenthesis5();
    }

    private void testExpressionParenthesis5() {
        assertEquals(-42.0, (1 + 2) + (3 + 4.5) * -6, 0.5);
        this.parseExpressionEvaluateAndCheck("(1+2)+(3+4.5)*-6", -42);
    }

    @Test
    public void testExpressionParenthesis6BigDecimal() {
        this.testExpressionParenthesis6();
    }

    @Test
    public void testExpressionParenthesis6Double() {
        this.testExpressionParenthesis6();
    }

    private void testExpressionParenthesis6() {
        this.parseExpressionEvaluateAndCheck("(1+2*(3+4*(5+6)*-7))", (1 + 2 * (3 + 4 * (5 + 6) * -7)));
    }

    @Test
    public void testExpressionParenthesis7BigDecimal() {
        this.testExpressionParenthesis7();
    }

    @Test
    public void testExpressionParenthesis7Double() {
        this.testExpressionParenthesis7();
    }

    private void testExpressionParenthesis7() {
        this.parseExpressionEvaluateAndCheck("-(1+2*(3+4*(5+6)*-7))", -(1 + 2 * (3 + 4 * (5 + 6) * -7)));
    }

    @Test
    public void testExpressionParenthesis8BigDecimal() {
        this.testExpressionParenthesis8();
    }

    @Test
    public void testExpressionParenthesis8Double() {
        this.testExpressionParenthesis8();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testExpressionParenthesis8() {
        this.parseExpressionEvaluateAndCheck("-(1+2*(3+4*(5+6)*-(7*8+(9+0))))", -(1 + 2 * (3 + 4 * (5 + 6) * -(7 * 8 + (9 + 0)))));
    }

    @Test
    public void testExpressionParenthesis9BigDecimal() {
        this.testExpressionParenthesis9();
    }

    @Test
    public void testExpressionParenthesis9Double() {
        this.testExpressionParenthesis9();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testExpressionParenthesis9() {
        this.parseExpressionEvaluateAndCheck("((((1+2))))", ((((1 + 2)))));
    }

    @Test
    public void testExpressionParenthesis10BigDecimal() {
        this.testExpressionParenthesis10();
    }

    @Test
    public void testExpressionParenthesis10Double() {
        this.testExpressionParenthesis10();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testExpressionParenthesis10() {
        this.parseExpressionEvaluateAndCheck("-(-(-(-(1+2))))", -(-(-(-(1 + 2)))));
    }

    @Test
    public void testExpressionLongFormulaWithoutParensBigDecimal() {
        this.testExpressionLongFormulaWithoutParens();
    }

    @Test
    public void testExpressionLongFormulaWithoutParensDouble() {
        this.testExpressionLongFormulaWithoutParens();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testExpressionLongFormulaWithoutParens() {
        this.parseExpressionEvaluateAndCheck("1+2-3+4-5*6+7-8*9", 1 + 2 - 3 + 4 - 5 * 6 + 7 - 8 * 9);
    }

    @Test
    public void testExpressionLongFormulaWithoutParens2BigDecimal() {
        this.testExpressionLongFormulaWithoutParens2();
    }

    @Test
    public void testExpressionLongFormulaWithoutParens2Double() {
        this.testExpressionLongFormulaWithoutParens2();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void testExpressionLongFormulaWithoutParens2() {
        this.parseExpressionEvaluateAndCheck("-1+2-3+4-5*6+7-8*9", -1 + 2 - 3 + 4 - 5 * 6 + 7 - 8 * 9);
    }

    @Test
    public void testExpressionLocalDateSubtractionBigDecimal() {
        this.testExpressionLocalDateSubtraction();
    }

    @Test
    public void testExpressionLocalDateSubtractionDouble() {
        this.testExpressionLocalDateSubtraction();
    }

    private void testExpressionLocalDateSubtraction() {
        this.parseExpressionEvaluateAndCheck("toDate(\"2000-01-03\")-toDate(\"1999-12-31\")", 3);// days!
    }

    @Test
    public void testExpressionLocalDateTimeSubtractionBigDecimal() {
        this.testExpressionLocalDateTimeSubtraction();
    }

    @Test
    public void testExpressionLocalDateTimeSubtractionDouble() {
        this.testExpressionLocalDateTimeSubtraction();
    }

    private void testExpressionLocalDateTimeSubtraction() {
        this.parseExpressionEvaluateAndCheck("toDateTime(\"2000-02-01T12:00:00\")-toDateTime(\"2000-01-31T06:00:00\")", 1.25); //1 1/4days
    }

    @Test
    public void testExpressionLocalTimeSubtractionBigDecimal() {
        this.testExpressionLocalTimeSubtraction();
    }

    @Test
    public void testExpressionLocalTimeSubtractionDouble() {
        this.testExpressionLocalTimeSubtraction();
    }

    private void testExpressionLocalTimeSubtraction() {
        this.parseExpressionEvaluateAndCheck("toTime(toTime(\"18:00:00\")-toTime(\"06:00:00\"))", "12:00"); //1/2 a day or 12noon
    }

    // helpers..........................................................................................................

    private void parseExpressionThrows(final String text,
                                       final String message) {
        this.parseThrows(
                SpreadsheetParsers.expression(),
                text,
                message
        );

        final int at = message.indexOf("at (");
        final int at2 = message.indexOf(",", at);
        final int column = Integer.parseInt(message.substring(at + 4, at2));

        //+1 to the column number in the message
        this.parseThrows(
                SpreadsheetParsers.valueOrExpression(),
                "=" + text,
                message.substring(0, at + 4) + (column+1) + message.substring(at2)
        );
    }

    private void parseExpressionAndCheck(final String formula,
                                         final ParserToken token,
                                         final String text) {
        this.parseAndCheck(
                SpreadsheetParsers.expression(),
                formula,
                token,
                text,
                ""
        );

        final String equalsFormula = "=" + formula;
        this.parseAndCheck(
                SpreadsheetParsers.valueOrExpression(),
                equalsFormula,
                SpreadsheetParserToken.expression(
                        Lists.of(
                                SpreadsheetParserToken.equalsSymbol("=", "="),
                                token
                        ),
                        equalsFormula
                ),
                equalsFormula,
                ""
        );
    }

    private void parseExpressionEvaluateAndCheck(final String formulaText,
                                                 final Object expectedText) {
        this.parseExpressionEvaluateAndCheck(formulaText, String.valueOf(expectedText));
    }

    /**
     * Accepts a formula with an expression. Note the expression is assumed to NOT having the leading equals sign.
     * The second part of the test will prefix an equals sign and attempt to parse using the {@link SpreadsheetParsers#valueOrExpression()} parser.
     */
    private void parseExpressionEvaluateAndCheck(final String formulaText,
                                                 final String expectedText) {
        this.parseExpressionEvaluateAndCheck0(
                SpreadsheetParsers.expression(),
                formulaText,
                expectedText
        );

        this.parseExpressionEvaluateAndCheck0(
                SpreadsheetParsers.valueOrExpression(),
                "=" + formulaText,
                expectedText
        );

        this.parseExpressionEvaluateAndCheck0(
                SpreadsheetParsers.valueOrExpression(),
                "= " + formulaText,
                expectedText
        );
    }

    private void parseExpressionEvaluateAndCheck0(final Parser<SpreadsheetParserContext> parser,
                                                  final String formulaText,
                                                  final String expectedText) {
        this.parseExpressionEvaluateAndCheck1(parser, formulaText, ExpressionNumberKind.BIG_DECIMAL, expectedText);
        this.parseExpressionEvaluateAndCheck1(parser, formulaText, ExpressionNumberKind.DOUBLE, expectedText);
    }

    private void parseExpressionEvaluateAndCheck1(final Parser<SpreadsheetParserContext> parser,
                                                  final String formulaText,
                                                  final ExpressionNumberKind kind,
                                                  final String expectedText) {
        final SpreadsheetParserToken formula = this.parse(parser, formulaText);
        final Optional<Expression> maybeExpression = formula.toExpression(ExpressionNumberContexts.basic(kind, MathContext.DECIMAL32));
        if (!maybeExpression.isPresent()) {
            fail("Failed to convert spreadsheet formula to expression " + CharSequences.quoteAndEscape(formulaText));
        }
        final Expression expression = maybeExpression.get();
        final String value = expression.toString(this.expressionEvaluationContext(kind));
        assertEquals(expectedText, value, () -> "expression " + CharSequences.quoteAndEscape(formulaText) + " as text is");
    }

    private SpreadsheetParserToken parse(final Parser<SpreadsheetParserContext> parser,
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
            fail("Parser left " + CharSequences.quoteAndEscape(leftOver) + " from " + CharSequences.quoteAndEscape(parse));
        }
        return spreadsheetFormula.get()
                .cast(SpreadsheetParserToken.class);
    }

    private ExpressionEvaluationContext expressionEvaluationContext(final ExpressionNumberKind kind) {
        final Function<ConverterContext, ParserContext> parserContext = (c) -> ParserContexts.basic(c, c);

        final Converter stringDouble = Converters.parser(
                Double.class,
                Parsers.doubleParser(),
                parserContext,
                (t) -> t.cast(DoubleParserToken.class).value()
        );
        final Converter stringLocalDate = Converters.parser(
                LocalDate.class,
                Parsers.localDate((c) -> DateTimeFormatter.ISO_LOCAL_DATE),
                parserContext,
                (t) -> t.cast(LocalDateParserToken.class).value()
        );
        final Converter stringLocalDateTime = Converters.parser(
                LocalDateTime.class,
                Parsers.localDateTime((c) -> DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                parserContext,
                (t) -> t.cast(LocalDateTimeParserToken.class).value()
        );
        final Converter stringLocalTime = Converters.parser(
                LocalTime.class,
                Parsers.localTime((c) -> DateTimeFormatter.ISO_LOCAL_TIME),
                parserContext,
                (t) -> t.cast(LocalTimeParserToken.class).value()
        );

        final Converter converter = Converters.collection(Lists.of(
                Converters.simple(),
                ExpressionNumber.toConverter(Converters.numberNumber()),
                ExpressionNumber.toConverter(Converters.localDateNumber(Converters.JAVA_EPOCH_OFFSET)),
                ExpressionNumber.toConverter(Converters.localDateTimeNumber(Converters.JAVA_EPOCH_OFFSET)),
                ExpressionNumber.toConverter(Converters.localTimeNumber()),
                ExpressionNumber.fromConverter(Converters.numberLocalTime()),
                ExpressionNumber.toConverter(stringDouble),
                stringLocalDate,
                stringLocalDateTime,
                stringLocalTime,
                Converters.objectString()
        ));

        return new FakeExpressionEvaluationContext() {

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return kind;
            }

            @Override
            public ExpressionFunction<?, ExpressionFunctionContext> function(final FunctionExpressionName name) {
                switch (name.value()) {
                    case "toDate":
                        return new FakeExpressionFunction<>() {
                            @Override
                            public Object apply(final List<Object> parameters,
                                                final ExpressionFunctionContext context) {
                                return convertStringParameter(parameters, LocalDate.class);
                            }

                            @Override
                            public boolean resolveReferences() {
                                return true;
                            }
                        };
                    case "toDateTime":
                        return new FakeExpressionFunction<>() {
                            @Override
                            public Object apply(final List<Object> parameters,
                                                final ExpressionFunctionContext context) {
                                return convertStringParameter(parameters, LocalDateTime.class);
                            }

                            @Override
                            public boolean resolveReferences() {
                                return true;
                            }
                        };
                    case "toTime":
                        return new FakeExpressionFunction<>() {
                            @Override
                            public Object apply(final List<Object> parameters,
                                                final ExpressionFunctionContext context) {
                                return convertStringParameter(parameters, LocalTime.class);
                            }

                            @Override
                            public boolean resolveReferences() {
                                return true;
                            }
                        };
                    default:
                        throw new UnknownExpressionFunctionException(name);
                }
            }

            private <T> T convertStringParameter(final List<Object> parameters, final Class<T> targetType) {
                if (parameters.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 parameter=" + parameters);
                }
                return this.convertOrFail(parameters.get(0), targetType);
            }

            @Override
            public Optional<Expression> reference(final ExpressionReference reference) {
                throw new UnsupportedOperationException();//return context.reference(reference);
            }


            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public <T> Either<T, String> convert(final Object value, final Class<T> target) {
                return converter.convert(value,
                        target,
                        ExpressionNumberConverterContexts.basic(Converters.fake(),
                                ConverterContexts.basic(Converters.fake(),
                                        dateTimeContext(),
                                        decimalNumberContext()),
                                this.expressionNumberKind()));
            }
        };
    }

    @Override
    public Parser<SpreadsheetParserContext> createParser() {
        return SpreadsheetParsers.expression();
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return this.createContext(this.expressionNumberKind);
    }

    private SpreadsheetParserContext createContext(final ExpressionNumberKind kind) {
        final DateTimeContext dateTimeContext = this.dateTimeContext();
        final DecimalNumberContext decimalNumberContext = this.decimalNumberContext();
        return new SpreadsheetParserContext() {
            @Override
            public List<String> ampms() {
                return dateTimeContext.ampms();
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
            public char groupingSeparator() {
                return decimalNumberContext.groupingSeparator();
            }

            @Override
            public char percentageSymbol() {
                return decimalNumberContext.percentageSymbol();
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
            public Locale locale() {
                return decimalNumberContext.locale();
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return kind;
            }
        };
    }

    private SpreadsheetParserToken number(final double value) {
        final ExpressionNumber expressionNumber = this.expressionNumberKind.create(value);
        return SpreadsheetParserToken.expressionNumber(expressionNumber, expressionNumber.toString());
    }

    private SpreadsheetCellReferenceParserToken cell(final int column, final String columnText, final int row) {
        final SpreadsheetParserToken columnToken = SpreadsheetParserToken.columnReference(SpreadsheetColumnOrRowReference.column(column, SpreadsheetReferenceKind.RELATIVE), columnText);
        final SpreadsheetParserToken rowToken = SpreadsheetParserToken.rowReference(SpreadsheetColumnOrRowReference.row(row, SpreadsheetReferenceKind.RELATIVE), String.valueOf(1 + row));
        return SpreadsheetParserToken.cellReference(Lists.of(columnToken, rowToken), columnToken.text() + rowToken.text());
    }

    private SpreadsheetParserToken functionName(final String name) {
        return SpreadsheetParserToken.functionName(SpreadsheetFunctionName.with(name), name);
    }

    private SpreadsheetLabelNameParserToken label(final String label) {
        return SpreadsheetParserToken.labelName(SpreadsheetExpressionReference.labelName(label), label);
    }

    private SpreadsheetParserToken negative(final SpreadsheetParserToken number) {
        return SpreadsheetParserToken.negative(Lists.of(minus(), number), "-" + number.text());
    }

    private SpreadsheetParserToken whitespace() {
        return SpreadsheetParserToken.whitespace("  ", "  ");
    }

    private SpreadsheetParserToken between() {
        return SpreadsheetParserToken.betweenSymbol(":", ":");
    }

    private SpreadsheetParserToken divide() {
        return SpreadsheetParserToken.divideSymbol("/", "/");
    }

    private SpreadsheetParserToken doubleQuotes() {
        return SpreadsheetParserToken.doubleQuoteSymbol("\"", "\"");
    }

    private SpreadsheetParserToken equals() {
        return SpreadsheetParserToken.equalsSymbol("==", "==");
    }

    private SpreadsheetParserToken greaterThan() {
        return SpreadsheetParserToken.greaterThanSymbol(">", ">");
    }

    private SpreadsheetParserToken greaterThanEquals() {
        return SpreadsheetParserToken.greaterThanEqualsSymbol(">=", ">=");
    }

    private SpreadsheetParserToken lessThan() {
        return SpreadsheetParserToken.lessThanSymbol("<", "<");
    }

    private SpreadsheetParserToken lessThanEquals() {
        return SpreadsheetParserToken.lessThanEqualsSymbol("<=", "<=");
    }

    private SpreadsheetParserToken minus() {
        return SpreadsheetParserToken.minusSymbol("-", "-");
    }

    private SpreadsheetParserToken multiply() {
        return SpreadsheetParserToken.multiplySymbol("*", "*");
    }

    private SpreadsheetParserToken notEquals() {
        return SpreadsheetParserToken.notEqualsSymbol("!=", "!=");
    }

    private SpreadsheetParserToken percent() {
        return SpreadsheetParserToken.percentSymbol("%", "%");
    }

    private SpreadsheetParserToken plus() {
        return SpreadsheetParserToken.plusSymbol("+", "+");
    }

    private SpreadsheetParserToken power() {
        return SpreadsheetParserToken.powerSymbol("^", "^");
    }

    private SpreadsheetParserToken openParenthesis() {
        return SpreadsheetParserToken.parenthesisOpenSymbol("(", "(");
    }

    private SpreadsheetParserToken closeParenthesis() {
        return SpreadsheetParserToken.parenthesisCloseSymbol(")", ")");
    }

    private SpreadsheetRangeParserToken range(final SpreadsheetParserToken from, final SpreadsheetParserToken to) {
        final String text = from.text() + between() + to.text();
        return SpreadsheetParserToken.range(Lists.of(from, between(), to), text);
    }

    private SpreadsheetParserToken valueSeparator() {
        return SpreadsheetParserToken.valueSeparatorSymbol(",", ",");
    }

    // PublicStaticHelperTesting........................................................................................

    @Override
    public Class<SpreadsheetParsers> type() {
        return SpreadsheetParsers.class;
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
