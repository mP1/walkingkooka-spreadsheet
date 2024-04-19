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

package walkingkooka.spreadsheet.compare;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * The {@link Name} of a {@link SpreadsheetComparator}. Note comparator names are case sensitive.
 */
final public class SpreadsheetComparatorName implements Name, Comparable<SpreadsheetComparatorName> {

    private final static CharPredicate INITIAL = CharPredicates.range('A', 'Z')
            .or(CharPredicates.range('a', 'z'));

    private final static CharPredicate PART = INITIAL.or(CharPredicates.range('0', '9'))
            .or(CharPredicates.is('-'));

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link SpreadsheetComparatorName}
     */
    public static SpreadsheetComparatorName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
                name,
                SpreadsheetComparatorName.class.getSimpleName(),
                INITIAL,
                PART
        );

        final int length = name.length();
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException("Name length " + length + " > " + MAX_LENGTH);
        }

        return new SpreadsheetComparatorName(name);
    }

    /**
     * Private constructor
     */
    private SpreadsheetComparatorName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Create a {@link SpreadsheetComparatorNameAndDirection} using this name and the given {@link SpreadsheetComparatorDirection direction}
     */
    public SpreadsheetComparatorNameAndDirection setDirection(final SpreadsheetComparatorDirection direction) {
        return SpreadsheetComparatorNameAndDirection.with(
                this,
                direction
        );
    }

    // Object..................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetComparatorName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetComparatorName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final SpreadsheetComparatorName other) {
        return CASE_SENSITIVITY.comparator()
                .compare(
                        this.name,
                        other.name
                );
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    // Json.............................................................................................................

    static SpreadsheetComparatorName unmarshall(final JsonNode node,
                                                final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetComparatorName.class),
                SpreadsheetComparatorName::unmarshall,
                SpreadsheetComparatorName::marshall,
                SpreadsheetComparatorName.class
        );
    }
}
