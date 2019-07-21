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

import org.junit.jupiter.api.Test;
import walkingkooka.tree.json.HasJsonNode;

public final class CharacterSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase<CharacterSpreadsheetMetadataPropertyValueHandler, Character> {

    @Test
    public void testFromJsonNode() {
        final Character character = this.propertyValue();
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(character), character);
    }

    @Test
    public void testToJsonNode() {
        final Character character = this.propertyValue();
        this.toJsonNodeAndCheck(character, HasJsonNode.toJsonNodeObject(character));
    }

    @Override
    CharacterSpreadsheetMetadataPropertyValueHandler handler() {
        return CharacterSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<Character> propertyName() {
        return SpreadsheetMetadataPropertyName.DECIMAL_POINT;
    }

    @Override
    Character propertyValue() {
        return '.';
    }

    @Override
    String propertyValueType() {
        return Character.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return Character.class.getSimpleName();
    }

    @Override
    public Class<CharacterSpreadsheetMetadataPropertyValueHandler> type() {
        return CharacterSpreadsheetMetadataPropertyValueHandler.class;
    }
}
