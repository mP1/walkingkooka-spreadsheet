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

package walkingkooka.spreadsheet.expression;

import walkingkooka.convert.Converter;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.util.Optional;

public class FakeSpreadsheetExpressionEvaluationContext extends FakeExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext {
    @Override
    public Optional<SpreadsheetCell> cell() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Converter<SpreadsheetExpressionEvaluationContext> converter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetParserToken parseExpression(final TextCursor expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbsoluteUrl serverUrl() {
        throw new UnsupportedOperationException();
    }
}
