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
import walkingkooka.naming.Name;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

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
    private static SpreadsheetMetadataPropertyName<EmailAddress> registerEmailAddressConstant(final String name,
                                                                                              final BiConsumer<EmailAddress, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.emailAddress(),
                visitor);
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles {@link LocalDateTime} metadata values.
     */
    private static SpreadsheetMetadataPropertyName<LocalDateTime> registerDateTimeConstant(final String name,
                                                                                           final BiConsumer<LocalDateTime, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.localDateTime(),
                visitor);
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles {@link Locale} metadata values.
     */
    private static SpreadsheetMetadataPropertyName<Locale> registerLocaleConstant(final String name,
                                                                                  final BiConsumer<Locale, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.locale(),
                visitor);
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles non empty string values.
     */
    private static SpreadsheetMetadataPropertyName<String> registerNonEmptyStringConstant(final String name,
                                                                                          final BiConsumer<String, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.nonEmpty(),
                visitor);
    }
    
    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles {@link SpreadsheetId} metadata values.
     */
    private static SpreadsheetMetadataPropertyName<SpreadsheetId> registerSpreadsheetIdConstant(final String name,
                                                                                                final BiConsumer<SpreadsheetId, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.spreadsheetId(),
                visitor);
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built that handles {@link SpreadsheetId} metadata values.
     */
    private static SpreadsheetMetadataPropertyName<String> registerStringConstant(final String name,
                                                                                  final BiConsumer<String, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name,
                SpreadsheetMetadataPropertyValueHandler.string(),
                visitor);
    }

    /**
     * Creates and adds a new {@link SpreadsheetMetadataPropertyName} to the cache being built.
     */
    private static <T> SpreadsheetMetadataPropertyName<T> registerConstant(final String name,
                                                                           final SpreadsheetMetadataPropertyValueHandler<T> handler,
                                                                           final BiConsumer<T, SpreadsheetMetadataVisitor> visitor) {
        final SpreadsheetMetadataPropertyName<T> spreadsheetMetadataName = new SpreadsheetMetadataPropertyName<T>(name,
                handler,
                visitor);
        SpreadsheetMetadataPropertyName.CONSTANTS.put(name, spreadsheetMetadataName);
        return spreadsheetMetadataName;
    }

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creator {@link EmailAddress}</code>
     */
    public final static SpreadsheetMetadataPropertyName<EmailAddress> CREATOR = registerEmailAddressConstant("creator",
            (e, v) -> v.visitCreator(e));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creation {@link LocalDateTime}</code>
     */
    public final static SpreadsheetMetadataPropertyName<LocalDateTime> CREATE_DATE_TIME = registerDateTimeConstant("create-date-time",
            (d, v) -> v.visitCreateDateTime(d));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>currency {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> CURRENCY_SYMBOL = registerNonEmptyStringConstant("currency-symbol",
            (c, v) -> v.visitCurrencySymbol(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>general decimal format pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> GENERAL_DECIMAL_FORMAT_PATTERN = registerStringConstant("general-decimal-format-pattern",
            (d, v) -> v.visitGeneralDecimalFormatPattern(d));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link Locale}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Locale> LOCALE = registerLocaleConstant("locale",
            (l, v) -> v.visitLocale(l));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>last modified by {@link EmailAddress}</code>
     */
    public final static SpreadsheetMetadataPropertyName<EmailAddress> MODIFIED_BY = registerEmailAddressConstant("modified-by",
            (e, v) -> v.visitModifiedBy(e));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>modified {@link LocalDateTime}</code>
     */
    public final static SpreadsheetMetadataPropertyName<LocalDateTime> MODIFIED_DATE_TIME = registerDateTimeConstant("modified-date-time",
            (d, v) -> v.visitModifiedDateTime(d));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-id {@link SpreadsheetId}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetId> SPREADSHEET_ID = registerSpreadsheetIdConstant("spreadsheet-id",
            (e, v) -> v.visitSpreadsheetId(e));

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
                                            final SpreadsheetMetadataPropertyValueHandler<T> handler,
                                            final BiConsumer<T, SpreadsheetMetadataVisitor> visitor) {
        super();
        this.name = name;
        this.inQuotes = CharSequences.quoteAndEscape(name).toString();
        this.jsonNodeName = JsonNodeName.with(name);

        this.handler = handler;

        this.visitor = visitor;
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

    // SpreadsheetMetadataVisitor.......................................................................................

    /**
     * Dispatches to the appropriate {@link SpreadsheetMetadataVisitor} visit method.
     */
    void accept(final Object value, final SpreadsheetMetadataVisitor visitor) {
        this.visitor.accept(Cast.to(value), visitor);
    }

    /**
     * Calls the appropriate {@link SpreadsheetMetadataVisitor} visit method
     */
    private final BiConsumer<T, SpreadsheetMetadataVisitor> visitor;
    
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
