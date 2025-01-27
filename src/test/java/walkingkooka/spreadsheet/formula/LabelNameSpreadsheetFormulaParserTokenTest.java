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
package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LabelNameSpreadsheetFormulaParserTokenTest extends NonSymbolSpreadsheetFormulaParserTokenTestCase<LabelNameSpreadsheetFormulaParserToken, SpreadsheetLabelName>
        implements HasSpreadsheetReferenceTesting {

    @Test
    public void testWithEmptyTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(""));
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final LabelNameSpreadsheetFormulaParserToken token = this.createToken();

        new FakeSpreadsheetFormulaParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetFormulaParserToken t) {
                assertSame(token, t);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormulaParserToken t) {
                assertSame(token, t);
                b.append("4");
            }

            @Override
            protected void visit(final LabelNameSpreadsheetFormulaParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        this.checkEquals("13542", b.toString());
    }

    @Override
    public String text() {
        return "Hello";
    }

    @Override
    SpreadsheetLabelName value() {
        return SpreadsheetSelection.labelName(this.text());
    }

    @Override
    LabelNameSpreadsheetFormulaParserToken createToken(final SpreadsheetLabelName value, final String text) {
        return LabelNameSpreadsheetFormulaParserToken.with(value, text);
    }

    @Override
    public LabelNameSpreadsheetFormulaParserToken createDifferentToken() {
        return LabelNameSpreadsheetFormulaParserToken.with(
                SpreadsheetSelection.labelName("different"),
                "different"
        );
    }

    @Override
    public Class<LabelNameSpreadsheetFormulaParserToken> type() {
        return LabelNameSpreadsheetFormulaParserToken.class;
    }

    @Override
    public LabelNameSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                             final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallLabelName(from, context);
    }

    // HasSpreadsheetReference.........................................................................................

    @Test
    public void testReference() {
        this.referenceAndCheck(
                this.createToken(),
                this.value()
        );
    }
}
