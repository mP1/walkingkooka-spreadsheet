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

import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.provider.FakeSpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.text.TextNode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public class FakeSpreadsheetEngineContext extends FakeSpreadsheetProvider implements SpreadsheetEngineContext, Fake {

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDateTime now() {
        throw new UnsupportedOperationException();
    }
    
    // formula..........................................................................................................
    
    @Override
    public SpreadsheetParserToken parseFormula(final TextCursor formula) {
        Objects.requireNonNull(formula, "formula");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetParserToken token) {
        Objects.requireNonNull(token, "token");
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final Expression expression,
                           final Optional<SpreadsheetCell> cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean evaluateAsBoolean(final Expression expression,
                                     final Optional<SpreadsheetCell> cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPure(final ExpressionFunctionName function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<TextNode> formatValue(final Object value,
                                          final SpreadsheetFormatter formatter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                               final Optional<SpreadsheetFormatter> formatter) {
        throw new UnsupportedOperationException();
    }

    // sort.............................................................................................................

    @Override
    public SpreadsheetCellRange sortCells(final SpreadsheetCellRange cells,
                                          final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators,
                                          final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCells) {
        throw new UnsupportedOperationException();
    }

    // ProviderContext..................................................................................................

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> environmentValueName) {
        throw new UnsupportedOperationException();
    }

    // storerespository.................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        throw new UnsupportedOperationException();
    }
}
