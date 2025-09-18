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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.lang.reflect.Method;

public final class SpreadsheetMediaTypesTest implements PublicStaticHelperTesting<SpreadsheetMediaTypes> {

    // json.............................................................................................................

    @Test
    public void testJsonCells() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_CELL,
            "application/json+walkingkooka-spreadsheet-Cell"
        );
    }

    @Test
    public void testJsonDateTimeSymbols() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_DATE_TIME_SYMBOLS,
            "application/json+walkingkooka-spreadsheet-DateTimeSymbols"
        );
    }

    @Test
    public void testJsonDecimalNumberSymbols() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_DECIMAL_NUMBER_SYMBOLS,
            "application/json+walkingkooka-spreadsheet-DecimalNumberSymbols"
        );
    }

    @Test
    public void testJsonFormulas() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMULA,
            "application/json+walkingkooka-spreadsheet-Formula"
        );
    }

    @Test
    public void testJsonFormatters() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTER,
            "application/json+walkingkooka-spreadsheet-Formatter"
        );
    }

    @Test
    public void testJsonLocale() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_LOCALE,
            "application/json+walkingkooka-spreadsheet-Locale"
        );
    }

    @Test
    public void testJsonParsers() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_PARSER,
            "application/json+walkingkooka-spreadsheet-Parser"
        );
    }

    @Test
    public void testJsonStyles() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_STYLE,
            "application/json+walkingkooka-spreadsheet-Style"
        );
    }

    @Test
    public void testJsonFormattedValue() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUE,
            "application/json+walkingkooka-spreadsheet-Formatted-Value"
        );
    }

    @Test
    public void testJsonValidator() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALIDATOR,
            "application/json+walkingkooka-spreadsheet-Validator"
        );
    }

    @Test
    public void testJsonValue() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALUE,
            "application/json+walkingkooka-spreadsheet-Value"
        );
    }

    @Test
    public void testJsonValueTypes() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALUE_TYPE,
            "application/json+walkingkooka-spreadsheet-Value-Type"
        );
    }

    // object...........................................................................................................

    @Test
    public void testObjectCell() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.OBJECT_CELL,
            "application/memory+walkingkooka-spreadsheet-Cell"
        );
    }

    @Test
    public void testObjectLabel() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.OBJECT_LABEL,
            "application/memory+walkingkooka-spreadsheet-Label"
        );
    }

    @Test
    public void testObjectMetadata() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.OBJECT_SPREADSHEET_METADATA,
            "application/memory+walkingkooka-spreadsheet-Metadata"
        );
    }

    // helper...........................................................................................................

    private void mediaTypeAndCheck(final MediaType mediaType,
                                   final String expected) {
        this.mediaTypeAndCheck(
            mediaType,
            MediaType.parse(expected)
        );
    }

    private void mediaTypeAndCheck(final MediaType mediaType,
                                   final MediaType expected) {
        this.checkEquals(
            expected,
            mediaType
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetMediaTypes> type() {
        return SpreadsheetMediaTypes.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
