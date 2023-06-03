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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class SpreadsheetFormatParsersTest extends SpreadsheetFormatParserTestCase implements PublicStaticHelperTesting<SpreadsheetFormatParsers>,
        ParserTesting2<Parser<SpreadsheetFormatParserContext>, SpreadsheetFormatParserContext> {

    // snakeCaseParserClassSimpleName...................................................................................

    @Test
    public void testSnakeCaseParserClassSimpleNameSpreadsheetFormatFractionSymbolParserToken() {
        this.snakeCaseParserClassSimpleNameAndCheck(SpreadsheetFormatFractionSymbolParserToken.class, "FRACTION");
    }

    @Test
    public void testSnakeCaseParserClassSimpleNameSpreadsheetFormatGreaterThanSymbolParserToken() {
        this.snakeCaseParserClassSimpleNameAndCheck(SpreadsheetFormatGreaterThanSymbolParserToken.class, "GREATER_THAN");
    }

    @Test
    public void testSnakeCaseParserClassSimpleNameSpreadsheetFormatGreaterThanEqualsSymbolParserToken() {
        this.snakeCaseParserClassSimpleNameAndCheck(SpreadsheetFormatGreaterThanEqualsSymbolParserToken.class, "GREATER_THAN_EQUALS");
    }

    @Test
    public void testSnakeCaseParserClassSimpleNameSpreadsheetFormatEscapeParserToken() {
        this.snakeCaseParserClassSimpleNameAndCheck(SpreadsheetFormatEscapeParserToken.class, "ESCAPE");
    }

    @Test
    public void testSnakeCaseParserClassSimpleNameSpreadsheetFormatStarParserToken() {
        this.snakeCaseParserClassSimpleNameAndCheck(SpreadsheetFormatStarParserToken.class, "STAR");
    }

    @Test
    public void testSnakeCaseParserClassSimpleNameSpreadsheetFormatUnderscoreParserToken() {
        this.snakeCaseParserClassSimpleNameAndCheck(SpreadsheetFormatUnderscoreParserToken.class, "UNDERSCORE");
    }

    private void snakeCaseParserClassSimpleNameAndCheck(final Class<? extends SpreadsheetFormatParserToken> type,
                                                        final String expected) {
        this.checkEquals(expected,
                SpreadsheetFormatParsers.snakeCaseParserClassSimpleName(type),
                () -> "snakeCaseParserClassSimpleName " + type.getSimpleName());
    }

    // color............................................................................................................

    @Test
    public void testColorDigitFails() {
        this.colorThrows(digit());
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
    public void testColorMinuteFails() {
        this.colorThrows(minute());
    }

    @Test
    public void testColorMonthFails() {
        this.colorThrows(month());
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
        this.colorParseAndCheck(
                bracketOpenSymbol(),
                red(),
                bracketCloseSymbol()
        );
    }

    @Test
    public void testColorNameWhitespace() {
        this.colorParseAndCheck(
                bracketOpenSymbol(),
                red(),
                whitespace3(),
                bracketCloseSymbol()
        );
    }

    @Test
    public void testColorNumber() {
        this.colorParseAndCheck(
                bracketOpenSymbol(),
                colorLiteral(),
                colorNumberFive(),
                bracketCloseSymbol()
        );
    }

    @Test
    public void testColorWhitespaceNumber() {
        this.colorParseAndCheck(
                bracketOpenSymbol(),
                colorLiteral(),
                whitespace3(),
                colorNumberFive(),
                bracketCloseSymbol()
        );
    }

    @Test
    public void testColorWhitespaceNumberWhitespace() {
        this.colorParseAndCheck(
                bracketOpenSymbol(),
                colorLiteral(),
                whitespace3(),
                colorNumberFive(),
                whitespace3(),
                bracketCloseSymbol()
        );
    }

    private void colorParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck3(
                this.colorParser(),
                SpreadsheetFormatParserToken::color,
                tokens
        );
    }

    private void colorThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                this.colorParser(),
                tokens
        );
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
    public void testConditionDigitFails() {
        this.conditionParseThrows(digit());
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
    public void testConditionMonthFails() {
        this.conditionParseThrows(month());
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
        this.conditionParseThrows(whitespace());
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
        this.parseAndCheck3(
                this.conditionParser(),
                factory,
                tokens
        );
    }

    private void conditionParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                this.conditionParser(),
                tokens
        );
    }

    private Parser<SpreadsheetFormatParserContext> conditionParser() {
        return SpreadsheetFormatParsers.condition();
    }

    // date format......................................................................................................

    @Test
    public void testDateFormatEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.dateFormat(),
                ""
        );
    }

    @Test
    public void testDateFormatSeparator() {
        this.dateFormatParseAndCheck(
                separator()
        );
    }

    @Test
    public void testDateFormatSeparatorSeparator() {
        this.dateFormatParseAndCheck(
                separator(),
                separator()
        );
    }

    @Test
    public void testDateFormatSeparatorSeparatorSeparator() {
        this.dateFormatParseAndCheck(
                separator(),
                separator(),
                separator()
        );
    }

    @Test
    public void testDateFormatGeneral() {
        this.dateFormatParseAndCheck(
                general()
        );
    }

    @Test
    public void testDateFormatWhitespaceGeneral() {
        this.dateFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateFormatGeneralWhitespace() {
        this.dateFormatParseAndCheck(
                general(
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateFormatWhitespaceGeneralWhitespace() {
        this.dateFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateFormatColorGeneral() {
        this.dateFormatParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateFormatColorWhitespaceGeneral() {
        this.dateFormatParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateFormatColorEscaped() {
        this.dateFormatParseAndCheck(
                date(
                        color(),
                        escape()
                )
        );
    }

    @Test
    public void testDateFormatTextDigitFails() {
        this.dateFormatParseThrows(
                digit()
        );
    }

    @Test
    public void testDateFormatTextDigitZeroFails() {
        this.dateFormatParseThrows(
                digitZero()
        );
    }

    @Test
    public void testDateFormatTextDigitSpaceFails() {
        this.dateFormatParseThrows(
                digitSpace()
        );
    }

    @Test
    public void testDateFormatHourFails() {
        this.dateFormatParseThrows(
                hour()
        );
    }

    @Test
    public void testDateFormatSecondFails() {
        this.dateFormatParseThrows(
                second()
        );
    }

    @Test
    public void testDateFormatTextPlaceholderFails() {
        this.dateFormatParseThrows(
                textPlaceholder()
        );
    }

    @Test
    public void testDateFormatEscaped() {
        this.dateFormatParseAndCheck(
                date(
                        escape()
                )
        );
    }

    @Test
    public void testDateFormatDollar() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testDateFormatMinus() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testDateFormatPlus() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testDateFormatSlash() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testDateFormatOpenParen() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testDateFormatCloseParen() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testDateFormatColon() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testDateFormatSpace() {
        this.dateFormatParseAndCheck(
                date(
                        whitespace()
                )
        );
    }

    @Test
    public void testDateFormatQuotedText() {
        this.dateFormatParseAndCheck(
                date(
                        quotedText()
                )
        );
    }

    @Test
    public void testDateFormatDay() {
        this.dateFormatParseAndCheck(
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateFormatMonth() {
        this.dateFormatParseAndCheck(
                date(
                        month()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDay2Month2Year2() {
        this.dateFormatParseAndCheck(
                date(
                        day(2),
                        month(2),
                        year(2)
                )
        );
    }

    @Test
    public void testDateFormatDay3Month3Year3() {
        this.dateFormatParseAndCheck(
                date(
                        day(3),
                        month(3),
                        year(3)
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearDateDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatMonthDayYear() {
        this.dateFormatParseAndCheck(
                date(
                        month(),
                        day(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatYearMonthDay() {
        this.dateFormatParseAndCheck(
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    @Test
    public void testDateFormatYearCommaMonthCommaDay() {
        this.dateFormatParseAndCheck(
                date(
                        year(),
                        textLiteralComma(),
                        month(),
                        textLiteralComma(),
                        day()
                )
        );
    }

    // escaped

    @Test
    public void testDateFormatEscapedDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        escape(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayEscapedMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        escape(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthEscapedYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        escape(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearEscaped() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        escape()
                )
        );
    }

    // quotedText

    @Test
    public void testDateFormatQuotedTextDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        quotedText(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayQuotedTextMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        quotedText(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthQuotedTextYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        quotedText(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearQuotedText() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        quotedText()
                )
        );
    }

    // closeParens

    @Test
    public void testDateFormatCloseParensDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralCloseParens(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayCloseParensMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralCloseParens(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthCloseParensYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralCloseParens(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearCloseParens() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralCloseParens()
                )
        );
    }

    // colon

    @Test
    public void testDateFormatColonDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralColon(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayColonMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralColon(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthColonYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralColon(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearColon() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralColon()

                )
        );
    }

    // dollar

    @Test
    public void testDateFormatDollarDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralDollar(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayDollarMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralDollar(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthDollarYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralDollar(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearDollar() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralDollar()
                )
        );
    }

    // minus

    @Test
    public void testDateFormatMinusDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralMinus(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMinusMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralMinus(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthMinusYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralMinus(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearMinus() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralMinus()
                )
        );
    }

    // openParens

    @Test
    public void testDateFormatOpenParensDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralOpenParens(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayOpenParensMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralOpenParens(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthOpenParensYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralOpenParens(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearOpenParens() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralOpenParens()
                )
        );
    }

    // plus

    @Test
    public void testDateFormatPlusDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralPlus(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayPlusMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralPlus(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthPlusYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralPlus(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearPlus() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralPlus()
                )
        );
    }

    // slash

    @Test
    public void testDateFormatSlashDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        textLiteralSlash(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDaySlashMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        textLiteralSlash(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthSlashYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralSlash(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearSlash() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralSlash()
                )
        );
    }

    // space

    @Test
    public void testDateFormatSpaceDayMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        whitespace(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDaySpaceMonthYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        whitespace(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthSpaceYear() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        whitespace(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayMonthYearSpace() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        whitespace()
                )
        );
    }

    // equals

    @Test
    public void testDateFormatEqualsDayMonthYearFails() {
        this.dateFormatParseThrows(
                equalsSymbol(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayEqualsMonthYearFails() {
        this.dateFormatParseThrows(
                day(),
                equalsSymbol(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthEqualsYearFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                equalsSymbol(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthYearEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                year(),
                equalsSymbol()
        );
    }

    // greaterThan

    @Test
    public void testDateFormatGreaterThanDayMonthYearFails() {
        this.dateFormatParseThrows(
                greaterThan(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayGreaterThanMonthYearFails() {
        this.dateFormatParseThrows(
                day(),
                greaterThan(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthGreaterThanYearFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                greaterThan(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthYearGreaterThanFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                year(),
                greaterThan()
        );
    }

    // greaterThanEquals

    @Test
    public void testDateFormatGreaterThanEqualsDayMonthYearFails() {
        this.dateFormatParseThrows(
                greaterThanEquals(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayGreaterThanEqualsMonthYearFails() {
        this.dateFormatParseThrows(
                day(),
                greaterThanEquals(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthGreaterThanEqualsYearFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                greaterThanEquals(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthYearGreaterThanEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                year(),
                greaterThanEquals()
        );
    }

    // lessThan

    @Test
    public void testDateFormatLessThanDayMonthYearFails() {
        this.dateFormatParseThrows(
                lessThan(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayLessThanMonthYearFails() {
        this.dateFormatParseThrows(
                day(),
                lessThan(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthLessThanYearFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                lessThan(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthYearLessThanFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                year(),
                lessThan()
        );
    }

    @Test
    public void testDateFormatLessThanEqualsDayMonthYearFails() {
        this.dateFormatParseThrows(
                lessThanEquals(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayLessThanEqualsMonthYearFails() {
        this.dateFormatParseThrows(
                day(),
                lessThanEquals(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthLessThanEqualsYearFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                lessThanEquals(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthYearLessThanEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                year(),
                lessThanEquals()
        );
    }

    // notEquals

    @Test
    public void testDateFormatNotEqualsDayMonthYearFails() {
        this.dateFormatParseThrows(
                notEquals(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayNotEqualsMonthYearFails() {
        this.dateFormatParseThrows(
                day(),
                notEquals(),
                month(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthNotEqualsYearFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                notEquals(),
                year()
        );
    }

    @Test
    public void testDateFormatDayMonthYearNotEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                month(),
                year(),
                notEquals()
        );
    }

    // color

    @Test
    public void testDateFormatColorDay() {
        this.dateFormatParseAndCheck(
                date(
                        color(),
                        day()
                )
        );
    }

    @Test
    public void testDateFormatColorMonth() {
        this.dateFormatParseAndCheck(
                date(
                        color(),
                        month()
                )
        );
    }

    @Test
    public void testDateFormatColorYear() {
        this.dateFormatParseAndCheck(
                date(
                        color(),
                        year()
                )
        );
    }

    @Test
    public void testDateFormatDayColor() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        color()
                )
        );
    }

    @Test
    public void testDateFormatMonthColor() {
        this.dateFormatParseAndCheck(
                date(
                        month(),
                        color()
                )
        );
    }

    @Test
    public void testDateFormatYearColor() {
        this.dateFormatParseAndCheck(
                date(
                        year(),
                        color()
                )
        );
    }

    // condition

    @Test
    public void testDateFormatConditionEqualsDay() {
        this.dateFormatParseAndCheck(
                conditionEquals(),
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateFormatConditionGreaterThanDay() {
        this.dateFormatParseAndCheck(
                conditionGreaterThan(),
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateFormatConditionGreaterThanEqualsDay() {
        this.dateFormatParseAndCheck(
                conditionGreaterThanEquals(),
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateFormatConditionLessThanDay() {
        this.dateFormatParseAndCheck(
                conditionLessThan(),
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateFormatConditionLessThanEqualsDay() {
        this.dateFormatParseAndCheck(
                conditionLessThanEquals(),
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateFormatConditionNotEqualsDay() {
        this.dateFormatParseAndCheck(
                conditionNotEquals(),
                date(
                        day()
                )
        );
    }

    @Test
    public void testDayFormatDateConditionEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                conditionEquals()
        );
    }

    @Test
    public void testDateFormatDayConditionGreaterThanFails() {
        this.dateFormatParseThrows(
                day(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testDateFormatDayConditionGreaterThanEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testDateFormatDayConditionLessThanFails() {
        this.dateFormatParseThrows(
                day(),
                conditionLessThan()
        );
    }

    @Test
    public void testDateFormatDayConditionLessThanEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testDateFormatDayConditionNotEqualsFails() {
        this.dateFormatParseThrows(
                day(),
                conditionNotEquals()
        );
    }

    @Test
    public void testDateFormatPatternSeparator() {
        this.dateFormatParseAndCheck(
                date(
                        year(),
                        month(),
                        day()
                ),
                separator()
        );
    }

    @Test
    public void testDateFormatPatternSeparatorPattern() {
        this.dateFormatParseAndCheck(
                date(
                        day(),
                        month(),
                        year()
                ),
                separator(),
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    @Test
    public void testDateFormatColorPatternSeparatorPattern() {
        this.dateFormatParseAndCheck(
                date(
                        color(),
                        day(),
                        month(),
                        year()
                ),
                separator(),
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    @Test
    public void testDateFormatConditionPatternSeparatorPattern() {
        this.dateFormatParseAndCheck(
                conditionEquals(),
                date(
                        day(),
                        month(),
                        year()
                ),
                separator(),
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    // date format helpers..............................................................................................

    private void dateFormatParseAndCheck(final SpreadsheetFormatDateParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.dateFormat(),
                tokens
        );
    }

    private void dateFormatParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.dateFormat(),
                tokens
        );
    }

    private void dateFormatParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.dateFormat(),
                tokens
        );
    }

    // date parse......................................................................................................

    @Test
    public void testDateParseEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.dateParse(),
                ""
        );
    }

    @Test
    public void testDateParseGeneral() {
        this.dateParseParseAndCheck(
                general()
        );
    }

    @Test
    public void testDateParseWhitespaceGeneral() {
        this.dateParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateParseGeneralWhitespace() {
        this.dateParseParseAndCheck(
                general(
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateParseWhitespaceGeneralWhitespace() {
        this.dateParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateParseColorGeneral() {
        this.dateParseParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateParseColorWhitespaceGeneral() {
        this.dateParseParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateParseColorEscapedFails() {
        this.dateParseParseThrows(
                date(
                        color(),
                        escape()
                )
        );
    }

    @Test
    public void testDateParseTextDigitFails() {
        this.dateParseParseThrows(
                digit()
        );
    }

    @Test
    public void testDateParseTextDigitZeroFails() {
        this.dateParseParseThrows(
                digitZero()
        );
    }

    @Test
    public void testDateParseTextDigitSpaceFails() {
        this.dateParseParseThrows(
                digitSpace()
        );
    }

    @Test
    public void testDateParseHourFails() {
        this.dateParseParseThrows(
                hour()
        );
    }

    @Test
    public void testDateParseSecondFails() {
        this.dateParseParseThrows(
                second()
        );
    }

    @Test
    public void testDateParseTextPlaceholderFails() {
        this.dateParseParseThrows(
                textPlaceholder()
        );
    }

    @Test
    public void testDateParseEscaped() {
        this.dateParseParseAndCheck(
                date(
                        escape()
                )
        );
    }

    @Test
    public void testDateParseDollar() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testDateParseMinus() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testDateParsePlus() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testDateParseSlash() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testDateParseOpenParen() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testDateParseCloseParen() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testDateParseColon() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testDateParseSpace() {
        this.dateParseParseAndCheck(
                date(
                        whitespace()
                )
        );
    }

    @Test
    public void testDateParseQuotedText() {
        this.dateParseParseAndCheck(
                date(
                        quotedText()
                )
        );
    }

    @Test
    public void testDateParseDay() {
        this.dateParseParseAndCheck(
                date(
                        day()
                )
        );
    }

    @Test
    public void testDateParseMonth() {
        this.dateParseParseAndCheck(
                date(
                        month()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDay2Month2Year2() {
        this.dateParseParseAndCheck(
                date(
                        day(2),
                        month(2),
                        year(2)
                )
        );
    }

    @Test
    public void testDateParseDay3Month3Year3() {
        this.dateParseParseAndCheck(
                date(
                        day(3),
                        month(3),
                        year(3)
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearDateDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseMonthDayYear() {
        this.dateParseParseAndCheck(
                date(
                        month(),
                        day(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseYearMonthDay() {
        this.dateParseParseAndCheck(
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    @Test
    public void testDateParseYearCommaMonthCommaDay() {
        this.dateParseParseAndCheck(
                date(
                        year(),
                        textLiteralComma(),
                        month(),
                        textLiteralComma(),
                        day()
                )
        );
    }

    // escaped

    @Test
    public void testDateParseEscapedDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        escape(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayEscapedMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        escape(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthEscapedYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        escape(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearEscaped() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        escape()
                )
        );
    }

    // quotedText

    @Test
    public void testDateParseQuotedTextDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        quotedText(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayQuotedTextMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        quotedText(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthQuotedTextYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        quotedText(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearQuotedText() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        quotedText()
                )
        );
    }

    // closeParens

    @Test
    public void testDateParseCloseParensDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralCloseParens(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayCloseParensMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralCloseParens(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthCloseParensYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralCloseParens(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearCloseParens() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralCloseParens()
                )
        );
    }

    // colon

    @Test
    public void testDateParseColonDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralColon(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayColonMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralColon(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthColonYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralColon(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearColon() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralColon()

                )
        );
    }

    // dollar

    @Test
    public void testDateParseDollarDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralDollar(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayDollarMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralDollar(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthDollarYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralDollar(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearDollar() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralDollar()
                )
        );
    }

    // minus

    @Test
    public void testDateParseMinusDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralMinus(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMinusMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralMinus(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthMinusYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralMinus(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearMinus() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralMinus()
                )
        );
    }

    // openParens

    @Test
    public void testDateParseOpenParensDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralOpenParens(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayOpenParensMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralOpenParens(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthOpenParensYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralOpenParens(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearOpenParens() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralOpenParens()
                )
        );
    }

    // plus

    @Test
    public void testDateParsePlusDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralPlus(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayPlusMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralPlus(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthPlusYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralPlus(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearPlus() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralPlus()
                )
        );
    }

    // slash

    @Test
    public void testDateParseSlashDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        textLiteralSlash(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDaySlashMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        textLiteralSlash(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthSlashYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        textLiteralSlash(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearSlash() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        textLiteralSlash()
                )
        );
    }

    // space

    @Test
    public void testDateParseSpaceDayMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        whitespace(),
                        day(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDaySpaceMonthYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        whitespace(),
                        month(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthSpaceYear() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        whitespace(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayMonthYearSpace() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year(),
                        whitespace()
                )
        );
    }

    // equals

    @Test
    public void testDateParseEqualsDayMonthYearFails() {
        this.dateParseParseThrows(
                equalsSymbol(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayEqualsMonthYearFails() {
        this.dateParseParseThrows(
                day(),
                equalsSymbol(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthEqualsYearFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                equalsSymbol(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthYearEqualsFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                year(),
                equalsSymbol()
        );
    }

    // greaterThan

    @Test
    public void testDateParseGreaterThanDayMonthYearFails() {
        this.dateParseParseThrows(
                greaterThan(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayGreaterThanMonthYearFails() {
        this.dateParseParseThrows(
                day(),
                greaterThan(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthGreaterThanYearFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                greaterThan(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthYearGreaterThanFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                year(),
                greaterThan()
        );
    }

    // greaterThanEquals

    @Test
    public void testDateParseGreaterThanEqualsDayMonthYearFails() {
        this.dateParseParseThrows(
                greaterThanEquals(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayGreaterThanEqualsMonthYearFails() {
        this.dateParseParseThrows(
                day(),
                greaterThanEquals(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthGreaterThanEqualsYearFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                greaterThanEquals(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthYearGreaterThanEqualsFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                year(),
                greaterThanEquals()
        );
    }

    // lessThan

    @Test
    public void testDateParseLessThanDayMonthYearFails() {
        this.dateParseParseThrows(
                lessThan(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayLessThanMonthYearFails() {
        this.dateParseParseThrows(
                day(),
                lessThan(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthLessThanYearFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                lessThan(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthYearLessThanFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                year(),
                lessThan()
        );
    }

    @Test
    public void testDateParseLessThanEqualsDayMonthYearFails() {
        this.dateParseParseThrows(
                lessThanEquals(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayLessThanEqualsMonthYearFails() {
        this.dateParseParseThrows(
                day(),
                lessThanEquals(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthLessThanEqualsYearFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                lessThanEquals(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthYearLessThanEqualsFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                year(),
                lessThanEquals()
        );
    }

    // notEquals

    @Test
    public void testDateParseNotEqualsDayMonthYearFails() {
        this.dateParseParseThrows(
                notEquals(),
                day(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayNotEqualsMonthYearFails() {
        this.dateParseParseThrows(
                day(),
                notEquals(),
                month(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthNotEqualsYearFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                notEquals(),
                year()
        );
    }

    @Test
    public void testDateParseDayMonthYearNotEqualsFails() {
        this.dateParseParseThrows(
                day(),
                month(),
                year(),
                notEquals()
        );
    }

    // color

    @Test
    public void testDateParseColorDayFails() {
        this.dateParseParseThrows(
                date(
                        color(),
                        day()
                )
        );
    }

    @Test
    public void testDateParseColorMonthFails() {
        this.dateParseParseThrows(
                date(
                        color(),
                        month()
                )
        );
    }

    @Test
    public void testDateParseColorYearFails() {
        this.dateParseParseThrows(
                date(
                        color(),
                        year()
                )
        );
    }

    @Test
    public void testDateParseDayColorFails() {
        this.dateParseParseThrows(
                date(
                        day(),
                        color()
                )
        );
    }

    @Test
    public void testDateParseMonthColorFails() {
        this.dateParseParseThrows(
                date(
                        month(),
                        color()
                )
        );
    }

    @Test
    public void testDateParseYearColorFails() {
        this.dateParseParseThrows(
                date(
                        year(),
                        color()
                )
        );
    }

    // condition

    @Test
    public void testDateParseConditionEqualsDayFails() {
        this.dateParseParseThrows(
                conditionEquals(),
                day()
        );
    }

    @Test
    public void testDateParseConditionGreaterThanDayFails() {
        this.dateParseParseThrows(
                conditionGreaterThan(),
                day()
        );
    }

    @Test
    public void testDateParseConditionGreaterThanEqualsDayFails() {
        this.dateParseParseThrows(
                conditionGreaterThanEquals(),
                day()
        );
    }

    @Test
    public void testDateParseConditionLessThanDayFails() {
        this.dateParseParseThrows(
                conditionLessThan(),
                day());
    }

    @Test
    public void testDateParseConditionLessThanEqualsDayFails() {
        this.dateParseParseThrows(
                conditionLessThanEquals(),
                day()
        );
    }

    @Test
    public void testDateParseConditionNotEqualsDayFails() {
        this.dateParseParseThrows(
                conditionNotEquals(),
                day()
        );
    }

    @Test
    public void testDateDayConditionEqualsFails() {
        this.dateParseParseThrows(
                day(),
                conditionEquals()
        );
    }

    @Test
    public void testDateParseDayConditionGreaterThanFails() {
        this.dateParseParseThrows(
                day(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testDateParseDayConditionGreaterThanEqualsFails() {
        this.dateParseParseThrows(
                day(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testDateParseDayConditionLessThanFails() {
        this.dateParseParseThrows(
                day(),
                conditionLessThan()
        );
    }

    @Test
    public void testDateParseDayConditionLessThanEqualsFails() {
        this.dateParseParseThrows(
                day(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testDateParseDayConditionNotEqualsFails() {
        this.dateParseParseThrows(
                day(),
                conditionNotEquals()
        );
    }

    @Test
    public void testDateParsePatternSeparator() {
        this.dateParseParseAndCheck(
                date(
                        year(),
                        month(),
                        day()
                ),
                separator()
        );
    }

    @Test
    public void testDateParsePatternSeparatorPattern() {
        this.dateParseParseAndCheck(
                date(
                        day(),
                        month(),
                        year()
                ),
                separator(),
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    @Test
    public void testDateParseColorPatternSeparatorPatternFails() {
        this.dateParseParseThrows(
                date(
                        color(),
                        day(),
                        month(),
                        year()
                ),
                separator(),
                date(
                        year(),
                        month(),
                        day()
                )
        );
    }

    // date parse helpers...............................................................................................

    private void dateParseParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.dateParse(),
                tokens
        );
    }

    private void dateParseParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.dateParse(),
                tokens
        );
    }

    // number format.....................................................................................................

    @Test
    public void testNumberFormatEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.numberFormat(),
                ""
        );
    }

    @Test
    public void testNumberFormatSeparator() {
        this.numberFormatParseAndCheck(
                separator()
        );
    }

    @Test
    public void testNumberFormatSeparatorSeparator() {
        this.numberFormatParseAndCheck(
                separator(),
                separator()
        );
    }

    @Test
    public void testNumberFormatSeparatorSeparatorSeparator() {
        this.numberFormatParseAndCheck(
                separator(),
                separator(),
                separator()
        );
    }

    @Test
    public void testNumberFormatDayFails() {
        this.numberFormatParseThrows(
                digit(),
                day()
        );
    }

    @Test
    public void testNumberFormatHourFails() {
        this.numberFormatParseThrows(
                digit(),
                hour()
        );
    }

    @Test
    public void testNumberFormatMinuteFails() {
        this.numberFormatParseThrows(
                digit(),
                minute()
        );
    }

    @Test
    public void testNumberFormatMonthFails() {
        this.numberFormatParseThrows(
                digit(),
                month()
        );
    }

    @Test
    public void testNumberFormatSecondFails() {
        this.numberFormatParseThrows(
                digit(),
                second()
        );
    }

    @Test
    public void testNumberFormatStarFails() {
        this.numberFormatParseThrows(
                star()
        );
    }

    @Test
    public void testNumberFormatTextPlaceholderFails() {
        this.numberFormatParseThrows(
                digit(),
                textPlaceholder()
        );
    }

    @Test
    public void testNumberFormatUnderscoreFails() {
        this.numberFormatParseThrows(
                underscore()
        );
    }

    @Test
    public void testNumberFormatYearFails() {
        this.numberFormatParseThrows(
                digit(),
                year()
        );
    }

    @Test
    public void testNumberFormatSlashFails() {
        this.numberFormatParseThrows(
                fractionSymbol()
        );
    }

    @Test
    public void testNumberFormatDigitSpaceNumberFails() {
        this.numberFormatParseThrows(
                digitSpace(),
                fractionSymbol()
        );
    }

    @Test
    public void testNumberFormatDigitZeroNumberFails() {
        this.numberFormatParseThrows(
                digitZero(),
                fractionSymbol()
        );
    }

    @Test
    public void testNumberFormatDigitNumberFails() {
        this.numberFormatParseThrows(
                digit(),
                fractionSymbol()
        );
    }

    // general.........................................................................................................

    @Test
    public void testNumberFormatGeneral() {
        this.numberFormatParseAndCheck(
                general()
        );
    }

    @Test
    public void testNumberFormatGeneralWhitespace() {
        this.numberFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testNumberFormatWhitespaceGeneralWhitespace() {
        this.numberFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testNumberFormatColorGeneral() {
        this.numberFormatParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testNumberFormatColorWhitespaceGeneral() {
        this.numberFormatParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testNumberFormatConditionEqualsGeneral() {
        this.numberFormatParseAndCheck(
                conditionEquals(),
                general()
        );
    }

    @Test
    public void testNumberFormatConditionGreaterThanGeneral() {
        this.numberFormatParseAndCheck(
                conditionGreaterThan(),
                general()
        );
    }

    @Test
    public void testNumberFormatConditionGreaterThanEqualsGeneral() {
        this.numberFormatParseAndCheck(
                conditionGreaterThanEquals(),
                general()
        );
    }

    @Test
    public void testNumberFormatConditionLessThanGeneral() {
        this.numberFormatParseAndCheck(
                conditionLessThan(),
                general()
        );
    }

    @Test
    public void testNumberFormatConditionLessThanEqualsGeneral() {
        this.numberFormatParseAndCheck(
                conditionLessThanEquals(),
                general()
        );
    }

    @Test
    public void testNumberFormatConditionNotEqualsGeneral() {
        this.numberFormatParseAndCheck(
                conditionNotEquals(),
                general()
        );
    }

    // literals only...........................................................................

    @Test
    public void testNumberFormatEscaped() {
        this.numberFormatParseAndCheck(
                number(
                        escape()
                )
        );
    }

    @Test
    public void testNumberFormatMinus() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testNumberFormatPlus() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testNumberFormatOpenParen() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testNumberFormatCloseParen() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testNumberFormatColon() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testNumberFormatSpace() {
        this.numberFormatParseAndCheck(
                number(
                        whitespace()
                )
        );
    }

    @Test
    public void testNumberFormatQuotedText() {
        this.numberFormatParseAndCheck(
                number(
                        quotedText()
                )
        );
    }

    // digitSpace

    @Test
    public void testNumberFormatDigitSpaceNumberDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digitSpace(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSpaceDigitSpaceNumberDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digitSpace(),
                        digitSpace(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSpaceDigitZeroNumberDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digitSpace(),
                        digitZero(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSpaceDigitNumberDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digitSpace(),
                        digit(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSpaceNumberDigitSpaceDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digitSpace(),
                        decimalPoint(),
                        digitSpace(),
                        digitSpace()
                )
        );
    }

    // digitSpace

    @Test
    public void testNumberFormatDigitZeroDigitSpaceNumberDigitZero() {
        this.numberFormatParseAndCheck(
                number(
                        digitZero(),
                        digitSpace(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testNumberFormatDigitZeroDigitZeroNumberDigitZero() {
        this.numberFormatParseAndCheck(
                number(
                        digitZero(),
                        digitZero(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testNumberFormatDigitZeroDigitNumberDigitZero() {
        this.numberFormatParseAndCheck(
                number(
                        digitZero(),
                        digit(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testNumberFormatDigitZeroNumberDigitZeroDigitZero() {
        this.numberFormatParseAndCheck(
                number(
                        digitZero(),
                        decimalPoint(),
                        digitZero(),
                        digitZero()
                )
        );
    }

    // digitZero

    @Test
    public void testNumberFormatDigitNumberDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitDigitSpaceNumberDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        digitSpace(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitDigitZeroNumberDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        digitZero(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitDigitNumberDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitNumberDigitDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        digit()
                )
        );
    }

    // currency

    @Test
    public void testNumberFormatCurrencyDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        currency(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitCurrencySlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        currency(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashCurrencyDigit() {
        this.numberFormatParseAndCheck(
                number(
                        currency(),
                        digit(),
                        decimalPoint(),
                        currency(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitCurrency() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        currency()
                )
        );
    }

    // percentage

    @Test
    public void testNumberFormatPercentageDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        percentSymbol(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitPercentageSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        percentSymbol(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashPercentageDigit() {
        this.numberFormatParseAndCheck(
                number(
                        percentSymbol(),
                        digit(),
                        decimalPoint(),
                        percentSymbol(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitPercentage() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        percentSymbol()
                )
        );
    }

    // groupSeparator

    @Test
    public void testNumberFormatGroupSeparatorDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        groupSeparator(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitGroupSeparatorSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        groupSeparator(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashGroupSeparatorDigit() {
        this.numberFormatParseAndCheck(
                number(
                        groupSeparator(),
                        digit(),
                        decimalPoint(),
                        groupSeparator(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitGroupSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        groupSeparator()
                )
        );
    }

    // text literals

    // escaped

    @Test
    public void testNumberFormatDigitEscapedDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        escape()
                )
        );
    }

    @Test
    public void testNumberFormatDigitEscapedSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        escape(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashEscapedDigit() {
        this.numberFormatParseAndCheck(
                number(
                        escape(),
                        digit(),
                        decimalPoint(),
                        escape(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitEscaped() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        escape()
                )
        );
    }

    // quotedText

    @Test
    public void testNumberFormatQuotedTextDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        quotedText(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitQuotedTextSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        quotedText(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashQuotedTextDigit() {
        this.numberFormatParseAndCheck(
                number(
                        quotedText(),
                        digit(),
                        decimalPoint(),
                        quotedText(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitQuotedText() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        quotedText()
                )
        );
    }

    // closeParens

    @Test
    public void testNumberFormatCloseParensDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralCloseParens(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitCloseParensSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        textLiteralCloseParens(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashCloseParensDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralCloseParens(),
                        digit(),
                        decimalPoint(),
                        textLiteralCloseParens(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitCloseParens() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralCloseParens()
                )
        );
    }

    // colon

    @Test
    public void testNumberFormatColonDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralColon(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitColonSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        textLiteralColon(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashColonDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralColon(),
                        digit(),
                        decimalPoint(),
                        textLiteralColon(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitColon() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralColon()
                )
        );
    }

    // minus

    @Test
    public void testNumberFormatMinusDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralMinus(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitMinusSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        textLiteralMinus(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashMinusDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralMinus(),
                        digit(),
                        decimalPoint(),
                        textLiteralMinus(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitMinus() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralMinus()
                )
        );
    }

    // openParens

    @Test
    public void testNumberFormatOpenParensDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralOpenParens(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitOpenParensSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        textLiteralOpenParens(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashOpenParensDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralOpenParens(),
                        digit(),
                        decimalPoint(),
                        textLiteralOpenParens(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitOpenParens() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralOpenParens()
                )
        );
    }

    // plus

    @Test
    public void testNumberFormatPlusDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralPlus(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitPlusSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        textLiteralPlus(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashPlusDigit() {
        this.numberFormatParseAndCheck(
                number(
                        textLiteralPlus(),
                        digit(),
                        decimalPoint(),
                        textLiteralPlus(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitPlus() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralPlus()
                )
        );
    }

    // space

    @Test
    public void testNumberFormatSpaceDigitSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        whitespace(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSpaceSlashDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        whitespace(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashSpaceDigit() {
        this.numberFormatParseAndCheck(
                number(
                        whitespace(),
                        digit(),
                        decimalPoint(),
                        whitespace(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitSlashDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        whitespace()
                )
        );
    }

    // equals

    @Test
    public void testNumberFormatEqualsDigitDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                equalsSymbol(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitEqualsDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                equalsSymbol(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                equalsSymbol(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointDigitEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                equalsSymbol()
        );
    }

    // greater than....................................................................................................

    @Test
    public void testNumberFormatGreaterThanDigitDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                greaterThan(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitGreaterThanDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                greaterThan(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointGreaterThanDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                greaterThan(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointDigitGreaterThanFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                greaterThan()
        );
    }

    // greaterThanEquals

    @Test
    public void testNumberFormatGreaterThanEqualsDigitDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                greaterThanEquals(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitGreaterThanEqualsDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                greaterThanEquals(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointGreaterThanEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                greaterThanEquals(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointDigitGreaterThanEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                greaterThanEquals()
        );
    }

    // lessThan

    @Test
    public void testNumberFormatLessThanDigitDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                lessThan(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitLessThanDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                lessThan(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointLessThanDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                lessThan(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointDigitLessThanFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                lessThan()
        );
    }

    // lessThanEquals

    @Test
    public void testNumberFormatLessThanEqualsDigitDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                lessThanEquals(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitLessThanEqualsDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                lessThanEquals(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointLessThanEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                lessThanEquals(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointDigitLessThanEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                lessThanEquals()
        );
    }

    // notEquals

    @Test
    public void testNumberFormatNotEqualsDigitDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                notEquals(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitNotEqualsDecimalPointDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                notEquals(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointNotEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                notEquals(),
                digit()
        );
    }

    @Test
    public void testNumberFormatDigitDecimalPointDigitNotEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                notEquals()
        );
    }

    // exponent.............................................................................

    // currency

    @Test
    public void testNumberFormatDigitExponentCurrencyDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(currency())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitCurrencyDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(currency())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitCurrency() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(currency())
                )
        );
    }

    // text literals

    // escaped

    @Test
    public void testNumberFormatDigitExponentEscapedDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(escape())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitEscapedDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(escape())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitEscaped() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(escape())
                )
        );
    }

    // quotedText

    @Test
    public void testNumberFormatDigitExponentQuotedTextDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(quotedText())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitQuotedTextDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(quotedText())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitQuotedText() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(quotedText())
                )
        );
    }

    // closeParens

    @Test
    public void testNumberFormatDigitExponentCloseParensDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralCloseParens())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitCloseParensDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralCloseParens())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitCloseParens() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralCloseParens())
                )
        );
    }

    // colon

    @Test
    public void testNumberFormatDigitExponentColonDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralColon())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitColonDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralColon())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitColon() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralColon())
                )
        );
    }

    // minus

    @Test
    public void testNumberFormatDigitExponentMinusDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralMinus())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitMinusDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralMinus())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitMinus() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralMinus())
                )
        );
    }

    // openParens

    @Test
    public void testNumberFormatDigitExponentOpenParensDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralOpenParens())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitOpenParensDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralOpenParens())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitOpenParens() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralOpenParens())
                )
        );
    }

    // plus

    @Test
    public void testNumberFormatDigitExponentPlusDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralPlus())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitPlusDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralPlus())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitPlus() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralPlus())
                )
        );
    }

    // space

    @Test
    public void testNumberFormatDigitExponentSpaceDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(whitespace())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitSpaceDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(whitespace())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitSpace() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(whitespace())
                )
        );
    }

    // equals

    @Test
    public void testNumberFormatDigitExponentEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent1(equalsSymbol())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent2(equalsSymbol())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent3(equalsSymbol())
        );
    }

    // greaterThan

    @Test
    public void testNumberFormatDigitExponentGreaterThanDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent1(greaterThan())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitGreaterThanDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent2(greaterThan())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitGreaterThanFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent3(greaterThan())
        );
    }

    // greaterThanEquals

    @Test
    public void testNumberFormatDigitExponentGreaterThanEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent1(greaterThanEquals())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitGreaterThanEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent2(greaterThanEquals())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitGreaterThanEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent3(greaterThanEquals())
        );
    }

    // lessThan

    @Test
    public void testNumberFormatDigitExponentLessThanDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent1(lessThan())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitLessThanDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent2(lessThan())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitLessThanFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent3(lessThan())
        );
    }

    // lessThanEquals

    @Test
    public void testNumberFormatDigitExponentLessThanEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent1(lessThanEquals())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitLessThanEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent2(lessThanEquals())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitLessThanEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent3(lessThanEquals())
        );
    }

    // notEquals

    @Test
    public void testNumberFormatDigitExponentNotEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent1(notEquals())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitNotEqualsDigitFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent2(notEquals())
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitNotEqualsFails() {
        this.numberFormatParseThrows(
                digit(),
                exponent3(notEquals())
        );
    }

    // color

    @Test
    public void testNumberFormatColorDigit() {
        this.numberFormatParseAndCheck(
                number(
                        color(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatDigitColor() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        color()
                )
        );
    }

    @Test
    public void testNumberFormatDigitDecimalColor() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        color()
                )
        );
    }

    @Test
    public void testNumberFormatDigitDecimalDigitColor() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        color()
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentColorDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent1(color())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitColorDigit() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent2(color())
                )
        );
    }

    @Test
    public void testNumberFormatDigitExponentDigitColor() {
        this.numberFormatParseAndCheck(
                number(
                        digit(),
                        exponent3(color())
                )
        );
    }

    // condition

    @Test
    public void testNumberFormatConditionEqualsNumber() {
        this.numberFormatParseAndCheck(
                conditionEquals(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatConditionGreaterThanNumber() {
        this.numberFormatParseAndCheck(
                conditionGreaterThan(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatConditionGreaterThanEqualsNumber() {
        this.numberFormatParseAndCheck(
                conditionGreaterThanEquals(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatConditionLessThanNumber() {
        this.numberFormatParseAndCheck(
                conditionLessThan(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatConditionLessThanEqualsNumber() {
        this.numberFormatParseAndCheck(
                conditionLessThanEquals(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatConditionNotEqualsNumber() {
        this.numberFormatParseAndCheck(
                conditionNotEquals(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatTokenEqualsConditionFails() {
        this.numberFormatParseThrows(
                digit(),
                conditionEquals()
        );
    }

    @Test
    public void testNumberFormatTokenLessThanConditionFails() {
        this.numberFormatParseThrows(
                digit(),
                conditionLessThan()
        );
    }

    @Test
    public void testNumberFormatTokenLessThanEqualsConditionFails() {
        this.numberFormatParseThrows(
                digit(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testNumberFormatTokenGreaterThanConditionFails() {
        this.numberFormatParseThrows(
                digit(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testNumberFormatTokenGreaterThanEqualsConditionFails() {
        this.numberFormatParseThrows(
                digit(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testNumberFormatTokenNotEqualsConditionFails() {
        this.numberFormatParseThrows(
                digit(),
                conditionNotEquals()
        );
    }

    @Test
    public void testNumberFormatPatternSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator()
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPattern() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorTextPattern() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorTextPatternSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                ),
                separator()
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator()
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorPattern() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorPatternSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator()
        );
    }


    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorTextPattern() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorTextPatternSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                ),
                separator()
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorPatternSeparatorTextPattern() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorPatternSeparatorTextPatternSeparator() {
        this.numberFormatParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                ),
                separator()
        );
    }

    @Test
    public void testNumberFormatPatternSeparatorPatternSeparatorPatternSeparatorConditionPatternFails() {
        this.numberFormatParseThrows(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator(),
                conditionEquals(),
                text(
                        textPlaceholder()
                )
        );
    }

    // number helpers...................................................................................................

    private void numberFormatParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.numberFormat(),
                tokens
        );
    }

    private void numberFormatParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.numberFormat(),
                tokens
        );
    }

    // number parse.....................................................................................................

    @Test
    public void testNumberParseEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.numberParse(),
                ""
        );
    }

    @Test
    public void testNumberParseDayFails() {
        this.numberParseParseThrows(
                digit(),
                day()
        );
    }

    @Test
    public void testNumberParseHourFails() {
        this.numberParseParseThrows(
                digit(),
                hour()
        );
    }

    @Test
    public void testNumberParseMonthFails() {
        this.numberParseParseThrows(
                digit(),
                month()
        );
    }

    @Test
    public void testNumberParseSecondFails() {
        this.numberParseParseThrows(
                digit(),
                second()
        );
    }

    @Test
    public void testNumberParseStarFails() {
        this.numberParseParseThrows(
                star()
        );
    }

    @Test
    public void testNumberParseTextPlaceholderFails() {
        this.numberParseParseThrows(
                digit(),
                textPlaceholder()
        );
    }

    @Test
    public void testNumberParseUnderscoreFails() {
        this.numberParseParseThrows(
                underscore()
        );
    }

    @Test
    public void testNumberParseYearFails() {
        this.numberParseParseThrows(
                digit(),
                year()
        );
    }

    @Test
    public void testNumberParseSlashFails() {
        this.numberParseParseThrows(
                fractionSymbol()
        );
    }

    @Test
    public void testNumberParseDigitSpaceNumberFails() {
        this.numberParseParseThrows(
                digitSpace(),
                fractionSymbol()
        );
    }

    @Test
    public void testNumberParseDigitZeroNumberFails() {
        this.numberParseParseThrows(
                digitZero(),
                fractionSymbol()
        );
    }

    @Test
    public void testNumberParseDigitNumberFails() {
        this.numberParseParseThrows(
                digit(),
                fractionSymbol()
        );
    }

    // general.........................................................................................................

    @Test
    public void testNumberParseGeneral() {
        this.numberParseParseAndCheck(
                general()
        );
    }

    @Test
    public void testNumberParseGeneralWhitespace() {
        this.numberParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testNumberParseWhitespaceGeneralWhitespace() {
        this.numberParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testNumberParseColorGeneral() {
        this.numberParseParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testNumberParseColorWhitespaceGeneral() {
        this.numberParseParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testNumberParseConditionEqualsGeneralFails() {
        this.numberParseParseThrows(
                conditionEquals(),
                general()
        );
    }

    @Test
    public void testNumberParseConditionGreaterThanGeneralFails() {
        this.numberParseParseThrows(
                conditionGreaterThan(),
                general()
        );
    }

    @Test
    public void testNumberParseConditionGreaterThanEqualsGeneralFails() {
        this.numberParseParseThrows(
                conditionGreaterThanEquals(),
                general()
        );
    }

    @Test
    public void testNumberParseConditionLessThanGeneralFails() {
        this.numberParseParseThrows(
                conditionLessThan(),
                general()
        );
    }

    @Test
    public void testNumberParseConditionLessThanEqualsGeneralFails() {
        this.numberParseParseThrows(
                conditionLessThanEquals(),
                general()
        );
    }

    @Test
    public void testNumberParseConditionNotEqualsGeneralFails() {
        this.numberParseParseThrows(
                conditionNotEquals(),
                general()
        );
    }

    // literals only...........................................................................

    @Test
    public void testNumberParseEscaped() {
        this.numberParseParseAndCheck(
                number(
                        escape()
                )
        );
    }

    @Test
    public void testNumberParseMinus() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testNumberParsePlus() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testNumberParseOpenParen() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testNumberParseCloseParen() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testNumberParseColon() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testNumberParseSpace() {
        this.numberParseParseAndCheck(
                number(
                        whitespace()
                )
        );
    }

    @Test
    public void testNumberParseQuotedText() {
        this.numberParseParseAndCheck(
                number(
                        quotedText()
                )
        );
    }

    // digitSpace

    @Test
    public void testNumberParseDigitSpaceNumberDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digitSpace(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberParseDigitSpaceDigitSpaceNumberDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digitSpace(),
                        digitSpace(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberParseDigitSpaceDigitZeroNumberDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digitSpace(),
                        digitZero(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberParseDigitSpaceDigitNumberDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digitSpace(),
                        digit(),
                        decimalPoint(),
                        digitSpace()
                )
        );
    }

    @Test
    public void testNumberParseDigitSpaceNumberDigitSpaceDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digitSpace(),
                        decimalPoint(),
                        digitSpace(),
                        digitSpace()
                )
        );
    }

    // digitSpace

    @Test
    public void testNumberParseDigitZeroDigitSpaceNumberDigitZero() {
        this.numberParseParseAndCheck(
                number(
                        digitZero(),
                        digitSpace(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testNumberParseDigitZeroDigitZeroNumberDigitZero() {
        this.numberParseParseAndCheck(
                number(
                        digitZero(),
                        digitZero(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testNumberParseDigitZeroDigitNumberDigitZero() {
        this.numberParseParseAndCheck(
                number(
                        digitZero(),
                        digit(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testNumberParseDigitZeroNumberDigitZeroDigitZero() {
        this.numberParseParseAndCheck(
                number(
                        digitZero(),
                        decimalPoint(),
                        digitZero(),
                        digitZero()
                )
        );
    }

    // digitZero

    @Test
    public void testNumberParseDigitNumberDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitDigitSpaceNumberDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        digitSpace(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitDigitZeroNumberDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        digitZero(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitDigitNumberDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitNumberDigitDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        digit()
                )
        );
    }

    // currency

    @Test
    public void testNumberParseCurrencyDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        currency(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitCurrencySlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        currency(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashCurrencyDigit() {
        this.numberParseParseAndCheck(
                number(
                        currency(),
                        digit(),
                        decimalPoint(),
                        currency(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitCurrency() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        currency()
                )
        );
    }

    // percentage

    @Test
    public void testNumberParsePercentageDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        percentSymbol(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitPercentageSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        percentSymbol(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashPercentageDigit() {
        this.numberParseParseAndCheck(
                number(
                        percentSymbol(),
                        digit(),
                        decimalPoint(),
                        percentSymbol(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitPercentage() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        percentSymbol()
                )
        );
    }

    // groupSeparator

    @Test
    public void testNumberParseGroupSeparatorDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        groupSeparator(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitGroupSeparatorSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        groupSeparator(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashGroupSeparatorDigit() {
        this.numberParseParseAndCheck(
                number(
                        groupSeparator(),
                        digit(),
                        decimalPoint(),
                        groupSeparator(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitGroupSeparator() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        groupSeparator()
                )
        );
    }

    // text literals

    // escaped

    @Test
    public void testNumberParseDigitEscapedDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        escape()
                )
        );
    }

    @Test
    public void testNumberParseDigitEscapedSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        escape(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashEscapedDigit() {
        this.numberParseParseAndCheck(
                number(
                        escape(),
                        digit(),
                        decimalPoint(),
                        escape(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitEscaped() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        escape()
                )
        );
    }

    // quotedText

    @Test
    public void testNumberParseQuotedTextDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        quotedText(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitQuotedTextSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        quotedText(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashQuotedTextDigit() {
        this.numberParseParseAndCheck(
                number(
                        quotedText(),
                        digit(),
                        decimalPoint(),
                        quotedText(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitQuotedText() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        quotedText()
                )
        );
    }

    // closeParens

    @Test
    public void testNumberParseCloseParensDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralCloseParens(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitCloseParensSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        textLiteralCloseParens(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashCloseParensDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralCloseParens(),
                        digit(),
                        decimalPoint(),
                        textLiteralCloseParens(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitCloseParens() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralCloseParens()
                )
        );
    }

    // colon

    @Test
    public void testNumberParseColonDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralColon(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitColonSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        textLiteralColon(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashColonDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralColon(),
                        digit(),
                        decimalPoint(),
                        textLiteralColon(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitColon() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralColon()
                )
        );
    }

    // minus

    @Test
    public void testNumberParseMinusDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralMinus(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitMinusSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        textLiteralMinus(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashMinusDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralMinus(),
                        digit(),
                        decimalPoint(),
                        textLiteralMinus(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitMinus() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralMinus()
                )
        );
    }

    // openParens

    @Test
    public void testNumberParseOpenParensDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralOpenParens(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitOpenParensSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        textLiteralOpenParens(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashOpenParensDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralOpenParens(),
                        digit(),
                        decimalPoint(),
                        textLiteralOpenParens(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitOpenParens() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralOpenParens()
                )
        );
    }

    // plus

    @Test
    public void testNumberParsePlusDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralPlus(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitPlusSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        textLiteralPlus(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashPlusDigit() {
        this.numberParseParseAndCheck(
                number(
                        textLiteralPlus(),
                        digit(),
                        decimalPoint(),
                        textLiteralPlus(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitPlus() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        textLiteralPlus()
                )
        );
    }

    // space

    @Test
    public void testNumberParseSpaceDigitSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        whitespace(),
                        digit(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSpaceSlashDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        whitespace(),
                        decimalPoint(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashSpaceDigit() {
        this.numberParseParseAndCheck(
                number(
                        whitespace(),
                        digit(),
                        decimalPoint(),
                        whitespace(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitSlashDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        whitespace()
                )
        );
    }

    // equals

    @Test
    public void testNumberParseEqualsDigitDecimalPointDigitFails() {
        this.numberParseParseThrows(
                equalsSymbol(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitEqualsDecimalPointDigitFails() {
        this.numberParseParseThrows(
                digit(),
                equalsSymbol(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                equalsSymbol(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointDigitEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                equalsSymbol()
        );
    }

    // greater than....................................................................................................

    @Test
    public void testNumberParseGreaterThanDigitDecimalPointDigitFails() {
        this.numberParseParseThrows(
                greaterThan(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitGreaterThanDecimalPointDigitFails() {
        this.numberParseParseThrows(
                digit(),
                greaterThan(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointGreaterThanDigitFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                greaterThan(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointDigitGreaterThanFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                greaterThan()
        );
    }

    // greaterThanEquals

    @Test
    public void testNumberParseGreaterThanEqualsDigitDecimalPointDigitFails() {
        this.numberParseParseThrows(
                greaterThanEquals(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitGreaterThanEqualsDecimalPointDigitFails() {
        this.numberParseParseThrows(
                digit(),
                greaterThanEquals(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointGreaterThanEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                greaterThanEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointDigitGreaterThanEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                greaterThanEquals()
        );
    }

    // lessThan

    @Test
    public void testNumberParseLessThanDigitDecimalPointDigitFails() {
        this.numberParseParseThrows(
                lessThan(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitLessThanDecimalPointDigitFails() {
        this.numberParseParseThrows(
                digit(),
                lessThan(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointLessThanDigitFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                lessThan(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointDigitLessThanFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                lessThan()
        );
    }

    // lessThanEquals

    @Test
    public void testNumberParseLessThanEqualsDigitDecimalPointDigitFails() {
        this.numberParseParseThrows(
                lessThanEquals(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitLessThanEqualsDecimalPointDigitFails() {
        this.numberParseParseThrows(
                digit(),
                lessThanEquals(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointLessThanEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                lessThanEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointDigitLessThanEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                lessThanEquals()
        );
    }

    // notEquals

    @Test
    public void testNumberParseNotEqualsDigitDecimalPointDigitFails() {
        this.numberParseParseThrows(
                notEquals(),
                digit(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitNotEqualsDecimalPointDigitFails() {
        this.numberParseParseThrows(
                digit(),
                notEquals(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointNotEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                notEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseDigitDecimalPointDigitNotEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                decimalPoint(),
                digit(),
                notEquals()
        );
    }

    // exponent.............................................................................

    // currency

    @Test
    public void testNumberParseDigitExponentCurrencyDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(currency())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitCurrencyDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(currency())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitCurrency() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(currency())
                )
        );
    }

    // text literals

    // escaped

    @Test
    public void testNumberParseDigitExponentEscapedDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(escape())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitEscapedDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(escape())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitEscaped() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(escape())
                )
        );
    }

    // quotedText

    @Test
    public void testNumberParseDigitExponentQuotedTextDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(quotedText())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitQuotedTextDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(quotedText())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitQuotedText() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(quotedText())
                )
        );
    }

    // closeParens

    @Test
    public void testNumberParseDigitExponentCloseParensDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralCloseParens())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitCloseParensDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralCloseParens())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitCloseParens() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralCloseParens())
                )
        );
    }

    // colon

    @Test
    public void testNumberParseDigitExponentColonDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralColon())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitColonDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralColon())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitColon() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralColon())
                )
        );
    }

    // minus

    @Test
    public void testNumberParseDigitExponentMinusDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralMinus())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitMinusDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralMinus())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitMinus() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralMinus())
                )
        );
    }

    // openParens

    @Test
    public void testNumberParseDigitExponentOpenParensDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralOpenParens())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitOpenParensDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralOpenParens())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitOpenParens() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralOpenParens())
                )
        );
    }

    // plus

    @Test
    public void testNumberParseDigitExponentPlusDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(textLiteralPlus())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitPlusDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(textLiteralPlus())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitPlus() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(textLiteralPlus())
                )
        );
    }

    // space

    @Test
    public void testNumberParseDigitExponentSpaceDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent1(whitespace())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitSpaceDigit() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent2(whitespace())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitSpace() {
        this.numberParseParseAndCheck(
                number(
                        digit(),
                        exponent3(whitespace())
                )
        );
    }

    // equals

    @Test
    public void testNumberParseDigitExponentEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent1(equalsSymbol())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent2(equalsSymbol())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                exponent3(equalsSymbol())
        );
    }

    // greaterThan

    @Test
    public void testNumberParseDigitExponentGreaterThanDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent1(greaterThan())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitGreaterThanDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent2(greaterThan())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitGreaterThanFails() {
        this.numberParseParseThrows(
                digit(),
                exponent3(greaterThan())
        );
    }

    // greaterThanEquals

    @Test
    public void testNumberParseDigitExponentGreaterThanEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent1(greaterThanEquals())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitGreaterThanEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent2(greaterThanEquals())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitGreaterThanEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                exponent3(greaterThanEquals())
        );
    }

    // lessThan

    @Test
    public void testNumberParseDigitExponentLessThanDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent1(lessThan())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitLessThanDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent2(lessThan())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitLessThanFails() {
        this.numberParseParseThrows(
                digit(),
                exponent3(lessThan())
        );
    }

    // lessThanEquals

    @Test
    public void testNumberParseDigitExponentLessThanEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent1(lessThanEquals())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitLessThanEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent2(lessThanEquals())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitLessThanEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                exponent3(lessThanEquals())
        );
    }

    // notEquals

    @Test
    public void testNumberParseDigitExponentNotEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent1(notEquals())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitNotEqualsDigitFails() {
        this.numberParseParseThrows(
                digit(),
                exponent2(notEquals())
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitNotEqualsFails() {
        this.numberParseParseThrows(
                digit(),
                exponent3(notEquals())
        );
    }

    // color

    @Test
    public void testNumberParseColorDigitFails() {
        this.numberParseParseThrows(
                number(
                        color(),
                        digit()
                )
        );
    }

    @Test
    public void testNumberParseDigitColorFails() {
        this.numberParseParseThrows(
                number(
                        digit(),
                        color()
                )
        );
    }

    @Test
    public void testNumberParseDigitDecimalColorFails() {
        this.numberParseParseThrows(
                number(
                        digit(),
                        decimalPoint(),
                        color()
                )
        );
    }

    @Test
    public void testNumberParseDigitDecimalDigitColorFails() {
        this.numberParseParseThrows(
                number(
                        digit(),
                        decimalPoint(),
                        digit(),
                        color()
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentColorDigitFails() {
        this.numberParseParseThrows(
                number(
                        digit(),
                        exponent1(color())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitColorDigitFails() {
        this.numberParseParseThrows(
                number(
                        digit(),
                        exponent2(color())
                )
        );
    }

    @Test
    public void testNumberParseDigitExponentDigitColorFails() {
        this.numberParseParseThrows(
                number(
                        digit(),
                        exponent3(color())
                )
        );
    }

    // condition

    @Test
    public void testNumberParseConditionEqualsNumberFails() {
        this.numberParseParseThrows(
                conditionEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseConditionGreaterThanNumberFails() {
        this.numberParseParseThrows(
                conditionGreaterThan(),
                digit()
        );
    }

    @Test
    public void testNumberParseConditionGreaterThanEqualsNumberFails() {
        this.numberParseParseThrows(
                conditionGreaterThanEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseConditionLessThanNumberFails() {
        this.numberParseParseThrows(
                conditionLessThan(),
                digit()
        );
    }

    @Test
    public void testNumberParseConditionLessThanEqualsNumberFails() {
        this.numberParseParseThrows(
                conditionLessThanEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseConditionNotEqualsNumberFails() {
        this.numberParseParseThrows(
                conditionNotEquals(),
                digit()
        );
    }

    @Test
    public void testNumberParseTokenEqualsConditionFails() {
        this.numberParseParseThrows(
                digit(),
                conditionEquals()
        );
    }

    @Test
    public void testNumberParseTokenLessThanConditionFails() {
        this.numberParseParseThrows(
                digit(),
                conditionLessThan()
        );
    }

    @Test
    public void testNumberParseTokenLessThanEqualsConditionFails() {
        this.numberParseParseThrows(
                digit(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testNumberParseTokenGreaterThanConditionFails() {
        this.numberParseParseThrows(
                digit(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testNumberParseTokenGreaterThanEqualsConditionFails() {
        this.numberParseParseThrows(
                digit(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testNumberParseTokenNotEqualsConditionFails() {
        this.numberParseParseThrows(
                digit(),
                conditionNotEquals()
        );
    }

    @Test
    public void testNumberParsePatternSeparator() {
        this.numberParseParseAndCheck(
                number(
                        digit()
                ),
                separator()
        );
    }

    @Test
    public void testNumberParsePatternSeparatorPattern() {
        this.numberParseParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                )
        );
    }

    @Test
    public void testNumberParsePatternSeparatorTextPatternFails() {
        this.numberParseParseThrows(
                number(
                        digit()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testNumberParsePatternSeparatorPatternSeparator() {
        this.numberParseParseAndCheck(
                number(
                        digit()
                ),
                separator(),
                number(
                        digit()
                ),
                separator()
        );
    }

    // number helpers...................................................................................................

    private void numberParseParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.numberParse(),
                tokens
        );
    }

    private void numberParseParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.numberParse(),
                tokens
        );
    }

    // fraction........................................................................................................

    @Test
    public void testFractionDayFails() {
        this.fractionParseThrows(digit(), day());
    }

    @Test
    public void testFractionHourFails() {
        this.fractionParseThrows(digit(), hour());
    }

    @Test
    public void testFractionMinuteFails() {
        this.fractionParseThrows(digit(), minute());
    }

    @Test
    public void testFractionMonthFails() {
        this.fractionParseThrows(digit(), month());
    }

    @Test
    public void testFractionSecondFails() {
        this.fractionParseThrows(digit(), second());
    }

    @Test
    public void testFractionStarFails() {
        this.fractionParseThrows(star());
    }

    @Test
    public void testFractionTextPlaceholderFails() {
        this.fractionParseThrows(digit(), textPlaceholder());
    }

    @Test
    public void testFractionUnderscoreFails() {
        this.fractionParseThrows(underscore());
    }

    @Test
    public void testFractionYearFails() {
        this.fractionParseThrows(digit(), year());
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
    public void testFractionDigitFractionFails() {
        this.fractionParseThrows(digit(), fractionSymbol());
    }

    @Test
    public void testFractionGroupSeparatorFails() {
        this.fractionParseThrows(groupSeparator());
    }

    @Test
    public void testFractionDigitGroupSeparatorFails() {
        this.fractionParseThrows(digit(), groupSeparator());
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
    public void testFractionDigitSpaceDigitFractionDigitSpace() {
        this.fractionParseAndCheck(digitSpace(), digit(), fractionSymbol(), digitSpace());
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
    public void testFractionDigitZeroDigitFractionDigitZero() {
        this.fractionParseAndCheck(digitZero(), digit(), fractionSymbol(), digitZero());
    }

    @Test
    public void testFractionDigitZeroFractionDigitZeroDigitZero() {
        this.fractionParseAndCheck(digitZero(), fractionSymbol(), digitZero(), digitZero());
    }

    // digitSpace

    @Test
    public void testFractionDigitFractionDigit() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitDigitSpaceFractionDigit() {
        this.fractionParseAndCheck(digit(), digitSpace(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitDigitZeroFractionDigit() {
        this.fractionParseAndCheck(digit(), digitZero(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitDigitFractionDigit() {
        this.fractionParseAndCheck(digit(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitDigit() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), digit());
    }

    // currency

    @Test
    public void testFractioncurrencyDigitSlashDigit() {
        this.fractionParseAndCheck(currency(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitcurrencySlashDigit() {
        this.fractionParseAndCheck(digit(), currency(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashcurrencyDigit() {
        this.fractionParseAndCheck(currency(), digit(), fractionSymbol(), currency(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitcurrency() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), currency());
    }

    // text literals

    // escaped

    @Test
    public void testFractionEscapedDigitSlashDigit() {
        this.fractionParseAndCheck(escape(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitEscapedSlashDigit() {
        this.fractionParseAndCheck(digit(), escape(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashEscapedDigit() {
        this.fractionParseAndCheck(escape(), digit(), fractionSymbol(), escape(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitEscaped() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), escape());
    }

    // quotedText

    @Test
    public void testFractionQuotedTextDigitSlashDigit() {
        this.fractionParseAndCheck(quotedText(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitQuotedTextSlashDigit() {
        this.fractionParseAndCheck(digit(), quotedText(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashQuotedTextDigit() {
        this.fractionParseAndCheck(quotedText(), digit(), fractionSymbol(), quotedText(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitQuotedText() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), quotedText());
    }

    // closeParens

    @Test
    public void testFractionCloseParensDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralCloseParens(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitCloseParensSlashDigit() {
        this.fractionParseAndCheck(digit(), textLiteralCloseParens(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashCloseParensDigit() {
        this.fractionParseAndCheck(textLiteralCloseParens(), digit(), fractionSymbol(), textLiteralCloseParens(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitCloseParens() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), textLiteralCloseParens());
    }

    // colon

    @Test
    public void testFractionColonDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralColon(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitColonSlashDigit() {
        this.fractionParseAndCheck(digit(), textLiteralColon(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashColonDigit() {
        this.fractionParseAndCheck(textLiteralColon(), digit(), fractionSymbol(), textLiteralColon(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitColon() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), textLiteralColon());
    }

    // minus

    @Test
    public void testFractionMinusDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralMinus(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitMinusSlashDigit() {
        this.fractionParseAndCheck(digit(), textLiteralMinus(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashMinusDigit() {
        this.fractionParseAndCheck(textLiteralMinus(), digit(), fractionSymbol(), textLiteralMinus(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitMinus() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), textLiteralMinus());
    }

    // openParens

    @Test
    public void testFractionOpenParensDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralOpenParens(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitOpenParensSlashDigit() {
        this.fractionParseAndCheck(digit(), textLiteralOpenParens(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashOpenParensDigit() {
        this.fractionParseAndCheck(textLiteralOpenParens(), digit(), fractionSymbol(), textLiteralOpenParens(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitOpenParens() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), textLiteralOpenParens());
    }

    // percentage

    @Test
    public void testFractionPercentageDigitSlashDigit() {
        this.fractionParseAndCheck(percentSymbol(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitPercentageSlashDigit() {
        this.fractionParseAndCheck(digit(), percentSymbol(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashPercentageDigit() {
        this.fractionParseAndCheck(percentSymbol(), digit(), fractionSymbol(), percentSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitPercentage() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), percentSymbol());
    }

    // plus

    @Test
    public void testFractionPlusDigitSlashDigit() {
        this.fractionParseAndCheck(textLiteralPlus(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitPlusSlashDigit() {
        this.fractionParseAndCheck(digit(), textLiteralPlus(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashPlusDigit() {
        this.fractionParseAndCheck(textLiteralPlus(), digit(), fractionSymbol(), textLiteralPlus(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitPlus() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), textLiteralPlus());
    }

    // space

    @Test
    public void testFractionSpaceDigitSlashDigit() {
        this.fractionParseAndCheck(whitespace(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSpaceSlashDigit() {
        this.fractionParseAndCheck(digit(), whitespace(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashSpaceDigit() {
        this.fractionParseAndCheck(whitespace(), digit(), fractionSymbol(), whitespace(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitSpace() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), whitespace());
    }

    // groupSeparator

    @Test
    public void testFractionGroupSeparatorDigitSlashDigit() {
        this.fractionParseAndCheck(groupSeparator(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitGroupSeparatorSlashDigit() {
        this.fractionParseAndCheck(digit(), groupSeparator(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitSlashGroupSeparatorDigit() {
        this.fractionParseAndCheck(groupSeparator(), digit(), fractionSymbol(), groupSeparator(), digit());
    }

    @Test
    public void testFractionDigitSlashDigitGroupSeparator() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), groupSeparator());
    }

    // equals

    @Test
    public void testFractionEqualsDigitFractionDigitFails() {
        this.fractionParseThrows(equalsSymbol(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitEqualsFractionDigitFails() {
        this.fractionParseThrows(digit(), equalsSymbol(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionEqualsDigitFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), equalsSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitEqualsFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), digit(), equalsSymbol());
    }

    // greaterThan

    @Test
    public void testFractionGreaterThanDigitFractionDigitFails() {
        this.fractionParseThrows(greaterThan(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitGreaterThanFractionDigitFails() {
        this.fractionParseThrows(digit(), greaterThan(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionGreaterThanDigitFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), greaterThan(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitGreaterThanFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), digit(), greaterThan());
    }

    // greaterThanEquals

    @Test
    public void testFractionGreaterThanEqualsDigitFractionDigitFails() {
        this.fractionParseThrows(greaterThanEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitGreaterThanEqualsFractionDigitFails() {
        this.fractionParseThrows(digit(), greaterThanEquals(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionGreaterThanEqualsDigitFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), greaterThanEquals(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitGreaterThanEqualsFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), digit(), greaterThanEquals());
    }

    // lessThan

    @Test
    public void testFractionLessThanDigitFractionDigitFails() {
        this.fractionParseThrows(lessThan(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitLessThanFractionDigitFails() {
        this.fractionParseThrows(digit(), lessThan(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionLessThanDigitFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), lessThan(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitLessThanFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), digit(), lessThan());
    }

    // lessThanEquals

    @Test
    public void testFractionLessThanEqualsDigitFractionDigitFails() {
        this.fractionParseThrows(lessThanEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitLessThanEqualsFractionDigitFails() {
        this.fractionParseThrows(digit(), lessThanEquals(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionLessThanEqualsDigitFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), lessThanEquals(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitLessThanEqualsFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), digit(), lessThanEquals());
    }

    // notEquals

    @Test
    public void testFractionNotEqualsDigitFractionDigitFails() {
        this.fractionParseThrows(notEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitNotEqualsFractionDigitFails() {
        this.fractionParseThrows(digit(), notEquals(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionNotEqualsDigitFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), notEquals(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitNotEqualsFails() {
        this.fractionParseThrows(digit(), fractionSymbol(), digit(), notEquals());
    }

    // color

    @Test
    public void testFractionColorDigitFractionDigit() {
        this.fractionParseAndCheck(color(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitColorFractionDigit() {
        this.fractionParseAndCheck(digit(), color(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitColorDigitFractionDigit() {
        this.fractionParseAndCheck(digit(), color(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionDigitFractionColorDigit() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), color(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitColorDigit() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), color(), digit());
    }

    @Test
    public void testFractionDigitFractionDigitColor() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), color());
    }

    // condition

    @Test
    public void testFractionConditionEqualsFraction() {
        this.fractionParseAndCheck(conditionEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionConditionGreaterThanFraction() {
        this.fractionParseAndCheck(conditionGreaterThan(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionConditionGreaterThanEqualsFraction() {
        this.fractionParseAndCheck(conditionGreaterThanEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionConditionLessThanFraction() {
        this.fractionParseAndCheck(conditionLessThan(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionConditionLessThanEqualsFraction() {
        this.fractionParseAndCheck(conditionLessThanEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionConditionNotEqualsFraction() {
        this.fractionParseAndCheck(conditionNotEquals(), digit(), fractionSymbol(), digit());
    }

    @Test
    public void testFractionFractionConditionEquals() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), conditionEquals());
    }

    @Test
    public void testFractionFractionConditionGreaterThan() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), conditionGreaterThan());
    }

    @Test
    public void testFractionFractionConditionGreaterThanEquals() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), conditionGreaterThanEquals());
    }

    @Test
    public void testFractionFractionConditionLessThan() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), conditionLessThan());
    }

    @Test
    public void testFractionFractionConditionLessThanEquals() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), conditionLessThanEquals());
    }

    @Test
    public void testFractionFractionConditionNotEquals() {
        this.fractionParseAndCheck(digit(), fractionSymbol(), digit(), conditionNotEquals());
    }

    // fraction helpers...

    private void fractionParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck3(
                this.fractionParser(),
                SpreadsheetFormatParserToken::fraction,
                tokens
        );
    }

    private void fractionParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                this.fractionParser(),
                tokens
        );
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
        this.generalParseAndCheck(whitespace3(), generalSymbol());
    }

    @Test
    public void testGeneralGeneralWhitespace() {
        this.generalParseAndCheck(generalSymbol(), whitespace3());
    }

    @Test
    public void testGeneralColorGeneral() {
        this.generalParseAndCheck(color(), generalSymbol());
    }

    @Test
    public void testGeneralColorWhitespaceGeneral() {
        this.generalParseAndCheck(color(), whitespace3(), generalSymbol());
    }

    @Test
    public void testGeneralGeneralColor() {
        this.generalParseAndCheck(generalSymbol(), color());
    }

    @Test
    public void testGeneralGeneralColorWhitespace() {
        this.generalParseAndCheck(generalSymbol(), color(), whitespace3());
    }

    @Test
    public void testGeneralGeneralWhitespaceColor() {
        this.generalParseAndCheck(generalSymbol(), whitespace3(), color());
    }

    /**
     * Parsers the general expression using the general parser.
     */
    private void generalParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck3(
                this.generalParser(),
                SpreadsheetFormatParserToken::general,
                tokens
        );
    }

    private Parser<SpreadsheetFormatParserContext> generalParser() {
        return SpreadsheetFormatParsers.general();
    }

    // text........................................................................................................

    @Test
    public void testTextFormatEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.textFormat(),
                ""
        );
    }

    @Test
    public void testTextFormatSeparator() {
        this.textFormatParseAndCheck(
                separator()
        );
    }

    @Test
    public void testTextFormatTextDigitZeroFails() {
        this.textFormatParseThrows(
                digitZero()
        );
    }

    @Test
    public void testTextFormatTextDigitSpaceFails() {
        this.textFormatParseThrows(
                digitSpace()
        );
    }

    @Test
    public void testTextFormatLetterFails() {
        this.textFormatParseThrows(
                textLiteral('A')
        );
    }

    @Test
    public void testTextFormatGeneraFails() {
        this.textFormatParseThrows(
                generalSymbol()
        );
    }

    @Test
    public void testTextFormatEscaped() {
        this.textFormatParseAndCheck(
                text(
                        escape()
                )
        );
    }

    @Test
    public void testTextFormatStar() {
        this.textFormatParseAndCheck(
                text(
                        star()
                )
        );
    }

    @Test
    public void testTextFormatStar2() {
        this.textFormatParseAndCheck(
                text(
                        star2()
                )
        );
    }

    @Test
    public void testTextFormatStarStarFails() {
        this.textFormatParseThrows(
                star(),
                star2()
        );
    }

    @Test
    public void testTextFormatStarTextPlaceholderStarFails() {
        this.textFormatParseThrows(
                star(),
                textPlaceholder(),
                star2()
        );
    }

    // text literals

    @Test
    public void testTextFormatDollar() {
        this.textFormatParseAndCheck(
                text(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testTextFormatMinusSign() {
        this.textFormatParseAndCheck(
                text(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testTextFormatPlusSign() {
        this.textFormatParseAndCheck(
                text(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testTextFormatSlash() {
        this.textFormatParseAndCheck(
                text(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testTextFormatOpenParens() {
        this.textFormatParseAndCheck(
                text(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testTextFormatCloseParens() {
        this.textFormatParseAndCheck(
                text(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testTextFormatColon() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral(':')
                )
        );
    }

    @Test
    public void testTextFormatEqualsSign() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral('=')
                )
        );
    }

    @Test
    public void testTextFormatGreaterThanEquals() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral('>')
                )
        );
    }

    @Test
    public void testTextFormatGreaterThanEqualsSign() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral(">=")
                )
        );
    }

    @Test
    public void testTextFormatLessThan() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral('<')
                )
        );
    }

    @Test
    public void testTextFormatLessThanEqualsSign() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral("<=")
                )
        );
    }

    @Test
    public void testTextFormatNotEqualsSign() {
        this.textFormatParseAndCheck(
                text(
                        textLiteral("!=")
                )
        );
    }

    @Test
    public void testTextFormatSpace() {
        this.textFormatParseAndCheck(
                text(
                        whitespace()
                )
        );
    }

    @Test
    public void testTextFormatSpaceSpaceSpace() {
        this.textFormatParseAndCheck(
                text(
                        whitespace3()
                )
        );
    }

    @Test
    public void testTextFormatTextPlaceholder() {
        this.textFormatParseAndCheck(
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatTextPlaceholder2() {
        this.textFormatParseAndCheck(
                text(
                        textPlaceholder(),
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatTextQuoted() {
        this.textFormatParseAndCheck(
                text(
                        quotedText()
                )
        );
    }

    @Test
    public void testTextFormatUnderscore() {
        this.textFormatParseAndCheck(
                text(
                        underscore()
                )
        );
    }

    @Test
    public void testTextFormatUnderscore2() {
        this.textFormatParseAndCheck(
                text(
                        underscore()
                )
        );
    }

    @Test
    public void testTextFormatUnderscoreUnderscore() {
        this.textFormatParseAndCheck(
                text(
                        underscore(),
                        underscore2()
                )
        );
    }

    @Test
    public void testTextFormatAll() {
        this.textFormatParseAndCheck(
                text(
                        whitespace(),
                        quotedText(),
                        textPlaceholder(),
                        underscore()
                )
        );
    }

    @Test
    public void testTextFormatColorQuotedText() {
        this.textFormatParseAndCheck(
                text(
                        color(),
                        quotedText()
                )
        );
    }

    @Test
    public void testTextFormatQuotedTextColor() {
        this.textFormatParseAndCheck(
                text(
                        quotedText(),
                        color()
                )
        );
    }

    @Test
    public void testTextFormatConditionNotEqualsTextPlaceholderFails() {
        this.textFormatParseThrows(
                conditionNotEquals(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionEqualsTextPlaceholderFails() {
        this.textFormatParseThrows(
                conditionEquals(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionGreaterThanTextPlaceholderFails() {
        this.textFormatParseThrows(
                conditionGreaterThan(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionGreaterThanEqualsTextPlaceholderFails() {
        this.textFormatParseThrows(
                conditionGreaterThanEquals(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionLessThanTextPlaceholderFails() {
        this.textFormatParseThrows(
                conditionLessThan(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionLessThanEqualsTextPlaceholderFails() {
        this.textFormatParseThrows(
                conditionLessThanEquals(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionEqualsFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                conditionEquals()
        );
    }

    @Test
    public void testTextFormatPlaceholderConditionGreaterThanFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testTextFormatPlaceholderConditionGreaterThanEqualsFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testTextFormatPlaceholderConditionLessThanFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                conditionLessThan()
        );
    }

    @Test
    public void testTextFormatPlaceholderConditionLessThanEqualsFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testTextFormatPlaceholderConditionNotEqualsFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                conditionNotEquals()
        );
    }

    // multiple patterns................................................................................................

    @Test
    public void testTextFormatConditionNotEqualsTextPlaceholderPatternSeparatorTextPlaceholder() {
        this.textFormatParseAndCheck(
                conditionNotEquals(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatConditionEqualsTextPlaceholderPatternSeparatorTextPlaceholder() {
        this.textFormatParseAndCheck(
                conditionEquals(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatConditionGreaterThanTextPlaceholderPatternSeparatorTextPlaceholder() {
        this.textFormatParseAndCheck(
                conditionGreaterThan(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatConditionGreaterThanEqualsTextPlaceholderPatternSeparatorTextPlaceholder() {
        this.textFormatParseAndCheck(
                conditionGreaterThanEquals(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatConditionLessThanTextPlaceholderPatternSeparatorTextPlaceholder() {
        this.textFormatParseAndCheck(
                conditionLessThan(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatConditionLessThanEqualsTextPlaceholderPatternSeparatorTextPlaceholder() {
        this.textFormatParseAndCheck(
                conditionLessThanEquals(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatPatternSeparatorPatternFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                separator(),
                textPlaceholder()
        );
    }

    @Test
    public void testTextFormatConditionColorPatternSeparatorColorPattern() {
        this.textFormatParseAndCheck(
                conditionEquals(),
                text(
                        color(),
                        textPlaceholder()
                ),
                separator(),
                text(
                        color(),
                        textPlaceholder()
                )
        );
    }

    @Test
    public void testTextFormatPatternSeparator() {
        this.textFormatParseAndCheck(
                text(
                        textPlaceholder()
                ),
                separator()
        );
    }

    @Test
    public void testTextFormatPatternSeparatorPatternSeparatorFails() {
        this.textFormatParseThrows(
                textPlaceholder(),
                separator(),
                textPlaceholder(),
                separator()
        );
    }

    @Test
    public void testTextFormatConditionPatternSeparatorPatternSeparator() {
        this.textFormatParseAndCheck(
                conditionEquals(),
                text(
                        textPlaceholder()
                ),
                separator(),
                text(
                        textPlaceholder()
                ),
                separator()
        );
    }

    // text helpers......................................................................................................

    private void textFormatParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.textFormat(),
                tokens
        );
    }

    private void textFormatParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.textFormat(),
                tokens
        );
    }

    // time format......................................................................................................

    @Test
    public void testTimeFormatEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.timeFormat(),
                ""
        );
    }

    @Test
    public void testTimeFormatSeparator() {
        this.timeFormatParseAndCheck(
                separator()
        );
    }

    @Test
    public void testTimeFormatSeparatorSeparator() {
        this.timeFormatParseAndCheck(
                separator(),
                separator()
        );
    }

    @Test
    public void testTimeFormatSeparatorSeparatorSeparator() {
        this.timeFormatParseAndCheck(
                separator(),
                separator(),
                separator()
        );
    }

    @Test
    public void testTimeFormatTextDigitFails() {
        this.timeFormatParseThrows(
                digit()
        );
    }

    @Test
    public void testTimeFormatTextDigitZeroFails() {
        this.timeFormatParseThrows(
                digitZero()
        );
    }

    @Test
    public void testTimeFormatTextDigitSpaceFails() {
        this.timeFormatParseThrows(
                digitSpace()
        );
    }

    @Test
    public void testTimeFormatDayFails() {
        this.timeFormatParseThrows(
                day()
        );
    }

    @Test
    public void testTimeFormatYearFails() {
        this.timeFormatParseThrows(
                year()
        );
    }

    @Test
    public void testTimeFormatTextPlaceholderFails() {
        this.timeFormatParseThrows(
                textPlaceholder()
        );
    }

    @Test
    public void testTimeFormatGeneral() {
        this.timeFormatParseAndCheck(
                general()
        );
    }

    @Test
    public void testTimeFormatWhitespaceGeneral() {
        this.timeFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testTimeFormatGeneralWhitespace() {
        this.timeFormatParseAndCheck(
                general(
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testTimeFormatWhitespaceGeneralWhitespace() {
        this.timeFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testTimeFormatColorGeneral() {
        this.timeFormatParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testTimeFormatColorWhitespaceGeneral() {
        this.timeFormatParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testTimeFormatColorEscaped() {
        this.timeFormatParseAndCheck(
                time(
                        color(),
                        escape()
                )
        );
    }

    @Test
    public void testTimeFormatEscaped() {
        this.timeFormatParseAndCheck(
                time(
                        escape()
                )
        );
    }

    @Test
    public void testTimeFormatDollar() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testTimeFormatMinus() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testTimeFormatPlus() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testTimeFormatSlash() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testTimeFormatOpenParen() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testTimeFormatCloseParen() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testTimeFormatColon() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testTimeFormatSpace() {
        this.timeFormatParseAndCheck(
                time(
                        whitespace()
                )
        );
    }

    @Test
    public void testTimeFormatQuotedText() {
        this.timeFormatParseAndCheck(
                time(
                        quotedText()
                )
        );
    }

    @Test
    public void testTimeFormatASlashP() {
        this.timeFormatParseAndCheck(
                time(
                        aSlashP()
                )
        );
    }

    @Test
    public void testTimeFormatAmSlashPm() {
        this.timeFormatParseAndCheck(
                time(
                        amSlashPm()
                )
        );
    }

    @Test
    public void testTimeFormatHour() {
        this.timeFormatParseAndCheck(
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatMinute() {
        this.timeFormatParseAndCheck(
                time(
                        minute()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondASlashP() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        aSlashP()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondAmSlashPm() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        amSlashPm()
                )
        );
    }

    @Test
    public void testTimeFormatHour2Minute2Second2() {
        this.timeFormatParseAndCheck(
                time(
                        hour(2),
                        minute(2),
                        second(2)
                )
        );
    }

    @Test
    public void testTimeFormatHour3Minute3Second3() {
        this.timeFormatParseAndCheck(
                time(
                        hour(3),
                        minute(3),
                        second(3)
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatMinuteHourSecond() {
        this.timeFormatParseAndCheck(
                time(
                        minute(),
                        hour(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatSecondMinuteHour() {
        this.timeFormatParseAndCheck(
                time(
                        second(),
                        minute(),
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatSecondCommaMinuteCommaHour() {
        this.timeFormatParseAndCheck(
                time(
                        second(),
                        textLiteralComma(),
                        minute(),
                        textLiteralComma(),
                        hour()
                )
        );
    }

    // escaped

    @Test
    public void testTimeFormatEscapedHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        escape(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourEscapedMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        escape(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteEscapedSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        escape(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsEscaped() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        escape()
                )
        );
    }

    // quotedText

    @Test
    public void testTimeFormatQuotedTextHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        quotedText(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourQuotedTextMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        quotedText(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteQuotedTextSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        quotedText(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsQuotedText() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        quotedText()
                )
        );
    }

    // closeParens

    @Test
    public void testTimeFormatCloseParensHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralCloseParens(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourCloseParensMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralCloseParens(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteCloseParensSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralCloseParens(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsCloseParens() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralCloseParens()
                )
        );
    }

    // colon

    @Test
    public void testTimeFormatColonHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralColon(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourColonMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralColon(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteColonSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralColon(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsColon() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralColon()
                )
        );
    }

    // dollar

    @Test
    public void testTimeFormatDollarHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralDollar(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourDollarMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralDollar(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteDollarSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralDollar(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsDollar() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralDollar()
                )
        );
    }

    // minus

    @Test
    public void testTimeFormatMinusHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralMinus(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinusMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralMinus(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteMinusSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralMinus(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsMinus() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralMinus()
                )
        );
    }

    // openParens

    @Test
    public void testTimeFormatOpenParensHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralOpenParens(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourOpenParensMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralOpenParens(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteOpenParensSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralOpenParens(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsOpenParens() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralOpenParens()
                )
        );
    }

    // plus

    @Test
    public void testTimeFormatPlusHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralPlus(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourPlusMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralPlus(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinutePlusSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralPlus(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsPlus() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralPlus()
                )
        );
    }

    // slash

    @Test
    public void testTimeFormatSlashHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        textLiteralSlash(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourSlashMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        textLiteralSlash(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSlashSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralSlash(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsSlash() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralSlash()
                )
        );
    }

    // space

    @Test
    public void testTimeFormatSpaceHourMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        whitespace(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourSpaceMinuteSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        whitespace(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSpaceSecond() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        whitespace(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsSpace() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        whitespace()
                )
        );
    }

    // equals

    @Test
    public void testTimeFormatEqualsHourMinuteSecondFails() {
        this.timeFormatParseThrows(
                equalsSymbol(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourEqualsMinuteSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                equalsSymbol(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteEqualsSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                equalsSymbol(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                second(),
                equalsSymbol()
        );
    }

    // greaterThan

    @Test
    public void testTimeFormatGreaterThanHourMinuteSecondFails() {
        this.timeFormatParseThrows(
                greaterThan(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourGreaterThanMinuteSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                greaterThan(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteGreaterThanSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                greaterThan(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsGreaterThanFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                second(),
                greaterThan()
        );
    }

    // greaterThanEquals

    @Test
    public void testTimeFormatGreaterThanEqualsHourMinuteSecondFails() {
        this.timeFormatParseThrows(
                greaterThanEquals(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourGreaterThanEqualsMinuteSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                greaterThanEquals(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteGreaterThanEqualsSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                greaterThanEquals(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsGreaterThanEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                second(),
                greaterThanEquals()
        );
    }

    // lessThan

    @Test
    public void testTimeFormatLessThanHourMinuteSecondFails() {
        this.timeFormatParseThrows(
                lessThan(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourLessThanMinuteSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                lessThan(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteLessThanSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                lessThan(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsLessThanFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                second(),
                lessThan()
        );
    }

    // lessThanEquals

    @Test
    public void testTimeFormatLessThanEqualsHourMinuteSecondFails() {
        this.timeFormatParseThrows(
                lessThanEquals(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourLessThanEqualsMinuteSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                lessThanEquals(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteLessThanEqualsSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                lessThanEquals(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsLessThanEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                second(),
                lessThanEquals()
        );
    }

    // notEquals

    @Test
    public void testTimeFormatNotEqualsHourMinuteSecondFails() {
        this.timeFormatParseThrows(
                notEquals(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourNotEqualsMinuteSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                notEquals(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteNotEqualsSecondFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                notEquals(),
                second()
        );
    }

    @Test
    public void testTimeFormatHourMinuteSecondsNotEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                minute(),
                second(),
                notEquals()
        );
    }

    // color

    @Test
    public void testTimeFormatColorHour() {
        this.timeFormatParseAndCheck(
                time(
                        color(),
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatColorMinute() {
        this.timeFormatParseAndCheck(
                time(
                        color(),
                        minute()
                )
        );
    }

    @Test
    public void testTimeFormatColorSeconds() {
        this.timeFormatParseAndCheck(
                time(
                        color(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatHourColor() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        color()
                )
        );
    }

    @Test
    public void testTimeFormatMinuteColor() {
        this.timeFormatParseAndCheck(
                time(
                        minute(),
                        color()
                )
        );
    }

    @Test
    public void testTimeFormatSecondsColor() {
        this.timeFormatParseAndCheck(
                time(
                        second(),
                        color()
                )
        );
    }

    // condition

    @Test
    public void testTimeFormatConditionEqualsHour() {
        this.timeFormatParseAndCheck(
                conditionEquals(),
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatConditionGreaterThanHour() {
        this.timeFormatParseAndCheck(
                conditionGreaterThan(),
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatConditionGreaterThanEqualsHour() {
        this.timeFormatParseAndCheck(
                conditionGreaterThanEquals(),
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatConditionLessThanHour() {
        this.timeFormatParseAndCheck(
                conditionLessThan(),
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatConditionLessThanEqualsHour() {
        this.timeFormatParseAndCheck(
                conditionLessThanEquals(),
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatConditionNotEqualsHour() {
        this.timeFormatParseAndCheck(
                conditionNotEquals(),
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeFormatHourConditionEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                conditionEquals()
        );
    }

    @Test
    public void testTimeFormatHourConditionGreaterThanFails() {
        this.timeFormatParseThrows(
                hour(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testTimeFormatHourConditionGreaterThanEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testTimeFormatHourConditionLessThanFails() {
        this.timeFormatParseThrows(
                hour(),
                conditionLessThan()
        );
    }

    @Test
    public void testTimeFormatHourConditionLessThanEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testTimeFormatHourConditionNotEqualsFails() {
        this.timeFormatParseThrows(
                hour(),
                conditionNotEquals()
        );
    }

    @Test
    public void testTimeFormatPatternSeparator() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute()
                ),
                separator()
        );
    }

    @Test
    public void testTimeFormatPatternSeparatorPattern() {
        this.timeFormatParseAndCheck(
                time(
                        hour(),
                        minute()
                ),
                separator(),
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatColorPatternSeparatorPattern() {
        this.timeFormatParseAndCheck(
                time(
                        color(),
                        hour(),
                        minute()
                ),
                separator(),
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeFormatConditionPatternSeparatorPattern() {
        this.timeFormatParseAndCheck(
                conditionEquals(),
                time(
                        hour(),
                        minute()
                ),
                separator(),
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    // time format helpers..............................................................................................

    private void timeFormatParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.timeFormat(),
                tokens
        );
    }

    private void timeFormatParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.timeFormat(),
                tokens
        );
    }

    // time parse.......................................................................................................

    @Test
    public void testTimeParseEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.timeParse(),
                ""
        );
    }

    @Test
    public void testTimeParseTextDigitFails() {
        this.timeParseParseThrows(
                digit()
        );
    }

    @Test
    public void testTimeParseTextDigitZeroFails() {
        this.timeParseParseThrows(
                digitZero()
        );
    }

    @Test
    public void testTimeParseTextDigitSpaceFails() {
        this.timeParseParseThrows(
                digitSpace()
        );
    }

    @Test
    public void testTimeParseDayFails() {
        this.timeParseParseThrows(
                day()
        );
    }

    @Test
    public void testTimeParseYearFails() {
        this.timeParseParseThrows(
                year()
        );
    }

    @Test
    public void testTimeParseTextPlaceholderFails() {
        this.timeParseParseThrows(
                textPlaceholder()
        );
    }

    @Test
    public void testTimeParseGeneral() {
        this.timeParseParseAndCheck(
                general()
        );
    }

    @Test
    public void testTimeParseWhitespaceGeneral() {
        this.timeParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testTimeParseGeneralWhitespace() {
        this.timeParseParseAndCheck(
                general(
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testTimeParseWhitespaceGeneralWhitespace() {
        this.timeParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testTimeParseColorGeneral() {
        this.timeParseParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testTimeParseColorWhitespaceGeneral() {
        this.timeParseParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testTimeParseColorEscapedFails() {
        this.timeParseParseThrows(
                time(
                        color(),
                        escape()
                )
        );
    }

    @Test
    public void testTimeParseEscaped() {
        this.timeParseParseAndCheck(
                time(
                        escape()
                )
        );
    }

    @Test
    public void testTimeParseDollar() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testTimeParseMinus() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testTimeParsePlus() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testTimeParseSlash() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testTimeParseOpenParen() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testTimeParseCloseParen() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testTimeParseColon() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testTimeParseSpace() {
        this.timeParseParseAndCheck(
                time(
                        whitespace()
                )
        );
    }

    @Test
    public void testTimeParseQuotedText() {
        this.timeParseParseAndCheck(
                time(
                        quotedText()
                )
        );
    }

    @Test
    public void testTimeParseASlashP() {
        this.timeParseParseAndCheck(
                time(
                        aSlashP()
                )
        );
    }

    @Test
    public void testTimeParseAmSlashPm() {
        this.timeParseParseAndCheck(
                time(
                        amSlashPm()
                )
        );
    }

    @Test
    public void testTimeParseHour() {
        this.timeParseParseAndCheck(
                time(
                        hour()
                )
        );
    }

    @Test
    public void testTimeParseMinute() {
        this.timeParseParseAndCheck(
                time(
                        minute()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondASlashP() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        aSlashP()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondAmSlashPm() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        amSlashPm()
                )
        );
    }

    @Test
    public void testTimeParseHour2Minute2Second2() {
        this.timeParseParseAndCheck(
                time(
                        hour(2),
                        minute(2),
                        second(2)
                )
        );
    }

    @Test
    public void testTimeParseHour3Minute3Second3() {
        this.timeParseParseAndCheck(
                time(
                        hour(3),
                        minute(3),
                        second(3)
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseMinuteHourSecond() {
        this.timeParseParseAndCheck(
                time(
                        minute(),
                        hour(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseSecondMinuteHour() {
        this.timeParseParseAndCheck(
                time(
                        second(),
                        minute(),
                        hour()
                )
        );
    }

    @Test
    public void testTimeParseSecondCommaMinuteCommaHour() {
        this.timeParseParseAndCheck(
                time(
                        second(),
                        textLiteralComma(),
                        minute(),
                        textLiteralComma(),
                        hour()
                )
        );
    }

    // escaped

    @Test
    public void testTimeParseEscapedHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        escape(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourEscapedMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        escape(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteEscapedSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        escape(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsEscaped() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        escape()
                )
        );
    }

    // quotedText

    @Test
    public void testTimeParseQuotedTextHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        quotedText(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourQuotedTextMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        quotedText(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteQuotedTextSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        quotedText(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsQuotedText() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        quotedText()
                )
        );
    }

    // closeParens

    @Test
    public void testTimeParseCloseParensHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralCloseParens(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourCloseParensMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralCloseParens(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteCloseParensSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralCloseParens(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsCloseParens() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralCloseParens()
                )
        );
    }

    // colon

    @Test
    public void testTimeParseColonHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralColon(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourColonMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralColon(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteColonSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralColon(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsColon() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralColon()
                )
        );
    }

    // dollar

    @Test
    public void testTimeParseDollarHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralDollar(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourDollarMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralDollar(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteDollarSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralDollar(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsDollar() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralDollar()
                )
        );
    }

    // minus

    @Test
    public void testTimeParseMinusHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralMinus(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinusMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralMinus(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteMinusSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralMinus(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsMinus() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralMinus()
                )
        );
    }

    // openParens

    @Test
    public void testTimeParseOpenParensHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralOpenParens(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourOpenParensMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralOpenParens(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteOpenParensSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralOpenParens(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsOpenParens() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralOpenParens()
                )
        );
    }

    // plus

    @Test
    public void testTimeParsePlusHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralPlus(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourPlusMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralPlus(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinutePlusSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralPlus(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsPlus() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralPlus()
                )
        );
    }

    // slash

    @Test
    public void testTimeParseSlashHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        textLiteralSlash(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourSlashMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        textLiteralSlash(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSlashSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        textLiteralSlash(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsSlash() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        textLiteralSlash()
                )
        );
    }

    // space

    @Test
    public void testTimeParseSpaceHourMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        whitespace(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourSpaceMinuteSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        whitespace(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSpaceSecond() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        whitespace(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsSpace() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute(),
                        second(),
                        whitespace()
                )
        );
    }

    // equals

    @Test
    public void testTimeParseEqualsHourMinuteSecondFails() {
        this.timeParseParseThrows(
                equalsSymbol(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourEqualsMinuteSecondFails() {
        this.timeParseParseThrows(
                hour(),
                equalsSymbol(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteEqualsSecondFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                equalsSymbol(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                second(),
                equalsSymbol()
        );
    }

    // greaterThan

    @Test
    public void testTimeParseGreaterThanHourMinuteSecondFails() {
        this.timeParseParseThrows(
                greaterThan(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourGreaterThanMinuteSecondFails() {
        this.timeParseParseThrows(
                hour(),
                greaterThan(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteGreaterThanSecondFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                greaterThan(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsGreaterThanFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                second(),
                greaterThan()
        );
    }

    // greaterThanEquals

    @Test
    public void testTimeParseGreaterThanEqualsHourMinuteSecondFails() {
        this.timeParseParseThrows(
                greaterThanEquals(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourGreaterThanEqualsMinuteSecondFails() {
        this.timeParseParseThrows(
                hour(),
                greaterThanEquals(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteGreaterThanEqualsSecondFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                greaterThanEquals(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsGreaterThanEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                second(),
                greaterThanEquals()
        );
    }

    // lessThan

    @Test
    public void testTimeParseLessThanHourMinuteSecondFails() {
        this.timeParseParseThrows(
                lessThan(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourLessThanMinuteSecondFails() {
        this.timeParseParseThrows(
                hour(),
                lessThan(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteLessThanSecondFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                lessThan(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsLessThanFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                second(),
                lessThan()
        );
    }

    // lessThanEquals

    @Test
    public void testTimeParseLessThanEqualsHourMinuteSecondFails() {
        this.timeParseParseThrows(
                lessThanEquals(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourLessThanEqualsMinuteSecondFails() {
        this.timeParseParseThrows(
                hour(),
                lessThanEquals(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteLessThanEqualsSecondFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                lessThanEquals(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsLessThanEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                second(),
                lessThanEquals()
        );
    }

    // notEquals

    @Test
    public void testTimeParseNotEqualsHourMinuteSecondFails() {
        this.timeParseParseThrows(
                notEquals(),
                hour(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourNotEqualsMinuteSecondFails() {
        this.timeParseParseThrows(
                hour(),
                notEquals(),
                minute(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteNotEqualsSecondFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                notEquals(),
                second()
        );
    }

    @Test
    public void testTimeParseHourMinuteSecondsNotEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                minute(),
                second(),
                notEquals()
        );
    }

    // color

    @Test
    public void testTimeParseColorHourFails() {
        this.timeParseParseThrows(
                time(
                        color(),
                        hour()
                )
        );
    }

    @Test
    public void testTimeParseColorMinuteFails() {
        this.timeParseParseThrows(
                time(
                        color(),
                        minute()
                )
        );
    }

    @Test
    public void testTimeParseColorSecondsFails() {
        this.timeParseParseThrows(
                time(
                        color(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseHourColorFails() {
        this.timeParseParseThrows(
                time(
                        hour(),
                        color()
                )
        );
    }

    @Test
    public void testTimeParseMinuteColorFails() {
        this.timeParseParseThrows(
                time(
                        minute(),
                        color()
                )
        );
    }

    @Test
    public void testTimeParseSecondsColorFails() {
        this.timeParseParseThrows(
                time(
                        second(),
                        color()
                )
        );
    }

    // condition

    @Test
    public void testTimeParseConditionEqualsHourFails() {
        this.timeParseParseThrows(
                conditionEquals(),
                hour()
        );
    }

    @Test
    public void testTimeParseConditionGreaterThanHourFails() {
        this.timeParseParseThrows(
                conditionGreaterThan(),
                hour()
        );
    }

    @Test
    public void testTimeParseConditionGreaterThanEqualsHourFails() {
        this.timeParseParseThrows(
                conditionGreaterThanEquals(),
                hour()
        );
    }

    @Test
    public void testTimeParseConditionLessThanHourFails() {
        this.timeParseParseThrows(
                conditionLessThan(),
                hour()
        );
    }

    @Test
    public void testTimeParseConditionLessThanEqualsHourFails() {
        this.timeParseParseThrows(
                conditionLessThanEquals(),
                hour()
        );
    }

    @Test
    public void testTimeParseConditionNotEqualsHourFails() {
        this.timeParseParseThrows(
                conditionNotEquals(),
                hour()
        );
    }

    @Test
    public void testTimeParseHourConditionEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                conditionEquals()
        );
    }

    @Test
    public void testTimeParseHourConditionGreaterThanFails() {
        this.timeParseParseThrows(
                hour(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testTimeParseHourConditionGreaterThanEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testTimeParseHourConditionLessThanFails() {
        this.timeParseParseThrows(
                hour(),
                conditionLessThan()
        );
    }

    @Test
    public void testTimeParseHourConditionLessThanEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testTimeParseHourConditionNotEqualsFails() {
        this.timeParseParseThrows(
                hour(),
                conditionNotEquals()
        );
    }

    @Test
    public void testTimeParsePatternSeparator() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute()
                ),
                separator()
        );
    }

    @Test
    public void testTimeParsePatternSeparatorPattern() {
        this.timeParseParseAndCheck(
                time(
                        hour(),
                        minute()
                ),
                separator(),
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testTimeParseColorPatternSeparatorPatternFails() {
        this.timeParseParseThrows(
                time(
                        color(),
                        hour(),
                        minute()
                ),
                separator(),
                time(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    private void timeParseParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.timeParse(),
                tokens
        );
    }

    private void timeParseParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.timeParse(),
                tokens
        );
    }

    // dateTime format..................................................................................................

    @Test
    public void testDateTimeFormatEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.dateTimeFormat(),
                ""
        );
    }

    @Test
    public void testDateTimeFormatSeparator() {
        this.dateTimeFormatParseAndCheck(
                separator()
        );
    }

    @Test
    public void testDateTimeFormatSeparatorSeparator() {
        this.dateTimeFormatParseAndCheck(
                separator(),
                separator()
        );
    }

    @Test
    public void testDateTimeFormatSeparatorSeparatorSeparator() {
        this.dateTimeFormatParseAndCheck(
                separator(),
                separator(),
                separator()
        );
    }

    @Test
    public void testDateTimeFormatGeneral() {
        this.dateTimeFormatParseAndCheck(
                general()
        );
    }

    @Test
    public void testDateTimeFormatWhitespaceGeneral() {
        this.dateTimeFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateTimeFormatGeneralWhitespace() {
        this.dateTimeFormatParseAndCheck(
                general(
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateTimeFormatWhitespaceGeneralWhitespace() {
        this.dateTimeFormatParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateTimeFormatColorGeneral() {
        this.dateTimeFormatParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateTimeFormatColorWhitespaceGeneral() {
        this.dateTimeFormatParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateTimeFormatColorEscaped() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        color(),
                        escape()
                )
        );
    }

    @Test
    public void testDateTimeFormatEscaped() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        escape()
                )
        );
    }

    @Test
    public void testDateTimeFormatDollar() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testDateTimeFormatMinus() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testDateTimeFormatPlus() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testDateTimeFormatSlash() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testDateTimeFormatOpenParen() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testDateTimeFormatCloseParen() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testDateTimeFormatColon() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testDateTimeFormatSpace() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        whitespace()
                )
        );
    }

    @Test
    public void testDateTimeFormatQuotedText() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        quotedText()
                )
        );
    }

    // date only........................................................................................................

    @Test
    public void testDateTimeFormatDay() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayMonth() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        month()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayMonthYear() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year()
                )
        );
    }

    // time only........................................................................................................

    @Test
    public void testDateTimeFormatHour() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatHourMinute() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        hour(),
                        minute()
                )
        );
    }

    @Test
    public void testDateTimeFormatHourMinuteSecond() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    // time with millis..................................................................................................

    @Test
    public void testDateTimeFormatSecondsDecimal() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        second(),
                        decimalPoint()
                )
        );
    }

    @Test
    public void testDateTimeFormatSecondsDecimalNonZeroFails() {
        this.dateTimeFormatParseThrows(
                second(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testDateTimeFormatSecondsDecimalSpaceFails() {
        this.dateTimeFormatParseThrows(
                second(),
                decimalPoint(),
                digitSpace()
        );
    }

    @Test
    public void testDateTimeFormatSecondsDecimalDigitZero() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        second(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testDateTimeFormatSecondsDecimalDigitZeroZero() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        second(),
                        decimalPoint(),
                        digitZero(),
                        digitZero()
                )
        );
    }

    // date&time........................................................................................................

    @Test
    public void testDateTimeFormatDayHour() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayHour2() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        hour(),
                        day(),
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayMonthYearHourMinuteSecond() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayMonthYearHourMinuteSecond2() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second(),
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayMonthYearHourMinuteSecondMillis() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second(),
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testDateTimeFormatColorDay() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        color(),
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayColor() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        color()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayCommaMonthCommaYear() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        day(),
                        textLiteralComma(),
                        month(),
                        textLiteralComma(),
                        year()
                )
        );
    }

    // condition

    @Test
    public void testDateTimeFormatConditionEqualsDay() {
        this.dateTimeFormatParseAndCheck(
                conditionEquals(),
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionGreaterThanDay() {
        this.dateTimeFormatParseAndCheck(
                conditionGreaterThan(),
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionGreaterThanEqualsDay() {
        this.dateTimeFormatParseAndCheck(
                conditionGreaterThanEquals(),
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionLessThanDay() {
        this.dateTimeFormatParseAndCheck(
                conditionLessThan(),
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionLessThanEqualsDay() {
        this.dateTimeFormatParseAndCheck(
                conditionLessThanEquals(),
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionNotEqualsDay() {
        this.dateTimeFormatParseAndCheck(
                conditionNotEquals(),
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionEqualsHour() {
        this.dateTimeFormatParseAndCheck(
                conditionEquals(),
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionGreaterThanHour() {
        this.dateTimeFormatParseAndCheck(
                conditionGreaterThan(),
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionGreaterThanEqualsHour() {
        this.dateTimeFormatParseAndCheck(
                conditionGreaterThanEquals(),
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionLessThanHour() {
        this.dateTimeFormatParseAndCheck(
                conditionLessThan(),
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionLessThanEqualsHour() {
        this.dateTimeFormatParseAndCheck(
                conditionLessThanEquals(),
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionNotEqualsHour() {
        this.dateTimeFormatParseAndCheck(
                conditionNotEquals(),
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeFormatDayConditionFails() {
        this.dateTimeFormatParseThrows(
                day(),
                conditionEquals()
        );
    }

    @Test
    public void testDateTimeFormatDayConditionGreaterThanFails() {
        this.dateTimeFormatParseThrows(
                day(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testDateTimeFormatDayConditionGreaterThanEqualsFails() {
        this.dateTimeFormatParseThrows(
                day(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testDateTimeFormatDayConditionLessThanFails() {
        this.dateTimeFormatParseThrows(
                day(),
                conditionLessThan()
        );
    }

    @Test
    public void testDateTimeFormatDayConditionLessThanEqualsFails() {
        this.dateTimeFormatParseThrows(
                day(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testDateTimeFormatDayConditionNotEqualsFails() {
        this.dateTimeFormatParseThrows(
                day(),
                conditionNotEquals()
        );
    }

    @Test
    public void testDateTimeFormatHourConditionEqualsFails() {
        this.dateTimeFormatParseThrows(
                hour(),
                conditionEquals()
        );
    }

    @Test
    public void testDateTimeFormatHourConditionGreaterThanFails() {
        this.dateTimeFormatParseThrows(
                hour(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testDateTimeFormatHourConditionGreaterThanEqualsFails() {
        this.dateTimeFormatParseThrows(
                hour(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testDateTimeFormatHourConditionLessThanFails() {
        this.dateTimeFormatParseThrows(
                hour(),
                conditionLessThan()
        );
    }

    @Test
    public void testDateTimeFormatHourConditionLessThanEqualsFails() {
        this.dateTimeFormatParseThrows(
                hour(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testDateTimeFormatHourConditionNotEqualsFails() {
        this.dateTimeFormatParseThrows(
                hour(),
                conditionNotEquals()
        );
    }

    @Test
    public void testDateTimeFormatPatternSeparator() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator()
        );
    }

    @Test
    public void testDateTimeFormatPatternSeparatorPattern() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator(),
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeFormatColorPatternSeparatorPattern() {
        this.dateTimeFormatParseAndCheck(
                dateTime(
                        color(),
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator(),
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeFormatConditionPatternSeparatorPattern() {
        this.dateTimeFormatParseAndCheck(
                conditionEquals(),
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator(),
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    // date format helpers..............................................................................................

    private void dateTimeFormatParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.dateTimeFormat(),
                tokens
        );
    }

    private void dateTimeFormatParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.dateTimeFormat(),
                tokens
        );
    }

    // date time parse..................................................................................................

    @Test
    public void testDateTimeParseEmpty() {
        this.parseFailAndCheck(
                SpreadsheetFormatParsers.dateTimeParse(),
                ""
        );
    }

    @Test
    public void testDateTimeParseGeneral() {
        this.dateTimeParseParseAndCheck(
                general()
        );
    }

    @Test
    public void testDateTimeParseWhitespaceGeneral() {
        this.dateTimeParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateTimeParseGeneralWhitespace() {
        this.dateTimeParseParseAndCheck(
                general(
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateTimeParseWhitespaceGeneralWhitespace() {
        this.dateTimeParseParseAndCheck(
                general(
                        whitespace3(),
                        generalSymbol(),
                        whitespace3()
                )
        );
    }

    @Test
    public void testDateTimeParseColorGeneral() {
        this.dateTimeParseParseAndCheck(
                general(
                        color(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateTimeParseColorWhitespaceGeneral() {
        this.dateTimeParseParseAndCheck(
                general(
                        color(),
                        whitespace3(),
                        generalSymbol()
                )
        );
    }

    @Test
    public void testDateTimeParseColorEscapedFails() {
        this.dateTimeParseParseThrows(
                dateTime(
                        color(),
                        escape()
                )
        );
    }

    @Test
    public void testDateTimeParseEscaped() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        escape()
                )
        );
    }

    @Test
    public void testDateTimeParseDollar() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralDollar()
                )
        );
    }

    @Test
    public void testDateTimeParseMinus() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralMinus()
                )
        );
    }

    @Test
    public void testDateTimeParsePlus() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralPlus()
                )
        );
    }

    @Test
    public void testDateTimeParseSlash() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralSlash()
                )
        );
    }

    @Test
    public void testDateTimeParseOpenParen() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralOpenParens()
                )
        );
    }

    @Test
    public void testDateTimeParseCloseParen() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralCloseParens()
                )
        );
    }

    @Test
    public void testDateTimeParseColon() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        textLiteralColon()
                )
        );
    }

    @Test
    public void testDateTimeParseSpace() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        whitespace()
                )
        );
    }

    @Test
    public void testDateTimeParseQuotedText() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        quotedText()
                )
        );
    }

    // date only........................................................................................................

    @Test
    public void testDateTimeParseDay() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day()
                )
        );
    }

    @Test
    public void testDateTimeParseDayMonth() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        month()
                )
        );
    }

    @Test
    public void testDateTimeParseDayMonthYear() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year()
                )
        );
    }

    // time only........................................................................................................

    @Test
    public void testDateTimeParseHour() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeParseHourMinute() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        hour(),
                        minute()
                )
        );
    }

    @Test
    public void testDateTimeParseHourMinuteSecond() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    // time with millis..................................................................................................

    @Test
    public void testDateTimeParseSecondsDecimal() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        second(),
                        decimalPoint()
                )
        );
    }

    @Test
    public void testDateTimeParseSecondsDecimalNonZeroFails() {
        this.dateTimeParseParseThrows(
                second(),
                decimalPoint(),
                digit()
        );
    }

    @Test
    public void testDateTimeParseSecondsDecimalSpaceFails() {
        this.dateTimeParseParseThrows(
                second(),
                decimalPoint(),
                digitSpace()
        );
    }

    @Test
    public void testDateTimeParseSecondsDecimalDigitZero() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        second(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testDateTimeParseSecondsDecimalDigitZeroZero() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        second(),
                        decimalPoint(),
                        digitZero(),
                        digitZero()
                )
        );
    }

    // date&time........................................................................................................

    @Test
    public void testDateTimeParseDayHour() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeParseDayHour2() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        hour(),
                        day(),
                        hour()
                )
        );
    }

    @Test
    public void testDateTimeParseDayMonthYearHourMinuteSecond() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeParseDayMonthYearHourMinuteSecond2() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second(),
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeParseDayMonthYearHourMinuteSecondMillis() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second(),
                        day(),
                        month(),
                        year(),
                        hour(),
                        minute(),
                        second(),
                        decimalPoint(),
                        digitZero()
                )
        );
    }

    @Test
    public void testDateTimeParseColorDayFails() {
        this.dateTimeParseParseThrows(
                dateTime(
                        color(),
                        day()
                )
        );
    }

    @Test
    public void testDateTimeParseDayColorFails() {
        this.dateTimeParseParseThrows(
                dateTime(
                        day(),
                        color()
                )
        );
    }

    @Test
    public void testDateTimeParseDayCommaMonthCommaYear() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        day(),
                        textLiteralComma(),
                        month(),
                        textLiteralComma(),
                        year()
                )
        );
    }

    // condition

    @Test
    public void testDateTimeParseConditionEqualsDayFails() {
        this.dateTimeParseParseThrows(
                conditionEquals(),
                day()
        );
    }

    @Test
    public void testDateTimeParseConditionGreaterThanDayFails() {
        this.dateTimeParseParseThrows(
                conditionGreaterThan(),
                day()
        );
    }

    @Test
    public void testDateTimeParseConditionGreaterThanEqualsDayFails() {
        this.dateTimeParseParseThrows(
                conditionGreaterThanEquals(),
                day()
        );
    }

    @Test
    public void testDateTimeParseConditionLessThanDayFails() {
        this.dateTimeParseParseThrows(
                conditionLessThan(),
                day()
        );
    }

    @Test
    public void testDateTimeParseConditionLessThanEqualsDayFails() {
        this.dateTimeParseParseThrows(
                conditionLessThanEquals(),
                day()
        );
    }

    @Test
    public void testDateTimeParseConditionNotEqualsDayFails() {
        this.dateTimeParseParseThrows(
                conditionNotEquals(),
                day()
        );
    }

    @Test
    public void testDateTimeParseConditionEqualsHourFails() {
        this.dateTimeParseParseThrows(
                conditionEquals(),
                hour()
        );
    }

    @Test
    public void testDateTimeParseConditionGreaterThanHourFails() {
        this.dateTimeParseParseThrows(
                conditionGreaterThan(),
                hour()
        );
    }

    @Test
    public void testDateTimeParseConditionGreaterThanEqualsHourFails() {
        this.dateTimeParseParseThrows(
                conditionGreaterThanEquals(),
                hour()
        );
    }

    @Test
    public void testDateTimeParseConditionLessThanHourFails() {
        this.dateTimeParseParseThrows(
                conditionLessThan(),
                hour()
        );
    }

    @Test
    public void testDateTimeParseConditionLessThanEqualsHourFails() {
        this.dateTimeParseParseThrows(
                conditionLessThanEquals(),
                hour()
        );
    }

    @Test
    public void testDateTimeParseConditionNotEqualsHourFails() {
        this.dateTimeParseParseThrows(
                conditionNotEquals(),
                hour()
        );
    }

    @Test
    public void testDateTimeParseDayConditionFails() {
        this.dateTimeParseParseThrows(
                day(),
                conditionEquals()
        );
    }

    @Test
    public void testDateTimeParseDayConditionGreaterThanFails() {
        this.dateTimeParseParseThrows(
                day(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testDateTimeParseDayConditionGreaterThanEqualsFails() {
        this.dateTimeParseParseThrows(
                day(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testDateTimeParseDayConditionLessThanFails() {
        this.dateTimeParseParseThrows(
                day(),
                conditionLessThan()
        );
    }

    @Test
    public void testDateTimeParseDayConditionLessThanEqualsFails() {
        this.dateTimeParseParseThrows(
                day(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testDateTimeParseDayConditionNotEqualsFails() {
        this.dateTimeParseParseThrows(
                day(),
                conditionNotEquals()
        );
    }

    @Test
    public void testDateTimeParseHourConditionEqualsFails() {
        this.dateTimeParseParseThrows(
                hour(),
                conditionEquals()
        );
    }

    @Test
    public void testDateTimeParseHourConditionGreaterThanFails() {
        this.dateTimeParseParseThrows(
                hour(),
                conditionGreaterThan()
        );
    }

    @Test
    public void testDateTimeParseHourConditionGreaterThanEqualsFails() {
        this.dateTimeParseParseThrows(
                hour(),
                conditionGreaterThanEquals()
        );
    }

    @Test
    public void testDateTimeParseHourConditionLessThanFails() {
        this.dateTimeParseParseThrows(
                hour(),
                conditionLessThan()
        );
    }

    @Test
    public void testDateTimeParseHourConditionLessThanEqualsFails() {
        this.dateTimeParseParseThrows(
                hour(),
                conditionLessThanEquals()
        );
    }

    @Test
    public void testDateTimeParseHourConditionNotEqualsFails() {
        this.dateTimeParseParseThrows(
                hour(),
                conditionNotEquals()
        );
    }


    @Test
    public void testDateTimeParsePatternSeparator() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator()
        );
    }

    @Test
    public void testDateTimeParsePatternSeparatorPattern() {
        this.dateTimeParseParseAndCheck(
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator(),
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    @Test
    public void testDateTimeParseColorPatternSeparatorPatternFails() {
        this.dateTimeParseParseThrows(
                dateTime(
                        color(),
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute()
                ),
                separator(),
                dateTime(
                        year(),
                        month(),
                        day(),
                        hour(),
                        minute(),
                        second()
                )
        );
    }

    private void dateTimeParseParseAndCheck(final SpreadsheetFormatParserToken... tokens) {
        this.parseAndCheck2(
                SpreadsheetFormatParsers.dateTimeParse(),
                tokens
        );
    }

    private void dateTimeParseParseThrows(final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows2(
                SpreadsheetFormatParsers.dateTimeParse(),
                tokens
        );
    }

    // helpers................................................................................................

    private void parseAndCheck2(final Parser<SpreadsheetFormatParserContext> parser,
                                final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        final String text = ParserToken.text(list);

        this.checkEquals(
                text.toUpperCase(),
                text,
                "text should be all upper case"
        );

        this.parseAndCheck4(
                parser,
                sequence(list)
        );

        final List<ParserToken> lower = Arrays.stream(tokens)
                .map(SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor::toLower)
                .collect(Collectors.toList());

        this.parseAndCheck4(
                parser,
                sequence(lower)
        );
    }

    private void parseAndCheck3(final Parser<SpreadsheetFormatParserContext> parser,
                                final BiFunction<List<ParserToken>, String, SpreadsheetFormatParserToken> factory,
                                final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);
        final String text = ParserToken.text(list);

        this.checkEquals(
                text.toUpperCase(),
                text,
                "text should be all upper case"
        );

        this.parseAndCheck4(
                parser,
                factory.apply(
                        list,
                        text
                )
        );

        final List<ParserToken> lower = Arrays.stream(tokens)
                .map(SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor::toLower)
                .collect(Collectors.toList());
        final String textLower = text.toLowerCase();

        this.parseAndCheck4(
                parser,
                factory.apply(
                        lower,
                        textLower
                )
        );
    }

    private void parseAndCheck4(final Parser<SpreadsheetFormatParserContext> parser,
                                final ParserToken token) {
        final List<ParserToken> list = Lists.of(token);
        final String text = ParserToken.text(list);

        this.parseAndCheck(
                parser,
                text,
                token,
                text
        );
    }

    private void parseFailAndCheck2(final Parser<SpreadsheetFormatParserContext> parser,
                                    final SpreadsheetFormatParserToken... tokens) {
        // https://github.com/mP1/walkingkooka-spreadsheet/issues/2626
        final TextCursor cursor = TextCursors.charSequence(
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
        final TextCursorSavePoint start = cursor.save();

        this.parse(
                parser,
                cursor,
                this.createContext()
        );
        this.checkNotEquals(
                "",
                start.textBetween().toString()
        );
    }

    private void parseFailAndCheck3(final Parser<SpreadsheetFormatParserContext> parser,
                                    final SpreadsheetFormatParserToken... tokens) {
        // https://github.com/mP1/walkingkooka-spreadsheet/issues/2626
        final String text = ParserToken.text(
                Lists.of(tokens)
        );
        final TextCursor cursor = TextCursors.charSequence(text);
        final TextCursorSavePoint start = cursor.save();

        this.checkEquals(
                Optional.empty(),
                this.parse(
                        parser,
                        cursor,
                        this.createContext()
                )
        );

        this.checkEquals(
                text,
                start.textBetween().toString()
        );
    }

    private void parseThrows2(final Parser<SpreadsheetFormatParserContext> parser,
                              final SpreadsheetFormatParserToken... tokens) {
        this.parseThrows(parser.orFailIfCursorNotEmpty(ParserReporters.basic()),
                ParserToken.text(Lists.of(tokens)));
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> createParser() {
        return Parsers.fake();
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
