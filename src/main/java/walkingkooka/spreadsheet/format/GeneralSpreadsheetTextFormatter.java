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

package walkingkooka.spreadsheet.format;

import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that delegates formatting to {@link SpreadsheetTextFormatContext#defaultFormatText(Object)}.
 */
final class GeneralSpreadsheetTextFormatter extends SpreadsheetTextFormatter2 {

    /**
     * The {@link GeneralSpreadsheetTextFormatter} singleton instance.
     */
    static final GeneralSpreadsheetTextFormatter INSTANCE = new GeneralSpreadsheetTextFormatter();

    /**
     * Private ctor use factory
     */
    private GeneralSpreadsheetTextFormatter() {
        super();
    }

    @Override
    public boolean canFormat(final Object value) {
        return this.isSpreadsheetValue(value);
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {
        return context.defaultFormatText(value);
    }

    @Override
    public String toString() {
        return "General";
    }
}
