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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverter;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;

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

        final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL, locale);
        final String expected = simpleDateFormat.format(Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)));

        this.checkEquals(expected, formatted, () -> pattern + "\nSimpleDateFormat: " + simpleDateFormat.toPattern());
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return SpreadsheetFormatterContexts.basic((n -> {
                    throw new UnsupportedOperationException();
                }),
                (n -> {
                    throw new UnsupportedOperationException();
                }),
                1,
                SpreadsheetFormatters.fake(),
                SpreadsheetConverterContexts.basic(
                        new FakeConverter<>() {

                            @Override
                            public boolean canConvert(final Object value,
                                                      final Class<?> type,
                                                      final SpreadsheetConverterContext context) {
                                return value instanceof LocalDate &&
                                        LocalDateTime.class == type;
                            }

                            @Override
                            public <T> Either<T, String> convert(final Object value,
                                                                 final Class<T> type,
                                                                 final SpreadsheetConverterContext context) {
                                return this.successfulConversion(
                                        type.cast(
                                                LocalDateTime.of(
                                                        (LocalDate) value,
                                                        LocalTime.MIDNIGHT
                                                )
                                        ),
                                        type
                                );
                            }
                        },
                        RESOLVE_IF_LABEL,
                        SpreadsheetConverterContexts.basic(
                                Converters.fake(),
                                RESOLVE_IF_LABEL,
                                ExpressionNumberConverterContexts.basic(
                                        Converters.fake(),
                                        ConverterContexts.basic(
                                                Converters.fake(),
                                                DateTimeContexts.locale(
                                                        Locale.ENGLISH,
                                                        1900,
                                                        20,
                                                        LocalDateTime::now
                                                ),
                                                DecimalNumberContexts.american(MathContext.DECIMAL32)
                                        ),
                                        ExpressionNumberKind.DEFAULT
                                )
                        )
                )
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
