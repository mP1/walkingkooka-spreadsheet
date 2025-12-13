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

package walkingkooka.spreadsheet.value;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FakeSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    protected FakeSpreadsheetValueVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final BigInteger value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final Byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final Character value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final Double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ExpressionNumber value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LocalDate value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LocalDateTime value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LocalTime value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final Long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetCellReference value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetColumnReference value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetError error) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLabelName value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetRowReference value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNull() {
        throw new UnsupportedOperationException();
    }
}
