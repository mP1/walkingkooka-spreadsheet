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

package walkingkooka.spreadsheet.reference;

import walkingkooka.spreadsheet.value.HasSpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * This exception is used to report that a label was required but not found.
 */
public class LabelNotFoundException extends RuntimeException implements HasSpreadsheetError {

    private static final long serialVersionUID = 0L;

    public LabelNotFoundException(final SpreadsheetLabelName label) {
        super(
            "Label " +
                CharSequences.quoteAndEscape(
                    Objects.requireNonNull(label, "label")
                        .value()
                ) +
                " not found"
        );

        this.label = label;
    }

    public SpreadsheetLabelName label() {
        return this.label;
    }

    private final SpreadsheetLabelName label;

    // HasSpreadsheetError..............................................................................................

    @Override
    public SpreadsheetError spreadsheetError() {
        return SpreadsheetError.referenceNotFound(this.label);
    }
}
