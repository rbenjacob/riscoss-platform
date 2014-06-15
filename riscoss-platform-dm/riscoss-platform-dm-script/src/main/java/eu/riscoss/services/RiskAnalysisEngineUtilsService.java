package eu.riscoss.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.riscoss.reasoner.Chunk;
import eu.riscoss.reasoner.Evidence;
import eu.riscoss.reasoner.Distribution;
import eu.riscoss.reasoner.Field;
import eu.riscoss.reasoner.FieldType;
import eu.riscoss.reasoner.ModelSlice;
import eu.riscoss.reasoner.RiskAnalysisEngine;

public class RiskAnalysisEngineUtilsService
{
    private final Logger logger;

    public RiskAnalysisEngineUtilsService(Logger logger)
    {
        this.logger = logger;
    }

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

    public Distribution createDistribution(List<Double> values)
    {
        Distribution result = new Distribution();

        result.setValues(values);

        return result;
    }

    public Map<String, Map<String, Object>> runAnalysis(RiskAnalysisEngine riskAnalysisEngine)
    {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

        riskAnalysisEngine.runAnalysis(new String[0]);

        Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.OUTPUT_DATA);
        for (Chunk chunk : chunks) {
            Field field = riskAnalysisEngine.getField(chunk, FieldType.OUTPUT_VALUE);

            Map<String, Object> item = new HashMap<String, Object>();
            item.put("DESCRIPTION", riskAnalysisEngine.getField(chunk, FieldType.DESCRIPTION).getValue());
            item.put("TYPE", field.getDataType());
            item.put("VALUE", field.getValue());

            result.put(chunk.getId(), item);
        }

