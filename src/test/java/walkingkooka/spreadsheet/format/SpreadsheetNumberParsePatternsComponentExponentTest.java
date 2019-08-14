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

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetNumberParsePatternsComponentExponentTest extends SpreadsheetNumberParsePatternsComponentTestCase2<SpreadsheetNumberParsePatternsComponentExponent> {

    @Test
    public void testLowerCaseExponent() {
        this.parseAndCheck2("x",
                "",
                false);
    }

    @Test
    public void testLowerCaseExponent2() {
        this.parseAndCheck2("x!",
                "!",
                false);
    }

    @Test
    public void testLowerCaseExponentPositive() {
        this.parseAndCheck2("xQ",
                "",
                false);
    }

    @Test
    public void testLowerCaseExponentPositive2() {
        this.parseAndCheck2("xQ!",
                "!",
                false);
    }

    @Test
    public void testUpperCaseExponent() {
        this.parseAndCheck2("X!",
                "!",
                false);
    }

    @Test
    public void testUpperCaseExponentNegativeSymbol() {
        this.parseAndCheck2("XN",
                "",
                true);
    }

    @Test
    public void testUpperCaseExponentNegativeSymbol2() {
        this.parseAndCheck2("XN!",
                "!",
                true);
    }

    final void parseAndCheck2(final String text,
                              final String textAfter,
                              final boolean negativeExponent) {
        final SpreadsheetNumberParsePatternsContext context = this.createContext();
        this.parseAndCheck(text,
                context,
                textAfter,
                BigDecimal.ZERO,
                true);
        this.checkMode(context, SpreadsheetNumberParsePatternsMode.EXPONENT);
        assertEquals(negativeExponent, context.negativeExponent, "negativeExponent");
    }

    @Test
    public void testNonSpaceFails() {
        this.parseFails("AB");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "E");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentExponent createComponent() {
        return SpreadsheetNumberParsePatternsComponentExponent.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentExponent> type() {
        return SpreadsheetNumberParsePatternsComponentExponent.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Exponent";
    }
}
