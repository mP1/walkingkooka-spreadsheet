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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetExpressionReferenceLoaderTest implements SpreadsheetExpressionReferenceLoaderTesting<BasicSpreadsheetExpressionReferenceLoader> {

    private final static SpreadsheetCellReference CELL = SpreadsheetSelection.A1;

    @Test
    public void testLoadCell() {
        this.loadCellAndCheck(
                BasicSpreadsheetExpressionReferenceLoader.INSTANCE,
                CELL,
                this.createContext(),
                CELL.setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                                Optional.of(123)
                        )
                )
        );
    }

    @Test
    public void testLoadCellRange() {
        this.loadCellRangeAndCheck(
                BasicSpreadsheetExpressionReferenceLoader.INSTANCE,
                SpreadsheetSelection.parseCellRange("A1:B2"),
                this.createContext(),
                CELL.setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                                Optional.of(111)
                        )
                ),
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setValue(
                                        Optional.of(222)
                                )
                        )
        );
    }

    @Test
    public void testLoadLabelFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> BasicSpreadsheetExpressionReferenceLoader.INSTANCE.loadLabel(
                        SpreadsheetSelection.labelName("Label123")
                )
        );
    }

    @Override
    public BasicSpreadsheetExpressionReferenceLoader createSpreadsheetExpressionReferenceLoader() {
        return BasicSpreadsheetExpressionReferenceLoader.INSTANCE;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext createContext() {
        return new FakeSpreadsheetExpressionEvaluationContext() {
            @Override
            public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
                return Optional.of(
                        cell.setFormula(
                                SpreadsheetFormula.EMPTY.setValue(
                                        Optional.of(123)
                                )
                        )
                );
            }

            @Override
            public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
                return Sets.of(
                        range.begin()
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setValue(
                                                Optional.of(111)
                                        )
                                ),
                        range.end()
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setValue(
                                                Optional.of(222)
                                        )
                                )
                );
            }
        };
    }
}
