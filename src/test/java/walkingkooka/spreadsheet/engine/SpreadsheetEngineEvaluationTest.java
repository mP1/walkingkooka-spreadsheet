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
import walkingkooka.net.header.LinkRelation;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetEngineEvaluationTest implements ClassTesting2<SpreadsheetEngineEvaluation>,
    JsonNodeMarshallingTesting<SpreadsheetEngineEvaluation>,
    ParseStringTesting<SpreadsheetEngineEvaluation> {

    // parse............................................................................................................

    @Test
    public void testParseWithComputeIfNecessary() {
        this.parseStringAndCheck(
            "compute-if-necessary",
            SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY
        );
    }

    @Test
    public void testParseWithForceRecompute() {
        this.parseStringAndCheck(
            "force-recompute",
            SpreadsheetEngineEvaluation.FORCE_RECOMPUTE
        );
    }

    @Override
    public SpreadsheetEngineEvaluation parseString(final String text) {
        return SpreadsheetEngineEvaluation.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallStringUnknownFails() {
        this.unmarshallFails(JsonNode.string("123"));
    }

    @Test
    public void testMarshallRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testMarshallRoundtrip2() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetEngineEvaluation.SKIP_EVALUATE);
    }

    // HasLinkRelation..................................................................................................

    @Test
    public void testToLinkRelationClearValueErrorSkipEvaluate() {
        toLinkRelationAndCheck(SpreadsheetEngineEvaluation.CLEAR_VALUE_ERROR_SKIP_EVALUATE, "clear-value-error-skip-evaluate");
    }

    @Test
    public void testToLinkRelationComputeIfNecessary() {
        toLinkRelationAndCheck(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, "compute-if-necessary");
    }

    @Test
    public void testToLinkRelationSkipEvaluate() {
        toLinkRelationAndCheck(SpreadsheetEngineEvaluation.SKIP_EVALUATE, "skip-evaluate");
    }

    private void toLinkRelationAndCheck(final SpreadsheetEngineEvaluation evaluation,
                                        final String linkRelation) {
        this.checkEquals(LinkRelation.with(linkRelation),
            evaluation.toLinkRelation(),
            () -> evaluation + ".toLinkRelation");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetEngineEvaluation> type() {
        return SpreadsheetEngineEvaluation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetEngineEvaluation createJsonNodeMarshallingValue() {
        return SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY;
    }

    @Override
    public SpreadsheetEngineEvaluation unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetEngineEvaluation.unmarshall(node, context);
    }
}
