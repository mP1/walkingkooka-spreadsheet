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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.SpreadsheetFormattedText;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatContext;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatContexts;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
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
                                              final DecimalNumberContext decimalNumberContext,
                                              final DateTimeContext dateTimeContext,
                                              final Function<Integer, Color> numberToColor,
                                              final Function<String, Color> nameToColor,
                                              final String generalDecimalFormatPattern,
                                              final int width,
                                              final Function<BigDecimal, Fraction> fractioner,
                                              final SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter) {
        Objects.requireNonNull(functions, "functions");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(decimalNumberContext, "decimalNumberContext");
        Objects.requireNonNull(dateTimeContext, "dateTimeContext");
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        Objects.requireNonNull(generalDecimalFormatPattern, "generalDecimalFormatPattern");
        if(width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(defaultSpreadsheetTextFormatter, "defaultSpreadsheetTextFormatter");

        return new BasicSpreadsheetEngineContext(functions,
                engine,
                labelStore,
                converter,
                decimalNumberContext,
                dateTimeContext,
                numberToColor,
                nameToColor,
                generalDecimalFormatPattern,
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
                                          final DecimalNumberContext decimalNumberContext,
                                          final DateTimeContext dateTimeContext,
                                          final Function<Integer, Color> numberToColor,
                                          final Function<String, Color> nameToColor,
                                          final String generalDecimalFormatPattern,
                                          final int width,
                                          final Function<BigDecimal, Fraction> fractioner,
                                          final SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter) {
        super();
        this.parserContext = SpreadsheetParserContexts.basic(decimalNumberContext);

        this.functions = functions;
        this.function = SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(engine, labelStore, this);

        this.converter = converter;
        this.converterContext = ConverterContexts.basic(decimalNumberContext);
        this.decimalNumberContext = decimalNumberContext;

        this.spreadsheetTextFormatContext = SpreadsheetTextFormatContexts.basic(numberToColor,
                nameToColor,
                generalDecimalFormatPattern,
                width,
                converter,
                dateTimeContext,
                decimalNumberContext);
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
                this.decimalNumberContext));
    }

    /**
     * Handles dispatching of functions.
     */
    private final BiFunction<ExpressionNodeName, List<Object>, Object> functions;

    private final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction function;

    /**
     * Should be a locale and user aware {@link DecimalNumberContext}.
     */
    private final DecimalNumberContext decimalNumberContext;

    // Converter........................................................................................................

    @Override
    public <T> T convert(final Object value, final Class<T> target) {
        return this.converter.convert(value, target, this.converterContext);
    }

    private final Converter converter;
    private final ConverterContext converterContext;

    // parsing and formatting text......................................................................................

    @Override
    public SpreadsheetTextFormatter<?> parseFormatPattern(final String pattern) {
        return SpreadsheetFormatParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic(this.decimalNumberContext))
                .map(SpreadsheetFormatExpressionParserToken.class::cast)
                .map(this::expression)
                .get();
    }

    private SpreadsheetTextFormatter<Object> expression(final SpreadsheetFormatExpressionParserToken token) {
        return SpreadsheetTextFormatters.expression(token,
                this.decimalNumberContext.mathContext(),
                this.fractioner);
    }

    /**
     * Used to convert a number info a fraction within expressions.
     */
    private final Function<BigDecimal, Fraction> fractioner;

    @Override
    public Optional<SpreadsheetFormattedText> format(final Object value,
                                                     final SpreadsheetTextFormatter<?> formatter) {
        return formatter.format(Cast.to(value), this.spreadsheetTextFormatContext);
    }

    private final SpreadsheetTextFormatContext spreadsheetTextFormatContext;

    @Override
    public SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter() {
        return this.defaultSpreadsheetTextFormatter;
    }

    private final SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("decimalNumberContext").value(this.decimalNumberContext)
                .label("converter").value(this.converter)
                .label("fractioner").value(this.fractioner)
                .label("defaultSpreadsheetTextFormatter").value(this.defaultSpreadsheetTextFormatter)
                .build();
    }
}
