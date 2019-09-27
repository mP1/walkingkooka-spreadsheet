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
import walkingkooka.color.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetMetadataPropertyValueHandlerColorTest extends SpreadsheetMetadataPropertyValueHandlerTestCase3<SpreadsheetMetadataPropertyValueHandlerColor, Color> {

    @Test
    public void testWith() {
        final SpreadsheetMetadataPropertyName<Color> name = SpreadsheetMetadataPropertyName.numberedColor(12);
        assertEquals("color-12", name.value(), "value");
    }

    @Test
    public void testCached() {
        for (int i = 0; i < SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER; i++) {
            assertSame(SpreadsheetMetadataPropertyName.numberedColor(i),
                    SpreadsheetMetadataPropertyName.numberedColor(i));
        }
    }

    @Test
    public void testWithUncached() {
        final int number = SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER + 1;
        final SpreadsheetMetadataPropertyName<Color> name = SpreadsheetMetadataPropertyName.numberedColor(number);
        assertEquals("color-" + number, name.value(), "value");
    }

    @Test
    public void testJsonNodeUnmarshall() {
        final Color color = this.propertyValue();
        this.unmarshallAndCheck(this.marshall(color), color);
    }

    @Test
    public void testJsonNodeMarshall() {
        final Color color = this.propertyValue();
        this.marshallAndCheck(color, this.marshall(color));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerColor handler() {
        return SpreadsheetMetadataPropertyValueHandlerColor.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<Color> propertyName() {
        return SpreadsheetMetadataPropertyName.numberedColor(12);
    }

    @Override
    Color propertyValue() {
        return Color.parse("#123abc");
    }

    @Override
    String propertyValueType() {
        return Color.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerColor> type() {
        return SpreadsheetMetadataPropertyValueHandlerColor.class;
    }
}
