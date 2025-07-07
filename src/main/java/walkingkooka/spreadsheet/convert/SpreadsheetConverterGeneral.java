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
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link Converter} that supports converting all basic types between each other.
 * Types like {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection} and {@link walkingkooka.net.Url} are not supported.
 */
final class SpreadsheetConverterGeneral extends SpreadsheetConverter {

    /**
     * Factory that creates a new {@link SpreadsheetConverterGeneral}.
     */
    static SpreadsheetConverterGeneral with(final SpreadsheetFormatter dateFormatter,
                                            final Parser<SpreadsheetParserContext> dateParser,
                                            final SpreadsheetFormatter dateTimeFormatter,
                                            final Parser<SpreadsheetParserContext> dateTimeParser,
                                            final SpreadsheetFormatter numberFormatter,
                                            final Parser<SpreadsheetParserContext> numberParser,
                                            final SpreadsheetFormatter textFormatter,
                                            final SpreadsheetFormatter timeFormatter,
                                            final Parser<SpreadsheetParserContext> timeParser) {
        Objects.requireNonNull(dateFormatter, "dateFormatter");
        Objects.requireNonNull(dateParser, "dateParser");

        Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");
        Objects.requireNonNull(dateTimeParser, "dateTimeParser");

        Objects.requireNonNull(numberFormatter, "numberFormatter");
        Objects.requireNonNull(numberParser, "numberParser");

        Objects.requireNonNull(textFormatter, "textFormatter");

        Objects.requireNonNull(timeFormatter, "timeFormatter");
        Objects.requireNonNull(timeParser, "timeParser");

        return new SpreadsheetConverterGeneral(
            dateFormatter,
            dateParser,
            dateTimeFormatter,
            dateTimeParser,
            numberFormatter,
            numberParser,
            textFormatter,
            timeFormatter,
            timeParser
        );
    }

    private SpreadsheetConverterGeneral(final SpreadsheetFormatter dateFormatter,
                                        final Parser<SpreadsheetParserContext> dateParser,
                                        final SpreadsheetFormatter dateTimeFormatter,
                                        final Parser<SpreadsheetParserContext> dateTimeParser,
                                        final SpreadsheetFormatter numberFormatter,
                                        final Parser<SpreadsheetParserContext> numberParser,
                                        final SpreadsheetFormatter textFormatter,
                                        final SpreadsheetFormatter timeFormatter,
                                        final Parser<SpreadsheetParserContext> timeParser) {
        super();

        // wrap all number parse/to converters to also handle ExpressionNumber

        // boolean ->
        final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> booleanTo = mapping(
            Converters.simple(), // boolean -> boolean
            Converters.booleanToNumber()
                .to(Integer.class, Converters.numberToLocalDate())
                .cast(SpreadsheetConverterContext.class),
            Converters.booleanToNumber()
                .to(Integer.class, Converters.numberToLocalDateTime())
                .cast(SpreadsheetConverterContext.class),
            ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.booleanToNumber()),
            SpreadsheetConverterGeneralBooleanString.with(
                booleanTo(
                    String.class,
                    TRUE_TO_STRING,
                    FALSE_TO_STRING
                ),
                textFormatter.converter()
                    .cast(SpreadsheetConverterContext.class)
            ), // boolean -> String
            booleanTo(LocalTime.class, TRUE_TO_TIME, FALSE_TO_TIME)
        ); // Time

        // LocalDate ->
        final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> dateTo = mapping(
            Converters.localDateToNumber()
                .to(
                    Integer.class,
                    Converters.numberToBoolean()
                ).cast(SpreadsheetConverterContext.class),
            Converters.simple(), // date -> date
            Converters.localDateToLocalDateTime(),
            ExpressionNumberConverters.toNumberOrExpressionNumber(
                Converters.localDateToNumber()
            ),
            dateFormatter.converter()
                .cast(SpreadsheetConverterContext.class),
            null // date -> time INVALID
        );

