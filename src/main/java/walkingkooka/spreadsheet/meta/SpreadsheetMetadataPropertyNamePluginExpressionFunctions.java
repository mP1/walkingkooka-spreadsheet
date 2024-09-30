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

import walkingkooka.collect.set.Sets;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link ExpressionFunctionInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginExpressionFunctions extends SpreadsheetMetadataPropertyNamePlugin<ExpressionFunctionInfoSet, ExpressionFunctionInfo, ExpressionFunctionName> {

    static {
        ExpressionFunctionInfoSet.with(Sets.empty()); // force registry of json marshaller
    }

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNamePluginExpressionFunctions instance() {
        return new SpreadsheetMetadataPropertyNamePluginExpressionFunctions();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNamePluginExpressionFunctions() {
        super("functions");
    }

    @Override
    void accept(final ExpressionFunctionInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFunctions(value);
    }

    @Override
    ExpressionFunctionInfoSet parseUrlFragmentSaveValue0(final String value) {
        return ExpressionFunctionInfoSet.parse(value);
    }

    @Override
    Class<ExpressionFunctionInfoSet> type() {
        return ExpressionFunctionInfoSet.class;
    }
}
