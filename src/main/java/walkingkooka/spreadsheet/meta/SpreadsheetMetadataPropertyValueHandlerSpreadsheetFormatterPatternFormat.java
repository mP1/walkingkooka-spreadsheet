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

import walkingkooka.spreadsheet.format.SpreadsheetFormatterPattern;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for valid {@link SpreadsheetFormatterPattern} patterns.
 */
final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat extends SpreadsheetMetadataPropertyValueHandler<SpreadsheetFormatterPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat INSTANCE = new SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat() {
        super();
    }

    @Override
    final void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormatSpreadsheetFormatParserTokenVisitor.check(name,
                this.checkType(value, SpreadsheetFormatterPattern.class, name));
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return SpreadsheetFormatterPattern.class.getSimpleName();
    }

    // ToString.........................................................................................................

    @Override
    public final String toString() {
        return SpreadsheetFormatterPattern.class.getSimpleName();
    }

    // HasJsonNode......................................................................................................

    @Override
    final SpreadsheetFormatterPattern fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(SpreadsheetFormatterPattern.class);
    }

    @Override
    final JsonNode toJsonNode(final SpreadsheetFormatterPattern value) {
        return value.toJsonNode();
    }
}
