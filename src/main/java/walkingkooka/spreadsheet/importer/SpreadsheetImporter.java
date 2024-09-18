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

package walkingkooka.spreadsheet.importer;

import walkingkooka.net.WebEntity;

import java.util.List;

/**
 * Imports a range of cells from the given {@link WebEntity} which may hold JSON, HTML or more.
 * This could be used to import a file or receive a paste in the browser with new content such as cells or parts of cells
 * such as formulas, formatting etc.
 */
public interface SpreadsheetImporter {

    boolean canImport(final WebEntity cells,
                      final SpreadsheetImporterContext context);

    List<ImportCellValue> doImport(final WebEntity cells,
                                   final SpreadsheetImporterContext context);
}
