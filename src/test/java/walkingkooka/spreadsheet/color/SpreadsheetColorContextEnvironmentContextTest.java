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

package walkingkooka.spreadsheet.color;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.color.WebColorName;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentValueName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColorContextEnvironmentContextTest implements SpreadsheetColorContextTesting2<SpreadsheetColorContextEnvironmentContext>,
    EnvironmentContextTesting {

    private final static Color COLOR12 = WebColorName.BEIGE.color();
    private final static Color COLOR13 = Color.indexed(14);
    private final static Color COLOR14 = Color.indexed(13);
    private final static Color COLOR_DARK_BLUE = WebColorName.DARKBLUE.color();

    private final static String ENVIRONMENT_COLOR12 = "color12";
    private final static String ENVIRONMENT_COLOR13 = "color13";
    private final static String ENVIRONMENT_COLOR14 = "color14";
    private final static String ENVIRONMENT_COLOR_DARK_BLUE = "darkBlue";

    static {
        final EnvironmentContext environmentContext = EnvironmentContextTesting.ENVIRONMENT_CONTEXT.cloneEnvironment();

        environmentContext.setEnvironmentValue(
            EnvironmentValueName.with(
                ENVIRONMENT_COLOR12,
                Color.class
            ),
            COLOR12
        );


        environmentContext.setEnvironmentValue(
            EnvironmentValueName.with(
                ENVIRONMENT_COLOR13,
                Color.class
            ),
            COLOR13
        );

        environmentContext.setEnvironmentValue(
            EnvironmentValueName.with(
                ENVIRONMENT_COLOR14,
                Color.class
            ),
            COLOR14
        );

        environmentContext.setEnvironmentValue(
            EnvironmentValueName.with(
                "color" + ENVIRONMENT_COLOR_DARK_BLUE,
                Color.class
            ),
            COLOR_DARK_BLUE
        );

        ENVIRONMENT_CONTEXT = environmentContext;
    }

    private final static EnvironmentContext ENVIRONMENT_CONTEXT;

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColorContextEnvironmentContext.with(null)
        );
    }

    // lookupColor......................................................................................................

    @Test
    public void testLookupColorWithIndexedColorUnknown() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(44) // 404
        );
    }

    @Test
    public void testLookupColorWithIndexedColor() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(12),
            COLOR12
        );
    }

    @Test
    public void testLookupColorWithNamedColor() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.named(ENVIRONMENT_COLOR_DARK_BLUE),
            COLOR_DARK_BLUE
        );
    }

    @Test
    public void testLookupColorWithCycleFails() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalStateException.class,
                () -> this.createContext()
                    .lookupColor(COLOR13)
            ),
            "Cycle detected looking up color 14"
        );
    }


    @Override
    public SpreadsheetColorContextEnvironmentContext createContext() {
        return SpreadsheetColorContextEnvironmentContext.with(ENVIRONMENT_CONTEXT);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{charset=UTF-8, color12=beige, color13=14, color14=13, colordarkBlue=darkblue, currency=AUD, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, loggingLevel=NONE, timeOffset=Z, user=user123@example.com}"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetColorContextEnvironmentContext> type() {
        return SpreadsheetColorContextEnvironmentContext.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
