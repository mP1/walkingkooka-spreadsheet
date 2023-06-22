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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetValueVisitorTesting;

public final class ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitorTest implements SpreadsheetValueVisitorTesting<ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor> {

    @Test
    public void testSpreadsheetType() {
        this.checkEquals(true, ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor.isSpreadsheetValue("hello"));
    }

    @Test
    public void testNotSpreadsheetType() {
        this.checkEquals(false, ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor.isSpreadsheetValue(this));
    }

    @Test
    public void testToStringCan() {
        final ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor visitor = new ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor();
        visitor.accept("abc");
        this.toStringAndCheck(visitor, "canFormat: true");
    }

    @Test
    public void testToStringCant() {
        final ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor visitor = new ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor();
        visitor.accept(this);
        this.toStringAndCheck(visitor, "canFormat: false");
    }

    @Override
    public ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor createVisitor() {
        return new ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return ContextFormatTextSpreadsheetFormatter.class.getSimpleName();
    }

    @Override
    public Class<ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor> type() {
        return ContextFormatTextSpreadsheetFormatterSpreadsheetValueVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
