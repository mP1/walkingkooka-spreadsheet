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
import walkingkooka.HasId;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.Value;
import walkingkooka.naming.Name;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * The name of a spreadsheet. Only printable characters may be used.
 */
public final class SpreadsheetName implements Comparable<SpreadsheetName>,
    Name,
    HasId<String>,
    Value<String>,
    HasUrlFragment {

    /**
     * Creates a new {@link SpreadsheetName} after vaildating only printable characters are used.
     */
    public static SpreadsheetName with(final String value) {
        CharPredicates.printable()
            .failIfNullOrEmptyOrFalse(
                "name",
                value
            );

        final int length = value.length();
        if (length >= MAX_LENGTH) {
            throw new InvalidTextLengthException("Spreadsheet name", value, 1, MAX_LENGTH);
        }
        return new SpreadsheetName(value);
    }

    private final static int MAX_LENGTH = 256;

    private SpreadsheetName(final String value) {
        super();

        this.value = value;
    }

    // Name.............................................................................................................

    /**
     * Spreadsheet names are case sensitive.
     */
    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    // HateosResource ....................................................................................................

    @Override
    public String id() {
        return this.value();
    }

    //@Override
    public String hateosLinkId() {
        return this.toString();
    }

    // Value ....................................................................................................

    @Override
    public String value() {
        return this.value;
    }

    private final String value;

    // HasUrlFragment...................................................................................................

    // spreadsheetName123
    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(this.toString());
    }

    // JsonNodeContext..................................................................................................

    static SpreadsheetName unmarshall(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetName.class),
            SpreadsheetName::unmarshall,
            SpreadsheetName::marshall,
            SpreadsheetName.class
        );
    }

    // Object............................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetName other) {
        return this.compareTo(other) == 0;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetName other) {
        return this.value.compareTo(other.value);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.value();
    }
}
