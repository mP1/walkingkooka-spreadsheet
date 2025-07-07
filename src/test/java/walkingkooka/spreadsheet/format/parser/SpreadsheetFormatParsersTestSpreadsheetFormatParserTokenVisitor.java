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
        return visitor.children.get(0)
            .cast(SpreadsheetFormatParserToken.class);
    }

    private SpreadsheetFormatParsersTestSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ColorSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::color);
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::date);
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::dateTime);
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::equalsSpreadsheetFormatParserToken);
    }

    @Override
    protected Visiting startVisit(final ExponentSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ExponentSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::exponent);
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::expression);
    }

    @Override
    protected Visiting startVisit(final FractionSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final FractionSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::fraction);
    }

    @Override
    protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GeneralSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::general);
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::greaterThanEquals);
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::greaterThan);
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::lessThanEquals);
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::lessThan);
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::notEquals);
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::number);
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TextSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::text);
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TimeSpreadsheetFormatParserToken token) {
        this.exit(SpreadsheetFormatParserToken::time);
    }

    @Override
    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::amPm);
    }

    @Override
    protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final ColorLiteralSymbolSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::colorLiteralSymbol);
    }

    @Override
    protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::colorName);
    }

    @Override
    protected void visit(final ColorNumberSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final CurrencySpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final DaySpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::day);
    }

    @Override
    protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final DigitSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final DigitSpaceSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final EqualsSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final EscapeSpreadsheetFormatParserToken token) {
        this.addCharacter(token, SpreadsheetFormatParserToken::escape);
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::exponentSymbol);
    }

    @Override
    protected void visit(final FractionSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final GeneralSymbolSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::generalSymbol);
    }

    @Override
    protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final GreaterThanSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final HourSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::hour);
    }

    @Override
    protected void visit(final LessThanEqualsSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final LessThanSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::minute);
    }

    @Override
    protected void visit(final MonthSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::month);
    }

    @Override
    protected void visit(final NotEqualsSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final PercentSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final QuotedTextSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::quotedText);
    }

    @Override
    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::second);
    }

    @Override
    protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final StarSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::textLiteral);
    }

    @Override
    protected void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final GroupSeparatorSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final UnderscoreSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetFormatParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final YearSpreadsheetFormatParserToken token) {
        this.addString(token, SpreadsheetFormatParserToken::year);
    }

    // GENERAL PURPOSE .................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
        return Visiting.CONTINUE;
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
