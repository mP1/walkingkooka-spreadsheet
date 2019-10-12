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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetFormatPatternTestCase<P extends SpreadsheetFormatPattern<T>,
        T extends SpreadsheetFormatParserToken & ParentParserToken> extends SpreadsheetPatternTestCase<P, T>
        implements SpreadsheetFormatterTesting {

    SpreadsheetFormatPatternTestCase() {
        super();
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> {
            createPattern((T)null);
        });
    }

    @Test
    public final void testWith() {
        final T token = this.parseFormatParserToken(this.patternText());

        final P patterns = this.createPattern(token);
        assertEquals(patterns.value(), token, "value");
    }

    @Test
    public final void testWithEscape() {
        final T tokens = this.createFormatParserToken(Lists.of(SpreadsheetFormatParserToken.escape('\t', "\\t")));
        final P patterns = this.createPattern(tokens);
        assertEquals(patterns.value(), tokens, "value");
    }

    final void withInvalidCharacterFails(final ParserToken token) {
        final String patternText = this.patternText();

        final List<ParserToken> tokens = Lists.array();
        tokens.addAll(this.parseFormatParserToken(patternText).value());

        final String patternText2 = patternText + token.text();
        tokens.add(token);

        final T parent = this.createFormatParserToken(tokens, patternText2);

        final InvalidCharacterException thrown = assertThrows(InvalidCharacterException.class, () -> {
            this.createPattern(parent);
        });
        assertEquals(patternText.length(), thrown.position(), () -> "position pattern=" + patternText2);
    }

    @Test
    public final void testWithWhitespace() {
        final T token = this.createFormatParserToken(Lists.of(whitespace()));
        final P patterns = this.createPattern(token);
        assertEquals(patterns.value(), token, "value");
    }

    // helpers..........................................................................................................

    final P createPattern(final String pattern) {
        return this.createPattern(this.parseFormatParserToken(pattern));
    }

    abstract P createPattern(final T token);

    abstract T parseFormatParserToken(final String text);

    private T createFormatParserToken(final List<ParserToken> tokens) {
        return this.createFormatParserToken(tokens, ParserToken.text(tokens));
    }

    abstract T createFormatParserToken(final List<ParserToken> tokens, final String text);

    // Parse............................................................................................................

    @Test
    public final void testParseStringGeneralFails() {
        this.parseStringFails("General", IllegalArgumentException.class);
    }

    @Test
    public final void testParseString() {
        final String patternText = this.patternText();

        this.parseStringAndCheck(patternText,
                this.createPattern(this.parseFormatParserToken(patternText)));
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final String isMethodTypeNameSuffix() {
        return "FormatPattern";
    }
}
