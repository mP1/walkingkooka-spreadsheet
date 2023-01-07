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

import java.text.DecimalFormatSymbols;

final class SpreadsheetMetadataPropertyNamePositiveSign extends SpreadsheetMetadataPropertyNameCharacter {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNamePositiveSign instance() {
        return new SpreadsheetMetadataPropertyNamePositiveSign();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNamePositiveSign() {
        super();
    }

    @Override
    void accept(final Character value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitPositiveSign(value);
    }

    @Override
    Character extractLocaleValueCharacter(final DecimalFormatSymbols symbols) {
        return '+'; // TODO Find propery WAY.
    }
}
