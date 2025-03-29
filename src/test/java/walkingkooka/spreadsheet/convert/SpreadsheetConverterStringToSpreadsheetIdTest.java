
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
import walkingkooka.spreadsheet.SpreadsheetId;

public final class SpreadsheetConverterStringToSpreadsheetIdTest extends SpreadsheetConverterTestCase<SpreadsheetConverterStringToSpreadsheetId> {

    @Test
    public void testConvertStringToSpreadsheetId() {
        final SpreadsheetId id = SpreadsheetId.parse("123");

        this.convertAndCheck(
                id.toString(),
                id
        );
    }

    @Override
    public SpreadsheetConverterStringToSpreadsheetId createConverter() {
        return SpreadsheetConverterStringToSpreadsheetId.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.successfulConversion(
                        target.cast(value),
                        target
                );
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterStringToSpreadsheetId> type() {
        return SpreadsheetConverterStringToSpreadsheetId.class;
    }
}
