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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetComparatorProviderTesting<T extends SpreadsheetComparatorProvider> extends ProviderTesting<T> {

    // spreadsheetComparator(SpreadsheetComparatorSelector, ProviderContext)............................................

    @Test
    default void testSpreadsheetComparatorSelectorWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetComparatorProvider()
                .spreadsheetComparator(
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetComparatorSelectorWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetComparatorProvider()
                .spreadsheetComparator(
                    SpreadsheetComparatorSelector.parse("comparator123"),
                    null
                )
        );
    }

    default void spreadsheetComparatorFails(final String selector,
                                            final ProviderContext context) {
        this.spreadsheetComparatorFails(
            this.createSpreadsheetComparatorProvider(),
            SpreadsheetComparatorSelector.parse(selector),
            context
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorProvider provider,
                                            final String selector,
                                            final ProviderContext context) {
        this.spreadsheetComparatorFails(
            provider,
            SpreadsheetComparatorSelector.parse(selector),
            context
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorSelector selector,
                                            final ProviderContext context) {
        this.spreadsheetComparatorFails(
            this.createSpreadsheetComparatorProvider(),
            selector,
            context
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorProvider provider,
                                            final SpreadsheetComparatorSelector selector,
                                            final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetComparator(
                selector,
                context
            )
        );
    }

    default void spreadsheetComparatorAndCheck(final String selector,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
            this.createSpreadsheetComparatorProvider(),
            SpreadsheetComparatorSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final String selector,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
            provider,
            SpreadsheetComparatorSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorSelector selector,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
            this.createSpreadsheetComparatorProvider(),
            selector,
            context,
            expected
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorSelector selector,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetComparator(
                selector,
                context
            ),
            selector::toString
        );
    }

    // SpreadsheetComparator(SpreadsheetComparatorName, List, ProviderContext)..........................................

    @Test
    default void testSpreadsheetComparatorNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetComparatorProvider()
                .spreadsheetComparator(
                    null,
                    Lists.empty(),
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetComparatorNameWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetComparatorProvider()
                .spreadsheetComparator(
                    SpreadsheetComparatorName.with("comparator-123"),
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetComparatorNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetComparatorProvider()
                .spreadsheetComparator(
                    SpreadsheetComparatorSelector.with(
                        SpreadsheetComparatorName.with("comparator-123"),
                        ""
                    ),
                    null
                )
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorName name,
                                            final List<?> values,
                                            final ProviderContext context) {
        this.spreadsheetComparatorFails(
            this.createSpreadsheetComparatorProvider(),
            name,
            values,
            context
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorProvider provider,
                                            final SpreadsheetComparatorName name,
                                            final List<?> values,
                                            final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetComparator(
                name,
                values,
                context
            )
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorName name,
                                               final List<?> values,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
            this.createSpreadsheetComparatorProvider(),
            name,
            values,
            context,
            expected
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorName name,
                                               final List<?> values,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetComparator(
                name,
                values,
                context
            ),
            () -> name + " " + values
        );
    }

    // spreadsheetComparatorInfos.......................................................................................

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorInfo... expected) {
        this.spreadsheetComparatorInfosAndCheck(
            this.createSpreadsheetComparatorProvider(),
            expected
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorProvider provider,
                                                    final SpreadsheetComparatorInfo... expected) {
        this.spreadsheetComparatorInfosAndCheck(
            provider,
            SpreadsheetComparatorInfoSet.with(
                Sets.of(
                    expected
                )
            )
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorInfoSet expected) {
        this.spreadsheetComparatorInfosAndCheck(
            this.createSpreadsheetComparatorProvider(),
            expected
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorProvider provider,
                                                    final SpreadsheetComparatorInfoSet expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetComparatorInfos(),
            provider::toString
        );
    }

    T createSpreadsheetComparatorProvider();
}
