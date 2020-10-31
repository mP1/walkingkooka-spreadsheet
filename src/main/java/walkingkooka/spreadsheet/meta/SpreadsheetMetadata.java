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
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.HasConverter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.HasDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.HasDecimalNumberContext;
import walkingkooka.math.HasMathContext;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.HasSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.HasSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.HasExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetMetadata} holds a {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 */
public abstract class SpreadsheetMetadata implements HasConverter<ExpressionNumberConverterContext>,
        HasDateTimeContext,
        HasDecimalNumberContext,
        HasExpressionNumberKind,
        HasMathContext,
        HasSpreadsheetFormatter,
        HasSpreadsheetFormatterContext,
        HateosResource<SpreadsheetId>,
        Value<Map<SpreadsheetMetadataPropertyName<?>, Object>> {

    /**
     * A {@link SpreadsheetMetadata} with no textStyle.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public final static SpreadsheetMetadata EMPTY = SpreadsheetMetadataEmpty.instance();

    /**
     * Factory that creates a {@link SpreadsheetMetadata} from a {@link Map}.
     */
    public static SpreadsheetMetadata with(final Map<SpreadsheetMetadataPropertyName<?>, Object> value) {
        return withSpreadsheetMetadataMap(SpreadsheetMetadataNonEmptyMap.with(value));
    }

    private static SpreadsheetMetadata withSpreadsheetMetadataMap(final SpreadsheetMetadataNonEmptyMap map) {
        return map.isEmpty() ?
                EMPTY :
                SpreadsheetMetadataNonEmpty.with(map);
    }

    /**
     * Private ctor to limit sub classes.
     */
    SpreadsheetMetadata(final SpreadsheetMetadata defaults) {
        super();
        this.defaults = defaults;
    }

    /**
     * Returns true if the {@link SpreadsheetMetadata} is empty.
     */
    public final boolean isEmpty() {
        return this instanceof SpreadsheetMetadataEmpty;
    }

    /**
     * Returns the {@link SpreadsheetId} or throws a {@link IllegalStateException} if missing.
     */
    public Optional<SpreadsheetId> id() {
        return this.get(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
    }

    @Override
    public String hateosLinkId() {
        return this.id()
                .orElseThrow(() -> new IllegalStateException("Missing " + SpreadsheetMetadataPropertyName.SPREADSHEET_ID + "=" + this))
                .hateosLinkId();
    }

    // get..............................................................................................................

    /**
     * Sets a possibly new property returning a {@link SpreadsheetMetadata} with the new definition which may or may not
     * require creating a new {@link SpreadsheetMetadata}.
     */
    public final <V> Optional<V> get(final SpreadsheetMetadataPropertyName<V> propertyName) {
        checkPropertyName(propertyName);

        return this.get0(propertyName);
    }

    /**
     * Potentially recursive fetch to find a property, trying locally and then the defaults if one is present.
     */
    private <V> Optional<V> get0(final SpreadsheetMetadataPropertyName<V> propertyName) {
        Optional<V> value = this.get1(propertyName);
        if (false == value.isPresent()) {
            // try again with defaults
            final SpreadsheetMetadata defaults = this.defaults;
            if (null != defaults) {
                value = defaults.get0(propertyName);
            }
        }
        return value;
    }

    /**
     * Sub classes will fetch the property returning the value.
     */
    abstract <V> Optional<V> get1(final SpreadsheetMetadataPropertyName<V> propertyName);

    /**
     * Fetches the required property or throws a {@link SpreadsheetMetadataPropertyValueException}.
     */
    public final <V> V getOrFail(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return this.get(propertyName)
                .orElseThrow(() -> new SpreadsheetMetadataPropertyValueException("Required property missing", propertyName, null));
    }

    // set..............................................................................................................

    /**
     * Sets a possibly new property returning a {@link SpreadsheetMetadata} with the new definition which may or may not
     * require creating a new {@link SpreadsheetMetadata}.
     */
    public final <V> SpreadsheetMetadata set(final SpreadsheetMetadataPropertyName<V> propertyName, final V value) {
        checkPropertyName(propertyName);

        propertyName.checkValue(value);
        return this.set0(propertyName, value);
    }

    abstract <V> SpreadsheetMetadata set0(final SpreadsheetMetadataPropertyName<V> propertyName, final V value);

    // remove...........................................................................................................

    /**
     * Removes a possibly existing property returning a {@link SpreadsheetMetadata} without.
     */
    public final SpreadsheetMetadata remove(final SpreadsheetMetadataPropertyName<?> propertyName) {
        checkPropertyName(propertyName);

        return this.remove0(propertyName);
    }

    abstract SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName);

    private static void checkPropertyName(final SpreadsheetMetadataPropertyName<?> propertyName) {
        Objects.requireNonNull(propertyName, "propertyName");
    }

    // HasExpressionNumberKind...........................................................................................

    public final ExpressionNumberKind expressionNumberKind() {
        return this.getOrFail(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);
    }

    // Function<SpreadsheetColorName, Optional<Color>>..................................................................

    /**
     * Returns a {@link Function} that returns a {@link Color} given its {@link SpreadsheetColorName}
     */
    public abstract Function<SpreadsheetColorName, Optional<Color>> nameToColor();

    /**
     * Lazy factory.
     */
    final Function<SpreadsheetColorName, Optional<Color>> nameToColor0() {
        return SpreadsheetMetadataColorFunction.with(SpreadsheetMetadataNameToColorSpreadsheetMetadataVisitor.nameToColorMap(this));
    }

    // Function<Integer, Optional<Color>>................................................................................

    /**
     * The maximum number of colors.
     */
    public final static int MAX_NUMBER_COLOR = SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER;

    /**
     * Returns a {@link Function} that returns a {@link Color} given its number.
     */
    public abstract Function<Integer, Optional<Color>> numberToColor();

    /**
     * Lazy factory that creates a {@link Function}.
     */
    final Function<Integer, Optional<Color>> numberToColor0() {
        return SpreadsheetMetadataColorFunction.with(SpreadsheetMetadataNumberToColorSpreadsheetMetadataVisitor.numberToColorMap(this));
    }

    /**
     * Returns a {@link DecimalNumberContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#DATETIME_OFFSET}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_FORMAT_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_PARSE_PATTERNS}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATETIME_FORMAT_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATETIME_PARSE_PATTERNS}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NUMBER_FORMAT_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NUMBER_PARSE_PATTERNS}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TEXT_FORMAT_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_FORMAT_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_PARSE_PATTERNS}</li>
     * </ul>
     */
    @Override
    public abstract Converter<ExpressionNumberConverterContext> converter();

    /**
     * Lazy factory that creates a {@link Converter} using current properties.
     */
    public final Converter converter0() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetDateFormatPattern dateFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN);
        final SpreadsheetDateParsePatterns dateParser = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS);

        final SpreadsheetDateTimeFormatPattern dateTimeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN);
        final SpreadsheetDateTimeParsePatterns dateTimeParser = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS);

        final SpreadsheetNumberFormatPattern numberFormat = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN);
        final SpreadsheetNumberParsePatterns numberParser = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS);

        final SpreadsheetTextFormatPattern textFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN);

        final SpreadsheetTimeFormatPattern timeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN);
        final SpreadsheetTimeParsePatterns timeParser = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS);

        final Long dateOffset = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_OFFSET);

        components.reportIfMissing();

        return SpreadsheetConverters.converter(dateFormat.formatter(),
                dateParser,
                dateTimeFormat.formatter(),
                dateTimeParser,
                numberFormat.formatter(),
                numberParser,
                textFormat.formatter(),
                timeFormat.formatter(),
                timeParser,
                dateOffset);
    }

    /**
     * Returns a {@link ExpressionNumberConverterContext}
     */
    public abstract ExpressionNumberConverterContext converterContext();

    /**
     * Factory that creates a {@link ConverterContext}
     */
    final ExpressionNumberConverterContext converterContext0() {
        return ExpressionNumberConverterContexts.basic(ConverterContexts.basic(this.dateTimeContext(), this.decimalNumberContext()), this.expressionNumberKind());
    }

    // HasDateTimeContext...............................................................................................

    /**
     * Returns a {@link DateTimeContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#LOCALE}</li>
     * </ul>
     */
    @Override
    public abstract DateTimeContext dateTimeContext();

    final DateTimeContext dateTimeContext0() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Locale locale = components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);
        final Integer twoYearDigit = components.getOrNull(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR);

        components.reportIfMissing();

        return DateTimeContexts.locale(locale, twoYearDigit);
    }

    // HasDecimalNumberContext..........................................................................................

    /**
     * Returns a {@link DecimalNumberContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#CURRENCY_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DECIMAL_SEPARATOR}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#EXPONENT_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#GROUPING_SEPARATOR}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NEGATIVE_SIGN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#PERCENTAGE_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#POSITIVE_SIGN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#PRECISION}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#ROUNDING_MODE}</li>
     * </ul>
     * or
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#LOCALE} which may provide some defaults if some of the above properties are missing.</li>
     * </ul>
     */
    @Override
    public abstract DecimalNumberContext decimalNumberContext();

    final DecimalNumberContext decimalNumberContext0() {
        return SpreadsheetMetadataDecimalNumberContextComponents.with(this).decimalNumberContext();
    }

    // HasMathContext....................................................................................................

    /**
     * Returns a {@link MathContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#PRECISION}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#ROUNDING_MODE}</li>
     * </ul>
     */
    @Override
    public abstract MathContext mathContext();

    final MathContext mathContext0() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Integer precision = components.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        final RoundingMode roundingMode = components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        components.reportIfMissing();

        return new MathContext(precision, roundingMode);
    }

    // HasSpreadsheetFormatter..........................................................................................

    /**
     * Creates a {@link SpreadsheetFormatter} that combines the formatting of all patterns.
     */
    @Override
    public abstract SpreadsheetFormatter formatter();
    
    final SpreadsheetFormatter formatter0() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetDateFormatPattern dateFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN);
        final SpreadsheetDateTimeFormatPattern dateTimeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN);
        final SpreadsheetNumberFormatPattern numberFormat = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN);
        final SpreadsheetTextFormatPattern textFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN);
        final SpreadsheetTimeFormatPattern timeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN);

        components.reportIfMissing();

        return SpreadsheetFormatters.chain(Lists.of(
                dateTimeFormat.formatter(),
                dateFormat.formatter(),
                timeFormat.formatter(),
                numberFormat.formatter(),
                textFormat.formatter()));
    }

    // HasSpreadsheetFormatterContext...................................................................................

    /**
     * Creates a {@link SpreadsheetFormatterContext} using the given formatter as the default.
     */
    public abstract SpreadsheetFormatterContext formatterContext(final SpreadsheetFormatter defaultFormatter);

    /**
     * Factory that combines properties and the given default {@link SpreadsheetFormatter} returning a {@link SpreadsheetFormatterContext}.
     */
    final SpreadsheetFormatterContext formatterContext0(final SpreadsheetFormatter defaultFormatter) {
        return SpreadsheetFormatterContexts.basic(this.numberToColor(),
                this.nameToColor(),
                this.getOrFail(SpreadsheetMetadataPropertyName.WIDTH),
                this.converter(),
                defaultFormatter,
                this.converterContext());
    }

    // loadFromLocale...................................................................................................

    /**
     * Assumes a {@link Locale} has been set, failing if one is absent, and proceeds to set numerous properties with defaults,
     * note that existing values will be overwritten.
     * Date, DateTime and Time defaults are loaded from {@link java.text.DateFormat} using the provided locale, formats pick the FULL style,
     * while parse patterns will include all patterns with all styles.
     */
    public final SpreadsheetMetadata loadFromLocale() {
        final Locale locale = this.getOrFail(SpreadsheetMetadataPropertyName.LOCALE);

        SpreadsheetMetadata updated = this;

        for (final SpreadsheetMetadataPropertyName<?> propertyName : SpreadsheetMetadataPropertyName.CONSTANTS.values()) {
            final Optional<?> localeValue = propertyName.extractLocaleValue(locale);
            if (localeValue.isPresent()) {
                updated = updated.set(propertyName, Cast.to(localeValue.get()));
            }
        }

        return updated;
    }

    // setDefaults......................................................................................................

    /**
     * Sets a provider which will provide defaults when the value is not actually present in this instance.
     */
    public final SpreadsheetMetadata setDefaults(final SpreadsheetMetadata defaults) {
        Objects.requireNonNull(defaults, "defaults");

        if (EMPTY != defaults) {
            defaults.checkDefaultsValues();
            this.checkDefaults(this, defaults);
        }

        // if new defaults is EMPTY set its defaults instead, even if that is null
        return this.defaults().equals(defaults) ?
                this :
                this.replaceDefaults(EMPTY == defaults ? null : defaults.isEmpty() ? defaults.defaults : defaults);
    }

    /**
     * Checks that all property values are valid or general and not specific to a single spreadsheet.
     */
    abstract void checkDefaultsValues();

    /**
     * Only {@link SpreadsheetMetadataNonEmpty} will perform checks
     */
    private void checkDefaults(final SpreadsheetMetadata defaults,
                               final SpreadsheetMetadata replacement) {
        if (null != defaults) {
            if (defaults == replacement) {
                throw new IllegalArgumentException("New defaults includes cycle: " + this);
            }
            this.checkDefaults(defaults.defaults, defaults);
        }
    }

    /**
     * Factory that creates a new {@link SpreadsheetMetadata} sub class with the given defaults.
     */
    abstract SpreadsheetMetadata replaceDefaults(final SpreadsheetMetadata defaults);

    /**
     * Returns another {@link SpreadsheetMetadata} which will provide defaults.
     */
    public final SpreadsheetMetadata defaults() {
        final SpreadsheetMetadata defaults = this.defaults;
        return null != defaults ?
                defaults :
                EMPTY;
    }

    // @VisibleForTesting
    final SpreadsheetMetadata defaults;

    // SpreadsheetMetadataStyleVisitor..................................................................................

    abstract void accept(final SpreadsheetMetadataVisitor visitor);

    // Object...........................................................................................................

    @Override
    public abstract int hashCode();

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetMetadata other) {
        return this.equalsValues(other) && Objects.equals(this.defaults, other.defaults);
    }

    /**
     * Sub classes will test if ALL their values are equal
     */
    abstract boolean equalsValues(final SpreadsheetMetadata other);

    @Override
    public abstract String toString();

    // JsonNodeContext..................................................................................................

    /**
     * Marshalls the individual properties and values and defaults but not the cached constructed values.
     */
    final JsonNode marshall(final JsonNodeMarshallContext context) {
        final JsonObject object = this.marshallProperties(context);
        final SpreadsheetMetadata defaults = this.defaults;
        return null != defaults ?
                object.set(DEFAULTS, defaults.marshall(context)) :
                object;
    }

    /**
     * This property in the json form will hold the defaults. Note actual property names may not start with underscore.
     */
    // VisibleForTesting
    final static JsonPropertyName DEFAULTS = JsonPropertyName.with("_defaults");

    /**
     * Sub classes must marshall their properties but not the defaults.
     */
    abstract JsonObject marshallProperties(final JsonNodeMarshallContext context);

    static SpreadsheetMetadata unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();
        SpreadsheetMetadata defaults = EMPTY;

        for (final JsonNode child : node.objectOrFail().children()) {
            if (child.name().equals(DEFAULTS)) {
                defaults = context.unmarshall(child, SpreadsheetMetadata.class);
                continue;
            }

            final SpreadsheetMetadataPropertyName<?> name = SpreadsheetMetadataPropertyName.unmarshallName(child);
            properties.put(name, name.unmarshall(child, context));
        }

        return with(properties).setDefaults(defaults);
    }

    static {
        JsonNodeContext.register("metadata",
                SpreadsheetMetadata::unmarshall,
                SpreadsheetMetadata::marshall,
                SpreadsheetMetadata.class,
                SpreadsheetMetadataNonEmpty.class,
                SpreadsheetMetadataEmpty.class);
    }

    /**
     * A {@link SpreadsheetMetadata} loaded with defaults that are not {@link Locale} aware.
     */
    public final static SpreadsheetMetadata NON_LOCALE_DEFAULTS = nonLocaleDefaults();

    private static SpreadsheetMetadata nonLocaleDefaults() {
        EMPTY.id(); // force JsonNodeContext registering of collaborating types.
        return JsonNodeUnmarshallContexts.basic(ExpressionNumberContexts.fake())
                .unmarshall(JsonNode.parse(new SpreadsheetMetadataDefaultTextResourceProvider().text()), SpreadsheetMetadata.class);
    }
}
