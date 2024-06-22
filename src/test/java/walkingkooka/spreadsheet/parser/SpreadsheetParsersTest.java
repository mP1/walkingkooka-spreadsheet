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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import walkingkooka.Cast;
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
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
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
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
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

public final class SpreadsheetParsersTest implements PublicStaticHelperTesting<SpreadsheetParsers>,
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

    // apostrophe string values.........................................................................................

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
                valueOrExpressionParser(),
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

    // date values.......................................................................................................

    @Test
    public void testParseDateWithExtraTextFails() {
        this.parseFailAndCheck(
                SpreadsheetParsers.valueOrExpression(SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").parser()),
                "2000/12/31 Extra"
        );
    }

    @Test
    public void testDateDayMonthYear2000() {
        this.parseDateAndCheck(
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
    public void testDateDayMonthYear99() {
        this.parseDateAndCheck(
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
    public void testDateYear2000MonthDay() {
        this.parseDateAndCheck(
                "2000/12/31",
                "yyyy/mm/dd",
                year2000(),
                slash(),
                month12(),
                slash(),
                day31()
        );
    }

    private void parseDateAndCheck(final String text,
                                   final String pattern,
                                   final SpreadsheetParserToken... tokens) {
        this.parseValueAndCheck(
                text,
                SpreadsheetPattern.parseDateParsePattern(pattern).parser(),
                SpreadsheetParserToken::date,
                tokens
        );
    }

    // date/time values.................................................................................................

    @Test
    public void testParseDateTimeWithExtraTextFails() {
        this.parseFailAndCheck(
                SpreadsheetParsers.valueOrExpression(SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:m").parser()),
                "2000/12/31 12:58 Extra"
        );
    }

    @Test
    public void testDateTimeDayMonthYearHourMinutes() {
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
                                       final SpreadsheetParserToken... tokens) {
        this.parseValueAndCheck(
                text,
                SpreadsheetPattern.parseDateTimeParsePattern(pattern).parser(),
                SpreadsheetParserToken::dateTime,
                tokens
        );
    }

    // time values.......................................................................................................

    @Test
    public void testParseTimeWithExtraTextFails() {
        this.parseFailAndCheck(
                SpreadsheetParsers.valueOrExpression(SpreadsheetPattern.parseTimeParsePattern("hh:mm").parser()),
                "12:58 Extra"
        );
    }

    @Test
    public void testTimeHourMinutes() {
        this.parseTimeAndCheck(
                "11:58",
                "hh:mm",
                hour11(),
                colon(),
                minute58()
        );
    }

    @Test
    public void testTimeHourMinutesSeconds() {
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
    public void testTimeHourMinutesSecondsMillis() {
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
                                   final SpreadsheetParserToken... tokens) {
        this.parseValueAndCheck(
                text,
                SpreadsheetPattern.parseTimeParsePattern(pattern).parser(),
                SpreadsheetParserToken::time,
                tokens
        );
    }

    private void parseValueAndCheck(final String text,
                                    final Parser<SpreadsheetParserContext> parser,
                                    final BiFunction<List<ParserToken>, String, SpreadsheetParentParserToken> factory,
                                    final SpreadsheetParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        this.parseAndCheck(
                SpreadsheetParsers.valueOrExpression(parser),
                text,
                factory.apply(
                        list,
                        ParserToken.text(list)
                ),
                text,
                ""
        );
    }

    private SpreadsheetTextLiteralParserToken colon() {
        return textLiteral(":");
    }

    private SpreadsheetDayNumberParserToken day31() {
        return SpreadsheetParserToken.dayNumber(31, "31");
    }

    private SpreadsheetDecimalSeparatorSymbolParserToken decimal() {
        return SpreadsheetParserToken.decimalSeparatorSymbol(".", ".");
    }

    private SpreadsheetHourParserToken hour11() {
        return SpreadsheetParserToken.hour(11, "11");
    }

    private SpreadsheetMillisecondParserToken millis123() {
        return SpreadsheetParserToken.millisecond(123_000_000, "123");
    }

    private SpreadsheetMinuteParserToken minute58() {
        return SpreadsheetParserToken.minute(58, "58");
    }

    private SpreadsheetMonthNumberParserToken month12() {
        return SpreadsheetParserToken.monthNumber(12, "12");
    }

    private SpreadsheetAmPmParserToken pm() {
        return SpreadsheetParserToken.amPm(12, "pm");
    }

    private SpreadsheetSecondsParserToken seconds59() {
        return SpreadsheetParserToken.seconds(59, "59");
    }

    private SpreadsheetTextLiteralParserToken slash() {
        return textLiteral("/");
    }

    private SpreadsheetTextLiteralParserToken textLiteral(final String text) {
        return SpreadsheetParserToken.textLiteral(text, text);
    }

    private SpreadsheetYearParserToken year99() {
        return SpreadsheetParserToken.year(99, "99");
    }

    private SpreadsheetYearParserToken year2000() {
        return SpreadsheetParserToken.year(2000, "2000");
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
                        text
                ),
                text
        );
    }

    @Test
    public void testTextWithEscapedDoubleQuote() {
        final String text = "\"abc-\"\"-123\"";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.text(
                        Lists.of(
                                doubleQuotes(),
                                SpreadsheetParserToken.textLiteral("abc-\"-123", "abc-\"\"-123"),
                                doubleQuotes()
                        ),
                        text
                ),
                text,
                "\"abc-\\\"-123\""
        );
    }

    // NUMBER...........................................................................................................

    @Test
    public void testParseNumberWithExtraTextFails() {
        this.parseFailAndCheck(
                SpreadsheetParsers.valueOrExpression(SpreadsheetPattern.parseNumberParsePattern("#").parser().andEmptyTextCursor()),
                "12 Extra"
        );
    }

    @Test
    public void testNumber() {
        final String text = "1";

        this.parseExpressionAndCheck(text,
                number(1),
                text);
    }

    @Test
    public void testNumberPercent() {
        final String text = "100%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(100),
                        percent()
                ),
                text,
                "1"
        );
    }

    @Test
    public void testNumberPercent2() {
        final String text = "123%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(123),
                        percent()
                ),
                text,
                "1.23"
        );
    }

    @Test
    public void testNumberDecimalPercent() {
        final String text = "100.%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(100),
                        decimal(),
                        percent()
                ),
                text,
                "1"
        );
    }

    @Test
    public void testNumberDecimalPercent2() {
        final String text = "123.%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(123),
                        decimal(),
                        percent()
                ),
                text,
                "1.23"
        );
    }

    @Test
    public void testNumberDecimalDigitPercent() {
        final String text = "100.0%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(100),
                        decimal(),
                        digits(0),
                        percent()
                ),
                text,
                "1"
        );
    }

    @Test
    public void testNumberDecimalDigitPercent2() {
        final String text = "123.5%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(123),
                        decimal(),
                        digits(5),
                        percent()
                ),
                text,
                "1.235"
        );
    }

    // CELL,............................................................................................................

    @Test
    public void testCellParserWithColumnFails() {
        this.parseFailAndCheck(
                SpreadsheetParsers.cell(),
                "A"
        );
    }

    @Test
    public void testCellParserWithLabelFails() {
        this.parseThrows(
                SpreadsheetParsers.cell(),
                "LABEL123"
        );
    }

    @Test
    public void testCellParserWithCell() {
        final String text = "A1";
        final SpreadsheetCellReferenceParserToken cell = cell(0, "A", 0);

        this.cellParseAndCheck(text, cell, text);
    }

    @Test
    public void testCellParserWithCell2() {
        final String text = "AA678";
        final SpreadsheetCellReferenceParserToken cell = this.cell(26, "AA", 678 - 1);

        this.cellParseAndCheck(text, cell, text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#cell()}} and then repeat again with
     * {@link SpreadsheetParsers#expression()}.
     */
    private void cellParseAndCheck(final String from,
                                   final SpreadsheetParserToken expected,
                                   final String text) {
        this.parseAndCheck(
                SpreadsheetParsers.cellOrCellRangeOrLabel(),
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

    // CELL, CELL RANGE & LABEL ........................................................................................

    @Test
    public void testCellOrCellRangeOrLabelParserWithExpressionFails() {
        this.parseFailAndCheck(
                SpreadsheetParsers.cellOrCellRangeOrLabel(),
                "1+2"
        );
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithCell() {
        final String text = "A1";
        final SpreadsheetCellReferenceParserToken cell = cell(0, "A", 0);

        this.cellOrCellRangeOrLabelParseAndCheck(text, cell, text);
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithCell2() {
        final String text = "AA678";
        final SpreadsheetCellReferenceParserToken cell = this.cell(26, "AA", 678 - 1);

        this.cellOrCellRangeOrLabelParseAndCheck(text, cell, text);
    }

    @Test
    public void testCellOrCellRangeOrLabelParserWithCellRange() {
        final String text = "A1:B2";

        final SpreadsheetCellRangeParserToken range = SpreadsheetParserToken.cellRange(
                Lists.of(
                        this.cell(0, "A", 0),
                        between(),
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
                SpreadsheetParserToken.labelName(SpreadsheetSelection.labelName(text), text),
                text);
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#cellOrCellRangeOrLabel()}} and then repeat again with
     * {@link SpreadsheetParsers#expression()}.
     */
    private void cellOrCellRangeOrLabelParseAndCheck(final String from,
                                                     final SpreadsheetParserToken expected,
                                                     final String text) {
        this.parseAndCheck(
                SpreadsheetParsers.cellOrCellRangeOrLabel(),
                from,
                expected,
                text
        );
        this.parseExpressionAndCheck(from, expected, text);
    }

    // RANGE............................................................................................................

    @Test
    public void testRangeParserOtherExpressionFails() {
        this.parseFailAndCheck(SpreadsheetParsers.cellRange(), "1+2");
    }

    @Test
    public void testRangeCellToCell() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetCellReferenceParserToken to = this.cell(1, "B", 1);

        final SpreadsheetCellRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/2197 SpreadsheetCellRangeReference only allowing begin/end cells")
    public void testRangeLabelToLabel() {
        final SpreadsheetLabelNameParserToken from = this.label("parse");
        final SpreadsheetLabelNameParserToken to = this.label("to");

        final SpreadsheetCellRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/2197 SpreadsheetCellRangeReference only allowing begin/end cells")
    public void testRangeCellToLabel() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetLabelNameParserToken to = this.label("to");

        final SpreadsheetCellRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/2197 SpreadsheetCellRangeReference only allowing begin/end cells")
    public void testRangeLabelToCell() {
        final SpreadsheetLabelNameParserToken from = this.label("to");
        final SpreadsheetCellReferenceParserToken to = this.cell(0, "A", 0);

        final SpreadsheetCellRangeParserToken range = range(from, to);
        final String text = range.text();

        this.rangeParseAndCheck(text, range, text);
    }

    @Test
    public void testRangeWhitespace() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetCellReferenceParserToken to = this.cell(1, "B", 1);

        final String text = from.text() + "  " + between() + "  " + to.text();
        final SpreadsheetCellRangeParserToken range = SpreadsheetParserToken.cellRange(
                Lists.of(
                        from,
                        whitespace2(),
                        between(),
                        whitespace2(),
                        to
                ),
                text
        );

        this.rangeParseAndCheck(
                text,
                range,
                text,
                text.replace(" ", "")
        );
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#cellRange()} and then repeat again with {@link SpreadsheetParsers#expression()}.
     */
    private void rangeParseAndCheck(final String from,
                                    final SpreadsheetCellRangeParserToken expected,
                                    final String text) {
        this.rangeParseAndCheck(
                from,
                expected,
                text,
                text
        );
    }

    private void rangeParseAndCheck(final String from,
                                    final SpreadsheetCellRangeParserToken expected,
                                    final String text,
                                    final String expressionToString) {
        this.parseAndCheck(SpreadsheetParsers.cellRange(), from, expected, text);
        this.parseExpressionAndCheck(from, expected, text, expressionToString);
    }

    // error...........................................................................................................

    @Test
    public void testErrorDiv0() {
        this.errorParseAndCheck(SpreadsheetErrorKind.DIV0);
    }

    @Test
    public void testErrorName() {
        this.errorParseAndCheck(SpreadsheetErrorKind.NAME);
    }

    @Test
    public void testErrorRef() {
        this.errorParseAndCheck(SpreadsheetErrorKind.REF);
    }

    private void errorParseAndCheck(final SpreadsheetErrorKind kind) {
        this.errorParseAndCheck(
                kind.text(),
                SpreadsheetParserToken.error(
                        kind.toError(),
                        kind.text()
                )
        );
    }

    private void errorParseAndCheck(final String from,
                                    final SpreadsheetErrorParserToken expected) {
        this.parseAndCheck(
                SpreadsheetParsers.error(),
                from,
                expected,
                from
        );
    }

    // Expression cell..................................................................................................

    @Test
    public void testExpressionStarFails() {
        this.parseExpressionThrows(
                "1+*",
                "Invalid character '*' at (3,1)"
        );
    }

    @Test
    public void testExpressionCellReference() {
        final String text = "A1";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.cellReference(
                        Lists.of(
                                SpreadsheetParserToken.columnReference(
                                        SpreadsheetSelection.parseColumn("A"),
                                        "A"
                                ),
                                SpreadsheetParserToken.rowReference(
                                        SpreadsheetSelection.parseRow("1"),
                                        "1"
                                )
                        ),
                        text
                ),
                text
        );
    }

    @Test
    public void testExpressionCellReferenceAbsolute() {
        final String text = "$A$1";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.cellReference(
                        Lists.of(
                                SpreadsheetParserToken.columnReference(
                                        SpreadsheetSelection.parseColumn("$A"),
                                        "$A"
                                ),
                                SpreadsheetParserToken.rowReference(
                                        SpreadsheetSelection.parseRow("$1"),
                                        "$1"
                                )
                        ),
                        text
                ),
                text
        );
    }

    @Test
    public void testExpressionCellReferenceMixed() {
        final String text = "$A1";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.cellReference(
                        Lists.of(
                                SpreadsheetParserToken.columnReference(
                                        SpreadsheetSelection.parseColumn("$A"),
                                        "$A"
                                ),
                                SpreadsheetParserToken.rowReference(
                                        SpreadsheetSelection.parseRow("1"),
                                        "1"
                                )
                        ),
                        text
                ),
                text
        );
    }

    @Test
    public void testExpressionCellReferenceMixed2() {
        final String text = "A$1";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.cellReference(
                        Lists.of(
                                SpreadsheetParserToken.columnReference(
                                        SpreadsheetSelection.parseColumn("A"),
                                        "A"
                                ),
                                SpreadsheetParserToken.rowReference(
                                        SpreadsheetSelection.parseRow("$1"),
                                        "$1"
                                )
                        ),
                        text
                ),
                text
        );
    }

    // Expression cell range............................................................................................

    @Test
    public void testExpressionCellRange() {
        final String text = "A1:B2";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.cellRange(
                        Lists.of(
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("A"),
                                                        "A"
                                                ),
                                                SpreadsheetParserToken.rowReference(
                                                        SpreadsheetSelection.parseRow("1"),
                                                        "1"
                                                )
                                        ),
                                        "A1"
                                ),
                                SpreadsheetParserToken.betweenSymbol(":", ":"),
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("B"),
                                                        "B"
                                                ),
                                                SpreadsheetParserToken.rowReference(
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
    public void testExpressionCellRangeAbsolute() {
        final String text = "$A$1:$B$2";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.cellRange(
                        Lists.of(
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("$A"),
                                                        "$A"
                                                ),
                                                SpreadsheetParserToken.rowReference(
                                                        SpreadsheetSelection.parseRow("$1"),
                                                        "$1"
                                                )
                                        ),
                                        "$A$1"
                                ),
                                SpreadsheetParserToken.betweenSymbol(":", ":"),
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("$B"),
                                                        "$B"
                                                ),
                                                SpreadsheetParserToken.rowReference(
                                                        SpreadsheetSelection.parseRow("$2"),
                                                        "$2"
                                                )
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
    public void testExpressionErrorDiv() {
        final String text = "#DIV/0!";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.error(
                        SpreadsheetErrorKind.DIV0.toError(),
                        text
                ),
                text
        );
    }

    @Test
    public void testExpressionErrorRef() {
        final String text = "#REF!";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.error(
                        SpreadsheetErrorKind.REF.toError(),
                        text
                ),
                text
        );
    }

    @Test
    public void testExpressionAdditionLeftErrorRef() {
        final SpreadsheetParserToken left = SpreadsheetParserToken.error(
                SpreadsheetErrorKind.REF.toError(),
                "#REF!"
        );
        final SpreadsheetParserToken right = number(456);
        final String text = "#REF!+456";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.addition(
                        Lists.of(
                                left,
                                plus(),
                                right
                        ),
                        text
                ),
                text
        );
    }

    // Negative........................................................................................................

    @Test
    public void testExpressionNegativeBigDecimal() {
        final String text = "-1";

        this.parseExpressionAndCheck(text,
                SpreadsheetParserToken.negative(Lists.of(minus(), number(1)), text),
                text);
    }

    @Test
    public void testExpressionNegativeWhitespaceBigDecimal() {
        final String text = "-  1";

        this.parseExpressionAndCheck(
                text,
                SpreadsheetParserToken.negative(
                        Lists.of(
                                minus(),
                                whitespace2(),
                                number(1)
                        ),
                        text
                ),
                text,
                "-1"
        );
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
    public void testExpressionNumberBigDecimal() {
        this.testExpressionNumber();
    }

    @Test
    public void testExpressionNumberDouble() {
        this.testExpressionNumber();
    }

    private void testExpressionNumber() {
        final String text = "1";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(1)
                ),
                text,
                text
        );
    }

    @Test
    public void testExpressionNumberWithDigitsBigDecimal() {
        this.testExpressionNumberWithDigits();
    }

    @Test
    public void testExpressionNumberWithDigitsDouble() {
        this.testExpressionNumberWithDigits();
    }

    private void testExpressionNumberWithDigits() {
        final String text = "1.75";

        this.parseExpressionAndCheck(
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
    public void testExpressionNumberPercentageBigDecimal() {
        this.testExpressionNumberPercentage();
    }

    @Test
    public void testExpressionNumberPercentageDouble() {
        this.testExpressionNumberPercentage();
    }

    private void testExpressionNumberPercentage() {
        final String text = "1%";

        this.parseExpressionAndCheck(
                text,
                number(
                        digits(1),
                        percent()
                ),
                "1%",
                "0.01" // expression without percent
        );
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

        final SpreadsheetParserToken percent = number(
                digits(1),
                percent()
        );

        this.parseExpressionAndCheck(
                text,
                negative(
                        percent
                ),
                text,
                "-0.01"
        );
    }

    @Test
    public void testExpressionGroupLabel() {
        final String labelText = "Hello";

        final String groupText = "(" + labelText + ")";
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(parenthesisOpen(), label(labelText), parenthesisClose()), groupText);

        this.parseExpressionAndCheck(groupText, group, groupText, labelText);
    }

    @Test
    public void testExpressionGroupWhitespaceLabelWhitespace() {
        final String labelText = "Hello";
        final String groupText = "(  " + labelText + "  )";

        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(
                Lists.of(
                        parenthesisOpen(),
                        whitespace2(),
                        label(labelText),
                        whitespace2(),
                        parenthesisClose()
                ),
                groupText
        );

        this.parseExpressionAndCheck(
                groupText,
                group,
                groupText,
                labelText
        );
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
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(parenthesisOpen(), negative, parenthesisClose()), groupText);

        this.parseExpressionAndCheck(
                groupText,
                group,
                groupText,
                groupText.replace("(", "").replace(")", "")
        );
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
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(parenthesisOpen(), number(123), parenthesisClose()), groupText);

        final String text = "-" + groupText;
        this.parseExpressionAndCheck(
                text,
                negative(group),
                text,
                text.replace("(", "").replace(")", "")
        );
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
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(
                Lists.of(
                        left,
                        whitespace2(),
                        minus(),
                        whitespace2(),
                        right
                ),
                text
        );

        final String text2 = text + "+789";
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(
                Lists.of(
                        sub,
                        plus(),
                        number(789)
                ),
                text2
        );

        this.parseExpressionAndCheck(
                text2,
                add2,
                text2,
                "123-456+789"
        );
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

        this.parseExpressionAndCheck(
                text,
                power,
                text,
                text.replace("^", "^^")
        );
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

        this.parseExpressionAndCheck(
                text2,
                multiply2,
                text2,
                text2.replace("^", "^^")
        );
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
        final String text = "123=456";
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
        final String text = "123=" + addText;
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
        final String text = "123<>456";
        final SpreadsheetNotEqualsParserToken ne = SpreadsheetParserToken.notEquals(Lists.of(left, notEquals(), right), text);

        this.parseExpressionAndCheck(
                text,
                ne,
                text,
                text.replace("<>", "!=")
        );
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
        final String text = "123<>" + addText;
        final SpreadsheetNotEqualsParserToken ne = SpreadsheetParserToken.notEquals(Lists.of(left, notEquals(), add), text);

        this.parseExpressionAndCheck(
                text,
                ne,
                text,
                text.replace("<>", "!=")
        );
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
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(parenthesisOpen(), negative(number(333)), parenthesisClose()), groupText);

        final String addText2 = add + "+" + groupText;
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(add, plus(), group), addText2);

        final String multiplyText = "444*555";
        final SpreadsheetMultiplicationParserToken multiply = SpreadsheetParserToken.multiplication(Lists.of(number(444), multiply(), number(555)), multiplyText);

        final String subText = addText2 + "-" + multiplyText;
        final SpreadsheetSubtractionParserToken sub = SpreadsheetParserToken.subtraction(Lists.of(add2, minus(), multiply), subText);

        this.parseExpressionAndCheck(
                subText,
                sub,
                subText,
                subText.replace("(", "").replace(")", "")
        );
    }

    // FunctionParameters...............................................................................................

    @Test
    public void testFunctionParametersEmpty() {
        this.parseFunctionParametersAndCheck(
                "()",
                SpreadsheetParserToken.functionParameters(
                        Lists.of(
                                parenthesisOpen(),
                                parenthesisClose()
                        ),
                        "()"
                )
        );
    }

    @Test
    public void testFunctionParametersEmptyOnlySpaces() {
        this.parseFunctionParametersAndCheck(
                "(  )",
                SpreadsheetParserToken.functionParameters(
                        Lists.of(
                                parenthesisOpen(),
                                whitespace2(),
                                parenthesisClose()
                        ),
                        "(  )"
                )
        );
    }

    @Test
    public void testFunctionParametersOneNumberParameter() {
        this.parseFunctionParametersAndCheck(
                "(1)",
                SpreadsheetParserToken.functionParameters(
                        Lists.of(
                                parenthesisOpen(),
                                number(1),
                                parenthesisClose()
                        ),
                        "(1)"
                )
        );
    }

    @Test
    public void testFunctionParametersOneStringParameter() {
        this.parseFunctionParametersAndCheck(
                "(\"abc\")",
                SpreadsheetParserToken.functionParameters(
                        Lists.of(
                                parenthesisOpen(),
                                SpreadsheetParserToken.text(
                                        Lists.of(
                                                doubleQuotes(),
                                                textLiteral("abc"),
                                                doubleQuotes()
                                        ),
                                        "\"abc\""
                                ),
                                parenthesisClose()
                        ),
                        "(\"abc\")"
                )
        );
    }

    @Test
    public void testFunctionParametersTwoNumberParameters() {
        this.parseFunctionParametersAndCheck(
                "(1;2)",
                SpreadsheetParserToken.functionParameters(
                        Lists.of(
                                parenthesisOpen(),
                                number(1),
                                valueSeparator(),
                                number(2),
                                parenthesisClose()
                        ),
                        "(1;2)"
                )
        );
    }

    @Test
    public void testFunctionParametersThreeNumberParameters() {
        this.parseFunctionParametersAndCheck(
                "(1;2;345)",
                SpreadsheetParserToken.functionParameters(
                        Lists.of(
                                parenthesisOpen(),
                                number(1),
                                valueSeparator(),
                                number(2),
                                valueSeparator(),
                                number(345),
                                parenthesisClose()
                        ),
                        "(1;2;345)"
                )
        );
    }

    private void parseFunctionParametersAndCheck(final String text,
                                                 final ParserToken token) {
        this.parseFunctionParametersAndCheck(
                text,
                token,
                ""
        );
    }

    private void parseFunctionParametersAndCheck(final String text,
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

    // LambdaFunction....................................................................................................

    @Test
    public void testLambdaFunctionOtherExpressionFails() {
        this.parseFailAndCheck(
                this.lambdaFunctionParser(),
                "1+2"
        );
    }

    @Test
    public void testLambdaFunctionNameWithNumberParameter() {
        final String text = "lambda(x;x+x)(1)";
        final SpreadsheetLabelNameParserToken x = this.label("x");

        final SpreadsheetLambdaFunctionParserToken lambda = lambdaFunction(
                functionName("lambda"),
                functionParameters(
                        parenthesisOpen(),
                        x,
                        valueSeparator(),
                        add(
                                x,
                                plus(),
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

        this.lambdaFunctionParseAndCheck(
                text,
                lambda,
                text
        );
    }

    @Test
    public void testLambdaFunctionNameWithStringParameter() {
        final String text = "lambda(x;x+x)(\"Hello\")";
        final SpreadsheetLabelNameParserToken x = this.label("x");

        final SpreadsheetLambdaFunctionParserToken lambda = lambdaFunction(
                functionName("lambda"),
                functionParameters(
                        parenthesisOpen(),
                        x,
                        valueSeparator(),
                        add(
                                x,
                                plus(),
                                x
                        ),
                        parenthesisClose()
                ),
                functionParameters(
                        parenthesisOpen(),
                        SpreadsheetParserToken.text(
                                Lists.of(
                                        doubleQuotes(),
                                        textLiteral("Hello"),
                                        doubleQuotes()
                                ),
                                "\"Hello\""
                        ),
                        parenthesisClose()
                )
        );

        this.lambdaFunctionParseAndCheck(
                text,
                lambda,
                text
        );
    }

    /**
     * First parseCellReference the range using {@link SpreadsheetParsers#lambdaFunction()} and then repeat again with
     * {@link SpreadsheetParsers#expression()}. Both should give the same results.
     */
    private void lambdaFunctionParseAndCheck(final String from,
                                             final SpreadsheetLambdaFunctionParserToken expected,
                                             final String text) {
        this.lambdaFunctionParseAndCheck(
                from,
                expected,
                text,
                text
        );
    }

    private void lambdaFunctionParseAndCheck(final String from,
                                             final SpreadsheetLambdaFunctionParserToken expected,
                                             final String text,
                                             final String expressionToString) {
        this.parseAndCheck(
                lambdaFunctionParser(),
                from,
                expected,
                text
        );
        this.parseExpressionAndCheck(
                from,
                expected,
                text,
                expressionToString
        );
    }

    private Parser<SpreadsheetParserContext> lambdaFunctionParser() {
        return SpreadsheetParsers.lambdaFunction();
    }

    // NamedFunction....................................................................................................

    @Test
    public void testNamedFunctionOtherExpressionFails() {
        this.parseFailAndCheck(this.namedFunctionParser(), "1+2");
    }

    @Test
    public void testNamedFunctionWithoutArguments() {
        final String text = "xyz()";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
    public void testNamedFunctionWithoutArgumentsWhitespace() {
        final String text = "xyz(  )";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
    public void testNamedFunctionWithOneArgumentBigDecimal() {
        this.testNamedFunctionWithOneArgument();
    }

    @Test
    public void testNamedFunctionWithOneArgumentDouble() {
        this.testNamedFunctionWithOneArgument();
    }

    private void testNamedFunctionWithOneArgument() {
        final String text = "xyz(123)";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
    public void testNamedFunctionWithOneArgument2BigDecimal() {
        this.testNamedFunctionWithOneArgument2();
    }

    @Test
    public void testNamedFunctionWithOneArgument2Double() {
        this.testNamedFunctionWithOneArgument2();
    }

    private void testNamedFunctionWithOneArgument2() {
        final String text = "xyz(  123)";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
    public void testNamedFunctionWithOneArgument3BigDecimal() {
        this.testNamedFunctionWithOneArgument3();
    }

    @Test
    public void testNamedFunctionWithOneArgument3Double() {
        this.testNamedFunctionWithOneArgument3();
    }

    private void testNamedFunctionWithOneArgument3() {
        final String text = "xyz(123  )";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
    public void testNamedFunctionWithOneArgument4BigDecimal() {
        this.testNamedFunctionWithOneArgument4();
    }

    @Test
    public void testNamedFunctionWithOneArgument4Double() {
        this.testNamedFunctionWithOneArgument4();
    }

    private void testNamedFunctionWithOneArgument4() {
        final String text = "xyz(  123  )";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
    public void testNamedFunctionWithTwoArgumentsBigDecimal() {
        this.testNamedFunctionWithTwoArguments();
    }

    @Test
    public void testNamedFunctionWithTwoArgumentsDouble() {
        this.testNamedFunctionWithTwoArguments();
    }

    private void testNamedFunctionWithTwoArguments() {
        final String text = "xyz(123;456)";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
                functionName("xyz"),
                functionParameters(
                        parenthesisOpen(),
                        number(123),
                        valueSeparator(),
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
    public void testNamedFunctionWithFourArgumentsBigDecimal() {
        this.testNamedFunctionWithFourArguments();
    }

    @Test
    public void testNamedFunctionWithFourArgumentsDouble() {
        this.testNamedFunctionWithFourArguments();
    }

    private void testNamedFunctionWithFourArguments() {
        final String text = "xyz(1;2;3;4)";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
                functionName("xyz"),
                functionParameters(
                        parenthesisOpen(),
                        number(1),
                        valueSeparator(),
                        number(2),
                        valueSeparator(),
                        number(3),
                        valueSeparator(),
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
    public void testNamedFunctionWithinFunctionBigDecimal() {
        this.testNamedFunctionWithinFunction();
    }

    @Test
    public void testNamedFunctionWithinFunctionDouble() {
        this.testNamedFunctionWithinFunction();
    }

    private void testNamedFunctionWithinFunction() {
        final String yText = "y(123)";
        final SpreadsheetNamedFunctionParserToken y = namedFunction(
                functionName("y"),
                functionParameters(
                        parenthesisOpen(),
                        number(123),
                        parenthesisClose()
                )
        );

        final String xText = "x(" + yText + ")";
        final SpreadsheetNamedFunctionParserToken x = namedFunction(
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
    public void testNamedFunctionWithinFunctionWithinFunctionBigDecimal() {
        this.testNamedFunctionWithinFunctionWithinFunction();
    }

    @Test
    public void testNamedFunctionWithinFunctionWithinFunctionDouble() {
        this.testNamedFunctionWithinFunctionWithinFunction();
    }

    private void testNamedFunctionWithinFunctionWithinFunction() {
        final String zText = "z(123)";
        final SpreadsheetNamedFunctionParserToken z = namedFunction(
                functionName("z"),
                functionParameters(
                        parenthesisOpen(),
                        number(123),
                        parenthesisClose()
                )
        );

        final String yText = "y(" + zText + ")";
        final SpreadsheetNamedFunctionParserToken y = namedFunction(

                functionName("y"),
                functionParameters(
                        parenthesisOpen(),
                        z,
                        parenthesisClose()
                )
        );

        final String xText = "x(" + yText + ")";
        final SpreadsheetNamedFunctionParserToken x = namedFunction(

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
    public void testNamedFunctionWithRangeArgumentBigDecimal() {
        this.testNamedFunctionWithRangeArgument();
    }

    @Test
    public void testNamedFunctionWithRangeArgumentDouble() {
        this.testNamedFunctionWithRangeArgument();
    }

    private void testNamedFunctionWithRangeArgument() {
        final SpreadsheetCellReferenceParserToken from = this.cell(0, "A", 0);
        final SpreadsheetCellReferenceParserToken to = this.cell(1, "B", 1);

        final SpreadsheetCellRangeParserToken range = range(from, to);
        final String rangeText = range.text();

        final String text = "xyz(" + rangeText + ")";
        final SpreadsheetNamedFunctionParserToken f = namedFunction(
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
     * First parseCellReference the range using {@link SpreadsheetParsers#namedFunction()} and then repeat again with
     * {@link SpreadsheetParsers#expression()}. Both should give the same results.
     */
    private void namedFunctionParseAndCheck(final String from,
                                            final SpreadsheetNamedFunctionParserToken expected,
                                            final String text) {
        this.namedFunctionParseAndCheck(
                from,
                expected,
                text,
                text
        );
    }

    private void namedFunctionParseAndCheck(final String from,
                                            final SpreadsheetNamedFunctionParserToken expected,
                                            final String text,
                                            final String expressionToString) {
        this.parseAndCheck(
                namedFunctionParser(),
                from,
                expected,
                text
        );
        this.parseExpressionAndCheck(
                from,
                expected,
                text,
                expressionToString
        );
    }

    private Parser<SpreadsheetParserContext> namedFunctionParser() {
        return SpreadsheetParsers.namedFunction();
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
        final SpreadsheetGroupParserToken group = SpreadsheetParserToken.group(Lists.of(this.parenthesisOpen(), add, this.parenthesisClose()), "(" + add.text() + ")");

        final SpreadsheetParserToken last = number(3);
        final SpreadsheetMultiplicationParserToken mul = SpreadsheetParserToken.multiplication(Lists.of(group, multiply(), last), group.text() + "*3");

        this.parseExpressionAndCheck(
                mul.text(),
                mul,
                mul.text(),
                "1+2*3"
        );
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
        final SpreadsheetGroupParserToken group1 = SpreadsheetParserToken.group(Lists.of(this.parenthesisOpen(), add1, this.parenthesisClose()), "(" + add1.text() + ")");

        final SpreadsheetParserToken left2 = number(3);
        final SpreadsheetParserToken right2 = number(4);
        final SpreadsheetAdditionParserToken add2 = SpreadsheetParserToken.addition(Lists.of(left2, plus(), right2), "3+4");
        final SpreadsheetGroupParserToken group2 = SpreadsheetParserToken.group(Lists.of(this.parenthesisOpen(), add2, this.parenthesisClose()), "(" + add2.text() + ")");

        final SpreadsheetMultiplicationParserToken mul = SpreadsheetParserToken.multiplication(Lists.of(group1, multiply(), group2), group1.text() + "*" + group2.text());

        final String text = mul.text();
        this.parseExpressionAndCheck(
                text,
                mul,
                text,
                text.replace("(", "").replace(")", "")
        );
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
        return "Invalid character " + CharSequences.quoteIfChars(c) + " at (" + column + "," + row + ")";
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
        this.parseExpressionEvaluateAndCheck("test.toDate(\"2000-01-03\")-test.toDate(\"1999-12-31\")", 3);// days!
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
        this.parseExpressionEvaluateAndCheck("test.toDateTime(\"2000-02-01T12:00:00\")-test.toDateTime(\"2000-01-31T06:00:00\")", 1.25); //1 1/4days
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
        this.parseExpressionEvaluateAndCheck("test.toTime(test.toTime(\"18:00:00\")-test.toTime(\"06:00:00\"))", "12:00"); //1/2 a day or 12noon
    }

    @Test
    public void testExpressionErrorDotType() {
        this.parseExpressionEvaluateAndCheck("Error.Type()", "Hello");
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
                valueOrExpressionParser(),
                "=" + text,
                message.substring(0, at + 4) + (column + 1) + message.substring(at2)
        );
    }

    private void parseExpressionAndCheck(final String formula,
                                         final ParserToken token,
                                         final String text) {
        this.parseExpressionAndCheck(
                formula,
                token,
                text,
                text
        );
    }

    private void parseExpressionAndCheck(final String formula,
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

        final SpreadsheetParserToken spreadsheetParserToken = token.cast(SpreadsheetParserToken.class);

        this.checkEquals(
                expressionToString.replace(';', ','),
                spreadsheetParserToken.toExpression(
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
                                        ConverterContexts.fake()
                                )
                        ).map(Object::toString)
                        .orElse("")
        );
    }

    private void parseExpressionEvaluateAndCheck(final String formulaText,
                                                 final Object expectedText) {
        this.parseExpressionEvaluateAndCheck(formulaText, String.valueOf(expectedText));
    }

    /**
     * Accepts a formula with an expression. Note the expression is assumed to NOT having the leading equals sign.
     * The second part of the test will prefix an equals sign and attempt to parse using the {@link SpreadsheetParsers#valueOrExpression} parser.
     */
    private void parseExpressionEvaluateAndCheck(final String formulaText,
                                                 final String expectedText) {
        this.parseExpressionEvaluateAndCheck0(
                expressionParser(),
                formulaText,
                expectedText
        );

        this.parseExpressionEvaluateAndCheck0(
                valueOrExpressionParser(),
                "=" + formulaText,
                expectedText
        );

        this.parseExpressionEvaluateAndCheck0(
                valueOrExpressionParser(),
                "= " + formulaText,
                expectedText
        );
    }

    private void parseExpressionEvaluateAndCheck0(final Parser<SpreadsheetParserContext> parser,
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
        final SpreadsheetParserToken formula = this.parse(parser, formulaText);
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
        return SpreadsheetParsers.expression();
    }

    private static Parser<SpreadsheetParserContext> functionParametersParser() {
        return SpreadsheetParsers.functionParameters();
    }

    private static Parser<SpreadsheetParserContext> valueOrExpressionParser() {
        return SpreadsheetParsers.valueOrExpression(
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
            fail("Parser left " + CharSequences.quoteAndEscape(leftOver) + " parse " + CharSequences.quoteAndEscape(parse));
        }
        return spreadsheetFormula.get()
                .cast(SpreadsheetParserToken.class);
    }

    private ExpressionEvaluationContext expressionEvaluationContext(final ExpressionNumberKind kind) {
        final Function<ConverterContext, ParserContext> parserContext = (c) -> ParserContexts.basic(c, c);

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
                        ExpressionNumber.toConverter(Converters.numberToNumber()),
                        ExpressionNumber.toConverter(Converters.localDateToNumber(Converters.JAVA_EPOCH_OFFSET)),
                        ExpressionNumber.toConverter(Converters.localDateTimeToNumber(Converters.JAVA_EPOCH_OFFSET)),
                        ExpressionNumber.toConverter(Converters.localTimeNumber()),
                        ExpressionNumber.fromConverter(Converters.numberToLocalTime()),
                        ExpressionNumber.toConverter(stringDouble),
                        stringLocalDate,
                        stringLocalDateTime,
                        stringLocalTime,
                        Converters.objectString()
                )
        );

        return new FakeExpressionEvaluationContext() {

            @Override
            public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                           final List<Object> parameters) {
                return function.apply(
                        this.prepareParameters(function, parameters),
                        Cast.to(this)
                );
            }

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
            public Optional<ExpressionFunction<?, ExpressionEvaluationContext>> expressionFunction(final FunctionExpressionName name) {
                switch (name.value()) {
                    case "Error.Type":
                        return Optional.of(
                                function((p, c) -> "Hello")
                        );
                    case "test.toDate":
                        return Optional.of(
                                function((p, c) -> convertStringParameter(p, LocalDate.class))
                        );
                    case "test.toDateTime":
                        return Optional.of(
                                function((p, c) -> convertStringParameter(p, LocalDateTime.class))
                        );
                    case "test.toTime":
                        return Optional.of(
                                function((p, c) -> convertStringParameter(p, LocalTime.class))
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
                        ExpressionNumberConverterContexts.basic(Converters.fake(),
                                ConverterContexts.basic(Converters.fake(),
                                        dateTimeContext(),
                                        decimalNumberContext()),
                                this.expressionNumberKind())
                );
            }

            @Override
            public boolean isText(final Object value) {
                return value instanceof Character || value instanceof CharSequence;
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

            @Override
            public char valueSeparator() {
                return VALUE_SEPARATOR;
            }
        };
    }

    private SpreadsheetParserToken add(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.addition(
                Lists.of(
                        tokens
                ),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
    }

    private SpreadsheetParserToken between() {
        return SpreadsheetParserToken.betweenSymbol(":", ":");
    }

    private SpreadsheetCellReferenceParserToken cell(final int column,
                                                     final String columnText,
                                                     final int row) {
        final SpreadsheetParserToken columnToken = SpreadsheetParserToken.columnReference(
                SpreadsheetColumnOrRowReference.column(
                        column,
                        SpreadsheetReferenceKind.RELATIVE
                ),
                columnText
        );
        final SpreadsheetParserToken rowToken = SpreadsheetParserToken.rowReference(
                SpreadsheetColumnOrRowReference.row(
                        row,
                        SpreadsheetReferenceKind.RELATIVE
                ),
                String.valueOf(1 + row)
        );
        return SpreadsheetParserToken.cellReference(
                Lists.of(
                        columnToken,
                        rowToken
                ),
                columnToken.text() + rowToken.text()
        );
    }

    private SpreadsheetParserToken decimalSymbols() {
        return SpreadsheetParserToken.decimalSeparatorSymbol(".", ".");
    }

    private SpreadsheetParserToken digits(final Number number) {
        return SpreadsheetParserToken.digits(
                "" + number,
                "" + number
        );
    }

    private SpreadsheetParserToken divide() {
        return SpreadsheetParserToken.divideSymbol("/", "/");
    }

    private SpreadsheetParserToken doubleQuotes() {
        return SpreadsheetParserToken.doubleQuoteSymbol("\"", "\"");
    }

    private SpreadsheetParserToken equals() {
        return SpreadsheetParserToken.equalsSymbol("=", "=");
    }

    private SpreadsheetParserToken functionName(final String name) {
        return SpreadsheetParserToken.functionName(
                SpreadsheetFunctionName.with(name),
                name
        );
    }

    private SpreadsheetFunctionParametersParserToken functionParameters(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.functionParameters(
                Lists.of(
                        tokens
                ),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
    }

    private SpreadsheetParserToken greaterThan() {
        return SpreadsheetParserToken.greaterThanSymbol(">", ">");
    }

    private SpreadsheetParserToken greaterThanEquals() {
        return SpreadsheetParserToken.greaterThanEqualsSymbol(">=", ">=");
    }

    private SpreadsheetLabelNameParserToken label(final String label) {
        return SpreadsheetParserToken.labelName(SpreadsheetSelection.labelName(label), label);
    }

    private SpreadsheetLambdaFunctionParserToken lambdaFunction(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.lambdaFunction(
                Lists.of(
                        tokens
                ),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
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

    private SpreadsheetNamedFunctionParserToken namedFunction(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.namedFunction(
                Lists.of(
                        tokens
                ),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
    }

    private SpreadsheetParserToken negative(final SpreadsheetParserToken number) {
        return SpreadsheetParserToken.negative(
                Lists.of(
                        minus(),
                        number
                ),
                "-" + number.text()
        );
    }

    private SpreadsheetParserToken notEquals() {
        return SpreadsheetParserToken.notEqualsSymbol("<>", "<>");
    }

    private SpreadsheetNumberParserToken number(final Number number) {
        return number(
                digits(number)
        );
    }

    private SpreadsheetNumberParserToken number(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.number(
                Lists.of(tokens),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
    }

    private SpreadsheetParserToken parenthesisOpen() {
        return SpreadsheetParserToken.parenthesisOpenSymbol("(", "(");
    }

    private SpreadsheetParserToken parenthesisClose() {
        return SpreadsheetParserToken.parenthesisCloseSymbol(")", ")");
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

    private SpreadsheetCellRangeParserToken range(final SpreadsheetParserToken from,
                                                  final SpreadsheetParserToken to) {
        final String text = from.text() + between() + to.text();
        return SpreadsheetParserToken.cellRange(
                Lists.of(
                        from,
                        between(),
                        to
                ),
                text
        );
    }

    private SpreadsheetParserToken valueSeparator() {
        return SpreadsheetParserToken.valueSeparatorSymbol(
                "" + VALUE_SEPARATOR,
                "" + VALUE_SEPARATOR
        );
    }

    private SpreadsheetParserToken whitespace1() {
        return whitespace(" ");
    }

    private SpreadsheetParserToken whitespace2() {
        return whitespace("  ");
    }

    private SpreadsheetParserToken whitespace(final String text) {
        return SpreadsheetParserToken.whitespace(
                text,
                text
        );
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
