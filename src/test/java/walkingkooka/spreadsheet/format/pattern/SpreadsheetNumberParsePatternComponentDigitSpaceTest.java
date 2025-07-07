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
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;

public final class SpreadsheetNumberParsePatternComponentDigitSpaceTest extends SpreadsheetNumberParsePatternComponentDigitTestCase<SpreadsheetNumberParsePatternComponentDigitSpace> {


    // space.............................................................................................................

    @Test
    public void testSpaceIntegerOrSign() {
        this.testSpace(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public void testSpaceInteger() {
        this.testSpace(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public void testSpaceDecimalFirst() {
        this.testSpace(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public void testSpaceDecimal() {
        this.testSpace(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public void testSpaceExponentOrSign() {
        this.testSpace(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START
        );
    }

    @Test
    public void testSpaceExponent() {
        this.testSpace(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testSpace(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        this.parseAndCheck3(
            1,
            mode,
            " ",
            "",
            mode,
            NEXT_SKIPPED,
            SpreadsheetFormulaParserToken.whitespace(" ", " ")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "?");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(
            SpreadsheetNumberParsePatternComponentDigitSpace.with(
                SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
                1
            ),
            "?");
    }

    @Override
    SpreadsheetNumberParsePatternComponentDigitSpace createComponent(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                                     final int max) {
        return SpreadsheetNumberParsePatternComponentDigitSpace.with(mode, max);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentDigitSpace> type() {
        return SpreadsheetNumberParsePatternComponentDigitSpace.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DigitSpace";
    }
}
