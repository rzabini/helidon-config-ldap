package ldaptive;

/**
 *
 */
public class LdapException extends Exception {
    public LdapException(final org.ldaptive.LdapException e) {
        super(e);
    }
}
