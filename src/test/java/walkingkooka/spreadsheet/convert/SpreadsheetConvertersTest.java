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
import walkingkooka.collect.list.BooleanList;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringList;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.color.RgbColor;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasDateTimeSymbols;
import walkingkooka.datetime.HasOptionalDateTimeSymbols;
import walkingkooka.datetime.LocalDateList;
import walkingkooka.datetime.LocalDateTimeList;
import walkingkooka.datetime.LocalTimeList;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.HasDecimalNumberSymbols;
import walkingkooka.math.HasOptionalDecimalNumberSymbols;
import walkingkooka.math.NumberList;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.engine.collection.SpreadsheetCellSet;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.HasText;
import walkingkooka.text.LineEnding;
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
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.text.Image;
import walkingkooka.tree.text.Styleable;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.util.HasLocale;
import walkingkooka.util.HasOptionalLocale;
import walkingkooka.validation.ValidationCheckbox;
import walkingkooka.validation.ValidationChoice;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetConvertersTest implements ClassTesting2<SpreadsheetConverters>,
    PublicStaticHelperTesting<SpreadsheetConverters>,
    ConverterTesting {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    // basic............................................................................................................

    @Test
    public void testBasicConvertNullToNumber() {
        this.basicConvertAndCheck(
            null,
            Integer.class,
            null
        );
    }

    @Test
    public void testBasicConvertListNullToNumber() {
        this.basicConvertAndCheck(
            Arrays.asList((Object) null),
            Integer.class,
            0
        );
    }

    @Test
    public void testBasicConvertListIntegerToLong() {
        this.basicConvertAndCheck(
            Lists.of(123),
            123L
        );
    }

    @Test
    public void testBasicConvertSetIntegerToLong() {
        this.basicConvertAndCheck(
            Sets.of(123),
            123L
        );
    }

    @Test
    public void testBasicConvertOptionalEmptyToNumber() {
        this.basicConvertAndCheck(
            Optional.empty(),
            Integer.class,
            0
        );
    }

    @Test
    public void testBasicConvertOptionalIntegerToLong() {
        this.basicConvertAndCheck(
            Optional.of(123),
            123L
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
        this.convertAndCheck(
            SpreadsheetConverters.basic(),
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
                public <TT> Either<TT, String> convert(final Object value,
                                                       final Class<TT> target) {
                    return this.converter.convert(
                        value,
                        target,
                        this
                    );
                }

                private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                    Lists.of(
                        SpreadsheetConverters.nullToNumber(),
                        SpreadsheetConverters.numberToNumber(),
                        SpreadsheetConverters.text()
                    )
                );

                @Override
                public ExpressionNumberKind expressionNumberKind() {
                    return EXPRESSION_NUMBER_KIND;
                }
            },
            expected
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testBooleanConvertBooleanTrueToString() {
        this.booleanConvertAndCheck(
            Boolean.TRUE,
            SpreadsheetStrings.BOOLEAN_TRUE
        );
    }

    @Test
    public void testBooleanConvertBooleanFalseToString() {
        this.booleanConvertAndCheck(
            Boolean.FALSE,
            SpreadsheetStrings.BOOLEAN_FALSE
        );
    }

    @Test
    public void testBooleanConvertStringTrueCapitalisedToBoolean() {
        this.booleanConvertAndCheck(
            "True",
            Boolean.TRUE
        );
    }

    @Test
    public void testBooleanConvertStringFalseCapitalisedToBoolean() {
        this.booleanConvertAndCheck(
            "False",
            Boolean.FALSE
        );
    }

    @Test
    public void testBooleanConvertStringTrueUppercaseToBoolean() {
        this.booleanConvertAndCheck(
            "TRUE",
            Boolean.TRUE
        );
    }

    @Test
    public void testBooleanConvertStringFalseUppercaseToBoolean() {
        this.booleanConvertAndCheck(
            "FALSE",
            Boolean.FALSE
        );
    }

    @Test
    public void testBooleanConvertStringTrueLowercaseToBoolean() {
        this.booleanConvertAndCheck(
            "false",
            Boolean.FALSE
        );
    }

    private void booleanConvertAndCheck(final Object value,
                                        final Object expected) {
        this.booleanConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void booleanConvertAndCheck(final Object value,
                                            final Class<T> type,
                                            final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.booleans(),
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
                public <TT> Either<TT, String> convert(final Object value,
                                                       final Class<TT> target) {
                    return this.converter.convert(
                        value,
                        target,
                        this
                    );
                }

                private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.text();
            },
            expected
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
                public <TT> Either<TT, String> convert(final Object value,
                                                       final Class<TT> target) {
                    return this.converter.convert(
                        value,
                        target,
                        this
                    );
                }

                private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                    Lists.of(
                        SpreadsheetConverters.basic(),
                        SpreadsheetConverters.text(),
                        SpreadsheetConverters.numberToNumber()//,
                        //SpreadsheetConverters.color()
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

    // dateTimeSymbols..................................................................................................

    @Test
    public void testDateTimeSymbolsConvertStringToDateTimeSymbols() {
        this.convertAndCheck(
            SpreadsheetConverters.dateTimeSymbols(),
            DATE_TIME_SYMBOLS_CONTEXT.locale()
                .toLanguageTag(),
            DateTimeSymbols.class,
            DATE_TIME_SYMBOLS_CONTEXT,
            DATE_TIME_SYMBOLS_CONTEXT.dateTimeSymbolsForLocale(DATE_TIME_SYMBOLS_CONTEXT.locale())
                .get()
        );
    }

    private final static SpreadsheetConverterContext DATE_TIME_SYMBOLS_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.text(),
                Converters.toLocale()
            )
        );

        @Override
        public Locale locale() {
            return Locale.forLanguageTag("en-AU");
        }

        @Override
        public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
            return LocaleContexts.jre(locale)
                .dateTimeSymbolsForLocale(locale);
        }
    };

    // decimalNumberSymbols.............................................................................................

    @Test
    public void testDecimalNumberSymbolsConvertStringToDecimalNumberSymbols() {
        this.convertAndCheck(
            SpreadsheetConverters.decimalNumberSymbols(),
            DECIMAL_NUMBER_SYMBOLS_CONTEXT.locale()
                .toLanguageTag(),
            DecimalNumberSymbols.class,
            DECIMAL_NUMBER_SYMBOLS_CONTEXT,
            DECIMAL_NUMBER_SYMBOLS_CONTEXT.decimalNumberSymbolsForLocale(DECIMAL_NUMBER_SYMBOLS_CONTEXT.locale())
                .get()
        );
    }

    private final static SpreadsheetConverterContext DECIMAL_NUMBER_SYMBOLS_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.text(),
                Converters.toLocale()
            )
        );

        @Override
        public Locale locale() {
            return Locale.forLanguageTag("en-AU");
        }

        @Override
        public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
            return LocaleContexts.jre(locale)
                .decimalNumberSymbolsForLocale(locale);
        }
    };

    // environmentSymbols...............................................................................................

    @Test
    public void testEnvironmentSymbolsConvertEnvironmentValueNameToEnvironmentValueName() {
        final EnvironmentValueName<?> name = EnvironmentValueName.with(
            "CurrentPath",
            String.class
        );

        this.environmentConvertAndCheck(
            name,
            EnvironmentValueName.class,
            name
        );
    }

    @Test
    public void testEnvironmentSymbolsConvertStringToEnvironmentValueName() {
        final EnvironmentValueName<?> name = EnvironmentValueName.with(
            "CurrentPath",
            String.class
        );

        this.environmentConvertAndCheck(
            name.value(),
            EnvironmentValueName.class,
            name
        );
    }

    private <T> void environmentConvertAndCheck(final Object value,
                                                final Class<T> targetType,
                                                final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.environment(),
            value,
            targetType,
            ENVIRONMENT_SYMBOLS_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext ENVIRONMENT_SYMBOLS_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.text()
            )
        );
    };

    // expression.......................................................................................................

    private final Expression EXPRESSION = Expression.add(
        Expression.value(
            EXPRESSION_NUMBER_KIND.one()
        ),
        Expression.value(
            EXPRESSION_NUMBER_KIND.create(2)
        )
    );

    @Test
    public void testExpressionConvertStringToTemplateValueNameFails() {
        this.convertFails(
            SpreadsheetConverters.expression(),
            "Template123",
            TemplateValueName.class,
            EXPRESSION_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testExpressionConvertStringToExpression() {
        this.expressionConvertAndCheck(
            "1+2",
            Expression.class,
            EXPRESSION
        );
    }

    private void expressionConvertAndCheck(final Object value,
                                           final Object expected) {
        this.expressionConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void expressionConvertAndCheck(final Object value,
                                               final Class<T> type,
                                               final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.expression(),
            value,
            type,
            EXPRESSION_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext EXPRESSION_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {

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
                SpreadsheetConverters.text()
            )
        );

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

        @Override
        public char valueSeparator() {
            return ',';
        }

        private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.DECIMAL32);
    };

    // formAndValidation................................................................................................

    @Test
    public void testFormAndValidationConvertStringToSpreadsheetErrorFails() {
        this.convertFails(
            SpreadsheetConverters.formAndValidation(),
            SpreadsheetErrorKind.DIV0.setMessage("Divide by zero 123")
                .toString(),
            SpreadsheetError.class,
            FORM_AND_VALIDATION_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testFormAndValidationConvertStringToValidatorSelectorFails() {
        final ValidatorSelector selector = ValidatorSelector.parse("validator-selector-123");

        this.convertFails(
            SpreadsheetConverters.formAndValidation(),
            selector.toString(),
            ValidatorSelector.class,
            FORM_AND_VALIDATION_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testFormAndValidationConvertStringToFormName() {
        final String formName = "FormName123";

        this.formAndValidationConvertAndCheck(
            formName,
            FormName.with(formName)
        );
    }

    @Test
    public void testFormAndValidationConvertEmptyStringToValidationCheckbox() {
        this.formAndValidationConvertAndCheck(
            "",
            ValidationCheckbox.TRUE_FALSE
        );
    }

    @Test
    public void testFormAndValidationConvertStringToValidationCheckbox() {
        this.formAndValidationConvertAndCheck(
            "111, 222",
            ValidationCheckbox.with(
                Optional.of("111"),
                Optional.of("222")
            )
        );
    }


    @Test
    public void testFormAndValidationConvertListToValidationCheckbox() {
        this.formAndValidationConvertAndCheck(
            Lists.of(
                333,
                444
            ),
            ValidationCheckbox.with(
                Optional.of(333),
                Optional.of(444)
            )
        );
    }

    @Test
    public void testFormAndValidationConvertNumberToValidationChoice() {
        final ExpressionNumber value = EXPRESSION_NUMBER_KIND.create(123);

        this.formAndValidationConvertAndCheck(
            value,
            ValidationChoice.with(
                "123",
                Optional.ofNullable(value)
            )
        );
    }

    @Test
    public void testFormAndValidationConvertStringToValidationChoice() {
        final String value = "Value1";

        this.formAndValidationConvertAndCheck(
            value,
            ValidationChoice.with(
                value,
                Optional.ofNullable(value)
            )
        );
    }

    @Test
    public void testFormAndValidationConvertListOfStringToValidationChoiceList() {
        final String value = "Value1";

        this.formAndValidationConvertAndCheck(
            Lists.of(value),
            ValidationChoiceList.EMPTY.concat(
                ValidationChoice.with(
                    value,
                    Optional.ofNullable(value)
                )
            )
        );
    }

    @Test
    public void testFormAndValidationConvertStringToValidationError() {
        final ValidationError<SpreadsheetExpressionReference> error = SpreadsheetErrorKind.DIV0.setMessage("Divide by zero 123")
            .toValidationError(SpreadsheetSelection.A1);

        this.formAndValidationConvertAndCheck(
            error.text(),
            error
        );
    }

    @Test
    public void testFormAndValidationConvertStringToTextFormAndValidationPropertyName() {
        final ValidationError<SpreadsheetExpressionReference> error = SpreadsheetErrorKind.DIV0.setMessage("Divide by zero 123")
            .toValidationError(SpreadsheetSelection.A1);

        this.formAndValidationConvertAndCheck(
            error,
            ValidationErrorList.<SpreadsheetExpressionReference>empty()
                .concat(error)
        );
    }

    private void formAndValidationConvertAndCheck(final Object value,
                                                  final Object expected) {
        this.formAndValidationConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void formAndValidationConvertAndCheck(final Object value,
                                                      final Class<T> type,
                                                      final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.formAndValidation(),
            value,
            type,
            FORM_AND_VALIDATION_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext FORM_AND_VALIDATION_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.basic(),
                SpreadsheetConverters.text(),
                Converters.objectToString(),
                SpreadsheetConverters.textToCsvStringList(),
                SpreadsheetConverters.toValidationChoice()
            )
        );

        @Override
        public SpreadsheetExpressionReference validationReference() {
            return SpreadsheetSelection.A1;
        }

        @Override
        public char valueSeparator() {
            return ',';
        }
    };

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
            (String cc) -> Optional.ofNullable(
                Currency.getInstance(cc)
            ),
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

    @Test
    public void testJsonConvertStringWithJsonToSpreadsheetCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+2")
        ).setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.COLOR,
                Color.BLACK
            )
        );

        this.jsonConvertAndCheck(
            JsonNodeMarshallContexts.basic()
                .marshall(cell)
                .toString(),
            cell
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
                SpreadsheetConverters.basic(),
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

    private final static Locale LOCALE = Locale.FRANCE;

    private final static DateTimeSymbols DATE_TIME_SYMBOLS = DateTimeSymbols.fromDateFormatSymbols(
        new DateFormatSymbols(LOCALE)
    );

    @Test
    public void testLocaleConvertLocaleToDateTimeSymbols() {
        this.localeConvertAndCheck(
            LOCALE,
            DATE_TIME_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasDateTimeSymbolsToDateTimeSymbols() {
        this.localeConvertAndCheck(
            new HasDateTimeSymbols() {
                @Override
                public DateTimeSymbols dateTimeSymbols() {
                    return DATE_TIME_SYMBOLS;
                }
            },
            DATE_TIME_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasOptionalDateTimeSymbolsToDateTimeSymbols() {
        this.localeConvertAndCheck(
            new HasOptionalDateTimeSymbols() {
                @Override
                public Optional<DateTimeSymbols> dateTimeSymbols() {
                    return Optional.of(DATE_TIME_SYMBOLS);
                }
            },
            DATE_TIME_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasLocaleToDateTimeSymbols() {
        this.localeConvertAndCheck(
            new HasLocale() {
                @Override
                public Locale locale() {
                    return LOCALE;
                }
            },
            DATE_TIME_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasOptionalLocaleToDateTimeSymbols() {
        this.localeConvertAndCheck(
            new HasOptionalLocale() {
                @Override
                public Optional<Locale> locale() {
                    return Optional.of(LOCALE);
                }
            },
            DATE_TIME_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertStringToDateTimeSymbols() {
        this.localeConvertAndCheck(
            LOCALE.toLanguageTag(),
            DATE_TIME_SYMBOLS
        );
    }


    private final static DecimalNumberSymbols DECIMAL_NUMBER_SYMBOLS = DecimalNumberSymbols.fromDecimalFormatSymbols(
        '+',
        new DecimalFormatSymbols(LOCALE)
    );

    @Test
    public void testLocaleConvertLocaleToDecimalNumberSymbols() {
        this.localeConvertAndCheck(
            LOCALE,
            DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasDecimalNumberSymbolsToDecimalNumberSymbols() {
        this.localeConvertAndCheck(
            new HasDecimalNumberSymbols() {
                @Override
                public DecimalNumberSymbols decimalNumberSymbols() {
                    return DECIMAL_NUMBER_SYMBOLS;
                }
            },
            DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasOptionalDecimalNumberSymbolsToDecimalNumberSymbols() {
        this.localeConvertAndCheck(
            new HasOptionalDecimalNumberSymbols() {
                @Override
                public Optional<DecimalNumberSymbols> decimalNumberSymbols() {
                    return Optional.of(DECIMAL_NUMBER_SYMBOLS);
                }
            },
            DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasLocaleToDecimalNumberSymbols() {
        this.localeConvertAndCheck(
            new HasLocale() {
                @Override
                public Locale locale() {
                    return LOCALE;
                }
            },
            DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertHasOptionalLocaleToDecimalNumberSymbols() {
        this.localeConvertAndCheck(
            new HasOptionalLocale() {
                @Override
                public Optional<Locale> locale() {
                    return Optional.of(LOCALE);
                }
            },
            DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertStringToDecimalNumberSymbols() {
        this.localeConvertAndCheck(
            LOCALE.toLanguageTag(),
            DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testLocaleConvertLocaleToLocale() {
        this.localeConvertAndCheck(
            LOCALE,
            LOCALE
        );
    }

    @Test
    public void testLocaleConvertHasLocaleToLocale() {
        this.localeConvertAndCheck(
            new HasLocale() {
                @Override
                public Locale locale() {
                    return LOCALE;
                }
            },
            LOCALE
        );
    }

    @Test
    public void testLocaleConvertHasOptionalLocaleToLocale() {
        this.localeConvertAndCheck(
            new HasOptionalLocale() {
                @Override
                public Optional<Locale> locale() {
                    return Optional.of(LOCALE);
                }
            },
            LOCALE
        );
    }

    @Test
    public void testLocaleConvertLocaleToString() {
        this.localeConvertAndCheck(
            LOCALE,
            LOCALE.toLanguageTag()
        );
    }

    @Test
    public void testLocaleConvertStringToLocale() {
        this.localeConvertAndCheck(
            LOCALE.toLanguageTag(),
            LOCALE
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

        private final Converter<SpreadsheetConverterContext> converter = Converters.collection(
            Lists.of(
                SpreadsheetConverters.text(),
                SpreadsheetConverters.locale()
            )
        );

        @Override
        public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
            return this.localeContext.dateTimeSymbolsForLocale(locale);
        }

        @Override
        public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
            return this.localeContext.decimalNumberSymbolsForLocale(locale);
        }

        private final LocaleContext localeContext = LocaleContexts.jre(LOCALE);
    };

    // number...........................................................................................................

    @Test
    public void testNumberConvertNullToNumber() {
        this.numberConvertAndCheck(
            null,
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testNumberConvertBooleanTrueToInteger() {
        this.numberConvertAndCheck(
            true,
            1
        );
    }

    @Test
    public void testNumberConvertBooleanTrueToNumber() {
        this.numberConvertAndCheck(
            true,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testNumberConvertBooleanFalseToNumber() {
        this.numberConvertAndCheck(
            false,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testNumberConvertNumberToStringWithInteger() {
        this.numberConvertAndCheck(
            123,
            "123"
        );
    }

    @Test
    public void testNumberConvertNumberToStringWithExpressionNumber() {
        this.numberConvertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            "123"
        );
    }

    @Test
    public void testNumberConvertStringTrueToBoolean() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Boolean.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToByte() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Byte.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToShort() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Short.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToInteger() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Integer.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToLong() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Long.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToFloat() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Float.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToDouble() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            Double.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToBigInteger() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            BigInteger.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToBigDecimal() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            BigDecimal.class
        );
    }

    @Test
    public void testNumberConvertStringTrueToExpressionNumber() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_TRUE,
            ExpressionNumber.class
        );
    }

    @Test
    public void testNumberConvertStringFalseToBoolean() {
        this.numberConvertAndCheckFails(
            SpreadsheetStrings.BOOLEAN_FALSE,
            Boolean.class
        );
    }

    @Test
    public void testNumberConvertStringToByte() {
        this.numberConvertAndCheck(
            "123",
            (byte) 123
        );
    }

    @Test
    public void testNumberConvertStringToShort() {
        this.numberConvertAndCheck(
            "123",
            (short) 123
        );
    }

    @Test
    public void testNumberConvertStringToInteger() {
        this.numberConvertAndCheck(
            "123",
            123
        );
    }

    @Test
    public void testNumberConvertStringToLong() {
        this.numberConvertAndCheck(
            "123",
            123L
        );
    }

    @Test
    public void testNumberConvertStringToFloat() {
        this.numberConvertAndCheck(
            "123.5",
            123.5f
        );
    }

    @Test
    public void testNumberConvertStringToDouble() {
        this.numberConvertAndCheck(
            "123.5",
            123.5
        );
    }

    @Test
    public void testNumberConvertStringToBigInteger() {
        this.numberConvertAndCheck(
            "123",
            BigInteger.valueOf(123)
        );
    }

    @Test
    public void testNumberConvertStringToBigDecimal() {
        this.numberConvertAndCheck(
            "123.5",
            BigDecimal.valueOf(123.5)
        );
    }

    @Test
    public void testNumberConvertStringToExpressionNumberWithWholeNumber() {
        this.numberConvertAndCheck(
            "123",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testNumberConvertStringToExpressionNumberWithDecimalPoints() {
        this.numberConvertAndCheck(
            "456.75",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(456.75)
        );
    }

    private void numberConvertAndCheckFails(final Object value,
                                            final Class<?> type) {
        this.convertFails(
            SpreadsheetConverters.number(),
            value,
            type,
            NUMBER_CONVERTER_CONTEXT
        );
    }


    private void numberConvertAndCheck(final Object value,
                                       final Object expected) {
        this.numberConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void numberConvertAndCheck(final Object value,
                                           final Class<T> type,
                                           final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.number(),
            value,
            type,
            NUMBER_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext NUMBER_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {

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
                SpreadsheetConverters.text(),
                SpreadsheetConverters.numberToNumber()
            )
        );

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public boolean canNumbersHaveGroupSeparator() {
            return false;
        }

        @Override
        public char valueSeparator() {
            return ',';
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
        public int decimalNumberDigitCount() {
            return this.decimalNumberContext.decimalNumberDigitCount();
        }

        @Override
        public MathContext mathContext() {
            return this.decimalNumberContext.mathContext();
        }

        private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.DECIMAL32);

        @Override
        public SpreadsheetMetadata spreadsheetMetadata() {
            return SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT,
                DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
            );
        }
    };

    // spreadsheetMetadata..............................................................................................

    @Test
    public void testSpreadsheetMetadataConvertStringToSpreadsheetCellReferenceFails() {
        this.convertFails(
            SpreadsheetConverters.spreadsheetMetadata(),
            SpreadsheetSelection.A1.text(),
            SpreadsheetCellReference.class,
            SPREADSHEET_METADATA_CONVERTER_CONTEXT
        );
    }

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SpreadsheetId.with(1)
    );

    @Test
    public void testSpreadsheetMetadataConvertStringToSpreadsheetId() {
        final SpreadsheetId id = SpreadsheetId.with(12345);

        this.spreadsheetMetadataConvertAndCheck(
            id.toString(),
            id
        );
    }

    @Test
    public void testSpreadsheetMetadataConvertStringToSpreadsheetMetadata() {
        this.spreadsheetMetadataConvertAndCheck(
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.marshall(SPREADSHEET_METADATA)
                .toString(),
            SPREADSHEET_METADATA
        );
    }

    @Test
    public void testSpreadsheetMetadataConvertStringToSpreadsheetMetadataPropertyName() {
        final SpreadsheetMetadataPropertyName<?> name = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;

        this.spreadsheetMetadataConvertAndCheck(
            name.toString(),
            name
        );
    }

    @Test
    public void testSpreadsheetMetadataConvertStringToSpreadsheetName() {
        final SpreadsheetName name = SpreadsheetName.with("SpreadsheetName222");

        this.spreadsheetMetadataConvertAndCheck(
            name.toString(),
            name
        );
    }

    private void spreadsheetMetadataConvertAndCheck(final Object value,
                                                    final Object expected) {
        this.spreadsheetMetadataConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void spreadsheetMetadataConvertAndCheck(final Object value,
                                                        final Class<T> type,
                                                        final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.spreadsheetMetadata(),
            value,
            type,
            SPREADSHEET_METADATA_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext SPREADSHEET_METADATA_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.basic(),
                SpreadsheetConverters.text(),
                SpreadsheetConverters.json()
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

    // spreadsheetValue.................................................................................................

    @Test
    public void testSpreadsheetValueConvertNullToNumber() {
        this.spreadsheetValueConvertAndCheck(
            null,
            Number.class,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellReferenceToSpreadsheetCellReference() {
        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellReferenceToSpreadsheetCellRangeReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.spreadsheetValueConvertAndCheck(
            cell,
            cell.toRange()
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellReferenceToSpreadsheetColumnReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.spreadsheetValueConvertAndCheck(
            cell,
            cell.toColumn()
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellReferenceToSpreadsheetRowReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.spreadsheetValueConvertAndCheck(
            cell,
            cell.toRow()
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetErrorToSpreadsheetError() {
        final SpreadsheetError error = SpreadsheetErrorKind.DIV0.setMessage("Divide by zero is not allowed 123");

        this.spreadsheetValueConvertAndCheck(
            error,
            SpreadsheetError.class,
            error
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetSelectionFails() {
        this.convertFails(
            SpreadsheetConverters.spreadsheetValue(),
            SpreadsheetSelection.A1.text(),
            SpreadsheetSelection.class,
            SPREADSHEET_VALUE_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetCellReference() {
        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.A1.text(),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetCellRangeReference() {
        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.A1.text(),
            SpreadsheetSelection.A1.toRange()
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetCellRangeReferenceWithColumn() {
        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.parseColumn("B"),
            SpreadsheetSelection.parseColumnRange("B")
                .setRowRange(SpreadsheetSelection.ALL_ROWS)
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetCellRangeReferenceWithRow() {
        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.parseRow("3"),
            SpreadsheetSelection.parseRowRange("3")
                .setColumnRange(SpreadsheetSelection.ALL_COLUMNS)
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetColumnReference() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");

        this.spreadsheetValueConvertAndCheck(
            column.toString(),
            column
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetColumnRangeReference() {
        final SpreadsheetColumnRangeReference column = SpreadsheetSelection.parseColumnRange("C:D");

        this.spreadsheetValueConvertAndCheck(
            column.toString(),
            column
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetLabelName() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.spreadsheetValueConvertAndCheck(
            label.toString(),
            label
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetLabelNameWithRowFails() {
        this.convertFails(
            SpreadsheetConverters.spreadsheetValue(),
            "2",
            SpreadsheetLabelName.class,
            SPREADSHEET_VALUE_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetRowReference() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("5");

        this.spreadsheetValueConvertAndCheck(
            row.toString(),
            row
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetRowRangeReference() {
        final SpreadsheetRowRangeReference row = SpreadsheetSelection.parseRowRange("6:77");

        this.spreadsheetValueConvertAndCheck(
            row.toString(),
            row
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToSpreadsheetError() {
        final SpreadsheetError error = SpreadsheetErrorKind.VALUE.setMessage("Value error 123");

        this.spreadsheetValueConvertAndCheck(
            error.toString(),
            error
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellReferenceToString() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.spreadsheetValueConvertAndCheck(
            cell,
            "A1"
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellReferenceToString2() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1.toAbsolute();

        this.spreadsheetValueConvertAndCheck(
            cell,
            "$A$1"
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellRangeReferenceToString() {
        final String text = "B2:C3";

        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.parseCellRange(text),
            text
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellToSpreadsheetFormatterSelector() {
        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.parse("hello-formatter");

        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(
                    Optional.of(formatter)
                ),
            formatter
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellToSpreadsheetParserSelector() {
        final SpreadsheetParserSelector parser = SpreadsheetParserSelector.parse("hello-parser");

        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setParser(
                    Optional.of(parser)
                ),
            parser
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellToValidatorSelector() {
        final ValidatorSelector validator = ValidatorSelector.parse("hello-validator");

        this.spreadsheetValueConvertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setValidator(
                    Optional.of(validator)
                ),
            validator
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellToSpreadsheetCellSet() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+2")
        );

        this.spreadsheetValueConvertAndCheck(
            cell,
            SpreadsheetCellSet.class,
            SpreadsheetCellSet.EMPTY.concat(cell)
        );
    }

    @Test
    public void testSpreadsheetValueConvertSpreadsheetCellToSpreadsheetCellReference() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+2")
        );

        this.spreadsheetValueConvertAndCheck(
            cell,
            SpreadsheetCellReference.class,
            cell.reference()
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToValueType() {
        final ValueType type = SpreadsheetValueType.NUMBER;

        this.spreadsheetValueConvertAndCheck(
            type.toString(),
            type
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfBooleanToBooleanList() {
        final List<Boolean> booleans = Lists.of(
            true,
            false,
            true,
            null
        );

        this.spreadsheetValueConvertAndCheck(
            booleans,
            BooleanList.EMPTY.setElements(booleans)
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfStringsToCsvStringList() {
        final List<String> strings = Lists.of(
            "Apple",
            "Banana",
            "333"
        );

        this.spreadsheetValueConvertAndCheck(
            strings,
            CsvStringList.EMPTY.setElements(strings)
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfDateToLocalDateList() {
        final List<LocalDate> dates = Lists.of(
            LocalDate.of(
                1999,
                12,
                31
            ),
            LocalDate.of(
                2000,
                2,
                2
            ),
            null
        );

        this.spreadsheetValueConvertAndCheck(
            dates,
            LocalDateList.EMPTY.setElements(dates)
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfDateTimeToLocalDateTimeList() {
        final List<LocalDateTime> datesTimes = Lists.of(
            LocalDateTime.of(
                1999,
                12,
                31,
                12,
                0,
                0
            ),
            LocalDateTime.of(
                2000,
                2,
                2,
                2,
                22,
                22
            ),
            null
        );

        this.spreadsheetValueConvertAndCheck(
            datesTimes,
            LocalDateTimeList.EMPTY.setElements(datesTimes)
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfTimeToLocalTimeList() {
        final List<LocalTime> times = Lists.of(
            LocalTime.of(
                12,
                58,
                59
            ),
            LocalTime.of(
                2,
                22,
                22
            ),
            null
        );

        this.spreadsheetValueConvertAndCheck(
            times,
            LocalTimeList.EMPTY.setElements(times)
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfNumbersToNumberList() {
        final List<Number> numbers = Lists.of(
            EXPRESSION_NUMBER_KIND.create(1),
            EXPRESSION_NUMBER_KIND.create(22),
            EXPRESSION_NUMBER_KIND.create(333.5),
            null
        );

        this.spreadsheetValueConvertAndCheck(
            numbers,
            NumberList.EMPTY.setElements(numbers)
        );
    }

    @Test
    public void testSpreadsheetValueConvertListOfStringsToStringList() {
        final List<String> strings = Lists.of(
            "Apple",
            "Banana",
            null
        );

        this.spreadsheetValueConvertAndCheck(
            strings,
            StringList.EMPTY.setElements(strings)
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToBooleanList() {
        this.spreadsheetValueConvertAndCheck(
            "TRUE, FALSE, true",
            BooleanList.EMPTY.setElements(
                Lists.of(
                    true,
                    false,
                    true
                )
            )
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToCsvStringList() {
        this.spreadsheetValueConvertAndCheck(
            "Apple, Banana, \"333 444\"",
            CsvStringList.EMPTY.setElements(
                Lists.of(
                    "Apple",
                    "Banana",
                    "333 444"
                )
            )
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToLocalDateList() {
        this.spreadsheetValueConvertAndCheck(
            "1999/12/31, 2000/2/2",
            LocalDateList.EMPTY.setElements(
                Lists.of(
                    LocalDate.of(
                        1999,
                        12,
                        31
                    ),
                    LocalDate.of(
                        2000,
                        2,
                        2
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToLocalDateTimeList() {
        this.spreadsheetValueConvertAndCheck(
            "1999/12/31 12:0:0, 2000/2/2 2:22:22",
            LocalDateTimeList.EMPTY.setElements(
                Lists.of(
                    LocalDateTime.of(
                        1999,
                        12,
                        31,
                        12,
                        0,
                        0
                    ),
                    LocalDateTime.of(
                        2000,
                        2,
                        2,
                        2,
                        22,
                        22
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToLocalTimeList() {
        this.spreadsheetValueConvertAndCheck(
            "12:58:59,2:22:22",
            LocalTimeList.EMPTY.setElements(
                Lists.of(
                    LocalTime.of(
                        12,
                        58,
                        59
                    ),
                    LocalTime.of(
                        2,
                        22,
                        22
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToNumberList() {
        this.spreadsheetValueConvertAndCheck(
            "1,22,333.5",
            NumberList.EMPTY.setElements(
                Lists.of(
                    EXPRESSION_NUMBER_KIND.create(1),
                    EXPRESSION_NUMBER_KIND.create(22),
                    EXPRESSION_NUMBER_KIND.create(333.5)
                )
            )
        );
    }

    @Test
    public void testSpreadsheetValueConvertStringToStringList() {
        this.spreadsheetValueConvertAndCheck(
            "Apple, Banana, Carrot ",
            StringList.EMPTY.setElements(
                Lists.of(
                    "Apple",
                    "Banana",
                    "Carrot"
                )
            )
        );
    }

    private void spreadsheetValueConvertAndCheck(final Object value,
                                                 final Object expected) {
        this.spreadsheetValueConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void spreadsheetValueConvertAndCheck(final Object value,
                                                     final Class<T> type,
                                                     final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.spreadsheetValue(),
            value,
            type,
            SPREADSHEET_VALUE_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext SPREADSHEET_VALUE_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.basic(),
                SpreadsheetConverters.text(),
                SpreadsheetConverters.numberToNumber(),
                SpreadsheetConverters.toBoolean(),
                SpreadsheetConverters.textToNumber(
                    SpreadsheetPattern.parseNumberParsePattern("#.##;#")
                        .parser()
                ),
                SpreadsheetConverters.textToDate(
                    SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                        .parser()
                ),
                SpreadsheetConverters.textToDateTime(
                    SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm:ss")
                        .parser()
                ),
                SpreadsheetConverters.textToTime(
                    SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                        .parser()
                )
            )
        );

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public boolean canNumbersHaveGroupSeparator() {
            return false;
        }

        @Override
        public char valueSeparator() {
            return ',';
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

        @Override
        public long dateOffset() {
            return Converters.EXCEL_1900_DATE_SYSTEM_OFFSET;
        }

        private final DateTimeContext dateTimeContext = DateTimeContexts.basic(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(LOCALE)
            ),
            LOCALE,
            1980, // defaultYear
            50, // twoDigitYear,
            () -> {
                throw new UnsupportedOperationException();
            }
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
    public void testStyleConvertHasStyle() {
        this.styleConvertAndCheck(
            STYLE,
            STYLE
        );
    }

    @Test
    public void testStyleConvertHasTextStyleToStyleable() {
        this.styleConvertAndCheck(
            STYLE,
            Styleable.class,
            STYLE
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
    public void testStyleConvertStringToStyleable() {
        this.styleConvertAndCheck(
            STYLE.text(),
            Styleable.class,
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
            STYLE_CONVERTER_CONTEXT.converter(),
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

        @Override
        public Converter<SpreadsheetConverterContext> converter() {
            return this.converter;
        }

        private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
            Lists.of(
                SpreadsheetConverters.basic(),
                SpreadsheetConverters.text(),
                SpreadsheetConverters.style()
            )
        );
    };

    // system...........................................................................................................

    @Test
    public void testSystemConvertStringTrueToBoolean() {
        this.systemConvertAndCheck(
            "true",
            true
        );
    }

    @Test
    public void testSystemConvertStringFalseToBoolean() {
        this.systemConvertAndCheck(
            "false",
            false
        );
    }

    @Test
    public void testSystemConvertBooleanTrueToString() {
        this.systemConvertAndCheck(
            true,
            SpreadsheetStrings.BOOLEAN_TRUE
        );
    }

    @Test
    public void testSystemConvertBooleanFalseToString() {
        this.systemConvertAndCheck(
            false,
            SpreadsheetStrings.BOOLEAN_FALSE
        );
    }

    @Test
    public void testSystemConvertStringToDate() {
        this.systemConvertAndCheck(
            "1999/12/31",
            LocalDate.of(
                1999,
                12,
                31
            )
        );
    }

    @Test
    public void testSystemConvertDateToString() {
        this.systemConvertAndCheck(
            LocalDate.of(
                1999,
                12,
                31
            ),
            "1999/12/31"
        );
    }

    @Test
    public void testSystemConvertStringToDateTime() {
        this.systemConvertAndCheck(
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
    public void testSystemConvertDateTimeToString() {
        this.systemConvertAndCheck(
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
    public void testSystemConvertStringToNumberInteger() {
        this.systemConvertAndCheck(
            "123",
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testSystemConvertNumberIntegerToString() {
        this.systemConvertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            "123"
        );
    }

    @Test
    public void testSystemConvertStringToNumberDecimal() {
        this.systemConvertAndCheck(
            "45.75",
            EXPRESSION_NUMBER_KIND.create(45.75)
        );
    }

    @Test
    public void testSystemConvertNumberDecimalToString() {
        this.systemConvertAndCheck(
            EXPRESSION_NUMBER_KIND.create(45.75),
            "45.75"
        );
    }

    @Test
    public void testSystemConvertStringToString() {
        final String text = "Hello World 123";

        this.systemConvertAndCheck(
            text,
            text
        );
    }

    @Test
    public void testSystemConvertStringToTime() {
        this.systemConvertAndCheck(
            "12:58:59",
            LocalTime.of(
                12,
                58,
                59
            )
        );
    }

    @Test
    public void testSystemConvertTimeToString() {
        this.systemConvertAndCheck(
            LocalTime.of(
                12,
                58,
                59
            ),
            "12:58:59"
        );
    }

    @Test
    public void testSystemConvertNumberToRgbColor() {
        final Integer number = 0x123456;

        this.systemConvertAndCheck(
            number,
            RgbColor.class,
            RgbColor.fromRgb(number)
        );
    }

    @Test
    public void testSystemConvertStringToRgbColor() {
        final String text = "#123";

        this.systemConvertAndCheck(
            text,
            RgbColor.class,
            RgbColor.parseRgb(text)
        );
    }

    @Test
    public void testSystemConvertStringToExpression() {
        this.systemConvertAndCheck(
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
    public void testSystemConvertStringToTextStyle() {
        final String text = "{color: #123}";

        this.systemConvertAndCheck(
            text,
            TextStyle.class,
            TextStyle.parse(text)
        );
    }

    private void systemConvertAndCheck(final Object value,
                                       final Object expected) {
        this.systemConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void systemConvertAndCheck(final Object value,
                                           final Class<T> type,
                                           final T expected) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.system();

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
                public <TT> Either<TT, String> convert(final Object v,
                                                       final Class<TT> t) {
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
                public boolean canNumbersHaveGroupSeparator() {
                    return false;
                }

                @Override
                public char valueSeparator() {
                    return ',';
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
                public int decimalNumberDigitCount() {
                    return this.decimalNumberContext.decimalNumberDigitCount();
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

                @Override
                public long dateOffset() {
                    return Converters.EXCEL_1900_DATE_SYSTEM_OFFSET;
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

                @Override
                public SpreadsheetMetadata spreadsheetMetadata() {
                    return SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT,
                        DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
                    );
                }

                @Override
                public <TT> TT unmarshall(final JsonNode json,
                                          final Class<TT> type) {
                    return this.jsonNodeUnmarshallContext.unmarshall(
                        json,
                        type
                    );
                }

                @Override
                public Optional<JsonString> typeName(final Class<?> type) {
                    return this.jsonNodeUnmarshallContext.typeName(type);
                }

                private final JsonNodeUnmarshallContext jsonNodeUnmarshallContext = JsonNodeUnmarshallContexts.basic(
                    (String cc) -> Optional.ofNullable(
                        Currency.getInstance(cc)
                    ),
                    ExpressionNumberKind.BIG_DECIMAL,
                    MathContext.DECIMAL32
                );
            },
            Cast.to(expected)
        );
    }

    // textNode.........................................................................................................

    private final static String TEXT = "Hello World 123!";

    @Test
    public void testTextNodeConvertStringToColorFails() {
        this.convertFails(
            SpreadsheetConverters.textNode(),
            Color.BLACK.value(),
            Color.class,
            TEXT_NODE_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testTextNodeConvertStringToSpreadsheetText() {
        this.textNodeConvertAndCheck(
            TEXT,
            SpreadsheetText.with(TEXT)
        );
    }

    @Test
    public void testTextNodeConvertStringToTextNode() {
        this.textNodeConvertAndCheck(
            TEXT,
            TextNode.text(TEXT)
        );
    }

    @Test
    public void testTextNodeConvertSpreadsheetCellToTextNode() {
        final TextNode textNode = TextNode.text(TEXT)
            .setTextStyle(STYLE);

        this.textNodeConvertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormattedValue(
                    Optional.of(textNode)
                ),
            textNode
        );
    }

    @Test
    public void testTextNodeConvertUrlToTextNode() {
        final Url url = Url.parseAbsolute("https://www.google.com");

        this.textNodeConvertAndCheck(
            url,
            TextNode.class,
            TextNode.text(url.value())
        );
    }

    @Test
    public void testTextNodeConvertUrlToHyperlink() {
        final Url url = Url.parseAbsolute("https://www.google.com");

        this.textNodeConvertAndCheck(
            url,
            TextNode.hyperlink(url)
        );
    }

    @Test
    public void testTextNodeConvertUrlToImage() {
        final Url url = Url.parseAbsolute("https://www.google.com");

        this.textNodeConvertAndCheck(
            url,
            Image.class,
            TextNode.image(url)
        );
    }

    private void textNodeConvertAndCheck(final Object value,
                                         final Object expected) {
        this.textNodeConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void textNodeConvertAndCheck(final Object value,
                                             final Class<T> type,
                                             final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.textNode(),
            value,
            type,
            TEXT_NODE_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext TEXT_NODE_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.basic(),
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
            HasUserDirectorieses.fake(),
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        (l) -> {
                            throw new UnsupportedOperationException();
                        }, // canDateTimeSymbolsForLocale
                        (l) -> {
                            throw new UnsupportedOperationException();
                        }, // canDecimalNumberSymbolsForLocale
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        INDENTATION,
                        LineEnding.NL,
                        ',', // valueSeparator
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
            ),
            LocaleContexts.jre(locale)
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

    private final static AbsoluteUrl URL = Url.parseAbsolute("https://www.example.com/123");

    @Test
    public void testConvertUrlToString() {
        final String url = "https://www.example.com";
        final SpreadsheetConverterContext context = this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL);

        this.convertAndCheck(
            context.converter(),
            Url.parseAbsolute(url),
            String.class,
            context,
            url
        );
    }

    // url..............................................................................................................

    @Test
    public void testUrlConvertStringToColorFails() {
        this.convertFails(
            SpreadsheetConverters.url(),
            Color.BLACK.value(),
            Color.class,
            URL_CONVERTER_CONTEXT
        );
    }

    @Test
    public void testUrlConvertHasTextUrlToUrl() {
        this.urlConvertAndCheck(
            URL,
            URL
        );
    }

    @Test
    public void testUrlConvertStringToUrl() {
        this.urlConvertAndCheck(
            URL.text(),
            URL
        );
    }

    @Test
    public void testUrlConvertStringToHyperlink() {
        this.urlConvertAndCheck(
            URL,
            TextNode.hyperlink(URL)
        );
    }

    @Test
    public void testUrlConvertStringToImage() {
        this.urlConvertAndCheck(
            URL,
            TextNode.image(URL)
        );
    }

    private void urlConvertAndCheck(final Object value,
                                    final Object expected) {
        this.urlConvertAndCheck(
            value,
            expected.getClass(),
            Cast.to(expected)
        );
    }

    private <T> void urlConvertAndCheck(final Object value,
                                        final Class<T> type,
                                        final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.url(),
            value,
            type,
            URL_CONVERTER_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext URL_CONVERTER_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.basic(),
                SpreadsheetConverters.text()
            )
        );
    };

    private SpreadsheetConverterContext spreadsheetConverterContext(final ExpressionNumberKind kind) {
        return SpreadsheetConverterContexts.basic(
            HasUserDirectorieses.fake(),
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        (l) -> {
                            throw new UnsupportedOperationException();
                        }, // canDateTimeSymbolsForLocale
                        (l) -> {
                            throw new UnsupportedOperationException();
                        }, // canDecimalNumberSymbolsForLocale
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        INDENTATION,
                        LineEnding.NL,
                        ',', // valueSeparator
                        Converters.fake(),
                        DateTimeContexts.fake(), // unused only doing numbers
                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                    ),
                    kind
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            ),
            LocaleContexts.fake()
        );
    }

    // template.........................................................................................................

    private final static TemplateValueName TEMPLATE_VALUE_NAME = TemplateValueName.with("Template123");

    @Test
    public void testTemplateConvertTemplateValueNameToTemplateValueName() {
        this.templateConvertAndCheck(
            TEMPLATE_VALUE_NAME,
            TemplateValueName.class,
            TEMPLATE_VALUE_NAME
        );
    }

    @Test
    public void testTemplateConvertStringToTemplateValueName() {
        this.templateConvertAndCheck(
            TEMPLATE_VALUE_NAME.text(),
            TemplateValueName.class,
            TEMPLATE_VALUE_NAME
        );
    }

    private <T> void templateConvertAndCheck(final Object value,
                                             final Class<T> type,
                                             final T expected) {
        this.convertAndCheck(
            SpreadsheetConverters.template(),
            value,
            type,
            TEMPLATE_CONTEXT,
            expected
        );
    }

    private final static SpreadsheetConverterContext TEMPLATE_CONTEXT = new FakeSpreadsheetConverterContext() {
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
                SpreadsheetConverters.basic(),
                SpreadsheetConverters.text()
            )
        );
    };

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
