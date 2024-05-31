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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterChainTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterChain, SpreadsheetFormatParserToken> {

    private final static Integer VALUE1 = 11;
    private final static Double VALUE2 = 222.5;
    private final static String TEXT1 = "1st";
    private final static String TEXT2 = "2nd";

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPatternSpreadsheetFormatterChain.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPatternSpreadsheetFormatterChain.with(Lists.empty())
        );
    }

    @Test
    public void testWithOneUnwraps() {
        final SpreadsheetPatternSpreadsheetFormatter formatter = SpreadsheetFormatters.fakeSpreadsheetPattern();
        assertSame(
                formatter,
                SpreadsheetPatternSpreadsheetFormatterChain.with(
                        Lists.of(formatter)
                )
        );
    }

    // canFormat........................................................................................................

    @Test
    public void testCanFormatFirst() {
        this.canFormatAndCheck(VALUE1, true);
    }

    @Test
    public void testCanFormatSecond() {
        this.canFormatAndCheck(VALUE2, true);
    }

    // format...........................................................................................................

    @Test
    public void testFormatFirst() {
        this.formatAndCheck(VALUE1, TEXT1);
    }

    @Test
    public void testFormatSecond() {
        this.formatAndCheck(VALUE2, TEXT2);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createFormatter(),
                VALUE1 + ";" + VALUE2
        );
    }

    // helpers..........................................................................................................

    @Override
    String pattern() {
        return "General";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.general();
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterChain createFormatter0(final SpreadsheetFormatParserToken token) {
        Objects.requireNonNull(token, "token"); // token is ignored by SpreadsheetPatternSpreadsheetFormatterChain

        return Cast.to(
                SpreadsheetPatternSpreadsheetFormatterChain.with(
                        Lists.of(
                                FORMATTER1,
                                FORMATTER2
                        )
                )
        );
    }

    private SpreadsheetPatternSpreadsheetFormatter FORMATTER1 = formatter(VALUE1, TEXT1);

    private SpreadsheetPatternSpreadsheetFormatter FORMATTER2 = formatter(VALUE2, TEXT2);

    private static SpreadsheetPatternSpreadsheetFormatter formatter(final Object value,
                                                                    final String text) {
        return new FakeSpreadsheetPatternSpreadsheetFormatter() {
            @Override
            public boolean canFormat(final Object v,
                                     final SpreadsheetFormatterContext context) {
                Objects.requireNonNull(v, "value");

                return value.equals(v);
            }

            @Override
            public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                                   final SpreadsheetFormatterContext context) {
                Objects.requireNonNull(value, "value");
                Objects.requireNonNull(context, "context");

                return Optional.of(spreadsheetText(text));
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }
        };
    }

    private static SpreadsheetText spreadsheetText(final String text) {
        return SpreadsheetText.with(text);
    }

    @Override
    public Object value() {
        return VALUE1;
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterContexts.fake();
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterChain> type() {
        return SpreadsheetPatternSpreadsheetFormatterChain.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentFormatters() {
        this.checkNotEquals(
                SpreadsheetPatternSpreadsheetFormatterChain.with(
                        Lists.of(
                                SpreadsheetPattern.parseTextFormatPattern("@")
                                        .formatter(),
                                SpreadsheetPattern.parseTextFormatPattern("@@")
                                        .formatter()
                        )
                ),
                SpreadsheetPatternSpreadsheetFormatterChain.with(
                        Lists.of(
                                SpreadsheetPattern.parseTextFormatPattern("@@@")
                                        .formatter()
                        )
                )
        );
    }
}
