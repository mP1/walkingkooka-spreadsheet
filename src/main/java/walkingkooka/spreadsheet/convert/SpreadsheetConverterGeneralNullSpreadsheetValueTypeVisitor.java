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

import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitor;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A {@link SpreadsheetValueTypeVisitor} that handles converting null values to a target type.
 * Number values will return their ZERO equivalent,
 * <pre>
 * Byte.class = (byte) 0
 * Integer.class = 0
 * ExpressionNumber.class = {@link ExpressionNumberKind#zero()} taken from the given {@link ExpressionNumberContext}.
 * </pre>
 */
final class SpreadsheetConverterGeneralNullSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static Object convertNull(final Class<?> target,
                              final ExpressionNumberContext context) {
        final SpreadsheetConverterGeneralNullSpreadsheetValueTypeVisitor visitor = new SpreadsheetConverterGeneralNullSpreadsheetValueTypeVisitor(context);
        visitor.accept(target);
        return visitor.value;
    }

    // @VisibleForTesting
    SpreadsheetConverterGeneralNullSpreadsheetValueTypeVisitor(final ExpressionNumberContext context) {
        super();
        this.context = context;
    }

    @Override
    protected void visitBigDecimal() {
        this.value = BigDecimal.ZERO;
    }

    @Override
    protected void visitBigInteger() {
        this.value = BigInteger.ZERO;
    }

    @Override
    protected void visitBoolean() {
        this.value = null;
    }

    @Override
    protected void visitByte() {
        this.value = (byte) 0;
    }

//    @Override
//    protected void visitCellRange() {
//        this.value = null;
//    }
//
//    @Override
//    protected void visitCellReference() {
//        this.value = null;
//    }

//    @Override
//    protected void visitCellReferenceOrRange() {
//        this.value = null;
//    }

    @Override
    protected void visitCharacter() {
        this.value = null;
    }

//    @Override
//    protected void visitColumnReference() {
//        this.value = null;
//    }
//
//    @Override
//    protected void visitColumnRangeReference() {
//        this.value = null;
//    }

    @Override
    protected void visitDouble() {
        this.value = 0.0;
    }

    @Override
    protected void visitExpressionNumber() {
        this.value = this.context.expressionNumberKind()
            .zero();
    }

//    @Override
//    protected void visitExpressionReference() {
//        this.value = null;
//    }

    @Override
    protected void visitFloat() {
        this.value = 0f;
    }

    @Override
    protected void visitInteger() {
        this.value = 0;
    }

    @Override
    protected void visitLabel() {
        this.value = null;
    }

    @Override
    protected void visitLocalDate() {
        this.value = null;
    }

    @Override
    protected void visitLocalDateTime() {
        this.value = null;
    }

    @Override
    protected void visitLocalTime() {
        this.value = null;
    }

    @Override
    protected void visitLong() {
        this.value = 0L;
    }

    @Override
    protected void visitNumber() {
        this.value = this.context.expressionNumberKind()
            .zero();
    }

//    @Override
//    protected void visitRowReference() {
//        this.value = null;
//    }
//
//    @Override
//    protected void visitRowRangeReference() {
//        this.value = null;
//    }

    @Override
    protected void visitSpreadsheetError() {
        this.value = null;
    }

//    @Override
//    protected void visitSpreadsheetSelection() {
//        this.value = null;
//    }

    @Override
    protected void visitShort() {
        this.value = (short) 0;
    }

    @Override
    protected void visitString() {
        this.value = null;
    }

    @Override
    protected void visitUnknown(final String typeName) {
        this.value = null;
    }

    /**
     * The {@link ExpressionNumberContext} will be used to supply ZERO for null {@link walkingkooka.tree.expression.ExpressionNumber}.
     */
    private final ExpressionNumberContext context;

    private Object value;

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
