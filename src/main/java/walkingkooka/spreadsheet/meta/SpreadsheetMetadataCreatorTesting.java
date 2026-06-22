
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

import javaemul.internal.annotations.GwtIncompatible;
import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Provides factory methods for creating a {@link SpreadsheetMetadata} for testing.
 */
@GwtIncompatible
public interface SpreadsheetMetadataCreatorTesting<C extends SpreadsheetMetadataCreator> extends TreePrintableTesting {

    // createMetadata...................................................................................................

    @Test
    default void testCreateMetadataWithNullUserFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetMetadataCreator()
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
            () -> this.createSpreadsheetMetadataCreator()
                .createMetadata(
                    EmailAddress.parse("user@example.com"),
                    null // locale
                )
        );
    }

    default void createSpreadsheetMetadataAndCheck(final C context,
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

    C createSpreadsheetMetadataCreator();
}
