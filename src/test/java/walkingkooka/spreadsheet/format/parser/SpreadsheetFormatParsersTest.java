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

package walkingkooka.spreadsheet.format.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.text.cursor.parser.ParserToken;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetFormatParsersTest implements ParserTesting<Parser<SpreadsheetFormatParserContext>,
        SpreadsheetFormatParserContext> {

    // color............................................................................................................

    @Test
    public void testColorDigitNonZeroFails() {
        this.colorThrows(digitNonZero());
    }

    @Test
    public void testColorDigitLeadingZeroFails() {
        this.colorThrows(digitLeadingZero());
    }

    @Test
    public void testColorDigitLeadingSpaceFails() {
        this.colorThrows(digitLeadingSpace());
    }

    @Test
    public void testColorHourFails() {
        this.colorThrows(hour());
    }

    @Test
    public void testColorMonthOrMinuteFails() {
        this.colorThrows(monthOrMinute());
    }

    @Test
    public void testColorSecondFails() {
        this.colorThrows(second());
    }

    @Test
    public void testColorDayFails() {
        this.colorThrows(day());
    }

    @Test
    public void testColorYearFails() {
        this.colorThrows(year());
    }

    @Test
    public void testColorName() {
        this.colorParseAndCheck(openSquareBracket(), red(), closeSquareBracket());
    }

    @Test
    public void testColorNameWhitespace() {
        this.colorParseAndCheck(openSquareBracket(), red(), whitespace(), closeSquareBracket());
    }

    @Test
    public void testColorNumber() {
        this.colorParseAndCheck(openSquareBracket(), colorLiteral(), whitespace(), colorNumberFive(), closeSquareBracket());
    }

    @Test
    public void testColorNumberWhitespace() {
        this.colorParseAndCheck(openSquareBracket(), colorLiteral(), whitespace(), colorNumberFive(), whitespace(), closeSquareBracket());
    }

    private void colorParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.colorParser(), SpreadsheetFormatParserToken::color, tokens);
    }

    private void colorThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.colorParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> colorParser() {
        return SpreadsheetFormatParsers.color();
    }

    // condition........................................................................................................

    @Test
    public void testConditionCloseParensFails() {
        this.conditionParseThrows(textLiteralCloseParens());
    }

    @Test
    public void testConditionColonFails() {
        this.conditionParseThrows(textLiteralColon());
    }

    @Test
    public void testConditionDayFails() {
        this.conditionParseThrows(day());
    }

    @Test
    public void testConditionDigitNonZeroFails() {
        this.conditionParseThrows(digitNonZero());
    }

    @Test
    public void testConditionDigitLeadingZeroFails() {
        this.conditionParseThrows(digitLeadingZero());
    }

    @Test
    public void testConditionDigitLeadingSpaceFails() {
        this.conditionParseThrows(digitLeadingSpace());
    }

    @Test
    public void testConditionDollarFails() {
        this.conditionParseThrows(textLiteralDollar());
    }

    @Test
    public void testConditionFractionFails() {
        this.conditionParseThrows(fraction());
    }

    @Test
    public void testConditionHourFails() {
        this.conditionParseThrows(hour());
    }

    @Test
    public void testConditionMinusFails() {
        this.conditionParseThrows(textLiteralMinus());
    }

    @Test
    public void testConditionMonthOrMinuteFails() {
        this.conditionParseThrows(monthOrMinute());
    }

    @Test
    public void testConditionOpenParensFails() {
        this.conditionParseThrows(textLiteralOpenParens());
    }

    @Test
    public void testConditionPlusFails() {
        this.conditionParseThrows(textLiteralPlus());
    }

    @Test
    public void testConditionSecondFails() {
        this.conditionParseThrows(second());
    }

    @Test
    public void testConditionSlashFails() {
        this.conditionParseThrows(textLiteralSlash());
    }

    @Test
    public void testConditionSpaceFails() {
        this.conditionParseThrows(textLiteralSpace());
    }

    @Test
    public void testConditionYearFails() {
        this.conditionParseThrows(year());
    }

    @Test
    public void testConditionTextPlaceholderFails() {
        this.conditionParseThrows(textPlaceholder());
    }

    @Test
    public void testConditionOpenSquareBracketFails() {
        this.conditionParseThrows(openSquareBracket());
    }

    @Test
    public void testConditionOpenSquareBracketEqualsFails() {
        this.conditionParseThrows(openSquareBracket(), equals());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanFails() {
        this.conditionParseThrows(openSquareBracket(), greaterThan());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanEqualsFails() {
        this.conditionParseThrows(openSquareBracket(), greaterThanEquals());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanFails() {
        this.conditionParseThrows(openSquareBracket(), lessThan());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanEqualsFails() {
        this.conditionParseThrows(openSquareBracket(), lessThanEquals());
    }

    @Test
    public void testConditionOpenSquareBracketNotEqualsFails() {
        this.conditionParseThrows(openSquareBracket(), notEquals());
    }

    @Test
    public void testConditionOpenSquareBracketEqualsNumberFails() {
        this.conditionParseThrows(openSquareBracket(), equals(), bigDecimal());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanNumberFails() {
        this.conditionParseThrows(openSquareBracket(), greaterThan(), bigDecimal());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanEqualsNumberFails() {
        this.conditionParseThrows(openSquareBracket(), greaterThanEquals(), bigDecimal());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanNumberFails() {
        this.conditionParseThrows(openSquareBracket(), lessThan(), bigDecimal());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanEqualsNumberFails() {
        this.conditionParseThrows(openSquareBracket(), lessThanEquals(), bigDecimal());
    }

    @Test
    public void testConditionOpenSquareBracketNotEqualsNumberFails() {
        this.conditionParseThrows(openSquareBracket(), notEquals(), bigDecimal());
    }

    @Test
    public void testConditionOpenSquareBracketEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::equalsParserToken,
                openSquareBracket(), equals(), bigDecimal(), closeSquareBracket());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::greaterThan,
                openSquareBracket(), greaterThan(), bigDecimal(), closeSquareBracket());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::greaterThanEquals,
                openSquareBracket(), greaterThanEquals(), bigDecimal(), closeSquareBracket());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::lessThan,
                openSquareBracket(), lessThan(), bigDecimal(), closeSquareBracket());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::lessThanEquals,
                openSquareBracket(), lessThanEquals(), bigDecimal(), closeSquareBracket());
    }

    @Test
    public void testConditionOpenSquareBracketNotEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::notEquals,
                openSquareBracket(), notEquals(), bigDecimal(), closeSquareBracket());
    }

    private void conditionParseAndCheck(final BiFunction<List<ParserToken>, String, SpreadsheetFormatParserToken> factory,
                                        final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.conditionParser(), factory, tokens);
    }

    private void conditionParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.conditionParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> conditionParser() {
        return SpreadsheetFormatParsers.condition();
    }

    // date........................................................................................................

    @Test
    public void testDateTextDigitNonZeroFails() {
        this.dateParseThrows(digitNonZero());
    }

    @Test
    public void testDateTextDigitLeadingZeroFails() {
        this.dateParseThrows(digitLeadingZero());
    }

    @Test
    public void testDateTextDigitLeadingSpaceFails() {
        this.dateParseThrows(digitLeadingSpace());
    }

    @Test
    public void testDateTextThousandsFails() {
        this.dateParseThrows(thousands());
    }

    @Test
    public void testDateHourFails() {
        this.dateParseThrows(hour());
    }

    @Test
    public void testDateSecondFails() {
        this.dateParseThrows(second());
    }

    @Test
    public void testDateTextPlaceholderFails() {
        this.dateParseThrows(textPlaceholder());
    }

    @Test
    public void testDateGeneral() {
        this.parseAndCheck2(this.dateParser(), SpreadsheetFormatParserToken::general, general());
    }

    @Test
    public void testDateEscaped() {
        this.dateParseAndCheck(escaped());
    }

    @Test
    public void testDateDollar() {
        this.dateParseAndCheck(textLiteralDollar());
    }

    @Test
    public void testDateMinus() {
        this.dateParseAndCheck(textLiteralMinus());
    }

    @Test
    public void testDatePlus() {
        this.dateParseAndCheck(textLiteralPlus());
    }

    @Test
    public void testDateSlash() {
        this.dateParseAndCheck(textLiteralSlash());
    }

    @Test
    public void testDateOpenParen() {
        this.dateParseAndCheck(textLiteralOpenParens());
    }

    @Test
    public void testDateCloseParen() {
        this.dateParseAndCheck(textLiteralCloseParens());
    }

    @Test
    public void testDateColon() {
        this.dateParseAndCheck(textLiteralColon());
    }

    @Test
    public void testDateSpace() {
        this.dateParseAndCheck(textLiteralSpace());
    }

    @Test
    public void testDateQuotedText() {
        this.dateParseAndCheck(quotedText());
    }

    @Test
    public void testDateDay() {
        this.dateParseAndCheck(day());
    }

    @Test
    public void testDateMonth() {
        this.dateParseAndCheck(monthOrMinute());
    }

    @Test
    public void testDateDayMonthYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDay2Month2Year2() {
        this.dateParseAndCheck(day(2), monthOrMinute(2), year(2));
    }

    @Test
    public void testDateDay3Month3Year3() {
        this.dateParseAndCheck(day(3), monthOrMinute(3), year(3));
    }

    @Test
    public void testDateDayMonthYearDateDayMonthYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateMonthDayYear() {
        this.dateParseAndCheck(monthOrMinute(), day(), year());
    }

    @Test
    public void testDateYearMonthDay() {
        this.dateParseAndCheck(year(), monthOrMinute(), day());
    }

    // escaped

    @Test
    public void testDateEscapedDayMonthYear() {
        this.dateParseAndCheck(escaped(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayEscapedMonthYear() {
        this.dateParseAndCheck(day(), escaped(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthEscapedYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), escaped(), year());
    }

    @Test
    public void testDateDayMonthYearEscaped() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), escaped());
    }

    // quotedText

    @Test
    public void testDateQuotedTextDayMonthYear() {
        this.dateParseAndCheck(quotedText(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayQuotedTextMonthYear() {
        this.dateParseAndCheck(day(), quotedText(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthQuotedTextYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), quotedText(), year());
    }

    @Test
    public void testDateDayMonthYearQuotedText() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), quotedText());
    }

    // closeParens

    @Test
    public void testDateCloseParensDayMonthYear() {
        this.dateParseAndCheck(textLiteralCloseParens(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayCloseParensMonthYear() {
        this.dateParseAndCheck(day(), textLiteralCloseParens(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthCloseParensYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralCloseParens(), year());
    }

    @Test
    public void testDateDayMonthYearCloseParens() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testDateColonDayMonthYear() {
        this.dateParseAndCheck(textLiteralColon(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayColonMonthYear() {
        this.dateParseAndCheck(day(), textLiteralColon(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthColonYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralColon(), year());
    }

    @Test
    public void testDateDayMonthYearColon() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralColon());
    }

    // dollar

    @Test
    public void testDateDollarDayMonthYear() {
        this.dateParseAndCheck(textLiteralDollar(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayDollarMonthYear() {
        this.dateParseAndCheck(day(), textLiteralDollar(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthDollarYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralDollar(), year());
    }

    @Test
    public void testDateDayMonthYearDollar() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralDollar());
    }

    // minus

    @Test
    public void testDateMinusDayMonthYear() {
        this.dateParseAndCheck(textLiteralMinus(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMinusMonthYear() {
        this.dateParseAndCheck(day(), textLiteralMinus(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthMinusYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralMinus(), year());
    }

    @Test
    public void testDateDayMonthYearMinus() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testDateOpenParensDayMonthYear() {
        this.dateParseAndCheck(textLiteralOpenParens(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayOpenParensMonthYear() {
        this.dateParseAndCheck(day(), textLiteralOpenParens(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthOpenParensYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralOpenParens(), year());
    }

    @Test
    public void testDateDayMonthYearOpenParens() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralOpenParens());
    }

    // plus

    @Test
    public void testDatePlusDayMonthYear() {
        this.dateParseAndCheck(textLiteralPlus(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayPlusMonthYear() {
        this.dateParseAndCheck(day(), textLiteralPlus(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthPlusYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralPlus(), year());
    }

    @Test
    public void testDateDayMonthYearPlus() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralPlus());
    }

    // slash

    @Test
    public void testDateSlashDayMonthYear() {
        this.dateParseAndCheck(textLiteralSlash(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDaySlashMonthYear() {
        this.dateParseAndCheck(day(), textLiteralSlash(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthSlashYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralSlash(), year());
    }

    @Test
    public void testDateDayMonthYearSlash() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralSlash());
    }

    // space

    @Test
    public void testDateSpaceDayMonthYear() {
        this.dateParseAndCheck(textLiteralSpace(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDaySpaceMonthYear() {
        this.dateParseAndCheck(day(), textLiteralSpace(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthSpaceYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), textLiteralSpace(), year());
    }

    @Test
    public void testDateDayMonthYearSpace() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), textLiteralSpace());
    }

    // equals

    @Test
    public void testDateEqualsDayMonthYearFails() {
        this.dateParseThrows(equals(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayEqualsMonthYearFails() {
        this.dateParseThrows(day(), equals(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthEqualsYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), equals(), year());
    }

    @Test
    public void testDateDayMonthYearEqualsFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), equals());
    }

    // greaterThan

    @Test
    public void testDateGreaterThanDayMonthYearFails() {
        this.dateParseThrows(greaterThan(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayGreaterThanMonthYearFails() {
        this.dateParseThrows(day(), greaterThan(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthGreaterThanYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), greaterThan(), year());
    }

    @Test
    public void testDateDayMonthYearGreaterThanFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testDateGreaterThanEqualsDayMonthYearFails() {
        this.dateParseThrows(greaterThanEquals(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayGreaterThanEqualsMonthYearFails() {
        this.dateParseThrows(day(), greaterThanEquals(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthGreaterThanEqualsYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), greaterThanEquals(), year());
    }

    @Test
    public void testDateDayMonthYearGreaterThanEqualsFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testDateLessThanDayMonthYearFails() {
        this.dateParseThrows(lessThan(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayLessThanMonthYearFails() {
        this.dateParseThrows(day(), lessThan(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthLessThanYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), lessThan(), year());
    }

    @Test
    public void testDateDayMonthYearLessThanFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testDateLessThanEqualsDayMonthYearFails() {
        this.dateParseThrows(lessThanEquals(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayLessThanEqualsMonthYearFails() {
        this.dateParseThrows(day(), lessThanEquals(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthLessThanEqualsYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), lessThanEquals(), year());
    }

    @Test
    public void testDateDayMonthYearLessThanEqualsFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testDateNotEqualsDayMonthYearFails() {
        this.dateParseThrows(notEquals(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayNotEqualsMonthYearFails() {
        this.dateParseThrows(day(), notEquals(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthNotEqualsYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), notEquals(), year());
    }

    @Test
    public void testDateDayMonthYearNotEqualsFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), notEquals());
    }

    // color

    @Test
    public void testDateColorDay() {
        this.dateParseAndCheck(color(), day());
    }

    @Test
    public void testDateColorMonth() {
        this.dateParseAndCheck(color(), monthOrMinute());
    }

    @Test
    public void testDateColorYear() {
        this.dateParseAndCheck(color(), year());
    }

    @Test
    public void testDateDayColor() {
        this.dateParseAndCheck(day(), color());
    }

    @Test
    public void testDateMonthColor() {
        this.dateParseAndCheck(monthOrMinute(), color());
    }

    @Test
    public void testDateYearColor() {
        this.dateParseAndCheck(year(), color());
    }

    // condition

    @Test
    public void testDateConditionEqualsDay() {
        this.dateParseAndCheck(conditionEquals(), day());
    }

    @Test
    public void testDateConditionGreaterThanDay() {
        this.dateParseAndCheck(conditionGreaterThan(), day());
    }

    @Test
    public void testDateConditionGreaterThanEqualsDay() {
        this.dateParseAndCheck(conditionGreaterThanEquals(), day());
    }

    @Test
    public void testDateConditionLessThanDay() {
        this.dateParseAndCheck(conditionLessThan(), day());
    }

    @Test
    public void testDateConditionLessThanEqualsDay() {
        this.dateParseAndCheck(conditionLessThanEquals(), day());
    }

    @Test
    public void testDateConditionNotEqualsDay() {
        this.dateParseAndCheck(conditionNotEquals(), day());
    }

    @Test
    public void testDayDateConditionEquals() {
        this.dateParseAndCheck(day(), conditionEquals());
    }

    @Test
    public void testDateDayConditionGreaterThan() {
        this.dateParseAndCheck(day(), conditionGreaterThan());
    }

    @Test
    public void testDateDayConditionGreaterThanEquals() {
        this.dateParseAndCheck(day(), conditionGreaterThanEquals());
    }

    @Test
    public void testDateDayConditionLessThan() {
        this.dateParseAndCheck(day(), conditionLessThan());
    }

    @Test
    public void testDateDayConditionLessThanEquals() {
        this.dateParseAndCheck(day(), conditionLessThanEquals());
    }

    @Test
    public void testDateDayConditionNotEquals() {
        this.dateParseAndCheck(day(), conditionNotEquals());
    }

    // date helpers....

    private void dateParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.dateParser(), SpreadsheetFormatParserToken::date, tokens);
    }

    private void dateParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.dateParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> dateParser() {
        return SpreadsheetFormatParsers.date();
    }

    // bigDecimal........................................................................................................

    @Test
    public void testBigDecimalDayFails() {
        this.bigDecimalParseThrows(digitNonZero(), day());
    }

    @Test
    public void testBigDecimalHourFails() {
        this.bigDecimalParseThrows(digitNonZero(), hour());
    }

    @Test
    public void testBigDecimalMinuteOrMonthFails() {
        this.bigDecimalParseThrows(digitNonZero(), monthOrMinute());
    }

    @Test
    public void testBigDecimalSecondFails() {
        this.bigDecimalParseThrows(digitNonZero(), second());
    }

    @Test
    public void testBigDecimalStarFails() {
        this.bigDecimalParseThrows(star());
    }

    @Test
    public void testBigDecimalTextPlaceholderFails() {
        this.bigDecimalParseThrows(digitNonZero(), textPlaceholder());
    }

    @Test
    public void testBigDecimalUnderscoreFails() {
        this.bigDecimalParseThrows(underscore());
    }

    @Test
    public void testBigDecimalYearFails() {
        this.bigDecimalParseThrows(digitNonZero(), year());
    }

    @Test
    public void testBigDecimalSlashFails() {
        this.bigDecimalParseThrows(fraction());
    }

    @Test
    public void testBigDecimalDigitLeadingSpaceNumberFails() {
        this.bigDecimalParseThrows(digitLeadingSpace(), fraction());
    }

    @Test
    public void testBigDecimalDigitLeadingZeroNumberFails() {
        this.bigDecimalParseThrows(digitLeadingZero(), fraction());
    }

    @Test
    public void testBigDecimalDigitNonZeroNumberFails() {
        this.bigDecimalParseThrows(digitNonZero(), fraction());
    }

    @Test
    public void testBigDecimalGeneral() {
        this.parseAndCheck2(this.bigDecimalParser(), SpreadsheetFormatParserToken::general, general());
    }

    // literals only...........................................................................

    @Test
    public void testBigDecimalEscaped() {
        this.bigDecimalParseAndCheck(escaped());
    }

    @Test
    public void testBigDecimalMinus() {
        this.bigDecimalParseAndCheck(textLiteralMinus());
    }

    @Test
    public void testBigDecimalPlus() {
        this.bigDecimalParseAndCheck(textLiteralPlus());
    }

    @Test
    public void testBigDecimalOpenParen() {
        this.bigDecimalParseAndCheck(textLiteralOpenParens());
    }

    @Test
    public void testBigDecimalCloseParen() {
        this.bigDecimalParseAndCheck(textLiteralCloseParens());
    }

    @Test
    public void testBigDecimalColon() {
        this.bigDecimalParseAndCheck(textLiteralColon());
    }

    @Test
    public void testBigDecimalSpace() {
        this.bigDecimalParseAndCheck(textLiteralSpace());
    }

    @Test
    public void testBigDecimalQuotedText() {
        this.bigDecimalParseAndCheck(quotedText());
    }

    // digitLeadingSpace

    @Test
    public void testBigDecimalDigitLeadingSpaceNumberDigitLeadingSpace() {
        this.bigDecimalParseAndCheck(digitLeadingSpace(), decimalPoint(), digitLeadingSpace());
    }

    @Test
    public void testBigDecimalDigitLeadingSpaceDigitLeadingSpaceNumberDigitLeadingSpace() {
        this.bigDecimalParseAndCheck(digitLeadingSpace(), digitLeadingSpace(), decimalPoint(), digitLeadingSpace());
    }

    @Test
    public void testBigDecimalDigitLeadingSpaceDigitLeadingZeroNumberDigitLeadingSpace() {
        this.bigDecimalParseAndCheck(digitLeadingSpace(), digitLeadingZero(), decimalPoint(), digitLeadingSpace());
    }

    @Test
    public void testBigDecimalDigitLeadingSpaceDigitNonZeroNumberDigitLeadingSpace() {
        this.bigDecimalParseAndCheck(digitLeadingSpace(), digitNonZero(), decimalPoint(), digitLeadingSpace());
    }

    @Test
    public void testBigDecimalDigitLeadingSpaceNumberDigitLeadingSpaceDigitLeadingSpace() {
        this.bigDecimalParseAndCheck(digitLeadingSpace(), decimalPoint(), digitLeadingSpace(), digitLeadingSpace());
    }

    // digitLeadingSpace

    @Test
    public void testBigDecimalDigitLeadingZeroDigitLeadingSpaceNumberDigitLeadingZero() {
        this.bigDecimalParseAndCheck(digitLeadingZero(), digitLeadingSpace(), decimalPoint(), digitLeadingZero());
    }

    @Test
    public void testBigDecimalDigitLeadingZeroDigitLeadingZeroNumberDigitLeadingZero() {
        this.bigDecimalParseAndCheck(digitLeadingZero(), digitLeadingZero(), decimalPoint(), digitLeadingZero());
    }

    @Test
    public void testBigDecimalDigitLeadingZeroDigitNonZeroNumberDigitLeadingZero() {
        this.bigDecimalParseAndCheck(digitLeadingZero(), digitNonZero(), decimalPoint(), digitLeadingZero());
    }

    @Test
    public void testBigDecimalDigitLeadingZeroNumberDigitLeadingZeroDigitLeadingZero() {
        this.bigDecimalParseAndCheck(digitLeadingZero(), decimalPoint(), digitLeadingZero(), digitLeadingZero());
    }

    // digitLeadingZero

    @Test
    public void testBigDecimalDigitNonZeroNumberDigitNonZero() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDigitLeadingSpaceNumberDigitNonZero() {
        this.bigDecimalParseAndCheck(digitNonZero(), digitLeadingSpace(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDigitLeadingZeroNumberDigitNonZero() {
        this.bigDecimalParseAndCheck(digitNonZero(), digitLeadingZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDigitNonZeroNumberDigitNonZero() {
        this.bigDecimalParseAndCheck(digitNonZero(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroNumberDigitNonZeroDigitNonZero() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), digitNonZero());
    }

    // currency

    @Test
    public void testBigDecimalCurrencyDigitSlashDigit() {
        this.bigDecimalParseAndCheck(currency(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitCurrencySlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), currency(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashCurrencyDigit() {
        this.bigDecimalParseAndCheck(currency(), digitNonZero(), decimalPoint(), currency(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitCurrency() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), currency());
    }

    // percentage

    @Test
    public void testBigDecimalPercentageDigitSlashDigit() {
        this.bigDecimalParseAndCheck(percentage(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitPercentageSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), percentage(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashPercentageDigit() {
        this.bigDecimalParseAndCheck(percentage(), digitNonZero(), decimalPoint(), percentage(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitPercentage() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), percentage());
    }

    // thousands

    @Test
    public void testBigDecimalThousandsDigitSlashDigit() {
        this.bigDecimalParseAndCheck(thousands(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitThousandsSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), thousands(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashThousandsDigit() {
        this.bigDecimalParseAndCheck(thousands(), digitNonZero(), decimalPoint(), thousands(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitThousands() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), thousands());
    }

    // text literals

    // escaped

    @Test
    public void testBigDecimalDigitEscapedDigitSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), escaped());
    }

    @Test
    public void testBigDecimalDigitEscapedSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), escaped(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashEscapedDigit() {
        this.bigDecimalParseAndCheck(escaped(), digitNonZero(), decimalPoint(), escaped(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitEscaped() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), escaped());
    }

    // quotedText

    @Test
    public void testBigDecimalQuotedTextDigitSlashDigit() {
        this.bigDecimalParseAndCheck(quotedText(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitQuotedTextSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), quotedText(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashQuotedTextDigit() {
        this.bigDecimalParseAndCheck(quotedText(), digitNonZero(), decimalPoint(), quotedText(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitQuotedText() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), quotedText());
    }

    // closeParens

    @Test
    public void testBigDecimalCloseParensDigitSlashDigit() {
        this.bigDecimalParseAndCheck(textLiteralCloseParens(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitCloseParensSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), textLiteralCloseParens(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashCloseParensDigit() {
        this.bigDecimalParseAndCheck(textLiteralCloseParens(), digitNonZero(), decimalPoint(), textLiteralCloseParens(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitCloseParens() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testBigDecimalColonDigitSlashDigit() {
        this.bigDecimalParseAndCheck(textLiteralColon(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitColonSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), textLiteralColon(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashColonDigit() {
        this.bigDecimalParseAndCheck(textLiteralColon(), digitNonZero(), decimalPoint(), textLiteralColon(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitColon() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralColon());
    }

    // minus

    @Test
    public void testBigDecimalMinusDigitSlashDigit() {
        this.bigDecimalParseAndCheck(textLiteralMinus(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitMinusSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), textLiteralMinus(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashMinusDigit() {
        this.bigDecimalParseAndCheck(textLiteralMinus(), digitNonZero(), decimalPoint(), textLiteralMinus(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitMinus() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testBigDecimalOpenParensDigitSlashDigit() {
        this.bigDecimalParseAndCheck(textLiteralOpenParens(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitOpenParensSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), textLiteralOpenParens(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashOpenParensDigit() {
        this.bigDecimalParseAndCheck(textLiteralOpenParens(), digitNonZero(), decimalPoint(), textLiteralOpenParens(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitOpenParens() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralOpenParens());
    }

    // plus

    @Test
    public void testBigDecimalPlusDigitSlashDigit() {
        this.bigDecimalParseAndCheck(textLiteralPlus(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitPlusSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), textLiteralPlus(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashPlusDigit() {
        this.bigDecimalParseAndCheck(textLiteralPlus(), digitNonZero(), decimalPoint(), textLiteralPlus(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitPlus() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralPlus());
    }

    // space

    @Test
    public void testBigDecimalSpaceDigitSlashDigit() {
        this.bigDecimalParseAndCheck(textLiteralSpace(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSpaceSlashDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), textLiteralSpace(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashSpaceDigit() {
        this.bigDecimalParseAndCheck(textLiteralSpace(), digitNonZero(), decimalPoint(), textLiteralSpace(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitSlashDigitSpace() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralSpace());
    }

    // equals

    @Test
    public void testBigDecimalEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(equals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroEqualsDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), equals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointEqualsDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), equals(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointDigitNonZeroEqualsFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), equals());
    }

    // greaterThan

    @Test
    public void testBigDecimalGreaterThanDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(greaterThan(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroGreaterThanDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), greaterThan(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointGreaterThanDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), greaterThan(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointDigitNonZeroGreaterThanFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testBigDecimalGreaterThanEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(greaterThanEquals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroGreaterThanEqualsDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), greaterThanEquals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointGreaterThanEqualsDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), greaterThanEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointDigitNonZeroGreaterThanEqualsFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testBigDecimalLessThanDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(lessThan(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroLessThanDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), lessThan(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointLessThanDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), lessThan(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointDigitNonZeroLessThanFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testBigDecimalLessThanEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(lessThanEquals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroLessThanEqualsDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), lessThanEquals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointLessThanEqualsDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), lessThanEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointDigitNonZeroLessThanEqualsFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testBigDecimalNotEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(notEquals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroNotEqualsDecimalPointDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), notEquals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointNotEqualsDigitNonZeroFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), notEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitNonZeroDecimalPointDigitNonZeroNotEqualsFails() {
        this.bigDecimalParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), notEquals());
    }

    // exponent.............................................................................

    // currency

    @Test
    public void testBigDecimalDigitExponentCurrencyDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(currency()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitCurrencyDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(currency()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitCurrency() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(currency()));
    }

    // text literals

    // escaped

    @Test
    public void testBigDecimalDigitExponentEscapedDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(escaped()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitEscapedDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(escaped()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitEscaped() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(escaped()));
    }

    // quotedText

    @Test
    public void testBigDecimalDigitExponentQuotedTextDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(quotedText()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitQuotedTextDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(quotedText()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitQuotedText() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(quotedText()));
    }

    // closeParens

    @Test
    public void testBigDecimalDigitExponentCloseParensDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(textLiteralCloseParens()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitCloseParensDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(textLiteralCloseParens()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitCloseParens() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(textLiteralCloseParens()));
    }

    // colon

    @Test
    public void testBigDecimalDigitExponentColonDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(textLiteralColon()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitColonDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(textLiteralColon()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitColon() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(textLiteralColon()));
    }

    // minus

    @Test
    public void testBigDecimalDigitExponentMinusDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(textLiteralMinus()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitMinusDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(textLiteralMinus()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitMinus() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(textLiteralMinus()));
    }

    // openParens

    @Test
    public void testBigDecimalDigitExponentOpenParensDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(textLiteralOpenParens()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitOpenParensDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(textLiteralOpenParens()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitOpenParens() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(textLiteralOpenParens()));
    }

    // plus

    @Test
    public void testBigDecimalDigitExponentPlusDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(textLiteralPlus()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitPlusDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(textLiteralPlus()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitPlus() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(textLiteralPlus()));
    }

    // space

    @Test
    public void testBigDecimalDigitExponentSpaceDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(textLiteralSpace()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitSpaceDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(textLiteralSpace()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitSpace() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(textLiteralSpace()));
    }

    // equals

    @Test
    public void testBigDecimalDigitExponentEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent1(equals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent2(equals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitEquals() {
        this.bigDecimalParseThrows(digitNonZero(), exponent3(equals()));
    }

    // greaterThan

    @Test
    public void testBigDecimalDigitExponentGreaterThanDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent1(greaterThan()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitGreaterThanDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent2(greaterThan()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitGreaterThan() {
        this.bigDecimalParseThrows(digitNonZero(), exponent3(greaterThan()));
    }

    // greaterThanEquals

    @Test
    public void testBigDecimalDigitExponentGreaterThanEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent1(greaterThanEquals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitGreaterThanEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent2(greaterThanEquals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitGreaterThanEquals() {
        this.bigDecimalParseThrows(digitNonZero(), exponent3(greaterThanEquals()));
    }

    // lessThan

    @Test
    public void testBigDecimalDigitExponentLessThanDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent1(lessThan()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitLessThanDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent2(lessThan()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitLessThan() {
        this.bigDecimalParseThrows(digitNonZero(), exponent3(lessThan()));
    }

    // lessThanEquals

    @Test
    public void testBigDecimalDigitExponentLessThanEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent1(lessThanEquals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitLessThanEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent2(lessThanEquals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitLessThanEquals() {
        this.bigDecimalParseThrows(digitNonZero(), exponent3(lessThanEquals()));
    }

    // notEquals

    @Test
    public void testBigDecimalDigitExponentNotEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent1(notEquals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitNotEqualsDigit() {
        this.bigDecimalParseThrows(digitNonZero(), exponent2(notEquals()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitNotEquals() {
        this.bigDecimalParseThrows(digitNonZero(), exponent3(notEquals()));
    }

    // color

    @Test
    public void testBigDecimalColorDigit() {
        this.bigDecimalParseAndCheck(color(), digitNonZero());
    }

    @Test
    public void testBigDecimalDigitColor() {
        this.bigDecimalParseAndCheck(digitNonZero(), color());
    }

    @Test
    public void testBigDecimalDigitDecimalColor() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), color());
    }

    @Test
    public void testBigDecimalDigitDecimalDigitColor() {
        this.bigDecimalParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), color());
    }

    @Test
    public void testBigDecimalDigitExponentColorDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent1(color()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitColorDigit() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent2(color()));
    }

    @Test
    public void testBigDecimalDigitExponentDigitColor() {
        this.bigDecimalParseAndCheck(digitNonZero(), exponent3(color()));
    }

    // condition

    @Test
    public void testBigDecimalConditionEqualsNumber() {
        this.bigDecimalParseAndCheck(conditionEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalConditionGreaterThanNumber() {
        this.bigDecimalParseAndCheck(conditionGreaterThan(), digitNonZero());
    }

    @Test
    public void testBigDecimalConditionGreaterThanEqualsNumber() {
        this.bigDecimalParseAndCheck(conditionGreaterThanEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalConditionLessThanNumber() {
        this.bigDecimalParseAndCheck(conditionLessThan(), digitNonZero());
    }

    @Test
    public void testBigDecimalConditionLessThanEqualsNumber() {
        this.bigDecimalParseAndCheck(conditionLessThanEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalConditionNotEqualsNumber() {
        this.bigDecimalParseAndCheck(conditionNotEquals(), digitNonZero());
    }

    @Test
    public void testBigDecimalNumberConditionEquals() {
        this.bigDecimalParseAndCheck(digitNonZero(), conditionEquals());
    }

    @Test
    public void testBigDecimalNumberConditionGreaterThan() {
        this.bigDecimalParseAndCheck(digitNonZero(), conditionGreaterThan());
    }

    @Test
    public void testBigDecimalNumberConditionGreaterThanEquals() {
        this.bigDecimalParseAndCheck(digitNonZero(), conditionGreaterThanEquals());
    }

    @Test
    public void testBigDecimalNumberConditionLessThan() {
        this.bigDecimalParseAndCheck(digitNonZero(), conditionLessThan());
    }

    @Test
    public void testBigDecimalNumberConditionLessThanEquals() {
        this.bigDecimalParseAndCheck(digitNonZero(), conditionLessThanEquals());
    }

    @Test
    public void testBigDecimalNumberConditionNotEquals() {
        this.bigDecimalParseAndCheck(digitNonZero(), conditionNotEquals());
    }

    // bigDecimal helpers...

    private void bigDecimalParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.bigDecimalParser(), SpreadsheetFormatParserToken::bigDecimal, tokens);
    }

    private void bigDecimalParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.bigDecimalParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> bigDecimalParser() {
        return SpreadsheetFormatParsers.bigDecimal();
    }

    // fraction........................................................................................................

    @Test
    public void testFractionDayFails() {
        this.fractionParseThrows(digitNonZero(), day());
    }

    @Test
    public void testFractionHourFails() {
        this.fractionParseThrows(digitNonZero(), hour());
    }

    @Test
    public void testFractionMinuteOrMonthFails() {
        this.fractionParseThrows(digitNonZero(), monthOrMinute());
    }

    @Test
    public void testFractionSecondFails() {
        this.fractionParseThrows(digitNonZero(), second());
    }

    @Test
    public void testFractionStarFails() {
        this.fractionParseThrows(star());
    }

    @Test
    public void testFractionTextPlaceholderFails() {
        this.fractionParseThrows(digitNonZero(), textPlaceholder());
    }

    @Test
    public void testFractionUnderscoreFails() {
        this.fractionParseThrows(underscore());
    }

    @Test
    public void testFractionYearFails() {
        this.fractionParseThrows(digitNonZero(), year());
    }

    @Test
    public void testFractionSlashFails() {
        this.fractionParseThrows(fraction());
    }

    @Test
    public void testFractionDigitLeadingSpaceFractionFails() {
        this.fractionParseThrows(digitLeadingSpace(), fraction());
    }

    @Test
    public void testFractionDigitLeadingZeroFractionFails() {
        this.fractionParseThrows(digitLeadingZero(), fraction());
    }

    @Test
    public void testFractionDigitNonZeroFractionFails() {
        this.fractionParseThrows(digitNonZero(), fraction());
    }

    @Test
    public void testFractionThousandsFails() {
        this.fractionParseThrows(thousands());
    }

    @Test
    public void testFractionDigitThousandsFails() {
        this.fractionParseThrows(digitNonZero(), thousands());
    }

    @Test
    public void testFractionGeneral() {
        this.parseAndCheck2(this.fractionParser(), SpreadsheetFormatParserToken::general, general());
    }

    // digitLeadingSpace

    @Test
    public void testFractionDigitLeadingSpaceFractionDigitLeadingSpace() {
        this.fractionParseAndCheck(digitLeadingSpace(), fraction(), digitLeadingSpace());
    }

    @Test
    public void testFractionDigitLeadingSpaceDigitLeadingSpaceFractionDigitLeadingSpace() {
        this.fractionParseAndCheck(digitLeadingSpace(), digitLeadingSpace(), fraction(), digitLeadingSpace());
    }

    @Test
    public void testFractionDigitLeadingSpaceDigitLeadingZeroFractionDigitLeadingSpace() {
        this.fractionParseAndCheck(digitLeadingSpace(), digitLeadingZero(), fraction(), digitLeadingSpace());
    }

    @Test
    public void testFractionDigitLeadingSpaceDigitNonZeroFractionDigitLeadingSpace() {
        this.fractionParseAndCheck(digitLeadingSpace(), digitNonZero(), fraction(), digitLeadingSpace());
    }

    @Test
    public void testFractionDigitLeadingSpaceFractionDigitLeadingSpaceDigitLeadingSpace() {
        this.fractionParseAndCheck(digitLeadingSpace(), fraction(), digitLeadingSpace(), digitLeadingSpace());
    }

    // digitLeadingSpace

    @Test
    public void testFractionDigitLeadingZeroDigitLeadingSpaceFractionDigitLeadingZero() {
        this.fractionParseAndCheck(digitLeadingZero(), digitLeadingSpace(), fraction(), digitLeadingZero());
    }

    @Test
    public void testFractionDigitLeadingZeroDigitLeadingZeroFractionDigitLeadingZero() {
        this.fractionParseAndCheck(digitLeadingZero(), digitLeadingZero(), fraction(), digitLeadingZero());
    }

    @Test
    public void testFractionDigitLeadingZeroDigitNonZeroFractionDigitLeadingZero() {
        this.fractionParseAndCheck(digitLeadingZero(), digitNonZero(), fraction(), digitLeadingZero());
    }

    @Test
    public void testFractionDigitLeadingZeroFractionDigitLeadingZeroDigitLeadingZero() {
        this.fractionParseAndCheck(digitLeadingZero(), fraction(), digitLeadingZero(), digitLeadingZero());
    }

    // digitLeadingSpace

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroDigitLeadingSpaceFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), digitLeadingSpace(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroDigitLeadingZeroFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), digitLeadingZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroDigitNonZeroFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), digitNonZero());
    }

    // currency

    @Test
    public void testFractioncurrencyDigitSlashDigit() {
        this.fractionParseAndCheck(currency(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitcurrencySlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), currency(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashcurrencyDigit() {
        this.fractionParseAndCheck(currency(), digitNonZero(), fraction(), currency(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitcurrency() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), currency());
    }

    // text literals

    // escaped

    @Test
    public void testFractionEscapedDigitSlashDigit() {
        this.fractionParseAndCheck(escaped(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitEscapedSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), escaped(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashEscapedDigit() {
        this.fractionParseAndCheck(escaped(), digitNonZero(), fraction(), escaped(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitEscaped() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), escaped());
    }

    // quotedText

    @Test
    public void testFractionQuotedTextDigitSlashDigit() {
        this.fractionParseAndCheck(quotedText(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitQuotedTextSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), quotedText(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashQuotedTextDigit() {
        this.fractionParseAndCheck(quotedText(), digitNonZero(), fraction(), quotedText(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitQuotedText() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), quotedText());
    }

    // closeParens

    @Test
    public void testFractionCloseParensDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralCloseParens(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitCloseParensSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralCloseParens(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashCloseParensDigit() {
        this.fractionParseAndCheck(textLiteralCloseParens(), digitNonZero(), fraction(), textLiteralCloseParens(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitCloseParens() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testFractionColonDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralColon(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitColonSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralColon(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashColonDigit() {
        this.fractionParseAndCheck(textLiteralColon(), digitNonZero(), fraction(), textLiteralColon(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitColon() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), textLiteralColon());
    }

    // minus

    @Test
    public void testFractionMinusDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralMinus(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitMinusSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralMinus(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashMinusDigit() {
        this.fractionParseAndCheck(textLiteralMinus(), digitNonZero(), fraction(), textLiteralMinus(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitMinus() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testFractionOpenParensDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralOpenParens(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitOpenParensSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralOpenParens(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashOpenParensDigit() {
        this.fractionParseAndCheck(textLiteralOpenParens(), digitNonZero(), fraction(), textLiteralOpenParens(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitOpenParens() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), textLiteralOpenParens());
    }

    // percentage

    @Test
    public void testFractionPercentageDigitSlashDigit() {
        this.fractionParseAndCheck(percentage(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitPercentageSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), percentage(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashPercentageDigit() {
        this.fractionParseAndCheck(percentage(), digitNonZero(), fraction(), percentage(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitPercentage() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), percentage());
    }

    // plus

    @Test
    public void testFractionPlusDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralPlus(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitPlusSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralPlus(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashPlusDigit() {
        this.fractionParseAndCheck(textLiteralPlus(), digitNonZero(), fraction(), textLiteralPlus(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitPlus() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), textLiteralPlus());
    }

    // space

    @Test
    public void testFractionSpaceDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralSpace(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSpaceSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralSpace(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashSpaceDigit() {
        this.fractionParseAndCheck(textLiteralSpace(), digitNonZero(), fraction(), textLiteralSpace(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitSpace() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), textLiteralSpace());
    }

    // thousands

    @Test
    public void testFractionThousandsDigitSlashDigit() {
        this.fractionParseAndCheck(thousands(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitThousandsSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), thousands(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashThousandsDigit() {
        this.fractionParseAndCheck(thousands(), digitNonZero(), fraction(), thousands(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitThousands() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), thousands());
    }

    // equals

    @Test
    public void testFractionEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(equals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), equals(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), equals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), digitNonZero(), equals());
    }

    // greaterThan

    @Test
    public void testFractionGreaterThanDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(greaterThan(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroGreaterThanFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), greaterThan(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionGreaterThanDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), greaterThan(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroGreaterThanFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), digitNonZero(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testFractionGreaterThanEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(greaterThanEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroGreaterThanEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), greaterThanEquals(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionGreaterThanEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), greaterThanEquals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroGreaterThanEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), digitNonZero(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testFractionLessThanDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(lessThan(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroLessThanFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), lessThan(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionLessThanDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), lessThan(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroLessThanFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), digitNonZero(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testFractionLessThanEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(lessThanEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroLessThanEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), lessThanEquals(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionLessThanEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), lessThanEquals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroLessThanEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), digitNonZero(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testFractionNotEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(notEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroNotEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), notEquals(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionNotEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), notEquals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroNotEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fraction(), digitNonZero(), notEquals());
    }

    // color

    @Test
    public void testFractionColorDigitFractionDigit() {
        this.fractionParseAndCheck(color(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitColorFractionDigit() {
        this.fractionParseAndCheck(digitNonZero(), color(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitColorDigitFractionDigit() {
        this.fractionParseAndCheck(digitNonZero(), color(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionDigitFractionColorDigit() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), color(), digitNonZero());
    }

    @Test
    public void testFractionDigitFractionDigitColorDigit() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), color(), digitNonZero());
    }

    @Test
    public void testFractionDigitFractionDigitColor() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), color());
    }

    // condition

    @Test
    public void testFractionConditionEqualsFraction() {
        this.fractionParseAndCheck(conditionEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionConditionGreaterThanFraction() {
        this.fractionParseAndCheck(conditionGreaterThan(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionConditionGreaterThanEqualsFraction() {
        this.fractionParseAndCheck(conditionGreaterThanEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionConditionLessThanFraction() {
        this.fractionParseAndCheck(conditionLessThan(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionConditionLessThanEqualsFraction() {
        this.fractionParseAndCheck(conditionLessThanEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionConditionNotEqualsFraction() {
        this.fractionParseAndCheck(conditionNotEquals(), digitNonZero(), fraction(), digitNonZero());
    }

    @Test
    public void testFractionFractionConditionEquals() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), conditionEquals());
    }

    @Test
    public void testFractionFractionConditionGreaterThan() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), conditionGreaterThan());
    }

    @Test
    public void testFractionFractionConditionGreaterThanEquals() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), conditionGreaterThanEquals());
    }

    @Test
    public void testFractionFractionConditionLessThan() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), conditionLessThan());
    }

    @Test
    public void testFractionFractionConditionLessThanEquals() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), conditionLessThanEquals());
    }

    @Test
    public void testFractionFractionConditionNotEquals() {
        this.fractionParseAndCheck(digitNonZero(), fraction(), digitNonZero(), conditionNotEquals());
    }

    // fraction helpers...

    private void fractionParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.fractionParser(), SpreadsheetFormatParserToken::fraction, tokens);
    }

    private void fractionParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.fractionParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> fractionParser() {
        return SpreadsheetFormatParsers.fraction();
    }

    // general .............................................................................................

    @Test
    public void testGeneralGeneral() {
        this.generalParseAndCheck(general());
    }

    @Test
    public void testGeneralWhitespaceGeneral() {
        this.generalParseAndCheck(whitespace(), general());
    }

    @Test
    public void testGeneralGeneralWhitespace() {
        this.generalParseAndCheck(general(), whitespace());
    }

    @Test
    public void testGeneralColorGeneral() {
        this.generalParseAndCheck(color(), general());
    }

    @Test
    public void testGeneralColorWhitespaceGeneral() {
        this.generalParseAndCheck(color(), whitespace(), general());
    }

    @Test
    public void testGeneralGeneralColor() {
        this.generalParseAndCheck(general(), color());
    }

    @Test
    public void testGeneralGeneralColorWhitespace() {
        this.generalParseAndCheck(general(), color(), whitespace());
    }

    @Test
    public void testGeneralGeneralWhitespaceColor() {
        this.generalParseAndCheck(general(), whitespace(), color());
    }

    /**
     * Parsers the general expression using the general parser.
     */
    private void generalParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        final String general = ParserToken.text(Lists.of(tokens));
        this.parseAndCheck(this.generalParser(),
                general,
                SpreadsheetFormatParserToken.general(Lists.of(tokens), general),
                general);
    }

    private Parser<SpreadsheetFormatParserContext> generalParser() {
        return SpreadsheetFormatParsers.general();
    }

    // text........................................................................................................

    @Test
    public void testTextTextDigitNonZeroFails() {
        this.textParseThrows(digitNonZero());
    }

    @Test
    public void testTextTextDigitLeadingZeroFails() {
        this.textParseThrows(digitLeadingZero());
    }

    @Test
    public void testTextTextDigitLeadingSpaceFails() {
        this.textParseThrows(digitLeadingSpace());
    }

    @Test
    public void testTextLetterFails() {
        this.textParseThrows(textLiteral('A'));
    }

    @Test
    public void testTextSeparatorFails() {
        this.textParseThrows(separator());
    }

    @Test
    public void testTextGeneraFailsl() {
        this.textParseThrows(general());
    }

    @Test
    public void testTextEscaped() {
        this.textParseAndCheck(escaped());
    }

    @Test
    public void testTextStar() {
        this.textParseAndCheck(star());
    }

    @Test
    public void testTextStar2() {
        this.textParseAndCheck(star2());
    }

    @Test
    public void testTextStarStarFails() {
        this.textParseThrows(star(), star2());
    }

    @Test
    public void testTextStarTextPlaceholderStarFails() {
        this.textParseThrows(star(), textPlaceholder(), star2());
    }

    // text literals

    @Test
    public void testTextTextLiteralDollar() {
        this.textParseAndCheck(textLiteralDollar());
    }

    @Test
    public void testTextTextLiteralMinusSign() {
        this.textParseAndCheck(textLiteralMinus());
    }

    @Test
    public void testTextTextLiteralPlusSign() {
        this.textParseAndCheck(textLiteralPlus());
    }

    @Test
    public void testTextTextLiteralSlash() {
        this.textParseAndCheck(textLiteralSlash());
    }

    @Test
    public void testTextTextLiteralOpenParens() {
        this.textParseAndCheck(textLiteralOpenParens());
    }

    @Test
    public void testTextTextLiteralCloseParens() {
        this.textParseAndCheck(textLiteralCloseParens());
    }

    @Test
    public void testTextTextLiteralColon() {
        this.textParseAndCheck(textLiteralColon());
    }

    @Test
    public void testTextTextLiteralSpace() {
        this.textParseAndCheck(textLiteralSpace());
    }

    @Test
    public void testTextTextPlaceholder() {
        this.textParseAndCheck(textPlaceholder());
    }

    @Test
    public void testTextTextPlaceholder2() {
        this.textParseAndCheck(textPlaceholder(), textPlaceholder());
    }

    @Test
    public void testTextTextQuoted() {
        this.textParseAndCheck(quotedText());
    }

    @Test
    public void testTextUnderscore() {
        this.textParseAndCheck(underscore());
    }

    @Test
    public void testTextUnderscore2() {
        this.textParseAndCheck(underscore());
    }

    @Test
    public void testTextUnderscoreUnderscore() {
        this.textParseAndCheck(underscore(), underscore2());
    }

    @Test
    public void testTextEqualsFails() {
        this.textParseThrows(equals());
    }

    @Test
    public void testTextGreaterThanFails() {
        this.textParseThrows(greaterThan());
    }

    @Test
    public void testTextGreaterThanEqualsFails() {
        this.textParseThrows(greaterThanEquals());
    }

    @Test
    public void testTextLessThanFails() {
        this.textParseThrows(lessThan());
    }

    @Test
    public void testTextLessThanEqualsFails() {
        this.textParseThrows(lessThanEquals());
    }

    @Test
    public void testTextNotEqualsFails() {
        this.textParseThrows(notEquals());
    }

    @Test
    public void testTextAll() {
        this.textParseAndCheck(textLiteralSpace(), quotedText(), textPlaceholder(), underscore());
    }

    @Test
    public void testTextColorQuotedText() {
        this.textParseAndCheck(color(), quotedText());
    }

    @Test
    public void testTextQuotedTextColor() {
        this.textParseAndCheck(quotedText(), color());
    }

    private void textParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.textParser(), SpreadsheetFormatParserToken::text, tokens);
    }

    private void textParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.textParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> textParser() {
        return SpreadsheetFormatParsers.text();
    }

    // time........................................................................................................

    @Test
    public void testTimeTextDigitNonZeroFails() {
        this.timeParseThrows(digitNonZero());
    }

    @Test
    public void testTimeTextDigitLeadingZeroFails() {
        this.timeParseThrows(digitLeadingZero());
    }

    @Test
    public void testTimeTextDigitLeadingSpaceFails() {
        this.timeParseThrows(digitLeadingSpace());
    }

    @Test
    public void testTimeTextThousandsFails() {
        this.timeParseThrows(thousands());
    }

    @Test
    public void testTimeDayFails() {
        this.timeParseThrows(day());
    }

    @Test
    public void testTimeYearFails() {
        this.timeParseThrows(year());
    }

    @Test
    public void testTimeTextPlaceholderFails() {
        this.timeParseThrows(textPlaceholder());
    }

    @Test
    public void testTimeGeneral() {
        this.parseAndCheck2(this.timeParser(), SpreadsheetFormatParserToken::general, general());
    }

    @Test
    public void testTimeEscaped() {
        this.timeParseAndCheck(escaped());
    }

    @Test
    public void testTimeDollar() {
        this.timeParseAndCheck(textLiteralDollar());
    }

    @Test
    public void testTimeMinus() {
        this.timeParseAndCheck(textLiteralMinus());
    }

    @Test
    public void testTimePlus() {
        this.timeParseAndCheck(textLiteralPlus());
    }

    @Test
    public void testTimeSlash() {
        this.timeParseAndCheck(textLiteralSlash());
    }

    @Test
    public void testTimeOpenParen() {
        this.timeParseAndCheck(textLiteralOpenParens());
    }

    @Test
    public void testTimeCloseParen() {
        this.timeParseAndCheck(textLiteralCloseParens());
    }

    @Test
    public void testTimeColon() {
        this.timeParseAndCheck(textLiteralColon());
    }

    @Test
    public void testTimeSpace() {
        this.timeParseAndCheck(textLiteralSpace());
    }

    @Test
    public void testTimeQuotedText() {
        this.timeParseAndCheck(quotedText());
    }

    @Test
    public void testTimeASlashP() {
        this.timeParseAndCheck(aSlashP());
    }

    @Test
    public void testTimeAmSlashPm() {
        this.timeParseAndCheck(amSlashPm());
    }

    @Test
    public void testTimeHour() {
        this.timeParseAndCheck(hour());
    }

    @Test
    public void testTimeMinute() {
        this.timeParseAndCheck(monthOrMinute());
    }

    @Test
    public void testTimeHourMinuteSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMinuteSecondASlashP() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), aSlashP());
    }

    @Test
    public void testTimeHourMinuteSecondAmSlashPm() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), amSlashPm());
    }

    @Test
    public void testTimeHour2Minute2Second2() {
        this.timeParseAndCheck(hour(2), monthOrMinute(2), second(2));
    }

    @Test
    public void testTimeHour3Minute3Second3() {
        this.timeParseAndCheck(hour(3), monthOrMinute(3), second(3));
    }

    @Test
    public void testTimeHourMinuteSecondHourMinuteSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeMinuteHourSecond() {
        this.timeParseAndCheck(monthOrMinute(), hour(), second());
    }

    @Test
    public void testTimeSecondMinuteHour() {
        this.timeParseAndCheck(second(), monthOrMinute(), hour());
    }

    // escaped

    @Test
    public void testTimeEscapedHourMonthSecond() {
        this.timeParseAndCheck(escaped(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourEscapedMonthSecond() {
        this.timeParseAndCheck(hour(), escaped(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthEscapedSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), escaped(), second());
    }

    @Test
    public void testTimeHourMonthSecondsEscaped() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), escaped());
    }

    // quotedText

    @Test
    public void testTimeQuotedTextHourMonthSecond() {
        this.timeParseAndCheck(quotedText(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourQuotedTextMonthSecond() {
        this.timeParseAndCheck(hour(), quotedText(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthQuotedTextSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), quotedText(), second());
    }

    @Test
    public void testTimeHourMonthSecondsQuotedText() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), quotedText());
    }

    // closeParens

    @Test
    public void testTimeCloseParensHourMonthSecond() {
        this.timeParseAndCheck(textLiteralCloseParens(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourCloseParensMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralCloseParens(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthCloseParensSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralCloseParens(), second());
    }

    @Test
    public void testTimeHourMonthSecondsCloseParens() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testTimeColonHourMonthSecond() {
        this.timeParseAndCheck(textLiteralColon(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourColonMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralColon(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthColonSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralColon(), second());
    }

    @Test
    public void testTimeHourMonthSecondsColon() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralColon());
    }

    // dollar

    @Test
    public void testTimeDollarHourMonthSecond() {
        this.timeParseAndCheck(textLiteralDollar(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourDollarMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralDollar(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthDollarSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralDollar(), second());
    }

    @Test
    public void testTimeHourMonthSecondsDollar() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralDollar());
    }

    // minus

    @Test
    public void testTimeMinusHourMonthSecond() {
        this.timeParseAndCheck(textLiteralMinus(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMinusMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralMinus(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthMinusSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralMinus(), second());
    }

    @Test
    public void testTimeHourMonthSecondsMinus() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testTimeOpenParensHourMonthSecond() {
        this.timeParseAndCheck(textLiteralOpenParens(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourOpenParensMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralOpenParens(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthOpenParensSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralOpenParens(), second());
    }

    @Test
    public void testTimeHourMonthSecondsOpenParens() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralOpenParens());
    }

    // plus

    @Test
    public void testTimePlusHourMonthSecond() {
        this.timeParseAndCheck(textLiteralPlus(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourPlusMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralPlus(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthPlusSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralPlus(), second());
    }

    @Test
    public void testTimeHourMonthSecondsPlus() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralPlus());
    }

    // slash

    @Test
    public void testTimeSlashHourMonthSecond() {
        this.timeParseAndCheck(textLiteralSlash(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourSlashMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralSlash(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthSlashSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralSlash(), second());
    }

    @Test
    public void testTimeHourMonthSecondsSlash() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralSlash());
    }

    // space

    @Test
    public void testTimeSpaceHourMonthSecond() {
        this.timeParseAndCheck(textLiteralSpace(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourSpaceMonthSecond() {
        this.timeParseAndCheck(hour(), textLiteralSpace(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthSpaceSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), textLiteralSpace(), second());
    }

    @Test
    public void testTimeHourMonthSecondsSpace() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), textLiteralSpace());
    }

    // equals

    @Test
    public void testTimeEqualsHourMonthSecondFails() {
        this.timeParseThrows(equals(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourEqualsMonthSecondFails() {
        this.timeParseThrows(hour(), equals(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthEqualsSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), equals(), second());
    }

    @Test
    public void testTimeHourMonthSecondsEqualsFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), equals());
    }

    // greaterThan

    @Test
    public void testTimeGreaterThanHourMonthSecondFails() {
        this.timeParseThrows(greaterThan(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourGreaterThanMonthSecondFails() {
        this.timeParseThrows(hour(), greaterThan(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthGreaterThanSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), greaterThan(), second());
    }

    @Test
    public void testTimeHourMonthSecondsGreaterThanFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testTimeGreaterThanEqualsHourMonthSecondFails() {
        this.timeParseThrows(greaterThanEquals(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourGreaterThanEqualsMonthSecondFails() {
        this.timeParseThrows(hour(), greaterThanEquals(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthGreaterThanEqualsSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), greaterThanEquals(), second());
    }

    @Test
    public void testTimeHourMonthSecondsGreaterThanEqualsFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testTimeLessThanHourMonthSecondFails() {
        this.timeParseThrows(lessThan(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourLessThanMonthSecondFails() {
        this.timeParseThrows(hour(), lessThan(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthLessThanSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), lessThan(), second());
    }

    @Test
    public void testTimeHourMonthSecondsLessThanFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testTimeLessThanEqualsHourMonthSecondFails() {
        this.timeParseThrows(lessThanEquals(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourLessThanEqualsMonthSecondFails() {
        this.timeParseThrows(hour(), lessThanEquals(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthLessThanEqualsSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), lessThanEquals(), second());
    }

    @Test
    public void testTimeHourMonthSecondsLessThanEqualsFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testTimeNotEqualsHourMonthSecondFails() {
        this.timeParseThrows(notEquals(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourNotEqualsMonthSecondFails() {
        this.timeParseThrows(hour(), notEquals(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthNotEqualsSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), notEquals(), second());
    }

    @Test
    public void testTimeHourMonthSecondsNotEqualsFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), notEquals());
    }

    // color

    @Test
    public void testTimeColorHour() {
        this.timeParseAndCheck(color(), hour());
    }

    @Test
    public void testTimeColorMinute() {
        this.timeParseAndCheck(color(), monthOrMinute());
    }

    @Test
    public void testTimeColorSeconds() {
        this.timeParseAndCheck(color(), second());
    }

    @Test
    public void testTimeHourColor() {
        this.timeParseAndCheck(hour(), color());
    }

    @Test
    public void testTimeMinuteColor() {
        this.timeParseAndCheck(monthOrMinute(), color());
    }

    @Test
    public void testTimeSecondsColor() {
        this.timeParseAndCheck(second(), color());
    }

    // condition

    @Test
    public void testTimeConditionEqualsHour() {
        this.timeParseAndCheck(conditionEquals(), hour());
    }

    @Test
    public void testTimeConditionGreaterThanHour() {
        this.timeParseAndCheck(conditionGreaterThan(), hour());
    }

    @Test
    public void testTimeConditionGreaterThanEqualsHour() {
        this.timeParseAndCheck(conditionGreaterThanEquals(), hour());
    }

    @Test
    public void testTimeConditionLessThanHour() {
        this.timeParseAndCheck(conditionLessThan(), hour());
    }

    @Test
    public void testTimeConditionLessThanEqualsHour() {
        this.timeParseAndCheck(conditionLessThanEquals(), hour());
    }

    @Test
    public void testTimeConditionNotEqualsHour() {
        this.timeParseAndCheck(conditionNotEquals(), hour());
    }

    @Test
    public void testHourTimeConditionEquals() {
        this.timeParseAndCheck(hour(), conditionEquals());
    }

    @Test
    public void testTimeHourConditionGreaterThan() {
        this.timeParseAndCheck(hour(), conditionGreaterThan());
    }

    @Test
    public void testTimeHourConditionGreaterThanEquals() {
        this.timeParseAndCheck(hour(), conditionGreaterThanEquals());
    }

    @Test
    public void testTimeHourConditionLessThan() {
        this.timeParseAndCheck(hour(), conditionLessThan());
    }

    @Test
    public void testTimeHourConditionLessThanEquals() {
        this.timeParseAndCheck(hour(), conditionLessThanEquals());
    }

    @Test
    public void testTimeHourConditionNotEquals() {
        this.timeParseAndCheck(hour(), conditionNotEquals());
    }

    // time helpers...

    private void timeParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.timeParser(), SpreadsheetFormatParserToken::time, tokens);
    }

    private void timeParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.timeParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> timeParser() {
        return SpreadsheetFormatParsers.time();
    }

    // dateAndTime....................................................................................................

    @Test
    public void testDateTimeGeneral() {
        this.parseAndCheck2(this.dateTimeParser(), SpreadsheetFormatParserToken::general, general());
    }

    // literals only...........................................................................

    @Test
    public void testDateTimeEscaped() {
        this.dateTimeParseAndCheck(escaped());
    }

    @Test
    public void testDateTimeDollar() {
        this.dateTimeParseAndCheck(textLiteralDollar());
    }

    @Test
    public void testDateTimeMinus() {
        this.dateTimeParseAndCheck(textLiteralMinus());
    }

    @Test
    public void testDateTimePlus() {
        this.dateTimeParseAndCheck(textLiteralPlus());
    }

    @Test
    public void testDateTimeSlash() {
        this.dateTimeParseAndCheck(textLiteralSlash());
    }

    @Test
    public void testDateTimeOpenParen() {
        this.dateTimeParseAndCheck(textLiteralOpenParens());
    }

    @Test
    public void testDateTimeCloseParen() {
        this.dateTimeParseAndCheck(textLiteralCloseParens());
    }

    @Test
    public void testDateTimeColon() {
        this.dateTimeParseAndCheck(textLiteralColon());
    }

    @Test
    public void testDateTimeSpace() {
        this.dateTimeParseAndCheck(textLiteralSpace());
    }

    @Test
    public void testDateTimeQuotedText() {
        this.dateTimeParseAndCheck(quotedText());
    }

    // date only........................................................................................................

    @Test
    public void testDateTimeDay() {
        this.dateTimeParseAndCheck(day());
    }

    @Test
    public void testDateTimeDayMonth() {
        this.dateTimeParseAndCheck(day(), monthOrMinute());
    }

    @Test
    public void testDateTimeDayMonthYear() {
        this.dateTimeParseAndCheck(day(), monthOrMinute(), year());
    }

    // time only........................................................................................................

    @Test
    public void testDateTimeHour() {
        this.dateTimeParseAndCheck(hour());
    }

    @Test
    public void testDateTimeHourMinute() {
        this.dateTimeParseAndCheck(hour(), monthOrMinute());
    }

    @Test
    public void testDateTimeHourMinuteSecond() {
        this.dateTimeParseAndCheck(hour(), monthOrMinute(), second());
    }

    // date&time........................................................................................................

    @Test
    public void testDateTimeDayHour() {
        this.dateTimeParseAndCheck(day(), hour());
    }

    @Test
    public void testDateTimeDayHour2() {
        this.dateTimeParseAndCheck(day(), hour(), day(), hour());
    }

    @Test
    public void testDateTimeDayMonthYearHourMinuteSecond() {
        this.dateTimeParseAndCheck(day(), monthOrMinute(), year(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testDateTimeDayMonthYearHourMinuteSecond2() {
        this.dateTimeParseAndCheck(day(), monthOrMinute(), year(), hour(), monthOrMinute(), second(), day(), monthOrMinute(), year(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testDateTimeColorDay() {
        this.dateTimeParseAndCheck(color(), day());
    }

    @Test
    public void testDateTimeDayColor() {
        this.dateTimeParseAndCheck(day(), color());
    }

    // condition

    @Test
    public void testDateTimeConditionEqualsDay() {
        this.dateTimeParseAndCheck(conditionEquals(), day());
    }

    @Test
    public void testDateTimeConditionGreaterThanDay() {
        this.dateTimeParseAndCheck(conditionGreaterThan(), day());
    }

    @Test
    public void testDateTimeConditionGreaterThanEqualsDay() {
        this.dateTimeParseAndCheck(conditionGreaterThanEquals(), day());
    }

    @Test
    public void testDateTimeConditionLessThanDay() {
        this.dateTimeParseAndCheck(conditionLessThan(), day());
    }

    @Test
    public void testDateTimeConditionLessThanEqualsDay() {
        this.dateTimeParseAndCheck(conditionLessThanEquals(), day());
    }

    @Test
    public void testDateTimeConditionNotEqualsDay() {
        this.dateTimeParseAndCheck(conditionNotEquals(), day());
    }

    @Test
    public void testDateTimeConditionEqualsHour() {
        this.dateTimeParseAndCheck(conditionEquals(), hour());
    }

    @Test
    public void testDateTimeConditionGreaterThanHour() {
        this.dateTimeParseAndCheck(conditionGreaterThan(), hour());
    }

    @Test
    public void testDateTimeConditionGreaterThanEqualsHour() {
        this.dateTimeParseAndCheck(conditionGreaterThanEquals(), hour());
    }

    @Test
    public void testDateTimeConditionLessThanHour() {
        this.dateTimeParseAndCheck(conditionLessThan(), hour());
    }

    @Test
    public void testDateTimeConditionLessThanEqualsHour() {
        this.dateTimeParseAndCheck(conditionLessThanEquals(), hour());
    }

    @Test
    public void testDateTimeConditionNotEqualsHour() {
        this.dateTimeParseAndCheck(conditionNotEquals(), hour());
    }

    @Test
    public void testDateTimeDayConditionEquals() {
        this.dateTimeParseAndCheck(day(), conditionEquals());
    }

    @Test
    public void testDateTimeDayConditionGreaterThan() {
        this.dateTimeParseAndCheck(day(), conditionGreaterThan());
    }

    @Test
    public void testDateTimeDayConditionGreaterThanEquals() {
        this.dateTimeParseAndCheck(day(), conditionGreaterThanEquals());
    }

    @Test
    public void testDateTimeDayConditionLessThan() {
        this.dateTimeParseAndCheck(day(), conditionLessThan());
    }

    @Test
    public void testDateTimeDayConditionLessThanEquals() {
        this.dateTimeParseAndCheck(day(), conditionLessThanEquals());
    }

    @Test
    public void testDateTimeDayConditionNotEquals() {
        this.dateTimeParseAndCheck(day(), conditionNotEquals());
    }

    @Test
    public void testDateTimeHourConditionEquals() {
        this.dateTimeParseAndCheck(hour(), conditionEquals());
    }

    @Test
    public void testDateTimeHourConditionGreaterThan() {
        this.dateTimeParseAndCheck(hour(), conditionGreaterThan());
    }

    @Test
    public void testDateTimeHourConditionGreaterThanEquals() {
        this.dateTimeParseAndCheck(hour(), conditionGreaterThanEquals());
    }

    @Test
    public void testDateTimeHourConditionLessThan() {
        this.dateTimeParseAndCheck(hour(), conditionLessThan());
    }

    @Test
    public void testDateTimeHourConditionLessThanEquals() {
        this.dateTimeParseAndCheck(hour(), conditionLessThanEquals());
    }

    @Test
    public void testDateTimeHourConditionNotEquals() {
        this.dateTimeParseAndCheck(hour(), conditionNotEquals());
    }

    private void dateTimeParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.dateTimeParser(), SpreadsheetFormatParserToken::dateTime, tokens);
    }

    private Parser<SpreadsheetFormatParserContext> dateTimeParser() {
        return SpreadsheetFormatParsers.dateTime();
    }

    // expression ..............................................................................................

    @Test
    public void testExpressionEmptyFails() {
        this.parseFailAndCheck(this.expressionParser(), "");
    }

    @Test
    public void testExpressionEscaped() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, escaped()));
    }

    @Test
    public void testExpressionDollarFails() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, currency()));
    }

    @Test
    public void testExpressionMinus() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, textLiteralMinus()));
    }

    @Test
    public void testExpressionPlus() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, textLiteralPlus()));
    }

    @Test
    public void testExpressionSlashFails() {
        this.parseFailAndCheck(this.expressionParser(), "/");
    }

    @Test
    public void testExpressionOpenParen() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, textLiteralOpenParens()));
    }

    @Test
    public void testExpressionCloseParen() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, textLiteralCloseParens()));
    }

    @Test
    public void testExpressionColon() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, textLiteralColon()));
    }

    @Test
    public void testExpressionSpace() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, textLiteralSpace()));
    }

    @Test
    public void testExpressionQuotedText() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, quotedText()));
    }

    @Test
    public void testExpressionDateTimeYear() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, year()));
    }

    @Test
    public void testExpressionDateTimeDayMonthYear() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, day(), monthOrMinute(), year()));
    }

    @Test
    public void testExpressionDateMonthDayYear() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, monthOrMinute(), day(), year()));
    }

    @Test
    public void testExpressionDateTimeMonthYearDay() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, monthOrMinute(), year(), hour()));
    }

    @Test
    public void testExpressionDateTimeYearDay() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, year(), hour()));
    }

    @Test
    public void testExpressionDateTimeDayMonthYearHourMinuteSecond() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, day(), monthOrMinute(), year(), hour(), monthOrMinute(), second()));
    }

    @Test
    public void testExpressionDateTimeDayMonthYearHourMinuteSecondAmPm() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, day(), monthOrMinute(), year(), hour(), monthOrMinute(), second(), amSlashPm()));
    }

    @Test
    public void testExpressionDateTimeDayMonthHourMinuteSecond() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, hour(), monthOrMinute(), second()));
    }

    @Test
    public void testExpressionDateTimeDayMonthMinuteHourSecond() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, monthOrMinute(), hour(), second()));
    }

    @Test
    public void testExpressionDateTimeHourMinuteSecondAmPm() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, hour(), monthOrMinute(), second(), amSlashPm()));
    }

    @Test
    public void testExpressionFractionDigitSlashDigit() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::fraction, digitNonZero(), fraction(), digitNonZero()));
    }

    @Test
    public void testExpressionNumberDigit() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()));
    }

    @Test
    public void testExpressionNumberDigitDecimalPointDigit() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero(), decimalPoint(), digitNonZero()));
    }

    @Test
    public void testExpressionNumberSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator());
    }

    @Test
    public void testExpressionNumberSeparatorNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()));
    }

    @Test
    public void testExpressionNumberSeparatorNumberSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator());
    }

    @Test
    public void testExpressionNumberSeparatorNumberSeparatorNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()));
    }

    @Test
    public void testExpressionNumberSeparatorNumberSeparatorNumberSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()));
    }

    @Test
    public void testExpressionConditionNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, conditionEquals(), digitNonZero()));
    }

    @Test
    public void testExpressionConditionNumberSeparatorConditionNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, conditionEquals(), digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, conditionEquals(), digitNonZero()));
    }

    @Test
    public void testExpressionConditionNumberSeparatorConditionNumberSeparatorConditionNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, conditionEquals(), digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, conditionEquals(), digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, conditionEquals(), digitNonZero()));
    }

    // empty separators......................................................................................

    @Test
    public void testExpressionSeparatorSeparatorSeparatorNumberFails() {
        this.parseThrows2(this.expressionParser(),
                separator(),
                separator(),
                separator(),
                token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()));
    }

    @Test
    public void testExpressionSeparator() {
        this.expressionParseAndCheck(separator());
    }

    @Test
    public void testExpressionSeparatorSeparator() {
        this.expressionParseAndCheck(separator(), separator());
    }

    @Test
    public void testExpressionSeparatorSeparatorSeparator() {
        this.expressionParseAndCheck(separator(), separator(), separator());
    }

    @Test
    public void testExpressionNumberSeparatorSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::bigDecimal, digitNonZero()),
                separator(),
                separator(),
                separator());
    }

    @Test
    public void testExpressionGeneralSeparatorSeparatorSeparator() {
        this.expressionParseAndCheck(general(),
                separator(),
                separator(),
                separator());
    }

    @Test
    public void testExpressionSeparatorGeneralSeparatorSeparator() {
        this.expressionParseAndCheck(separator(),
                general(),
                separator(),
                separator());
    }

    @Test
    public void testExpressionSeparatorSeparatorGeneralSeparator() {
        this.expressionParseAndCheck(separator(),
                separator(),
                general(),
                separator());
    }

    @Test
    public void testExpressionSeparatorSeparatorSeparatorGeneral() {
        this.expressionParseAndCheck(separator(),
                separator(),
                separator(),
                general());
    }

    @Test
    public void testExpressionSeparatorSeparatorSeparatorTextPlaceholder() {
        this.expressionParseAndCheck(separator(),
                separator(),
                separator(),
                token(SpreadsheetFormatParserToken::text, textPlaceholder()));
    }

    // helpers..................................................................................................

    private void expressionParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.expressionParser(), SpreadsheetFormatParserToken::expression, tokens);
    }

    private Parser<SpreadsheetFormatParserContext> expressionParser() {
        return SpreadsheetFormatParsers.expression();
    }

    private SpreadsheetFormatParserToken token(final BiFunction<List<ParserToken>, String, SpreadsheetFormatParserToken> factory,
                                               final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        return factory.apply(list, ParserToken.text(list));
    }

    // helpers................................................................................................

    private void parseAndCheck2(final Parser<SpreadsheetFormatParserContext> parser,
                                final BiFunction<List<ParserToken>, String, SpreadsheetFormatParserToken> factory,
                                final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        final String text = ParserToken.text(list);

        assertEquals(text.toUpperCase(), text, "text should be all upper case");

        this.parseAndCheck(parser,
                text,
                factory.apply(list, text),
                text);

        final List<ParserToken> lower = Arrays.stream(tokens)
                .map(t -> SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor.toLower(t))
                .collect(Collectors.toList());
        final String textLower = text.toLowerCase();

        this.parseAndCheck(parser,
                textLower,
                factory.apply(lower, textLower),
                textLower);
    }

    private void parseThrows2(final Parser<SpreadsheetFormatParserContext> parser,
                              final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows(parser.orFailIfCursorNotEmpty(ParserReporters.basic()),
                ParserToken.text(Lists.of(tokens)));
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> createParser() {
        return SpreadsheetFormatParsers.expression();
    }

    @Override
    public SpreadsheetFormatParserContext createContext() {
        return SpreadsheetFormatParserContexts.basic(this.decimalNumberContext());
    }

    private static SpreadsheetFormatParserToken aSlashP() {
        return SpreadsheetFormatParserToken.amPm("A/P", "A/P");
    }

    private static SpreadsheetFormatParserToken amSlashPm() {
        return SpreadsheetFormatParserToken.amPm("AM/PM", "AM/PM");
    }

    private static SpreadsheetFormatParserToken bigDecimal() {
        final String text = "12.75";
        return SpreadsheetFormatParserToken.conditionNumber(new BigDecimal(text), text);
    }

    private static SpreadsheetFormatParserToken closeSquareBracket() {
        return SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]");
    }

    private static SpreadsheetFormatParserToken color() {
        final List<ParserToken> tokens = Lists.of(openSquareBracket(), colorLiteral(), whitespace(), colorNumberFive(), closeSquareBracket());
        return SpreadsheetFormatParserToken.color(tokens, ParserToken.text(tokens));
    }

    private static SpreadsheetFormatParserToken colorName(final String name) {
        return SpreadsheetFormatParserToken.colorName(name, name);
    }

    private static SpreadsheetFormatParserToken colorNumberFive() {
        return colorNumber(5);
    }

    private static SpreadsheetFormatParserToken colorLiteral() {
        return SpreadsheetFormatParserToken.colorLiteralSymbol("COLOR", "COLOR");
    }

    private static SpreadsheetFormatParserToken colorNumber(final int number) {
        return SpreadsheetFormatParserToken.colorNumber(number, String.valueOf(number));
    }

    private static SpreadsheetFormatParserToken conditionEquals() {
        final List<ParserToken> list = Lists.of(openSquareBracket(), whitespace(), equals(), bigDecimal(), closeSquareBracket());
        return SpreadsheetFormatParserToken.equalsParserToken(list, ParserToken.text(list));
    }

    private static SpreadsheetFormatParserToken conditionGreaterThanEquals() {
        final List<ParserToken> list = Lists.of(openSquareBracket(), greaterThanEquals(), bigDecimal(), closeSquareBracket());
        return SpreadsheetFormatParserToken.greaterThanEquals(list, ParserToken.text(list));
    }

    private static SpreadsheetFormatParserToken conditionGreaterThan() {
        final List<ParserToken> list = Lists.of(openSquareBracket(), greaterThan(), bigDecimal(), closeSquareBracket());
        return SpreadsheetFormatParserToken.greaterThan(list, ParserToken.text(list));
    }

    private static SpreadsheetFormatParserToken conditionLessThanEquals() {
        final List<ParserToken> list = Lists.of(openSquareBracket(), lessThanEquals(), bigDecimal(), closeSquareBracket());
        return SpreadsheetFormatParserToken.lessThanEquals(list, ParserToken.text(list));
    }

    private static SpreadsheetFormatParserToken conditionLessThan() {
        final List<ParserToken> list = Lists.of(openSquareBracket(), lessThan(), bigDecimal(), closeSquareBracket());
        return SpreadsheetFormatParserToken.lessThan(list, ParserToken.text(list));
    }

    private static SpreadsheetFormatParserToken conditionNotEquals() {
        final List<ParserToken> list = Lists.of(openSquareBracket(), notEquals(), bigDecimal(), closeSquareBracket());
        return SpreadsheetFormatParserToken.notEquals(list, ParserToken.text(list));
    }

    private static SpreadsheetFormatParserToken currency() {
        return SpreadsheetFormatParserToken.currency("$", "$");
    }

    private static SpreadsheetFormatParserToken day() {
        return day(1);
    }

    private static SpreadsheetFormatParserToken day(final int count) {
        final String text = repeat('D', count);
        return SpreadsheetFormatParserToken.day(text, text);
    }

    private static SpreadsheetFormatParserToken decimalPoint() {
        return SpreadsheetFormatParserToken.decimalPoint(".", ".");
    }

    private static SpreadsheetFormatParserToken digitNonZero() {
        return SpreadsheetFormatParserToken.digit("#", "#");
    }

    private static SpreadsheetFormatParserToken digitLeadingSpace() {
        return SpreadsheetFormatParserToken.digitLeadingSpace("?", "?");
    }

    private static SpreadsheetFormatParserToken digitLeadingZero() {
        return SpreadsheetFormatParserToken.digitLeadingZero("0", "0");
    }

    private static SpreadsheetFormatParserToken equals() {
        return SpreadsheetFormatParserToken.equalsSymbol("=", "=");
    }

    private static SpreadsheetFormatParserToken escaped() {
        return SpreadsheetFormatParserToken.escape('A', "\\A");
    }

    private static SpreadsheetFormatParserToken exponent1(final SpreadsheetFormatParserToken token) {
        final List<ParserToken> tokens = Lists.of(exponentSymbol(), token, digitLeadingSpace(), digitLeadingZero(), digitNonZero());
        return SpreadsheetFormatParserToken.exponent(tokens, ParserToken.text(tokens));
    }

    private static SpreadsheetFormatParserToken exponent2(final SpreadsheetFormatParserToken token) {
        final List<ParserToken> tokens = Lists.of(exponentSymbol(), digitLeadingSpace(), token, digitLeadingZero(), digitNonZero());
        return SpreadsheetFormatParserToken.exponent(tokens, ParserToken.text(tokens));
    }

    private static SpreadsheetFormatParserToken exponent3(final SpreadsheetFormatParserToken token) {
        final List<ParserToken> tokens = Lists.of(exponentSymbol(), digitLeadingSpace(), digitLeadingZero(), digitNonZero(), token);
        return SpreadsheetFormatParserToken.exponent(tokens, ParserToken.text(tokens));
    }

    private static SpreadsheetFormatParserToken exponentSymbol() {
        return SpreadsheetFormatParserToken.exponentSymbol("E+", "E+");
    }

    private static SpreadsheetFormatParserToken fraction() {
        return SpreadsheetFormatParserToken.fractionSymbol("/", "/");
    }

    private static SpreadsheetFormatParserToken general() {
        return SpreadsheetFormatParserToken.generalSymbol("GENERAL", "GENERAL");
    }

    private static SpreadsheetFormatParserToken greaterThan() {
        return SpreadsheetFormatParserToken.greaterThanSymbol(">", ">");
    }

    private static SpreadsheetFormatParserToken greaterThanEquals() {
        return SpreadsheetFormatParserToken.greaterThanEqualsSymbol(">=", ">=");
    }

    private static SpreadsheetFormatParserToken hour() {
        return hour(1);
    }

    private static SpreadsheetFormatParserToken hour(final int count) {
        final String text = repeat('H', count);
        return SpreadsheetFormatParserToken.hour(text, text);
    }

    private static SpreadsheetFormatParserToken lessThan() {
        return SpreadsheetFormatParserToken.lessThanSymbol("<", "<");
    }

    private static SpreadsheetFormatParserToken lessThanEquals() {
        return SpreadsheetFormatParserToken.lessThanEqualsSymbol("<=", "<=");
    }

    private static SpreadsheetFormatParserToken monthOrMinute() {
        return monthOrMinute(1);
    }

    private static SpreadsheetFormatParserToken monthOrMinute(final int count) {
        final String text = repeat('M', count);
        return SpreadsheetFormatParserToken.monthOrMinute(text, text);
    }

    private static SpreadsheetFormatParserToken notEquals() {
        return SpreadsheetFormatParserToken.notEqualsSymbol("!=", "!=");
    }

    private static SpreadsheetFormatParserToken openSquareBracket() {
        return SpreadsheetFormatParserToken.bracketOpenSymbol("[", "[");
    }

    private static SpreadsheetFormatParserToken percentage() {
        return SpreadsheetFormatParserToken.percentSymbol("%", "%");
    }

    private static SpreadsheetFormatParserToken quotedText() {
        return SpreadsheetFormatParserToken.quotedText("HELLO!", "\"HELLO!\"");
    }

    private static SpreadsheetFormatParserToken red() {
        return colorName("RED");
    }

    private static SpreadsheetFormatParserToken second() {
        return second(1);
    }

    private static SpreadsheetFormatParserToken second(final int count) {
        final String text = repeat('S', count);
        return SpreadsheetFormatParserToken.second(text, text);
    }

    private static SpreadsheetFormatParserToken separator() {
        return SpreadsheetFormatParserToken.separatorSymbol(";", ";");
    }

    private static SpreadsheetFormatParserToken star() {
        return SpreadsheetFormatParserToken.star('?', "*?");
    }

    private static SpreadsheetFormatParserToken star2() {
        return SpreadsheetFormatParserToken.star('#', "*#");
    }

    private static SpreadsheetFormatParserToken textPlaceholder() {
        return SpreadsheetFormatParserToken.textPlaceholder("@", "@");
    }

    private static SpreadsheetFormatParserToken textLiteralCloseParens() {
        return textLiteral(')');
    }

    private static SpreadsheetFormatParserToken textLiteralColon() {
        return textLiteral(':');
    }

    private static SpreadsheetFormatParserToken textLiteralDollar() {
        return textLiteral('$');
    }

    private static SpreadsheetFormatParserToken textLiteralMinus() {
        return textLiteral('-');
    }

    private static SpreadsheetFormatParserToken textLiteralPlus() {
        return textLiteral('+');
    }

    private static SpreadsheetFormatParserToken textLiteralOpenParens() {
        return textLiteral('(');
    }

    private static SpreadsheetFormatParserToken textLiteralSlash() {
        return textLiteral('/');
    }

    private static SpreadsheetFormatParserToken textLiteralSpace() {
        return textLiteral(' ');
    }

    private static SpreadsheetFormatParserToken textLiteral(final char c) {
        return SpreadsheetFormatParserToken.textLiteral("" + c, "" + c);
    }

    private static SpreadsheetFormatParserToken thousands() {
        return SpreadsheetFormatParserToken.thousands(",", ",");
    }

    private static SpreadsheetFormatParserToken underscore() {
        return SpreadsheetFormatParserToken.underscore('?', "_?");
    }

    private static SpreadsheetFormatParserToken underscore2() {
        return SpreadsheetFormatParserToken.underscore('#', "_#");
    }

    private static SpreadsheetFormatParserToken whitespace() {
        return SpreadsheetFormatParserToken.whitespace("   ", "   ");
    }

    private static SpreadsheetFormatParserToken year() {
        return year(1);
    }

    private static SpreadsheetFormatParserToken year(final int count) {
        final String text = repeat('Y', count);
        return SpreadsheetFormatParserToken.year(text, text);
    }

    private static String repeat(final char c, final int count) {
        final char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    @Override
    public String parserTokenTypeNamePrefix() {
        return "SpreadsheetFormat";
    }
}
