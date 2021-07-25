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

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetNumberParsePatternsComponentDecimalSeparatorTest extends SpreadsheetNumberParsePatternsComponentTestCase2<SpreadsheetNumberParsePatternsComponentDecimalSeparator> {

    @Test
    public void testDecimal() {
        assertEquals(DECIMAL, 'd');

        this.parseAndCheck2(
                DECIMAL,
                ""
        );
    }

    @Test
    public void testDecimalCharacter() {
        this.parseAndCheck2(
                DECIMAL,
                "!"
        );
    }

    @Test
    public void testDecimalTwice() {
        this.parseAndCheck2(
                DECIMAL,
                DECIMAL + ""
        );
    }

    @Test
    public void testDecimalTwiceCharacter() {
        this.parseAndCheck2(
                DECIMAL,
                DECIMAL + "!"
        );
    }

    final void parseAndCheck2(final char c,
                              final String textAfter) {
        final String text = "" + c;
        this.parseAndCheck2(
                text,
                textAfter,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                NEXT_CALLED,
                SpreadsheetParserToken.decimalSeparatorSymbol(text, text));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), ".");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentDecimalSeparator createComponent() {
        return SpreadsheetNumberParsePatternsComponentDecimalSeparator.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentDecimalSeparator> type() {
        return SpreadsheetNumberParsePatternsComponentDecimalSeparator.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DecimalSeparator";
    }
}
