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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.Optional;
import java.util.Set;

public interface SpreadsheetStorageContextTesting extends TreePrintableTesting {
    
    // loadCells........................................................................................................
    
    default void loadCellsAndCheck(final SpreadsheetStorageContext context,
                                   final SpreadsheetExpressionReference cellsOrLabel,
                                   final SpreadsheetCell... expected) {
        this.loadCellsAndCheck(
            context,
            cellsOrLabel,
            Sets.of(expected)
        );
    }

    default void loadCellsAndCheck(final SpreadsheetStorageContext context,
                                   final SpreadsheetExpressionReference cellsOrLabel,
                                   final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.loadCells(cellsOrLabel),
            () -> "loadCells " + cellsOrLabel
        );
    }

    // saveCells........................................................................................................

    default void saveCellsAndCheck(final SpreadsheetStorageContext context,
                                   final Set<SpreadsheetCell> cells,
                                   final SpreadsheetCell... expected) {
        this.saveCellsAndCheck(
            context,
            cells,
            Sets.of(expected)
        );
    }

    default void saveCellsAndCheck(final SpreadsheetStorageContext context,
                                   final Set<SpreadsheetCell> cells,
                                   final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            context.saveCells(cells),
            () -> "saveCells " + cells
        );
    }

    // loadForm.........................................................................................................

    default void loadFormAndCheck(final SpreadsheetStorageContext context,
                                  final FormName formName) {
        this.loadFormAndCheck(
            context,
            formName,
            Optional.empty()
        );
    }

    default void loadFormAndCheck(final SpreadsheetStorageContext context,
                                  final FormName formName,
                                  final Form<SpreadsheetValidationReference> expected) {
        this.loadFormAndCheck(
            context,
            formName,
            Optional.of(expected)
        );
    }

    default void loadFormAndCheck(final SpreadsheetStorageContext context,
                                  final FormName formName,
                                  final Optional<Form<SpreadsheetValidationReference>> expected) {
        this.checkEquals(
            expected,
            context.loadForm(formName),
            () -> "loadForm " + formName
        );
    }

    // saveForm........................................................................................................

    default void saveFormAndCheck(final SpreadsheetStorageContext context,
                                  final Form<SpreadsheetValidationReference> form,
                                  final Form<SpreadsheetValidationReference> expected) {
        this.checkEquals(
            expected,
            context.saveForm(form),
            () -> "saveForm " + form
        );
    }

    // findFormsByName.................................................................................................

    default void findFormsByNameAndCheck(final SpreadsheetStorageContext context,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final Form<SpreadsheetValidationReference>... expected) {
        this.findFormsByNameAndCheck(
            context,
            text,
            offset,
            count,
            Sets.of(expected)
        );
    }

    default void findFormsByNameAndCheck(final SpreadsheetStorageContext context,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final Set<Form<SpreadsheetValidationReference>> expected) {
        this.checkEquals(
            expected,
            context.findFormsByName(
                text,
                offset,
                count
            ),
            () -> "findFormsByName " + CharSequences.quoteAndEscape(text) + " offset=" + offset + " count=" + count
        );
    }
    
    // loadLabel........................................................................................................

    default void loadLabelAndCheck(final SpreadsheetStorageContext context,
                                   final SpreadsheetLabelName labels) {
        this.loadLabelAndCheck(
            context,
            labels,
            Optional.empty()
        );
    }

    default void loadLabelAndCheck(final SpreadsheetStorageContext context,
                                   final SpreadsheetLabelName labels,
                                   final SpreadsheetLabelMapping expected) {
        this.loadLabelAndCheck(
            context,
            labels,
            Optional.of(expected)
        );
    }

    default void loadLabelAndCheck(final SpreadsheetStorageContext context,
                                   final SpreadsheetLabelName labels,
                                   final Optional<SpreadsheetLabelMapping> expected) {
        this.checkEquals(
            expected,
            context.loadLabel(labels),
            () -> "loadLabels " + labels
        );
    }

    // saveLabel........................................................................................................

    default void saveLabelAndCheck(final SpreadsheetStorageContext context,
                                   final SpreadsheetLabelMapping label,
                                   final SpreadsheetLabelMapping expected) {
        this.checkEquals(
            expected,
            context.saveLabel(label),
            () -> "saveLabel " + label
        );
    }

    // findLabelsByName.................................................................................................

    default void findLabelsByNameAndCheck(final SpreadsheetStorageContext context,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetLabelName... expected) {
        this.findLabelsByNameAndCheck(
            context,
            text,
            offset,
            count,
            Sets.of(expected)
        );
    }

    default void findLabelsByNameAndCheck(final SpreadsheetStorageContext context,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final Set<SpreadsheetLabelName> expected) {
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
}
