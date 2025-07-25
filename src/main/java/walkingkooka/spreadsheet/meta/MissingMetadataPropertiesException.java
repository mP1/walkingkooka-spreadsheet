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

import java.util.Set;
import java.util.stream.Collectors;

final class MissingMetadataPropertiesException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    MissingMetadataPropertiesException(final Set<SpreadsheetMetadataPropertyName<?>> missing) {
        super();

        this.missing = missing;
    }

    @Override
    public String getMessage() {
        return this.missing.stream()
            .map(Object::toString)
            .collect(
                Collectors.joining(
                    ", ",
                    "Metadata missing: ",
                    ""
                )
            );
    }

    final Set<SpreadsheetMetadataPropertyName<?>> missing;
}
