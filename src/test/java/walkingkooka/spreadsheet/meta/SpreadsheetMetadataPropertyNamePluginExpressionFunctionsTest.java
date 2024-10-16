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
import walkingkooka.net.Url;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAlias;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;

public final class SpreadsheetMetadataPropertyNamePluginExpressionFunctionsTest extends SpreadsheetMetadataPropertyNamePluginTestCase<SpreadsheetMetadataPropertyNamePluginExpressionFunctions,
        ExpressionFunctionName,
        ExpressionFunctionInfo,
        ExpressionFunctionInfoSet,
        ExpressionFunctionSelector,
        ExpressionFunctionAlias,
        ExpressionFunctionAliasSet> {

    @Override
    SpreadsheetMetadataPropertyNamePluginExpressionFunctions createName() {
        return SpreadsheetMetadataPropertyNamePluginExpressionFunctions.instance();
    }

    @Override
    ExpressionFunctionInfoSet propertyValue() {
        return ExpressionFunctionInfoSet.with(
                Sets.of(
                        ExpressionFunctionInfo.with(
                                Url.parseAbsolute("https://example.com/test-function-111"),
                                ExpressionFunctionName.with("test-function")
                        )
                )
        );
    }

    @Override
    String propertyValueType() {
        return ExpressionFunctionInfoSet.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyNamePluginExpressionFunctions> type() {
        return SpreadsheetMetadataPropertyNamePluginExpressionFunctions.class;
    }
}
