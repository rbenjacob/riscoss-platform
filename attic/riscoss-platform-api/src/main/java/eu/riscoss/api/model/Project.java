package eu.riscoss.api.model;

/**
 * Project.
 *
 * @version $Id$
 */
public class Project extends Scope
{
    @Override public String toString()
    {
        return String.format("[Project - id: %s, name: %s]", id, name);
    }
}
