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
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorTokenAlternative;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorTokenList;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.validation.ValueTypeName;

import java.util.List;
import java.util.Optional;

/**
 * A specialised {@link Parser} that supports extra operations
 */
public interface SpreadsheetParser extends Parser<SpreadsheetParserContext> {

    /**
     * Useful constant for {@link SpreadsheetParser} with no text components.
     */
    List<SpreadsheetParserSelectorToken> NO_TOKENS = SpreadsheetParserSelectorTokenList.with(Lists.empty());

    /**
     * Returns a list of {@link SpreadsheetParserSelectorToken} if this {@link SpreadsheetParser} supports
     * tokenizing its pattern. A {@link SpreadsheetParserContext} could be useful such as displaying day names in the label for an {@link SpreadsheetParserSelectorTokenAlternative}.
     */
    List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context);

    /**
     * Constant for no value type.
     */
    Optional<ValueTypeName> NO_VALUE_TYPE = Optional.empty();

    /**
     * Returns the {@link ValueTypeName that this parser returns.
     */
    Optional<ValueTypeName> valueType();

    @Override
    default SpreadsheetParser setToString(final String toString) {
        return SpreadsheetParsers.toString(
            this,
            toString
        );
    }
}
