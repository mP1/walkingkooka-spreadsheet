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

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyNameNumberedColorTest extends SpreadsheetMetadataPropertyNameTestCase4<SpreadsheetMetadataPropertyNameNumberedColor> {

    @Test
    public void testWithNumberFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetMetadataPropertyName.numberedColor(-1));
    }

    @Test
    public void testConstants() {
        final Color color = Color.fromRgb(0);

        IntStream.range(0, SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER)
                .forEach(i -> {
                    final SpreadsheetMetadataPropertyNameNumberedColor propertyName = SpreadsheetMetadataPropertyNameNumberedColor.withNumber(i);
                    final String value = "color-" + i;
                    assertSame(propertyName, SpreadsheetMetadataPropertyName.with(value));

                    assertEquals(value, propertyName.value(), "value");

                    propertyName.checkValue(color);
                });
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameNumberedColor.withNumber(123), "color-123");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameNumberedColor> type() {
        return SpreadsheetMetadataPropertyNameNumberedColor.class;
    }
}
