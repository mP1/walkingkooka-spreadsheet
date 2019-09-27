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

package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

public abstract class IdentityTestCase<V extends Identity<I>, I extends IdentityId> implements IdentityTesting<V, I>,
        JsonNodeMarshallingTesting<V> {

    IdentityTestCase() {
        super();
    }

    // JsonNodeMarshallingTesting...............................................................................................

    @Test
    public void testJsonNodeUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array(), JsonNodeException.class);
    }

    @Test
    public void testJsonNodeUnmarshallObjectFails() {
        this.unmarshallFails(JsonNode.object(), JsonNodeException.class);
    }
}
