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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormatterContext} that basically delegates each of its methods to a dependency given at create time.
 */
final class BasicSpreadsheetFormatterContext implements SpreadsheetFormatterContext,
    SpreadsheetConverterContextDelegator {

    static BasicSpreadsheetFormatterContext with(final Optional<SpreadsheetCell> cell,
                                                 final Function<Integer, Optional<Color>> numberToColor,
                                                 final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                                 final int cellCharacterWidth,
                                                 final int generalFormatNumberDigitCount,
                                                 final SpreadsheetFormatter formatter,
                                                 final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                                 final SpreadsheetConverterContext spreadsheetConverterContext,
                                                 final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                 final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
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
            cell,
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

    private BasicSpreadsheetFormatterContext(final Optional<SpreadsheetCell> cell,
                                             final Function<Integer, Optional<Color>> numberToColor,
                                             final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                             final int cellCharacterWidth,
                                             final int generalFormatNumberDigitCount,
                                             final SpreadsheetFormatter formatter,
                                             final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                             final SpreadsheetConverterContext spreadsheetConverterContext,
                                             final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                             final ProviderContext providerContext) {
        super();

        this.cell = cell;

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
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

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

    // formatValue......................................................................................................

    @Override
    public Optional<TextNode> formatValue(final Optional<Object> value) {
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
    public SpreadsheetFormatterContext setLocale(final Locale locale) {
        this.spreadsheetConverterContext.setLocale(locale);
        return this;
    }

    @Override
    public SpreadsheetFormatterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        return this.setConverterContext(
            this.spreadsheetConverterContext.setObjectPostProcessor(processor)
        );
    }

    @Override
    public SpreadsheetFormatterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        return this.setConverterContext(
            this.spreadsheetConverterContext.setPreProcessor(processor)
        );
    }

    private SpreadsheetFormatterContext setConverterContext(final SpreadsheetConverterContext context) {
        return this.spreadsheetConverterContext.equals(context) ?
            this :
            new BasicSpreadsheetFormatterContext(
                this.cell,
                this.numberToColor,
                this.nameToColor,
                this.cellCharacterWidth,
                this.generalFormatNumberDigitCount,
                this.formatter,
                this.spreadsheetExpressionEvaluationContext,
                context,
                this.spreadsheetFormatterProvider,
                this.providerContext
            );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("cell").value(this.cell)
            .label("cellCharacterWidth").value(this.cellCharacterWidth)
            .label("numberToColor").value(this.numberToColor)
            .label("nameToColor").value(this.nameToColor)
            .label("spreadsheetConverterContext").value(this.spreadsheetConverterContext)
            .label("spreadsheetFormatterProvider").value(this.spreadsheetFormatterProvider)
            .label("providerContext").value(this.providerContext)

            .build();
    }
}
