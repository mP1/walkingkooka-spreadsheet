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
import walkingkooka.datetime.HasDateTimeSymbolsTesting;
import walkingkooka.util.HasLocaleTesting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public final class SpreadsheetFormatterSharedDateTimeTest extends SpreadsheetFormatterSharedTestCase<SpreadsheetFormatterSharedDateTime>
implements HasDateTimeSymbolsTesting,
    HasLocaleTesting {

    private final static LocalDateTime DATE_TIME = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    @Test
    public void testFormatWithShort() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedDateTime.with(DateFormatKind.SHORT),
            DATE_TIME,
            "31/12/99, 12:58 PM"
        );
    }

    @Test
    public void testFormatWithMedium() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedDateTime.with(DateFormatKind.MEDIUM),
            DATE_TIME,
            "31 Dec. 1999, 12:58:59 PM"
        );
    }

    @Test
    public void testFormatWithLong() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedDateTime.with(DateFormatKind.LONG),
            DATE_TIME,
            "31 December 1999 at 12:58:59 PM"
        );
    }

    @Test
    public void testFormatWithFull() {
        this.formatAndCheck(
            SpreadsheetFormatterSharedDateTime.with(DateFormatKind.FULL),
            DATE_TIME,
            "Friday, 31 December 1999 at 12:58:59 PM"
        );
    }

    @Override
    public SpreadsheetFormatterSharedDateTime createFormatter() {
        return SpreadsheetFormatterSharedDateTime.with(DateFormatKind.MEDIUM);
    }

    @Override
    public Object value() {
        return DATE_TIME;
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

            private final Converter<SpreadsheetFormatterContext> converter = Converters.simple();

            @Override
            public List<String> ampms() {
                return DATE_TIME_SYMBOLS.ampms();
            }

            @Override
            public List<String> monthNames() {
                return DATE_TIME_SYMBOLS.monthNames();
            }

            @Override
            public List<String> monthNameAbbreviations() {
                return DATE_TIME_SYMBOLS.monthNameAbbreviations();
            }

            @Override
            public List<String> weekDayNames() {
                return DATE_TIME_SYMBOLS.weekDayNames();
            }

            @Override
            public List<String> weekDayNameAbbreviations() {
                return DATE_TIME_SYMBOLS.weekDayNameAbbreviations();
            }

            @Override
            public DateTimeSymbols dateTimeSymbols() {
                return DATE_TIME_SYMBOLS;
            }

            @Override
            public char zeroDigit() {
                return '0';
            }
        };
    }

    @Test
    public void testToStringShort() {
        this.toStringAndCheck(
            SpreadsheetFormatterSharedDateTime.with(DateFormatKind.SHORT),
            "dateTime-short"
        );
    }

    @Test
    public void testToStringMedium() {
        this.toStringAndCheck(
            SpreadsheetFormatterSharedDateTime.with(DateFormatKind.MEDIUM),
            "dateTime-medium"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createFormatter(),
            "SpreadsheetFormatterSharedDateTime\n" +
                "  MEDIUM\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSharedDateTime> type() {
        return SpreadsheetFormatterSharedDateTime.class;
    }
}
