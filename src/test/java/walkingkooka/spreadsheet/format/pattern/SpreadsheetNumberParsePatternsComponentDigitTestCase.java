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

public abstract class SpreadsheetNumberParsePatternsComponentDigitTestCase<C extends SpreadsheetNumberParsePatternsComponentDigit> extends SpreadsheetNumberParsePatternsComponentTestCase<C> {

    private final static String TEXT5 = "5";
    private final static String VALUE5 = TEXT5;

    private final static String TEXT6 = "6";
    private final static String VALUE6 = TEXT6;

    SpreadsheetNumberParsePatternsComponentDigitTestCase() {
    }

    // grouping.............................................................................................................

    @Test
    public final void testGroupingIntegerOrSign() {
        this.testGrouping(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public final void testGroupingInteger() {
        this.testGrouping(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testGroupingDecimalFirst() {
        this.testGrouping(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public final void testGroupingDecimal() {
        this.testGrouping(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testGroupingExponentOrSign() {
        this.testGrouping(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN
        );
    }

    @Test
    public final void testGroupingExponent() {
        this.testGrouping(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testGrouping(final SpreadsheetNumberParsePatternsComponentDigitMode mode) {
        this.parseAndCheck3(
                1,
                mode,
                "" + GROUP,
                "",
                mode,
                NEXT_SKIPPED,
                SpreadsheetParserToken.groupingSeparatorSymbol("" + GROUP, "" + GROUP)
        );
    }

    // plus.............................................................................................................

    @Test
    public final void testPlusIntegerOrSign() {
        this.testPlus(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public final void testPlusInteger() {
        this.testPlus(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testPlusDecimalFirst() {
        this.testPlus(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public final void testPlusDecimal() {
        this.testPlus(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testPlusExponentOrSign() {
        this.testPlus(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN
        );
    }

    @Test
    public final void testPlusExponent() {
        this.testPlus(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }
    
    private void testPlus(final SpreadsheetNumberParsePatternsComponentDigitMode mode) {
        final String text = "" + PLUS;

        if(mode.isSign()) {
            this.parseAndCheck3(
                    1,
                    mode,
                    text,
                    "",
                    mode.next(),
                    NEXT_SKIPPED,
                    SpreadsheetParserToken.plusSymbol(text, text)
            );
        } else {
            this.parseAndCheck3(
                    1,
                    mode,
                    "",
                    text,
                    mode,
                    NEXT_SKIPPED
            );
        }
    }

    // minus.............................................................................................................

    @Test
    public final void testMinusIntegerOrSign() {
        this.testMinus(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public final void testMinusInteger() {
        this.testMinus(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testMinusDecimalFirst() {
        this.testMinus(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public final void testMinusDecimal() {
        this.testMinus(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testMinusExponentOrSign() {
        this.testMinus(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN
        );
    }

    @Test
    public final void testMinusExponent() {
        this.testMinus(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testMinus(final SpreadsheetNumberParsePatternsComponentDigitMode mode) {
        final String text = "" + MINUS;

        if(mode.isSign()) {
            this.parseAndCheck3(
                    1,
                    mode,
                    text,
                    "",
                    mode.next(),
                    NEXT_SKIPPED,
                    SpreadsheetParserToken.minusSymbol(text, text)
            );
        } else {
            this.parseAndCheck3(
                    1,
                    mode,
                    "",
                    text,
                    mode,
                    NEXT_SKIPPED
            );
        }
    }
    
    // digits 1, max=1..................................................................................................

    @Test
    public final void testDigit1Max1IntegerOrSign() {
        this.testDigit1Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit1Max1Integer() {
        this.testDigit1Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit1Max1DecimalFirst() {
        this.testDigit1Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testDigit1Max1Decimal() {
        this.testDigit1Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testDigit1Max1ExponentOrSign() {
        this.testDigit1Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit1Max1Exponent() {
        this.testDigit1Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testDigit1Max1(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        this.parseAndCheck3(
                1,
                mode,
                TEXT5,
                "",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetParserToken.digits(VALUE5, TEXT5)
        );
    }

    // 1x digit, text max=1

    @Test
    public final void testDigit1TextMax1IntegerOrSign() {
        this.testDigit1TextMax1(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit1TextMax1Integer() {
        this.testDigit1TextMax1(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit1TextMax1DecimalFirst() {
        this.testDigit1TextMax1(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testDigit1TextMax1Decimal() {
        this.testDigit1TextMax1(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testDigit1TextMax1ExponentOrSign() {
        this.testDigit1TextMax1(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit1TextMax1Exponent() {
        this.testDigit1TextMax1(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testDigit1TextMax1(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                    final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        this.parseAndCheck3(
                1,
                mode,
                TEXT5,
                "a",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetParserToken.digits(VALUE5, TEXT5)
        );
    }

    // 1x digit, digit max=1

    @Test
    public final void testDigit2Max1IntegerOrSign() {
        this.testDigit2Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit2Max1Integer() {
        this.testDigit2Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit2Max1DecimalFirst() {
        this.testDigit2Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testDigit2Max1Decimal() {
        this.testDigit2Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testDigit2Max1ExponentOrSign() {
        this.testDigit2Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit2Max1Exponent() {
        this.testDigit2Max1(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testDigit2Max1(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        this.parseAndCheck3(
                1,
                mode,
                TEXT5,
                "a",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetParserToken.digits(VALUE5, TEXT5)
        );
    }

    // digits2, max=2..................................................................................................

    @Test
    public final void testDigit2Max2IntegerOrSign() {
        this.testDigit2Max2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit2Max2Integer() {
        this.testDigit2Max2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit2Max2DecimalFirst() {
        this.testDigit2Max2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testDigit2Max2Decimal() {
        this.testDigit2Max2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testDigit2Max2ExponentOrSign() {
        this.testDigit2Max2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit2Max2Exponent() {
        this.testDigit2Max2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testDigit2Max2(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                     final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        final String text = TEXT5 + TEXT6;
        this.parseAndCheck3(
                2,
                mode,
                text,
                "",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetParserToken.digits(text, text)
        );
    }

    // digits2, text max=2..................................................................................................

    @Test
    public final void testDigit2TextMax2IntegerOrSign() {
        this.testDigit2TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit2TextMax2Integer() {
        this.testDigit2TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit2TextMax2DecimalFirst() {
        this.testDigit2TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testDigit2TextMax2Decimal() {
        this.testDigit2TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testDigit2TextMax2ExponentOrSign() {
        this.testDigit2TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit2TextMax2Exponent() {
        this.testDigit2TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testDigit2TextMax2(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                         final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        final String text = TEXT5 + TEXT6;
        this.parseAndCheck3(
                2,
                mode,
                text,
                "!",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetParserToken.digits(text, text)
        );
    }

    // plus, digits, text max=2..................................................................................................

    @Test
    public final void testPlusDigit1TextMax2IntegerOrSign() {
        this.testPlusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testPlusDigit1TextMax2Integer() {
        this.testPlusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testPlusDigit1TextMax2DecimalFirst() {
        this.testPlusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testPlusDigit1TextMax2Decimal() {
        this.testPlusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testPlusDigit1TextMax2ExponentOrSign() {
        this.testPlusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testPlusDigit1TextMax2Exponent() {
        this.testPlusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testPlusDigit1TextMax2(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                        final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        final String text = TEXT5;
        final String plusText = PLUS + text;

        if(mode.isSign()) {
            this.parseAndCheck3(
                    2,
                    mode,
                    plusText,
                    "",
                    expectedMode,
                    NEXT_CALLED,
                    SpreadsheetParserToken.plusSymbol("" + PLUS, "" + PLUS),
                    SpreadsheetParserToken.digits(text, text)
            );
        } else {
            this.parseAndCheck3(
                    2,
                    mode,
                    "",
                    plusText,
                    mode,
                    NEXT_SKIPPED
            );
        }
    }


    // sign, digits, text max=2..................................................................................................

    @Test
    public final void testMinusDigit1TextMax2IntegerOrSign() {
        this.testMinusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testMinusDigit1TextMax2Integer() {
        this.testMinusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER,
                SpreadsheetNumberParsePatternsComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testMinusDigit1TextMax2DecimalFirst() {
        this.testMinusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );

    }

    @Test
    public final void testMinusDigit1TextMax2Decimal() {
        this.testMinusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL,
                SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL
        );
    }

    @Test
    public final void testMinusDigit1TextMax2ExponentOrSign() {
        this.testMinusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT_OR_SIGN,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testMinusDigit1TextMax2Exponent() {
        this.testMinusDigit1TextMax2(
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT,
                SpreadsheetNumberParsePatternsComponentDigitMode.EXPONENT
        );
    }

    private void testMinusDigit1TextMax2(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                         final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode) {
        final String text = TEXT5;
        final String minusText = MINUS + text;

        if(mode.isSign()) {
            this.parseAndCheck3(
                    2,
                    mode,
                    minusText,
                    "",
                    expectedMode,
                    NEXT_CALLED,
                    SpreadsheetParserToken.minusSymbol("" + MINUS, "" + MINUS),
                    SpreadsheetParserToken.digits(text, text)
            );
        } else {
            this.parseAndCheck3(
                    2,
                    mode,
                    "",
                    minusText,
                    mode,
                    NEXT_SKIPPED
            );
        }
    }

    // helpers...........................................................................................................

    final void parseAndCheck3(final int max,
                              final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                              final String text,
                              final String textAfter,
                              final SpreadsheetNumberParsePatternsComponentDigitMode expectedMode,
                              final boolean hasNext,
                              final SpreadsheetParserToken... tokens) {
        final SpreadsheetNumberParsePatternsRequest request = this.createRequest(hasNext);
        request.digitMode = mode;

        this.parseAndCheck2(
                this.createComponent(mode, max),
                text,
                textAfter,
                request,
                hasNext,
                tokens
        );

        assertEquals(
                "",
                request.digits.toString(),
                () -> "digits\nrequest: " + request
        );

        assertEquals(
                expectedMode,
                request.digitMode,
                () -> "mode\nrequest: " + request
        );
    }

    @Override
    final C createComponent() {
        return this.createComponent(SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST, 3);
    }

    abstract C createComponent(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                               final int max);
}
