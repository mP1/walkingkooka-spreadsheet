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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.HasId;
import walkingkooka.Value;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * The name of a spreadsheet. Every name must start with a letter, followed by letter/digits or space or dot or dollar or ampersand or at-sign.
 * TODO finalize actual valid names in future.
 */
public final class SpreadsheetName implements Comparable<SpreadsheetName>,
        Name,
        HasId<String>,
        Value<String> {

    /**
     * Creates a new {@link SpreadsheetName} after vaildating only supported characters are entered.
     */
    public static SpreadsheetName with(final String value) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(value, "name", INITIAL, PART);
        return new SpreadsheetName(value);
    }

    private final static CharPredicate INITIAL = Character::isAlphabetic;
    private final static CharPredicate PART = INITIAL.or(Character::isDigit).or(SpreadsheetName::contains);

    /**
     * Returns true if the character is a space and a few other useful characters.
     */
    private static boolean contains(final char c) {
        return -1 != " .-$&@".indexOf(c);
    }

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

    private String value;

    // JsonNodeContext..................................................................................................

    static SpreadsheetName unmarshall(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        return with(node.stringValueOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register("spreadsheet-name",
                SpreadsheetName::unmarshall,
                SpreadsheetName::marshall,
                SpreadsheetName.class);
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
