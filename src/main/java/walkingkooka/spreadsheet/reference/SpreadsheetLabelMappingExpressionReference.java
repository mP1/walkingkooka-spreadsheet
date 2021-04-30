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

import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

/**
 * A tag interface that selects some sub classes of {@link SpreadsheetExpressionReference} as
 * candidates for {@link SpreadsheetLabelMapping#reference()}.
 */
public interface SpreadsheetLabelMappingExpressionReference extends ExpressionReference {

    // This method is not intended to be consumed and should be considered internal.
    JsonNode marshall(final JsonNodeMarshallContext context);
}
