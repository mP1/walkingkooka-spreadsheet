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

import java.util.Objects;

/**
 * A {@link Visitor} that dispatches on a spreadsheet value {@link Class type}. Unknown types will call {@link #visitUnknown()}.
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
                case "java.math.BigDecimal":
                    this.visitBigDecimal();
                    break;
                case "java.math.BigInteger":
                    this.visitBigInteger();
                    break;
                case "java.lang.Boolean":
                    this.visitBoolean();
                    break;
                case "java.lang.Double":
                    this.visitDouble();
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
                case "java.lang.String":
                    this.visitString();
                    break;
                default:
                    this.visitUnknown();
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

    protected void visitBigDecimal() {

    }

    protected void visitBigInteger() {

    }

    protected void visitBoolean() {

    }

    protected void visitDouble() {

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

    protected void visitString() {

    }

    protected void visitUnknown() {

    }
}
