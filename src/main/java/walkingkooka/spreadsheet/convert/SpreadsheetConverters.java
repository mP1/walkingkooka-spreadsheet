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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.Binary;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.convert.ColorConverters;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.convert.EnvironmentConverters;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.Url;
import walkingkooka.net.convert.NetConverters;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.props.Properties;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.storage.convert.StorageConverters;
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
import walkingkooka.validation.convert.ValidationConvertConverters;

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
public final class SpreadsheetConverters extends SpreadsheetConvertersGwt
    implements PublicStaticHelper {

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
     * {@see SpreadsheetConverterBasic}
     */
    public static Converter<SpreadsheetConverterContext> basic() {
        if (null == BASIC) {
            BASIC = namedCollection(
                "BASIC",
                Converters.simple(),
                SpreadsheetConverters.collectionTo(),
                SpreadsheetConverters.optionalTo()
            );
        }
        return BASIC;
    }

    private static Converter<SpreadsheetConverterContext> BASIC;

    /**
     * A {@link Converter} that handles converting from or to a {@link Binary} value
     */
    public static Converter<SpreadsheetConverterContext> binary() {
        if (null == BINARY) {
            BINARY = namedCollection(
                "BINARY",
                SpreadsheetConverters.textToBinary(),
                SpreadsheetConverters.toBinary(),
                SpreadsheetConverters.binaryToText()
            );
        }
        return BINARY;
    }

    private static Converter<SpreadsheetConverterContext> BINARY;

    /**
     * {@link  Converters#binaryToString()}
     */
    public static Converter<SpreadsheetConverterContext> binaryToText() {
        return Converters.binaryToString();
    }

    /**
     * A {@link Converter} that handles converting from or to a {@link Boolean} value
     */
    public static Converter<SpreadsheetConverterContext> booleans() {
        if (null == BOOLEAN) {
            BOOLEAN = namedCollection(
                "BOOLEAN",
                SpreadsheetConverters.toBoolean(),
                SpreadsheetConverters.booleanToText()
            );
        }
        return BOOLEAN;
    }

    private static Converter<SpreadsheetConverterContext> BOOLEAN;

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
     * {@see ConverterCollectionTo}
     */
    public static <C extends ConverterContext> Converter<C> collectionTo() {
        return Converters.collectionTo();
    }

    /**
     * {@see ConverterCollectionToList}
     */
    public static <C extends ConverterContext> Converter<C> collectionToList() {
        return Converters.collectionToList();
    }

    /**
     * A converter that involves color as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> color() {
        if (null == COLOR) {
            COLOR = namedCollection(
                "COLOR",
                text(),
                colorToColor(),
                textToColor(),
                colorToNumber(),
                numberToColor(),
                textToSpreadsheetColorName(),
                textToSpreadsheetMetadataColor()
            );
        }
        return COLOR;
    }

    private static Converter<SpreadsheetConverterContext> COLOR;

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
     * A converter that involves {@link walkingkooka.collect.list.CsvStringList}.
     */
    public static Converter<SpreadsheetConverterContext> csv() {
        if (null == CSV) {
            CSV = namedCollection(
                "CSV",
                textToCsvStringList(),
                textToCsvStringSet(),
                toCsvStringList()
            );
        }
        return CSV;
    }

    private static Converter<SpreadsheetConverterContext> CSV;

    /**
     * A collection of currency {@link Converter}.
     */
    public static Converter<SpreadsheetConverterContext> currency() {
        if (null == CURRENCY) {
            CURRENCY = namedCollection(
                "CURRENCY",
                currencyCodeToCurrency(),
                currencyValueToNumber(),
                currencyValueTo(),
                numberToCurrencyValue(),
                textToCurrency(),
                textToCurrencyCode(),
                textToCurrencyValue()
            );
        }
        return CURRENCY;
    }

    private static Converter<SpreadsheetConverterContext> CURRENCY;

    /**
     * {@see Converters.currencyCodeToCurrency}
     */
    public static Converter<SpreadsheetConverterContext> currencyCodeToCurrency() {
        return Converters.currencyCodeToCurrency();
    }

    /**
     * {@see Converters.currencyValueTo}
     */
    public static Converter<SpreadsheetConverterContext> currencyValueTo() {
        return Converters.currencyValueTo();
    }

    /**
     * {@see Converters.currencyValueToNumber}
     */
    public static Converter<SpreadsheetConverterContext> currencyValueToNumber() {
        return Converters.currencyValueToNumber();
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
     * A converter that handles transformations such as {@link Locale} and {@link Properties} to {@link DateTimeSymbols}.
     */
    public static Converter<SpreadsheetConverterContext> dateTimeSymbols() {
        return collection(
            Lists.of(
                toDateTimeSymbols(),
                Converters.localeToDateTimeSymbols(),
                propertiesToDateTimeSymbols()
            )
        );
    }

    /**
     * A converter that handles transformations such as {@link Locale} and {@link Properties} to {@link DecimalNumberSymbols}.
     */
    public static Converter<SpreadsheetConverterContext> decimalNumberSymbols() {
        return collection(
            Lists.of(
                toDecimalNumberSymbols(),
                Converters.localeToDecimalNumberSymbols(),
                propertiesToDecimalNumberSymbols()
            )
        );
    }

    /**
     * A converter that involves {@link walkingkooka.environment.EnvironmentContext} as a source or destination,
     * or values from an {@link walkingkooka.environment.EnvironmentContext}.
     */
    public static Converter<SpreadsheetConverterContext> environment() {
        if (null == ENVIRONMENT) {
            ENVIRONMENT = namedCollection(
                "ENVIRONMENT",
                EnvironmentConverters.toEnvironment(),
                EnvironmentConverters.textToEnvironmentValueName()
            );
        }
        return ENVIRONMENT;
    }

    private static Converter<SpreadsheetConverterContext> ENVIRONMENT;

    /**
     * {@link EnvironmentConverters#environmentToBinary()}
     */
    public static Converter<SpreadsheetConverterContext> environmentToBinary() {
        return EnvironmentConverters.environmentToBinary();
    }

    /**
     * {@link EnvironmentConverters#environmentToString()}
     */
    public static Converter<SpreadsheetConverterContext> environmentToText() {
        return EnvironmentConverters.environmentToString();
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetErrorToSpreadsheetError}
     */
    public static Converter<SpreadsheetConverterContext> errorToError() {
        return SpreadsheetConverterSpreadsheetErrorToSpreadsheetError.INSTANCE;
    }

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
        if (null == EXPRESSION) {
            EXPRESSION = namedCollection(
                "EXPRESSION",
                textToExpression()
            );
        }
        return EXPRESSION;
    }

    private static Converter<SpreadsheetConverterContext> EXPRESSION;

    /**
     * A converter that involves {@link walkingkooka.validation.form.Form} and {@link walkingkooka.validation.Validator}
     */
    public static Converter<SpreadsheetConverterContext> formAndValidation() {
        if (null == FORM_AND_VALIDATION) {
            FORM_AND_VALIDATION = namedCollection(
                "FORM-AND-VALIDATION",
                textToFormName(),
                textToValidationError(),
                toValidationCheckbox(),
                toValidationChoice(),
                toValidationChoiceList(),
                toValidationErrorList()
            );
        }
        return FORM_AND_VALIDATION;
    }

    private static Converter<SpreadsheetConverterContext> FORM_AND_VALIDATION;

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
     * {@see SpreadsheetConverterHasSpreadsheetSelection}
     */
    public static Converter<SpreadsheetConverterContext> hasSpreadsheetSelection() {
        return SpreadsheetConverterHasSpreadsheetSelection.INSTANCE;
    }

    /**
     * {@link ValidationConvertConverters#hasOptionalValidatorSelector}
     */
    public static Converter<SpreadsheetConverterContext> hasValidatorSelector() {
        return ValidationConvertConverters.hasOptionalValidatorSelector();
    }

    /**
     * A converter that involves {@link JsonNode} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> json() {
        return JSON;
    }

    private final static Converter<SpreadsheetConverterContext> JSON = namedCollection(
        "JSON",
        jsonTo(),
        textToObject(),
        toJsonNode(),
        textToJson(),
        textToJsonPointer(),
        textToJsonSelector(),
        toJsonText()
    );

    /**
     * {@see JsonNodeConverters#jsonNodeTo}
     */
    public static Converter<SpreadsheetConverterContext> jsonTo() {
        return JsonNodeConverters.jsonNodeTo();
    }

    /**
     * A converter that involves {@link Locale}, {@link walkingkooka.locale.LocaleLanguageTag} as a source or destination.
     * In any {@link Converter} declarations it should appear before {@link #value()}, otherwise converting {@link Locale}
     * to {@link String} will be incorrect returning {@link Locale#toString()} rather than {@link Locale#toLanguageTag()}.
     */
    public static Converter<SpreadsheetConverterContext> locale() {
        if (null == LOCALE) {
            LOCALE = namedCollection(
                "LOCALE",
                localeToText(),
                toLocale(),
                toLocaleLanguageTag(),
                dateTimeSymbols(),
                decimalNumberSymbols(),
                textToLocaleLanguageTag()
            );
        }
        return LOCALE;
    }

    private static Converter<SpreadsheetConverterContext> LOCALE;

    /**
     * {@link Converters#localeToString}
     */
    public static Converter<SpreadsheetConverterContext> localeToText() {
        return Converters.localeToString();
    }

    /**
     * {@link NetConverters#net()}
     */
    public static Converter<SpreadsheetConverterContext> net() {
        return NetConverters.net();
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
        if (null == NUMBER) {
            NUMBER = namedCollection(
                "NUMBER",
                nullToNumber(),
                numberToNumber(),
                toNumber(),
                numberToText()
            );
        }
        return NUMBER;
    }

    private static Converter<SpreadsheetConverterContext> NUMBER;
    
    /**
     * {@see ColorConverters#numberToColor}
     */
    public static Converter<SpreadsheetConverterContext> numberToColor() {
        return ColorConverters.numberToColor();
    }

    /**
     * {@see Converters#numberToCurrencyValue}
     */
    public static Converter<SpreadsheetConverterContext> numberToCurrencyValue() {
        return Converters.numberToCurrencyValue();
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
    public static Converter<SpreadsheetConverterContext> numberToText() {
        return SpreadsheetConverterNumberToText.INSTANCE;
    }

    /**
     * {@see Converters#objectToString()}
     */
    public static Converter<SpreadsheetConverterContext> objectToString() {
        return Converters.objectToString();
    }

    /**
     * {@see ConverterOptionalTo}
     */
    public static Converter<SpreadsheetConverterContext> optionalTo() {
        return Converters.optionalTo();
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
        if (null == PLUGINS) {
            PLUGINS = namedCollection(
                "PLUGINS",
                hasSpreadsheetFormatterSelector(),
                hasSpreadsheetParserSelector(),
                hasValidatorSelector(),
                textToSpreadsheetFormatterSelector(),
                textToValidatorSelector()
            );
        }
        return PLUGINS;
    }

    private static Converter<SpreadsheetConverterContext> PLUGINS;

    /**
     * A converter for properties
     */
    public static Converter<SpreadsheetConverterContext> properties() {
        if (null == PROPERTIES) {
            PROPERTIES = namedCollection(
                "PROPERTIES",
                toProperties(),
                textToProperties()
            );
        }
        return PROPERTIES;
    }

    private static Converter<SpreadsheetConverterContext> PROPERTIES;

    /**
     * {@link Converters#propertiesToDateTimeSymbols()}.
     */
    public static Converter<SpreadsheetConverterContext> propertiesToDateTimeSymbols() {
        return Converters.propertiesToDateTimeSymbols();
    }

    /**
     * {@link Converters#propertiesToDecimalNumberSymbols()}.
     */
    public static Converter<SpreadsheetConverterContext> propertiesToDecimalNumberSymbols() {
        return Converters.propertiesToDecimalNumberSymbols();
    }

    /**
     * {@see SpreadsheetConverterPropertiesToSpreadsheetMetadata}
     */
    public static Converter<SpreadsheetConverterContext> propertiesToSpreadsheetMetadata() {
        return SpreadsheetConverterPropertiesToSpreadsheetMetadata.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterPropertiesToSpreadsheetMetadata}
     */
    public static Converter<SpreadsheetConverterContext> propertiesToTextStyle() {
        return TreeTextConverters.propertiesToTextStyle();
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetCellSet}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetCellSet() {
        return SpreadsheetConverterSpreadsheetCellSet.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetIdToSpreadsheetMetadata() {
        return SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata.INSTANCE;
    }

    /**
     * A converter that involves {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata} data types.
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetMetadata() {
        if (null == SPREADSHEET_METADATA) {
            SPREADSHEET_METADATA = namedCollection(
                "SPREADSHEET METADATA",
                textToSpreadsheetId(),
                textToSpreadsheetMetadata(),
                textToSpreadsheetMetadataPropertyName(),
                textToSpreadsheetName(),
                propertiesToSpreadsheetMetadata(),
                spreadsheetIdToSpreadsheetMetadata()
            );
        }
        return SPREADSHEET_METADATA;
    }

    private static Converter<SpreadsheetConverterContext> SPREADSHEET_METADATA;

    /**
     * A converter that involves converting from or to a {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection}.
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetSelection() {
        if (null == SPREADSHEET_SELECTION) {
            SPREADSHEET_SELECTION = namedCollection(
                "SPREADSHEET SELECTION",
                hasSpreadsheetSelection(),
                spreadsheetSelectionToSpreadsheetSelection(),
                spreadsheetSelectionToText(),
                textToSpreadsheetSelection()
            );
        }
        return SPREADSHEET_SELECTION;
    }

    private static Converter<SpreadsheetConverterContext> SPREADSHEET_SELECTION;

    /**
     * {@see SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetSelectionToSpreadsheetSelection() {
        return SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelection.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetSelectionToText}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetSelectionToText() {
        return SpreadsheetConverterSpreadsheetSelectionToText.INSTANCE;
    }

    /**
     * A converter for storage
     */
    public static Converter<SpreadsheetConverterContext> storage() {
        return SpreadsheetConvertersStorage.STORAGE;
    }

    /**
     * {see StorageConverterStorageBinaryToStorageValueBinary}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueBinary() {
        return StorageConverters.storageBinaryToStorageValueBinary();
    }

    /**
     * {see StorageConverterStorageBinaryToStorageValueSharedCsv}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueCsv() {
        return StorageConverters.storageBinaryToStorageValueCsv();
    }

    /**
     * {see StorageConverterStorageBinaryToStorageValueSharedEnvironment}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueEnvironment() {
        return StorageConverters.storageBinaryToStorageValueEnvironment();
    }
    
    /**
     * {see StorageConverterStorageBinaryToStorageValueSharedExpression}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueExpression() {
        return StorageConverters.storageBinaryToStorageValueExpression();
    }

    /**
     * {see StorageConverterStorageBinaryToStorageSharedValueJson}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueJson() {
        return StorageConverters.storageBinaryToStorageValueJson();
    }

    /**
     * {see StorageConverterStorageBinaryToStorageValueSharedProperties}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueProperties() {
        return StorageConverters.storageBinaryToStorageValueProperties();
    }

    /**
     * {see StorageConverterStorageBinaryToStorageValueSharedTsv}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueTsv() {
        return StorageConverters.storageBinaryToStorageValueTsv();
    }
    
    /**
     * {see StorageConverterStorageBinaryToStorageValueSharedTxt}
     */
    public static <C extends SpreadsheetConverterContext> Converter<C> storageBinaryToStorageValueTxt() {
        return StorageConverters.storageBinaryToStorageValueTxt();
    }

    /**
     * {@see StorageConverters#storageValueInfoListToText}
     */
    public static Converter<SpreadsheetConverterContext> storageValueInfoListToText() {
        return StorageConverters.storageValueInfoListToText();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedBinary}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryBinary() {
        return StorageConverters.storageValueToStorageBinaryBinary();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedCsv}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryCsv() {
        return StorageConverters.storageValueToStorageBinaryCsv();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedEnvironment}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryEnvironment() {
        return StorageConverters.storageValueToStorageBinaryEnvironment();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedExpression}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryExpression() {
        return StorageConverters.storageValueToStorageBinaryExpression();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedJson}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryJson() {
        return StorageConverters.storageValueToStorageBinaryJson();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedProperties}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryProperties() {
        return StorageConverters.storageValueToStorageBinaryProperties();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedTsv}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryTsv() {
        return StorageConverters.storageValueToStorageBinaryTsv();
    }

    /**
     * {@see StorageConverterStorageValueToStorageBinarySharedTxt}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueToStorageBinaryTxt() {
        return StorageConverters.storageValueToStorageBinaryTxt();
    }

    /**
     * A converter that involves {@link TextStyle} as a source or destination
     */
    public static Converter<SpreadsheetConverterContext> style() {
        if (null == STYLE) {
            STYLE = namedCollection(
                "STYLE",
                toStyle(),
                textToBorder(),
                textToMargin(),
                textToPadding(),
                textToTextStyle(),
                textToTextStylePropertyName(),
                SpreadsheetConverters.toStyleable(),
                propertiesToTextStyle()
            );
        }
        return STYLE;
    }

    private static Converter<SpreadsheetConverterContext> STYLE;

    // @VisibleForTesting
    public final static ConverterSelector SYSTEM_CONVERTER_SELECTOR = ConverterSelector.parse(
        "collection(text, boolean, number, date-time, locale, value, error-throwing, color, expression, environment, json, currency, plugins, spreadsheet-metadata, style, text-node, template, net, form-and-validation, basic)"
    );

    /**
     * A {@link Converter} that supports most of the provider conversions and will be used as the system converter for
     * the system {@link ProviderContext}.
     */
    public static Converter<SpreadsheetConverterContext> system() {
        if (null == SYSTEM_CONVERTER) {
            SYSTEM_CONVERTER = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                (ProviderContext context) -> dateTime(
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
                )
            ).converter(
                SYSTEM_CONVERTER_SELECTOR,
                ProviderContexts.fake()
            );
        }
        return SYSTEM_CONVERTER;
    }

    private static Converter<SpreadsheetConverterContext> SYSTEM_CONVERTER;

    /**
     * A converter that involves templating.
     */
    public static Converter<SpreadsheetConverterContext> template() {
        if (null == TEMPLATE) {
            TEMPLATE = namedCollection(
                "TEMPLATE",
                TemplateConverters.textToTemplateValueName()
            );
        }
        return TEMPLATE;
    }

    private static Converter<SpreadsheetConverterContext> TEMPLATE;

    /**
     * A {@link Converter} that handles converting system text conversions.
     */
    public static Converter<SpreadsheetConverterContext> text() {
        if (null == TEXT) {
            TEXT = namedCollection(
                "TEXT",
                Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                SpreadsheetConverters.textToCharset(),
                SpreadsheetConverters.textToIndentation(),
                SpreadsheetConverters.textToLineEnding()
            );
        }
        return TEXT;
    }

    private static Converter<SpreadsheetConverterContext> TEXT;

    /**
     * A converter that involves {@link TextNode} as a source or destination.
     * <br>
     * To create a {@link walkingkooka.tree.text.Hyperlink} with a {@link Url} the target must be {@link walkingkooka.tree.text.Hyperlink}.
     * To create a {@link walkingkooka.tree.text.Image} with a {@link Url} the target must be {@link walkingkooka.tree.text.Image}.
     */
    public static Converter<SpreadsheetConverterContext> textNode() {
        if (null == TEXT_NODE) {
            TEXT_NODE = namedCollection(
                "TEXTNODE",
                textToFlag(),
                textToSpreadsheetText(),
                textToTextNode(),
                toTextNode(),
                urlToHyperlink(),
                urlToImage()
            );
        }
        return TEXT_NODE;
    }

    private static Converter<SpreadsheetConverterContext> TEXT_NODE;

    /**
     * {@link Converters#textToBooleanList()}
     */
    public static Converter<SpreadsheetConverterContext> textToBooleanList() {
        return Converters.textToBooleanList();
    }

    /**
     * {@link TreeTextConverters#textToBorder()}
     */
    public static Converter<SpreadsheetConverterContext> textToBorder() {
        return TreeTextConverters.textToBorder();
    }

    /**
     * {@link Converters#textToBinary()}
     */
    public static Converter<SpreadsheetConverterContext> textToBinary() {
        return Converters.textToBinary();
    }

    /**
     * {@see Converters.textToCharset}
     */
    public static Converter<SpreadsheetConverterContext> textToCharset() {
        return Converters.textToCharset();
    }

    /**
     * {@see ColorConverters#textToColor}
     */
    public static Converter<SpreadsheetConverterContext> textToColor() {
        return ColorConverters.textToColor();
    }

    /**
     * {@link Converters#textToCsvStringList()}
     */
    public static Converter<SpreadsheetConverterContext> textToCsvStringList() {
        return Converters.textToCsvStringList();
    }

    /**
     * {@link Converters#textToCsvStringSet()}
     */
    public static Converter<SpreadsheetConverterContext> textToCsvStringSet() {
        return Converters.textToCsvStringSet();
    }

    /**
     * {@see Converters#textToCurrency()}
     */
    public static Converter<SpreadsheetConverterContext> textToCurrency() {
        return Converters.textToCurrency();
    }

    /**
     * {@see Converters#textToCurrencyCode()}
     */
    public static Converter<SpreadsheetConverterContext> textToCurrencyCode() {
        return Converters.textToCurrencyCode();
    }

    /**
     * {@see Converters#textToCurrencyValue()}
     */
    public static Converter<SpreadsheetConverterContext> textToCurrencyValue() {
        return Converters.textToCurrencyValue();
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
     * {@link Converters#textToLocalDateList()}
     */
    public static Converter<SpreadsheetConverterContext> textToDateList() {
        return Converters.textToLocalDateList();
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
     * {@link Converters#textToLocalDateTimeList()}
     */
    public static Converter<SpreadsheetConverterContext> textToDateTimeList() {
        return Converters.textToLocalDateTimeList();
    }

    /**
     * {@link NetConverters#textToEmailAddress}
     */
    public static Converter<SpreadsheetConverterContext> textToEmailAddress() {
        return NetConverters.textToEmailAddress();
    }

    /**
     * {@see EnvironmentConverters#textToEnvironment}
     */
    public static Converter<SpreadsheetConverterContext> textToEnvironment() {
        return EnvironmentConverters.textToEnvironment();
    }

    /**
     * {@see EnvironmentConverters#textToEnvironmentValueName}
     */
    public static Converter<SpreadsheetConverterContext> textToEnvironmentValueName() {
        return EnvironmentConverters.textToEnvironmentValueName();
    }

    /**
     * {@see SpreadsheetConverterTextToExpression}
     */
    public static Converter<SpreadsheetConverterContext> textToExpression() {
        return SpreadsheetConverterTextToExpression.INSTANCE;
    }

    /**
     * {@link TreeTextConverters#textToFlag()}
     */
    public static Converter<SpreadsheetConverterContext> textToFlag() {
        return TreeTextConverters.textToFlag();
    }

    /**
     * {@see ValidationConvertConverters.textToFormName}
     */
    public static Converter<SpreadsheetConverterContext> textToFormName() {
        return ValidationConvertConverters.textToFormName();
    }

    /**
     * {@link NetConverters#textToHasHostAddress}
     */
    public static Converter<SpreadsheetConverterContext> textToHasHostAddress() {
        return NetConverters.textToHasHostAddress();
    }

    /**
     * {@link NetConverters#textToHostAddress}
     */
    public static Converter<SpreadsheetConverterContext> textToHostAddress() {
        return NetConverters.textToHostAddress();
    }

    /**
     * {@see Converters#textToIndentation}
     */
    public static Converter<SpreadsheetConverterContext> textToIndentation() {
        return Converters.textToIndentation();
    }

    /**
     * {@see JsonNodeConverters#textToJsonNode}
     */
    public static Converter<SpreadsheetConverterContext> textToJson() {
        return JsonNodeConverters.textToJsonNode();
    }

    /**
     * {@see JsonNodeConverterTextToJsonPointer}
     */
    public static Converter<SpreadsheetConverterContext> textToJsonPointer() {
        return JsonNodeConverters.textToJsonPointer();
    }

    /**
     * {@see JsonNodeConverterTextToJsonSelector}
     */
    public static Converter<SpreadsheetConverterContext> textToJsonSelector() {
        return JsonNodeConverters.textToJsonSelector();
    }

    /**
     * {@see Converters#textToLineEnding}
     */
    public static Converter<SpreadsheetConverterContext> textToLineEnding() {
        return Converters.textToLineEnding();
    }

    /**
     * {@see Converters#textToLocale()}
     */
    public static Converter<SpreadsheetConverterContext> textToLocale() {
        return Converters.textToLocale();
    }

    /**
     * {@see Converters#textToLocaleLanguageTag()}
     */
    public static Converter<SpreadsheetConverterContext> textToLocaleLanguageTag() {
        return Converters.textToLocaleLanguageTag();
    }

    /**
     * {@link TreeTextConverters#textToMargin()}
     */
    public static Converter<SpreadsheetConverterContext> textToMargin() {
        return TreeTextConverters.textToMargin();
    }

    /**
     * {@see NetConverters#textToMediaType()}
     */
    public static Converter<SpreadsheetConverterContext> textToMediaType() {
        return NetConverters.textToMediaType();
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
     * {@link Converters#textToNumberList()}
     */
    public static Converter<SpreadsheetConverterContext> textToNumberList() {
        return Converters.textToNumberList();
    }

    /**
     * {@link JsonNodeConverters#textToObject()}
     */
    public static Converter<SpreadsheetConverterContext> textToObject() {
        return JsonNodeConverters.textToObject();
    }

    /**
     * {@link TreeTextConverters#textToPadding()}
     */
    public static Converter<SpreadsheetConverterContext> textToPadding() {
        return TreeTextConverters.textToPadding();
    }

    /**
     * {@see ConverterTextToPath}
     */
    @GwtIncompatible
    public static Converter<SpreadsheetConverterContext> textToPath() {
        return Converters.textToPath();
    }
    
    /**
     * {@link Converters#textToProperties}
     */
    public static Converter<SpreadsheetConverterContext> textToProperties() {
        return Converters.textToProperties();
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
     * {@see SpreadsheetConverterTextToSpreadsheetMetadataColor}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadataColor() {
        return SpreadsheetConverterTextToSpreadsheetMetadataColor.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadataPropertyName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadataPropertyName() {
        return SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE;
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
     * {@see SpreadsheetConverterTextToSpreadsheetText}
     */
    public static Converter<SpreadsheetConverterContext> textToStoragePath() {
        return StorageConverters.textToStoragePath();
    }

    /**
     * {@link Converters#textToStringList()}
     */
    public static Converter<SpreadsheetConverterContext> textToStringList() {
        return Converters.textToStringList();
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
     * {@link Converters#textToLocalTimeList()}
     */
    public static Converter<SpreadsheetConverterContext> textToTimeList() {
        return Converters.textToLocalTimeList();
    }

    /**
     * {@link Converters#textToTsvStringList()}
     */
    public static Converter<SpreadsheetConverterContext> textToTsvStringList() {
        return Converters.textToTsvStringList();
    }

    /**
     * {@link Converters#textToTsvStringSet()}
     */
    public static Converter<SpreadsheetConverterContext> textToTsvStringSet() {
        return Converters.textToTsvStringSet();
    }

    /**
     * {@see NetConverters#textToUrl()}
     */
    public static Converter<SpreadsheetConverterContext> textToUrl() {
        return NetConverters.textToUrl();
    }

    /**
     * {@see NetConverters#textToUrlFragment()}
     */
    public static Converter<SpreadsheetConverterContext> textToUrlFragment() {
        return NetConverters.textToUrlFragment();
    }

    /**
     * {@see NetConverters#textToUrlQueryString()}
     */
    public static Converter<SpreadsheetConverterContext> textToUrlQueryString() {
        return NetConverters.textToUrlQueryString();
    }

    /**
     * {@see SpreadsheetConverterTextToValidationError}
     */
    public static Converter<SpreadsheetConverterContext> textToValidationError() {
        return SpreadsheetConverterTextToValidationError.INSTANCE;
    }

    /**
     * {@see ValidationConvertConverters.textToValidatorSelector}
     */
    public static Converter<SpreadsheetConverterContext> textToValidatorSelector() {
        return ValidationConvertConverters.textToValidatorSelector();
    }

    /**
     * {@see ValidationConvertConverters.textToValueType}
     */
    public static Converter<SpreadsheetConverterContext> textToValueType() {
        return ValidationConvertConverters.textToValueType();
    }

    /**
     * {@see SpreadsheetConverterTextToZoneOffset}
     */
    public static Converter<SpreadsheetConverterContext> textToZoneOffset() {
        return Converters.textToZoneOffset();
    }

    /**
     * {@link  Converters#toBinary()}
     */
    public static Converter<SpreadsheetConverterContext> toBinary() {
        return Converters.toBinary();
    }

    /**
     * A {@link Converter} that handles converting to a {@link Boolean} value.
     */
    public static Converter<SpreadsheetConverterContext> toBoolean() {
        return SpreadsheetConverterToBoolean.INSTANCE;
    }

    /**
     * {@link Converters#toCsvStringList()}
     */
    public static Converter<SpreadsheetConverterContext> toCsvStringList() {
        return Converters.toCsvStringList();
    }

    /**
     * {@see ConverterToDateTimeSymbols}
     */
    public static Converter<SpreadsheetConverterContext> toDateTimeSymbols() {
        return Converters.toDateTimeSymbols();
    }

    /**
     * {@see ConverterToDecimalNumberSymbols}
     */
    public static Converter<SpreadsheetConverterContext> toDecimalNumberSymbols() {
        return Converters.toDecimalNumberSymbols();
    }

    /**
     * {@link  EnvironmentConverters#toEnvironment()}
     */
    public static Converter<SpreadsheetConverterContext> toEnvironment() {
        return EnvironmentConverters.toEnvironment();
    }

    /**
     * {@link  NetConverters#toHostAddress()}
     */
    public static Converter<SpreadsheetConverterContext> toHostAddress() {
        return NetConverters.toHostAddress();
    }

    /**
     * {@see JsonNodeConverters#toJsonNode}
     */
    public static Converter<SpreadsheetConverterContext> toJsonNode() {
        return JsonNodeConverters.toJsonNode();
    }

    /**
     * {@see JsonNodeConverters#toJsonNodeText}
     */
    public static Converter<SpreadsheetConverterContext> toJsonText() {
        return JsonNodeConverters.toJsonText();
    }

    /**
     * {@see ConverterLocaleToLocale}
     */
    public static Converter<SpreadsheetConverterContext> toLocale() {
        return Converters.toLocale();
    }

    /**
     * {@see ConverterLocaleToLocaleLanguageTag}
     */
    public static Converter<SpreadsheetConverterContext> toLocaleLanguageTag() {
        return Converters.toLocaleLanguageTag();
    }

    /**
     * A {@link Converter} that handles converting from or to a {@link Number} values
     */
    public static Converter<SpreadsheetConverterContext> toNumber() {
        return SpreadsheetConverterToNumber.INSTANCE;
    }

    /**
     * {@see Converters#toProperties}
     */
    public static Converter<SpreadsheetConverterContext> toProperties() {
        return Converters.toProperties();
    }

    /**
     * {@see TreeTextConverters#toTextStyle}
     */
    public static Converter<SpreadsheetConverterContext> toStyle() {
        return TreeTextConverters.toTextStyle();
    }

    /**
     * {@see TreeTextConverters.toStyleable}
     */
    public static Converter<SpreadsheetConverterContext> toStyleable() {
        return TreeTextConverters.toStyleable();
    }

    /**
     * {@see TreeTextConverters#toTextNode}
     */
    public static Converter<SpreadsheetConverterContext> toTextNode() {
        return TreeTextConverters.toTextNode();
    }

    /**
     * {@link Converters#toTsvStringList()}
     */
    public static Converter<SpreadsheetConverterContext> toTsvStringList() {
        return Converters.toTsvStringList();
    }

    /**
     * {@see ValidationConvertConverters.toValidationCheckbox}
     */
    public static Converter<SpreadsheetConverterContext> toValidationCheckbox() {
        return ValidationConvertConverters.toValidationCheckbox();
    }

    /**
     * {@see ValidationConvertConverters.toValidationChoice}
     */
    public static Converter<SpreadsheetConverterContext> toValidationChoice() {
        return ValidationConvertConverters.toValidationChoice();
    }
    
    /**
     * {@see ValidationConvertConverters.toValidationChoiceList}
     */
    public static Converter<SpreadsheetConverterContext> toValidationChoiceList() {
        return ValidationConvertConverters.toValidationChoiceList();
    }

    /**
     * {@see ValidationConvertConverters#toValidationErrorList}
     */
    public static Converter<SpreadsheetConverterContext> toValidationErrorList() {
        return ValidationConvertConverters.toValidationErrorList();
    }

    /**
     * {@link Converters#toValue}
     */
    public static Converter<SpreadsheetConverterContext> toValue() {
        return Converters.toValue();
    }

    /**
     * A converter that involves {@link walkingkooka.collect.list.TsvStringList}.
     */
    public static Converter<SpreadsheetConverterContext> tsv() {
        if (null == TSV) {
            TSV = namedCollection(
                "TSV",
                textToTsvStringList(),
                textToTsvStringSet(),
                toTsvStringList()
            );
        }
        return TSV;
    }

    private static Converter<SpreadsheetConverterContext> TSV;
    
    /**
     * A converter that involves {@link Url}.
     */
    public static Converter<SpreadsheetConverterContext> url() {
        if (null == URL) {
            URL = namedCollection(
                "URL",
                textToUrl(),
                urlToHyperlink(),
                urlToImage()
            );
        }
        return URL;
    }

    private static Converter<SpreadsheetConverterContext> URL;

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

    /**
     * A converter that involves spreadsheet values like {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection},
     * but not system types like number, date etc.
     */
    public static Converter<SpreadsheetConverterContext> value() {
        if (null == VALUE) {
            VALUE = namedCollection(
                "VALUE",
                errorToNumber(),
                nullToNumber(),
                spreadsheetSelection(),
                errorToError(), // must be before #textToSpreadsheetError
                textToSpreadsheetError(),
                textToValueType(),
                textToZoneOffset(),
                spreadsheetCellSet(),
                collectionToList(),
                textToBooleanList(),
                textToDateList(),
                textToDateTimeList(),
                textToLineEnding(),
                textToNumberList(),
                textToTimeList(),
                textToStringList(),
                csv(),
                tsv(),
                binaryToText(),
                Converters.objectToString()
            );
        }
        return VALUE;
    }

    private static Converter<SpreadsheetConverterContext> VALUE;
    
    @SafeVarargs
    static <C extends ConverterContext> Converter<C> namedCollection(final String toString,
                                                                     final Converter<C>... converters) {
        return namedCollection(
            toString,
            Lists.of(
                converters
            )
        );
    }

    static <C extends ConverterContext> Converter<C> namedCollection(final String toString,
                                                                     final List<Converter<C>> converters) {
        return collection(converters)
            .setToString(toString);
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverters() {
        throw new UnsupportedOperationException();
    }
}
