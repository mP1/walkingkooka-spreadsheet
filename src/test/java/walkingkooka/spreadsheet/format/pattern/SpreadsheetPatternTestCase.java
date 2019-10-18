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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.type.JavaVisibility;

import java.util.function.Predicate;

walkingkooka.reflect.*;
        walkingkooka.reflect.*;
        walkingkooka.reflect.*;
        walkingkooka.reflect.*;
        walkingkooka.reflect.*;

public abstract class SpreadsheetPatternTestCase<P extends SpreadsheetPattern<V>, V>
        implements ClassTesting2<P>,
        HashCodeEqualsDefinedTesting2<P>,
        JsonNodeMarshallingTesting<P>,
        IsMethodTesting<P>,
        ParserTesting,
        ParseStringTesting<P>,
        ToStringTesting<P> {

    SpreadsheetPatternTestCase() {
        super();
    }

    final SpreadsheetFormatParserToken ampm() {
        return SpreadsheetFormatParserToken.amPm("A/P", "A/P");
    }

    final SpreadsheetFormatParserToken bracketClose() {
        return SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]");
    }

    final SpreadsheetFormatParserToken bracketOpen() {
        return SpreadsheetFormatParserToken.bracketOpenSymbol("[", "[");
    }

    final SpreadsheetFormatParserToken color() {
        return SpreadsheetFormatParserToken.color(Lists.of(colorName()), "[RED]");
    }

    final SpreadsheetFormatParserToken colorName() {
        return SpreadsheetFormatParserToken.colorName("RED", "RED");
    }

    final SpreadsheetFormatParserToken currency() {
        return SpreadsheetFormatParserToken.currency("%", "%");
    }

    final SpreadsheetFormatParserToken date() {
        return SpreadsheetFormatParserToken.date(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken dateTime() {
        return SpreadsheetFormatParserToken.dateTime(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken day() {
        return SpreadsheetFormatParserToken.day("d", "d");
    }

    final SpreadsheetFormatParserToken decimalPoint() {
        return SpreadsheetFormatParserToken.decimalPoint(".", ".");
    }

    final SpreadsheetFormatParserToken digit() {
        return SpreadsheetFormatParserToken.digit("#", "#");
    }

    final SpreadsheetFormatParserToken digitSpace() {
        return SpreadsheetFormatParserToken.digitSpace("?", "?");
    }

    final SpreadsheetFormatParserToken digitZero() {
        return SpreadsheetFormatParserToken.digitZero("0", "0");
    }

    final SpreadsheetFormatParserToken equalsSymbol() {
        return SpreadsheetFormatParserToken.equalsSymbol("=", "=");
    }

    final SpreadsheetFormatParserToken exponentSymbol() {
        return SpreadsheetFormatParserToken.exponentSymbol("^", "^");
    }

    final SpreadsheetFormatParserToken fractionSymbol() {
        return SpreadsheetFormatParserToken.fractionSymbol("/", "/");
    }

    final SpreadsheetFormatParserToken greaterThanSymbol() {
        return SpreadsheetFormatParserToken.greaterThanSymbol("<", "<");
    }

    final SpreadsheetFormatParserToken greaterThanEqualsSymbol() {
        return SpreadsheetFormatParserToken.greaterThanEqualsSymbol("<=", "<=");
    }
    
    final SpreadsheetFormatParserToken hour() {
        return SpreadsheetFormatParserToken.hour("h", "h");
    }

    final SpreadsheetFormatParserToken lessThanSymbol() {
        return SpreadsheetFormatParserToken.lessThanSymbol("<", "<");
    }

    final SpreadsheetFormatParserToken lessThanEqualsSymbol() {
        return SpreadsheetFormatParserToken.lessThanEqualsSymbol("<=", "<=");
    }

    final SpreadsheetFormatParserToken monthOrMinute() {
        return SpreadsheetFormatParserToken.monthOrMinute("m", "m");
    }

    final SpreadsheetFormatParserToken notEqualsSymbol() {
        return SpreadsheetFormatParserToken.notEqualsSymbol("<=", "<=");
    }

    final SpreadsheetFormatParserToken number() {
        return SpreadsheetFormatParserToken.number(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken percentSymbol() {
        return SpreadsheetFormatParserToken.percent("%", "%");
    }

    final SpreadsheetFormatParserToken second() {
        return SpreadsheetFormatParserToken.second("s", "s");
    }

    final SpreadsheetFormatParserToken thousands() {
        return SpreadsheetFormatParserToken.thousands(",", ",");
    }

    final SpreadsheetFormatParserToken time() {
        return SpreadsheetFormatParserToken.time(Lists.of(color()), "[RED]");
    }

    final SpreadsheetFormatParserToken underscore() {
        return SpreadsheetFormatParserToken.underscore('_', "_");
    }

    final SpreadsheetFormatParserToken whitespace() {
        return SpreadsheetFormatParserToken.whitespace(" ", " ");
    }

    final SpreadsheetFormatParserToken year() {
        return SpreadsheetFormatParserToken.year("y", "y");
    }

    // Parse............................................................................................................

    @Test
    public final void testParseStringIllegalPatternFails() {
        this.parseStringFails("\"unclosed quoted text inside patterns", IllegalArgumentException.class);
    }

    @Test
    public final void testParseStringHangingSeparatorFails() {
        this.parseStringFails(this.patternText() + ";", IllegalArgumentException.class);
    }

    // HashCodeEqualsDefined............................................................................................

    @Test
    public final void testDifferentPattern() {
        this.checkNotEquals(this.createPattern("\"different-text-literal\""));
    }

    // JsonNodeTesting.................................................................................................

    @Test
    public final void testJsonNodeUnmarshallInvalidPattern() {
        this.unmarshallFails(JsonNode.string("\"unclosed quoted text inside patterns"), IllegalArgumentException.class);
    }

    // ToString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createPattern(), this.patternText());
    }

    // helpers..........................................................................................................

    final P createPattern() {
        return this.createPattern(this.patternText());
    }

    abstract P createPattern(final String pattern);

    abstract String patternText();

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualityDefinedTesting...................................................................................

    @Override
    public final P createObject() {
        return this.createPattern();
    }

    // JsonNodeMarshallingTesting................................................................................................

    @Override
    public final P createJsonNodeMappingValue() {
        return this.createPattern();
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final P createIsMethodObject() {
        return this.createPattern();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.never();
    }

    // ParseStringTesting...............................................................................................

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
