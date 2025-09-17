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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.HasName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorDirection;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * A pair that holds a {@link SpreadsheetComparatorName} and {@link SpreadsheetComparatorDirection}
 */
public final class SpreadsheetComparatorNameAndDirection implements HasName<SpreadsheetComparatorName> {

    final static char SEPARATOR_CHAR = ' ';

    public final static CharacterConstant SEPARATOR = CharacterConstant.with(SEPARATOR_CHAR);

    public static SpreadsheetComparatorNameAndDirection parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final SpreadsheetComparatorName name;
        final SpreadsheetComparatorDirection direction;

        final int space = text.lastIndexOf(SEPARATOR_CHAR);
        if (-1 != space) {
            name = SpreadsheetComparatorName.with(
                text.substring(0, space)
            );
            direction = SpreadsheetComparatorDirection.valueOf(
                text.substring(space + 1)
            );
        } else {
            name = SpreadsheetComparatorName.with(text);
            direction = SpreadsheetComparatorDirection.DEFAULT;
        }

        return new SpreadsheetComparatorNameAndDirection(
            name,
            direction
        );
    }

    public static SpreadsheetComparatorNameAndDirection with(final SpreadsheetComparatorName name,
                                                             final SpreadsheetComparatorDirection direction) {
        return new SpreadsheetComparatorNameAndDirection(
            Objects.requireNonNull(name, "name"),
            Objects.requireNonNull(direction, "direction")
        );
    }

    private SpreadsheetComparatorNameAndDirection(final SpreadsheetComparatorName name,
                                                  final SpreadsheetComparatorDirection direction) {
        this.name = name;
        this.direction = direction;
    }

    @Override
    public SpreadsheetComparatorName name() {
        return this.name;
    }

    private final SpreadsheetComparatorName name;

    public SpreadsheetComparatorDirection direction() {
        return this.direction;
    }

    private final SpreadsheetComparatorDirection direction;

    // SpreadsheetColumnOrRowSpreadsheetComparatorNames.................................................................

    /**
     * Setter that creates a new {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames}.
     */
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames setColumnOrRow(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
            columnOrRow,
            Lists.of(
                this
            )
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.name,
            this.direction
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetComparatorNameAndDirection &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetComparatorNameAndDirection other) {
        return this.name.equals(other.name) &&
            this.direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return this.name.value()
            .concat(this.direction.toStringWithEmptyDefault());
    }

    // Json.............................................................................................................

    static SpreadsheetComparatorNameAndDirection unmarshall(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetComparatorNameAndDirection.class),
            SpreadsheetComparatorNameAndDirection::unmarshall,
            SpreadsheetComparatorNameAndDirection::marshall,
            SpreadsheetComparatorNameAndDirection.class
        );
    }
}
