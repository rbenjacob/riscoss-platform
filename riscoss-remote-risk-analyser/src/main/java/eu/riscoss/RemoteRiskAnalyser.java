/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package eu.riscoss;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.IOUtils;

import eu.riscoss.reasoner.ReasoningLibrary;
import eu.riscoss.reasoner.RiskAnalysisEngine;
import eu.riscoss.reasoner.Chunk;
import eu.riscoss.reasoner.ModelSlice;
import eu.riscoss.reasoner.Field;
import eu.riscoss.reasoner.FieldType;
import eu.riscoss.reasoner.Evidence;
import eu.riscoss.reasoner.Distribution;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class RemoteRiskAnalyser
{
    static class RiskDataAndErrors
    {
        Map<String, Object> riskData;
        Map<String, String> errors;
    }

    static List<String> setRiskData(RiskAnalysisEngine riskAnalysisEngine,
                                    Map<String, Object> riskData)
    {
        Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.INPUT_DATA);
        List<String> warnings = new ArrayList<String>();
        for (Chunk chunk : chunks) {
            Field field = riskAnalysisEngine.getField(chunk, FieldType.INPUT_VALUE);

            Object value = riskData.get(chunk.getId());
            if (value != null) {
                switch (field.getDataType()) {
                    case INTEGER:
                        if (value instanceof Integer) {
                            field.setValue(value);
                            riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                        } else {
                            warnings.add(String.format(
                                    "Retrieved risk data for %s has the wrong type. Expected double, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                    case REAL:
                        if (value instanceof Double) {
                            field.setValue(value);
                            riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                        } else {
                            warnings.add(String.format(
                                    "Retrieved risk data for %s has the wrong type. Expected double, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                    case EVIDENCE:
                        if (value instanceof Evidence) {
                            field.setValue(value);
                            riskAnalysisEngine.setField(chunk, FieldType.INPUT_VALUE, field);
                        } else {
                            warnings.add(String.format(
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
                                warnings.add(String.format(
                                        "Retrieved risk data for %s has the wrong size. Expected %d-Distribution, got %d-Distribution",
                                        chunk.getId(), ((Distribution) value).getValues().size(),
                                        ((Distribution) field.getValue()).getValues().size()));
                            }
                        } else {
                            warnings.add(String.format(
                                    "Retrieved risk data for %s has the wrong type. Expected Distribution, got %s",
                                    chunk.getId(), value.getClass().getName()));
                        }
                        break;
                }
            }
        }
        return warnings;
    }

    static RiskDataAndErrors getRiskDataFromRequest(RiskAnalysisEngine riskAnalysisEngine,
                                                    Map<String, String[]> requestParams)
    {
        Map<String, Object> riskData = new HashMap<String, Object>();
        Map<String, String> errors = new HashMap<String, String>();

        Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.INPUT_DATA);
        for (Chunk chunk : chunks) {
            Field field = riskAnalysisEngine.getField(chunk, FieldType.INPUT_VALUE);
            try {

                String[] values = requestParams.get(chunk.getId());

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

        RiskDataAndErrors result = new RiskDataAndErrors();
        result.riskData = riskData;
        result.errors = errors;

        return result;
    }

    static Map<String, Map<String, Object>> runAnalysis(RiskAnalysisEngine riskAnalysisEngine)
    {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

        riskAnalysisEngine.runAnalysis(new String[0]);

        Iterable<Chunk> chunks = riskAnalysisEngine.queryModel(ModelSlice.OUTPUT_DATA);
        for (Chunk chunk : chunks) {
            Field field = riskAnalysisEngine.getField(chunk, FieldType.OUTPUT_VALUE);

            Map<String, Object> item = new HashMap<String, Object>();
            Field descriptionField = riskAnalysisEngine.getField(chunk, FieldType.DESCRIPTION);
            if (descriptionField != null) {
                item.put("DESCRIPTION", riskAnalysisEngine.getField(chunk, FieldType.DESCRIPTION).getValue());
            } else {
                item.put("DESCRIPTION", chunk.getId());
            }
            item.put("TYPE", field.getDataType());
            item.put("VALUE", field.getValue());

            result.put(chunk.getId(), item);
        }

        return result;
    }

    static Map<String, String[]> unpackRequestMap(JSONObject obj)
    {
        Map<String, String[]> out = new HashMap<String, String[]>();
        for (String key : JSONObject.getNames(obj)) {
            JSONArray arr = obj.getJSONArray(key);
            String[] x = new String[arr.length()];
            for (int i = 0; i < x.length; i++) {
                x[i] = arr.getString(i);
            }
            out.put(key, x);
        }
        return out;
    }

    static void load(String input, JSONObject out)
    {
        JSONObject in = new JSONObject(input);
        RiskAnalysisEngine engine = ReasoningLibrary.get().createRiskAnalysisEngine();
        JSONArray riskModels = in.getJSONArray("riskModels");
        for (int i = 0; i < riskModels.length(); i++) {
            engine.loadModel(riskModels.getString(i));
        }
        Map<String, String[]> rm = unpackRequestMap(in.getJSONObject("requestMap"));
        RiskDataAndErrors res = getRiskDataFromRequest(engine, rm);
        if (res.errors.size() > 0) {
            out.put("errors", new JSONObject(res.errors));
        } else {
            List<String> warnings = setRiskData(engine, res.riskData);
            out.put("warnings", new JSONArray(warnings));
            Map<String, Map<String, Object>> m = runAnalysis(engine);
            // use net.sf.json for this particular field to preserve backward compat behavior.
            out.put("result", net.sf.json.JSONObject.fromObject(m).toString());
        }
    }

    public static void main() throws Exception
    {
        JSONObject out = new JSONObject();
        String stdin = IOUtils.toString(System.in, "UTF-8");
        load(stdin, out);
        System.out.println("-----BEGIN ANALYSIS OUTPUT-----");
        System.out.println(out.toString());
        System.out.println("-----END ANALYSIS OUTPUT-----");
    }
}
