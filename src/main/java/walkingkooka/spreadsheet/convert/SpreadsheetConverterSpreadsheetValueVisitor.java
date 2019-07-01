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
                               final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> converters) {

        final SpreadsheetConverterSpreadsheetValueVisitor visitor = new SpreadsheetConverterSpreadsheetValueVisitor(targetType,
                converters);
        visitor.accept(value); // value = from
        return visitor.converter;
    }

    SpreadsheetConverterSpreadsheetValueVisitor(final Class<?> targetType,
                                                final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> all) {
        super();
        this.targetType = targetType;
        this.all = all;
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.converter(this.all.bigDecimal);
    }

    @Override
    protected void visit(final BigInteger value) {
        this.converter(this.all.bigInteger);
    }

    @Override
    protected void visit(final Boolean value) {
        this.converter(this.all.booleanValue);
    }

    @Override
    protected void visit(final Double value) {
        this.converter(this.all.doubleValue);
    }

    @Override
    protected void visit(final LocalDate value) {
        this.converter(this.all.localDate);
    }

    @Override
    protected void visit(final LocalDateTime value) {
        this.converter(this.all.localDateTime);
    }

    @Override
    protected void visit(final LocalTime value) {
        this.converter(this.all.localTime);
    }

    @Override
    protected void visit(final Long value) {
        this.converter(this.all.longValue);
    }

    @Override
    protected void visit(final String value) {
        this.converter(this.all.string);
    }

    @Override
    protected void visit(final Object value) {
        throw new ConversionException("Unable to convert " + CharSequences.quoteIfChars(value) + " to " + this.targetType.getName());
    }

    /**
     * The target type to convert the value to.
     */
    private final Class<?> targetType;

    private final SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> all;

    private void converter(final SpreadsheetConverterMapping<Converter> converters) {
        this.converter = SpreadsheetConverterSpreadsheetValueTypeVisitor.converter(converters, this.targetType);
    }

    private Converter converter;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.targetType.getSimpleName())
                .value(this.all)
                .build();
    }
}
