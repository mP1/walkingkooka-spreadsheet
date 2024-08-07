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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextNode;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Supplier;

public final class SpreadsheetFormatPatternSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider>,
        ToStringTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider>,
        SpreadsheetMetadataTesting {



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
    public void testSpreadsheetFormatterSelectorSpreadsheetPatternCollection() {
        this.spreadsheetFormatterAndCheck(
                "spreadsheet-pattern-collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))",
                SpreadsheetFormatters.spreadsheetPatternCollection(
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
    public void testSpreadsheetFormatterSelectorNextTextComponentSpreadsheetPatternCollection() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse(
                        "spreadsheet-pattern-collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))"
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

    // spreadsheetFormatterSamples......................................................................................

    @Test
    public void testSpreadsheetFormatterSamplesAutomatic() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.AUTOMATIC,
                SpreadsheetFormatterProviderSamplesContexts.fake()
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesCollection() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.COLLECTION,
                SpreadsheetFormatterProviderSamplesContexts.fake()
        );
    }

    // Short
    //  date-format-pattern
    //    "d/m/yy"
    //  Text "31/12/99"
    //
    //Medium
    //  date-format-pattern
    //    "d mmm yyyy"
    //  Text "31 Dec. 1999"
    //
    //Long
    //  date-format-pattern
    //    "d mmmm yyyy"
    //  Text "31 December 1999"
    //
    //Full
    //  date-format-pattern
    //    "dddd, d mmmm yyyy"
    //  Text "Friday, 31 December 1999"
    @Test
    public void testSpreadsheetFormatterSamplesDateFormatPattern() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Short",
                        SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("d/m/yy"),
                        TextNode.text("31/12/99")
                ),
                SpreadsheetFormatterSample.with(
                        "Medium",
                        SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("d mmm yyyy"),
                        TextNode.text("31 Dec. 1999")
                ),
                SpreadsheetFormatterSample.with(
                        "Long",
                        SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("d mmmm yyyy"),
                        TextNode.text("31 December 1999")
                ),
                SpreadsheetFormatterSample.with(
                        "Full",
                        SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("dddd, d mmmm yyyy"),
                        TextNode.text("Friday, 31 December 1999")
                )
        );
    }

    // Short
    //  date-time-format-pattern
    //    "d/m/yy, h:mm AM/PM"
    //  Text "31/12/99, 12:58 PM"
    //
    //Medium
    //  date-time-format-pattern
    //    "d mmm yyyy, h:mm:ss AM/PM"
    //  Text "31 Dec. 1999, 12:58:00 PM"
    //
    //Long
    //  date-time-format-pattern
    //    "d mmmm yyyy \\a\\t h:mm:ss AM/PM"
    //  Text "31 December 1999 at 12:58:00 PM"
    //
    //Full
    //  date-time-format-pattern
    //    "dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"
    //  Text "Friday, 31 December 1999 at 12:58:00 PM"
    @Test
    public void testSpreadsheetFormatterSamplesDateTimeFormatPattern() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Short",
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setText("d/m/yy, h:mm AM/PM"),
                        TextNode.text("31/12/99, 12:58 PM")
                ),
                SpreadsheetFormatterSample.with(
                        "Medium",
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setText("d mmm yyyy, h:mm:ss AM/PM"),
                        TextNode.text("31 Dec. 1999, 12:58:00 PM")
                ),
                SpreadsheetFormatterSample.with(
                        "Long",
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setText("d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                        TextNode.text("31 December 1999 at 12:58:00 PM")
                ),
                SpreadsheetFormatterSample.with(
                        "Full",
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setText("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                        TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
                )
        );
    }

    // General
    //  general
    //  Text "123.5"
    //
    //General
    //  general
    //  Text "-123.5"
    //
    //General
    //  general
    //  Text "0."
    @Test
    public void testSpreadsheetFormatterSamplesGeneral() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.GENERAL,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "General",
                        SpreadsheetFormatterName.GENERAL.setText(""),
                        TextNode.text("123.5")
                ),
                SpreadsheetFormatterSample.with(
                        "General",
                        SpreadsheetFormatterName.GENERAL.setText(""),
                        TextNode.text("-123.5")
                ),
                SpreadsheetFormatterSample.with(
                        "General",
                        SpreadsheetFormatterName.GENERAL.setText(""),
                        TextNode.text("0.")
                )
        );
    }

    // Number
    //  number-format-pattern
    //    "#,##0.###"
    //  Text "123.5"
    //
    //Number
    //  number-format-pattern
    //    "#,##0.###"
    //  Text "-123.5"
    //
    //Number
    //  number-format-pattern
    //    "#,##0.###"
    //  Text "0."
    //
    //Integer
    //  number-format-pattern
    //    "#,##0"
    //  Text "124"
    //
    //Integer
    //  number-format-pattern
    //    "#,##0"
    //  Text "-124"
    //
    //Integer
    //  number-format-pattern
    //    "#,##0"
    //  Text "0"
    //
    //Percent
    //  number-format-pattern
    //    "#,##0%"
    //  Text "12,350%"
    //
    //Percent
    //  number-format-pattern
    //    "#,##0%"
    //  Text "-12,350%"
    //
    //Percent
    //  number-format-pattern
    //    "#,##0%"
    //  Text "0%"
    //
    //Currency
    //  number-format-pattern
    //    "$#,##0.00"
    //  Text "$123.50"
    //
    //Currency
    //  number-format-pattern
    //    "$#,##0.00"
    //  Text "$-123.50"
    //
    //Currency
    //  number-format-pattern
    //    "$#,##0.00"
    //  Text "$0.00"
    @Test
    public void testSpreadsheetFormatterSamplesNumberFormatPattern() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Number",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0.###"),
                        TextNode.text("123.5")
                ),
                SpreadsheetFormatterSample.with(
                        "Number",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0.###"),
                        TextNode.text("-123.5")
                ),
                SpreadsheetFormatterSample.with(
                        "Number",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0.###"),
                        TextNode.text("0.")
                ),
                SpreadsheetFormatterSample.with(
                        "Integer",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0"),
                        TextNode.text("124")
                ),
                SpreadsheetFormatterSample.with(
                        "Integer",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0"),
                        TextNode.text("-124")
                ),
                SpreadsheetFormatterSample.with(
                        "Integer",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0"),
                        TextNode.text("0")
                ),
                SpreadsheetFormatterSample.with(
                        "Percent",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0%"),
                        TextNode.text("12,350%")
                ),
                SpreadsheetFormatterSample.with(
                        "Percent",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0%"),
                        TextNode.text("-12,350%")
                ),
                SpreadsheetFormatterSample.with(
                        "Percent",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("#,##0%"),
                        TextNode.text("0%")
                ),
                SpreadsheetFormatterSample.with(
                        "Currency",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("$#,##0.00"),
                        TextNode.text("$123.50")
                ),
                SpreadsheetFormatterSample.with(
                        "Currency",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("$#,##0.00"),
                        TextNode.text("$-123.50")
                ),
                SpreadsheetFormatterSample.with(
                        "Currency",
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setText("$#,##0.00"),
                        TextNode.text("$0.00")
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesTextFormatPattern() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.TEXT_FORMAT_PATTERN,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Default",
                        SpreadsheetFormatterName.TEXT_FORMAT_PATTERN.setText("@"),
                        TextNode.text("Hello 123")
                )
        );
    }

    // Short
    //  time-format-pattern
    //    "h:mm AM/PM"
    //  Text "12:58 PM"
    //
    //Long
    //  time-format-pattern
    //    "h:mm:ss AM/PM"
    //  Text "12:58:00 PM"
    @Test
    public void testSpreadsheetFormatterSamplesTimeFormatPattern() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.TIME_FORMAT_PATTERN,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Short",
                        SpreadsheetFormatterName.TIME_FORMAT_PATTERN.setText("h:mm AM/PM"),
                        TextNode.text("12:58 PM")
                ),
                SpreadsheetFormatterSample.with(
                        "Long",
                        SpreadsheetFormatterName.TIME_FORMAT_PATTERN.setText("h:mm:ss AM/PM"),
                        TextNode.text("12:58:00 PM")
                )
        );
    }

    @Override
    public SpreadsheetFormatPatternSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE;
    }

    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");

    private final static Supplier<LocalDateTime> NOW = () -> LocalDateTime.of(
            1999,
            12,
            31,
            12,
            58,
            59
    );

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createSpreadsheetFormatterProvider(),
                "SpreadsheetFormatPattern.spreadsheetFormatter"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
                SpreadsheetFormatterInfoSet.with(
                        this.createSpreadsheetFormatterProvider()
                                .spreadsheetFormatterInfos()
                ),
                "SpreadsheetFormatterInfoSet\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern date-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern date-time-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general general\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern number-format-pattern\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection spreadsheet-pattern-collection\n" +
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
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection\",\n" +
                                "    \"name\": \"spreadsheet-pattern-collection\"\n" +
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
                                        this.createSpreadsheetFormatterProvider()
                                                .spreadsheetFormatterInfos()
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
