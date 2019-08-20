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

import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Converter} that supports converting all the spreadsheet types to other types.
 * </pre>
 */
final class SpreadsheetConverter implements Converter {

    /**
     * Factory that creates a new {@link SpreadsheetConverter}.
     */
    final static SpreadsheetConverter with(final SpreadsheetFormatter dateFormatter,
                                           final SpreadsheetDateParsePatterns dateParser,
                                           final SpreadsheetFormatter dateTimeFormatter,
                                           final SpreadsheetDateTimeParsePatterns dateTimeParser,
                                           final SpreadsheetFormatter numberFormatter,
                                           final SpreadsheetNumberParsePatterns numberParser,
                                           final SpreadsheetFormatter timeFormatter,
                                           final SpreadsheetTimeParsePatterns timeParser,
                                           final long dateOffset) {
        Objects.requireNonNull(dateFormatter, "dateFormatter");
        Objects.requireNonNull(dateParser, "dateParser");

        Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");
        Objects.requireNonNull(dateTimeParser, "dateTimeParser");

        Objects.requireNonNull(numberFormatter, "numberFormatter");
        Objects.requireNonNull(numberParser, "numberParser");

        Objects.requireNonNull(timeFormatter, "timeFormatter");
        Objects.requireNonNull(timeParser, "timeParser");

        return new SpreadsheetConverter(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                timeFormatter,
                timeParser,
                dateOffset);
    }

    private SpreadsheetConverter(final SpreadsheetFormatter dateFormatter,
                                 final SpreadsheetDateParsePatterns dateParser,
                                 final SpreadsheetFormatter dateTimeFormatter,
                                 final SpreadsheetDateTimeParsePatterns dateTimeParser,
                                 final SpreadsheetFormatter numberFormatter,
                                 final SpreadsheetNumberParsePatterns numberParser,
                                 final SpreadsheetFormatter timeFormatter,
                                 final SpreadsheetTimeParsePatterns timeParser,
                                 final long dateOffset) {
        super();

        final LocalDate dateTrue = LocalDate.ofEpochDay(dateOffset + 1);
        final LocalDate dateFalse = LocalDate.ofEpochDay(dateOffset);

        final LocalDateTime dateTimeTrue = dateTime(dateTrue);
        final LocalDateTime dateTimeFalse = dateTime(dateFalse);

        final String stringTrue = Boolean.TRUE.toString();
        final String stringFalse = Boolean.FALSE.toString();

        final LocalTime timeTrue = LocalTime.ofSecondOfDay(1);
        final LocalTime timeFalse = LocalTime.MIDNIGHT;

        final SpreadsheetConverterMapping<Converter> booleanConverter = SpreadsheetConverterMapping.with(null, // boolean -> boolean
                fromBoolean(LocalDate.class, dateTrue, dateFalse),
                fromBoolean(LocalDateTime.class, dateTimeTrue, dateTimeFalse),
                Converters.booleanNumber(),
                fromBoolean(String.class, stringTrue, stringFalse), // boolean -> String
                fromBoolean(LocalTime.class, timeTrue, timeFalse)); // Time

        final SpreadsheetConverterMapping<Converter> date = SpreadsheetConverterMapping.with(toBoolean(LocalDate.class, dateFalse),
                null, // date -> date
                Converters.localDateLocalDateTime(),
                Converters.localDateNumber(dateOffset),
                dateFormatter.converter(),
                null); // date -> time INVALID

        final SpreadsheetConverterMapping<Converter> dateTime = SpreadsheetConverterMapping.with(toBoolean(LocalDateTime.class, dateTimeFalse),
                Converters.localDateTimeLocalDate(),
                null, // dateTime -> dateTime
                Converters.localDateTimeNumber(dateOffset),
                dateTimeFormatter.converter(),
                Converters.localDateTimeLocalTime());

        final SpreadsheetConverterMapping<Converter> number = SpreadsheetConverterMapping.with(Converters.truthyNumberBoolean(),
                Converters.numberLocalDate(dateOffset),
                Converters.numberLocalDateTime(dateOffset),
                Converters.numberNumber(),
                numberFormatter.converter(),
                Converters.numberLocalTime());

        final SpreadsheetConverterMapping<Converter> string = SpreadsheetConverterMapping.with(toBoolean(String.class, stringFalse), // string -> boolean
                dateParser.converter(),
                dateTimeParser.converter(),
                numberParser.converter(),
                null, // String -> String
                timeParser.converter()
        );

        // time to
        final SpreadsheetConverterMapping<Converter> time = SpreadsheetConverterMapping.with(toBoolean(LocalTime.class, timeFalse),
                null, // time -> date invalid
                Converters.localDateTimeLocalTime(),
                Converters.localTimeNumber(),
                timeFormatter.converter(),
                null); // time -> time

        this.mapping = SpreadsheetConverterMapping.with(booleanConverter,
                date,
                dateTime,
                number,
                string,
                time);
    }

    /**
     * Creates a {@link Converter} that converts {@link Boolean} values to the given type.
     */
    private static <T> Converter fromBoolean(final Class<T> type,
                                             final T trueValue,
                                             final T falseValue) {
        return Converters.booleanConverter(Boolean.class,
                Boolean.FALSE,
                type,
                trueValue,
                falseValue);
    }

    private static <T> Converter toBoolean(final Class<T> from,
                                           final T falseValue) {
        return Converters.booleanConverter(from,
                falseValue,
                Boolean.class,
                Boolean.TRUE,
                Boolean.FALSE);
    }

    private static LocalDateTime dateTime(final LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIDNIGHT);
    }

    // Converter........................................................................................................

    /**
     * All spreadsheet types may be converted to another except for {@link LocalTime} to {@link LocalDate} and vice versa.
     */
    @Override
    public boolean canConvert(final Object value,
                              final Class<?> targetType,
                              final ConverterContext context) {
        return SUPPORTED_TYPES.contains(targetType) &&
                false == (value instanceof LocalTime && targetType == LocalDate.class) &&
                false == (value instanceof LocalDate && targetType == LocalTime.class);
    }

    private final static Set<Class<?>> SUPPORTED_TYPES = Sets.of(Boolean.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            BigDecimal.class,
            BigInteger.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Number.class,
            Short.class,
            Float.class,
            Double.class,
            String.class);

    @Override
    public <T> T convert(final Object value,
                         final Class<T> targetType,
                         final ConverterContext context) {
        return targetType.isInstance(value) ?
                targetType.cast(value) :
                SpreadsheetConverterSpreadsheetValueVisitor.converter(value, targetType, this.mapping)
                        .convert(value, targetType, context);
    }

    private final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> mapping;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
