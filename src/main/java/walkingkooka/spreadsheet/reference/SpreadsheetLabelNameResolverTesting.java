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
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetLabelNameResolverTesting extends TreePrintableTesting {

    @Test
    default void testResolveIfLabelWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.spreadsheetLabelNameResolver()
                        .resolveIfLabel(null)
        );
    }

    @Test
    default void testResolveIfLabelWithCell() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    default void testResolveIfLabelWithColumn() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseColumn("Z")
        );
    }

    @Test
    default void testResolveIfLabelWithColumnRange() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseColumnRange("X:Y")
        );
    }

    @Test
    default void testResolveIfLabelWithRow() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    default void testResolveIfLabelWithRowRange() {
        this.resolveIfLabelAndCheck(
                SpreadsheetSelection.parseRowRange("3:4")
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetSelection selection) {
        this.resolveIfLabelAndCheck(
                selection,
                selection
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetSelection selection,
                                        final SpreadsheetSelection expected) {
        this.resolveIfLabelAndCheck(
                this.spreadsheetLabelNameResolver(),
                selection,
                expected
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetLabelNameResolver resolver,
                                        final SpreadsheetSelection selection,
                                        final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                resolver.resolveIfLabel(selection),
                () -> "resolveIfLabel " + selection
        );
    }

    // resolveIfLabelFails..............................................................................................

    default void resolveIfLabelFails(final SpreadsheetSelection selection) {
        this.resolveIfLabelFails(
                this.spreadsheetLabelNameResolver(),
                selection
        );
    }

    default void resolveIfLabelFails(final SpreadsheetLabelNameResolver resolver,
                                     final SpreadsheetSelection selection) {
        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolveIfLabel(selection),
                () -> "resolveIfLabelFalls " + selection
        );
    }

    // resolveLabel.....................................................................................................

    @Test
    default void testResolveLabelWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.spreadsheetLabelNameResolver()
                        .resolveLabel(null)
        );
    }

    default void resolveLabelAndCheck(final String labelName,
                                      final SpreadsheetSelection expected) {
        this.resolveLabelAndCheck(
                SpreadsheetSelection.labelName(labelName),
                expected
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelName labelName,
                                      final SpreadsheetSelection expected) {
        this.resolveLabelAndCheck(
                this.spreadsheetLabelNameResolver(),
                labelName,
                expected
        );
    }

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
                                      final SpreadsheetLabelName labelName,
                                      final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                resolver.resolveLabel(labelName),
                () -> "resolveLabel " + labelName
        );
    }

    // resolveLabelFails................................................................................................

    default void resolveLabelFails(final String labelName) {
        this.resolveLabelFails(
                SpreadsheetSelection.labelName(labelName)
        );
    }

    default void resolveLabelFails(final SpreadsheetLabelName labelName) {
        this.resolveLabelFails(
                this.spreadsheetLabelNameResolver(),
                labelName
        );
    }

    default void resolveLabelFails(final SpreadsheetLabelNameResolver resolver,
                                   final String labelName) {
        this.resolveLabelFails(
                resolver,
                SpreadsheetSelection.labelName(labelName)
        );
    }

    default void resolveLabelFails(final SpreadsheetLabelNameResolver resolver,
                                   final SpreadsheetLabelName labelName) {
        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolveLabel(labelName),
                () -> "resolveLabel " + labelName
        );
    }

    // spreadsheetLabelNameResolver.....................................................................................

    SpreadsheetLabelNameResolver spreadsheetLabelNameResolver();
}
