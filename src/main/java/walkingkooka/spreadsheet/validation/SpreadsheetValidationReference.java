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

package walkingkooka.spreadsheet.validation;

import walkingkooka.net.HasUrlFragment;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationReference;

import java.util.Comparator;

/**
 * Base class for {@link SpreadsheetSelection} that is also a {@link ValidationReference}.
 */
public interface SpreadsheetValidationReference extends ValidationReference,
    HasUrlFragment {

    Comparator<SpreadsheetValidationReference> IGNORES_REFERENCE_KIND_COMPARATOR = (final SpreadsheetValidationReference left,
                                                                                    final SpreadsheetValidationReference right) ->
        SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR.compare(
            left.toSpreadsheetSelection(),
            right.toSpreadsheetSelection()
        );

    boolean isCell();

    boolean isLabelName();

    SpreadsheetCellReference toCell();

    SpreadsheetLabelName toLabelName();

    default SpreadsheetSelection toSpreadsheetSelection() {
        return (SpreadsheetSelection) this;
    }

    // ValidationError..................................................................................................

    @Override
    default ValidationError<SpreadsheetValidationReference> setValidationErrorMessage(final String message) {
        return SpreadsheetError.parse(message)
            .toValidationError(this);
    }
}
