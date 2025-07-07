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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

public final class SpreadsheetFormatParserTokenVisitorTest extends SpreadsheetFormatParserTestCase implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testColor() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken name = colorName("RED");
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final ColorSpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.color(Lists.of(
                open,
                name,
                close),
            "[RED]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ColorSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("135138421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                name, name, name, name, name,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testColor2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(color());
    }

    // conditions.......................................................................................................

    @Test
    public void testConditionEquals() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken symbol = equalsSymbol();
        final SpreadsheetFormatParserToken number = conditionNumber();
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.equalsSpreadsheetFormatParserToken(Lists.of(
                open,
                symbol,
                number,
                close),
            "[=123]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final EqualsSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final EqualsSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final EqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213A421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                symbol, symbol, symbol, symbol, symbol,
                number, number, number, number, number,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionEquals2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionEquals());
    }

    @Test
    public void testConditionGreaterThan() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken symbol = greaterThan();
        final SpreadsheetFormatParserToken number = conditionNumber();
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.greaterThan(Lists.of(
                open,
                symbol,
                number,
                close),
            "[>123]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final GreaterThanSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final GreaterThanSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final GreaterThanSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213A421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                symbol, symbol, symbol, symbol, symbol,
                number, number, number, number, number,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionGreaterThan2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionGreaterThan());
    }

    @Test
    public void testConditionGreaterThanEquals() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken symbol = greaterThanEquals();
        final SpreadsheetFormatParserToken number = conditionNumber();
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.greaterThanEquals(Lists.of(
                open,
                symbol,
                number,
                close),
            "[>=123]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213A421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                symbol, symbol, symbol, symbol, symbol,
                number, number, number, number, number,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionGreaterThanEquals2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionGreaterThanEquals());
    }

    @Test
    public void testConditionLessThan() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken symbol = lessThan();
        final SpreadsheetFormatParserToken number = conditionNumber();
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.lessThan(Lists.of(
                open,
                symbol,
                number,
                close),
            "[>123]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final LessThanSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final LessThanSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final LessThanSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213A421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                symbol, symbol, symbol, symbol, symbol,
                number, number, number, number, number,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionLessThan2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionLessThan());
    }

    @Test
    public void testConditionLessThanEquals() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken symbol = lessThanEquals();
        final SpreadsheetFormatParserToken number = conditionNumber();
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.lessThanEquals(Lists.of(
                open,
                symbol,
                number,
                close),
            "[>=123]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final LessThanEqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213A421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                symbol, symbol, symbol, symbol, symbol,
                number, number, number, number, number,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionLessThanEquals2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionLessThanEquals());
    }

    @Test
    public void testConditionNotEquals() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken symbol = notEquals();
        final SpreadsheetFormatParserToken number = conditionNumber();
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.notEquals(Lists.of(
                open,
                symbol,
                number,
                close),
            "[=123]");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final NotEqualsSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final NotEqualsSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final NotEqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213A421394213742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                symbol, symbol, symbol, symbol, symbol,
                number, number, number, number, number,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionNotEquals2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionNotEquals());
    }

    @Test
    public void testDate() {
        final SpreadsheetFormatParserToken d = day();
        final SpreadsheetFormatParserToken m = month();
        final SpreadsheetFormatParserToken y = year();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.date(Lists.of(
                d,
                m,
                y),
            "dmy");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final DateSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final DaySpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final MonthSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final YearSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("135137421384213942642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                d, d, d, d, d,
                m, m, m, m, m,
                y, y, y, y, y,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDate2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.date(Lists.of(day()), "d"));
    }

    @Test
    public void testDateTime() {
        final SpreadsheetFormatParserToken d = day();
        final SpreadsheetFormatParserToken month = month();
        final SpreadsheetFormatParserToken y = year();
        final SpreadsheetFormatParserToken h = hour();
        final SpreadsheetFormatParserToken min = minute();
        final SpreadsheetFormatParserToken s = second();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.date(Lists.of(
                d,
                month,
                y,
                h,
                min,
                s),
            "dmyhms");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final DateSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final DaySpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final HourSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final MinuteSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final MonthSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }

            @Override
            protected void visit(final SecondSpreadsheetFormatParserToken token) {
                b.append("B");
                visited.add(token);
            }

            @Override
            protected void visit(final YearSpreadsheetFormatParserToken token) {
                b.append("C");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351374213A4213C42138421394213B42642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                d, d, d, d, d,
                month, month, month, month, month,
                y, y, y, y, y,
                h, h, h, h, h,
                min, min, min, min, min,
                s, s, s, s, s,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDateTime2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.dateTime(Lists.of(day()), "d"));
    }

    @Test
    public void testExponent() {
        final SpreadsheetFormatParserToken e = exponentSymbolPlus();
        final SpreadsheetFormatParserToken digit = digit();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.exponent(Lists.of(
                e,
                digit),
            "e+#");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final ExponentSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExponentSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final ExponentSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final DigitSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351374213842642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                e, e, e, e, e,
                digit, digit, digit, digit, digit,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testExponent2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.exponent(Lists.of(exponentSymbolPlus()),
            "e"));
    }

    @Test
    public void testExpression() {
        final SpreadsheetFormatParserToken open = bracketOpenSymbol();
        final SpreadsheetFormatParserToken name = colorName("RED");
        final SpreadsheetFormatParserToken close = bracketCloseSymbol();

        final ColorSpreadsheetFormatParserToken color = SpreadsheetFormatParserToken.color(Lists.of(
                open,
                name,
                close),
            "[RED]");

        final ExpressionSpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.expression(Lists.of(color),
            "[RED]");

        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();
        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final ExpressionSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ColorSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("A");
                visited.add(token);
            }

            @Override
            protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
                b.append("B");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13513713A4213B4213942842642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                color, color, color,
                open, open, open, open, open,
                name, name, name, name, name,
                close, close, close, close, close,
                color, color, color,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testExpression2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.expression(Lists.of(color()), "[RED]"));
    }

    @Test
    public void testFraction() {
        final SpreadsheetFormatParserToken top = digit();
        final SpreadsheetFormatParserToken symbol = fractionSymbol();
        final SpreadsheetFormatParserToken bottom = digit();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.fraction(Lists.of(
                top,
                symbol,
                bottom),
            "#/#");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final FractionSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final FractionSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final FractionSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final DigitSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("135138421374213842642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                top, top, top, top, top,
                symbol, symbol, symbol, symbol, symbol,
                bottom, bottom, bottom, bottom, bottom,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testFraction2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.fraction(Lists.of(
                digit(),
                fractionSymbol(),
                digit()),
            "#/#"));
    }

    @Test
    public void testGeneral() {
        final SpreadsheetFormatParserToken general = SpreadsheetFormatParserToken.generalSymbol("general", "general");

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.general(Lists.of(
                general),
            "general");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final GeneralSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final GeneralSymbolSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13513742642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                general, general, general, general, general,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testGeneral2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.general(Lists.of(generalSymbol()), "general"));
    }

    @Test
    public void testNumber() {
        final SpreadsheetFormatParserToken integer = digit();
        final SpreadsheetFormatParserToken decimalPoint = decimalPoint();
        final SpreadsheetFormatParserToken decimals = digitZero();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.number(Lists.of(
                integer,
                decimalPoint,
                decimals),
            "#/#");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final NumberSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final DigitSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("135138421374213942642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                integer, integer, integer, integer, integer,
                decimalPoint, decimalPoint, decimalPoint, decimalPoint, decimalPoint,
                decimals, decimals, decimals, decimals, decimals,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testNumber2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.number(Lists.of(digit()), "#"));
    }

    @Test
    public void testText() {
        final SpreadsheetFormatParserToken whitespace = textLiteral("   ");
        final SpreadsheetFormatParserToken placeholder = textPlaceholder();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.text(Lists.of(
                whitespace,
                placeholder),
            " @");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final TextSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("1351384213742642", b.toString(), "visited");
        this.checkEquals(
            Lists.of(
                token, token, token,
                whitespace, whitespace, whitespace, whitespace, whitespace,
                placeholder, placeholder, placeholder, placeholder, placeholder,
                token, token, token
            ),
            visited,
            "visitedTokens"
        );
    }

    @Test
    public void testText2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.text(Lists.of(textPlaceholder()), "@"));
    }

    @Test
    public void testTime() {
        final SpreadsheetFormatParserToken h = hour();
        final SpreadsheetFormatParserToken m = minute();
        final SpreadsheetFormatParserToken s = second();

        final SpreadsheetFormatParserToken token = SpreadsheetFormatParserToken.time(Lists.of(
                h,
                m,
                s),
            "hms");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final TimeSpreadsheetFormatParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void visit(final HourSpreadsheetFormatParserToken token) {
                b.append("7");
                visited.add(token);
            }

            @Override
            protected void visit(final MinuteSpreadsheetFormatParserToken token) {
                b.append("8");
                visited.add(token);
            }

            @Override
            protected void visit(final SecondSpreadsheetFormatParserToken token) {
                b.append("9");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("135137421384213942642", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token,
                h, h, h, h, h,
                m, m, m, m, m,
                s, s, s, s, s,
                token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testTime2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(SpreadsheetFormatParserToken.time(Lists.of(hour()), "h"));
    }

    // nonSymbol........................................................................................................

    @Test
    public void testAmpm() {
        final SpreadsheetFormatParserToken token = amSlashPm();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final AmPmSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testAmpm2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(amSlashPm());
    }

    @Test
    public void testColorName() {
        final SpreadsheetFormatParserToken token = colorName("RED");
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testColorName2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(colorName("RED"));
    }

    @Test
    public void testColorNumber() {
        final SpreadsheetFormatParserToken token = colorNumberFive();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final ColorNumberSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testColorNumber2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(colorNumberFive());
    }

    @Test
    public void testConditionNumber() {
        final SpreadsheetFormatParserToken token = conditionNumber();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testConditionNumber2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(conditionNumber());
    }

    @Test
    public void testCurrency() {
        final SpreadsheetFormatParserToken token = currency();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final CurrencySpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testCurrency2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(currency());
    }

    @Test
    public void testDay() {
        final SpreadsheetFormatParserToken token = day();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {
            @Override
            protected void visit(final DaySpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDay2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(day());
    }

    @Test
    public void testDecimalPoint() {
        final SpreadsheetFormatParserToken token = decimalPoint();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDecimalPoint2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(decimalPoint());
    }

    @Test
    public void testDigit() {
        final SpreadsheetFormatParserToken token = digit();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final DigitSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDigit2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(digit());
    }

    @Test
    public void testDigitSpace() {
        final SpreadsheetFormatParserToken token = digitSpace();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final DigitSpaceSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDigitSpace2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(digitSpace());
    }

    @Test
    public void testDigitZero() {
        final SpreadsheetFormatParserToken token = digitZero();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testDigitZero2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(digitZero());
    }

    @Test
    public void testEscaped() {
        final SpreadsheetFormatParserToken token = escape();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final EscapeSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testEscaped2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(escape());
    }

    @Test
    public void testGroupSeparator() {
        final SpreadsheetFormatParserToken token = groupSeparator();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final GroupSeparatorSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testGroupSeparator2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(groupSeparator());
    }

    @Test
    public void testHour() {
        final SpreadsheetFormatParserToken token = hour();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final HourSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testHour2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(hour());
    }

    @Test
    public void testMonth() {
        final SpreadsheetFormatParserToken token = month();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final MonthSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testMinute2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(minute());
    }

    @Test
    public void testMonth2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(month());
    }

    @Test
    public void testPercent() {
        final SpreadsheetFormatParserToken token = percentSymbol();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final PercentSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testPercent2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(percentSymbol());
    }

    @Test
    public void testQuotedText() {
        final SpreadsheetFormatParserToken token = quotedText();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final QuotedTextSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testQuotedText2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(quotedText());
    }

    @Test
    public void testSecond() {
        final SpreadsheetFormatParserToken token = second();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final SecondSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testSecond2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(second());
    }

    @Test
    public void testStar() {
        final SpreadsheetFormatParserToken token = star();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final StarSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testStar2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(star());
    }

    @Test
    public void testTextLiteral() {
        final SpreadsheetFormatParserToken token = textLiteral('!');
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {
            @Override
            protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testTextLiteral2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(textLiteral('!'));
    }

    @Test
    public void testTextPlaceholder() {
        final SpreadsheetFormatParserToken token = textPlaceholder();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testTextPlaceholder2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(textPlaceholder());
    }

    @Test
    public void testUnderscore() {
        final SpreadsheetFormatParserToken token = underscore();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final UnderscoreSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testUnderscore2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(underscore());
    }

    @Test
    public void testYear() {
        final SpreadsheetFormatParserToken token = year();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final YearSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testYear2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(year());
    }

    // symbol...........................................................................................................

    @Test
    public void testBracketCloseSymbol() {
        final SpreadsheetFormatParserToken token = bracketCloseSymbol();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testBracketCloseSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(bracketCloseSymbol());
    }

    @Test
    public void testBracketOpenSymbol() {
        final SpreadsheetFormatParserToken token = bracketOpenSymbol();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testBracketOpenSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(bracketOpenSymbol());
    }

    @Test
    public void testColorLiteralSymbol() {
        final SpreadsheetFormatParserToken token = colorLiteral();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final ColorLiteralSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testColorLiteralSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(colorLiteral());
    }

    @Test
    public void testEqualsSymbol() {
        final SpreadsheetFormatParserToken token = equalsSymbol();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final EqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testEqualsSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(equalsSymbol());
    }

    @Test
    public void testExponentSymbol() {
        final SpreadsheetFormatParserToken token = exponentSymbolPlus();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final ExponentSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testExponentSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(exponentSymbolPlus());
    }

    @Test
    public void testFractionSymbol() {
        final SpreadsheetFormatParserToken token = fractionSymbol();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final FractionSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testFractionSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(fractionSymbol());
    }

    @Test
    public void testGreaterThanSymbol() {
        final SpreadsheetFormatParserToken token = greaterThan();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final GreaterThanSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testGreaterThanSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(greaterThan());
    }

    @Test
    public void testGreaterThanEqualsSymbol() {
        final SpreadsheetFormatParserToken token = greaterThanEquals();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testGreaterThanEqualsSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(greaterThanEquals());
    }

    @Test
    public void testLessThanSymbol() {
        final SpreadsheetFormatParserToken token = lessThan();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final LessThanSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testLessThanSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(lessThan());
    }

    @Test
    public void testLessThanEqualsSymbol() {
        final SpreadsheetFormatParserToken token = lessThanEquals();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final LessThanEqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testLessThanEqualsSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(lessThanEquals());
    }

    @Test
    public void testNotEqualsSymbol() {
        final SpreadsheetFormatParserToken token = notEquals();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final NotEqualsSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testNotEqualsSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(notEquals());
    }

    @Test
    public void testSeparatorSymbol() {
        final SpreadsheetFormatParserToken token = separator();
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatParserTokenVisitor visitor = new TestSpreadsheetFormatParserTokenVisitor(b, visited) {

            @Override
            protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
                b.append("5");
                visited.add(token);
            }
        };
        visitor.accept(token);
        this.checkEquals("13542", b.toString(), "visited");
        this.checkEquals(Lists.of(token, token, token, token, token),
            visited,
            "visitedTokens");
    }

    @Test
    public void testSeparatorSymbol2() {
        new SpreadsheetFormatParserTokenVisitor() {
        }.accept(separator());
    }

    abstract static class TestSpreadsheetFormatParserTokenVisitor extends FakeSpreadsheetFormatParserTokenVisitor {

        TestSpreadsheetFormatParserTokenVisitor(final StringBuilder b,
                                                final List<ParserToken> visited) {
            super();
            this.b = b;
            this.visited = visited;
        }

        @Override
        protected Visiting startVisit(final ParserToken token) {
            b.append("1");
            visited.add(token);
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final ParserToken token) {
            b.append("2");
            visited.add(token);
        }

        @Override
        protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
            b.append("3");
            visited.add(token);
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final SpreadsheetFormatParserToken token) {
            b.append("4");
            visited.add(token);
        }

        final StringBuilder b;
        final List<ParserToken> visited;
    }

    // ToString.........................................................................................................

    @Override
    public void testCheckToStringOverridden() {
    }

    // helpers..........................................................................................................

    @Override
    public SpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetFormatParserTokenVisitor() {
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetFormatParserTokenVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }
}
