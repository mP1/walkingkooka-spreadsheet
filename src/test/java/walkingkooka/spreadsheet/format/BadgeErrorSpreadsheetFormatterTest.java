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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BadgeErrorSpreadsheetFormatterTest implements SpreadsheetFormatterTesting2<BadgeErrorSpreadsheetFormatter> {

    @Test
    public void testWithNullFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> BadgeErrorSpreadsheetFormatter.with(null)
        );
    }

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            Optional.empty(),
            Optional.empty()
        );
    }

    @Test
    public void testFormatSpreadsheetErrorWithDiv0() {
        this.formatAndCheck(
            SpreadsheetErrorKind.DIV0.toError(),
            TextNode.badge("#DIV/0!")
                .appendChild(
                    TextNode.text("#DIV/0!#DIV/0!")
                )
        );
    }

    @Test
    public void testFormatSpreadsheetErrorWithError() {
        this.formatAndCheck(
            SpreadsheetErrorKind.ERROR.toError(),
            TextNode.badge("#ERROR")
                .appendChild(
                    TextNode.text("#ERROR#ERROR")
                )
        );
    }

    @Test
    public void testFormatSpreadsheetErrorWithMessage() {
        final String message = "Error message 123";

        this.formatAndCheck(
            SpreadsheetErrorKind.ERROR.setMessage(message),
            TextNode.badge(message)
                .appendChild(
                    TextNode.text("#ERROR#ERROR")
                )
        );
    }

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createContext(),
            Lists.empty()
        );
    }

    @Override
    public BadgeErrorSpreadsheetFormatter createFormatter() {
        return BadgeErrorSpreadsheetFormatter.with(
            SpreadsheetPattern.parseTextFormatPattern("@@")
                    .formatter()
        );
    }

    @Override
    public Object value() {
        return SpreadsheetErrorKind.DIV0.setMessage("Divide by zero is bad!");
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                Lists.of(
                    SpreadsheetConverters.text(),
                    SpreadsheetConverters.spreadsheetValue()
                )
            );
        };
    }

    // class............................................................................................................

    @Override
    public Class<BadgeErrorSpreadsheetFormatter> type() {
        return BadgeErrorSpreadsheetFormatter.class;
    }
}
