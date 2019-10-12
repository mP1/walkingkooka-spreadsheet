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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.function.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class SpreadsheetParsersTest implements PublicStaticHelperTesting<SpreadsheetParsers>,
        ParserTesting2<Parser<SpreadsheetParserContext>, SpreadsheetParserContext> {

    @Test
    public void testText() {
        final String text = "\"abc-123\"";

        this.parseAndCheck(text,
                SpreadsheetTextParserToken.text("abc-123", text),
                text);
    }

    @Test
    public void testBigDecimal() {
        final String text = "1.5";

        this.parseAndCheck(text,
                bigDecimal(1.5),
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
        this.parseAndCheck(from, expected, text);
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
        this.parseAndCheck(from, expected, text);
    }

    // Negative.........................................................................................

    @Test
    public void testNegativeBigDecimal() {
        final String text = "-1.5";

        this.parseAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), bigDecimal(1.5)), text),
                text);
    }

    @Test
    public void testNegativeWhitespaceBigDecimal() {
        final String text = "-  1.5";

        this.parseAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), whitespace(), bigDecimal(1.5)), text),
                text);
    }

    @Test
    public void testNegativeBigDecimal2() {
        final String text = "-1";

        this.parseAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), bigDecimal(1)), text),
                text);
    }

    @Test
    public void testNegativeCell() {
        final String text = "-A1";

        this.parseAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), cell(0, "A", 0)), text),
                text);
    }

    @Test
    public void testNegativeLabel() {
        final String text = "-LabelABC";

        this.parseAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), label("LabelABC")), text),
                text);
    }

    @Test
    public void testNumberPercentage() {
        final String text = "1%";

        this.parseAndCheck(text,
                SpreadsheetParserToken.percentage(Lists.of(bigDecimal(1), percent()), text),
                text);
    }

    @Test
    public void testNegativeNumberPercentage() {
        final String text = "-1%";
        final SpreadsheetParserToken percent = SpreadsheetParserToken.percentage(Lists.of(bigDecimal(1), percent()), "1%");

        this.parseAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), percent), text),
                text);
    }

    @Test
    public void testGroupLabel() {
        final String labelText = "Hello";

        final String groupText = "(" + labelText + ")";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), label(labelText), closeParenthesis()), groupText);

        this.parseAndCheck(groupText, group, groupText);
    }

    @Test
    public void testGroupWhitespaceLabelWhitespace() {
        final String labelText = "Hello";
        final String groupText = "(  " + labelText + "  )";

        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), whitespace(), label(labelText), whitespace(), closeParenthesis()), groupText);

        this.parseAndCheck(groupText, group, groupText);
    }

    @Test
    public void testGroupNegativeNumber() {
        final SpreadsheetParserToken negative = negative(bigDecimal(123));

        final String groupText = "(-123)";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), negative, closeParenthesis()), groupText);

        this.parseAndCheck(groupText, group, groupText);
    }

    @Test
    public void testNegativeGroupNumber() {
        final String groupText = "(123)";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), bigDecimal(123), closeParenthesis()), groupText);

        final String text = "-" + groupText;
        this.parseAndCheck(text, negative(group), text);
    }

    @Test
    public void testAdd() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123+456";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), text);

        this.parseAndCheck(text, add, text);
    }

    @Test
    public void testAdd2() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123+456";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), bigDecimal(789)), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testAddNegative() {
        // 123+-456+789
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = negative(bigDecimal(456));
        final String text = "123+-456";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), bigDecimal(789)), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testSubtract() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123-456";
        final SpreadsheetSubtractionParserToken add = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        this.parseAndCheck(text, add, text);
    }

    @Test
    public void testSubtract2() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123-456";
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        final String text2 = text + "-789";
        final SpreadsheetSubtractionParserToken add2 = SpreadsheetParserToken.subtraction(Lists.of(sub, minus(), bigDecimal(789)), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testSubtractNegative() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = negative(bigDecimal(456));
        final String text = "123--456";
        final SpreadsheetSubtractionParserToken add = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        this.parseAndCheck(text, add, text);
    }

    @Test
    public void testSubtractAdd() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123-456";
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(left, minus(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(sub, plus(), bigDecimal(789)), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testSubtractWhitespaceAroundMinusSign() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123  -  456";
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(left, whitespace(), minus(), whitespace(), right), text);

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(sub, plus(), bigDecimal(789)), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testMultiply() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123*456";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(left, multiply(), right), text);

        this.parseAndCheck(text, multiply, text);
    }

    @Test
    public void testMultiply2() {
        final SpreadsheetParserToken left = bigDecimal(222);
        final SpreadsheetParserToken right = bigDecimal(333);
        final String text = "222*333";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(left, multiply(), right), text);

        final String text2 = "111+" + text;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(bigDecimal(111), plus(), multiply), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testNegativeMultiplyNegative() {
        final SpreadsheetParserToken left = negative(bigDecimal(123));
        final SpreadsheetParserToken right = negative(bigDecimal(456));
        final String text = "-123*-456";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(left, multiply(), right), text);

        this.parseAndCheck(text, multiply, text);
    }

    @Test
    public void testDivide() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123/456";
        final SpreadsheetDivisionParserToken divide = SpreadsheetParserToken.division(Lists.of(left, divide(), right), text);

        this.parseAndCheck(text, divide, text);
    }

    @Test
    public void testDivide2() {
        final SpreadsheetParserToken left = bigDecimal(222);
        final SpreadsheetParserToken right = bigDecimal(333);
        final String text = "222/333";
        final SpreadsheetDivisionParserToken divide = SpreadsheetParserToken.division(Lists.of(left, divide(), right), text);

        final String text2 = "111+" + text;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(bigDecimal(111), plus(), divide), text2);

        this.parseAndCheck(text2, add2, text2);
    }

    @Test
    public void testNegativeDivideNegative() {
        final SpreadsheetParserToken left = negative(bigDecimal(123));
        final SpreadsheetParserToken right = negative(bigDecimal(456));
        final String text = "-123/-456";
        final SpreadsheetDivisionParserToken divide = SpreadsheetParserToken.division(Lists.of(left, divide(), right), text);

        this.parseAndCheck(text, divide, text);
    }

    @Test
    public void testPower() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123^456";
        final SpreadsheetPowerParserToken power = SpreadsheetParserToken.power(Lists.of(left, power(), right), text);

        this.parseAndCheck(text, power, text);
    }

    @Test
    public void testPower2() {
        final SpreadsheetParserToken left = bigDecimal(222);
        final SpreadsheetParserToken right = bigDecimal(333);
        final String text = "222^333";
        final SpreadsheetPowerParserToken power = SpreadsheetParserToken.power(Lists.of(left, power(), right), text);

        final String text2 = "111*" + text;
        final SpreadsheetMultiplicationParserToken multiply2 = SpreadsheetParserToken.multiplication(Lists.of(bigDecimal(111), multiply(), power), text2);

        this.parseAndCheck(text2, multiply2, text2);
    }

    @Test
    public void testEquals() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123==456";
        final SpreadsheetEqualsParserToken equals = SpreadsheetParserToken.equalsParserToken(Lists.of(left, equals(), right), text);

        this.parseAndCheck(text, equals, text);
    }

    @Test
    public void testEqualsAdd() {
        final SpreadsheetParserToken middle = bigDecimal(456);
        final SpreadsheetParserToken right = bigDecimal(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = bigDecimal(123);
        final String text = "123==" + addText;
        final SpreadsheetEqualsParserToken equals = SpreadsheetParserToken.equalsParserToken(Lists.of(left, equals(), add), text);

        this.parseAndCheck(text, equals, text);
    }

    @Test
    public void testNotEquals() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123!=456";
        final SpreadsheetNotEqualsParserToken ne = SpreadsheetParserToken.notEquals(Lists.of(left, notEquals(), right), text);

        this.parseAndCheck(text, ne, text);
    }

    @Test
    public void testNotEqualsAdd() {
        final SpreadsheetParserToken middle = bigDecimal(456);
        final SpreadsheetParserToken right = bigDecimal(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = bigDecimal(123);
        final String text = "123!=" + addText;
        final SpreadsheetNotEqualsParserToken ne = SpreadsheetParserToken.notEquals(Lists.of(left, notEquals(), add), text);

        this.parseAndCheck(text, ne, text);
    }

    @Test
    public void testGreaterThan() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123>456";
        final SpreadsheetGreaterThanParserToken gt = SpreadsheetParserToken.greaterThan(Lists.of(left, greaterThan(), right), text);

        this.parseAndCheck(text, gt, text);
    }

    @Test
    public void testGreaterThanAdd() {
        final SpreadsheetParserToken middle = bigDecimal(456);
        final SpreadsheetParserToken right = bigDecimal(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = bigDecimal(123);
        final String text = "123>" + addText;
        final SpreadsheetGreaterThanParserToken gt = SpreadsheetParserToken.greaterThan(Lists.of(left, greaterThan(), add), text);

        this.parseAndCheck(text, gt, text);
    }

    @Test
    public void testGreaterThanEquals() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123>=456";
        final SpreadsheetGreaterThanEqualsParserToken gte = SpreadsheetParserToken.greaterThanEquals(Lists.of(left, greaterThanEquals(), right), text);

        this.parseAndCheck(text, gte, text);
    }

    @Test
    public void testGreaterThanEqualsAdd() {
        final SpreadsheetParserToken middle = bigDecimal(456);
        final SpreadsheetParserToken right = bigDecimal(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = bigDecimal(123);
        final String text = "123>=" + addText;
        final SpreadsheetGreaterThanEqualsParserToken gte = SpreadsheetParserToken.greaterThanEquals(Lists.of(left, greaterThanEquals(), add), text);

        this.parseAndCheck(text, gte, text);
    }

    @Test
    public void testLessThan() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123<456";
        final SpreadsheetLessThanParserToken lt = SpreadsheetParserToken.lessThan(Lists.of(left, lessThan(), right), text);

        this.parseAndCheck(text, lt, text);
    }

    @Test
    public void testLessThanAdd() {
        final SpreadsheetParserToken middle = bigDecimal(456);
        final SpreadsheetParserToken right = bigDecimal(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = bigDecimal(123);
        final String text = "123<" + addText;
        final SpreadsheetLessThanParserToken lt = SpreadsheetParserToken.lessThan(Lists.of(left, lessThan(), add), text);

        this.parseAndCheck(text, lt, text);
    }

    @Test
    public void testLessThanEquals() {
        final SpreadsheetParserToken left = bigDecimal(123);
        final SpreadsheetParserToken right = bigDecimal(456);
        final String text = "123<=456";
        final SpreadsheetLessThanEqualsParserToken lte = SpreadsheetParserToken.lessThanEquals(Lists.of(left, lessThanEquals(), right), text);

        this.parseAndCheck(text, lte, text);
    }

    @Test
    public void testLessThanEqualsAdd() {
        final SpreadsheetParserToken middle = bigDecimal(456);
        final SpreadsheetParserToken right = bigDecimal(789);
        final String addText = "456+789";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(middle, plus(), right), addText);

        final SpreadsheetParserToken left = bigDecimal(123);
        final String text = "123<=" + addText;
        final SpreadsheetLessThanEqualsParserToken lte = SpreadsheetParserToken.lessThanEquals(Lists.of(left, lessThanEquals(), add), text);

        this.parseAndCheck(text, lte, text);
    }

    @Test
    public void testComplexExpression() {
        //111+222+(-333)-444*555
        final String addText = "111+222";
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(bigDecimal(111), plus(), bigDecimal(222)), addText);

        final String groupText = "(-333)";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(openParenthesis(), negative(bigDecimal(333)), closeParenthesis()), groupText);

        final String addText2 = add + "+" + groupText;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), group), addText2);

        final String multiplyText = "444*555";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(bigDecimal(444), multiply(), bigDecimal(555)), multiplyText);

        final String subText = addText2 + "-" + multiplyText;
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(add2, minus(), multiply), subText);

        this.parseAndCheck(subText, sub, subText);
    }

    // Function.........................................................................................................

    @Test
    public void testFunctionParserOtherExpressionFails() {
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
    public void testFunctionWithOneArgument() {
        final String text = "xyz(123)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), bigDecimal(123), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgument2() {
        final String text = "xyz(  123)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), whitespace(), bigDecimal(123), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgument3() {
        final String text = "xyz(123  )";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), bigDecimal(123), whitespace(), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithOneArgument4() {
        final String text = "xyz(  123  )";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), whitespace(), bigDecimal(123), whitespace(), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithTwoArguments() {
        final String text = "xyz(123,456)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), bigDecimal(123), comma(), bigDecimal(456), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithFourArguments() {
        final String text = "xyz(1,2,3,4)";
        final SpreadsheetFunctionParserToken f = SpreadsheetParserToken.function(Lists.of(functionName("xyz"), openParenthesis(), bigDecimal(1), comma(), bigDecimal(2), comma(), bigDecimal(3), comma(), bigDecimal(4), closeParenthesis()), text);

        this.functionParseAndCheck(text, f, text);
    }

    @Test
    public void testFunctionWithinFunction() {
        final String yText = "y(123)";
        final SpreadsheetFunctionParserToken y = SpreadsheetParserToken.function(Lists.of(functionName("y"), openParenthesis(), bigDecimal(123), closeParenthesis()), yText);

        final String xText = "x(" + yText + ")";
        final SpreadsheetFunctionParserToken x = SpreadsheetParserToken.function(Lists.of(functionName("x"), openParenthesis(), y, closeParenthesis()), xText);

        this.functionParseAndCheck(xText, x, xText);
    }

    @Test
    public void testFunctionWithinFunctionWithinFunction() {
        final String zText = "z(123)";
        final SpreadsheetFunctionParserToken z = SpreadsheetParserToken.function(Lists.of(functionName("z"), openParenthesis(), bigDecimal(123), closeParenthesis()), zText);

        final String yText = "y(" + zText + ")";
        final SpreadsheetFunctionParserToken y = SpreadsheetParserToken.function(Lists.of(functionName("y"), openParenthesis(), z, closeParenthesis()), yText);

        final String xText = "x(" + yText + ")";
        final SpreadsheetFunctionParserToken x = SpreadsheetParserToken.function(Lists.of(functionName("x"), openParenthesis(), y, closeParenthesis()), xText);

        this.functionParseAndCheck(xText, x, xText);
    }

    @Test
    public void testFunctionWithRangeArgument() {
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
        this.parseAndCheck(this.functionParser(), from, expected, text);
        this.parseAndCheck(from, expected, text);
    }

    private Parser<SpreadsheetParserContext> functionParser() {
        return SpreadsheetParsers.function();
    }

    // Group ....................................................................................................

    @Test
    public void testGroupAndFurtherExpressions() {
        final SpreadsheetParserToken left = bigDecimal(1);
        final SpreadsheetParserToken right = bigDecimal(2);
        final SpreadsheetAdditionParserToken add = SpreadsheetParserToken.addition(Lists.of(left, plus(), right), "1+2");
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(this.openParenthesis(), add, this.closeParenthesis()), "(" + add.text() + ")");

        final SpreadsheetParserToken last = bigDecimal(3);
        final SpreadsheetMultiplicationParserToken mul = SpreadsheetParserToken.multiplication(Lists.of(group, multiply(), last), group.text() + "*3");

        this.parseAndCheck(mul.text(), mul, mul.text());
    }

    @Test
    public void testNestedGroupAddition() {
        final SpreadsheetParserToken left1 = bigDecimal(1);
        final SpreadsheetParserToken right1 = bigDecimal(2);
        final SpreadsheetAdditionParserToken add1 = SpreadsheetParserToken.addition(Lists.of(left1, plus(), right1), "1+2");
        final SpreadsheetGroupParserToken group1 = SpreadsheetParserToken.group(Lists.of(this.openParenthesis(), add1, this.closeParenthesis()), "(" + add1.text() + ")");

        final SpreadsheetParserToken left2 = bigDecimal(3);
        final SpreadsheetParserToken right2 = bigDecimal(4);
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(left2, plus(), right2), "3+4");
        final SpreadsheetGroupParserToken group2 = SpreadsheetParserToken.group(Lists.of(this.openParenthesis(), add2, this.closeParenthesis()), "(" + add2.text() + ")");

        final SpreadsheetMultiplicationParserToken mul = SpreadsheetParserToken.multiplication(Lists.of(group1, multiply(), group2), group1.text() + "*" + group2.text());
        this.parseAndCheck(mul.text(), mul, mul.text());
    }

    @Test
    public void testInvalidTokenFails() {
        this.parseThrows("!", this.reporterMessage('!', 1, 1));
    }

    @Test
    public void testInvalidTokenFails2() {
        this.parseThrows("  !", this.reporterMessage('!', 3, 1));
    }

    @Test
    public void testInvalidBinaryTokenRightFails() {
        this.parseThrows("1+!", this.reporterMessage('!', 3, 1));
    }

    @Test
    public void testInvalidGroupTokenFails() {
        this.parseThrows("( !", this.reporterMessage('!', 3, 1));
    }

    private String reporterMessage(final char c, final int column, final int row) {
        return "Unrecognized character " + CharSequences.quoteIfChars(c) + " at (" + column + "," + row + ")";
    }

    @Test
    public void testSimpleAddition() {
        this.parseEvaluateAndCheck("1+2", 1 + 2);
    }

    @Test
    public void testSimpleAdditionLeadingWhitespace() {
        this.parseEvaluateAndCheck(" 1+2", 1 + 2);
    }

    @Test
    public void testSimpleAdditionLeadingWhitespace2() {
        this.parseEvaluateAndCheck("  1+2", 1 + 2);
    }

    @Test
    public void testSimpleAdditionTrailingWhitespace() {
        this.parseEvaluateAndCheck("1+2 ", 1 + 2);
    }

    @Test
    public void testSimpleAdditionTrailingWhitespace2() {
        this.parseEvaluateAndCheck("1+2  ", 1 + 2);
    }

    @Test
    public void testSimpleAdditionSurroundedByWhitespace() {
        this.parseEvaluateAndCheck(" 1+2 ", 1 + 2);
    }

    @Test
    public void testSimpleMultiplication() {
        this.parseEvaluateAndCheck("3*4.5", 3 * 4.5);
    }

    @Test
    public void testMathOperatorPriority() {
        this.parseEvaluateAndCheck("1+2*3+4.5", 1 + 2 * 3 + 4.5);
    }

    @Test
    public void testParenthesis() {
        this.parseEvaluateAndCheck("((1+2))", ((1 + 2)));
    }

