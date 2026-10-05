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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserToStringTest implements SpreadsheetParserTesting2<SpreadsheetParserToString>,
    HashCodeEqualsDefinedTesting2<SpreadsheetParserToString>,
    ToStringTesting<SpreadsheetParserToString>,
    ClassTesting2<SpreadsheetParserToString> {

    private final static List<SpreadsheetParserSelectorToken> TOKENS = Lists.of(
        SpreadsheetParserSelectorToken.with(
            "Label1",
            "Text1",
            SpreadsheetParserSelectorToken.NO_ALTERNATIVES
        )
    );

    private final static SpreadsheetParser PARSER = new FakeSpreadsheetParser() {
        @Override
        public int minCount() {
            return 1;
        }

        @Override
        public int maxCount() {
            return 2;
        }

        @Override
        public List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context) {
            Objects.requireNonNull(context, "context");
            return TOKENS;
        }

        @Override
        public String toString() {
            return "TestSpreadsheetParser";
        }
    };

    private final static String TO_STRING = "TestToString";

    // with.............................................................................................................

    @Test
    public void testWithNullParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserToString.with(
                null,
                TO_STRING
            )
        );
    }

    @Test
    public void testWithNullToStringFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserToString.with(
                PARSER,
                null
            )
        );
    }

    @Test
    public void testWithToStringSameToString() {
        assertSame(
            PARSER,
            SpreadsheetParserToString.with(
                PARSER,
                PARSER.toString()
            )
        );
    }

    @Test
    public void testWithToStringSameToString2() {
        final SpreadsheetParserToString parser = this.createParser();
        assertSame(
            parser,
            SpreadsheetParserToString.with(
                parser,
                parser.toString()
            )
        );
    }

    @Test
    public void testWithToStringSpreadsheetParserUnwraps() {
        final SpreadsheetParserToString parser = this.createParser();

        assertSame(
            parser,
            SpreadsheetParserToString.with(
                parser,
                PARSER.toString()
            )
        );
    }

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createParser(),
            this.createContext(),
            TOKENS
        );
    }

    @Override
    public SpreadsheetParserToString createParser() {
        return (SpreadsheetParserToString)
            SpreadsheetParserToString.with(
                PARSER,
                TO_STRING
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
            SpreadsheetParserToString.with(
                SpreadsheetParsers.fake(),
                TO_STRING
            )
        );
    }

    @Test
    public void testEqualsDifferentToString() {
        this.checkNotEquals(
            SpreadsheetParserToString.with(
                PARSER,
                "DifferentToString"
            )
        );
    }

    @Override
    public SpreadsheetParserToString createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createParser(),
            TO_STRING
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            this.createParser(),
            "SpreadsheetParserToString\n" +
                "  TestSpreadsheetParser (walkingkooka.spreadsheet.parser.SpreadsheetParserToStringTest$1)\n"
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetParserToString> type() {
        return SpreadsheetParserToString.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
