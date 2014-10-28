package eu.riscoss.services;

import eu.riscoss.reasoner.ReasoningLibrary;
import eu.riscoss.reasoner.RiskAnalysisEngine;
import eu.riscoss.JSmile;

public class RiskAnalysisEngineService
{
    public RiskAnalysisEngine createInstance() {
        JSmile.load();
        return ReasoningLibrary.get().createRiskAnalysisEngine();
    }
}
