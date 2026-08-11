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
import walkingkooka.currency.CurrencyLocaleContextTesting2;
import walkingkooka.net.header.MediaTypeDetectorTesting2;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetContextTesting2<C extends SpreadsheetContext> extends SpreadsheetContextTesting,
    SpreadsheetEnvironmentContextTesting2<C>,
    CurrencyLocaleContextTesting2<C>,
    MediaTypeDetectorTesting2<C>,
    SpreadsheetMetadataContextTesting2<C> {

    // setCurrency......................................................................................................

    @Test
    @Override
    default void testSetCurrencyWithNullFails() {
        CurrencyLocaleContextTesting2.super.testSetCurrencyWithNullFails();
    }

    // setLocale........................................................................................................

    @Test
    @Override
    default void testSetLocaleWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setLocale(null)
        );
    }

    // MediaTypeDetector................................................................................................

    @Override
    default C createMediaTypeDetector() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetContext.class.getSimpleName();
    }
}
