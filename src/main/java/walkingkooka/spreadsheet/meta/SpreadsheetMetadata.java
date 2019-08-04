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
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.HasDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.HasDecimalNumberContext;
import walkingkooka.math.HasMathContext;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetMetadata} holds a {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 */
public abstract class SpreadsheetMetadata implements HasDateTimeContext,
        HasDecimalNumberContext,
        HashCodeEqualsDefined,
        HasJsonNode,
        HasHateosLinkId,
        HasMathContext,
        HateosResource<Optional<SpreadsheetId>>,
        Value<Map<SpreadsheetMetadataPropertyName<?>, Object>> {

    /**
     * A {@link SpreadsheetMetadata} with no textStyle.
     */
    public final static SpreadsheetMetadata EMPTY = SpreadsheetMetadataEmpty.instance();

    /**
     * Factory that creates a {@link SpreadsheetMetadata} from a {@link Map}.
     */
    public static SpreadsheetMetadata with(final Map<SpreadsheetMetadataPropertyName<?>, Object> value) {
        return withSpreadsheetMetadataMap(SpreadsheetMetadataNonEmptyMap.with(value));
    }

    static SpreadsheetMetadata withSpreadsheetMetadataMap(final SpreadsheetMetadataNonEmptyMap map) {
        return map.isEmpty() ?
                EMPTY :
                SpreadsheetMetadataNonEmpty.with(map);
    }

    /**
     * Private ctor to limit sub classes.
     */
    SpreadsheetMetadata() {
        super();
    }

    /**
     * Returns true if the {@link SpreadsheetMetadata} is empty.
     */
    public abstract boolean isEmpty();

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

    abstract <V> Optional<V> get0(final SpreadsheetMetadataPropertyName<V> propertyName);

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

    // Function<Integer, Optional<Color>>................................................................................

    /**
     * Returns a {@link Function} that returns a {@link Color} given its number.
     */
    abstract public Function<Integer, Optional<Color>> numberToColor();

    static void checkColorNumber(final int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number " + number + " < 0");
        }
    }

    // SpreadsheetMetadataStyleVisitor..................................................................................

    abstract void accept(final SpreadsheetMetadataVisitor visitor);

    // HasDateTimeContext...............................................................................................

    /**
     * Returns a {@link DateTimeContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#LOCALE}</li>
     * </ul>
     */
    @Override
    public final DateTimeContext dateTimeContext() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Locale locale = components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);
        final Integer twoYearDigit = components.getOrNull(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR_INTERPRETATION);

        components.reportIfMissing();

        return DateTimeContexts.dateFormatSymbols(DateFormatSymbols.getInstance(locale), twoYearDigit);
    }

    // HasDecimalNumberContext..........................................................................................

    /**
     * Returns a {@link DecimalNumberContext} if the required properties are present.
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#CURRENCY_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#DECIMAL_POINT}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#EXPONENT_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#GROUPING_SEPARATOR}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#MINUS_SIGN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#PERCENTAGE_SYMBOL}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#PLUS_SIGN}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#PRECISION}</li>
     * <li>{@link SpreadsheetMetadataPropertyName#ROUNDING_MODE}</li>
     * </ul>
     * or
     * <ul>
     * <li>{@link SpreadsheetMetadataPropertyName#LOCALE} which may provide some defaults if some of the above properties are missing.</li>
     * </ul>
     */
    @Override
    public final DecimalNumberContext decimalNumberContext() {
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
    public final MathContext mathContext() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(this);

        final Integer precision = components.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        final RoundingMode roundingMode = components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        components.reportIfMissing();

        return new MathContext(precision, roundingMode);
    }

    // Object...........................................................................................................

    @Override
    abstract public int hashCode();

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    abstract boolean equals0(final SpreadsheetMetadata other);

    @Override
    abstract public String toString();

    // HasJsonNode......................................................................................................

    /**
     * Accepts a json object holding the metadata as a map.
     */
    static SpreadsheetMetadata fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

        for (JsonNode child : node.objectOrFail().children()) {
            final SpreadsheetMetadataPropertyName<?> name = SpreadsheetMetadataPropertyName.fromJsonNodeName(child);
            properties.put(name,
                    name.handler().fromJsonNode(child, name));
        }

        return with(properties);
    }

    static {
        HasJsonNode.register("metadata", SpreadsheetMetadata::fromJsonNode, SpreadsheetMetadata.class,
                SpreadsheetMetadataNonEmpty.class,
                SpreadsheetMetadataEmpty.class);
    }
}
