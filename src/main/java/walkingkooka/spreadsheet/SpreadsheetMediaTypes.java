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

import java.util.Optional;

/**
 * A collection of {@link MediaType} for cells. This will be mostly used by {@link walkingkooka.spreadsheet.importer.SpreadsheetImporter} and {@link walkingkooka.spreadsheet.export.SpreadsheetExporter}.
 */
public final class SpreadsheetMediaTypes implements PublicStaticHelper {

    /**
     * The base {@link MediaType} that is common to all application media types.
     */
    private final static MediaType SPREADSHEET = MediaType.parse("application/spreadsheet");

    // json.............................................................................................................

    public static final MediaType JSON_CELLS = json("cell");

    public static final MediaType JSON_FORMULAS = json("formula");

    public static final MediaType JSON_FORMATTERS = json("formatter");

    public static final MediaType JSON_PARSERS = json("parser");

    public static final MediaType JSON_STYLES = json("style");

    public static final MediaType JSON_FORMATTED_VALUES = json("formatted-value");

    public static final MediaType JSON_VALUE_TYPE = json("value-type");

    private static MediaType json(final String value) {
        return mediaType(
            value,
            "json"
        );
    }

    private static MediaType mediaType(final String value,
                                       final String contentType) {
        return SPREADSHEET.setSubType(
            SPREADSHEET.subType() + "-" + value
        ).setSuffix(
            Optional.of(contentType)
        );
    }

    private SpreadsheetMediaTypes() {
        throw new UnsupportedOperationException();
    }
}
