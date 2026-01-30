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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ToStringSpreadsheetFormatterTest implements SpreadsheetFormatterTesting2<ToStringSpreadsheetFormatter>,
    HashCodeEqualsDefinedTesting2<ToStringSpreadsheetFormatter>,
    ToStringTesting<ToStringSpreadsheetFormatter>,
    ClassTesting2<ToStringSpreadsheetFormatter> {

    private final static List<SpreadsheetFormatterSelectorToken> TOKENS = Lists.of(
        SpreadsheetFormatterSelectorToken.with(
            "Label1",
            "Text1",
            SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
        )
    );

    private final static SpreadsheetFormatter FORMATTER = new FakeSpreadsheetFormatter() {

        @Override
        public Optional<TextNode> format(final Optional<Object> value,
                                         final SpreadsheetFormatterContext context) {
            Objects.requireNonNull(value, "value");
            Objects.requireNonNull(context, "context");

            return Optional.of(
                TextNode.text(
                    value.orElse("***")
                        .toString()
                        + " World"
                )
            );
        }

        @Override
        public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
            Objects.requireNonNull(context, "context");
            return TOKENS;
        }

        @Override
        public String toString() {
            return "TestSpreadsheetFormatter";
        }
    };

    private final static String TO_STRING = "TestToString";

    // with.............................................................................................................

    @Test
    public void testWithNullFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> ToStringSpreadsheetFormatter.with(
                null,
                TO_STRING
            )
        );
    }

    @Test
    public void testWithNullToStringFails() {
        assertThrows(
            NullPointerException.class,
            () -> ToStringSpreadsheetFormatter.with(
                FORMATTER,
                null
            )
        );
    }

    @Test
    public void testWithToStringSameToString() {
        assertSame(
            FORMATTER,
            ToStringSpreadsheetFormatter.with(
                FORMATTER,
                FORMATTER.toString()
            )
        );
    }

    @Test
    public void testWithToStringSameToString2() {
        final ToStringSpreadsheetFormatter formatter = this.createFormatter();
        assertSame(
            formatter,
            ToStringSpreadsheetFormatter.with(
                formatter,
                formatter.toString()
            )
        );
    }

    @Test
    public void testWithToStringSpreadsheetFormatterUnwraps() {
        final ToStringSpreadsheetFormatter formatter = this.createFormatter();

        assertSame(
            formatter,
            ToStringSpreadsheetFormatter.with(
                formatter,
                FORMATTER.toString()
            )
        );
    }

    @Test
    public void testFormat() {
        this.formatAndCheck(
            "Hello",
            "Hello World"
        );
    }

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createFormatter(),
            this.createContext(),
            TOKENS
        );
    }

    @Override
    public ToStringSpreadsheetFormatter createFormatter() {
        return (ToStringSpreadsheetFormatter)
            ToStringSpreadsheetFormatter.with(
                FORMATTER,
                TO_STRING
            );
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterContexts.fake();
    }

    @Override
    public Object value() {
        return "Hello";
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentFormatter() {
        this.checkNotEquals(
            ToStringSpreadsheetFormatter.with(
                SpreadsheetFormatters.fake(),
                TO_STRING
            )
        );
    }

    @Test
    public void testEqualsDifferentToString() {
        this.checkNotEquals(
            ToStringSpreadsheetFormatter.with(
                FORMATTER,
                "DifferentToString"
            )
        );
    }

    @Override
    public ToStringSpreadsheetFormatter createObject() {
        return this.createFormatter();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createFormatter(),
            TO_STRING
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            this.createFormatter(),
            "ToStringSpreadsheetFormatter\n" +
                "  TestSpreadsheetFormatter (walkingkooka.spreadsheet.format.ToStringSpreadsheetFormatterTest$1)\n"
        );
    }

    // Class............................................................................................................

    @Override
    public Class<ToStringSpreadsheetFormatter> type() {
        return ToStringSpreadsheetFormatter.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
