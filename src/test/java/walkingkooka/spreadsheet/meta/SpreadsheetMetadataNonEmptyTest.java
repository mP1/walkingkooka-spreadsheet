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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataNonEmptyTest extends SpreadsheetMetadataTestCase<SpreadsheetMetadataNonEmpty>
        implements ConverterTesting,
        DateTimeContextTesting,
        DecimalNumberContextTesting,
        SpreadsheetFormatterTesting {

    @Test
    public void testWithSpreadsheetMetadataMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());
        final SpreadsheetMetadataNonEmptyMap metadataMap = SpreadsheetMetadataNonEmptyMap.with(map);

        final SpreadsheetMetadataNonEmpty metadata = this.createSpreadsheetMetadata(metadataMap);
        assertSame(metadataMap, metadata.value(), "value");
    }

    @Test
    public void testWithMapCopied() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(map);

        final SpreadsheetMetadataNonEmpty metadata = this.createSpreadsheetMetadata(map);

        map.clear();
        assertEquals(copy, metadata.value(), "value");
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataNonEmptyMap.EMPTY, SpreadsheetMetadataNonEmptyMap.with(Maps.empty()));
    }

    @Test
    public void testId() {
        final SpreadsheetId id = SpreadsheetId.with(123);
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id));
        assertEquals(Optional.of(id), metadata.id(), "id");
    }

    @Test
    public void testValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final SpreadsheetMetadataNonEmpty metadata = this.createSpreadsheetMetadata(map);
        assertEquals(SpreadsheetMetadataNonEmptyMap.class, metadata.value().getClass(), () -> "" + metadata.value);
    }

    // get..............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(this.createSpreadsheetMetadata(),
                this.property1(),
                this.value1());
    }

    @Test
    public void testGet2() {
        this.getAndCheck(this.createSpreadsheetMetadata(),
                this.property2(),
                this.value2());
    }

    // getOrFail........................................................................................................

    @Test
    public void testGetOrFailPresent() {
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress email = EmailAddress.parse("creator123@example.com");

        final SpreadsheetMetadata metadata = SpreadsheetMetadataNonEmpty.with(Maps.of(propertyName, email));
        assertEquals(email,
                metadata.getOrFail(propertyName),
                () -> "getOrFail " + propertyName + " in " + metadata);
    }

    // set..............................................................................................................

    @Test
    public void testSetExistingPropertyAndValue() {
        this.setAndCheck(this.createSpreadsheetMetadata(),
                this.property1(),
                this.value1());
    }

    @Test
    public void testSetExistingPropertyAndValue2() {
        this.setAndCheck(this.createSpreadsheetMetadata(),
                this.property2(),
                this.value2());
    }

    @Test
    public void testSetReplacePropertyAndValue() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final LocalDateTime different = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        assertNotSame(different, value1);

        this.setAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, value2),
                property1,
                different,
                this.createSpreadsheetMetadata(property1, different, property2, value2));
    }

    @Test
    public void testSetReplacePropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final EmailAddress different = EmailAddress.parse("different@example.com");
        assertNotSame(different, value2);

        this.setAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, value2),
                property2,
                different,
                this.createSpreadsheetMetadata(property1, value1, property2, different));
    }

    @Test
    public void testSetNewPropertyAndValue() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final SpreadsheetMetadataPropertyName<EmailAddress> property3 = this.property3();
        final EmailAddress value3 = this.value3();

        this.setAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, value2),
                property3,
                value3,
                this.createSpreadsheetMetadata(property1, value1, property2, value2, property3, value3));
    }

    @Test
    public void testSetNewPropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final SpreadsheetMetadataPropertyName<EmailAddress> property3 = this.property3();
        final EmailAddress value3 = this.value3();

        this.setAndCheck(this.createSpreadsheetMetadata(property2, value2, property3, value3),
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1, property2, value2, property3, value3));
    }

    private <T> void setAndCheck(final SpreadsheetMetadata metadata,
                                 final SpreadsheetMetadataPropertyName<T> propertyName,
                                 final T value) {
        assertSame(metadata,
                metadata.set(propertyName, value),
                () -> metadata + " set " + propertyName + " and " + CharSequences.quoteIfChars(value));
    }

    // remove...........................................................................................................

    @Test
    public void testRemove() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, this.value1(), property2, value2),
                property1,
                this.createSpreadsheetMetadata(property2, value2));
    }

    @Test
    public void testRemove2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, this.value2()),
                property2,
                this.createSpreadsheetMetadata(property1, value1));
    }

    @Test
    public void testRemoveBecomesEmpty() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, value1),
                property1,
                SpreadsheetMetadata.EMPTY);
    }

    // set & remove ...................................................................................................

    @Test
    public void testSetSetRemoveRemove() {
        //set
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1));

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(metadata1,
                property2,
                value2,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));

        // remove1
        final SpreadsheetMetadata metadata3 = this.removeAndCheck(metadata2,
                property1,
                this.createSpreadsheetMetadata(property2, value2));

        this.removeAndCheck(metadata3,
                property2,
                SpreadsheetMetadata.EMPTY);
    }

    @Test
    public void testSetSetRemoveSet() {
        //set
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1));

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(metadata1,
                property2,
                value2,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));

        // remove1
        final SpreadsheetMetadata metadata3 = this.removeAndCheck(metadata2,
                property1,
                this.createSpreadsheetMetadata(property2, value2));


        //set property1 again
        this.setAndCheck(metadata3,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));
    }

    // NameToColor......................................................................................................

    @Test
    public final void testNameToColor2() {
        final Color color1 = Color.fromRgb(0x111);
        final SpreadsheetColorName name1 = SpreadsheetColorName.with("title");

        final Color color2 = Color.fromRgb(0x222);
        final SpreadsheetColorName name2 = SpreadsheetColorName.with("that");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.namedColor(name1), color1)
                .set(SpreadsheetMetadataPropertyName.namedColor(name2), color2)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        Stream.of(name1, name2, SpreadsheetColorName.with("unknown"))
                .forEach(n -> this.nameToColorAndCheck(metadata,
                        n,
                        name1 == n ? color1 :
                                name2 == n ? color2 :
                                        null));
    }
    
    // NumberToColor....................................................................................................

    @Test
    public final void testNumberToColor2() {
        final Color color1 = Color.fromRgb(0x111);
        final int number1 = 1;

        final Color color7 = Color.fromRgb(0x777);
        final int number7 = 7;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(number1), color1)
                .set(SpreadsheetMetadataPropertyName.numberedColor(number7), color7)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("#0.0"));

        for(int i = 0; i < 10; i++) {
            this.numberToColorAndCheck(metadata,
                    i,
                    number1 == i ? color1 :
                    number7 == i ? color7 :
                    null);
        }
    }

    // HateosResource...................................................................................................

    @Test
    public void testHateosLinkId() {
        this.hateosLinkIdAndCheck(SpreadsheetMetadataNonEmpty.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(0x12347f))),
                "12347f");
    }

    @Test
    public void testHateosLinkIdMissingIdFails() {
        assertThrows(IllegalStateException.class, () -> this.createSpreadsheetMetadata().hateosLinkId());
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverterBigDecimalToString() {
        this.convertAndCheck2(BigDecimal.valueOf(123.5),
                "Number 123.500");
    }

    @Test
    public void testConverterBigIntegerToString() {
        this.convertAndCheck2(BigInteger.valueOf(123),
                "Number 123.000");
    }

    @Test
    public void testConverterByteToString() {
        this.convertAndCheck2((byte) 123,
                "Number 123.000");
    }

    @Test
    public void testConverterShortToString() {
        this.convertAndCheck2((short) 123,
                "Number 123.000");
    }

    @Test
    public void testConverterIntegerToString() {
        this.convertAndCheck2(123,
                "Number 123.000");
    }

    @Test
    public void testConverterLongToString() {
        this.convertAndCheck2(123L,
                "Number 123.000");
    }

    @Test
    public void testConverterFloatToString() {
        this.convertAndCheck2(123.5f,
                "Number 123.500");
    }

    @Test
    public void testConverterDoubleToString() {
        this.convertAndCheck2(123.5,
                "Number 123.500");
    }

    @Test
    public void testConverterStringToBigDecimal() {
        this.convertAndCheck2("Number 123.500", BigDecimal.valueOf(123.5));
    }

    @Test
    public void testConverterStringToBigInteger() {
        this.convertAndCheck2("Number 123.000", BigInteger.valueOf(123));
    }

    @Test
    public void testConverterStringToByte() {
        this.convertAndCheck2("Number 123.000", (byte) 123);
    }

    @Test
    public void testConverterStringToShort() {
        this.convertAndCheck2("Number 123.000", (short) 123);
    }

    @Test
    public void testConverterStringToInteger() {
        this.convertAndCheck2("Number 123.000", 123);
    }

    @Test
    public void testConverterStringToLong() {
        this.convertAndCheck2("Number 123.000", 123L);
    }

    @Test
    public void testConverterStringToFloat() {
        this.convertAndCheck2("Number 123.500", 123.5f);
    }

    @Test
    public void testConverterStringToDouble() {
        this.convertAndCheck2("Number 123.500", 123.5);
    }

    @Test
    public void testConverterDateToString() {
        this.convertAndCheck2("Date 2000 12 31", LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testConverterStringToDate() {
        this.convertAndCheck2(LocalDate.of(2000, 12, 31), "Date 2000 12 31");
    }

    @Test
    public void testConverterDateTimeToString() {
        this.convertAndCheck2("DateTime 2000 12", LocalDateTime.of(2000, 1, 1, 12, 0, 0));
    }

    @Test
    public void testConverterStringToDateTime() {
        this.convertAndCheck2(LocalDateTime.of(2000, 1, 1, 12, 0, 0), "DateTime 2000 12");
    }

    @Test
    public void testConverterStringToString() {
        final String text = "abc123";
        this.convertAndCheck2(text, "Text " + text);
    }

    @Test
    public void testConverterTimeToString() {
        this.convertAndCheck2("Time 59 12", LocalTime.of(12, 0, 59));
    }

    @Test
    public void testConverterStringToTime() {
        this.convertAndCheck2(LocalTime.of(12, 58, 59), "Time 59 12");
    }

    private void convertAndCheck2(final Object value,
                                  final Object expected) {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("\"Date\" yyyy mm dd"))
                .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("\"Date\" yyyy mm dd"))
                .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" yyyy hh"))
                .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("\"DateTime\" yyyy hh"))
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" 00.000"))
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("\"Number\" 00.000"))
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @"))
                .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" ss hh"))
                .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("\"Time\" ss hh"));

        this.convertAndCheck(metadata.converter(),
                value,
                Cast.to(expected.getClass()),
                ConverterContexts.basic(DateTimeContexts.locale(Locale.ENGLISH, 20), DecimalNumberContexts.american(MathContext.DECIMAL32)),
                expected);
    }

    // HasDateTimeContext...............................................................................................

    @Test
    public void testDateTimeContext() {
        Arrays.stream(Locale.getAvailableLocales())
                .forEach(l -> {
                            final int twoDigitYear = 49;
                            final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.LOCALE, l,
                                    SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear));

                            final DateFormatSymbols symbols = DateFormatSymbols.getInstance(l);
                            final DateTimeContext context = metadata.dateTimeContext();
                            this.amPmAndCheck(context, 13, symbols.getAmPmStrings()[1]);
                            this.monthNameAndCheck(context, 2, symbols.getMonths()[2]);
                            this.monthNameAbbreviationAndCheck(context, 3, symbols.getShortMonths()[3]);
                            this.twoDigitYearAndCheck(context, twoDigitYear);
                            this.weekDayNameAndCheck(context, 1, symbols.getWeekdays()[1]);
                            this.weekDayNameAbbreviationAndCheck(context, 3, symbols.getShortWeekdays()[3]);

                        }
                );
    }

    // HasDecimalNumberContext..........................................................................................

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "CS")
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, 'D')
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E')
                .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, 'G')
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, 'M')
                .decimalNumberContext());
        assertEquals("Required properties \"locale\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "CS")
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, 'D')
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E')
                .decimalNumberContext());
        assertEquals("Required properties \"grouping-separator\", \"locale\", \"negative-sign\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testDecimalNumberContextPropertiesPresent() {
        final String currencySymbol = "CS";
        final Character decimalSeparator = 'D';
        final Character exponentSymbol = 'E';
        final Character groupingSeparator = 'G';
        final Character negativeSign = 'N';
        final Character percentSymbol = 'P';
        final Character positiveSign = '+';
        final Locale locale = Locale.CANADA_FRENCH;

        Lists.of(MathContext.DECIMAL32, MathContext.DECIMAL64, MathContext.DECIMAL128, MathContext.UNLIMITED)
                .forEach(mc -> {
                    final int precision = mc.getPrecision();
                    final RoundingMode roundingMode = mc.getRoundingMode();

                    this.decimalNumberContextAndCheck(SpreadsheetMetadata.EMPTY
                                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, currencySymbol)
                                    .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator)
                                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, exponentSymbol)
                                    .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, groupingSeparator)
                                    .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                                    .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, negativeSign)
                                    .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, percentSymbol)
                                    .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, positiveSign)
                                    .set(SpreadsheetMetadataPropertyName.PRECISION, precision)
                                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, roundingMode),
                            currencySymbol,
                            decimalSeparator,
                            exponentSymbol,
                            groupingSeparator,
                            locale,
                            negativeSign,
                            percentSymbol,
                            positiveSign,
                            precision,
                            roundingMode);
                });
    }

    @Test
    public void testDecimalNumberContextLocaleDefaults() {
        final Character exponentSymbol = 'E';
        final Character positiveSign = '+';

        Arrays.stream(Locale.getAvailableLocales())
                .forEach(locale -> Lists.of(MathContext.DECIMAL32, MathContext.DECIMAL64, MathContext.DECIMAL128, MathContext.UNLIMITED)
                        .forEach(mc -> {
                            final int precision = mc.getPrecision();
                            final RoundingMode roundingMode = mc.getRoundingMode();

                            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);

                            this.decimalNumberContextAndCheck(SpreadsheetMetadata.EMPTY
                                            .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, exponentSymbol)
                                            .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                                            .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, positiveSign)
                                            .set(SpreadsheetMetadataPropertyName.PRECISION, precision)
                                            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, roundingMode),
                                    symbols.getCurrencySymbol(),
                                    symbols.getDecimalSeparator(),
                                    exponentSymbol,
                                    symbols.getGroupingSeparator(),
                                    locale,
                                    symbols.getMinusSign(),
                                    symbols.getPercent(),
                                    positiveSign,
                                    precision,
                                    roundingMode);
                        }));
    }

    private void decimalNumberContextAndCheck(final SpreadsheetMetadata metadata,
                                              final String currencySymbol,
                                              final Character decimalSeparator,
                                              final Character exponentSymbol,
                                              final Character groupingSeparator,
                                              final Locale locale,
                                              final Character negativeSign,
                                              final Character percentSymbol,
                                              final Character positiveSign,
                                              final int precision,
                                              final RoundingMode roundingMode) {
        final DecimalNumberContext context = metadata.decimalNumberContext();
        this.checkCurrencySymbol(context, currencySymbol);
        this.checkDecimalSeparator(context, decimalSeparator);
        this.checkExponentSymbol(context, exponentSymbol);
        this.checkGroupingSeparator(context, groupingSeparator);
        this.checkNegativeSign(context, negativeSign);
        this.checkPercentageSymbol(context, percentSymbol);
        this.checkPositiveSign(context, positiveSign);

        this.hasLocaleAndCheck(context, locale);
        this.hasMathContextAndCheck(context, new MathContext(precision, roundingMode));
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatDate() {
        this.formatAndCheck2(LocalDate.of(2000, 12, 31), "Date 31122000");
    }

    @Test
    public void testFormatterFormatDateTime() {
        this.formatAndCheck2(LocalDateTime.of(2000, 12, 31, 12, 58, 59), "DateTime 31122000 125859");
    }

    @Test
    public void testFormatterFormatNumber() {
        this.formatAndCheck2(125.5, "Number 125.500");
    }

    @Test
    public void testFormatterFormatText() {
        this.formatAndCheck2("abc123", "Text abc123");
    }

    @Test
    public void testFormatterFormatTime() {
        this.formatAndCheck2(LocalTime.of(12, 58, 59), "Time 125859");
    }

    private void formatAndCheck2(final Object value,
                                 final String text) {
        this.formatAndCheck(SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("\"Date\" ddmmyyyy"))
                        .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" ddmmyyyy hhmmss"))
                        .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" #.000"))
                        .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @"))
                        .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" hhmmss"))
                        .formatter(),
                value,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> target) {
                        return this.convert(value, target).isLeft();
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return Converters.collection(Lists.of(Converters.simple(),
                                Converters.numberNumber(),
                                Converters.localDateLocalDateTime(),
                                Converters.localTimeLocalDateTime()))
                                .convert(value, target, ConverterContexts.fake());
                    }

                    @Override
                    public char decimalSeparator() {
                        return this.decimalNumberContext.decimalSeparator();
                    }

                    @Override
                    public char groupingSeparator() {
                        return this.decimalNumberContext.groupingSeparator();
                    }

                    @Override
                    public char negativeSign() {
                        return this.decimalNumberContext.negativeSign();
                    }

                    @Override
                    public char positiveSign() {
                        return this.decimalNumberContext.positiveSign();
                    }

                    @Override
                    public MathContext mathContext() {
                        return this.decimalNumberContext.mathContext();
                    }

                    private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.UNLIMITED);
                },
                SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, text));
    }

    // HasMathContext...................................................................................................

    @Test
    public void testHasMathContextRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadataNonEmpty.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, 1))
                .mathContext());
        this.checkMessage(thrown, "Required properties \"rounding-mode\" missing.");
    }

    @Test
    public void testMathContext() {
        final int precision = 11;

        Arrays.stream(RoundingMode.values()).forEach(r -> {
            final MathContext mathContext = SpreadsheetMetadataNonEmpty.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, precision,
                    SpreadsheetMetadataPropertyName.ROUNDING_MODE, r))
                    .mathContext();
            assertEquals(precision, mathContext.getPrecision(), "precision");
            assertEquals(r, mathContext.getRoundingMode(), "roundingMode");
        });
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(SpreadsheetMetadataNonEmpty.with(map), map.toString());
    }

    // JsonNodeMarshallingTesting...........................................................................................

    /**
     * This test verifies that all {@link SpreadsheetMetadataPropertyName} value types are also
     * {@link walkingkooka.tree.json.marshall.JsonNodeContext} registered.
     */
    @Test
    public void testFromJson() {
        final JsonNode json = JsonNode.parse("{\n" +
                "  \"color-0\": \"#000000\",\n" +
                "  \"color-1\": \"#000001\",\n" +
                "  \"color-10\": \"#00000a\",\n" +
                "  \"color-11\": \"#00000b\",\n" +
                "  \"color-12\": \"#00000c\",\n" +
                "  \"color-13\": \"#00000d\",\n" +
                "  \"color-14\": \"#00000e\",\n" +
                "  \"color-15\": \"#00000f\",\n" +
                "  \"color-16\": \"#000010\",\n" +
                "  \"color-17\": \"#000011\",\n" +
                "  \"color-18\": \"#000012\",\n" +
                "  \"color-19\": \"#000013\",\n" +
                "  \"color-2\": \"#000002\",\n" +
                "  \"color-20\": \"#000014\",\n" +
                "  \"color-21\": \"#000015\",\n" +
                "  \"color-22\": \"#000016\",\n" +
                "  \"color-23\": \"#000017\",\n" +
                "  \"color-24\": \"#000018\",\n" +
                "  \"color-25\": \"#000019\",\n" +
                "  \"color-26\": \"#00001a\",\n" +
                "  \"color-27\": \"#00001b\",\n" +
                "  \"color-28\": \"#00001c\",\n" +
                "  \"color-29\": \"#00001d\",\n" +
                "  \"color-3\": \"#000003\",\n" +
                "  \"color-30\": \"#00001e\",\n" +
                "  \"color-31\": \"#00001f\",\n" +
                "  \"color-32\": \"#000020\",\n" +
                "  \"color-33\": \"#000021\",\n" +
                "  \"color-4\": \"#000004\",\n" +
                "  \"color-5\": \"#000005\",\n" +
                "  \"color-6\": \"#000006\",\n" +
                "  \"color-7\": \"#000007\",\n" +
                "  \"color-8\": \"#000008\",\n" +
                "  \"color-9\": \"#000009\",\n" +
                "  \"color-big\": \"#017d0000\",\n" +
                "  \"color-medium\": \"#be8f75bf\",\n" +
                "  \"color-small\": \"#87950706\",\n" +
                "  \"create-date-time\": \"2000-12-31T12:58:59\",\n" +
                "  \"creator\": \"creator@example.com\",\n" +
                "  \"currency-symbol\": \"$AUD\",\n" +
                "  \"date-format-pattern\": \"DD/MM/YYYY\",\n" +
                "  \"date-parse-pattern\": \"DD/MM/YYYY;DDMMYYYY\",\n" +
                "  \"date-time-format-pattern\": \"DD/MM/YYYY hh:mm\",\n" +
                "  \"date-time-offset\": \"0\",\n" +
                "  \"date-time-parse-patterns\": \"DD/MM/YYYY hh:mm;DDMMYYYYHHMM;DDMMYYYY HHMM\",\n" +
                "  \"decimal-separator\": \"D\",\n" +
                "  \"exponent-symbol\": \"E\",\n" +
                "  \"grouping-separator\": \"G\",\n" +
                "  \"locale\": \"en\",\n" +
                "  \"modified-by\": \"modified@example.com\",\n" +
                "  \"modified-date-time\": \"1999-12-31T12:58:59\",\n" +
                "  \"negative-sign\": \"N\",\n" +
                "  \"number-format-pattern\": \"#0.0\",\n" +
                "  \"number-parse-patterns\": \"#0.0;$#0.00\",\n" +
                "  \"percentage-symbol\": \"P\",\n" +
                "  \"positive-sign\": \"O\",\n" +
                "  \"precision\": 123,\n" +
                "  \"rounding-mode\": \"FLOOR\",\n" +
                "  \"spreadsheet-id\": \"7b\",\n" +
                "  \"text-format-pattern\": \"@@\",\n" +
                "  \"time-format-pattern\": \"hh:mm\",\n" +
                "  \"time-parse-patterns\": \"hh:mm;hh:mm:ss.000\",\n" +
                "  \"two-digit-year\": 31,\n" +
                "  \"width\": 0\n" +
                "}");
        final SpreadsheetMetadata metadata = this.unmarshall(json);
        assertNotEquals(metadata, SpreadsheetMetadata.EMPTY);
    }

    @Test
    public void testJsonNodeMarshallRoundtrip() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

        properties.put(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD");
        properties.put(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY"));
        properties.put(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("DD/MM/YYYY;DDMMYYYY"));
        properties.put(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET);
        properties.put(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm"));
        properties.put(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("DD/MM/YYYY hh:mm;DDMMYYYYHHMM;DDMMYYYY HHMM"));
        properties.put(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, 'D');
        properties.put(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E');
        properties.put(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, 'G');
        properties.put(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, 'N');
        properties.put(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("#0.0"));
        properties.put(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("#0.0;$#0.00"));
        properties.put(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, 'P');
        properties.put(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, 'O');
        properties.put(SpreadsheetMetadataPropertyName.PRECISION, 123);
        properties.put(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR);
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
        properties.put(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("@@"));
        properties.put(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("hh:mm"));
        properties.put(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("hh:mm;hh:mm:ss.000"));
        properties.put(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 31);
        properties.put(SpreadsheetMetadataPropertyName.WIDTH, 0);

        for (int i = 0; i < SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER + 2; i++) {
            properties.put(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
        }

        Stream.of("big", "small", "medium")
                .forEach(n -> properties.put(SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.with(n)), Color.fromArgb(n.hashCode())));

        final Set<SpreadsheetMetadataPropertyName<?>> missing = Sets.ordered();
        missing.addAll(SpreadsheetMetadataPropertyName.CONSTANTS.values());
        missing.removeAll(properties.keySet());

        assertEquals(Sets.empty(),
                missing,
                () -> "Several properties are missing values in " + properties);

        this.marshallRoundTripTwiceAndCheck(SpreadsheetMetadata.with(properties));
    }

    // helpers...........................................................................................................

    @Override
    public SpreadsheetMetadataNonEmpty createObject() {
        return this.createSpreadsheetMetadata();
    }

    private SpreadsheetMetadataNonEmpty createSpreadsheetMetadata() {
        return this.createSpreadsheetMetadata(this.property1(), this.value1(), this.property2(), this.value2());
    }

    private <X> SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                      final X value1) {
        return this.createSpreadsheetMetadata(Maps.of(property1, value1));
    }

    private <X, Y> SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                         final X value1,
                                                                         final SpreadsheetMetadataPropertyName<Y> property2,
                                                                         final Y value2) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        return this.createSpreadsheetMetadata(map);
    }

    private <X, Y, Z> SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                            final X value1,
                                                                            final SpreadsheetMetadataPropertyName<Y> property2,
                                                                            final Y value2,
                                                                            final SpreadsheetMetadataPropertyName<Z> property3,
                                                                            final Z value3) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        map.put(property3, value3);
        return this.createSpreadsheetMetadata(map);
    }

    private SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        return Cast.to(SpreadsheetMetadata.with(map));
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<LocalDateTime> property1() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value1() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<EmailAddress> property2() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private EmailAddress value2() {
        return EmailAddress.parse("user@example.com");
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<EmailAddress> property3() {
        return SpreadsheetMetadataPropertyName.MODIFIED_BY;
    }

    private EmailAddress value3() {
        return EmailAddress.parse("different@example.com");
    }

    @Override
    Class<SpreadsheetMetadataNonEmpty> metadataType() {
        return SpreadsheetMetadataNonEmpty.class;
    }

}
