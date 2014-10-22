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
package eu.riscoss.rdc;

import java.util.Date;

import eu.riscoss.rdc.model.RiskData;
import eu.riscoss.rdc.model.RiskDataType;
import eu.riscoss.rdc.model.internal.RiskDataImpl;

public class RiskDataFactory
{
    public static RiskData createRiskData(String id, String target, Date date, RiskDataType type, Object value)
    {
        RiskDataImpl riskData = new RiskDataImpl();
        riskData.setId(id);
        riskData.setTarget(target);
        riskData.setDate(date);
        riskData.setType(type);
        riskData.setValue(value);

        return riskData;
    }
}
