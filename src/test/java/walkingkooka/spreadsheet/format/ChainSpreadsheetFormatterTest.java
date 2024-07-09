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
import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ChainSpreadsheetFormatterTest extends SpreadsheetFormatterTestCase<ChainSpreadsheetFormatter> {

    private final static Integer VALUE1 = 11;
    private final static Double VALUE2 = 222.5;
    private final static String TEXT1 = "1st";
    private final static String TEXT2 = "2nd";

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> ChainSpreadsheetFormatter.with(null));
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> ChainSpreadsheetFormatter.with(Lists.empty()));
    }

    @Test
    public void testWithOneUnwraps() {
        final SpreadsheetFormatter formatter = SpreadsheetFormatters.fake();
        assertSame(formatter, ChainSpreadsheetFormatter.with(Lists.of(formatter)));
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
    public ChainSpreadsheetFormatter createFormatter() {
        return Cast.to(ChainSpreadsheetFormatter.with(Lists.of(this.formatter1(), this.formatter2())));
    }

    private SpreadsheetFormatter formatter1() {
        return this.formatter(VALUE1, TEXT1);
    }

    private SpreadsheetFormatter formatter2() {
        return this.formatter(VALUE2, TEXT2);
    }

    private SpreadsheetFormatter formatter(final Object value, final String text) {
        return new FakeSpreadsheetFormatter() {

            @Override
            public Optional<TextNode> format(final Object v,
                                             final SpreadsheetFormatterContext context) {
                Objects.requireNonNull(v, "value");
                Objects.requireNonNull(context, "context");

                return Optional.ofNullable(
                        v.equals(value) ?
                                SpreadsheetText.with(text)
                                        .toTextNode()
                                :
                                null
                );
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }
        };
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
    public Class<ChainSpreadsheetFormatter> type() {
        return ChainSpreadsheetFormatter.class;
    }
}
