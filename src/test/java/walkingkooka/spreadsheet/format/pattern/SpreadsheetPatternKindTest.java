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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetPatternKindTest implements ClassTesting<SpreadsheetPatternKind> {

    @Test
    public void testDateTimeFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
                "date-time-format-pattern"
        );
    }

    @Test
    public void testDateTimeParse() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERNS,
                "date-time-parse-patterns"
        );
    }

    @Test
    public void testTextFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                "text-format-pattern"
        );
    }

    @Test
    public void testTimeFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
                "time-format-pattern"
        );
    }

    @Test
    public void testTimeParse() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TIME_PARSE_PATTERNS,
                "time-parse-patterns"
        );
    }

    private void typeNameAndCheck(final SpreadsheetPatternKind kind,
                                  final String expected) {
        this.checkEquals(
                expected,
                kind.typeName(),
                () -> kind.toString()
        );
    }

    @Override
    public Class<SpreadsheetPatternKind> type() {
        return SpreadsheetPatternKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
