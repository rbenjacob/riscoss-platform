package eu.riscoss;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;

import eu.riscoss.reasoner.AnalysisResponse;
import eu.riscoss.reasoner.ModelSlice;
import eu.riscoss.reasoner.ReasoningLibrary;
import eu.riscoss.reasoner.RiskAnalysisEngine;
import eu.riscoss.services.RiskAnalysisEngineService;
import eu.riscoss.services.RiskAnalysisEngineUtilsService;

@Component
@Named("riscoss")
@Singleton
public class RISCOSSScriptService implements ScriptService
{
    private RiskAnalysisEngineService riskAnalysisEngineService;

    private RiskAnalysisEngineUtilsService riskAnalysisEngineUtilsService;

    public RISCOSSScriptService()
    {
        riskAnalysisEngineService = new RiskAnalysisEngineService();
        riskAnalysisEngineUtilsService = new RiskAnalysisEngineUtilsService();
    }

    public RiskAnalysisEngineService getRiskAnalysisEngineService()
    {
        return riskAnalysisEngineService;
    }

    public RiskAnalysisEngineUtilsService getRiskAnalysisEngineUtilsService()
    {
        return riskAnalysisEngineUtilsService;
    }
}
