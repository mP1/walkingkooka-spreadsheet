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

package walkingkooka.spreadsheet.math;

import walkingkooka.ToStringBuilder;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * A {@link SpreadsheetValueVisitor} that converts any {@link Number}value} into a {@link BigDecimal}, all others will return an
 * empty {@link Optional}.
 */
final class SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    static Optional<BigDecimal> bigDecimal(final Object value) {
        final SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor visitor = new SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor();
        visitor.accept(value);
        return Optional.ofNullable(visitor.bigDecimal);
    }

    SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor() {
        super();
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.bigDecimal = value;
    }

    @Override
    protected void visit(final BigInteger value) {
        this.bigDecimal = new BigDecimal(value);
    }

    @Override
    protected void visit(final Double value) {
        this.bigDecimal = BigDecimal.valueOf(value);
    }

    @Override
    protected void visit(final Long value) {
        this.bigDecimal = BigDecimal.valueOf(value);
    }

    private BigDecimal bigDecimal;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.bigDecimal)
                .build();
    }
}
