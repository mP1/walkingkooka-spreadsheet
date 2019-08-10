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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.BiFunction;

final class SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    /**
     * Accepts a graph of {@link SpreadsheetFormatParserToken} and returns the same tokens but with all text and values lower-cased.
     */
    static SpreadsheetFormatParserToken toLower(final SpreadsheetFormatParserToken token) {
        final SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor.children.get(0).cast();
    }

    private SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatColorParserToken token) {
        this.exit(SpreadsheetFormatParserToken::color);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateParserToken token) {
        this.exit(SpreadsheetFormatParserToken::date);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.exit(SpreadsheetFormatParserToken::dateTime);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.exit(SpreadsheetFormatParserToken::equalsParserToken);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatExponentParserToken token) {
        this.exit(SpreadsheetFormatParserToken::exponent);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExpressionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatExpressionParserToken token) {
        this.exit(SpreadsheetFormatParserToken::expression);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatFractionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatFractionParserToken token) {
        this.exit(SpreadsheetFormatParserToken::fraction);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGeneralParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGeneralParserToken token) {
        this.exit(SpreadsheetFormatParserToken::general);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.exit(SpreadsheetFormatParserToken::greaterThanEquals);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.exit(SpreadsheetFormatParserToken::greaterThan);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.exit(SpreadsheetFormatParserToken::lessThanEquals);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.exit(SpreadsheetFormatParserToken::lessThan);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.exit(SpreadsheetFormatParserToken::notEquals);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNumberParserToken token) {
        this.exit(SpreadsheetFormatParserToken::number);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatTextParserToken token) {
        this.exit(SpreadsheetFormatParserToken::text);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatTimeParserToken token) {
        this.exit(SpreadsheetFormatParserToken::time);
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::amPm);
    }

    @Override
    protected void visit(final SpreadsheetFormatBracketCloseSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatBracketOpenSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorLiteralSymbolParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::colorLiteralSymbol);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNameParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::colorName);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNumberParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatConditionNumberParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::day);
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatEqualsSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.addCharacter(token, SpreadsheetFormatParserToken::escape);
    }

    @Override
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::exponentSymbol);
    }

    @Override
    protected void visit(final SpreadsheetFormatFractionSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatGeneralSymbolParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::generalSymbol);
    }

    @Override
    protected void visit(final SpreadsheetFormatGreaterThanEqualsSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatGreaterThanSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::hour);
    }

    @Override
    protected void visit(final SpreadsheetFormatLessThanEqualsSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatLessThanSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthOrMinuteParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::monthOrMinute);
    }

    @Override
    protected void visit(final SpreadsheetFormatNotEqualsSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::quotedText);
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::second);
    }

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatStarParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::textLiteral);
    }

    @Override
    protected void visit(final SpreadsheetFormatTextPlaceholderParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatUnderscoreParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::year);
    }

    // GENERAL PURPOSE .................................................................................................

    private void enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
    }

    private void exit(final BiFunction<List<ParserToken>, String, SpreadsheetFormatParserToken> factory) {
        final List<ParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(factory.apply(children, ParserToken.text(children)));
    }

    private <T extends SpreadsheetFormatLeafParserToken<Character>> void addCharacter(final T token,
                                                                                      final BiFunction<Character, String, T> factory) {
        this.add(factory.apply(Character.toLowerCase(token.value()), token.text().toLowerCase()));
    }

    private <T extends SpreadsheetFormatLeafParserToken<String>> void addString(final T token,
                                                                                final BiFunction<String, String, T> factory) {
        this.add(factory.apply(token.value().toLowerCase(), token.text().toLowerCase()));
    }

    private void add(final SpreadsheetFormatParserToken token) {
        this.children.add(token);
    }

    private Stack<List<ParserToken>> previousChildren = Stacks.arrayList();

    private List<ParserToken> children = Lists.array();

    @Override
    public String toString() {
        return this.children + "," + this.previousChildren;
    }
}
