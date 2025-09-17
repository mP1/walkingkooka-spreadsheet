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

package walkingkooka.spreadsheet.export.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExporterProviderTesting<T extends SpreadsheetExporterProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetExporterSelectorWithNullSelectorFails() {
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
    default void testSpreadsheetExporterSelectorWithNullContextFails() {
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

    // SpreadsheetExporter(SpreadsheetExporterName).....................................................................

    @Test
    default void testSpreadsheetExporterNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetExporterProvider()
                .spreadsheetExporter(
                    null,
                    Lists.empty(),
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetExporterNameWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetExporterProvider()
                .spreadsheetExporter(
                    SpreadsheetExporterName.JSON,
                    null,

                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetExporterNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetExporterProvider()
                .spreadsheetExporter(
                    SpreadsheetExporterName.JSON,
                    Lists.empty(),
                    null
                )
        );
    }

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
            expected
        );
    }

    default void spreadsheetExporterInfosAndCheck(final SpreadsheetExporterProvider provider,
                                                  final SpreadsheetExporterInfo... expected) {
        this.spreadsheetExporterInfosAndCheck(
            provider,
            SpreadsheetExporterInfoSet.with(
                Sets.of(
                    expected
                )
            )
        );
    }

    default void spreadsheetExporterInfosAndCheck(final SpreadsheetExporterInfoSet expected) {
        this.spreadsheetExporterInfosAndCheck(
            this.createSpreadsheetExporterProvider(),
            expected
        );
    }

    default void spreadsheetExporterInfosAndCheck(final SpreadsheetExporterProvider provider,
                                                  final SpreadsheetExporterInfoSet expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetExporterInfos(),
            provider::toString
        );
    }

    T createSpreadsheetExporterProvider();
}
