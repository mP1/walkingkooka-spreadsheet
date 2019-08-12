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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class BasicSpreadsheetEngineContext implements SpreadsheetEngineContext {

    /**
     * Creates a new {@link BasicSpreadsheetEngineContext}
     */
    static BasicSpreadsheetEngineContext with(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                              final SpreadsheetEngine engine,
                                              final SpreadsheetLabelStore labelStore,
                                              final Converter converter,
                                              final ConverterContext converterContext,
                                              final Function<Integer, Optional<Color>> numberToColor,
                                              final Function<String, Optional<Color>> nameToColor,
                                              final int width,
                                              final Function<BigDecimal, Fraction> fractioner,
                                              final SpreadsheetFormatter defaultSpreadsheetTextFormatter) {
        Objects.requireNonNull(functions, "functions");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(converterContext, "converterContext");
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(defaultSpreadsheetTextFormatter, "defaultSpreadsheetTextFormatter");

        return new BasicSpreadsheetEngineContext(functions,
                engine,
                labelStore,
                converter,
                converterContext,
                numberToColor,
                nameToColor,
                width,
                fractioner,
                defaultSpreadsheetTextFormatter);
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetEngineContext(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                          final SpreadsheetEngine engine,
                                          final SpreadsheetLabelStore labelStore,
                                          final Converter converter,
                                          final ConverterContext converterContext,
                                          final Function<Integer, Optional<Color>> numberToColor,
                                          final Function<String, Optional<Color>> nameToColor,
                                          final int width,
                                          final Function<BigDecimal, Fraction> fractioner,
                                          final SpreadsheetFormatter defaultSpreadsheetTextFormatter) {
        super();
        this.parserContext = SpreadsheetParserContexts.basic(converterContext, converterContext);

        this.functions = functions;
        this.function = SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(engine, labelStore, this);

        this.converter = converter;
        this.converterContext = converterContext;

        this.spreadsheetTextFormatContext = SpreadsheetFormatterContexts.basic(numberToColor,
                nameToColor,
                width,
                converter,
                defaultSpreadsheetTextFormatter,
                converterContext);
        this.fractioner = fractioner;
        this.defaultSpreadsheetTextFormatter = defaultSpreadsheetTextFormatter;
    }

    // parsing formula and executing.....................................................................................

    @Override
    public SpreadsheetParserToken parseFormula(final String formula) {
        return SpreadsheetParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(formula), this.parserContext)
                .get()
                .cast();
    }

    private SpreadsheetParserContext parserContext;

    @Override
    public Object evaluate(final ExpressionNode node) {
        return node.toValue(ExpressionEvaluationContexts.basic(this.functions,
                this.function,
                this.converter,
                this.converterContext));
    }

    /**
     * Handles dispatching of functions.
     */
    private final BiFunction<ExpressionNodeName, List<Object>, Object> functions;

    private final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction function;

    // Converter........................................................................................................

    @Override
    public <T> T convert(final Object value, final Class<T> target) {
        return this.converter.convert(value, target, this.converterContext);
    }

    private final Converter converter;
    private final ConverterContext converterContext;

    // parsing and formatting text......................................................................................

    @Override
    public SpreadsheetFormatter parsePattern(final String pattern) {
        return SpreadsheetFormatParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatExpressionParserToken.class::cast)
                .map(this::expression)
                .get();
    }

    private SpreadsheetFormatter expression(final SpreadsheetFormatExpressionParserToken token) {
        return SpreadsheetFormatters.expression(token,
                this.fractioner);
    }

    /**
     * Used to convert a number info a fraction within expressions.
     */
    private final Function<BigDecimal, Fraction> fractioner;

    @Override
    public Optional<SpreadsheetText> format(final Object value,
                                            final SpreadsheetFormatter formatter) {
        return formatter.format(Cast.to(value), this.spreadsheetTextFormatContext);
    }

    private final SpreadsheetFormatterContext spreadsheetTextFormatContext;

    @Override
    public SpreadsheetFormatter defaultSpreadsheetTextFormatter() {
        return this.defaultSpreadsheetTextFormatter;
    }

    private final SpreadsheetFormatter defaultSpreadsheetTextFormatter;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("converter").value(this.converter)
                .label("converterContext").value(this.converterContext)
                .label("fractioner").value(this.fractioner)
                .label("defaultSpreadsheetTextFormatter").value(this.defaultSpreadsheetTextFormatter)
                .build();
    }
}
