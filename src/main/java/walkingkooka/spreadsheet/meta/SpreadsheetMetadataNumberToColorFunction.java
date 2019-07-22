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
import java.util.function.Function;

abstract class SpreadsheetMetadataNumberToColorFunction implements Function<Integer, Optional<Color>> {

    SpreadsheetMetadataNumberToColorFunction() {
        super();
    }

    @Override
    public Optional<Color> apply(final Integer number) {
        SpreadsheetMetadata.checkColorNumber(number);
        return this.apply0(number);
    }

    abstract Optional<Color> apply0(final Integer number);

    abstract public String toString();
}
