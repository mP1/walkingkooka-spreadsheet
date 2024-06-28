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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link Converter} that supports converting all the spreadsheet types to other types.
 */
final class GeneralSpreadsheetConverter implements Converter<SpreadsheetConverterContext> {

    /**
     * Factory that creates a new {@link GeneralSpreadsheetConverter}.
     */
    static GeneralSpreadsheetConverter with(final SpreadsheetFormatter dateFormatter,
                                            final Parser<SpreadsheetParserContext> dateParser,
                                            final SpreadsheetFormatter dateTimeFormatter,
                                            final Parser<SpreadsheetParserContext> dateTimeParser,
                                            final SpreadsheetFormatter numberFormatter,
                                            final Parser<SpreadsheetParserContext> numberParser,
                                            final SpreadsheetFormatter textFormatter,
                                            final SpreadsheetFormatter timeFormatter,
                                            final Parser<SpreadsheetParserContext> timeParser,
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

        return new GeneralSpreadsheetConverter(
                dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
                timeFormatter,
                timeParser,
                dateOffset
        );
    }

    private GeneralSpreadsheetConverter(final SpreadsheetFormatter dateFormatter,
                                        final Parser<SpreadsheetParserContext> dateParser,
                                        final SpreadsheetFormatter dateTimeFormatter,
                                        final Parser<SpreadsheetParserContext> dateTimeParser,
                                        final SpreadsheetFormatter numberFormatter,
                                        final Parser<SpreadsheetParserContext> numberParser,
                                        final SpreadsheetFormatter textFormatter,
                                        final SpreadsheetFormatter timeFormatter,
                                        final Parser<SpreadsheetParserContext> timeParser,
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

        // wrap all number parse/to converters to also handle ExpressionNumber

        // boolean ->
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> booleanTo = mapping(
                Converters.simple(), // boolean -> boolean
                booleanTo(LocalDate.class, dateTrue, dateFalse),
                booleanTo(LocalDateTime.class, dateTimeTrue, dateTimeFalse),
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.booleanToNumber()),
                null, // selection
                GeneralSpreadsheetConverterBooleanString.with(
                        booleanTo(
                                String.class,
                                stringTrue,
                                stringFalse
                        ),
                        textFormatter.converter()
                                .cast(SpreadsheetConverterContext.class)
                ), // boolean -> String
                booleanTo(LocalTime.class, timeTrue, timeFalse)
        ); // Time

        this.booleanConverter = booleanTo;

