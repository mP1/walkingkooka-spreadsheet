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
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link Converter} that supports converting all the spreadsheet types to other types.
 * </pre>
 */
final class SpreadsheetConverter implements Converter<ExpressionNumberConverterContext> {

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
                                     final long dateOffset) {
        Objects.requireNonNull(dateFormatter, "dateFormatter");
        Objects.requireNonNull(dateParser, "dateParser");

        Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");
        Objects.requireNonNull(dateTimeParser, "dateTimeParser");

        Objects.requireNonNull(numberFormatter, "numberFormatter");
        Objects.requireNonNull(numberParser, "numberParser");

        Objects.requireNonNull(textFormatter, "textFormatter");

        Objects.requireNonNull(timeFormatter, "timeFormatter");
        Objects.requireNonNull(timeParser, "timeParser");

        return new SpreadsheetConverter(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
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
                                 final SpreadsheetFormatter textFormatter,
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

        // wrap all number from/to converters to also handle ExpressionNumber

        // boolean ->
        final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> booleanConverter = mapping(
                Converters.simple(), // boolean -> boolean
                fromBoolean(LocalDate.class, dateTrue, dateFalse),
                fromBoolean(LocalDateTime.class, dateTimeTrue, dateTimeFalse),
                ExpressionNumber.toConverter(Converters.booleanNumber()),
                SpreadsheetConverterBooleanString.with(fromBoolean(String.class, stringTrue, stringFalse), textFormatter.converter()), // boolean -> String
                fromBoolean(LocalTime.class, timeTrue, timeFalse)
        ); // Time

        this.booleanConverter = booleanConverter;

        // LocalDate ->
        final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> date = mapping(
                toBoolean(LocalDate.class, dateTrue),
                Converters.simple(), // date -> date
                Converters.localDateLocalDateTime(),
                ExpressionNumber.toConverter(Converters.localDateNumber(dateOffset)),
                dateFormatter.converter(),
                null // date -> time INVALID
        );

        // LocalDateTime ->
        final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> dateTime = mapping(
                toBoolean(LocalDateTime.class, dateTimeTrue),
                Converters.localDateTimeLocalDate(),
                Converters.simple(), // dateTime -> dateTime
                ExpressionNumber.toConverter(Converters.localDateTimeNumber(dateOffset)),
                dateTimeFormatter.converter(),
                Converters.localDateTimeLocalTime()
        );

        // Number ->
        final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> number = mapping(
                ExpressionNumber.fromConverter(Converters.truthyNumberBoolean()),
                ExpressionNumber.fromConverter(Converters.numberLocalDate(dateOffset)),
                ExpressionNumber.fromConverter(Converters.numberLocalDateTime(dateOffset)),
                ExpressionNumber.toConverter(ExpressionNumber.fromConverter(Converters.numberNumber())),
                ExpressionNumber.fromConverter(numberFormatter.converter()),
                ExpressionNumber.fromConverter(Converters.numberLocalTime()));

        // most attempts to support conversions such as Date -> Character are pointless but keep for the error failures.
        // String|Character ->
        final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> string = SpreadsheetConverterMapping.with(
                fromCharacterOrString(
                        toBoolean(String.class, stringTrue)
                ), // string -> boolean
                fromCharacterOrString(
                        dateParser.converter().cast(ExpressionNumberConverterContext.class)
                ),
                fromCharacterOrString(
                        dateTimeParser.converter().cast(ExpressionNumberConverterContext.class)
                ),
                fromCharacterOrString(
                        ExpressionNumber.toConverter(numberParser.converter().cast(ExpressionNumberConverterContext.class))
                ),
                fromCharacterOrString(
                        toCharacterOrString(
                                Converters.simple() // String -> String
                        )
                ),
                fromCharacterOrString(
                        timeParser.converter().cast(ExpressionNumberConverterContext.class)
                )
        );

        // LocalTime ->
        final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> time = mapping(
                toBoolean(LocalTime.class, timeTrue),
                null, // time -> date invalid
                Converters.localTimeLocalDateTime(),
                ExpressionNumber.toConverter(Converters.localTimeNumber()),
                timeFormatter.converter(),
                Converters.simple()
        ); // time -> time

        this.mapping = SpreadsheetConverterMapping.with(
                booleanConverter,
                date,
                dateTime,
                number,
                string,
                time
        );
    }

    /**
     * Creates a {@link Converter} that converts {@link Boolean} values to the given type.
     */
    private static <T> Converter<ExpressionNumberConverterContext> fromBoolean(final Class<T> targetType,
                                                                               final T trueValue,
                                                                               final T falseValue) {
        return booleanTrueFalseConverter(
                Boolean.class,
                targetType,
                Boolean.TRUE,
                trueValue,
                falseValue
        );
    }

    private static <T> Converter<ExpressionNumberConverterContext> toBoolean(final Class<T> from,
                                                                             final T trueValue) {
        return booleanTrueFalseConverter(from,
                Boolean.class,
                trueValue,
                Boolean.TRUE,
                Boolean.FALSE
        );
    }

    private static <T> Converter<ExpressionNumberConverterContext> booleanTrueFalseConverter(final Class<?> fromType,
                                                                                             final Class<T> targetType,
                                                                                             final Object falseValueTest,
                                                                                             final T falseValueResult,
                                                                                             final T trueValueResult) {
        return Converters.booleanTrueFalse(t -> t.getClass() == fromType,
                Predicates.is(targetType),
                Predicates.is(falseValueTest),
                falseValueResult,
                trueValueResult
        );
    }

    /**
     * Adds support for Character or String to Character or String.
     */
    private static Converter<ExpressionNumberConverterContext> fromCharacterOrString(final Converter<ExpressionNumberConverterContext> converter) {
        return Converters.characterStringConverter(
                converter
        );
    }

    /**
     * Adds support for converting to String and then maybe Character.
     */
    private static Converter<ExpressionNumberConverterContext> toCharacterOrString(final Converter<ExpressionNumberConverterContext> converter) {
        return Converters.converterStringCharacter(
                converter
        );
    }

    private static LocalDateTime dateTime(final LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIDNIGHT);
    }

    private static SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> mapping(
            final Converter<ExpressionNumberConverterContext> booleanValue,
            final Converter<ExpressionNumberConverterContext> date,
            final Converter<ExpressionNumberConverterContext> dateTime,
            final Converter<ExpressionNumberConverterContext> number,
            final Converter<ExpressionNumberConverterContext> string,
            final Converter<ExpressionNumberConverterContext> time) {
        return SpreadsheetConverterMapping.with(
                booleanValue,
                date,
                dateTime,
                number,
                toCharacterOrString(string),
                time
        );
    }

    // Converter........................................................................................................

    /**
     * All spreadsheet types may be converted to another except for {@link LocalTime} to {@link LocalDate} and vice versa.
     */
    @Override
    public boolean canConvert(final Object value,
                              final Class<?> targetType,
                              final ExpressionNumberConverterContext context) {
        return isSupportedType(targetType) &&
                false == (value instanceof LocalTime && targetType == LocalDate.class) &&
                false == (value instanceof LocalDate && targetType == LocalTime.class);
    }

    private static boolean isSupportedType(final Class<?> type) {
        return Boolean.class == type ||
                Character.class == type ||
                LocalDate.class == type ||
                LocalDateTime.class == type ||
                LocalTime.class == type ||
                ExpressionNumber.isClass(type) ||
                Number.class == type ||
                String.class == type;
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> targetType,
                                         final ExpressionNumberConverterContext context) {
        return this.canConvert(value, targetType, context) ?
                null == value ?
                        this.convertNull(targetType, context) :
                        this.convertNonNull(value, targetType, context) :
                this.failConversion(value, targetType);
    }

    private <T> Either<T, String> convertNull(final Class<T> targetType,
                                              final ExpressionNumberConverterContext context) {
        return SpreadsheetConverterSpreadsheetValueTypeVisitor.converter(
                this.booleanConverter,
                targetType
        ).convert(null, targetType, context);
    }

    /**
     * Handles converting null to the target type.
     */
    private final SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>> booleanConverter;

    private <T> Either<T, String> convertNonNull(final Object value,
                                                 final Class<T> targetType,
                                                 final ExpressionNumberConverterContext context) {
        final Converter<ExpressionNumberConverterContext> converter = SpreadsheetConverterSpreadsheetValueVisitor.converter(
                value,
                targetType,
                this.mapping
        );
        return null != converter ?
                converter.convert(value, targetType, context) :
                this.failConversion(value, targetType);
    }

    private final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter<ExpressionNumberConverterContext>>> mapping;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
