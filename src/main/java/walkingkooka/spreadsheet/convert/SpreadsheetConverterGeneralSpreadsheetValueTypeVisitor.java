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
import walkingkooka.convert.ConverterContext;
import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitor;
import walkingkooka.visit.Visitor;

/**
 * A {@link Visitor} that locates the appropriate {@link Converter} for a given source and target {@link Class}.
 */
final class SpreadsheetConverterGeneralSpreadsheetValueTypeVisitor<C extends ConverterContext> extends SpreadsheetValueTypeVisitor {

    static <C extends ConverterContext> Converter<C> converter(final SpreadsheetConverterGeneralMapping<Converter<C>> mapping,
                                                               final Class<?> targetType) {
        final SpreadsheetConverterGeneralSpreadsheetValueTypeVisitor<C> visitor = new SpreadsheetConverterGeneralSpreadsheetValueTypeVisitor<>(mapping);
        visitor.accept(targetType);
        return visitor.converter;
    }

    SpreadsheetConverterGeneralSpreadsheetValueTypeVisitor(final SpreadsheetConverterGeneralMapping<Converter<C>> mapping) {
        super();
        this.mapping = mapping;
    }

    @Override
    protected void visitBigDecimal() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitBigInteger() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitBoolean() {
        this.converter = this.mapping.booleanValue;
    }

    @Override
    protected void visitByte() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitCharacter() {
        this.converter = this.mapping.string;
    }

    @Override
    protected void visitDouble() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitExpressionNumber() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitFloat() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitInteger() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitLocalDate() {
        this.converter = this.mapping.date;
    }

    @Override
    protected void visitLocalDateTime() {
        this.converter = this.mapping.dateTime;
    }

    @Override
    protected void visitLocalTime() {
        this.converter = this.mapping.time;
    }

    @Override
    protected void visitLong() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitNumber() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitShort() {
        this.converter = this.mapping.number;
    }

    @Override
    protected void visitString() {
        this.converter = this.mapping.string;
    }

    @Override
    protected void visitUnknown(final String typeName) {
        this.converter = this.mapping.number; // handles other Number types.
    }

    private final SpreadsheetConverterGeneralMapping<Converter<C>> mapping;

    /**
     * The {@link Converter} selected using the target type.
     */
    private Converter<C> converter;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .separator(", ")
            .valueSeparator(", ")
            .label("mapping").value(this.mapping)
            .label("general").value(this.converter)
            .build();
    }
}
