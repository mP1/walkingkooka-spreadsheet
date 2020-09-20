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

import walkingkooka.net.email.EmailAddress;

abstract class SpreadsheetMetadataPropertyNameEmailAddress extends SpreadsheetMetadataPropertyName<EmailAddress> {

    /**
     * Package private constructor to limit sub classing.
     */
    SpreadsheetMetadataPropertyNameEmailAddress(final String name) {
        super(name);
    }

    @Override
    final void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof EmailAddress);
    }

    @Override
    final String expected() {
        return EmailAddress.class.getSimpleName();
    }

    @Override
    Class<EmailAddress> type() {
        return EmailAddress.class;
    }
}
