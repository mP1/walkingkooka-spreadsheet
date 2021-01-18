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
import java.util.List;

public final class SpreadsheetDateParserTokenTest extends SpreadsheetParentParserTokenTestCase<SpreadsheetDateParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken("1/2/2003");
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

    private void toExpressionAndCheck2(final LocalDate expected,
                                       final SpreadsheetParserToken...tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        this.toExpressionAndCheck(
                SpreadsheetDateParserToken.with(
                        tokensList,
                        ParserToken.text(tokensList)
                ),
                Expression.localDate(expected));
    }

    @Override
    SpreadsheetDateParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.date(tokens, text);
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

    private LocalDate date() {
        return LocalDate.of(YEAR, MONTH, DAY);
    }

    @Override
    public SpreadsheetDateParserToken createDifferentToken() {
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
    public Class<SpreadsheetDateParserToken> type() {
        return SpreadsheetDateParserToken.class;
    }

    @Override
    public SpreadsheetDateParserToken unmarshall(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallDate(from, context);
    }
}
