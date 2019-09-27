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

public final class SpreadsheetMetadataPropertyValueHandlerCharacterTest extends SpreadsheetMetadataPropertyValueHandlerTestCase3<SpreadsheetMetadataPropertyValueHandlerCharacter, Character> {

    @Test
    public void testJsonNodeUnmarshall() {
        final Character character = this.propertyValue();
        this.unmarshallAndCheck(this.marshall(character), character);
    }

    @Test
    public void testJsonNodeMarshall() {
        final Character character = this.propertyValue();
        this.marshallAndCheck(character, this.marshall(character));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerCharacter handler() {
        return SpreadsheetMetadataPropertyValueHandlerCharacter.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<Character> propertyName() {
        return SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
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
    public Class<SpreadsheetMetadataPropertyValueHandlerCharacter> type() {
        return SpreadsheetMetadataPropertyValueHandlerCharacter.class;
    }
}
