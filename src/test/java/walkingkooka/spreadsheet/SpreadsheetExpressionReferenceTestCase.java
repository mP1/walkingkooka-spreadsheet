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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.predicate.Predicates;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.IsMethodTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;

import java.util.function.Predicate;

public abstract class SpreadsheetExpressionReferenceTestCase<R extends SpreadsheetExpressionReference> implements ClassTesting2<R>,
        HasJsonNodeTesting<R>,
        IsMethodTesting<R>,
        ToStringTesting<R> {

    SpreadsheetExpressionReferenceTestCase() {
        super();
    }

    @Test
    public final void testToJsonNode() {
        final R reference = this.createReference();
        this.toJsonNodeAndCheck(reference, JsonNode.string(reference.toString()));
    }

    abstract R createReference();

    // HasJsonNode......................................................................................................

    @Override
    public final R createHasJsonNode() {
        return this.createReference();
    }

    // IsMethodTesting...................................................................................................

    @Override
    public final R createIsMethodObject() {
        return this.createReference();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return "";//ExpressionReference.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.never();
    }
}
