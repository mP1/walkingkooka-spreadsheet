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
import walkingkooka.tree.json.map.FromJsonNodeContext;
import walkingkooka.tree.json.map.ToJsonNodeContext;

import java.time.LocalDateTime;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link LocalDateTime} entries.
 */
final class SpreadsheetMetadataPropertyValueHandlerLocalDateTime extends SpreadsheetMetadataPropertyValueHandler<LocalDateTime> {

    /**
     * A singleton
     */
    static final SpreadsheetMetadataPropertyValueHandlerLocalDateTime INSTANCE = new SpreadsheetMetadataPropertyValueHandlerLocalDateTime();

    private SpreadsheetMetadataPropertyValueHandlerLocalDateTime() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, LocalDateTime.class, name);
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return LocalDateTime.class.getSimpleName();
    }

    @Override
    public String toString() {
        return LocalDateTime.class.getSimpleName();
    }

    // JsonNodeContext..................................................................................................

    @Override
    LocalDateTime fromJsonNode(final JsonNode node,
                               final SpreadsheetMetadataPropertyName<?> name,
                               final FromJsonNodeContext context) {
        return context.fromJsonNode(node, LocalDateTime.class);
    }

    @Override
    JsonNode toJsonNode(final LocalDateTime value,
                        final ToJsonNodeContext context) {
        return context.toJsonNode(value);
    }
}
