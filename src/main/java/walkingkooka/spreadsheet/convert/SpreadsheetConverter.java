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
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * A {@link Converter} that supports the 9 main types within a spreadsheet which are:
 * <ul>
 * <li>{@link Boolean}</li>
 * <li>{@link java.time.LocalDate}</li>
 * <li>{@link java.time.LocalDateTime}</li>
 * <li>{@link java.time.LocalTime}</li>
 * <li>{@link java.math.BigDecimal}</li>
 * <li>{@link java.math.BigInteger}</li>
 * <li>{@link Double}</li>
 * <li>{@link Long}</li>
 * <li>{@link String}</li>
 * </ul>
 * More types like {@link walkingkooka.net.Url} and {@link Color} may be added.
 * </pre>
 */
final class SpreadsheetConverter implements Converter {

    /**
     * Singleton. Has no state.
     */
    final static SpreadsheetConverter with(final long dateOffset,
                                           final String bigDecimalFormat,
                                           final String bigIntegerFormat,
                                           final String doubleFormat,
                                           final DateTimeFormatter date,
                                           final DateTimeFormatter dateTime,
                                           final DateTimeFormatter time,
                                           final String longFormat) {
        return new SpreadsheetConverter(dateOffset,
                bigDecimalFormat,
                bigIntegerFormat,
                doubleFormat,
                date,
                dateTime,
                time,
                longFormat);
    }

    private SpreadsheetConverter(final long dateOffset,
                                 final String bigDecimalFormat,
                                 final String bigIntegerFormat,
                                 final String doubleFormat,
                                 final DateTimeFormatter date,
                                 final DateTimeFormatter dateTime,
                                 final DateTimeFormatter time,
                                 final String longFormat) {
        super();
        this.mapping = SpreadsheetConverterMapping.with(dateOffset,
                bigDecimalFormat,
                bigIntegerFormat,
                doubleFormat,
                date,
                dateTime,
                time,
                longFormat);
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> targetType,
                              final ConverterContext context) {
        return SUPPORTED_TYPES.contains(targetType);
    }

    private final static Set<Class<?>> SUPPORTED_TYPES = Sets.of(Boolean.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            BigDecimal.class,
            BigInteger.class,
            Double.class,
            Long.class,
            Number.class,
            String.class);

    @Override
    public <T> T convert(final Object value,
                         final Class<T> targetType,
                         final ConverterContext context) {
        return SpreadsheetConverterSpreadsheetValueVisitor.converter(value, targetType, this.mapping)
                .convert(value, targetType, context);
    }

    private final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> mapping;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
