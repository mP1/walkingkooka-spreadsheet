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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalTime;
import java.util.List;

public final class TimeSpreadsheetParserTokenTest extends ValueSpreadsheetParserTokenTestCase<TimeSpreadsheetParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken("12:58:59");
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

    private void toExpressionAndCheck2(final LocalTime expected,
                                       final SpreadsheetParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        final TimeSpreadsheetParserToken timeParserToken = TimeSpreadsheetParserToken.with(
                tokensList,
                ParserToken.text(tokensList)
        );

        this.checkEquals(
                expected,
                timeParserToken.toLocalTime(),
                () -> "toLocalTime() " + timeParserToken
        );

        this.toExpressionAndCheck(
                timeParserToken,
                Expression.value(expected)
        );
    }

    @Override
    TimeSpreadsheetParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.time(tokens, text);
    }

    @Override
    public String text() {
        return "" + HOUR + ":" + MINUTE + ":" + SECONDS + "." + MILLISECOND;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(
                this.hour(),
                this.colonTextLiteral(),
                this.minute(),
                this.colonTextLiteral(),
                this.seconds(),
                this.decimalSeparator(),
                this.millisecond()
        );
    }

    private LocalTime time() {
        return LocalTime.of(HOUR, MINUTE, SECONDS, MILLISECOND);
    }

    @Override
    public TimeSpreadsheetParserToken createDifferentToken() {
        final String different = "" + SECONDS + ":" + MINUTE + ":" + HOUR;

        return this.createToken(
                different,
                seconds(),
                colonTextLiteral(),
                minute(),
                colonTextLiteral(),
                hour()
        );
    }

    @Override
    public Class<TimeSpreadsheetParserToken> type() {
        return TimeSpreadsheetParserToken.class;
    }

    @Override
    public TimeSpreadsheetParserToken unmarshall(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallTime(from, context);
    }
}
