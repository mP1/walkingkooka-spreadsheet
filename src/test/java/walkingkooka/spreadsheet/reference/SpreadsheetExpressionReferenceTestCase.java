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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.predicate.Predicates;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.function.Predicate;

walkingkooka.reflect.*;
        walkingkooka.reflect.*;
        walkingkooka.reflect.*;
        walkingkooka.reflect.*;

public abstract class SpreadsheetExpressionReferenceTestCase<R extends SpreadsheetExpressionReference> implements ClassTesting2<R>,
        HashCodeEqualsDefinedTesting2<R>,
        JsonNodeMarshallingTesting<R>,
        IsMethodTesting<R>,
        ToStringTesting<R> {

    SpreadsheetExpressionReferenceTestCase() {
        super();
    }

    @Test
    public final void testJsonNodeMarshall() {
        final R reference = this.createReference();
        this.marshallAndCheck(reference, JsonNode.string(reference.toString()));
    }

    abstract R createReference();

    // HashCodeEqualsDefinedTesting.....................................................................................

    @Override
    public final R createObject() {
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

    // JsonNodeTesting..................................................................................................

    @Override
    public final R createJsonNodeMappingValue() {
        return this.createReference();
    }
}
