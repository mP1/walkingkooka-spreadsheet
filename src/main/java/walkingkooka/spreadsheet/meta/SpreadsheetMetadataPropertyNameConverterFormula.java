
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

package walkingkooka.spreadsheet.meta;

import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.spreadsheet.SpreadsheetCell;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link ConverterSelector} used during
 * {@link SpreadsheetCell#formula()} expression evaluation.
 */
final class SpreadsheetMetadataPropertyNameConverterFormula extends SpreadsheetMetadataPropertyNameConverter {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameConverterFormula instance() {
        return new SpreadsheetMetadataPropertyNameConverterFormula();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameConverterFormula() {
        super(
                "formula-converter"
        );
    }

    @Override
    void accept(final ConverterSelector selector,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFormulaConverter(selector);
    }
}
