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

package walkingkooka.spreadsheet.environment;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextMissingValues;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.MissingEnvironmentValuesException;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.plugin.HasProviderContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps a {@link SpreadsheetEnvironmentContext} with numerous factory methods that cache various components built from the given {@link EnvironmentValueName}.
 */
public final class SpreadsheetEnvironmentContextFactory implements SpreadsheetEnvironmentContextDelegator,
    HasProviderContext {

    public final static EnvironmentValueName<ConverterSelector> CONVERTER = EnvironmentValueName.registerConstant(
        "converter",
        ConverterSelector.class
    );

    public final static EnvironmentValueName<SpreadsheetParserSelector> DATE_PARSER = SpreadsheetMetadataPropertyName.DATE_PARSER.toEnvironmentValueName();

    public final static EnvironmentValueName<Long> DATE_TIME_OFFSET = SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET.toEnvironmentValueName();

    public final static EnvironmentValueName<SpreadsheetParserSelector> DATE_TIME_PARSER = SpreadsheetMetadataPropertyName.DATE_TIME_PARSER.toEnvironmentValueName();

    public final static EnvironmentValueName<DateTimeSymbols> DATE_TIME_SYMBOLS = SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS.toEnvironmentValueName();

    public final static EnvironmentValueName<Integer> DECIMAL_NUMBER_DIGIT_COUNT = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT.toEnvironmentValueName();

    public final static EnvironmentValueName<DecimalNumberSymbols> DECIMAL_NUMBER_SYMBOLS = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS.toEnvironmentValueName();

    public final static EnvironmentValueName<Integer> DEFAULT_YEAR = SpreadsheetMetadataPropertyName.DEFAULT_YEAR.toEnvironmentValueName();

    public final static EnvironmentValueName<ExpressionNumberKind> EXPRESSION_NUMBER_KIND = SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND.toEnvironmentValueName();

    public final static EnvironmentValueName<ExpressionFunctionAliasSet> FUNCTIONS = SpreadsheetMetadataPropertyName.FUNCTIONS.toEnvironmentValueName();

    public final static EnvironmentValueName<Locale> LOCALE = SpreadsheetMetadataPropertyName.LOCALE.toEnvironmentValueName();

    public final static EnvironmentValueName<SpreadsheetParserSelector> NUMBER_PARSER = SpreadsheetMetadataPropertyName.NUMBER_PARSER.toEnvironmentValueName();

    public final static EnvironmentValueName<Integer> PRECISION = SpreadsheetMetadataPropertyName.PRECISION.toEnvironmentValueName();

    public final static EnvironmentValueName<RoundingMode> ROUNDING_MODE = SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName();

    public final static EnvironmentValueName<SpreadsheetParserSelector> TIME_PARSER = SpreadsheetMetadataPropertyName.TIME_PARSER.toEnvironmentValueName();

    public final static EnvironmentValueName<Integer> TWO_DIGIT_YEAR = SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR.toEnvironmentValueName();

    public final static EnvironmentValueName<Character> VALUE_SEPARATOR = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR.toEnvironmentValueName();

    /**
     * A {@link Set} of all the {@link EnvironmentValueName} constants in this class.
     */
    public final static Set<EnvironmentValueName<?>> ENVIRONMENT_VALUE_NAMES = Sets.of(
        CONVERTER,
        DATE_PARSER,
        DATE_TIME_OFFSET,
        DATE_TIME_PARSER,
        DATE_TIME_SYMBOLS,
        DECIMAL_NUMBER_DIGIT_COUNT,
        DECIMAL_NUMBER_SYMBOLS,
        DEFAULT_YEAR,
        EXPRESSION_NUMBER_KIND,
        FUNCTIONS,
        LOCALE,
        NUMBER_PARSER,
        PRECISION,
        ROUNDING_MODE,
        TIME_PARSER,
        TWO_DIGIT_YEAR,
        VALUE_SEPARATOR
    );

    /**
     * Tests if the given {@link EnvironmentValueName} is one of the above constants.
     */
    public static boolean isEnvironmentValueName(final EnvironmentValueName<?> name) {
        return null != name &&
            (
                CONVERTER.equals(name) ||
                    DATE_PARSER.equals(name) ||
                    DATE_TIME_OFFSET.equals(name) ||
                    DATE_TIME_PARSER.equals(name) ||
                    DATE_TIME_SYMBOLS.equals(name) ||
                    DECIMAL_NUMBER_DIGIT_COUNT.equals(name) ||
                    DECIMAL_NUMBER_SYMBOLS.equals(name) ||
                    DEFAULT_YEAR.equals(name) ||
                    EXPRESSION_NUMBER_KIND.equals(name) ||
                    FUNCTIONS.equals(name) ||
                    LINE_ENDING.equals(name) ||
                    LOCALE.equals(name) ||
                    NUMBER_PARSER.equals(name) ||
                    PRECISION.equals(name) ||
                    ROUNDING_MODE.equals(name) ||
                    TIME_PARSER.equals(name) ||
                    TWO_DIGIT_YEAR.equals(name) ||
                    VALUE_SEPARATOR.equals(name)
            );
    }


    public static SpreadsheetEnvironmentContextFactory with(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                            final LocaleContext localeContext,
                                                            final SpreadsheetProvider spreadsheetProvider,
                                                            final ProviderContext providerContext) {
        return new SpreadsheetEnvironmentContextFactory(
            null, // Converter
            null, // SpreadsheetConverterContext
            null, // DateTimeContext
            null, // DecimalNumberContext
            null, // ExpressionNumberContext
            null, // JsonNodeMarshallContext
            null, // JsonNodeMarshallContextObjectPostProcessor
            null, // JsonNodeUnmarshallContext
            null, // JsonNodeUnmarshallContextPreProcessor
            null, // MathContext
            Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext"),
            Objects.requireNonNull(localeContext, "localeContext"),
            Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider"),
            Objects.requireNonNull(providerContext, "providerContext")
        );
    }

    private SpreadsheetEnvironmentContextFactory(final Converter<SpreadsheetConverterContext> converter,
                                                 final SpreadsheetConverterContext spreadsheetConverterContext,
                                                 final DateTimeContext dateTimeContext,
                                                 final DecimalNumberContext decimalNumberContext,
                                                 final ExpressionNumberContext expressionNumberContext,
                                                 final JsonNodeMarshallContext jsonNodeMarshallContext,
                                                 final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                 final JsonNodeUnmarshallContext jsonNodeUnmarshallContext,
                                                 final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor,
                                                 final MathContext mathContext,
                                                 final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                 final LocaleContext localeContext,
                                                 final SpreadsheetProvider spreadsheetProvider,
                                                 final ProviderContext providerContext) {
        super();

        this.converter = converter;
        this.spreadsheetConverterContext = spreadsheetConverterContext;
        this.dateTimeContext = dateTimeContext;
        this.decimalNumberContext = decimalNumberContext;
        this.expressionNumberContext = expressionNumberContext;

        this.jsonNodeMarshallContext = jsonNodeMarshallContext;
        this.jsonNodeMarshallContextObjectPostProcessor = jsonNodeMarshallContextObjectPostProcessor;

        this.jsonNodeUnmarshallContext = jsonNodeUnmarshallContext;
        this.jsonNodeUnmarshallContextPreProcessor = jsonNodeUnmarshallContextPreProcessor;

        this.mathContext = mathContext;

        this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;
        this.localeContext = localeContext;

        this.spreadsheetProvider = spreadsheetProvider;
        this.providerContext = providerContext;

        spreadsheetEnvironmentContext.addEventValueWatcher(this::onEnvironmentValueName);
    }

    /**
     * If one of the core component {@link EnvironmentValueName} changes clear the cached properties so that component will be re-created.
     */
    private void onEnvironmentValueName(final EnvironmentValueName<?> name,
                                        final Optional<?> oldValue,
                                        final Optional<?> newValue) {
        if (isEnvironmentValueName(name)) {
            this.clear();
        }
    }

    // factory methods..................................................................................................

    /**
     * Clears all cached components created using {@link EnvironmentValueName}.
     */
    public void clear() {
        this.converter = null;
        this.spreadsheetConverterContext = null;
        this.dateTimeContext = null;
        this.decimalNumberContext = null;
        this.expressionNumberContext = null;

        this.jsonNodeMarshallContext = null;

        this.jsonNodeUnmarshallContext = null;

        this.mathContext = null;
    }

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
                            this.spreadsheetEnvironmentContext.lineEnding(),
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

    private transient SpreadsheetConverterContext spreadsheetConverterContext;

    // converter........................................................................................................

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

    private transient Converter<SpreadsheetConverterContext> converter;

    // dateTimeContext..................................................................................................

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

    private transient DateTimeContext dateTimeContext;

    // DecimalNumberContext.............................................................................................

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

    private transient DecimalNumberContext decimalNumberContext;

    // ExpressionNumberContext...........................................................................................

    public ExpressionNumberContext expressionNumberContext() {
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

    private transient ExpressionNumberContext expressionNumberContext;

    // expressionNumberKind..................................................................................................

    public ExpressionNumberKind expressionNumberKind() {
        if (null == this.expressionNumberKind) {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = this.spreadsheetEnvironmentContext;
            final EnvironmentContextMissingValues missing = spreadsheetEnvironmentContext.environmentContextMissingValues();

            final ExpressionNumberKind kind = missing.getOrNull(EXPRESSION_NUMBER_KIND);

            missing.reportIfMissing();

            this.expressionNumberKind = kind;
        }
        return this.expressionNumberKind;
    }

    private transient ExpressionNumberKind expressionNumberKind;

    // JsonNodeMarshallContext..........................................................................................

    public JsonNodeMarshallContext jsonNodeMarshallContext() {
        if (null == this.jsonNodeMarshallContext) {
            JsonNodeMarshallContext jsonNodeMarshallContext = JsonNodeMarshallContexts.basic();

            final JsonNodeMarshallContextObjectPostProcessor postProcessor = this.jsonNodeMarshallContextObjectPostProcessor;
            if (null != postProcessor) {
                jsonNodeMarshallContext = jsonNodeMarshallContext.setObjectPostProcessor(postProcessor);
            }

            this.jsonNodeMarshallContext = jsonNodeMarshallContext;
        }
        return this.jsonNodeMarshallContext;
    }

    private transient JsonNodeMarshallContext jsonNodeMarshallContext;

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
            if (null != preProcessor) {
                jsonNodeUnmarshallContext = jsonNodeUnmarshallContext.setPreProcessor(preProcessor);
            }
            this.jsonNodeUnmarshallContext = jsonNodeUnmarshallContext;
        }

        return this.jsonNodeUnmarshallContext;
    }

    private transient JsonNodeUnmarshallContext jsonNodeUnmarshallContext;

    // JsonNodeUnmarshallContext........................................................................................

    public SpreadsheetEnvironmentContextFactory setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
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

    public SpreadsheetEnvironmentContextFactory setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
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

    private SpreadsheetEnvironmentContextFactory setSpreadsheetConverterContext(final SpreadsheetConverterContext spreadsheetConverterContext,
                                                                                final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                                                final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor) {
        return new SpreadsheetEnvironmentContextFactory(
            this.converter,
            spreadsheetConverterContext,
            this.dateTimeContext, //
            this.decimalNumberContext, //
            this.expressionNumberContext,
            this.jsonNodeMarshallContext,
            jsonNodeMarshallContextObjectPostProcessor,
            this.jsonNodeUnmarshallContext,
            jsonNodeUnmarshallContextPreProcessor,
            this.mathContext,
            this.spreadsheetEnvironmentContext,
            this.localeContext,
            this.spreadsheetProvider,
            this.providerContext
        );
    }

    // MathContext......................................................................................................

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

    private transient MathContext mathContext;

    // SpreadsheetParser................................................................................................

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

    private transient SpreadsheetParser spreadsheetParser;

    // SpreadsheetParserContext.........................................................................................

    /**
     * Returns a {@link SpreadsheetParserContext}, built from a few {@link EnvironmentValueName}.
     */
    public SpreadsheetParserContext spreadsheetParserContext() {
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

    // constants........................................................................................................

    public LocaleContext localeContext() {
        return this.localeContext;
    }

    private final LocaleContext localeContext;

    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetProvider;
    }

    private final SpreadsheetProvider spreadsheetProvider;

    // HasProviderContext...............................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    public SpreadsheetEnvironmentContextFactory cloneEnvironment() {
        return setEnvironmentContext(
            this.spreadsheetEnvironmentContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetEnvironmentContextFactory setEnvironmentContext(final EnvironmentContext environmentContext) {
        return this.spreadsheetEnvironmentContext == environmentContext ?
            this :
            with(
                SpreadsheetEnvironmentContexts.basic(
                    environmentContext
                ),
                this.localeContext,
                this.spreadsheetProvider,
                this.providerContext
            );
    }

    @Override
    public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetEnvironmentContext;
    }

    private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.spreadsheetEnvironmentContext.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetEnvironmentContextFactory &&
                this.equals0((SpreadsheetEnvironmentContextFactory) other));
    }

    private boolean equals0(final SpreadsheetEnvironmentContextFactory other) {
        return Objects.equals(this.jsonNodeMarshallContextObjectPostProcessor, other.jsonNodeMarshallContextObjectPostProcessor) &&
            Objects.equals(this.jsonNodeUnmarshallContextPreProcessor, other.jsonNodeUnmarshallContextPreProcessor) &&
            this.spreadsheetEnvironmentContext.equals(other.spreadsheetEnvironmentContext) &&
            this.localeContext.equals(other.localeContext) &&
            this.spreadsheetProvider.equals(other.spreadsheetProvider) &&
            this.providerContext.equals(other.providerContext);
    }

    @Override
    public String toString() {
        return this.spreadsheetEnvironmentContext.toString();
    }
}
