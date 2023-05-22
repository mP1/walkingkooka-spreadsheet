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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SpreadsheetFormatParserTokenKindTest implements ClassTesting<SpreadsheetFormatParserTokenKind> {

    // isXXX............................................................................................................

    @Test
    public void testIsColour() {
        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER
                ),
                this.collect(SpreadsheetFormatParserTokenKind::isColor)
        );
    }

    @Test
    public void testIsDate() {
        final Set<SpreadsheetFormatParserTokenKind> date = this.collect(SpreadsheetFormatParserTokenKind::isDate);

        this.checkEquals(
                this.collect(k -> {
                    final String name = k.name();
                    return name.startsWith("DAY_") || name.startsWith("MONTH_") || name.startsWith("YEAR_");
                }),
                date
        );

        this.checkNoOverlapOrFail(
                date,
                this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
                date,
                this.collect(SpreadsheetFormatParserTokenKind::isText)
        );

        this.checkNoOverlapOrFail(
                date,
                this.collect(SpreadsheetFormatParserTokenKind::isTime)
        );
    }

    @Test
    public void testIsGeneral() {
        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.GENERAL
                ),
                this.collect(SpreadsheetFormatParserTokenKind::isGeneral)
        );
    }

    @Test
    public void testIsNumber() {
        final Set<SpreadsheetFormatParserTokenKind> number = this.collect(SpreadsheetFormatParserTokenKind::isNumber);

        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.DIGIT,
                        SpreadsheetFormatParserTokenKind.DIGIT_SPACE,
                        SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
                        SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
                        SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
                        SpreadsheetFormatParserTokenKind.EXPONENT,
                        SpreadsheetFormatParserTokenKind.FRACTION,
                        SpreadsheetFormatParserTokenKind.PERCENT,
                        SpreadsheetFormatParserTokenKind.THOUSANDS
                ),
                this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
                number,
                this.collect(SpreadsheetFormatParserTokenKind::isDate)
        );

        this.checkNoOverlapOrFail(
                number,
                this.collect(SpreadsheetFormatParserTokenKind::isDateTime)
        );

        this.checkNoOverlapOrFail(
                number,
                this.collect(SpreadsheetFormatParserTokenKind::isText)
        );

        this.checkNoOverlapOrFail(
                number,
                this.collect(SpreadsheetFormatParserTokenKind::isTime)
        );
    }

    @Test
    public void testIsText() {
        final Set<SpreadsheetFormatParserTokenKind> text = this.collect(SpreadsheetFormatParserTokenKind::isText);

        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        SpreadsheetFormatParserTokenKind.STAR,
                        SpreadsheetFormatParserTokenKind.UNDERSCORE
                ),
                text
        );

        this.checkNoOverlapOrFail(
                text,
                this.collect(SpreadsheetFormatParserTokenKind::isDate)
        );

        this.checkNoOverlapOrFail(
                text,
                this.collect(SpreadsheetFormatParserTokenKind::isDateTime)
        );

        this.checkNoOverlapOrFail(
                text,
                this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
                text,
                this.collect(SpreadsheetFormatParserTokenKind::isTime)
        );
    }

    @Test
    public void testIsTime() {
        final Set<SpreadsheetFormatParserTokenKind> time = this.collect(SpreadsheetFormatParserTokenKind::isTime);

        this.checkEquals(
                Sets.of(
                        SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                        SpreadsheetFormatParserTokenKind.HOUR_WITHOUT_LEADING_ZERO,
                        SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                        SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO,
                        SpreadsheetFormatParserTokenKind.SECONDS_WITH_LEADING_ZERO,
                        SpreadsheetFormatParserTokenKind.SECONDS_WITHOUT_LEADING_ZERO,
                        SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
                        SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER,
                        SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER,
                        SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER
                ),
                time
        );

        this.checkNoOverlapOrFail(
                time,
                this.collect(SpreadsheetFormatParserTokenKind::isDate)
        );

        this.checkNoOverlapOrFail(
                time,
                this.collect(SpreadsheetFormatParserTokenKind::isNumber)
        );

        this.checkNoOverlapOrFail(
                time,
                this.collect(SpreadsheetFormatParserTokenKind::isText)
        );
    }

    private Set<SpreadsheetFormatParserTokenKind> collect(final Predicate<SpreadsheetFormatParserTokenKind> predicate) {
        return Arrays.stream(SpreadsheetFormatParserTokenKind.values())
                .filter(predicate)
                .collect(Collectors.toCollection(Sets::sorted));
    }

    private void checkNoOverlapOrFail(final Set<SpreadsheetFormatParserTokenKind> left,
                                      final Set<SpreadsheetFormatParserTokenKind> right) {
        final Set<SpreadsheetFormatParserTokenKind> overlap = EnumSet.copyOf(left);
        overlap.retainAll(right);

        this.checkEquals(
                Sets.empty(),
                overlap
        );
    }

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
