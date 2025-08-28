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

import walkingkooka.math.NumberVisitor;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberSign;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A {@link NumberVisitor} that handles testing each number type against zero return false for zero values.
 */
final class SpreadsheetConverterToBooleanNumberVisitor extends NumberVisitor {

    static boolean toBoolean(final Number value) {
        final SpreadsheetConverterToBooleanNumberVisitor visitor = new SpreadsheetConverterToBooleanNumberVisitor();
        visitor.accept(value);
        return visitor.booleanValue;
    }

    SpreadsheetConverterToBooleanNumberVisitor() {
        super();
    }

    @Override 
    protected void visit(final BigDecimal number) {
        this.booleanValue = number.signum() != 0;
    }

    @Override
    protected void visit(final BigInteger number) {
        this.booleanValue = number.signum() != 0;
    }

    @Override
    protected void visit(final Byte number) {
        this.booleanValue = number.byteValue() != 0;
    }

    @Override
    protected void visit(final Double number) {
        this.booleanValue = number.doubleValue() != 0.0;
    }

    @Override
    protected void visit(final Float number) {
        this.booleanValue = number.floatValue() != 0.0f;
    }

    @Override
    protected void visit(final Integer number) {
        this.booleanValue = number.intValue() != 0;
    }

    @Override 
    protected void visit(final Long number) {
        this.booleanValue = number.longValue() != 0L;
    }

    @Override
    protected void visit(final Short number) {
        this.booleanValue = number.shortValue() != 0;
    }

    @Override
    protected void visitUnknown(final Number number) {
        this.booleanValue = ExpressionNumberSign.ZERO != ((ExpressionNumber)number).sign();
    }

    Boolean booleanValue;

    @Override
    public String toString() {
        return String.valueOf(this.booleanValue);
    }
}
