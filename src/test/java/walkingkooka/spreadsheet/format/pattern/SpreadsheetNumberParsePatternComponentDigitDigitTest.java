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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;

public final class SpreadsheetNumberParsePatternComponentDigitDigitTest extends SpreadsheetNumberParsePatternComponentDigitTestCase<SpreadsheetNumberParsePatternComponentDigitDigit> {

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "#");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(
            SpreadsheetNumberParsePatternComponentDigitDigit.with(
                SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
                1
            ),
            "#"
        );
    }

    @Override
    SpreadsheetNumberParsePatternComponentDigitDigit createComponent(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                                     final int max) {
        return SpreadsheetNumberParsePatternComponentDigitDigit.with(mode, max);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentDigitDigit> type() {
        return SpreadsheetNumberParsePatternComponentDigitDigit.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DigitDigit";
    }
}
