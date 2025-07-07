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

package walkingkooka.spreadsheet.security;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.Predicates;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The name of a group.
 */
final public class GroupName implements Name,
    Comparable<GroupName> {

    private final static CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));

    private final static CharPredicate INITIAL = LETTER;

    private final static CharPredicate DIGIT = CharPredicates.range('0', '9');

    private final static CharPredicate PART = INITIAL.or(DIGIT.or(CharPredicates.is('-')));

    private final static Predicate<CharSequence> PREDICATE = Predicates.initialAndPart(INITIAL, PART);

    private final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link GroupName}
     */
    public static GroupName with(final String name) {
        Objects.requireNonNull(name, "name");

        if (!isAcceptableLength(name)) {
            throw new IllegalArgumentException("Name length " + name.length() + " is greater than allowed " + MAX_LENGTH);
        }

        if (!PREDICATE.test(name)) {
            throw new IllegalArgumentException("Name contains invalid character(s)=" + CharSequences.quote(name));
        }

        return new GroupName(name);
    }

    private static boolean isAcceptableLength(final String name) {
        return name.length() < MAX_LENGTH;
    }

    /**
     * Private constructor
     */
    private GroupName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // JsonNodeContext..................................................................................................

    /**
     * Accepts a json string holding the name string
     */
    static GroupName unmarshall(final JsonNode node,
                                final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(GroupName.class),
            GroupName::unmarshall,
            GroupName::marshall,
            GroupName.class
        );
    }

    // Object...........................................................................................................

    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof GroupName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final GroupName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final GroupName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;
}
