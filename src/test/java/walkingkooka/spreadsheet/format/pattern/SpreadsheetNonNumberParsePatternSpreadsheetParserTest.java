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
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorTextComponent;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorTextComponentAlternative;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTesting2;

public final class SpreadsheetNonNumberParsePatternSpreadsheetParserTest implements SpreadsheetParserTesting2<SpreadsheetNonNumberParsePatternSpreadsheetParser>,
        ClassTesting<SpreadsheetNonNumberParsePatternSpreadsheetParser>,
        ToStringTesting<SpreadsheetNonNumberParsePatternSpreadsheetParser> {

    @Test
    public void testTextComponents() {
        this.textComponentsAndCheck(
                this.createContext(),
                SpreadsheetParserSelectorTextComponent.with(
                        "dd",
                        "dd",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                )
                        )
                ),
                SpreadsheetParserSelectorTextComponent.with(
                        "/",
                        "/",
                        SpreadsheetParserSelectorTextComponent.NO_ALTERNATIVES
                ),
                SpreadsheetParserSelectorTextComponent.with(
                        "mm",
                        "mm",
                        Lists.of(
                                SpreadsheetParserSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
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
                ),
                SpreadsheetParserSelectorTextComponent.with(
                        "/",
                        "/",
                        SpreadsheetParserSelectorTextComponent.NO_ALTERNATIVES
                ),
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
