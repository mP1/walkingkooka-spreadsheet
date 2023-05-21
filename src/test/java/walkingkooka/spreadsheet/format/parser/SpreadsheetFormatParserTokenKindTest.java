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

package walkingkooka.spreadsheet.format.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public final class SpreadsheetFormatParserTokenKindTest implements ClassTesting<SpreadsheetFormatParserTokenKind> {

    // XXXOnly..........................................................................................................

    @Test
    public void testTextOnly() {
        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
                        SpreadsheetFormatParserTokenKind.STAR,
                        SpreadsheetFormatParserTokenKind.UNDERSCORE
                ),
                SpreadsheetFormatParserTokenKind.textOnly()
        );
    }

    @Test
    public void testFormatOnlyAndFormatAndParserOnlyAndTextOnly() {
        final Set<SpreadsheetFormatParserTokenKind> kinds = Sets.ordered();
        kinds.addAll(SpreadsheetFormatParserTokenKind.formatOnly());
        kinds.addAll(SpreadsheetFormatParserTokenKind.formatAndParseOnly());
        kinds.addAll(SpreadsheetFormatParserTokenKind.textOnly());

        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                        SpreadsheetFormatParserTokenKind.CONDITION,
                        SpreadsheetFormatParserTokenKind.GENERAL,
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
                        SpreadsheetFormatParserTokenKind.STAR,
                        SpreadsheetFormatParserTokenKind.UNDERSCORE
                ),
                kinds
        );
    }

    @Test
    public void testOnlyNotEmpty() throws Exception {
        final Map<String, Set<SpreadsheetFormatParserTokenKind>> methodNameToKinds = Maps.sorted();

        for (final Method method : SpreadsheetFormatParserTokenKind.class.getDeclaredMethods()) {
            if (false == method.getName().endsWith("Only")) {
                continue;
            }

            methodNameToKinds.put(
                    method.getName(),
                    Cast.to(
                            method.invoke(null)
                    )
            );
        }

        for (final Map.Entry<String, Set<SpreadsheetFormatParserTokenKind>> methodNameAndKinds : methodNameToKinds.entrySet()) {
            final String methodName = methodNameAndKinds.getKey();
            final Set<SpreadsheetFormatParserTokenKind> kinds = methodNameAndKinds.getValue();

            for (final Map.Entry<String, Set<SpreadsheetFormatParserTokenKind>> methodNameAndKinds2 : methodNameToKinds.entrySet()) {
                final String methodName2 = methodNameAndKinds2.getKey();
                if (methodName.equals(methodName2)) {
                    continue;
                }

                final Set<SpreadsheetFormatParserTokenKind> duplicates = Sets.sorted();
                duplicates.addAll(kinds);
                duplicates.retainAll(methodNameAndKinds2.getValue());

                this.checkEquals(
                        Sets.empty(),
                        duplicates,
                        () -> methodName + " kinds also appear in " + methodName2
                );
            }
        }
    }

    // labelText........................................................................................................

    @Test
    public void testLabelTextForCOLOR_NAME() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.COLOR_NAME,
                "Color name"
        );
    }

    @Test
    public void testLabelTextForCONDITION() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.CONDITION,
                "Condition"
        );
    }

    @Test
    public void testLabelTextForDAY_WITH_LEADING_ZERO() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                "Day with leading zero"
        );
    }

    @Test
    public void testLabelTextForAMPM_FULL_LOWER() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
                "AMPM full lower"
        );
    }

    private void labelTextAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                   final String expected) {
        this.checkEquals(
                expected,
                kind.labelText(),
                () -> kind + " labelText()"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatParserTokenKind> type() {
        return SpreadsheetFormatParserTokenKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
