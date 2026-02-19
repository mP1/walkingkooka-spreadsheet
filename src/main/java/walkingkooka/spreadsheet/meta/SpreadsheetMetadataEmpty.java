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

import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetMetadata} with no properties and values.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final class SpreadsheetMetadataEmpty extends SpreadsheetMetadata {

    /**
     * Lazy singleton necessary to avoid race conditions to a init'd static field
     */
    static SpreadsheetMetadataEmpty instance() {
        if (null == instance) {
            instance = new SpreadsheetMetadataEmpty(null);
        }
        return instance;
    }

    private static SpreadsheetMetadataEmpty instance;

    /**
     * Private ctor
     */
    private SpreadsheetMetadataEmpty(final SpreadsheetMetadata defaults) {
        super(defaults);
    }

    // Value............................................................................................................

    @Override
    public Map<SpreadsheetMetadataPropertyName<?>, Object> value() {
        return Maps.empty();
    }

    // get/remove.......................................................................................................

    @Override
    <V> Optional<V> getIgnoringDefaults0(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return Optional.empty();
    }

    @Override
    <V> SpreadsheetMetadata setSameValue(final SpreadsheetMetadataPropertyName<V> propertyName,
                                         final V value) {
        return SpreadsheetMetadataNonEmpty.with(Maps.of(propertyName, value), this.defaults);
    }

    @Override
    SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName) {
        return this;
    }

    // setDefaults......................................................................................................

    @Override
    SpreadsheetMetadata replaceDefaults(final SpreadsheetMetadata metadata) {
        return new SpreadsheetMetadataEmpty(metadata.isEmpty() ? null : metadata); // will be null if original was empty
    }

    @Override
    SpreadsheetMetadata checkDefault() {
        return null; // null means empty when defaults
    }

    // getters..........................................................................................................

    @Override
    public Function<SpreadsheetColorName, Optional<Color>> nameToColor() {
        return this.nameToColor0(); // dont cache let factory fail.
    }

    @Override
    public Function<Integer, Optional<Color>> numberToColor() {
        return this.numberToColor0();
    }

    @Override
    public Function<Integer, Optional<SpreadsheetColorName>> numberToColorName() {
        return this.numberToColorName0();
    }

    @Override
    public MathContext mathContext() {
        return this.mathContext0();
    }

    // SpreadsheetMetadataVisitor........................................................................................

    @Override
    void accept(final SpreadsheetMetadataVisitor visitor) {
        // no properties
    }

    // JsonNodeContext..................................................................................................

    @Override
    void marshallProperties(final List<JsonNode> children,
                            final JsonNodeMarshallContext context) {
    }
}
