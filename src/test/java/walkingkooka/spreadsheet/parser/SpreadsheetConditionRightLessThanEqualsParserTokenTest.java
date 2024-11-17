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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetConditionRightLessThanEqualsParserTokenTest extends SpreadsheetConditionRightParserTokenTestCase<SpreadsheetConditionRightLessThanEqualsParserToken> {

    @Test
    public void testSetConditionLeft() {
        final SpreadsheetConditionRightLessThanEqualsParserToken token = this.createToken();
        final SpreadsheetParserToken left = number("999");

        this.setConditionLeftAndCheck(
                token,
                left,
                SpreadsheetParserToken.lessThanEquals(
                        Lists.of(
                                left,
                                token
                        ),
                        left.text() + token.text()
                )
        );
    }
    
    @Override
    SpreadsheetSymbolParserToken symbolParserToken() {
        return SpreadsheetParserToken.lessThanEqualsSymbol(
                "<=",
                "<="
        );
    }

    @Override
    SpreadsheetConditionRightLessThanEqualsParserToken createToken(final String text,
                                                                   final List<ParserToken> tokens) {
        return SpreadsheetConditionRightLessThanEqualsParserToken.with(
                tokens,
                text
        );
    }

    @Override
    public SpreadsheetConditionRightLessThanEqualsParserToken unmarshall(final JsonNode json,
                                                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallConditionRightLessThanEquals(
                json,
                context
        );
    }

    @Override
    public Class<SpreadsheetConditionRightLessThanEqualsParserToken> type() {
        return SpreadsheetConditionRightLessThanEqualsParserToken.class;
    }
}
