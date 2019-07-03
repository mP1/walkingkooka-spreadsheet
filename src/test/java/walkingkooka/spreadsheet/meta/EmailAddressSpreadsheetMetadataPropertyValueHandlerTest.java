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
import walkingkooka.net.email.EmailAddress;

public final class EmailAddressSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase<EmailAddressSpreadsheetMetadataPropertyValueHandler, EmailAddress> {

    @Test
    public void testFromJsonNode() {
        final EmailAddress emailAddress = this.propertyValue();
        this.fromJsonNodeAndCheck(emailAddress.toJsonNode(), emailAddress);
    }

    @Test
    public void testToJsonNode() {
        final EmailAddress emailAddress = this.propertyValue();
        this.toJsonNodeAndCheck(emailAddress, emailAddress.toJsonNode());
    }

    @Override
    EmailAddressSpreadsheetMetadataPropertyValueHandler handler() {
        return EmailAddressSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<EmailAddress> propertyName() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    @Override
    EmailAddress propertyValue() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    String propertyValueType() {
        return EmailAddress.class.getSimpleName();
    }

    @Override
    public Class<EmailAddressSpreadsheetMetadataPropertyValueHandler> type() {
        return EmailAddressSpreadsheetMetadataPropertyValueHandler.class;
    }
}
