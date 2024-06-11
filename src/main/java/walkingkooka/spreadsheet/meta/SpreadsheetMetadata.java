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

import walkingkooka.CanBeEmpty;
import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.HasLocale;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.HasDecimalNumberContext;
import walkingkooka.math.HasMathContext;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.HasParser;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.HasExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.Patchable;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link SpreadsheetMetadata} holds a {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 * <br>
 * This class represents the global settings for a single spreadsheet, capturing its ID, name, audit details
 * (creator, last modified by, timestamps), locale, computation settings (precisions, rounding etc),
 * global formatting patterns, and many other non cell focused values.
 * <br>
 * For more information examine the {@link SpreadsheetMetadataPropertyName} properties.
 * <br>
 * Cell specific data such as individual format patterns are not stored here but on the {@link walkingkooka.spreadsheet.SpreadsheetCell}.
 */
public abstract class SpreadsheetMetadata implements CanBeEmpty,
        HasDecimalNumberContext,
        HasExpressionNumberKind,
        HasLocale,
        HasMathContext,
        HasParser<SpreadsheetParserContext>,
        HateosResource<SpreadsheetId>,
        Patchable<SpreadsheetMetadata>,
        TreePrintable,
        Value<Map<SpreadsheetMetadataPropertyName<?>, Object>> {

    /**
     * A {@link SpreadsheetMetadata} with no textStyle.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final SpreadsheetMetadata EMPTY = SpreadsheetMetadataEmpty.instance();

    /**
     * Private ctor to limit sub-classes.
     */
    SpreadsheetMetadata(final SpreadsheetMetadata defaults) {
        super();
        this.defaults = defaults;
    }

    /**
     * Returns all the missing required properties. NOTE the defaults are also checked.
     */
    public final Set<SpreadsheetMetadataPropertyName<?>> missingRequiredProperties() {
        final Set<SpreadsheetMetadataPropertyName<?>> missing = Sets.sorted();

        addIfMissing(SpreadsheetMetadataPropertyName.CREATOR, missing);
        addIfMissing(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, missing);
        addIfMissing(SpreadsheetMetadataPropertyName.MODIFIED_BY, missing);
        addIfMissing(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, missing);

        addIfMissing(SpreadsheetMetadataPropertyName.LOCALE, missing);

        return Sets.readOnly(missing);
    }

    private void addIfMissing(final SpreadsheetMetadataPropertyName<?> property,
                              final Set<SpreadsheetMetadataPropertyName<?>> missing) {
        if (false == this.get(property).isPresent()) {
            missing.add(property);
        }
    }

    /**
     * Returns true if the {@link SpreadsheetMetadata} is empty.
     */
    @Override
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

    /**
     * Returns the {@link SpreadsheetName} if one is present.
     */
    public Optional<SpreadsheetName> name() {
        return this.get(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME);
    }

    // get..............................................................................................................

    /**
     * Sets a possibly new property returning a {@link SpreadsheetMetadata} with the new definition which may or may not
     * require creating a new {@link SpreadsheetMetadata}.
     */
    public final <V> Optional<V> get(final SpreadsheetMetadataPropertyName<V> propertyName) {
        checkPropertyName(propertyName);

        return this.getOrGetDefaults(propertyName);
    }

    /**
     * Potentially recursive fetch to find a property, trying locally and then the defaults if one is present.
     */
    final <V> Optional<V> getOrGetDefaults(final SpreadsheetMetadataPropertyName<V> propertyName) {
        Optional<V> value = this.getIgnoringDefaults0(propertyName);
        if (false == value.isPresent()) {
            // try again with defaults
            final SpreadsheetMetadata defaults = this.defaults;
            if (null != defaults) {
                value = defaults.getIgnoringDefaults0(propertyName); // defaults cannot have further defaults
            }
        }
        return value;
    }

    /**
     * sub-classes will fetch the property returning the value.
     */
    public final <V> Optional<V> getIgnoringDefaults(final SpreadsheetMetadataPropertyName<V> propertyName) {
        Objects.requireNonNull(propertyName, "propertyName");

        return this.getIgnoringDefaults0(propertyName);
    }

    abstract <V> Optional<V> getIgnoringDefaults0(final SpreadsheetMetadataPropertyName<V> propertyName);

    /**
     * Fetches the required property or throws a {@link SpreadsheetMetadataPropertyValueException}.
     */
    public final <V> V getOrFail(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return this.get(propertyName)
                .orElseThrow(() -> new SpreadsheetMetadataPropertyValueException("Required property missing", propertyName, null));
    }

    /**
     * Gets the effective style property, first checking the current {@link SpreadsheetMetadataPropertyName#STYLE}
     * and if absent parse there the defaults are then checked.
     */
    public final <V> Optional<V> getEffectiveStyleProperty(final TextStylePropertyName<V> property) {
        Objects.requireNonNull(property, "property");

        Optional<V> value = Optional.empty();

        do {
            Optional<TextStyle> style = this.get(SpreadsheetMetadataPropertyName.STYLE);
            if (style.isPresent()) {
                value = style.get().get(property);
                if (value.isPresent()) {
                    break;
                }
            }
            style = this.defaults()
                    .get(SpreadsheetMetadataPropertyName.STYLE);
            if (style.isPresent()) {
                value = style.get().get(property);
            }
        } while (false);

        return value;
    }

    /**
     * Fetches the requested {@link TextStylePropertyName} searching the current {@link SpreadsheetMetadataPropertyName#STYLE}
     * and if absent, the defaults. If it is absent parse both a {@link IllegalArgumentException} will then be thrown.
     */
    public final <V> V getEffectiveStylePropertyOrFail(final TextStylePropertyName<V> propertyName) {
        return this.getEffectiveStyleProperty(propertyName)
                .orElseThrow(() -> new IllegalArgumentException("Missing " + propertyName));
    }

    /**
     * Fetches the effective style with properties replaced by non defaults when they exist.
     */
    public final TextStyle effectiveStyle() {
        if (null == this.effectiveStyle) {
            final TextStyle style = this.getStyleOrEmpty();
            final TextStyle defaultStyle = this.defaults().getStyleOrEmpty();

            this.effectiveStyle = style.merge(defaultStyle);
        }

        return this.effectiveStyle;
    }

    private TextStyle effectiveStyle;

    private TextStyle getStyleOrEmpty() {
        return this.getIgnoringDefaults(SpreadsheetMetadataPropertyName.STYLE)
                .orElse(TextStyle.EMPTY);
    }

    // set..............................................................................................................

    /**
     * Sets a possibly new property returning a {@link SpreadsheetMetadata} with the new definition which may or may not
     * require creating a new {@link SpreadsheetMetadata}.
     */
    public final <V> SpreadsheetMetadata set(final SpreadsheetMetadataPropertyName<V> propertyName,
                                             final V value) {
        checkPropertyName(propertyName);
        return this.set0(
                propertyName,
                propertyName.checkValue(value) // necessary because absolute references values are made relative
        );
    }

    private <V> SpreadsheetMetadata set0(final SpreadsheetMetadataPropertyName<V> propertyName,
                                         final V value) {
        SpreadsheetMetadata result;

        if (value.equals(this.getIgnoringDefaults(propertyName).orElse(null))) {
            result = this.setSameValue(propertyName, value);
        } else {
            result = this.setDifferentValue(propertyName, value);
        }

        return result;
    }

    /**
     * Handle the special case where a property is being set with the same effective value,
     * which could be the current or default value. sub-classes need to test.
     */
    abstract <V> SpreadsheetMetadata setSameValue(final SpreadsheetMetadataPropertyName<V> propertyName,
                                                  final V value);

    /**
     * Handles the case where a value is different and if a character swaps might need to happen to avoid duplicates/clashes.
     */
    private <V> SpreadsheetMetadata setDifferentValue(final SpreadsheetMetadataPropertyName<V> propertyName,
                                                      final V value) {
        final Object previousValue = this.get(propertyName).orElse(null);

        // property is different or new
        final boolean swapIfDuplicateValue = propertyName.swapIfDuplicateValue();

        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(this.value());
        copy.put(propertyName, value);

        final boolean groupOrValue = propertyName.isGroupSeparatorOrValueSeparator();

        if (swapIfDuplicateValue) {
            for (final SpreadsheetMetadataPropertyName<Character> duplicate : SWAPPABLE_PROPERTIES) {
                if (propertyName.equals(duplicate)) {
                    continue;
                }
                final boolean duplicateIsGroupSeparatorOrValue = duplicate.isGroupSeparatorOrValueSeparator();
                if (groupOrValue && duplicateIsGroupSeparatorOrValue) {
                    continue;
                }

                final Character duplicateValue = this.get(duplicate).orElse(null);
                if (null != duplicateValue) {
                    if (value.equals(duplicateValue)) {
                        if (null == previousValue) {
                            if (!duplicateIsGroupSeparatorOrValue) {
                                reportDuplicateProperty(propertyName, value, duplicate);
                            }
                        } else {
                            copy.put(duplicate, previousValue);
                        }
                    }
                }
            }
        }

        // update and possibly swap of character properties
        return SpreadsheetMetadataNonEmpty.with(Maps.immutable(copy), this.defaults);
    }

    // @VisibleForTesting
    static final SpreadsheetMetadataPropertyName<Character>[] SWAPPABLE_PROPERTIES = new SpreadsheetMetadataPropertyName[]{
            SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
            SpreadsheetMetadataPropertyName.GROUP_SEPARATOR,
            SpreadsheetMetadataPropertyName.NEGATIVE_SIGN,
            SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
            SpreadsheetMetadataPropertyName.POSITIVE_SIGN,
            SpreadsheetMetadataPropertyName.VALUE_SEPARATOR
    };

    private static void reportDuplicateProperty(final SpreadsheetMetadataPropertyName<?> property,
                                                final Object value,
                                                final SpreadsheetMetadataPropertyName<?> original) {
        throw new IllegalArgumentException("Cannot set " + property + "=" + CharSequences.quoteIfChars(value) + " duplicate of " + original);
    }

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

    // setOrRemove......................................................................................................

    /**
     * Performs a set if the value is non null or removes the property when the value is null.
     */
    public final <V> SpreadsheetMetadata setOrRemove(final SpreadsheetMetadataPropertyName<V> propertyName,
                                                     final V value) {
        return null != value ?
                this.set(propertyName, value) :
                this.remove(propertyName);
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
     * Lazy factory that maps a {@link SpreadsheetColorName} to its eventual color number and then its {@link Color}.
     */
    final Function<SpreadsheetColorName, Optional<Color>> nameToColor0() {
        return SpreadsheetMetadataColorFunction.with(SpreadsheetMetadataNameToColorSpreadsheetMetadataVisitor.nameToColorMap(this));
    }

    // Function<Integer, Optional<Color>>................................................................................

    /**
     * Returns a {@link Function} that returns a {@link Color} given its number.
     */
    public abstract Function<Integer, Optional<Color>> numberToColor();

    /**
     * Lazy factory that creates a {@link Function} that maps an integer to a {@link Color}
     */
    final Function<Integer, Optional<Color>> numberToColor0() {
        return SpreadsheetMetadataColorFunction.with(SpreadsheetMetadataNumberToColorSpreadsheetMetadataVisitor.numberToColorMap(this));
    }

    // Function<Integer, Optional<SpreadsheetColorName>>................................................................

    /**
     * Returns a {@link Function} that returns a {@link SpreadsheetColorName} given its number.
     */
    public abstract Function<Integer, Optional<SpreadsheetColorName>> numberToColorName();

    /**
     * Lazy factory that creates a {@link Function} that maps an integer to a {@link SpreadsheetColorName}
     */
    final Function<Integer, Optional<SpreadsheetColorName>> numberToColorName0() {
        return SpreadsheetMetadataColorFunction.with(
                SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor.numberToColorNameMap(this)
        );
    }

    // Converter.......................................................................................................

    /**
     * Returns a {@link Converter} using the required properties.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#DATETIME_OFFSET}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_PARSE_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_TIME_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATETIME_PARSE_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NUMBER_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NUMBER_PARSE_PATTERN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TEXT_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_PARSE_PATTERN}</li>
     * </ul>
     */
    public final Converter<SpreadsheetConverterContext> converter(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetFormatterSelector dateFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMATTER);
        final SpreadsheetDateParsePattern dateParser = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERN);

        final SpreadsheetFormatterSelector dateTimeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);
        final SpreadsheetDateTimeParsePattern dateTimeParser = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN);

        final SpreadsheetFormatterSelector numberFormat = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER);
        final SpreadsheetNumberParsePattern numberParser = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERN);

        final SpreadsheetFormatterSelector textFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMATTER);

        final SpreadsheetFormatterSelector timeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMATTER);
        final SpreadsheetTimeParsePattern timeParser = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERN);

        final Long dateOffset = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_OFFSET);

        components.reportIfMissing();

        return SpreadsheetConverters.general(
                spreadsheetFormatterProvider.spreadsheetFormatterOrFail(dateFormat),
                dateParser,
                spreadsheetFormatterProvider.spreadsheetFormatterOrFail(dateTimeFormat),
                dateTimeParser,
                spreadsheetFormatterProvider.spreadsheetFormatterOrFail(numberFormat),
                numberParser,
                spreadsheetFormatterProvider.spreadsheetFormatterOrFail(textFormat),
                spreadsheetFormatterProvider.spreadsheetFormatterOrFail(timeFormat),
                timeParser,
                dateOffset
        );
    }

    /**
     * Returns a {@link ExpressionNumberConverterContext}
     */
    public final SpreadsheetConverterContext converterContext(final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                              final Supplier<LocalDateTime> now,
                                                              final SpreadsheetLabelNameResolver labelNameResolver) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");

        return SpreadsheetConverterContexts.basic(
                this.converter(spreadsheetFormatterProvider),
                labelNameResolver,
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                                Converters.fake(),
                                this.dateTimeContext(now),
                                this.decimalNumberContext()
                        ),
                        this.expressionNumberKind()
                )
        );
    }

    // HasDateTimeContext...............................................................................................

    /**
     * Returns a {@link DateTimeContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#LOCALE}</li>
     * </ul>
     */
    public final DateTimeContext dateTimeContext(final Supplier<LocalDateTime> now) {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Locale locale = components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);
        final Integer defaultYear = components.getOrNull(SpreadsheetMetadataPropertyName.DEFAULT_YEAR);
        final Integer twoYearDigit = components.getOrNull(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR);

        components.reportIfMissing();

        return DateTimeContexts.locale(
                locale,
                defaultYear,
                twoYearDigit,
                now
        );
    }

    static final List<SpreadsheetMetadataPropertyName<?>> DATE_TIME_CONTEXT_REQUIRED = Lists.of(
            SpreadsheetMetadataPropertyName.LOCALE,
            SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR
    );

    // HasDecimalNumberContext..........................................................................................

    /**
     * Returns a {@link DecimalNumberContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#CURRENCY_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DECIMAL_SEPARATOR}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#EXPONENT_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#GROUP_SEPARATOR}</li>
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

    // ExpressionNumberProvider.........................................................................................

    /**
     * Returns a {@link ExpressionFunctionProvider} that only contains the selected {@link walkingkooka.tree.expression.function.ExpressionFunction}
     * in {@link SpreadsheetMetadataPropertyName#EXPRESSION_FUNCTIONS}
     */
    public final ExpressionFunctionProvider expressionFunctionProvider(final ExpressionFunctionProvider provider) {
        Objects.requireNonNull(provider, "provider");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        components.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_FUNCTIONS);

        components.reportIfMissing();

        return ExpressionFunctionProviders.mapped(
                this.getOrFail(SpreadsheetMetadataPropertyName.EXPRESSION_FUNCTIONS),
                provider
        );
    }

    // HasExpressionNumberContext.......................................................................................

    /**
     * Returns a {@link ExpressionNumberContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#EXPRESSION_NUMBER_KIND}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#PRECISION}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#ROUNDING_MODE}</li>
     * </ul>
     */
    public abstract ExpressionNumberContext expressionNumberContext();

    final ExpressionNumberContext expressionNumberContext0() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        components.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);
        components.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        components.reportIfMissing();

        return ExpressionNumberContexts.basic(
                this.expressionNumberKind(),
                this.decimalNumberContext()
        );
    }

    // HasJsonNodeMarshallContext.......................................................................................

    /**
     * Returns a {@link JsonNodeMarshallContext}
     */
    public final JsonNodeMarshallContext jsonNodeMarshallContext() {
        return JsonNodeMarshallContexts.basic();
    }

    // HasJsonNodeUnmarshallContext......................................................................................

    public abstract JsonNodeUnmarshallContext jsonNodeUnmarshallContext();

    final JsonNodeUnmarshallContext jsonNodeUnmarshallContext0() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final ExpressionNumberKind expressionNumberKind = components.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);

        final Integer precision = components.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        final RoundingMode roundingMode = components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        components.reportIfMissing();

        return JsonNodeUnmarshallContexts.basic(
                expressionNumberKind,
                new MathContext(
                        precision,
                        roundingMode
                )
        );
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
     * Creates a {@link SpreadsheetFormatter} that creates a single formatter from all formatters.
     */
    public final SpreadsheetFormatter formatter(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetFormatterSelector date = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMATTER);
        final SpreadsheetFormatterSelector dateTime = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);
        final SpreadsheetFormatterSelector number = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER);
        final SpreadsheetFormatterSelector text = components.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMATTER);
        final SpreadsheetFormatterSelector time = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMATTER);

        components.reportIfMissing();

        return SpreadsheetFormatters.chain(
                Lists.of(
                        spreadsheetFormatterProvider.spreadsheetFormatterOrFail(date),
                        spreadsheetFormatterProvider.spreadsheetFormatterOrFail(dateTime),
                        spreadsheetFormatterProvider.spreadsheetFormatterOrFail(number),
                        spreadsheetFormatterProvider.spreadsheetFormatterOrFail(text),
                        spreadsheetFormatterProvider.spreadsheetFormatterOrFail(time)
                )
        );
    }

    // HasSpreadsheetFormatterContext...................................................................................

    /**
     * Creates a {@link SpreadsheetFormatterContext}.
     */
    public final SpreadsheetFormatterContext formatterContext(final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                              final Supplier<LocalDateTime> now,
                                                              final SpreadsheetLabelNameResolver labelNameResolver) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Integer characterWidth = components.getOrNull(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH);
        final Integer generalNumberFormatDigitCount = components.getOrNull(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT);

        components.reportIfMissing();

        return SpreadsheetFormatterContexts.basic(
                this.numberToColor(),
                this.nameToColor(),
                characterWidth,
                generalNumberFormatDigitCount,
                this.formatter(spreadsheetFormatterProvider),
                this.converterContext(
                        spreadsheetFormatterProvider,
                        now,
                        labelNameResolver
                )
        );
    }

    // HasParsers.......................................................................................................

    /**
     * Returns a {@link Parser} that can be used to parse formulas.
     */
    public abstract Parser<SpreadsheetParserContext> parser();

    /**
     * Creates a {@link Parser} that may be used to parse formulas after verifying required properties.
     */
    final Parser<SpreadsheetParserContext> createParser() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetDateParsePattern date = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERN);
        final SpreadsheetDateTimeParsePattern dateTime = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN);
        final SpreadsheetNumberParsePattern number = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERN);
        final SpreadsheetTimeParsePattern time = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERN);

        components.reportIfMissing();

        return SpreadsheetParsers.valueOrExpression(
                Parsers.alternatives(
                        Lists.of(
                                date.parser(),
                                dateTime.parser(),
                                number.parser().andEmptyTextCursor(),
                                time.parser()
                        )
                )
        );
    }

    // HasSpreadsheetParserContext......................................................................................

    /**
     * Returns a {@link SpreadsheetParserContext}.
     */
    public final SpreadsheetParserContext parserContext(final Supplier<LocalDateTime> now) {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        // DateTimeContext
        DATE_TIME_CONTEXT_REQUIRED.forEach(components::getOrNull);

        // DecimalNumberContext
        SpreadsheetMetadataDecimalNumberContextComponents.REQUIRED.forEach(components::getOrNull);

        // ExpressionNumberKind
        components.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);

        // valueSeparator
        final Character valueSeparator = components.getOrNull(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR);

        components.reportIfMissing();

        return SpreadsheetParserContexts.basic(
                this.dateTimeContext(now),
                this.expressionNumberContext(),
                valueSeparator
        );
    }

    // loadFromLocale...................................................................................................

    /**
     * Assumes a {@link Locale} has been set, failing if one is absent, and proceeds to set numerous properties with defaults,
     * note that existing values will be overwritten.
     * Date, DateTime and Time defaults are loaded parse {@link java.text.DateFormat} using the provided locale, formats pick the FULL style,
     * while parse pattern will include all patterns with all styles.
     */
    public final SpreadsheetMetadata loadFromLocale() {
        final Locale locale = this.getOrFail(SpreadsheetMetadataPropertyName.LOCALE);

        SpreadsheetMetadata updated = this;

        for (final SpreadsheetMetadataPropertyName<?> propertyName : SpreadsheetMetadataPropertyName.CONSTANTS.values()) {
            final Optional<?> localeAwareValue = propertyName.extractLocaleAwareValue(locale);
            if (localeAwareValue.isPresent()) {
                updated = updated.set(
                        propertyName,
                        Cast.to(localeAwareValue.get())
                );
            }
        }

        return updated;
    }

    // setDefaults......................................................................................................

    /**
     * Sets a {@link SpreadsheetMetadata} which will provide defaults when the value is not actually present in this instance.
     */
    public final SpreadsheetMetadata setDefaults(final SpreadsheetMetadata defaults) {
        Objects.requireNonNull(defaults, "defaults");

        return this.defaults().equals(defaults) ?
                this :
                this.replaceDefaults(defaults.checkDefault());
    }

    /**
     * Factory that creates a new {@link SpreadsheetMetadata} with the given defaults. Defaults will be null
     * if it was empty.
     */
    abstract SpreadsheetMetadata replaceDefaults(final SpreadsheetMetadata defaults);

    /**
     * Checks that all property values are valid or general and not specific to a single spreadsheet, and then
     * return the defaults {@link SpreadsheetMetadata} or null if its empty.
     */
    abstract SpreadsheetMetadata checkDefault();

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

    // viewport.........................................................................................................

    /**
     * This may be used to test if there is sufficient differences between this and the given {@link SpreadsheetMetadata}.
     */
    public final boolean shouldViewRefresh(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        boolean should = false;
        if (this.id().isPresent()) {

            for (final SpreadsheetMetadataPropertyName<?> name : SpreadsheetMetadataPropertyName.CONSTANTS.values()) {
                switch (name.name) {
                    case "creator":
                    case "create-date-time":
                    case "modified-by":
                    case "modified-date-time":
                    case "spreadsheet-name":
                    case "viewport":
                        break;
                    default:
                        should = false == this.get(name).equals(metadata.get(name));
                        break;
                }

                // any important property differences, stop checking and return true
                if (should) {
                    break;
                }
            }
        }

        return should;
    }

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
     * sub-classes will test if ALL their values are equal
     */
    abstract boolean equalsValues(final SpreadsheetMetadata other);

    @Override
    public final String toString() {
        return this.marshall(JsonNodeMarshallContexts.basic()).toString();
    }

    // JsonNodeContext..................................................................................................

    /**
     * Marshalls the individual properties and values and defaults but not the cached constructed values.
     */
    final JsonNode marshall(final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        final SpreadsheetMetadata defaults = this.defaults;
        if (null != defaults) {
            children.add(defaults.marshall(context).setName(DEFAULTS));
        }

        this.marshallProperties(children, context);
        return JsonNode.object().setChildren(children);
    }

    /**
     * This property in the json form will hold the defaults. Note actual property names may not start with underscore.
     */
    // VisibleForTesting
    static final JsonPropertyName DEFAULTS = JsonPropertyName.with("_defaults");

    /**
     * sub-classes must marshall their properties but not the defaults.
     */
    abstract void marshallProperties(final List<JsonNode> children,
                                     final JsonNodeMarshallContext context);

    static SpreadsheetMetadata unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        SpreadsheetMetadata metadata = EMPTY;

        final Optional<JsonNode> defaults = node.objectOrFail().get(DEFAULTS);
        if (defaults.isPresent()) {
            metadata = metadata.setDefaults(context.unmarshall(defaults.get(), SpreadsheetMetadata.class));
        }

        for (final JsonNode child : node.children()) {
            if (child.name().equals(DEFAULTS)) {
                continue;
            }

            final SpreadsheetMetadataPropertyName<?> name = SpreadsheetMetadataPropertyName.unmarshallName(child);
            metadata = metadata.set(
                    name,
                    Cast.to(
                            context.unmarshall(child, name.type())
                    )
            );
        }

        return metadata;
    }

    static {
        SpreadsheetMetadataPropertyName.CREATOR.value();

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetMetadata.class),
                SpreadsheetMetadata::unmarshall,
                SpreadsheetMetadata::marshall,
                SpreadsheetMetadata.class,
                SpreadsheetMetadataNonEmpty.class,
                SpreadsheetMetadataEmpty.class
        );
    }

    /**
     * A {@link SpreadsheetMetadata} loaded with defaults that are not {@link Locale} aware.
     */
    public static final SpreadsheetMetadata NON_LOCALE_DEFAULTS = nonLocaleDefaults();

    private static SpreadsheetMetadata nonLocaleDefaults() {
        EMPTY.id(); // force JsonNodeContext registering of collaborating types.
        SpreadsheetSelection.parseCellRange("A1");
        TextStyle.EMPTY.isEmpty();

        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.DEFAULT,
                MathContext.DECIMAL32
        ).unmarshall(
                JsonNode.parse(
                        new SpreadsheetMetadataDefaultTextResourceProvider()
                                .text()
                ),
                SpreadsheetMetadata.class
        );
    }

    // Patchable.....................................................................................................

    /**
     * Accepts a JSON object that represents a PATCH to this {@link SpreadsheetMetadata}, where properties with null values
     * will remove that property and other properties will set the new value.
     */
    @Override
    public final SpreadsheetMetadata patch(final JsonNode patch,
                                           final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(patch, "patch");
        Objects.requireNonNull(context, "context");

        SpreadsheetMetadata result = this;

        int patchCount = 0;
        for (final JsonNode nameAndValue : patch.objectOrFail().children()) {
            patchCount++;

            final SpreadsheetMetadataPropertyName<?> name = SpreadsheetMetadataPropertyName.unmarshallName(nameAndValue);

            if (nameAndValue.isNull()) {
                result = result.remove(name);
            } else {
                final Object value;
                if (name instanceof SpreadsheetMetadataPropertyNameStyle) {
                    final TextStyle style = result.getIgnoringDefaults(SpreadsheetMetadataPropertyName.STYLE)
                            .orElse(TextStyle.EMPTY);

                    value = style.patch(
                            nameAndValue,
                            context
                    );
                } else {
                    value = context.unmarshall(nameAndValue, name.type());
                }

                result = result.set(
                        name,
                        Cast.to(value)
                );
            }
        }

        if (0 == patchCount) {
            throw new IllegalArgumentException("Empty patch");
        }

        return result;
    }

    // TreePrintable...................................................................................................

    @Override
    public final void printTree(final IndentingPrinter printer) {
        for (final Map.Entry<SpreadsheetMetadataPropertyName<?>, Object> nameAndValue : this.value().entrySet()) {
            printer.print(nameAndValue.getKey().value());
            printer.print(": ");

            TreePrintable.printTreeOrToString(
                    nameAndValue.getValue(),
                    printer
            );

            printer.lineStart();
        }
    }

    // HasLocale.......................................................................................................

    @Override
    public Locale locale() {
        return this.getOrFail(SpreadsheetMetadataPropertyName.LOCALE);
    }
}
