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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.text.HasTextStyle;

public final class SpreadsheetConverterTextToSpreadsheetMetadataPropertyNameTest extends SpreadsheetConverterTestCase<SpreadsheetConverterTextToSpreadsheetMetadataPropertyName>
        implements SpreadsheetMetadataTesting {

    @Test
    public void testConvertWithNull() {
        this.convertFails(
                null,
                SpreadsheetMetadataPropertyName.class
        );
    }

    @Test
    public void testConvertWithInterfaceType() {
        this.convertFails(
                null,
                HasTextStyle.class
        );
    }

    @Test
    public void testConvertEmptyString() {
        this.convertFails(
                SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE,
                "",
                SpreadsheetMetadataPropertyName.class,
                this.createContext(),
                "Empty \"name\""
        );
    }

    @Test
    public void testConvertInvalidSpreadsheetMetadataPropertyName() {
        this.convertFails(
                SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE,
                "!invalid",
                SpreadsheetMetadataPropertyName.class,
                this.createContext(),
                "Unknown metadata property name \"!invalid\""
        );
    }

    @Test
    public void testConvertCharSequenceSpreadsheetMetadataPropertyNameNamedColor() {
        final SpreadsheetMetadataPropertyName<Integer> name = SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.RED);
        this.convertAndCheck(
                new StringBuilder(name.value()),
                name
        );
    }

    @Test
    public void testConvertStringNamedColor() {
        this.convertSpreadsheetMetadataPropertyNameAndCheck(
                SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.RED)
        );
    }

    @Test
    public void testConvertStringNumberedColor() {
        this.convertSpreadsheetMetadataPropertyNameAndCheck(
                SpreadsheetMetadataPropertyName.numberedColor(1)
        );
    }

    @Test
    public void testConvertStringRoundingMode() {
        this.convertSpreadsheetMetadataPropertyNameAndCheck(
                SpreadsheetMetadataPropertyName.ROUNDING_MODE
        );
    }

    private void convertSpreadsheetMetadataPropertyNameAndCheck(final SpreadsheetMetadataPropertyName<?> propertyName) {
        this.convertAndCheck(
                propertyName.value(),
                propertyName
        );
    }

    @Override
    public SpreadsheetConverterTextToSpreadsheetMetadataPropertyName createConverter() {
        return SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SPREADSHEET_FORMATTER_CONTEXT;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE,
                "String to SpreadsheetMetadataPropertyName"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterTextToSpreadsheetMetadataPropertyName> type() {
        return SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.class;
    }
}
