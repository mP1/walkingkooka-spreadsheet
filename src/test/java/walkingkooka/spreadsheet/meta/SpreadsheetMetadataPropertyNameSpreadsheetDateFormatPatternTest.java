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
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverter;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;

import javax.swing.text.html.parser.DTDConstants;
import java.math.MathContext;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPatternTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern, SpreadsheetDateFormatPattern> {

    @Test
    public void testExtractLocaleValue() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetDateFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern.instance()
                .extractLocaleValue(locale)
                .get();

        final LocalDate date = LocalDate.of(1999, 12, 31);
        final String formatted = pattern.formatter()
                .format(date, spreadsheetFormatterContext()).get().text();

        final SimpleDateFormat simpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.FULL, locale);
        final String expected = simpleDateFormat.format(Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)));

        assertEquals(expected, formatted, () -> pattern + "\nSimpleDateFormat: " + simpleDateFormat.toPattern());
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return SpreadsheetFormatterContexts.basic((n-> {throw new UnsupportedOperationException();}),
        (n-> {throw new UnsupportedOperationException();}),
        1,
                new FakeConverter(){

                    @Override
                    public <T> Either<T, String> convert(final Object value, final Class<T> type, final ConverterContext context) {
                        final LocalDate date = (LocalDate)value;
                        return Either.left(type.cast(LocalDateTime.of(date, LocalTime.MIDNIGHT)));
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
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern.instance(), "date-format-pattern");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern.instance();
    }

    @Override
    SpreadsheetDateFormatPattern propertyValue() {
        return SpreadsheetDateFormatPattern.parseDateFormatPattern("dd mm yyyy \"custom\"");
    }

    @Override
    String propertyValueType() {
        return "Date format pattern";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern.class;
    }
}
