import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import eu.riscoss.HibernateSessionProvider;

/**
 * HibernateSessionProviderImpl.
 *
 * @version $Id$
 */
public class HibernateSessionProviderImpl implements HibernateSessionProvider, Initializable
{
    private SessionFactory sessionFactory;

    @Override public void initialize() throws InitializationException
    {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
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
