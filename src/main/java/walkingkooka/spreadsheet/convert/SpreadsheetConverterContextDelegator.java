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

package walkingkooka.spreadsheet.convert;

import walkingkooka.convert.Converter;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;

import java.util.Locale;
import java.util.Optional;

public interface SpreadsheetConverterContextDelegator extends SpreadsheetConverterContext,
    JsonNodeConverterContextDelegator,
    LocaleContextDelegator {

    @Override
    default JsonNodeConverterContext jsonNodeConverterContext() {
        return this.spreadsheetConverterContext();
    }

    @Override
    default Locale locale() {
        return this.localeContext()
            .locale();
    }

    @Override
    default LocaleContext localeContext() {
        return this.spreadsheetConverterContext();
    }

    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetConverterContext()
            .converter();
    }

    @Override
    default Optional<StoragePath> currentWorkingDirectory() {
        return this.spreadsheetConverterContext()
            .currentWorkingDirectory();
    }

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetConverterContext()
            .spreadsheetMetadata();
    }

    @Override
    default Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetConverterContext()
            .resolveLabel(labelName);
    }

    @Override
    default SpreadsheetExpressionReference validationReference() {
        return this.spreadsheetConverterContext()
            .validationReference();
    }

    SpreadsheetConverterContext spreadsheetConverterContext();
}
