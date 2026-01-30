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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.math.MathTesting;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetPatternTestCase<P extends SpreadsheetPattern>
    implements ClassTesting2<P>,
    SpreadsheetPatternSpreadsheetFormatterTesting,
    HashCodeEqualsDefinedTesting2<P>,
    JsonNodeMarshallingTesting<P>,
    IsMethodTesting<P>,
    MathTesting,
    ParserTesting,
    ParseStringTesting<P>,
    TreePrintableTesting,
    ToStringTesting<P> {

    SpreadsheetPatternTestCase() {
        super();
    }

    // patterns.........................................................................................................

    @Test
    public final void testPatternsCached() {
        final P pattern = this.createPattern();
        final List<?> patterns = pattern.patterns();

        assertSame(
            patterns,
            pattern.patterns(),
            "patterns not cached"
        );
    }

    final void patternsAndCheck(final P pattern,
                                final String... patterns) {
        this.patternsAndCheck2(
            pattern,
            Arrays.stream(patterns)
                .map(this::createPattern)
                .collect(Collectors.toList())
        );
    }

    final void patternsAndCheck2(final P pattern,
                                 final P... patterns) {
        this.patternsAndCheck2(
            pattern,
            Lists.of(patterns)
        );
    }

    final void patternsAndCheck2(final P pattern,
                                 final List<P> patterns) {
        this.checkEquals(
            patterns,
            pattern.patterns(),
            () -> pattern + " patterns"
        );
    }

    // removeColor......................................................................................................

    final void removeColorAndCheck(final P pattern) {
        this.removeColorAndCheck(
            pattern,
            pattern
        );
    }

    final void removeColorAndCheck(final P pattern,
                                   final P expected) {
        this.checkEquals(
            expected,
            pattern.removeColor(),
            () -> pattern + " removeColor"
        );
    }

    // setColor.........................................................................................................

    @Test
    public final void testSetColorNameWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createPattern()
                .setColorName(null)
        );
    }

    @Test
    public final void testSetColorNumberWithLessThanMinFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createPattern()
                .setColorNumber(SpreadsheetColors.MIN - 1)
        );
    }

    @Test
    public final void testSetColorNumberWithGreaterThanMaxFails2() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createPattern()
                .setColorNumber(SpreadsheetColors.MAX + 1)
        );
    }

    // removeCondition......................................................................................................

    final void removeConditionAndCheck(final P pattern) {
        this.removeConditionAndCheck(
            pattern,
            pattern
        );
    }

    final void removeConditionAndCheck(final P pattern,
                                       final P expected) {
        this.checkEquals(
            expected,
            pattern.removeCondition(),
            () -> pattern + " removeCondition"
        );
    }

    // token helpers....................................................................................................

    final SpreadsheetFormatParserToken ampm() {
        return SpreadsheetFormatParserToken.amPm(
            "A/P",
            "A/P"
        );
    }

    final SpreadsheetFormatParserToken bracketClose() {
        return SpreadsheetFormatParserToken.bracketCloseSymbol(
            "]",
            "]"
        );
    }

    final SpreadsheetFormatParserToken bracketOpen() {
        return SpreadsheetFormatParserToken.bracketOpenSymbol(
            "[",
            "["
        );
    }

    final SpreadsheetFormatParserToken color() {
        return SpreadsheetFormatParserToken.color(
            Lists.of(
                colorName()
            ),
            "[RED]"
        );
    }

    final SpreadsheetFormatParserToken colorName() {
        return SpreadsheetFormatParserToken.colorName(
            "RED",
            "RED"
        );
    }

    final SpreadsheetFormatParserToken currency() {
        return SpreadsheetFormatParserToken.currency(
            "%",
            "%"
        );
    }

    final SpreadsheetFormatParserToken date() {
        return SpreadsheetFormatParserToken.date(
            Lists.of(
                color()
            ),
            "[RED]"
        );
    }

    final SpreadsheetFormatParserToken dateTime() {
        return SpreadsheetFormatParserToken.dateTime(
            Lists.of(
                color()
            ),
            "[RED]"
        );
    }

    final SpreadsheetFormatParserToken day() {
        return SpreadsheetFormatParserToken.day(
            "d",
            "d"
        );
    }

    final SpreadsheetFormatParserToken decimalPoint() {
        return SpreadsheetFormatParserToken.decimalPoint(
            ".",
            "."
        );
    }

    final SpreadsheetFormatParserToken digit() {
        return SpreadsheetFormatParserToken.digit(
            "#",
            "#"
        );
    }

    final SpreadsheetFormatParserToken digitSpace() {
        return SpreadsheetFormatParserToken.digitSpace(
            "?",
            "?"
        );
    }

    final SpreadsheetFormatParserToken digitZero() {
        return SpreadsheetFormatParserToken.digitZero(
            "0",
            "0"
        );
    }

    final SpreadsheetFormatParserToken equalsSymbol() {
        return SpreadsheetFormatParserToken.equalsSymbol(
            "=",
            "="
        );
    }

    final SpreadsheetFormatParserToken exponentSymbol() {
        return SpreadsheetFormatParserToken.exponentSymbol(
            "^",
            "^"
        );
    }

    final SpreadsheetFormatParserToken fractionSymbol() {
        return SpreadsheetFormatParserToken.fractionSymbol(
            "/",
            "/"
        );
    }

    final SpreadsheetFormatParserToken greaterThanSymbol() {
        return SpreadsheetFormatParserToken.greaterThanSymbol(
            "<",
            "<"
        );
    }

    final SpreadsheetFormatParserToken greaterThanEqualsSymbol() {
        return SpreadsheetFormatParserToken.greaterThanEqualsSymbol(
            "<=",
            "<="
        );
    }

    final SpreadsheetFormatParserToken hour() {
        return SpreadsheetFormatParserToken.hour(
            "h",
            "h"
        );
    }

    final SpreadsheetFormatParserToken lessThanSymbol() {
        return SpreadsheetFormatParserToken.lessThanSymbol(
            "<",
            "<"
        );
    }

    final SpreadsheetFormatParserToken lessThanEqualsSymbol() {
        return SpreadsheetFormatParserToken.lessThanEqualsSymbol(
            "<=",
            "<="
        );
    }

    final SpreadsheetFormatParserToken minute() {
        return SpreadsheetFormatParserToken.minute(
            "m",
            "m"
        );
    }

    final SpreadsheetFormatParserToken month() {
        return SpreadsheetFormatParserToken.month(
            "m",
            "m"
        );
    }

    final SpreadsheetFormatParserToken notEqualsSymbol() {
        return SpreadsheetFormatParserToken.notEqualsSymbol(
            "<=",
            "<="
        );
    }

    final SpreadsheetFormatParserToken number() {
        return SpreadsheetFormatParserToken.number(
            Lists.of(
                color()
            ),
            "[RED]"
        );
    }

    final SpreadsheetFormatParserToken percentSymbol() {
        return SpreadsheetFormatParserToken.percent(
            "%",
            "%"
        );
    }

    final SpreadsheetFormatParserToken second() {
        return SpreadsheetFormatParserToken.second(
            "s",
            "s"
        );
    }

    final SpreadsheetFormatParserToken separator() {
        return SpreadsheetFormatParserToken.separatorSymbol(
            SpreadsheetPattern.SEPARATOR.string(),
            SpreadsheetPattern.SEPARATOR.string()
        );
    }

    final SpreadsheetFormatParserToken textLiteral() {
        return SpreadsheetFormatParserToken.textLiteral(
            "@",
            "@"
        );
    }

    final SpreadsheetFormatParserToken groupSeparator() {
        return SpreadsheetFormatParserToken.groupSeparator(
            ",",
            ","
        );
    }

    final SpreadsheetFormatParserToken time() {
        return SpreadsheetFormatParserToken.time(
            Lists.of(
                color()
            ),
            "[RED]"
        );
    }

    final SpreadsheetFormatParserToken underscore() {
        return SpreadsheetFormatParserToken.underscore(
            '_',
            "_"
        );
    }

    final SpreadsheetFormatParserToken whitespace() {
        return SpreadsheetFormatParserToken.whitespace(
            " ",
            " "
        );
    }

    final SpreadsheetFormatParserToken year() {
        return SpreadsheetFormatParserToken.year(
            "y",
            "y"
        );
    }

    // helpers..........................................................................................................

    final P createPattern() {
        return this.createPattern(
            this.patternText()
        );
    }

    abstract P createPattern(final String pattern);

    abstract String patternText();

    // format...........................................................................................................

    final void formatAndCheck2(final String pattern,
                               final Object value,
                               final String expected) {
        this.formatAndCheck2(
            pattern,
            value,
            SpreadsheetText.with(expected)
        );
    }

    final void formatAndCheck2(final String pattern,
                               final Object value,
                               final SpreadsheetText expected) {
        this.formatAndCheck(
            this.createPattern(pattern)
                .formatter(),
            value,
            this.createContext(),
            expected
        );
    }

    final void formatAndCheck2(final String pattern,
                               final Object value,
                               final char zeroDigit,
                               final String expected) {
        this.formatAndCheck2(
            pattern,
            value,
            zeroDigit,
            SpreadsheetText.EMPTY.setText(expected)
        );
    }

    final void formatAndCheck2(final String pattern,
                               final Object value,
                               final char zeroDigit,
                               final SpreadsheetText expected) {
        this.formatAndCheck(
            this.createPattern(pattern)
                .formatter(),
            value,
            this.createContext(zeroDigit),
            expected
        );
    }

    final SpreadsheetFormatterContext createContext() {
        return this.createContext('0');
    }

    abstract SpreadsheetFormatterContext createContext(final char zeroDigit);

    // hashCode/equals..................................................................................................

    @Test
    public final void testEqualsDifferentPattern() {
        this.checkNotEquals(
            this.createPattern("\"different-text-literal\"")
        );
    }

    @Override
    public final P createObject() {
        return this.createPattern();
    }

    // json.............................................................................................................

    @Test
    public final void testUnmarshallInvalidPattern() {
        this.unmarshallFails(JsonNode.string("\"unclosed quoted text inside patterns"));
    }

    @Override
    public final P createJsonNodeMarshallingValue() {
        return this.createPattern();
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final P createIsMethodObject() {
        return this.createPattern();
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

    // ToString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(
            this.createPattern(),
            CharSequences.quoteIfChars(this.patternText()).toString()
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
