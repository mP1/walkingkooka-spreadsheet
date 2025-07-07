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
import java.util.List;

public final class DateSpreadsheetFormulaParserTokenTest extends ValueSpreadsheetFormulaParserTokenTestCase<DateSpreadsheetFormulaParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken("1/2/2003");
    }

    @Test
    public void testToLocalDateDayNumberMonthNumberYear() {
        this.testToLocalDateAndCheck(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthNumber(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToLocalDateYearMonthNumberDayNumber() {
        this.testToLocalDateAndCheck(
            date(),
            year(),
            slashTextLiteral(),
            monthNumber(),
            slashTextLiteral(),
            dayNumber()
        );
    }

    @Test
    public void testToLocalDateDayNumberMonthNameYear() {
        this.testToLocalDateAndCheck(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthName(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToLocalDateDayNumberMonthNameAbbreviationYear() {
        this.testToLocalDateAndCheck(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthNameAbbreviation(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToLocalDateDayNumberMonthNameInitialYear() {
        this.testToLocalDateAndCheck(
            date(),
            dayNumber(),
            slashTextLiteral(),
            monthNameInitial(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToLocalDateDayNumberMonthNumber() {
        this.testToLocalDateAndCheck(
            LocalDate.of(DEFAULT_YEAR, MONTH, DAY),
            dayNumber(),
            slashTextLiteral(),
            monthNumber()
        );
    }

    @Test
    public void testToLocalDateDayNumberYear() {
        this.testToLocalDateAndCheck(
            LocalDate.of(YEAR, 1, DAY),
            dayNumber(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToLocalDateMonthNumberYear() {
        this.testToLocalDateAndCheck(
            LocalDate.of(YEAR, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            year()
        );
    }

    @Test
    public void testToLocalDateMonthNumberYearBeforeTwoDigitYearBefore() {
        this.testToLocalDateAndCheck(
            LocalDate.of(2010, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(10, "10")
        );
    }

    @Test
    public void testToLocalDateMonthNumberYearBeforeTwoDigitYearEqual() {
        this.testToLocalDateAndCheck(
            LocalDate.of(1920, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(20, "20")
        );
    }

    @Test
    public void testToLocalDateMonthNumberYearBeforeTwoDigitYearAfter() {
        this.testToLocalDateAndCheck(
            LocalDate.of(1950, MONTH, 1),
            monthNumber(),
            slashTextLiteral(),
            SpreadsheetFormulaParserToken.year(50, "50")
        );
    }

    private void testToLocalDateAndCheck(final LocalDate expected,
                                         final SpreadsheetFormulaParserToken... tokens) {
        this.testToLocalDateAndCheck(
            this.expressionEvaluationContext(DEFAULT_YEAR, 20),
            expected,
            tokens
        );
    }

    private void testToLocalDateAndCheck(final ExpressionEvaluationContext context,
                                         final LocalDate expected,
                                         final SpreadsheetFormulaParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        final DateSpreadsheetFormulaParserToken dateParserToken = DateSpreadsheetFormulaParserToken.with(
            tokensList,
            ParserToken.text(tokensList)
        );

        this.checkEquals(
            expected,
            dateParserToken.toLocalDate(context),
            () -> "toLocalDate() " + dateParserToken
        );

        this.toExpressionAndCheck(
            dateParserToken,
            Expression.value(expected)
        );
    }

    @Override
    DateSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.date(tokens, text);
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
    public DateSpreadsheetFormulaParserToken createDifferentToken() {
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
    public Class<DateSpreadsheetFormulaParserToken> type() {
        return DateSpreadsheetFormulaParserToken.class;
    }

    @Override
    public DateSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallDate(from, context);
    }
}
