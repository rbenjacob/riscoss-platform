package eu.riscoss.api.model;

/**
 * A class for representing a goal model.
 *
 * @version $Id$
 */
public class GoalModel
{
    /**
     * The goal model id.
     */
    String id;

    /**
     * The XML data that represents the goal model.
     *
     * Currently we store the model using an XML representation that is passed as-is to the tools that needs to
     * process/interact with the goal model. In future version the content of this XML file will be stored explicitly in
     * the RISCOSS platform so that its element can be queried/manipulated directly.
     *
     * TODO: Get rid of XML representation and store the goal model explicitly.
     */
    String xml;

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

    public String getXml()
    {
        return xml;
    }

    public void setXml(String xml)
    {
        this.xml = xml;
    }

    @Override public String toString()
    {
        return String.format("[GoalModel - id: %s]", id);
    }
}
