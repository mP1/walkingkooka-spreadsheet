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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetFormatPatternTestCase<P extends SpreadsheetFormatPattern,
        T extends SpreadsheetFormatParentParserToken> extends SpreadsheetPatternTestCase<P, T> {

    final static Color RED = Color.parse("#FF0000");

    SpreadsheetFormatPatternTestCase() {
        super();
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> createPattern((T) null));
    }

    @Test
    public final void testWith() {
        final ParserToken token = this.parseFormatParserToken(
                this.patternText()
        );

        final P patterns = this.createPattern(token);
        this.checkEquals(patterns.value(), token, "value");
    }

    @Test
    public final void testWithEscape() {
        final T tokens = this.createFormatParserToken(Lists.of(SpreadsheetFormatParserToken.escape('\t', "\\t")));
        final P patterns = this.createPattern(tokens);
        this.checkEquals(patterns.value(), tokens, "value");
    }

    final void withInvalidCharacterFails(final ParserToken token) {
        final String patternText = this.patternText();

        final List<ParserToken> tokens = Lists.array();
        tokens.add(this.parseFormatParserToken(patternText));

        final String patternText2 = patternText + token.text();
        tokens.add(token);

        final T parent = this.createFormatParserToken(tokens, patternText2);

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> this.createPattern(parent)
        );
        this.checkEquals(
                patternText.length(),
                thrown.position(),
                () -> "position pattern=" + patternText2
        );
    }

    @Test
    public final void testWithWhitespace() {
        final T token = this.createFormatParserToken(Lists.of(whitespace()));
        final P patterns = this.createPattern(token);
        this.checkEquals(patterns.value(), token, "value");
    }

    // toFormat.........................................................................................................

    @Test
    public final void testToFormat() {
        final P pattern = this.createPattern();
        assertSame(pattern, pattern.toFormat());
    }

    // helpers..........................................................................................................

    final P createPattern(final String pattern) {
        return this.createPattern(
                this.parseFormatParserToken(pattern)
        );
    }

    abstract P createPattern(final ParserToken token);

    abstract ParserToken parseFormatParserToken(final String text);

    private T createFormatParserToken(final List<ParserToken> tokens) {
        return this.createFormatParserToken(tokens, ParserToken.text(tokens));
    }

    abstract T createFormatParserToken(final List<ParserToken> tokens, final String text);

    // Parse............................................................................................................

    @Test
    public final void testParseString() {
        final String patternText = this.patternText();

        this.parseStringAndCheck(patternText,
                this.createPattern(this.parseFormatParserToken(patternText)));
    }

    // removeColor..................................................................................................

    @Test
    public final void testRemoveColorNameWithout() {
        this.removeColorAndCheck(
                this.createPattern()
        );
    }

    @Test
    public final void testRemoveColorName() {
        this.removeColorAndCheck(
                this.createPattern("[color 1]\"Text-literal\""),
                this.createPattern("\"Text-literal\"")
        );
    }

    @Test
    public final void testRemoveColorName2() {
        this.removeColorAndCheck(
                this.createPattern("\"Text-literal\"[color 2]"),
                this.createPattern("\"Text-literal\"")
        );
    }

    @Test
    public final void testRemoveColorNameWithColorName() {
        this.removeColorAndCheck(
                this.createPattern("[Red]\"Text-literal\""),
                this.createPattern("\"Text-literal\"")
        );
    }

    // setColor.........................................................................................................

    @Test
    public final void testSetColorNameMultiplePatternsFails() {
        if (false == this instanceof SpreadsheetTextFormatPatternTest) {
            final IllegalStateException thrown = assertThrows(
                    IllegalStateException.class,
                    () -> this.createPattern("\"Hello\";\"Hello2\"")
                            .setColorName(SpreadsheetColorName.BLACK)
            );

            this.checkEquals(
                    "Cannot color name for multiple patterns=\"Hello\\\";\\\"Hello2\"",
                    thrown.getMessage()
            );
        }
    }

    final void setColorNameAndCheck(final P pattern,
                                    final SpreadsheetColorName name,
                                    final String expected) {
        this.checkEquals(
                this.createPattern(expected),
                pattern.setColorName(name),
                () -> pattern + " set color name " + name
        );
    }

    @Test
    public final void testSetColorNumberMultiplePatternsFails() {
        if (false == this instanceof SpreadsheetTextFormatPatternTest) {
            final IllegalStateException thrown = assertThrows(
                    IllegalStateException.class,
                    () -> this.createPattern("\"Hello\";\"Hello2\"")
                            .setColorNumber(SpreadsheetColors.MIN)
            );

            this.checkEquals(
                    "Cannot color number for multiple patterns=\"Hello\\\";\\\"Hello2\"",
                    thrown.getMessage()
            );
        }
    }

    final void setColorNumberAndCheck(final P pattern,
                                      final int number,
                                      final String expected) {
        this.checkEquals(
                this.createPattern(expected),
                pattern.setColorNumber(number),
                () -> pattern + " set color number " + number
        );
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final String isMethodTypeNameSuffix() {
        return "FormatPattern";
    }
}
