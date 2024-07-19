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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

public final class SpreadsheetFormatPatternSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider>,
        ToStringTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider> {

    @Test
    public void testSpreadsheetFormatterSelectorAutomatic() {
        this.spreadsheetFormatterAndCheck(
                "automatic (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))",
                SpreadsheetFormatters.automatic(
                        SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter(),
                        SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yy hh:mm").formatter(),
                        SpreadsheetPattern.parseNumberFormatPattern("0.00").formatter(),
                        SpreadsheetPattern.parseTextFormatPattern("@@").formatter(),
                        SpreadsheetPattern.parseTimeFormatPattern("hh:mm").formatter()
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorDateFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "date-format-pattern dd/mm/yy",
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameDateFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with("date-format-pattern"),
                Lists.of("dd/mm/yy"),
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorDateTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "date-time-format-pattern dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameDateTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with("date-time-format-pattern"),
                Lists.of(
                        "dd/mm/yyyy hh:mm:ss"
                ),
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorNumberFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "number-format-pattern $0.00",
                SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameNumberFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with("number-format-pattern"),
                Lists.of("$0.00"),
                SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorTextFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "text-format-pattern @@\"Hello\"",
                SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameTextFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with("text-format-pattern"),
                Lists.of(
                        "@@\"Hello\""
                ),
                SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                "time-format-pattern hh:mm:ss",
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with("time-format-pattern"),
                Lists.of("hh:mm:ss"),
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
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern date-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern date-time-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general general\n" +
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
                        "[\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic\",\n" +
                                "    \"name\": \"automatic\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern\",\n" +
                                "    \"name\": \"date-format-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern\",\n" +
                                "    \"name\": \"date-time-format-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general\",\n" +
                                "    \"name\": \"general\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern\",\n" +
                                "    \"name\": \"number-format-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern\",\n" +
                                "    \"name\": \"text-format-pattern\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time-format-pattern\",\n" +
                                "    \"name\": \"time-format-pattern\"\n" +
                                "  }\n" +
                                "]"
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
