package eu.riscoss.services;

import eu.riscoss.reasoner.Chunk;
import eu.riscoss.reasoner.Evidence;
import eu.riscoss.reasoner.Field;
import eu.riscoss.reasoner.FieldType;
import eu.riscoss.reasoner.ModelSlice;
import eu.riscoss.reasoner.RiskAnalysisEngine;

public class RiskAnalysisEngineUtilsService
{
    public Iterable<Chunk> getInputs(RiskAnalysisEngine riskAnalysisEngine)
    {
        return riskAnalysisEngine.queryModel(ModelSlice.INPUT_DATA);
    }

    public Iterable<Chunk> getOutputs(RiskAnalysisEngine riskAnalysisEngine)
    {
        return riskAnalysisEngine.queryModel(ModelSlice.OUTPUT_DATA);
    }

    public Field getInputField(RiskAnalysisEngine riskAnalysisEngine, Chunk chunk)
    {
        return riskAnalysisEngine.getField(chunk, FieldType.INPUT_VALUE);
    }

    public void setInputField(RiskAnalysisEngine riskAnalysisEngine, Chunk chunk, Field field)
    {
        riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
    }

    public Field getOutputField(RiskAnalysisEngine riskAnalysisEngine, Chunk chunk)
    {
        return riskAnalysisEngine.getField(chunk, FieldType.OUTPUT_VALUE);
    }

    public String getDescription(RiskAnalysisEngine riskAnalysisEngine, Chunk chunk)
    {
        return riskAnalysisEngine.getField(chunk, FieldType.DESCRIPTION).getValue();
    }

    public String getQuestion(RiskAnalysisEngine riskAnalysisEngine, Chunk chunk)
    {
        return riskAnalysisEngine.getField(chunk, FieldType.QUESTION).getValue();
    }

    public Evidence createEvidence(double p, double n)
    {
        return new Evidence(p, n);
    }
}
