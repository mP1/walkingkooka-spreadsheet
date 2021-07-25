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
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;

public final class SpreadsheetNumberParsePatternsComponentDigitSpaceTest extends SpreadsheetNumberParsePatternsComponentDigitTestCase<SpreadsheetNumberParsePatternsComponentDigitSpace> {


    // space.............................................................................................................

    @Test
    public void testSpaceIntegerOrSign() {
        this.testSpace(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public void testSpaceInteger() {
        this.testSpace(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public void testSpaceDecimalFirst() {
        this.testSpace(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public void testSpaceDecimal() {
        this.testSpace(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public void testSpaceExponentOrSign() {
        this.testSpace(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN
        );
    }

    @Test
    public void testSpaceExponent() {
        this.testSpace(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testSpace(final SpreadsheetNumberParsePatternsComponentDigitMode mode) {
        this.parseAndCheck3(
                1,
                mode,
                " ",
                "",
                mode,
                NEXT_SKIPPED,
                SpreadsheetParserToken.whitespace(" ", " ")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "?");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(
                SpreadsheetNumberParsePatternsComponentDigitSpace.with(
                        SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                        1
                ),
                "?");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentDigitSpace createComponent(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                                                      final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitSpace.with(mode, max);
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
