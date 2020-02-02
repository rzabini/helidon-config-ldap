package helidon.config.ldap;

/**
 * LdapEndoint represents an uri with authentication credentials.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class LdapEndpoint {
    private final String ldapUrl;
    private final String bindDn;
    private final String password;

    public LdapEndpoint(final String ldapUrl, final String bindDn, final String password) {
        this.ldapUrl = ldapUrl;
        this.bindDn = bindDn;
        this.password = password;
    }

    public String ldapUrl() {
        return ldapUrl;
    }

    public String bindDn() {
        return bindDn;
    }

    public String password() {
        return password;
    }
}
