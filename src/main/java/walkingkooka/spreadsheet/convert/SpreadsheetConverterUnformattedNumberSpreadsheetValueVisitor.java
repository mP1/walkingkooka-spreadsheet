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

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A {@link SpreadsheetValueVisitor} that only supports converting to a String, but using the provided
 * {@link ExpressionNumberConverterContext} for {@link LocalDate}, {@link LocalDateTime} and {@link LocalTime}.
 */
final class SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    /**
     * Uses the source value type and target type to pick a {@link Converter}.
     */
    static Object convertToString(final Object value,
                                  final ExpressionNumberConverterContext context) {
        return null == value ?
            null :
            convertToString0(value, context);
    }

    private static Object convertToString0(final Object value,
                                           final ExpressionNumberConverterContext context) {
        final SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor visitor = new SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor(
            context
        );
        visitor.accept(value);

        return visitor.converted;
    }

    SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor(final ExpressionNumberConverterContext context) {
        super();
        this.context = context;
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final BigInteger value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Boolean value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Byte value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Character value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Double value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final ExpressionNumber value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Float value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Integer value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final LocalDate value) {
        this.convert(value);
    }

    @Override
    protected void visit(final LocalDateTime value) {
        this.convert(value);
    }

    @Override
    protected void visit(final LocalTime value) {
        this.convert(value);
    }

    private void convert(final Object value) {
        this.accept(
            this.context.convert(
                value,
                String.class
            ).leftValue()
        );
    }

    private final ExpressionNumberConverterContext context;

    @Override
    protected void visit(final Long value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final Short value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetCellReference value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetColumnReference value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetLabelName value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final SpreadsheetRowReference value) {
        this.converted = value.toString();
    }

    @Override
    protected void visit(final String value) {
        this.converted = value;
    }

    @Override
    protected void visit(final Object value) {
        this.converted = value;
    }

    @Override
    protected void visitNull() {
        this.converted = null;
    }

    private Object converted;

    @Override
    public String toString() {
        return "converted: " + CharSequences.quoteIfChars(this.converted);
    }
}
