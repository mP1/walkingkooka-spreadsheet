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
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<SpreadsheetParserSpreadsheetParserProvider>,
    ToStringTesting<SpreadsheetParserSpreadsheetParserProvider> {

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserSpreadsheetParserProvider.with(null)
        );
    }

    // spreadsheetFormatterSelector.....................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorWithUnknownFails() {
        this.spreadsheetFormatterSelectorFails(
            SpreadsheetParserSelector.parse("unknown123")
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateWithEmptyPattern() {
        final String text = "";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateAndNotEmptyPattern() {
        final String text = " yyyy/mm/dd";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithDateTimeAndPattern() {
        final String text = " yyyy/mm/dd hh:mm";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_TIME + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.DATE_TIME + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithGeneral() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.GENERAL_STRING),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.GENERAL + "")
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithNumberAndPattern() {
        final String text = " $0.00";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.NUMBER + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.NUMBER + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithTimeAndPattern() {
        final String text = " hh:mm";

        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.TIME + text),
            SpreadsheetFormatterSelector.parse(SpreadsheetFormatterName.TIME + text)
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithWholeNumber() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.WHOLE_NUMBER_STRING)
        );
    }

    // SpreadsheetParserSelector........................................................................................

    @Test
    public void testSpreadsheetParserNameWithDate() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.DATE,
            Lists.of("dd/mm/yy"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameWithDateTime() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.DATE_TIME,
            Lists.of("dd/mm/yyyy hh:mm:ss"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameWithGeneral() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.GENERAL,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetParsers.general()
        );
    }

    @Test
    public void testSpreadsheetParserNameWithNumber() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.NUMBER,
            Lists.of("$0.00"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberParsePattern("$0.00")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameWithTextFails() {
        this.spreadsheetParserFails(
            SpreadsheetParserName.with("text"),
            Lists.of("@@\"Hello\""),
            PROVIDER_CONTEXT
        );
    }


    @Test
    public void testSpreadsheetParserNameWithTime() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.TIME,
            Lists.of("hh:mm:ss"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameWithWholeNumber() {
        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.WHOLE_NUMBER,
            Lists.empty(),
            PROVIDER_CONTEXT,
            SpreadsheetParsers.wholeNumber()
        );
    }

    // SpreadsheetParserSelector........................................................................................

    @Test
    public void testSpreadsheetParserSelectorWithDate() {
        this.spreadsheetParserAndCheck(
            "date dd/mm/yy",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yy")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithDateTime() {
        this.spreadsheetParserAndCheck(
            "date-time dd/mm/yyyy hh:mm:ss",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithGeneral() {
        this.spreadsheetParserAndCheck(
            "general",
            PROVIDER_CONTEXT,
            SpreadsheetParsers.general()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithNumber() {
        this.spreadsheetParserAndCheck(
            "number $0.00",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseNumberParsePattern("$0.00")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithTextFails() {
        this.spreadsheetParserFails(
            "text-parse-pattern @@\"Hello\"",
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithTime() {
        this.spreadsheetParserAndCheck(
            "time hh:mm:ss.SSS AM/PM",
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss.SSS AM/PM")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithWholeNumber() {
        this.spreadsheetParserAndCheck(
            "whole-number",
            PROVIDER_CONTEXT,
            SpreadsheetParsers.wholeNumber()
        );
    }

    // spreadsheetParserSelectorNextToken...............................................................................

    @Test
    public void testSpreadsheetParserSelectorNextTokenWithDateAndEmptyPattern() {
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
    public void testSpreadsheetParserNextTokenWithDateAndPatternNotEmpty() {
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
    public void testSpreadsheetParserNextTokenWithDateTimeAndPatternEmpty() {
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
    public void testSpreadsheetParserNextTokenWithDateTimeAndPatternNotEmpty() {
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
    public void testSpreadsheetParserNextTokenWithGeneral() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("general")
        );
    }

    @Test
    public void testSpreadsheetParserNextTokenWithNumberAndPatternEmpty() {
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
    public void testSpreadsheetParserNextTokenWithNumberAndPatternNotEmpty() {
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
    public void testSpreadsheetParserNextTokenWithTimeAndPatternEmpty() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("time"),
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
    public void testSpreadsheetParserNextTokenWithTimeWithNotEmptyPattern() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse("time hh:mm"),
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

    @Test
    public void testSpreadsheetParserNextTokenWithWholeNumber() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse(
                SpreadsheetParserName.WHOLE_NUMBER_STRING
            )
        );
    }

    // SpreadsheetParserProvider........................................................................................

    @Override
    public SpreadsheetParserSpreadsheetParserProvider createSpreadsheetParserProvider() {
        return SpreadsheetParserSpreadsheetParserProvider.with(
            SpreadsheetFormatterProviders.spreadsheetFormatters()
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testSpreadsheetParserSelectorToString() {
        this.toStringAndCheck(
            this.createSpreadsheetParserProvider(),
            "SpreadsheetParserSpreadsheetParserProvider"
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
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/general general\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/number number\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/time time\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/whole-number whole-number\n"
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
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/general general\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/number number\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/time time\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/whole-number whole-number\"\n" +
                    "]"
            ),
            JsonNodeMarshallContexts.basic()
                .marshall(
                    this.createSpreadsheetParserProvider()
                        .spreadsheetParserInfos()
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserSpreadsheetParserProvider> type() {
        return SpreadsheetParserSpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
