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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetValueVisitorTesting;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetTextFormatter2SpreadsheetValueVisitorTest implements SpreadsheetValueVisitorTesting<SpreadsheetTextFormatter2SpreadsheetValueVisitor> {

    @Test
    public void testSpreadsheetType() {
        assertEquals(true, SpreadsheetTextFormatter2SpreadsheetValueVisitor.isSpreadsheetValue("hello"));
    }

    @Test
    public void testNotSpreadsheetType() {
        assertEquals(false, SpreadsheetTextFormatter2SpreadsheetValueVisitor.isSpreadsheetValue(this));
    }

    @Test
    public void testToStringCan() {
        final SpreadsheetTextFormatter2SpreadsheetValueVisitor visitor = new SpreadsheetTextFormatter2SpreadsheetValueVisitor();
        visitor.accept("abc");
        this.toStringAndCheck(visitor, "canFormat: true");
    }

    @Test
    public void testToStringCant() {
        final SpreadsheetTextFormatter2SpreadsheetValueVisitor visitor = new SpreadsheetTextFormatter2SpreadsheetValueVisitor();
        visitor.accept(this);
        this.toStringAndCheck(visitor, "canFormat: false");
    }

    @Override
    public SpreadsheetTextFormatter2SpreadsheetValueVisitor createVisitor() {
        return new SpreadsheetTextFormatter2SpreadsheetValueVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetTextFormatter2.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetTextFormatter2SpreadsheetValueVisitor> type() {
        return SpreadsheetTextFormatter2SpreadsheetValueVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
