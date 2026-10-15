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
import walkingkooka.color.NamedColor;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyNameIntegerNamedColorTest extends SpreadsheetMetadataPropertyNameIntegerTestCase<SpreadsheetMetadataPropertyNameIntegerNamedColor> {

    private final static NamedColor NAMED_COLOR = Color.named("dull");

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataPropertyNameIntegerNamedColor.withNamedColor(null)
        );
    }

    @Test
    public void testWithInvalidNamedColorFails() {
        final NamedColor namedColor = Color.named("Invalid1");

        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetMetadataPropertyNameIntegerNamedColor.withNamedColor(namedColor)
            ),
            "Invalid character '1' at 7"
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetMetadataPropertyNameIntegerNamedColor property = SpreadsheetMetadataPropertyNameIntegerNamedColor.withNamedColor(NAMED_COLOR);
        assertSame(
            NAMED_COLOR,
            property.name,
            "colorName"
        );
    }

    @Test
    public void testCheckValueWithColorNumberZeroFails() {
        this.checkValueFails(
            0,
            "Metadata colordull=0, Invalid color number 0 < 1 or > 56"
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameIntegerNamedColor.withNamedColor(NAMED_COLOR),
            "colordull"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameIntegerNamedColor createName() {
        return SpreadsheetMetadataPropertyNameIntegerNamedColor.withNamedColor(NAMED_COLOR);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameIntegerNamedColor> type() {
        return SpreadsheetMetadataPropertyNameIntegerNamedColor.class;
    }
}
