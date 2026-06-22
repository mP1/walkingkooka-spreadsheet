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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetMetadataLoaderTesting<C extends SpreadsheetMetadataLoader> extends TreePrintableTesting {

    // loadMetadata.....................................................................................................

    @Test
    default void testLoadMetadataWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetMetadataLoader()
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

    // SpreadsheetMetadataLoader........................................................................................

    C createSpreadsheetMetadataLoader();
}
