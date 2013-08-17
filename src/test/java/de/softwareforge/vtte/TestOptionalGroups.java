/*
 * Copyright (C) 2013 Henning Schmiedehausen
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
package de.softwareforge.vtte;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

public class TestOptionalGroups
{
    @Test
    public void testOptionalGroupWithoutProperty()
    {
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("[this is an optional text]");
        assertEquals("", vtte.render(ImmutableMap.<String, String>of()));
    }

    @Test
    public void testOptionalGroupWithDefaultValue()
    {
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("[Hello {user:unknown user}]");
        assertEquals("Hello unknown user", vtte.render(ImmutableMap.<String, String>of()));
        assertEquals("Hello John", vtte.render(ImmutableMap.<String, String>of("user", "John")));

        // Empty string is a valid result.
        assertEquals("Hello ", vtte.render(ImmutableMap.<String, String>of("user", "")));

        // Null value does not trigger default.
        assertEquals("", vtte.render(singletonMap("user", null)));
    }

    @Test
    public void testConditionalText()
    {
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("[{condition}This is conditional text]");
        assertEquals("", vtte.render(ImmutableMap.<String, String>of()));
        // Empty string triggers rendering.
        assertEquals("This is conditional text", vtte.render(ImmutableMap.<String, String>of("condition", "")));

        // Boolean true triggers rendering
        assertEquals("This is conditional text", vtte.render(ImmutableMap.<String, Object>of("condition", true)));

        // Boolean false skips
        assertEquals("", vtte.render(ImmutableMap.<String, Object>of("condition", false)));

    }
}
