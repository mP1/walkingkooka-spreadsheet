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

import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValueTypeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParser} that only parsers whole numbers, with decimals and scientific numbers failing.
 */
final class WholeNumberSpreadsheetParser implements SpreadsheetParser {

    /**
     * Singleton
     */
    final static WholeNumberSpreadsheetParser INSTANCE = new WholeNumberSpreadsheetParser();

    private WholeNumberSpreadsheetParser() {
        super();
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor text,
                                       final SpreadsheetParserContext context) {
        return this.parser.parse(
            text,
            context
        );
    }

    @Override
    public int minCount() {
        return this.parser.minCount();
    }

    @Override
    public int maxCount() {
        return this.parser.maxCount();
    }

    private final Parser<SpreadsheetParserContext> parser = SpreadsheetPattern.parseNumberParsePattern(
        "#" // integer only
    ).parser();

    @Override
    public List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context) {
        Objects.requireNonNull(context, "context");
        return SpreadsheetParser.NO_TOKENS;
    }

    @Override
    public Optional<ValueTypeName> valueType() {
        return Optional.of(
            SpreadsheetValueType.WHOLE_NUMBER
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "whole-number";
    }
}
