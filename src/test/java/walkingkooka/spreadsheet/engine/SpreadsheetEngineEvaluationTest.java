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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

public final class SpreadsheetEngineEvaluationTest implements ClassTesting2<SpreadsheetEngineEvaluation>,
        HasJsonNodeTesting<SpreadsheetEngineEvaluation> {

    // HasJsonNode..........................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testFromJsonNodeNullFails() {
        this.fromJsonNodeFails(JsonNode.nullNode());
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(123));
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array());
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object());
    }

    @Test
    public void testFromJsonNodeStringUnknownFails() {
        this.fromJsonNodeFails(JsonNode.string("123"));
    }

    @Test
    public void testToJsonRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testToJsonRoundtrip2() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetEngineEvaluation.SKIP_EVALUATE);
    }

    @Override
    public Class<SpreadsheetEngineEvaluation> type() {
        return SpreadsheetEngineEvaluation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public SpreadsheetEngineEvaluation createHasJsonNode() {
        return SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY;
    }

    @Override
    public SpreadsheetEngineEvaluation fromJsonNode(final JsonNode node) {
        return SpreadsheetEngineEvaluation.fromJsonNode(node);
    }
}
