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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterInfo;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link ConverterProvider} for {@link Converter} in {@link SpreadsheetConverters}.
 */
final class SpreadsheetConvertersConverterProvider implements ConverterProvider {

    /**
     * Factory
     */
    static SpreadsheetConvertersConverterProvider with(final Function<ProviderContext, Converter<SpreadsheetConverterContext>> general) {
        return new SpreadsheetConvertersConverterProvider(
            Objects.requireNonNull(general, "general")
        );
    }

    private SpreadsheetConvertersConverterProvider(final Function<ProviderContext, Converter<SpreadsheetConverterContext>> general) {
        super();
        this.general = general;
    }

    @Override
    public <C extends ConverterContext> Converter<C> converter(final ConverterSelector selector,
                                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");

        return selector.evaluateValueText(
            this,
            context
        );
    }

    @Override
    public <C extends ConverterContext> Converter<C> converter(final ConverterName name,
                                                               final List<?> values,
                                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        Converter<?> converter;

        final List<?> copy = Lists.immutable(values);

        switch (name.value()) {
            case BASIC_SPREADSHEET_CONVERTER_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.basic();
                break;
            case BOOLEAN_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.booleans();
                break;
            case COLLECTION_STRING:
                converter = Converters.collection(
                    values.stream()
                        .map(c -> (Converter<C>) c)
                        .collect(Collectors.toList())
                );
                break;
            case COLOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.color();
                break;
            case COLOR_TO_COLOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.colorToColor();
                break;
            case COLOR_TO_NUMBER_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.colorToNumber();
                break;
            case DATE_TIME_SYMBOLS_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.dateTimeSymbols();
                break;
            case DECIMAL_NUMBER_SYMBOLS_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.decimalNumberSymbols();
                break;
            case ENVIRONMENT_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.environment();
                break;
            case ERROR_THROWING_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.errorThrowing();
                break;
            case ERROR_TO_NUMBER_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.errorToNumber();
                break;
            case EXPRESSION_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.expression();
                break;
            case FORMAT_PATTERN_TO_STRING_STRING:
                parameterCountCheck(copy, 1);

                converter = SpreadsheetConverters.formatPatternToString(
                    copy.get(0).toString()
                );
                break;
            case FORM_AND_VALIDATION_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.formAndValidation();
                break;
            case GENERAL_STRING:
                noParameterCheck(copy);

                converter = general.apply(context);
                break;
            case HAS_TEXT_STYLE_TO_STYLE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.hasTextStyleToTextStyle();
                break;
            case JSON_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.json();
                break;
            case JSON_TO_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.jsonTo();
                break;
            case LOCALE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.locale();
                break;
            case NUMBER_TO_COLOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.numberToColor();
                break;
            case NUMBER_TO_NUMBER_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.numberToNumber();
                break;
            case NUMBER_TO_TEXT_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.numberToText();
                break;
            case NULL_TO_NUMBER_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.nullToNumber();
                break;
            case PLUGINS_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.plugins();
                break;
            case SELECTION_TO_SELECTION_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.selectionToSelection();
                break;
            case SELECTION_TO_TEXT_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.selectionToText();
                break;
            case SIMPLE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.simple();
                break;
            case SPREADSHEET_CELL_TO_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.spreadsheetCellTo();
                break;
            case SPREADSHEET_METADATA_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.spreadsheetMetadata();
                break;
            case SPREADSHEET_VALUE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.spreadsheetValue();
                break;
            case STYLE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.style();
                break;
            case TEMPLATE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.template();
                break;
            case TEXT_TO_BOOLEAN_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToBoolean();
                break;
            case TEXT_NODE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textNode();
                break;
            case TEXT_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.text();
                break;
            case TEXT_TO_COLOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToColor();
                break;
            case TEXT_TO_ENVIRONMENT_VALUE_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToEnvironmentValueName();
                break;
            case TEXT_TO_ERROR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetError();
                break;
            case TEXT_TO_EXPRESSION_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToExpression();
                break;
            case TEXT_TO_FORM_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToFormName();
                break;
            case TEXT_TO_JSON_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToJson();
                break;
            case TEXT_TO_LOCALE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToLocale();
                break;
            case TEXT_TO_SELECTION_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSelection();
                break;
            case TEXT_TO_SPREADSHEET_COLOR_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetColorName();
                break;
            case TEXT_TO_SPREADSHEET_FORMATTER_SELECTOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetFormatterSelector();
                break;
            case TEXT_TO_SPREADSHEET_ID_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetId();
                break;
            case TEXT_TO_SPREADSHEET_METADATA_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetMetadata();
                break;
            case TEXT_TO_SPREADSHEET_METADATA_COLOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetMetadataColor();
                break;
            case TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetMetadataPropertyName();
                break;
            case TEXT_TO_SPREADSHEET_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetName();
                break;
            case TEXT_TO_SPREADSHEET_TEXT_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToSpreadsheetText();
                break;
            case TEXT_TO_TEMPLATE_VALUE_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToTemplateValueName();
                break;
            case TEXT_TO_TEXT_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToText();
                break;
            case TEXT_TO_TEXT_NODE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToTextNode();
                break;
            case TEXT_TO_TEXT_STYLE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToTextStyle();
                break;
            case TEXT_TO_TEXT_STYLE_PROPERTY_NAME_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToTextStylePropertyName();
                break;
            case TEXT_TO_URL_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToUrl();
                break;
            case TEXT_TO_VALIDATION_ERROR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToValidationError();
                break;
            case TEXT_TO_VALIDATOR_SELECTOR_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToValidatorSelector();
                break;
            case TEXT_TO_VALUE_TYPE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.textToValueType();
                break;
            case TO_JSON_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.toJson();
                break;
            case TO_NUMBER_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.toNumber();
                break;
            case TO_STYLEABLE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.toStyleable();
                break;
            case TO_TEXT_NODE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.toTextNode();
                break;
            case TO_VALIDATION_ERROR_LIST_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.toValidationErrorList();
                break;
            case URL_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.url();
                break;
            case URL_TO_HYPERLINK_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.urlToHyperlink();
                break;
            case URL_TO_IMAGE_STRING:
                noParameterCheck(copy);

                converter = SpreadsheetConverters.urlToImage();
                break;
            default:
                throw new IllegalArgumentException("Unknown converter " + name);
        }

        return Cast.to(converter);
    }

