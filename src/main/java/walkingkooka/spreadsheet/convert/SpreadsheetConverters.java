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

package walkingkooka.spreadsheet.convert;

import walkingkooka.collect.list.Lists;
import walkingkooka.color.convert.ColorConverters;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.environment.convert.EnvironmentConverters;
import walkingkooka.locale.convert.LocaleConverters;
import walkingkooka.net.Url;
import walkingkooka.net.convert.NetConverters;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.template.convert.TemplateConverters;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.convert.TreeTextConverters;
import walkingkooka.validation.convert.ValidatorConvertConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A collection of factory methods for creating {@link Converter} converters.
 */
public final class SpreadsheetConverters implements PublicStaticHelper {

    private final static Converter<SpreadsheetConverterContext> TEXT = namedCollection(
        "TEXT",
        Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString()
    );

    private static final Function<SpreadsheetConverterContext, SpreadsheetParserContext> SPREADSHEET_CONVERTER_CONTEXT_TO_SPREADSHEET_PARSER_CONTEXT = (final SpreadsheetConverterContext scc) ->
        SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.POSITION,
            scc,
            scc,
            '*' // valueSeparator not required because not parsing multiple values.
        );


    private static final BiFunction<ParserToken, SpreadsheetConverterContext, LocalDate> TOKEN_N_CONTEXT_TO_DATE = (final ParserToken t,
                                                                                                                    final SpreadsheetConverterContext scc) ->
        t.cast(DateSpreadsheetFormulaParserToken.class)
            .toLocalDate(scc);


    private static final BiFunction<ParserToken, SpreadsheetConverterContext, LocalDateTime> TOKEN_N_CONTEXT_TO_DATE_TIME = (final ParserToken t,
                                                                                                                             final SpreadsheetConverterContext scc) ->
        t.cast(DateTimeSpreadsheetFormulaParserToken.class)
            .toLocalDateTime(scc);

    private static final BiFunction<ParserToken, SpreadsheetConverterContext, ExpressionNumber> TOKEN_N_CONTEXT_TO_NUMBER = (final ParserToken t,
                                                                                                                             final SpreadsheetConverterContext scc) ->
        t.cast(NumberSpreadsheetFormulaParserToken.class)
            .toNumber(scc);

    private static final BiFunction<ParserToken, SpreadsheetConverterContext, LocalTime> TOKEN_AND_CONTEXT_TO_TIME = (final ParserToken t,
                                                                                                                      final SpreadsheetConverterContext scc) ->
        t.cast(TimeSpreadsheetFormulaParserToken.class)
            .toLocalTime();

    /**
     * A {@link Converter} that handles the most basic conversion requests
     */
    public static Converter<SpreadsheetConverterContext> basic() {
        return SpreadsheetConverterBasic.INSTANCE;
    }

    /**
     * A {@link Converter} that handles converting from or to a {@link Boolean} value
     */
    public static Converter<SpreadsheetConverterContext> booleans() {
        return BOOLEAN;
    }

    private final static Converter<SpreadsheetConverterContext> BOOLEAN = namedCollection(
        "boolean",
        SpreadsheetConverters.toBoolean(),
        SpreadsheetConverters.booleanToText()
    );

    /**
     * {@see SpreadsheetConverterBooleanToText}
     */
    public static Converter<SpreadsheetConverterContext> booleanToText() {
        return SpreadsheetConverterBooleanToText.INSTANCE;
    }

    /**
     * {@see Converters#collection}
     */
    public static <C extends ConverterContext> Converter<C> collection(final List<Converter<C>> converters) {
        return Converters.collection(
            converters
        );
    }

    /**
     * A converter that involves color as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> color() {
        return COLOR;
    }

    private final static Converter<SpreadsheetConverterContext> COLOR = namedCollection(
        "color",
        text(),
        colorToColor(),
        textToColor(),
        colorToNumber(),
        numberToColor(),
        textToSpreadsheetColorName(),
        textToSpreadsheetMetadataColor()
    );

    /**
     * {@see ColorConverters#colorToColor}
     */
    public static Converter<SpreadsheetConverterContext> colorToColor() {
        return ColorConverters.colorToColor();
    }

    /**
     * {@see ColorConverters#colorToNumber}
     */
    public static Converter<SpreadsheetConverterContext> colorToNumber() {
        return ColorConverters.colorToNumber();
    }

    /**
     * {@link SpreadsheetConverterDateTime()}
     */
    public static Converter<SpreadsheetConverterContext> dateTime(final Converter<SpreadsheetConverterContext> dateToString,
                                                                  final Converter<SpreadsheetConverterContext> dateTimeToString,
                                                                  final Converter<SpreadsheetConverterContext> timeToString,
                                                                  final Converter<SpreadsheetConverterContext> stringToDate,
                                                                  final Converter<SpreadsheetConverterContext> stringToDateTime,
                                                                  final Converter<SpreadsheetConverterContext> stringToTime) {
        return SpreadsheetConverterDateTime.with(
            dateToString,
            dateTimeToString,
            timeToString,
            stringToDate,
            stringToDateTime,
            stringToTime
        );
    }

    /**
     * {@link LocaleConverters#dateTimeSymbols()}
     */
    public static Converter<SpreadsheetConverterContext> dateTimeSymbols() {
        return LocaleConverters.dateTimeSymbols();
    }

    /**
     * {@link LocaleConverters#decimalNumberSymbols()}
     */
    public static Converter<SpreadsheetConverterContext> decimalNumberSymbols() {
        return LocaleConverters.decimalNumberSymbols();
    }

    /**
     * A converter that involves {@link walkingkooka.environment.EnvironmentContext} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> environment() {
        return ENVIRONMENT;
    }

    private final static Converter<SpreadsheetConverterContext> ENVIRONMENT = namedCollection(
        "environment",
        EnvironmentConverters.textToEnvironmentValueName()
    );

    /**
     * {@see SpreadsheetConverterSpreadsheetErrorThrowing}
     */
    public static Converter<SpreadsheetConverterContext> errorThrowing() {
        return SpreadsheetConverterSpreadsheetErrorThrowing.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetErrorToNumber}
     */
    public static Converter<SpreadsheetConverterContext> errorToNumber() {
        return SpreadsheetConverterSpreadsheetErrorToNumber.INSTANCE;
    }

    /**
     * A converter that involves {@link walkingkooka.tree.expression.Expression}
     */
    public static Converter<SpreadsheetConverterContext> expression() {
        return EXPRESSION;
    }

    private final static Converter<SpreadsheetConverterContext> EXPRESSION = namedCollection(
        "expression",
        textToExpression()
    );

    /**
     * A converter that involves {@link walkingkooka.validation.form.Form} and {@link walkingkooka.validation.Validator}
     */
    public static Converter<SpreadsheetConverterContext> formAndValidation() {
        return FORM_AND_VALIDATION;
    }

    private final static Converter<SpreadsheetConverterContext> FORM_AND_VALIDATION = namedCollection(
        "form-and-validation",
        textToFormName(),
        textToValidationError(),
        toValidationErrorList()
    );

    /**
     * {@see SpreadsheetConverterFormatPatternToString}
     */
    public static Converter<SpreadsheetConverterContext> formatPatternToString(final String pattern) {
        return SpreadsheetConverterFormatPatternToString.with(pattern);
    }

    /**
     * {@see SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector}
     */
    public static Converter<SpreadsheetConverterContext> hasSpreadsheetFormatterSelector() {
        return SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterHasOptionalSpreadsheetParserSelector}
     */
    public static Converter<SpreadsheetConverterContext> hasSpreadsheetParserSelector() {
        return SpreadsheetConverterHasOptionalSpreadsheetParserSelector.INSTANCE;
    }
    
    /**
     * {@see TreeTextConverters#hasTextStyle}
     */
    public static Converter<SpreadsheetConverterContext> hasStyle() {
        return TreeTextConverters.hasTextStyle();
    }

    /**
     * {@see TreeTextConverters#hasTextNode}
     */
    public static Converter<SpreadsheetConverterContext> hasTextNode() {
        return TreeTextConverters.hasTextNode();
    }

    /**
     * {@link ValidatorConvertConverters#hasOptionalValidatorSelector}
     */
    public static Converter<SpreadsheetConverterContext> hasValidatorSelector() {
        return ValidatorConvertConverters.hasOptionalValidatorSelector();
    }

    /**
     * A converter that involves {@link JsonNode} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> json() {
        return JSON;
    }

    private final static Converter<SpreadsheetConverterContext> JSON = namedCollection(
        "json",
        textToJson(),
        jsonTo(),
        toJson()
    );

    /**
     * {@see JsonNodeConverters#jsonNodeTo}
     */
    public static Converter<SpreadsheetConverterContext> jsonTo() {
        return JsonNodeConverters.jsonNodeTo();
    }

    /**
     * A converter that involves {@link Locale} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> locale() {
        return LOCALE;
    }

    private final static Converter<SpreadsheetConverterContext> LOCALE = namedCollection(
        "locale",
        localeToText(),
        LocaleConverters.locale(),
        dateTimeSymbols(),
        decimalNumberSymbols()
    );

    /**
     * {@link LocaleConverters#localeToText}
     */
    public static Converter<SpreadsheetConverterContext> localeToText() {
        return LocaleConverters.localeToText();
    }

    /**
     * {@see SpreadsheetConverterNullToNumber}
     */
    public static Converter<SpreadsheetConverterContext> nullToNumber() {
        return SpreadsheetConverterNullToNumber.INSTANCE;
    }

    /**
     * A converter that involves {@link Number} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> number() {
        return NUMBER;
    }

    private final static Converter<SpreadsheetConverterContext> NUMBER = namedCollection(
        "number",
        nullToNumber(),
        numberToNumber(),
        toNumber(
            true // ignoreDecimalNumberContextSymbols
        ),
        numberToText(
            true // ignoreDecimalNumberContextSymbols
        )
    );
    
    /**
     * {@see ColorConverters#numberToColor}
     */
    public static Converter<SpreadsheetConverterContext> numberToColor() {
        return ColorConverters.numberToColor();
    }

    /**
     * {@see ExpressionNumberConverters#numberToNumber}
     */
    public static Converter<SpreadsheetConverterContext> numberToNumber() {
        return ExpressionNumberConverters.numberToNumber();
    }

    /**
     * {@see SpreadsheetConverterNumberToText}
     */
    public static Converter<SpreadsheetConverterContext> numberToText(final boolean ignoreDecimalNumberContextSymbols) {
        return SpreadsheetConverterNumberToText.with(ignoreDecimalNumberContextSymbols);
    }

    /**
     * A wrapper around {@link Converters#parser(Class, Parser, Function, BiFunction)} simplifying the abstraction.
     */
    public static <V> Converter<SpreadsheetConverterContext> parser(final Class<V> parserValueType,
                                                                    final Parser<SpreadsheetParserContext> parser,
                                                                    final BiFunction<ParserToken, SpreadsheetConverterContext, V> parserTokenToValue) {
        return Converters.parser(
            parserValueType, // parserValueType
            parser, // parser
            SPREADSHEET_CONVERTER_CONTEXT_TO_SPREADSHEET_PARSER_CONTEXT,
            parserTokenToValue
        );
    }

    /**
     * A converter that involves plugin as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> plugins() {
        return PLUGINS;
    }

    private final static Converter<SpreadsheetConverterContext> PLUGINS = namedCollection(
        "plugins",
        textToSpreadsheetFormatterSelector(),
        textToValidatorSelector()
    );

    /**
     * {@see Converters#simple}
     */
    public static Converter<SpreadsheetConverterContext> simple() {
        return Converters.simple();
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetCell}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetCellTo() {
        return SpreadsheetConverterSpreadsheetCell.INSTANCE;
    }

    /**
     * A converter that involves {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata} data types.
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetMetadata() {
        return SPREADSHEET_METADATA;
    }

    private final static Converter<SpreadsheetConverterContext> SPREADSHEET_METADATA = namedCollection(
        "spreadsheetMetadata",
        textToSpreadsheetId(),
        textToSpreadsheetMetadata(),
        textToSpreadsheetMetadataPropertyName(),
        textToSpreadsheetName()
    );

    /**
     * {@see SpreadsheetSelectionToSpreadsheetSelectionConverter}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetSelectionToSpreadsheetSelection() {
        return SpreadsheetSelectionToSpreadsheetSelectionConverter.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetSelectionToText}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetSelectionToText() {
        return SpreadsheetConverterSpreadsheetSelectionToText.INSTANCE;
    }

    /**
     * A converter that involves spreadsheet values like {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection},
     * but not system types like number, date etc.
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetValue() {
        return SPREADSHEET_VALUE;
    }

    private final static Converter<SpreadsheetConverterContext> SPREADSHEET_VALUE = namedCollection(
        "spreadsheetValue",
        errorToNumber(),
        nullToNumber(),
        spreadsheetSelectionToSpreadsheetSelection(),
        spreadsheetSelectionToText(),
        textToSpreadsheetSelection(),
        textToSpreadsheetError(),
        textToValueType(),
        hasSpreadsheetFormatterSelector(),
        hasSpreadsheetParserSelector(),
        hasValidatorSelector()
    );

    /**
     * A converter that involves {@link TextStyle} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> style() {
        return STYLE;
    }

    private final static Converter<SpreadsheetConverterContext> STYLE = namedCollection(
        "style",
        hasStyle(),
        textToTextStyle(),
        textToTextStylePropertyName(),
        SpreadsheetConverters.toStyleable()
    );

    /**
     * A {@link Converter} that supports most of the provider conversions and will be used as the system converter for
     * the system {@link ProviderContext}.
     */
    public static Converter<SpreadsheetConverterContext> system() {
        return SYSTEM_CONVERTER;
    }

    private final static Converter<SpreadsheetConverterContext> SYSTEM_DATE_TIME = dateTime(
        SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd")
            .formatter()
            .converter(), // dateToString
        SpreadsheetPattern.parseDateTimeFormatPattern("yyyy/mm/dd hh:mm:ss")
            .formatter()
            .converter(), // dateTimeToString
        SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss")
            .formatter()
            .converter(), // timeToString
        SpreadsheetConverters.textToDate(
            SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                .parser()
        ), // stringToDate
        SpreadsheetConverters.textToDateTime(
            SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm:ss")
                .parser()
        ), // stringToDateTime
        SpreadsheetConverters.textToTime(
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                .parser()
        ) // stringToTime
    );

    // @VisibleForTesting
    final static ConverterSelector SYSTEM_CONVERTER_SELECTOR = ConverterSelector.parse(
        "collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, style, text-node, template, url)"
    );

    private final static Converter<SpreadsheetConverterContext> SYSTEM_CONVERTER = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
        (ProviderContext context) -> SYSTEM_DATE_TIME
    ).converter(
        SYSTEM_CONVERTER_SELECTOR,
        ProviderContexts.fake()
    );

    /**
     * A converter that involves templating.
     */
    public static Converter<SpreadsheetConverterContext> template() {
        return TEMPLATE;
    }

    private final static Converter<SpreadsheetConverterContext> TEMPLATE = namedCollection(
        "template",
        TemplateConverters.textToTemplateValueName()
    );

    /**
     * A {@link Converter} that handles converting system text conversions.
     */
    public static Converter<SpreadsheetConverterContext> text() {
        return TEXT;
    }

    /**
     * A converter that involves {@link TextNode} as a source or destination.
     * <br>
     * To create a {@link walkingkooka.tree.text.Hyperlink} with a {@link Url} the target must be {@link walkingkooka.tree.text.Hyperlink}.
     * To create a {@link walkingkooka.tree.text.Image} with a {@link Url} the target must be {@link walkingkooka.tree.text.Image}.
     */
    public static Converter<SpreadsheetConverterContext> textNode() {
        return TEXT_NODE;
    }

    private final static Converter<SpreadsheetConverterContext> TEXT_NODE = namedCollection(
        "textNode",
        textToSpreadsheetText(),
        textToTextNode(),
        hasTextNode(),
        urlToHyperlink(),
        urlToImage()
    );

    /**
     * {@see ColorConverters#textToColor}
     */
    public static Converter<SpreadsheetConverterContext> textToColor() {
        return ColorConverters.textToColor();
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link DateSpreadsheetFormulaParserToken} and converting
     * that into a {@link LocalDate}.
     */
    public static Converter<SpreadsheetConverterContext> textToDate(final Parser<SpreadsheetParserContext> parser) {
        return parser(
            LocalDate.class, // parserValueType
            parser,
            TOKEN_N_CONTEXT_TO_DATE
        );
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link DateTimeSpreadsheetFormulaParserToken} and converting
     * that into a {@link LocalDateTime}.
     */
    public static Converter<SpreadsheetConverterContext> textToDateTime(final Parser<SpreadsheetParserContext> parser) {
        return parser(
            LocalDateTime.class, // parserValueType
            parser,
            TOKEN_N_CONTEXT_TO_DATE_TIME
        );
    }

    /**
     * {@see SpreadsheetConverterTextToExpression}
     */
    public static Converter<SpreadsheetConverterContext> textToExpression() {
        return SpreadsheetConverterTextToExpression.INSTANCE;
    }

    /**
     * {@see EnvironmentConverters#textToEnvironmentValueName}
     */
    public static Converter<SpreadsheetConverterContext> textToEnvironmentValueName() {
        return EnvironmentConverters.textToEnvironmentValueName();
    }

    /**
     * {@see ValidatorConvertConverters.textToFormName}
     */
    public static Converter<SpreadsheetConverterContext> textToFormName() {
        return ValidatorConvertConverters.textToFormName();
    }

    /**
     * {@see JsonNodeConverters#textToJsonNode}
     */
    public static Converter<SpreadsheetConverterContext> textToJson() {
        return JsonNodeConverters.textToJsonNode();
    }

    /**
     * {@see SpreadsheetConverterTextToLocale}
     */
    public static Converter<SpreadsheetConverterContext> textToLocale() {
        return SpreadsheetConverterTextToLocale.INSTANCE;
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link NumberSpreadsheetFormulaParserToken} and converting
     * that into a {@link Number}.
     */
    public static Converter<SpreadsheetConverterContext> textToNumber(final Parser<SpreadsheetParserContext> parser) {
        return ExpressionNumberConverters.toExpressionNumberThen(
            parser(
                ExpressionNumber.class, // parserValueType
                parser,
                TOKEN_N_CONTEXT_TO_NUMBER
            ),
            numberToNumber()
        );
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetColorName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetColorName() {
        return SpreadsheetConverterTextToSpreadsheetColorName.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetError}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetError() {
        return SpreadsheetConverterTextToSpreadsheetError.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetFormatterSelector}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetFormatterSelector() {
        return SpreadsheetConverterTextToSpreadsheetFormatterSelector.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetId}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetId() {
        return SpreadsheetConverterTextToSpreadsheetId.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadata}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadata() {
        return SpreadsheetConverterTextToSpreadsheetMetadata.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadataPropertyName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadataPropertyName() {
        return SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadataColor}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadataColor() {
        return SpreadsheetConverterTextToSpreadsheetMetadataColor.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetName() {
        return SpreadsheetConverterTextToSpreadsheetName.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetSelection}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetSelection() {
        return SpreadsheetConverterTextToSpreadsheetSelection.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetText}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetText() {
        return SpreadsheetConverterTextToSpreadsheetText.INSTANCE;
    }

    /**
     * {@see TemplateConverters#textToTemplateValueName()}
     */
    public static Converter<SpreadsheetConverterContext> textToTemplateValueName() {
        return TemplateConverters.textToTemplateValueName();
    }

    /**
     * {@see Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString}
     */
    public static Converter<SpreadsheetConverterContext> textToText() {
        return Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
    }

    /**
     * {@see TreeTextConverters.textToTextNode()}
     */
    public static Converter<SpreadsheetConverterContext> textToTextNode() {
        return TreeTextConverters.textToTextNode();
    }

    /**
     * {@see TreeTextConverters.textToTextStyle()}
     */
    public static Converter<SpreadsheetConverterContext> textToTextStyle() {
        return TreeTextConverters.textToTextStyle();
    }

    /**
     * {@see TreeTextConverters.textToTextStylePropertyName()}
     */
    public static Converter<SpreadsheetConverterContext> textToTextStylePropertyName() {
        return TreeTextConverters.textToTextStylePropertyName();
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link TimeSpreadsheetFormulaParserToken} and converting
     * that into a {@link LocalTime}.
     */
    public static Converter<SpreadsheetConverterContext> textToTime(final Parser<SpreadsheetParserContext> parser) {
        return parser(
            LocalTime.class, // parserValueType
            parser,
            TOKEN_AND_CONTEXT_TO_TIME
        );
    }

    /**
     * {@see NetConverters#textToUrl()}
     */
    public static Converter<SpreadsheetConverterContext> textToUrl() {
        return NetConverters.textToUrl();
    }

    /**
     * {@see SpreadsheetConverterTextToValidationError}
     */
    public static Converter<SpreadsheetConverterContext> textToValidationError() {
        return SpreadsheetConverterTextToValidationError.INSTANCE;
    }

    /**
     * {@see ValidatorConvertConverters.textToValidatorSelector}
     */
    public static Converter<SpreadsheetConverterContext> textToValidatorSelector() {
        return ValidatorConvertConverters.textToValidatorSelector();
    }

    /**
     * {@see ValidatorConvertConverters.textToValidationValueTypeName}
     */
    public static Converter<SpreadsheetConverterContext> textToValueType() {
        return ValidatorConvertConverters.textToValidationValueTypeName();
    }

    /**
     * A {@link Converter} that handles converting to a {@link Boolean} value.
     */
    public static Converter<SpreadsheetConverterContext> toBoolean() {
        return SpreadsheetConverterToBoolean.INSTANCE;
    }

    /**
     * {@see JsonNodeConverters#toJsonNode}
     */
    public static Converter<SpreadsheetConverterContext> toJson() {
        return JsonNodeConverters.toJsonNode();
    }

    /**
     * A {@link Converter} that handles converting from or to a {@link Number} values
     */
    public static Converter<SpreadsheetConverterContext> toNumber(final boolean ignoreDecimalNumberContextSymbols) {
        return SpreadsheetConverterToNumber.with(ignoreDecimalNumberContextSymbols);
    }

    /**
     * {@see TreeTextConverters.toStyleable}
     */
    public static Converter<SpreadsheetConverterContext> toStyleable() {
        return TreeTextConverters.toStyleable();
    }

    /**
     * {@see ValidatorConvertConverters#toValidationErrorList}
     */
    public static Converter<SpreadsheetConverterContext> toValidationErrorList() {
        return ValidatorConvertConverters.toValidationErrorList();
    }

    /**
     * {@see SpreadsheetConverterUnformattedNumber}
     */
    public static Converter<SpreadsheetConverterContext> unformattedNumber() {
        return SpreadsheetConverterUnformattedNumber.INSTANCE;
    }

    /**
     * A converter that involves {@link Url}.
     */
    public static Converter<SpreadsheetConverterContext> url() {
        return URL;
    }

    private final static Converter<SpreadsheetConverterContext> URL = namedCollection(
        "url",
        textToUrl(),
        urlToHyperlink(),
        urlToImage()
    );

    /**
     * {@see TreeTextConverters.urlToHyperlink()}
     */
    public static Converter<SpreadsheetConverterContext> urlToHyperlink() {
        return TreeTextConverters.urlToHyperlink();
    }

    /**
     * {@see TreeTextConverters.urlToImage()}
     */
    public static Converter<SpreadsheetConverterContext> urlToImage() {
        return TreeTextConverters.urlToImage();
    }

    @SafeVarargs
    private static <C extends ConverterContext> Converter<C> namedCollection(final String toString,
                                                                             final Converter<C>... converters) {
        return collection(
            Lists.of(
                converters
            )
        ).setToString(toString);
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverters() {
        throw new UnsupportedOperationException();
    }
}
