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
import walkingkooka.ToStringTesting;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetViewportSelectionNavigationContextTest implements ClassTesting<BasicSpreadsheetViewportSelectionNavigationContext>,
        SpreadsheetViewportSelectionNavigationContextTesting,
        ToStringTesting<BasicSpreadsheetViewportSelectionNavigationContext> {

    @Test
    public void testWithNullColumnHiddenFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetViewportSelectionNavigationContext.with(
                        null,
                        Predicates.fake()
                )
        );
    }

    @Test
    public void testWithNullRowHiddenFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        null
                )
        );
    }

    @Test
    public void testIsColumnHidden() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");

        final BasicSpreadsheetViewportSelectionNavigationContext context = BasicSpreadsheetViewportSelectionNavigationContext.with(
                Predicates.is(column),
                Predicates.fake()
        );
        this.isColumnHiddenAndCheck(
                context,
                column,
                true
        );

        this.isColumnHiddenAndCheck(
                context,
                column.add(1),
                false
        );
    }

    @Test
    public void testIsRowHidden() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("123");

        final BasicSpreadsheetViewportSelectionNavigationContext context = BasicSpreadsheetViewportSelectionNavigationContext.with(
                Predicates.fake(),
                Predicates.is(row)
        );

        this.isRowHiddenAndCheck(
                context,
                row,
                true
        );

        this.isRowHiddenAndCheck(
                context,
                row.add(1),
                false
        );
    }

    @Test
    public void testToString() {
        final Predicate<SpreadsheetColumnReference> column = Predicates.fake();
        final Predicate<SpreadsheetRowReference> row = Predicates.fake();

        this.toStringAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        column,
                        row
                ),
                column + " " + row
        );
    }

    @Override
    public Class<BasicSpreadsheetViewportSelectionNavigationContext> type() {
        return BasicSpreadsheetViewportSelectionNavigationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
