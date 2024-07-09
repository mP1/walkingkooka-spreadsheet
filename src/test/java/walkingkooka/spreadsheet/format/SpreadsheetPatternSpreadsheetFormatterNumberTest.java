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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporterException;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionNumberConverterContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * In expectations all symbols are changed parse defaults to characters to verify the context is supplying such symbols.
 */
public final class SpreadsheetPatternSpreadsheetFormatterNumberTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterNumber,
        SpreadsheetFormatNumberParserToken> {

    private final static Color RED = Color.parse("#FF0000");

    // text-literal, escaped etc........................................................................................

    @Test
    public void testFormatCloseParens() {
        this.parseFormatAndCheck2(")");
    }

    @Test
    public void testFormatColon() {
        this.parseFormatAndCheck2(":");
    }

    @Test
    public void testFormatMinus() {
        this.parseFormatAndCheck2("-");
    }

    @Test
    public void testFormatPlus() {
        this.parseFormatAndCheck2("+");
    }

    @Test
    public void testFormatOpenParens() {
        this.parseFormatAndCheck2("(");
    }

    private void parseFormatAndCheck2(final String pattern) {
        this.parseFormatAndCheck2(pattern, pattern);
    }

    @Test
    public void testFormatEscaped() {
        this.parseFormatAndCheck2("\\A", "A");
    }

    @Test
    public void testFormatTextLiteral() {
        this.parseFormatAndCheck2("\"Quoted text\"", "Quoted text");
    }

    private void parseFormatAndCheck2(final String pattern,
                                      final String text) {
        this.parseFormatNumberAndCheck(
                pattern,
                "-999999",
                text
        );
    }

    // integers ......................................................................................................

    @Test
    public void testFormatHash0() {
        this.parseFormatAndCheck(
                "#",
                0,
                ""
        );
    }

    @Test
    public void testFormatHash1() {
        this.parseFormatAndCheck(
                "#",
                1,
                "1"
        );
    }

    @Test
    public void testFormatHash12() {
        this.parseFormatAndCheck(
                "#",
                12,
                "12"
        );
    }

    @Test
    public void testFormatHash123() {
        this.parseFormatAndCheck(
                "#",
                123,
                "123"
        );
    }

    @Test
    public void testFormatHash1000() {
        this.parseFormatAndCheck(
                "#",
                1000,
                "1000"
        );
    }

    @Test
    public void testFormatHash1234() {
        this.parseFormatAndCheck(
                "#",
                1234,
                "1234"
        );
    }

    @Test
    public void testFormatHash0_025() {
        this.parseFormatAndCheck(
                "#",
                0.025,
                ""
        );
    }

    @Test
    public void testFormatExtraHash0_025() {
        this.parseFormatAndCheck(
                "###",
                0.025,
                ""
        );
    }

    @Test
    public void testFormatQuestion0() {
        this.parseFormatAndCheck(
                "?",
                0,
                " "
        );
    }

    @Test
    public void testFormatQuestion1() {
        this.parseFormatAndCheck(
                "?",
                1,
                "1"
        );
    }

    @Test
    public void testFormatQuestion12() {
        this.parseFormatAndCheck(
                "?",
                12,
                "12"
        );
    }

    @Test
    public void testFormatQuestion123() {
        this.parseFormatAndCheck(
                "?",
                123,
                "123"
        );
    }

    @Test
    public void testFormatQuestion1234() {
        this.parseFormatAndCheck(
                "?",
                1234,
                "1234"
        );
    }

    @Test
    public void testFormatQuestion0_025() {
        this.parseFormatAndCheck(
                "?",
                0.025,
                " "
        );
    }

    @Test
    public void testFormatExtraQuestion() {
        this.parseFormatAndCheck(
                "???",
                12,
                " 12"
        );
    }

    @Test
    public void testFormatZero0() {
        this.parseFormatAndCheck(
                "0",
                0,
                "0"
        );
    }

    @Test
    public void testFormatZero1() {
        this.parseFormatAndCheck(
                "0",
                1,
                "1"
        );
    }

    @Test
    public void testFormatZero12() {
        this.parseFormatAndCheck(
                "0",
                12,
                "12"
        );
    }

    @Test
    public void testFormatZero123() {
        this.parseFormatAndCheck(
                "0",
                123,
                "123"
        );
    }

    @Test
    public void testFormatZero1000() {
        this.parseFormatAndCheck(
                "0",
                1000,
                "1000"
        );
    }

    @Test
    public void testFormatZero1234() {
        this.parseFormatAndCheck(
                "0",
                1234,
                "1234"
        );
    }

    @Test
    public void testFormatZero0_025() {
        this.parseFormatAndCheck(
                "0",
                0.025,
                "0"
        );
    }

    @Test
    public void testFormatExtraZero() {
        this.parseFormatAndCheck(
                "0000",
                12,
                "0012"
        );
    }

    // pattern longer than digits

    @Test
    public void testFormatHashHashHashHashHash0() {
        this.parseFormatAndCheck(
                "#####",
                0,
                ""
        );
    }

    @Test
    public void testFormatHashHashHashHashHash1() {
        this.parseFormatAndCheck(
                "#####",
                1,
                "1"
        );
    }

    @Test
    public void testFormatHashHashHashHashHash12() {
        this.parseFormatAndCheck(
                "#####",
                12,
                "12"
        );
    }

    @Test
    public void testFormatHashHashHashHashHash123() {
        this.parseFormatAndCheck(
                "#####",
                123,
                "123"
        );
    }

    @Test
    public void testFormatHashHashHashHashHash1234() {
        this.parseFormatAndCheck(
                "#####",
                1234,
                "1234"
        );
    }

    @Test
    public void testFormatQuestionQuestionQuestionQuestionQuestion0() {
        this.parseFormatAndCheck(
                "?????",
                0,
                "     "
        );
    }

    @Test
    public void testFormatQuestionQuestionQuestionQuestionQuestion1() {
        this.parseFormatAndCheck(
                "?????",
                1,
                "    1"
        );
    }

    @Test
    public void testFormatQuestionQuestionQuestionQuestionQuestion12() {
        this.parseFormatAndCheck(
                "?????",
                12,
                "   12"
        );
    }

    @Test
    public void testFormatQuestionQuestionQuestionQuestionQuestion123() {
        this.parseFormatAndCheck(
                "?????",
                123,
                "  123"
        );
    }

    @Test
    public void testFormatQuestionQuestionQuestionQuestionQuestion1234() {
        this.parseFormatAndCheck(
                "?????",
                1234,
                " 1234"
        );
    }

    @Test
    public void testFormatZeroZeroZeroZeroZero0() {
        this.parseFormatAndCheck(
                "00000",
                0,
                "00000"
        );
    }

    @Test
    public void testFormatZeroZeroZeroZeroZero1() {
        this.parseFormatAndCheck(
                "00000",
                1,
                "00001"
        );
    }

    @Test
    public void testFormatZeroZeroZeroZeroZero12() {
        this.parseFormatAndCheck(
                "00000",
                12,
                "00012"
        );
    }

    @Test
    public void testFormatZeroZeroZeroZeroZero123() {
        this.parseFormatAndCheck(
                "00000",
                123,
                "00123"
        );
    }

    @Test
    public void testFormatZeroZeroZeroZeroZero1234() {
        this.parseFormatAndCheck(
                "00000",
                1234,
                "01234"
        );
    }

    // hash + zero + question

    @Test
    public void testFormatHashHashZeroZeroQuestionQuestionHash5() {
        this.parseFormatAndCheck(
                "##00??#",
                5,
                "00  5"
        );
    }

    // negative.........................................................................................................

    @Test
    public void testFormatHashHashZeroZeroQuestionQuestionHashNegative5() {
        this.parseFormatAndCheck(
                "##00??#",
                -5,
                "N00  5"
        );
    }

    // plus.............................................................................................................

    @Test
    public void testFormatPlusNegativeNumber() {
        this.parseFormatAndCheck(
                "+#",
                -5,
                "+N5"
        );
    }

    @Test
    public void testFormatPlusPositiveNumber() {
        this.parseFormatAndCheck(
                "+#",
                +5,
                "+5"
        );
    }

    // minus............................................................................................................

    @Test
    public void testFormatMinusNegativeNumber() {
        this.parseFormatAndCheck(
                "-#",
                -5,
                "-N5"
        );
    }

    @Test
    public void testFormatMinusPositiveNumber() {
        this.parseFormatAndCheck(
                "-#",
                +5,
                "-5"
        );
    }

    // fraction.......................................................................................................

    @Test
    public void testFormatDecimal0_0() {
        this.parseFormatAndCheck(
                ".",
                0,
                "!"
        );
    }

    @Test
    public void testFormatDecimal0_4() {
        this.parseFormatAndCheck(
                ".",
                0.4,
                "!"
        );
    }

    @Test
    public void testFormatDecimal0_75() {
        this.parseFormatAndCheck(
                ".",
                0.75,
                "!"
        );
    }

    // fraction hash decimal hash dot............................................................................................

    @Test
    public void testFormatHashDecimal0_0() {
        this.parseFormatAndCheck(
                "#.",
                0.0,
                "!"
        );
    }

    @Test
    public void testFormatHashDecimal0_2() {
        this.parseFormatAndCheck(
                "#.",
                0.2,
                "!"
        );
    }

    @Test
    public void testFormatHashDecimal0_7() {
        this.parseFormatAndCheck(
                "#.",
                0.7,
                "1!"
        );
    }

    @Test
    public void testFormatHashDecimal1_4() {
        this.parseFormatAndCheck(
                "#.",
                1.4,
                "1!"
        );
    }

    @Test
    public void testFormatHashDecimal1_7() {
        this.parseFormatAndCheck(
                "#.",
                1.7,
                "2!"
        );
    }

    @Test
    public void testFormatHashDecimal0_025() {
        this.parseFormatAndCheck(
                "#.",
                0.025,
                "!"
        );
    }

    // fraction space. space dot............................................................................................

    @Test
    public void testFormatQuestionDecimal0_0() {
        this.parseFormatAndCheck(
                "?.",
                0.0,
                " !"
        );
    }

    @Test
    public void testFormatQuestionDecimal0_2() {
        this.parseFormatAndCheck(
                "?.",
                0.2,
                " !"
        );
    }

    @Test
    public void testFormatQuestionDecimal0_7() {
        this.parseFormatAndCheck(
                "?.",
                0.7,
                "1!"
        );
    }

    @Test
    public void testFormatQuestionDecimal1_4() {
        this.parseFormatAndCheck(
                "?.",
                1.4,
                "1!"
        );
    }

    @Test
    public void testFormatQuestionDecimal1_7() {
        this.parseFormatAndCheck(
                "?.",
                1.7,
                "2!"
        );
    }

    @Test
    public void testFormatQuestionDecimal0_025() {
        this.parseFormatAndCheck(
                "?.",
                0.025,
                " !"
        );
    }

    // fraction zero. zero dot............................................................................................

    @Test
    public void testFormatZeroDecimal0_0() {
        this.parseFormatAndCheck(
                "0.",
                0.0,
                "0!"
        );
    }

    @Test
    public void testFormatZeroDecimal0_2() {
        this.parseFormatAndCheck(
                "0.",
                0.2,
                "0!"
        );
    }

    @Test
    public void testFormatZeroDecimal0_7() {
        this.parseFormatAndCheck(
                "0.",
                0.7,
                "1!"
        );
    }

    @Test
    public void testFormatZeroDecimal1_4() {
        this.parseFormatAndCheck(
                "0.",
                1.4,
                "1!"
        );
    }

    @Test
    public void testFormatZeroDecimal1_7() {
        this.parseFormatAndCheck(
                "0.",
                1.7,
                "2!"
        );
    }

    @Test
    public void testFormatZeroDecimal0_025() {
        this.parseFormatAndCheck(
                "0.",
                0.025,
                "0!"
        );
    }

    @Test
    public void testFormatZeroDecimal0_075() {
        this.parseFormatAndCheck(
                "0.",
                0.075,
                "0!"
        );
    }

    // fraction space. space dot space ...................................................................................

    @Test
    public void testFormatHashDecimalHash0_0() {
        this.parseFormatAndCheck(
                "#.#",
                0.0,
                "!"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_2() {
        this.parseFormatAndCheck(
                "#.#",
                0.2,
                "!2"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_6() {
        this.parseFormatAndCheck(
                "#.#",
                0.6,
                "!6"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_01() {
        this.parseFormatAndCheck(
                "#.#",
                0.01,
                "!"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_71() {
        this.parseFormatAndCheck(
                "#.#",
                0.71,
                "!7"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_85() {
        this.parseFormatAndCheck(
                "#.#",
                0.85,
                "!9"
        );
    }

    @Test
    public void testFormatHashDecimalHash1_0() {
        this.parseFormatAndCheck(
                "#.#",
                1,
                "1!"
        );
    }

    @Test
    public void testFormatHashDecimalHash1_99() {
        this.parseFormatAndCheck(
                "#.#",
                1.99,
                "2!"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_025() {
        this.parseFormatAndCheck(
                "#.#",
                0.025,
                "!"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_075() {
        this.parseFormatAndCheck(
                "#.#",
                0.075,
                "!1"
        );
    }

    @Test
    public void testFormatHashDecimalHash0_0001() {
        this.parseFormatAndCheck(
                "#.#",
                0.0001,
                "!"
        );
    }

    @Test
    public void testFormatHashDecimalHashHash0_01() {
        this.parseFormatAndCheck(
                "#.##",
                0.01,
                "!01"
        );
    }

    @Test
    public void testFormatHashDecimalHashHash0_012() {
        this.parseFormatAndCheck(
                "#.##",
                0.012,
                "!01"
        );
    }

    @Test
    public void testFormatHashDecimalHashHash0_0123() {
        this.parseFormatAndCheck(
                "#.##",
                0.0123,
                "!01"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashHash0_001() {
        this.parseFormatAndCheck(
                "#.###",
                0.001,
                "!001"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashHash0_0012() {
        this.parseFormatAndCheck(
                "#.###",
                0.0012,
                "!001"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashHash0_00123() {
        this.parseFormatAndCheck(
                "#.###",
                0.00123,
                "!001"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashHash0_001234() {
        this.parseFormatAndCheck(
                "#.###",
                0.001234,
                "!001"
        );
    }

    // fraction space. space dot space ...................................................................................

    @Test
    public void testFormatQuestionDecimalQuestion0_0() {
        this.parseFormatAndCheck(
                "?.?",
                0.0,
                " ! "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_2() {
        this.parseFormatAndCheck(
                "?.?",
                0.2,
                " !2"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_6() {
        this.parseFormatAndCheck(
                "?.?",
                0.6,
                " !6"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_71() {
        this.parseFormatAndCheck(
                "?.?",
                0.71,
                " !7"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_85() {
        this.parseFormatAndCheck(
                "?.?",
                0.85,
                " !9"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion1_0() {
        this.parseFormatAndCheck(
                "?.?",
                1,
                "1! "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion1_99() {
        this.parseFormatAndCheck(
                "?.?",
                1.99,
                "2! "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_025() {
        this.parseFormatAndCheck(
                "?.?",
                0.025,
                " ! "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_075() {
        this.parseFormatAndCheck(
                "?.?",
                0.075,
                " !1"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestion0_0001() {
        this.parseFormatAndCheck(
                "?.?",
                0.0001,
                " ! "
        );
    }

    // fraction zero. zero dot zero ...................................................................................

    @Test
    public void testFormatZeroDecimalZero0_0() {
        this.parseFormatAndCheck(
                "0.0",
                0.0,
                "0!0"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_2() {
        this.parseFormatAndCheck(
                "0.0",
                0.2,
                "0!2"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_6() {
        this.parseFormatAndCheck(
                "0.0",
                0.6,
                "0!6"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_71() {
        this.parseFormatAndCheck(
                "0.0",
                0.71,
                "0!7"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_85() {
        this.parseFormatAndCheck(
                "0.0",
                0.85,
                "0!9"
        );
    }

    @Test
    public void testFormatZeroDecimalZero1_0() {
        this.parseFormatAndCheck(
                "0.0",
                1,
                "1!0"
        );
    }

    @Test
    public void testFormatZeroDecimalZero1_25() {
        this.parseFormatAndCheck(
                "0.0",
                1.25,
                "1!3"
        );
    }

    @Test
    public void testFormatZeroDecimalZero1_975() {
        this.parseFormatAndCheck(
                "0.0",
                1.975,
                "2!0"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_025() {
        this.parseFormatAndCheck(
                "0.0",
                0.025,
                "0!0"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_075() {
        this.parseFormatAndCheck(
                "0.0",
                0.075,
                "0!1"
        );
    }

    @Test
    public void testFormatZeroDecimalZero0_0005() {
        this.parseFormatAndCheck(
                "0.0",
                0.0005,
                "0!0"
        );
    }

    // long fraction patterns

    @Test
    public void testFormatHashDecimalHashHashHash0_0000005() {
        this.parseFormatAndCheck(
                "#.###",
                0.0000005,
                "!"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionQuestion0_0000005() {
        this.parseFormatAndCheck(
                "?.???",
                0.0000005,
                " !   "
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroZero0_0000005() {
        this.parseFormatAndCheck(
                "0.000",
                0.0000005,
                "0!000"
        );
    }

    // hash space zero.................................................................................

    @Test
    public void testFormatHashDecimalSpaceHash1_05() {
        this.parseFormatAndCheck(
                "?.?#",
                1.05,
                "1!05"
        );
    }

    @Test
    public void testFormatHashDecimalZeroHash1_05() {
        this.parseFormatAndCheck(
                "?.?0",
                1.05,
                "1!05"
        );
    }

    @Test
    public void testFormatHashDecimalSpaceZeroHash1_00005() {
        this.parseFormatAndCheck(
                "?.?",
                1.005,
                "1! "
        );
    }

    //exponent .......................................................................................................

    // zero exponent minus.......................................................................................

    @Test
    public void testFormatZeroExponentZero0() {
        this.parseFormatAndCheck(
                "0E-0",
                0,
                "0E0"
        );
    }

    @Test
    public void testFormatZeroExponentZero1() {
        this.parseFormatAndCheck(
                "0E-0",
                1,
                "1E0"
        );
    }

    @Test
    public void testFormatZeroExponentZero12() {
        this.parseFormatAndCheck(
                "0E-0",
                12,
                "1E1"
        );
    }

    @Test
    public void testFormatZeroExponentZero90() {
        this.parseFormatAndCheck(
                "0E-0",
                90,
                "9E1"
        );
    }

    @Test
    public void testFormatZeroExponentZero123() {
        this.parseFormatAndCheck(
                "0E-0",
                123,
                "1E2"
        );
    }

    @Test
    public void testFormatZeroExponentZero123456789() {
        this.parseFormatNumberAndCheck(
                "0E-0",
                "12345678901",
                "1E10"
        );
    }

    @Test
    public void testFormatZeroExponentZero0_0123() {
        this.parseFormatAndCheck(
                "0E-0",
                0.0123,
                "1EN2"
        );
    }

    // hash exponent minus.......................................................................................

    @Test
    public void testFormatHashExponentHash0() {
        this.parseFormatAndCheck(
                "#E-#",
                0,
                "E"
        );
    }

    @Test
    public void testFormatHashExponentHash1() {
        this.parseFormatAndCheck(
                "#E-#",
                1,
                "1E"
        );
    }

    @Test
    public void testFormatHashExponentHash12() {
        this.parseFormatAndCheck(
                "#E-#",
                12,
                "1E1"
        );
    }

    @Test
    public void testFormatHashExponentHash19() {
        this.parseFormatAndCheck(
                "#E-#",
                19,
                "2E1"
        );
    }

    @Test
    public void testFormatHashExponentHash90() {
        this.parseFormatAndCheck(
                "#E-#",
                90,
                "9E1"
        );
    }

    @Test
    public void testFormatHashExponentHash123() {
        this.parseFormatAndCheck(
                "#E-#",
                123,
                "1E2"
        );
    }

    @Test
    public void testFormatHashExponentHash1234567890() {
        this.parseFormatNumberAndCheck(
                "#E-#",
                "12345678901",
                "1E10"
        );
    }

    @Test
    public void testFormatHashExponentHash0_0123() {
        this.parseFormatAndCheck(
                "#E-#",
                0.0123,
                "1EN2"
        );
    }

    // question exponent minus.......................................................................................

    @Test
    public void testFormatQuestionExponentQuestion0() {
        this.parseFormatAndCheck(
                "?E-?",
                0,
                " E "
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion1() {
        this.parseFormatAndCheck(
                "?E-?",
                1,
                "1E "
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion12() {
        this.parseFormatAndCheck(
                "?E-?",
                12,
                "1E1"
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion19() {
        this.parseFormatAndCheck(
                "?E-?",
                19,
                "2E1"
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion90() {
        this.parseFormatAndCheck(
                "?E-?",
                90,
                "9E1"
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion123() {
        this.parseFormatAndCheck(
                "?E-?",
                123,
                "1E2"
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion1234567890() {
        this.parseFormatNumberAndCheck(
                "?E-?",
                "12345678901",
                "1E10"
        );
    }

    @Test
    public void testFormatQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck(
                "?E-?",
                0.0123,
                "1EN2"
        );
    }

    // zero decimal Exponent.......................................................................................

    @Test
    public void testFormatZeroDecimalExponentZero0() {
        this.parseFormatAndCheck(
                "0.E-0",
                0,
                "0!E0"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero1() {
        this.parseFormatAndCheck(
                "0.E-0",
                1,
                "1!E0"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero12() {
        this.parseFormatAndCheck(
                "0.E-0",
                12,
                "1!E1"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero19() {
        this.parseFormatAndCheck(
                "0.E-0",
                19,
                "2!E1"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero90() {
        this.parseFormatAndCheck(
                "0.E-0",
                90,
                "9!E1"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero123() {
        this.parseFormatAndCheck(
                "0.E-0",
                123,
                "1!E2"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero1234567890() {
        this.parseFormatNumberAndCheck(
                "0.E-0",
                "12345678901",
                "1!E10"
        );
    }

    @Test
    public void testFormatZeroDecimalExponentZero0_0123() {
        this.parseFormatAndCheck(
                "0.E-0",
                0.0123,
                "1!EN2"
        );
    }

    // hash decimal Exponent.......................................................................................

    @Test
    public void testFormatHashDecimalExponentHash0() {
        this.parseFormatAndCheck(
                "#.E-#",
                0,
                "!E"
        );
    }

    @Test
    public void testFormatHashDecimalExponentHash1() {
        this.parseFormatAndCheck(
                "#.E-#",
                1,
                "1!E"
        );
    }

    @Test
    public void testFormatHashDecimalExponentHash12() {
        this.parseFormatAndCheck(
                "#.E-#",
                12,
                "1!E1"
        );
    }

    @Test
    public void testFormatHashDecimalExponentHash19() {
        this.parseFormatAndCheck(
                "#.E-#",
                19,
                "2!E1"
        );
    }

    @Test
    public void testFormatHashDecimalExponentHash129() {
        this.parseFormatAndCheck(
                "#.E-#",
                129,
                "1!E2"
        );
    }

    @Test
    public void testFormatHashDecimalExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
                "#.E-#",
                "12345678901",
                "1!E10"
        );
    }

    @Test
    public void testFormatHashDecimalExponentHash0_0123() {
        this.parseFormatAndCheck(
                "#.E-#",
                0.0123,
                "1!EN2"
        );
    }

    // question decimal Exponent.......................................................................................

    @Test
    public void testFormatQuestionDecimalExponentQuestion0() {
        this.parseFormatAndCheck(
                "?.E-?",
                0,
                " !E "
        );
    }

    @Test
    public void testFormatQuestionDecimalExponentQuestion1() {
        this.parseFormatAndCheck(
                "?.E-?",
                1,
                "1!E "
        );
    }

    @Test
    public void testFormatQuestionDecimalExponentQuestion12() {
        this.parseFormatAndCheck(
                "?.E-?",
                12,
                "1!E1"
        );
    }

    @Test
    public void testFormatQuestionDecimalExponentQuestion19() {
        this.parseFormatAndCheck(
                "?.E-?",
                19,
                "2!E1"
        );
    }

    @Test
    public void testFormatQuestionDecimalExponentQuestion123() {
        this.parseFormatAndCheck(
                "?.E-?",
                123,
                "1!E2"
        );
    }

    @Test
    public void testFormatQuestionDecimalExponentQuestion12345678901() {
        this.parseFormatNumberAndCheck(
                "?.E-?",
                "12345678901",
                "1!E10"
        );
    }

    @Test
    public void testFormatQuestionDecimalExponentQuestion0_0123() {
        this.parseFormatAndCheck(
                "?.E-?",
                0.0123,
                "1!EN2"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero0() {
        this.parseFormatAndCheck(
                "0.0E-0",
                0,
                "0!0E0"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero1() {
        this.parseFormatAndCheck(
                "0.0E-0",
                1,
                "1!0E0"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero12() {
        this.parseFormatAndCheck(
                "0.0E-0",
                12,
                "1!2E1"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero123() {
        this.parseFormatAndCheck(
                "0.0E-0",
                123,
                "1!2E2"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero129() {
        this.parseFormatAndCheck(
                "0.0E-0",
                129,
                "1!3E2"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero12345678901() {
        this.parseFormatNumberAndCheck(
                "0.0E-0",
                "12345678901",
                "1!2E10"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroExponentZero0_0123() {
        this.parseFormatAndCheck(
                "0.E-0",
                0.0123,
                "1!EN2"
        );
    }

    // hash decimal hash Exponent.......................................................................................

    @Test
    public void testFormatHashDecimalHashExponentHash0() {
        this.parseFormatAndCheck(
                "#.#E-#",
                0,
                "!E"
        );
    }

    @Test
    public void testFormatHashDecimalHashExponentHash1() {
        this.parseFormatAndCheck(
                "#.#E-#",
                1,
                "1!E"
        );
    }

    @Test
    public void testFormatHashDecimalHashExponentHash12() {
        this.parseFormatAndCheck(
                "#.#E-#",
                12,
                "1!2E1"
        );
    }

    @Test
    public void testFormatHashDecimalHashExponentHash123() {
        this.parseFormatAndCheck(
                "#.#E-#",
                123,
                "1!2E2"
        );
    }

    @Test
    public void testFormatHashDecimalHashExponentHash129() {
        this.parseFormatAndCheck(
                "#.#E-#",
                129,
                "1!3E2"
        );
    }

    @Test
    public void testFormatHashDecimalHashExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
                "#.#E-#",
                "12345678901",
                "1!2E10"
        );
    }

    @Test
    public void testFormatHashDecimalHashExponentHash0_0123() {
        this.parseFormatAndCheck(
                "#.#E-#",
                0.0123,
                "1!2EN2"
        );
    }

    // question decimal question Exponent.......................................................................................

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion0() {
        this.parseFormatAndCheck(
                "?.?E-?",
                0,
                " ! E "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion1() {
        this.parseFormatAndCheck(
                "?.?E-?",
                1,
                "1! E "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion12() {
        this.parseFormatAndCheck(
                "?.?E-?",
                12,
                "1!2E1"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion122() {
        this.parseFormatAndCheck(
                "?.?E-?",
                122,
                "1!2E2"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion129() {
        this.parseFormatAndCheck(
                "?.?E-?",
                129,
                "1!3E2"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion12345678901() {
        this.parseFormatNumberAndCheck(
                "?.?E-?",
                "12345678901",
                "1!2E10"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck(
                "?.?E-?",
                0.0123,
                "1!2EN2"
        );
    }

    // zero decimal zero Exponent plus.......................................................................................

    @Test
    public void testFormatZeroDecimalZeroZeroExponentZero0() {
        this.parseFormatAndCheck(
                "0.00E-0",
                0,
                "0!00E0"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroExponentZero1() {
        this.parseFormatAndCheck(
                "0.00E-0",
                1,
                "1!00E0"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroExponentZero12() {
        this.parseFormatAndCheck(
                "0.00E-0",
                12,
                "1!20E1"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroExponentZero123_1() {
        this.parseFormatAndCheck(
                "0.00E-0",
                123.1,
                "1!23E2"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroExponentZero124_9() {
        this.parseFormatAndCheck(
                "0.00E-0",
                124.9,
                "1!25E2"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
                "0.00E-#",
                "12345678901",
                "1!23E10"
        );
    }

    @Test
    public void testFormatZeroDecimalZeroZeroExponentZero0_0123() {
        this.parseFormatAndCheck(
                "0.00E-0",
                0.0123,
                "1!23EN2"
        );
    }

    // hash decimal hash Exponent.......................................................................................

    @Test
    public void testFormatHashDecimalHashHashExponentHash0() {
        this.parseFormatAndCheck(
                "#.##E-#",
                0,
                "!E"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashExponentHash1() {
        this.parseFormatAndCheck(
                "#.##E-#",
                1,
                "1!E"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashExponentHash12() {
        this.parseFormatAndCheck(
                "#.##E-#",
                12,
                "1!2E1"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashExponentHash122_1() {
        this.parseFormatAndCheck(
                "#.##E-#",
                122.1,
                "1!22E2"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashExponentHash122_9() {
        this.parseFormatAndCheck(
                "#.##E-#",
                122.9,
                "1!23E2"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
                "#.##E-#",
                "12345678901",
                "1!23E10"
        );
    }

    @Test
    public void testFormatHashDecimalHashHashExponentHash0_0123() {
        this.parseFormatAndCheck(
                "#.##E-#",
                0.0123,
                "1!23EN2"
        );
    }

    // question decimal question Exponent.......................................................................................

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion0() {
        this.parseFormatAndCheck(
                "?.??E-?",
                0,
                " !  E "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion1() {
        this.parseFormatAndCheck(
                "?.??E-?",
                1,
                "1!  E "
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion12() {
        this.parseFormatAndCheck(
                "?.??E-?",
                12,
                "1!2 E1"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion122_1() {
        this.parseFormatAndCheck(
                "?.??E-?",
                122.1,
                "1!22E2"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion122_9() {
        this.parseFormatAndCheck(
                "?.??E-?",
                122.9,
                "1!23E2"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion12345678901() {
        this.parseFormatNumberAndCheck(
                "?.??E-?",
                "12345678901",
                "1!23E10"
        );
    }

    @Test
    public void testFormatQuestionDecimalQuestionQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck(
                "?.??E-?",
                0.0123,
                "1!23EN2"
        );
    }

    // exponent plus.

    // currency .........................................................................................

    @Test
    public void testFormatCurrency() {
        this.parseFormatAndCheck(
                "$000",
                100,
                "C100"
        );
    }

    // percentage .........................................................................................

    @Test
    public void testFormatPercentage0() {
        this.parseFormatAndCheck(
                "0%",
                0,
                "0R"
        );
    }

    @Test
    public void testFormatPercentagePositive1() {
        this.parseFormatAndCheck(
                "0%",
                1,
                "100R"
        );
    }

    @Test
    public void testFormatPercentageNegative1() {
        this.parseFormatAndCheck(
                "0%",
                -1,
                "N100R"
        );
    }

    @Test
    public void testFormatPercentagePositive0_01() {
        this.parseFormatAndCheck(
                "0%",
                0.01,
                "1R"
        );
    }

    @Test
    public void testFormatPercentagePositive0_001() {
        this.parseFormatAndCheck(
                "0.#%",
                0.001,
                "0!1R"
        );
    }

    // groupSeparator...................................................................................................

    @Test
    public void testFormatGroupSeparator0() {
        this.parseFormatAndCheck(
                "#,0",
                0,
                "0"
        );
    }

    @Test
    public void testFormatGroupSeparatorPositive1() {
        this.parseFormatAndCheck(
                "#,0",
                1,
                "1"
        );
    }

    @Test
    public void testFormatGroupSeparatorNegative1() {
        this.parseFormatAndCheck(
                "#,0",
                -1,
                "N1"
        );
    }

    @Test
    public void testFormatGroupSeparator12() {
        this.parseFormatAndCheck(
                "#,0",
                12,
                "12"
        );
    }

    @Test
    public void testFormatGroupSeparator123() {
        this.parseFormatAndCheck(
                "#,0",
                123,
                "123"
        );
    }

    @Test
    public void testFormatGroupSeparator1234() {
        this.parseFormatAndCheck(
                "#,0",
                1234,
                "1G234"
        );
    }

    @Test
    public void testFormatGroupSeparator12345() {
        this.parseFormatAndCheck(
                "#,0",
                12345,
                "12G345"
        );
    }

    @Test
    public void testFormatGroupSeparator123456() {
        this.parseFormatAndCheck(
                "#,0",
                123456,
                "123G456"
        );
    }

    @Test
    public void testFormatGroupSeparator1234567() {
        this.parseFormatAndCheck(
                "#,0",
                1234567,
                "1G234G567"
        );
    }

    @Test
    public void testFormatGroupSeparatorGroupSeparator1234567() {
        this.parseFormatAndCheck(
                "#,0",
                1234567,
                "1G234G567"
        );
    }

    @Test
    public void testFormatGroupSeparator0_1() {
        this.parseFormatAndCheck(
                "#,#.#",
                0.1,
                "!1"
        );
    }

    // groupSeparator multiplier .........................................................................................

    @Test
    public void testFormatGroupSeparatorDividerDigitComma0() {
        this.parseFormatAndCheck(
                "0,",
                0,
                "0"
        );
    }

    @Test
    public void testFormatGroupSeparatorDividerCommaDecimal12345() {
        this.parseFormatAndCheck(
                "0,.0#######,",
                12345,
                "12!345"
        );
    }

    @Test
    public void testFormatGroupSeparatorDividerCommaTextLiteralDecimal12345() {
        this.parseFormatAndCheck(
                "0,\"Text\".0#######,",
                12345,
                "12Text!345"
        );
    }

    @Test
    public void testFormatGroupSeparatorDividerCommaCommaDecimal123456789() {
        this.parseFormatAndCheck(
                "0,,.0000##########,",
                123456789,
                "123!456789"
        );
    }

    // misc tests...........................................................................................

    @Test
    public void testFormatFractionDecimal() {
        this.parseFormatAndCheck(
                "#.#.",
                1.5,
                "1!5!"
        );
    }

    @Test
    public void testFormatExponentDecimalFails() {
        assertThrows(ParserReporterException.class, () -> this.createFormatter("#E#."));
    }

    @Test
    public void testFormatExponentExponentFails() {
        assertThrows(ParserReporterException.class, () -> this.createFormatter("#E0E0"));
    }

    //Number............................................................................................................

    @Test
    public void testFormatBigInteger() {
        this.formatIntegerAndCheck(BigInteger.valueOf(123));
    }

    @Test
    public void testFormatByte() {
        this.formatIntegerAndCheck((byte) 123);
    }

    @Test
    public void testFormatShort() {
        this.formatIntegerAndCheck((short) 123);
    }

    @Test
    public void testFormatInteger() {
        this.formatIntegerAndCheck(123);
    }

    @Test
    public void testFormatLong() {
        this.formatIntegerAndCheck(123L);
    }

    private void formatIntegerAndCheck(final Number number) {
        this.formatAndCheck(
                this.createFormatter("\"before\" 0000 \"after\""),
                number,
                "before 0123 after"
        );
    }

    @Test
    public void testFormatBigDecimal() {
        this.formatDecimalAndCheck(BigDecimal.valueOf(123.5));
    }

    @Test
    public void testFormatDouble() {
        this.formatDecimalAndCheck(123.5);
    }

    @Test
    public void testFormatFloat() {
        this.formatDecimalAndCheck(123.5);
    }

    private void formatDecimalAndCheck(final Number number) {
        this.formatAndCheck(
                this.createFormatter("\"before\" #.0000 \"after\""),
                number,
                "before 123!5000 after"
        );
    }

    //rounding..........................................................................................................

    @Test
    public void testFormatRoundingHalfUp() {
        this.parseFormatNumberAndCheck(
                "#",
                "1.5",
                RoundingMode.HALF_UP,
                "2"
        );
    }

    @Test
    public void testFormatRoundingDown() {
        this.parseFormatNumberAndCheck(
                "#",
                "1.5",
                RoundingMode.DOWN,
                "1"
        );
    }

    @Test
    public void testFormatIncludesColorName() {
        this.parseFormatNumberAndCheck(
                "[RED]#",
                "3",
                SpreadsheetText.with("3")
                        .setColor(
                                Optional.of(
                                        RED
                                )
                        )
        );
    }

    @Test
    public void testFormatIncludesColorNumber() {
        this.parseFormatNumberAndCheck(
                "[color44]#",
                "4",
                SpreadsheetText.with("4")
                        .setColor(
                                Optional.of(
                                        RED
                                )
                        )
        );
    }

    //toString .........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    //helpers ..........................................................................................................

    private void parseFormatAndCheck(final String pattern,
                                     final double value,
                                     final String text) {
        this.parseFormatNumberAndCheck(
                pattern,
                String.valueOf(value),
                text
        );
    }

    private void parseFormatNumberAndCheck(final String pattern,
                                           final String value,
                                           final String text) {
        this.parseFormatNumberAndCheck(
                pattern,
                value,
                RoundingMode.HALF_UP,
                text
        );
    }

    private void parseFormatNumberAndCheck(final String pattern,
                                           final String value,
                                           final RoundingMode roundingMode,
                                           final String text) {
        this.parseFormatNumberAndCheck(
                pattern,
                value,
                this.createContext(roundingMode),
                SpreadsheetText.with(text)
        );
    }

    private void parseFormatNumberAndCheck(final String pattern,
                                           final String value,
                                           final SpreadsheetText text) {
        this.parseFormatNumberAndCheck(
                pattern,
                value,
                this.createContext(),
                text
        );
    }

    private void parseFormatNumberAndCheck(final String pattern,
                                           final String value,
                                           final SpreadsheetFormatterContext context,
                                           final SpreadsheetText text) {
        final SpreadsheetPatternSpreadsheetFormatterNumber formatter = this.createFormatter(pattern);

        this.formatNumberAndCheck(
                formatter,
                value,
                ExpressionNumberKind.BIG_DECIMAL,
                context,
                text
        );

        BigInteger bigInteger;
        try {
            bigInteger = new BigInteger(value);
        } catch (final NumberFormatException ignore) {
            bigInteger = null;
        }
        if (null != bigInteger) {
            this.formatAndCheck(formatter, bigInteger, text);
        }

        this.formatNumberAndCheck(
                formatter,
                value,
                ExpressionNumberKind.DOUBLE,
                context,
                text
        );

        Long longValue;
        try {
            longValue = Long.parseLong(value);
        } catch (final NumberFormatException ignore) {
            longValue = null;
        }
        if (null != longValue) {
            this.formatAndCheck(formatter, longValue, text);
        }
    }

    private void formatNumberAndCheck(final SpreadsheetPatternSpreadsheetFormatterNumber formatter,
                                      final String value,
                                      final ExpressionNumberKind kind,
                                      final SpreadsheetFormatterContext context,
                                      final SpreadsheetText text) {
        this.formatAndCheck(
                formatter,
                kind.create(new BigDecimal(value)),
                context,
                text
        );

        this.formatAndCheck(
                formatter,
                kind.create(Double.parseDouble(value)),
                context,
                text
        );
    }

    @Override
    String pattern() {
        return "\"quoted text\"";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.numberFormat()
                .transform((v, c) -> v.cast(SequenceParserToken.class).value().get(0));
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterNumber createFormatter0(final SpreadsheetFormatNumberParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterNumber.with(token);
    }

    @Override
    public BigDecimal value() {
        return new BigDecimal(123);
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return this.createContext(RoundingMode.HALF_UP);
    }

    private SpreadsheetFormatterContext createContext(final RoundingMode roundingMode) {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                try {
                    this.convert(value, target);
                    return true;
                } catch (final Exception failed) {
                    return false;
                }
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return ExpressionNumberConverters.toNumberOrExpressionNumber(
                        Converters.numberToNumber()
                ).convert(
                        value,
                        target,
                        new FakeExpressionNumberConverterContext() {
                            @Override
                            public ExpressionNumberKind expressionNumberKind() {
                                return ExpressionNumberKind.BIG_DECIMAL;
                            }
                        }
                );
            }

            @Override
            public String currencySymbol() {
                return "C";
            }

            @Override
            public char decimalSeparator() {
                return '!';
            }

            @Override
            public String exponentSymbol() {
                return "E";
            }

            @Override
            public char groupSeparator() {
                return 'G';
            }

            @Override
            public MathContext mathContext() {
                return new MathContext(32, roundingMode);
            }

            @Override
            public char negativeSign() {
                return 'N';
            }

            @Override
            public char percentageSymbol() {
                return 'R';
            }

            @Override
            public char positiveSign() {
                return 'P';
            }

            @Override
            public Optional<Color> colorName(final SpreadsheetColorName name) {
                checkEquals(
                        SpreadsheetColorName.with("red"),
                        name,
                        "colorName"
                );
                return Optional.of(
                        RED
                );
            }

            @Override
            public Optional<Color> colorNumber(final int number) {
                checkEquals(
                        44,
                        number,
                        "colorNumber"
                );
                return Optional.of(
                        RED
                );
            }
        };
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterNumber> type() {
        return SpreadsheetPatternSpreadsheetFormatterNumber.class;
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentToken() {
        this.checkNotEquals(
                this.createFormatter("$0.00"),
                this.createFormatter("#.##")
        );
    }
}
