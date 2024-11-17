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

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetConditionRightGreaterThanEqualsParserTokenTest extends SpreadsheetConditionRightParserTokenTestCase<SpreadsheetConditionRightGreaterThanEqualsParserToken> {
    @Override
    SpreadsheetSymbolParserToken symbolParserToken() {
        return SpreadsheetParserToken.greaterThanEqualsSymbol(
                ">=",
                ">="
        );
    }

    @Override
    SpreadsheetConditionRightGreaterThanEqualsParserToken createToken(final String text,
                                                                      final List<ParserToken> tokens) {
        return SpreadsheetConditionRightGreaterThanEqualsParserToken.with(
                tokens,
                text
        );
    }

    @Override
    public SpreadsheetConditionRightGreaterThanEqualsParserToken unmarshall(final JsonNode json,
                                                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallConditionRightGreaterThanEquals(
                json,
                context
        );
    }

    @Override
    public Class<SpreadsheetConditionRightGreaterThanEqualsParserToken> type() {
        return SpreadsheetConditionRightGreaterThanEqualsParserToken.class;
    }
}