        // LocalDateTime ->
        final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> dateTimeTo = mapping(
            Converters.localDateTimeToNumber()
                .to(
                    Integer.class,
                    Converters.numberToBoolean()
                ).cast(SpreadsheetConverterContext.class),
            Converters.localDateTimeToLocalDate(),
            Converters.simple(), // dateTime -> dateTime
            ExpressionNumberConverters.toNumberOrExpressionNumber(
                Converters.localDateTimeToNumber()
            ),
            dateTimeFormatter.converter()
                .cast(SpreadsheetConverterContext.class),
            Converters.localDateTimeToLocalTime()
        );

        // Number ->
        final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> numberTo = mapping(
            ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                .to(
                    Number.class,
                    Converters.numberToBoolean()
                ).cast(SpreadsheetConverterContext.class),
            ExpressionNumberConverters.numberOrExpressionNumberToNumber().to(
                Number.class,
                Converters.numberToLocalDate()
            ).cast(SpreadsheetConverterContext.class),
            ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                .to(
                    Number.class,
                    Converters.numberToLocalDateTime()
                ).cast(SpreadsheetConverterContext.class),
            ExpressionNumberConverters.toNumberOrExpressionNumber(
                ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                    .to(
                        Number.class,
                        Converters.numberToNumber()
                    )
            ).cast(SpreadsheetConverterContext.class),
            ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                .cast(SpreadsheetConverterContext.class)
                .to(
                    Number.class,
                    numberFormatter.converter()
                ),
            ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                .to(
                    Number.class,
                    Converters.numberToLocalTime()
                ).cast(SpreadsheetConverterContext.class)
        );

        // most attempts to support conversions such as Date -> Character are pointless but keep for the error failures.
        // String|Character ->
        final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> stringTo = SpreadsheetConverterGeneralMapping.with(
            characterOrStringTo(
                toBoolean(
                    String.class,
                    TRUE_TO_STRING
                )
            ), // string -> boolean
            characterOrStringTo(
                SpreadsheetConverters.textToDate(dateParser)
            ),
            characterOrStringTo(
                SpreadsheetConverters.textToDateTime(dateTimeParser)
            ),
            characterOrStringTo(
                ExpressionNumberConverters.toExpressionNumberThen(
                    SpreadsheetConverters.textToNumber(
                        numberParser
                    ),
                    ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                        .to(
                            Number.class,
                            Converters.numberToNumber()
                        ).cast(SpreadsheetConverterContext.class)
                )
            ),
            characterOrStringTo(
                toCharacterOrString(
                    Converters.simple() // String -> String
                )
            ),
            characterOrStringTo(
                SpreadsheetConverters.textToTime(timeParser)
            )
        );

        // LocalTime ->
        final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> timeTo = mapping(
            toBoolean(
                LocalTime.class,
                TRUE_TO_TIME
            ),
            null, // time -> date invalid
            Converters.localTimeToLocalDateTime(),
            ExpressionNumberConverters.toNumberOrExpressionNumber(
                Converters.localTimeToNumber()
            ),
            timeFormatter.converter()
                .cast(SpreadsheetConverterContext.class),
            Converters.simple()
        ); // time -> time

