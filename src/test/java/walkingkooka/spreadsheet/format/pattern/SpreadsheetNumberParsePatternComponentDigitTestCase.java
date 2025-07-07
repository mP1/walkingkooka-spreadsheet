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

public abstract class SpreadsheetNumberParsePatternComponentDigitTestCase<C extends SpreadsheetNumberParsePatternComponentDigit> extends SpreadsheetNumberParsePatternComponentTestCase<C> {

    private final static String TEXT5 = "5";
    private final static String VALUE5 = TEXT5;

    private final static String TEXT6 = "6";

    SpreadsheetNumberParsePatternComponentDigitTestCase() {
    }

    // grouping.............................................................................................................

    @Test
    public final void testIntegerOrSignGroupSeparator() {
        this.groupAndCheck(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public final void testIntegerGroupSeparator() {
        this.groupAndCheck(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    private void groupAndCheck(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        this.parseAndCheck3(
            1,
            mode,
            "" + GROUP,
            "",
            mode,
            NEXT_SKIPPED,
            SpreadsheetFormulaParserToken.groupSeparatorSymbol("" + GROUP, "" + GROUP)
        );
    }

    @Test
    public final void testDecimalFirstGroupSeparator() {
        this.groupAndCheck2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public final void testDecimalNotFirstGroupSeparator() {
        this.groupAndCheck2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testExponentOrSignGroupSeparator() {
        this.groupAndCheck2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START
        );
    }

    @Test
    public final void testExponentGroupSeparator() {
        this.groupAndCheck2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void groupAndCheck2(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        this.parseAndCheck3(
            1,
            mode,
            "",
            "" + GROUP,
            mode,
            NEXT_CALLED
        );
    }

    // plus.............................................................................................................

    @Test
    public final void testPlusIntegerOrSign() {
        this.testPlus(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public final void testPlusInteger() {
        this.testPlus(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testPlusDecimalFirst() {
        this.testPlus(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public final void testPlusDecimal() {
        this.testPlus(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testPlusExponentOrSign() {
        this.testPlus(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START
        );
    }

    @Test
    public final void testPlusExponent() {
        this.testPlus(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testPlus(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        final String text = "" + PLUS;

        if (mode.isSign()) {
            this.parseAndCheck3(
                1,
                mode,
                text,
                "",
                mode.next(),
                NEXT_SKIPPED,
                SpreadsheetFormulaParserToken.plusSymbol(text, text)
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
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN
        );
    }

    @Test
    public final void testMinusInteger() {
        this.testMinus(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testMinusDecimalFirst() {
        this.testMinus(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST
        );
    }

    @Test
    public final void testMinusDecimal() {
        this.testMinus(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testMinusExponentOrSign() {
        this.testMinus(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START
        );
    }

    @Test
    public final void testMinusExponent() {
        this.testMinus(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testMinus(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        final String text = "" + MINUS;

        if (mode.isSign()) {
            this.parseAndCheck3(
                1,
                mode,
                text,
                "",
                mode.next(),
                NEXT_SKIPPED,
                SpreadsheetFormulaParserToken.minusSymbol(text, text)
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
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit1Max1Integer() {
        this.testDigit1Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit1Max1DecimalFirst() {
        this.testDigit1Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testDigit1Max1Decimal() {
        this.testDigit1Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testDigit1Max1ExponentOrSign() {
        this.testDigit1Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit1Max1Exponent() {
        this.testDigit1Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testDigit1Max1(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        this.parseAndCheck3(
            1,
            mode,
            TEXT5,
            "",
            expectedMode,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.digits(VALUE5, TEXT5)
        );
    }

    // 1x digit, text max=1

    @Test
    public final void testDigit1TextMax1IntegerOrSign() {
        this.testDigit1TextMax1(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit1TextMax1Integer() {
        this.testDigit1TextMax1(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit1TextMax1DecimalFirst() {
        this.testDigit1TextMax1(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testDigit1TextMax1Decimal() {
        this.testDigit1TextMax1(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testDigit1TextMax1ExponentOrSign() {
        this.testDigit1TextMax1(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit1TextMax1Exponent() {
        this.testDigit1TextMax1(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testDigit1TextMax1(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                    final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        this.parseAndCheck3(
            1,
            mode,
            TEXT5,
            "a",
            expectedMode,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.digits(VALUE5, TEXT5)
        );
    }

    // 1x digit, digit max=1

    @Test
    public final void testDigit2Max1IntegerOrSign() {
        this.testDigit2Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit2Max1Integer() {
        this.testDigit2Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit2Max1DecimalFirst() {
        this.testDigit2Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testDigit2Max1Decimal() {
        this.testDigit2Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testDigit2Max1ExponentOrSign() {
        this.testDigit2Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit2Max1Exponent() {
        this.testDigit2Max1(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testDigit2Max1(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        this.parseAndCheck3(
            1,
            mode,
            TEXT5,
            "a",
            expectedMode,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.digits(VALUE5, TEXT5)
        );
    }

    // digits2, max=2..................................................................................................

    @Test
    public final void testDigit2Max2IntegerOrSign() {
        this.testDigit2Max2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit2Max2Integer() {
        this.testDigit2Max2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit2Max2DecimalFirst() {
        this.testDigit2Max2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testDigit2Max2Decimal() {
        this.testDigit2Max2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testDigit2Max2ExponentOrSign() {
        this.testDigit2Max2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit2Max2Exponent() {
        this.testDigit2Max2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testDigit2Max2(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        final String text = TEXT5 + TEXT6;
        this.parseAndCheck3(
            2,
            mode,
            text,
            "",
            expectedMode,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.digits(text, text)
        );
    }

    // digits2, text max=2..................................................................................................

    @Test
    public final void testDigit2TextMax2IntegerOrSign() {
        this.testDigit2TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testDigit2TextMax2Integer() {
        this.testDigit2TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testDigit2TextMax2DecimalFirst() {
        this.testDigit2TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testDigit2TextMax2Decimal() {
        this.testDigit2TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testDigit2TextMax2ExponentOrSign() {
        this.testDigit2TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testDigit2TextMax2Exponent() {
        this.testDigit2TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testDigit2TextMax2(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                    final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        final String text = TEXT5 + TEXT6;
        this.parseAndCheck3(
            2,
            mode,
            text,
            "!",
            expectedMode,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.digits(text, text)
        );
    }

    // plus, digits, text max=2..................................................................................................

    @Test
    public final void testPlusDigit1TextMax2IntegerOrSign() {
        this.testPlusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testPlusDigit1TextMax2Integer() {
        this.testPlusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testPlusDigit1TextMax2DecimalFirst() {
        this.testPlusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testPlusDigit1TextMax2Decimal() {
        this.testPlusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testPlusDigit1TextMax2ExponentOrSign() {
        this.testPlusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testPlusDigit1TextMax2Exponent() {
        this.testPlusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testPlusDigit1TextMax2(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                        final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        final String text = TEXT5;
        final String plusText = PLUS + text;

        if (mode.isSign()) {
            this.parseAndCheck3(
                2,
                mode,
                plusText,
                "",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetFormulaParserToken.plusSymbol("" + PLUS, "" + PLUS),
                SpreadsheetFormulaParserToken.digits(text, text)
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
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );

    }

    @Test
    public final void testMinusDigit1TextMax2Integer() {
        this.testMinusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER,
            SpreadsheetNumberParsePatternComponentDigitMode.INTEGER
        );
    }

    @Test
    public final void testMinusDigit1TextMax2DecimalFirst() {
        this.testMinusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );

    }

    @Test
    public final void testMinusDigit1TextMax2Decimal() {
        this.testMinusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_NOT_FIRST
        );
    }

    @Test
    public final void testMinusDigit1TextMax2ExponentOrSign() {
        this.testMinusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    @Test
    public final void testMinusDigit1TextMax2Exponent() {
        this.testMinusDigit1TextMax2(
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT
        );
    }

    private void testMinusDigit1TextMax2(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                         final SpreadsheetNumberParsePatternComponentDigitMode expectedMode) {
        final String text = TEXT5;
        final String minusText = MINUS + text;

        if (mode.isSign()) {
            this.parseAndCheck3(
                2,
                mode,
                minusText,
                "",
                expectedMode,
                NEXT_CALLED,
                SpreadsheetFormulaParserToken.minusSymbol("" + MINUS, "" + MINUS),
                SpreadsheetFormulaParserToken.digits(text, text)
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
                              final SpreadsheetNumberParsePatternComponentDigitMode mode,
                              final String text,
                              final String textAfter,
                              final SpreadsheetNumberParsePatternComponentDigitMode expectedMode,
                              final boolean hasNext,
                              final SpreadsheetFormulaParserToken... tokens) {
        final SpreadsheetNumberParsePatternRequest request = this.createRequest(hasNext);
        request.digitMode = mode;

        this.parseAndCheck2(
            this.createComponent(mode, max),
            text,
            textAfter,
            request,
            hasNext,
            tokens
        );

        this.checkEquals(
            "",
            request.digits.toString(),
            () -> "digits\nrequest: " + request
        );

        this.checkEquals(
            expectedMode,
            request.digitMode,
            () -> "mode\nrequest: " + request
        );
    }

    @Override final C createComponent() {
        return this.createComponent(SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST, 3);
    }

    abstract C createComponent(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                               final int max);
}
