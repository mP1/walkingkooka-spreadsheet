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

package walkingkooka.spreadsheet.storage;

import walkingkooka.convert.ConverterLike;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StorageContext;
import walkingkooka.text.LineEnding;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public interface SpreadsheetStorageContext extends StorageContext,
    SpreadsheetEnvironmentContext,
    SpreadsheetMetadataContext,
    ConverterLike {

    Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel);

    Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells);

    void deleteCells(final SpreadsheetExpressionReference cellsOrLabel);

    @Override
    SpreadsheetStorageContext cloneEnvironment();

    @Override
    SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext environmentContext);

    @Override
    <T> SpreadsheetStorageContext setEnvironmentValue(final EnvironmentValueName<T> environmentValueName,
                                                      final T reference);

    @Override
    SpreadsheetStorageContext removeEnvironmentValue(final EnvironmentValueName<?> environmentValueName);

    @Override
    SpreadsheetStorageContext setLineEnding(final LineEnding lineEnding);

    @Override
    SpreadsheetStorageContext setLocale(final Locale locale);

    @Override
    SpreadsheetStorageContext setSpreadsheetId(final SpreadsheetId spreadsheetId);

    @Override
    SpreadsheetStorageContext setUser(final Optional<EmailAddress> optional);
}
