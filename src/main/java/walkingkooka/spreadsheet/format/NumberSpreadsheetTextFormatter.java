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
import walkingkooka.math.Maths;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.text.CharSequences;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that handles formatting the 4 spreadsheet {@link Number} values.
 * <ul>
 * <li>{@link BigDecimal}</li>
 * <li>{@link java.math.BigInteger}</li>
 * <li>{@link Double}</li>
 * <li>{@link Long}</li>
 * </ul>
 * producing the text equivalent without a {@link Color}. The pattern would have been a {@link String} but the factory accepts it represented as a {@link SpreadsheetFormatBigDecimalParserToken}.
 */
final class NumberSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<SpreadsheetFormatBigDecimalParserToken> {

    /**
     * Creates a {@link NumberSpreadsheetTextFormatter} from a {@link SpreadsheetFormatBigDecimalParserToken}.
     */
    static NumberSpreadsheetTextFormatter with(final SpreadsheetFormatBigDecimalParserToken token) {
        check(token);

        return new NumberSpreadsheetTextFormatter(token);
    }

    /**
     * Private ctor use static method.
     */
    private NumberSpreadsheetTextFormatter(final SpreadsheetFormatBigDecimalParserToken token) {
        super(token);


        final NumberSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor =
                NumberSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.analyze(token);

        this.components = visitor.components;
        this.normalOrScientific = visitor.normalOrScientific;

        this.integerDigitSymbolCount = visitor.integerDigitSymbolCount;
        this.fractionDigitSymbolCount = visitor.fractionDigitSymbolCount;
        this.exponentDigitSymbolCount = visitor.exponentDigitSymbolCount;

        this.decimalPlacesShift = visitor.decimalPlacesShift;
        this.thousandsSeparator = visitor.thousandsSeparator;
    }

    /**
     * Only accepts any of the four {@link Number} types.
     * <ul>
     * <li>{@link BigDecimal}</li>
     * <li>{@link java.math.BigInteger}</li>
     * <li>{@link Double}</li>
     * <li>{@link Long}</li>
     * </ul>
     */
    @Override
    public boolean canFormat(final Object value) {
        return value instanceof BigDecimal ||
                value instanceof BigInteger ||
                value instanceof Double ||
                value instanceof Long;
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {
        return Optional.of(SpreadsheetFormattedText.with(
                SpreadsheetFormattedText.WITHOUT_COLOR,
                this.format1(this.normalOrScientific.context(Maths.toBigDecimal(Number.class.cast(value)).orElseThrow(() -> new IllegalArgumentException("Non number value " + CharSequences.quoteIfChars(value))), this, context))));
    }

    /**
     * Executes each of the format components eventually resulting in a {@link String}.
     */
    private String format1(final NumberSpreadsheetTextFormatterComponentContext context) {
        this.components.forEach(c -> c.append(context));
        return context.formattedText();
    }

    final NumberSpreadsheetTextFormatterNormalOrScientific normalOrScientific;

    /**
     * Used to move the decimal places because of formatting options such as percentage(multiply by 100) etc.
     * Positive values represent multiply by 10, and negative represent divide by 10.
     */
    final int decimalPlacesShift;

    /**
     * Components for each symbol in the original pattern.
     */
    final List<NumberSpreadsheetTextFormatterComponent> components;

    final int integerDigitSymbolCount;
    final int fractionDigitSymbolCount;
    final int exponentDigitSymbolCount;

    /**
     * When true thousands separators should appear in the output.
     */
    final NumberSpreadsheetTextFormatterThousandsSeparator thousandsSeparator;

    @Override
    String toStringSuffix() {
        return "";
    }
}
