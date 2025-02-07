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

package walkingkooka.spreadsheet.template;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.template.TemplateValueName;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A Context that leverages Spreadsheet components to parse and render templates.
 */
public interface SpreadsheetTemplateContext extends SpreadsheetParserContext,
        SpreadsheetExpressionEvaluationContext {

    /**
     * {@see TemplateContext#templateValue}
     */
    String templateValue(final TemplateValueName name);

    @Override
    default SpreadsheetTemplateContext setCell(final Optional<SpreadsheetCell> cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetCell> cell() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    default Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        throw new UnsupportedOperationException();
    }

    @Override
    default SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        throw new UnsupportedOperationException();
    }
}
