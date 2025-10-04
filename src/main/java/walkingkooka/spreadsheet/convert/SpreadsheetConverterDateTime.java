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
import walkingkooka.tree.expression.ExpressionNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link Converter} that handles converting {@link Boolean}, {@link LocalDate}, {@link LocalDateTime},
 * {@link Number} to any of the following {@link LocalDate}, {@link LocalDateTime} or {@link LocalTime}.
 */
final class SpreadsheetConverterDateTime extends SpreadsheetConverter {

    /**
     * Factory that creates a {@link SpreadsheetConverterDateTime}.
     */
    static SpreadsheetConverterDateTime with(final Converter<SpreadsheetConverterContext> dateToString,
                                             final Converter<SpreadsheetConverterContext> dateTimeToString,
                                             final Converter<SpreadsheetConverterContext> timeToString,
                                             final Converter<SpreadsheetConverterContext> stringToDate,
                                             final Converter<SpreadsheetConverterContext> stringToDateTime,
                                             final Converter<SpreadsheetConverterContext> stringToTime) {
        return new SpreadsheetConverterDateTime(
            Objects.requireNonNull(dateToString, "dateToString"),
            Objects.requireNonNull(dateTimeToString, "dateTimeToString"),
            Objects.requireNonNull(timeToString, "timeToString"),
            Objects.requireNonNull(stringToDate, "stringToDate"),
            Objects.requireNonNull(stringToDateTime, "stringToDateTime"),
            Objects.requireNonNull(stringToTime, "stringToTime")
        );
    }

