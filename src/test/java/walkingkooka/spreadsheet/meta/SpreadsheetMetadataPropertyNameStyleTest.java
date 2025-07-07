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
import walkingkooka.tree.text.FontFamily;
import walkingkooka.tree.text.FontSize;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

public final class SpreadsheetMetadataPropertyNameStyleTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameStyle, TextStyle> {

    @Test
    public void testCheckValueEmptyTextStyle() {
        this.checkValue(TextStyle.EMPTY);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameStyle.instance(), "style");
    }

    @Override
    SpreadsheetMetadataPropertyNameStyle createName() {
        return SpreadsheetMetadataPropertyNameStyle.instance();
    }

    @Override
    TextStyle propertyValue() {
        return TextStyle.EMPTY
            .set(TextStylePropertyName.FONT_FAMILY, FontFamily.with("Times"))
            .set(TextStylePropertyName.FONT_SIZE, FontSize.with(10))
            .set(TextStylePropertyName.HEIGHT, Length.pixel(12.0))
            .set(TextStylePropertyName.WIDTH, Length.pixel(100.0));
    }

    @Override
    String propertyValueType() {
        return TextStyle.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameStyle> type() {
        return SpreadsheetMetadataPropertyNameStyle.class;
    }
}
