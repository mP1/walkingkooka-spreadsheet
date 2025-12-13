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

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetValueVisitor;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A {@link SpreadsheetValueVisitor} that selects one of the {@link SpreadsheetFormatter} on {@link AutomaticSpreadsheetFormatter}.
 */
final class SpreadsheetMetadataFormattersSpreadsheetFormatterSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    static SpreadsheetFormatter select(final AutomaticSpreadsheetFormatter spreadsheetMetadataFormattersSpreadsheetFormatter,
                                       final Object value) {
        final SpreadsheetMetadataFormattersSpreadsheetFormatterSpreadsheetValueVisitor visitor = new SpreadsheetMetadataFormattersSpreadsheetFormatterSpreadsheetValueVisitor(spreadsheetMetadataFormattersSpreadsheetFormatter);
        visitor.accept(value);
        return visitor.formatter;
    }

    private SpreadsheetMetadataFormattersSpreadsheetFormatterSpreadsheetValueVisitor(final AutomaticSpreadsheetFormatter spreadsheetMetadataFormattersSpreadsheetFormatter) {
        this.automaticSpreadsheetFormatter = spreadsheetMetadataFormattersSpreadsheetFormatter;
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.number();
    }

    @Override
    protected void visit(final BigInteger value) {
        this.number();
    }

    @Override
    protected void visit(final Boolean value) {
        this.text();
    }

    @Override
    protected void visit(final Byte value) {
        this.number();
    }

    @Override
    protected void visit(final Character value) {
        this.text();
    }

    @Override
    protected void visit(final ExpressionNumber value) {
        this.number();
    }

    @Override
    protected void visit(final Float value) {
        this.number();
    }

    @Override
    protected void visit(final Double value) {
        this.number();
    }

    @Override
    protected void visit(final Integer value) {
        this.number();
    }

    @Override
    protected void visit(final LocalDate value) {
        this.formatter = this.automaticSpreadsheetFormatter.date;
    }

    @Override
    protected void visit(final LocalDateTime value) {
        this.formatter = this.automaticSpreadsheetFormatter.dateTime;
    }

    @Override
    protected void visit(final LocalTime value) {
        this.formatter = this.automaticSpreadsheetFormatter.time;
    }

    @Override
    protected void visit(final Long value) {
        this.number();
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference value) {
        this.text();
    }

    @Override
    protected void visit(final SpreadsheetCellReference value) {
        this.text();
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference value) {
        this.text();
    }

    @Override
    protected void visit(final SpreadsheetColumnReference value) {
        this.text();
    }

    @Override
    protected void visit(final SpreadsheetError error) {
        this.formatter = this.automaticSpreadsheetFormatter.error;
    }

    @Override
    protected void visit(final SpreadsheetLabelName value) {
        this.text();
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference value) {
        this.text();
    }

    @Override
    protected void visit(final SpreadsheetRowReference value) {
        this.text();
    }

    @Override
    protected void visit(final Short value) {
        this.number();
    }

    @Override
    protected void visit(final String value) {
        this.text();
    }

    @Override
    protected void visit(final Object value) {
        this.number();
    }

    @Override
    protected void visitNull() {
        this.text();
    }

    private void number() {
        this.formatter = this.automaticSpreadsheetFormatter.number;
    }

    private void text() {
        this.formatter = this.automaticSpreadsheetFormatter.text;
    }

    final AutomaticSpreadsheetFormatter automaticSpreadsheetFormatter;

    private SpreadsheetFormatter formatter;
}
