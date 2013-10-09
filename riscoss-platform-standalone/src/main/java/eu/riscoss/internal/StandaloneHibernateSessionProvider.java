package eu.riscoss.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import eu.riscoss.HibernateSessionProvider;

/**
 * StandaloneHibernateSessionProvider.
 *
 * @version $Id$
 */
@Component
@Singleton
public class StandaloneHibernateSessionProvider implements HibernateSessionProvider, Initializable
{
    @Inject
    private Logger logger;

    private Session session;

    @Override public void initialize() throws InitializationException
    {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            session = sessionFactory.openSession();
            session.setFlushMode(FlushMode.COMMIT);

            logger.info("Initialized");
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override public Session getSession()
    {
        return session;
    }
}
