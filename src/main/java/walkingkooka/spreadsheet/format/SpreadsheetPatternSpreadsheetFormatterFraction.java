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

import walkingkooka.color.Color;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormatter} that unconditionally formats a {@link BigDecimal}, without a {@link Color} using a pattern
 * parsed into a {@link SpreadsheetFormatFractionParserToken}.
 */
final class SpreadsheetPatternSpreadsheetFormatterFraction extends SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterFraction} parse a {@link SpreadsheetFormatNumberParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterFraction with(final SpreadsheetFormatFractionParserToken token,
                                                               final Function<BigDecimal, Fraction> fractioner) {
        checkParserToken(token);
        Objects.requireNonNull(fractioner, "fractioner");

        return new SpreadsheetPatternSpreadsheetFormatterFraction(token, fractioner);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterFraction(final SpreadsheetFormatFractionParserToken token,
                                                           final Function<BigDecimal, Fraction> fractioner) {
        super(token);

        this.fractioner = fractioner;

        final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor =
                SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor.analyze(token);

        this.components = visitor.components;

        this.numeratorDigitSymbolCount = visitor.numeratorDigitSymbolCount;
        this.denominatorDigitSymbolCount = visitor.denominatorDigitSymbolCount;
        this.multiplier = visitor.multiplier;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return value instanceof BigDecimal;
    }

    @Override
    Optional<SpreadsheetText> format0(final Object value, final SpreadsheetFormatterContext context) {
        return Optional.ofNullable(context.convert(value, BigDecimal.class)
                .mapLeft(v -> SpreadsheetText.with(this.format1(v, context)))
                .orElseLeft(null));
    }

    /**
     * Compute the fraction of the {@link BigDecimal value}, and if the denominator is too many places, perform some
     * rounding until its the right number of digits.
     */
    private String format1(final BigDecimal value, final SpreadsheetFormatterContext context) {
        final BigDecimal rounded = value.multiply(this.multiplier, context.mathContext())
                .setScale(this.denominatorDigitSymbolCount, RoundingMode.HALF_UP);

        final Fraction fraction = this.fractioner.apply(rounded);

        final int sign = rounded.signum();
        BigInteger numerator = fraction.numerator();
        if (numerator.signum() < 0) {
            numerator = numerator.negate();
        }

        BigInteger denominator = fraction.denominator().abs();

        final int places = decimalPlaces(denominator);
        for (int i = this.denominatorDigitSymbolCount; i < places; i++) {
            numerator = numerator.add(FIVE).divide(BigInteger.TEN);
            denominator = denominator.add(FIVE).divide(BigInteger.TEN);
        }

        return this.format2(SpreadsheetPatternSpreadsheetFormatterFractionContext.with(SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign.fromSignum(sign),
                SpreadsheetPatternSpreadsheetFormatterFractionDigits.numerator(numerator.toString()),
                SpreadsheetPatternSpreadsheetFormatterFractionDigits.denominator(denominator.toString()),
                this,
                context));
    }

    /**
     * A non zero value multiplied against the {@link BigDecimal} being formatted as text.
     */
    private final BigDecimal multiplier;

    /**
     * Converts a {@link BigDecimal} into a {@link Fraction}.
     */
    private final Function<BigDecimal, Fraction> fractioner;

    /**
     * Returns the number of places in the value in its decimal form.
     */
    private static int decimalPlaces(final BigInteger value) {
        return new BigDecimal(value).precision();
    }

    private final static BigInteger FIVE = BigInteger.valueOf(5);

    /**
     * Applies the pattern and value into text.
     */
    private String format2(final SpreadsheetPatternSpreadsheetFormatterFractionContext context) {
        this.components.forEach(c -> c.append(context));
        return context.formattedText();
    }

    /**
     * Components for each symbol in the original pattern.
     */
    private final List<SpreadsheetPatternSpreadsheetFormatterFractionComponent> components;

    final int numeratorDigitSymbolCount;
    final int denominatorDigitSymbolCount;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.token.text();
    }
}
