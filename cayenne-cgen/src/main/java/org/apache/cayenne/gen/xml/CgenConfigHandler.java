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
package org.apache.cayenne.gen.xml;

import org.apache.cayenne.configuration.xml.DataChannelMetaData;
import org.apache.cayenne.configuration.xml.NamespaceAwareNestedTagHandler;
import org.apache.cayenne.gen.ClassGenerationAction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;

/**
 * @since 4.1
 */
public class CgenConfigHandler extends NamespaceAwareNestedTagHandler{

    public static final String CONFIG_TAG = "cgen";

    private static final String OUTPUT_DIRECTORY_TAG = "outputDirectory";
    private static final String GENERATION_MODE_TAG = "generationMode";
    private static final String SUBCLASS_TEMPLATE_TAG = "subclassTemplate";
    private static final String SUPERCLASS_TEMPLATE_TAG = "superclassTemplate";
    private static final String OUTPUT_PATTERN_TAG = "outputPattern";
    private static final String MAKE_PAIRS_TAG = "makePairs";
    private static final String USE_PKG_PATH_TAG = "usePkgPath";
    private static final String OVERWRITE_SUBCLASSES_TAG = "overwriteSubclasses";
    private static final String CREATE_PROPERTY_NAMES_TAG = "createPropertyNames";
    private static final String SUPER_PKG_TAG = "superPkg";
    private static final String OBJENTITY_TAG = "objEntity";
    private static final String EMBEDDABLE_TAG = "embeddable";
    private static final String ENCODING_TAG = "encoding";
    private static final String EMBEDDABLE_TEMPLATE_TAG = "embeddableTemplate";
    private static final String EMBEDDABLE_SUPERCLASS_TEMPLATE_TAG = "embeddableSuperclassTemplate";
    private static final String DATAMAP_TEMPLATE_TAG = "dataMapTemplate";
    private static final String DATAMAP_SUPERCLASS_TEMPLATE_TAG = "dataMapSuperclassTemplate";

    public static final String TRUE = "true";

    private DataChannelMetaData metaData;
    private ClassGenerationAction configuration;

    CgenConfigHandler(NamespaceAwareNestedTagHandler parentHandler, DataChannelMetaData metaData) {
        super(parentHandler);
        this.metaData = metaData;
        this.targetNamespace = CgenExtension.NAMESPACE;
    }

    @Override
    protected boolean processElement(String namespaceURI, String localName, Attributes attributes) throws SAXException {
        switch (localName) {
            case CONFIG_TAG:
                createConfig();
                return true;
        }
        return false;
    }

    @Override
    protected ContentHandler createChildTagHandler(String namespaceURI, String localName,
                                                   String qName, Attributes attributes) {

        if (namespaceURI.equals(targetNamespace)) {
            switch (localName) {
                case OBJENTITY_TAG:
                    return new ObjEntityHandler(this, configuration);
                case EMBEDDABLE_TAG:
                    return new EmbeddableHandler(this, configuration);
            }
        }

        return super.createChildTagHandler(namespaceURI, localName, qName, attributes);
    }

    @Override
    protected void processCharData(String localName, String data) {
        switch (localName) {
            case OUTPUT_DIRECTORY_TAG:
                createOutputDir(data);
                break;
            case GENERATION_MODE_TAG:
                createGenerationMode(data);
                break;
            case SUBCLASS_TEMPLATE_TAG:
                createSubclassTemplate(data);
                break;
            case SUPERCLASS_TEMPLATE_TAG:
                createSuperclassTemplate(data);
                break;
            case OUTPUT_PATTERN_TAG:
                createOutputPattern(data);
                break;
            case MAKE_PAIRS_TAG:
                createMakePairs(data);
                break;
            case USE_PKG_PATH_TAG:
                createUsePkgPath(data);
                break;
            case OVERWRITE_SUBCLASSES_TAG:
                createOverwriteSubclasses(data);
                break;
            case CREATE_PROPERTY_NAMES_TAG:
                createPropertyNamesTag(data);
                break;
            case SUPER_PKG_TAG:
                createSuperPkg(data);
                break;
            case ENCODING_TAG:
                createEncoding(data);
                break;
            case EMBEDDABLE_TEMPLATE_TAG:
                createEmbeddableTemplate(data);
                break;
            case EMBEDDABLE_SUPERCLASS_TEMPLATE_TAG:
                createEmbeddableSuperclassTemplate(data);
                break;
            case DATAMAP_TEMPLATE_TAG:
                createDataMapTemplate(data);
                break;
            case DATAMAP_SUPERCLASS_TEMPLATE_TAG:
                createDataMapSuperclassTemplate(data);
                break;
        }
    }

    private void createOutputDir(String path) {
        if(path.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setDestDir(new File(path));
        }
    }

    private void createGenerationMode(String mode) {
        if(mode.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setArtifactsGenerationMode(mode);
        }
    }

    private void createSubclassTemplate(String template) {
        if(template.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setTemplate(template);
        }
    }

    private void createSuperclassTemplate(String template) {
        if(template.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setSuperTemplate(template);
        }
    }

    private void createEmbeddableTemplate(String template) {
        if(template.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setEmbeddableTemplate(template);
        }
    }

    private void createEmbeddableSuperclassTemplate(String template) {
        if(template.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setEmbeddableSuperTemplate(template);
        }
    }

    private void createOutputPattern(String pattern) {
        if(pattern.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setOutputPattern(pattern);
        }
    }

    private void createMakePairs(String makePairs) {
        if (makePairs.trim().length() == 0) {
            return;
        }

        if (configuration != null) {
            if (makePairs.equals(TRUE)) {
                configuration.setMakePairs(true);
            } else {
                configuration.setMakePairs(false);
            }
        }
    }

    private void createUsePkgPath(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            if(data.equals(TRUE)) {
                configuration.setUsePkgPath(true);
            } else {
                configuration.setUsePkgPath(false);
            }
        }
    }

    private void createOverwriteSubclasses(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            if(data.equals(TRUE)) {
                configuration.setOverwrite(true);
            } else {
                configuration.setOverwrite(false);
            }
        }
    }

    private void createPropertyNamesTag(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            if(data.equals(TRUE)) {
                configuration.setCreatePropertyNames(true);
            } else {
                configuration.setCreatePropertyNames(false);
            }
        }
    }

    private void createSuperPkg(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setSuperPkg(data);
        }
    }

    private void createEncoding(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setEncoding(data);
        }
    }

    private void createDataMapTemplate(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setQueryTemplate(data);
        }
    }

    private void createDataMapSuperclassTemplate(String data) {
        if(data.trim().length() == 0) {
            return;
        }

        if(configuration != null) {
            configuration.setQuerySuperTemplate(data);
        }
    }

    private void createConfig() {
        configuration = new ClassGenerationAction();
        loaderContext.addDataMapListener(dataMap -> {
            configuration.setDataMap(dataMap);
            CgenConfigHandler.this.metaData.add(dataMap, configuration);
        });
    }
}