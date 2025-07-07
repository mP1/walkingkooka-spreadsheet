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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class BooleanSpreadsheetFormulaParserTokenTest extends ValueSpreadsheetFormulaParserTokenTestCase<BooleanSpreadsheetFormulaParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken("true");
    }

    @Test
    public void testToExpressionDayWithTrue() {
        this.toExpressionAndCheck(
            booleanValue(
                true,
                "true"
            ),
            Expression.value(true)
        );
    }

    @Test
    public void testToExpressionDayWithFalse() {
        this.toExpressionAndCheck(
            booleanValue(
                false,
                "false"
            ),
            Expression.value(false)
        );
    }


    @Override
    BooleanSpreadsheetFormulaParserToken createToken(final String text,
                                                     final List<ParserToken> tokens) {
        return BooleanSpreadsheetFormulaParserToken.with(
            tokens,
            text
        );
    }

    @Override
    List<ParserToken> tokens() {
        return List.of(
            booleanLiteralTrue()
        );
    }

    @Override
    public String text() {
        return booleanLiteralTrue()
            .toString();
    }

    @Override
    public BooleanSpreadsheetFormulaParserToken createDifferentToken() {
        return this.createToken(
            booleanLiteralFalse().text(),
            booleanLiteralFalse()
        );
    }

    // json.............................................................................................................

    @Override
    public BooleanSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallBoolean(
            from,
            context
        );
    }

    // class............................................................................................................

    @Override
    public Class<BooleanSpreadsheetFormulaParserToken> type() {
        return BooleanSpreadsheetFormulaParserToken.class;
    }
}
