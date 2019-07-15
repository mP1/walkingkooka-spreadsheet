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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.compare.RangeVisitorTesting;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.CharSequences;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetDeltaRangeRangeVisitorTest implements RangeVisitorTesting<SpreadsheetDeltaRangeRangeVisitor<SpreadsheetCellReference>, SpreadsheetCellReference> {

    @Test
    public void testToBounded() {
        this.rangeToStringAndCheck("A1:B2");
    }

    @Test
    public void testToBounded2() {
        this.rangeToStringAndCheck("A1:Z99");
    }

    private void rangeToStringAndCheck(final String text) {
        assertEquals(text,
                SpreadsheetDeltaRangeRangeVisitor.rangeToString(range(text)),
                () -> text);
    }

    @Test
    public void testToStringEmpty() {
        this.toStringAndCheck(new SpreadsheetDeltaRangeRangeVisitor(), "");
    }

    @Test
    public void testToString() {
        final String text = "A1:B2";

        final SpreadsheetDeltaRangeRangeVisitor visitor = new SpreadsheetDeltaRangeRangeVisitor();
        visitor.accept(range(text));

        this.toStringAndCheck(visitor, CharSequences.quoteAndEscape(text).toString());
    }

    private static Range<SpreadsheetCellReference> range(final String text) {
        return SpreadsheetRange.parseRange(text).range();
    }

    @Override
    public SpreadsheetDeltaRangeRangeVisitor<SpreadsheetCellReference> createVisitor() {
        return new SpreadsheetDeltaRangeRangeVisitor<>();
    }

    @Override
    public Class<SpreadsheetDeltaRangeRangeVisitor<SpreadsheetCellReference>> type() {
        return Cast.to(SpreadsheetDeltaRangeRangeVisitor.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetDeltaRange.class.getSimpleName();
    }
}
