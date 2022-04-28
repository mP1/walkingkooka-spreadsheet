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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserTokenVisitorToExpressionTest extends SpreadsheetParserTokenVisitorTestCase<SpreadsheetParserTokenVisitorToExpression> {

    @Test
    public void testNullExpressionNumberKindFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParserTokenVisitorToExpression.toExpression(
                        SpreadsheetParserToken.number(
                                Lists.of(
                                        SpreadsheetParserToken.digits("1", "1")
                                ),
                                "1"),
                        null));
    }

    @Test
    public void testCellReference() {
        this.toExpressionAndCheck(
                SpreadsheetParserToken.cellReference(
                        Lists.of(
                                SpreadsheetParserToken.columnReference(
                                        SpreadsheetSelection.parseColumn("A"),
                                        "A"
                                ),
                                SpreadsheetParserToken.rowReference(
                                        SpreadsheetSelection.parseRow("1"),
                                        "1"
                                )
                        ),
                        "A1"
                ),
                Expression.reference(
                        SpreadsheetSelection.parseCell("A1")
                )
        );
    }

    @Test
    public void testCellRange() {
        this.toExpressionAndCheck(
                SpreadsheetParserToken.cellRange(
                        Lists.of(
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("A"),
                                                        "A"
                                                ),
                                                SpreadsheetParserToken.rowReference(
                                                        SpreadsheetSelection.parseRow("1"),
                                                        "1"
                                                )
                                        ),
                                        "A1"
                                ),
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("B"),
                                                        "B"
                                                ),
                                                SpreadsheetParserToken.rowReference(
                                                        SpreadsheetSelection.parseRow("2"),
                                                        "2"
                                                )
                                        ),
                                        "A1"
                                )
                        ),
                        "A1:B2"
                ),
                Expression.reference(
                        SpreadsheetSelection.parseCellRange("A1:B2")
                )
        );
    }

    private void toExpressionAndCheck(final SpreadsheetParserToken token,
                                      final Expression expression) {
        this.checkEquals(
                Optional.of(
                        expression
                ),
                SpreadsheetParserTokenVisitorToExpression.toExpression(
                        token,
                        ExpressionEvaluationContexts.fake()
                )
        );
    }

    @Override
    public SpreadsheetParserTokenVisitorToExpression createVisitor() {
        return new SpreadsheetParserTokenVisitorToExpression(null);
    }

    @Override
    public Class<SpreadsheetParserTokenVisitorToExpression> type() {
        return SpreadsheetParserTokenVisitorToExpression.class;
    }
}
