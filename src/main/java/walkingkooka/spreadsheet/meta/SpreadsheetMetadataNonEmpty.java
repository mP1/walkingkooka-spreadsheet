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
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetMetadataNonEmpty} holds a non empty {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final class SpreadsheetMetadataNonEmpty extends SpreadsheetMetadata {

    /**
     * Factory that creates a {@link SpreadsheetMetadataNonEmpty} parse a {@link Map}.
     */
    static SpreadsheetMetadataNonEmpty with(final Map<SpreadsheetMetadataPropertyName<?>, Object> properties,
                                            final SpreadsheetMetadata defaults) {
        return new SpreadsheetMetadataNonEmpty(properties, defaults);
    }

    private SpreadsheetMetadataNonEmpty(final Map<SpreadsheetMetadataPropertyName<?>, Object> value,
                                        final SpreadsheetMetadata defaults) {
        super(defaults);
        this.value = value;
    }

    // Value..........................................................................................................

    @Override
    public Map<SpreadsheetMetadataPropertyName<?>, Object> value() {
        return this.value;
    }

    final Map<SpreadsheetMetadataPropertyName<?>, Object> value;

    // setDefaults......................................................................................................

    /**
     * The new {@link SpreadsheetMetadata} must be rebuilt because a new default could result in some properties being swapped.
     */
    @Override
    SpreadsheetMetadata replaceDefaults(final SpreadsheetMetadata defaults) {
        SpreadsheetMetadata metadata = EMPTY.setDefaults(defaults);

        for (final Entry<SpreadsheetMetadataPropertyName<?>, Object> nameAndValue : this.value.entrySet()) {
            metadata = metadata.set(
                nameAndValue.getKey(),
                Cast.to(nameAndValue.getValue())
            );
        }

        return metadata;
    }

    /**
     * Checks that all property values are valid or general and not specific to a single spreadsheet.
     */
    @Override
    SpreadsheetMetadata checkDefault() {
        final SpreadsheetMetadata defaults = this.defaults();
        if (defaults.isNotEmpty()) {
            throw new IllegalArgumentException("Default cannot have defaults: " + defaults);
        }

        final String invalid = this.value.keySet()
            .stream()
            .filter(SpreadsheetMetadataPropertyName::isNotDefaultProperty)
            .map(Object::toString)
            .sorted()
            .collect(Collectors.joining(", "));
        if (false == invalid.isEmpty()) {
            throw new IllegalArgumentException("Defaults includes invalid default values: " + invalid);
        }
        return this;
    }

    // get..............................................................................................................

    @Override
    <V> Optional<V> getIgnoringDefaults0(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return Optional.ofNullable(
            Cast.to(
                this.value.get(propertyName)
            )
        );
    }

    // set..............................................................................................................

    /**
     * Could be setting a property that has the same effective value but it could be in the defaults.
     * If the value is only in the defaults create a new instance with the value.
     */
    @Override
    <V> SpreadsheetMetadata setSameValue(final SpreadsheetMetadataPropertyName<V> propertyName,
                                         final V value) {
        SpreadsheetMetadata result = this;

        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = this.value;

        // save value anyway if previousValue was parse defaults.
        if (!properties.containsKey(propertyName)) {
            final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
            copy.putAll(properties);
            copy.put(propertyName, value);

            result = SpreadsheetMetadataNonEmpty.with(Maps.immutable(copy), this.defaults);
        }

        return result;
    }

    // remove...........................................................................................................

    @Override
    SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(this.value());
        final Object removed = copy.remove(propertyName);
        return null == removed ?
            this :
            this.removeNonEmpty(copy);
    }

    private SpreadsheetMetadata removeNonEmpty(final Map<SpreadsheetMetadataPropertyName<?>, Object> copy) {
        final SpreadsheetMetadata defaults = this.defaults;
        return copy.isEmpty() && null == defaults ?
            EMPTY :
            new SpreadsheetMetadataNonEmpty(Maps.immutable(copy), defaults);
    }

    // colorName........................................................................................................

    @Override
    public Optional<SpreadsheetColorName> colorName(final int colorNumber) {
        if (null == this.numberToColorName) {
            this.numberToColorName = SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor.numberToColorNameMap(this);
        }
        return Optional.ofNullable(
            this.numberToColorName.get(colorNumber)
        );
    }

    /**
     * Cached color number to {@link SpreadsheetColorName} lookup.
     */
    private Map<Integer, SpreadsheetColorName> numberToColorName;

    // ColorContext.....................................................................................................

    @Override
    public Optional<Color> lookupColor(final Color color) {
        Objects.requireNonNull(color, "color");

        Optional<Color> lookup = null;

        final boolean isIndexed = color.isIndexed();
        if (isIndexed || color.isNamed()) {
            final Integer index;

            if (isIndexed) {
                final IndexedColor indexedColor = (IndexedColor) color;
                index = indexedColor.index();
            } else {
                final NamedColor namedColor = (NamedColor) color;

                index = (Integer) this.value.get(
                    // ColorRED -> RED
                    SpreadsheetMetadataPropertyName.namedColor(
                        SpreadsheetColorName.with(
                            namedColor.value()
                        )
                    )
                );
            }

            if (null != index) {
                lookup = this.get(
                    SpreadsheetMetadataPropertyName.numberedColor(index)
                );
            } else {
                lookup = NO_LOOKUP_COLOR;
            }
        } else {
            lookup = Optional.of(color);
        }

        return lookup;
    }

    // HasMathContext...................................................................................................

    @Override
    public MathContext mathContext() {
        if (null == this.mathContext) {
            this.mathContext = this.mathContext0();
        }
        return this.mathContext;
    }

    /**
     * Cached {@link MathContext}.
     */
    private MathContext mathContext;

    // SpreadsheetMetadataVisitor.......................................................................................

    @Override
    void accept(final SpreadsheetMetadataVisitor visitor) {
        this.value()
            .entrySet()
            .forEach(visitor::acceptPropertyAndValue);
    }

    // JsonNodeContext..................................................................................................

    @Override
    void marshallProperties(final List<JsonNode> children,
                            final JsonNodeMarshallContext context) {
        for (final Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : this.value().entrySet()) {
            final SpreadsheetMetadataPropertyName<?> propertyName = propertyAndValue.getKey();
            final Object value = propertyAndValue.getValue();

            children.add(
                context.marshall(value)
                    .setName(propertyName.jsonPropertyName)
            );
        }
    }
}
