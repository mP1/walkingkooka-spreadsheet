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

import walkingkooka.Either;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

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
    static SpreadsheetConverter with(final SpreadsheetFormatter dateFormatter,
                                     final SpreadsheetDateParsePatterns dateParser,
                                     final SpreadsheetFormatter dateTimeFormatter,
                                     final SpreadsheetDateTimeParsePatterns dateTimeParser,
                                     final SpreadsheetFormatter numberFormatter,
                                     final SpreadsheetNumberParsePatterns numberParser,
                                     final SpreadsheetFormatter textFormatter,
                                     final SpreadsheetFormatter timeFormatter,
                                     final SpreadsheetTimeParsePatterns timeParser,
                                     final long dateOffset,
                                     final ExpressionNumberKind expressionNumberKind) {
        Objects.requireNonNull(dateFormatter, "dateFormatter");
        Objects.requireNonNull(dateParser, "dateParser");

        Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");
        Objects.requireNonNull(dateTimeParser, "dateTimeParser");

        Objects.requireNonNull(numberFormatter, "numberFormatter");
        Objects.requireNonNull(numberParser, "numberParser");

        Objects.requireNonNull(textFormatter, "textFormatter");

        Objects.requireNonNull(timeFormatter, "timeFormatter");
        Objects.requireNonNull(timeParser, "timeParser");

        Objects.requireNonNull(expressionNumberKind, "expressionNumberKind");

        return new SpreadsheetConverter(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
                timeFormatter,
                timeParser,
                dateOffset,
                expressionNumberKind);
    }

    private SpreadsheetConverter(final SpreadsheetFormatter dateFormatter,
                                 final SpreadsheetDateParsePatterns dateParser,
                                 final SpreadsheetFormatter dateTimeFormatter,
                                 final SpreadsheetDateTimeParsePatterns dateTimeParser,
                                 final SpreadsheetFormatter numberFormatter,
                                 final SpreadsheetNumberParsePatterns numberParser,
                                 final SpreadsheetFormatter textFormatter,
                                 final SpreadsheetFormatter timeFormatter,
                                 final SpreadsheetTimeParsePatterns timeParser,
                                 final long dateOffset,
                                 final ExpressionNumberKind expressionNumberKind) {
        super();

        final LocalDate dateTrue = LocalDate.ofEpochDay(dateOffset + 1);
        final LocalDate dateFalse = LocalDate.ofEpochDay(dateOffset);

        final LocalDateTime dateTimeTrue = dateTime(dateTrue);
        final LocalDateTime dateTimeFalse = dateTime(dateFalse);

        final String stringTrue = Boolean.TRUE.toString();
        final String stringFalse = Boolean.FALSE.toString();

        final LocalTime timeTrue = LocalTime.ofSecondOfDay(1);
        final LocalTime timeFalse = LocalTime.MIDNIGHT;

        // wrap all number from/to converters to also handle ExpressionNumber

        // boolean ->
        final SpreadsheetConverterMapping<Converter> booleanConverter = SpreadsheetConverterMapping.with(
                Converters.simple(), // boolean -> boolean
                fromBoolean(LocalDate.class, dateTrue, dateFalse),
                fromBoolean(LocalDateTime.class, dateTimeTrue, dateTimeFalse),
                expressionNumberKind.toConverter(Converters.booleanNumber()),
                SpreadsheetConverterBooleanString.with(fromBoolean(String.class, stringTrue, stringFalse), textFormatter.converter()), // boolean -> String
                fromBoolean(LocalTime.class, timeTrue, timeFalse)); // Time

        // LocalDate ->
        final SpreadsheetConverterMapping<Converter> date = SpreadsheetConverterMapping.with(
                toBoolean(LocalDate.class, dateFalse),
                null, // date -> date
                Converters.localDateLocalDateTime(),
                expressionNumberKind.toConverter(Converters.localDateNumber(dateOffset)),
                dateFormatter.converter(),
                null); // date -> time INVALID

        // LocalDateTime ->
        final SpreadsheetConverterMapping<Converter> dateTime = SpreadsheetConverterMapping.with(
                toBoolean(LocalDateTime.class, dateTimeFalse),
                Converters.localDateTimeLocalDate(),
                null, // dateTime -> dateTime
                expressionNumberKind.toConverter(Converters.localDateTimeNumber(dateOffset)),
                dateTimeFormatter.converter(),
                Converters.localDateTimeLocalTime());

        // Number ->
        final SpreadsheetConverterMapping<Converter> number = SpreadsheetConverterMapping.with(
                ExpressionNumber.fromExpressionNumberConverter(Converters.truthyNumberBoolean()),
                ExpressionNumber.fromExpressionNumberConverter(Converters.numberLocalDate(dateOffset)),
                ExpressionNumber.fromExpressionNumberConverter(Converters.numberLocalDateTime(dateOffset)),
                expressionNumberKind.toConverter(ExpressionNumber.fromExpressionNumberConverter(Converters.numberNumber())),
                ExpressionNumber.fromExpressionNumberConverter(numberFormatter.converter()),
                ExpressionNumber.fromExpressionNumberConverter(Converters.numberLocalTime()));

        // String ->
        final SpreadsheetConverterMapping<Converter> string = SpreadsheetConverterMapping.with(
                toBoolean(String.class, stringFalse), // string -> boolean
                dateParser.converter(),
                dateTimeParser.converter(),
                expressionNumberKind.toConverter(numberParser.converter()),
                textFormatter.converter(), // String -> String
                timeParser.converter()
        );

        // LocalTime ->
        final SpreadsheetConverterMapping<Converter> time = SpreadsheetConverterMapping.with(toBoolean(LocalTime.class, timeFalse),
                null, // time -> date invalid
                Converters.localDateTimeLocalTime(),
                ExpressionNumber.fromExpressionNumberConverter(Converters.localTimeNumber()),
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
    private static <T> Converter fromBoolean(final Class<T> targetType,
                                             final T trueValue,
                                             final T falseValue) {
        return booleanTrueFalseConverter(Boolean.class,
                Boolean.FALSE,
                targetType,
                trueValue,
                falseValue);
    }

    private static <T> Converter toBoolean(final Class<T> from,
                                           final T falseValue) {
        return booleanTrueFalseConverter(from,
                falseValue,
                Boolean.class,
                Boolean.TRUE,
                Boolean.FALSE);
    }

    private static <T> Converter booleanTrueFalseConverter(final Class<?> fromType,
                                                           final Object falseValueTest,
                                                           final Class<T> targetType,
                                                           final T trueValueResult,
                                                           final T falseValueResult) {
        return Converters.booleanTrueFalse(t -> t.getClass() == fromType,
                Predicates.is(falseValueTest),
                Predicates.is(targetType),
                trueValueResult,
                falseValueResult);
    }

    // booleanTrueFalse(Predicate<Object> source, Predicate<Object> falseValue, Predicate<Class<?>> target, D trueAnswer, D falseAnswer)

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
            ExpressionNumber.class,
            ExpressionNumberKind.BIG_DECIMAL.create(1).getClass(),
            ExpressionNumberKind.DOUBLE.create(1).getClass(),
            String.class);

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> targetType,
                                         final ConverterContext context) {
        final Converter converter = SpreadsheetConverterSpreadsheetValueVisitor.converter(value, targetType, this.mapping);
        return null != converter ?
                converter.convert(value, targetType, context) :
                this.failConversion(value, targetType);
    }

    private final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> mapping;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
