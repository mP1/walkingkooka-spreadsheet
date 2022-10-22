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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that handles formatting the all {@link Number} values producing the text equivalent without a {@link Color}.
 * The pattern would have been a {@link String} but the factory accepts it represented as a {@link SpreadsheetFormatNumberParserToken}.
 */
final class NumberSpreadsheetFormatter extends SpreadsheetFormatter3<SpreadsheetFormatNumberParserToken> {

    /**
     * Creates a {@link NumberSpreadsheetFormatter} from a {@link SpreadsheetFormatNumberParserToken}.
     */
    static NumberSpreadsheetFormatter with(final SpreadsheetFormatNumberParserToken token) {
        checkParserToken(token);

        return new NumberSpreadsheetFormatter(token);
    }

    /**
     * Private ctor use static method.
     */
    private NumberSpreadsheetFormatter(final SpreadsheetFormatNumberParserToken token) {
        super(token);


        final NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor =
                NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.analyze(token);

        this.color = visitor.color;

        this.components = visitor.components;
        this.normalOrScientific = visitor.normalOrScientific;

        this.integerDigitSymbolCount = visitor.integerDigitSymbolCount;
        this.fractionDigitSymbolCount = visitor.fractionDigitSymbolCount;
        this.exponentDigitSymbolCount = visitor.exponentDigitSymbolCount;

        this.decimalPlacesShift = visitor.decimalPlacesShift;
        this.thousandsSeparator = visitor.thousandsSeparator;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return ExpressionNumber.is(value) && context.canConvertOrFail(value, BigDecimal.class);
    }

    @Override
    Optional<SpreadsheetText> format0(final Object value,
                                      final SpreadsheetFormatterContext context) {
        return Optional.of(
                SpreadsheetText.with(
                        this.color(context),
                        this.format1(
                                this.normalOrScientific.context(
                                        context.convertOrFail(value, BigDecimal.class),
                                        this,
                                        context
                                )
                        )
                )
        );
    }

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
     * Executes each of the format components eventually resulting in a {@link String}.
     */
    private String format1(final NumberSpreadsheetFormatterContext context) {
        this.components.forEach(c -> c.append(context));
        return context.formattedText();
    }

    private final NumberSpreadsheetFormatterNormalOrScientific normalOrScientific;

    /**
     * Used to move the decimal places because of formatting options such as percentage(multiply by 100) etc.
     * Positive values represent multiply by 10, and negative represent divide by 10.
     */
    final int decimalPlacesShift;

    /**
     * Components for each symbol in the original pattern.
     */
    private final List<NumberSpreadsheetFormatterComponent> components;

    final int integerDigitSymbolCount;
    final int fractionDigitSymbolCount;
    final int exponentDigitSymbolCount;

    /**
     * When true thousands separators should appear in the output.
     */
    final NumberSpreadsheetFormatterThousandsSeparator thousandsSeparator;

    @Override
    String toStringSuffix() {
        return "";
    }
}
