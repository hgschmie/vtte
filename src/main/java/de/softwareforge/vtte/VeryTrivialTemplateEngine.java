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

import java.util.Map;
import java.util.Stack;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;

/**
 * A very trivial template engine. Supports property replacement with <tt>{&lt;name&gt;}</tt> and
 * optional elements with <tt>[... text block {&lt;name&gt;}... ]</tt>. An optional block is only included if at least one
 * template replacement exists in the block and evaluates to a non-empty value.
 */
public class VeryTrivialTemplateEngine
{
    private final String rootTemplate;

x    public VeryTrivialTemplateEngine(final String rootTemplate)
    {
        if (rootTemplate == null) {
            throw new IllegalArgumentException("rootTemplate is null");
        }
        this.rootTemplate = rootTemplate;
    }

    public String render(final Map<String, ?> properties)
    {
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }

        final Renderer r = new Renderer(0, rootTemplate, unmodifiableMap(properties));
        final StringBuilder sb = new StringBuilder();
        r.render(sb);
        final String result = sb.toString();
        return result;
    }

    private final class Renderer
    {
        private int templatePos = 0;
        private final String template;
        private final Map<String, ?> properties;
        private final int rootOffset;

        private Renderer(final int rootOffset, final String template, final Map<String, ?> properties)
        {
            this.rootOffset = rootOffset;
            this.template = template;
            this.properties = properties;
        }

        private boolean render(final StringBuilder target)
        {
            boolean propSubstition = false;

            while (templatePos < template.length()) {
                final char c = template.charAt(templatePos++);

                int endPos = -1;
                switch (c) {
                // Escape
                    case '\\':
                        // ignore escape as last character
                        if (templatePos < template.length()) {
                            target.append(processEscape(templatePos++));
                        }
                        break;

                    case '[':
                        endPos = findClosingPosition(templatePos, ']');
                        final Renderer r = new Renderer(templatePos, template.substring(templatePos, endPos), properties);
                        final StringBuilder sb = new StringBuilder();
                        if (r.render(sb)) {
                            propSubstition = true;
                            target.append(sb);
                        }
                        templatePos = endPos + 1;
                        break;

                    case '{':
                        endPos = findClosingPosition(templatePos, '}');
                        propSubstition |= substituteProperty(target, template.substring(templatePos, endPos));
                        templatePos = endPos + 1;
                        break;

                    default:
                        target.append(c);
                        break;
                }
            }
            return propSubstition;
        }

        private char processEscape(final int pos)
        {
            char e = template.charAt(pos);
            switch (e) {
                case 'n':
                    return '\n';
                case 't':
                    return '\t';
                default:
                    return e;
            }
        }

        private int findClosingPosition(final int start, final char match)
        {
            final Stack<StackEntry> braceStack = new Stack<>();
            int pos = start;

            braceStack.push(new StackEntry(match, start));

            while (pos < template.length()) {
                final char c = template.charAt(pos++);
                switch (c) {
                    case '\\':
                        pos++;
                        break;
                    case '[':
                        braceStack.push(new StackEntry(']', pos));
                        break;
                    case '{':
                        braceStack.push(new StackEntry('}', pos));
                        break;
                    case '}':
                    case ']':
                        final StackEntry entry = braceStack.pop();

                        if (entry.match != c) {
                            throw new IllegalStateException(format("encountered unexpected '%c' (%s)", c, shortenString(rootTemplate, rootOffset, entry.pos - 1, pos)));
                        }

                        if (braceStack.isEmpty()) {
                            return pos - 1;
                        }

                        break;

                    default:
                        break;
                }
            }

            throw new IllegalStateException(format("required match '%c' not found (%s)", match, shortenString(rootTemplate, rootOffset, start - 1, template.length())));
        }

        private boolean substituteProperty(final StringBuilder target, String key)
        {
            if (key.length() == 0) {
                return false;
            }

            String defaultValue = "";

            final int colonIndex = key.indexOf(':');
            if (colonIndex != -1) {
                defaultValue = key.substring(colonIndex + 1);
                key = key.substring(0, colonIndex);
            }

            if (properties.containsKey(key)) {
                final Object value = properties.get(key);
                if (value != null) {
                    if (value instanceof Boolean) {
                        return ((Boolean)value).booleanValue();
                    }
                    else {
                        target.append(value.toString());
                    }
                    return true;
                }
                return false;
            }

            if (defaultValue.length() > 0) {
                target.append(defaultValue);
                return true;
            }

            return false;
        }
    }

    static String shortenString(final String str, final int offset, final int start, final int end)
    {
        // 12 and 24 are magic values for the length of each field around start and end.
        // 3 is the length of the "..."
        //
        String result = "";

        final int startPos = start + offset;
        final int endPos = end + offset;

        int s0 = Math.max(0, startPos - 12);

        if (s0 < 3) {
            s0 = 0;
        }
        else {
            result = "...";
        }

        final int e0 = Math.min(startPos + 12, str.length());
        int e1 = Math.min(endPos + 12, str.length());

        if (str.length() - 3 < e1) {
            e1 = str.length();
        }

        final int s1 = Math.max(0, endPos - 12);

        if (s1 < e0 + 3 || (e1 - s0) < 24) {
            result = result + str.substring(s0, startPos) + "*" + str.substring(startPos, endPos) + "*" + str.substring(endPos, e1);
        }
        else {
            result = result + str.substring(s0, startPos) + "*" + str.substring(startPos, e0) + "..." + str.substring(s1, endPos) + "*" + str.substring(endPos, e1);
        }

        if (e1 != str.length()) {
            result = result + "...";
        }

        return result;
    }

    private static class StackEntry
    {
        private final char match;
        private final int pos;

        private StackEntry(char match, int pos)
        {
            this.match = match;
            this.pos = pos;
        }
    }
}
