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

package walkingkooka.spreadsheet.format.edit;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelectorTextComponent;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelectorTextComponentAlternative;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSelectorEditTest implements ParseStringTesting<SpreadsheetFormatterSelectorEdit>,
        TreePrintableTesting,
        JsonNodeMarshallingTesting<SpreadsheetFormatterSelectorEdit>,
        ClassTesting<SpreadsheetFormatterSelectorEdit> {

    @Test
    public void testParseWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormatterSelectorEdit.parse(
                        "",
                        null
                )
        );
    }

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInvalidSpreadsheetFormatterName() {
        this.parseStringAndCheck(
                "1",
                SpreadsheetFormatterSelectorEdit.with(
                        Optional.empty(),
                        "Invalid character '1' at 0 in \"1\" in \"1\"",
                        Lists.empty(),
                        Optional.empty(),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testParseOnlySpreadsheetFormatterName() {
        this.parseStringAndCheck(
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN.value(),
                SpreadsheetFormatterSelectorEdit.with(
                        Optional.of(SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("")),
                        "text is empty",
                        Lists.empty(),
                        Optional.empty(),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testParseSpreadsheetFormatterNameInvalidPattern() {
        final String selector = SpreadsheetFormatterName.DATE_FORMAT_PATTERN + " !";

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetFormatterSelector.parse(selector)
                        .spreadsheetFormatPattern()
        );

        this.parseStringAndCheck(
                selector,
                SpreadsheetFormatterSelectorEdit.with(
                        Optional.of(SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("!")),
                        thrown.getMessage(),
                        Lists.empty(),
                        Optional.empty(),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
                SpreadsheetFormatterName.DATE_FORMAT_PATTERN + " yyyy",
                SpreadsheetFormatterSelectorEdit.with(
                        Optional.of(SpreadsheetFormatterName.DATE_FORMAT_PATTERN.setText("yyyy")),
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponent.with(
                                        "yyyy",
                                        "yyyy",
                                        Lists.of(
                                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                                        "yy",
                                                        "yy"
                                                )
                                        )
                                )
                        ),
                        Optional.of(
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
                        ),
                        Lists.empty()
                )
        );
    }

    @Override
    public SpreadsheetFormatterSelectorEdit parseString(final String selector) {
        return SpreadsheetFormatterSelectorEdit.parse(
                selector,
                SpreadsheetFormatterSelectorEditContexts.basic(
                        SpreadsheetFormatterContexts.fake(),
                        SpreadsheetFormatterProviders.spreadsheetFormatPattern(
                                Locale.forLanguageTag("EN-AU"),
                                () -> LocalDateTime.of(
                                        1999,
                                        12,
                                        31,
                                        12,
                                        58
                                )
                        )
                )
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.parseString(SpreadsheetFormatterName.DATE_FORMAT_PATTERN + " yyyy/mm/dd"),
                "SpreadsheetFormatterSelectorEdit\n" +
                        "  selector\n" +
                        "    date-format-pattern\n" +
                        "      \"yyyy/mm/dd\"\n" +
                        "  text-components\n" +
                        "    yyyy\n" +
                        "    yyyy\n" +
                        "      yy\n" +
                        "      yy\n" +
                        "    /\n" +
                        "    /\n" +
                        "    mm\n" +
                        "    mm\n" +
                        "      m\n" +
                        "      m\n" +
                        "      mmm\n" +
                        "      mmm\n" +
                        "      mmmm\n" +
                        "      mmmm\n" +
                        "      mmmmm\n" +
                        "      mmmmm\n" +
                        "    /\n" +
                        "    /\n" +
                        "    dd\n" +
                        "    dd\n" +
                        "      d\n" +
                        "      d\n" +
                        "      ddd\n" +
                        "      ddd\n" +
                        "      dddd\n" +
                        "      dddd\n" +
                        "  next\n" +
                        "    \n" +
                        "    \n" +
                        "      m\n" +
                        "      m\n" +
                        "      mm\n" +
                        "      mm\n" +
                        "      mmm\n" +
                        "      mmm\n" +
                        "      mmmm\n" +
                        "      mmmm\n" +
                        "      mmmmm\n" +
                        "      mmmmm\n" +
                        "      yy\n" +
                        "      yy\n" +
                        "      yyyy\n" +
                        "      yyyy\n"
        );
    }

    // json............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                "{\n" +
                        "  \"selector\": \"date-format-pattern dd/mm/yyyy\",\n" +
                        "  \"message\": \"\",\n" +
                        "  \"textComponents\": [\n" +
                        "    {\n" +
                        "      \"type\": \"spreadsheet-formatter-selector-text-component\",\n" +
                        "      \"value\": {\n" +
                        "        \"label\": \"dd\",\n" +
                        "        \"text\": \"dd\",\n" +
                        "        \"alternatives\": [\n" +
                        "          {\n" +
                        "            \"label\": \"d\",\n" +
                        "            \"text\": \"d\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"label\": \"ddd\",\n" +
                        "            \"text\": \"ddd\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"label\": \"dddd\",\n" +
                        "            \"text\": \"dddd\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"spreadsheet-formatter-selector-text-component\",\n" +
                        "      \"value\": {\n" +
                        "        \"label\": \"/\",\n" +
                        "        \"text\": \"/\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"spreadsheet-formatter-selector-text-component\",\n" +
                        "      \"value\": {\n" +
                        "        \"label\": \"mm\",\n" +
                        "        \"text\": \"mm\",\n" +
                        "        \"alternatives\": [\n" +
                        "          {\n" +
                        "            \"label\": \"m\",\n" +
                        "            \"text\": \"m\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"label\": \"mmm\",\n" +
                        "            \"text\": \"mmm\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"label\": \"mmmm\",\n" +
                        "            \"text\": \"mmmm\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"label\": \"mmmmm\",\n" +
                        "            \"text\": \"mmmmm\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"spreadsheet-formatter-selector-text-component\",\n" +
                        "      \"value\": {\n" +
                        "        \"label\": \"/\",\n" +
                        "        \"text\": \"/\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"spreadsheet-formatter-selector-text-component\",\n" +
                        "      \"value\": {\n" +
                        "        \"label\": \"yyyy\",\n" +
                        "        \"text\": \"yyyy\",\n" +
                        "        \"alternatives\": [\n" +
                        "          {\n" +
                        "            \"label\": \"yy\",\n" +
                        "            \"text\": \"yy\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"next\": {\n" +
                        "    \"alternatives\": [\n" +
                        "      {\n" +
                        "        \"label\": \"d\",\n" +
                        "        \"text\": \"d\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"dd\",\n" +
                        "        \"text\": \"dd\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"ddd\",\n" +
                        "        \"text\": \"ddd\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"dddd\",\n" +
                        "        \"text\": \"dddd\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"m\",\n" +
                        "        \"text\": \"m\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"mm\",\n" +
                        "        \"text\": \"mm\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"mmm\",\n" +
                        "        \"text\": \"mmm\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"mmmm\",\n" +
                        "        \"text\": \"mmmm\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"label\": \"mmmmm\",\n" +
                        "        \"text\": \"mmmmm\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  \"samples\": [\n" +
                        "    {\n" +
                        "      \"label\": \"Short\",\n" +
                        "      \"selector\": \"date-format-pattern d/m/yy\",\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"local-date\",\n" +
                        "        \"value\": \"1999-12-31\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"Medium\",\n" +
                        "      \"selector\": \"date-format-pattern d mmm yyyy\",\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"local-date\",\n" +
                        "        \"value\": \"1999-12-31\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"Long\",\n" +
                        "      \"selector\": \"date-format-pattern d mmmm yyyy\",\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"local-date\",\n" +
                        "        \"value\": \"1999-12-31\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"Full\",\n" +
                        "      \"selector\": \"date-format-pattern dddd, d mmmm yyyy\",\n" +
                        "      \"value\": {\n" +
                        "        \"type\": \"local-date\",\n" +
                        "        \"value\": \"1999-12-31\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"
        );
    }

    @Override
    public SpreadsheetFormatterSelectorEdit unmarshall(final JsonNode json,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterSelectorEdit.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetFormatterSelectorEdit createJsonNodeMarshallingValue() {
        return this.parseString(SpreadsheetFormatterName.DATE_FORMAT_PATTERN + " dd/mm/yyyy");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSelectorEdit> type() {
        return SpreadsheetFormatterSelectorEdit.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