        return result;
    }

    public void setRiskData(RiskAnalysisEngine riskAnalysisEngine, Map<String, Object> riskData)
    {
        Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.INPUT_DATA);
        for (Chunk chunk : chunks) {
            Field field = riskAnalysisEngine.getField(chunk, FieldType.INPUT_VALUE);

            Object value = riskData.get(chunk.getId());
            if (value != null) {
                switch (field.getDataType()) {
                    case INTEGER:
                        if (value instanceof Integer) {
                            riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                        } else {
                            logger.warn(String.format(
                                    "Retrieved risk data for %s has the wrong type. Expected double, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                    case REAL:
                        if (value instanceof Double) {
                            field.setValue(value);
                            riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                        } else {
                            logger.warn(String.format(
                                    "Retrieved risk data for %s has the wrong type. Expected double, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                    case EVIDENCE:
                        if (value instanceof Evidence) {
                            field.setValue(value);
                            riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                        } else {
                            logger.warn(String.format(
                                    "Retrieved risk data for %s has the wrong type. Evidence double, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                    case DISTRIBUTION:
                        if (value instanceof Distribution) {
                            if (((Distribution) value).getValues().size() ==
                                    ((Distribution) field.getValue()).getValues().size())
                            {
                                field.setValue(value);
                                riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                            } else {
                                logger.warn(String.format(
                                        "Retrieved risk data for %s has the wrong size. Expected %d-Distribution, got %d-Distribution",
                                        chunk.getId(), ((Distribution) value).getValues().size(),
                                        ((Distribution) field.getValue()).getValues().size()));
                            }
                        } else {
                            logger.warn(String.format(
                                    "Retrieved risk data for %s has the wrong type. Expected Distribution, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                }
            }
        }
    }

    public RiskDataResult getRiskDataFromRequest(RiskAnalysisEngine riskAnalysisEngine,
            Map<String, Object> requestParams)
    {
        Map<String, Object> riskData = new HashMap<String, Object>();
        Map<String, String> errors = new HashMap<String, String>();

        Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.INPUT_DATA);
        for (Chunk chunk : chunks) {
            Field field = riskAnalysisEngine.getField(chunk, FieldType.INPUT_VALUE);
            try {

                String[] values = (String[]) requestParams.get(chunk.getId());

                switch (field.getDataType()) {
                    case INTEGER:
                        int i = 0;

                        if (values != null && !values[0].isEmpty()) {
                            i = Integer.parseInt(values[0]);
                        }

                        riskData.put(chunk.getId(), i);
                        break;
                    case REAL:
                        double d = 0.0d;

                        if (values != null && !values[0].isEmpty()) {
                            d = Double.parseDouble(values[0]);
                        }

                        riskData.put(chunk.getId(), d);
                        break;
                    case EVIDENCE:
                        if (values != null && values.length == 2) {
                            double p = 0.0;
                            double n = 0.0;

                            if (!values[0].isEmpty()) {
                                p = Double.parseDouble(values[0]);
                            }

                            if (!values[1].isEmpty()) {
                                n = Double.parseDouble(values[1]);
                            }

                            Evidence evidence = new Evidence(p, n);
                            riskData.put(chunk.getId(), evidence);
                        } else {
                            errors.put(chunk.getId(), "Two values are required");
                        }
                        break;
                    case DISTRIBUTION:
                        List<Double> distributionValues = new ArrayList<Double>();
                        if (values != null) {
                            for (String v : values) {
                                if (!v.isEmpty()) {
                                    distributionValues.add(Double.parseDouble(v));
                                } else {
                                    distributionValues.add(0.0d);
                                }
                            }
                        } else {
                            for (int n = 0; n < ((Distribution) field.getValue()).getValues().size(); n++) {
                                distributionValues.add(0.0d);
                            }
                        }

                        Distribution distribution = new Distribution();
                        distribution.setValues(distributionValues);
                        riskData.put(chunk.getId(), distribution);
                        break;
                    case STRING:
                        riskData.put(chunk.getId(), values[0]);
                        break;
                }
            } catch (NumberFormatException e) {
                errors.put(chunk.getId(), String.format("Invalid number format for %s", field.getDataType()));
            } catch (Exception e) {
                errors.put(chunk.getId(), e.getMessage());
            }
        }

        RiskDataResult result = new RiskDataResult(riskData, errors);

        return result;
    }

    public Map<String, Object> fetchRiskData(RiskAnalysisEngine riskAnalysisEngine, String riskDataRepositoryURI,
            String target)
    {
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            Gson gson = new Gson();
            CloseableHttpClient client = HttpClientBuilder.create().build();

            Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.INPUT_DATA);
            for (Chunk chunk : chunks) {
                HttpGet get =
                        new HttpGet(String.format("%s/%s?id=%s&limit=1", riskDataRepositoryURI, target, chunk.getId()));
                CloseableHttpResponse response = client.execute(get);

                if (response.getStatusLine().getStatusCode() != 200) {
                    logger.error(String.format("Error retrieving risk data for %s: %s", chunk.getId(),
                            response.getStatusLine().getReasonPhrase()));
                }

                JsonObject jsonObject =
                        gson.fromJson(IOUtils.toString(response.getEntity().getContent()), JsonObject.class);
                JsonArray riskDataArray = jsonObject.getAsJsonArray("results");
                if (riskDataArray.size() != 0) {
                    JsonObject riskData = riskDataArray.get(0).getAsJsonObject();

                    String riskDataType = riskData.get("type").getAsString();
                    if ("NUMBER".equalsIgnoreCase(riskDataType)) {
                        result.put(chunk.getId(), riskData.get("value").getAsDouble());
                    } else if ("EVIDENCE".equalsIgnoreCase(riskDataType)) {
                        JsonArray array = riskData.get("value").getAsJsonArray();
                        Evidence evidence = new Evidence(array.get(0).getAsDouble(), array.get(1).getAsDouble());
                        result.put(chunk.getId(), evidence);
                    } else if ("DISTRIBUTION".equalsIgnoreCase(riskDataType)) {
                        List<Double> values = new ArrayList<Double>();
                        JsonArray array = riskData.get("value").getAsJsonArray();
                        for (int i = 0; i < array.size(); i++) {
                            values.add(array.get(i).getAsDouble());
                        }

                        Distribution distribution = new Distribution();
                        distribution.setValues(values);
                        result.put(chunk.getId(), distribution);
                    } else {
                        logger.warn(
                                String.format("Risk data type %s not supported for %s", riskDataType, chunk.getId()));
                    }
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Error fetching risk data from %s", riskDataRepositoryURI), e);
        }

        return result;
    }
}
