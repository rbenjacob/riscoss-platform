package eu.riscoss.api.model;

import java.util.HashSet;
import java.util.Set;

/**
 * OSSCommunity.
 *
 * @version $Id$
 */
public class OSSCommunity
{
    private String id;

    private Set<OSSComponent> components = new HashSet<OSSComponent>();

    public OSSCommunity()
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

    public Set<OSSComponent> getComponents()
    {
        return components;
    }

    public void setComponents(Set<OSSComponent> components)
    {
        this.components = components;
    }

    @Override public String toString()
    {
        return String.format("[OSSCommunity - id: %s]", id);
    }
}
