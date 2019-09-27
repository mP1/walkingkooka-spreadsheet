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

import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link String} entries, using the {@link Predicate} to
 * test valid {@link String values}.
 */
final class SpreadsheetMetadataPropertyValueHandlerString extends SpreadsheetMetadataPropertyValueHandler<String> {

    /**
     * Creates a new {@link SpreadsheetMetadataPropertyValueHandlerString}
     */
    static final SpreadsheetMetadataPropertyValueHandlerString with(final Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new SpreadsheetMetadataPropertyValueHandlerString(predicate);
    }

    private SpreadsheetMetadataPropertyValueHandlerString(final Predicate<String> predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final String string = this.checkType(value, String.class, name);
        if (false == this.predicate.test(string)) {
            throw new SpreadsheetMetadataPropertyValueException("Invalid value", name, string);
        }
    }

    private final Predicate<String> predicate;

    @Override
    String expectedTypeName(final Class<?> type) {
        return String.class.getSimpleName();
    }

    @Override
    public String toString() {
        return String.class.getSimpleName();
    }

    // JsonNodeContext..................................................................................................

    @Override
    String unmarshall(final JsonNode node,
                      final SpreadsheetMetadataPropertyName<?> name,
                      final JsonNodeUnmarshallContext context) {
        return context.unmarshall(node, String.class);
    }

    @Override
    JsonNode marshall(final String value,
                      final JsonNodeMarshallContext context) {
        return JsonNode.string(value);
    }
}