        this.mapping = SpreadsheetConverterGeneralMapping.with(
            booleanTo,
            dateTo,
            dateTimeTo,
            numberTo,
            stringTo,
            timeTo
        );
    }

    private final static LocalTime TRUE_TO_TIME = LocalTime.ofSecondOfDay(1);

    private final static LocalTime FALSE_TO_TIME = LocalTime.MIDNIGHT;

    private final static String TRUE_TO_STRING = Boolean.TRUE.toString();

    private final static String FALSE_TO_STRING = Boolean.FALSE.toString();

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
     * Converters any {@link Character} to {@link String} if necessary before calling the given {@link Converter}.
     */
    private static Converter<SpreadsheetConverterContext> characterOrStringTo(final Converter<SpreadsheetConverterContext> converter) {
        return Converters.characterOrStringToString()
            .cast(SpreadsheetConverterContext.class)
            .to(
                String.class,
                converter
            );
    }

    /**
     * Wraps a {@link Converter} converting any {@link String} to {@link Character} if necessary.
     */
    private static Converter<SpreadsheetConverterContext> toCharacterOrString(final Converter<SpreadsheetConverterContext> converter) {
        return converter.to(
            String.class,
            Converters.stringToCharacterOrString()
        );
    }

    private static SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> mapping(
        final Converter<SpreadsheetConverterContext> booleanValue,
        final Converter<SpreadsheetConverterContext> date,
        final Converter<SpreadsheetConverterContext> dateTime,
        final Converter<SpreadsheetConverterContext> number,
        final Converter<SpreadsheetConverterContext> string,
        final Converter<SpreadsheetConverterContext> time) {
        return SpreadsheetConverterGeneralMapping.with(
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
                              final SpreadsheetConverterContext context) {
        return isNonNullAndValueIsInstanceofType(
            value,
            targetType,
            context
        ) ||
            isSupportedValueAndType(
                value,
                targetType
            );
    }

    private static boolean isNonNullAndValueIsInstanceofType(final Object value,
                                                             final Class<?> targetType,
                                                             final SpreadsheetConverterContext context) {
        return null != value &&
            INSTANCE_OF.canConvert(
                value,
                targetType,
                context
            );
    }

    private static boolean isSupportedValueAndType(final Object value,
                                                   final Class<?> targetType) {
        return isSupportedType(targetType) &&
            false == isDateToTime(
                value,
                targetType
            ) &&
            false == isTimeToDate(
                value,
                targetType
            );
    }

    private static boolean isDateToTime(final Object value,
                                        final Class<?> targetType) {
        return value instanceof LocalTime &&
            targetType == LocalDate.class;
    }

    private static boolean isTimeToDate(final Object value,
                                        final Class<?> targetType) {
        return value instanceof LocalTime &&
            targetType == LocalDate.class;
    }

    private static boolean isDateOrDateTimeOrTimeOrNumber(final Class<?> type) {
        return LocalDate.class == type ||
            LocalDateTime.class == type ||
            LocalTime.class == type ||
            ExpressionNumber.isClass(type) ||
            Number.class == type;
    }

    private static boolean isSupportedType(final Class<?> type) {
        return Object.class == type ||
            Boolean.class == type ||
            Character.class == type ||
            String.class == type ||
            isDateOrDateTimeOrTimeOrNumber(type);
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> targetType,
                                           final SpreadsheetConverterContext context) {
        // special case if targetType = Object just return value.
        return Object.class == targetType ?
            this.successfulConversion(
                value,
                targetType
            ) :
            null == value ?
                this.convertNull(
                    targetType,
                    context
                ) :
                this.convertNonNull(
                    value,
                    targetType,
                    context
                );
    }

    private <T> Either<T, String> convertNull(final Class<T> targetType,
                                              final SpreadsheetConverterContext context) {
        return this.successfulConversion(
            SpreadsheetConverterGeneralNullSpreadsheetValueTypeVisitor.convertNull(
                targetType,
                context
            ),
            targetType
        );
    }

    private <T> Either<T, String> convertNonNull(final Object value,
                                                 final Class<T> targetType,
                                                 final SpreadsheetConverterContext context) {
        return INSTANCE_OF.canConvert(
            value,
            targetType,
            context
        ) ?
            this.successfulConversion(
                value,
                targetType
            ) :
            TRUE_TO_STRING.equals(value) && isDateOrDateTimeOrTimeOrNumber(targetType) ?
                this.convertNonNull0(
                    true,
                    targetType,
                    context
                ) :
                FALSE_TO_STRING.equals(value) && isDateOrDateTimeOrTimeOrNumber(targetType) ?
                    this.convertNonNull0(
                        false,
                        targetType,
                        context
                    ) :
                    this.convertNonNull0(
                        value,
                        targetType,
                        context
                    );
    }

    /**
     * Used to check that a value is an instanceof the target type.
     */
    private final static Converter<SpreadsheetConverterContext> INSTANCE_OF = Converters.simple();

    private <T> Either<T, String> convertNonNull0(final Object value,
                                                  final Class<T> targetType,
                                                  final SpreadsheetConverterContext context) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverterGeneralSpreadsheetValueVisitor.converter(
            value,
            targetType,
            this.mapping
        );
        return null != converter ?
            converter.convert(
                value,
                targetType,
                context
            ) :
            this.failConversion(
                value,
                targetType
            );
    }

    private final SpreadsheetConverterGeneralMapping<SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>>> mapping;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
