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
import walkingkooka.spreadsheet.color.SpreadsheetColors;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyNameNumberedColorTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameNumberedColor, Color> {

    @Test
    public void testWithNumberLessThanMinFails() {
        this.withFails(
            SpreadsheetColors.MIN - 1,
            "color number 0 < 1 or > 56"
        );
    }

    @Test
    public void testWithNumberGreaterThanMaxFails() {
        this.withFails(
            SpreadsheetColors.MAX + 1,
            "color number 57 < 1 or > 56"
        );
    }

    private void withFails(final int value,
                           final String message) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetMetadataPropertyName.numberedColor(value)
        );
        this.checkEquals(
            message,
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testWithNumberMin() {
        SpreadsheetMetadataPropertyNameNumberedColor.withNumber(SpreadsheetColors.MIN);
    }

    @Test
    public void testWithNumberMax() {
        SpreadsheetMetadataPropertyNameNumberedColor.withNumber(SpreadsheetColors.MIN);
    }

    @Test
    public void testConstants() {
        final Color color = Color.fromRgb(0);

        IntStream.range(SpreadsheetColors.MIN, SpreadsheetColors.MAX)
            .forEach(i -> {
                final SpreadsheetMetadataPropertyNameNumberedColor propertyName = SpreadsheetMetadataPropertyNameNumberedColor.withNumber(i);
                final String value = "color" + i;
                assertSame(propertyName, SpreadsheetMetadataPropertyName.with(value));

                this.checkEquals(value, propertyName.value(), "value");

                propertyName.checkValue(color);
            });
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameNumberedColor.withNumber(12),
            "color12"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameNumberedColor createName() {
        return SpreadsheetMetadataPropertyNameNumberedColor.withNumber(12);
    }

    @Override
    Color propertyValue() {
        return Color.fromArgb(0xffddaa);
    }

    @Override
    String propertyValueType() {
        return Color.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameNumberedColor> type() {
        return SpreadsheetMetadataPropertyNameNumberedColor.class;
    }
}
