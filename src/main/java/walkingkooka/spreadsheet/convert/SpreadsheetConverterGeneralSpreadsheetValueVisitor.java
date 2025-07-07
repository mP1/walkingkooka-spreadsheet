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
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A {@link SpreadsheetValueVisitor} which accepts the source value being converted. Each visit method will then select a
 * {@link SpreadsheetConverterGeneralMapping}
 */
final class SpreadsheetConverterGeneralSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    /**
     * Uses the source value type and target type to pick a {@link Converter}.
     */
    static Converter<SpreadsheetConverterContext> converter(final Object value,
                                                            final Class<?> targetType,
                                                            final SpreadsheetConverterGeneralMapping<SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>>> mapping) {

        final SpreadsheetConverterGeneralSpreadsheetValueVisitor visitor = new SpreadsheetConverterGeneralSpreadsheetValueVisitor(
            targetType,
            mapping
        );
        visitor.accept(value);
        return visitor.converter;
    }

    SpreadsheetConverterGeneralSpreadsheetValueVisitor(final Class<?> targetType,
                                                       final SpreadsheetConverterGeneralMapping<SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>>> mapping) {
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
    protected void visit(final Character value) {
        this.converter(this.mapping.string);
    }

    @Override
    protected void visit(final Double value) {
        this.converter(this.mapping.number);
    }

    @Override
    protected void visit(final ExpressionNumber value) {
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

//    @Override
//    protected void visit(final SpreadsheetCellRangeReference value) {
//        // fail
//    }
//
//    @Override
//    protected void visit(final SpreadsheetCellReference value) {
//        // fail
//    }
//
//    @Override
//    protected void visit(final SpreadsheetColumnRangeReference value) {
//        // fail
//    }
//
//    @Override
//    protected void visit(final SpreadsheetColumnReference value) {
//        // fail
//    }

    @Override
    protected void visit(final SpreadsheetError error) {
        // fail
    }

//    @Override
//    protected void visit(final SpreadsheetLabelName value) {
//        // fail
//    }
//
//    @Override
//    protected void visit(final SpreadsheetRowRangeReference value) {
//        // fail
//    }
//
//    @Override
//    protected void visit(final SpreadsheetRowReference value) {
//        // fail
//    }

    @Override
    protected void visit(final String value) {
        this.converter(this.mapping.string);
    }

    @Override
    protected void visit(final Object value) {
        // fail!
    }

    @Override
    protected void visitNull() {
        // fail
    }

    /**
     * The target type to convert the value to.
     */
    private final Class<?> targetType;

    private final SpreadsheetConverterGeneralMapping<SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>>> mapping;

    private void converter(final SpreadsheetConverterGeneralMapping<Converter<SpreadsheetConverterContext>> converters) {
        this.converter = SpreadsheetConverterGeneralSpreadsheetValueTypeVisitor.converter(converters, this.targetType);
    }

    private Converter<SpreadsheetConverterContext> converter;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .separator(", ")
            .value(this.targetType.getSimpleName())
            .value(this.mapping)
            .build();
    }
}
