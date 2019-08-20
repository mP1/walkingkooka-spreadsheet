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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SpreadsheetNumberParsePatternsComponentDigitTestCase<C extends SpreadsheetNumberParsePatternsComponentDigit> extends SpreadsheetNumberParsePatternsComponentTestCase<C> {

    SpreadsheetNumberParsePatternsComponentDigitTestCase() {
    }

    final static Boolean NEGATIVE = true;
    final static Boolean POSITIVE = false;
    final static Boolean NEGATIVE_POSITIVE_MISSING = null;

    @Test
    public final void testGroupingSeparatorNonDigit() {
        this.parseAndCheck2("GA",
                "A",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testGroupingSeparatorDigit() {
        this.parseAndCheck2("G0A",
                "A",
                VALUE_WITHOUT,
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testDigits() {
        final String after = "abc";

        this.parseAndCheck2("123" + after,
                after,
                BigDecimal.valueOf(123),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testMaximumDigits() {
        this.parseAndCheck2("123",
                "",
                BigDecimal.valueOf(123),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testMaximumDigitsLetter() {
        this.parseAndCheck2("123A",
                "A",
                BigDecimal.valueOf(123),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testMaximumDigitsDigit() {
        this.parseAndCheck2("123456",
                "456",
                BigDecimal.valueOf(123),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testPlusDigit() {
        this.parseAndCheck2("Q123456",
                "456",
                BigDecimal.valueOf(123),
                POSITIVE);
    }

    @Test
    public final void testMinusDigit() {
        this.parseAndCheck2("N123456",
                "456",
                BigDecimal.valueOf(-123),
                NEGATIVE);
    }

    @Test
    public final void testDigitPlusDigit() {
        this.parseAndCheck2("12Q345",
                "45",
                BigDecimal.valueOf(123),
                POSITIVE);
    }

    @Test
    public final void testDigitMinusDigit() {
        this.parseAndCheck2("12N345",
                "45",
                BigDecimal.valueOf(-123),
                NEGATIVE);
    }

    @Test
    public final void testDigitGroupingSeparatorDigit() {
        this.parseAndCheck2("1G2",
                "",
                BigDecimal.valueOf(12),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testDigitGroupingSeparatorLetter() {
        this.parseAndCheck2("1GA",
                "A",
                BigDecimal.valueOf(1),
                NEGATIVE_POSITIVE_MISSING);
    }

    @Test
    public final void testDigitGroupingSeparatorDigitMaximum() {
        this.parseAndCheck2("1G234",
                "4",
                BigDecimal.valueOf(123),
                NEGATIVE_POSITIVE_MISSING);
    }

    final void parseAndCheck2(final String text,
                              final String textAfter,
                              final BigDecimal value,
                              final Boolean negative) {
        final SpreadsheetNumberParsePatternsContext context = this.createContext();
        this.parseAndCheck(text,
                context,
                textAfter,
                value,
                NEXT_CALLED);
        assertEquals(negative, context.negativeMantissa, "negativeMantissa");
        this.checkMode(context, SpreadsheetNumberParsePatternsMode.INTEGER);
    }

    @Override
    final C createComponent() {
        return this.createComponent(3);
    }

    abstract C createComponent(final int max);
}
