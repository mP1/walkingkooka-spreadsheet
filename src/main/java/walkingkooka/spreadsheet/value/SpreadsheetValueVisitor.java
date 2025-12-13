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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A {@link Visitor} for all supported value types belonging to a {@link SpreadsheetCell}. If the type is not one of the
 * supported types a default {@link #visit(Object)} is invoked. Note that null values can be passed and will not fail
 * any guard.
 */
public abstract class SpreadsheetValueVisitor extends Visitor<Object> {

    protected SpreadsheetValueVisitor() {
        super();
    }

    @Override
    public final void accept(final Object value) {
        if (Visiting.CONTINUE == this.startVisit(value)) {
            do {
                if (null == value) {
                    this.visitNull();
                    break;
                }
                if (value instanceof String) {
                    this.visit((String) value);
                    break;
                }
                if (value instanceof Number) {
                    SpreadsheetValueVisitorNumberVisitor.with(this).accept((Number) value);
                    break;
                }
                if (value instanceof Boolean) {
                    this.visit((Boolean) value);
                    break;
                }
                if (value instanceof Character) {
                    this.visit((Character) value);
                    break;
                }
                if (value instanceof LocalDate) {
                    this.visit((LocalDate) value);
                    break;
                }
                if (value instanceof LocalDateTime) {
                    this.visit((LocalDateTime) value);
                    break;
                }
                if (value instanceof LocalTime) {
                    this.visit((LocalTime) value);
                    break;
                }
                if (value instanceof SpreadsheetError) {
                    this.visit((SpreadsheetError) value);
                    break;
                }
                if (value instanceof SpreadsheetSelection) {
                    this.selectionVisitor.accept((SpreadsheetSelection) value);
                    break;
                }
                this.visit(value);
            } while (false);
        }
        this.endVisit(value);
    }

    private final SpreadsheetValueVisitorSpreadsheetSelectionVisitor selectionVisitor = SpreadsheetValueVisitorSpreadsheetSelectionVisitor.with(this);

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

    protected void visit(final Byte value) {
        // nop
    }

    protected void visit(final Character value) {
        // nop
    }

    protected void visit(final ExpressionNumber value) {
        // nop
    }

    protected void visit(final Float value) {
        // nop
    }

    protected void visit(final Double value) {
        // nop
    }

    protected void visit(final Integer value) {
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

    protected void visit(final SpreadsheetCellRangeReference value) {
        // nop
    }

    protected void visit(final SpreadsheetCellReference value) {
        // nop
    }

    protected void visit(final SpreadsheetColumnRangeReference value) {
        // nop
    }

    protected void visit(final SpreadsheetColumnReference value) {
        // nop
    }

    protected void visit(final SpreadsheetError error) {
        // nop
    }

    protected void visit(final SpreadsheetLabelName value) {
        // nop
    }

    protected void visit(final SpreadsheetRowRangeReference value) {
        // nop
    }

    protected void visit(final SpreadsheetRowReference value) {
        // nop
    }

    protected void visit(final Short value) {
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

    protected void visitNull() {
        // nop
    }
}
