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
            "application/walkingkooka-spreadsheet-Cell+json"
        );
    }

    @Test
    public void testJsonDateTimeSymbols() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_DATE_TIME_SYMBOLS,
            "application/walkingkooka-spreadsheet-DateTimeSymbols+json"
        );
    }

    @Test
    public void testJsonDecimalNumberSymbols() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_DECIMAL_NUMBER_SYMBOLS,
            "application/walkingkooka-spreadsheet-DecimalNumberSymbols+json"
        );
    }

    @Test
    public void testJsonFormulas() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMULA,
            "application/walkingkooka-spreadsheet-Formula+json"
        );
    }

    @Test
    public void testJsonFormatters() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTER,
            "application/walkingkooka-spreadsheet-Formatter+json"
        );
    }

    @Test
    public void testJsonLocale() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_LOCALE,
            "application/walkingkooka-spreadsheet-Locale+json"
        );
    }

    @Test
    public void testJsonParsers() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_PARSER,
            "application/walkingkooka-spreadsheet-Parser+json"
        );
    }

    @Test
    public void testJsonStyles() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_STYLE,
            "application/walkingkooka-spreadsheet-Style+json"
        );
    }

    @Test
    public void testJsonFormattedValue() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUE,
            "application/walkingkooka-spreadsheet-Formatted-Value+json"
        );
    }

    @Test
    public void testJsonValidator() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALIDATOR,
            "application/walkingkooka-spreadsheet-Validator+json"
        );
    }

    @Test
    public void testJsonValueTypes() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALUE_TYPE,
            "application/walkingkooka-spreadsheet-Value-Type+json"
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