    /**
     * The {@link Function} that supplies the {@link Converter} when a request comes through for {@link #GENERAL}.
     */
    private final Function<ProviderContext, Converter<SpreadsheetConverterContext>> general;

    private static void noParameterCheck(final List<?> values) {
        parameterCountCheck(
            values,
            0
        );
    }

    private static void parameterCountCheck(final List<?> values,
                                            final int expected) {
        if (expected != values.size()) {
            throw new IllegalArgumentException("Expected " + expected + " values got " + values.size() + " " + values);
        }
    }

    private final static String BASIC_SPREADSHEET_CONVERTER_STRING = "basic";

    final static ConverterName BASIC_SPREADSHEET_CONVERTER = ConverterName.with(BASIC_SPREADSHEET_CONVERTER_STRING);

    private final static String BOOLEAN_STRING = "boolean";

    final static ConverterName BOOLEAN = ConverterName.with(BOOLEAN_STRING);

    private final static String COLLECTION_STRING = "collection";

    final static ConverterName COLLECTION = ConverterName.COLLECTION;

    private final static String COLOR_STRING = "color";

    final static ConverterName COLOR = ConverterName.with(COLOR_STRING);

    private final static String COLOR_TO_COLOR_STRING = "color-to-color";

    final static ConverterName COLOR_TO_COLOR = ConverterName.with(COLOR_TO_COLOR_STRING);

    private final static String COLOR_TO_NUMBER_STRING = "color-to-number";

    final static ConverterName COLOR_TO_NUMBER = ConverterName.with(COLOR_TO_NUMBER_STRING);

    private final static String DATE_TIME_SYMBOLS_STRING = "date-time-symbols";

    final static ConverterName DATE_TIME_SYMBOLS= ConverterName.with(DATE_TIME_SYMBOLS_STRING);

    private final static String DECIMAL_NUMBER_SYMBOLS_STRING = "decimal-number-symbols";

    final static ConverterName DECIMAL_NUMBER_SYMBOLS= ConverterName.with(DECIMAL_NUMBER_SYMBOLS_STRING);

