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

import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;

import java.util.Objects;

/**
 * A {@link SpreadsheetLabelNameResolver} that uses a {@link SpreadsheetLabelStore} to query the final non label target.
 */
final class SpreadsheetLabelStoreSpreadsheetLabelNameResolver implements SpreadsheetLabelNameResolver {

    static SpreadsheetLabelStoreSpreadsheetLabelNameResolver with(final SpreadsheetLabelStore labelStore) {
        return new SpreadsheetLabelStoreSpreadsheetLabelNameResolver(
                Objects.requireNonNull(labelStore, "labelStore")
        );
    }

    private SpreadsheetLabelStoreSpreadsheetLabelNameResolver(final SpreadsheetLabelStore labelStore) {
        this.labelStore = labelStore;
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return SpreadsheetLabelStoreSpreadsheetLabelNameResolverSpreadsheetSelectionVisitor.resolveLabel(
                labelName,
                this.labelStore
        );
    }

    private final SpreadsheetLabelStore labelStore;

    public String toString() {
        return this.labelStore.toString();
    }
}
