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

import java.util.Objects;
import java.util.Optional;

/**
 * A SAM interface that defines a single method to resolve any {@link SpreadsheetLabelName} selections to a NON
 * {@link SpreadsheetSelection}. In the cases of a unknown label, a {@link RuntimeException} will be thrown.
 */
public interface SpreadsheetLabelNameResolver {

    /**
     * Resolves a {@link SpreadsheetSelection} if it is a {@link SpreadsheetLabelName} otherwise returning the target.
     * This must never return a {@link SpreadsheetLabelName}.
     */
    default SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        return selection.isLabelName() ?
                this.resolveLabelOrFail(selection.toLabelName()) :
                selection;
    }

    /**
     * Resolves the given {@link SpreadsheetLabelName} into a non label {@link SpreadsheetSelection}.
     */
    Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName);

    /**
     * Resolves the given {@link SpreadsheetLabelName} into a non label {@link SpreadsheetSelection}.
     * Unknown labels will result in an {@link LabelNotFoundException} being thrown.
     */
    default SpreadsheetSelection resolveLabelOrFail(final SpreadsheetLabelName labelName) {
        return this.resolveLabel(labelName)
                .orElseThrow(() -> new LabelNotFoundException(labelName));
    }
}
