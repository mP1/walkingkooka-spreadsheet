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

package walkingkooka.spreadsheet.parser;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * A typed {@link Optional} necessary because generic types are lost in java.
 * This class is intended to be a target when converting a cell to a parser to patch cells.
 */
public final class OptionalSpreadsheetParserSelector implements Value<Optional<SpreadsheetParserSelector>> {

    public final static OptionalSpreadsheetParserSelector EMPTY = new OptionalSpreadsheetParserSelector(Optional.empty());

    public static OptionalSpreadsheetParserSelector with(final Optional<SpreadsheetParserSelector> value) {
        Objects.requireNonNull(value, "value");

        return value.isPresent() ?
            new OptionalSpreadsheetParserSelector(value) :
            EMPTY;
    }

    private OptionalSpreadsheetParserSelector(final Optional<SpreadsheetParserSelector> value) {
        this.value = value;
    }

    @Override
    public Optional<SpreadsheetParserSelector> value() {
        return this.value;
    }

    private final Optional<SpreadsheetParserSelector> value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof OptionalSpreadsheetParserSelector &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final OptionalSpreadsheetParserSelector other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    // json.............................................................................................................

    static OptionalSpreadsheetParserSelector unmarshall(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallOptional(
                node,
                SpreadsheetParserSelector.class
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallOptional(this.value);
    }

    static {
        SpreadsheetParserName.DATE_PARSER_PATTERN.setValueText("yyyy/mm/ddd");

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(OptionalSpreadsheetParserSelector.class),
            OptionalSpreadsheetParserSelector::unmarshall,
            OptionalSpreadsheetParserSelector::marshall,
            OptionalSpreadsheetParserSelector.class
        );
    }
}
