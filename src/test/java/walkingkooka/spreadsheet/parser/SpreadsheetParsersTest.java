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
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.lang.reflect.Method;
import java.math.MathContext;
import java.util.List;

public final class SpreadsheetParsersTest implements PublicStaticHelperTesting<SpreadsheetParsers>,
    SpreadsheetParserTesting {

    // general..........................................................................................................

    @Test
    public void testGeneralParseInvalidFails() {
        this.parseFailAndCheck(
            SpreadsheetParsers.general(),
            this.generalParserContext(),
            "!"
        );
    }

    @Test
    public void testGeneralParseIntegerZero() {
        this.generalParseAndCheck(
            digits("0")
        );
    }

    @Test
    public void testGeneralParseIntegerPositive() {
        this.generalParseAndCheck(
            digits("1")
        );
    }

    @Test
    public void testGeneralParseIntegerPositive2() {
        this.generalParseAndCheck(
            digits("123")
        );
    }

    @Test
    public void testGeneralParseIntegerNegative() {
        this.generalParseAndCheck(
            minus(),
            digits("1")
        );
    }

    @Test
    public void testGeneralParseIntegerNegative2() {
        this.generalParseAndCheck(
            minus(),
            digits("123")
        );
    }

    @Test
    public void testGeneralParseDecimalZero() {
        this.generalParseAndCheck(
            digits("0"),
            decimal(),
            digits("0")
        );
    }

    @Test
    public void testGeneralParseDecimalZero2() {
        this.generalParseAndCheck(
            digits("0"),
            decimal(),
            digits("00")
        );
    }

    @Test
    public void testGeneralParseDecimalPositiveZero() {
        this.generalParseAndCheck(
            plus(),
            digits("0"),
            decimal(),
            digits("0")
        );
    }

    @Test
    public void testGeneralParseDecimalNegativeZero() {
        this.generalParseAndCheck(
            minus(),
            digits("0"),
            decimal(),
            digits("0")
        );
    }

    @Test
    public void testGeneralParseDecimalPositiveNumber() {
        this.generalParseAndCheck(
            plus(),
            digits("1"),
            decimal(),
            digits("0")
        );
    }

    @Test
    public void testGeneralParseDecimalPositiveNumber2() {
        this.generalParseAndCheck(
            plus(),
            digits("1"),
            decimal(),
            digits("23")
        );
    }

    @Test
    public void testGeneralParseDecimalNegativeNumber() {
        this.generalParseAndCheck(
            minus(),
            digits("1"),
            decimal(),
            digits("0")
        );
    }

    @Test
    public void testGeneralParseDecimalNegativeNumber2() {
        this.generalParseAndCheck(
            minus(),
            digits("1"),
            decimal(),
            digits("23")
        );
    }

    // 0E0
    @Test
    public void testGeneralParseScientificZero() {
        this.generalParseAndCheck(
            digits("0"),
            exponent(),
            digits("0")
        );
    }

    // 0.0E0
    @Test
    public void testGeneralParseScientificZero2() {
        this.generalParseAndCheck(
            digits("0"),
            decimal(),
            digits("0"),
            exponent(),
            digits("0")
        );
    }

    @Test
    public void testGeneralParseScientificNumber() {
        this.generalParseAndCheck(
            digits("1"),
            decimal(),
            digits("2"),
            exponent(),
            digits("3")
        );
    }

    @Test
    public void testGeneralParseScientificNumber2() {
        this.generalParseAndCheck(
            digits("1"),
            decimal(),
            digits("2"),
            exponent(),
            digits("34")
        );
    }

    @Test
    public void testGeneralParseScientificPositiveNumber() {
        this.generalParseAndCheck(
            plus(),
            digits("1"),
            decimal(),
            digits("2"),
            exponent(),
            digits("3")
        );
    }

    @Test
    public void testGeneralParseScientificPositiveNumber2() {
        this.generalParseAndCheck(
            plus(),
            digits("1"),
            decimal(),
            digits("2"),
            exponent(),
            digits("34")
        );
    }

    @Test
    public void testGeneralParseScientificNegativeNumber() {
        this.generalParseAndCheck(
            minus(),
            digits("1"),
            decimal(),
            digits("2"),
            exponent(),
            digits("3")
        );
    }

    @Test
    public void testGeneralParseScientificNegativeNumber2() {
        this.generalParseAndCheck(
            minus(),
            digits("1"),
            decimal(),
            digits("2"),
            exponent(),
            digits("34")
        );
    }

    private void generalParseAndCheck(final SpreadsheetFormulaParserToken... tokens) {
        this.generalParseAndCheck(
            Lists.of(tokens)
        );
    }

    private void generalParseAndCheck(final List<ParserToken> tokens) {
        final String text = ParserToken.text(tokens);

        this.generalParseAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                tokens,
                text
            )
        );
    }

    private void generalParseAndCheck(final String text,
                                      final ParserToken token) {
        this.parseAndCheck(
            SpreadsheetParsers.general(),
            this.generalParserContext(),
            text,
            token,
            text
        );
    }

    private SpreadsheetParserContext generalParserContext() {
        return SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.POSITION_EXPECTED,
            DateTimeContexts.fake(), // DateTimeContext unnecessary
            ExpressionNumberContexts.basic(
                ExpressionNumberKind.BIG_DECIMAL,
                DecimalNumberContexts.american(MathContext.DECIMAL32)
            ),
            ',' // valueSeparator
        );
    }


    private SpreadsheetFormulaParserToken decimal() {
        return SpreadsheetFormulaParserToken.decimalSeparatorSymbol(
            ".",
            "."
        );
    }

    private SpreadsheetFormulaParserToken digits(final String text) {
        return SpreadsheetFormulaParserToken.digits(
            text,
            text
        );
    }

    private SpreadsheetFormulaParserToken exponent() {
        return SpreadsheetFormulaParserToken.exponentSymbol(
            "E",
            "E"
        );
    }

    private SpreadsheetFormulaParserToken minus() {
        return SpreadsheetFormulaParserToken.minusSymbol(
            "-",
            "-"
        );
    }

    private SpreadsheetFormulaParserToken plus() {
        return SpreadsheetFormulaParserToken.plusSymbol(
            "+",
            "+"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParsers> type() {
        return SpreadsheetParsers.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
