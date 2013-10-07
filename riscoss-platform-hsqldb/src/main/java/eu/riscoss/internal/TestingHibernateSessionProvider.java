package eu.riscoss.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import eu.riscoss.HibernateSessionProvider;

/**
 * TestingHibernateSessionProvider.
 *
 * @version $Id$
 */
@Component
@Singleton
public class TestingHibernateSessionProvider implements HibernateSessionProvider, Initializable
{
    @Inject
    private Logger logger;

    private SessionFactory sessionFactory;

    @Override public void initialize() throws InitializationException
    {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();

            logger.info("Initialized");
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override public Session getSession()
    {
        return sessionFactory.getCurrentSession();
    }
}
