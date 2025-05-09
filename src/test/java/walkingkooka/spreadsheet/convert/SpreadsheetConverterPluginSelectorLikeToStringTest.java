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
import walkingkooka.spreadsheet.export.SpreadsheetExporterSelector;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;

public final class SpreadsheetConverterPluginSelectorLikeToStringTest extends SpreadsheetConverterTestCase<SpreadsheetConverterPluginSelectorLikeToString> {

    @Test
    public void testConvertExporterToBooleanFails() {
        this.convertFails(
                SpreadsheetExporterSelector.parse("json"),
                Boolean.class
        );
    }

    @Test
    public void testConvertExporterToString() {
        final String selector = "json";

        this.convertAndCheck(
                SpreadsheetExporterSelector.parse(selector),
                String.class,
                selector
        );
    }

    @Test
    public void testConvertExpressionFunctionSelectorToString() {
        final String selector = "magic-function(123)";

        this.convertAndCheck(
                ExpressionFunctionSelector.parse(
                        selector,
                        SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY
                ),
                String.class,
                selector
        );
    }

    @Test
    public void testConvertFormatterToString() {
        final String selector = "date-formatter dd/mm/yyyy";

        this.convertAndCheck(
                SpreadsheetFormatterSelector.parse(selector),
                String.class,
                selector
        );
    }

    @Test
    public void testConvertImporterToString() {
        final String selector = "json";

        this.convertAndCheck(
                SpreadsheetImporterSelector.parse(selector),
                String.class,
                selector
        );
    }

    @Test
    public void testConvertParserToString() {
        final String selector = "date-parser dd/mm/yyyy";

        this.convertAndCheck(
                SpreadsheetParserSelector.parse(selector),
                String.class,
                selector
        );
    }

    @Override
    public SpreadsheetConverterPluginSelectorLikeToString createConverter() {
        return SpreadsheetConverterPluginSelectorLikeToString.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetConverterPluginSelectorLikeToString.INSTANCE,
                "plugin-selector-like to String"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterPluginSelectorLikeToString> type() {
        return SpreadsheetConverterPluginSelectorLikeToString.class;
    }
}
