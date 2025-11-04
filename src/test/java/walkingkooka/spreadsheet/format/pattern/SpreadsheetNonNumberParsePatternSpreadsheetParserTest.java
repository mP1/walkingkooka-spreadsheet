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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTesting2;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorTokenAlternative;
import walkingkooka.text.printer.TreePrintableTesting;

public final class SpreadsheetNonNumberParsePatternSpreadsheetParserTest implements SpreadsheetParserTesting2<SpreadsheetNonNumberParsePatternSpreadsheetParser>,
    ClassTesting<SpreadsheetNonNumberParsePatternSpreadsheetParser>,
    ToStringTesting<SpreadsheetNonNumberParsePatternSpreadsheetParser>,
    TreePrintableTesting {

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createContext(),
            SpreadsheetParserSelectorToken.with(
                "dd",
                "dd",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "d",
                        "d"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    )
                )
            ),
            SpreadsheetParserSelectorToken.with(
                "/",
                "/",
                SpreadsheetParserSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetParserSelectorToken.with(
                "mm",
                "mm",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "m",
                        "m"
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
            ),
            SpreadsheetParserSelectorToken.with(
                "/",
                "/",
                SpreadsheetParserSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetParserSelectorToken.with(
                "yyyy",
                "yyyy",
                Lists.of(
                    SpreadsheetParserSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    )
                )
            )
        );
    }

    @Override
    public SpreadsheetNonNumberParsePatternSpreadsheetParser createParser() {
        return (SpreadsheetNonNumberParsePatternSpreadsheetParser) SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy").parser();
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createParser(),
            "\"dd/mm/yyyy\""
        );
    }

    // toString.........................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            this.createParser(),
            "SpreadsheetNonNumberParsePatternSpreadsheetParser\n" +
                "  parser\n" +
                "    \"dd/mm/yyyy\" (walkingkooka.text.cursor.parser.AndEmptyTextCursorParser)\n" +
                "  valueType\n" +
                "    date\n" +
                "  tokens\n" +
                "    Sequence \"dd/mm/yyyy\"\n" +
                "      DateSpreadsheetFormat \"dd/mm/yyyy\"\n" +
                "        DaySpreadsheetFormat \"dd\" \"dd\"\n" +
                "        TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "        MonthSpreadsheetFormat \"mm\" \"mm\"\n" +
                "        TextLiteralSpreadsheetFormat \"/\" \"/\"\n" +
                "        YearSpreadsheetFormat \"yyyy\" \"yyyy\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetNonNumberParsePatternSpreadsheetParser> type() {
        return SpreadsheetNonNumberParsePatternSpreadsheetParser.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
