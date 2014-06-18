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
package eu.riscoss.rdr.model;

import java.util.ArrayList;
import java.util.List;

public class Distribution
{
    private List<Double> values;

    public Distribution(Double... values)
    {
        this.values = new ArrayList<Double>();
        for (Double d : values) {
            this.values.add(d);
        }
    }

    public List<Double> getValues()
    {
        return values;
    }

    public void setValues(List<Double> values)
    {
        this.values = values;
    }
}
