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

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link String} entries which cannot be empty.
 */
final class SpreadsheetMetadataPropertyValueHandlerNonEmptyString extends SpreadsheetMetadataPropertyValueHandler<String> {

    /**
     * A singleton
     */
    static final SpreadsheetMetadataPropertyValueHandlerNonEmptyString INSTANCE = new SpreadsheetMetadataPropertyValueHandlerNonEmptyString();

    private SpreadsheetMetadataPropertyValueHandlerNonEmptyString() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final String string = this.checkType(value, String.class, name);
        if (string.isEmpty()) {
            throw new SpreadsheetMetadataPropertyValueException("Empty value", name, string);
        }
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return String.class.getSimpleName();
    }

    @Override
    public String toString() {
        return "NonEmpty" + String.class.getSimpleName();
    }

    // JsonNodeContext...................................................................................................

    @Override
    String fromJsonNode(final JsonNode node,
                        final SpreadsheetMetadataPropertyName<?> name,
                        final FromJsonNodeContext context) {
        return context.fromJsonNode(node, String.class);
    }

    @Override
    JsonNode toJsonNode(final String value,
                        final ToJsonNodeContext context) {
        return JsonNode.string(value);
    }
}
