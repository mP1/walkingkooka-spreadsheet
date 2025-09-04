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

package walkingkooka.spreadsheet.validation.form;

import walkingkooka.Cast;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.validation.form.FormHandlerContext;

import java.util.Comparator;

/**
 * A type-safe {@link FormHandlerContext} using {@link SpreadsheetExpressionReference} as the {@link walkingkooka.validation.ValidationReference}.
 * No new methods are added.
 */
public interface SpreadsheetFormHandlerContext extends FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> {

    @Override
    SpreadsheetFormHandlerContext cloneEnvironment();

    @Override
    <T> SpreadsheetFormHandlerContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                          final T value);

    /**
     * {@link SpreadsheetSelection#IGNORES_REFERENCE_KIND_COMPARATOR}
     */
    @Override
    default Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        return Cast.to(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
    }

    @Override
    SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference spreadsheetExpressionReference);
}