    private SpreadsheetConverterDateTime(final Converter<SpreadsheetConverterContext> dateToString,
                                         final Converter<SpreadsheetConverterContext> dateTimeToString,
                                         final Converter<SpreadsheetConverterContext> timeToString,
                                         final Converter<SpreadsheetConverterContext> stringToDate,
                                         final Converter<SpreadsheetConverterContext> stringToDateTime,
                                         final Converter<SpreadsheetConverterContext> stringToTime) {
        super();

        this.dateToString = dateToString;
        this.dateTimeToString = dateTimeToString;
        this.timeToString = timeToString;

        this.stringToDate = stringToDate;
        this.stringToDateTime = stringToDateTime;
        this.stringToTime = stringToTime;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        boolean can = false;

        // Number -> Date | DateTime | Time
        if (ExpressionNumber.is(value)) {
            can = isDateDateTimeOrTimeClass(type);

        } else {
            // Boolean  -> Date | DateTime | Time
            if (value instanceof Boolean) {

                can = isDateDateTimeOrTimeClass(type);
            } else {
                // Date -> DateTime | Number | String
                if (value instanceof LocalDate) {
                    can = LocalDate.class == type || LocalDateTime.class == type || ExpressionNumber.isClass(type) || String.class == type;
                } else {
                    // Date | DateTime | Time -> DateTime | Number | String
                    if (value instanceof LocalDateTime) {
                        can = LocalDate.class == type || LocalDateTime.class == type || LocalTime.class == type || ExpressionNumber.isClass(type) || String.class == type;
                    } else {
                        // Time -> Boolean | DateTime | Time | Number | String
                        if (value instanceof LocalTime) {
                            can = Boolean.class == type || LocalDateTime.class == type || LocalTime.class == type || ExpressionNumber.isClass(type) || String.class == type;
                        } else {
                            if(value instanceof String) {
                                can = isDateDateTimeOrTimeClass(type);
                            }
                        }
                    }
                }
            }
        }
        return can;
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        Either<T, String> result = null;

        if (value.getClass() == type) {
            result = this.successfulConversion(
                value,
                type
            );
        } else {
            if (value instanceof LocalDate && String.class == type) {
                result = this.dateToString.convert(
                    value,
                    type,
                    context
                );
            } else {
                if (value instanceof LocalDateTime && String.class == type) {
                    result = this.dateTimeToString.convert(
                        value,
                        type,
                        context
                    );
                } else {
                    if (value instanceof LocalTime && String.class == type) {
                        result = this.timeToString.convert(
                            value,
                            type,
                            context
                        );
                    } else {
                        if (value instanceof LocalDateTime && LocalTime.class == type) {
                            result = this.successfulConversion(
                                ((LocalDateTime) value).toLocalTime(),
                                type
                            );
                        } else {
                            if (value instanceof String && LocalDate.class == type) {
                                result = this.stringToDate.convert(
                                    value,
                                    type,
                                    context
                                );
                            } else {
                                if (value instanceof String && LocalDateTime.class == type) {
                                    result = this.stringToDateTime.convert(
                                        value,
                                        type,
                                        context
                                    );
                                } else {
                                    if (value instanceof String && LocalTime.class == type) {
                                        result = this.stringToTime.convert(
                                            value,
                                            type,
                                            context
                                        );
                                    } else {
                                        Number number = null;

                                        boolean valueIsBoolean = value instanceof Boolean;
                                        boolean valueIsNumber = false;

                                        // Boolean -> Date | DateTime | Time
                                        if (valueIsBoolean) {
                                            number = Boolean.TRUE.equals(value) ?
                                                1 :
                                                0;
                                        } else {
                                            valueIsNumber = ExpressionNumber.is(value);
                                            if (valueIsNumber) {
                                                // ExpressionNumber | Number -> Number
                                                number = value instanceof ExpressionNumber ?
                                                    ((ExpressionNumber) value).value() :
                                                    (Number) value;
                                            } else {
                                                // Date | DateTime | Time -> Number -> Date | DateTime | Time
                                                Converter<SpreadsheetConverterContext> converter;
                                                if (value instanceof LocalDate) {
                                                    converter = Converters.localDateToNumber();
                                                } else {
                                                    if (value instanceof LocalDateTime) {
                                                        converter = Converters.localDateTimeToNumber();
                                                    } else {
                                                        if (value instanceof LocalTime) {
                                                            converter = Converters.localTimeToNumber();
                                                        } else {
                                                            throw new IllegalArgumentException("Unsupported type: " + type);
                                                        }
                                                    }
                                                }

                                                number = converter.convert(
                                                    value,
                                                    Number.class, // Date | DateTime | Time -> a Number
                                                    context
                                                ).orElseLeft(null);
                                            }
                                        }

                                        if (isDateDateTimeOrTimeClass(type)) {
                                            // Boolean | Date | DateTime | Time -> Number -> Date | DateTime | Time
                                            result = this.toDateOrDateTimeOrTime(
                                                number,
                                                type,
                                                context
                                            );
                                        } else {
                                            // Boolean | Date | DateTime | Time -> Number
                                            if (valueIsBoolean || isDateDateTimeOrTime(value) || valueIsNumber) {
                                                result = context.convert(
                                                    number,
                                                    type
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (null == result) {
            result = this.failConversion(value, type);
        }

        return result;
    }

    private <T> Either<T, String> toDateOrDateTimeOrTime(final Number number,
                                                         final Class<T> type,
                                                         final SpreadsheetConverterContext context) {
        Converter<SpreadsheetConverterContext> converter;

        if (LocalDate.class == type) {
            converter = Converters.numberToLocalDate();
        } else {
            if (LocalDateTime.class == type) {
                converter = Converters.numberToLocalDateTime();
            } else {
                if (LocalTime.class == type) {
                    converter = Converters.numberToLocalTime();
                } else {
                    throw new IllegalArgumentException("Unsupported type: " + type);
                }
            }
        }

        // convert the number to Date | DateTime | Time
        return converter.convert(
            number,
            type,
            context
        );
    }

    private static boolean isDateDateTimeOrTime(final Object value) {
        return value instanceof LocalDate ||
            value instanceof LocalDateTime ||
            value instanceof LocalTime;
    }

    private static boolean isDateDateTimeOrTimeClass(final Class<?> type) {
        return LocalDate.class == type ||
            LocalDateTime.class == type ||
            LocalTime.class == type;
    }

    private final Converter<SpreadsheetConverterContext> dateToString;

    private final Converter<SpreadsheetConverterContext> dateTimeToString;

    private final Converter<SpreadsheetConverterContext> timeToString;

    private final Converter<SpreadsheetConverterContext> stringToDate;

    private final Converter<SpreadsheetConverterContext> stringToDateTime;

    private final Converter<SpreadsheetConverterContext> stringToTime;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.dateToString,
            this.dateTimeToString,
            this.timeToString,
            this.stringToDate,
            this.stringToDateTime,
            this.stringToTime
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetConverterDateTime && this.equals0((SpreadsheetConverterDateTime) other);
    }

    private boolean equals0(final SpreadsheetConverterDateTime other) {
        return this.dateToString.equals(other.dateToString) &&
            this.dateTimeToString.equals(other.dateTimeToString) &&
            this.timeToString.equals(other.timeToString) &&
            this.stringToDate.equals(other.stringToDate) &&
            this.stringToDateTime.equals(other.stringToDateTime) &&
            this.stringToTime.equals(other.stringToTime);
    }

    @Override
    public String toString() {
        return "dateTime";
    }
}
