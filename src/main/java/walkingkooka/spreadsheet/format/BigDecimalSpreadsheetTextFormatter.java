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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that unconditionally formats a {@link BigDecimal} using a pattern, without a {@link Color}.
 * The pattern would have been a {@link String} but the factory accepts it represented as a {@link SpreadsheetFormatBigDecimalParserToken}.
 */
final class BigDecimalSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<SpreadsheetFormatBigDecimalParserToken> {

    /**
     * Creates a {@link BigDecimalSpreadsheetTextFormatter} from a {@link SpreadsheetFormatBigDecimalParserToken}.
     */
    static BigDecimalSpreadsheetTextFormatter with(final SpreadsheetFormatBigDecimalParserToken token) {
        check(token);

        return new BigDecimalSpreadsheetTextFormatter(token);
    }

    /**
     * Private ctor use static parse.
     */
    private BigDecimalSpreadsheetTextFormatter(final SpreadsheetFormatBigDecimalParserToken token) {
        super(token);


        final BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor =
                BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.analyze(token);

        this.components = visitor.components;
        this.format = visitor.format;

        this.integerDigitSymbolCount = visitor.integerDigitSymbolCount;
        this.fractionDigitSymbolCount = visitor.fractionDigitSymbolCount;
        this.exponentDigitSymbolCount = visitor.exponentDigitSymbolCount;

        this.decimalPlacesShift = visitor.decimalPlacesShift;
        this.thousandsSeparator = visitor.thousandsSeparator;
    }

    /**
     * Only accepts {@link BigDecimal} values for formatting.
     */
    @Override
    public boolean canFormat(final Object value) {
        return value instanceof BigDecimal;
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {
        return Optional.of(SpreadsheetFormattedText.with(
                SpreadsheetFormattedText.WITHOUT_COLOR,
                this.format1(this.format.context(BigDecimal.class.cast(value), this.thousandsSeparator, this, context))));
    }

    final BigDecimalSpreadsheetTextFormatterFormat format;

    private String format1(final BigDecimalSpreadsheetTextFormatterComponentContext context) {
        this.components.forEach(c -> c.append(context));
        return context.formattedText();
    }

    /**
     * Used to move the decimal places because of formatting options such as percentage(multiply by 100) etc.
     * Positive values represent multiply by 10, and negative represent divide by 10.
     */
    final int decimalPlacesShift;

    /**
     * Components for each symbol in the original pattern.
     */
    private final List<BigDecimalSpreadsheetTextFormatterComponent> components;

    final int integerDigitSymbolCount;
    final int fractionDigitSymbolCount;
    final int exponentDigitSymbolCount;

    /**
     * When true thousands separators should appear in the output.
     */
    private final BigDecimalSpreadsheetTextFormatterThousandsSeparator thousandsSeparator;

    @Override
    String toStringSuffix() {
        return "";
    }
}
