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

public class FakeSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    protected FakeSpreadsheetValueTypeVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitBigDecimal() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitBigInteger() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitBoolean() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDouble() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitFloat() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitInteger() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitLocalDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitLocalDateTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitLocalTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitNumber() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitShort() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitString() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitUnknown() {
        throw new UnsupportedOperationException();
    }
}
