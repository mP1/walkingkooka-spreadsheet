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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporterException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * In expectations all symbols are changed from defaults to characters to verify the context is supplying such symbols.
 */
public final class NumberSpreadsheetFormatterTest extends SpreadsheetFormatter3TestCase<NumberSpreadsheetFormatter,
        SpreadsheetFormatNumberParserToken> {

    // text-literal, escaped etc........................................................................................

    @Test
    public void testCloseParens() {
        this.parseFormatAndCheck2(")");
    }

    @Test
    public void testColon() {
        this.parseFormatAndCheck2(":");
    }

    @Test
    public void testMinus() {
        this.parseFormatAndCheck2("-");
    }

    @Test
    public void testPlus() {
        this.parseFormatAndCheck2("+");
    }

    @Test
    public void testOpenParens() {
        this.parseFormatAndCheck2("(");
    }

    private void parseFormatAndCheck2(final String pattern) {
        this.parseFormatAndCheck2(pattern, pattern);
    }

    @Test
    public void testEscaped() {
        this.parseFormatAndCheck2("\\A", "A");
    }

    @Test
    public void testTextLiteral() {
        this.parseFormatAndCheck2("\"Quoted text\"", "Quoted text");
    }

    private void parseFormatAndCheck2(final String pattern, final String text) {
        this.parseFormatAndCheck(pattern, "-999999", text);
    }

    // integers ......................................................................................................

    @Test
    public void testHash0() {
        this.parseFormatAndCheck("#", 0, "");
    }

    @Test
    public void testHash1() {
        this.parseFormatAndCheck("#", 1, "1");
    }

    @Test
    public void testHash12() {
        this.parseFormatAndCheck("#", 12, "12");
    }

    @Test
    public void testHash123() {
        this.parseFormatAndCheck("#", 123, "123");
    }

    @Test
    public void testHash1000() {
        this.parseFormatAndCheck("#", 1000, "1000");
    }

    @Test
    public void testHash1234() {
        this.parseFormatAndCheck("#", 1234, "1234");
    }

    @Test
    public void testHash0_025() {
        this.parseFormatAndCheck("#", 0.025, "");
    }

    @Test
    public void testQuestion0() {
        this.parseFormatAndCheck("?", 0, " ");
    }

    @Test
    public void testQuestion1() {
        this.parseFormatAndCheck("?", 1, "1");
    }

    @Test
    public void testQuestion12() {
        this.parseFormatAndCheck("?", 12, "12");
    }

    @Test
    public void testQuestion123() {
        this.parseFormatAndCheck("?", 123, "123");
    }

    @Test
    public void testQuestion1234() {
        this.parseFormatAndCheck("?", 1234, "1234");
    }

    @Test
    public void testQuestion0_025() {
        this.parseFormatAndCheck("?", 0.025, " ");
    }

    @Test
    public void testZero0() {
        this.parseFormatAndCheck("0", 0, "0");
    }

    @Test
    public void testZero1() {
        this.parseFormatAndCheck("0", 1, "1");
    }

    @Test
    public void testZero12() {
        this.parseFormatAndCheck("0", 12, "12");
    }

    @Test
    public void testZero123() {
        this.parseFormatAndCheck("0", 123, "123");
    }

    @Test
    public void testZero1000() {
        this.parseFormatAndCheck("0", 1000, "1000");
    }

    @Test
    public void testZero1234() {
        this.parseFormatAndCheck("0", 1234, "1234");
    }

    @Test
    public void testZero0_025() {
        this.parseFormatAndCheck("0", 0.025, "0");
    }

    // pattern longer than digits

    @Test
    public void testHashHashHashHashHash0() {
        this.parseFormatAndCheck("#####", 0, "");
    }

    @Test
    public void testHashHashHashHashHash1() {
        this.parseFormatAndCheck("#####", 1, "1");
    }

    @Test
    public void testHashHashHashHashHash12() {
        this.parseFormatAndCheck("#####", 12, "12");
    }

    @Test
    public void testHashHashHashHashHash123() {
        this.parseFormatAndCheck("#####", 123, "123");
    }

    @Test
    public void testHashHashHashHashHash1234() {
        this.parseFormatAndCheck("#####", 1234, "1234");
    }

    @Test
    public void testQuestionQuestionQuestionQuestionQuestion0() {
        this.parseFormatAndCheck("?????", 0, "     ");
    }

    @Test
    public void testQuestionQuestionQuestionQuestionQuestion1() {
        this.parseFormatAndCheck("?????", 1, "    1");
    }

    @Test
    public void testQuestionQuestionQuestionQuestionQuestion12() {
        this.parseFormatAndCheck("?????", 12, "   12");
    }

    @Test
    public void testQuestionQuestionQuestionQuestionQuestion123() {
        this.parseFormatAndCheck("?????", 123, "  123");
    }

    @Test
    public void testQuestionQuestionQuestionQuestionQuestion1234() {
        this.parseFormatAndCheck("?????", 1234, " 1234");
    }

    @Test
    public void testZeroZeroZeroZeroZero0() {
        this.parseFormatAndCheck("00000", 0, "00000");
    }

    @Test
    public void testZeroZeroZeroZeroZero1() {
        this.parseFormatAndCheck("00000", 1, "00001");
    }

    @Test
    public void testZeroZeroZeroZeroZero12() {
        this.parseFormatAndCheck("00000", 12, "00012");
    }

    @Test
    public void testZeroZeroZeroZeroZero123() {
        this.parseFormatAndCheck("00000", 123, "00123");
    }

    @Test
    public void testZeroZeroZeroZeroZero1234() {
        this.parseFormatAndCheck("00000", 1234, "01234");
    }

    // hash + zero + question

    @Test
    public void testHashHashZeroZeroQuestionQuestionHash5() {
        this.parseFormatAndCheck("##00??#", 5, "00  5");
    }

    // negative.........................................................................................

    @Test
    public void testHashHashZeroZeroQuestionQuestionHashNegative5() {
        this.parseFormatAndCheck("##00??#", -5, "N00  5");
    }

    // fraction.......................................................................................................

    @Test
    public void testDecimal0_0() {
        this.parseFormatAndCheck(".", 0, "!");
    }

    @Test
    public void testDecimal0_4() {
        this.parseFormatAndCheck(".", 0.4, "!");
    }

    @Test
    public void testDecimal0_75() {
        this.parseFormatAndCheck(".", 0.75, "!");
    }

    // fraction hash decimal hash dot............................................................................................

    @Test
    public void testHashDecimal0_0() {
        this.parseFormatAndCheck("#.", 0.0, "!");
    }

    @Test
    public void testHashDecimal0_2() {
        this.parseFormatAndCheck("#.", 0.2, "!");
    }

    @Test
    public void testHashDecimal0_7() {
        this.parseFormatAndCheck("#.", 0.7, "1!");
    }

    @Test
    public void testHashDecimal1_4() {
        this.parseFormatAndCheck("#.", 1.4, "1!");
    }

    @Test
    public void testHashDecimal1_7() {
        this.parseFormatAndCheck("#.", 1.7, "2!");
    }

    @Test
    public void testHashDecimal0_025() {
        this.parseFormatAndCheck("#.", 0.025, "!");
    }

    // fraction space. space dot............................................................................................

    @Test
    public void testQuestionDecimal0_0() {
        this.parseFormatAndCheck("?.", 0.0, " !");
    }

    @Test
    public void testQuestionDecimal0_2() {
        this.parseFormatAndCheck("?.", 0.2, " !");
    }

    @Test
    public void testQuestionDecimal0_7() {
        this.parseFormatAndCheck("?.", 0.7, "1!");
    }

    @Test
    public void testQuestionDecimal1_4() {
        this.parseFormatAndCheck("?.", 1.4, "1!");
    }

    @Test
    public void testQuestionDecimal1_7() {
        this.parseFormatAndCheck("?.", 1.7, "2!");
    }

    @Test
    public void testQuestionDecimal0_025() {
        this.parseFormatAndCheck("?.", 0.025, " !");
    }

    // fraction zero. zero dot............................................................................................

    @Test
    public void testZeroDecimal0_0() {
        this.parseFormatAndCheck("0.", 0.0, "0!");
    }

    @Test
    public void testZeroDecimal0_2() {
        this.parseFormatAndCheck("0.", 0.2, "0!");
    }

    @Test
    public void testZeroDecimal0_7() {
        this.parseFormatAndCheck("0.", 0.7, "1!");
    }

    @Test
    public void testZeroDecimal1_4() {
        this.parseFormatAndCheck("0.", 1.4, "1!");
    }

    @Test
    public void testZeroDecimal1_7() {
        this.parseFormatAndCheck("0.", 1.7, "2!");
    }

    @Test
    public void testZeroDecimal0_025() {
        this.parseFormatAndCheck("0.", 0.025, "0!");
    }

    @Test
    public void testZeroDecimal0_075() {
        this.parseFormatAndCheck("0.", 0.075, "0!");
    }

    // fraction space. space dot space ...................................................................................

    @Test
    public void testHashDecimalHash0_0() {
        this.parseFormatAndCheck("#.#", 0.0, "!");
    }

    @Test
    public void testHashDecimalHash0_2() {
        this.parseFormatAndCheck("#.#", 0.2, "!2");
    }

    @Test
    public void testHashDecimalHash0_6() {
        this.parseFormatAndCheck("#.#", 0.6, "!6");
    }

    @Test
    public void testHashDecimalHash0_71() {
        this.parseFormatAndCheck("#.#", 0.71, "!7");
    }

    @Test
    public void testHashDecimalHash0_85() {
        this.parseFormatAndCheck("#.#", 0.85, "!9");
    }

    @Test
    public void testHashDecimalHash1_0() {
        this.parseFormatAndCheck("#.#", 1, "1!");
    }

    @Test
    public void testHashDecimalHash1_99() {
        this.parseFormatAndCheck("#.#", 1.99, "2!");
    }

    @Test
    public void testHashDecimalHash0_025() {
        this.parseFormatAndCheck("#.#", 0.025, "!");
    }

    @Test
    public void testHashDecimalHash0_075() {
        this.parseFormatAndCheck("#.#", 0.075, "!1");
    }

    @Test
    public void testHashDecimalHash0_0001() {
        this.parseFormatAndCheck("#.#", 0.0001, "!");
    }

    // fraction space. space dot space ...................................................................................

    @Test
    public void testQuestionDecimalQuestion0_0() {
        this.parseFormatAndCheck("?.?", 0.0, " ! ");
    }

    @Test
    public void testQuestionDecimalQuestion0_2() {
        this.parseFormatAndCheck("?.?", 0.2, " !2");
    }

    @Test
    public void testQuestionDecimalQuestion0_6() {
        this.parseFormatAndCheck("?.?", 0.6, " !6");
    }

    @Test
    public void testQuestionDecimalQuestion0_71() {
        this.parseFormatAndCheck("?.?", 0.71, " !7");
    }

    @Test
    public void testQuestionDecimalQuestion0_85() {
        this.parseFormatAndCheck("?.?", 0.85, " !9");
    }

    @Test
    public void testQuestionDecimalQuestion1_0() {
        this.parseFormatAndCheck("?.?", 1, "1! ");
    }

    @Test
    public void testQuestionDecimalQuestion1_99() {
        this.parseFormatAndCheck("?.?", 1.99, "2! ");
    }

    @Test
    public void testQuestionDecimalQuestion0_025() {
        this.parseFormatAndCheck("?.?", 0.025, " ! ");
    }

    @Test
    public void testQuestionDecimalQuestion0_075() {
        this.parseFormatAndCheck("?.?", 0.075, " !1");
    }

    @Test
    public void testQuestionDecimalQuestion0_0001() {
        this.parseFormatAndCheck("?.?", 0.0001, " ! ");
    }

    // fraction zero. zero dot zero ...................................................................................

    @Test
    public void testZeroDecimalZero0_0() {
        this.parseFormatAndCheck("0.0", 0.0, "0!0");
    }

    @Test
    public void testZeroDecimalZero0_2() {
        this.parseFormatAndCheck("0.0", 0.2, "0!2");
    }

    @Test
    public void testZeroDecimalZero0_6() {
        this.parseFormatAndCheck("0.0", 0.6, "0!6");
    }

    @Test
    public void testZeroDecimalZero0_71() {
        this.parseFormatAndCheck("0.0", 0.71, "0!7");
    }

    @Test
    public void testZeroDecimalZero0_85() {
        this.parseFormatAndCheck("0.0", 0.85, "0!9");
    }

    @Test
    public void testZeroDecimalZero1_0() {
        this.parseFormatAndCheck("0.0", 1, "1!0");
    }

    @Test
    public void testZeroDecimalZero1_25() {
        this.parseFormatAndCheck("0.0", 1.25, "1!3");
    }

    @Test
    public void testZeroDecimalZero1_975() {
        this.parseFormatAndCheck("0.0", 1.975, "2!0");
    }

    @Test
    public void testZeroDecimalZero0_025() {
        this.parseFormatAndCheck("0.0", 0.025, "0!0");
    }

    @Test
    public void testZeroDecimalZero0_075() {
        this.parseFormatAndCheck("0.0", 0.075, "0!1");
    }

    @Test
    public void testZeroDecimalZero0_0005() {
        this.parseFormatAndCheck("0.0", 0.0005, "0!0");
    }

    // long fraction patterns

    @Test
    public void testHashDecimalHashHashHash0_0000005() {
        this.parseFormatAndCheck("#.###", 0.0000005, "!");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionQuestion0_0000005() {
        this.parseFormatAndCheck("?.???", 0.0000005, " !   ");
    }

    @Test
    public void testZeroDecimalZeroZeroZero0_0000005() {
        this.parseFormatAndCheck("0.000", 0.0000005, "0!000");
    }

    // hash space zero.................................................................................

    @Test
    public void testHashDecimalSpaceHash1_05() {
        this.parseFormatAndCheck("?.?#", 1.05, "1!05");
    }

    @Test
    public void testHashDecimalZeroHash1_05() {
        this.parseFormatAndCheck("?.?0", 1.05, "1!05");
    }

    @Test
    public void testHashDecimalSpaceZeroHash1_00005() {
        this.parseFormatAndCheck("?.?", 1.005, "1! ");
    }

    //exponent .......................................................................................................

    // zero exponent minus.......................................................................................

    @Test
    public void testZeroExponentZero0() {
        this.parseFormatAndCheck("0E-0", 0, "0E0");
    }

    @Test
    public void testZeroExponentZero1() {
        this.parseFormatAndCheck("0E-0", 1, "1E0");
    }

    @Test
    public void testZeroExponentZero12() {
        this.parseFormatAndCheck("0E-0", 12, "1E1");
    }

    @Test
    public void testZeroExponentZero90() {
        this.parseFormatAndCheck("0E-0", 90, "9E1");
    }

    @Test
    public void testZeroExponentZero123() {
        this.parseFormatAndCheck("0E-0", 123, "1E2");
    }

    @Test
    public void testZeroExponentZero123456789() {
        this.parseFormatAndCheck("0E-0", "12345678901", "1E10");
    }

    @Test
    public void testZeroExponentZero0_0123() {
        this.parseFormatAndCheck("0E-0", 0.0123, "1EN2");
    }

    // hash exponent minus.......................................................................................

    @Test
    public void testHashExponentHash0() {
        this.parseFormatAndCheck("#E-#", 0, "E");
    }

    @Test
    public void testHashExponentHash1() {
        this.parseFormatAndCheck("#E-#", 1, "1E");
    }

    @Test
    public void testHashExponentHash12() {
        this.parseFormatAndCheck("#E-#", 12, "1E1");
    }

    @Test
    public void testHashExponentHash19() {
        this.parseFormatAndCheck("#E-#", 19, "2E1");
    }

    @Test
    public void testHashExponentHash90() {
        this.parseFormatAndCheck("#E-#", 90, "9E1");
    }

    @Test
    public void testHashExponentHash123() {
        this.parseFormatAndCheck("#E-#", 123, "1E2");
    }

    @Test
    public void testHashExponentHash1234567890() {
        this.parseFormatAndCheck("#E-#", "12345678901", "1E10");
    }

    @Test
    public void testHashExponentHash0_0123() {
        this.parseFormatAndCheck("#E-#", 0.0123, "1EN2");
    }

    // question exponent minus.......................................................................................

    @Test
    public void testQuestionExponentQuestion0() {
        this.parseFormatAndCheck("?E-?", 0, " E ");
    }

    @Test
    public void testQuestionExponentQuestion1() {
        this.parseFormatAndCheck("?E-?", 1, "1E ");
    }

    @Test
    public void testQuestionExponentQuestion12() {
        this.parseFormatAndCheck("?E-?", 12, "1E1");
    }

    @Test
    public void testQuestionExponentQuestion19() {
        this.parseFormatAndCheck("?E-?", 19, "2E1");
    }

    @Test
    public void testQuestionExponentQuestion90() {
        this.parseFormatAndCheck("?E-?", 90, "9E1");
    }

    @Test
    public void testQuestionExponentQuestion123() {
        this.parseFormatAndCheck("?E-?", 123, "1E2");
    }

    @Test
    public void testQuestionExponentQuestion1234567890() {
        this.parseFormatAndCheck("?E-?", "12345678901", "1E10");
    }

    @Test
    public void testQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck("?E-?", 0.0123, "1EN2");
    }

    // zero decimal Exponent.......................................................................................

    @Test
    public void testZeroDecimalExponentZero0() {
        this.parseFormatAndCheck("0.E-0", 0, "0!E0");
    }

    @Test
    public void testZeroDecimalExponentZero1() {
        this.parseFormatAndCheck("0.E-0", 1, "1!E0");
    }

    @Test
    public void testZeroDecimalExponentZero12() {
        this.parseFormatAndCheck("0.E-0", 12, "1!E1");
    }

    @Test
    public void testZeroDecimalExponentZero19() {
        this.parseFormatAndCheck("0.E-0", 19, "2!E1");
    }

    @Test
    public void testZeroDecimalExponentZero90() {
        this.parseFormatAndCheck("0.E-0", 90, "9!E1");
    }

    @Test
    public void testZeroDecimalExponentZero123() {
        this.parseFormatAndCheck("0.E-0", 123, "1!E2");
    }

    @Test
    public void testZeroDecimalExponentZero1234567890() {
        this.parseFormatAndCheck("0.E-0", "12345678901", "1!E10");
    }

    @Test
    public void testZeroDecimalExponentZero0_0123() {
        this.parseFormatAndCheck("0.E-0", 0.0123, "1!EN2");
    }

    // hash decimal Exponent.......................................................................................

    @Test
    public void testHashDecimalExponentHash0() {
        this.parseFormatAndCheck("#.E-#", 0, "!E");
    }

    @Test
    public void testHashDecimalExponentHash1() {
        this.parseFormatAndCheck("#.E-#", 1, "1!E");
    }

    @Test
    public void testHashDecimalExponentHash12() {
        this.parseFormatAndCheck("#.E-#", 12, "1!E1");
    }

    @Test
    public void testHashDecimalExponentHash19() {
        this.parseFormatAndCheck("#.E-#", 19, "2!E1");
    }

    @Test
    public void testHashDecimalExponentHash129() {
        this.parseFormatAndCheck("#.E-#", 129, "1!E2");
    }

    @Test
    public void testHashDecimalExponentHash12345678901() {
        this.parseFormatAndCheck("#.E-#", "12345678901", "1!E10");
    }

    @Test
    public void testHashDecimalExponentHash0_0123() {
        this.parseFormatAndCheck("#.E-#", 0.0123, "1!EN2");
    }

    // question decimal Exponent.......................................................................................

    @Test
    public void testQuestionDecimalExponentQuestion0() {
        this.parseFormatAndCheck("?.E-?", 0, " !E ");
    }

    @Test
    public void testQuestionDecimalExponentQuestion1() {
        this.parseFormatAndCheck("?.E-?", 1, "1!E ");
    }

    @Test
    public void testQuestionDecimalExponentQuestion12() {
        this.parseFormatAndCheck("?.E-?", 12, "1!E1");
    }

    @Test
    public void testQuestionDecimalExponentQuestion19() {
        this.parseFormatAndCheck("?.E-?", 19, "2!E1");
    }

    @Test
    public void testQuestionDecimalExponentQuestion123() {
        this.parseFormatAndCheck("?.E-?", 123, "1!E2");
    }

    @Test
    public void testQuestionDecimalExponentQuestion12345678901() {
        this.parseFormatAndCheck("?.E-?", "12345678901", "1!E10");
    }

    @Test
    public void testQuestionDecimalExponentQuestion0_0123() {
        this.parseFormatAndCheck("?.E-?", 0.0123, "1!EN2");
    }

    @Test
    public void testZeroDecimalZeroExponentZero0() {
        this.parseFormatAndCheck("0.0E-0", 0, "0!0E0");
    }

    @Test
    public void testZeroDecimalZeroExponentZero1() {
        this.parseFormatAndCheck("0.0E-0", 1, "1!0E0");
    }

    @Test
    public void testZeroDecimalZeroExponentZero12() {
        this.parseFormatAndCheck("0.0E-0", 12, "1!2E1");
    }

    @Test
    public void testZeroDecimalZeroExponentZero123() {
        this.parseFormatAndCheck("0.0E-0", 123, "1!2E2");
    }

    @Test
    public void testZeroDecimalZeroExponentZero129() {
        this.parseFormatAndCheck("0.0E-0", 129, "1!3E2");
    }

    @Test
    public void testZeroDecimalZeroExponentZero12345678901() {
        this.parseFormatAndCheck("0.0E-0", "12345678901", "1!2E10");
    }

    @Test
    public void testZeroDecimalZeroExponentZero0_0123() {
        this.parseFormatAndCheck("0.E-0", 0.0123, "1!EN2");
    }

    // hash decimal hash Exponent.......................................................................................

    @Test
    public void testHashDecimalHashExponentHash0() {
        this.parseFormatAndCheck("#.#E-#", 0, "!E");
    }

    @Test
    public void testHashDecimalHashExponentHash1() {
        this.parseFormatAndCheck("#.#E-#", 1, "1!E");
    }

    @Test
    public void testHashDecimalHashExponentHash12() {
        this.parseFormatAndCheck("#.#E-#", 12, "1!2E1");
    }

    @Test
    public void testHashDecimalHashExponentHash123() {
        this.parseFormatAndCheck("#.#E-#", 123, "1!2E2");
    }

    @Test
    public void testHashDecimalHashExponentHash129() {
        this.parseFormatAndCheck("#.#E-#", 129, "1!3E2");
    }

    @Test
    public void testHashDecimalHashExponentHash12345678901() {
        this.parseFormatAndCheck("#.#E-#", "12345678901", "1!2E10");
    }

    @Test
    public void testHashDecimalHashExponentHash0_0123() {
        this.parseFormatAndCheck("#.#E-#", 0.0123, "1!2EN2");
    }

    // question decimal question Exponent.......................................................................................

    @Test
    public void testQuestionDecimalQuestionExponentQuestion0() {
        this.parseFormatAndCheck("?.?E-?", 0, " ! E ");
    }

    @Test
    public void testQuestionDecimalQuestionExponentQuestion1() {
        this.parseFormatAndCheck("?.?E-?", 1, "1! E ");
    }

    @Test
    public void testQuestionDecimalQuestionExponentQuestion12() {
        this.parseFormatAndCheck("?.?E-?", 12, "1!2E1");
    }

    @Test
    public void testQuestionDecimalQuestionExponentQuestion122() {
        this.parseFormatAndCheck("?.?E-?", 122, "1!2E2");
    }

    @Test
    public void testQuestionDecimalQuestionExponentQuestion129() {
        this.parseFormatAndCheck("?.?E-?", 129, "1!3E2");
    }

    @Test
    public void testQuestionDecimalQuestionExponentQuestion12345678901() {
        this.parseFormatAndCheck("?.?E-?", "12345678901", "1!2E10");
    }

    @Test
    public void testQuestionDecimalQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck("?.?E-?", 0.0123, "1!2EN2");
    }

    // zero decimal zero Exponent plus.......................................................................................

    @Test
    public void testZeroDecimalZeroZeroExponentZero0() {
        this.parseFormatAndCheck("0.00E-0", 0, "0!00E0");
    }

    @Test
    public void testZeroDecimalZeroZeroExponentZero1() {
        this.parseFormatAndCheck("0.00E-0", 1, "1!00E0");
    }

    @Test
    public void testZeroDecimalZeroZeroExponentZero12() {
        this.parseFormatAndCheck("0.00E-0", 12, "1!20E1");
    }

    @Test
    public void testZeroDecimalZeroZeroExponentZero123_1() {
        this.parseFormatAndCheck("0.00E-0", 123.1, "1!23E2");
    }

    @Test
    public void testZeroDecimalZeroZeroExponentZero124_9() {
        this.parseFormatAndCheck("0.00E-0", 124.9, "1!25E2");
    }

    @Test
    public void testZeroDecimalZeroZeroExponentHash12345678901() {
        this.parseFormatAndCheck("0.00E-#", "12345678901", "1!23E10");
    }

    @Test
    public void testZeroDecimalZeroZeroExponentZero0_0123() {
        this.parseFormatAndCheck("0.00E-0", 0.0123, "1!23EN2");
    }

    // hash decimal hash Exponent.......................................................................................

    @Test
    public void testHashDecimalHashHashExponentHash0() {
        this.parseFormatAndCheck("#.##E-#", 0, "!E");
    }

    @Test
    public void testHashDecimalHashHashExponentHash1() {
        this.parseFormatAndCheck("#.##E-#", 1, "1!E");
    }

    @Test
    public void testHashDecimalHashHashExponentHash12() {
        this.parseFormatAndCheck("#.##E-#", 12, "1!2E1");
    }

    @Test
    public void testHashDecimalHashHashExponentHash122_1() {
        this.parseFormatAndCheck("#.##E-#", 122.1, "1!22E2");
    }

    @Test
    public void testHashDecimalHashHashExponentHash122_9() {
        this.parseFormatAndCheck("#.##E-#", 122.9, "1!23E2");
    }

    @Test
    public void testHashDecimalHashHashExponentHash12345678901() {
        this.parseFormatAndCheck("#.##E-#", "12345678901", "1!23E10");
    }

    @Test
    public void testHashDecimalHashHashExponentHash0_0123() {
        this.parseFormatAndCheck("#.##E-#", 0.0123, "1!23EN2");
    }

    // question decimal question Exponent.......................................................................................

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion0() {
        this.parseFormatAndCheck("?.??E-?", 0, " !  E ");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion1() {
        this.parseFormatAndCheck("?.??E-?", 1, "1!  E ");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion12() {
        this.parseFormatAndCheck("?.??E-?", 12, "1!2 E1");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion122_1() {
        this.parseFormatAndCheck("?.??E-?", 122.1, "1!22E2");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion122_9() {
        this.parseFormatAndCheck("?.??E-?", 122.9, "1!23E2");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion12345678901() {
        this.parseFormatAndCheck("?.??E-?", "12345678901", "1!23E10");
    }

    @Test
    public void testQuestionDecimalQuestionQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck("?.??E-?", 0.0123, "1!23EN2");
    }

    // exponent plus.

    // currency .........................................................................................

    @Test
    public void testCurrency() {
        this.parseFormatAndCheck("$000", 100, "C100");
    }

    // percentage .........................................................................................

    @Test
    public void testPercentage0() {
        this.parseFormatAndCheck("0%", 0, "0R");
    }

    @Test
    public void testPercentagePositive1() {
        this.parseFormatAndCheck("0%", 1, "100R");
    }

    @Test
    public void testPercentageNegative1() {
        this.parseFormatAndCheck("0%", -1, "N100R");
    }

    @Test
    public void testPercentagePositive0_01() {
        this.parseFormatAndCheck("0%", 0.01, "1R");
    }

    @Test
    public void testPercentagePositive0_001() {
        this.parseFormatAndCheck("0.#%", 0.001, "0!1R");
    }

    // thousands grouping .........................................................................................

    @Test
    public void testThousandsGrouping0() {
        this.parseFormatAndCheck("#,0", 0, "0");
    }

    @Test
    public void testThousandsGroupingPositive1() {
        this.parseFormatAndCheck("#,0", 1, "1");
    }

    @Test
    public void testThousandsGroupingNegative1() {
        this.parseFormatAndCheck("#,0", -1, "N1");
    }

    @Test
    public void testThousandsGrouping12() {
        this.parseFormatAndCheck("#,0", 12, "12");
    }

    @Test
    public void testThousandsGrouping123() {
        this.parseFormatAndCheck("#,0", 123, "123");
    }

    @Test
    public void testThousandsGrouping1234() {
        this.parseFormatAndCheck("#,0", 1234, "1G234");
    }

    @Test
    public void testThousandsGrouping12345() {
        this.parseFormatAndCheck("#,0", 12345, "12G345");
    }

    @Test
    public void testThousandsGrouping123456() {
        this.parseFormatAndCheck("#,0", 123456, "123G456");
    }

    @Test
    public void testThousandsGrouping1234567() {
        this.parseFormatAndCheck("#,0", 1234567, "1G234G567");
    }

    @Test
    public void testThousandsGroupingThousandsGrouping1234567() {
        this.parseFormatAndCheck("#,0", 1234567, "1G234G567");
    }

    @Test
    public void testThousandsGrouping0_1() {
        this.parseFormatAndCheck("#,#.#", 0.1, "!1");
    }

    // thousands multiplier .........................................................................................

    @Test
    public void testThousandsDividerDigitComma0() {
        this.parseFormatAndCheck("0,", 0, "0");
    }

    @Test
    public void testThousandsDividerCommaDecimal12345() {
        this.parseFormatAndCheck("0,.0#######,", 12345, "12!345");
    }

    @Test
    public void testThousandsDividerCommaTextLiteralDecimal12345() {
        this.parseFormatAndCheck("0,\"Text\".0#######,", 12345, "12Text!345");
    }

    @Test
    public void testThousandsDividerCommaCommaDecimal123456789() {
        this.parseFormatAndCheck("0,,.0000##########,", 123456789, "123!456789");
    }

    // misc tests...........................................................................................

    @Test
    public void testFractionDecimal() {
        this.parseFormatAndCheck("#.#.", 1.5, "1!5!");
    }

    @Test
    public void testExponentDecimalFails() {
        assertThrows(ParserReporterException.class, () -> {
            this.createFormatter("#E#.");
        });
    }

    @Test
    public void testExponentExponentFails() {
        assertThrows(ParserReporterException.class, () -> {
            this.createFormatter("#E0E0");
        });
    }

    //toString .........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    //helpers ..........................................................................................................

    private void parseFormatAndCheck(final String pattern,
                                     final double value,
                                     final String text) {
        this.parseFormatAndCheck(pattern, String.valueOf(value), text);
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final String text) {
        this.parseFormatAndCheck0(pattern, value, SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, text));
    }

    private void parseFormatAndCheck0(final String pattern,
                                      final String value,
                                      final SpreadsheetText text) {
        final NumberSpreadsheetFormatter formatter = this.createFormatter(pattern);

        this.formatAndCheck(formatter, new BigDecimal(value), text);

        BigInteger bigInteger;
        try {
            bigInteger = new BigInteger(value);
        } catch (final NumberFormatException ignore) {
            bigInteger = null;
        }
        if (null != bigInteger) {
            this.formatAndCheck(formatter, bigInteger, text);
        }

        this.formatAndCheck(formatter, Double.parseDouble(value), text);

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

    @Override
    String pattern() {
        return "\"quoted text\"";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.number();
    }

    @Override
    NumberSpreadsheetFormatter createFormatter0(final SpreadsheetFormatNumberParserToken token) {
        return NumberSpreadsheetFormatter.with(token);
    }

    @Override
    public BigDecimal value() {
        return new BigDecimal(123);
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public String currencySymbol() {
                return "C";
            }

            @Override
            public char decimalSeparator() {
                return '!';
            }

            @Override
            public char exponentSymbol() {
                return 'E';
            }

            @Override
            public char groupingSeparator() {
                return 'G';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.UNLIMITED;
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
        };
    }

    @Override
    public Class<NumberSpreadsheetFormatter> type() {
        return NumberSpreadsheetFormatter.class;
    }
}
