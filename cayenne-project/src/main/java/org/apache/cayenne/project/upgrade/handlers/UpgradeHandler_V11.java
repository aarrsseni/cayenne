package org.apache.cayenne.project.upgrade.handlers;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.project.ProjectUtils;
import org.apache.cayenne.project.upgrade.AttributePair;
import org.apache.cayenne.project.upgrade.RelationshipDescriptor;
import org.apache.cayenne.project.upgrade.UpgradeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpgradeHandler_V11 implements UpgradeHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpgradeHandler_V11.class);

    private List<RelationshipDescriptor> relationshipDescriptors;

    public UpgradeHandler_V11() {
        this.relationshipDescriptors = new ArrayList<>();
    }

    @Override
    public String getVersion() {
        return "11";
    }

    @Override
    public void processProjectDom(UpgradeUnit upgradeUnit) {
        Element domain = upgradeUnit.getDocument().getDocumentElement();
        // introduce xml namespace and schema for domain
        domain.setAttribute("xmlns","http://cayenne.apache.org/schema/11/domain");
        domain.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        domain.setAttribute("xsi:schemaLocation", "http://cayenne.apache.org/schema/11/domain " +
                "https://cayenne.apache.org/schema/11/domain.xsd");
        // update version
        domain.setAttribute("project-version", getVersion());
    }

    @Override
    public void processDataMapDom(UpgradeUnit upgradeUnit) {
        Element dataMap = upgradeUnit.getDocument().getDocumentElement();
        // update schema
        dataMap.setAttribute("xmlns","http://cayenne.apache.org/schema/11/modelMap");
        dataMap.setAttribute("xsi:schemaLocation", "http://cayenne.apache.org/schema/11/modelMap " +
                "https://cayenne.apache.org/schema/11/modelMap.xsd");
        // update version
        dataMap.setAttribute("project-version", getVersion());

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList relationshipNodes;
        try {
            relationshipNodes = (NodeList) xpath.evaluate("/data-map/db-relationship", upgradeUnit.getDocument(), XPathConstants.NODESET);
        } catch (Exception ex) {
            logger.warn("Can't process dataMap DOM: ", ex);
            return;
        }

        for (int j = 0; j < relationshipNodes.getLength(); j++) {
            Element reElement = (Element) relationshipNodes.item(j);
            String name = reElement.getAttribute("name");
            String srcEntity = reElement.getAttribute("source");
            String targetEntity = reElement.getAttribute("target");
            String toMany = reElement.getAttribute("toMany");
            String toDependentPk = reElement.getAttribute("toDependentPK");

            NodeList dbAttrPairNodes = reElement.getElementsByTagName("db-attribute-pair");
            int length = dbAttrPairNodes.getLength();
            List<AttributePair> attributePairs = new ArrayList<>(length);
            for(int i = 0; i < length; i++) {
                Node dbElement = dbAttrPairNodes.item(i);
                NamedNodeMap namedNodeMap = dbElement.getAttributes();
                String source = namedNodeMap.getNamedItem("source").getNodeValue();
                String target = namedNodeMap.getNamedItem("target").getNodeValue();
                attributePairs.add(new AttributePair(source, target));
            }
            NodeList commentNodes = reElement.getElementsByTagName("info:property");
            if(commentNodes.getLength() > 1) {
                throw new CayenneRuntimeException("Invalid number of comments tag in one relationship.");
            }
            Element commentNode = (Element) commentNodes.item(0);
            String comment = null;
            if(commentNode != null) {
                comment = commentNode.getAttribute("value");
            }
            RelationshipDescriptor currRelationshipDescriptor =
                    new RelationshipDescriptor(name,
                            srcEntity,
                            targetEntity,
                            toMany,
                            toDependentPk,
                            comment,
                            attributePairs);
            if(needToUpgrade(currRelationshipDescriptor)) {
                relationshipDescriptors.add(currRelationshipDescriptor);
            }

            dataMap.removeChild(reElement);
        }

        for(RelationshipDescriptor relationshipDescriptor : relationshipDescriptors) {
            upgradeToJoin(upgradeUnit, relationshipDescriptor);
        }

        relationshipDescriptors.clear();
    }

    private boolean needToUpgrade(RelationshipDescriptor currDescriptor) {
        for(RelationshipDescriptor relationshipDescriptor : relationshipDescriptors) {
            if(relationshipDescriptor.isReverse(currDescriptor)) {
                return false;
            }
        }
        return true;
    }

    private void upgradeToJoin(UpgradeUnit upgradeUnit, RelationshipDescriptor relationshipDescriptor) {
        boolean needToNormalize = ProjectUtils.needToNormalize(
                relationshipDescriptor.getSrcEntity(),
                relationshipDescriptor.getTargetEntity(),
                relationshipDescriptor.getName(),
                relationshipDescriptor.getReverseName());
        Element root = upgradeUnit.getDocument().createElement("db-join");
        ToManySemantics toManySemantics = compileToMany(relationshipDescriptor);
        root.setAttribute("toMany", needToNormalize ? toManySemantics.getReverse().name() :
                toManySemantics.name());
        ToDependentPkSemantics toDependentPkSemantics = compileToDependentPk(relationshipDescriptor);
        root.setAttribute("toDependentPK", needToNormalize ? toDependentPkSemantics.getReverse().name() :
                toDependentPkSemantics.name());

        String leftEntity, rightEntity, relName, reverseRelName, comment, targetComment;
        if(needToNormalize) {
            leftEntity = relationshipDescriptor.getTargetEntity();
            rightEntity = relationshipDescriptor.getSrcEntity();
            relName = relationshipDescriptor.getReverseName();
            reverseRelName = relationshipDescriptor.getName();
            comment = relationshipDescriptor.getTargetComment();
            targetComment = relationshipDescriptor.getComment();
        } else {
            leftEntity = relationshipDescriptor.getSrcEntity();
            rightEntity = relationshipDescriptor.getTargetEntity();
            relName = relationshipDescriptor.getName();
            reverseRelName = relationshipDescriptor.getReverseName();
            comment = relationshipDescriptor.getComment();
            targetComment = relationshipDescriptor.getTargetComment();
        }

        Element left = upgradeUnit.getDocument().createElement("left");
        left.setAttribute("entity", leftEntity);
        if(relName != null) {
            left.setAttribute("name", relName);
        }
        root.appendChild(left);

        Element right = upgradeUnit.getDocument().createElement("right");
        right.setAttribute("entity", rightEntity);

        if(reverseRelName != null) {
            right.setAttribute("name", reverseRelName);
        }
        root.appendChild(right);

        Element dbJoinCondition = upgradeUnit.getDocument().createElement("db-join-condition");
        for(AttributePair attributePair : relationshipDescriptor.getAttributePairs()) {
            Element columnPairElement = upgradeUnit.getDocument().createElement("column-pair");
            columnPairElement.setAttribute("left", needToNormalize ?
                    attributePair.getRight() : attributePair.getLeft());
            columnPairElement.setAttribute("right", needToNormalize ?
                    attributePair.getLeft() : attributePair.getRight());
            dbJoinCondition.appendChild(columnPairElement);
        }
        root.appendChild(dbJoinCondition);

        String resultComment = buildComment(comment, targetComment);
        if(resultComment != null) {
            Element commentElement = upgradeUnit.getDocument().createElement("info:property");
            commentElement.setAttribute("xmlns:info", "http://cayenne.apache.org/schema/11/info");
            commentElement.setAttribute("name", "comment");
            commentElement.setAttribute("value", resultComment);
            root.appendChild(commentElement);
        }

        upgradeUnit.getDocument().getDocumentElement().appendChild(root);
    }

    private String buildComment(String comment, String targetComment) {
        String result = "";
        if(comment != null) {
            result = "LEFT:" + comment;
        }
        if(targetComment != null) {
            if(!result.isEmpty()) {
                result += ", ";
            }
            result += "RIGHT:" + targetComment;
        }

        return result.isEmpty() ? null : result;
    }

    private ToManySemantics compileToMany(RelationshipDescriptor relationshipDescriptor) {
        String toMany = relationshipDescriptor.getToMany();
        String targetToMany = relationshipDescriptor.getTargetToMany();
        boolean toManyFlag = toMany != null && toMany.equalsIgnoreCase("TRUE");
        boolean targetToManyFlag = targetToMany != null && targetToMany.equalsIgnoreCase("TRUE");
        return ToManySemantics.getSemantics(toManyFlag, targetToManyFlag);
    }

    private ToDependentPkSemantics compileToDependentPk(RelationshipDescriptor relationshipDescriptor) {
        String toDependentPk = relationshipDescriptor.getToDependentPk();
        String targetToDependentPk = relationshipDescriptor.getTargetToDependentPk();
        boolean toDepPkFlag = toDependentPk != null &&
                toDependentPk.equalsIgnoreCase("TRUE");
        boolean targetToDepPkFlag = targetToDependentPk != null &&
                targetToDependentPk.equalsIgnoreCase("TRUE");
        return ToDependentPkSemantics.getSemantics(toDepPkFlag, targetToDepPkFlag);
    }
}
