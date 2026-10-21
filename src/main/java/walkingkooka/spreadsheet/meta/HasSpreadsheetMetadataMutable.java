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

import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HasSpreadsheetMetadata} that is mutable via the setter.
 */
final class HasSpreadsheetMetadataMutable implements HasSpreadsheetMetadata,
    UsesToStringBuilder {

    static HasSpreadsheetMetadataMutable with(final Optional<SpreadsheetMetadata> spreadsheetMetadata) {
        return new HasSpreadsheetMetadataMutable(
            Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata")
        );
    }

    private HasSpreadsheetMetadataMutable(final Optional<SpreadsheetMetadata> spreadsheetMetadata) {
        super();

        this.spreadsheetMetadata = spreadsheetMetadata;
    }

    @Override
    public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
        return this.spreadsheetMetadata;
    }

    @Override
    public void setSpreadsheetMetadata(final Optional<SpreadsheetMetadata> spreadsheetMetadata) {
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");

        this.spreadsheetMetadata = spreadsheetMetadata;
    }

    private Optional<SpreadsheetMetadata> spreadsheetMetadata;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder toStringBuilder) {
        toStringBuilder.value(this.spreadsheetMetadata);
    }
}
