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
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.FromJsonNodeException;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetMetadata} holds a {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 */
public abstract class SpreadsheetMetadata implements HashCodeEqualsDefined,
        HasJsonNode,
        HasHateosLinkId,
        HateosResource<Optional<SpreadsheetId>>,
        Value<Map<SpreadsheetMetadataPropertyName<?>, Object>> {

    /**
     * A {@link SpreadsheetMetadata} with no textStyle.
     */
    public final static SpreadsheetMetadata EMPTY = EmptySpreadsheetMetadata.instance();

    /**
     * Factory that creates a {@link SpreadsheetMetadata} from a {@link Map}.
     */
    public static SpreadsheetMetadata with(final Map<SpreadsheetMetadataPropertyName<?>, Object> value) {
        return withSpreadsheetMetadataMap(SpreadsheetMetadataMap.with(value));
    }

    static SpreadsheetMetadata withSpreadsheetMetadataMap(final SpreadsheetMetadataMap map) {
        return map.isEmpty() ?
                EMPTY :
                NonEmptySpreadsheetMetadata.with(map);
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

    // SpreadsheetMetadataStyleVisitor..................................................................................

    abstract void accept(final SpreadsheetMetadataVisitor visitor);

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

        try {
            return fromJson0(node.objectOrFail());
        } catch (final FromJsonNodeException cause) {
            throw cause;
        } catch (final RuntimeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
        }
    }

    private static SpreadsheetMetadata fromJson0(final JsonObjectNode json) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

        for (JsonNode child : json.children()) {
            final SpreadsheetMetadataPropertyName<?> name = SpreadsheetMetadataPropertyName.fromJsonNodeName(child);
            properties.put(name,
                    name.handler.fromJsonNode(child, name));
        }

        return with(properties);
    }

    static {
        HasJsonNode.register("metadata", SpreadsheetMetadata::fromJsonNode, SpreadsheetMetadata.class,
                NonEmptySpreadsheetMetadata.class,
                EmptySpreadsheetMetadata.class);
    }
}
