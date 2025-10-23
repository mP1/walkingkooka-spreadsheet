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

package walkingkooka.spreadsheet.parser.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParsePatternSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<SpreadsheetParsePatternSpreadsheetParserProvider>,
    ToStringTesting<SpreadsheetParsePatternSpreadsheetParserProvider> {

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

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
            "date dd/mm/yy",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorNextTokenDateParsePatternEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse(
                "date"
            ),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "yyyy",
                        "yyyy"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenDateParsePatternNotEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("date yyyy"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
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
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorDateTimeParsePattern() {
        this.spreadsheetParserAndCheck(
            "date-time dd/mm/yyyy hh:mm:ss",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameDateTimeParsePattern() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.DATE_TIME_PARSER_PATTERN,
            Lists.of("dd/mm/yyyy hh:mm:ss"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenDateTimeParsePatternEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("date-time"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "yyyy",
                        "yyyy"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenDateTimeParsePatternNotEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("date-time yyyy"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
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
            "number $0.00",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberParsePattern("$0.00")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameNumberParsePattern() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.NUMBER_PARSER_PATTERN,
            Lists.of("$0.00"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberParsePattern("$0.00")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenNumberParsePatternEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("number"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "#",
                        "#"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "$",
                        "$"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "%",
                        "%"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ",",
                        ","
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "/",
                        "/"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "?",
                        "?"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "E",
                        "E"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenNumberParsePatternNotEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("number $0.00"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "#",
                        "#"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "$",
                        "$"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "%",
                        "%"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ",",
                        ","
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "/",
                        "/"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "?",
                        "?"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
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
            "text-parse-pattern @@\"Hello\"",
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserNameTextParsePatternFails() {
        this.spreadsheetParserFails(
            SpreadsheetParserName.with("text"),
            Lists.of("@@\"Hello\""),
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserSelectorTimeParsePattern() {
        this.spreadsheetParserAndCheck(
            "time-parse-pattern hh:mm:ss.SSS AM/PM",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss.SSS AM/PM")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameTimeParsePattern() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.TIME_PARSER_PATTERN,
            Lists.of("hh:mm:ss"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenTimeParsePatternEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("time-parse-pattern"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenTimeParsePatternNotEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("time-parse-pattern hh:mm"),
            SpreadsheetParserSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
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
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateParsePattern() {
        final String text = " yyyy/mm/dd";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_PARSER_PATTERN + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateTimeParsePattern() {
        final String text = " yyyy/mm/dd hh:mm";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_TIME_PARSER_PATTERN + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE_TIME + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithNumberParsePattern() {
        final String text = " $0.00";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.NUMBER_PARSER_PATTERN + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.NUMBER + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithTimeParsePattern() {
        final String text = " hh:mm";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.TIME_PARSER_PATTERN + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.TIME + text)
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
            SpreadsheetFormatterProviders.spreadsheetFormatters()
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
            this.createSpreadsheetParserProvider()
                .spreadsheetParserInfos(),
            "SpreadsheetParserInfoSet\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date date\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date-time date-time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/number number\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/time-parse-pattern time-parse-pattern\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testSpreadsheetParserSelectorMarshall() {
        this.checkEquals(
            JsonNode.parse(
                "[\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date date\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date-time date-time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/number number\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/time-parse-pattern time-parse-pattern\"\n" +
                    "]"
            ),
            JsonNodeMarshallContexts.basic()
                .marshall(
                    this.createSpreadsheetParserProvider()
                        .spreadsheetParserInfos()
                )
        );
    }
}
