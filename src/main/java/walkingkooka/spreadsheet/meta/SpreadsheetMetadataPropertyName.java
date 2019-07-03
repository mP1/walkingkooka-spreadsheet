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
import walkingkooka.naming.Name;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The {@link Name} of metadata property, used to fetch a value given a name.
 */
final public class SpreadsheetMetadataPropertyName<T> implements Name, Comparable<SpreadsheetMetadataPropertyName<?>> {

    // constants

    final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    /**
     * A read only cache of already prepared {@link SpreadsheetMetadataPropertyName names}..
     */
    final static Map<String, SpreadsheetMetadataPropertyName<?>> CONSTANTS = Maps.sorted(SpreadsheetMetadataPropertyName.CASE_SENSITIVITY.comparator());

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles {@link EmailAddress} metadata values.
     */
    private static SpreadsheetMetadataPropertyName<EmailAddress> registerEmailAddressConstant(final String name) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.emailAddress());
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles {@link LocalDateTime} metadata values.
     */
    private static SpreadsheetMetadataPropertyName<LocalDateTime> registerDateTimeConstant(final String name) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.localDateTime());
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built.
     */
    private static <T> SpreadsheetMetadataPropertyName<T> registerConstant(final String name,
                                                                           final SpreadsheetMetadataPropertyValueHandler<T> handler) {
        final SpreadsheetMetadataPropertyName<T> spreadsheetMetadataName = new SpreadsheetMetadataPropertyName<T>(name, handler);
        SpreadsheetMetadataPropertyName.CONSTANTS.put(name, spreadsheetMetadataName);
        return spreadsheetMetadataName;
    }

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creator {@link EmailAddress}</code>
     */
    public final static SpreadsheetMetadataPropertyName<EmailAddress> CREATOR = registerEmailAddressConstant("creator");

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creation {@link LocalDateTime}</code>
     */
    public final static SpreadsheetMetadataPropertyName<LocalDateTime> CREATE_DATE_TIME = registerDateTimeConstant("create-date-time");

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>last modified by {@link EmailAddress}</code>
     */
    public final static SpreadsheetMetadataPropertyName<EmailAddress> MODIFIED_BY = registerEmailAddressConstant("modified-by");

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>modified {@link LocalDateTime}</code>
     */
    public final static SpreadsheetMetadataPropertyName<LocalDateTime> MODIFIED_DATE_TIME = registerDateTimeConstant("modified-date-time");

    /**
     * Factory that assumnes a valid {@link SpreadsheetMetadataPropertyName} or fails.
     */
    public static SpreadsheetMetadataPropertyName with(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        final SpreadsheetMetadataPropertyName propertyName = CONSTANTS.get(name);
        if (null == propertyName) {
            throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(name));
        }
        return propertyName;
    }

    /**
     * Private constructor use factory.
     */
    private SpreadsheetMetadataPropertyName(final String name,
                                            final SpreadsheetMetadataPropertyValueHandler<T> handler) {
        super();
        this.name = name;
        this.inQuotes = CharSequences.quoteAndEscape(name).toString();
        this.jsonNodeName = JsonNodeName.with(name);

        this.handler = handler;
    }

    @Override
    public final String value() {
        return this.name;
    }

    final String name;

    final String inQuotes;

    final JsonNodeName jsonNodeName;

    /**
     * Validates the value.
     */
    public T checkValue(final Object value) {
        return this.handler.check(value, this);
    }

    final SpreadsheetMetadataPropertyValueHandler<T> handler;

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.caseSensitivity().hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetMetadataPropertyName &&
                        this.equals0((SpreadsheetMetadataPropertyName) other);
    }

    private boolean equals0(final SpreadsheetMetadataPropertyName other) {
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
    public final CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetMetadataPropertyName<?> other) {
        return this.caseSensitivity().comparator().compare(this.name, other.name);
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that retrieves a {@link SpreadsheetMetadataPropertyName} from a {@link JsonNode#name()}.
     */
    static SpreadsheetMetadataPropertyName<?> fromJsonNodeName(final JsonNode node) {
        return CONSTANTS.get(node.name().value());
    }
}
