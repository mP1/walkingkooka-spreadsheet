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
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetLabelNameResolverTesting<R extends SpreadsheetLabelNameResolver> extends TreePrintableTesting {

    @Test
    default void testResolveIfLabelWithNullExpressionReferenceFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetLabelNameResolver()
                        .resolveIfLabel((ExpressionReference) null)
        );
    }

    @Test
    default void testResolveIfLabelWithNullSpreadsheetSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetLabelNameResolver()
                        .resolveIfLabel((SpreadsheetSelection) null)
        );
    }

    @Test
    default void testResolveIfLabelWithCell() {
        this.resolveIfLabelAndCheckSame(
                SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    default void testResolveIfLabelWithCellRange() {
        this.resolveIfLabelAndCheckSame(
                SpreadsheetSelection.parseCellRange("C3:D4")
        );
    }

    @Test
    default void testResolveIfLabelWithColumn() {
        this.resolveIfLabelAndCheckSame(
                SpreadsheetSelection.parseColumn("Z")
        );
    }

    @Test
    default void testResolveIfLabelWithColumnRange() {
        this.resolveIfLabelAndCheckSame(
                SpreadsheetSelection.parseColumnRange("X:Y")
        );
    }

    @Test
    default void testResolveIfLabelWithRow() {
        this.resolveIfLabelAndCheckSame(
                SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    default void testResolveIfLabelWithRowRange() {
        this.resolveIfLabelAndCheckSame(
                SpreadsheetSelection.parseRowRange("3:4")
        );
    }

    default void resolveIfLabelAndCheckSame(final SpreadsheetSelection selection) {
        this.resolveIfLabelAndCheck(
                selection,
                selection
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetSelection selection) {
        this.resolveIfLabelAndCheck(
                this.createSpreadsheetLabelNameResolver(),
                selection,
                Optional.empty()
        );
    }

    default void resolveIfLabelAndCheck(final SpreadsheetSelection selection,
                                        final SpreadsheetSelection expected) {
        this.resolveIfLabelAndCheck(
                this.createSpreadsheetLabelNameResolver(),
                selection,
                expected
        );
    }

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

    @Test
    default void testResolveLabelWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetLabelNameResolver()
                        .resolveLabel(null)
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelName labelName) {
        this.resolveLabelAndCheck(
                labelName,
                Optional.empty()
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelName labelName,
                                      final SpreadsheetSelection expected) {
        this.resolveLabelAndCheck(
                labelName,
                Optional.of(expected)
        );
    }

    default void resolveLabelAndCheck(final SpreadsheetLabelName labelName,
                                      final Optional<SpreadsheetSelection> expected) {
        this.resolveLabelAndCheck(
                this.createSpreadsheetLabelNameResolver(),
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

    // createSpreadsheetLabelNameResolver.....................................................................................

    R createSpreadsheetLabelNameResolver();
}
