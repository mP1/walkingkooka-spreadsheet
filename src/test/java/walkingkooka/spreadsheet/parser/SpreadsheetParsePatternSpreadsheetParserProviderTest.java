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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParsePatternSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<SpreadsheetParsePatternSpreadsheetParserProvider>,
        ToStringTesting<SpreadsheetParsePatternSpreadsheetParserProvider> {

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParsePatternSpreadsheetParserProvider.with(null)
        );
    }

    @Test
    public void testSpreadsheetParserSelectorDateParsePattern() {
        this.spreadsheetParserAndCheck(
                "date-parse-pattern dd/mm/yy",
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorNextTextComponentDateParsePatternEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse(
                        "date-parse-pattern"
                ),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentDateParsePatternNotEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("date-parse-pattern yyyy"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserNameDateParsePattern() {
        this.spreadsheetParserAndCheck(
                SpreadsheetParserName.DATE_PARSER_PATTERN,
                Lists.of("dd/mm/yy"),
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorDateTimeParsePattern() {
        this.spreadsheetParserAndCheck(
                "date-time-parse-pattern dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameDateTimeParsePattern() {
        this.spreadsheetParserAndCheck(
                SpreadsheetParserName.DATE_TIME_PARSER_PATTERN,
                Lists.of("dd/mm/yyyy hh:mm:ss"),
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentDateTimeParsePatternEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("date-time-parse-pattern"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentDateTimeParsePatternNotEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("date-time-parse-pattern yyyy"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserSelectorNumberParsePattern() {
        this.spreadsheetParserAndCheck(
                "number-parse-pattern $0.00",
                SpreadsheetPattern.parseNumberParsePattern("$0.00")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameNumberParsePattern() {
        this.spreadsheetParserAndCheck(
                SpreadsheetParserName.NUMBER_PARSER_PATTERN,
                Lists.of("$0.00"),
                SpreadsheetPattern.parseNumberParsePattern("$0.00")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentNumberParsePatternEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("number-parse-pattern"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "#",
                                        "#"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "$",
                                        "$"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "%",
                                        "%"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ",",
                                        ","
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "/",
                                        "/"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "?",
                                        "?"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "E",
                                        "E"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentNumberParsePatternNotEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("number-parse-pattern $0.00"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "#",
                                        "#"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "$",
                                        "$"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "%",
                                        "%"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ",",
                                        ","
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "/",
                                        "/"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "?",
                                        "?"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "E",
                                        "E"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserSelectorTextParsePatternFails() {
        this.spreadsheetParserFails(
                "text-parse-pattern @@\"Hello\""
        );
    }

    @Test
    public void testSpreadsheetParserNameTextParsePatternFails() {
        this.spreadsheetParserFails(
                SpreadsheetParserName.with("text"),
                Lists.of("@@\"Hello\"")
        );
    }

    @Test
    public void testSpreadsheetParserSelectorTimeParsePattern() {
        this.spreadsheetParserAndCheck(
                "time-parse-pattern hh:mm:ss",
                SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameTimeParsePattern() {
        this.spreadsheetParserAndCheck(
                SpreadsheetParserName.TIME_PARSER_PATTERN,
                Lists.of("hh:mm:ss"),
                SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                        .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentTimeParsePatternEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("time-parse-pattern"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentTimeParsePatternNotEmpty() {
        this.spreadsheetParserNextTextComponentAndCheck(
                SpreadsheetParserSelector.parse("time-parse-pattern hh:mm"),
                SpreadsheetParserSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    // spreadsheetFormatterSelector.....................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithEmptyDateParsePattern() {
        final String text = "";

        this.spreadsheetFormatterSelectorAndCheck(
                SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_PARSER_PATTERN + text),
                SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE_FORMAT_PATTERN + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateParsePattern() {
        final String text = " yyyy/mm/dd";

        this.spreadsheetFormatterSelectorAndCheck(
                SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_PARSER_PATTERN + text),
                SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE_FORMAT_PATTERN + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateTimeParsePattern() {
        final String text = " yyyy/mm/dd hh:mm";

        this.spreadsheetFormatterSelectorAndCheck(
                SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_TIME_PARSER_PATTERN + text),
                SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithNumberParsePattern() {
        final String text = " $0.00";

        this.spreadsheetFormatterSelectorAndCheck(
                SpreadsheetParserSelector.parse(SpreadsheetParserName.NUMBER_PARSER_PATTERN + text),
                SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithTimeParsePattern() {
        final String text = " hh:mm";

        this.spreadsheetFormatterSelectorAndCheck(
                SpreadsheetParserSelector.parse(SpreadsheetParserName.TIME_PARSER_PATTERN + text),
                SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.TIME_FORMAT_PATTERN + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithNonPattern() {
        this.spreadsheetFormatterSelectorAndCheck(
                SpreadsheetParserSelector.parse("unknown123")
        );
    }

    @Override
    public SpreadsheetParsePatternSpreadsheetParserProvider createSpreadsheetParserProvider() {
        return SpreadsheetParsePatternSpreadsheetParserProvider.with(
                SpreadsheetFormatterProviders.spreadsheetFormatPattern(
                        Locale.forLanguageTag("EN-AU"),
                        () -> {
                            throw new UnsupportedOperationException();
                        }
                )
        );
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
    public void testSpreadsheetParserSelectorToString() {
        this.toStringAndCheck(
                this.createSpreadsheetParserProvider(),
                "SpreadsheetPattern.parser"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testSpreadsheetParserSelectorTreePrintable() {
        this.treePrintAndCheck(
                SpreadsheetParserInfoSet.with(
                        this.createSpreadsheetParserProvider()
                                .spreadsheetParserInfos()
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
    public void testSpreadsheetParserSelectorMarshall() {
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
                                        this.createSpreadsheetParserProvider()
                                                .spreadsheetParserInfos()
                                )
                        )
        );
    }
}
