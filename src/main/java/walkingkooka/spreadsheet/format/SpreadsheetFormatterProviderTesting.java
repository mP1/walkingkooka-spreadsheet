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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetFormatterProviderTesting<T extends SpreadsheetFormatterProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetFormatterSelectorWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatter(
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetFormatterSelectorWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatter(
                    SpreadsheetFormatterName.GENERAL.setValueText(""),
                    null
                )
        );
    }

    // SpreadsheetFormatter(SpreadsheetFormatterSelector)...............................................................

    default void spreadsheetFormatterFails(final String selector,
                                           final ProviderContext context) {
        this.spreadsheetFormatterFails(
            this.createSpreadsheetFormatterProvider(),
            SpreadsheetFormatterSelector.parse(selector),
            context
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterProvider provider,
                                           final String selector,
                                           final ProviderContext context) {
        this.spreadsheetFormatterFails(
            provider,
            SpreadsheetFormatterSelector.parse(selector),
            context
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterSelector selector,
                                           final ProviderContext context) {
        this.spreadsheetFormatterFails(
            this.createSpreadsheetFormatterProvider(),
            selector,
            context
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterProvider provider,
                                           final SpreadsheetFormatterSelector selector,
                                           final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetFormatter(
                selector,
                context
            )
        );
    }

    default void spreadsheetFormatterAndCheck(final String selector,
                                              final ProviderContext context,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
            this.createSpreadsheetFormatterProvider(),
            SpreadsheetFormatterSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final String selector,
                                              final ProviderContext context,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
            provider,
            SpreadsheetFormatterSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterSelector selector,
                                              final ProviderContext context,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
            this.createSpreadsheetFormatterProvider(),
            selector,
            context,
            expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterSelector selector,
                                              final ProviderContext context,
                                              final SpreadsheetFormatter expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetFormatter(
                selector,
                context
            ),
            selector::toString
        );
    }

    // SpreadsheetFormatter(SpreadsheetFormatterName)...................................................................

    @Test
    default void testSpreadsheetFormatterNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatter(
                    null,
                    Lists.empty(),
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetFormatterNameWithNullValuesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatter(
                    SpreadsheetFormatterName.GENERAL,
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetFormatterNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatter(
                    SpreadsheetFormatterName.GENERAL,
                    Lists.empty(),
                    null
                )
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterName name,
                                           final List<?> values,
                                           final ProviderContext context) {
        this.spreadsheetFormatterFails(
            this.createSpreadsheetFormatterProvider(),
            name,
            values,
            context
        );
    }

    default void spreadsheetFormatterFails(final SpreadsheetFormatterProvider provider,
                                           final SpreadsheetFormatterName name,
                                           final List<?> values,
                                           final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetFormatter(
                name,
                values,
                context
            )
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterName name,
                                              final List<?> values,
                                              final ProviderContext context,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
            this.createSpreadsheetFormatterProvider(),
            name,
            values,
            context,
            expected
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterName name,
                                              final List<?> values,
                                              final ProviderContext context,
                                              final SpreadsheetFormatter expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetFormatter(
                name,
                values,
                context
            ),
            () -> name + " " + values
        );
    }

    // spreadsheetFormatterNextTokensAndCheck...................................................................

    @Test
    default void testSpreadsheetFormatterNextTextComponentWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider().spreadsheetFormatterNextToken(null)
        );
    }

    default void spreadsheetFormatterNextTokenFails(final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterNextTokenFails(
            this.createSpreadsheetFormatterProvider(),
            selector
        );
    }

    default void spreadsheetFormatterNextTokenFails(final SpreadsheetFormatterProvider provider,
                                                    final SpreadsheetFormatterSelector selector) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetFormatterNextToken(
                selector
            )
        );
    }

    default void spreadsheetFormatterNextTokenAndCheck(final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterNextTokenAndCheck(
            this.createSpreadsheetFormatterProvider(),
            selector
        );
    }

    default void spreadsheetFormatterNextTokenAndCheck(final SpreadsheetFormatterSelector selector,
                                                       final SpreadsheetFormatterSelectorToken expected) {
        this.spreadsheetFormatterNextTokenAndCheck(
            this.createSpreadsheetFormatterProvider(),
            selector,
            expected
        );
    }

    default void spreadsheetFormatterNextTokenAndCheck(final SpreadsheetFormatterSelector selector,
                                                       final Optional<SpreadsheetFormatterSelectorToken> expected) {
        this.spreadsheetFormatterNextTokenAndCheck(
            this.createSpreadsheetFormatterProvider(),
            selector,
            expected
        );
    }

    default void spreadsheetFormatterNextTokenAndCheck(final SpreadsheetFormatterProvider provider,
                                                       final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterNextTokenAndCheck(
            provider,
            selector,
            Optional.empty()
        );
    }

    default void spreadsheetFormatterNextTokenAndCheck(final SpreadsheetFormatterProvider provider,
                                                       final SpreadsheetFormatterSelector selector,
                                                       final SpreadsheetFormatterSelectorToken expected) {
        this.spreadsheetFormatterNextTokenAndCheck(
            provider,
            selector,
            Optional.of(expected)
        );
    }

    default void spreadsheetFormatterNextTokenAndCheck(final SpreadsheetFormatterProvider provider,
                                                       final SpreadsheetFormatterSelector selector,
                                                       final Optional<SpreadsheetFormatterSelectorToken> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetFormatterNextToken(selector),
            provider::toString
        );
    }

    // SpreadsheetFormatterSamples......................................................................................

    @Test
    default void testSpreadsheetFormatterSamplesWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatterSamples(
                    null,
                    SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
                    new FakeSpreadsheetFormatterProviderSamplesContext()
                )
        );
    }

    @Test
    default void testSpreadsheetFormatterSamplesWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetFormatterProvider()
                .spreadsheetFormatterSamples(
                    SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                    SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
                    null
                )
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterName name,
                                                     final boolean includeSamples,
                                                     final SpreadsheetFormatterProviderSamplesContext context,
                                                     final SpreadsheetFormatterSample... expected) {
        this.spreadsheetFormatterSamplesAndCheck(
            name.setValueText(""),
            includeSamples,
            context,
            expected
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterSelector selector,
                                                     final boolean includeSamples,
                                                     final SpreadsheetFormatterProviderSamplesContext context,
                                                     final SpreadsheetFormatterSample... expected) {
        this.spreadsheetFormatterSamplesAndCheck(
            this.createSpreadsheetFormatterProvider(),
            selector,
            includeSamples,
            context,
            expected
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterProvider provider,
                                                     final SpreadsheetFormatterName name,
                                                     final SpreadsheetFormatterProviderSamplesContext context,
                                                     final SpreadsheetFormatterSample... expected) {
        this.spreadsheetFormatterSamplesAndCheck(
            provider,
            name.setValueText(""),
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context,
            expected
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterProvider provider,
                                                     final SpreadsheetFormatterSelector selector,
                                                     final boolean includeSamples,
                                                     final SpreadsheetFormatterProviderSamplesContext context,
                                                     final SpreadsheetFormatterSample... expected) {
        this.spreadsheetFormatterSamplesAndCheck(
            provider,
            selector,
            includeSamples,
            context,
            Lists.of(
                expected
            )
        );
    }

    default void spreadsheetFormatterSamplesAndCheck(final SpreadsheetFormatterProvider provider,
                                                     final SpreadsheetFormatterSelector selector,
                                                     final boolean includeSamples,
                                                     final SpreadsheetFormatterProviderSamplesContext context,
                                                     final List<SpreadsheetFormatterSample> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetFormatterSamples(
                selector,
                includeSamples,
                context
            ),
            selector + " includeSamples=" + includeSamples
        );
    }

    default void spreadsheetFormatterSamplesFails(final SpreadsheetFormatterName name,
                                                  final SpreadsheetFormatterProviderSamplesContext context) {
        this.spreadsheetFormatterSamplesFails(
            name.setValueText(""),
            SpreadsheetFormatterProvider.SKIP_SAMPLES,
            context
        );
    }

    default void spreadsheetFormatterSamplesFails(final SpreadsheetFormatterSelector selector,
                                                  final boolean includeSamples,
                                                  final SpreadsheetFormatterProviderSamplesContext context) {
        this.spreadsheetFormatterSamplesFails(
            this.createSpreadsheetFormatterProvider(),
            selector,
            includeSamples,
            context
        );
    }

    default void spreadsheetFormatterSamplesFails(final SpreadsheetFormatterProvider provider,
                                                  final SpreadsheetFormatterSelector selector,
                                                  final boolean includeSamples,
                                                  final SpreadsheetFormatterProviderSamplesContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetFormatterSamples(
                selector,
                includeSamples,
                context
            )
        );
    }

    // SpreadsheetFormatterInfos........................................................................................

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterInfo... expected) {
        this.spreadsheetFormatterInfosAndCheck(
            SpreadsheetFormatterInfoSet.with(
                Sets.of(
                    expected
                )
            )
        );
    }

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterProvider provider,
                                                   final SpreadsheetFormatterInfo... expected) {
        this.spreadsheetFormatterInfosAndCheck(
            provider,
            SpreadsheetFormatterInfoSet.with(
                Sets.of(
                    expected
                )
            )
        );
    }

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterInfoSet expected) {
        this.spreadsheetFormatterInfosAndCheck(
            this.createSpreadsheetFormatterProvider(),
            expected
        );
    }

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterProvider provider,
                                                   final SpreadsheetFormatterInfoSet expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetFormatterInfos(),
            provider::toString
        );
    }


    T createSpreadsheetFormatterProvider();
}
