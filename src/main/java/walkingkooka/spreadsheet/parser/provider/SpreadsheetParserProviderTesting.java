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

package walkingkooka.spreadsheet.parser.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetParserProviderTesting<T extends SpreadsheetParserProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetParserSelectorWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider()
                .spreadsheetParser(
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetParserSelectorWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider()
                .spreadsheetParser(
                    SpreadsheetParserName.DATE.setValueText(""),
                    null
                )
        );
    }

    default void spreadsheetParserFails(final String selector,
                                        final ProviderContext context) {
        this.spreadsheetParserFails(
            this.createSpreadsheetParserProvider(),
            SpreadsheetParserSelector.parse(selector),
            context
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserProvider provider,
                                        final String selector,
                                        final ProviderContext context) {
        this.spreadsheetParserFails(
            provider,
            SpreadsheetParserSelector.parse(selector),
            context
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserSelector selector,
                                        final ProviderContext context) {
        this.spreadsheetParserFails(
            this.createSpreadsheetParserProvider(),
            selector,
            context
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserProvider provider,
                                        final SpreadsheetParserSelector selector,
                                        final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetParser(
                selector,
                context
            )
        );
    }

    default void spreadsheetParserAndCheck(final String selector,
                                           final ProviderContext context,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
            this.createSpreadsheetParserProvider(),
            SpreadsheetParserSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final String selector,
                                           final ProviderContext context,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
            provider,
            SpreadsheetParserSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserSelector selector,
                                           final ProviderContext context,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
            this.createSpreadsheetParserProvider(),
            selector,
            context,
            expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserSelector selector,
                                           final ProviderContext context,
                                           final SpreadsheetParser expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetParser(
                selector,
                context
            ),
            selector::toString
        );
    }

    // spreadsheetParser(SpreadsheetParserName, List)...................................................................

    @Test
    default void testSpreadsheetParserNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider()
                .spreadsheetParser(
                    null,
                    Lists.empty(),
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetParserNameWithNullValuesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider().spreadsheetParser(
                SpreadsheetParserName.TIME,
                null,
                ProviderContexts.fake()
            )
        );
    }

    @Test
    default void testSpreadsheetParserNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider().spreadsheetParser(
                SpreadsheetParserName.TIME,
                Lists.empty(),
                null
            )
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserName name,
                                        final List<?> values,
                                        final ProviderContext context) {
        this.spreadsheetParserFails(
            this.createSpreadsheetParserProvider(),
            name,
            values,
            context
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserProvider provider,
                                        final SpreadsheetParserName name,
                                        final List<?> values,
                                        final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetParser(
                name,
                values,
                context
            )
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserName name,
                                           final List<?> values,
                                           final ProviderContext context,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
            this.createSpreadsheetParserProvider(),
            name,
            values,
            context,
            expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserName name,
                                           final List<?> values,
                                           final ProviderContext context,
                                           final SpreadsheetParser expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetParser(
                name,
                values,
                context
            ),
            () -> name + " " + values
        );
    }

    // spreadsheetParserNextTokenAndCheck...............................................................................

    @Test
    default void testSpreadsheetParserNextTokenWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider().spreadsheetParserNextToken(null)
        );
    }

    default void spreadsheetParserNextTokenFails(final SpreadsheetParserSelector selector) {
        this.spreadsheetParserNextTokenFails(
            this.createSpreadsheetParserProvider(),
            selector
        );
    }

    default void spreadsheetParserNextTokenFails(final SpreadsheetParserProvider provider,
                                                 final SpreadsheetParserSelector selector) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetParserNextToken(
                selector
            )
        );
    }

    default void spreadsheetParserNextTokenAndCheck(final SpreadsheetParserSelector selector) {
        this.spreadsheetParserNextTokenAndCheck(
            this.createSpreadsheetParserProvider(),
            selector
        );
    }

    default void spreadsheetParserNextTokenAndCheck(final SpreadsheetParserSelector selector,
                                                    final SpreadsheetParserSelectorToken expected) {
        this.spreadsheetParserNextTokenAndCheck(
            this.createSpreadsheetParserProvider(),
            selector,
            expected
        );
    }

    default void spreadsheetParserNextTokenAndCheck(final SpreadsheetParserSelector selector,
                                                    final Optional<SpreadsheetParserSelectorToken> expected) {
        this.spreadsheetParserNextTokenAndCheck(
            this.createSpreadsheetParserProvider(),
            selector,
            expected
        );
    }

    default void spreadsheetParserNextTokenAndCheck(final SpreadsheetParserProvider provider,
                                                    final SpreadsheetParserSelector selector) {
        this.spreadsheetParserNextTokenAndCheck(
            provider,
            selector,
            Optional.empty()
        );
    }

    default void spreadsheetParserNextTokenAndCheck(final SpreadsheetParserProvider provider,
                                                    final SpreadsheetParserSelector selector,
                                                    final SpreadsheetParserSelectorToken expected) {
        this.spreadsheetParserNextTokenAndCheck(
            provider,
            selector,
            Optional.of(expected)
        );
    }

    default void spreadsheetParserNextTokenAndCheck(final SpreadsheetParserProvider provider,
                                                    final SpreadsheetParserSelector selector,
                                                    final Optional<SpreadsheetParserSelectorToken> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetParserNextToken(selector),
            provider::toString
        );
    }

    // spreadsheetFormatterSelector.....................................................................................

    @Test
    default void testSpreadsheetFormatterSelectorWithNullParserSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetParserProvider().spreadsheetFormatterSelector(null)
        );
    }

    default void spreadsheetFormatterSelectorFails(final SpreadsheetParserSelector selector) {
        this.spreadsheetFormatterSelectorFails(
            this.createSpreadsheetParserProvider(),
            selector
        );
    }

    default void spreadsheetFormatterSelectorFails(final SpreadsheetParserProvider provider,
                                                   final SpreadsheetParserSelector selector) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetFormatterSelector(
                selector
            )
        );
    }

    default void spreadsheetFormatterSelectorAndCheck(final SpreadsheetParserSelector selector) {
        this.spreadsheetFormatterSelectorAndCheck(
            this.createSpreadsheetParserProvider(),
            selector
        );
    }

    default void spreadsheetFormatterSelectorAndCheck(final SpreadsheetParserSelector selector,
                                                      final SpreadsheetFormatterSelector expected) {
        this.spreadsheetFormatterSelectorAndCheck(
            this.createSpreadsheetParserProvider(),
            selector,
            expected
        );
    }

    default void spreadsheetFormatterSelectorAndCheck(final SpreadsheetParserSelector selector,
                                                      final Optional<SpreadsheetFormatterSelector> expected) {
        this.spreadsheetFormatterSelectorAndCheck(
            this.createSpreadsheetParserProvider(),
            selector,
            expected
        );
    }

    default void spreadsheetFormatterSelectorAndCheck(final SpreadsheetParserProvider provider,
                                                      final SpreadsheetParserSelector selector) {
        this.spreadsheetFormatterSelectorAndCheck(
            provider,
            selector,
            Optional.empty()
        );
    }

    default void spreadsheetFormatterSelectorAndCheck(final SpreadsheetParserProvider provider,
                                                      final SpreadsheetParserSelector selector,
                                                      final SpreadsheetFormatterSelector expected) {
        this.spreadsheetFormatterSelectorAndCheck(
            provider,
            selector,
            Optional.of(expected)
        );
    }

    default void spreadsheetFormatterSelectorAndCheck(final SpreadsheetParserProvider provider,
                                                      final SpreadsheetParserSelector selector,
                                                      final Optional<SpreadsheetFormatterSelector> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetFormatterSelector(selector),
            provider::toString
        );
    }

    // spreadsheetParserInfo............................................................................................

    default void spreadsheetParserInfosAndCheck(final SpreadsheetParserInfo... expected) {
        this.spreadsheetParserInfosAndCheck(
            this.createSpreadsheetParserProvider(),
            Sets.of(
                expected
            )
        );
    }

    default void spreadsheetParserInfosAndCheck(final SpreadsheetParserProvider provider,
                                                final SpreadsheetParserInfo... expected) {
        this.spreadsheetParserInfosAndCheck(
            provider,
            Sets.of(
                expected
            )
        );
    }

    default void spreadsheetParserInfosAndCheck(final Set<SpreadsheetParserInfo> expected) {
        this.spreadsheetParserInfosAndCheck(
            this.createSpreadsheetParserProvider(),
            expected
        );
    }

    default void spreadsheetParserInfosAndCheck(final SpreadsheetParserProvider provider,
                                                final Set<SpreadsheetParserInfo> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetParserInfos(),
            provider::toString
        );
    }

    T createSpreadsheetParserProvider();
}
