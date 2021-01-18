/*
 * Copyclose 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class SpreadsheetDateTimeParserTokenTest extends SpreadsheetParentParserTokenTestCase<SpreadsheetDateTimeParserToken> {

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
                LocalDate.of(0, MONTH, DAY),
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
                SpreadsheetParserToken.hour(11, "11"),
                colonTextLiteral(),
                minute(),
                SpreadsheetParserToken.amPm(12, "PM")
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

    private void toExpressionAndCheck2(final LocalDate expected,
                                       final SpreadsheetParserToken... tokens) {
        this.toExpressionAndCheck2(
                LocalDateTime.of(
                        expected,
                        LocalTime.of(0, 0)
                ),
                tokens
        );
    }

    private void toExpressionAndCheck2(final LocalTime expected,
                                       final SpreadsheetParserToken... tokens) {
        this.toExpressionAndCheck2(
                LocalDateTime.of(
                        LocalDate.of(0, 1, 1),
                        expected
                ),
                tokens
        );
    }

    private void toExpressionAndCheck2(final LocalDateTime expected,
                                       final SpreadsheetParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        this.toExpressionAndCheck(
                SpreadsheetDateTimeParserToken.with(
                        tokensList,
                        ParserToken.text(tokensList)
                ),
                Expression.localDateTime(expected)
        );
    }

    @Override
    SpreadsheetDateTimeParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.dateTime(tokens, text);
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
    public SpreadsheetDateTimeParserToken createDifferentToken() {
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
    public Class<SpreadsheetDateTimeParserToken> type() {
        return SpreadsheetDateTimeParserToken.class;
    }

    @Override
    public SpreadsheetDateTimeParserToken unmarshall(final JsonNode from,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallDateTime(from, context);
    }
}
