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
import walkingkooka.plugin.FakeProviderContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextNode;

public final class SpreadsheetFormattersSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormattersSpreadsheetFormatterProvider>,
    ToStringTesting<SpreadsheetFormattersSpreadsheetFormatterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = new FakeProviderContext() {
        @Override
        public <T> T convertOrFail(final Object value,
                                   final Class<T> type) {
            if (value instanceof String && type == Expression.class) {
                return type.cast(
                    Expression.value(
                        String.class.cast(value)
                    )
                );
            }
            throw this.convertThrowable(
                "Only support converting String to Expression but got " + value.getClass().getSimpleName() + " " + type.getSimpleName(),
                value,
                type
            );
        }
    };

    @Test
    public void testSpreadsheetFormatterSelectorAutomaticFiveParameters() {
        this.spreadsheetFormatterAndCheck(
            "automatic (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))",
            CONTEXT,
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
    public void testSpreadsheetFormatterSelectorAutomaticZeroParameters() {
        this.spreadsheetFormatterAndCheck(
            "automatic",
            PROVIDER_CONTEXT,
            SpreadsheetFormatters.automatic(
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.DATE_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.TEXT_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter(),
                METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.TIME_FORMATTER)
                    .spreadsheetFormatPattern()
                    .get()
                    .formatter()
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorCollection() {
        this.spreadsheetFormatterAndCheck(
            "collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))",
            CONTEXT,
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
    public void testSpreadsheetFormatterSelectorNextTokenCollection() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse(
                "collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))"
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorDateFormatPattern() {
        this.spreadsheetFormatterAndCheck(
            "date-format-pattern dd/mm/yy",
            CONTEXT,
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameDateFormatPattern() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("date-format-pattern"),
            Lists.of("dd/mm/yy"),
            CONTEXT,
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenDateFormatPatternEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date-format-pattern"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yyyy",
                        "yyyy"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenDateFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date-format-pattern yyyy"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
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
            CONTEXT,
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
            CONTEXT,
            SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenDateTimeFormatPatternEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date-time-format-pattern"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yyyy",
                        "yyyy"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenDateTimeFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("date-time-format-pattern yyyy"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    )
                )
            )
        );
    }


    @Test
    public void testSpreadsheetFormatterSelectorWithDefaultText() {
        this.spreadsheetFormatterAndCheck(
            "default-text",
            CONTEXT,
            SpreadsheetFormatters.defaultText()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithExpression() {
        this.spreadsheetFormatterAndCheck(
            "expression (\"Hello\")",
            CONTEXT,
            SpreadsheetFormatters.expression(
                Expression.value("Hello")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNameGeneral() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("general"),
            Lists.empty(),
            CONTEXT,
            SpreadsheetFormatters.general()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorGeneral() {
        this.spreadsheetFormatterAndCheck(
            "general",
            CONTEXT,
            SpreadsheetFormatters.general()
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorNumberFormatPattern() {
        this.spreadsheetFormatterAndCheck(
            "number-format-pattern $0.00",
            CONTEXT,
            SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameNumberFormatPattern() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("number-format-pattern"),
            Lists.of("$0.00"),
            CONTEXT,
            SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenNumberFormatPatternEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("number-format-pattern"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "#",
                        "#"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "$",
                        "$"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "%",
                        "%"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ",",
                        ","
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "/",
                        "/"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "?",
                        "?"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "E",
                        "E"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenNumberFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("number-format-pattern $0.00"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "#",
                        "#"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "$",
                        "$"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "%",
                        "%"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ",",
                        ","
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "/",
                        "/"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "?",
                        "?"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
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
            CONTEXT,
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
    public void testSpreadsheetFormatterSelectorNextTokenSpreadsheetPatternCollection() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse(
                "spreadsheet-pattern-collection (date-format-pattern(\"dd/mm/yy\"), date-time-format-pattern(\"dd/mm/yy hh:mm\"), number-format-pattern(\"0.00\"), text-format-pattern(\"@@\"), time-format-pattern(\"hh:mm\"))"
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorTextFormatPattern() {
        this.spreadsheetFormatterAndCheck(
            "text-format-pattern @@\"Hello\"",
            CONTEXT,
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
            CONTEXT,
            SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenTextFormatPatternEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("text-format-pattern"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "* ",
                        "* "
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "@",
                        "@"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "_ ",
                        "_ "
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenTextFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("text-format-pattern @"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "* ",
                        "* "
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
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
            CONTEXT,
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNameTimeFormatPattern() {
        this.spreadsheetFormatterAndCheck(
            SpreadsheetFormatterName.with("time-format-pattern"),
            Lists.of("hh:mm:ss"),
            CONTEXT,
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenTimeFormatPatternEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("time-format-pattern"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mm",
                        "mm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ss",
                        "ss"
                    )
                )
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenTimeFormatPatternNotEmpty() {
        this.spreadsheetFormatterNextTokenAndCheck(
            SpreadsheetFormatterSelector.parse("time-format-pattern hh:mm"),
            SpreadsheetFormatterSelectorToken.with(
                "",
                "",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        ".",
                        "."
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "0",
                        "0"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "A/P",
                        "A/P"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "AM/PM",
                        "AM/PM"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "a/p",
                        "a/p"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "am/pm",
                        "am/pm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "h",
                        "h"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "hh",
                        "hh"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "s",
                        "s"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
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
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setValueText("d/m/yy"),
                TextNode.text("31/12/99")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setValueText("d mmm yyyy"),
                TextNode.text("31 Dec. 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setValueText("d mmmm yyyy"),
                TextNode.text("31 December 1999")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setValueText("dddd, d mmmm yyyy"),
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
                SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setValueText("d/m/yy, h:mm AM/PM"),
                TextNode.text("31/12/99, 12:58 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Medium",
                SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setValueText("d mmm yyyy, h:mm:ss AM/PM"),
                TextNode.text("31 Dec. 1999, 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setValueText("d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("31 December 1999 at 12:58:00 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Full",
                SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN.setValueText("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM"),
                TextNode.text("Friday, 31 December 1999 at 12:58:00 PM")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSamplesExpression() {
        this.spreadsheetFormatterSamplesAndCheck(
            SpreadsheetFormatterName.EXPRESSION,
            SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT
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
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "General",
                SpreadsheetFormatterName.GENERAL.setValueText(""),
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
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0.###"),
                TextNode.text("123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0.###"),
                TextNode.text("-123.5")
            ),
            SpreadsheetFormatterSample.with(
                "Number",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0.###"),
                TextNode.text("0.")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0"),
                TextNode.text("124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0"),
                TextNode.text("-124")
            ),
            SpreadsheetFormatterSample.with(
                "Integer",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0"),
                TextNode.text("0")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0%"),
                TextNode.text("12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0%"),
                TextNode.text("-12,350%")
            ),
            SpreadsheetFormatterSample.with(
                "Percent",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("#,##0%"),
                TextNode.text("0%")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("$#,##0.00"),
                TextNode.text("$123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("$#,##0.00"),
                TextNode.text("$-123.50")
            ),
            SpreadsheetFormatterSample.with(
                "Currency",
                SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN.setValueText("$#,##0.00"),
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
                SpreadsheetFormatterName.TEXT_FORMAT_PATTERN.setValueText("@"),
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
                SpreadsheetFormatterName.TIME_FORMAT_PATTERN.setValueText("h:mm AM/PM"),
                TextNode.text("12:58 PM")
            ),
            SpreadsheetFormatterSample.with(
                "Long",
                SpreadsheetFormatterName.TIME_FORMAT_PATTERN.setValueText("h:mm:ss AM/PM"),
                TextNode.text("12:58:00 PM")
            )
        );
    }

    @Override
    public SpreadsheetFormattersSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return SpreadsheetFormattersSpreadsheetFormatterProvider.INSTANCE;
    }

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
            this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatterInfos(),
            "SpreadsheetFormatterInfoSet\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern date-format-pattern\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern date-time-format-pattern\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/default-text default-text\n" +
                "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/expression expression\n" +
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
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic automatic\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection collection\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern date-format-pattern\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern date-time-format-pattern\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/default-text default-text\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/expression expression\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general general\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern number-format-pattern\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection spreadsheet-pattern-collection\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern text-format-pattern\",\n" +
                    "  \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time-format-pattern time-format-pattern\"\n" +
                    "]"
            ),
            JsonNodeMarshallContexts.basic()
                .marshall(
                    this.createSpreadsheetFormatterProvider()
                        .spreadsheetFormatterInfos()
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormattersSpreadsheetFormatterProvider> type() {
        return SpreadsheetFormattersSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
