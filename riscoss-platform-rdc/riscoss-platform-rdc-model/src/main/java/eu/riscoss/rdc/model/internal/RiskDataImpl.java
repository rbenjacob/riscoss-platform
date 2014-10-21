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
package eu.riscoss.rdr.model.internal;

import java.util.Date;

import eu.riscoss.rdr.model.RiskData;
import eu.riscoss.rdr.model.RiskDataType;

public class RiskDataImpl implements RiskData
{
    private String id;

    private String target;

    private Date date;

    private RiskDataType type;

    private Object value;

    @Override public String getId()
    {
        return id;
    }

    @Override public String getTarget()
    {
        return target;
    }

    @Override public Date getDate()
    {
        return date;
    }

    @Override public RiskDataType getType()
    {
        return type;
    }

    @Override public Object getValue()
    {
        return value;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setType(RiskDataType type)
    {
        this.type = type;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
