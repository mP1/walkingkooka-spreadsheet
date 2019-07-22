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

import walkingkooka.color.Color;

import java.util.Optional;

/**
 * A {@link java.util.function.Function} that always returns nothing.
 */
final class EmptySpreadsheetMetadataNumberToColorFunction extends SpreadsheetMetadataNumberToColorFunction {

    /**
     * Singleton
     */
    static EmptySpreadsheetMetadataNumberToColorFunction INSTANCE = new EmptySpreadsheetMetadataNumberToColorFunction();

    private EmptySpreadsheetMetadataNumberToColorFunction() {
        super();
    }

    @Override
    Optional<Color> apply0(final Integer number) {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "{}";
    }
}
