package eu.riscoss.api;

import java.util.List;

import org.xwiki.component.annotation.Role;

import eu.riscoss.api.model.Measurement;

/**
 * RISCOSSPlatform.
 *
 * @version $Id$
 */
@Role
public interface RISCOSSPlatform
{
    ToolFactory getToolFactory(String toolId);

    List<ToolFactory> getToolFactories();

    void storeMeasurement(Measurement measurement);
}
