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
import java.util.function.IntPredicate;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} that only allows any integer value.
 */
final class SpreadsheetMetadataPropertyValueHandlerInteger extends SpreadsheetMetadataPropertyValueHandler<Integer> {

    /**
     * Creates a new {@link SpreadsheetMetadataPropertyValueHandlerInteger}
     */
    static final SpreadsheetMetadataPropertyValueHandlerInteger with(final IntPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new SpreadsheetMetadataPropertyValueHandlerInteger(predicate);
    }

    /**
     * Private ctor use singleton
     */
    private SpreadsheetMetadataPropertyValueHandlerInteger(final IntPredicate predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final Integer integer = this.checkType(value, Integer.class, name);
        if (false == this.predicate.test(integer)) {
            throw new SpreadsheetMetadataPropertyValueException("Invalid value", name, integer);
        }
    }

    private final IntPredicate predicate;

    @Override
    String expectedTypeName(final Class<?> type) {
        return Integer.class.getSimpleName();
    }

    @Override
    public String toString() {
        return "Integer";
    }

    // JsonNodeContext..................................................................................................

    @Override
    Integer unmarshall(final JsonNode node,
                       final SpreadsheetMetadataPropertyName<?> name,
                       final JsonNodeUnmarshallContext context) {
        return context.unmarshall(node, Integer.class);
    }

    @Override
    JsonNode marshall(final Integer value,
                      final JsonNodeMarshallContext context) {
        return context.marshall(value);
    }
}