    private final static String ENVIRONMENT_STRING = "environment";

    final static ConverterName ENVIRONMENT = ConverterName.with(ENVIRONMENT_STRING);

    private final static String ERROR_THROWING_STRING = "error-throwing";

    final static ConverterName ERROR_THROWING = ConverterName.with(ERROR_THROWING_STRING);

    private final static String ERROR_TO_NUMBER_STRING = "error-to-number";

    final static ConverterName ERROR_TO_NUMBER = ConverterName.with(ERROR_TO_NUMBER_STRING);

    private final static String EXPRESSION_STRING = "expression";

    final static ConverterName EXPRESSION = ConverterName.with(EXPRESSION_STRING);

    private final static String FORMAT_PATTERN_TO_STRING_STRING = "format-pattern-to-string";

    final static ConverterName FORMAT_PATTERN_TO_STRING = ConverterName.with(FORMAT_PATTERN_TO_STRING_STRING);

    private final static String FORM_AND_VALIDATION_STRING = "form-and-validation";

    final static ConverterName FORM_AND_VALIDATION = ConverterName.with(FORM_AND_VALIDATION_STRING);

    private final static String GENERAL_STRING = "general";

    final static ConverterName GENERAL = ConverterName.with(GENERAL_STRING);

    private final static String HAS_TEXT_STYLE_TO_STYLE_STRING = "has-style-to-style";

    final static ConverterName HAS_TEXT_STYLE_TO_STYLE = ConverterName.with(HAS_TEXT_STYLE_TO_STYLE_STRING);

    private final static String JSON_STRING = "json";

    final static ConverterName JSON = ConverterName.with(JSON_STRING);

    private final static String JSON_TO_STRING = "jsonTo";

    final static ConverterName JSON_TO = ConverterName.with(JSON_TO_STRING);

    private final static String LOCALE_STRING = "locale";

    final static ConverterName LOCALE = ConverterName.with(LOCALE_STRING);

    private final static String NULL_TO_NUMBER_STRING = "null-to-number";

    final static ConverterName NULL_TO_NUMBER = ConverterName.with(NULL_TO_NUMBER_STRING);

    private final static String NUMBER_TO_COLOR_STRING = "number-to-color";

    final static ConverterName NUMBER_TO_COLOR = ConverterName.with(NUMBER_TO_COLOR_STRING);
    
    private final static String NUMBER_TO_NUMBER_STRING = "number-to-number";

    final static ConverterName NUMBER_TO_NUMBER = ConverterName.with(NUMBER_TO_NUMBER_STRING);

    private final static String NUMBER_TO_TEXT_STRING = "number-to-text";

    final static ConverterName NUMBER_TO_TEXT = ConverterName.with(NUMBER_TO_TEXT_STRING);

    private final static String PLUGINS_STRING = "plugins";

    final static ConverterName PLUGINS = ConverterName.with(PLUGINS_STRING);

    private final static String SELECTION_TO_SELECTION_STRING = "selection-to-selection";

    final static ConverterName SELECTION_TO_SELECTION = ConverterName.with(SELECTION_TO_SELECTION_STRING);

    private final static String SELECTION_TO_TEXT_STRING = "selection-to-text";

    final static ConverterName SELECTION_TO_TEXT = ConverterName.with(SELECTION_TO_TEXT_STRING);

    private final static String SIMPLE_STRING = "simple";

    final static ConverterName SIMPLE = ConverterName.with(SIMPLE_STRING);

    private final static String SPREADSHEET_CELL_TO_STRING = "spreadsheet-cell-to";

    final static ConverterName SPREADSHEET_CELL_TO = ConverterName.with(SPREADSHEET_CELL_TO_STRING);

    private final static String SPREADSHEET_METADATA_STRING = "spreadsheet-metadata";

    final static ConverterName SPREADSHEET_METADATA = ConverterName.with(SPREADSHEET_METADATA_STRING);

    private final static String SPREADSHEET_VALUE_STRING = "spreadsheet-value";

    final static ConverterName SPREADSHEET_VALUE = ConverterName.with(SPREADSHEET_VALUE_STRING);

    private final static String STYLE_STRING = "style";

    final static ConverterName STYLE = ConverterName.with(STYLE_STRING);

    private final static String TEMPLATE_STRING = "template";

