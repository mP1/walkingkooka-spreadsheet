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

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link SpreadsheetId} entries.
 */
final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetId extends SpreadsheetMetadataPropertyValueHandler<SpreadsheetId> {

    /**
     * A singleton
     */
    static final SpreadsheetMetadataPropertyValueHandlerSpreadsheetId INSTANCE = new SpreadsheetMetadataPropertyValueHandlerSpreadsheetId();

    private SpreadsheetMetadataPropertyValueHandlerSpreadsheetId() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, SpreadsheetId.class, name);
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return SpreadsheetId.class.getSimpleName();
    }

    @Override
    public String toString() {
        return SpreadsheetId.class.getSimpleName();
    }

    // HasJsonNode......................................................................................................

    @Override
    SpreadsheetId fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(SpreadsheetId.class);
    }

    @Override
    JsonNode toJsonNode(final SpreadsheetId value) {
        return value.toJsonNode();
    }
}
