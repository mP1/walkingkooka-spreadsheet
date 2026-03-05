
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
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateFormatKind;
import walkingkooka.datetime.DateTimeSymbols;

import java.text.DateFormatSymbols;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public final class SpreadsheetFormatterSharedTimeTest extends SpreadsheetFormatterSharedTestCase<SpreadsheetFormatterSharedTime> {

    private final static LocalTime TIME = LocalTime.of(
        12,
        58,
        59
    );

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    @Test
    public void testFormatWithShort() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedTime.with(DateFormatKind.SHORT),
            TIME,
            "12:58 PM"
        );
    }

    @Test
    public void testFormatWithMedium() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedTime.with(DateFormatKind.MEDIUM),
            TIME,
            "12:58:59 PM"
        );
    }

    @Test
    public void testFormatWithLong() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedTime.with(DateFormatKind.LONG),
            TIME,
            "12:58:59 PM"
        );
    }

    @Test
    public void testFormatWithFull() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedTime.with(DateFormatKind.FULL),
            TIME,
            "12:58:59 PM"
        );
    }

    @Override
    public SpreadsheetFormatterSharedTime createFormatter() {
        return SpreadsheetFormatterSharedTime.with(DateFormatKind.MEDIUM);
    }

    @Override
    public Object value() {
        return TIME;
    }
    
    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public Locale locale() {
                return LOCALE;
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

            private final Converter<SpreadsheetFormatterContext> converter = Converters.localTimeToLocalDateTime();

            @Override
            public List<String> ampms() {
                return this.dateTimeSymbols()
                    .ampms();
            }

            @Override
            public DateTimeSymbols dateTimeSymbols() {
                return this.dateTimeSymbols;
            }

            private final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(LOCALE)
            );

            @Override
            public char zeroDigit() {
                return '0';
            }
        };
    }

    @Test
    public void testToStringShort() {
        this.toStringAndCheck(
            SpreadsheetFormatterSharedTime.with(DateFormatKind.SHORT),
            "time-short"
        );
    }

    @Test
    public void testToStringMedium() {
        this.toStringAndCheck(
            SpreadsheetFormatterSharedTime.with(DateFormatKind.MEDIUM),
            "time-medium"
        );
    }

    @Override
    public Class<SpreadsheetFormatterSharedTime> type() {
        return SpreadsheetFormatterSharedTime.class;
    }
}
