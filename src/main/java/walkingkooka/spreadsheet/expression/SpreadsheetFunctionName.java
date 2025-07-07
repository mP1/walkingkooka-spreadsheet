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

package walkingkooka.spreadsheet.expression;

import walkingkooka.Cast;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * The {@link Name} of a function within a spreadsheet formula expression.
 */
final public class SpreadsheetFunctionName implements Name, Comparable<SpreadsheetFunctionName> {

    /**
     * {@link CharPredicate} that may be used to match the first valid character of a {@link SpreadsheetFunctionName}.
     */
    public final static CharPredicate INITIAL = CharPredicates.range('A', 'Z')
        .or(
            CharPredicates.range('a', 'z')
        );

    /**
     * {@link CharPredicate} that may be used to match the non first valid character of a {@link SpreadsheetFunctionName}.
     */
    public final static CharPredicate PART = INITIAL.or(
        CharPredicates.range('0', '9')
            .or(
                CharPredicates.is('.')
            )
    );

    /**
     * The minimum valid length
     */
    public final static int MIN_LENGTH = 1;

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link SpreadsheetFunctionName}
     */
    public static SpreadsheetFunctionName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
            name,
            SpreadsheetFunctionName.class.getSimpleName(),
            INITIAL,
            PART
        );

        InvalidTextLengthException.throwIfFail(
            "function name",
            name,
            MIN_LENGTH,
            MAX_LENGTH
        );

        return new SpreadsheetFunctionName(name);
    }

    /**
     * Private constructor
     */
    private SpreadsheetFunctionName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // toExpressionFunctionName.........................................................................................

    /**
     * Returns a {@link ExpressionFunctionName}.
     */
    public ExpressionFunctionName toExpressionFunctionName() {
        return SpreadsheetExpressionFunctions.name(this.name);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetFunctionName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFunctionName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final SpreadsheetFunctionName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;

    // Json.............................................................................................................

    static SpreadsheetFunctionName unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFunctionName.class),
            SpreadsheetFunctionName::unmarshall,
            SpreadsheetFunctionName::marshall,
            SpreadsheetFunctionName.class
        );
    }
}
