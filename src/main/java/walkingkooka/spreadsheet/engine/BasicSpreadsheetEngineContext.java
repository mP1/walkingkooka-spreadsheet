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
import walkingkooka.convert.ConverterContext;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class BasicSpreadsheetEngineContext implements SpreadsheetEngineContext {

    /**
     * Creates a new {@link BasicSpreadsheetEngineContext}
     */
    static BasicSpreadsheetEngineContext with(final SpreadsheetMetadata metadata,
                                              final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                              final SpreadsheetEngine engine,
                                              final Function<BigDecimal, Fraction> fractioner,
                                              final SpreadsheetStoreRepository storeRepository) {
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(functions, "functions");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(storeRepository, "storeRepository");

        return new BasicSpreadsheetEngineContext(
                metadata,
                functions,
                engine,
                fractioner,
                storeRepository
        );
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetEngineContext(final SpreadsheetMetadata metadata,
                                          final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                          final SpreadsheetEngine engine,
                                          final Function<BigDecimal, Fraction> fractioner,
                                          final SpreadsheetStoreRepository storeRepository) {
        super();

        this.metadata = metadata;

        final ExpressionNumberConverterContext converterContext = metadata.converterContext();

        this.parserContext = SpreadsheetParserContexts.basic(
                converterContext,
                converterContext,
                metadata.getOrFail(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND),
                metadata.getOrFail(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR)
        );

        this.functions = functions;
        this.function = SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunction.with(
                engine,
                storeRepository.labels(),
                this
        );

        this.spreadsheetFormatContext = SpreadsheetFormatterContexts.basic(
                metadata.numberToColor(),
                metadata.nameToColor(),
                metadata.getOrFail(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH),
                metadata.formatter(),
                converterContext);
        this.fractioner = fractioner;

        this.storeRepository = storeRepository;
    }

    // metadata........................................................................................................

    @Override
    public SpreadsheetMetadata metadata() {
        return this.metadata;
    }

    private final SpreadsheetMetadata metadata;

    // resolveCellReference.............................................................................................

    @Override
    public SpreadsheetCellReference resolveCellReference(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return BasicSpreadsheetEngineContextLookupSpreadsheetExpressionReferenceVisitor.lookup(reference, this.storeRepository().labels());
    }

    // parsing formula and executing.....................................................................................

    @Override
    public SpreadsheetParserToken parseFormula(final String formula) {
        return SpreadsheetParsers.valueOrExpression(this.metadata.parser())
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(formula), this.parserContext)
                .get()
                .cast(SpreadsheetParserToken.class);
    }

    /**
     * This parser is used to parse strings, date, date/time, time and numbers outside an expression but within a formula.
     */
    private final SpreadsheetParserContext parserContext;

    @Override
    public Object evaluate(final Expression node,
                           final Optional<SpreadsheetCell> cell) {
        final SpreadsheetMetadata metadata = this.metadata;

        final ExpressionNumberKind kind = metadata.expressionNumberKind();
        final ConverterContext converterContext = metadata.converterContext();

        return node.toValue(
                ExpressionEvaluationContexts.basic(
                        kind,
                        this.functions,
                        this.function,
                        BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext.with(
                                cell,
                                kind,
                                this.functions,
                                converterContext
                        )
                )
        );
    }


    /**
     * Handles dispatching of functions.
     */
    private final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions;

    private final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunction function;

    // parsing and formatting text......................................................................................

    @Override
    public SpreadsheetFormatter parsePattern(final String pattern) {
        return SpreadsheetFormatParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                .map(t -> SpreadsheetFormatters.expression(t.cast(SpreadsheetFormatExpressionParserToken.class), this.fractioner))
                .get();
    }

    /**
     * Used to convert a number into a fraction within expressions.
     */
    private final Function<BigDecimal, Fraction> fractioner;

    @Override
    public Optional<SpreadsheetText> format(final Object value,
                                            final SpreadsheetFormatter formatter) {
        return formatter.format(Cast.to(value), this.spreadsheetFormatContext);
    }

    private final SpreadsheetFormatterContext spreadsheetFormatContext;

    // Store............................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .globalLength(Integer.MAX_VALUE)
                .valueLength(Integer.MAX_VALUE)
                .label("metadata")
                .value(this.metadata)
                .append(LineEnding.SYSTEM)
                .label("fractioner").value(this.fractioner)
                .build();
    }
}
