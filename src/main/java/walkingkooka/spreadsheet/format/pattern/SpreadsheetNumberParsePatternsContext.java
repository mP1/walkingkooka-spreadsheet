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

import walkingkooka.Context;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.text.cursor.TextCursor;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * The {@link Context} which accompanies the parsing process and builds up the {@link Number}.
 */
final class SpreadsheetNumberParsePatternsContext implements Context {

    static SpreadsheetNumberParsePatternsContext with(final Iterator<SpreadsheetNumberParsePatternsComponent> next,
                                                      final DecimalNumberContext context) {
        return new SpreadsheetNumberParsePatternsContext(next, context);
    }

    private SpreadsheetNumberParsePatternsContext(final Iterator<SpreadsheetNumberParsePatternsComponent> next,
                                                  final DecimalNumberContext context) {
        super();

        this.next = next;
        this.context = context;
    }

    final DecimalNumberContext context;

    /**
     * Controls what part of the number the next digit belongs.
     */
    SpreadsheetNumberParsePatternsMode mode = SpreadsheetNumberParsePatternsMode.INTEGER;

    /**
     * Tri-state (using wrapper) used so that the first positive or negative sign is saved here, and all others must be unescaped symbols.
     */
    Boolean negativeMantissa = null;

    /**
     * The mantissa value.
     */
    BigDecimal mantissa = BigDecimal.ZERO;

    /**
     * When true the final value will be divided by 100.
     */
    boolean percentage = false;

    /**
     * When true the exponent value is negative.
     */
    boolean negativeExponent = false;

    /**
     * The exponent value.
     */
    int exponent = 0;

    /**
     * Calls the nextComponent component if one exists.
     */
    void nextComponent(final TextCursor cursor) {
        if (this.next.hasNext()) {
            this.next.next().parse(cursor, this);
        }
    }

    /**
     * Computes the {@link BigDecimal} value from the parts which are present.
     */
    BigDecimal computeValue() {
        final BigDecimal mantissa = this.mantissa;
        final int exponent = this.exponent;

        BigDecimal computed = mantissa;
        if (Boolean.TRUE.equals(this.negativeMantissa)) {
            computed = computed.negate(this.context.mathContext());
        }

        return computed.scaleByPowerOfTen((this.negativeExponent ?
                -exponent :
                +exponent) +
                (this.percentage ? -2 : 0));
    }

    /**
     * Returns true if ANY of the remaining components are {@link SpreadsheetNumberParsePatternsComponent#isRequired()}.
     */
    boolean isRequired() {
        boolean required = false;

        final Iterator<SpreadsheetNumberParsePatternsComponent> next = this.next;
        while(false == required && next.hasNext()) {
            required = next.next().isRequired();
        }

        return required;
    }

    /**
     * An {@link Iterator} which contains the next component, when empty the end of the text has been reached.
     */
    final Iterator<SpreadsheetNumberParsePatternsComponent> next;

    @Override
    public String toString() {
        return this.mode + " " + this.computeValue().toString();
    }
}
