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

package walkingkooka.spreadsheet.viewport;

import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.Optional;

public class FakeSpreadsheetViewportNavigationContext implements SpreadsheetViewportNavigationContext {
    @Override
    public boolean isColumnHidden(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRowHidden(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumnReference> leftColumn(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumnReference> rightColumn(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> upRow(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> downRow(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumnReference> leftPixels(final SpreadsheetColumnReference column,
                                                           final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumnReference> rightPixels(final SpreadsheetColumnReference column,
                                                            final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> upPixels(final SpreadsheetRowReference row,
                                                      final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> downPixels(final SpreadsheetRowReference row,
                                                        final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetViewportWindows windows(final SpreadsheetViewport viewport) {
        throw new UnsupportedOperationException();
    }
}
