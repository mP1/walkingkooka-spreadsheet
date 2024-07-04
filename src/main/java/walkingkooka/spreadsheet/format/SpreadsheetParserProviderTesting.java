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
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetParserProviderTesting<T extends SpreadsheetParserProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetParserWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetParserProvider().spreadsheetParser(null)
        );
    }

    T createSpreadsheetParserProvider();

    default void spreadsheetParserAndCheck(final String selector) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                SpreadsheetParserSelector.parse(selector)
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final String selector) {
        this.spreadsheetParserAndCheck(
                provider,
                SpreadsheetParserSelector.parse(selector)
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserSelector selector) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                selector,
                Optional.empty()
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserSelector selector) {
        this.spreadsheetParserAndCheck(
                provider,
                selector,
                Optional.empty()
        );
    }

    default void spreadsheetParserAndCheck(final String selector,
                                           final Parser<SpreadsheetParserContext> expected) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                SpreadsheetParserSelector.parse(selector),
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final String selector,
                                           final Parser<SpreadsheetParserContext> expected) {
        this.spreadsheetParserAndCheck(
                provider,
                SpreadsheetParserSelector.parse(selector),
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserSelector selector,
                                           final Parser<SpreadsheetParserContext> expected) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                selector,
                Optional.of(expected)
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserSelector selector,
                                           final Parser<SpreadsheetParserContext> expected) {
        this.spreadsheetParserAndCheck(
                provider,
                selector,
                Optional.of(expected)
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserSelector selector,
                                           final Optional<Parser<SpreadsheetParserContext>> expected) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                selector,
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserSelector selector,
                                           final Optional<Parser<SpreadsheetParserContext>> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetParser(selector),
                () -> selector.toString()
        );
    }

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
                () -> provider.toString()
        );
    }
}
