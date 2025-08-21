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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.color.RgbColor;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.HasText;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.lang.reflect.Method;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetConvertersTest implements ClassTesting2<SpreadsheetConverters>,
    PublicStaticHelperTesting<SpreadsheetConverters>,
    ConverterTesting {

    // basic............................................................................................................

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testBasicConvertStringTrueToBoolean() {
        this.basicConvertAndCheck(
            "true",
            true
        );
    }

    @Test
    public void testBasicConvertStringFalseToBoolean() {
        this.basicConvertAndCheck(
            "false",
            false
        );
    }

    @Test
    public void testBasicConvertBooleanTrueToString() {
        this.basicConvertAndCheck(
            true,
            "true"
        );
    }

    @Test
    public void testBasicConvertBooleanFalseToString() {
        this.basicConvertAndCheck(
            false,
            "false"
        );
    }

    @Test
    public void testBasicConvertStringToDate() {
        this.basicConvertAndCheck(
            "1999/12/31",
            LocalDate.of(
                1999,
                12,
                31
            )
        );
    }

    @Test
    public void testBasicConvertDateToString() {
        this.basicConvertAndCheck(
            LocalDate.of(
                1999,
                12,
                31
            ),
            "1999/12/31"
        );
    }

    @Test
    public void testBasicConvertStringToDateTime() {
        this.basicConvertAndCheck(
            "1999/12/31 12:58:59",
            LocalDateTime.of(
                1999,
                12,
                31,
                12,
                58,
                59
            )
        );
    }

    @Test
    public void testBasicConvertDateTimeToString() {
        this.basicConvertAndCheck(
            LocalDateTime.of(
                1999,
                12,
                31,
                12,
                58,
                59
            ),
            "1999/12/31 12:58:59"
        );
    }

    @Test
    public void testBasicConvertStringToNumberInteger() {
        this.basicConvertAndCheck(
            "123",
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testBasicConvertNumberIntegerToString() {
        this.basicConvertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            "123."
        );
    }

    @Test
    public void testBasicConvertStringToNumberDecimal() {
        this.basicConvertAndCheck(
            "45.75",
            EXPRESSION_NUMBER_KIND.create(45.75)
        );
    }

    @Test
    public void testBasicConvertNumberDecimalToString() {
        this.basicConvertAndCheck(
            EXPRESSION_NUMBER_KIND.create(45.75),
            "45.75"
        );
    }

    @Test
    public void testBasicConvertStringToString() {
        final String text = "Hello World 123";

        this.basicConvertAndCheck(
            text,
            text
        );
    }

    @Test
    public void testBasicConvertStringToTime() {
        this.basicConvertAndCheck(
            "12:58:59",
            LocalTime.of(
                12,
                58,
                59
            )
        );
    }

    @Test
    public void testBasicConvertTimeToString() {
        this.basicConvertAndCheck(
            LocalTime.of(
                12,
                58,
                59
            ),
            "12:58:59"
        );
    }

    @Test
    public void testBasicConvertNumberToRgbColor() {
        final Integer number = 0x123456;

        this.basicConvertAndCheck(
            number,
            RgbColor.class,
            RgbColor.fromRgb(number)
        );
    }

    @Test
    public void testBasicConvertStringToRgbColor() {
        final String text = "#123";

        this.basicConvertAndCheck(
            text,
            RgbColor.class,
            RgbColor.parseRgb(text)
        );
    }

    @Test
    public void testBasicConvertStringToExpression() {
        this.basicConvertAndCheck(
            "1+2",
            Expression.class,
            Expression.add(
                Expression.value(
                    EXPRESSION_NUMBER_KIND.one()
                ),
                Expression.value(
                    EXPRESSION_NUMBER_KIND.create(2)
                )
            )
        );
    }

    @Test
    public void testBasicConvertStringToTextStyle() {
        final String text = "{color: #123}";

        this.basicConvertAndCheck(
            text,
            TextStyle.class,
            TextStyle.parse(text)
        );
    }

    private void basicConvertAndCheck(final Object value,
                                      final Object expected) {
        this.basicConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void basicConvertAndCheck(final Object value,
                                          final Class<T> type,
                                      final T expected) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.basic();

        this.convertAndCheck(
            converter,
            value,
            type,
            new FakeSpreadsheetConverterContext() {
                @Override
                public boolean canConvert(final Object v,
                                          final Class<?> t) {
                    return converter.canConvert(
                        v,
                        t,
                        this
                    );
                }

                @Override
                public <T> Either<T, String> convert(final Object v,
                                                     final Class<T> t) {
                    return converter.convert(
                        v,
                        t,
                        this
                    );
                }

                @Override
                public ExpressionNumberKind expressionNumberKind() {
                    return EXPRESSION_NUMBER_KIND;
                }

                @Override
                public String currencySymbol() {
                    return this.decimalNumberContext.currencySymbol();
                }

                @Override
                public char decimalSeparator() {
                    return this.decimalNumberContext.decimalSeparator();
                }

                @Override
                public String exponentSymbol() {
                    return this.decimalNumberContext.exponentSymbol();
                }

                @Override
                public char groupSeparator() {
                    return this.decimalNumberContext.groupSeparator();
                }

                @Override
                public String infinitySymbol() {
                    return this.decimalNumberContext.infinitySymbol();
                }

                @Override
                public char monetaryDecimalSeparator() {
                    return this.decimalNumberContext.monetaryDecimalSeparator();
                }

                @Override
                public String nanSymbol() {
                    return this.decimalNumberContext.nanSymbol();
                }

                @Override
                public char negativeSign() {
                    return this.decimalNumberContext.negativeSign();
                }

                @Override
                public char percentSymbol() {
                    return this.decimalNumberContext.percentSymbol();
                }

                @Override
                public char permillSymbol() {
                    return this.decimalNumberContext.permillSymbol();
                }

                @Override
                public char positiveSign() {
                    return this.decimalNumberContext.positiveSign();
                }

                @Override
                public char zeroDigit() {
                    return this.decimalNumberContext.zeroDigit();
                }

                @Override
                public DecimalNumberSymbols decimalNumberSymbols() {
                    return this.decimalNumberContext.decimalNumberSymbols();
                }

                @Override
                public Locale locale() {
                    return this.decimalNumberContext.locale();
                }

                @Override
                public MathContext mathContext() {
                    return this.decimalNumberContext.mathContext();
                }

                private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.DECIMAL32);

                @Override
                public List<String> ampms() {
                    return this.dateTimeContext.ampms();
                }

                @Override
                public String ampm(final int hourOfDay) {
                    return this.dateTimeContext.ampm(hourOfDay);
                }

                @Override
                public int defaultYear() {
                    return this.dateTimeContext.defaultYear();
                }

                @Override
                public List<String> monthNames() {
                    return this.dateTimeContext.ampms();
                }

                @Override
                public String monthName(final int month) {
                    return this.dateTimeContext.monthName(month);
                }

                @Override
                public List<String> monthNameAbbreviations() {
                    return this.dateTimeContext.monthNameAbbreviations();
                }

                @Override
                public String monthNameAbbreviation(final int month) {
                    return this.dateTimeContext.monthNameAbbreviation(month);
                }

                @Override
                public int twoDigitYear() {
                    return this.dateTimeContext.twoDigitYear();
                }

                @Override
                public List<String> weekDayNames() {
                    return this.dateTimeContext.weekDayNames();
                }

                @Override
                public String weekDayName(final int day) {
                    return this.dateTimeContext.weekDayName(day);
                }

                @Override
                public List<String> weekDayNameAbbreviations() {
                    return this.dateTimeContext.weekDayNameAbbreviations();
                }

                @Override
                public String weekDayNameAbbreviation(final int day) {
                    return this.dateTimeContext.weekDayNameAbbreviation(day);
                }

                @Override
                public DateTimeSymbols dateTimeSymbols() {
                    return this.dateTimeContext.dateTimeSymbols();
                }

                private final DateTimeContext dateTimeContext = DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(Locale.ENGLISH)
                    ),
                    Locale.forLanguageTag("en-AU"),
                    1980, // defaultYear
                    50, // twoDigitYear,
                    () -> {
                        throw new UnsupportedOperationException();
                    }
                );
            },
            Cast.to(expected)
        );
    }

    // color.............................................................................................................

    @Test
    public void testColorConvertColorToColor() {
        final Color rgb = RgbColor.BLACK;

        this.colorConvertAndCheck(
            rgb,
            rgb.toHsv()
        );
    }

    @Test
    public void testColorConvertColorToNumber() {
        final RgbColor color = Color.parseRgb("#123456");

        this.colorConvertAndCheck(
            color,
            color.value()
        );
    }

    @Test
    public void testColorConvertColorToString() {
        final RgbColor color = Color.parseRgb("#123456");

        this.colorConvertAndCheck(
            color,
            color.toString()
        );
    }

    @Test
    public void testColorConvertColorBlackToString() {
        final RgbColor color = Color.BLACK;

        this.colorConvertAndCheck(
            color,
            color.toString()
        );
    }

    @Test
    public void testColorConvertNumberToColor() {
        final RgbColor color = Color.parseRgb("#123456");

        this.colorConvertAndCheck(
            color.value(),
            color
        );
    }

    @Test
    public void testColorConvertStringToColor() {
        final String text = "#123";

        this.colorConvertAndCheck(
            text,
            Color.class,
            Color.parseRgb(text)
        );
    }

    @Test
    public void testColorConvertStringToRgbColor() {
        final String text = "#123";

        this.colorConvertAndCheck(
            text,
            Color.parseRgb(text)
        );
    }

    @Test
    public void testColorConvertStringToSpreadsheetColorName() {
        final SpreadsheetColorName name = SpreadsheetColorName.RED;

        this.colorConvertAndCheck(
            name.value(),
            name
        );
    }

    @Test
    public void testColorConvertSpreadsheetColorNameToString() {
        final SpreadsheetColorName name = SpreadsheetColorName.RED;

        this.colorConvertAndCheck(
            name,
            name.value()
        );
    }

    private final static String SPREADSHEET_COLOR_NAME = "HelloColor";
    private final static Color COLOR10 = Color.parse("#101010");

    @Test
    public void testColorConvertSpreadsheetMetadataColorToString() {
        this.colorConvertAndCheck(
            "[" + SPREADSHEET_COLOR_NAME + "]",
            COLOR10
        );
    }

    @Test
    public void testColorConvertStringToSpreadsheetMetadataColorName() {
        this.colorConvertAndCheck(
            SPREADSHEET_COLOR_NAME,
            SpreadsheetColorName.with(SPREADSHEET_COLOR_NAME)
        );
    }

    private void colorConvertAndCheck(final Object value,
                                      final Object expected) {
        this.colorConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void colorConvertAndCheck(final Object value,
                                         final Class<T> type,
                                         final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.color(),
            value,
            type,
            new FakeSpreadsheetConverterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return this.converter.canConvert(
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

                private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                    Lists.of(
                        SpreadsheetConverters.simple(),
                        SpreadsheetConverters.text(),
                        SpreadsheetConverters.numberToNumber()
                    )
                );

                @Override
                public SpreadsheetMetadata spreadsheetMetadata() {
                    return SpreadsheetMetadataTesting.METADATA_EN_AU
                        .set(
                            SpreadsheetMetadataPropertyName.namedColor(
                                SpreadsheetColorName.with(SPREADSHEET_COLOR_NAME)
                            ),
                            10
                        ).set(
                            SpreadsheetMetadataPropertyName.numberedColor(
                                10
                            ),
                            COLOR10
                        );
                }
            },
            expected
        );
    }

    // json.............................................................................................................

    @Test
    @Disabled
    public void testJsonConvertJsonToString() {
        final JsonNode json = JsonNode.object()
            .set(
                JsonPropertyName.with("message"),
                JsonNode.string("Hello World 123")
            );

        this.convertFails(
            SpreadsheetConverters.json(),
            json,
            String.class,
            JSON_CONVERTER_CONTEXT
        );
    }

    private final static JsonNodeMarshallUnmarshallContext JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT = JsonNodeMarshallUnmarshallContexts.basic(
        JsonNodeMarshallContexts.basic(),
        JsonNodeUnmarshallContexts.basic(
            EXPRESSION_NUMBER_KIND,
            MathContext.DECIMAL32
        )
    );

    @Test
    public void testJsonConvertExpressionNumberToJsonNode() {
        final ExpressionNumber number = EXPRESSION_NUMBER_KIND.create(123.5);
        final JsonNode json = JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.marshall(number);

        this.jsonConvertAndCheck(
            number,
            JsonNode.class,
            json
        );
    }

    @Test
    public void testJsonConvertJsonToExpressionNumber() {
        final ExpressionNumber number = EXPRESSION_NUMBER_KIND.create(123.5);
        final JsonNode json = JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.marshall(number);

        this.jsonConvertAndCheck(
            json,
            number
        );
    }

    private void jsonConvertAndCheck(final Object value,
                                     final Object expected) {
        this.jsonConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void jsonConvertAndCheck(final Object value,
                                          final Class<T> type,
                                          final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.json(),
            value,
            type,
            JSON_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext JSON_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(
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

        private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
            Lists.of(
                SpreadsheetConverters.simple(),
                SpreadsheetConverters.text()
            )
        );

        @Override
        public Optional<JsonString> typeName(final Class<?> type) {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.typeName(type);
        }

        @Override
        public JsonNode marshall(final Object object) {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.marshall(object);
        }

        @Override
        public <T> T unmarshall(final JsonNode json,
                                final Class<T> type) {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.unmarshall(
                json,
                type
            );
        }
    };
    
    // locale...........................................................................................................

    @Test
    public void testLocaleConvertLocaleToLocaleFails() {
        final Locale locale = Locale.FRANCE;

        this.convertFails(
            SpreadsheetConverters.locale(),
            locale,
            Locale.class,
            LOCALE_CONTEXT
        );
    }

    @Test
    public void testLocaleConvertStringToLocale() {
        final Locale locale = Locale.FRANCE;

        this.localeConvertAndCheck(
            locale.toLanguageTag(),
            locale
        );
    }

    private void localeConvertAndCheck(final Object value,
                                       final Object expected) {
        this.localeConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void localeConvertAndCheck(final Object value,
                                           final Class<T> type,
                                           final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.locale(),
            value,
            type,
            LOCALE_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext LOCALE_CONTEXT = new FakeSpreadsheetConverterContext() {
        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(
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

        private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
            Lists.of(
                SpreadsheetConverters.simple(),
                SpreadsheetConverters.text()
            )
        );
    };

    // style............................................................................................................

    private final TextStyle STYLE = TextStyle.parse("background-color: red");

    @Test
    public void testStyleConvertStringToColorFails() {
        this.convertFails(
            SpreadsheetConverters.style(),
            Color.BLACK.value(),
            Color.class,
            STYLE_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testStyleConvertHasTextStyleToStyleWithSpreadsheetCell() {
        this.styleConvertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(STYLE),
            STYLE
        );
    }

    @Test
    public void testStyleConvertStringToTextStyle() {
        this.styleConvertAndCheck(
            STYLE.text(),
            STYLE
        );
    }

    @Test
    public void testStyleConvertStringToTextStylePropertyName() {
        final TextStylePropertyName<?> propertyName = TextStylePropertyName.COLOR;

        this.styleConvertAndCheck(
            propertyName.text(),
            propertyName
        );
    }

    private void styleConvertAndCheck(final Object value,
                                      final Object expected) {
        this.styleConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void styleConvertAndCheck(final Object value,
                                          final Class<T> type,
                                          final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.style(),
            value,
            type,
            STYLE_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext STYLE_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(
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

        private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
            Lists.of(
                SpreadsheetConverters.simple(),
                SpreadsheetConverters.text()
            )
        );
    };
    
    // date.............................................................................................................

    @Test
    public void testTextToDateConvertInvalidStringToDateFails() {
        this.convertFails(
            SpreadsheetConverters.textToDate(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                    .parser()
            ),
            "1999/12", // missing day
            LocalDate.class,
            this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testTextToDateConvertStringToDate() {
        this.convertAndCheck(
            SpreadsheetConverters.textToDate(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                    .parser()
            ),
            "1999/12/31",
            LocalDate.class,
            this.dateTimeSpreadsheetConverterContext(),
            LocalDate.of(1999, 12, 31)
        );
    }

    // dateTime.............................................................................................................

    @Test
    public void testTextToDateTimeConvertFails() {
        this.convertFails(
            SpreadsheetConverters.textToDateTime(
                SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm")
                    .parser()
            ),
            "1999/12", // missing day
            LocalDateTime.class,
            this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testTextToDateTimeConvertStringToDateTime() {
        this.convertAndCheck(
            SpreadsheetConverters.textToDateTime(
                SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm")
                    .parser()
            ),
            "1999/12/31 12:59",
            LocalDateTime.class,
            this.dateTimeSpreadsheetConverterContext(),
            LocalDateTime.of(1999, 12, 31, 12, 59)
        );
    }

    // time.............................................................................................................

    @Test
    public void testTextToTimeConvertInvalidStringToTimeFails() {
        this.convertFails(
            SpreadsheetConverters.textToTime(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                    .parser()
            ),
            "12:", // missing minutes
            LocalTime.class,
            this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testTextToTimeConvertStringToTime() {
        this.convertAndCheck(
            SpreadsheetConverters.textToTime(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                    .parser()
            ),
            "12:59",
            LocalTime.class,
            this.dateTimeSpreadsheetConverterContext(),
            LocalTime.of(12, 59)
        );
    }

    private SpreadsheetConverterContext dateTimeSpreadsheetConverterContext() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        DateTimeContexts.basic(
                            DateTimeSymbols.fromDateFormatSymbols(
                                new DateFormatSymbols(locale)
                            ),
                            locale,
                            1950,
                            50,
                            () -> {
                                throw new UnsupportedOperationException("now() not supported");
                            }
                        ),
                        DecimalNumberContexts.fake()
                    ),
                    ExpressionNumberKind.BIG_DECIMAL
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    // textToNumber.....................................................................................................

    @Test
    public void testTextToNumberConvertInvalidStringPatternToExpressionNumberFails() {
        this.convertFails(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .parser()
            ),
            "1",
            ExpressionNumber.class,
            this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL)
        );
    }

    @Test
    public void testTextToNumberConvertCharSequenceToExpressionNumberBigDecimalConvert() {
        this.textToNumberConvertCharSequenceToExpressionNumberAndCheck(
            ExpressionNumberKind.BIG_DECIMAL
        );
    }

    @Test
    public void testTextToNumberConvertCharSequenceToExpressionNumberDoubleConvert() {
        this.textToNumberConvertCharSequenceToExpressionNumberAndCheck(
            ExpressionNumberKind.DOUBLE
        );
    }

    private void textToNumberConvertCharSequenceToExpressionNumberAndCheck(final ExpressionNumberKind kind) {
        this.convertAndCheck(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .parser()
            ),
            new StringBuilder("1.25"),
            ExpressionNumber.class,
            this.spreadsheetConverterContext(kind),
            kind.create(1.25)
        );
    }

    @Test
    public void testTextToNumberConvertTextToExpressionNumberBigDecimalConvert() {
        this.textToNumberConvertTextToExpressionNumberAndCheck(
            ExpressionNumberKind.BIG_DECIMAL
        );
    }

    @Test
    public void testTextToNumberConvertTextToExpressionNumberDoubleConvert() {
        this.textToNumberConvertTextToExpressionNumberAndCheck(
            ExpressionNumberKind.DOUBLE
        );
    }

    private void textToNumberConvertTextToExpressionNumberAndCheck(final ExpressionNumberKind kind) {
        this.convertAndCheck(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .parser()
            ),
            "1.25",
            ExpressionNumber.class,
            this.spreadsheetConverterContext(kind),
            kind.create(1.25)
        );
    }

    @Test
    public void testTextToNumberConvertStringToInteger() {
        this.convertAndCheck(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("000")
                    .parser()
            ),
            "123",
            Integer.class,
            this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL),
            123
        );
    }

    @Test
    public void testConvertUrlToString() {
        final String url = "http://www.example.com";
        final SpreadsheetConverterContext context = this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL);

        this.convertAndCheck(
            context.converter(),
            Url.parseAbsolute(url),
            String.class,
            context,
            url
        );
    }

    private SpreadsheetConverterContext spreadsheetConverterContext(final ExpressionNumberKind kind) {
        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        DateTimeContexts.fake(), // unused only doing numbers
                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                    ),
                    kind
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    // text.............................................................................................................

    @Test
    public void testTextConvertCharacterToString() {
        this.textConvertAndCheck(
            'A',
            String.class,
            "A"
        );
    }

    @Test
    public void testTextConvertHasTextToString() {
        final String string = "Hello123";

        this.textConvertAndCheck(
            new HasText() {
                @Override
                public String text() {
                    return string;
                }
            },
            String.class,
            string
        );
    }

    @Test
    public void testTextConvertStringToCharacter() {
        this.textConvertAndCheck(
            "Z",
            Character.class,
            'Z'
        );
    }

    @Test
    public void testTextConvertStringToString() {
        this.textConvertAndCheck(
            "Hello",
            String.class,
            "Hello"
        );
    }

    private <T> void textConvertAndCheck(final Object value,
                                         final Class<T> type,
                                         final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.text(),
            value,
            type,
            SpreadsheetConverterContexts.fake(),
            expected
        );
    }


    // PublicStaticHelperTesting........................................................................................

    @Test
    public void testPublicStaticMethodsWithoutMathContextParameter() {
        this.publicStaticMethodParametersTypeCheck(MathContext.class);
    }

    @Override
    public Class<SpreadsheetConverters> type() {
        return SpreadsheetConverters.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
