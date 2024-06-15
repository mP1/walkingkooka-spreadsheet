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
import walkingkooka.naming.Name;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetParserName;
import walkingkooka.spreadsheet.format.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.format.pattern.HasSpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
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
    public static final SpreadsheetMetadataPropertyName<Integer> CELL_CHARACTER_WIDTH = registerConstant(SpreadsheetMetadataPropertyNameCellCharacterWidth.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creator {@link EmailAddress}</code>
     */
    public static final SpreadsheetMetadataPropertyName<EmailAddress> CREATOR = registerConstant(SpreadsheetMetadataPropertyNameCreator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creation {@link LocalDateTime}</code>
     */
    public static final SpreadsheetMetadataPropertyName<LocalDateTime> CREATE_DATE_TIME = registerConstant(SpreadsheetMetadataPropertyNameCreateDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>currency {@link String}</code>
     */
    public static final SpreadsheetMetadataPropertyName<String> CURRENCY_SYMBOL = registerConstant(SpreadsheetMetadataPropertyNameCurrencySymbol.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the default {@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector} for {@link java.time.LocalDate} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> DATE_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameFormatterDate.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-parse-pattern {@link String}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetDateParsePattern> DATE_PARSE_PATTERN = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-offset {@link Long}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Long> DATETIME_OFFSET = registerConstant(SpreadsheetMetadataPropertyNameDateTimeOffset.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the default {@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector} for {@link LocalDateTime} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> DATE_TIME_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameFormatterDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-parse-pattern</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetDateTimeParsePattern> DATETIME_PARSE_PATTERN = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimal-separator {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> DECIMAL_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameDecimalSeparator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the {@link Integer} <code>default-year</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> DEFAULT_YEAR = registerConstant(SpreadsheetMetadataPropertyNameDefaultYear.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>exponent-symbol {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<String> EXPONENT_SYMBOL = registerConstant(SpreadsheetMetadataPropertyNameExponentSymbol.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetFormatterInfoSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionFunctionInfoSet> EXPRESSION_FUNCTIONS = registerConstant(SpreadsheetMetadataPropertyNamePluginExpressionFunctions.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>expression-number-kind {@link walkingkooka.tree.expression.ExpressionNumberKind}</code>
     */
    public static final SpreadsheetMetadataPropertyName<ExpressionNumberKind> EXPRESSION_NUMBER_KIND = registerConstant(SpreadsheetMetadataPropertyNameExpressionNumberKind.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>frozen-columns {@link SpreadsheetColumnRangeReference}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetColumnRangeReference> FROZEN_COLUMNS = registerConstant(SpreadsheetMetadataPropertyNameFrozenColumns.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>frozen-rows {@link .SpreadsheetRow}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetRowRangeReference> FROZEN_ROWS = registerConstant(SpreadsheetMetadataPropertyNameFrozenRows.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>general-number-format-digit-count {@link Integer}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> GENERAL_NUMBER_FORMAT_DIGIT_COUNT = registerConstant(SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>group-separator {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> GROUP_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameGroupSeparator.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>group-separator {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Boolean> HIDE_ZERO_VALUES = registerConstant(SpreadsheetMetadataPropertyNameHideZeroValues.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link Locale}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Locale> LOCALE = registerConstant(SpreadsheetMetadataPropertyNameLocale.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>last modified by {@link EmailAddress}</code>
     */
    public static final SpreadsheetMetadataPropertyName<EmailAddress> MODIFIED_BY = registerConstant(SpreadsheetMetadataPropertyNameModifiedBy.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>modified {@link LocalDateTime}</code>
     */
    public static final SpreadsheetMetadataPropertyName<LocalDateTime> MODIFIED_DATE_TIME = registerConstant(SpreadsheetMetadataPropertyNameModifiedDateTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>negative-sign {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> NEGATIVE_SIGN = registerConstant(SpreadsheetMetadataPropertyNameNegativeSign.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector}</code> for {@link ExpressionNumber} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> NUMBER_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameFormatterNumber.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>number-parse-pattern</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetNumberParsePattern> NUMBER_PARSE_PATTERN = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParsePatternNumber.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>percentage-symbol {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> PERCENTAGE_SYMBOL = registerConstant(SpreadsheetMetadataPropertyNamePercentageSymbol.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>positive-sign {@link Character}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Character> POSITIVE_SIGN = registerConstant(SpreadsheetMetadataPropertyNamePositiveSign.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>precision {@link Integer}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> PRECISION = registerConstant(SpreadsheetMetadataPropertyNamePrecision.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>rounding-mode {@link RoundingMode}</code>
     */
    public static final SpreadsheetMetadataPropertyName<RoundingMode> ROUNDING_MODE = registerConstant(SpreadsheetMetadataPropertyNameRoundingMode.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetComparatorInfoSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetComparatorInfoSet> SPREADSHEET_COMPARATORS = registerConstant(SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetFormatterInfoSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterInfoSet> SPREADSHEET_FORMATTERS = registerConstant(SpreadsheetMetadataPropertyNamePluginSpreadsheetFormatters.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-id {@link SpreadsheetId}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetId> SPREADSHEET_ID = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetId.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-name {@link SpreadsheetName}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetName> SPREADSHEET_NAME = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetName.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link SpreadsheetParserInfoSet}</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetParserInfoSet> SPREADSHEET_PARSERS = registerConstant(SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers.instance());
    
    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>style {@link walkingkooka.tree.text.TextStyle}</code>
     */
    public static final SpreadsheetMetadataPropertyName<TextStyle> STYLE = registerConstant(SpreadsheetMetadataPropertyNameStyle.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector}</code> for {@link String} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> TEXT_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameFormatterText.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector}</code> for {@link LocalTime} values.
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> TIME_FORMATTER = registerConstant(SpreadsheetMetadataPropertyNameFormatterTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>time-parse-pattern</code>
     */
    public static final SpreadsheetMetadataPropertyName<SpreadsheetTimeParsePattern> TIME_PARSE_PATTERN = registerConstant(SpreadsheetMetadataPropertyNameSpreadsheetParsePatternTime.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>two-digit-year {@link SpreadsheetFormatPattern}</code>
     */
    public static final SpreadsheetMetadataPropertyName<Integer> TWO_DIGIT_YEAR = registerConstant(SpreadsheetMetadataPropertyNameTwoDigitYear.instance());

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>value-separator</code> {@link Character}
     */
    public static final SpreadsheetMetadataPropertyName<Character> VALUE_SEPARATOR = registerConstant(SpreadsheetMetadataPropertyNameValueSeparator.instance());

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
        return SpreadsheetMetadataPropertyNameNamedColor.withColorName(name);
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
                                .replace("Spreadsheet", ""), // handles sub-classes like SpreadsheetMetadataPropertyNameFormatterText
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
        return this instanceof SpreadsheetMetadataPropertyNameGroupSeparator || this instanceof SpreadsheetMetadataPropertyNameValueSeparator;
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
    public T checkValue(final Object value) {
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

    /**
     * Not all values may be represented by a {@link UrlFragment} which indicates that a property may have its value
     * updated.
     */
    public abstract boolean isParseUrlFragmentSaveValueSupported();

    /**
     * This parse method is called with the encoded text from a {@link UrlFragment} representing a save operation of this
     * property. Not all properties support this operation, the {@link #isParseUrlFragmentSaveValueSupported()} provides
     * the answer.
     */
    public final T parseUrlFragmentSaveValue(final String value) {
        Objects.requireNonNull(value, value);

        return this.checkValue(
                this.parseUrlFragmentSaveValue0(value)
        );
    }

    abstract T parseUrlFragmentSaveValue0(final String value);

    /**
     * This common method should be called by sub-classes where {@link #isParseUrlFragmentSaveValueSupported()} returns false.
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
            case "date-parse-pattern":
            case "date-time-parse-pattern":
            case "number-parse-pattern":
            case "time-parse-pattern":
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
     * The corresponding {@link SpreadsheetPatternKind} for this property.
     */
    // time-parse-pattern -> TIME_PARSE_PATTERN
    @Override
    public final Optional<SpreadsheetPatternKind> patternKind() {
        final String name = this.value();

        SpreadsheetPatternKind kind;
        if (this instanceof SpreadsheetMetadataPropertyNameFormatter) {
            final SpreadsheetMetadataPropertyNameFormatter formatter = (SpreadsheetMetadataPropertyNameFormatter) this;
            kind = formatter.spreadsheetPatternKind;

        } else {
            if (name.endsWith("parse-pattern")) {
                kind = SpreadsheetPatternKind.valueOf(
                        CaseKind.KEBAB.change(
                                name,
                                CaseKind.SNAKE
                        )
                );
            } else {
                kind = null;
            }
        }

        return Optional.ofNullable(kind);
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
        return this.caseSensitivity().comparator().compare(this.compareToName(), other.compareToName());
    }

    abstract String compareToName();


    abstract Class<T> type();

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
        EmailAddress.tryParse("user@example.com");
        ExpressionNumberKind.DEFAULT.name();
        FontFamily.with("MS Sans Serif");
        FontSize.with(1);
        SpreadsheetFormatterSelector.with(
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN,
                "dd/mm/yyyy"
        );
        SpreadsheetParserSelector.with(
                SpreadsheetParserName.DATE_PARSE_PATTERN,
                "dd/mm/yyyy"
        );
        //noinspection ResultOfMethodCallIgnored
        SpreadsheetId.with(0);
        SpreadsheetName.with("Untitled");
        SpreadsheetViewport.NO_NAVIGATION.isEmpty();
    }
}
