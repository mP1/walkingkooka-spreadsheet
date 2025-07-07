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

package walkingkooka.spreadsheet.formula.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class ConditionRightLessThanEqualsSpreadsheetFormulaParserTokenTest extends ConditionRightSpreadsheetFormulaParserTokenTestCase<ConditionRightLessThanEqualsSpreadsheetFormulaParserToken> {

    @Test
    public void testSetConditionLeft() {
        final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token = this.createToken();
        final SpreadsheetFormulaParserToken left = number("999");

        this.setConditionLeftAndCheck(
            token,
            left,
            SpreadsheetFormulaParserToken.lessThanEquals(
                Lists.of(
                    left,
                    token
                ),
                left.text() + token.text()
            )
        );
    }

    @Override
    SymbolSpreadsheetFormulaParserToken symbolParserToken() {
        return SpreadsheetFormulaParserToken.lessThanEqualsSymbol(
            "<=",
            "<="
        );
    }

    @Override
    ConditionRightLessThanEqualsSpreadsheetFormulaParserToken createToken(final String text,
                                                                          final List<ParserToken> tokens) {
        return ConditionRightLessThanEqualsSpreadsheetFormulaParserToken.with(
            tokens,
            text
        );
    }

    @Override
    public ConditionRightLessThanEqualsSpreadsheetFormulaParserToken unmarshall(final JsonNode json,
                                                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallConditionRightLessThanEquals(
            json,
            context
        );
    }

    @Override
    public Class<ConditionRightLessThanEqualsSpreadsheetFormulaParserToken> type() {
        return ConditionRightLessThanEqualsSpreadsheetFormulaParserToken.class;
    }
}
