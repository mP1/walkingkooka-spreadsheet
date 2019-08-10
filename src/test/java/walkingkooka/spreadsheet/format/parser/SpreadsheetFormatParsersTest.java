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
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetFormatParsersTest extends SpreadsheetFormatParserTestCase implements PublicStaticHelperTesting<SpreadsheetFormatParsers>,
        ParserTesting2<Parser<SpreadsheetFormatParserContext>, SpreadsheetFormatParserContext> {

    // color............................................................................................................

    @Test
    public void testColorDigitNonZeroFails() {
        this.colorThrows(digitNonZero());
    }

    @Test
    public void testColorDigitZeroFails() {
        this.colorThrows(digitZero());
    }

    @Test
    public void testColorDigitSpaceFails() {
        this.colorThrows(digitSpace());
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
        this.colorParseAndCheck(bracketOpenSymbol(), red(), bracketCloseSymbol());
    }

    @Test
    public void testColorNameWhitespace() {
        this.colorParseAndCheck(bracketOpenSymbol(), red(), whitespace(), bracketCloseSymbol());
    }

    @Test
    public void testColorNumber() {
        this.colorParseAndCheck(bracketOpenSymbol(), colorLiteral(), whitespace(), colorNumberFive(), bracketCloseSymbol());
    }

    @Test
    public void testColorNumberWhitespace() {
        this.colorParseAndCheck(bracketOpenSymbol(), colorLiteral(), whitespace(), colorNumberFive(), whitespace(), bracketCloseSymbol());
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
    public void testConditionDigitZeroFails() {
        this.conditionParseThrows(digitZero());
    }

    @Test
    public void testConditionDigitSpaceFails() {
        this.conditionParseThrows(digitSpace());
    }

    @Test
    public void testConditionDollarFails() {
        this.conditionParseThrows(textLiteralDollar());
    }

    @Test
    public void testConditionFractionFails() {
        this.conditionParseThrows(fractionSymbol());
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
        this.conditionParseThrows(bracketOpenSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketEqualsFails() {
        this.conditionParseThrows(bracketOpenSymbol(), equalsSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanFails() {
        this.conditionParseThrows(bracketOpenSymbol(), greaterThan());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanEqualsFails() {
        this.conditionParseThrows(bracketOpenSymbol(), greaterThanEquals());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanFails() {
        this.conditionParseThrows(bracketOpenSymbol(), lessThan());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanEqualsFails() {
        this.conditionParseThrows(bracketOpenSymbol(), lessThanEquals());
    }

    @Test
    public void testConditionOpenSquareBracketNotEqualsFails() {
        this.conditionParseThrows(bracketOpenSymbol(), notEquals());
    }

    @Test
    public void testConditionOpenSquareBracketEqualsNumberFails() {
        this.conditionParseThrows(bracketOpenSymbol(), equalsSymbol(), conditionNumber());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanNumberFails() {
        this.conditionParseThrows(bracketOpenSymbol(), greaterThan(), conditionNumber());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanEqualsNumberFails() {
        this.conditionParseThrows(bracketOpenSymbol(), greaterThanEquals(), conditionNumber());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanNumberFails() {
        this.conditionParseThrows(bracketOpenSymbol(), lessThan(), conditionNumber());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanEqualsNumberFails() {
        this.conditionParseThrows(bracketOpenSymbol(), lessThanEquals(), conditionNumber());
    }

    @Test
    public void testConditionOpenSquareBracketNotEqualsNumberFails() {
        this.conditionParseThrows(bracketOpenSymbol(), notEquals(), conditionNumber());
    }

    @Test
    public void testConditionOpenSquareBracketEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::equalsParserToken,
                bracketOpenSymbol(), equalsSymbol(), conditionNumber(), bracketCloseSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::greaterThan,
                bracketOpenSymbol(), greaterThan(), conditionNumber(), bracketCloseSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketGreaterThanEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::greaterThanEquals,
                bracketOpenSymbol(), greaterThanEquals(), conditionNumber(), bracketCloseSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::lessThan,
                bracketOpenSymbol(), lessThan(), conditionNumber(), bracketCloseSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketLessThanEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::lessThanEquals,
                bracketOpenSymbol(), lessThanEquals(), conditionNumber(), bracketCloseSymbol());
    }

    @Test
    public void testConditionOpenSquareBracketNotEqualsNumber() {
        this.conditionParseAndCheck(SpreadsheetFormatParserToken::notEquals,
                bracketOpenSymbol(), notEquals(), conditionNumber(), bracketCloseSymbol());
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
    public void testDateTextDigitZeroFails() {
        this.dateParseThrows(digitZero());
    }

    @Test
    public void testDateTextDigitSpaceFails() {
        this.dateParseThrows(digitSpace());
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
    public void testDateGeneralFails() {
        this.dateParseThrows(generalSymbol());
    }

    @Test
    public void testDateEscaped() {
        this.dateParseAndCheck(escape());
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
        this.dateParseAndCheck(escape(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayEscapedMonthYear() {
        this.dateParseAndCheck(day(), escape(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthEscapedYear() {
        this.dateParseAndCheck(day(), monthOrMinute(), escape(), year());
    }

    @Test
    public void testDateDayMonthYearEscaped() {
        this.dateParseAndCheck(day(), monthOrMinute(), year(), escape());
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
        this.dateParseThrows(equalsSymbol(), day(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayEqualsMonthYearFails() {
        this.dateParseThrows(day(), equalsSymbol(), monthOrMinute(), year());
    }

    @Test
    public void testDateDayMonthEqualsYearFails() {
        this.dateParseThrows(day(), monthOrMinute(), equalsSymbol(), year());
    }

    @Test
    public void testDateDayMonthYearEqualsFails() {
        this.dateParseThrows(day(), monthOrMinute(), year(), equalsSymbol());
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
    public void testNumberDayFails() {
        this.numberParseThrows(digitNonZero(), day());
    }

    @Test
    public void testNumberHourFails() {
        this.numberParseThrows(digitNonZero(), hour());
    }

    @Test
    public void testNumberMinuteOrMonthFails() {
        this.numberParseThrows(digitNonZero(), monthOrMinute());
    }

    @Test
    public void testNumberSecondFails() {
        this.numberParseThrows(digitNonZero(), second());
    }

    @Test
    public void testNumberStarFails() {
        this.numberParseThrows(star());
    }

    @Test
    public void testNumberTextPlaceholderFails() {
        this.numberParseThrows(digitNonZero(), textPlaceholder());
    }

    @Test
    public void testNumberUnderscoreFails() {
        this.numberParseThrows(underscore());
    }

    @Test
    public void testNumberYearFails() {
        this.numberParseThrows(digitNonZero(), year());
    }

    @Test
    public void testNumberSlashFails() {
        this.numberParseThrows(fractionSymbol());
    }

    @Test
    public void testNumberDigitSpaceNumberFails() {
        this.numberParseThrows(digitSpace(), fractionSymbol());
    }

    @Test
    public void testNumberDigitZeroNumberFails() {
        this.numberParseThrows(digitZero(), fractionSymbol());
    }

    @Test
    public void testNumberDigitNonZeroNumberFails() {
        this.numberParseThrows(digitNonZero(), fractionSymbol());
    }

    @Test
    public void testNumberGeneralFails() {
        this.numberParseThrows(generalSymbol());
    }

    // literals only...........................................................................

    @Test
    public void testNumberEscaped() {
        this.numberParseAndCheck(escape());
    }

    @Test
    public void testNumberMinus() {
        this.numberParseAndCheck(textLiteralMinus());
    }

    @Test
    public void testNumberPlus() {
        this.numberParseAndCheck(textLiteralPlus());
    }

    @Test
    public void testNumberOpenParen() {
        this.numberParseAndCheck(textLiteralOpenParens());
    }

    @Test
    public void testNumberCloseParen() {
        this.numberParseAndCheck(textLiteralCloseParens());
    }

    @Test
    public void testNumberColon() {
        this.numberParseAndCheck(textLiteralColon());
    }

    @Test
    public void testNumberSpace() {
        this.numberParseAndCheck(textLiteralSpace());
    }

    @Test
    public void testNumberQuotedText() {
        this.numberParseAndCheck(quotedText());
    }

    // digitSpace

    @Test
    public void testNumberDigitSpaceNumberDigitSpace() {
        this.numberParseAndCheck(digitSpace(), decimalPoint(), digitSpace());
    }

    @Test
    public void testNumberDigitSpaceDigitSpaceNumberDigitSpace() {
        this.numberParseAndCheck(digitSpace(), digitSpace(), decimalPoint(), digitSpace());
    }

    @Test
    public void testNumberDigitSpaceDigitZeroNumberDigitSpace() {
        this.numberParseAndCheck(digitSpace(), digitZero(), decimalPoint(), digitSpace());
    }

    @Test
    public void testNumberDigitSpaceDigitNonZeroNumberDigitSpace() {
        this.numberParseAndCheck(digitSpace(), digitNonZero(), decimalPoint(), digitSpace());
    }

    @Test
    public void testNumberDigitSpaceNumberDigitSpaceDigitSpace() {
        this.numberParseAndCheck(digitSpace(), decimalPoint(), digitSpace(), digitSpace());
    }

    // digitSpace

    @Test
    public void testNumberDigitZeroDigitSpaceNumberDigitZero() {
        this.numberParseAndCheck(digitZero(), digitSpace(), decimalPoint(), digitZero());
    }

    @Test
    public void testNumberDigitZeroDigitZeroNumberDigitZero() {
        this.numberParseAndCheck(digitZero(), digitZero(), decimalPoint(), digitZero());
    }

    @Test
    public void testNumberDigitZeroDigitNonZeroNumberDigitZero() {
        this.numberParseAndCheck(digitZero(), digitNonZero(), decimalPoint(), digitZero());
    }

    @Test
    public void testNumberDigitZeroNumberDigitZeroDigitZero() {
        this.numberParseAndCheck(digitZero(), decimalPoint(), digitZero(), digitZero());
    }

    // digitZero

    @Test
    public void testNumberDigitNonZeroNumberDigitNonZero() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDigitSpaceNumberDigitNonZero() {
        this.numberParseAndCheck(digitNonZero(), digitSpace(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDigitZeroNumberDigitNonZero() {
        this.numberParseAndCheck(digitNonZero(), digitZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDigitNonZeroNumberDigitNonZero() {
        this.numberParseAndCheck(digitNonZero(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroNumberDigitNonZeroDigitNonZero() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), digitNonZero());
    }

    // currency

    @Test
    public void testNumberCurrencyDigitSlashDigit() {
        this.numberParseAndCheck(currency(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitCurrencySlashDigit() {
        this.numberParseAndCheck(digitNonZero(), currency(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashCurrencyDigit() {
        this.numberParseAndCheck(currency(), digitNonZero(), decimalPoint(), currency(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitCurrency() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), currency());
    }

    // percentage

    @Test
    public void testNumberPercentageDigitSlashDigit() {
        this.numberParseAndCheck(percentSymbol(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitPercentageSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), percentSymbol(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashPercentageDigit() {
        this.numberParseAndCheck(percentSymbol(), digitNonZero(), decimalPoint(), percentSymbol(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitPercentage() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), percentSymbol());
    }

    // thousands

    @Test
    public void testNumberThousandsDigitSlashDigit() {
        this.numberParseAndCheck(thousands(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitThousandsSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), thousands(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashThousandsDigit() {
        this.numberParseAndCheck(thousands(), digitNonZero(), decimalPoint(), thousands(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitThousands() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), thousands());
    }

    // text literals

    // escaped

    @Test
    public void testNumberDigitEscapedDigitSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), escape());
    }

    @Test
    public void testNumberDigitEscapedSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), escape(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashEscapedDigit() {
        this.numberParseAndCheck(escape(), digitNonZero(), decimalPoint(), escape(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitEscaped() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), escape());
    }

    // quotedText

    @Test
    public void testNumberQuotedTextDigitSlashDigit() {
        this.numberParseAndCheck(quotedText(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitQuotedTextSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), quotedText(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashQuotedTextDigit() {
        this.numberParseAndCheck(quotedText(), digitNonZero(), decimalPoint(), quotedText(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitQuotedText() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), quotedText());
    }

    // closeParens

    @Test
    public void testNumberCloseParensDigitSlashDigit() {
        this.numberParseAndCheck(textLiteralCloseParens(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitCloseParensSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), textLiteralCloseParens(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashCloseParensDigit() {
        this.numberParseAndCheck(textLiteralCloseParens(), digitNonZero(), decimalPoint(), textLiteralCloseParens(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitCloseParens() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testNumberColonDigitSlashDigit() {
        this.numberParseAndCheck(textLiteralColon(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitColonSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), textLiteralColon(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashColonDigit() {
        this.numberParseAndCheck(textLiteralColon(), digitNonZero(), decimalPoint(), textLiteralColon(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitColon() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralColon());
    }

    // minus

    @Test
    public void testNumberMinusDigitSlashDigit() {
        this.numberParseAndCheck(textLiteralMinus(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitMinusSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), textLiteralMinus(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashMinusDigit() {
        this.numberParseAndCheck(textLiteralMinus(), digitNonZero(), decimalPoint(), textLiteralMinus(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitMinus() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testNumberOpenParensDigitSlashDigit() {
        this.numberParseAndCheck(textLiteralOpenParens(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitOpenParensSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), textLiteralOpenParens(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashOpenParensDigit() {
        this.numberParseAndCheck(textLiteralOpenParens(), digitNonZero(), decimalPoint(), textLiteralOpenParens(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitOpenParens() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralOpenParens());
    }

    // plus

    @Test
    public void testNumberPlusDigitSlashDigit() {
        this.numberParseAndCheck(textLiteralPlus(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitPlusSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), textLiteralPlus(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashPlusDigit() {
        this.numberParseAndCheck(textLiteralPlus(), digitNonZero(), decimalPoint(), textLiteralPlus(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitPlus() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralPlus());
    }

    // space

    @Test
    public void testNumberSpaceDigitSlashDigit() {
        this.numberParseAndCheck(textLiteralSpace(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSpaceSlashDigit() {
        this.numberParseAndCheck(digitNonZero(), textLiteralSpace(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashSpaceDigit() {
        this.numberParseAndCheck(textLiteralSpace(), digitNonZero(), decimalPoint(), textLiteralSpace(), digitNonZero());
    }

    @Test
    public void testNumberDigitSlashDigitSpace() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), textLiteralSpace());
    }

    // equals

    @Test
    public void testNumberEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(equalsSymbol(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroEqualsDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), equalsSymbol(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointEqualsDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), equalsSymbol(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointDigitNonZeroEqualsFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), equalsSymbol());
    }

    // greaterThan

    @Test
    public void testNumberGreaterThanDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(greaterThan(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroGreaterThanDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), greaterThan(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointGreaterThanDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), greaterThan(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointDigitNonZeroGreaterThanFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testNumberGreaterThanEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(greaterThanEquals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroGreaterThanEqualsDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), greaterThanEquals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointGreaterThanEqualsDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), greaterThanEquals(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointDigitNonZeroGreaterThanEqualsFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testNumberLessThanDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(lessThan(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroLessThanDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), lessThan(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointLessThanDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), lessThan(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointDigitNonZeroLessThanFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testNumberLessThanEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(lessThanEquals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroLessThanEqualsDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), lessThanEquals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointLessThanEqualsDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), lessThanEquals(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointDigitNonZeroLessThanEqualsFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testNumberNotEqualsDigitNonZeroDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(notEquals(), digitNonZero(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroNotEqualsDecimalPointDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), notEquals(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointNotEqualsDigitNonZeroFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), notEquals(), digitNonZero());
    }

    @Test
    public void testNumberDigitNonZeroDecimalPointDigitNonZeroNotEqualsFails() {
        this.numberParseThrows(digitNonZero(), decimalPoint(), digitNonZero(), notEquals());
    }

    // exponent.............................................................................

    // currency

    @Test
    public void testNumberDigitExponentCurrencyDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(currency()));
    }

    @Test
    public void testNumberDigitExponentDigitCurrencyDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(currency()));
    }

    @Test
    public void testNumberDigitExponentDigitCurrency() {
        this.numberParseAndCheck(digitNonZero(), exponent3(currency()));
    }

    // text literals

    // escaped

    @Test
    public void testNumberDigitExponentEscapedDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(escape()));
    }

    @Test
    public void testNumberDigitExponentDigitEscapedDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(escape()));
    }

    @Test
    public void testNumberDigitExponentDigitEscaped() {
        this.numberParseAndCheck(digitNonZero(), exponent3(escape()));
    }

    // quotedText

    @Test
    public void testNumberDigitExponentQuotedTextDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(quotedText()));
    }

    @Test
    public void testNumberDigitExponentDigitQuotedTextDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(quotedText()));
    }

    @Test
    public void testNumberDigitExponentDigitQuotedText() {
        this.numberParseAndCheck(digitNonZero(), exponent3(quotedText()));
    }

    // closeParens

    @Test
    public void testNumberDigitExponentCloseParensDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(textLiteralCloseParens()));
    }

    @Test
    public void testNumberDigitExponentDigitCloseParensDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(textLiteralCloseParens()));
    }

    @Test
    public void testNumberDigitExponentDigitCloseParens() {
        this.numberParseAndCheck(digitNonZero(), exponent3(textLiteralCloseParens()));
    }

    // colon

    @Test
    public void testNumberDigitExponentColonDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(textLiteralColon()));
    }

    @Test
    public void testNumberDigitExponentDigitColonDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(textLiteralColon()));
    }

    @Test
    public void testNumberDigitExponentDigitColon() {
        this.numberParseAndCheck(digitNonZero(), exponent3(textLiteralColon()));
    }

    // minus

    @Test
    public void testNumberDigitExponentMinusDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(textLiteralMinus()));
    }

    @Test
    public void testNumberDigitExponentDigitMinusDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(textLiteralMinus()));
    }

    @Test
    public void testNumberDigitExponentDigitMinus() {
        this.numberParseAndCheck(digitNonZero(), exponent3(textLiteralMinus()));
    }

    // openParens

    @Test
    public void testNumberDigitExponentOpenParensDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(textLiteralOpenParens()));
    }

    @Test
    public void testNumberDigitExponentDigitOpenParensDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(textLiteralOpenParens()));
    }

    @Test
    public void testNumberDigitExponentDigitOpenParens() {
        this.numberParseAndCheck(digitNonZero(), exponent3(textLiteralOpenParens()));
    }

    // plus

    @Test
    public void testNumberDigitExponentPlusDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(textLiteralPlus()));
    }

    @Test
    public void testNumberDigitExponentDigitPlusDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(textLiteralPlus()));
    }

    @Test
    public void testNumberDigitExponentDigitPlus() {
        this.numberParseAndCheck(digitNonZero(), exponent3(textLiteralPlus()));
    }

    // space

    @Test
    public void testNumberDigitExponentSpaceDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(textLiteralSpace()));
    }

    @Test
    public void testNumberDigitExponentDigitSpaceDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(textLiteralSpace()));
    }

    @Test
    public void testNumberDigitExponentDigitSpace() {
        this.numberParseAndCheck(digitNonZero(), exponent3(textLiteralSpace()));
    }

    // equals

    @Test
    public void testNumberDigitExponentEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent1(equalsSymbol()));
    }

    @Test
    public void testNumberDigitExponentDigitEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent2(equalsSymbol()));
    }

    @Test
    public void testNumberDigitExponentDigitEquals() {
        this.numberParseThrows(digitNonZero(), exponent3(equalsSymbol()));
    }

    // greaterThan

    @Test
    public void testNumberDigitExponentGreaterThanDigit() {
        this.numberParseThrows(digitNonZero(), exponent1(greaterThan()));
    }

    @Test
    public void testNumberDigitExponentDigitGreaterThanDigit() {
        this.numberParseThrows(digitNonZero(), exponent2(greaterThan()));
    }

    @Test
    public void testNumberDigitExponentDigitGreaterThan() {
        this.numberParseThrows(digitNonZero(), exponent3(greaterThan()));
    }

    // greaterThanEquals

    @Test
    public void testNumberDigitExponentGreaterThanEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent1(greaterThanEquals()));
    }

    @Test
    public void testNumberDigitExponentDigitGreaterThanEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent2(greaterThanEquals()));
    }

    @Test
    public void testNumberDigitExponentDigitGreaterThanEquals() {
        this.numberParseThrows(digitNonZero(), exponent3(greaterThanEquals()));
    }

    // lessThan

    @Test
    public void testNumberDigitExponentLessThanDigit() {
        this.numberParseThrows(digitNonZero(), exponent1(lessThan()));
    }

    @Test
    public void testNumberDigitExponentDigitLessThanDigit() {
        this.numberParseThrows(digitNonZero(), exponent2(lessThan()));
    }

    @Test
    public void testNumberDigitExponentDigitLessThan() {
        this.numberParseThrows(digitNonZero(), exponent3(lessThan()));
    }

    // lessThanEquals

    @Test
    public void testNumberDigitExponentLessThanEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent1(lessThanEquals()));
    }

    @Test
    public void testNumberDigitExponentDigitLessThanEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent2(lessThanEquals()));
    }

    @Test
    public void testNumberDigitExponentDigitLessThanEquals() {
        this.numberParseThrows(digitNonZero(), exponent3(lessThanEquals()));
    }

    // notEquals

    @Test
    public void testNumberDigitExponentNotEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent1(notEquals()));
    }

    @Test
    public void testNumberDigitExponentDigitNotEqualsDigit() {
        this.numberParseThrows(digitNonZero(), exponent2(notEquals()));
    }

    @Test
    public void testNumberDigitExponentDigitNotEquals() {
        this.numberParseThrows(digitNonZero(), exponent3(notEquals()));
    }

    // color

    @Test
    public void testNumberColorDigit() {
        this.numberParseAndCheck(color(), digitNonZero());
    }

    @Test
    public void testNumberDigitColor() {
        this.numberParseAndCheck(digitNonZero(), color());
    }

    @Test
    public void testNumberDigitDecimalColor() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), color());
    }

    @Test
    public void testNumberDigitDecimalDigitColor() {
        this.numberParseAndCheck(digitNonZero(), decimalPoint(), digitNonZero(), color());
    }

    @Test
    public void testNumberDigitExponentColorDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent1(color()));
    }

    @Test
    public void testNumberDigitExponentDigitColorDigit() {
        this.numberParseAndCheck(digitNonZero(), exponent2(color()));
    }

    @Test
    public void testNumberDigitExponentDigitColor() {
        this.numberParseAndCheck(digitNonZero(), exponent3(color()));
    }

    // condition

    @Test
    public void testNumberConditionEqualsNumber() {
        this.numberParseAndCheck(conditionEquals(), digitNonZero());
    }

    @Test
    public void testNumberConditionGreaterThanNumber() {
        this.numberParseAndCheck(conditionGreaterThan(), digitNonZero());
    }

    @Test
    public void testNumberConditionGreaterThanEqualsNumber() {
        this.numberParseAndCheck(conditionGreaterThanEquals(), digitNonZero());
    }

    @Test
    public void testNumberConditionLessThanNumber() {
        this.numberParseAndCheck(conditionLessThan(), digitNonZero());
    }

    @Test
    public void testNumberConditionLessThanEqualsNumber() {
        this.numberParseAndCheck(conditionLessThanEquals(), digitNonZero());
    }

    @Test
    public void testNumberConditionNotEqualsNumber() {
        this.numberParseAndCheck(conditionNotEquals(), digitNonZero());
    }

    @Test
    public void testNumberNumberConditionEquals() {
        this.numberParseAndCheck(digitNonZero(), conditionEquals());
    }

    @Test
    public void testNumberNumberConditionGreaterThan() {
        this.numberParseAndCheck(digitNonZero(), conditionGreaterThan());
    }

    @Test
    public void testNumberNumberConditionGreaterThanEquals() {
        this.numberParseAndCheck(digitNonZero(), conditionGreaterThanEquals());
    }

    @Test
    public void testNumberNumberConditionLessThan() {
        this.numberParseAndCheck(digitNonZero(), conditionLessThan());
    }

    @Test
    public void testNumberNumberConditionLessThanEquals() {
        this.numberParseAndCheck(digitNonZero(), conditionLessThanEquals());
    }

    @Test
    public void testNumberNumberConditionNotEquals() {
        this.numberParseAndCheck(digitNonZero(), conditionNotEquals());
    }

    // number helpers...

    private void numberParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(this.numberParser(), SpreadsheetFormatParserToken::number, tokens);
    }

    private void numberParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(this.numberParser(), tokens);
    }

    private Parser<SpreadsheetFormatParserContext> numberParser() {
        return SpreadsheetFormatParsers.number();
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
        this.fractionParseThrows(fractionSymbol());
    }

    @Test
    public void testFractionDigitSpaceFractionFails() {
        this.fractionParseThrows(digitSpace(), fractionSymbol());
    }

    @Test
    public void testFractionDigitZeroFractionFails() {
        this.fractionParseThrows(digitZero(), fractionSymbol());
    }

    @Test
    public void testFractionDigitNonZeroFractionFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol());
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
    public void testFractionGeneralFails() {
        this.fractionParseThrows(generalSymbol());
    }

    // digitSpace

    @Test
    public void testFractionDigitSpaceFractionDigitSpace() {
        this.fractionParseAndCheck(digitSpace(), fractionSymbol(), digitSpace());
    }

    @Test
    public void testFractionDigitSpaceDigitSpaceFractionDigitSpace() {
        this.fractionParseAndCheck(digitSpace(), digitSpace(), fractionSymbol(), digitSpace());
    }

    @Test
    public void testFractionDigitSpaceDigitZeroFractionDigitSpace() {
        this.fractionParseAndCheck(digitSpace(), digitZero(), fractionSymbol(), digitSpace());
    }

    @Test
    public void testFractionDigitSpaceDigitNonZeroFractionDigitSpace() {
        this.fractionParseAndCheck(digitSpace(), digitNonZero(), fractionSymbol(), digitSpace());
    }

    @Test
    public void testFractionDigitSpaceFractionDigitSpaceDigitSpace() {
        this.fractionParseAndCheck(digitSpace(), fractionSymbol(), digitSpace(), digitSpace());
    }

    // digitSpace

    @Test
    public void testFractionDigitZeroDigitSpaceFractionDigitZero() {
        this.fractionParseAndCheck(digitZero(), digitSpace(), fractionSymbol(), digitZero());
    }

    @Test
    public void testFractionDigitZeroDigitZeroFractionDigitZero() {
        this.fractionParseAndCheck(digitZero(), digitZero(), fractionSymbol(), digitZero());
    }

    @Test
    public void testFractionDigitZeroDigitNonZeroFractionDigitZero() {
        this.fractionParseAndCheck(digitZero(), digitNonZero(), fractionSymbol(), digitZero());
    }

    @Test
    public void testFractionDigitZeroFractionDigitZeroDigitZero() {
        this.fractionParseAndCheck(digitZero(), fractionSymbol(), digitZero(), digitZero());
    }

    // digitSpace

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroDigitSpaceFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), digitSpace(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroDigitZeroFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), digitZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroDigitNonZeroFractionDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroDigitNonZero() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), digitNonZero());
    }

    // currency

    @Test
    public void testFractioncurrencyDigitSlashDigit() {
        this.fractionParseAndCheck(currency(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitcurrencySlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), currency(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashcurrencyDigit() {
        this.fractionParseAndCheck(currency(), digitNonZero(), fractionSymbol(), currency(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitcurrency() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), currency());
    }

    // text literals

    // escaped

    @Test
    public void testFractionEscapedDigitSlashDigit() {
        this.fractionParseAndCheck(escape(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitEscapedSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), escape(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashEscapedDigit() {
        this.fractionParseAndCheck(escape(), digitNonZero(), fractionSymbol(), escape(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitEscaped() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), escape());
    }

    // quotedText

    @Test
    public void testFractionQuotedTextDigitSlashDigit() {
        this.fractionParseAndCheck(quotedText(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitQuotedTextSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), quotedText(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashQuotedTextDigit() {
        this.fractionParseAndCheck(quotedText(), digitNonZero(), fractionSymbol(), quotedText(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitQuotedText() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), quotedText());
    }

    // closeParens

    @Test
    public void testFractionCloseParensDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralCloseParens(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitCloseParensSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralCloseParens(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashCloseParensDigit() {
        this.fractionParseAndCheck(textLiteralCloseParens(), digitNonZero(), fractionSymbol(), textLiteralCloseParens(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitCloseParens() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testFractionColonDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralColon(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitColonSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralColon(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashColonDigit() {
        this.fractionParseAndCheck(textLiteralColon(), digitNonZero(), fractionSymbol(), textLiteralColon(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitColon() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), textLiteralColon());
    }

    // minus

    @Test
    public void testFractionMinusDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralMinus(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitMinusSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralMinus(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashMinusDigit() {
        this.fractionParseAndCheck(textLiteralMinus(), digitNonZero(), fractionSymbol(), textLiteralMinus(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitMinus() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testFractionOpenParensDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralOpenParens(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitOpenParensSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralOpenParens(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashOpenParensDigit() {
        this.fractionParseAndCheck(textLiteralOpenParens(), digitNonZero(), fractionSymbol(), textLiteralOpenParens(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitOpenParens() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), textLiteralOpenParens());
    }

    // percentage

    @Test
    public void testFractionPercentageDigitSlashDigit() {
        this.fractionParseAndCheck(percentSymbol(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitPercentageSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), percentSymbol(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashPercentageDigit() {
        this.fractionParseAndCheck(percentSymbol(), digitNonZero(), fractionSymbol(), percentSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitPercentage() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), percentSymbol());
    }

    // plus

    @Test
    public void testFractionPlusDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralPlus(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitPlusSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralPlus(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashPlusDigit() {
        this.fractionParseAndCheck(textLiteralPlus(), digitNonZero(), fractionSymbol(), textLiteralPlus(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitPlus() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), textLiteralPlus());
    }

    // space

    @Test
    public void testFractionSpaceDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralSpace(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSpaceSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), textLiteralSpace(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashSpaceDigit() {
        this.fractionParseAndCheck(textLiteralSpace(), digitNonZero(), fractionSymbol(), textLiteralSpace(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitSpace() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), textLiteralSpace());
    }

    // thousands

    @Test
    public void testFractionThousandsDigitSlashDigit() {
        this.fractionParseAndCheck(thousands(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitThousandsSlashDigit() {
        this.fractionParseAndCheck(digitNonZero(), thousands(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashThousandsDigit() {
        this.fractionParseAndCheck(thousands(), digitNonZero(), fractionSymbol(), thousands(), digitNonZero());
    }

    @Test
    public void testFractionDigitSlashDigitThousands() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), thousands());
    }

    // equals

    @Test
    public void testFractionEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(equalsSymbol(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), equalsSymbol(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), equalsSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), digitNonZero(), equalsSymbol());
    }

    // greaterThan

    @Test
    public void testFractionGreaterThanDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(greaterThan(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroGreaterThanFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), greaterThan(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionGreaterThanDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), greaterThan(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroGreaterThanFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), digitNonZero(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testFractionGreaterThanEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(greaterThanEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroGreaterThanEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), greaterThanEquals(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionGreaterThanEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), greaterThanEquals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroGreaterThanEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), digitNonZero(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testFractionLessThanDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(lessThan(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroLessThanFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), lessThan(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionLessThanDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), lessThan(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroLessThanFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), digitNonZero(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testFractionLessThanEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(lessThanEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroLessThanEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), lessThanEquals(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionLessThanEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), lessThanEquals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroLessThanEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), digitNonZero(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testFractionNotEqualsDigitNonZeroFractionDigitNonZeroFails() {
        this.fractionParseThrows(notEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroNotEqualsFractionDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), notEquals(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionNotEqualsDigitNonZeroFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), notEquals(), digitNonZero());
    }

    @Test
    public void testFractionDigitNonZeroFractionDigitNonZeroNotEqualsFails() {
        this.fractionParseThrows(digitNonZero(), fractionSymbol(), digitNonZero(), notEquals());
    }

    // color

    @Test
    public void testFractionColorDigitFractionDigit() {
        this.fractionParseAndCheck(color(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitColorFractionDigit() {
        this.fractionParseAndCheck(digitNonZero(), color(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitColorDigitFractionDigit() {
        this.fractionParseAndCheck(digitNonZero(), color(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionDigitFractionColorDigit() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), color(), digitNonZero());
    }

    @Test
    public void testFractionDigitFractionDigitColorDigit() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), color(), digitNonZero());
    }

    @Test
    public void testFractionDigitFractionDigitColor() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), color());
    }

    // condition

    @Test
    public void testFractionConditionEqualsFraction() {
        this.fractionParseAndCheck(conditionEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionConditionGreaterThanFraction() {
        this.fractionParseAndCheck(conditionGreaterThan(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionConditionGreaterThanEqualsFraction() {
        this.fractionParseAndCheck(conditionGreaterThanEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionConditionLessThanFraction() {
        this.fractionParseAndCheck(conditionLessThan(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionConditionLessThanEqualsFraction() {
        this.fractionParseAndCheck(conditionLessThanEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionConditionNotEqualsFraction() {
        this.fractionParseAndCheck(conditionNotEquals(), digitNonZero(), fractionSymbol(), digitNonZero());
    }

    @Test
    public void testFractionFractionConditionEquals() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), conditionEquals());
    }

    @Test
    public void testFractionFractionConditionGreaterThan() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), conditionGreaterThan());
    }

    @Test
    public void testFractionFractionConditionGreaterThanEquals() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), conditionGreaterThanEquals());
    }

    @Test
    public void testFractionFractionConditionLessThan() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), conditionLessThan());
    }

    @Test
    public void testFractionFractionConditionLessThanEquals() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), conditionLessThanEquals());
    }

    @Test
    public void testFractionFractionConditionNotEquals() {
        this.fractionParseAndCheck(digitNonZero(), fractionSymbol(), digitNonZero(), conditionNotEquals());
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
        this.generalParseAndCheck(generalSymbol());
    }

    @Test
    public void testGeneralWhitespaceGeneral() {
        this.generalParseAndCheck(whitespace(), generalSymbol());
    }

    @Test
    public void testGeneralGeneralWhitespace() {
        this.generalParseAndCheck(generalSymbol(), whitespace());
    }

    @Test
    public void testGeneralColorGeneral() {
        this.generalParseAndCheck(color(), generalSymbol());
    }

    @Test
    public void testGeneralColorWhitespaceGeneral() {
        this.generalParseAndCheck(color(), whitespace(), generalSymbol());
    }

    @Test
    public void testGeneralGeneralColor() {
        this.generalParseAndCheck(generalSymbol(), color());
    }

    @Test
    public void testGeneralGeneralColorWhitespace() {
        this.generalParseAndCheck(generalSymbol(), color(), whitespace());
    }

    @Test
    public void testGeneralGeneralWhitespaceColor() {
        this.generalParseAndCheck(generalSymbol(), whitespace(), color());
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
    public void testTextTextDigitZeroFails() {
        this.textParseThrows(digitZero());
    }

    @Test
    public void testTextTextDigitSpaceFails() {
        this.textParseThrows(digitSpace());
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
        this.textParseThrows(generalSymbol());
    }

    @Test
    public void testTextEscaped() {
        this.textParseAndCheck(escape());
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
        this.textParseThrows(equalsSymbol());
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
    public void testTimeTextDigitZeroFails() {
        this.timeParseThrows(digitZero());
    }

    @Test
    public void testTimeTextDigitSpaceFails() {
        this.timeParseThrows(digitSpace());
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
    public void testTimeGeneralFails() {
        this.timeParseThrows(generalSymbol());
    }

    @Test
    public void testTimeEscaped() {
        this.timeParseAndCheck(escape());
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
        this.timeParseAndCheck(escape(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourEscapedMonthSecond() {
        this.timeParseAndCheck(hour(), escape(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthEscapedSecond() {
        this.timeParseAndCheck(hour(), monthOrMinute(), escape(), second());
    }

    @Test
    public void testTimeHourMonthSecondsEscaped() {
        this.timeParseAndCheck(hour(), monthOrMinute(), second(), escape());
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
        this.timeParseThrows(equalsSymbol(), hour(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourEqualsMonthSecondFails() {
        this.timeParseThrows(hour(), equalsSymbol(), monthOrMinute(), second());
    }

    @Test
    public void testTimeHourMonthEqualsSecondFails() {
        this.timeParseThrows(hour(), monthOrMinute(), equalsSymbol(), second());
    }

    @Test
    public void testTimeHourMonthSecondsEqualsFails() {
        this.timeParseThrows(hour(), monthOrMinute(), second(), equalsSymbol());
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

    // dateAndTime......................................................................................................

    @Test
    public void testDateTimeGeneralFails() {
        this.parseThrows2(this.dateTimeParser(), generalSymbol());
    }

    // literals only....................................................................................................

    @Test
    public void testDateTimeEscaped() {
        this.dateTimeParseAndCheck(escape());
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

    // time with millis..................................................................................................

    @Test
    public void testDateTimeSecondsDecimalFails() {
        this.dateParseThrows(second(), decimalPoint());
    }

    @Test
    public void testDateTimeSecondsDecimalNonZeroFails() {
        this.dateParseThrows(second(), decimalPoint(), digitNonZero());
    }

    @Test
    public void testDateTimeSecondsDecimalSpaceFails() {
        this.dateParseThrows(second(), decimalPoint(), digitSpace());
    }

    @Test
    public void testDateTimeSecondsDecimalDigitZero() {
        this.dateTimeParseAndCheck(second(), decimalPoint(), digitZero());
    }

    @Test
    public void testDateTimeSecondsDecimalDigitZeroZero() {
        this.dateTimeParseAndCheck(second(), decimalPoint(), digitZero(), digitZero());
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
    public void testDateTimeDayMonthYearHourMinuteSecondMillis() {
        this.dateTimeParseAndCheck(day(),
                monthOrMinute(),
                year(),
                hour(),
                monthOrMinute(),
                second(),
                day(),
                monthOrMinute(),
                year(),
                hour(),
                monthOrMinute(),
                second(),
                decimalPoint(),
                digitZero());
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
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, escape()));
    }

    @Test
    public void testExpressionDollarFails() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, currency()));
    }

    @Test
    public void testExpressionMinus() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, textLiteralMinus()));
    }

    @Test
    public void testExpressionPlus() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, textLiteralPlus()));
    }

    @Test
    public void testExpressionSlashFails() {
        this.parseFailAndCheck(this.expressionParser(), "/");
    }

    @Test
    public void testExpressionOpenParen() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, textLiteralOpenParens()));
    }

    @Test
    public void testExpressionCloseParen() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, textLiteralCloseParens()));
    }

    @Test
    public void testExpressionColon() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, textLiteralColon()));
    }

    @Test
    public void testExpressionSpace() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, textLiteralSpace()));
    }

    @Test
    public void testExpressionQuotedText() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, quotedText()));
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
    public void testExpressionDateTimeDayMonthMinuteHourSecondMills() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, monthOrMinute(), hour(), second(), decimalPoint(), digitZero()));
    }

    @Test
    public void testExpressionDateTimeHourMinuteSecondAmPm() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::dateTime, hour(), monthOrMinute(), second(), amSlashPm()));
    }

    @Test
    public void testExpressionFractionDigitSlashDigit() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::fraction, digitNonZero(), fractionSymbol(), digitNonZero()));
    }

    @Test
    public void testExpressionNumberDigit() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()));
    }

    @Test
    public void testExpressionNumberDigitDecimalPointDigit() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero(), decimalPoint(), digitNonZero()));
    }

    @Test
    public void testExpressionNumberSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator());
    }

    @Test
    public void testExpressionNumberSeparatorNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()));
    }

    @Test
    public void testExpressionNumberSeparatorNumberSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator());
    }

    @Test
    public void testExpressionNumberSeparatorNumberSeparatorNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()));
    }

    @Test
    public void testExpressionNumberSeparatorNumberSeparatorNumberSeparator() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()));
    }

    @Test
    public void testExpressionConditionNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, conditionEquals(), digitNonZero()));
    }

    @Test
    public void testExpressionConditionNumberSeparatorConditionNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, conditionEquals(), digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, conditionEquals(), digitNonZero()));
    }

    @Test
    public void testExpressionConditionNumberSeparatorConditionNumberSeparatorConditionNumber() {
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, conditionEquals(), digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, conditionEquals(), digitNonZero()),
                separator(),
                token(SpreadsheetFormatParserToken::number, conditionEquals(), digitNonZero()));
    }

    // empty separators......................................................................................

    @Test
    public void testExpressionSeparatorSeparatorSeparatorNumberFails() {
        this.parseThrows2(this.expressionParser(),
                separator(),
                separator(),
                separator(),
                token(SpreadsheetFormatParserToken::number, digitNonZero()));
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
        this.expressionParseAndCheck(token(SpreadsheetFormatParserToken::number, digitNonZero()),
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
        return SpreadsheetFormatParserContexts.basic();
    }

    // PublicStaticHelperTesting........................................................................................

    @Override
    public Class<SpreadsheetFormatParsers> type() {
        return SpreadsheetFormatParsers.class;
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
