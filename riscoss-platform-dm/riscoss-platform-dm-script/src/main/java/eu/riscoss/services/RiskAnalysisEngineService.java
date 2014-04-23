package eu.riscoss.services;

import eu.riscoss.reasoner.ReasoningLibrary;
import eu.riscoss.reasoner.RiskAnalysisEngine;

public class RiskAnalysisEngineService
{
    public RiskAnalysisEngine createInstance() {
        return (RiskAnalysisEngine) ReasoningLibrary.get().createRiskStructure();
    }
}
