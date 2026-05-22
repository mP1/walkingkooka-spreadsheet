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
import walkingkooka.net.header.HasContentType;
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
import walkingkooka.validation.ValueType;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A collection of {@link MediaType} for cells. This will be mostly used by {@link walkingkooka.spreadsheet.importer.SpreadsheetImporter} and {@link walkingkooka.spreadsheet.export.SpreadsheetExporter}.
 */
public final class SpreadsheetMediaTypes implements PublicStaticHelper {

    // json.............................................................................................................

    public static final MediaType JSON_CELL = SpreadsheetCell.CONTENT_TYPE;

    public static final MediaType JSON_CURRENCY = HasContentType.CURRENCY;

    public static final MediaType JSON_DATE_TIME_SYMBOLS = HasContentType.DATE_TIME_SYMBOLS;

    public static final MediaType JSON_DECIMAL_NUMBER_SYMBOLS = HasContentType.DECIMAL_NUMBER_SYMBOLS;

    public static final MediaType JSON_FORM = Form.CONTENT_TYPE;

    public static final MediaType JSON_FORMATTED_VALUE = TextNode.CONTENT_TYPE;

    public static final MediaType JSON_FORMATTER = SpreadsheetFormatterSelector.CONTENT_TYPE;

    public static final MediaType JSON_FORMULA = SpreadsheetFormula.CONTENT_TYPE;

    public static final MediaType JSON_LOCALE = HasContentType.LOCALE;

    public static final MediaType JSON_PARSER = SpreadsheetParserSelector.CONTENT_TYPE;

    public static final MediaType JSON_STYLE = TextStyle.CONTENT_TYPE;

    public static final MediaType JSON_VALUE = HasContentType.json(Object.class);

    public static final MediaType JSON_VALIDATOR = ValidatorSelector.CONTENT_TYPE;

    public static final MediaType JSON_VALUE_TYPE = ValueType.CONTENT_TYPE;

    // object...........................................................................................................

    private final static MediaType MEMORY = MediaType.parse("application/memory");

    public static final MediaType MEMORY_CELL = object(SpreadsheetCell.class);

    public static final MediaType MEMORY_CURRENCY = object(Currency.class);

    public static final MediaType MEMORY_DATE_TIME_SYMBOLS = object(DateTimeSymbols.class);

    public static final MediaType MEMORY_DECIMAL_NUMBER_SYMBOLS = object(DecimalNumberSymbols.class);

    public static final MediaType MEMORY_FORM = object(Form.class);

    public static final MediaType MEMORY_FORMATTED_VALUE = object(TextNode.class);

    public static final MediaType MEMORY_FORMATTER = object(SpreadsheetFormatterSelector.class);

    public static final MediaType MEMORY_FORMULA = object(SpreadsheetFormula.class);

    public static final MediaType MEMORY_LABEL = object(SpreadsheetLabelName.class);

    public static final MediaType MEMORY_LOCALE = object(Locale.class);

    public static final MediaType MEMORY_PARSER = object(SpreadsheetParserSelector.class);

    public static final MediaType MEMORY_SPREADSHEET_METADATA = object(SpreadsheetMetadata.class);

    public static final MediaType MEMORY_STYLE = object(TextStyle.class);

    public static final MediaType MEMORY_VALUE = object(Object.class);

    public static final MediaType MEMORY_VALIDATOR = object(ValidatorSelector.class);

    public static final MediaType MEMORY_VALUE_TYPE = object(ValueType.class);

    public static MediaType object(final Class<?> type) {
        Objects.requireNonNull(type, "type");

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
