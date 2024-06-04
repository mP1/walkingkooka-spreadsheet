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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameFormatterDateTimeTest extends SpreadsheetMetadataPropertyNameFormatterTestCase<SpreadsheetMetadataPropertyNameFormatterDateTime> {

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(
                Locale.ENGLISH,
                SpreadsheetDateParsePattern.parseDateTimeFormatPattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM")
                        .spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testExtractLocaleValueAndFormat() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetFormatPattern pattern = SpreadsheetMetadataPropertyNameFormatterDateTime.instance()
                .extractLocaleValue(locale)
                .get()
                .spreadsheetFormatPattern()
                .get();

        final LocalDateTime date = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        final String formatted = pattern.formatter()
                .format(
                        date,
                        spreadsheetFormatterContext()
                )
                .get()
                .text();
        this.checkEquals(
                "Friday, December 31, 1999 at 12:58:59 PM",
                formatted,
                pattern::toString
        );
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return SpreadsheetFormatterContexts.basic(
                (n -> {
                    throw new UnsupportedOperationException();
                }),
                (n -> {
                    throw new UnsupportedOperationException();
                }),
                1, // cellCharacterWidth
                8, // generalNumberFormatDigitCount
                SpreadsheetFormatters.fake(),
                SpreadsheetConverterContexts.basic(
                        new FakeConverter<>() {

                            @Override
                            public boolean canConvert(final Object value,
                                                      final Class<?> type,
                                                      final SpreadsheetConverterContext context) {
                                return type.isInstance(value);
                            }

                            @Override
                            public <T> Either<T, String> convert(final Object value,
                                                                 final Class<T> type,
                                                                 final SpreadsheetConverterContext context) {
                                return this.successfulConversion(
                                        type.cast(value),
                                        type
                                );
                            }
                        },
                        LABEL_NAME_RESOLVER,
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
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameFormatterDateTime.instance(),
                "date-time-formatter"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameFormatterDateTime createName() {
        return SpreadsheetMetadataPropertyNameFormatterDateTime.instance();
    }

    @Override
    SpreadsheetFormatterSelector propertyValue() {
        return SpreadsheetDateTimeFormatPattern.parseDateTimeFormatPattern("dd mm yyyy hh mm ss\"custom\"")
                .spreadsheetFormatterSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameFormatterDateTime> type() {
        return SpreadsheetMetadataPropertyNameFormatterDateTime.class;
    }
}
