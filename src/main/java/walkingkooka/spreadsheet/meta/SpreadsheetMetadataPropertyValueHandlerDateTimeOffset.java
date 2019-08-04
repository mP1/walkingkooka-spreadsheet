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
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for date time offset entries.
 */
final class SpreadsheetMetadataPropertyValueHandlerDateTimeOffset extends SpreadsheetMetadataPropertyValueHandler<Long> {

    /**
     * A singleton
     */
    static final SpreadsheetMetadataPropertyValueHandlerDateTimeOffset INSTANCE = new SpreadsheetMetadataPropertyValueHandlerDateTimeOffset();

    private SpreadsheetMetadataPropertyValueHandlerDateTimeOffset() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final Long longValue = this.checkType(value, Long.class, name);
        for (; ; ) {
            if (Converters.JAVA_EPOCH_OFFSET == longValue) {
                break;
            }
            if (Converters.EXCEL_OFFSET == longValue) {
                break;
            }
            throw new SpreadsheetMetadataPropertyValueException("Invalid date time offset " + value, name, value);
        }
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return Long.class.getSimpleName();
    }

    @Override
    public String toString() {
        return "DateTimeOffset";
    }

    // HasJsonNode......................................................................................................

    @Override
    Long fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(Long.class);
    }

    @Override
    JsonNode toJsonNode(final Long value) {
        return HasJsonNode.toJsonNodeObject(value);
    }
}
