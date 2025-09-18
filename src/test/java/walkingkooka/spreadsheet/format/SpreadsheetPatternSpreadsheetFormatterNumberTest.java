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
import walkingkooka.InvalidCharacterException;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorTokenAlternative;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.tree.expression.convert.FakeExpressionNumberConverterContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * In expectations all symbols are changed parse defaults to characters to verify the context is supplying such symbols.
 */
public final class SpreadsheetPatternSpreadsheetFormatterNumberTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterNumber,
    NumberSpreadsheetFormatParserToken> {

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            this.createFormatter("0.00"),
            Optional.empty(), // value
            Optional.empty() // expected
        );
    }

    @Test
    public void testFormatDateFails() {
        this.formatAndCheck(
            this.createFormatter("0.00"),
            LocalDate.of(1999, 12, 31),
            new FakeSpreadsheetFormatterContext() {
                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> type) {
                    return ExpressionNumberConverters.toNumberOrExpressionNumber(
                        Converters.localDateTimeToNumber()
                    ).convert(
                        value,
                        type,
                        this
                    );
                }

                @Override
                public ExpressionNumberKind expressionNumberKind() {
                    return ExpressionNumberKind.DOUBLE;
                }
            }
        );
    }

    @Test
    public void testFormatDateTimeFails() {
        this.formatAndCheck(
            this.createFormatter("0.00"),
            LocalDateTime.of(1999, 12, 31, 12, 58)
        );
    }

    @Test
    public void testFormatTimeFails() {
        this.formatAndCheck(
            this.createFormatter("0.00"),
            LocalTime.of(12, 58, 59)
        );
    }

    @Test
    public void testFormatTextFails() {
        this.formatAndCheck(
            this.createFormatter("0.00"),
            "Text123",
            this.createContext(),
            Optional.empty()
        );
    }

    private final static Color RED = Color.parse("#FF0000");

    // text-literal, escaped etc........................................................................................

    @Test
    public void testFormatNumberWithPatternCloseParens() {
        this.parseFormatAndCheck2(")");
    }

    @Test
    public void testFormatNumberWithPatternColon() {
        this.parseFormatAndCheck2(":");
    }

    @Test
    public void testFormatNumberWithPatternMinus() {
        this.parseFormatAndCheck2("-");
    }

    @Test
    public void testFormatNumberWithPatternPlus() {
        this.parseFormatAndCheck2("+");
    }

    @Test
    public void testFormatNumberWithPatternOpenParens() {
        this.parseFormatAndCheck2("(");
    }

    private void parseFormatAndCheck2(final String pattern) {
        this.parseFormatAndCheck2(pattern, pattern);
    }

    @Test
    public void testFormatNumberWithPatternEscaped() {
        this.parseFormatAndCheck2("\\A", "A");
    }

    @Test
    public void testFormatNumberWithPatternTextLiteral() {
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

    // integers ........................................................................................................

    @Test
    public void testFormatNumberWithPatternHash0() {
        this.parseFormatAndCheck(
            "#",
            0,
            ""
        );
    }

    @Test
    public void testFormatNumberWithPatternHash1() {
        this.parseFormatAndCheck(
            "#",
            1,
            "1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHash12() {
        this.parseFormatAndCheck(
            "#",
            12,
            "12"
        );
    }

    @Test
    public void testFormatNumberWithPatternHash123() {
        this.parseFormatAndCheck(
            "#",
            123,
            "123"
        );
    }

    @Test
    public void testFormatNumberWithPatternHash1000() {
        this.parseFormatAndCheck(
            "#",
            1000,
            "1000"
        );
    }

    @Test
    public void testFormatNumberWithPatternHash1234() {
        this.parseFormatAndCheck(
            "#",
            1234,
            "1234"
        );
    }

    @Test
    public void testFormatNumberWithPatternHash0_025() {
        this.parseFormatAndCheck(
            "#",
            0.025,
            ""
        );
    }

    @Test
    public void testFormatNumberWithPatternExtraHash0_025() {
        this.parseFormatAndCheck(
            "###",
            0.025,
            ""
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestion0() {
        this.parseFormatAndCheck(
            "?",
            0,
            " "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestion1() {
        this.parseFormatAndCheck(
            "?",
            1,
            "1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestion12() {
        this.parseFormatAndCheck(
            "?",
            12,
            "12"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestion123() {
        this.parseFormatAndCheck(
            "?",
            123,
            "123"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestion1234() {
        this.parseFormatAndCheck(
            "?",
            1234,
            "1234"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestion0_025() {
        this.parseFormatAndCheck(
            "?",
            0.025,
            " "
        );
    }

    @Test
    public void testFormatNumberWithPatternExtraQuestion() {
        this.parseFormatAndCheck(
            "???",
            12,
            " 12"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero0() {
        this.parseFormatAndCheck(
            "0",
            0,
            "0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero1() {
        this.parseFormatAndCheck(
            "0",
            1,
            "1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero12() {
        this.parseFormatAndCheck(
            "0",
            12,
            "12"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero123() {
        this.parseFormatAndCheck(
            "0",
            123,
            "123"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero1000() {
        this.parseFormatAndCheck(
            "0",
            1000,
            "1000"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero1234() {
        this.parseFormatAndCheck(
            "0",
            1234,
            "1234"
        );
    }

    @Test
    public void testFormatNumberWithPatternZero0_025() {
        this.parseFormatAndCheck(
            "0",
            0.025,
            "0"
        );
    }

    @Test
    public void testFormatNumberWithPatternExtraZero() {
        this.parseFormatAndCheck(
            "0000",
            12,
            "0012"
        );
    }

    // pattern longer than digits

    @Test
    public void testFormatNumberWithPatternHashHashHashHashHash0() {
        this.parseFormatAndCheck(
            "#####",
            0,
            ""
        );
    }

    @Test
    public void testFormatNumberWithPatternHashHashHashHashHash1() {
        this.parseFormatAndCheck(
            "#####",
            1,
            "1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashHashHashHashHash12() {
        this.parseFormatAndCheck(
            "#####",
            12,
            "12"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashHashHashHashHash123() {
        this.parseFormatAndCheck(
            "#####",
            123,
            "123"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashHashHashHashHash1234() {
        this.parseFormatAndCheck(
            "#####",
            1234,
            "1234"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionQuestionQuestionQuestionQuestion0() {
        this.parseFormatAndCheck(
            "?????",
            0,
            "     "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionQuestionQuestionQuestionQuestion1() {
        this.parseFormatAndCheck(
            "?????",
            1,
            "    1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionQuestionQuestionQuestionQuestion12() {
        this.parseFormatAndCheck(
            "?????",
            12,
            "   12"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionQuestionQuestionQuestionQuestion123() {
        this.parseFormatAndCheck(
            "?????",
            123,
            "  123"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionQuestionQuestionQuestionQuestion1234() {
        this.parseFormatAndCheck(
            "?????",
            1234,
            " 1234"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroZeroZeroZeroZero0() {
        this.parseFormatAndCheck(
            "00000",
            0,
            "00000"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroZeroZeroZeroZero1() {
        this.parseFormatAndCheck(
            "00000",
            1,
            "00001"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroZeroZeroZeroZero12() {
        this.parseFormatAndCheck(
            "00000",
            12,
            "00012"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroZeroZeroZeroZero123() {
        this.parseFormatAndCheck(
            "00000",
            123,
            "00123"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroZeroZeroZeroZero1234() {
        this.parseFormatAndCheck(
            "00000",
            1234,
            "01234"
        );
    }

    // hash + zero + question

    @Test
    public void testFormatNumberWithPatternHashHashZeroZeroQuestionQuestionHash5() {
        this.parseFormatAndCheck(
            "##00??#",
            5,
            "00  5"
        );
    }

    // negative.........................................................................................................

    @Test
    public void testFormatNumberWithPatternHashHashZeroZeroQuestionQuestionHashNegative5() {
        this.parseFormatAndCheck(
            "##00??#",
            -5,
            "N00  5"
        );
    }

    // plus.............................................................................................................

    @Test
    public void testFormatNumberWithPatternPlusNegativeNumber() {
        this.parseFormatAndCheck(
            "+#",
            -5,
            "+N5"
        );
    }

    @Test
    public void testFormatNumberWithPatternPlusPositiveNumber() {
        this.parseFormatAndCheck(
            "+#",
            +5,
            "+5"
        );
    }

    // minus............................................................................................................

    @Test
    public void testFormatNumberWithPatternMinusNegativeNumber() {
        this.parseFormatAndCheck(
            "-#",
            -5,
            "-N5"
        );
    }

    @Test
    public void testFormatNumberWithPatternMinusPositiveNumber() {
        this.parseFormatAndCheck(
            "-#",
            +5,
            "-5"
        );
    }

    // fraction.........................................................................................................

    @Test
    public void testFormatNumberWithPatternDecimal0_0() {
        this.parseFormatAndCheck(
            ".",
            0,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternDecimal0_4() {
        this.parseFormatAndCheck(
            ".",
            0.4,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternDecimal0_75() {
        this.parseFormatAndCheck(
            ".",
            0.75,
            "!"
        );
    }

    // fraction hash decimal hash dot...................................................................................

    @Test
    public void testFormatNumberWithPatternHashDecimal0_0() {
        this.parseFormatAndCheck(
            "#.",
            0.0,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimal0_2() {
        this.parseFormatAndCheck(
            "#.",
            0.2,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimal0_7() {
        this.parseFormatAndCheck(
            "#.",
            0.7,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimal1_4() {
        this.parseFormatAndCheck(
            "#.",
            1.4,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimal1_7() {
        this.parseFormatAndCheck(
            "#.",
            1.7,
            "2!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimal0_025() {
        this.parseFormatAndCheck(
            "#.",
            0.025,
            "!"
        );
    }

    // fraction space. space dot........................................................................................

    @Test
    public void testFormatNumberWithPatternQuestionDecimal0_0() {
        this.parseFormatAndCheck(
            "?.",
            0.0,
            " !"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimal0_2() {
        this.parseFormatAndCheck(
            "?.",
            0.2,
            " !"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimal0_7() {
        this.parseFormatAndCheck(
            "?.",
            0.7,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimal1_4() {
        this.parseFormatAndCheck(
            "?.",
            1.4,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimal1_7() {
        this.parseFormatAndCheck(
            "?.",
            1.7,
            "2!"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimal0_025() {
        this.parseFormatAndCheck(
            "?.",
            0.025,
            " !"
        );
    }

    // fraction zero. zero dot............................................................................................

    @Test
    public void testFormatNumberWithPatternZeroDecimal0_0() {
        this.parseFormatAndCheck(
            "0.",
            0.0,
            "0!"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimal0_2() {
        this.parseFormatAndCheck(
            "0.",
            0.2,
            "0!"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimal0_7() {
        this.parseFormatAndCheck(
            "0.",
            0.7,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimal1_4() {
        this.parseFormatAndCheck(
            "0.",
            1.4,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimal1_7() {
        this.parseFormatAndCheck(
            "0.",
            1.7,
            "2!"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimal0_025() {
        this.parseFormatAndCheck(
            "0.",
            0.025,
            "0!"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimal0_075() {
        this.parseFormatAndCheck(
            "0.",
            0.075,
            "0!"
        );
    }

    // fraction space. space dot space ...................................................................................

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_0() {
        this.parseFormatAndCheck(
            "#.#",
            0.0,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_2() {
        this.parseFormatAndCheck(
            "#.#",
            0.2,
            "!2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_6() {
        this.parseFormatAndCheck(
            "#.#",
            0.6,
            "!6"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_01() {
        this.parseFormatAndCheck(
            "#.#",
            0.01,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_71() {
        this.parseFormatAndCheck(
            "#.#",
            0.71,
            "!7"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_85() {
        this.parseFormatAndCheck(
            "#.#",
            0.85,
            "!9"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash1_0() {
        this.parseFormatAndCheck(
            "#.#",
            1,
            "1!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash1_99() {
        this.parseFormatAndCheck(
            "#.#",
            1.99,
            "2!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_025() {
        this.parseFormatAndCheck(
            "#.#",
            0.025,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_075() {
        this.parseFormatAndCheck(
            "#.#",
            0.075,
            "!1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHash0_0001() {
        this.parseFormatAndCheck(
            "#.#",
            0.0001,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHash0_01() {
        this.parseFormatAndCheck(
            "#.##",
            0.01,
            "!01"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHash0_012() {
        this.parseFormatAndCheck(
            "#.##",
            0.012,
            "!01"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHash0_0123() {
        this.parseFormatAndCheck(
            "#.##",
            0.0123,
            "!01"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashHash0_001() {
        this.parseFormatAndCheck(
            "#.###",
            0.001,
            "!001"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashHash0_0012() {
        this.parseFormatAndCheck(
            "#.###",
            0.0012,
            "!001"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashHash0_00123() {
        this.parseFormatAndCheck(
            "#.###",
            0.00123,
            "!001"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashHash0_001234() {
        this.parseFormatAndCheck(
            "#.###",
            0.001234,
            "!001"
        );
    }

    // fraction space. space dot space .................................................................................

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_0() {
        this.parseFormatAndCheck(
            "?.?",
            0.0,
            " ! "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_2() {
        this.parseFormatAndCheck(
            "?.?",
            0.2,
            " !2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_6() {
        this.parseFormatAndCheck(
            "?.?",
            0.6,
            " !6"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_71() {
        this.parseFormatAndCheck(
            "?.?",
            0.71,
            " !7"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_85() {
        this.parseFormatAndCheck(
            "?.?",
            0.85,
            " !9"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion1_0() {
        this.parseFormatAndCheck(
            "?.?",
            1,
            "1! "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion1_99() {
        this.parseFormatAndCheck(
            "?.?",
            1.99,
            "2! "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_025() {
        this.parseFormatAndCheck(
            "?.?",
            0.025,
            " ! "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_075() {
        this.parseFormatAndCheck(
            "?.?",
            0.075,
            " !1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestion0_0001() {
        this.parseFormatAndCheck(
            "?.?",
            0.0001,
            " ! "
        );
    }

    // fraction zero. zero dot zero ....................................................................................

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_0() {
        this.parseFormatAndCheck(
            "0.0",
            0.0,
            "0!0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_2() {
        this.parseFormatAndCheck(
            "0.0",
            0.2,
            "0!2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_6() {
        this.parseFormatAndCheck(
            "0.0",
            0.6,
            "0!6"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_71() {
        this.parseFormatAndCheck(
            "0.0",
            0.71,
            "0!7"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_85() {
        this.parseFormatAndCheck(
            "0.0",
            0.85,
            "0!9"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero1_0() {
        this.parseFormatAndCheck(
            "0.0",
            1,
            "1!0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero1_25() {
        this.parseFormatAndCheck(
            "0.0",
            1.25,
            "1!3"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero1_975() {
        this.parseFormatAndCheck(
            "0.0",
            1.975,
            "2!0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_025() {
        this.parseFormatAndCheck(
            "0.0",
            0.025,
            "0!0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_075() {
        this.parseFormatAndCheck(
            "0.0",
            0.075,
            "0!1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZero0_0005() {
        this.parseFormatAndCheck(
            "0.0",
            0.0005,
            "0!0"
        );
    }

    // long fraction patterns

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashHash0_0000005() {
        this.parseFormatAndCheck(
            "#.###",
            0.0000005,
            "!"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionQuestion0_0000005() {
        this.parseFormatAndCheck(
            "?.???",
            0.0000005,
            " !   "
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroZero0_0000005() {
        this.parseFormatAndCheck(
            "0.000",
            0.0000005,
            "0!000"
        );
    }

    // hash space zero.................................................................................

    @Test
    public void testFormatNumberWithPatternHashDecimalSpaceHash1_05() {
        this.parseFormatAndCheck(
            "?.?#",
            1.05,
            "1!05"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalZeroHash1_05() {
        this.parseFormatAndCheck(
            "?.?0",
            1.05,
            "1!05"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalSpaceZeroHash1_00005() {
        this.parseFormatAndCheck(
            "?.?",
            1.005,
            "1! "
        );
    }

    //exponent zero exponent minus......................................................................................

    @Test
    public void testFormatNumberWithPatternZeroExponentZero0() {
        this.parseFormatAndCheck(
            "0E-0",
            0,
            "0E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroExponentZero1() {
        this.parseFormatAndCheck(
            "0E-0",
            1,
            "1E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroExponentZero12() {
        this.parseFormatAndCheck(
            "0E-0",
            12,
            "1E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroExponentZero90() {
        this.parseFormatAndCheck(
            "0E-0",
            90,
            "9E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroExponentZero123() {
        this.parseFormatAndCheck(
            "0E-0",
            123,
            "1E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroExponentZero123456789() {
        this.parseFormatNumberAndCheck(
            "0E-0",
            "12345678901",
            "1E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroExponentZero0_0123() {
        this.parseFormatAndCheck(
            "0E-0",
            0.0123,
            "1EN2"
        );
    }

    // hash exponent minus..............................................................................................

    @Test
    public void testFormatNumberWithPatternHashExponentHash0() {
        this.parseFormatAndCheck(
            "#E-#",
            0,
            "E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash1() {
        this.parseFormatAndCheck(
            "#E-#",
            1,
            "1E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash12() {
        this.parseFormatAndCheck(
            "#E-#",
            12,
            "1E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash19() {
        this.parseFormatAndCheck(
            "#E-#",
            19,
            "2E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash90() {
        this.parseFormatAndCheck(
            "#E-#",
            90,
            "9E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash123() {
        this.parseFormatAndCheck(
            "#E-#",
            123,
            "1E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash1234567890() {
        this.parseFormatNumberAndCheck(
            "#E-#",
            "12345678901",
            "1E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashExponentHash0_0123() {
        this.parseFormatAndCheck(
            "#E-#",
            0.0123,
            "1EN2"
        );
    }

    // question exponent minus..........................................................................................

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion0() {
        this.parseFormatAndCheck(
            "?E-?",
            0,
            " E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion1() {
        this.parseFormatAndCheck(
            "?E-?",
            1,
            "1E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion12() {
        this.parseFormatAndCheck(
            "?E-?",
            12,
            "1E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion19() {
        this.parseFormatAndCheck(
            "?E-?",
            19,
            "2E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion90() {
        this.parseFormatAndCheck(
            "?E-?",
            90,
            "9E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion123() {
        this.parseFormatAndCheck(
            "?E-?",
            123,
            "1E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion1234567890() {
        this.parseFormatNumberAndCheck(
            "?E-?",
            "12345678901",
            "1E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck(
            "?E-?",
            0.0123,
            "1EN2"
        );
    }

    // zero decimal Exponent............................................................................................

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero0() {
        this.parseFormatAndCheck(
            "0.E-0",
            0,
            "0!E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero1() {
        this.parseFormatAndCheck(
            "0.E-0",
            1,
            "1!E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero12() {
        this.parseFormatAndCheck(
            "0.E-0",
            12,
            "1!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero19() {
        this.parseFormatAndCheck(
            "0.E-0",
            19,
            "2!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero90() {
        this.parseFormatAndCheck(
            "0.E-0",
            90,
            "9!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero123() {
        this.parseFormatAndCheck(
            "0.E-0",
            123,
            "1!E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero1234567890() {
        this.parseFormatNumberAndCheck(
            "0.E-0",
            "12345678901",
            "1!E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalExponentZero0_0123() {
        this.parseFormatAndCheck(
            "0.E-0",
            0.0123,
            "1!EN2"
        );
    }

    // hash decimal Exponent............................................................................................

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash0() {
        this.parseFormatAndCheck(
            "#.E-#",
            0,
            "!E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash1() {
        this.parseFormatAndCheck(
            "#.E-#",
            1,
            "1!E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash12() {
        this.parseFormatAndCheck(
            "#.E-#",
            12,
            "1!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash19() {
        this.parseFormatAndCheck(
            "#.E-#",
            19,
            "2!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash129() {
        this.parseFormatAndCheck(
            "#.E-#",
            129,
            "1!E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
            "#.E-#",
            "12345678901",
            "1!E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalExponentHash0_0123() {
        this.parseFormatAndCheck(
            "#.E-#",
            0.0123,
            "1!EN2"
        );
    }

    // question decimal Exponent........................................................................................

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion0() {
        this.parseFormatAndCheck(
            "?.E-?",
            0,
            " !E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion1() {
        this.parseFormatAndCheck(
            "?.E-?",
            1,
            "1!E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion12() {
        this.parseFormatAndCheck(
            "?.E-?",
            12,
            "1!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion19() {
        this.parseFormatAndCheck(
            "?.E-?",
            19,
            "2!E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion123() {
        this.parseFormatAndCheck(
            "?.E-?",
            123,
            "1!E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion12345678901() {
        this.parseFormatNumberAndCheck(
            "?.E-?",
            "12345678901",
            "1!E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalExponentQuestion0_0123() {
        this.parseFormatAndCheck(
            "?.E-?",
            0.0123,
            "1!EN2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero0() {
        this.parseFormatAndCheck(
            "0.0E-0",
            0,
            "0!0E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero1() {
        this.parseFormatAndCheck(
            "0.0E-0",
            1,
            "1!0E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero12() {
        this.parseFormatAndCheck(
            "0.0E-0",
            12,
            "1!2E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero123() {
        this.parseFormatAndCheck(
            "0.0E-0",
            123,
            "1!2E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero129() {
        this.parseFormatAndCheck(
            "0.0E-0",
            129,
            "1!3E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero12345678901() {
        this.parseFormatNumberAndCheck(
            "0.0E-0",
            "12345678901",
            "1!2E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroExponentZero0_0123() {
        this.parseFormatAndCheck(
            "0.E-0",
            0.0123,
            "1!EN2"
        );
    }

    // hash decimal hash Exponent.......................................................................................

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash0() {
        this.parseFormatAndCheck(
            "#.#E-#",
            0,
            "!E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash1() {
        this.parseFormatAndCheck(
            "#.#E-#",
            1,
            "1!E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash12() {
        this.parseFormatAndCheck(
            "#.#E-#",
            12,
            "1!2E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash123() {
        this.parseFormatAndCheck(
            "#.#E-#",
            123,
            "1!2E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash129() {
        this.parseFormatAndCheck(
            "#.#E-#",
            129,
            "1!3E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
            "#.#E-#",
            "12345678901",
            "1!2E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashExponentHash0_0123() {
        this.parseFormatAndCheck(
            "#.#E-#",
            0.0123,
            "1!2EN2"
        );
    }

    // question decimal question Exponent...............................................................................

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion0() {
        this.parseFormatAndCheck(
            "?.?E-?",
            0,
            " ! E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion1() {
        this.parseFormatAndCheck(
            "?.?E-?",
            1,
            "1! E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion12() {
        this.parseFormatAndCheck(
            "?.?E-?",
            12,
            "1!2E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion122() {
        this.parseFormatAndCheck(
            "?.?E-?",
            122,
            "1!2E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion129() {
        this.parseFormatAndCheck(
            "?.?E-?",
            129,
            "1!3E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion12345678901() {
        this.parseFormatNumberAndCheck(
            "?.?E-?",
            "12345678901",
            "1!2E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck(
            "?.?E-?",
            0.0123,
            "1!2EN2"
        );
    }

    // zero decimal zero Exponent plus..................................................................................

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentZero0() {
        this.parseFormatAndCheck(
            "0.00E-0",
            0,
            "0!00E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentZero1() {
        this.parseFormatAndCheck(
            "0.00E-0",
            1,
            "1!00E0"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentZero12() {
        this.parseFormatAndCheck(
            "0.00E-0",
            12,
            "1!20E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentZero123_1() {
        this.parseFormatAndCheck(
            "0.00E-0",
            123.1,
            "1!23E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentZero124_9() {
        this.parseFormatAndCheck(
            "0.00E-0",
            124.9,
            "1!25E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
            "0.00E-#",
            "12345678901",
            "1!23E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternZeroDecimalZeroZeroExponentZero0_0123() {
        this.parseFormatAndCheck(
            "0.00E-0",
            0.0123,
            "1!23EN2"
        );
    }

    // hash decimal hash Exponent.......................................................................................

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash0() {
        this.parseFormatAndCheck(
            "#.##E-#",
            0,
            "!E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash1() {
        this.parseFormatAndCheck(
            "#.##E-#",
            1,
            "1!E"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash12() {
        this.parseFormatAndCheck(
            "#.##E-#",
            12,
            "1!2E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash122_1() {
        this.parseFormatAndCheck(
            "#.##E-#",
            122.1,
            "1!22E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash122_9() {
        this.parseFormatAndCheck(
            "#.##E-#",
            122.9,
            "1!23E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash12345678901() {
        this.parseFormatNumberAndCheck(
            "#.##E-#",
            "12345678901",
            "1!23E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternHashDecimalHashHashExponentHash0_0123() {
        this.parseFormatAndCheck(
            "#.##E-#",
            0.0123,
            "1!23EN2"
        );
    }

    // question decimal question Exponent...............................................................................

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion0() {
        this.parseFormatAndCheck(
            "?.??E-?",
            0,
            " !  E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion1() {
        this.parseFormatAndCheck(
            "?.??E-?",
            1,
            "1!  E "
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion12() {
        this.parseFormatAndCheck(
            "?.??E-?",
            12,
            "1!2 E1"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion122_1() {
        this.parseFormatAndCheck(
            "?.??E-?",
            122.1,
            "1!22E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion122_9() {
        this.parseFormatAndCheck(
            "?.??E-?",
            122.9,
            "1!23E2"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion12345678901() {
        this.parseFormatNumberAndCheck(
            "?.??E-?",
            "12345678901",
            "1!23E10"
        );
    }

    @Test
    public void testFormatNumberWithPatternQuestionDecimalQuestionQuestionExponentQuestion0_0123() {
        this.parseFormatAndCheck(
            "?.??E-?",
            0.0123,
            "1!23EN2"
        );
    }

    // exponent plus.

    // currency .........................................................................................................

    @Test
    public void testFormatNumberWithPatternCurrency() {
        this.parseFormatAndCheck(
            "$000",
            100,
            "C100"
        );
    }

    @Test
    public void testFormatNumberWithPatternCurrencyMonetaryDecimalSeparator() {
        this.parseFormatAndCheck(
            "$000.000",
            123.456,
            "C123*456"
        );
    }

    // percentage ......................................................................................................

    @Test
    public void testFormatNumberWithPatternPercentage0() {
        this.parseFormatAndCheck(
            "0%",
            0,
            "0R"
        );
    }

    @Test
    public void testFormatNumberWithPatternPercentagePositive1() {
        this.parseFormatAndCheck(
            "0%",
            1,
            "100R"
        );
    }

    @Test
    public void testFormatNumberWithPatternPercentageNegative1() {
        this.parseFormatAndCheck(
            "0%",
            -1,
            "N100R"
        );
    }

    @Test
    public void testFormatNumberWithPatternPercentagePositive0_01() {
        this.parseFormatAndCheck(
            "0%",
            0.01,
            "1R"
        );
    }

    @Test
    public void testFormatNumberWithPatternPercentagePositive0_001() {
        this.parseFormatAndCheck(
            "0.#%",
            0.001,
            "0!1R"
        );
    }

    // groupSeparator...................................................................................................

    @Test
    public void testFormatNumberWithPatternGroupSeparator0() {
        this.parseFormatAndCheck(
            "#,0",
            0,
            "0"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparatorPositive1() {
        this.parseFormatAndCheck(
            "#,0",
            1,
            "1"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparatorNegative1() {
        this.parseFormatAndCheck(
            "#,0",
            -1,
            "N1"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator12() {
        this.parseFormatAndCheck(
            "#,0",
            12,
            "12"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator123() {
        this.parseFormatAndCheck(
            "#,0",
            123,
            "123"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator1234() {
        this.parseFormatAndCheck(
            "#,0",
            1234,
            "1G234"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator12345() {
        this.parseFormatAndCheck(
            "#,0",
            12345,
            "12G345"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator123456() {
        this.parseFormatAndCheck(
            "#,0",
            123456,
            "123G456"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator1234567() {
        this.parseFormatAndCheck(
            "#,0",
            1234567,
            "1G234G567"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparatorGroupSeparator1234567() {
        this.parseFormatAndCheck(
            "#,0",
            1234567,
            "1G234G567"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparator0_1() {
        this.parseFormatAndCheck(
            "#,#.#",
            0.1,
            "!1"
        );
    }

    // groupSeparator multiplier .......................................................................................

    @Test
    public void testFormatNumberWithPatternGroupSeparatorDividerDigitComma0() {
        this.parseFormatAndCheck(
            "0,",
            0,
            "0"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparatorDividerCommaDecimal12345() {
        this.parseFormatAndCheck(
            "0,.0#######,",
            12345,
            "12!345"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparatorDividerCommaTextLiteralDecimal12345() {
        this.parseFormatAndCheck(
            "0,\"Text\".0#######,",
            12345,
            "12Text!345"
        );
    }

    @Test
    public void testFormatNumberWithPatternGroupSeparatorDividerCommaCommaDecimal123456789() {
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
        assertThrows(
            InvalidCharacterException.class,
            () -> this.createFormatter("#E#.")
        );
    }

    @Test
    public void testFormatExponentExponentFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> this.createFormatter("#E0E0")
        );
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

    @Test
    public void testFormatArabicZeroDigit() {
        this.formatNumberAndCheck(
            this.createFormatter("0.00"),
            "12.34",
            ExpressionNumberKind.BIG_DECIMAL,
            this.createContext(ARABIC_ZERO_DIGIT),
            SpreadsheetText.EMPTY.setText(
                arabicDigit(1) +
                    arabicDigit(2) +
                    "!" +
                    arabicDigit(3) +
                    arabicDigit(4)
            )
        );
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
    SpreadsheetPatternSpreadsheetFormatterNumber createFormatter0(final NumberSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterNumber.with(token);
    }

    @Override
    public BigDecimal value() {
        return new BigDecimal(123);
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return this.createContext('0');
    }

    private SpreadsheetFormatterContext createContext(final RoundingMode roundingMode) {
        return this.createContext(
            '0',
            roundingMode
        );
    }

    private SpreadsheetFormatterContext createContext(final char zeroDigit) {
        return this.createContext(
            zeroDigit,
            RoundingMode.HALF_UP
        );
    }

    private SpreadsheetFormatterContext createContext(final char zeroDigit,
                                                      final RoundingMode roundingMode) {
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
            public char monetaryDecimalSeparator() {
                return '*';
            }

            @Override
            public char negativeSign() {
                return 'N';
            }

            @Override
            public char percentSymbol() {
                return 'R';
            }

            @Override
            public char positiveSign() {
                return 'P';
            }

            @Override
            public char zeroDigit() {
                return zeroDigit;
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

    // tokens...........................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createFormatter("$#0.00"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "$",
                "$",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "#",
                "#",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                ".",
                ".",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            )
        );
    }

    @Test
    public void testTokensWithColor() {
        this.tokensAndCheck(
            this.createFormatter("[RED]0.00"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "[RED]",
                "[RED]",
                Stream.concat(
                    SpreadsheetColorName.DEFAULTS.stream()
                        .map(n -> "[" + n.text() + "]")
                        .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(t, t)),
                    IntStream.rangeClosed(
                            SpreadsheetColors.MIN,
                            SpreadsheetColors.MAX
                        ).mapToObj(n -> "[Color " + n + "]")
                        .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(t, t))
                ).collect(Collectors.toList())
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                ".",
                ".",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentToken() {
        this.checkNotEquals(
            this.createFormatter("$0.00"),
            this.createFormatter("#.##")
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterNumber> type() {
        return SpreadsheetPatternSpreadsheetFormatterNumber.class;
    }
}
