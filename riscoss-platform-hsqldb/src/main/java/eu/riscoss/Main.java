package eu.riscoss;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.OSSComponent;

/**
 * Main.
 *
 * @version $Id$
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        RISCOSSPlatform riscossPlatform = RISCOSSPlatformTesting.getRISCOSSPlatform();
        System.out.format("%s\n", riscossPlatform);

        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("xwiki");
        ossComponent.setName("XWiki");
        riscossPlatform.storeScope(ossComponent);

        Measurement measurement = new Measurement();
        measurement.setType("foo");
        measurement.setValue("1.0");
        measurement.setScope(ossComponent);
        riscossPlatform.storeMeasurement(measurement);
    }
}
