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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.CharSequences;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class NonEmptySpreadsheetMetadataTest extends SpreadsheetMetadataTestCase<NonEmptySpreadsheetMetadata>
        implements ConverterTesting,
        DateTimeContextTesting,
        DecimalNumberContextTesting {

    @Test
    public void testWithSpreadsheetMetadataMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());
        final SpreadsheetMetadataMap metadataMap = SpreadsheetMetadataMap.with(map);

        final NonEmptySpreadsheetMetadata metadata = this.createSpreadsheetMetadata(metadataMap);
        assertSame(metadataMap, metadata.value(), "value");
    }

    @Test
    public void testWithMapCopied() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(map);

        final NonEmptySpreadsheetMetadata metadata = this.createSpreadsheetMetadata(map);

        map.clear();
        assertEquals(copy, metadata.value(), "value");
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataMap.EMPTY, SpreadsheetMetadataMap.with(Maps.empty()));
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

        final NonEmptySpreadsheetMetadata metadata = this.createSpreadsheetMetadata(map);
        assertEquals(SpreadsheetMetadataMap.class, metadata.value().getClass(), () -> "" + metadata.value);
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

        final SpreadsheetMetadata metadata = NonEmptySpreadsheetMetadata.with(Maps.of(propertyName, email));
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

    // NumberToColor....................................................................................................

    @Test
    public final void testNumberToColorFunction() {
        final Color color1 = Color.fromRgb(0x111);
        final int number1 = 1;

        final Color color7 = Color.fromRgb(0x777);
        final int number7 = 7;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.BIG_DECIMAL_PATTERN, "#0.0")
                .set(SpreadsheetMetadataPropertyName.color(number1), color1)
                .set(SpreadsheetMetadataPropertyName.color(number7), color7)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        final NonEmptySpreadsheetMetadataNumberToColorFunction function = Cast.to(metadata.numberToColor());

        assertEquals(Maps.of(number1, color1, number7, color7),
                function.numberToColor,
                () -> metadata.toString());
    }

    @Test
    public final void testNumberToColor2() {
        final Color color1 = Color.fromRgb(0x111);
        final int number1 = 1;

        final Color color7 = Color.fromRgb(0x777);
        final int number7 = 7;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.BIG_DECIMAL_PATTERN, "#0.0")
                .set(SpreadsheetMetadataPropertyName.color(number1), color1)
                .set(SpreadsheetMetadataPropertyName.color(number7), color7)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

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
        this.hateosLinkIdAndCheck(NonEmptySpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(0x12347f))),
                "12347f");
    }

    @Test
    public void testHateosLinkIdMissingIdFails() {
        assertThrows(IllegalStateException.class, () -> {
            this.createSpreadsheetMetadata().hateosLinkId();
        });
    }

    // HasDateTimeContext...............................................................................................

    @Test
    public void testDateTimeContext() {
        Arrays.stream(Locale.getAvailableLocales())
                .forEach(l -> {
                            final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.LOCALE, l));
                            final DateFormatSymbols symbols = DateFormatSymbols.getInstance(l);
                            final DateTimeContext context = metadata.dateTimeContext();
                            this.amPmAndCheck(context, 13, symbols.getAmPmStrings()[1]);
                            this.monthNameAndCheck(context, 2, symbols.getMonths()[2]);
                            this.monthNameAbbreviationAndCheck(context, 3, symbols.getShortMonths()[3]);
                            this.weekDayNameAndCheck(context, 1, symbols.getWeekdays()[1]);
                            this.weekDayNameAbbreviationAndCheck(context, 3, symbols.getShortWeekdays()[3]);

                        }
                );
    }

    // HasDecimalNumberContext..........................................................................................

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "CS")
                    .set(SpreadsheetMetadataPropertyName.DECIMAL_POINT, 'D')
                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E')
                    .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, 'G')
                    .set(SpreadsheetMetadataPropertyName.MINUS_SIGN, 'M')
                    .decimalNumberContext();
        });
        assertEquals("Required properties \"locale\", \"percentage-symbol\", \"plus-sign\", \"precision\", \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "CS")
                    .set(SpreadsheetMetadataPropertyName.DECIMAL_POINT, 'D')
                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E')
                    .decimalNumberContext();
        });
        assertEquals("Required properties \"grouping-separator\", \"locale\", \"minus-sign\", \"percentage-symbol\", \"plus-sign\", \"precision\", \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testDecimalNumberContextPropertiesPresent() {
        final String currencySymbol = "CS";
        final Character decimalPoint = 'D';
        final Character exponentSymbol = 'E';
        final Character groupingSeparator = 'G';
        final Character minusSign = 'M';
        final Character percentSymbol = 'P';
        final Character plusSign = '+';
        final Locale locale = Locale.CANADA_FRENCH;

        Lists.of(MathContext.DECIMAL32, MathContext.DECIMAL64, MathContext.DECIMAL128, MathContext.UNLIMITED)
                .stream()
                .forEach(mc -> {
                    final int precision = mc.getPrecision();
                    final RoundingMode roundingMode = mc.getRoundingMode();

                    this.decimalNumberContextAndCheck(SpreadsheetMetadata.EMPTY
                                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, currencySymbol)
                                    .set(SpreadsheetMetadataPropertyName.DECIMAL_POINT, decimalPoint)
                                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, exponentSymbol)
                                    .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, groupingSeparator)
                                    .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                                    .set(SpreadsheetMetadataPropertyName.MINUS_SIGN, minusSign)
                                    .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, percentSymbol)
                                    .set(SpreadsheetMetadataPropertyName.PLUS_SIGN, plusSign)
                                    .set(SpreadsheetMetadataPropertyName.PRECISION, precision)
                                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, roundingMode),
                            currencySymbol,
                            decimalPoint,
                            exponentSymbol,
                            groupingSeparator,
                            locale,
                            minusSign,
                            percentSymbol,
                            plusSign,
                            precision,
                            roundingMode);
                });
    }

    @Test
    public void testDecimalNumberContextLocaleDefaults() {
        final Character exponentSymbol = 'E';
        final Character plusSign = '+';

        Arrays.stream(Locale.getAvailableLocales())
                .forEach(locale -> {
                    Lists.of(MathContext.DECIMAL32, MathContext.DECIMAL64, MathContext.DECIMAL128, MathContext.UNLIMITED)
                            .stream()
                            .forEach(mc -> {
                                final int precision = mc.getPrecision();
                                final RoundingMode roundingMode = mc.getRoundingMode();

                                final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);

                                this.decimalNumberContextAndCheck(SpreadsheetMetadata.EMPTY
                                                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, exponentSymbol)
                                                .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                                                .set(SpreadsheetMetadataPropertyName.PLUS_SIGN, plusSign)
                                                .set(SpreadsheetMetadataPropertyName.PRECISION, precision)
                                                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, roundingMode),
                                        symbols.getCurrencySymbol(),
                                        symbols.getDecimalSeparator(),
                                        exponentSymbol,
                                        symbols.getGroupingSeparator(),
                                        locale,
                                        symbols.getMinusSign(),
                                        symbols.getPercent(),
                                        plusSign,
                                        precision,
                                        roundingMode);
                            });
                });
    }

    private void decimalNumberContextAndCheck(final SpreadsheetMetadata metadata,
                                              final String currencySymbol,
                                              final Character decimalPoint,
                                              final Character exponentSymbol,
                                              final Character groupingSeparator,
                                              final Locale locale,
                                              final Character minusSign,
                                              final Character percentSymbol,
                                              final Character plusSign,
                                              final int precision,
                                              final RoundingMode roundingMode) {
        final DecimalNumberContext context = metadata.decimalNumberContext();
        this.checkCurrencySymbol(context, currencySymbol);
        this.checkDecimalPoint(context, decimalPoint);
        this.checkExponentSymbol(context, exponentSymbol);
        this.checkGroupingSeparator(context, groupingSeparator);
        this.checkMinusSign(context, minusSign);
        this.checkPercentageSymbol(context, percentSymbol);
        this.checkPlusSign(context, plusSign);

        this.hasLocaleAndCheck(context, locale);
        this.hasMathContextAndCheck(context, new MathContext(precision, roundingMode));
    }

    // HasMathContext...................................................................................................

    @Test
    public void testHasMathContextRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            NonEmptySpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, 1))
                    .mathContext();
        });
        this.checkMessage(thrown, "Required properties \"rounding-mode\" missing.");
    }

    @Test
    public void testMathContext() {
        final int precision = 11;

        Arrays.stream(RoundingMode.values()).forEach(r -> {
            final MathContext mathContext = NonEmptySpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, precision,
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

        this.toStringAndCheck(NonEmptySpreadsheetMetadata.with(map), map.toString());
    }

    // HasJsonNode......................................................................................................

    @Test
    public void testHasJsonNodeRoundtrip() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

        properties.put(SpreadsheetMetadataPropertyName.BIG_DECIMAL_PATTERN, "#0.0");
        properties.put(SpreadsheetMetadataPropertyName.BIG_INTEGER_PATTERN, "#0");
        properties.put(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD");
        properties.put(SpreadsheetMetadataPropertyName.DATE_PATTERN, "DD/MM/YYYY");
        properties.put(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET);
        properties.put(SpreadsheetMetadataPropertyName.DATETIME_PATTERN, "DD/MM/YYYY hh:mm");
        properties.put(SpreadsheetMetadataPropertyName.DECIMAL_POINT, 'D');
        properties.put(SpreadsheetMetadataPropertyName.DEFAULT_PATTERN, "#0.000");
        properties.put(SpreadsheetMetadataPropertyName.DOUBLE_PATTERN, "#0.#");
        properties.put(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E');
        properties.put(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, 'G');
        properties.put(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        properties.put(SpreadsheetMetadataPropertyName.LONG_PATTERN, "#0");
        properties.put(SpreadsheetMetadataPropertyName.MINUS_SIGN, 'M');
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, 'P');
        properties.put(SpreadsheetMetadataPropertyName.PLUS_SIGN, 'L');
        properties.put(SpreadsheetMetadataPropertyName.PRECISION, 123);
        properties.put(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR);
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
        properties.put(SpreadsheetMetadataPropertyName.TIME_PATTERN, "hh:mm");

        for (int i = 0; i < SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER + 2; i++) {
            properties.put(SpreadsheetMetadataPropertyName.color(i), Color.fromRgb(i));
        }

        final Set<SpreadsheetMetadataPropertyName<?>> missing = Sets.ordered();
        missing.addAll(SpreadsheetMetadataPropertyName.CONSTANTS.values());
        missing.removeAll(properties.keySet());

        assertEquals(Sets.empty(),
                missing,
                () -> "Several properties are missing values in " + properties);

        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetMetadata.with(properties));
    }

    // helpers...........................................................................................................

    @Override
    public NonEmptySpreadsheetMetadata createObject() {
        return this.createSpreadsheetMetadata();
    }

    private NonEmptySpreadsheetMetadata createSpreadsheetMetadata() {
        return this.createSpreadsheetMetadata(this.property1(), this.value1(), this.property2(), this.value2());
    }

    private <X> NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                      final X value1) {
        return this.createSpreadsheetMetadata(Maps.of(property1, value1));
    }

    private <X, Y> NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                         final X value1,
                                                                         final SpreadsheetMetadataPropertyName<Y> property2,
                                                                         final Y value2) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        return this.createSpreadsheetMetadata(map);
    }

    private <X, Y, Z> NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
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

    private NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        return Cast.to(SpreadsheetMetadata.with(map));
    }

    private SpreadsheetMetadataPropertyName<LocalDateTime> property1() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value1() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    private SpreadsheetMetadataPropertyName<EmailAddress> property2() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private EmailAddress value2() {
        return EmailAddress.parse("user@example.com");
    }

    private SpreadsheetMetadataPropertyName<EmailAddress> property3() {
        return SpreadsheetMetadataPropertyName.MODIFIED_BY;
    }

    private EmailAddress value3() {
        return EmailAddress.parse("different@example.com");
    }

    @Override
    Class<NonEmptySpreadsheetMetadata> metadataType() {
        return NonEmptySpreadsheetMetadata.class;
    }

}
