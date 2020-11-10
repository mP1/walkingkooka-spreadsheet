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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

public final class SpreadsheetRectangleTest implements ClassTesting2<SpreadsheetRectangle>, ParseStringTesting<SpreadsheetRectangle> {

    @Test
    public void testParsePixelRectangle() {
        this.parseStringAndCheck("A1/123/456", SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("A1"), 123, 456));
    }

    @Test
    public void testParsePixelRectangle2() {
        this.parseStringAndCheck("$A$1/123/456", SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("$A$1"), 123, 456));
    }

    @Test
    public void testParsePixelRectangle3() {
        this.parseStringAndCheck("A1/123.5/456.5", SpreadsheetPixelRectangle.with(SpreadsheetCellReference.parseCellReference("A1"),123.5, 456.5));
    }

    @Test
    public void testParseRange() {
        this.parseStringAndCheck("A1:B2", SpreadsheetRange.parseRange("A1:B2"));
    }

    @Test
    public void testParseRange2() {
        this.parseStringAndCheck("$A1:B2", SpreadsheetRange.parseRange("$A1:B2"));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetRectangle> type() {
        return SpreadsheetRectangle.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetRectangle parseString(final String text) {
        return SpreadsheetRectangle.parseRectangle(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
