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

import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.util.Objects;

/**
 * A {@link Visitor} that dispatches on a spreadsheet value {@link Class type}. Unknown types will call {@link #visitUnknown(String)}.
 */
public abstract class SpreadsheetValueTypeVisitor extends Visitor<Class<?>> {

    protected SpreadsheetValueTypeVisitor() {
        super();
    }

    @Override
    public final void accept(final Class<?> type) {
        Objects.requireNonNull(type, "type");

        if (Visiting.CONTINUE == this.startVisit(type)) {
            final String name = type.getName();

            switch (name) {
                case "walkingkooka.net.AbsoluteUrl":
                    this.visitAbsoluteUrl();
                    break;
                case "java.math.BigDecimal":
                    this.visitBigDecimal();
                    break;
                case "java.math.BigInteger":
                    this.visitBigInteger();
                    break;
                case "java.lang.Boolean":
                    this.visitBoolean();
                    break;
                case "java.lang.Byte":
                    this.visitByte();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference":
                    this.visitCellRange();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetCellReference":
                    this.visitCellReference();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange":
                    this.visitCellReferenceOrRange();
                    break;
                case "java.lang.Character":
                    this.visitCharacter();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange":
                    this.visitColumnOrRowReferenceOrRange();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetColumnReference":
                    this.visitColumnReference();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceOrRange":
                    this.visitColumnReferenceOrRange();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference":
                    this.visitColumnRangeReference();
                    break;
                case "java.lang.Double":
                    this.visitDouble();
                    break;
                case "walkingkooka.net.email.EmailAddress":
                    this.visitEmail();
                    break;
                case "walkingkooka.tree.expression.ExpressionNumber":
                case "walkingkooka.tree.expression.ExpressionNumberBigDecimal":
                case "walkingkooka.tree.expression.ExpressionNumberDouble":
                    this.visitExpressionNumber();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference":
                    this.visitExpressionReference();
                    break;
                case "java.lang.Float":
                    this.visitFloat();
                    break;
                case "java.lang.Integer":
                    this.visitInteger();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetLabelName":
                    this.visitLabel();
                    break;
                case "java.time.LocalDate":
                    this.visitLocalDate();
                    break;
                case "java.time.LocalDateTime":
                    this.visitLocalDateTime();
                    break;
                case "java.time.LocalTime":
                    this.visitLocalTime();
                    break;
                case "java.lang.Long":
                    this.visitLong();
                    break;
                case "java.lang.Number":
                    this.visitNumber();
                    break;
                case "java.lang.Short":
                    this.visitShort();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetRowReference":
                    this.visitRowReference();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceOrRange":
                    this.visitRowReferenceOrRange();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference":
                    this.visitRowRangeReference();
                    break;
                case "walkingkooka.spreadsheet.value.SpreadsheetError":
                    this.visitSpreadsheetError();
                    break;
                case "walkingkooka.spreadsheet.reference.SpreadsheetSelection":
                    this.visitSpreadsheetSelection();
                    break;
                case "java.lang.String":
                    this.visitString();
                    break;
                default:
                    this.visitUnknown(name);
                    break;
            }
        }
        this.endVisit(type);
    }

    protected Visiting startVisit(final Class<?> type) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final Class<?> type) {

    }

    protected void visitAbsoluteUrl() {

    }

    protected void visitBigDecimal() {

    }

    protected void visitBigInteger() {

    }

    protected void visitBoolean() {

    }

    protected void visitByte() {

    }

    protected void visitCellRange() {

    }

    protected void visitCellReference() {

    }

    protected void visitCellReferenceOrRange() {

    }

    protected void visitCharacter() {

    }

    protected void visitColumnReference() {

    }

    protected void visitColumnOrRowReferenceOrRange() {

    }

    protected void visitColumnReferenceOrRange() {

    }

    protected void visitColumnRangeReference() {

    }

    protected void visitDouble() {

    }

    protected void visitEmail() {

    }

    protected void visitEmailAddress() {

    }

    protected void visitExpressionNumber() {

    }

    protected void visitExpressionReference() {

    }

    protected void visitFloat() {

    }

    protected void visitInteger() {

    }

    protected void visitLabel() {

    }

    protected void visitLocalDate() {

    }

    protected void visitLocalDateTime() {

    }

    protected void visitLocalTime() {

    }

    protected void visitLong() {

    }

    protected void visitNumber() {

    }

    protected void visitRowReference() {

    }

    protected void visitRowReferenceOrRange() {

    }

    protected void visitRowRangeReference() {

    }

    protected void visitSpreadsheetError() {
    }

    protected void visitSpreadsheetSelection() {

    }

    protected void visitShort() {

    }

    protected void visitString() {

    }

    protected void visitUrl() {

    }

    protected void visitUnknown(final String typeName) {

    }
}
