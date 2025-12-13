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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.math.NumberVisitorTesting;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetValueVisitorNumberVisitorTest implements NumberVisitorTesting<SpreadsheetValueVisitorNumberVisitor> {

    @Test
    public void testToString() {
        final SpreadsheetValueVisitor visitor = new FakeSpreadsheetValueVisitor() {
        };
        this.toStringAndCheck(new SpreadsheetValueVisitorNumberVisitor(visitor), visitor.toString());
    }

    @Override
    public SpreadsheetValueVisitorNumberVisitor createVisitor() {
        return new SpreadsheetValueVisitorNumberVisitor(null);
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetValueVisitor.class.getSimpleName();
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<SpreadsheetValueVisitorNumberVisitor> type() {
        return SpreadsheetValueVisitorNumberVisitor.class;
    }
}
