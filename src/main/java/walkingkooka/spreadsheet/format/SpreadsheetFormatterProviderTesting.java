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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderTesting;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetFormatterProviderTesting<T extends SpreadsheetFormatterProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetFormatterWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetFormatterProvider().spreadsheetFormatter(null)
        );
    }

    T createSpreadsheetFormatterProvider();

    // SpreadsheetFormatter(SpreadsheetFormatterSelector)...............................................................

    default void spreadsheetFormatterFails(final String selector) {
        this.spreadsheetFormatterFails(
                this.createSpreadsheetFormatterProvider(),
                SpreadsheetFormatterSelector.parse(selector)
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterProvider provider,
                                           final String selector) {
        this.spreadsheetFormatterFails(
                provider,
                SpreadsheetFormatterSelector.parse(selector)
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterFails(
                this.createSpreadsheetFormatterProvider(),
                selector
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterProvider provider,
                                           final SpreadsheetFormatterSelector selector) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetFormatter(selector)
        );
    }

    default void spreadsheetFormatterAndCheck(final String selector,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
                this.createSpreadsheetFormatterProvider(),
                SpreadsheetFormatterSelector.parse(selector),
                expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final String selector,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
                provider,
                SpreadsheetFormatterSelector.parse(selector),
                expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterSelector selector,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
                this.createSpreadsheetFormatterProvider(),
                selector,
                expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterSelector selector,
                                              final SpreadsheetFormatter expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatter(selector),
                selector::toString
        );
    }

    // SpreadsheetFormatter(SpreadsheetFormatterSelector)...............................................................

    default void spreadsheetFormatterFails(final SpreadsheetFormatterName name,
                                           final List<?> values) {
        this.spreadsheetFormatterFails(
                this.createSpreadsheetFormatterProvider(),
                name,
                values
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterProvider provider,
                                           final SpreadsheetFormatterName name,
                                           final List<?> values) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetFormatter(
                        name,
                        values
                )
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterName name,
                                              final List<?> values,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
                this.createSpreadsheetFormatterProvider(),
                name,
                values,
                expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterName name,
                                              final List<?> values,
                                              final SpreadsheetFormatter expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatter(
                        name,
                        values
                ),
                () -> name + " " + values
        );
    }

    // spreadsheetFormatterNextTextComponentsAndCheck...................................................................

    @Test
    default void testSpreadsheetFormatterNextTextComponentWithNullSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetFormatterProvider().spreadsheetFormatterNextTextComponent(null)
        );
    }

    default void spreadsheetFormatterNextTextComponentFails(final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterNextTextComponentFails(
                this.createSpreadsheetFormatterProvider(),
                selector
        );
    }

    default void spreadsheetFormatterNextTextComponentFails(final SpreadsheetFormatterProvider provider,
                                                            final SpreadsheetFormatterSelector selector) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetFormatterNextTextComponent(
                        selector
                )
        );
    }

    default void spreadsheetFormatterNextTextComponentAndCheck(final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                this.createSpreadsheetFormatterProvider(),
                selector
        );
    }

    default void spreadsheetFormatterNextTextComponentAndCheck(final SpreadsheetFormatterSelector selector,
                                                               final SpreadsheetFormatterSelectorTextComponent expected) {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                this.createSpreadsheetFormatterProvider(),
                selector,
                expected
        );
    }

    default void spreadsheetFormatterNextTextComponentAndCheck(final SpreadsheetFormatterSelector selector,
                                                               final Optional<SpreadsheetFormatterSelectorTextComponent> expected) {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                this.createSpreadsheetFormatterProvider(),
                selector,
                expected
        );
    }

    default void spreadsheetFormatterNextTextComponentAndCheck(final SpreadsheetFormatterProvider provider,
                                                               final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                provider,
                selector,
                Optional.empty()
        );
    }

    default void spreadsheetFormatterNextTextComponentAndCheck(final SpreadsheetFormatterProvider provider,
                                                               final SpreadsheetFormatterSelector selector,
                                                               final SpreadsheetFormatterSelectorTextComponent expected) {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                provider,
                selector,
                Optional.of(expected)
        );
    }

    default void spreadsheetFormatterNextTextComponentAndCheck(final SpreadsheetFormatterProvider provider,
                                                               final SpreadsheetFormatterSelector selector,
                                                               final Optional<SpreadsheetFormatterSelectorTextComponent> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatterNextTextComponent(selector),
                provider::toString
        );
    }

    // SpreadsheetFormatterSamples......................................................................................

    @Test
    default void testSpreadsheetFormatterSamplesWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetFormatterProvider()
                        .spreadsheetFormatterSamples(null)
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterName name,
                                                     final SpreadsheetFormatterSample... expected) {
        this.spreadsheetFormatterSamplesAndCheck(
                this.createSpreadsheetFormatterProvider(),
                name,
                expected
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterProvider provider,
                                                     final SpreadsheetFormatterName name,
                                                     final SpreadsheetFormatterSample... expected) {
        this.spreadsheetFormatterSamplesAndCheck(
                provider,
                name,
                Lists.of(
                        expected
                )
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterProvider provider,
                                                     final SpreadsheetFormatterName name,
                                                     final List<SpreadsheetFormatterSample> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatterSamples(name),
                name + " samples"
        );
    }
    
    // SpreadsheetFormatterInfos........................................................................................

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterInfo... expected) {
        this.spreadsheetFormatterInfosAndCheck(
                this.createSpreadsheetFormatterProvider(),
                Sets.of(
                        expected
                )
        );
    }

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterProvider provider,
                                                   final SpreadsheetFormatterInfo... expected) {
        this.spreadsheetFormatterInfosAndCheck(
                provider,
                Sets.of(
                        expected
                )
        );
    }

    default void spreadsheetFormatterInfosAndCheck(final Set<SpreadsheetFormatterInfo> expected) {
        this.spreadsheetFormatterInfosAndCheck(
                this.createSpreadsheetFormatterProvider(),
                expected
        );
    }

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterProvider provider,
                                                   final Set<SpreadsheetFormatterInfo> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatterInfos(),
                provider::toString
        );
    }
}
