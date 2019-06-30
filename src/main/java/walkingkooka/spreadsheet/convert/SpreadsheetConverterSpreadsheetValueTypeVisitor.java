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
import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitor;
import walkingkooka.tree.visit.Visitor;

/**
 * A {@link Visitor} that locates the appropriate {@link Converter} for a given source and target {@link Class}.
 */
final class SpreadsheetConverterSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static Converter converter(final SpreadsheetConverterMapping<Converter> all,
                               final Class<?> targetType) {
        final SpreadsheetConverterSpreadsheetValueTypeVisitor visitor = new SpreadsheetConverterSpreadsheetValueTypeVisitor(all);
        visitor.accept(targetType);
        return visitor.converter;
    }

    SpreadsheetConverterSpreadsheetValueTypeVisitor(final SpreadsheetConverterMapping<Converter> all) {
        super();
        this.all = all;
    }

    @Override
    protected void visitBigDecimal() {
        this.converter = this.all.bigDecimal;
    }

    @Override
    protected void visitBigInteger() {
        this.converter = this.all.bigInteger;
    }

    @Override
    protected void visitBoolean() {
        this.converter = this.all.booleanValue;
    }

    @Override
    protected void visitDouble() {
        this.converter = this.all.doubleValue;
    }

    @Override
    protected void visitLocalDate() {
        this.converter = this.all.localDate;
    }

    @Override
    protected void visitLocalDateTime() {
        this.converter = this.all.localDateTime;
    }

    @Override
    protected void visitLocalTime() {
        this.converter = this.all.localTime;
    }

    @Override
    protected void visitLong() {
        this.converter = this.all.longValue;
    }

    @Override
    protected void visitString() {
        this.converter = this.all.string;
    }

    private SpreadsheetConverterMapping<Converter> all;

    /**
     * The {@link Converter} selected using the target type.
     */
    private Converter converter;

    @Override
    public final String toString() {
        return ToStringBuilder.empty()
                .label("all").value(this.all)
                .label("converter").value(this.converter)
                .build();
    }
}
