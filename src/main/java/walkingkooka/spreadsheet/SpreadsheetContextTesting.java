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
import walkingkooka.locale.LocaleContextTesting2;
import walkingkooka.plugin.HasProviderContextTesting;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextTesting;
import walkingkooka.spreadsheet.net.HasSpreadsheetServerUrlTesting;

public interface SpreadsheetContextTesting<C extends SpreadsheetContext> extends SpreadsheetEnvironmentContextTesting2<C>,
    HasProviderContextTesting,
    HasSpreadsheetMetadataTesting,
    HasSpreadsheetServerUrlTesting,
    LocaleContextTesting2<C>,
    SpreadsheetMetadataContextTesting<C> {

    // setLocale........................................................................................................

    @Test
    @Override
    default void testSetLocaleWithNullFails() {
        LocaleContextTesting2.super.testSetLocaleWithNullFails();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetContext.class.getSimpleName();
    }
}
