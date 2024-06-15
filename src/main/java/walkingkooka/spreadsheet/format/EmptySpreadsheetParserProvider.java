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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetParserProvider} that is empty and always returns nothing when queried.
 */
final class EmptySpreadsheetParserProvider implements SpreadsheetParserProvider {

    /**
     * Singleton.
     */
    final static EmptySpreadsheetParserProvider INSTANCE = new EmptySpreadsheetParserProvider();

    private EmptySpreadsheetParserProvider() {
        super();
    }

    @Override
    public Optional<Parser<SpreadsheetParserContext>> spreadsheetParser(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");
        return Optional.empty();
    }

    @Override
    public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
        return Sets.empty();
    }
}
