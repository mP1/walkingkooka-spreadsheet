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

package walkingkooka.spreadsheet.expression;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentContextMissingValues;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.MissingEnvironmentValuesException;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} using {@link EnvironmentValueName} to create each of the core components
 * required during evaluation, such as a {@link Converter} using the {@link #CONVERTER}. A full list of required
 * {@link EnvironmentValueName} are listed below.
 */
final class SpreadsheetExpressionEvaluationContextSpreadsheetEnvironmentContext implements SpreadsheetExpressionEvaluationContext,
    EnvironmentContextDelegator,
    SpreadsheetConverterContextDelegator,
    TerminalContextDelegator {

    final static EnvironmentValueName<ConverterSelector> CONVERTER = EnvironmentValueName.with("converter");

    final static EnvironmentValueName<SpreadsheetParserSelector> DATE_PARSER = SpreadsheetMetadataPropertyName.DATE_PARSER.toEnvironmentValueName();

    final static EnvironmentValueName<Long> DATE_TIME_OFFSET = SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET.toEnvironmentValueName();

    final static EnvironmentValueName<SpreadsheetParserSelector> DATE_TIME_PARSER = SpreadsheetMetadataPropertyName.DATE_TIME_PARSER.toEnvironmentValueName();

    final static EnvironmentValueName<DateTimeSymbols> DATE_TIME_SYMBOLS = SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS.toEnvironmentValueName();

    final static EnvironmentValueName<Integer> DECIMAL_NUMBER_DIGIT_COUNT = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT.toEnvironmentValueName();

    final static EnvironmentValueName<DecimalNumberSymbols> DECIMAL_NUMBER_SYMBOLS = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS.toEnvironmentValueName();

    final static EnvironmentValueName<Integer> DEFAULT_YEAR = SpreadsheetMetadataPropertyName.DEFAULT_YEAR.toEnvironmentValueName();

    final static EnvironmentValueName<ExpressionNumberKind> EXPRESSION_NUMBER_KIND = SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND.toEnvironmentValueName();

    final static EnvironmentValueName<ExpressionFunctionAliasSet> FUNCTIONS = SpreadsheetMetadataPropertyName.FUNCTIONS.toEnvironmentValueName();

    final static EnvironmentValueName<Locale> LOCALE = EnvironmentContext.LOCALE;

    final static EnvironmentValueName<SpreadsheetParserSelector> NUMBER_PARSER = SpreadsheetMetadataPropertyName.NUMBER_PARSER.toEnvironmentValueName();

    final static EnvironmentValueName<Integer> PRECISION = SpreadsheetMetadataPropertyName.PRECISION.toEnvironmentValueName();

    final static EnvironmentValueName<RoundingMode> ROUNDING_MODE = SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName();

    final static EnvironmentValueName<SpreadsheetParserSelector> TIME_PARSER = SpreadsheetMetadataPropertyName.TIME_PARSER.toEnvironmentValueName();

    final static EnvironmentValueName<Integer> TWO_DIGIT_YEAR = SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR.toEnvironmentValueName();

    final static EnvironmentValueName<Character> VALUE_SEPARATOR = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR.toEnvironmentValueName();

    static SpreadsheetExpressionEvaluationContextSpreadsheetEnvironmentContext with(final LocaleContext localeContext,
                                                                                    final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                    final TerminalContext terminalContext,
                                                                                    final SpreadsheetProvider spreadsheetProvider,
                                                                                    final ProviderContext providerContext) {
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(terminalContext, "terminalContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetExpressionEvaluationContextSpreadsheetEnvironmentContext(
            null, // SpreadsheetConverterContext
            spreadsheetEnvironmentContext,
            null, // JsonNodeMarshallContextObjectPostProcessor
            null, // JsonNodeUnmarshallContextPreProcessor
            localeContext,
            terminalContext,
            spreadsheetProvider,
            null, // ExpressionFunctionProvider
            providerContext
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetEnvironmentContext(final SpreadsheetConverterContext spreadsheetConverterContext,
                                                                                final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                                                final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor,
                                                                                final LocaleContext localeContext,
                                                                                final TerminalContext terminalContext,
                                                                                final SpreadsheetProvider spreadsheetProvider,
                                                                                final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                final ProviderContext providerContext) {
        super();

        this.spreadsheetConverterContext = spreadsheetConverterContext; // may be null
        this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;
        this.jsonNodeMarshallContextObjectPostProcessor = jsonNodeMarshallContextObjectPostProcessor;
        this.jsonNodeUnmarshallContextPreProcessor = jsonNodeUnmarshallContextPreProcessor;

        this.localeContext = localeContext;
        this.terminalContext = terminalContext;

        this.expressionFunctionProvider = expressionFunctionProvider; // may be null
        this.spreadsheetProvider = spreadsheetProvider;

        this.providerContext = providerContext;
    }

    // SpreadsheetExpressionEvaluationContext............................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(cell, "cell");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return NO_CELL;
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
        return Optional.empty();
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");
        return Sets.empty();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");
        return Optional.empty();
    }

    @Override
    public SpreadsheetFormulaParserToken parseExpression(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetParserContext();

        return SpreadsheetFormulaParsers.expression()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(expression, parserContext)
            .get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    @Override
    public SpreadsheetFormulaParserToken parseValueOrExpression(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetParserContext();

        return SpreadsheetFormulaParsers.valueOrExpression(this.spreadsheetParser())
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(expression, parserContext)
            .get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    private ExpressionNumberContext expressionNumberContext() {
        if (null == this.expressionNumberContext) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final ExpressionNumberKind kind = missing.getOrNull(EXPRESSION_NUMBER_KIND);

            DecimalNumberContext decimalNumberContext;
            try {
                decimalNumberContext = this.decimalNumberContext();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                decimalNumberContext = null;
            }

            missing.reportIfMissing();

            this.expressionNumberContext = ExpressionNumberContexts.basic(
                kind,
                decimalNumberContext
            );
        }

        return this.expressionNumberContext;
    }

    private ExpressionNumberContext expressionNumberContext;

    public SpreadsheetParser spreadsheetParser() {
        if (null == this.spreadsheetParser) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final SpreadsheetParserSelector date = missing.getOrNull(DATE_PARSER);
            final SpreadsheetParserSelector dateTime = missing.getOrNull(DATE_TIME_PARSER);
            final SpreadsheetParserSelector number = missing.getOrNull(NUMBER_PARSER);
            final SpreadsheetParserSelector time = missing.getOrNull(TIME_PARSER);

            missing.reportIfMissing();

            final SpreadsheetProvider provider = this.spreadsheetProvider;
            final ProviderContext providerContext = this.providerContext;

            this.spreadsheetParser = SpreadsheetFormulaParsers.valueOrExpression(
                Parsers.alternatives(
                    Lists.of(
                        provider.spreadsheetParser(date, providerContext),
                        provider.spreadsheetParser(dateTime, providerContext),
                        provider.spreadsheetParser(number, providerContext)
                            .andEmptyTextCursor(),
                        provider.spreadsheetParser(time, providerContext)
                    )
                )
            );
        }

        return this.spreadsheetParser;
    }

    private SpreadsheetParser spreadsheetParser;

    /**
     * Returns a {@link SpreadsheetParserContext}, built from a few {@link EnvironmentValueName}.
     */
    private SpreadsheetParserContext spreadsheetParserContext() {
        if (null == this.spreadsheetParserContext) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            // DateTimeContext
            DateTimeContext dateTimeContext;
            try {
                dateTimeContext = this.dateTimeContext();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                dateTimeContext = null;
            }

            // ExpressionNumberContext
            ExpressionNumberContext expressionNumberContext;
            try {
                expressionNumberContext = this.expressionNumberContext();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                expressionNumberContext = null;
            }

            // valueSeparator
            final Character valueSeparator = missing.getOrNull(VALUE_SEPARATOR);

            missing.reportIfMissing();

            this.spreadsheetParserContext = SpreadsheetParserContexts.basic(
                InvalidCharacterExceptionFactory.COLUMN_AND_LINE_EXPECTED,
                dateTimeContext,
                expressionNumberContext,
                valueSeparator
            );
        }

        return this.spreadsheetParserContext;
    }

    private SpreadsheetParserContext spreadsheetParserContext;

    /**
     * Lazily created {@link ExpressionFunctionProvider}, should be nulled whenever environment changes.
     */
    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        if (null == this.expressionFunctionProvider) {
            final EnvironmentContextMissingValues missing = this.spreadsheetEnvironmentContext.environmentContextMissingValues();

            final ExpressionFunctionAliasSet functions = missing.getOrNull(FUNCTIONS);

            missing.reportIfMissing();

            this.expressionFunctionProvider = ExpressionFunctionProviders.aliases(
                functions,
                this.spreadsheetProvider
            );
        }
        return this.expressionFunctionProvider;
    }

    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    private final SpreadsheetProvider spreadsheetProvider;

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");
        return Optional.empty();
    }

    @Override
    public CanConvert canConvert() {
        return this.spreadsheetConverterContext(); // inherit unrelated defaults
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        if (null == this.spreadsheetConverterContext) {
            final EnvironmentContextMissingValues missing = this.spreadsheetEnvironmentContext.environmentContextMissingValues();

            Converter<SpreadsheetConverterContext> converter;
            try {
                converter = this.converter();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                converter = null;
            }

            DateTimeContext dateTimeContext;
            try {
                dateTimeContext = this.dateTimeContext();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                dateTimeContext = null;
            }

            DecimalNumberContext decimalNumberContext;
            try {
                decimalNumberContext = this.decimalNumberContext();
            } catch (final MissingEnvironmentValuesException cause) {
                decimalNumberContext = null;
                missing.addMissing(cause);
            }

            JsonNodeMarshallContext jsonNodeMarshallContext;
            try {
                jsonNodeMarshallContext = this.jsonNodeMarshallContext();
            } catch (final MissingEnvironmentValuesException cause) {
                jsonNodeMarshallContext = null;
                missing.addMissing(cause);
            }

            JsonNodeUnmarshallContext jsonNodeUnmarshallContext;
            try {
                jsonNodeUnmarshallContext = this.jsonNodeUnmarshallContext();
            } catch (final MissingEnvironmentValuesException cause) {
                jsonNodeUnmarshallContext = null;
                missing.addMissing(cause);
            }

            final Long dateOffset = missing.getOrNull(DATE_TIME_OFFSET);
            final ExpressionNumberKind expressionNumberKind = missing.getOrNull(EXPRESSION_NUMBER_KIND);
            final Character valueSeparator = missing.getOrNull(VALUE_SEPARATOR);

            missing.reportIfMissing();

            this.spreadsheetConverterContext = SpreadsheetConverterContexts.basic(
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                converter,
                SpreadsheetLabelNameResolvers.empty(),
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            dateOffset,
                            valueSeparator, // valueSeparator
                            Converters.fake(),
                            dateTimeContext,
                            decimalNumberContext
                        ),
                        expressionNumberKind
                    ),
                    JsonNodeMarshallUnmarshallContexts.basic(
                        jsonNodeMarshallContext,
                        jsonNodeUnmarshallContext
                    )
                ),
                this.localeContext
            );
        }
        return this.spreadsheetConverterContext;
    }

    private SpreadsheetConverterContext spreadsheetConverterContext;

    // converter........................................................................................................

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        if (null == this.converter) {
            final EnvironmentContextMissingValues missing = this.spreadsheetEnvironmentContext.environmentContextMissingValues();

            final ConverterSelector converterSelector = missing.getOrNull(CONVERTER);

            missing.reportIfMissing();

            final Converter<SpreadsheetConverterContext> converter = converterSelector.evaluateValueText(
                this.spreadsheetProvider,
                this.providerContext
            );

            // prefix toString with property name
            this.converter = converter.setToString(
                converterSelector.toString()
            );
        }

        return this.converter;
    }

    private Converter<SpreadsheetConverterContext> converter;

    // dateTimeContext..................................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        if (null == this.dateTimeContext) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final Locale locale = missing.getOrNull(LOCALE);
            final Integer defaultYear = missing.getOrNull(DEFAULT_YEAR);
            final Integer twoYearDigit = missing.getOrNull(TWO_DIGIT_YEAR);

            missing.reportIfMissing();

            DateTimeSymbols dateTimeSymbols = missing.getOrNull(DATE_TIME_SYMBOLS);

            if (null == dateTimeSymbols) {
                dateTimeSymbols = this.localeContext.dateTimeSymbolsForLocale(locale)
                    // Missing DateTimeSymbols for locale EN-AU
                    .orElseThrow(() -> new IllegalArgumentException("Missing " + DateTimeSymbols.class.getSimpleName() + " for locale " + locale));
            }

            this.dateTimeContext = DateTimeContexts.basic(
                dateTimeSymbols,
                locale,
                defaultYear,
                twoYearDigit,
                spreadsheetEnvironmentContext
            );
        }
        return this.dateTimeContext;
    }

    private DateTimeContext dateTimeContext;

    // DecimalNumberContext.............................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        if (null == this.decimalNumberContext) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final Integer decimalNumberDigitCount = missing.getOrNull(DECIMAL_NUMBER_DIGIT_COUNT);
            final Locale locale = missing.getOrNull(LOCALE);

            MathContext mathContext;
            try {
                mathContext = this.mathContext();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                mathContext = null;
            }

            missing.reportIfMissing();

            DecimalNumberSymbols decimalNumberSymbols = missing.getOrNull(DECIMAL_NUMBER_SYMBOLS);

            if (null == decimalNumberSymbols) {
                decimalNumberSymbols = this.localeContext.decimalNumberSymbolsForLocale(locale)
                    .orElseThrow(
                        // Missing DecimalNumberSymbols for locale EN-AU
                        () -> new IllegalArgumentException("Missing " + DecimalNumberSymbols.class.getSimpleName() + " for locale " + locale)
                    );
            }

            this.decimalNumberContext = DecimalNumberContexts.basic(
                decimalNumberDigitCount,
                decimalNumberSymbols,
                locale,
                mathContext
            );
        }
        return this.decimalNumberContext;
    }

    private DecimalNumberContext decimalNumberContext;

    @Override
    public JsonNodeMarshallContext jsonNodeMarshallContext() {
        if(null == this.jsonNodeMarshallContext) {
            JsonNodeMarshallContext jsonNodeMarshallContext = JsonNodeMarshallContexts.basic();

            final JsonNodeMarshallContextObjectPostProcessor postProcessor = this.jsonNodeMarshallContextObjectPostProcessor;
            if(null != postProcessor) {
                jsonNodeMarshallContext = jsonNodeMarshallContext.setObjectPostProcessor(postProcessor);
            }

            this.jsonNodeMarshallContext = jsonNodeMarshallContext;
        }
        return this.jsonNodeMarshallContext;
    }

    private JsonNodeMarshallContext jsonNodeMarshallContext;

    @Override
    public JsonNodeUnmarshallContext jsonNodeUnmarshallContext() {
        if (null == this.jsonNodeUnmarshallContext) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final ExpressionNumberKind expressionNumberKind = missing.getOrNull(EXPRESSION_NUMBER_KIND);

            MathContext mathContext;
            try {
                mathContext = this.mathContext();
            } catch (final MissingEnvironmentValuesException cause) {
                missing.addMissing(cause);
                mathContext = null;
            }

            missing.reportIfMissing();

            JsonNodeUnmarshallContext jsonNodeUnmarshallContext = JsonNodeUnmarshallContexts.basic(
                expressionNumberKind,
                mathContext
            );

            final JsonNodeUnmarshallContextPreProcessor preProcessor = this.jsonNodeUnmarshallContextPreProcessor;
            if(null != preProcessor) {
                jsonNodeUnmarshallContext = jsonNodeUnmarshallContext.setPreProcessor(preProcessor);
            }
            this.jsonNodeUnmarshallContext = jsonNodeUnmarshallContext;
        }

        return this.jsonNodeUnmarshallContext;
    }

    private JsonNodeUnmarshallContext jsonNodeUnmarshallContext;

    @Override
    public MathContext mathContext() {
        if (null == this.mathContext) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final Integer precision = missing.getOrNull(PRECISION);
            final RoundingMode roundingMode = missing.getOrNull(ROUNDING_MODE);

            missing.reportIfMissing();

            this.mathContext = new MathContext(
                precision,
                roundingMode
            );
        }

        return this.mathContext;
    }

    private MathContext mathContext;

    @Override
    public LocaleContext localeContext() {
        return this.localeContext;
    }

    private final LocaleContext localeContext;

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.spreadsheetEnvironmentContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetEnvironmentContext before = this.spreadsheetEnvironmentContext;
        final SpreadsheetEnvironmentContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetExpressionEvaluationContextSpreadsheetEnvironmentContext(
                null,// spreadsheetConverterContext  clear force recreate!
                after,
                this.jsonNodeMarshallContextObjectPostProcessor,
                this.jsonNodeUnmarshallContextPreProcessor,
                this.localeContext,
                this.terminalContext,
                this.spreadsheetProvider,
                this.expressionFunctionProvider,
                this.providerContext
            );
    }

    @Override
    public <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                          final T value) {
        this.spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public LineEnding lineEnding() {
        return this.spreadsheetEnvironmentContext.lineEnding();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.spreadsheetEnvironmentContext.setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Locale locale() {
        return this.spreadsheetEnvironmentContext.locale();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
        this.spreadsheetEnvironmentContext.setLocale(locale);
        return this;
    }

    @Override
    public LocalDateTime now() {
        return this.spreadsheetEnvironmentContext.now(); // inherit unrelated defaults
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.spreadsheetEnvironmentContext.serverUrl();
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.spreadsheetEnvironmentContext.spreadsheetId();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);
        return this;
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.spreadsheetEnvironmentContext.user();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
        this.spreadsheetEnvironmentContext.setUser(user);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

    // ExpressionEvaluationContext......................................................................................

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return Cast.to(
            this.expressionFunctionProvider()
                .expressionFunction(
                    name,
                    Lists.empty(),
                    this.providerContext
                )
        );
    }

    private final ProviderContext providerContext;

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        return this.expressionFunction(name)
            .isPure(this);
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return parameter.convertOrFail(value, this);
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        return SpreadsheetErrorKind.translate(exception);
    }

    /**
     * If the {@link ExpressionReference} is a {@link EnvironmentValueName} resolves the environment value.
     */
    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return Optional.ofNullable(
            reference instanceof EnvironmentValueName ?
                Cast.to(
                    this.environmentValue(
                        (EnvironmentValueName<?>) reference
                    )
                ) :
                null
        );
    }

    // FormHandlerContext...............................................................................................

    @Override
    public Form<SpreadsheetExpressionReference> form() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> formFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    // JsonNodeUnmarshallContext........................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final SpreadsheetConverterContext before = this.spreadsheetConverterContext();
        final SpreadsheetConverterContext after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            this.setSpreadsheetConverterContext(
                after,
                processor,
                this.jsonNodeUnmarshallContextPreProcessor
            );
    }

    private final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor;

    @Override
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetConverterContext before = this.spreadsheetConverterContext();
        final SpreadsheetConverterContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            this.setSpreadsheetConverterContext(
                after,
                this.jsonNodeMarshallContextObjectPostProcessor,
                processor
            );
    }

    private final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor;

    private SpreadsheetExpressionEvaluationContext setSpreadsheetConverterContext(final SpreadsheetConverterContext context,
                                                                                  final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                                                  final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor) {
        return new SpreadsheetExpressionEvaluationContextSpreadsheetEnvironmentContext(
            context,
            this.spreadsheetEnvironmentContext,
            jsonNodeMarshallContextObjectPostProcessor,
            jsonNodeUnmarshallContextPreProcessor,
            this.localeContext,
            this.terminalContext,
            this.spreadsheetProvider,
            this.expressionFunctionProvider,
            this.providerContext
        );
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public Storage<StorageExpressionEvaluationContext> storage() {
        throw new UnsupportedOperationException();
    }

    // TerminalContextDelegator.........................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext exitTerminal() {
        this.terminalContext.exitTerminal();
        return this;
    }

    @Override
    public TerminalContext terminalContext() {
        return this.terminalContext;
    }

    private final TerminalContext terminalContext;

    // ValidationExpressionEvaluationContext............................................................................

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Object> validationValue() {
        return Optional.empty();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetEnvironmentContext.toString();
    }
}
