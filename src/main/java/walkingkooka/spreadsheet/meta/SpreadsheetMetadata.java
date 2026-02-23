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
import walkingkooka.currency.CanCurrencyForCurrencyCode;
import walkingkooka.currency.CurrencyContext;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.CanLocaleForLanguageTag;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.HasMathContext;
import walkingkooka.naming.HasOptionalName;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetStartup;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviderSamplesContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviderSamplesContexts;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContexts;
import walkingkooka.spreadsheet.value.HasMissingCellNumberValue;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.storage.HasUserDirectories;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.HasExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.Patchable;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.util.HasLocale;
import walkingkooka.validation.Validator;
import walkingkooka.validation.ValidatorContexts;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
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
 * Cell specific data such as individual format patterns are not stored here but on the {@link SpreadsheetCell}.
 */
public abstract class SpreadsheetMetadata implements CanBeEmpty,
    HasExpressionNumberKind,
    HasLocale,
    HasMathContext,
    HasOptionalName<SpreadsheetName>,
    HateosResource<SpreadsheetId>,
    Patchable<SpreadsheetMetadata>,
    TreePrintable,
    Value<Map<SpreadsheetMetadataPropertyName<?>, Object>>,
    HasMissingCellNumberValue {

    public static final String HATEOS_RESOURCE_NAME_STRING = "spreadsheet";

    public static final HateosResourceName HATEOS_RESOURCE_NAME = HateosResourceName.with(HATEOS_RESOURCE_NAME_STRING);

    /**
     * A {@link SpreadsheetMetadata} with no textStyle.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final SpreadsheetMetadata EMPTY = SpreadsheetMetadataEmpty.instance();

    /**
     * Suggested prefix that should be used by wrapped {@link EnvironmentContext} for {@link #spreadsheetEnvironmentContext(SpreadsheetEnvironmentContext)}.
     */
    public static final String ENVIRONMENT_VALUE_NAME_PREFIX = "spreadsheet.";

    /**
     * Private ctor to limit subclasses.
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

        addIfMissing(SpreadsheetMetadataPropertyName.AUDIT_INFO, missing);
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
            Objects.requireNonNull(propertyName, "propertyName")
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
     * subclasses will fetch the property returning the value.
     */
    public final <V> Optional<V> getIgnoringDefaults(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return this.getIgnoringDefaults0(
            Objects.requireNonNull(propertyName, "propertyName")
        );
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
            final TextStyle defaultStyle = this.defaults()
                .getStyleOrEmpty();

            this.effectiveStyle = defaultStyle.merge(style);
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
        Objects.requireNonNull(
            propertyName,
            "propertyName"
        );
        final V typedValue = propertyName.checkValue(value); // necessary because absolute references values are made relative
        if (typedValue instanceof SpreadsheetViewport) {
            // https://github.com/mP1/walkingkooka-spreadsheet/issues/7246
            final SpreadsheetViewport viewport = (SpreadsheetViewport) typedValue;

            if (viewport.navigations().isNotEmpty()) {
                throw new SpreadsheetMetadataPropertyValueException(
                    "Navigations not empty",
                    propertyName,
                    viewport
                );
            }
        }
        return this.set0(
            propertyName,
            typedValue
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
     * which could be the current or default value. subclasses need to test.
     */
    abstract <V> SpreadsheetMetadata setSameValue(final SpreadsheetMetadataPropertyName<V> propertyName,
                                                  final V value);

    /**
     * Handles the case where a value is different and if a character swaps might need to happen to avoid duplicates/clashes.
     */
    private <V> SpreadsheetMetadata setDifferentValue(final SpreadsheetMetadataPropertyName<V> propertyName,
                                                      final V value) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(this.value());
        copy.put(
            propertyName,
            value
        );

        return SpreadsheetMetadataNonEmpty.with(
            Maps.immutable(copy),
            this.defaults
        );
    }

    // remove...........................................................................................................

    /**
     * Removes a possibly existing property returning a {@link SpreadsheetMetadata} without.
     */
    public final SpreadsheetMetadata remove(final SpreadsheetMetadataPropertyName<?> propertyName) {
        return this.remove0(
            Objects.requireNonNull(
                propertyName,
                "propertyName"
            )
        );
    }

    abstract SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName);

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
    final Converter<SpreadsheetConverterContext> converter(final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelectorPropertyName,
                                                           final ConverterProvider converterProvider,
                                                           final ProviderContext context) {
        Objects.requireNonNull(converterSelectorPropertyName, "converterSelectorPropertyName");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final ConverterSelector converterSelector = missing.getOrNull(converterSelectorPropertyName);

        missing.reportIfMissing();

        final Converter<SpreadsheetConverterContext> converter = converterSelector.evaluateValueText(
            converterProvider,
            context
        );

        // prefix toString with property name
        return converter.setToString(
            converterSelectorPropertyName + ": " + converterSelector
        );
    }

    /**
     * Returns a general {@link Converter} using the required properties.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_TIME_OFFSET}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_PARSER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_TIME_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DATE_TIME_PARSER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_FORMATTER}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#TIME_PARSER}</li>
     * </ul>
     */
    public final Converter<SpreadsheetConverterContext> dateTimeConverter(final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                                          final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                          final ProviderContext context) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final SpreadsheetFormatterSelector dateFormat = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMATTER);
        final SpreadsheetParserSelector dateParser = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSER);

        final SpreadsheetFormatterSelector dateTimeFormat = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);
        final SpreadsheetParserSelector dateTimeParser = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER);

        final SpreadsheetFormatterSelector timeFormat = missing.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMATTER);
        final SpreadsheetParserSelector timeParser = missing.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSER);

        missing.reportIfMissing();

        return SpreadsheetConverters.dateTime(
            spreadsheetFormatterProvider.spreadsheetFormatter(dateFormat, context)
                .converter(), // dateToString
            spreadsheetFormatterProvider.spreadsheetFormatter(dateTimeFormat, context)
                .converter(), // dateTimeToString
            spreadsheetFormatterProvider.spreadsheetFormatter(timeFormat, context)
                .converter(), // timeToString
            SpreadsheetConverters.textToDate(
                spreadsheetParserProvider.spreadsheetParser(dateParser, context)
            ), // stringToDate
            SpreadsheetConverters.textToDateTime(
                spreadsheetParserProvider.spreadsheetParser(dateTimeParser, context)
            ), // stringToDateTime
            SpreadsheetConverters.textToTime(
                spreadsheetParserProvider.spreadsheetParser(timeParser, context)
            ) // stringToTime
        );
    }

    // DateTimeContext..................................................................................................

    /**
     * Constant holding no {@link SpreadsheetCell}.
     */
    public final static Optional<SpreadsheetCell> NO_CELL = Optional.empty();

    /**
     * Returns a {@link DateTimeContext} using a combination of {@link SpreadsheetCell} and {@link SpreadsheetMetadata}
     * properties in the following order:
     * <ol>
     *    <li>If the given cell has {@link DateTimeSymbols} that will be used</li>
     *    <li>If the given cell has {@link Locale} that will be used</li>
     *    <li>With the {@link SpreadsheetMetadataPropertyName#LOCALE} from this metadata</li>
     * </ol>
     */
    public final DateTimeContext dateTimeContext(final Optional<SpreadsheetCell> cell,
                                                 final HasNow now,
                                                 final LocaleContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final Locale locale = missing.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);
        final Integer defaultYear = missing.getOrNull(SpreadsheetMetadataPropertyName.DEFAULT_YEAR);
        final Integer twoYearDigit = missing.getOrNull(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR);

        missing.reportIfMissing();

        DateTimeSymbols dateTimeSymbols = null;

        if (cell.isPresent()) {
            final SpreadsheetCell spreadsheetCell = cell.get();

            dateTimeSymbols = spreadsheetCell.dateTimeSymbols()
                .orElse(null);

            if (null == dateTimeSymbols) {
                final Locale spreadsheetCellLocale = spreadsheetCell.locale()
                    .orElse(null);
                if (null != spreadsheetCellLocale) {
                    dateTimeSymbols = context.dateTimeSymbolsForLocale(spreadsheetCellLocale)
                        .orElse(null);
                }
            }
        }

        if (null == dateTimeSymbols) {
            dateTimeSymbols = this.get(SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS)
                .orElse(null);
        }

        if (null == dateTimeSymbols) {
            dateTimeSymbols = context.dateTimeSymbolsForLocale(locale)
                // Missing DateTimeSymbols for locale EN-AU
                .orElseThrow(() -> new IllegalArgumentException("Missing " + DateTimeSymbols.class.getSimpleName() + " for locale " + locale));
        }

        return DateTimeContexts.basic(
            dateTimeSymbols,
            locale,
            defaultYear,
            twoYearDigit,
            now
        );
    }

    // DecimalNumberContext.............................................................................................

    /**
     * Returns a {@link DecimalNumberContext} using a combination of {@link SpreadsheetCell} and {@link SpreadsheetMetadata}
     * properties in the following order:
     * <ol>
     *    <li>If the given cell has {@link DecimalNumberSymbols} that will be used</li>
     *    <li>If the given cell has {@link Locale} that will be used</li>
     *    <li>With the {@link SpreadsheetMetadataPropertyName#LOCALE} from this metadata</li>
     * </ol>
     */
    public final DecimalNumberContext decimalNumberContext(final Optional<SpreadsheetCell> cell,
                                                           final LocaleContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final Integer decimalNumberDigitCount = missing.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT);
        final Locale locale = missing.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);

        MathContext mathContext;
        try {
            mathContext = this.mathContext();
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            mathContext = null;
        }

        missing.reportIfMissing();

        DecimalNumberSymbols decimalNumberSymbols = null;
        if (cell.isPresent()) {
            final SpreadsheetCell spreadsheetCell = cell.get();
            decimalNumberSymbols = spreadsheetCell.decimalNumberSymbols()
                .orElse(null);

            if (null == decimalNumberSymbols) {
                final Locale spreadsheetCellLocale = spreadsheetCell.locale()
                    .orElse(null);
                if (null != spreadsheetCellLocale) {
                    decimalNumberSymbols = context.decimalNumberSymbolsForLocale(spreadsheetCellLocale)
                        .orElse(null);
                }
            }
        }

        if (null == decimalNumberSymbols) {
            decimalNumberSymbols = this.get(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS)
                .orElse(null);
        }

        if (null == decimalNumberSymbols) {
            decimalNumberSymbols = context.decimalNumberSymbolsForLocale(locale)
                .orElseThrow(
                    // Missing DecimalNumberSymbols for locale EN-AU
                    () -> new IllegalArgumentException("Missing " + DecimalNumberSymbols.class.getSimpleName() + " for locale " + locale)
                );
        }

        return DecimalNumberContexts.basic(
            decimalNumberDigitCount,
            decimalNumberSymbols,
            locale,
            mathContext
        );
    }

    // ExpressionFunctionProvider.......................................................................................

    /**
     * Returns a {@link ExpressionFunctionProvider} that applies the {@link ExpressionFunctionAliasSet} for the given
     * {@link SpreadsheetMetadataPropertyName}.
     */
    public final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> propertyName,
                                                                                                               final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> provider) {
        Objects.requireNonNull(propertyName, "propertyName");
        Objects.requireNonNull(provider, "provider");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final ExpressionFunctionAliasSet functionsAliases = missing.getOrNull(propertyName);

        missing.reportIfMissing();

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
    public final ExpressionNumberContext expressionNumberContext(final Optional<SpreadsheetCell> cell,
                                                                 final LocaleContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final ExpressionNumberKind kind = missing.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);

        DecimalNumberContext decimalNumberContext;
        try {
            decimalNumberContext = this.decimalNumberContext(
                cell,
                context
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            decimalNumberContext = null;
        }

        missing.reportIfMissing();

        return ExpressionNumberContexts.basic(
            kind,
            decimalNumberContext
        );
    }

    // HasExpressionNumberKind...........................................................................................

    @Override
    public final ExpressionNumberKind expressionNumberKind() {
        return this.getOrFail(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);
    }

    // HasJsonNodeMarshallContext.......................................................................................

    /**
     * Returns a {@link JsonNodeMarshallContext}
     */
    public final JsonNodeMarshallContext jsonNodeMarshallContext() {
        return JsonNodeMarshallContexts.basic();
    }

    // HasJsonNodeUnmarshallContext......................................................................................

    public final JsonNodeUnmarshallContext jsonNodeUnmarshallContext(final CanCurrencyForCurrencyCode canCurrencyForCurrencyCode,
                                                                     final CanLocaleForLanguageTag canLocaleForLanguageTag) {
        Objects.requireNonNull(canCurrencyForCurrencyCode, "canCurrencyForCurrencyCode");
        Objects.requireNonNull(canLocaleForLanguageTag, "canLocaleForLanguageTag");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final ExpressionNumberKind expressionNumberKind = missing.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);

        MathContext mathContext;
        try {
            mathContext = this.mathContext();
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            mathContext = null;
        }

        missing.reportIfMissing();

        return JsonNodeUnmarshallContexts.basic(
            canCurrencyForCurrencyCode,
            canLocaleForLanguageTag,
            expressionNumberKind,
            mathContext
        );
    }

    /**
     * Returns a {@link JsonNodeMarshallUnmarshallContext} build using properties from this metadata.
     */
    public final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext(final CanCurrencyForCurrencyCode canCurrencyForCurrencyCode,
                                                                                     final CanLocaleForLanguageTag canLocaleForLanguageTag) {
        return JsonNodeMarshallUnmarshallContexts.basic(
            this.jsonNodeMarshallContext(),
            this.jsonNodeUnmarshallContext(
                canCurrencyForCurrencyCode,
                canLocaleForLanguageTag
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
    public final SpreadsheetMetadata loadFromLocale(final CurrencyLocaleContext context) {
        Objects.requireNonNull(context, "context");
        context.setLocale(this.locale());

        SpreadsheetMetadata updated = this;

        for (final SpreadsheetMetadataPropertyName<?> propertyName : SpreadsheetMetadataPropertyName.CONSTANTS.values()) {
            final Optional<?> localeAwareValue = propertyName.extractLocaleAwareValue(context);
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
        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final Integer precision = missing.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        final RoundingMode roundingMode = missing.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        missing.reportIfMissing();

        return new MathContext(precision, roundingMode);
    }

    // HasOptionalName..................................................................................................

    /**
     * Returns the {@link SpreadsheetName} if one is present.
     */
    @Override
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
    public final SpreadsheetComparatorContext sortSpreadsheetComparatorContext(final HasUserDirectories hasUserDirectories,
                                                                               final Indentation indentation,
                                                                               final SpreadsheetLabelNameResolver resolveIfLabel,
                                                                               final LineEnding lineEnding,
                                                                               final SpreadsheetProvider spreadsheetProvider,
                                                                               final CurrencyLocaleContext currencyLocaleContext,
                                                                               final ProviderContext providerContext) {
        return this.spreadsheetComparatorContext(
            this.sortSpreadsheetConverterContext(
                resolveIfLabel,
                spreadsheetProvider, // ConverterProvider
                hasUserDirectories,
                indentation,
                lineEnding,
                currencyLocaleContext,
                providerContext // ProviderContext
            )
        );
    }

    /**
     * Creates a {@link SpreadsheetConverterContext} to be used when doing a sort.
     */
    private SpreadsheetConverterContext sortSpreadsheetConverterContext(final SpreadsheetLabelNameResolver labelNameResolver,
                                                                        final ConverterProvider converterProvider,
                                                                        final HasUserDirectories hasUserDirectories,
                                                                        final Indentation indentation,
                                                                        final LineEnding lineEnding,
                                                                        final CurrencyLocaleContext currencyLocaleContext,
                                                                        final ProviderContext providerContext) {
        return this.spreadsheetConverterContext(
            NO_CELL,
            NO_VALIDATION_REFERENCE,
            SpreadsheetMetadataPropertyName.SORT_CONVERTER,
            hasUserDirectories,
            indentation,
            labelNameResolver,
            lineEnding,
            converterProvider,
            currencyLocaleContext,
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

    // SpreadsheetConverterContext......................................................................................

    /**
     * Returns a {@link SpreadsheetConverterContext}
     */
    public final SpreadsheetConverterContext spreadsheetConverterContext(final Optional<SpreadsheetCell> cell,
                                                                         final Optional<SpreadsheetExpressionReference> validationReference,
                                                                         final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelectorPropertyName,
                                                                         final HasUserDirectories hasUserDirectories,
                                                                         final Indentation indentation,
                                                                         final SpreadsheetLabelNameResolver labelNameResolver,
                                                                         final LineEnding lineEnding,
                                                                         final ConverterProvider converterProvider,
                                                                         final CurrencyLocaleContext currencyLocaleContext,
                                                                         final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(validationReference, "validationReference");
        Objects.requireNonNull(converterSelectorPropertyName, "converterSelectorPropertyName");
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");
        Objects.requireNonNull(indentation, "indentation");
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");
        Objects.requireNonNull(lineEnding, "lineEnding");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(currencyLocaleContext, "currencyLocaleContext");
        Objects.requireNonNull(providerContext, "providerContext");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        Converter<SpreadsheetConverterContext> converter;
        try {
            converter = this.converter(
                converterSelectorPropertyName,
                converterProvider,
                providerContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            converter = null;
        }

        DateTimeContext dateTimeContext;
        try {
            dateTimeContext = this.dateTimeContext(
                cell,
                providerContext, // now
                currencyLocaleContext // LocaleContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            dateTimeContext = null;
        }

        DecimalNumberContext decimalNumberContext;
        try {
            decimalNumberContext = this.decimalNumberContext(
                cell,
                currencyLocaleContext // LocaleContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            decimalNumberContext = null;
            missing.addMissing(cause);
        }

        JsonNodeMarshallContext jsonNodeMarshallContext;
        try {
            jsonNodeMarshallContext = this.jsonNodeMarshallContext();
        } catch (final MissingMetadataPropertiesException cause) {
            jsonNodeMarshallContext = null;
            missing.addMissing(cause);
        }

        JsonNodeUnmarshallContext jsonNodeUnmarshallContext;
        try {
            jsonNodeUnmarshallContext = this.jsonNodeUnmarshallContext(
                currencyLocaleContext, // CanCurrencyForCurrencyCode
                currencyLocaleContext // CanLocaleForLanguageTag
            );
        } catch (final MissingMetadataPropertiesException cause) {
            jsonNodeUnmarshallContext = null;
            missing.addMissing(cause);
        }

        final Long dateOffset = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET);
        final ExpressionNumberKind expressionNumberKind = missing.getOrNull(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND);
        final Character valueSeparator = missing.getOrNull(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR);

        missing.reportIfMissing();

        return SpreadsheetConverterContexts.basic(
            hasUserDirectories,
            Optional.of(this),
            validationReference,
            converter,
            labelNameResolver,
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                        currencyLocaleContext, // canCurrencyForLocale
                        false, // canNumbersHaveGroupSeparator
                        dateOffset,
                        indentation,
                        lineEnding,
                        valueSeparator, // valueSeparator
                        Converters.fake(),
                        dateTimeContext,
                        decimalNumberContext,
                        currencyLocaleContext // LocaleContext
                    ),
                    expressionNumberKind
                ),
                JsonNodeMarshallUnmarshallContexts.basic(
                    jsonNodeMarshallContext,
                    jsonNodeUnmarshallContext
                )
            ),
            currencyLocaleContext // LocaleContext
        );
    }

    public final static Optional<SpreadsheetExpressionReference> NO_VALIDATION_REFERENCE = SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE;

    // SpreadsheetEnvironmentContext....................................................................................

    /**
     * Getter that returns a {@link EnvironmentContext} view o this {@link SpreadsheetMetadata} using the given {@link EnvironmentContext}
     * for the time and user.
     */
    public final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext(final SpreadsheetEnvironmentContext context) {
        return SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            this,
            context
        );
    }

    // SpreadsheetFormatter.............................................................................................

    /**
     * Creates a {@link SpreadsheetFormatter} that creates a single formatter that formats values using {@link SpreadsheetFormatters#automatic(SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter, SpreadsheetFormatter)}
     */
    public final SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                           final ProviderContext context) {
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(context, "context");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final SpreadsheetFormatterSelector date = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_FORMATTER);
        final SpreadsheetFormatterSelector dateTime = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER);
        final SpreadsheetFormatterSelector error = missing.getOrNull(SpreadsheetMetadataPropertyName.ERROR_FORMATTER);
        final SpreadsheetFormatterSelector number = missing.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER);
        final SpreadsheetFormatterSelector text = missing.getOrNull(SpreadsheetMetadataPropertyName.TEXT_FORMATTER);
        final SpreadsheetFormatterSelector time = missing.getOrNull(SpreadsheetMetadataPropertyName.TIME_FORMATTER);

        missing.reportIfMissing();

        return SpreadsheetFormatters.automatic(
            spreadsheetFormatterProvider.spreadsheetFormatter(date, context),
            spreadsheetFormatterProvider.spreadsheetFormatter(dateTime, context),
            spreadsheetFormatterProvider.spreadsheetFormatter(error, context),
            spreadsheetFormatterProvider.spreadsheetFormatter(number, context),
            spreadsheetFormatterProvider.spreadsheetFormatter(text, context),
            spreadsheetFormatterProvider.spreadsheetFormatter(time, context)
        );
    }

    // SpreadsheetFormatterContext......................................................................................

    /**
     * Creates a {@link SpreadsheetFormatterContext}.
     */
    public final SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell,
                                                                         final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                                                         final HasUserDirectories hasUserDirectories,
                                                                         final Indentation indentation,
                                                                         final SpreadsheetLabelNameResolver labelNameResolver,
                                                                         final LineEnding lineEnding,
                                                                         final CurrencyLocaleContext currencyLocaleContext,
                                                                         final SpreadsheetProvider spreadsheetProvider,
                                                                         final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(spreadsheetExpressionEvaluationContext, "spreadsheetExpressionEvaluationContext");
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");
        Objects.requireNonNull(indentation, "indentation");
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");
        Objects.requireNonNull(lineEnding, "lineEnding");
        Objects.requireNonNull(currencyLocaleContext, "currencyLocaleContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        SpreadsheetFormatter spreadsheetFormatter;
        try {
            spreadsheetFormatter = this.spreadsheetFormatter(
                spreadsheetProvider,
                providerContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            spreadsheetFormatter = null;
        }

        SpreadsheetConverterContext formatSpreadsheetConverterContext;
        try {
            formatSpreadsheetConverterContext = this.spreadsheetConverterContext(
                cell,
                NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER,
                hasUserDirectories,
                indentation,
                labelNameResolver,
                lineEnding,
                spreadsheetProvider,
                currencyLocaleContext,
                providerContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            formatSpreadsheetConverterContext = null;
        }

        final Integer characterWidth = missing.getOrNull(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH);

        missing.reportIfMissing();

        return SpreadsheetFormatterContexts.basic(
            cell,
            this.numberToColor(),
            this.nameToColor(),
            characterWidth,
            spreadsheetFormatter,
            spreadsheetExpressionEvaluationContext,
            formatSpreadsheetConverterContext,
            spreadsheetProvider,
            providerContext
        );
    }

    // SpreadsheetFormatterProviderSamplesContext.......................................................................

    /**
     * Creates a {@link SpreadsheetFormatterContext}.
     */
    public final SpreadsheetFormatterProviderSamplesContext spreadsheetFormatterProviderSamplesContext(final Optional<SpreadsheetCell> cell,
                                                                                                       final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                                                                                       final HasUserDirectories hasUserDirectories,
                                                                                                       final Indentation indentation,
                                                                                                       final SpreadsheetLabelNameResolver labelNameResolver,
                                                                                                       final LineEnding lineEnding,
                                                                                                       final CurrencyLocaleContext currencyLocaleContext,
                                                                                                       final SpreadsheetProvider spreadsheetProvider,
                                                                                                       final ProviderContext providerContext) {
        return SpreadsheetFormatterProviderSamplesContexts.basic(
            this.spreadsheetFormatterContext(
                cell,
                spreadsheetExpressionEvaluationContext,
                hasUserDirectories,
                indentation,
                labelNameResolver,
                lineEnding,
                currencyLocaleContext,
                spreadsheetProvider,
                providerContext
            ),
            providerContext
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

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final SpreadsheetParserSelector date = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_PARSER);
        final SpreadsheetParserSelector dateTime = missing.getOrNull(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER);
        final SpreadsheetParserSelector number = missing.getOrNull(SpreadsheetMetadataPropertyName.NUMBER_PARSER);
        final SpreadsheetParserSelector time = missing.getOrNull(SpreadsheetMetadataPropertyName.TIME_PARSER);

        missing.reportIfMissing();

        return SpreadsheetFormulaParsers.valueOrExpression(
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
    public final SpreadsheetParserContext spreadsheetParserContext(final Optional<SpreadsheetCell> cell,
                                                                   final LocaleContext localeContext,
                                                                   final HasNow now) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(now, "now");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        // DateTimeContext
        DateTimeContext dateTimeContext;
        try {
            dateTimeContext = this.dateTimeContext(
                cell,
                now,
                localeContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            dateTimeContext = null;
        }

        // ExpressionNumberContext
        ExpressionNumberContext expressionNumberContext;
        try {
            expressionNumberContext = this.expressionNumberContext(
                cell,
                localeContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            expressionNumberContext = null;
        }

        // valueSeparator
        final Character valueSeparator = missing.getOrNull(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR);

        missing.reportIfMissing();

        return SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.COLUMN_AND_LINE_EXPECTED,
            dateTimeContext,
            expressionNumberContext,
            valueSeparator
        );
    }

    // SpreadsheetValidatorContext......................................................................................

    /**
     * Creates a {@link SpreadsheetValidatorContext} with the given {@link SpreadsheetCellReference}.
     */
    public final SpreadsheetValidatorContext spreadsheetValidatorContext(final SpreadsheetExpressionReference cellOrLabel,
                                                                         final Function<ValidatorSelector, Validator<SpreadsheetExpressionReference, SpreadsheetValidatorContext>> validatorSelectorToValidator,
                                                                         final BiFunction<Object, SpreadsheetExpressionReference, SpreadsheetExpressionEvaluationContext> referenceToExpressionEvaluationContext,
                                                                         final HasUserDirectories hasUserDirectories,
                                                                         final Indentation indentation,
                                                                         final SpreadsheetLabelNameResolver labelNameResolver,
                                                                         final LineEnding lineEnding,
                                                                         final ConverterProvider converterProvider,
                                                                         final CurrencyContext currencyContext,
                                                                         final LocaleContext localeContext,
                                                                         final ProviderContext providerContext) {
        Objects.requireNonNull(cellOrLabel, "cellOrLabel");
        Objects.requireNonNull(validatorSelectorToValidator, "validatorSelectorToValidator");
        Objects.requireNonNull(referenceToExpressionEvaluationContext, "referenceToExpressionEvaluationContext");
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");
        Objects.requireNonNull(indentation, "indentation");
        Objects.requireNonNull(labelNameResolver, "labelNameResolver");
        Objects.requireNonNull(lineEnding, "lineEnding");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(currencyContext, "currencyContext");
        Objects.requireNonNull(providerContext, "providerContext");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        SpreadsheetConverterContext spreadsheetConverterContext;
        try {
            spreadsheetConverterContext = this.spreadsheetConverterContext(
                NO_CELL,
                Optional.of(cellOrLabel), // validationReference
                SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
                hasUserDirectories,
                indentation,
                labelNameResolver,
                lineEnding,
                converterProvider,
                currencyContext.setLocaleContext(localeContext),
                providerContext
            );
        } catch (final MissingMetadataPropertiesException cause) {
            missing.addMissing(cause);
            spreadsheetConverterContext = null;
        }

        missing.reportIfMissing();

        return SpreadsheetValidatorContexts.basic(
            ValidatorContexts.basic(
                cellOrLabel,
                Cast.to(validatorSelectorToValidator),
                Cast.to(referenceToExpressionEvaluationContext),
                spreadsheetConverterContext,
                providerContext // EnvironmentContext
            )
        );
    }

    // SpreadsheetProvider..............................................................................................

    /**
     * Creates a {@link SpreadsheetProvider} honouring any provider properties wrapping the given {@link SpreadsheetProvider}.
     */
    public final SpreadsheetProvider spreadsheetProvider(final SpreadsheetProvider provider) {
        Objects.requireNonNull(provider, "provider");

        final SpreadsheetMetadataMissingComponents missing = SpreadsheetMetadataMissingComponents.with(this);

        final ConverterAliasSet converters = missing.getOrNull(SpreadsheetMetadataPropertyName.CONVERTERS);
        final ExpressionFunctionAliasSet functions = missing.getOrNull(SpreadsheetMetadataPropertyName.FUNCTIONS);
        final SpreadsheetComparatorAliasSet comparators = missing.getOrNull(SpreadsheetMetadataPropertyName.COMPARATORS);
        final SpreadsheetExporterAliasSet exporters = missing.getOrNull(SpreadsheetMetadataPropertyName.EXPORTERS);
        final SpreadsheetFormatterAliasSet formatters = missing.getOrNull(SpreadsheetMetadataPropertyName.FORMATTERS);
        final FormHandlerAliasSet formHandlers = missing.getOrNull(SpreadsheetMetadataPropertyName.FORM_HANDLERS);
        final SpreadsheetImporterAliasSet importers = missing.getOrNull(SpreadsheetMetadataPropertyName.IMPORTERS);
        final SpreadsheetParserAliasSet parsers = missing.getOrNull(SpreadsheetMetadataPropertyName.PARSERS);
        final ValidatorAliasSet validators = missing.getOrNull(SpreadsheetMetadataPropertyName.VALIDATORS);

        missing.reportIfMissing();

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
            FormHandlerProviders.aliases(
                formHandlers,
                provider
            ),
            SpreadsheetImporterProviders.aliases(
                importers,
                provider
            ),
            SpreadsheetParserProviders.aliases(
                parsers,
                provider
            ),
            ValidatorProviders.aliases(
                validators,
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
                    case "auditInfo":
                    case "spreadsheetName":
                        break;
                    case "viewportHome":
                        should = false == this.getOrFail(SpreadsheetMetadataPropertyName.VIEWPORT_HOME)
                            .equalsIgnoreReferenceKind(metadata.getOrFail(SpreadsheetMetadataPropertyName.VIEWPORT_HOME));
                        break;
                    case "viewportSelection":
                        final AnchoredSpreadsheetSelection selection = this.get(SpreadsheetMetadataPropertyName.VIEWPORT_SELECTION)
                            .orElse(null);
                        final AnchoredSpreadsheetSelection otherSelection = metadata.get(SpreadsheetMetadataPropertyName.VIEWPORT_SELECTION)
                            .orElse(null);
                        should = false == (
                            (null == selection && null == otherSelection) ||
                                (null != selection && selection.equalsIgnoreReferenceKind(otherSelection))
                        );
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
    public final int hashCode() {
        if (0 == this.hashCode) {
            this.hashCode = this.value()
                .hashCode();
        }
        return this.hashCode;
    }

    private int hashCode;

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            null != other &&
                this.getClass() == other.getClass() &&
                this.equals0((SpreadsheetMetadata) other);
    }

    private boolean equals0(final SpreadsheetMetadata other) {
        return this.value()
            .equals(other.value()) &&
            Objects.equals(
                this.defaults,
                other.defaults
            );
    }

    @Override
    public final String toString() {
        return this.marshall(
            JsonNodeMarshallContexts.basic()
        ).toString();
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
     * subclasses must marshall their properties but not the defaults.
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
        SpreadsheetStartup.init();

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
        // locale and now are not actually used w/in this method.
        final SpreadsheetFormatterProvider spreadsheetFormatterProvider = SpreadsheetFormatterProviders.spreadsheetFormatters();

        return JsonNodeUnmarshallContexts.basic(
            (String cc) -> Optional.ofNullable(
                Currency.getInstance(cc)
            ),
            (String lt) -> Optional.of(
                Locale.forLanguageTag(lt)
            ),
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
                    (final ProviderContext c) -> {
                        throw new UnsupportedOperationException();
                    }
                ).converterInfos()
                .aliasSet()
        ).set(
            SpreadsheetMetadataPropertyName.COMPARATORS,
            SpreadsheetComparatorProviders.spreadsheetComparators()
                .spreadsheetComparatorInfos()
                .aliasSet()
        ).set(
            SpreadsheetMetadataPropertyName.ERROR_FORMATTER,
            SpreadsheetFormatterSelector.parse(
                "badge-error default-text"
            )
        ).set(
            SpreadsheetMetadataPropertyName.EXPORTERS,
            SpreadsheetExporterProviders.spreadsheetExport()
                .spreadsheetExporterInfos()
                .aliasSet()
        ).set(
            SpreadsheetMetadataPropertyName.FUNCTIONS,
            SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
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
        ).set(
            SpreadsheetMetadataPropertyName.VALIDATORS,
            ValidatorProviders.validators()
                .validatorInfos()
                .aliasSet()
        );
    }

    // TreePrintable...................................................................................................

    @Override
    public final void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            final SpreadsheetId spreadsheetId = this.get(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                .orElse(null);
            if(null != spreadsheetId) {
                printTreeValue(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    spreadsheetId,
                    printer
                );

                final SpreadsheetName spreadsheetName = this.get(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME)
                    .orElse(null);
                if(null != spreadsheetName) {
                    printTreeValue(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        spreadsheetName,
                        printer
                    );
                }

                printer.indent();
            }

            for (final Map.Entry<SpreadsheetMetadataPropertyName<?>, Object> nameAndValue : this.value().entrySet()) {
                final SpreadsheetMetadataPropertyName<?> name = nameAndValue.getKey();

                // skip SpreadsheetId printed "first"
                if(SpreadsheetMetadataPropertyName.SPREADSHEET_ID.equals(name) || SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.equals(name)) {
                    continue;
                }
                this.printTreeValue(
                    name,
                    nameAndValue.getValue(),
                    printer
                );
            }

            if(null != spreadsheetId) {
                printer.outdent();
            }
        }
        printer.outdent();
    }

    private void printTreeValue(final SpreadsheetMetadataPropertyName<?> name,
                                final Object value,
                                final IndentingPrinter printer) {
        printer.print(name.value());
        printer.print(": ");

        final boolean autoIndent = value instanceof Collection ||
            value instanceof AuditInfo ||
            value instanceof DateTimeSymbols ||
            value instanceof DecimalNumberSymbols ||
            value instanceof PluginSelectorLike ||
            value instanceof TextStyle;
        if (autoIndent) {
            printer.indent();
            printer.println();
        }

        TreePrintable.printTreeOrToString(
            value,
            printer
        );

        printer.lineStart();

        if (autoIndent) {
            printer.outdent();
        }
    }

    // HasMissingCellNumberValue........................................................................................

    @Override
    public final ExpressionNumber missingCellNumberValue() {
        return this.expressionNumberKind()
            .zero();
    }
}
