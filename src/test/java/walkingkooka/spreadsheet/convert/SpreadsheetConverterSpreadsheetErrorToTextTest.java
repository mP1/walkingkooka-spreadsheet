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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;

public final class SpreadsheetConverterSpreadsheetErrorToTextTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetErrorToText> {

    @Test
    public void testConvertNonErrorFails() {
        this.convertFails(15, String.class);
    }

    @Test
    public void testConvertNonErrorStringFails() {
        this.convertFails("Hello", String.class);
    }

    @Test
    public void testConvertSpreadsheetErrorErrorToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.ERROR;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testConvertSpreadsheetErrorDiv0ToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored2"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testConvertSpreadsheetErrorDiv0ToCharSequence() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored2"),
                CharSequence.class,
                kind.text()
        );
    }

    @Test
    public void testConvertSpreadsheetErrorToNumber() {
        this.convertFails(
                SpreadsheetErrorKind.DIV0.setMessage("!!"),
                Number.class
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetErrorToText createConverter() {
        return SpreadsheetConverterSpreadsheetErrorToText.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                        value,
                        target,
                        this
                );
            }

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                        value,
                        type,
                        this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.textToText();
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetConverterSpreadsheetErrorToText.INSTANCE,
                "SpreadsheetError to text"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetErrorToText> type() {
        return SpreadsheetConverterSpreadsheetErrorToText.class;
    }
}
