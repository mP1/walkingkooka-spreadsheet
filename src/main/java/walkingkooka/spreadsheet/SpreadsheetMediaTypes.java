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

import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of {@link MediaType} for cells. This will be mostly used by {@link walkingkooka.spreadsheet.importer.SpreadsheetImporter} and {@link walkingkooka.spreadsheet.export.SpreadsheetExporter}.
 */
public final class SpreadsheetMediaTypes implements PublicStaticHelper {

    public static final MediaType JSON_CELLS = MediaType.parse("application/cells+json");

    public static final MediaType JSON_FORMULAS = MediaType.parse("application/formulas+json");

    public static final MediaType JSON_FORMATTERS = MediaType.parse("application/formatters+json");

    public static final MediaType JSON_PARSERS = MediaType.parse("application/parsers+json");

    public static final MediaType JSON_STYLES = MediaType.parse("application/styles+json");

    public static final MediaType JSON_FORMATTED_VALUES = MediaType.parse("application/formatted-values+json");

    private SpreadsheetMediaTypes() {
        throw new UnsupportedOperationException();
    }
}
