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
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.JsonNode;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataEmptyTest extends SpreadsheetMetadataTestCase<SpreadsheetMetadataEmpty> {

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadata.EMPTY, SpreadsheetMetadata.with(Maps.empty()));
    }

    @Test
    public void testValue() {
        assertSame(SpreadsheetMetadata.EMPTY.value(), SpreadsheetMetadata.EMPTY.value());
    }

    // set..............................................................................................................

    @Test
    public void testSet() {
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress familyName = EmailAddress.parse("user@example.com");

        this.setAndCheck(SpreadsheetMetadata.EMPTY,
                propertyName,
                familyName,
                SpreadsheetMetadata.with(Maps.of(propertyName, familyName)));
    }

    // HateosResourceTesting............................................................................................

    @Test
    public void testHateosLinkIdMissingIdFails() {
        assertThrows(IllegalStateException.class, () -> SpreadsheetMetadataEmpty.instance().hateosLinkId());
    }

    // SpreadsheetMetadataVisitor.......................................................................................

    @Test
    public void testAccept() {
        SpreadsheetMetadata.EMPTY.accept(new SpreadsheetMetadataVisitor() {
        });
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadata.EMPTY, "");
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testFromEmptyJsonObject() {
        assertSame(SpreadsheetMetadata.EMPTY, SpreadsheetMetadata.unmarshall(JsonNode.object(), this.unmarshallContext()));
    }

    // helper...........................................................................................................

    @Override
    public SpreadsheetMetadataEmpty createObject() {
        return Cast.to(SpreadsheetMetadata.EMPTY);
    }

    @Override
    Class<SpreadsheetMetadataEmpty> metadataType() {
        return SpreadsheetMetadataEmpty.class;
    }
}
