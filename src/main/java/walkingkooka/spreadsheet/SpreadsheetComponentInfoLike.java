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

package walkingkooka.spreadsheet;

import walkingkooka.naming.HasName;
import walkingkooka.net.http.server.hateos.HateosResource;

import java.util.Optional;

/**
 * Captures the common members for a SpreadsheetComponent INFO.
 */
public interface SpreadsheetComponentInfoLike<I extends SpreadsheetComponentInfoLike<I, N>, N extends SpreadsheetComponentNameLike<N>> extends
        HasName<N>,
        Comparable<I>,
        HateosResource<N> {

    // Comparable.......................................................................................................

    @Override
    default int compareTo(final I other) {
        return this.name().compareTo(other.name());
    }

    // HateoResource....................................................................................................

    @Override
    default String hateosLinkId() {
        return this.name().value();
    }

    @Override
    default Optional<N> id() {
        return Optional.of(
                this.name()
        );
    }
}