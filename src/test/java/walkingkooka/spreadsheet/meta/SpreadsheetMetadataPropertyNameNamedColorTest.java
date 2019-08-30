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
import walkingkooka.spreadsheet.format.SpreadsheetColorName;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyNameNamedColorTest extends SpreadsheetMetadataPropertyNameTestCase4<SpreadsheetMetadataPropertyNameNamedColor> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetMetadataPropertyNameNamedColor.withColorName(null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetColorName colorName = this.colorName();
        final SpreadsheetMetadataPropertyNameNamedColor property = SpreadsheetMetadataPropertyNameNamedColor.withColorName(colorName);
        assertSame(colorName, property.name, "colorName");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameNamedColor.withColorName(this.colorName()), "color-dull");
    }

    private SpreadsheetColorName colorName() {
        return SpreadsheetColorName.with("dull");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameNamedColor> type() {
        return SpreadsheetMetadataPropertyNameNamedColor.class;
    }
}
