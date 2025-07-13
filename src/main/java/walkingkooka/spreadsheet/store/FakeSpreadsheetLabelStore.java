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

package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.store.FakeStore;
import walkingkooka.test.Fake;

import java.util.Set;

public class FakeSpreadsheetLabelStore extends FakeStore<SpreadsheetLabelName, SpreadsheetLabelMapping> implements SpreadsheetLabelStore, Fake {

    @Override
    public Set<SpreadsheetLabelMapping> findLabelsByName(final String text,
                                                         final int offset,
                                                         final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellReferenceOrRange> loadCellOrCellRanges(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetLabelMapping> findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                                final int offset,
                                                                final int count) {
        throw new UnsupportedOperationException();
    }
}
