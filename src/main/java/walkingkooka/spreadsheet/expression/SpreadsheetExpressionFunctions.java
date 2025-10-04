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

package walkingkooka.spreadsheet.expression;

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAlias;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A collection of helpers for getting {@link ExpressionFunction}.
 */
public final class SpreadsheetExpressionFunctions implements PublicStaticHelper {

    /**
     * Function names are {@link CaseSensitivity#INSENSITIVE}
     */
    public static final CaseSensitivity NAME_CASE_SENSITIVITY = SpreadsheetStrings.CASE_SENSITIVITY;

    /**
     * {@link ExpressionFunctionAliasSet#empty(CaseSensitivity)}.
     */
    public static final ExpressionFunctionAliasSet EMPTY_ALIAS_SET = ExpressionFunctionAliasSet.empty(NAME_CASE_SENSITIVITY);

    /**
     * {@link ExpressionFunctionInfoSet#empty(CaseSensitivity)}.
     */
    public static final ExpressionFunctionInfoSet EMPTY_INFO_SET = ExpressionFunctionInfoSet.empty(NAME_CASE_SENSITIVITY);

    /**
     * {@link ExpressionFunctionAlias#with(ExpressionFunctionName, Optional, Optional)}
     */
    public static ExpressionFunctionAlias alias(final ExpressionFunctionName name,
                                                final Optional<ExpressionFunctionSelector> selector,
                                                final Optional<AbsoluteUrl> url) {
        return ExpressionFunctionAlias.with(
            name,
            selector,
            url
        );
    }

    /**
     * {@link ExpressionFunctionInfo#with(AbsoluteUrl, ExpressionFunctionName)}
     */
    public static ExpressionFunctionInfo info(final AbsoluteUrl url,
                                              final ExpressionFunctionName name) {
        return ExpressionFunctionInfo.with(
            url,
            name
        );
    }

    /**
     * {@link ExpressionFunctionInfoSet#setElements(Collection)}.
     */
    public static ExpressionFunctionInfoSet infoSet(final Set<ExpressionFunctionInfo> infos) {
        return EMPTY_INFO_SET.setElements(infos);
    }

    /**
     * Factory that creates a {@link ExpressionFunctionName} with {@link #NAME_CASE_SENSITIVITY}.
     */
    public static ExpressionFunctionName name(final String name) {
        return ExpressionFunctionName.with(name)
            .setCaseSensitivity(NAME_CASE_SENSITIVITY);
    }

    /**
     * {@link ExpressionFunctionAlias#parse(String, CaseSensitivity)}
     */
    public static ExpressionFunctionAlias parseAlias(final String text) {
        return ExpressionFunctionAlias.parse(
            text,
            NAME_CASE_SENSITIVITY
        );
    }

    /**
     * {@link ExpressionFunctionAliasSet#parse(String, CaseSensitivity)}
     */
    public static ExpressionFunctionAliasSet parseAliasSet(final String text) {
        return ExpressionFunctionAliasSet.parse(
            text,
            NAME_CASE_SENSITIVITY
        );
    }

    /**
     * {@link ExpressionFunctionInfo#parse(String, CaseSensitivity)
     */
    public static ExpressionFunctionInfo parseInfo(final String text) {
        return ExpressionFunctionInfo.parse(
            text,
            NAME_CASE_SENSITIVITY
        );
    }

    /**
     * {@link ExpressionFunctionInfoSet#parse(String, CaseSensitivity)
     */
    public static ExpressionFunctionInfoSet parseInfoSet(final String text) {
        return ExpressionFunctionInfoSet.parse(
            text,
            NAME_CASE_SENSITIVITY
        );
    }

    /**
     * {@link ExpressionFunctionSelector#parse(String, CaseSensitivity)}
     */
    public static ExpressionFunctionSelector parseSelector(final String selector) {
        return ExpressionFunctionSelector.parse(
            selector,
            NAME_CASE_SENSITIVITY
        );
    }

    /**
     * {@link ErrorExpressionFunction}
     */
    public static ExpressionFunction<SpreadsheetError, ExpressionEvaluationContext> error() {
        return ErrorExpressionFunction.INSTANCE;
    }

    private SpreadsheetExpressionFunctions() {
        throw new UnsupportedOperationException();
    }
}
