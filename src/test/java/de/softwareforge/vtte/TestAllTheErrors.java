/*
 * Copyright (C) 2013-2014 Henning Schmiedehausen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.softwareforge.vtte;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestAllTheErrors
{

    void assertThrowable(Throwable t, Class<? extends Throwable> clazz, String msg)
    {
        Assert.assertSame("Different Throwable thrown", clazz, t.getClass());
        Assert.assertEquals(msg, t.getMessage());
    }

    @Test
    public void testNullTemplate()
    {
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(null);
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalArgumentException.class, "rootTemplate is null");
        }
    }

    @Test
    public void testNullProperties()
    {
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("");
            String result = vtte.render(null);
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalArgumentException.class, "properties is null");
        }
    }

    @Test
    public void badEscape()
    {
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("\\");
        Assert.assertEquals("", vtte.render(ImmutableMap.<String, String> of()));
    }

    @Test
    public void openSquareBracket()
    {
        String template = "Hello, [{world}";
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
            String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalStateException.class, "required match ']' not found (Hello, *[{world}*)");
        }

        template = "Hello, [world";
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
            String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalStateException.class, "required match ']' not found (Hello, *[world*)");
        }
    }

    @Test
    public void openCurlyBracket()
    {
        String template = "Hello, [{world]";
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
            String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalStateException.class, "encountered unexpected ']' (Hello, [*{world]*)");
        }

        template = "Hello, {world";
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
            String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalStateException.class, "required match '}' not found (Hello, *{world*)");
        }
    }

    @Test
    public void bracketMismatch()
    {
        String template = "Hello, [[[world}]}";
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
            String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalStateException.class, "encountered unexpected '}' (Hello, [[*[world}*]})");
        }

        template = "Hello, {world]}";
        try {
            final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
            String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
            fail();
        }
        catch (Throwable t) {
            assertThrowable(t, IllegalStateException.class, "encountered unexpected ']' (Hello, *{world]*})");
        }

        template = "Hello, world}";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);
        String result = vtte.render(ImmutableMap.<String, String> of("world", "joe"));
        assertEquals(template, result);
    }
}