    final static ConverterName TEMPLATE = ConverterName.with(TEMPLATE_STRING);
    
    private final static String TEXT_STRING = "text";

    final static ConverterName TEXT = ConverterName.with(TEXT_STRING);

    private final static String TEXT_NODE_STRING = "text-node";

    final static ConverterName TEXT_NODE = ConverterName.with(TEXT_NODE_STRING);

    private final static String TEXT_TO_BOOLEAN_STRING = "text-to-boolean";

    final static ConverterName TEXT_TO_BOOLEAN = ConverterName.with(TEXT_TO_BOOLEAN_STRING);
    
    private final static String TEXT_TO_COLOR_STRING = "text-to-color";

    final static ConverterName TEXT_TO_COLOR = ConverterName.with(TEXT_TO_COLOR_STRING);

    private final static String TEXT_TO_ENVIRONMENT_VALUE_NAME_STRING = "text-to-environment-value-name";

    final static ConverterName TEXT_TO_ENVIRONMENT_VALUE_NAME = ConverterName.with(TEXT_TO_ENVIRONMENT_VALUE_NAME_STRING);

    private final static String TEXT_TO_ERROR_STRING = "text-to-error";

    final static ConverterName TEXT_TO_ERROR = ConverterName.with(TEXT_TO_ERROR_STRING);

    private final static String TEXT_TO_EXPRESSION_STRING = "text-to-expression";

    final static ConverterName TEXT_TO_EXPRESSION = ConverterName.with(TEXT_TO_EXPRESSION_STRING);

    private final static String TEXT_TO_FORM_NAME_STRING = "text-to-form-name";

    final static ConverterName TEXT_TO_FORM_NAME = ConverterName.with(TEXT_TO_FORM_NAME_STRING);

    private final static String TEXT_TO_JSON_STRING = "text-to-json";

    final static ConverterName TEXT_TO_JSON = ConverterName.with(TEXT_TO_JSON_STRING);

    private final static String TEXT_TO_LOCALE_STRING = "text-to-locale";

    final static ConverterName TEXT_TO_LOCALE = ConverterName.with(TEXT_TO_LOCALE_STRING);

    private final static String TEXT_TO_SELECTION_STRING = "text-to-selection";

    final static ConverterName TEXT_TO_SELECTION = ConverterName.with(TEXT_TO_SELECTION_STRING);

    private final static String TEXT_TO_SPREADSHEET_COLOR_NAME_STRING = "text-to-spreadsheet-color-name";

    final static ConverterName TEXT_TO_SPREADSHEET_COLOR_NAME = ConverterName.with(TEXT_TO_SPREADSHEET_COLOR_NAME_STRING);

    private final static String TEXT_TO_SPREADSHEET_FORMATTER_SELECTOR_STRING = "text-to-spreadsheet-formatter-selector";

    final static ConverterName TEXT_TO_SPREADSHEET_FORMATTER_SELECTOR = ConverterName.with(TEXT_TO_SPREADSHEET_FORMATTER_SELECTOR_STRING);

    private final static String TEXT_TO_SPREADSHEET_ID_STRING = "text-to-spreadsheet-id";

    final static ConverterName TEXT_TO_SPREADSHEET_ID = ConverterName.with(TEXT_TO_SPREADSHEET_ID_STRING);

    private final static String TEXT_TO_SPREADSHEET_METADATA_STRING = "text-to-spreadsheet-metadata";

    final static ConverterName TEXT_TO_SPREADSHEET_METADATA = ConverterName.with(TEXT_TO_SPREADSHEET_METADATA_STRING);

    private final static String TEXT_TO_SPREADSHEET_METADATA_COLOR_STRING = "text-to-spreadsheet-metadata-color";

    final static ConverterName TEXT_TO_SPREADSHEET_METADATA_COLOR = ConverterName.with(TEXT_TO_SPREADSHEET_METADATA_COLOR_STRING);

    private final static String TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME_STRING = "text-to-spreadsheet-metadata-property-name";

    final static ConverterName TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME = ConverterName.with(TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME_STRING);

    private final static String TEXT_TO_SPREADSHEET_NAME_STRING = "text-to-spreadsheet-name";

