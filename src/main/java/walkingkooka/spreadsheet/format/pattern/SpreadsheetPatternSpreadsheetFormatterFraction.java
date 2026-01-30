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

import walkingkooka.Either;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.FractionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats any number as a fraction.
 */
final class SpreadsheetPatternSpreadsheetFormatterFraction implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterFraction} parse a {@link NumberSpreadsheetFormatParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterFraction with(final FractionSpreadsheetFormatParserToken token,
                                                               final Function<BigDecimal, Fraction> fractioner) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(fractioner, "fractioner");

        return new SpreadsheetPatternSpreadsheetFormatterFraction(token, fractioner);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterFraction(final FractionSpreadsheetFormatParserToken token,
                                                           final Function<BigDecimal, Fraction> fractioner) {
        super();

        this.token = token;

        this.fractioner = fractioner;

        final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor =
            SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor.analyze(token);

        this.components = visitor.components;

        this.numeratorDigitSymbolCount = visitor.numeratorDigitSymbolCount;
        this.denominatorDigitSymbolCount = visitor.denominatorDigitSymbolCount;
        this.multiplier = visitor.multiplier;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        final Either<BigDecimal, String> converted = context.convert(
            value.orElse(null),
            BigDecimal.class
        );
        final BigDecimal bigDecimal = converted.isLeft() ?
            converted.leftValue() :
            null;

        return Optional.ofNullable(
            null != bigDecimal ?
                SpreadsheetText.with(
                    this.formatSpreadsheetTextBigDecimal(
                        bigDecimal,
                        context
                    )
                ) :
                null
        );
    }

    /**
     * Compute the fraction of the {@link BigDecimal value}, and if the denominator is too many places, perform some
     * rounding until its the right number of digits.
     */
    private String formatSpreadsheetTextBigDecimal(final BigDecimal value,
                                                   final SpreadsheetFormatterContext context) {
        final BigDecimal rounded = value.multiply(
            this.multiplier,
            context.mathContext()
        ).setScale(
            this.denominatorDigitSymbolCount,
            RoundingMode.HALF_UP
        );

        final Fraction fraction = this.fractioner.apply(rounded);

        final int sign = rounded.signum();
        BigInteger numerator = fraction.numerator();
        if (numerator.signum() < 0) {
            numerator = numerator.negate();
        }

        BigInteger denominator = fraction.denominator().abs();

        final int places = decimalPlaces(denominator);
        for (int i = this.denominatorDigitSymbolCount; i < places; i++) {
            numerator = numerator.add(FIVE)
                .divide(BigInteger.TEN);
            denominator = denominator.add(FIVE)
                .divide(BigInteger.TEN);
        }

        final char zeroDigit = context.zeroDigit();

        final SpreadsheetPatternSpreadsheetFormatterFractionContext context2 = SpreadsheetPatternSpreadsheetFormatterFractionContext.with(
            SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign.fromSignum(sign),
            SpreadsheetPatternSpreadsheetFormatterFractionDigits.numerator(
                SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific.fixDigits(
                    numerator.toString(),
                    zeroDigit
                )
            ),
            SpreadsheetPatternSpreadsheetFormatterFractionDigits.denominator(
                SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific.fixDigits(
                    denominator.toString(),
                    zeroDigit
                )
            ),
            this,
            context
        );

        this.components.forEach(c -> c.append(context2));
        return context2.formattedText();
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatterSelectorToken.tokens(this.token);
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
     * Components for each symbol in the original pattern.
     */
    private final List<SpreadsheetPatternSpreadsheetFormatterFractionComponent> components;

    final int numeratorDigitSymbolCount;
    final int denominatorDigitSymbolCount;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.token.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetPatternSpreadsheetFormatterFraction &&
                this.equals0((SpreadsheetPatternSpreadsheetFormatterFraction) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterFraction other) {
        return this.token.equals(other.token) &&
            this.fractioner.equals(other.fractioner);
    }

    @Override
    public String toString() {
        return this.token.text();
    }

    private final FractionSpreadsheetFormatParserToken token;
}
