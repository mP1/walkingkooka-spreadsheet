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

import java.util.Locale;

public abstract class SpreadsheetMetadataPropertyNameEmailAddressTestCase<N extends SpreadsheetMetadataPropertyNameEmailAddress> extends SpreadsheetMetadataPropertyNameTestCase<N, EmailAddress> {

    SpreadsheetMetadataPropertyNameEmailAddressTestCase() {
        super();
    }

    @Test
    public final void testCheckValueWithInvalidEmailFails() {
        this.checkValueFails(
                "invalid email",
                "Metadata " + this.createName() + "=\"invalid email\", Expected EmailAddress");
    }

    @Test
    public final void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(Locale.ENGLISH, null);
    }

    @Override
    final EmailAddress propertyValue() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    final String propertyValueType() {
        return "EmailAddress";
    }
}
