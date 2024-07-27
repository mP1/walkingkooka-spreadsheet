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

package walkingkooka.spreadsheet.parser.edit;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorTextComponent;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorTextComponentAlternative;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserSelectorEditTest implements ParseStringTesting<SpreadsheetParserSelectorEdit>,
        TreePrintableTesting,
        JsonNodeMarshallingTesting<SpreadsheetParserSelectorEdit>,
        ClassTesting<SpreadsheetParserSelectorEdit> {

    @Test
    public void testParseWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParserSelectorEdit.parse(
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
    public void testParseInvalidSpreadsheetParserName() {
        this.parseStringAndCheck(
                "1",
                SpreadsheetParserSelectorEdit.with(
                        Optional.empty(),
                        "Invalid character '1' at 0 in \"1\" in \"1\"",
                        Lists.empty(),
                        Optional.empty(),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testParseOnlySpreadsheetParserName() {
        this.parseStringAndCheck(
                SpreadsheetParserName.DATE_PARSER_PATTERN.value(),
                SpreadsheetParserSelectorEdit.with(
                        Optional.of(
                                SpreadsheetParserName.DATE_PARSER_PATTERN.setText("")
                        ),
                        "text is empty",
                        Lists.empty(),
                        Optional.empty(),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testParseSpreadsheetParserNameInvalidPattern() {
        final String selector = SpreadsheetParserName.DATE_PARSER_PATTERN + " !";

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetParserSelector.parse(selector)
                        .spreadsheetParsePattern()
        );

        this.parseStringAndCheck(
                selector,
                SpreadsheetParserSelectorEdit.with(
                        Optional.of(
                                SpreadsheetParserName.DATE_PARSER_PATTERN.setText("!")
                        ),
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
                SpreadsheetParserName.DATE_PARSER_PATTERN + " yyyy",
                SpreadsheetParserSelectorEdit.with(
                        Optional.of(
                                SpreadsheetParserName.DATE_PARSER_PATTERN.setText("yyyy")
                        ),
                        "",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponent.with(
                                        "yyyy",
                                        "yyyy",
                                        Lists.of(
                                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                                        "yy",
                                                        "yy"
                                                )
                                        )
                                )
                        ),
                        Optional.of(
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
                        ),
                        Lists.empty()
                )
        );
    }

    @Override
    public SpreadsheetParserSelectorEdit parseString(final String selector) {
        final SpreadsheetFormatterProvider spreadsheetFormatterProvider = SpreadsheetFormatterProviders.spreadsheetFormatPattern(
                Locale.forLanguageTag("EN-AU"),
                LocalDateTime::now
        );

        return SpreadsheetParserSelectorEdit.parse(
                selector,
                SpreadsheetParserSelectorEditContexts.basic(
                        SpreadsheetParserProviders.spreadsheetParsePattern(
                                spreadsheetFormatterProvider
                        ),
                        SpreadsheetParserContexts.fake(),
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
                this.parseString("date-parse-pattern yyyy/mm/dd"),
                "SpreadsheetParserSelectorEdit\n" +
                        "  selector\n" +
                        "    date-parse-pattern\n" +
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

    @Override
    public SpreadsheetParserSelectorEdit unmarshall(final JsonNode json,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserSelectorEdit.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetParserSelectorEdit createJsonNodeMarshallingValue() {
        return this.parseString("date-parse-pattern dd/mm/yyyy");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserSelectorEdit> type() {
        return SpreadsheetParserSelectorEdit.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
