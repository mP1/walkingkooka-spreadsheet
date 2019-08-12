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

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.SpreadsheetValueVisitor;

final class SpreadsheetFormatter2SpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    static boolean isSpreadsheetValue(final Object value) {
        final SpreadsheetFormatter2SpreadsheetValueVisitor visitor = new SpreadsheetFormatter2SpreadsheetValueVisitor();
        visitor.accept(value);
        return visitor.canFormat;
    }

    SpreadsheetFormatter2SpreadsheetValueVisitor() {
        super();
    }

    @Override
    protected void visit(final Object value) {
        this.canFormat = false;
    }

    private boolean canFormat = true;

    @Override
    public String toString() {
        return "canFormat: " + this.canFormat;
    }
}
