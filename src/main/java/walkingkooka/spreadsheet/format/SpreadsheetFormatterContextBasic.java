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
import walkingkooka.color.ColorContext;
import walkingkooka.color.ColorContextDelegator;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.HasSpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormatterContext} that basically delegates each of its methods to a dependency given at create time.
 */
final class SpreadsheetFormatterContextBasic implements SpreadsheetFormatterContext,
    SpreadsheetConverterContextDelegator,
    ColorContextDelegator {

    static SpreadsheetFormatterContextBasic with(final HasSpreadsheetCell hasSpreadsheetCell,
                                                 final int cellCharacterWidth,
                                                 final SpreadsheetFormatter formatter,
                                                 final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                                 final SpreadsheetConverterContext spreadsheetConverterContext,
                                                 final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                 final ProviderContext providerContext) {
        Objects.requireNonNull(hasSpreadsheetCell, "hasSpreadsheetCell");
        if (cellCharacterWidth <= 0) {
            throw new IllegalArgumentException("Invalid cellCharacterWidth " + cellCharacterWidth + " <= 0");
        }
        Objects.requireNonNull(formatter, "formatter");
        Objects.requireNonNull(spreadsheetExpressionEvaluationContext, "spreadsheetExpressionEvaluationContext");
        Objects.requireNonNull(spreadsheetConverterContext, "spreadsheetConverterContext");
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetFormatterContextBasic(
            hasSpreadsheetCell,
            cellCharacterWidth,
            formatter,
            spreadsheetExpressionEvaluationContext,
            spreadsheetConverterContext,
            spreadsheetFormatterProvider,
            providerContext
        );
    }

    private SpreadsheetFormatterContextBasic(final HasSpreadsheetCell hasSpreadsheetCell,
                                             final int cellCharacterWidth,
                                             final SpreadsheetFormatter formatter,
                                             final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                             final SpreadsheetConverterContext spreadsheetConverterContext,
                                             final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                             final ProviderContext providerContext) {
        super();

        this.hasSpreadsheetCell = hasSpreadsheetCell;

        this.cellCharacterWidth = cellCharacterWidth;

        this.formatter = formatter;
        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext;

        this.spreadsheetConverterContext = spreadsheetConverterContext;

        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.providerContext = providerContext;
    }

    // SpreadsheetFormatterContextBasic................................................................................

    @Override
    public Optional<SpreadsheetCell> spreadsheetCell() {
        return this.hasSpreadsheetCell.spreadsheetCell();
    }

    private final HasSpreadsheetCell hasSpreadsheetCell;

    @Override
    public int cellCharacterWidth() {
        return this.cellCharacterWidth;
    }

    private final int cellCharacterWidth;

    @Override
    public Optional<Color> colorNumber(final int number) {
        return this.lookupColor(
            Color.indexed(number)
        );
    }

    @Override
    public Optional<Color> colorName(final SpreadsheetColorName name) {
        return this.lookupColor(
            Color.named(
                name.value()
            )
        );
    }

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

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetConverterContext.spreadsheetMetadata();
    }

    @Override
    public SpreadsheetValidationReference validationReference() {
        return SpreadsheetFormatterContext.super.validationReference();
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetConverterContext;
    }

    private final SpreadsheetConverterContext spreadsheetConverterContext;

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
            new SpreadsheetFormatterContextBasic(
                this.hasSpreadsheetCell,
                this.cellCharacterWidth,
                this.formatter,
                this.spreadsheetExpressionEvaluationContext,
                context,
                this.spreadsheetFormatterProvider,
                this.providerContext
            );
    }

    // ColorContextDelegator............................................................................................

    @Override
    public ColorContext colorContext() {
        return this.spreadsheetMetadata();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("cellCharacterWidth").value(this.cellCharacterWidth)
            .label("hasSpreadsheetCell").value(this.hasSpreadsheetCell)
            .label("spreadsheetConverterContext").value(this.spreadsheetConverterContext)
            .label("spreadsheetFormatterProvider").value(this.spreadsheetFormatterProvider)
            .label("providerContext").value(this.providerContext)

            .build();
    }
}
