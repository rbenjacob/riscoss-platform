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
