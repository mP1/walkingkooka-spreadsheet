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

import walkingkooka.tree.visit.Visiting;

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
        return super.startVisit(value);
    }

    @Override
    protected void endVisit(final Object value) {
        super.endVisit(value);
    }

    @Override
    protected void visit(final BigDecimal value) {
        super.visit(value);
    }

    @Override
    protected void visit(final BigInteger value) {
        super.visit(value);
    }

    @Override
    protected void visit(final Boolean value) {
        super.visit(value);
    }

    @Override
    protected void visit(final Double value) {
        super.visit(value);
    }

    @Override
    protected void visit(final LocalDate value) {
        super.visit(value);
    }

    @Override
    protected void visit(final LocalDateTime value) {
        super.visit(value);
    }

    @Override
    protected void visit(final LocalTime value) {
        super.visit(value);
    }

    @Override
    protected void visit(final Long value) {
        super.visit(value);
    }

    @Override
    protected void visit(final String value) {
        super.visit(value);
    }

    @Override
    protected void visit(final Object value) {
        super.visit(value);
    }
}
