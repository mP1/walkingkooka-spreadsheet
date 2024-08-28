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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExporterProviderTesting<T extends SpreadsheetExporterProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetExporterWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExporterProvider()
                        .spreadsheetExporter(
                                null,
                                ProviderContexts.fake()
                        )
        );
    }

    @Test
    default void testSpreadsheetExporterWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExporterProvider()
                        .spreadsheetExporter(
                                SpreadsheetExporterSelector.with(
                                        SpreadsheetExporterName.with("exporter-123"),
                                        ""
                                ),
                                null
                        )
        );
    }

    T createSpreadsheetExporterProvider();

    // SpreadsheetExporter(SpreadsheetExporterSelector).................................................................

    default void spreadsheetExporterFails(final String selector,
                                          final ProviderContext context) {
        this.spreadsheetExporterFails(
                this.createSpreadsheetExporterProvider(),
                SpreadsheetExporterSelector.parse(selector),
                context
        );
    }

    default void spreadsheetExporterFails(final SpreadsheetExporterProvider provider,
                                          final String selector,
                                          final ProviderContext context) {
        this.spreadsheetExporterFails(
                provider,
                SpreadsheetExporterSelector.parse(selector),
                context
        );
    }

    default void spreadsheetExporterFails(final SpreadsheetExporterSelector selector,
                                          final ProviderContext context) {
        this.spreadsheetExporterFails(
                this.createSpreadsheetExporterProvider(),
                selector,
                context
        );
    }

    default void spreadsheetExporterFails(final SpreadsheetExporterProvider provider,
                                          final SpreadsheetExporterSelector selector,
                                          final ProviderContext context) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetExporter(
                        selector,
                        context
                )
        );
    }

    default void spreadsheetExporterAndCheck(final String selector,
                                             final ProviderContext context,
                                             final SpreadsheetExporter expected) {
        this.spreadsheetExporterAndCheck(
                this.createSpreadsheetExporterProvider(),
                SpreadsheetExporterSelector.parse(selector),
                context,
                expected
        );
    }

    default void spreadsheetExporterAndCheck(final SpreadsheetExporterProvider provider,
                                             final String selector,
                                             final ProviderContext context,
                                             final SpreadsheetExporter expected) {
        this.spreadsheetExporterAndCheck(
                provider,
                SpreadsheetExporterSelector.parse(selector),
                context,
                expected
        );
    }

    default void spreadsheetExporterAndCheck(final SpreadsheetExporterSelector selector,
                                             final ProviderContext context,
                                             final SpreadsheetExporter expected) {
        this.spreadsheetExporterAndCheck(
                this.createSpreadsheetExporterProvider(),
                selector,
                context,
                expected
        );
    }

    default void spreadsheetExporterAndCheck(final SpreadsheetExporterProvider provider,
                                             final SpreadsheetExporterSelector selector,
                                             final ProviderContext context,
                                             final SpreadsheetExporter expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetExporter(
                        selector,
                        context
                ),
                selector::toString
        );
    }

    // SpreadsheetExporter(SpreadsheetExporterSelector).................................................................

    default void spreadsheetExporterFails(final SpreadsheetExporterName name,
                                          final List<?> values,
                                          final ProviderContext context) {
        this.spreadsheetExporterFails(
                this.createSpreadsheetExporterProvider(),
                name,
                values,
                context
        );
    }

    default void spreadsheetExporterFails(final SpreadsheetExporterProvider provider,
                                          final SpreadsheetExporterName name,
                                          final List<?> values,
                                          final ProviderContext context) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetExporter(
                        name,
                        values,
                        context
                )
        );
    }

    default void spreadsheetExporterAndCheck(final SpreadsheetExporterName name,
                                             final List<?> values,
                                             final ProviderContext context,
                                             final SpreadsheetExporter expected) {
        this.spreadsheetExporterAndCheck(
                this.createSpreadsheetExporterProvider(),
                name,
                values,
                context,
                expected
        );
    }

    default void spreadsheetExporterAndCheck(final SpreadsheetExporterProvider provider,
                                             final SpreadsheetExporterName name,
                                             final List<?> values,
                                             final ProviderContext context,
                                             final SpreadsheetExporter expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetExporter(
                        name,
                        values,
                        context
                ),
                () -> name + " " + values
        );
    }

    // SpreadsheetExporterInfos.........................................................................................

    default void spreadsheetExporterInfosAndCheck(final SpreadsheetExporterInfo... expected) {
        this.spreadsheetExporterInfosAndCheck(
                this.createSpreadsheetExporterProvider(),
                Sets.of(
                        expected
                )
        );
    }

    default void spreadsheetExporterInfosAndCheck(final SpreadsheetExporterProvider provider,
                                                  final SpreadsheetExporterInfo... expected) {
        this.spreadsheetExporterInfosAndCheck(
                provider,
                Sets.of(
                        expected
                )
        );
    }

    default void spreadsheetExporterInfosAndCheck(final Set<SpreadsheetExporterInfo> expected) {
        this.spreadsheetExporterInfosAndCheck(
                this.createSpreadsheetExporterProvider(),
                expected
        );
    }

    default void spreadsheetExporterInfosAndCheck(final SpreadsheetExporterProvider provider,
                                                  final Set<SpreadsheetExporterInfo> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetExporterInfos(),
                provider::toString
        );
    }
}
