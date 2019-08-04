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

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link String} entries, with no restrictions on the string content itself,
 * including empty.
 */
final class SpreadsheetMetadataPropertyValueHandlerString extends SpreadsheetMetadataPropertyValueHandler<String> {

    /**
     * A singleton
     */
    static final SpreadsheetMetadataPropertyValueHandlerString INSTANCE = new SpreadsheetMetadataPropertyValueHandlerString();

    private SpreadsheetMetadataPropertyValueHandlerString() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, String.class, name);
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return String.class.getSimpleName();
    }

    @Override
    public String toString() {
        return String.class.getSimpleName();
    }

    // HasJsonNode......................................................................................................

    @Override
    String fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(String.class);
    }

    @Override
    JsonNode toJsonNode(final String value) {
        return JsonNode.string(value);
    }
}
