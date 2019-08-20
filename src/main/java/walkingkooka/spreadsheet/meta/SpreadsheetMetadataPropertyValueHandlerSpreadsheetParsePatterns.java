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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePatterns;
import walkingkooka.tree.json.JsonNode;

/**
 * A base {@link SpreadsheetMetadataPropertyValueHandler} for all sub classes of {@link SpreadsheetParsePatterns patterns}.
 */
abstract class SpreadsheetMetadataPropertyValueHandlerSpreadsheetParsePatterns<P extends SpreadsheetParsePatterns<? extends SpreadsheetFormatParserToken>> extends SpreadsheetMetadataPropertyValueHandler<P> {

    SpreadsheetMetadataPropertyValueHandlerSpreadsheetParsePatterns() {
        super();
    }

    @Override
    final void check0(final Object value,
                      final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, this.valueType(), name);
    }

    @Override
    final String expectedTypeName(final Class<?> type) {
        return this.valueType().getSimpleName();
    }

    @Override
    final public String toString() {
        return this.valueType().getSimpleName();
    }

    @Override
    final P fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(this.valueType());
    }

    @Override
    final JsonNode toJsonNode(final P value) {
        return value.toJsonNode();
    }

    abstract Class<P> valueType();
}
