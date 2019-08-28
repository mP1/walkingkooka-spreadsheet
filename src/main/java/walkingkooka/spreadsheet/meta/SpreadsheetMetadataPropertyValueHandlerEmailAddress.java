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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for {@link EmailAddress} entries.
 */
final class SpreadsheetMetadataPropertyValueHandlerEmailAddress extends SpreadsheetMetadataPropertyValueHandler<EmailAddress> {

    /**
     * A singleton
     */
    static final SpreadsheetMetadataPropertyValueHandlerEmailAddress INSTANCE = new SpreadsheetMetadataPropertyValueHandlerEmailAddress();

    private SpreadsheetMetadataPropertyValueHandlerEmailAddress() {
        super();
    }

    @Override
    void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, EmailAddress.class, name);
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return EmailAddress.class.getSimpleName();
    }

    @Override
    public String toString() {
        return EmailAddress.class.getSimpleName();
    }

    // JsonNodeContext..................................................................................................

    @Override
    EmailAddress fromJsonNode(final JsonNode node,
                              final SpreadsheetMetadataPropertyName<?> name,
                              final FromJsonNodeContext context) {
        return context.fromJsonNode(node, EmailAddress.class);
    }

    @Override
    JsonNode toJsonNode(final EmailAddress value,
                        final ToJsonNodeContext context) {
        return context.toJsonNode(value);
    }
}
