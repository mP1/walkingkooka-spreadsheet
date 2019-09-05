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

import walkingkooka.convert.Converters;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for date time offset entries.
 */
final class SpreadsheetMetadataPropertyValueHandlerLong extends SpreadsheetMetadataPropertyValueHandler<Long> {

    /**
     * Creates a new {@link SpreadsheetMetadataPropertyValueHandlerLong}
     */
    static final SpreadsheetMetadataPropertyValueHandlerLong with(final LongPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new SpreadsheetMetadataPropertyValueHandlerLong(predicate);
    }

    /**
     * Private ctor use singleton
     */
    private SpreadsheetMetadataPropertyValueHandlerLong(final LongPredicate predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final Long longValue = this.checkType(value, Long.class, name);
        if (false == this.predicate.test(longValue)) {
            throw new SpreadsheetMetadataPropertyValueException("Invalid value", name, longValue);
        }
    }

    private final LongPredicate predicate;

    @Override
    String expectedTypeName(final Class<?> type) {
        return Long.class.getSimpleName();
    }

    @Override
    public String toString() {
        return Long.class.getSimpleName();
    }

    // JsonNodeContext..................................................................................................

    @Override
    Long fromJsonNode(final JsonNode node,
                      final SpreadsheetMetadataPropertyName<?> name,
                      final FromJsonNodeContext context) {
        return context.fromJsonNode(node, Long.class);
    }

    @Override
    JsonNode toJsonNode(final Long value,
                        final ToJsonNodeContext context) {
        return context.toJsonNode(value);
    }
}
