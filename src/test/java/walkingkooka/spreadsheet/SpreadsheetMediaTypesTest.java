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
            SpreadsheetMediaTypes.JSON_CELLS,
            "application/Spreadsheet-cell+json"
        );
    }

    @Test
    public void testJsonFormulas() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMULAS,
            "application/Spreadsheet-formula+json"
        );
    }

    @Test
    public void testJsonFormatters() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTERS,
            "application/Spreadsheet-formatter+json"
        );
    }

    @Test
    public void testJsonParsers() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_PARSERS,
            "application/Spreadsheet-parser+json"
        );
    }

    @Test
    public void testJsonStyles() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_STYLES,
            "application/Spreadsheet-style+json"
        );
    }

    @Test
    public void testJsonFormattedValue() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUES,
            "application/Spreadsheet-formatted-value+json"
        );
    }

    @Test
    public void testJsonValueTypes() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALUE_TYPE,
            "application/Spreadsheet-value-type+json"
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
