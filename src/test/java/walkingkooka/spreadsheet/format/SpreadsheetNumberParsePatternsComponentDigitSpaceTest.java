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

import java.math.BigDecimal;

public final class SpreadsheetNumberParsePatternsComponentDigitSpaceTest extends SpreadsheetNumberParsePatternsComponentDigitTestCase<SpreadsheetNumberParsePatternsComponentDigitSpace> {

    @Test
    public void testDigitsSpace() {
        this.parseAndCheck("12 Z",
                "Z",
                BigDecimal.valueOf(12),
                NEXT_CALLED);
    }

    @Test
    public void testSpaceDigitDigit() {
        this.parseAndCheck2(" 12",
                "",
                BigDecimal.valueOf(12),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testSpacePlusDigitDigit() {
        this.parseAndCheck2(" Q12",
                "",
                BigDecimal.valueOf(12),
                POSITIVE);
    }

    @Test
    public void testSpaceMinusDigitDigit() {
        this.parseAndCheck2(" N12",
                "",
                BigDecimal.valueOf(-12),
                NEGATIVE);
    }

    @Test
    public void testSpaceMinusDigitDigitLetter() {
        this.parseAndCheck2(" N12!",
                "!",
                BigDecimal.valueOf(-12),
                NEGATIVE);
    }

    @Test
    public void testMaximumSpaces() {
        this.parseAndCheck2("    ",
                " ",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testMaximumSpaceDigit() {
        this.parseAndCheck2("   4",
                "4",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "?");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetNumberParsePatternsComponentDigitSpace.with(1), "?");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentDigitSpace createComponent(final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitSpace.with(max);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentDigitSpace> type() {
        return SpreadsheetNumberParsePatternsComponentDigitSpace.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DigitSpace";
    }
}
