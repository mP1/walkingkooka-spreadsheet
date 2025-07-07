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

import walkingkooka.Either;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.tree.expression.ExpressionNumber;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats value after converting them to a number using the provided number pattern.
 */
final class SpreadsheetPatternSpreadsheetFormatterNumber implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterNumber} parse a {@link NumberSpreadsheetFormatParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterNumber with(final NumberSpreadsheetFormatParserToken token) {
        Objects.requireNonNull(token, "token");

        return new SpreadsheetPatternSpreadsheetFormatterNumber(token);
    }

    /**
     * Private ctor use static method.
     */
    private SpreadsheetPatternSpreadsheetFormatterNumber(final NumberSpreadsheetFormatParserToken token) {
        super();

        this.token = token;

        final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor =
            SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor.analyze(token);

        this.currency = visitor.currency;

        this.color = visitor.color;

        this.components = visitor.components;
        this.normalOrScientific = visitor.normalOrScientific;

        this.integerDigitSymbolCount = visitor.integerDigitSymbolCount;
        this.fractionDigitSymbolCount = visitor.fractionDigitSymbolCount;
        this.exponentDigitSymbolCount = visitor.exponentDigitSymbolCount;

        this.decimalPlacesShift = visitor.decimalPlacesShift;
        this.groupSeparator = visitor.groupSeparator;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        final Either<ExpressionNumber, String> valueAsNumber = context.convert(
            value.orElse(null),
            ExpressionNumber.class
        );

        final ExpressionNumber expressionNumber = valueAsNumber.isLeft() ?
            valueAsNumber.leftValue() :
            null;

        return Optional.ofNullable(
            null != expressionNumber ?
                SpreadsheetText.with(
                    this.formatSpreadsheetTextExpressionNumber(
                        this.normalOrScientific.context(
                            expressionNumber.bigDecimal(),
                            this,
                            context
                        )
                    )
                ).setColor(
                    this.color(context)
                ) :
                null
        );
    }

    /**
     * When true indicates that his pattern formats currency values.
     */
    final boolean currency;

    private Optional<Color> color(final SpreadsheetFormatterContext context) {
        Object colorNameOrNumber = this.color;
        Optional<Color> color = SpreadsheetText.WITHOUT_COLOR;

        if (colorNameOrNumber instanceof Integer) {
            color = context.colorNumber(
                (Integer) colorNameOrNumber
            );
        } else {
            if (colorNameOrNumber instanceof SpreadsheetColorName) {
                color = context.colorName(
                    (SpreadsheetColorName) colorNameOrNumber
                );
            }
        }

        return color;
    }

    // the color name or number
    private final Object color;

    /**
     * Executes each of the format tokens eventually resulting in a {@link String}.
     */
    private String formatSpreadsheetTextExpressionNumber(final SpreadsheetPatternSpreadsheetFormatterNumberContext context) {
        this.components.forEach(c -> c.append(context));
        return context.formattedText();
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatterSelectorToken.tokens(this.token);
    }

    private final SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific normalOrScientific;

    /**
     * Used to move the decimal places because of formatting options such as percentage(multiply by 100) etc.
     * Positive values represent multiply by 10, and negative represent divide by 10.
     */
    final int decimalPlacesShift;

    /**
     * Components for each symbol in the original pattern.
     */
    private final List<SpreadsheetPatternSpreadsheetFormatterNumberComponent> components;

    final int integerDigitSymbolCount;
    final int fractionDigitSymbolCount;
    final int exponentDigitSymbolCount;

    /**
     * When true groupSeparator(thousand) separators should appear in the output.
     */
    final SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator groupSeparator;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.token.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetPatternSpreadsheetFormatterNumber && this.equals0((SpreadsheetPatternSpreadsheetFormatterNumber) other);
    }

    // all other fields are derived from examining the token, so no need to include them in hashCode/equals
    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterNumber other) {
        return this.token.equals(other.token);
    }

    @Override
    public String toString() {
        return this.token.text();
    }

    private final NumberSpreadsheetFormatParserToken token;
}
