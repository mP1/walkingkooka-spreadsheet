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

import walkingkooka.math.NumberVisitor;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

final class SpreadsheetValueVisitorNumberVisitor extends NumberVisitor {

    static SpreadsheetValueVisitorNumberVisitor with(final SpreadsheetValueVisitor visitor) {
        return new SpreadsheetValueVisitorNumberVisitor(visitor);
    }

    SpreadsheetValueVisitorNumberVisitor(final SpreadsheetValueVisitor visitor) {
        super();
        this.visitor = visitor;
    }

    @Override
    protected void visit(final BigDecimal number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final BigInteger number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final Byte number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final Float number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final Double number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final Integer number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final Long number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visit(final Short number) {
        this.visitor.visit(number);
    }

    @Override
    protected void visitUnknown(final Number number) {
        if (number instanceof ExpressionNumber) {
            this.visitor.visit((ExpressionNumber) number);
        } else {
            this.visitor.visit(number);
        }
    }

    private final SpreadsheetValueVisitor visitor;

    @Override
    public String toString() {
        return this.visitor.toString();
    }
}
