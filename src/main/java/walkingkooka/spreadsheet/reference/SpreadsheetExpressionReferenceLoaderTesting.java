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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExpressionReferenceLoaderTesting<T extends SpreadsheetExpressionReferenceLoader> extends TreePrintableTesting {

    // loadCell.........................................................................................................

    @Test
    default void testLoadCellWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExpressionReferenceLoader()
                        .loadCell(
                                null,
                                SpreadsheetExpressionEvaluationContexts.fake()
                        )
        );
    }

    @Test
    default void testLoadCellWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExpressionReferenceLoader()
                        .loadCell(
                                SpreadsheetSelection.A1,
                                null
                        )
        );
    }

    default void loadCellAndCheck(final T loader,
                                  final SpreadsheetCellReference cellReference,
                                  final SpreadsheetExpressionEvaluationContext context,
                                  final Optional<SpreadsheetCell> expected) {
        this.checkEquals(
                expected,
                loader.loadCell(
                        cellReference,
                        context
                ),
                () -> "loadCell " + cellReference
        );
    }

    // loadCellRange........................................................................................................

    @Test
    default void testLoadCellRangeWithNullRangeFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExpressionReferenceLoader()
                        .loadCellRange(
                                null,
                                SpreadsheetExpressionEvaluationContexts.fake()
                        )
        );
    }

    @Test
    default void testLoadCellRangeWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExpressionReferenceLoader()
                        .loadCellRange(
                                SpreadsheetSelection.ALL_CELLS,
                                null
                        )
        );
    }

    default void loadCellRangeAndCheck(final T loader,
                                       final SpreadsheetCellRangeReference range,
                                       final SpreadsheetExpressionEvaluationContext context,
                                       final SpreadsheetCell... expected) {
        this.loadCellRangeAndCheck(
                loader,
                range,
                context,
                SortedSets.of(expected)
        );
    }

    default void loadCellRangeAndCheck(final T loader,
                                       final SpreadsheetCellRangeReference range,
                                       final SpreadsheetExpressionEvaluationContext context,
                                       final Set<SpreadsheetCell> expected) {
        this.checkEquals(
                expected,
                loader.loadCellRange(
                        range,
                        context
                ),
                () -> "loadCellRange " + range
        );
    }

    // loadLabel........................................................................................................

    @Test
    default void testLoadLabelWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExpressionReferenceLoader()
                        .loadLabel(null)
        );
    }

    default void loadLabelAndCheck(final T loader,
                                   final SpreadsheetLabelName labelName,
                                   final Optional<SpreadsheetLabelMapping> expected) {
        this.checkEquals(
                expected,
                loader.loadLabel(labelName),
                () -> "loadLabel " + labelName
        );
    }

    // createLoader.....................................................................................................

    T createSpreadsheetExpressionReferenceLoader();


    SpreadsheetExpressionEvaluationContext createContext();
}
