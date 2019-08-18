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

public final class SpreadsheetNumberParsePatternsComponentDigitDigitTest extends SpreadsheetNumberParsePatternsComponentDigitTestCase<SpreadsheetNumberParsePatternsComponentDigitDigit> {

    @Test
    public void testSpace() {
        this.parseAndCheck2(" ",
                " ",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testSpaceLetter() {
        this.parseAndCheck2(" A",
                " A",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testSpaceDigit() {
        this.parseAndCheck2(" 1",
                " 1",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "#");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetNumberParsePatternsComponentDigitDigit.with(1), "#");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentDigitDigit createComponent(final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitDigit.with(max);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentDigitDigit> type() {
        return SpreadsheetNumberParsePatternsComponentDigitDigit.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DigitDigit";
    }
}
