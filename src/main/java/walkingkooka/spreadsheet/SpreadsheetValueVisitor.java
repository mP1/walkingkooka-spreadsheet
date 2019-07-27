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

package walkingkooka.spreadsheet;

import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link Visitor} for all supported value types belonging to a {@link SpreadsheetCell}. If the type is not one of the
 * supported types a default {@link #visit(Object)} is invoked.
 */
public abstract class SpreadsheetValueVisitor extends Visitor<Object> {

    protected SpreadsheetValueVisitor() {
        super();
    }

    public final void accept(final Object value) {
        Objects.requireNonNull(value, "value");

        if (Visiting.CONTINUE == this.startVisit(value)) {
            do {
                if (value instanceof String) {
                    this.visit(String.class.cast(value));
                    break;
                }
                if (value instanceof Number) {
                    SpreadsheetValueVisitorNumberVisitor.with(this).accept(Number.class.cast(value));
                    break;
                }
                if (value instanceof Boolean) {
                    this.visit(Boolean.class.cast(value));
                    break;
                }
                if (value instanceof LocalDate) {
                    this.visit(LocalDate.class.cast(value));
                    break;
                }
                if (value instanceof LocalDateTime) {
                    this.visit(LocalDateTime.class.cast(value));
                    break;
                }
                if (value instanceof LocalTime) {
                    this.visit(LocalTime.class.cast(value));
                    break;
                }
                this.visit(value);
            } while (false);
        }
        this.endVisit(value);
    }

    protected Visiting startVisit(final Object value) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final Object value) {
        // nop
    }

    protected void visit(final BigDecimal value) {
        // nop
    }

    protected void visit(final BigInteger value) {
        // nop
    }

    protected void visit(final Boolean value) {
        // nop
    }

    protected void visit(final Double value) {
        // nop
    }

    protected void visit(final LocalDate value) {
        // nop
    }

    protected void visit(final LocalDateTime value) {
        // nop
    }

    protected void visit(final LocalTime value) {
        // nop
    }

    protected void visit(final Long value) {
        // nop
    }

    protected void visit(final String value) {
        // nop
    }

    /**
     * This is called when the value is not one of the supported types.
     */
    protected void visit(final Object value) {
        // nop
    }
}
