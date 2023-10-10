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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.Predicates;
import walkingkooka.test.ParseStringTesting;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SpreadsheetViewportSelectionNavigationTestCase2<T extends SpreadsheetViewportSelectionNavigation> extends
        SpreadsheetViewportSelectionNavigationTestCase<T> implements ParseStringTesting<List<T>> {

    SpreadsheetViewportSelectionNavigationTestCase2() {
        super();
    }

    // parse............................................................................................................

    @Override
    public final void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public final void testParseToStringRoundtrip() {
        final T navigation = this.createSpreadsheetViewportSelectionNavigation();

        this.parseStringAndCheck(
                navigation.text(),
                Lists.of(
                        navigation
                )
        );
    }

    // update...........................................................................................................

    final static int COLUMN_WIDTH = 100;

    final static int ROW_HEIGHT = 30;

    void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                        final SpreadsheetSelection selection) {
        this.updateAndCheck(
                navigation,
                selection,
                selection
        );
    }

    void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                        final SpreadsheetSelection selection,
                        final SpreadsheetSelection expected) {
        this.updateAndCheck(
                navigation,
                selection,
                expected.setAnchor(expected.defaultAnchor())
        );
    }

    void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                        final SpreadsheetSelection selection,
                        final SpreadsheetViewport expected) {
        this.updateAndCheck(
                navigation,
                selection,
                selection.defaultAnchor(),
                expected
        );
    }

    void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                        final SpreadsheetSelection selection,
                        final SpreadsheetViewportSelectionAnchor anchor,
                        final SpreadsheetViewport expected) {
        this.updateAndCheck(
                navigation,
                selection,
                anchor,
                Predicates.never(),
                Predicates.never(),
                expected
        );
    }

    void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                        final SpreadsheetSelection selection,
                        final SpreadsheetViewportSelectionAnchor anchor,
                        final Predicate<SpreadsheetColumnReference> hiddenColumns,
                        final Predicate<SpreadsheetRowReference> hiddenRows,
                        final SpreadsheetViewport expected) {
        this.updateAndCheck(
                navigation,
                selection,
                anchor,
                hiddenColumns,
                hiddenRows,
                Optional.of(expected)
        );
    }

    void updateAndCheck(final SpreadsheetViewportSelectionNavigation navigation,
                        final SpreadsheetSelection selection,
                        final SpreadsheetViewportSelectionAnchor anchor,
                        final Predicate<SpreadsheetColumnReference> hiddenColumns,
                        final Predicate<SpreadsheetRowReference> hiddenRows,
                        final Optional<SpreadsheetViewport> expected) {
        this.checkEquals(
                expected,
                navigation.update(
                        selection,
                        anchor,
                        SpreadsheetViewportSelectionNavigationContexts.basic(
                                hiddenColumns,
                                COLUMN_TO_WIDTH,
                                hiddenRows,
                                ROW_TO_HEIGHT
                        )
                ),
                () -> navigation + " update " + selection + " " + anchor
        );
    }

    final static Function<SpreadsheetColumnReference, Double> COLUMN_TO_WIDTH = (c) -> 1.0 * COLUMN_WIDTH;

    final static Function<SpreadsheetRowReference, Double> ROW_TO_HEIGHT = (c) -> 1.0 * ROW_HEIGHT;

    abstract T createSpreadsheetViewportSelectionNavigation();

    // ParseString......................................................................................................

    @Override
    public final List<T> parseString(final String string) {
        return Cast.to(
                SpreadsheetViewportSelectionNavigation.parse(string)
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
