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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.lang.reflect.Method;
import java.util.ArrayList;

public final class SpreadsheetValuesTest implements PublicStaticHelperTesting<SpreadsheetValues> {

    @Test
    public void testAll() {
        this.checkEquals(
                Lists.of(
                        SpreadsheetValues.BOOLEAN,
                        SpreadsheetValues.DATE,
                        SpreadsheetValues.DATE_TIME,
                        SpreadsheetValues.NUMBER,
                        SpreadsheetValues.TEXT,
                        SpreadsheetValues.TIME
                ),
                new ArrayList<>(
                        SpreadsheetValues.ALL
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetValues> type() {
        return SpreadsheetValues.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return true;
    }
}
