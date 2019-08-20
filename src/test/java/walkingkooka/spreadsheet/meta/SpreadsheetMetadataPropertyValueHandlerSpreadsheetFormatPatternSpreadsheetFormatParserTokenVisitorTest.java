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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.type.JavaVisibility;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitorTest implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), this.name() + "=" + this.pattern());
    }

    @Override
    public SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor(this.name(), this.pattern());
    }

    private SpreadsheetMetadataPropertyName<?> name() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private SpreadsheetFormatPattern pattern() {
        return SpreadsheetFormatPattern.parse("#.00");
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern.class.getSimpleName();
    }
}
