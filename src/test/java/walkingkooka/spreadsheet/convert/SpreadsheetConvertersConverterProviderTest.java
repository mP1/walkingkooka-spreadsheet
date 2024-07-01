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
import walkingkooka.convert.provider.ConverterProviderTesting;
import walkingkooka.reflect.JavaVisibility;

public class SpreadsheetConvertersConverterProviderTest implements ConverterProviderTesting<SpreadsheetConvertersConverterProvider> {

    @Test
    public void testConverterWithBasicSpreadsheetConverter() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.BASIC_SPREADSHEET_CONVERTER + "",
                SpreadsheetConverters.basic()
        );
    }

    @Test
    public void testConverterWithErrorThrowing() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_THROWING + "",
                SpreadsheetConverters.errorThrowing()
        );
    }

    @Test
    public void testConverterWithErrorToNumber() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_TO_NUMBER + "",
                SpreadsheetConverters.errorToNumber()
        );
    }

    @Test
    public void testConverterWithErrorToString() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.ERROR_TO_STRING + "",
                SpreadsheetConverters.errorToString()
        );
    }

    @Test
    public void testConverterWithSelectionToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION + "",
                SpreadsheetConverters.selectionToSelection()
        );
    }

    @Test
    public void testConverterWithStringToSelection() {
        this.converterAndCheck(
                SpreadsheetConvertersConverterProvider.STRING_TO_SELECTION + "",
                SpreadsheetConverters.stringToSelection()
        );
    }

    @Override
    public SpreadsheetConvertersConverterProvider createConverterProvider() {
        return SpreadsheetConvertersConverterProvider.INSTANCE;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<SpreadsheetConvertersConverterProvider> type() {
        return SpreadsheetConvertersConverterProvider.class;
    }
}
