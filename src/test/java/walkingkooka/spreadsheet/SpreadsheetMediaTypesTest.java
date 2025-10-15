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
            "application/json+walkingkooka.spreadsheet.SpreadsheetCell"
        );
    }

    @Test
    public void testJsonDateTimeSymbols() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_DATE_TIME_SYMBOLS,
            "application/json+walkingkooka.datetime.DateTimeSymbols"
        );
    }

    @Test
    public void testJsonDecimalNumberSymbols() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_DECIMAL_NUMBER_SYMBOLS,
            "application/json+walkingkooka.math.DecimalNumberSymbols"
        );
    }

    @Test
    public void testJsonFormula() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMULA,
            "application/json+walkingkooka.spreadsheet.formula.SpreadsheetFormula"
        );
    }

    @Test
    public void testJsonFormatters() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTER,
            "application/json+walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector"
        );
    }

    @Test
    public void testJsonLocale() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_LOCALE,
            "application/json+java.util.Locale"
        );
    }

    @Test
    public void testJsonParser() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_PARSER,
            "application/json+walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector"
        );
    }

    @Test
    public void testJsonStyle() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_STYLE,
            "application/json+walkingkooka.tree.text.TextStyle"
        );
    }

    @Test
    public void testJsonFormattedValue() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_FORMATTED_VALUE,
            "application/json+walkingkooka.tree.text.TextNode"
        );
    }

    @Test
    public void testJsonValidator() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALIDATOR,
            "application/json+walkingkooka.validation.Validator"
        );
    }

    @Test
    public void testJsonValue() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALUE,
            "application/json+java.lang.Object"
        );
    }

    @Test
    public void testJsonValueType() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.JSON_VALUE_TYPE,
            "application/json+walkingkooka.validation.ValueTypeName"
        );
    }

    // object...........................................................................................................

    @Test
    public void testObjectCell() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.MEMORY_CELL,
            "application/memory+walkingkooka.spreadsheet.SpreadsheetCell"
        );
    }

    @Test
    public void testObjectLabel() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.MEMORY_LABEL,
            "application/memory+walkingkooka.spreadsheet.reference.SpreadsheetLabelName"
        );
    }

    @Test
    public void testObjectMetadata() {
        this.mediaTypeAndCheck(
            SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA,
            "application/memory+walkingkooka.spreadsheet.meta.SpreadsheetMetadata"
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
