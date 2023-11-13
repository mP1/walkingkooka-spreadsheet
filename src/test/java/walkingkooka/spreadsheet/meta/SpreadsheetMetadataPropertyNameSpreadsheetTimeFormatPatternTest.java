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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPatternTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern, SpreadsheetTimeFormatPattern> {

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(
                Locale.ENGLISH,
                SpreadsheetTimeParsePattern.parseTimeFormatPattern("h:mm:ss AM/PM")
        );
    }

    @Test
    public void testExtractLocaleValueAndFormat() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetTimeFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern.instance()
                .extractLocaleValue(locale)
                .get();

        final LocalTime time = LocalTime.of(12, 58, 59);
        final String formatted = pattern.formatter()
                .format(time, spreadsheetFormatterContext()).get().text();

        this.checkEquals(
                "12:58:59 PM",
                formatted,
                pattern::toString
        );
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
                                return value instanceof LocalTime &&
                                        LocalDateTime.class == type;
                            }

                            @Override
                            public <T> Either<T, String> convert(final Object value,
                                                                 final Class<T> type,
                                                                 final SpreadsheetConverterContext context) {
                                final LocalTime time = (LocalTime) value;
                                return this.successfulConversion(
                                        type.cast(
                                                LocalDateTime.of(LocalDate.EPOCH, time)
                                        ),
                                        type
                                );
                            }
                        },
                        RESOLVE_IF_LABEL,
                        ExpressionNumberConverterContexts.basic(
                                Converters.fake(),
                                ConverterContexts.basic(Converters.fake(),
                                        DateTimeContexts.locale(
                                                Locale.ENGLISH,
                                                1900,
                                                20,
                                                LocalDateTime::now
                                        ),
                                        DecimalNumberContexts.american(MathContext.DECIMAL32)),
                                ExpressionNumberKind.DEFAULT
                        )
                )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern.instance(), "time-format-pattern");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern.instance();
    }

    @Override
    SpreadsheetTimeFormatPattern propertyValue() {
        return SpreadsheetTimeFormatPattern.parseTimeFormatPattern("hh mm ss\"custom\"");
    }

    @Override
    String propertyValueType() {
        return "Time format pattern";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern.class;
    }
}
