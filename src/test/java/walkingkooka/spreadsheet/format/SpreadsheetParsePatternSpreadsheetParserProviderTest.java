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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

public final class SpreadsheetParsePatternSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<SpreadsheetParsePatternSpreadsheetParserProvider>,
        ToStringTesting<SpreadsheetParsePatternSpreadsheetParserProvider> {

    @Test
    public void testDateParsePattern() {
        this.spreadsheetParserAndCheck(
                "date-parse-pattern dd/mm/yy",
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                        .parser()
        );
    }

    @Test
    public void testDateTimeParsePattern() {
        this.spreadsheetParserAndCheck(
                "date-time-parse-pattern dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                        .parser()
        );
    }

    @Test
    public void testNumberParsePattern() {
        this.spreadsheetParserAndCheck(
                "number-parse-pattern $0.00",
                SpreadsheetPattern.parseNumberParsePattern("$0.00")
                        .parser()
        );
    }

    @Test
    public void testTextParsePattern() {
        this.spreadsheetParserAndCheck(
                "text-parse-pattern @@\"Hello\""
        );
    }

    @Test
    public void testTimeParsePattern() {
        this.spreadsheetParserAndCheck(
                "time-parse-pattern hh:mm:ss",
                SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                        .parser()
        );
    }

    @Override
    public SpreadsheetParsePatternSpreadsheetParserProvider createSpreadsheetParserProvider() {
        return SpreadsheetParsePatternSpreadsheetParserProvider.INSTANCE;
    }

    @Override
    public Class<SpreadsheetParsePatternSpreadsheetParserProvider> type() {
        return SpreadsheetParsePatternSpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetParsePatternSpreadsheetParserProvider.INSTANCE,
                "SpreadsheetPattern.parser"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
                SpreadsheetParserInfoSet.with(
                        SpreadsheetParsePatternSpreadsheetParserProvider.INSTANCE.spreadsheetParserInfos()
                ),
                "SpreadsheetParserInfoSet\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-parse-pattern date-parse-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-time-parse-pattern date-time-parse-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/Parser/number-parse-pattern number-parse-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/Parser/time-parse-pattern time-parse-pattern\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.checkEquals(
                JsonNode.parse(
                        "[\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-parse-pattern\",\n" +
                                "    \"name\": \"date-parse-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-time-parse-pattern\",\n" +
                                "    \"name\": \"date-time-parse-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/number-parse-pattern\",\n" +
                                "    \"name\": \"number-parse-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/time-parse-pattern\",\n" +
                                "    \"name\": \"time-parse-pattern\"\n" +
                                "  }\n" +
                                "]"
                ),
                JsonNodeMarshallContexts.basic()
                        .marshall(
                                SpreadsheetParserInfoSet.with(
                                        SpreadsheetParsePatternSpreadsheetParserProvider.INSTANCE.spreadsheetParserInfos()
                                )
                        )
        );
    }
}
