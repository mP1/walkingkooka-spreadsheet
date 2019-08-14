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

public final class SpreadsheetNumberParsePatternsComponentDigitZeroTest extends SpreadsheetNumberParsePatternsComponentDigitTestCase<SpreadsheetNumberParsePatternsComponentDigitZero> {

    @Test
    public void testSpace() {
        this.parseAndCheck2(" ",
                " ",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "0");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetNumberParsePatternsComponentDigitZero.with(1), "0");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentDigitZero createComponent(final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitZero.with(max);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentDigitZero> type() {
        return SpreadsheetNumberParsePatternsComponentDigitZero.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DigitZero";
    }
}
