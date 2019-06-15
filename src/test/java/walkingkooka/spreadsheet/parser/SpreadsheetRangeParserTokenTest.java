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
import walkingkooka.tree.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetRangeParserTokenTest extends SpreadsheetBinaryParserTokenTestCase<SpreadsheetRangeParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetBinaryParserToken binary = this.createToken();
        final SpreadsheetParserToken left = binary.left();
        final SpreadsheetParserToken right = binary.right();
        final SpreadsheetParserToken symbol = operatorSymbol();

        new FakeSpreadsheetParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetParserToken n) {
                b.append("1");
                visited.add(n);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetParserToken n) {
                b.append("2");
                visited.add(n);
            }

            @Override
            protected Visiting startVisit(final SpreadsheetRangeParserToken t) {
                assertSame(binary, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetRangeParserToken t) {
                assertSame(binary, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetLabelNameParserToken t) {
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetBetweenSymbolParserToken t) {
                b.append("6");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("7");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("8");
                visited.add(t);
            }
        }.accept(binary);
        assertEquals("713715287162871528428", b.toString());
        assertEquals(Lists.of(binary, binary, binary,
                left, left, left, left, left,
                symbol, symbol, symbol, symbol, symbol,
                right, right, right, right, right,
                binary, binary, binary),
                visited,
                "visited");
    }

    @Override
    SpreadsheetRangeParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.range(tokens, text);
    }

    @Override
    SpreadsheetParserToken leftToken() {
        return SpreadsheetParserToken.labelName(SpreadsheetLabelName.with("left"), "left");
    }

    @Override
    SpreadsheetParserToken rightToken() {
        return SpreadsheetParserToken.labelName(SpreadsheetLabelName.with("right"), "right");
    }

    @Override
    SpreadsheetParserToken operatorSymbol() {
        return SpreadsheetParserToken.betweenSymbol(":", ":");
    }

    @Override
    public Class<SpreadsheetRangeParserToken> type() {
        return SpreadsheetRangeParserToken.class;
    }
}