    final static ConverterName TEXT_TO_SPREADSHEET_NAME = ConverterName.with(TEXT_TO_SPREADSHEET_NAME_STRING);

    private final static String TEXT_TO_SPREADSHEET_TEXT_STRING = "text-to-spreadsheet-text";

    final static ConverterName TEXT_TO_SPREADSHEET_TEXT = ConverterName.with(TEXT_TO_SPREADSHEET_TEXT_STRING);

    private final static String TEXT_TO_TEMPLATE_VALUE_NAME_STRING = "text-to-template-value-name";

    final static ConverterName TEXT_TO_TEMPLATE_VALUE_NAME = ConverterName.with(TEXT_TO_TEMPLATE_VALUE_NAME_STRING);

    private final static String TEXT_TO_TEXT_STRING = "text-to-text";

    final static ConverterName TEXT_TO_TEXT = ConverterName.with(TEXT_TO_TEXT_STRING);

    private final static String TEXT_TO_TEXT_NODE_STRING = "text-to-text-node";

    final static ConverterName TEXT_TO_TEXT_NODE = ConverterName.with(TEXT_TO_TEXT_NODE_STRING);

    private final static String TEXT_TO_TEXT_STYLE_STRING = "text-to-text-style";

    final static ConverterName TEXT_TO_TEXT_STYLE = ConverterName.with(TEXT_TO_TEXT_STYLE_STRING);

    private final static String TEXT_TO_TEXT_STYLE_PROPERTY_NAME_STRING = "text-to-text-style-property-name";

    final static ConverterName TEXT_TO_TEXT_STYLE_PROPERTY_NAME = ConverterName.with(TEXT_TO_TEXT_STYLE_PROPERTY_NAME_STRING);

    private final static String TEXT_TO_URL_STRING = "text-to-url";

    final static ConverterName TEXT_TO_URL = ConverterName.with(TEXT_TO_URL_STRING);

    private final static String TEXT_TO_VALIDATION_ERROR_STRING = "text-to-validation-error";

    final static ConverterName TEXT_TO_VALIDATION_ERROR = ConverterName.with(TEXT_TO_VALIDATION_ERROR_STRING);

    private final static String TEXT_TO_VALIDATOR_SELECTOR_STRING = "text-to-validator-selector";

    final static ConverterName TEXT_TO_VALIDATOR_SELECTOR = ConverterName.with(TEXT_TO_VALIDATOR_SELECTOR_STRING);

    private final static String TEXT_TO_VALUE_TYPE_STRING = "text-to-value-type";

    final static ConverterName TEXT_TO_VALUE_TYPE = ConverterName.with(TEXT_TO_VALUE_TYPE_STRING);

    private final static String TO_JSON_STRING = "to-json";

    final static ConverterName TO_JSON = ConverterName.with(TO_JSON_STRING);

    private final static String TO_NUMBER_STRING = "to-number";

    final static ConverterName TO_NUMBER = ConverterName.with(TO_NUMBER_STRING);

    private final static String TO_STYLEABLE_STRING = "to-styleable";

    final static ConverterName TO_STYLEABLE = ConverterName.with(TO_STYLEABLE_STRING);
    
    private final static String TO_TEXT_NODE_STRING = "to-text-node";

    final static ConverterName TO_TEXT_NODE = ConverterName.with(TO_TEXT_NODE_STRING);

    private final static String TO_VALIDATION_ERROR_LIST_STRING = "to-validation-error-list";

    final static ConverterName TO_VALIDATION_ERROR_LIST = ConverterName.with(TO_VALIDATION_ERROR_LIST_STRING);
    
    private final static String URL_STRING = "url";

    final static ConverterName URL = ConverterName.with(URL_STRING);
    
    private final static String URL_TO_HYPERLINK_STRING = "url-to-hyperlink";

    final static ConverterName URL_TO_HYPERLINK = ConverterName.with(URL_TO_HYPERLINK_STRING);

    private final static String URL_TO_IMAGE_STRING = "url-to-image";

    final static ConverterName URL_TO_IMAGE = ConverterName.with(URL_TO_IMAGE_STRING);

    @Override
    public ConverterInfoSet converterInfos() {
        return INFOS;
    }

