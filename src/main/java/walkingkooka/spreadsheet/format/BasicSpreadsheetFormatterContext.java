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
import walkingkooka.ToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormatterContext} that basically delegates each of its methods to a dependency given at create time.
 */
final class BasicSpreadsheetFormatterContext implements SpreadsheetFormatterContext,
        SpreadsheetConverterContextDelegator {

    static BasicSpreadsheetFormatterContext with(final Function<Integer, Optional<Color>> numberToColor,
                                                 final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                                 final int cellCharacterWidth,
                                                 final int generalFormatNumberDigitCount,
                                                 final SpreadsheetFormatter formatter,
                                                 final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                                 final SpreadsheetConverterContext spreadsheetConverterContext,
                                                 final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                 final ProviderContext providerContext) {
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        if (cellCharacterWidth <= 0) {
            throw new IllegalArgumentException("Invalid cellCharacterWidth " + cellCharacterWidth + " <= 0");
        }
        if (generalFormatNumberDigitCount <= 0) {
            throw new IllegalArgumentException("Invalid generalFormatNumberDigitCount " + generalFormatNumberDigitCount + " <= 0");
        }
        Objects.requireNonNull(formatter, "formatter");
        Objects.requireNonNull(spreadsheetExpressionEvaluationContext, "spreadsheetExpressionEvaluationContext");
        Objects.requireNonNull(spreadsheetConverterContext, "spreadsheetConverterContext");
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new BasicSpreadsheetFormatterContext(
                numberToColor,
                nameToColor,
                cellCharacterWidth,
                generalFormatNumberDigitCount,
                formatter,
                spreadsheetExpressionEvaluationContext,
                spreadsheetConverterContext,
                spreadsheetFormatterProvider,
                providerContext
        );
    }

    private BasicSpreadsheetFormatterContext(final Function<Integer, Optional<Color>> numberToColor,
                                             final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                             final int cellCharacterWidth,
                                             final int generalFormatNumberDigitCount,
                                             final SpreadsheetFormatter formatter,
                                             final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                             final SpreadsheetConverterContext spreadsheetConverterContext,
                                             final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                             final ProviderContext providerContext) {
        super();

        this.numberToColor = numberToColor;
        this.nameToColor = nameToColor;
        this.cellCharacterWidth = cellCharacterWidth;
        this.generalFormatNumberDigitCount = generalFormatNumberDigitCount;

        this.formatter = formatter;
        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext;

        this.spreadsheetConverterContext = spreadsheetConverterContext;

        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.providerContext = providerContext;
    }

    // BasicSpreadsheetFormatterContext................................................................................

    @Override
    public int cellCharacterWidth() {
        return this.cellCharacterWidth;
    }

    private final int cellCharacterWidth;

    @Override
    public Optional<Color> colorNumber(final int number) {
        return this.numberToColor.apply(number);
    }

    private final Function<Integer, Optional<Color>> numberToColor;

    @Override
    public Optional<Color> colorName(final SpreadsheetColorName name) {
        return this.nameToColor.apply(name);
    }

    private final Function<SpreadsheetColorName, Optional<Color>> nameToColor;

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<Object> value) {
        return this.spreadsheetExpressionEvaluationContext.apply(value);
    }

    private final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext;

    // Converter........................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converter()
                .canConvert(
                        value,
                        type,
                        this
                );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converter()
                .convert(
                        value,
                        type,
                        this
                );
    }

    // format...........................................................................................................

    @Override
    public Optional<TextNode> format(final Optional<Object> value) {
        return this.formatter.format(
                value,
                this
        );
    }

    private final SpreadsheetFormatter formatter;

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return this.spreadsheetFormatterProvider.spreadsheetFormatter(
                selector,
                this.providerContext
        );
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    private final ProviderContext providerContext;

    @Override
    public int generalFormatNumberDigitCount() {
        return this.generalFormatNumberDigitCount;
    }

    private final int generalFormatNumberDigitCount;

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetConverterContext.spreadsheetMetadata();
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        return SpreadsheetFormatterContext.super.validationReference();
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetConverterContext;
    }

    private final SpreadsheetConverterContext spreadsheetConverterContext;

    @Override
    public SpreadsheetFormatterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetConverterContext before = this.spreadsheetConverterContext;
        final SpreadsheetConverterContext after = this.spreadsheetConverterContext.setPreProcessor(processor);

        return before.equals(after) ?
                this :
                new BasicSpreadsheetFormatterContext(
                        this.numberToColor,
                        this.nameToColor,
                        this.cellCharacterWidth,
                        this.generalFormatNumberDigitCount,
                        this.formatter,
                        this.spreadsheetExpressionEvaluationContext,
                        after,
                        this.spreadsheetFormatterProvider,
                        this.providerContext
                );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("cellCharacterWidth").value(this.cellCharacterWidth)
                .label("numberToColor").value(this.numberToColor)
                .label("nameToColor").value(this.nameToColor)
                .label("spreadsheetConverterContext").value(this.spreadsheetConverterContext)
                .label("spreadsheetFormatterProvider").value(this.spreadsheetFormatterProvider)
                .label("providerContext").value(this.providerContext)

                .build();
    }
}
