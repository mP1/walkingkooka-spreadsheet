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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.ContextTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.HasProviderContextTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetGlobalContextTesting<C extends SpreadsheetGlobalContext> extends ContextTesting<C>,
    SpreadsheetProviderContextTesting<C>,
    HasProviderContextTesting {

    // createMetadata...................................................................................................

    @Test
    default void testCreateMetadataWithNullUserFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .createMetadata(
                    null,
                    Optional.empty()
                )
        );
    }

    @Test
    default void testCreateMetadataWithNullLocaleFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .createMetadata(
                    EmailAddress.parse("user@example.com"),
                    null // locale
                )
        );
    }

    default void createMetadataAndCheck(final C context,
                                        final EmailAddress user,
                                        final Optional<Locale> locale,
                                        final SpreadsheetMetadata expected) {
        this.checkEquals(
            expected,
            context.createMetadata(
                user,
                locale
            ),
            "createMetadata " + user + " " + locale.orElse(null)
        );
    }

    // loadMetadata.....................................................................................................

    @Test
    default void testLoadMetadataWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadMetadata(null)
        );
    }

    default void loadMetadataAndCheck(final C context,
                                      final SpreadsheetId id) {
        this.loadMetadataAndCheck(
            context,
            id,
            Optional.empty()
        );
    }

    default void loadMetadataAndCheck(final C context,
                                      final SpreadsheetId id,
                                      final SpreadsheetMetadata expected) {
        this.loadMetadataAndCheck(
            context,
            id,
            Optional.of(expected)
        );
    }

    default void loadMetadataAndCheck(final C context,
                                      final SpreadsheetId id,
                                      final Optional<SpreadsheetMetadata> expected) {
        this.checkEquals(
            expected,
            context.loadMetadata(id),
            () -> "loadMetadata " + id
        );
    }

    // saveMetadata.....................................................................................................

    @Test
    default void testSaveMetadataWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveMetadata(null)
        );
    }

    default void saveMetadataAndCheck(final C context,
                                      final SpreadsheetMetadata metadata,
                                      final Optional<SpreadsheetMetadata> expected) {
        this.checkEquals(
            expected,
            context.saveMetadata(metadata),
            () -> "saveMetadata " + metadata
        );
    }

    // deleteMetadata...................................................................................................

    @Test
    default void testDeleteMetadataWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteMetadata(null)
        );
    }

    // findMetadataBySpreadsheetName....................................................................................

    @Test
    default void testFindMetadataBySpreadsheetNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .findMetadataBySpreadsheetName(
                    null,
                    0, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testFindMetadataBySpreadsheetNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findMetadataBySpreadsheetName(
                    "Hello",
                    -1, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testFindMetadataBySpreadsheetNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findMetadataBySpreadsheetName(
                    "Hello",
                    0, // offset
                    -1 // count
                )
        );
    }

    default void findMetadataBySpreadsheetNameAndCheck(final SpreadsheetGlobalContext context,
                                                       final String name,
                                                       final int offset,
                                                       final int count,
                                                       final SpreadsheetMetadata... expected) {
        this.findMetadataBySpreadsheetNameAndCheck(
            context,
            name,
            offset,
            count,
            Lists.of(expected)
        );
    }

    default void findMetadataBySpreadsheetNameAndCheck(final SpreadsheetGlobalContext context,
                                                       final String name,
                                                       final int offset,
                                                       final int count,
                                                       final List<SpreadsheetMetadata> expected) {
        this.checkEquals(
            expected,
            context.findMetadataBySpreadsheetName(
                name,
                offset,
                count
            ),
            () -> "findMetadataBySpreadsheetName " + CharSequences.quoteAndEscape(name) + " offset=" + offset + " count=" + count
        );
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetGlobalContext.class.getSimpleName();
    }
}
