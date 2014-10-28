package eu.riscoss.services;

import java.util.Map;

/**
 * This class is used to wrap the result of the functions that return risk data from various sources. It is used to
 * associate the risk data to the risk models ids, but also to report errors so that they can be exposed in the UI.
 */
public class RiskDataResult
{
    private Map<String, Object> riskData;

    private Map<String, String> errors;

    public RiskDataResult(Map<String, Object> riskData, Map<String, String> errors)
    {
        this.riskData = riskData;
        this.errors = errors;
    }

    public Map<String, Object> getRiskData()
    {
        return riskData;
    }

    public Map<String, String> getErrors()
    {
        return errors;
    }
}
