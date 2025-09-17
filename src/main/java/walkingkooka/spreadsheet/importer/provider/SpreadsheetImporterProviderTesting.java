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

package walkingkooka.spreadsheet.importer.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetImporterProviderTesting<T extends SpreadsheetImporterProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetImporterSelectorWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetImporterProvider()
                .spreadsheetImporter(
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetImporterSelectorWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetImporterProvider()
                .spreadsheetImporter(
                    SpreadsheetImporterSelector.with(
                        SpreadsheetImporterName.with("importer-123"),
                        ""
                    ),
                    null
                )
        );
    }

    // SpreadsheetImporter(SpreadsheetImporterSelector).................................................................

    default void spreadsheetImporterFails(final String selector,
                                          final ProviderContext context) {
        this.spreadsheetImporterFails(
            this.createSpreadsheetImporterProvider(),
            SpreadsheetImporterSelector.parse(selector),
            context
        );
    }

    default void spreadsheetImporterFails(final SpreadsheetImporterProvider provider,
                                          final String selector,
                                          final ProviderContext context) {
        this.spreadsheetImporterFails(
            provider,
            SpreadsheetImporterSelector.parse(selector),
            context
        );
    }

    default void spreadsheetImporterFails(final SpreadsheetImporterSelector selector,
                                          final ProviderContext context) {
        this.spreadsheetImporterFails(
            this.createSpreadsheetImporterProvider(),
            selector,
            context
        );
    }

    default void spreadsheetImporterFails(final SpreadsheetImporterProvider provider,
                                          final SpreadsheetImporterSelector selector,
                                          final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetImporter(
                selector,
                context
            )
        );
    }

    default void spreadsheetImporterAndCheck(final String selector,
                                             final ProviderContext context,
                                             final SpreadsheetImporter expected) {
        this.spreadsheetImporterAndCheck(
            this.createSpreadsheetImporterProvider(),
            SpreadsheetImporterSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetImporterAndCheck(final SpreadsheetImporterProvider provider,
                                             final String selector,
                                             final ProviderContext context,
                                             final SpreadsheetImporter expected) {
        this.spreadsheetImporterAndCheck(
            provider,
            SpreadsheetImporterSelector.parse(selector),
            context,
            expected
        );
    }

    default void spreadsheetImporterAndCheck(final SpreadsheetImporterSelector selector,
                                             final ProviderContext context,
                                             final SpreadsheetImporter expected) {
        this.spreadsheetImporterAndCheck(
            this.createSpreadsheetImporterProvider(),
            selector,
            context,
            expected
        );
    }

    default void spreadsheetImporterAndCheck(final SpreadsheetImporterProvider provider,
                                             final SpreadsheetImporterSelector selector,
                                             final ProviderContext context,
                                             final SpreadsheetImporter expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetImporter(
                selector,
                context
            ),
            selector::toString
        );
    }

    // spreadsheetImporter(name)........................................................................................

    @Test
    default void testSpreadsheetImporterNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetImporterProvider()
                .spreadsheetImporter(
                    null,
                    Lists.empty(),
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetImporterNameWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetImporterProvider()
                .spreadsheetImporter(
                    SpreadsheetImporterName.JSON,
                    null,
                    ProviderContexts.fake()
                )
        );
    }

    @Test
    default void testSpreadsheetImporterNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetImporterProvider()
                .spreadsheetImporter(
                    SpreadsheetImporterName.JSON,
                    Lists.empty(),
                    null
                )
        );
    }

    default void spreadsheetImporterFails(final SpreadsheetImporterName name,
                                          final List<?> values,
                                          final ProviderContext context) {
        this.spreadsheetImporterFails(
            this.createSpreadsheetImporterProvider(),
            name,
            values,
            context
        );
    }

    default void spreadsheetImporterFails(final SpreadsheetImporterProvider provider,
                                          final SpreadsheetImporterName name,
                                          final List<?> values,
                                          final ProviderContext context) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.spreadsheetImporter(
                name,
                values,
                context
            )
        );
    }

    default void spreadsheetImporterAndCheck(final SpreadsheetImporterName name,
                                             final List<?> values,
                                             final ProviderContext context,
                                             final SpreadsheetImporter expected) {
        this.spreadsheetImporterAndCheck(
            this.createSpreadsheetImporterProvider(),
            name,
            values,
            context,
            expected
        );
    }

    default void spreadsheetImporterAndCheck(final SpreadsheetImporterProvider provider,
                                             final SpreadsheetImporterName name,
                                             final List<?> values,
                                             final ProviderContext context,
                                             final SpreadsheetImporter expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetImporter(
                name,
                values,
                context
            ),
            () -> name + " " + values
        );
    }

    // SpreadsheetImporterInfos.........................................................................................

    default void spreadsheetImporterInfosAndCheck(final SpreadsheetImporterInfo... expected) {
        this.spreadsheetImporterInfosAndCheck(
            this.createSpreadsheetImporterProvider(),
            Sets.of(
                expected
            )
        );
    }

    default void spreadsheetImporterInfosAndCheck(final SpreadsheetImporterProvider provider,
                                                  final SpreadsheetImporterInfo... expected) {
        this.spreadsheetImporterInfosAndCheck(
            provider,
            Sets.of(
                expected
            )
        );
    }

    default void spreadsheetImporterInfosAndCheck(final Set<SpreadsheetImporterInfo> expected) {
        this.spreadsheetImporterInfosAndCheck(
            this.createSpreadsheetImporterProvider(),
            expected
        );
    }

    default void spreadsheetImporterInfosAndCheck(final SpreadsheetImporterProvider provider,
                                                  final Set<SpreadsheetImporterInfo> expected) {
        this.checkEquals(
            expected,
            provider.spreadsheetImporterInfos(),
            provider::toString
        );
    }

    T createSpreadsheetImporterProvider();
}
