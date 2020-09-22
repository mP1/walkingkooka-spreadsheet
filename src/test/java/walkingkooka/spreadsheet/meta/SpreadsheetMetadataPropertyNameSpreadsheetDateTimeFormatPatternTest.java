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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.FakeConverter;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;

import java.math.MathContext;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPatternTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern, SpreadsheetDateTimeFormatPattern> {

    @Test
    public void testExtractLocaleValue() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetDateTimeFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern.instance()
                .extractLocaleValue(locale)
                .get();

        final LocalDateTime date = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        final String formatted = pattern.formatter()
                .format(date, spreadsheetFormatterContext()).get().text();
        assertEquals("Friday, December 31, 1999 at 12:58:59 PM", formatted, () -> pattern.toString());
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return SpreadsheetFormatterContexts.basic((n-> {throw new UnsupportedOperationException();}),
                (n-> {throw new UnsupportedOperationException();}),
                1,
                new FakeConverter(){

                    @Override
                    public <T> Either<T, String> convert(final Object value, final Class<T> type, final ConverterContext context) {
                        return Either.left(type.cast(value));
                    }

                    @Override
                    public <T> T convertOrFail(Object value, Class<T> target, ConverterContext context) {
                        final LocalDate date = (LocalDate)value;
                        return target.cast(LocalDateTime.of(date, LocalTime.MIDNIGHT));
                    }
                },
                SpreadsheetFormatters.fake(),
                ConverterContexts.basic(DateTimeContexts.locale(Locale.ENGLISH, 20), DecimalNumberContexts.american(MathContext.DECIMAL32))
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern.instance(), "date-time-format-pattern");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern.instance();
    }

    @Override
    SpreadsheetDateTimeFormatPattern propertyValue() {
        return SpreadsheetDateTimeFormatPattern.parseDateTimeFormatPattern("dd mm yyyy hh mm ss\"custom\"");
    }

    @Override
    String propertyValueType() {
        return "DateTime format pattern";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern.class;
    }
}
