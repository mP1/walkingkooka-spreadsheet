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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest extends BasicSpreadsheetEngineSpreadsheetParserTokenVisitorTestCase<BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static char VALUE_SEPARATOR = ',';

    @Test
    public void testZeroZeroOffset() {
        final SpreadsheetParserToken token = SpreadsheetParsers.cellOrCellRangeOrLabel()
                .parse(TextCursors.charSequence("$A$1"), SpreadsheetParserContexts.basic(
                                DateTimeContexts.fake(),
                                ExpressionNumberContexts.basic(
                                        EXPRESSION_NUMBER_KIND,
                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                ),
                                VALUE_SEPARATOR
                        )
                )
                .map(SpreadsheetParserToken.class::cast)
                .orElseThrow(() -> new Error("Unable to parseFormula"));
        assertSame(
                token,
                BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(
                        token,
                        0,
                        0
                )
        );
    }

    @Test
    public void testToString() {
        final BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor visitor = new BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(12, 34);
        visitor.startVisit(
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits("1", "1")
                        ),
                        "1")
        );
        this.toStringAndCheck(visitor, "12,34 [], [[]]");
    }

    @Override
    public BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createVisitor() {
        return new BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(0, 0);
    }

    @Override
    public Class<BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> type() {
        return BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }
}
