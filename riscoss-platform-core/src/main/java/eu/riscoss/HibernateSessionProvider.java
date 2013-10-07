package eu.riscoss;

import org.hibernate.Session;
import org.xwiki.component.annotation.Role;

/**
 * HibernateSessionProvider.
 *
 * @version $Id$
 */
@Role
public interface HibernateSessionProvider
{
    Session getSession();
}
