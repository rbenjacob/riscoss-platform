package eu.riscoss;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
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
public class RISCOSSScriptService implements ScriptService, Initializable
{
    @Inject
    private Logger logger;

    private RiskAnalysisEngineService riskAnalysisEngineService;

    private RiskAnalysisEngineUtilsService riskAnalysisEngineUtilsService;

    public RiskAnalysisEngineService getRiskAnalysisEngineService()
    {
        return riskAnalysisEngineService;
    }

    public RiskAnalysisEngineUtilsService getRiskAnalysisEngineUtilsService()
    {
        return riskAnalysisEngineUtilsService;
    }

    @Override public void initialize() throws InitializationException
    {
        riskAnalysisEngineService = new RiskAnalysisEngineService();
        riskAnalysisEngineUtilsService = new RiskAnalysisEngineUtilsService(logger);
    }
}
