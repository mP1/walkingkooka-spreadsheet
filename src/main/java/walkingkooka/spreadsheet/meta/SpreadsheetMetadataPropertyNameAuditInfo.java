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

import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleContext;

import java.util.Optional;

/**
 * This property contains audit info for a spreadsheet.
 */
final class SpreadsheetMetadataPropertyNameAuditInfo extends SpreadsheetMetadataPropertyName<AuditInfo> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameAuditInfo instance() {
        return new SpreadsheetMetadataPropertyNameAuditInfo();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameAuditInfo() {
        super();
    }

    @Override
    AuditInfo checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof AuditInfo
        );
    }

    @Override
    String expected() {
        return AuditInfo.class.getSimpleName();
    }

    @Override
    void accept(final AuditInfo value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitAuditInfo(value);
    }

    @Override
    Optional<AuditInfo> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<AuditInfo> type() {
        return AuditInfo.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    AuditInfo parseUrlFragmentSaveValueNonNull(final String value) {
        throw new UnsupportedOperationException();
    }
}
