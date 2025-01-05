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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.HasLocale;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.HasDecimalNumberContext;
import walkingkooka.math.HasMathContext;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContexts;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.export.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderSamplesContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderSamplesContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.HasExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
        HateosResource<SpreadsheetId>,
        Patchable<SpreadsheetMetadata>,
        TreePrintable,
        Value<Map<SpreadsheetMetadataPropertyName<?>, Object>> {

    public static final String HATEOS_RESOURCE_NAME_STRING = "spreadsheet";

    public static final HateosResourceName HATEOS_RESOURCE_NAME = HateosResourceName.with(HATEOS_RESOURCE_NAME_STRING);

    /**
     * A {@link SpreadsheetMetadata} with no textStyle.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final SpreadsheetMetadata EMPTY = SpreadsheetMetadataEmpty.instance();

    /**
     * Suggested prefix that should be used by wrapped {@link EnvironmentContext} for {@link #environmentContext(EnvironmentContext)}.
     */
    public static final String ENVIRONMENT_VALUE_NAME_PREFIX = "spreadsheet.";

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
        final Set<SpreadsheetMetadataPropertyName<?>> missing = SortedSets.tree();

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

    // CanBeEmpty.......................................................................................................

    /**
     * Returns true if the {@link SpreadsheetMetadata} is empty.
     */
    @Override
    public final boolean isEmpty() {
        return this instanceof SpreadsheetMetadataEmpty;
    }

    // HateosResource...................................................................................................

    /**
     * Returns the {@link SpreadsheetId} or throws a {@link IllegalStateException} if missing.
     */
    @Override
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
        return this.getOrGetDefaults(
                checkPropertyName(propertyName)
        );
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
                .orElseThrow(() -> new SpreadsheetMetadataPropertyValueException("Missing", propertyName, null));
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
        return this.set0(
                checkPropertyName(propertyName),
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
        return this.remove0(
                checkPropertyName(propertyName)
        );
    }

    abstract SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName);

    private static <T> SpreadsheetMetadataPropertyName<T> checkPropertyName(final SpreadsheetMetadataPropertyName<T> propertyName) {
        return Objects.requireNonNull(propertyName, "propertyName");
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

    // Converter........................................................................................................

    /**
     * Creates a {@link Converter} using the {@link SpreadsheetMetadataPropertyName} along with requiring other metadata properties.
     */
    // @VisibleForTesting
    final Converter<SpreadsheetConverterContext> converter(final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector,
                                                           final ConverterProvider converterProvider,
                                                           final ProviderContext context) {
        Objects.requireNonNull(converterSelector, "converterSelector");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final ConverterSelector converter = components.getOrNull(converterSelector);

        components.reportIfMissing();

        return converter.evaluateValueText(
                converterProvider,
                context
        );
    }
    
    // HasDateTimeContext...............................................................................................

    /**
     * Returns a {@link DateTimeContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#LOCALE}</li>
     * </ul>
     */
    public final DateTimeContext dateTimeContext(final HasNow now) {
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

    // EnvironmentContext...............................................................................................

    /**
     * Getter that returns a {@link EnvironmentContext} view o this {@link SpreadsheetMetadata} combined with the given {@link EnvironmentContext}.
     */
    public final EnvironmentContext environmentContext(final EnvironmentContext context) {
        Objects.requireNonNull(context, "context");

        if (null == this.environmentContext) {
            this.environmentContext = SpreadsheetMetadataEnvironmentContext.with(this);
        }
        return EnvironmentContexts.collection(
                Lists.of(
                        this.environmentContext,
                        context
                ),
                context
        );
    }

    /**
     * Cached {@link SpreadsheetMetadataEnvironmentContext}.
     */
    private EnvironmentContext environmentContext;

    // ExpressionFunctionProvider.......................................................................................

    /**
     * Returns a {@link ExpressionFunctionProvider} that applies the {@link ExpressionFunctionAliasSet} for the given
     * {@link SpreadsheetMetadataPropertyName}.
     */
    public final ExpressionFunctionProvider expressionFunctionProvider(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> propertyName,
                                                                       final ExpressionFunctionProvider provider) {
        Objects.requireNonNull(propertyName, "propertyName");
        Objects.requireNonNull(provider, "provider");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final ExpressionFunctionAliasSet functionsAliases = components.getOrNull(propertyName);

        components.reportIfMissing();

        return ExpressionFunctionProviders.aliases(
                functionsAliases,
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

        final ExpressionNumberKind kind = components.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);
        components.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        components.reportIfMissing();

        return ExpressionNumberContexts.basic(
                kind,
                this.decimalNumberContext()
        );
    }

    // HasExpressionNumberKind...........................................................................................

    @Override
    public final ExpressionNumberKind expressionNumberKind() {
        return this.getOrFail(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);
    }

    /**
     * Creates a {@link SpreadsheetConverterContext} to be used to convert {@link SpreadsheetCell cell} {@link SpreadsheetFormula#value()}
     * during a format.
     */
    private SpreadsheetConverterContext formatSpreadsheetConverterContext(final SpreadsheetLabelNameResolver labelNameResolver,
                                                                          final ConverterProvider converterProvider,
                                                                          final ProviderContext providerContext) {
        return this.spreadsheetConverterContext(
                SpreadsheetMetadataPropertyName.FORMAT_CONVERTER,
                labelNameResolver,
                converterProvider,
                providerContext
        );
    }

    /**
     * Returns a general {@link Converter} using the required properties.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#DATETIME_OFFSET}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_PARSER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_TIME_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_TIME_PARSER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NUMBER_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#NUMBER_PARSER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TEXT_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_PARSER}</li>
     * </ul>
     */
    public final Converter<SpreadsheetConverterContext> generalConverter(final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                                         final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                         final ProviderContext context) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetFormatterSelector dateFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMATTER);
        final SpreadsheetParserSelector dateParser = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSER);

        final SpreadsheetFormatterSelector dateTimeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);
        final SpreadsheetParserSelector dateTimeParser = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER);

        final SpreadsheetFormatterSelector numberFormat = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER);
        final SpreadsheetParserSelector numberParser = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_PARSER);

        final SpreadsheetFormatterSelector textFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMATTER);

        final SpreadsheetFormatterSelector timeFormat = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMATTER);
        final SpreadsheetParserSelector timeParser = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSER);

        components.reportIfMissing();

        return SpreadsheetConverters.general(
                spreadsheetFormatterProvider.spreadsheetFormatter(dateFormat, context),
                spreadsheetParserProvider.spreadsheetParser(dateParser, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(dateTimeFormat, context),
                spreadsheetParserProvider.spreadsheetParser(dateTimeParser, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(numberFormat, context),
                spreadsheetParserProvider.spreadsheetParser(numberParser, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(textFormat, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(timeFormat, context),
                spreadsheetParserProvider.spreadsheetParser(timeParser, context)
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

    // HasLocale........................................................................................................

    @Override
    public final Locale locale() {
        return this.getOrFail(SpreadsheetMetadataPropertyName.LOCALE);
    }

    // HasMathContext...................................................................................................

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

    /**
     * Returns the {@link SpreadsheetName} if one is present.
     */
    public Optional<SpreadsheetName> name() {
        return this.get(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME);
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

    // sort.............................................................................................................

    /**
     * Returns a {@link SpreadsheetComparatorContext} which may be used for sorting.
     */
    public final SpreadsheetComparatorContext sortSpreadsheetComparatorContext(final SpreadsheetLabelNameResolver resolveIfLabel,
                                                                               final SpreadsheetProvider spreadsheetProvider,
                                                                               final ProviderContext providerContext) {
        return this.spreadsheetComparatorContext(
                this.sortSpreadsheetConverterContext(
                        resolveIfLabel,
                        spreadsheetProvider, // ConverterProvider
                        providerContext // ProviderContext
                )
        );
    }

    /**
     * Creates a {@link SpreadsheetConverterContext} to be used when doing a sort.
     */
    private SpreadsheetConverterContext sortSpreadsheetConverterContext(final SpreadsheetLabelNameResolver labelNameResolver,
                                                                        final ConverterProvider converterProvider,
                                                                        final ProviderContext providerContext) {
        return this.spreadsheetConverterContext(
                SpreadsheetMetadataPropertyName.SORT_CONVERTER,
                labelNameResolver,
                converterProvider,
                providerContext
        );
    }

    // SpreadsheetComparatorContext.....................................................................................

    /**
     * Returns a {@link SpreadsheetComparatorContext}
     */
    private SpreadsheetComparatorContext spreadsheetComparatorContext(final SpreadsheetConverterContext converterContext) {
        return SpreadsheetComparatorContexts.basic(
                converterContext
        );
    }

    /**
     * Returns a {@link SpreadsheetConverterContext}
     */
    public final SpreadsheetConverterContext spreadsheetConverterContext(final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelectorPropertyName,
                                                                         final SpreadsheetLabelNameResolver labelNameResolver,
                                                                         final ConverterProvider converterProvider,
                                                                         final ProviderContext providerContext) {
        Objects.requireNonNull(converterSelectorPropertyName, "converterSelectorPropertyName");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");
        Objects.requireNonNull(providerContext, "providerContext");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final ConverterSelector converterSelector = components.getOrNull(converterSelectorPropertyName);
        final Long dateOffset = components.getOrNull(SpreadsheetMetadataPropertyName.DATETIME_OFFSET);
        final ExpressionNumberKind expressionNumberKind = components.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);

        components.reportIfMissing();

        return SpreadsheetConverterContexts.basic(
                this.converter(
                        converterSelectorPropertyName,
                        converterProvider,
                        providerContext
                ),
                labelNameResolver,
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                                dateOffset,
                                Converters.fake(),
                                this.dateTimeContext(providerContext),
                                this.decimalNumberContext()
                        ),
                        expressionNumberKind
                )
        );
    }

    // SpreadsheetFormatter.............................................................................................

    /**
     * Creates a {@link SpreadsheetFormatter} that creates a single formatter that formats values using {@link SpreadsheetFormatters#automatic(SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter)}
     */
    public final SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                           final ProviderContext context) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetFormatterSelector date = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMATTER);
        final SpreadsheetFormatterSelector dateTime = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);
        final SpreadsheetFormatterSelector number = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER);
        final SpreadsheetFormatterSelector text = components.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMATTER);
        final SpreadsheetFormatterSelector time = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMATTER);

        components.reportIfMissing();

        return SpreadsheetFormatters.automatic(
                spreadsheetFormatterProvider.spreadsheetFormatter(date, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(dateTime, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(number, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(text, context),
                spreadsheetFormatterProvider.spreadsheetFormatter(time, context)
        );
    }

    // SpreadsheetFormatterContext......................................................................................

    /**
     * Creates a {@link SpreadsheetFormatterContext}.
     */
    public final SpreadsheetFormatterContext spreadsheetFormatterContext(final SpreadsheetLabelNameResolver labelNameResolver,
                                                                         final ConverterProvider converterProvider,
                                                                         final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                                         final ProviderContext providerContext) {
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Integer characterWidth = components.getOrNull(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH);
        final Integer generalNumberFormatDigitCount = components.getOrNull(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT);

        components.reportIfMissing();

        return SpreadsheetFormatterContexts.basic(
                this.numberToColor(),
                this.nameToColor(),
                characterWidth,
                generalNumberFormatDigitCount,
                this.spreadsheetFormatter(
                        spreadsheetFormatterProvider,
                        providerContext
                ),
                this.formatSpreadsheetConverterContext(
                        labelNameResolver,
                        converterProvider,
                        providerContext
                )
        );
    }

    // SpreadsheetFormatterProviderSamplesContext.......................................................................

    /**
     * Creates a {@link SpreadsheetFormatterContext}.
     */
    public final SpreadsheetFormatterProviderSamplesContext spreadsheetFormatterProviderSamplesContext(final SpreadsheetLabelNameResolver labelNameResolver,
                                                                                                       final ConverterProvider converterProvider,
                                                                                                       final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                                                                       final ProviderContext providerContext) {
        return SpreadsheetFormatterProviderSamplesContexts.basic(
                this.spreadsheetFormatterContext(
                        labelNameResolver,
                        converterProvider,
                        spreadsheetFormatterProvider,
                        providerContext
                )
        );
    }

    // SpreadsheetParser................................................................................................

    /**
     * Returns a {@link Parser} that can be used to parse formulas.
     */
    public final SpreadsheetParser spreadsheetParser(final SpreadsheetParserProvider provider,
                                                     final ProviderContext context) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final SpreadsheetParserSelector date = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSER);
        final SpreadsheetParserSelector dateTime = components.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER);
        final SpreadsheetParserSelector number = components.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_PARSER);
        final SpreadsheetParserSelector time = components.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSER);

        components.reportIfMissing();

        return SpreadsheetParsers.valueOrExpression(
                Parsers.alternatives(
                        Lists.of(
                                provider.spreadsheetParser(date, context),
                                provider.spreadsheetParser(dateTime, context),
                                provider.spreadsheetParser(number, context)
                                        .andEmptyTextCursor(),
                                provider.spreadsheetParser(time, context)
                        )
                )
        );
    }

    // SpreadsheetParserContext.........................................................................................

    /**
     * Returns a {@link SpreadsheetParserContext}.
     */
    public final SpreadsheetParserContext spreadsheetParserContext(final HasNow now) {
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

    // SpreadsheetParser................................................................................................

    /**
     * Creates a {@link SpreadsheetProvider} honouring any provider properties wrapping the given {@link SpreadsheetProvider}.
     */
    public final SpreadsheetProvider spreadsheetProvider(final SpreadsheetProvider provider) {
        Objects.requireNonNull(provider, "provider");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final ConverterAliasSet converters = components.getOrNull(SpreadsheetMetadataPropertyName.CONVERTERS);
        final ExpressionFunctionAliasSet functions = components.getOrNull(SpreadsheetMetadataPropertyName.FUNCTIONS);
        final SpreadsheetComparatorAliasSet comparators = components.getOrNull(SpreadsheetMetadataPropertyName.COMPARATORS);
        final SpreadsheetExporterAliasSet exporters = components.getOrNull(SpreadsheetMetadataPropertyName.EXPORTERS);
        final SpreadsheetFormatterAliasSet formatters = components.getOrNull(SpreadsheetMetadataPropertyName.FORMATTERS);
        final SpreadsheetImporterAliasSet importers = components.getOrNull(SpreadsheetMetadataPropertyName.IMPORTERS);
        final SpreadsheetParserAliasSet parsers = components.getOrNull(SpreadsheetMetadataPropertyName.PARSERS);

        components.reportIfMissing();

        return SpreadsheetProviders.basic(
                ConverterProviders.aliases(
                        converters,
                        provider
                ),
                ExpressionFunctionProviders.aliases(
                        functions,
                        provider
                ),
                SpreadsheetComparatorProviders.aliases(
                        comparators,
                        provider
                ),
                SpreadsheetExporterProviders.aliases(
                        exporters,
                        provider
                ),
                SpreadsheetFormatterProviders.aliases(
                        formatters,
                        provider
                ),
                SpreadsheetImporterProviders.aliases(
                        importers,
                        provider
                ),
                SpreadsheetParserProviders.aliases(
                        parsers,
                        provider
                )
        );
    }

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

        this.marshallProperties(children, context);

        // marshall defaults AFTER non defaults,
        // this saves time when browsing json in chrome dev tools, where non default are often more interesting.
        final SpreadsheetMetadata defaults = this.defaults;
        if (null != defaults) {
            children.add(defaults.marshall(context).setName(DEFAULTS));
        }

        return JsonNode.object()
                .setChildren(children);
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

        // locale and now are not actually used w/in this method.
        final SpreadsheetFormatterProvider spreadsheetFormatterProvider = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.DEFAULT,
                MathContext.DECIMAL32
        ).unmarshall(
                JsonNode.parse(
                        new SpreadsheetMetadataDefaultTextResourceProvider()
                                .text()
                ),
                SpreadsheetMetadata.class
        ).set(
                SpreadsheetMetadataPropertyName.CONVERTERS,
                SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                        SpreadsheetMetadata.EMPTY,
                        SpreadsheetFormatterProviders.fake(),
                        SpreadsheetParserProviders.fake()
                ).converterInfos()
                        .aliasSet()
        ).set(
                SpreadsheetMetadataPropertyName.COMPARATORS,
                SpreadsheetComparatorProviders.spreadsheetComparators()
                                .spreadsheetComparatorInfos()
                        .aliasSet()
        ).set(
                SpreadsheetMetadataPropertyName.EXPORTERS,
                SpreadsheetExporterProviders.spreadsheetExport()
                        .spreadsheetExporterInfos()
                        .aliasSet()
        ).set(
                SpreadsheetMetadataPropertyName.FUNCTIONS,
                ExpressionFunctionAliasSet.EMPTY
        ).set(
                SpreadsheetMetadataPropertyName.FORMATTERS,
                spreadsheetFormatterProvider.spreadsheetFormatterInfos()
                        .aliasSet()
        ).set(
                SpreadsheetMetadataPropertyName.IMPORTERS,
                SpreadsheetImporterProviders.spreadsheetImport()
                        .spreadsheetImporterInfos()
                        .aliasSet()
        ).set(
                SpreadsheetMetadataPropertyName.PARSERS,
                SpreadsheetParserProviders.spreadsheetParsePattern(
                        spreadsheetFormatterProvider
                ).spreadsheetParserInfos()
                        .aliasSet()
        ).set(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
        );
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
}
