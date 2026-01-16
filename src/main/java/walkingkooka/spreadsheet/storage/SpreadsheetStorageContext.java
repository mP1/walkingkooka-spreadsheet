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
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StorageContext;

import java.util.Optional;
import java.util.Set;

public interface SpreadsheetStorageContext extends StorageContext,
    SpreadsheetEnvironmentContext,
    SpreadsheetMetadataContext,
    ConverterLike {

    Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel);

    Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells);

    void deleteCells(final SpreadsheetExpressionReference cellsOrLabel);

    // labels...........................................................................................................

    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName);

    SpreadsheetLabelMapping saveLabel(final SpreadsheetLabelMapping label);

    void deleteLabel(final SpreadsheetLabelName labelName);

    Set<SpreadsheetLabelName> findLabelsByName(final String labelName,
                                               final int offset,
                                               final int count);

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    SpreadsheetStorageContext cloneEnvironment();

    @Override
    SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext environmentContext);

    @Override
    void setSpreadsheetId(final SpreadsheetId spreadsheetId);
}
