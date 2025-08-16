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

/**
 * When true the UI will hide scrollbars unless the mouse hovers near the right and bottom edges of the viewport.
 */
final class SpreadsheetMetadataPropertyNameBooleanAutoHideScrollbars extends SpreadsheetMetadataPropertyNameBoolean {

    /**
     * Getter rather than field to allow lazily creation.
     */
    static SpreadsheetMetadataPropertyNameBooleanAutoHideScrollbars instance() {
        return new SpreadsheetMetadataPropertyNameBooleanAutoHideScrollbars();
    }

    private SpreadsheetMetadataPropertyNameBooleanAutoHideScrollbars() {
        super("autoHideScrollbars");
    }

    @Override
    void accept(final Boolean value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitAutoHideScrollbars(value);
    }
}
