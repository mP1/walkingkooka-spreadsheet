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

import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;

public interface SpreadsheetLabelNameResolverTesting extends TreePrintableTesting {

    default void resolveIfLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                        final SpreadsheetSelection selection,
                                        final SpreadsheetSelection expected) {
        this.resolveIfLabelAndCheck(
            resolver,
            selection,
            Optional.of(expected)
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                        final SpreadsheetSelection selection,
                                        final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            resolver.resolveIfLabel(selection),
            () -> "resolveIfLabel " + selection
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                        final SpreadsheetSelection selection) {
        this.resolveIfLabelAndCheck(
            resolver,
            selection,
            Optional.empty()
        );
    }

    // resolveLabel.....................................................................................................


    default void resolveLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                      final String labelName,
                                      final SpreadsheetSelection expected) {
        this.resolveLabelAndCheck(
            resolver,
            SpreadsheetSelection.labelName(labelName),
            expected
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                      final SpreadsheetLabelName labelName) {
        this.resolveLabelAndCheck(
            resolver,
            labelName,
            Optional.empty()
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                      final SpreadsheetLabelName labelName,
                                      final SpreadsheetSelection expected) {
        this.resolveLabelAndCheck(
            resolver,
            labelName,
            Optional.of(expected)
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                      final SpreadsheetLabelName labelName,
                                      final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            resolver.resolveLabel(labelName),
            () -> "resolveLabel " + labelName
        );
    }
}
