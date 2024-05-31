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

import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

public interface SpreadsheetFormatterProviderTesting<T extends SpreadsheetFormatterProvider> extends ClassTesting2<T>,
        TreePrintableTesting {

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final String selector) {
        this.spreadsheetFormatterAndCheck(
                provider,
                SpreadsheetFormatterSelector.parse(selector)
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterSelector selector) {
        this.spreadsheetFormatterAndCheck(
                provider,
                selector,
                Optional.empty()
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

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterSelector selector,
                                              final SpreadsheetFormatter expected) {
        this.spreadsheetFormatterAndCheck(
                provider,
                selector,
                Optional.of(expected)
        );
    }

    default void spreadsheetFormatterAndCheck(final SpreadsheetFormatterProvider provider,
                                              final SpreadsheetFormatterSelector selector,
                                              final Optional<SpreadsheetFormatter> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatter(selector),
                () -> selector.toString()
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

    default void spreadsheetFormatterInfosAndCheck(final SpreadsheetFormatterProvider provider,
                                                   final Set<SpreadsheetFormatterInfo> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetFormatterInfos(),
                () -> provider.toString()
        );
    }
}