    // @see SpreadsheetConverters constants
    final static ConverterInfoSet INFOS = ConverterInfoSet.with(
        Sets.of(
            converterInfo(BASIC_SPREADSHEET_CONVERTER),
            converterInfo(BOOLEAN),
            converterInfo(COLLECTION),
            converterInfo(COLOR),
            converterInfo(COLOR_TO_COLOR),
            converterInfo(COLOR_TO_NUMBER),
            converterInfo(DATE_TIME_SYMBOLS),
            converterInfo(DECIMAL_NUMBER_SYMBOLS),
            converterInfo(ENVIRONMENT),
            converterInfo(ERROR_THROWING),
            converterInfo(ERROR_TO_NUMBER),
            converterInfo(EXPRESSION),
            converterInfo(FORMAT_PATTERN_TO_STRING),
            converterInfo(FORM_AND_VALIDATION),
            converterInfo(GENERAL),
            converterInfo(HAS_TEXT_STYLE_TO_STYLE),
            converterInfo(JSON),
            converterInfo(JSON_TO),
            converterInfo(LOCALE),
            converterInfo(NULL_TO_NUMBER),
            converterInfo(NUMBER_TO_COLOR),
            converterInfo(NUMBER_TO_NUMBER),
            converterInfo(NUMBER_TO_TEXT),
            converterInfo(PLUGINS),
            converterInfo(SPREADSHEET_CELL_TO),
            converterInfo(SELECTION_TO_SELECTION),
            converterInfo(SELECTION_TO_TEXT),
            converterInfo(SIMPLE),
            converterInfo(SPREADSHEET_METADATA),
            converterInfo(SPREADSHEET_VALUE),
            converterInfo(STYLE),
            converterInfo(TEMPLATE),
            converterInfo(TEXT),
            converterInfo(TEXT_NODE),
            converterInfo(TEXT_TO_BOOLEAN),
            converterInfo(TEXT_TO_COLOR),
            converterInfo(TEXT_TO_ENVIRONMENT_VALUE_NAME),
            converterInfo(TEXT_TO_ERROR),
            converterInfo(TEXT_TO_EXPRESSION),
            converterInfo(TEXT_TO_FORM_NAME),
            converterInfo(TEXT_TO_JSON),
            converterInfo(TEXT_TO_LOCALE),
            converterInfo(TEXT_TO_SELECTION),
            converterInfo(TEXT_TO_SPREADSHEET_COLOR_NAME),
            converterInfo(TEXT_TO_SPREADSHEET_FORMATTER_SELECTOR),
            converterInfo(TEXT_TO_SPREADSHEET_ID),
            converterInfo(TEXT_TO_SPREADSHEET_METADATA),
            converterInfo(TEXT_TO_SPREADSHEET_METADATA_COLOR),
            converterInfo(TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME),
            converterInfo(TEXT_TO_SPREADSHEET_NAME),
            converterInfo(TEXT_TO_SPREADSHEET_TEXT),
            converterInfo(TEXT_TO_TEMPLATE_VALUE_NAME),
            converterInfo(TEXT_TO_TEXT),
            converterInfo(TEXT_TO_TEXT_NODE),
            converterInfo(TEXT_TO_TEXT_STYLE),
            converterInfo(TEXT_TO_TEXT_STYLE_PROPERTY_NAME),
            converterInfo(TEXT_TO_URL),
            converterInfo(TEXT_TO_VALIDATION_ERROR),
            converterInfo(TEXT_TO_VALIDATOR_SELECTOR),
            converterInfo(TEXT_TO_VALUE_TYPE),
            converterInfo(TO_JSON),
            converterInfo(TO_NUMBER),
            converterInfo(TO_STYLEABLE),
            converterInfo(TO_TEXT_NODE),
            converterInfo(TO_VALIDATION_ERROR_LIST),
            converterInfo(URL),
            converterInfo(URL_TO_HYPERLINK),
            converterInfo(URL_TO_IMAGE)
        )
    );

    /**
     * Helper that creates a {@link ConverterInfo} from the given {@link ConverterName} and {@link SpreadsheetConvertersConverterProviders#BASE_URL}.
     */
    private static ConverterInfo converterInfo(final ConverterName name) {
        return ConverterInfo.with(
            SpreadsheetConvertersConverterProviders.BASE_URL.appendPath(
                UrlPath.parse(
                    name.value()
                )
            ),
            name
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
