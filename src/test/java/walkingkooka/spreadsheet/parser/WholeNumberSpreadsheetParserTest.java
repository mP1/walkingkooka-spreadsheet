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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Locale;

public final class WholeNumberSpreadsheetParserTest implements SpreadsheetParserTesting2<WholeNumberSpreadsheetParser>,
    ClassTesting<WholeNumberSpreadsheetParser> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testParseInvalidFails() {
        this.parseFailAndCheck("Invalid");
    }

    @Test
    public void testParseZero() {
        this.parseWholeNumberAndCheck(
            "0",
            digits("0")
        );
    }

    @Test
    public void testParsePlusZero() {
        this.parseWholeNumberAndCheck(
            "+0",
            plus(),
            digits("0")
        );
    }

    @Test
    public void testParseMinusZero() {
        this.parseWholeNumberAndCheck(
            "-0",
            minus(),
            digits("0")
        );
    }

    @Test
    public void testParseNumber() {
        this.parseWholeNumberAndCheck(
            "123",
            digits("123")
        );
    }

    @Test
    public void testParseNumberManyManyDigits() {
        final String text = "12345678901234567890";

        this.parseWholeNumberAndCheck(
            text,
            digits(text)
        );
    }

    @Test
    public void testParsePlusNumber() {
        this.parseWholeNumberAndCheck(
            "+456",
            plus(),
            digits("456")
        );
    }

    @Test
    public void testParseMinusNumber() {
        this.parseWholeNumberAndCheck(
            "-789",
            minus(),
            digits("789")
        );
    }

    private void parseWholeNumberAndCheck(final String text,
                                          final SpreadsheetFormulaParserToken... expected) {
        this.parseAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    expected
                ),
                ParserToken.text(
                    Lists.of(expected)
                )
            ),
            text
        );
    }

    private SpreadsheetFormulaParserToken digits(final String text) {
        return SpreadsheetFormulaParserToken.digits(
            text,
            text
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

    @Override
    public WholeNumberSpreadsheetParser createParser() {
        return WholeNumberSpreadsheetParser.INSTANCE;
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return new TestSpreadsheetParserContext();
    }

    static final class TestSpreadsheetParserContext implements SpreadsheetParserContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator {

        @Override
        public boolean canNumbersHaveGroupSeparator() {
            return false;
        }

        @Override
        public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                   final TextCursor text) {
            return InvalidCharacterExceptionFactory.POSITION_EXPECTED.apply(
                parser,
                text
            );
        }

        @Override
        public char valueSeparator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public DateTimeContext dateTimeContext() {
            return DateTimeContexts.fake();
        }

        @Override
        public DecimalNumberContext decimalNumberContext() {
            return DecimalNumberContexts.american(MathContext.DECIMAL32);
        }

        @Override
        public Locale locale() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    // class............................................................................................................

    @Override
    public Class<WholeNumberSpreadsheetParser> type() {
        return WholeNumberSpreadsheetParser.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
