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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.spreadsheet.HasSpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;

import java.math.MathContext;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetFormatterConverterSpreadsheetFormatterContext implements SpreadsheetFormatterContext,
    ConverterContextDelegator,
    JsonNodeMarshallUnmarshallContextDelegator,
    LocaleContextDelegator {

    static SpreadsheetFormatterConverterSpreadsheetFormatterContext with(final SpreadsheetConverterContext context) {
        return new SpreadsheetFormatterConverterSpreadsheetFormatterContext(context);
    }

    private SpreadsheetFormatterConverterSpreadsheetFormatterContext(final SpreadsheetConverterContext context) {
        super();

        this.context = context;
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return HasSpreadsheetCell.NO_CELL;
    }

    @Override
    public int cellCharacterWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Color> colorNumber(final int number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Color> colorName(final SpreadsheetColorName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<TextNode> formatValue(final Optional<Object> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int generalFormatNumberDigitCount() {
        return this.context.spreadsheetMetadata()
            .getOrFail(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT);
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.context.resolveLabel(labelName);
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<Object> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    // ConverterContextDelegator........................................................................................

    @Override
    public ConverterContext converterContext() {
        return this.context;
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.context.converter();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    // JsonNodeMarshallUnmarshallContextDelegator.......................................................................

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.context;
    }

    @Override
    public SpreadsheetFormatterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final SpreadsheetConverterContext before = this.context;
        final SpreadsheetConverterContext after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            new SpreadsheetFormatterConverterSpreadsheetFormatterContext(after);
    }

    @Override
    public SpreadsheetFormatterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetConverterContext before = this.context;
        final SpreadsheetConverterContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new SpreadsheetFormatterConverterSpreadsheetFormatterContext(after);
    }

    // LocaleContextDelegator...........................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.context;
    }

    @Override
    public SpreadsheetFormatterContext setLocale(final Locale locale) {
        this.context.setLocale(locale);
        return this;
    }

    // HasSpreadsheetMetadata............................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.context.spreadsheetMetadata();
    }

    private final SpreadsheetConverterContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.context.toString();
    }
}
