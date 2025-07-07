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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class DateTimeSpreadsheetFormulaParserTokenTest extends ValueSpreadsheetFormulaParserTokenTestCase<DateTimeSpreadsheetFormulaParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken("1/2/2003 12:58:59.12345678");
    }

    @Test
    public void testToExpressionDayNumberMonthNumberYearHourMinuteSecondMillis() {
        this.toExpressionAndCheck2(
            dateTime(),
            dayNumber(),
            slashTextLiteral(),
            monthNumber(),
            slashTextLiteral(),
            year(),
            whitespace(),
            hour(),
            colonTextLiteral(),
            minute(),
            colonTextLiteral(),
            seconds(),
            decimalSeparator(),
            millisecond()
        );
    }

    @Test
    public void testToExpressionDayNumberMonthNumberYear() {
        this.toExpressionAndCheck2(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthNumber(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToExpressionYearMonthNumberDayNumber() {
        this.toExpressionAndCheck2(
            date(),
            year(),
            slashTextLiteral(),
            monthNumber(),
            slashTextLiteral(),
            dayNumber()
        );
    }

    @Test
    public void testToExpressionDayNumberMonthNameYear() {
        this.toExpressionAndCheck2(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthName(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToExpressionDayNumberMonthNameAbbreviationYear() {
        this.toExpressionAndCheck2(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthNameAbbreviation(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToExpressionDayNumberMonthNameInitialYear() {
        this.toExpressionAndCheck2(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthNameInitial(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToExpressionDayNumberMonthNumber() {
        this.toExpressionAndCheck2(
            LocalDate.of(DEFAULT_YEAR, MONTH, DAY),
            dayNumber(),
            slashTextLiteral(),
            monthNumber()
        );
    }

    @Test
    public void testToExpressionDayNumberYear() {
        this.toExpressionAndCheck2(
            LocalDate.of(YEAR, 1, DAY),
            dayNumber(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToExpressionDayNumber() {
        this.toExpressionAndCheck2(
            LocalDate.of(DEFAULT_YEAR, MONTH, DAY),
            dayNumber(),
            slashTextLiteral(),
            monthName()
        );
    }

    @Test
    public void testToExpressionMonthNumberYear() {
        this.toExpressionAndCheck2(
            LocalDate.of(YEAR, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToExpressionHourMinuteSecondsMilli() {
        this.toExpressionAndCheck2(
            time(),
            hour(),
            colonTextLiteral(),
            minute(),
            colonTextLiteral(),
            seconds(),
            decimalSeparator(),
            millisecond()
        );
    }

    @Test
    public void testToExpressionHourMinuteSeconds() {
        this.toExpressionAndCheck2(
            LocalTime.of(HOUR, MINUTE, SECONDS),
            hour(),
            colonTextLiteral(),
            minute(),
            colonTextLiteral(),
            seconds()
        );
    }

    @Test
    public void testToExpressionHourMinute() {
        this.toExpressionAndCheck2(
            LocalTime.of(HOUR, MINUTE, 0),
            hour(),
            colonTextLiteral(),
            minute()
        );
    }

    @Test
    public void testToExpressionHourMinutePm() {
        this.toExpressionAndCheck2(
            LocalTime.of(23, MINUTE, 0),
            SpreadsheetFormulaParserToken.hour(11, "11"),
            colonTextLiteral(),
            minute(),
            SpreadsheetFormulaParserToken.amPm(12, "PM")
        );
    }

    @Test
    public void testToExpressionHourSeconds() {
        this.toExpressionAndCheck2(
            LocalTime.of(HOUR, 0, SECONDS),
            hour(),
            colonTextLiteral(),
            seconds()
        );
    }

    @Test
    public void testToExpressionHourSecondsMillis() {
        this.toExpressionAndCheck2(
            LocalTime.of(HOUR, 0, SECONDS, MILLISECOND),
            hour(),
            colonTextLiteral(),
            seconds(),
            decimalSeparator(),
            millisecond()
        );
    }

    @Test
    public void testToExpressionMonthNumberYearBeforeTwoDigitYearBefore() {
        this.toExpressionAndCheck2(
            LocalDate.of(2010, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(10, "10")
        );
    }

    @Test
    public void testToExpressionMonthNumberYearBeforeTwoDigitYearEqual() {
        this.toExpressionAndCheck2(
            LocalDate.of(1920, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(20, "20")
        );
    }

    @Test
    public void testToExpressionMonthNumberYearBeforeTwoDigitYearAfter() {
        this.toExpressionAndCheck2(
            LocalDate.of(1950, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(50, "50")
        );
    }

    @Test
    public void testToExpressionMonthNumberYearBeforeTwoDigitYearBefore2() {
        this.toExpressionAndCheck2(
            this.expressionEvaluationContext(DEFAULT_YEAR, 50),
            LocalDate.of(2040, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(40, "40")
        );
    }

    private void toExpressionAndCheck2(final LocalDate expected,
                                       final SpreadsheetFormulaParserToken... tokens) {
        this.toExpressionAndCheck2(
            this.expressionEvaluationContext(DEFAULT_YEAR, 20),
            LocalDateTime.of(
                expected,
                LocalTime.of(0, 0)
            ),
            tokens
        );
    }

    private void toExpressionAndCheck2(final ExpressionEvaluationContext context,
                                       final LocalDate expected,
                                       final SpreadsheetFormulaParserToken... tokens) {
        this.toExpressionAndCheck2(
            context,
            LocalDateTime.of(
                expected,
                LocalTime.of(0, 0)
            ),
            tokens
        );
    }

    private void toExpressionAndCheck2(final LocalTime expected,
                                       final SpreadsheetFormulaParserToken... tokens) {
        this.toExpressionAndCheck2(
            LocalDateTime.of(
                LocalDate.of(DEFAULT_YEAR, 1, 1),
                expected
            ),
            tokens
        );
    }

    private void toExpressionAndCheck2(final LocalDateTime expected,
                                       final SpreadsheetFormulaParserToken... tokens) {
        this.toExpressionAndCheck2(
            this.expressionEvaluationContext(DEFAULT_YEAR, 20),
            expected,
            tokens
        );
    }

    private void toExpressionAndCheck2(final ExpressionEvaluationContext context,
                                       final LocalDateTime expected,
                                       final SpreadsheetFormulaParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        final DateTimeSpreadsheetFormulaParserToken dateTimeParserToken = DateTimeSpreadsheetFormulaParserToken.with(
            tokensList,
            ParserToken.text(tokensList)
        );

        this.checkEquals(
            expected,
            dateTimeParserToken.toLocalDateTime(context),
            () -> "toLocalDateTime() " + dateTimeParserToken
        );

        this.toExpressionAndCheck(
            dateTimeParserToken,
            context,
            Expression.value(expected)
        );
    }

    @Override
    DateTimeSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.dateTime(tokens, text);
    }

    @Override
    public String text() {
        return "" + DAY + "/" + MONTH + "/" + YEAR + " " + HOUR + ":" + MINUTE + ":" + SECONDS + "." + MILLISECOND;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(
            this.dayNumber(),
            this.slashTextLiteral(),
            this.monthNumber(),
            this.slashTextLiteral(),
            this.year(),
            this.whitespace(),
            this.hour(),
            this.colonTextLiteral(),
            this.minute(),
            this.colonTextLiteral(),
            this.seconds(),
            this.decimalSeparator(),
            this.millisecond()
        );
    }

    private LocalDate date() {
        return LocalDate.of(YEAR, MONTH, DAY);
    }

    private LocalTime time() {
        return LocalTime.of(HOUR, MINUTE, SECONDS, MILLISECOND);
    }

    private LocalDateTime dateTime() {
        return LocalDateTime.of(
            date(),
            time()
        );
    }

    @Override
    public DateTimeSpreadsheetFormulaParserToken createDifferentToken() {
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
    public Class<DateTimeSpreadsheetFormulaParserToken> type() {
        return DateTimeSpreadsheetFormulaParserToken.class;
    }

    @Override
    public DateTimeSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallDateTime(from, context);
    }
}
