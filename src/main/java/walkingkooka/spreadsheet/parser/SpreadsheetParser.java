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
import walkingkooka.text.cursor.parser.Parser;

import java.util.List;
import java.util.Optional;

/**
 * A specialised {@link Parser} that supports extra operations
 */
public interface SpreadsheetParser extends Parser<SpreadsheetParserContext> {

    /**
     * Useful constant for {@link SpreadsheetParser} with no text components.
     */
    Optional<List<SpreadsheetParserSelectorTextComponent>> NO_TEXT_COMPONENTS = Optional.empty();

    /**
     * Useful constant for {@link SpreadsheetParser} with empty text components.
     */
    Optional<List<SpreadsheetParserSelectorTextComponent>> EMPTY_TEXT_COMPONENTS = Optional.of(Lists.empty());

    /**
     * Returns a list of {@link SpreadsheetParserSelectorTextComponent} if this {@link SpreadsheetParser} supports
     * tokenizing its pattern. A {@link SpreadsheetParserContext} could be useful such as displaying day names in the label for an {@link SpreadsheetParserSelectorTextComponentAlternative}.
     */
    Optional<List<SpreadsheetParserSelectorTextComponent>> textComponents(final SpreadsheetParserContext context);
}
