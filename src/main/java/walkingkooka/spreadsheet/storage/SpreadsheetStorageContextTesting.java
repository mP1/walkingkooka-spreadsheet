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

package walkingkooka.spreadsheet.storage;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.ConverterLikeTesting;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StorageContextTesting;
import walkingkooka.text.CharSequences;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetStorageContextTesting<C extends SpreadsheetStorageContext> extends StorageContextTesting<C>,
    SpreadsheetEnvironmentContextTesting2<C>,
    ConverterLikeTesting<C> {

    // loadCells........................................................................................................

    @Test
    default void testLoadCellsWithNullCellOrLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadCells(null)
        );
    }

    default void loadCellsAndCheck(final C context,
                                   final SpreadsheetExpressionReference cellsOrLabel,
                                   final SpreadsheetCell... expected) {
        this.loadCellsAndCheck(
            context,
            cellsOrLabel,
            Sets.of(expected)
        );
    }

    default void loadCellsAndCheck(final C context,
                                   final SpreadsheetExpressionReference cellsOrLabel,
                                   final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadCells(cellsOrLabel),
            () -> "loadCells " + cellsOrLabel
        );
    }

    // saveCells........................................................................................................

    @Test
    default void testSaveCellsWithNullCellOrLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveCells(null)
        );
    }

    default void saveCellsAndCheck(final C context,
                                   final Set<SpreadsheetCell> cells,
                                   final SpreadsheetCell... expected) {
        this.checkEquals(
            expected,
            context.saveCells(cells),
            () -> "saveCells " + cells
        );
    }

    // deleteCells......................................................................................................

    @Test
    default void testDeleteCellsWithNullCellOrLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteCells(null)
        );
    }

    // loadLabels.......................................................................................................

    @Test
    default void testLoadLabelsWithNullLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadLabels(null)
        );
    }

    default void loadLabelsAndCheck(final C context,
                                    final SpreadsheetLabelName labels,
                                    final SpreadsheetCell... expected) {
        this.loadLabelsAndCheck(
            context,
            labels,
            Sets.of(expected)
        );
    }

    default void loadLabelsAndCheck(final C context,
                                    final SpreadsheetLabelName labels,
                                    final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadLabels(labels),
            () -> "loadLabels " + labels
        );
    }

    // saveLabels.......................................................................................................

    @Test
    default void testSaveLabelsWithNullLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveLabels(null)
        );
    }

    default void saveLabelsAndCheck(final C context,
                                    final Set<SpreadsheetLabelMapping> labels,
                                    final SpreadsheetCell... expected) {
        this.checkEquals(
            expected,
            context.saveLabels(labels),
            () -> "saveLabels " + labels
        );
    }

    // deleteLabels.....................................................................................................

    @Test
    default void testDeleteLabelsWithLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteLabels(null)
        );
    }

    // findLabelsByName.................................................................................................

    @Test
    default void testFindLabelsByNameWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .findLabelsByName(
                    null,
                    0, // offset
                    0 // count,
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findLabelsByName(
                    "",
                    -1, // offset
                    0 // count,
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findLabelsByName(
                    "",
                    0, // offset
                    -1 // count,
                )
        );
    }

    default void findLabelsByNameAndCheck(final C context,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetLabelMapping... expected) {
        this.findLabelsByNameAndCheck(
            context,
            text,
            offset,
            count,
            Sets.of(expected)
        );
    }

    default void findLabelsByNameAndCheck(final C context,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final Set<SpreadsheetLabelMapping> expected) {
        this.checkEquals(
            expected,
            context.findLabelsByName(
                text,
                offset,
                count
            ),
            () -> "findLabelsByName " + CharSequences.quoteAndEscape(text) + " offset=" + offset + " count=" + count
        );
    }

    // ConverterLike....................................................................................................

    @Override
    default C createConverterLike() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default void testTypeNaming() {
        StorageContextTesting.super.testTypeNaming();
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetStorageContext.class.getSimpleName();
    }
}
