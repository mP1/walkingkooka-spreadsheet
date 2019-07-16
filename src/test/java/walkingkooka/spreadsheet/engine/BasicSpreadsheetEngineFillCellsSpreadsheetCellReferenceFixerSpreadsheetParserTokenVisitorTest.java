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
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitorTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.type.JavaVisibility;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest implements SpreadsheetParserTokenVisitorTesting<BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Test
    public void testZeroZeroOffset() {
        final SpreadsheetParserToken token = SpreadsheetParsers.cellReferences().parse(TextCursors.charSequence("$A$1"), SpreadsheetParserContexts.basic(DecimalNumberContexts.american(MathContext.DECIMAL32)))
                .map(SpreadsheetParserToken.class::cast)
                .orElseThrow(() -> new Error("Unable to parse"));
        assertSame(token,
                BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token, 0, 0));
    }

    @Test
    public void testToString() {
        final BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor visitor = new BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(12, 34);
        visitor.visit(SpreadsheetParserToken.doubleParserToken(1.24, "1.24"));
        this.toStringAndCheck(visitor, "12,34 [1.24], []");
    }

    @Override
    public BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createVisitor() {
        return new BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(0, 0);
    }

    @Override
    public String typeNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> type() {
        return BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
