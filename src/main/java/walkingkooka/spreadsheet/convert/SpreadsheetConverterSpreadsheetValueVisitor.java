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

import walkingkooka.ToStringBuilder;
import walkingkooka.convert.ConversionException;
import walkingkooka.convert.Converter;
import walkingkooka.convert.FailedConversionException;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;
import walkingkooka.text.CharSequences;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A {@link SpreadsheetValueVisitor} which accepts the source value being converted. Each visit method will then select a
 * {@link SpreadsheetConverterMapping}
 */
final class SpreadsheetConverterSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    /**
     * Uses the source value type and target type to pick a {@link Converter}.
     */
    static Converter converter(final Object value,
                               final Class<?> targetType,
                               final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> mapping) {

        final SpreadsheetConverterSpreadsheetValueVisitor visitor = new SpreadsheetConverterSpreadsheetValueVisitor(targetType,
                mapping);
        visitor.accept(value); // value = from
        final Converter converter = visitor.converter;
        if (null == converter) {
            throw new FailedConversionException(value, targetType);
        }
        return converter;
    }

    SpreadsheetConverterSpreadsheetValueVisitor(final Class<?> targetType,
                                                final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> mapping) {
        super();
        this.targetType = targetType;
        this.mapping = mapping;
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final BigInteger value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final Boolean value) {
        this.converter(this.mapping.booleanValue);
    }

    @Override
    protected void visit(final Byte value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final Double value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final Float value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final Integer value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final LocalDate value) {
        this.converter(this.mapping.date);
    }

    @Override
    protected void visit(final LocalDateTime value) {
        this.converter(this.mapping.dateTime);
    }

    @Override
    protected void visit(final LocalTime value) {
        this.converter(this.mapping.time);
    }

    @Override
    protected void visit(final Long value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final Short value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final String value) {
        this.converter(this.mapping.string);
    }

    @Override
    protected void visit(final Object value) {
        throw new ConversionException("Unable to convert " + CharSequences.quoteIfChars(value) + " to " + this.targetType.getName());
    }

    /**
     * The target type to convert the value to.
     */
    private final Class<?> targetType;

    private final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> mapping;

    private void converter(final SpreadsheetConverterMapping<Converter> converters) {
        this.converter = SpreadsheetConverterSpreadsheetValueTypeVisitor.converter(converters, this.targetType);
    }

    private Converter converter;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .separator(", ")
                .value(this.targetType.getSimpleName())
                .value(this.mapping)
                .build();
    }
}
