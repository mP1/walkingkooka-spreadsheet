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

public final class SpreadsheetConditionRightLessThanParserTokenTest extends SpreadsheetConditionRightParserTokenTestCase<SpreadsheetConditionRightLessThanParserToken> {

    @Test
    public void testSetConditionLeft() {
        final SpreadsheetConditionRightLessThanParserToken token = this.createToken();
        final SpreadsheetParserToken left = number("999");

        this.setConditionLeftAndCheck(
                token,
                left,
                SpreadsheetParserToken.lessThan(
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
        return SpreadsheetParserToken.lessThanSymbol(
                "<",
                "<"
        );
    }

    @Override
    SpreadsheetConditionRightLessThanParserToken createToken(final String text,
                                                             final List<ParserToken> tokens) {
        return SpreadsheetConditionRightLessThanParserToken.with(
                tokens,
                text
        );
    }

    @Override
    public SpreadsheetConditionRightLessThanParserToken unmarshall(final JsonNode json,
                                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallConditionRightLessThan(
                json,
                context
        );
    }

    @Override
    public Class<SpreadsheetConditionRightLessThanParserToken> type() {
        return SpreadsheetConditionRightLessThanParserToken.class;
    }
}
