package ldaptive;

public class LdapException extends Exception {
    public LdapException(org.ldaptive.LdapException e) {
        super(e);
    }
}
