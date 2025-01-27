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

package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;

public final class SpreadsheetParsersValueSeparatorParserTest extends SpreadsheetParserTestCase<SpreadsheetParsersValueSeparatorParser,
        ValueSeparatorSymbolSpreadsheetParserToken>
        implements ToStringTesting<SpreadsheetParsersValueSeparatorParser> {

    @Test
    public void testIncorrectCharacterFails() {
        this.checkNotEquals("@", VALUE_SEPARATOR, "valueSeparator");

        this.parseFailAndCheck("@");
    }

    @Test
    public void testMatch() {
        final String text = VALUE_SEPARATOR + "";

        this.parseAndCheck(
                text,
                SpreadsheetParserToken.valueSeparatorSymbol(text, text),
                text
        );
    }

    @Test
    public void testMatch2() {
        final String text = VALUE_SEPARATOR + "";
        final String after = "@";

        this.parseAndCheck(
                text + after,
                SpreadsheetParserToken.valueSeparatorSymbol(text, text),
                text,
                after
        );
    }

    // ensure only consumes a single character
    @Test
    public void testMatch3() {
        final String text = VALUE_SEPARATOR + "";

        this.parseAndCheck(
                text + text,
                SpreadsheetParserToken.valueSeparatorSymbol(text, text),
                text,
                text
        );
    }

    @Test
    public void testEqualsDifferentValueSeparator() {
        final char c = ';';
        this.checkNotEquals(c, VALUE_SEPARATOR, "valueSeparator");
        final String text = c + "";

        this.parseAndCheck(
                SpreadsheetParsersValueSeparatorParser.INSTANCE,
                new FakeSpreadsheetParserContext() {
                    @Override
                    public char valueSeparator() {
                        return c;
                    }
                },
                text,
                SpreadsheetParserToken.valueSeparatorSymbol(text, text),
                text
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser().toString(), ",");
    }

    @Override
    public SpreadsheetParsersValueSeparatorParser createParser() {
        return SpreadsheetParsersValueSeparatorParser.INSTANCE;
    }

    @Override
    public Class<SpreadsheetParsersValueSeparatorParser> type() {
        return SpreadsheetParsersValueSeparatorParser.class;
    }
}