        // LocalDate ->
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> dateTo = mapping(
                toBoolean(LocalDate.class, dateTrue),
                Converters.simple(), // date -> date
                Converters.localDateToLocalDateTime(),
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localDateToNumber(dateOffset)),
                null, // selection
                dateFormatter.converter()
                        .cast(SpreadsheetConverterContext.class),
                null // date -> time INVALID
        );

        // LocalDateTime ->
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> dateTimeTo = mapping(
                toBoolean(LocalDateTime.class, dateTimeTrue),
                Converters.localDateTimeToLocalDate(),
                Converters.simple(), // dateTime -> dateTime
                ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localDateTimeToNumber(dateOffset)),
                null, // selection
                dateTimeFormatter.converter()
                        .cast(SpreadsheetConverterContext.class),
                Converters.localDateTimeToLocalTime()
        );

        // Number ->
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> numberTo = mapping(
                ExpressionNumberConverters.numberOrExpressionNumberTo(Converters.numberToBoolean()),
                ExpressionNumberConverters.numberOrExpressionNumberTo(Converters.numberToLocalDate(dateOffset)),
                ExpressionNumberConverters.numberOrExpressionNumberTo(Converters.numberToLocalDateTime(dateOffset)),
                ExpressionNumberConverters.toNumberOrExpressionNumber(
                        ExpressionNumberConverters.numberOrExpressionNumberTo(
                                Converters.numberToNumber()
                        )
                ),
                null, // selection
                ExpressionNumberConverters.numberOrExpressionNumberTo(
                        numberFormatter.converter()
                ).cast(SpreadsheetConverterContext.class),
                ExpressionNumberConverters.numberOrExpressionNumberTo(
                        Converters.numberToLocalTime()
                )
        );

        // selection
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> selectionTo = GeneralSpreadsheetConverterMapping.with(
                null, // boolean
                null, // date
                null, // date-time
                null, // number
                SELECTION_TO_SELECTION, // selection
                Converters.objectToString(), // string
                null // time
        );

        // most attempts to support conversions such as Date -> Character are pointless but keep for the error failures.
        // String|Character ->
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> stringTo = GeneralSpreadsheetConverterMapping.with(
                characterOrStringTo(
                        toBoolean(String.class, stringTrue)
                ), // string -> boolean
                characterOrStringTo(
                        SpreadsheetConverters.stringToDate(dateParser)
                ),
                characterOrStringTo(
                        SpreadsheetConverters.stringToDateTime(dateTimeParser)
                ),
                characterOrStringTo(
                        ExpressionNumberConverters.toExpressionNumberThen(
                                SpreadsheetConverters.stringToExpressionNumber(
                                        numberParser
                                ),
                                ExpressionNumberConverters.numberOrExpressionNumberTo(
                                        Converters.numberToNumber()
                                )
                        )
                ),
                characterOrStringTo(
                        SpreadsheetConverters.stringToSelection()
                ), // selection
                characterOrStringTo(
                        toCharacterOrString(
                                Converters.simple() // String -> String
                        )
                ),
                characterOrStringTo(
                        SpreadsheetConverters.stringToTime(timeParser)
                )
        );

        // LocalTime ->
        final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> timeTo = mapping(
                toBoolean(LocalTime.class, timeTrue),
                null, // time -> date invalid
                Converters.localTimeToLocalDateTime(),
                ExpressionNumberConverters.toNumberOrExpressionNumber(
                        Converters.localTimeToNumber()
                ),
                null, // selection
                timeFormatter.converter()
                        .cast(SpreadsheetConverterContext.class),
                Converters.simple()
        ); // time -> time

        this.mapping = GeneralSpreadsheetConverterMapping.with(
                booleanTo,
                dateTo,
                dateTimeTo,
                numberTo,
                selectionTo,
                stringTo,
                timeTo
        );
    }

    /**
     * Creates a {@link Converter} that converts {@link Boolean} values to the given type.
     */
    private static <T> Converter<SpreadsheetConverterContext> booleanTo(final Class<T> targetType,
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

    private static <T> Converter<SpreadsheetConverterContext> toBoolean(final Class<T> from,
                                                                        final T trueValue) {
        return booleanTrueFalseConverter(
                from,
                Boolean.class,
                trueValue,
                Boolean.TRUE,
                Boolean.FALSE
        );
    }

    private static <T> Converter<SpreadsheetConverterContext> booleanTrueFalseConverter(final Class<?> fromType,
                                                                                        final Class<T> targetType,
                                                                                        final Object falseValueTest,
                                                                                        final T falseValueResult,
                                                                                        final T trueValueResult) {
        return Converters.toBoolean(
                t -> t.getClass() == fromType,
                Predicates.is(targetType),
                Predicates.is(falseValueTest),
                falseValueResult,
                trueValueResult
        );
    }

    /**
     * Adds support for Character or String to Character or String.
     */
    private static Converter<SpreadsheetConverterContext> characterOrStringTo(final Converter<? extends ExpressionNumberConverterContext> converter) {
        return Converters.chain(
                Converters.characterOrStringToString(),
                String.class,
                converter
        ).cast(SpreadsheetConverterContext.class);
    }

    /**
     * Adds support for converting to String and then maybe Character.
     */
    private static Converter<SpreadsheetConverterContext> toCharacterOrString(final Converter<? extends ExpressionNumberConverterContext> converter) {
        return Converters.chain(
                converter,
                String.class,
                Converters.stringToCharacterOrString()
        ).cast(SpreadsheetConverterContext.class);
    }

    private static LocalDateTime dateTime(final LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIDNIGHT);
    }

    private static GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> mapping(
            final Converter<SpreadsheetConverterContext> booleanValue,
            final Converter<SpreadsheetConverterContext> date,
            final Converter<SpreadsheetConverterContext> dateTime,
            final Converter<SpreadsheetConverterContext> number,
            final Converter<SpreadsheetConverterContext> selection,
            final Converter<SpreadsheetConverterContext> string,
            final Converter<SpreadsheetConverterContext> time) {
        return GeneralSpreadsheetConverterMapping.with(
                booleanValue,
                date,
                dateTime,
                number,
                selection,
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
                              final SpreadsheetConverterContext context) {
        return isNonNullAndValueIsInstanceofType(value, targetType) ||
                isSupportedValueAndType(value, targetType) ||
                SELECTION_TO_SELECTION.canConvert(value, targetType, context) ||
                StringToSpreadsheetSelectionConverter.INSTANCE.canConvert(value, targetType, context);
    }

    private static boolean isNonNullAndValueIsInstanceofType(final Object value,
                                                             final Class<?> targetType) {
        return null != value && value.getClass() == targetType;
    }

    private static boolean isSupportedValueAndType(final Object value,
                                                   final Class<?> targetType) {
        return isSupportedType(targetType) &&
                false == isDateToTime(value, targetType) &&
                false == isTimeToDate(value, targetType);
    }

    private static boolean isDateToTime(final Object value,
                                        final Class<?> targetType) {
        return value instanceof LocalTime && targetType == LocalDate.class;
    }

    private static boolean isTimeToDate(final Object value,
                                        final Class<?> targetType) {
        return value instanceof LocalTime && targetType == LocalDate.class;
    }

    private static boolean isSupportedType(final Class<?> type) {
        return Object.class == type ||
                Boolean.class == type ||
                Character.class == type ||
                LocalDate.class == type ||
                LocalDateTime.class == type ||
                LocalTime.class == type ||
                ExpressionNumber.isClass(type) ||
                Number.class == type ||
                SpreadsheetError.class == type ||
                String.class == type;
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> targetType,
                                         final SpreadsheetConverterContext context) {
        // special case if targetType = Object just return value.
        return this.canConvert(value, targetType, context) ?
                Object.class == targetType ?
                        this.successfulConversion(value, targetType) :
                        null == value ?
                                this.convertNull(targetType, context) :
                                this.convertNonNull(value, targetType, context) :
                this.failConversion(value, targetType);
    }

    private <T> Either<T, String> convertNull(final Class<T> targetType,
                                              final SpreadsheetConverterContext context) {
        return GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor.converter(
                this.booleanConverter,
                targetType
        ).convert(null, targetType, context);
    }

    private final GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>> booleanConverter;

    private <T> Either<T, String> convertNonNull(final Object value,
                                                 final Class<T> targetType,
                                                 final SpreadsheetConverterContext context) {
        return value.getClass() == targetType ?
                this.successfulConversion(value, targetType) :
                convertNonNull0(
                        value,
                        targetType,
                        context
                );
    }

    private <T> Either<T, String> convertNonNull0(final Object value,
                                                  final Class<T> targetType,
                                                  final SpreadsheetConverterContext context) {
        final Converter<SpreadsheetConverterContext> converter = GeneralSpreadsheetConverterSpreadsheetValueVisitor.converter(
                value,
                targetType,
                this.mapping
        );
        return null != converter ?
                converter.convert(value, targetType, context) :
                this.failConversion(value, targetType);
    }

    private final GeneralSpreadsheetConverterMapping<GeneralSpreadsheetConverterMapping<Converter<SpreadsheetConverterContext>>> mapping;

    /**
     * Singleton
     */
    private final static Converter<SpreadsheetConverterContext> SELECTION_TO_SELECTION = SpreadsheetConverters.selectionToSelection();

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
