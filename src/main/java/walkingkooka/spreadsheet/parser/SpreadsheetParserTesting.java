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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserTesting;

import java.util.List;
import java.util.Optional;

public interface SpreadsheetParserTesting extends ParserTesting {

    // textComponentsAndCheck...........................................................................................

    default void textComponentsAndCheck(final SpreadsheetParser parser,
                                        final SpreadsheetParserContext context) {
        this.textComponentsAndCheck(
                parser,
                context,
                Optional.empty()
        );
    }

    default void textComponentsAndCheck(final SpreadsheetParser parser,
                                        final SpreadsheetParserContext context,
                                        final SpreadsheetParserSelectorTextComponent... expected) {
        this.textComponentsAndCheck(
                parser,
                context,
                Lists.of(expected)
        );
    }

    default void textComponentsAndCheck(final SpreadsheetParser parser,
                                        final SpreadsheetParserContext context,
                                        final List<SpreadsheetParserSelectorTextComponent> expected) {
        this.textComponentsAndCheck(
                parser,
                context,
                Optional.of(expected)
        );
    }

    default void textComponentsAndCheck(final SpreadsheetParser parser,
                                        final SpreadsheetParserContext context,
                                        final Optional<List<SpreadsheetParserSelectorTextComponent>> expected) {
        this.checkEquals(
                expected,
                parser.textComponents(context),
                parser::toString
        );
    }
}
