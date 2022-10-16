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
import walkingkooka.tree.json.marshall.JsonNodeContext;

public final class SpreadsheetPatternKindTest implements ClassTesting<SpreadsheetPatternKind> {

    @Test
    public void testDateTimeFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
                SpreadsheetDateTimeFormatPattern.class
        );
    }

    @Test
    public void testDateTimeParse() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERNS,
                SpreadsheetDateTimeParsePatterns.class
        );
    }

    @Test
    public void testTextFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetTextFormatPattern.class
        );
    }

    @Test
    public void testTimeFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
                SpreadsheetTimeFormatPattern.class
        );
    }

    @Test
    public void testTimeParse() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TIME_PARSE_PATTERNS,
                SpreadsheetTimeParsePatterns.class
        );
    }

    private void typeNameAndCheck(final SpreadsheetPatternKind kind,
                                  final Class<? extends SpreadsheetPattern> expected) {
        this.checkEquals(
                JsonNodeContext.computeTypeName(expected),
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
