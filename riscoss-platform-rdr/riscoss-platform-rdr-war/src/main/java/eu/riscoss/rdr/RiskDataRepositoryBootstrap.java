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
package eu.riscoss.rdr;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.rdr.rest.RiskDataRepositoryProvider;

public class RiskDataRepositoryBootstrap implements ServletContextListener
{
    private Logger logger = LoggerFactory.getLogger(RiskDataRepositoryBootstrap.class);

    public void contextDestroyed(ServletContextEvent event)
    {
        RiskDataRepositoryProvider.setRiskDataRepository(null);
    }

    public void contextInitialized(ServletContextEvent event)
    {
        try {
            RiskDataRepositoryProvider.setRiskDataRepository(RiskDataRepositoryFactory
                    .create(new HibernateSessionProviderImpl()));
        } catch (Exception e) {
            logger.error("Unable to initialize Risk Data Repository", e);
            throw new RuntimeException(e);
        }

        logger.info("Risk data repository initialized.");
    }
}
