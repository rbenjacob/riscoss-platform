package eu.riscoss.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import eu.riscoss.HibernateSessionProvider;

/**
 * TestingHibernateSessionProvider.
 *
 * @version $Id$
 */
@Component
@Singleton
public class XWikiHibernateSessionProvider implements HibernateSessionProvider
{
    @Inject
    private Logger logger;

    private SessionFactory sessionFactory;

    @Override public Session getSession()
    {
        //TODO: Implement this
        return null;
    }
}
