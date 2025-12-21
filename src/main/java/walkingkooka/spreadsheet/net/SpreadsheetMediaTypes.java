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

package walkingkooka.spreadsheet.net;

import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.Validator;
import walkingkooka.validation.ValueType;

import java.util.Locale;
import java.util.Optional;

/**
 * A collection of {@link MediaType} for cells. This will be mostly used by {@link walkingkooka.spreadsheet.importer.SpreadsheetImporter} and {@link walkingkooka.spreadsheet.export.SpreadsheetExporter}.
 */
public final class SpreadsheetMediaTypes implements PublicStaticHelper {

    // json.............................................................................................................

    public static final MediaType JSON_CELL = json(SpreadsheetCell.class);

    public static final MediaType JSON_DATE_TIME_SYMBOLS = json(DateTimeSymbols.class);

    public static final MediaType JSON_DECIMAL_NUMBER_SYMBOLS = json(DecimalNumberSymbols.class);

    public static final MediaType JSON_FORMULA = json(SpreadsheetFormula.class);

    public static final MediaType JSON_FORMATTER = json(SpreadsheetFormatterSelector.class);

    public static final MediaType JSON_LOCALE = json(Locale.class);

    public static final MediaType JSON_PARSER = json(SpreadsheetParserSelector.class);

    public static final MediaType JSON_STYLE = json(TextStyle.class);

    public static final MediaType JSON_FORMATTED_VALUE = json(TextNode.class);

    public static final MediaType JSON_VALUE = json(Object.class);

    public static final MediaType JSON_VALIDATOR = json(Validator.class);

    public static final MediaType JSON_VALUE_TYPE = json(ValueType.class);

    private static MediaType json(final Class<?> type) {
        return MediaType.APPLICATION_JSON.setSuffix(
            Optional.of(type.getName())
        );
    }
    
    // object...........................................................................................................

    private final static MediaType MEMORY = MediaType.parse("application/memory");

    public static final MediaType MEMORY_CELL = object(SpreadsheetCell.class);

    public static final MediaType MEMORY_LABEL = object(SpreadsheetLabelName.class);

    public static final MediaType MEMORY_SPREADSHEET_METADATA = object(SpreadsheetMetadata.class);

    private static MediaType object(final Class<?> type) {
        return MEMORY.setSuffix(
            Optional.of(
                type.getName()
            )
        );
    }

    private SpreadsheetMediaTypes() {
        throw new UnsupportedOperationException();
    }
}
