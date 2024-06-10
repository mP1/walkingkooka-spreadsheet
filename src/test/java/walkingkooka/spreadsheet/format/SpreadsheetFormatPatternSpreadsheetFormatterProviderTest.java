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

public final class SpreadsheetFormatPatternSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider>,
        ToStringTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider> {

    @Test
    public void testDateFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "date-format-pattern dd/mm/yy",
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    @Test
    public void testDateTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "date-time-format-pattern dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    @Test
    public void testNumberFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "number-format-pattern $0.00",
                SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testTextFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "text-format-pattern @@\"Hello\"",
                SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "time-format-pattern hh:mm:ss",
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    @Override
    public SpreadsheetFormatPatternSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE;
    }

    @Override
    public Class<SpreadsheetFormatPatternSpreadsheetFormatterProvider> type() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE,
                "SpreadsheetFormatPattern.spreadsheetFormatter"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
                SpreadsheetFormatterInfoSet.with(
                        SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE.spreadsheetFormatterInfos()
                ),
                "SpreadsheetFormatterInfoSet\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern date-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern date-time-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern number-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern text-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time-format-pattern time-format-pattern\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.checkEquals(
                JsonNode.parse(
                        "walkingkooka.tree.json.JsonArray\n" +
                                "  [\n" +
                                "    {\n" +
                                "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern\",\n" +
                                "      \"name\": \"date-format-pattern\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern\",\n" +
                                "      \"name\": \"date-time-format-pattern\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern\",\n" +
                                "      \"name\": \"number-format-pattern\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern\",\n" +
                                "      \"name\": \"text-format-pattern\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time-format-pattern\",\n" +
                                "      \"name\": \"time-format-pattern\"\n" +
                                "    }\n" +
                                "  ]"
                ),
                JsonNodeMarshallContexts.basic()
                        .marshall(
                                SpreadsheetFormatterInfoSet.with(
                                        SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE.spreadsheetFormatterInfos()
                                )
                        )
        );
    }
}
