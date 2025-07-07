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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.FakeParser;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Optional;

public final class SpreadsheetFormatParsersFormatColorParserTest implements ParserTesting2<SpreadsheetFormatParsersFormatColorParser, SpreadsheetFormatParserContext>,
    ToStringTesting<SpreadsheetFormatParsersFormatColorParser> {

    @Test
    public void testParseNotColor() {
        final String text = "123";
        final ParserToken expected = ParserTokens.sequence(
            Lists.of(
                SpreadsheetFormatParserToken.number(
                    Lists.of(
                        SpreadsheetFormatParserToken.digit(text, text)
                    ),
                    text
                )
            ),
            text
        );
        this.parseAndCheck(
            SpreadsheetFormatParsersFormatColorParser.with(
                new FakeParser<>() {
                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final SpreadsheetFormatParserContext context) {
                        Parsers.string(text, CaseSensitivity.SENSITIVE)
                            .orFailIfCursorNotEmpty(ParserReporters.basic())
                            .parse(cursor, context);

                        return Optional.of(
                            expected
                        );
                    }
                }
            ),
            this.createContext(),
            text,
            expected,
            text
        );
    }

    @Test
    public void testParseNotColor2() {
        final String text = "123";
        final ParserToken expected = ParserTokens.sequence(
            Lists.of(
                SpreadsheetFormatParserToken.number(
                    Lists.of(
                        SpreadsheetFormatParserToken.digit(text, text)
                    ),
                    text
                )
            ),
            text
        );
        this.parseAndCheck(
            SpreadsheetFormatParsersFormatColorParser.with(
                new FakeParser<>() {
                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final SpreadsheetFormatParserContext context) {
                        checkNotEquals(
                            Optional.empty(),
                            Parsers.string(text, CaseSensitivity.SENSITIVE)
                                .parse(cursor, context)
                        );

                        return Optional.of(
                            expected
                        );
                    }
                }
            ),
            this.createContext(),
            text + "!",
            expected,
            text,
            "!"
        );
    }

    @Test
    public void testParseColorEmpty() {
        final String text = "[Black]";

        this.parseFailAndCheck(
            SpreadsheetFormatParsersFormatColorParser.with(
                new FakeParser<>() {
                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final SpreadsheetFormatParserContext context) {
                        Parsers.string(text, CaseSensitivity.SENSITIVE)
                            .orFailIfCursorNotEmpty(ParserReporters.basic())
                            .parse(cursor, context);

                        return Optional.of(
                            ParserTokens.sequence(
                                Lists.of(
                                    SpreadsheetFormatParserToken.color(
                                        Lists.of(
                                            SpreadsheetFormatParserToken.colorName(text, text)
                                        ),
                                        text
                                    )
                                ),
                                text
                            )
                        );
                    }
                }
            ),
            this.createContext(),
            text
        );
    }

    @Test
    public void testParseColorAndMoreTokens() {
        final String color = "[Black]";
        final String textPlaceholder = "@";
        final String text = color + textPlaceholder;

        final ParserToken expected = ParserTokens.sequence(
            Lists.of(
                SpreadsheetFormatParserToken.text(
                    Lists.of(
                        SpreadsheetFormatParserToken.color(
                            Lists.of(
                                SpreadsheetFormatParserToken.colorName(color, color)
                            ),
                            color
                        ),
                        SpreadsheetFormatParserToken.textLiteral(
                            textPlaceholder,
                            textPlaceholder
                        )
                    ),
                    text
                )
            ),
            text
        );

        this.parseAndCheck(
            SpreadsheetFormatParsersFormatColorParser.with(
                new FakeParser<>() {
                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final SpreadsheetFormatParserContext context) {
                        Parsers.string(text, CaseSensitivity.SENSITIVE)
                            .orFailIfCursorNotEmpty(ParserReporters.basic())
                            .parse(cursor, context);

                        return Optional.of(
                            expected
                        );
                    }
                }
            ),
            text,
            expected,
            text
        );
    }

    @Test
    public void testMinCount() {
        this.minCountAndCheck(
            111
        );
    }

    @Test
    public void testMaxCount() {
        this.maxCountAndCheck(
            222
        );
    }

    @Test
    public void testToString() {
        final String toString = "wrapped parser toString";

        this.toStringAndCheck(
            SpreadsheetFormatParsersFormatColorParser.with(
                Parsers.fake()
            ).setToString(toString),
            toString
        );
    }

    // ParserTesting...................................................................................................

    @Override
    public SpreadsheetFormatParsersFormatColorParser createParser() {
        return SpreadsheetFormatParsersFormatColorParser.with(
            new FakeParser<>() {
                @Override
                public int minCount() {
                    return 111;
                }

                @Override
                public int maxCount() {
                    return 222;
                }
            }
        );
    }

    @Override
    public SpreadsheetFormatParserContext createContext() {
        return SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION);
    }

    // ToStringTesting..................................................................................................

    @Override
    public Class<SpreadsheetFormatParsersFormatColorParser> type() {
        return SpreadsheetFormatParsersFormatColorParser.class;
    }
}
