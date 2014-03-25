package eu.riscoss.api.model;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A class for representing a goal model.
 * 
 * @version $Id$
 */
public class GoalModel
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoalModel.class);

    public enum ElementType
    {
        ACTOR,
        ACTOR_LINK,
        INTENTIONAL_ELEMENT,
        IE_LINK,
        DEPENDENCY,
        NO_ELEMENT
    }

    public enum ElementOperation
    {
        REFINEMENT,
        CHOOSING,
        PATTERN,
        NO_OPERATION
    }

    /**
     * The goal model id.
     */
    private String id;

    /**
     * The XML data that represents the goal model. Currently we store the model using an XML representation that is
     * passed as-is to the tools that needs to process/interact with the goal model. In future version the content of
     * this XML file will be stored explicitly in the RISCOSS platform so that its element can be queried/manipulated
     * directly.
     */
    // private String xml; // this attribute is not necessary
    // If the absence of this attribute is a problem because of the setXml getXml,
    // the good solution is having setModel/getModel (changing the interface of the class)
    // the bad solution is changing the name of the attribute model by xml (no changes in the interface but bad name for
    // the attribute)

    /**
     * The model in a org.w3c.doc.Document in order to navegate it
     */
    private Document model;

    /**
     * Default constructor.
     */
    public GoalModel()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the model as a XML string
     * 
     * @return the string containing the current model in XML
     */
    public String getXml()
    {
        DOMSource domSource = new DOMSource(model);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer tr = tf.newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");
            tr.transform(domSource, result);
        } catch (TransformerException e) {
            LOGGER.error(e.toString());
        }
        String xml = writer.toString();
        return xml;
    }

    /**
     * Set the model as a XML String
     * 
     * @param xml the string containing the istar model using istarML format
     */
    public void setXml(String xml)
    { // Transforms the XML string containing the istar model into a org.w3c.dom.Document to work with
      // this.xml = xml;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            model = dBuilder.parse(is);
            model.getDocumentElement().normalize();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    @Override
    public String toString()
    {
        return String.format("[GoalModel - id: %s]", id);
    }

    /******************************************************************************
     * Functions 'set' for changing i* elements attributes
     ******************************************************************************/
    /**
     * Changes the attribute Name for an i* element
     * 
     * @param node the org.w3c.dom.Node containing the i* element to be modified
     * @param name the new name for the i* element
     */
    public void setName(Node node, String name)
    {
        Node attr = node.getAttributes().getNamedItem("name");
        if (attr == null) {
            ((Element) node).setAttribute("name", name);
        } else
            attr.setNodeValue(name);
    }

    /**
     * Mark the i* model to processed
     * 
     * @param model the model as org.w3c.dom.Document
     * @param node the node to be marked as processed as org.w3c.Node
     */
    public void setProcessed(Node node)
    {
        Attr attr = (Attr) node.getAttributes().getNamedItem("processed");
        if (attr == null) {
            attr = model.createAttribute("processed");
            ((Element) node).setAttributeNode(attr);
        }
        attr.setNodeValue("yes");
    }

    /**
     * Set a new value for the iref attibute, it is the attribute that indicates that this i* element is a reference to
     * another
     * 
     * @param node the i* element to be modified
     * @param iref the new value indicating the new referenciated id
     */
    public void setIRef(Node node, String iref)
    {
        Node attr = node.getAttributes().getNamedItem("iref");
        if (attr == null) {
            ((Element) node).setAttribute("iref", iref);
        } else
            attr.setNodeValue(iref);
    }

    /******************************************************************************
     * Functions managing the model
     ******************************************************************************/
    public GoalModel clone(String cloneID)
    {
        GoalModel cloned = new GoalModel();
        cloned.setXml(this.getXml());
        cloned.setId(cloneID);
        return cloned;
    }

    public void addElementFromOtherModel(Node element)
    {
        Node diagram = model.getElementsByTagName("diagram").item(0);
        diagram.appendChild(model.importNode(element, true));
    }

    public void replaceChildFromOtherModel(Node node, Node oldChild, Node newChild)
    {
        node.replaceChild(model.importNode(newChild, true), oldChild);
    }

    public void addChildFromOtherModel(Node node, Node child)
    {
        node.appendChild(model.importNode(child, true));
    }

    public void addChildsFromOtherModel(Node node, Node childsParent)
    {
        NodeList children = model.importNode(childsParent, true).getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
            node.appendChild(children.item(i));

    }

    /******************************************************************************
     * Functions 'get' for obtained the value for i* element attributes
     ******************************************************************************/

    /**
     * Gets the i* element type
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the node type
     */
    public ElementType getType(Node node)
    {
        String type;
        try {
            type = node.getNodeName();
        } catch (Exception e) {
            type = "";
        }

        if (type.equalsIgnoreCase("actor"))
            return ElementType.ACTOR;
        if (type.equalsIgnoreCase("actorLink"))
            return ElementType.ACTOR_LINK;
        else if (type.equalsIgnoreCase("ielement"))
            return ElementType.INTENTIONAL_ELEMENT;
        else if (type.equalsIgnoreCase("ielementLink"))
            return ElementType.IE_LINK;
        else if (type.equalsIgnoreCase("dependency"))
            return ElementType.DEPENDENCY;
        else
            return ElementType.NO_ELEMENT;
    }

    /**
     * Gets the identifier associated to an i* element
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the identifier of the i*operation to be applied over this i* element. 'NO_ID' in the case that the
     *         element does not have an identifier.
     */
    public String getIdentifier(Node node)
    {
        try {
            Node attr = node.getAttributes().getNamedItem("id");
            return attr.getNodeValue();
        } catch (Exception e) {
            return "NOID";
        }
    }

    /**
     * Gets the name associated to an i* element
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the name of the i*operation to be applied over this i* element. emtpy string in the case that the element
     *         does not have a name.
     */
    public String getName(Node node)
    {
        try {
            Node attr = node.getAttributes().getNamedItem("name");
            return attr.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the operation associated to an i* element
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the name of the operation to be applied over this i* element
     */
    public ElementOperation getOperation(Node node)
    {
        String op;
        try {
            Node attr = node.getAttributes().getNamedItem("op");
            op = attr.getNodeValue();
        } catch (Exception e) {
            op = "";
        }

        if (op.equalsIgnoreCase("refine"))
            return ElementOperation.REFINEMENT;
        else if (op.equalsIgnoreCase("choose"))
            return ElementOperation.CHOOSING;
        else if (op.equalsIgnoreCase("pattern"))
            return ElementOperation.PATTERN;
        else
            return ElementOperation.NO_OPERATION;
    }

    /**
     * Gets the pattern associated to an i* element (attribute 'pattern')
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the pattern identifier to be applied over this i* element
     */
    public String getPattern(Node node)
    {
        try {
            Node attr = node.getAttributes().getNamedItem("pattern");
            return attr.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns the question id associated to an i* element
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the question identified for the questions associated to this i* element
     */
    public String getQuestion(Node node)
    {
        try {
            Node attr = node.getAttributes().getNamedItem("question");
            return attr.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns the iref attribute associated to an i* element
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return the value for the attribute iref associated to this i* element
     */
    public String getIRef(Node node)
    {
        try {
            Node attr = node.getAttributes().getNamedItem("iref");
            return attr.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns true when the i* element is marked as processed
     * 
     * @param node the i* element as org.w3c.dom.Node
     * @return true if the node is already processed false otherwise false otherwise
     */
    public boolean isProcessed(Node node)
    {
        try {
            Node attr = node.getAttributes().getNamedItem("processed");
            return attr.getNodeValue().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return true when the id is valid
     * 
     * @param id the requested identifier
     * @return false if is a non-valid identifier
     */
    public boolean isValidID(String id)
    {
        return (!id.equalsIgnoreCase("NOID"));
    }

    /******************************************************************************
     * Functions 'get' to obtain i* elements from an specific i* element
     ******************************************************************************/
    /**
     * Gets the node associated to element parent
     * 
     * @param element the i* element as org.w3c.dom.Node
     * @return the i* element parent as org.w3c.dom.Node
     */
    public Node getParent(Node element)
    {
        if (element != null) {
            element = element.getParentNode();
            // If we have arrived to the diagram level, no more parents have to be processed.
            if (element.getNodeName().equalsIgnoreCase("diagram"))
                element = null;
        }

        return element;
    }

    /**
     * Gets the node associated to the actor where this element is
     * 
     * @param ielement the intentional element as org.w3c.dom.Node
     * @return the actor the intentional elements belongs to as org.w3c.dom.Node
     */
    public Node getActor(Node ielement)
    {
        while (ielement != null && getType(ielement) != ElementType.ACTOR)
            ielement = ielement.getParentNode();

        return ielement;
    }

    /******************************************************************************
     * Functions 'get' to obtain i* elements from a model element
     ******************************************************************************/
    /**
     * Get all the nodes (actors and dependencies) from the model
     * 
     * @return the list of nodes associated to all the elements in the model (actors and dependencies)
     */
    public NodeList getModelElements()
    {
        return model.getElementsByTagName("diagram").item(0).getChildNodes();
    }

    /**
     * Gets the node associated to the whole model (tag <diagram> for the starml format)
     * 
     * @return the node as org.w3c.doc.Node associated to the whole model
     */
    public Node getModelNode()
    {
        return model.getElementsByTagName("diagram").item(0);
    }
    /**
     * Gets the first non-processed i* element from an especific model element
     * 
     * @param element the element as org.w3c.doc.Node
     * @return the first non-processed i* element (actor, IE, IELink, dependency), as org.w3c.doc.Node, with an
     *         associated question. If there is no non-processed i* elements, returns null.
     */
    public Node getFirstPendingElement(Node element)
    {
        return getNode(element, ".//*[(@question and (@processed='no' or not (@processed)))][1]");
    }

    /******************************************************************************
     * Functions 'get' to obtain i* elements from the entire model
     ******************************************************************************/

    public Node getElementById(String id)
    {
        return getNode(model, "//*[@id='" + id + "']");
    }

    /**
     * Returns the i* element associated to a specific question
     * 
     * @param question the question identifier to select the i* element
     * @return the list of i* element as a org.w3c.dom.NodeList associated to the question
     */
    public NodeList getElementsByQuestion(String question)
    {
        // return getNodes(model, "//*[@question='" + question + "' and (@processed='no' or not (@processed))]");

        // The processed nodes are also returned, e.g. is someone edits the Name of a company, the new name should
        // replace
        // the older already changed!
        return getNodes(model, "//*[@question='" + question + "']");
    }

    /**
     * Gets all the intentional elements that referenced another IE
     * 
     * @param id the identifier of the referenciated node
     * @return the list of intentional elements that reference the specific id
     */
    public NodeList getIEsByIRef(String id)
    {
        return getNodes(model, "//*/ielement[@iref='" + id + "']");
    }

    /**
     * Gets the first non-processed i* element from the entire model
     * 
     * @return the first non-processed i* element (actor, IE, IELink, dependency), as org.w3c.doc.Node, with an
     *         associated question. If there is no non-processed i* elements, returns null.
     */
    public Node getFirstPendingElement()
    {
        return getNode(model, "//*[(@question and (@processed='no' or not (@processed)))][1]");
    }

    /**
     * Gets the list of ALL the dependencies for a specific actor that are not processed and are NOT linked to an
     * internal IE
     * 
     * @param actor the actor as a org.w3c.doc.Node
     * @return the list of outgoing dependencies (itarml tagged as <ielement>) as org.w3c.doc.Node that correspond to
     *         all non-processed actor's outgoing dependencies with an associated question.
     */
    public NodeList getPendingActorOutgoingDependencies(Node actor)
    {
        String actorID = getIdentifier(actor);
        // only the dependencies linked to the actor, no linked to an internal intentional element
        return getNodes(model, "//ielement/dependency/depender[(@aref='" + actorID
            + "' and not (@iref))]/ancestor::ielement[(@question and (@processed='no' or not (@processed)))]");
    }

    /**
     * Gets the list of ALL the outgoing dependencies for a specific actor that are not processed and are linked to an
     * internal IE
     * 
     * @param actor the actor as a org.w3c.doc.Node
     * @return the list of outgoing dependencies (itarml tagged as <ielement>) as org.w3c.doc.Node that correspond to
     *         all non-processed actor's outgoing dependencies with an associated question.
     */
    public NodeList getPendingActorIEOutgoingDependencies(Node actor)
    {
        String actorID = getIdentifier(actor);
        // only the dependencies linked to the actor, no linked to an internal intentional element
        return getNodes(model, "//ielement/dependency/depender[(@aref='" + actorID
            + "' and (@iref))]/ancestor::ielement[(@question and (@processed='no' or not (@processed)))]");
    }

    /**
     * Gets the list of ALL the outgoing dependencies for a specific actor that are not processed and are linked to an
     * internal IE
     * 
     * @param actor the actor as a org.w3c.doc.Node
     * @return the list of outgoing dependencies (itarml tagged as <ielement>) as org.w3c.doc.Node that correspond to
     *         all non-processed actor's outgoing dependencies with an associated question.
     */
    public NodeList getPendingIEDependencies(Node ielement)
    {
        String ielementID = getIdentifier(ielement);
        String actorID = getIdentifier(getActor(ielement));
        // only the dependencies linked to the pair actor -internal intentional.
        // The actorID is included to be more generic
        // return getNodes(model, "//ielement/dependency/*[(@aref='" + actorID + "' and @iref='" + ielementID +
        // "')]/ancestor::ielement[(@question and (@processed='no' or not (@processed)))]");
        return getNodes(model, "//ielement/dependency/depender[(@aref='" + actorID + "' and @iref='" + ielementID
            + "')]/ancestor::ielement[(@question and (@processed='no' or not (@processed)))]");
    }

    /**
     * Gets the list ALL the incoming dependencies for a specific actor that are not processed and not linked to an
     * internal IE
     * 
     * @param actor the actor as a org.w3c.doc.Node
     * @return the list of incoming dependencies (itarml tagged as <ielement>) as org.w3c.doc.Node that correspond to
     *         all non-processed actor's outgoing dependencies with an associated question.
     */
    public NodeList getPendingIEIncomingDependencies(Node actor)
    {
        String actorID = getIdentifier(actor);
        // only the dependencies linked to the actor, no linked to an internal intentional element
        return getNodes(model, "//ielement/dependency/dependee[(@aref='" + actorID
            + "' and not (@iref))]/ancestor::ielement[(@question and (@processed='no' or not (@processed)))]");
    }

    /******************************************************************************
     * Auxiliar functions for the 'get' functions related to XPath
     ******************************************************************************/
    /**
     * Gets the first node that fits with the XPath expression.
     * 
     * @param o the whole model (org.w3c.doc.Document) or the i* element (org.w3c.doc.Node) to be requested
     * @param expression the text of the XPath expression to be evaluated over the model
     * @return the node as org.w3c.doc.Node that fits with the expression
     */
    private Node getNode(Object o, String expression)
    {
        Node node = null;
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            node = (Node) xPath.compile(expression).evaluate(o, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            LOGGER.error(e.toString());
        }
        return node;
    }

    /**
     * Gets the list of nodes that fits with the XPath expression.
     * 
     * @param o the whole model (org.w3c.doc.Document) or the i* element (org.w3c.doc.Node) to be requested
     * @param expression the text of the XPath expression to be evaluated over the model
     * @return the list of nodes that fits with the expression
     */
    private NodeList getNodes(Object o, String expression)
    {
        NodeList nodeList = null;
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(o, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.error(e.toString());
        }
        return nodeList;
    }
}
