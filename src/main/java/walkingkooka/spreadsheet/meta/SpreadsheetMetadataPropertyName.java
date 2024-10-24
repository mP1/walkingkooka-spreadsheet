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

package walkingkooka.spreadsheet.meta;

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.naming.Name;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.export.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.SpreadsheetExporterName;
import walkingkooka.spreadsheet.export.SpreadsheetExporterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.HasSpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterName;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.FontFamily;
import walkingkooka.tree.text.FontSize;
import walkingkooka.tree.text.TextStyle;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The {@link Name} of metadata property.
 */
public abstract class SpreadsheetMetadataPropertyName<T> implements Name,
        Comparable<SpreadsheetMetadataPropertyName<?>>,
        HasSpreadsheetPatternKind,
        HasUrlFragment {

    // constants

    private static final CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    /**
     * A read only cache of already prepared {@link SpreadsheetMetadataPropertyName names}..
     */
    static final Map<String, SpreadsheetMetadataPropertyName<?>> CONSTANTS = Maps.sorted(SpreadsheetMetadataPropertyName.CASE_SENSITIVITY.comparator());

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName}.
     */
    private static <T> SpreadsheetMetadataPropertyName<T> registerConstant(final SpreadsheetMetadataPropertyName<T> constant) {
        SpreadsheetMetadataPropertyName.CONSTANTS.put(constant.name, constant);
        return constant;
    }

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>width {@link Integer}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> CELL_CHARACTER_WIDTH = registerConstant(SpreadsheetMetadataPropertyNameIntegerCellCharacterWidth.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>width {@link SpreadsheetExporterAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetExporterAliasSet> CLIPBOARD_EXPORTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetExporterAliasSetClipboard.instance());
    
    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>width {@link SpreadsheetImporterAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetImporterAliasSet> CLIPBOARD_IMPORTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetImporterAliasSetClipboard.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetComparatorAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetComparatorAliasSet> COMPARATORS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetComparatorAliasSetComparators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ConverterAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<ConverterAliasSet> CONVERTERS = registerConstant(SpreadsheetMetadataPropertyNameConverterAliasSetConverters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creator {@link EmailAddress}</code>
     */
    public static final SpreadsheetMetadataPropertyName<EmailAddress> CREATOR = registerConstant(SpreadsheetMetadataPropertyNameEmailAddressCreator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creation {@link LocalDateTime}</code>
     */
    public static final SpreadsheetMetadataPropertyName<LocalDateTime> CREATE_DATE_TIME = registerConstant(SpreadsheetMetadataPropertyNameLocalDateTimeCreateDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>currency {@link String}</code>
     */
    public static final SpreadsheetMetadataPropertyName<String> CURRENCY_SYMBOL = registerConstant(SpreadsheetMetadataPropertyNameStringCurrencySymbol.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the default {@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector} for {@link java.time.LocalDate} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> DATE_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-parser {@link String}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> DATE_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserDate.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-offset {@link Long}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Long> DATETIME_OFFSET = registerConstant(SpreadsheetMetadataPropertyNameDateTimeOffset.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the default {@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector} for {@link LocalDateTime} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> DATE_TIME_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-parser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> DATE_TIME_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimal-separator {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> DECIMAL_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameCharacterDecimalSeparator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the {@link Integer} <code>default-year</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> DEFAULT_YEAR = registerConstant(SpreadsheetMetadataPropertyNameIntegerDefaultYear.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>exponent-symbol {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<String> EXPONENT_SYMBOL = registerConstant(SpreadsheetMetadataPropertyNameStringExponentSymbol.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetExporterAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetExporterAliasSet> EXPORTERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetExporterAliasSetExporters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>expression-number-kind {@link walkingkooka.tree.expression.ExpressionNumberKind}</code>
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionNumberKind> EXPRESSION_NUMBER_KIND = registerConstant(SpreadsheetMetadataPropertyNameExpressionNumberKind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ConverterSelector}</code> which will be used to convert values within a find expression.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> FIND_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorFind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ExpressionFunctionAliasSet}</code> which will be used to pick available functions within find expressions.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FIND_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ConverterSelector}</code> which will be used to convert values during a formatting of values.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> FORMAT_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorFormat.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetFormatterAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterAliasSet> FORMATTERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterAliasSetFormatters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ConverterSelector}</code> which will be used to convert values within an {@link SpreadsheetCell#formula()} expression.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> FORMULA_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorFormula.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ExpressionFunctionAliasSet}</code> which will be used to pick available functions within {@link SpreadsheetCell#formula()}.
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FORMULA_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFormula.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>frozen-columns {@link SpreadsheetColumnRangeReference}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetColumnRangeReference> FROZEN_COLUMNS = registerConstant(SpreadsheetMetadataPropertyNameFrozenColumns.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>frozen-rows {@link .SpreadsheetRow}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetRowRangeReference> FROZEN_ROWS = registerConstant(SpreadsheetMetadataPropertyNameFrozenRows.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ExpressionFunctionInfoSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNameExpressionFunctionAliasSetFunctions.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>general-number-format-digit-count {@link Integer}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> GENERAL_NUMBER_FORMAT_DIGIT_COUNT = registerConstant(SpreadsheetMetadataPropertyNameIntegerGeneralNumberFormatDigitCount.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>group-separator {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> GROUP_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameCharacterGroupSeparator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>group-separator {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> HIDE_ZERO_VALUES = registerConstant(SpreadsheetMetadataPropertyNameBooleanHideZeroValues.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetImporterAliasSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetImporterAliasSet> IMPORTERS = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetImporterAliasSetImporters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link Locale}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Locale> LOCALE = registerConstant(SpreadsheetMetadataPropertyNameLocale.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>last modified by {@link EmailAddress}</code>
     */
    public static final SpreadsheetMetadataPropertyName<EmailAddress> MODIFIED_BY = registerConstant(SpreadsheetMetadataPropertyNameEmailAddressModifiedBy.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>modified {@link LocalDateTime}</code>
     */
    public static final SpreadsheetMetadataPropertyName<LocalDateTime> MODIFIED_DATE_TIME = registerConstant(SpreadsheetMetadataPropertyNameLocalDateTimeModifiedDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>negative-sign {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> NEGATIVE_SIGN = registerConstant(SpreadsheetMetadataPropertyNameCharacterNegativeSign.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector}</code> for {@link ExpressionNumber} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> NUMBER_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>number-parser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> NUMBER_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserNumber.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetParserInfoSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserInfoSet> PARSERS = registerConstant(SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>percentage-symbol {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> PERCENTAGE_SYMBOL = registerConstant(SpreadsheetMetadataPropertyNameCharacterPercentageSymbol.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>positive-sign {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> POSITIVE_SIGN = registerConstant(SpreadsheetMetadataPropertyNameCharacterPositiveSign.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>precision {@link Integer}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> PRECISION = registerConstant(SpreadsheetMetadataPropertyNameIntegerPrecision.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>rounding-mode {@link RoundingMode}</code>
     */
    public static final SpreadsheetMetadataPropertyName<RoundingMode> ROUNDING_MODE = registerConstant(SpreadsheetMetadataPropertyNameRoundingMode.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the current <code>sort comparators</code> {@link SpreadsheetViewport}.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetComparatorNameList> SORT_COMPARATORS = registerConstant(SpreadsheetMetadataPropertyNameSortComparators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link ConverterSelector}</code> which will be used to convert values during a sort.
     */
    public static final SpreadsheetMetadataPropertyName<ConverterSelector> SORT_CONVERTER = registerConstant(SpreadsheetMetadataPropertyNameConverterSelectorSort.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-id {@link SpreadsheetId}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetId> SPREADSHEET_ID = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetId.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-name {@link SpreadsheetName}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetName> SPREADSHEET_NAME = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetName.instance());
    
    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>style {@link walkingkooka.tree.text.TextStyle}</code>
     */
    public static final SpreadsheetMetadataPropertyName<TextStyle> STYLE = registerConstant(SpreadsheetMetadataPropertyNameStyle.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector}</code> for {@link String} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> TEXT_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorText.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector}</code> for {@link LocalTime} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> TIME_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>time-parser</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> TIME_PARSER = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParserTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>two-digit-year {@link SpreadsheetFormatPattern}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> TWO_DIGIT_YEAR = registerConstant(SpreadsheetMetadataPropertyNameIntegerTwoDigitYear.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>value-separator</code> {@link Character}
     */
    public static final SpreadsheetMetadataPropertyName<Character> VALUE_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameCharacterValueSeparator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the current <code>viewport</code> {@link SpreadsheetViewport}.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetViewport> VIEWPORT = registerConstant(SpreadsheetMetadataPropertyNameViewport.instance());

    /**
     * Factory that assumes a valid {@link SpreadsheetMetadataPropertyName} or fails.
     */
    public static SpreadsheetMetadataPropertyName<?> with(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        SpreadsheetMetadataPropertyName<?> propertyName = CONSTANTS.get(name);
        if (null == propertyName) {
            if (false == name.startsWith(COLOR_PREFIX) || name.length() == COLOR_PREFIX.length()) {
                throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(name));
            }

            final String after = name.substring(COLOR_PREFIX.length());

            // name dash color is a numbered color, named dash letter is a named color
            try {
                if (Character.isLetter(after.charAt(0))) {
                    propertyName = namedColor(SpreadsheetColorName.with(after));
                } else {
                    propertyName = numberedColor(Integer.parseInt(after));
                }
            } catch (final RuntimeException cause) {
                throw new IllegalArgumentException("Invalid metadata property name " + CharSequences.quoteAndEscape(name), cause);
            }
        }
        return propertyName;
    }

    static final String COLOR_PREFIX = "color-";

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a {@link SpreadsheetColorName named}.
     */
    public static SpreadsheetMetadataPropertyName<Integer> namedColor(final SpreadsheetColorName name) {
        return SpreadsheetMetadataPropertyNameIntegerNamedColor.withColorName(name);
    }

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a numbered {@link Color}.
     */
    public static SpreadsheetMetadataPropertyName<Color> numberedColor(final int number) {
        return SpreadsheetMetadataPropertyNameNumberedColor.withNumber(number);
    }

    /**
     * Calls to this constructor will compute the {@link #name} parse the {@link Class#getSimpleName}
     */
    SpreadsheetMetadataPropertyName() {
        this(null);
    }

    /**
     * Package private constructor use factory.
     */
    SpreadsheetMetadataPropertyName(final String name) {
        super();

        final String finalName = null == name ?
                CaseKind.CAMEL.change(
                        this.getClass()
                                .getSimpleName()
                                .substring(SpreadsheetMetadataPropertyName.class.getSimpleName().length())
                                .replace("Spreadsheet", ""), // handles sub-classes like SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorText
                        CaseKind.KEBAB
                ) :
                name;
        this.name = finalName;

        this.jsonPropertyName = JsonPropertyName.with(finalName);

        this.patchRemove = JsonNode.object()
                .set(
                        this.jsonPropertyName,
                        JsonNode.nullNode()
                );

        this.urlFragment = UrlFragment.parse(finalName);
    }

    /**
     * Setting a {@link Character} property that is a duplicate value of another {@link Character} should result
     * in the duplicate value being replaced with the value of the property being set.<br>
     * <pre>
     * BEFORE
     * decimal=dot
     * group=comma
     * SET
     * decimal=comma
     * AFTER
     * decimal=comma
     * group=dot
     * </pre>
     * Because group held the new value, it actually gains the old value of decimal, aka values were swapped.
     * Note that grouping and value separator may have the same value and not be considered duplicates.
     */
    final boolean swapIfDuplicateValue() {
        return this instanceof SpreadsheetMetadataPropertyNameCharacter;
    }

    final boolean isGroupSeparatorOrValueSeparator() {
        return this instanceof SpreadsheetMetadataPropertyNameCharacterGroupSeparator || this instanceof SpreadsheetMetadataPropertyNameCharacterValueSeparator;
    }

    @Override
    public final String value() {
        return this.name;
    }

    final String name;

    final JsonPropertyName jsonPropertyName;

    /**
     * Validates the value, returning the value that will be saved.
     */
    @SuppressWarnings("UnusedReturnValue")
    public final T checkValue(final Object value) {
        if (null == value) {
            throw new SpreadsheetMetadataPropertyValueException("Missing value", this, value);
        }

        return this.checkValue0(value);
    }

    abstract T checkValue0(final Object value);

    /**
     * Checks the type of the given value and throws a {@link SpreadsheetMetadataPropertyValueException} if this test fails.
     */
    final T checkValueType(final Object value,
                           final Predicate<Object> typeChecker) {
        if (!typeChecker.test(value)) {
            throw this.spreadsheetMetadataPropertyValueException(value);
        }
        return Cast.to(value);
    }

    /**
     * Creates a {@link SpreadsheetMetadataPropertyValueException} used to report an invalid value.
     */
    final SpreadsheetMetadataPropertyValueException spreadsheetMetadataPropertyValueException(final Object value) {
        return new SpreadsheetMetadataPropertyValueException("Expected " + this.expected(),
                this,
                value);
    }

    abstract Class<T> type();

    /**
     * Provides the specific text about an invalid value for {@link #spreadsheetMetadataPropertyValueException(Object)}.
     */
    abstract String expected();

    /**
     * Defaults must not include a spreadsheet-id, email address or timestamp.
     */
    final boolean isNotDefaultProperty() {
        return this instanceof SpreadsheetMetadataPropertyNameEmailAddress ||
                this instanceof SpreadsheetMetadataPropertyNameLocalDateTime ||
                this instanceof SpreadsheetMetadataPropertyNameSpreadsheetId;
    }

    // loadFromLocale...................................................................................................

    /**
     * Some properties support providing a value for the given Locale for the parent {@link SpreadsheetMetadata} to be updated.
     */
    abstract Optional<T> extractLocaleAwareValue(final Locale locale);

    // SpreadsheetMetadataVisitor.......................................................................................

    /**
     * Dispatches to the appropriate {@link SpreadsheetMetadataVisitor} visit method.
     */
    abstract void accept(final T value, final SpreadsheetMetadataVisitor visitor);

    // HasUrlFragment...................................................................................................

    @Override
    public final UrlFragment urlFragment() {
        return this.urlFragment;
    }

    private final UrlFragment urlFragment;


    // parseUrlFragmentSaveValue........................................................................................

    /**
     * This parse method is called with the encoded text from a {@link UrlFragment} representing a save operation of this
     * property. Not all properties support this operation, and will throw a {@link UnsupportedOperationException}.
     */
    public final T parseUrlFragmentSaveValue(final String value) {
        Objects.requireNonNull(value, value);

        return this.checkValue(
                this.parseUrlFragmentSaveValue0(value)
        );
    }

    abstract T parseUrlFragmentSaveValue0(final String value);

    /**
     * This common method should be called by sub-classes to indicate {@link #parseUrlFragmentSaveValue(String)} is not supported.
     */
    final T failParseUrlFragmentSaveValueUnsupported() {
        throw new UnsupportedOperationException("UrlFragment save value not supported for " + CharSequences.quoteAndEscape(this.value()));
    }

    // SpreadsheetCellStore.............................................................................................

    /**
     * Returns the appropriate {@link SpreadsheetCellStoreAction} for changes to this {@link SpreadsheetMetadataPropertyName}.
     */
    public final SpreadsheetCellStoreAction spreadsheetCellStoreAction() {
        final SpreadsheetCellStoreAction action;

        switch (this.value()) {
            // id
            case "spreadsheet-id":
            case "spreadsheet-name":
                action = SpreadsheetCellStoreAction.NONE;
                break;

            // authorship & timestamp
            case "create-date-time":
            case "creator":
            case "modified-date-time":
            case "modified-by":
                action = SpreadsheetCellStoreAction.NONE;
                break;
            // viewport
            case "frozen-columns":
            case "frozen-rows":
            case "selection":
            case "viewport-cell":
                action = SpreadsheetCellStoreAction.NONE;
                break;
            // number parsing characters.
            case "currency-symbol":
            case "decimal-separator":
            case "exponent-symbol":
            case "group-separator":
            case "negative-sign":
            case "percentage-symbol":
            case "positive-sign":
            case "value-separator":
                action = SpreadsheetCellStoreAction.PARSE_FORMULA;
                break;
            // parse-patterns
            case "date-parser":
            case "date-time-parser":
            case "number-parser":
            case "time-parser":
                action = SpreadsheetCellStoreAction.PARSE_FORMULA;
                break;
            default:
                // all other properties require a full evaluate and format of all cells.
                action = SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT;
                break;
        }

        return action;
    }

    // HasSpreadsheetPatternKind........................................................................................

    /**
     * The corresponding {@link SpreadsheetPatternKind} for this property. Only <code>formatter</code> and
     * <code>parser</code> properties will return a {@link SpreadsheetPatternKind}.
     */
    // time-parse-pattern -> TIME_PARSER
    @Override
    public final Optional<SpreadsheetPatternKind> patternKind() {
        SpreadsheetPatternKind kind;
        if (this instanceof SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector) {
            final SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector formatter = (SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector) this;
            kind = formatter.spreadsheetPatternKind;

        } else {
            if (this instanceof SpreadsheetMetadataPropertyNameSpreadsheetParser) {
                final SpreadsheetMetadataPropertyNameSpreadsheetParser parser = (SpreadsheetMetadataPropertyNameSpreadsheetParser) this;
                kind = parser.spreadsheetPatternKind;
            } else {
                kind = null;
            }
        }

        return Optional.ofNullable(kind);
    }

    // isXXX............................................................................................................

    /**
     * Returns true if this property contains a {@link SpreadsheetFormatterSelector}.
     */
    public final boolean isSpreadsheetFormatterSelector() {
        return this instanceof SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector;
    }

    /**
     * Returns true if this property contains a {@link SpreadsheetParserSelector}.
     */
    public final boolean isSpreadsheetParserSelector() {
        return this instanceof SpreadsheetMetadataPropertyNameSpreadsheetParser;
    }

    /**
     * Returns true if this property name is a plugin {@link walkingkooka.plugin.PluginInfoSetLike}.
     */
    public final boolean isPlugin() {
        return this instanceof SpreadsheetMetadataPropertyNamePlugin;
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.caseSensitivity().hash(this.name);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetMetadataPropertyName &&
                        this.equals0((SpreadsheetMetadataPropertyName<?>) other);
    }

    private boolean equals0(final SpreadsheetMetadataPropertyName<?> other) {
        return this.caseSensitivity().equals(this.name, other.name);
    }

    @Override
    public final String toString() {
        return this.value();
    }

    // HasCaseSensitivity...............................................................................................

    /**
     * Used during hashing and equality checks.
     */
    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetMetadataPropertyName<?> other) {
        return this.caseSensitivity()
                .comparator()
                .compare(
                        this.compareToValue(),
                        other.compareToValue()
                );
    }

    private String compareToValue() {
        String value = this.value();

        if (this instanceof SpreadsheetMetadataPropertyNameSpreadsheetId) {
            value = ""; // make ids sort first
        } else {
            if (this instanceof SpreadsheetMetadataPropertyNameNumberedColor) {
                final SpreadsheetMetadataPropertyNameNumberedColor numberedColor = (SpreadsheetMetadataPropertyNameNumberedColor) this;
                value = numberedColor.compareToValue;
            }
        }

        return value;
    }

    // JsonNode.........................................................................................................

    /**
     * Creates a {@link JsonNode} which may be used to patch a {@link SpreadsheetMetadata}.
     */
    public final JsonNode patch(final T value) {
        return null == value ?
                this.patchRemove :
                SpreadsheetMetadata.EMPTY.set(this, value)
                        .marshall(JsonNodeMarshallContexts.basic());
    }

    /**
     * Cached {@link JsonNode}
     */
    private final JsonNode patchRemove;

    /**
     * Factory that retrieves a {@link SpreadsheetMetadataPropertyName} parse a {@link JsonNode#name()}.
     */
    static SpreadsheetMetadataPropertyName<?> unmarshallName(final JsonNode node) {
        return with(node.name().value());
    }

    /*
     * Force class initialization of the following types which will ensure they also {@link walkingkooka.tree.json.marshall.JsonNodeContext#register(String, BiFunction, BiFunction, Class, Class[])}
     */
    static {
        Color.BLACK.alpha();
        ConverterSelector.parse("Dummy");
        EmailAddress.tryParse("user@example.com");
        ExpressionNumberKind.DEFAULT.name();
        ExpressionFunctionAliasSet.parse("hello");
        FontFamily.with("MS Sans Serif");
        FontSize.with(1);
        SpreadsheetComparatorNameList.parse(
                SpreadsheetComparatorName.TEXT.toString()
        );
        ConverterSelector.with(
                ConverterName.NEVER,
                ""
        );
        SpreadsheetExporterAliasSet.parse("json");
        SpreadsheetExporterSelector.with(
                SpreadsheetExporterName.EMPTY,
                ""
        );
        SpreadsheetFormatterSelector.with(
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN,
                "dd/mm/yyyy"
        );
        SpreadsheetImporterAliasSet.parse("json");
        SpreadsheetImporterSelector.with(
                SpreadsheetImporterName.EMPTY,
                ""
        );
        SpreadsheetParserSelector.with(
                SpreadsheetParserName.DATE_PARSER_PATTERN,
                "dd/mm/yyyy"
        );
        //noinspection ResultOfMethodCallIgnored
        SpreadsheetId.with(0);
        SpreadsheetName.with("Untitled");
        SpreadsheetViewport.NO_NAVIGATION.isEmpty();
    }
}
