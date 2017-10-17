/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.asciidoc;

import java.util.Collections;

import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.DocumentRuby;
import org.asciidoctor.extension.Postprocessor;

/**
 * @since 4.1
 */
public class CayennePostProcessor extends Postprocessor {

    private static final String FRONT_MATTER = "front-matter";
    private static final String EMPTY_FRONT_MATTER = "---\n---\n\n";
    private static final String POSITION_TOP = "top";
    private static final String POSITION_BODY = "body";
    private static final String POSITION_BOTTOM = "bottom";

    public CayennePostProcessor(DocumentRuby documentRuby) {
        super();
    }

    protected String processHeader(Document document, String output) {
        String headerFile = (String) document.getAttr("cayenne-header", "");
        String headerPosition = (String)document.getAttr("cayenne-header-position", POSITION_TOP);

        if(headerFile.isEmpty()) {
            return output;
        }

        String header = "";
        // inject empty front matter
        if(FRONT_MATTER.equals(headerFile.trim())) {
            header = EMPTY_FRONT_MATTER ;
        } else {
            // treat as a file
            header = document.readAsset(headerFile, Collections.emptyMap());
        }

        switch (headerPosition.trim()) {
            case POSITION_BODY: {
                int bodyStart = output.indexOf("<div id=\"header\">");
                if(bodyStart == -1) {
                    // no header
                    return header + output;
                }
                return output.substring(0, bodyStart) + header + output.substring(bodyStart);
            }

            case POSITION_TOP:
            default:
                return header + output;
        }
    }

    protected String processFooter(Document document, String output) {
        String footerFile = (String) document.getAttr("cayenne-footer", "");
        String footerPosition = (String)document.getAttr("cayenne-footer-position", POSITION_BOTTOM);

        if(footerFile.isEmpty()) {
            return output;
        }

        String footer = document.readAsset(footerFile, Collections.emptyMap());

        switch (footerPosition.trim()) {
            case POSITION_BODY: {
                int bodyStart = output.indexOf("</body>");
                if(bodyStart == -1) {
                    // no footer
                    return output + footer;
                }
                return output.substring(0, bodyStart) + footer + output.substring(bodyStart);
            }

            case POSITION_BOTTOM:
            default:
                return output + footer;
        }
    }

    public String process(Document document, String output) {
        output = processHeader(document, output);
        return processFooter(document, output);
    }
}