// FIXME Spreadsheet parsing ignores everything after the first right parens.

    @Test
    public void testParenthesis2() {
        this.parseEvaluateAndCheck("(1+2)*3.5", (1 + 2) * 3.5);
    }

    @Test
    public void testParenthesis3() {
        this.parseEvaluateAndCheck("((1+2)*3.5)", ((1 + 2) * 3.5));
    }

    @Test
    public void testParenthesis4() {
        this.parseEvaluateAndCheck("(1+2)+(3+4.5)", (1 + 2) + (3 + 4.5));
    }

    @Test
    public void testParenthesis5() {
        assertEquals(-42.0, (1 + 2) + (3 + 4.5) * -6, 0.5);
        this.parseEvaluateAndCheck("(1+2)+(3+4.5)*-6", -42.0);
    }

    @Test
    public void testParenthesis6() {
        this.parseEvaluateAndCheck("(1+2*(3+4*(5+6)*-7))", (1 + 2 * (3 + 4 * (5 + 6) * -7)));
    }

    @Test
    public void testParenthesis7() {
        this.parseEvaluateAndCheck("-(1+2*(3+4*(5+6)*-7))", -(1 + 2 * (3 + 4 * (5 + 6) * -7)));
    }

    @Test
    public void testParenthesis8() {
        this.parseEvaluateAndCheck("-(1+2*(3+4*(5+6)*-(7*8+(9+0))))", -(1 + 2 * (3 + 4 * (5 + 6) * -(7 * 8 + (9 + 0)))));
    }

    @Test
    public void testParenthesis9() {
        this.parseEvaluateAndCheck("((((1+2))))", ((((1 + 2)))));
    }

    @Test
    public void testParenthesis10() {
        this.parseEvaluateAndCheck("-(-(-(-(1+2))))", -(-(-(-(1 + 2)))));
    }

    @Test
    public void testLongFormulaWithoutParens() {
        this.parseEvaluateAndCheck("1+2-3+4-5*6+7-8*9", 1 + 2 - 3 + 4 - 5 * 6 + 7 - 8 * 9);
    }

    @Test
    public void testLongFormulaWithoutParens2() {
        this.parseEvaluateAndCheck("-1+2-3+4-5*6+7-8*9", -1 + 2 - 3 + 4 - 5 * 6 + 7 - 8 * 9);
    }

    @Test
    public void testLocalDateSubtraction() {
        this.parseEvaluateAndCheck("toDate(\"2000-01-03\")-toDate(\"1999-12-31\")", 3);// days!
    }

    @Test
    public void testLocalDateTimeSubtraction() {
        this.parseEvaluateAndCheck("toDateTime(\"2000-02-01T12:00:00\")-toDateTime(\"2000-01-31T06:00:00\")", 1.25); //1 1/4days
    }

    @Test
    public void testLocalTimeSubtraction() {
        this.parseEvaluateAndCheck("toTime(toTime(\"18:00:00\")-toTime(\"06:00:00\"))", "12:00"); //1/2 a day or 12noon
    }

    private void parseEvaluateAndCheck(final String formulaText, final Object expectedText) {
        this.parseEvaluateAndCheck(formulaText, String.valueOf(expectedText));
    }

    private void parseEvaluateAndCheck(final String formulaText, final String expectedText) {
        final SpreadsheetParserToken formula = this.parse(formulaText);
        final Optional<ExpressionNode> maybeExpression = formula.expressionNode();
        if (!maybeExpression.isPresent()) {
            fail("Failed to convert spreadsheet formula to expression " + CharSequences.quoteAndEscape(formulaText));
        }
        final ExpressionNode expression = maybeExpression.get();
        final String value = expression.toText(this.expressionEvaluationContext());
        assertEquals(expectedText, value, "expression " + CharSequences.quoteAndEscape(formulaText) + " as text is");
    }

    private SpreadsheetParserToken parse(final String parse) {
        final TextCursor cursor = TextCursors.charSequence(parse);
        final Optional<ParserToken> spreadsheetFormula = this.createParser().parse(cursor, this.createContext());
        if (!spreadsheetFormula.isPresent()) {
            fail("Parser failed to parseCellReference " + CharSequences.quoteAndEscape(parse));
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

    private ExpressionEvaluationContext expressionEvaluationContext() {
        final ExpressionNodeName toDate = ExpressionNodeName.with("toDate");
        final ExpressionNodeName toDateTime = ExpressionNodeName.with("toDateTime");
        final ExpressionNodeName toTime = ExpressionNodeName.with("toTime");

        final Function<ConverterContext, ParserContext> parserContext = (c) -> ParserContexts.basic(c, c);

        final Converter stringNumber = Converters.parser(Number.class,
                Parsers.bigDecimal(),
                parserContext);
        final Converter stringDouble = Converters.parser(Double.class,
                Parsers.doubleParser(),
                parserContext);
        final Converter stringLocalDate = Converters.parser(LocalDate.class,
                Parsers.localDate((c) -> DateTimeFormatter.ISO_LOCAL_DATE),
                parserContext);
        final Converter stringLocalDateTime = Converters.parser(LocalDateTime.class,
                Parsers.localDateTime((c) -> DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                parserContext);
        final Converter stringLocalTime = Converters.parser(LocalTime.class,
                Parsers.localTime((c) -> DateTimeFormatter.ISO_LOCAL_TIME),
                parserContext);
        final Converter stringLong = Converters.parser(Long.class,
                Parsers.longParser(10),
                parserContext);

        final Converter converter = Converters.collection(Lists.of(
                Converters.simple(),
                Converters.localDateNumber(Converters.JAVA_EPOCH_OFFSET),
                Converters.localDateTimeNumber(Converters.JAVA_EPOCH_OFFSET),
                Converters.localTimeNumber(),
                Converters.numberLocalTime(),
                stringDouble,
                stringLocalDate,
                stringLocalDateTime,
                stringLocalTime,
                stringLong,
                stringNumber,
                Converters.objectString()
        ));

        return new FakeExpressionEvaluationContext() {
            @Override
            public Object function(final ExpressionNodeName name, final List<Object> parameters) {
                if (toDate.equals(name)) {
                    return this.convertStringParameter(parameters, LocalDate.class);
                }
                if (toDateTime.equals(name)) {
                    return this.convertStringParameter(parameters, LocalDateTime.class);
                }
                if (toTime.equals(name)) {
                    return this.convertStringParameter(parameters, LocalTime.class);
                }

                throw new UnsupportedOperationException(); //return context.function(name, parameters);
            }

            private <T> T convertStringParameter(final List<Object> parameters, final Class<T> targetType) {
                if (parameters.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 parameter=" + parameters);
                }
                return this.convertOrFail(parameters.get(0), targetType);
            }

            @Override
            public Optional<ExpressionNode> reference(final ExpressionReference reference) {
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
                        ConverterContexts.basic(dateTimeContext(), decimalNumberContext()));
            }
        };
    }

    @Override
    public Parser<SpreadsheetParserContext> createParser() {
        return SpreadsheetParsers.expression();
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.basic(this.dateTimeContext(), this.decimalNumberContext());
    }

    private SpreadsheetParserToken bigDecimal(final double value) {
        final BigDecimal bigDecimal = BigDecimal.valueOf(value).stripTrailingZeros();
        return SpreadsheetParserToken.bigDecimal(bigDecimal, bigDecimal.toString());
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

    private SpreadsheetParserToken comma() {
        return SpreadsheetParserToken.functionParameterSeparatorSymbol(",", ",");
    }

    private SpreadsheetParserToken divide() {
        return SpreadsheetParserToken.divideSymbol("/", "/");
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
