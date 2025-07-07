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

package walkingkooka.spreadsheet.formula.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class NumberSpreadsheetFormulaParserTokenTest extends ValueSpreadsheetFormulaParserTokenTestCase<NumberSpreadsheetFormulaParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken("1/2/2003");
    }

    @Test
    public void testNumberNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createToken().toNumber(null));
    }

    @Test
    public void testToExpressionNumber0() {
        this.toExpressionAndCheck2(
            0.0,
            digit("0")
        );
    }

    @Test
    public void testToExpressionNumber01() {
        this.toExpressionAndCheck2(
            1.0,
            digit("01")
        );
    }

    @Test
    public void testToExpressionNumber001() {
        this.toExpressionAndCheck2(
            1.0,
            digit("001")
        );
    }

    @Test
    public void testToExpressionNumber1() {
        this.toExpressionAndCheck2(
            1.0,
            digit("1")
        );
    }

    @Test
    public void testToExpressionNumber5600() {
        this.toExpressionAndCheck2(
            5600.0,
            digit("5600")
        );
    }

    @Test
    public void testToExpressionNumber123() {
        this.toExpressionAndCheck2(
            123.0,
            digit("123")
        );
    }

    @Test
    public void testToExpressionPlusNumber1() {
        this.toExpressionAndCheck2(
            1.0,
            plus(),
            digit("1")
        );
    }

    @Test
    public void testToExpressionNumber0Dot0() {
        this.toExpressionAndCheck2(
            0.0,
            plus(),
            digit("0"),
            decimalSeparator(),
            digit("0")
        );
    }

    @Test
    public void testToExpressionPlusNumber1Dot0() {
        this.toExpressionAndCheck2(
            1.0,
            plus(),
            digit("1"),
            decimalSeparator(),
            digit("0")
        );
    }

    @Test
    public void testToExpressionPlusNumber1Dot23() {
        this.toExpressionAndCheck2(
            1.23,
            plus(),
            digit("1"),
            decimalSeparator(),
            digit("23")
        );
    }

    @Test
    public void testToExpressionMinusNumber1() {
        this.toExpressionAndCheck2(
            -1.0,
            minus(),
            digit("1")
        );
    }

    @Test
    public void testToExpressionMinusNumber1Dot0() {
        this.toExpressionAndCheck2(
            -1.0,
            minus(),
            digit("1"),
            decimalSeparator(),
            digit("0")
        );
    }

    @Test
    public void testToExpressionMinusNumber1Dot23() {
        this.toExpressionAndCheck2(
            -1.23,
            minus(),
            digit("1"),
            decimalSeparator(),
            digit("23")
        );
    }

    @Test
    public void testToExpressionNumber1ExponentNumber1() {
        this.toExpressionAndCheck2(
            1E2,
            digit("1"),
            exponent(),
            digit("2")
        );
    }

    @Test
    public void testToExpressionNumber1ExponentNumber12() {
        this.toExpressionAndCheck2(
            1E12,
            digit("1"),
            exponent(),
            digit("12")
        );
    }

    @Test
    public void testToExpressionNumber1DecimalNumber2ExponentPlusNumber3() {
        this.toExpressionAndCheck2(
            1.2E+3,
            digit("1"),
            decimalSeparator(),
            digit("2"),
            exponent(),
            plus(),
            digit("3")
        );
    }

    @Test
    public void testToExpressionNumber1DecimalNumber2ExponentPlusNumber34() {
        this.toExpressionAndCheck2(
            1.2E+34,
            digit("1"),
            decimalSeparator(),
            digit("2"),
            exponent(),
            plus(),
            digit("34")
        );
    }

    @Test
    public void testToExpressionNumber1DecimalNumber2ExponentMinusNumber3() {
        this.toExpressionAndCheck2(
            1.2E-3,
            digit("1"),
            decimalSeparator(),
            digit("2"),
            exponent(),
            minus(),
            digit("3")
        );
    }

    @Test
    public void testToExpressionNumber1DecimalNumber2ExponentMinusNumber34() {
        this.toExpressionAndCheck2(
            1.2E-34,
            digit("1"),
            decimalSeparator(),
            digit("2"),
            exponent(),
            minus(),
            digit("34")
        );
    }

    @Test
    public void testToExpressionNumber1ExponentPlusNumber1() {
        this.toExpressionAndCheck2(
            1E+2,
            digit("1"),
            exponent(),
            plus(),
            digit("2")
        );
    }

    @Test
    public void testToExpressionNumber1ExponentPlusNumber12() {
        this.toExpressionAndCheck2(
            1E+12,
            digit("1"),
            exponent(),
            plus(),
            digit("12")
        );
    }

    @Test
    public void testToExpressionNumber1ExponentMinusNumber1() {
        this.toExpressionAndCheck2(
            1E-2,
            digit("1"),
            exponent(),
            minus(),
            digit("2")
        );
    }

    @Test
    public void testToExpressionNumber1ExponentMinusNumber23() {
        this.toExpressionAndCheck2(
            1E-23,
            digit("1"),
            exponent(),
            minus(),
            digit("23")
        );
    }

    @Test
    public void testToExpressionNumber0Percent() {
        this.toExpressionAndCheck2(
            0.0,
            digit("0"),
            percent()
        );
    }

    @Test
    public void testToExpressionNumber50Percent() {
        this.toExpressionAndCheck2(
            0.5,
            digit("50"),
            percent()
        );
    }

    @Test
    public void testToExpressionNumber200Percent() {
        this.toExpressionAndCheck2(
            2.0,
            digit("200"),
            percent()
        );
    }

    @Test
    public void testToExpressionNumberPercent300() {
        this.toExpressionAndCheck2(
            3.0,
            digit("300"),
            percent()
        );
    }

    @Test
    public void testToExpressionNumberMinusPercent400() {
        this.toExpressionAndCheck2(
            -4.0,
            minus(),
            digit("400"),
            percent()
        );
    }

    private static DigitsSpreadsheetFormulaParserToken digit(final String text) {
        return DigitsSpreadsheetFormulaParserToken.digits(text, text);
    }

    private static ExponentSymbolSpreadsheetFormulaParserToken exponent() {
        return DigitsSpreadsheetFormulaParserToken.exponentSymbol("E", "E");
    }

    private static MinusSymbolSpreadsheetFormulaParserToken minus() {
        return DigitsSpreadsheetFormulaParserToken.minusSymbol("-", "-");
    }

    private static PercentSymbolSpreadsheetFormulaParserToken percent() {
        return DigitsSpreadsheetFormulaParserToken.percentSymbol("%", "%");
    }

    private static PlusSymbolSpreadsheetFormulaParserToken plus() {
        return DigitsSpreadsheetFormulaParserToken.plusSymbol("+", "+");
    }

    private void toExpressionAndCheck2(final Double expected,
                                       final SpreadsheetFormulaParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        final NumberSpreadsheetFormulaParserToken numberParserToken = NumberSpreadsheetFormulaParserToken.with(
            tokensList,
            ParserToken.text(tokensList)
        );
        this.toExpressionAndCheck2(
            numberParserToken,
            ExpressionNumberKind.BIG_DECIMAL,
            expected
        );
        this.toExpressionAndCheck2(
            numberParserToken,
            ExpressionNumberKind.DOUBLE,
            expected
        );
    }


    private void toExpressionAndCheck2(final NumberSpreadsheetFormulaParserToken token,
                                       final ExpressionNumberKind kind,
                                       final Double expected) {
        final ExpressionNumber expressionNumber = kind.create(expected);
        final ExpressionEvaluationContext context = this.expressionEvaluationContext(kind);

        this.checkEquals(
            expressionNumber,
            token.toNumber(context),
            () -> "toNumber() " + token
        );

        this.toExpressionAndCheck(
            token,
            context,
            Expression.value(expressionNumber)
        );
    }

    private ExpressionEvaluationContext expressionEvaluationContext(final ExpressionNumberKind kind) {
        return new FakeExpressionEvaluationContext() {

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return kind;
            }
        };
    }

    @Override
    NumberSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.number(tokens, text);
    }

    @Override
    public String text() {
        return "" + DAY + "/" + MONTH + "/" + YEAR;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(
            this.dayNumber(),
            this.slashTextLiteral(),
            this.monthNumber(),
            this.slashTextLiteral(),
            this.year()
        );
    }

    @Override
    public NumberSpreadsheetFormulaParserToken createDifferentToken() {
        final String different = "" + YEAR + "/" + MONTH + "/" + DAY;

        return this.createToken(
            different,
            year(),
            slashTextLiteral(),
            monthNumber(),
            slashTextLiteral(),
            dayNumber()
        );
    }

    @Override
    public Class<NumberSpreadsheetFormulaParserToken> type() {
        return NumberSpreadsheetFormulaParserToken.class;
    }

    @Override
    public NumberSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallNumber(from, context);
    }
}
