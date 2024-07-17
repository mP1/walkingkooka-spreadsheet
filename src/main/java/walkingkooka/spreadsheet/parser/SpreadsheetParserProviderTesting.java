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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderTesting;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetParserProviderTesting<T extends SpreadsheetParserProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetParserWithNullSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetParserProvider().spreadsheetParser(null)
        );
    }

    T createSpreadsheetParserProvider();

    // spreadsheetParser(SpreadsheetParserSelector).....................................................................

    default void spreadsheetParserFails(final String selector) {
        this.spreadsheetParserFails(
                this.createSpreadsheetParserProvider(),
                SpreadsheetParserSelector.parse(selector)
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserProvider provider,
                                        final String selector) {
        this.spreadsheetParserFails(
                provider,
                SpreadsheetParserSelector.parse(selector)
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserSelector selector) {
        this.spreadsheetParserFails(
                this.createSpreadsheetParserProvider(),
                selector
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserProvider provider,
                                        final SpreadsheetParserSelector selector) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetParser(selector)
        );
    }

    default void spreadsheetParserAndCheck(final String selector,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                SpreadsheetParserSelector.parse(selector),
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final String selector,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
                provider,
                SpreadsheetParserSelector.parse(selector),
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserSelector selector,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                selector,
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserSelector selector,
                                           final SpreadsheetParser expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetParser(selector),
                selector::toString
        );
    }

    // spreadsheetParser(SpreadsheetParserName, List)...................................................................

    @Test
    default void testSpreadsheetParserWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetParserProvider().spreadsheetParser(
                        null,
                        Lists.empty()
                )
        );
    }

    @Test
    default void testSpreadsheetParserWithNullListOfValuesFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetParserProvider().spreadsheetParser(
                        SpreadsheetParserName.TIME_PARSER_PATTERN,
                        null
                )
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserName name,
                                        final List<?> values) {
        this.spreadsheetParserFails(
                this.createSpreadsheetParserProvider(),
                name,
                values
        );
    }

    default void spreadsheetParserFails(final SpreadsheetParserProvider provider,
                                        final SpreadsheetParserName name,
                                        final List<?> values) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetParser(
                        name,
                        values
                )
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserName name,
                                           final List<?> values,
                                           final SpreadsheetParser expected) {
        this.spreadsheetParserAndCheck(
                this.createSpreadsheetParserProvider(),
                name,
                values,
                expected
        );
    }

    default void spreadsheetParserAndCheck(final SpreadsheetParserProvider provider,
                                           final SpreadsheetParserName name,
                                           final List<?> values,
                                           final SpreadsheetParser expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetParser(
                        name,
                        values
                ),
                () -> name + " " + values
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
}
