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
import walkingkooka.convert.Converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class ConverterSpreadsheetFormatterTest implements SpreadsheetFormatterTesting2<ConverterSpreadsheetFormatter> {

    @Test
    public void testConvertable() {
        this.formatAndCheck(LocalDate.of(1999, 12, 31), "1999-12-31");
    }

    @Test
    public void testInvalid() {
        this.formatFailAndCheck("fail!");
    }

    @Override
    public ConverterSpreadsheetFormatter createFormatter() {
        return ConverterSpreadsheetFormatter.with(Converters.localDateString(c -> DateTimeFormatter.ISO_DATE));
    }

    @Override
    public Object value() {
        return LocalDate.now();
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public int twoDigitYear() {
                return 20;
            }

            @Override
            public Locale locale() {
                return Locale.ENGLISH;
            }
        };
    }

    @Override
    public Class<ConverterSpreadsheetFormatter> type() {
        return ConverterSpreadsheetFormatter.class;
    }
}
