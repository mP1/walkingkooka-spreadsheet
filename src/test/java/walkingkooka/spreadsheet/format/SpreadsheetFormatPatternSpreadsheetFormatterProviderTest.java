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
    public void testSpreadsheetFormatterSelectorCollection() {
        this.spreadsheetFormatterAndCheck(
                "collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))",
                SpreadsheetFormatters.collection(
                        Lists.of(
                                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter(),
                                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yy hh:mm").formatter(),
                                SpreadsheetPattern.parseNumberFormatPattern("0.00").formatter(),
                                SpreadsheetPattern.parseTextFormatPattern("@@").formatter(),
                                SpreadsheetPattern.parseTimeFormatPattern("hh:mm").formatter()
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorNextTextComponentCollection() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse(
                        "collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))"
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
    public void testSpreadsheetFormatterNextTextComponentDateFormatPatternEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("date-format-pattern"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentDateFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("date-format-pattern yyyy"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                )
                        )
                )
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
    public void testSpreadsheetFormatterNextTextComponentDateTimeFormatPatternEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("date-time-format-pattern"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentDateTimeFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("date-time-format-pattern yyyy"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNameGeneral() {
        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with("general"),
                Lists.empty(),
                SpreadsheetFormatters.general()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorGeneral() {
        this.spreadsheetFormatterAndCheck(
                "general",
                SpreadsheetFormatters.general()
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
    public void testSpreadsheetFormatterNextTextComponentNumberFormatPatternEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("number-format-pattern"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "#",
                                        "#"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "$",
                                        "$"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "%",
                                        "%"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ",",
                                        ","
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "/",
                                        "/"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "?",
                                        "?"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "E",
                                        "E"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentNumberFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("number-format-pattern $0.00"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "#",
                                        "#"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "$",
                                        "$"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "%",
                                        "%"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ",",
                                        ","
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "/",
                                        "/"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "?",
                                        "?"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "E",
                                        "E"
                                )
                        )
                )
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
    public void testSpreadsheetFormatterNextTextComponentTextFormatPatternEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("text-format-pattern"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "* ",
                                        "* "
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "@",
                                        "@"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "_ ",
                                        "_ "
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentTextFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("text-format-pattern @"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "* ",
                                        "* "
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "_ ",
                                        "_ "
                                )
                        )
                )
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

    @Test
    public void testSpreadsheetFormatterNextTextComponentTimeFormatPatternEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("time-format-pattern"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                    ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentTimeFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("time-format-pattern hh:mm"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    @Override
    public SpreadsheetFormatPatternSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE;
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
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\n" +
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
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection\",\n" +
                                "    \"name\": \"collection\"\n" +
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

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatPatternSpreadsheetFormatterProvider> type() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
