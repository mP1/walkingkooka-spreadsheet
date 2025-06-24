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

import org.junit.jupiter.api.Test;
import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameAuditInfoTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameAuditInfo, AuditInfo> {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
                LocaleContexts.jre(Locale.ENGLISH),
                null
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameAuditInfo.instance(),
                "auditInfo"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameAuditInfo createName() {
        return SpreadsheetMetadataPropertyNameAuditInfo.instance();
    }

    @Override
    AuditInfo propertyValue() {
        return AuditInfo.with(
                EmailAddress.parse("creator@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("modified@example.com"),
                LocalDateTime.MAX
        );
    }

    @Override
    String propertyValueType() {
        return AuditInfo.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameAuditInfo> type() {
        return SpreadsheetMetadataPropertyNameAuditInfo.class;
    }
}
