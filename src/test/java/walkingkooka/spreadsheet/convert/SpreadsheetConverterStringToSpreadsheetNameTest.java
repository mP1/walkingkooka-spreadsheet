
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
import walkingkooka.spreadsheet.SpreadsheetName;

public final class SpreadsheetConverterStringToSpreadsheetNameTest extends SpreadsheetConverterTestCase<SpreadsheetConverterStringToSpreadsheetName> {

    @Test
    public void testConvertStringToSpreadsheetName() {
        final SpreadsheetName name = SpreadsheetName.with("SpreadsheetName222");

        this.convertAndCheck(
                name.toString(),
                name
        );
    }

    @Test
    public void testConvertCharSequenceToSpreadsheetName() {
        final SpreadsheetName name = SpreadsheetName.with("SpreadsheetName222");

        this.convertAndCheck(
                new StringBuilder(name.toString()),
                name
        );
    }

    @Override
    public SpreadsheetConverterStringToSpreadsheetName createConverter() {
        return SpreadsheetConverterStringToSpreadsheetName.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converter.canConvert(
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

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.textToText();
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterStringToSpreadsheetName> type() {
        return SpreadsheetConverterStringToSpreadsheetName.class;
    }
}
