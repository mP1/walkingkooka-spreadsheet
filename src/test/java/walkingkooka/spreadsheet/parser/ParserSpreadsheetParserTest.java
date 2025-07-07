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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.Parsers;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ParserSpreadsheetParserTest implements SpreadsheetParserTesting2<ParserSpreadsheetParser>,
    HashCodeEqualsDefinedTesting2<ParserSpreadsheetParser>,
    ToStringTesting<ParserSpreadsheetParser> {

    private final static String TOKEN = "Hello123";

    private final static Parser<SpreadsheetParserContext> PARSER = Parsers.string(
        TOKEN,
        CaseSensitivity.SENSITIVE
    );

    @Test
    public void testWithNullParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> ParserSpreadsheetParser.with(
                null,
                SpreadsheetParser.NO_VALUE_TYPE
            )
        );
    }

    @Test
    public void testWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> ParserSpreadsheetParser.with(
                Parsers.fake(),
                null
            )
        );
    }

    @Test
    public void testWithSpreadsheetParser() {
        final SpreadsheetParser wrapped = SpreadsheetParsers.fake();
        assertSame(
            wrapped,
            ParserSpreadsheetParser.with(
                wrapped,
                SpreadsheetParser.NO_VALUE_TYPE
            )
        );
    }

    // parser...........................................................................................................

    @Test
    public void testParse() {
        this.parseAndCheck(
            this.createParser(),
            TOKEN,
            ParserTokens.string(
                TOKEN,
                TOKEN
            ),
            TOKEN
        );
    }

    @Test
    public void testMinCount() {
        this.minCountAndCheck(
            1
        );
    }

    @Test
    public void testMaxCount() {
        this.maxCountAndCheck(
            1
        );
    }

    @Override
    public ParserSpreadsheetParser createParser() {
        return (ParserSpreadsheetParser) ParserSpreadsheetParser.with(
            PARSER,
            SpreadsheetParser.NO_VALUE_TYPE
        );
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.fake();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
            ParserSpreadsheetParser.with(
                Parsers.fake(),
                SpreadsheetParser.NO_VALUE_TYPE
            ),
            ParserSpreadsheetParser.with(
                Parsers.fake(),
                SpreadsheetParser.NO_VALUE_TYPE
            )
        );
    }

    @Override
    public ParserSpreadsheetParser createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            ParserSpreadsheetParser.with(
                PARSER,
                SpreadsheetParser.NO_VALUE_TYPE
            ),
            PARSER.toString()
        );
    }

    // type.............................................................................................................

    @Override
    public Class<ParserSpreadsheetParser> type() {
        return ParserSpreadsheetParser.class;
    }
}
