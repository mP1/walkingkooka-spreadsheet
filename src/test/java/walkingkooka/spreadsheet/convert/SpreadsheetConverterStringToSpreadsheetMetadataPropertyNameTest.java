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

public final class SpreadsheetConverterStringToSpreadsheetMetadataPropertyNameTest extends SpreadsheetConverterTestCase<SpreadsheetConverterStringToSpreadsheetMetadataPropertyName>
        implements SpreadsheetMetadataTesting {

    @Test
    public void testConvertEmptyString() {
        this.convertFails(
                SpreadsheetConverterStringToSpreadsheetMetadataPropertyName.INSTANCE,
                "",
                SpreadsheetMetadataPropertyName.class,
                this.createContext(),
                "Empty \"name\""
        );
    }

    @Test
    public void testConvertInvalidSpreadsheetMetadataPropertyName() {
        this.convertFails(
                SpreadsheetConverterStringToSpreadsheetMetadataPropertyName.INSTANCE,
                "!invalid",
                SpreadsheetMetadataPropertyName.class,
                this.createContext(),
                "Unknown metadata property name \"!invalid\""
        );
    }

    @Test
    public void testConvertNamedColor() {
        this.convertStringAndCheck(
                SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.RED)
        );
    }

    @Test
    public void testConvertNumberedColor() {
        this.convertStringAndCheck(
                SpreadsheetMetadataPropertyName.numberedColor(1)
        );
    }

    @Test
    public void testConvertPositiveSign() {
        this.convertStringAndCheck(
                SpreadsheetMetadataPropertyName.POSITIVE_SIGN
        );
    }

    @Test
    public void testConvertRoundingMode() {
        this.convertStringAndCheck(
                SpreadsheetMetadataPropertyName.ROUNDING_MODE
        );
    }

    private void convertStringAndCheck(final SpreadsheetMetadataPropertyName<?> propertyName) {
        this.convertAndCheck(
                propertyName.value(),
                propertyName
        );
    }

    @Override
    public SpreadsheetConverterStringToSpreadsheetMetadataPropertyName createConverter() {
        return SpreadsheetConverterStringToSpreadsheetMetadataPropertyName.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SPREADSHEET_FORMATTER_CONTEXT;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetConverterStringToSpreadsheetMetadataPropertyName.INSTANCE,
                "String to SpreadsheetMetadataPropertyName"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterStringToSpreadsheetMetadataPropertyName> type() {
        return SpreadsheetConverterStringToSpreadsheetMetadataPropertyName.class;
    }
}
