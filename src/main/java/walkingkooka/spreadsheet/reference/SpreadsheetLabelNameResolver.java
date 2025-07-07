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

import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;

/**
 * A SAM interface that defines a single method to resolve any {@link SpreadsheetLabelName} selections to a NON
 * {@link SpreadsheetSelection}. In the cases of a unknown label, a {@link RuntimeException} will be thrown.
 */
public interface SpreadsheetLabelNameResolver {

    /**
     * Helper that resolves labels into a {@link SpreadsheetSelection}. Non {@link SpreadsheetExpressionReference}
     * return an {@link Optional#empty()}.
     */
    default Optional<SpreadsheetSelection> resolveIfLabel(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return reference instanceof SpreadsheetExpressionReference ?
            this.resolveIfLabel((SpreadsheetSelection) reference) :
            Optional.empty();
    }

    /**
     * Resolves a {@link SpreadsheetSelection} never returning a {@link SpreadsheetLabelName}, resolving labels to their
     * target {@link SpreadsheetSelection}.
     * If a label was not found an {@link Optional#empty()}.
     */
    default Optional<SpreadsheetSelection> resolveIfLabel(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        return selection.isLabelName() ?
            this.resolveLabel(selection.toLabelName()) :
            Optional.of(selection);
    }

    /**
     * Resolves a {@link SpreadsheetSelection} into a non {@link SpreadsheetLabelName} reference.
     */
    default SpreadsheetSelection resolveIfLabelOrFail(final SpreadsheetSelection selection) {
        return this.resolveIfLabel(selection)
            .orElseThrow(
                () -> this.labelNotFound(selection.toLabelName())
            );
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
            .orElseThrow(
                () -> this.labelNotFound(labelName)
            );
    }

    /**
     * Reports a {@link SpreadsheetLabelName} was not found.
     */
    default LabelNotFoundException labelNotFound(final SpreadsheetLabelName labelName) {
        return new LabelNotFoundException(labelName);
    }
}
