package ldaptive;

import helidon.config.ldap.LdapEndpoint;
import org.ldaptive.*;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public interface LdapTree {

    Map<String, String> toMap(String baseDn, String attrName) throws LdapException;

    class Default implements LdapTree {
        private final String ldapUrl;
        private final String password;
        private final String bindDn;

        public Default(final LdapEndpoint ldapEndpoint) {
            this.ldapUrl = ldapEndpoint.ldapUrl();
            this.bindDn = ldapEndpoint.bindDn();
            this.password = ldapEndpoint.password();
        }

        private static String value(final LdapEntry ldapEntry, final String attrName) {
            return ldapEntry.getAttribute(attrName).getStringValue();
        }

        private String toDotted(final LdapEntry ldapEntry) {
            return ldapName(ldapEntry).getRdns().stream()
                    .map(rdn -> rdn.getValue().toString())
                    .collect(Collectors.joining("."));
        }

        private static LdapName ldapName(final LdapEntry ldapEntry) {
            try {
                return new LdapName(ldapEntry.getDn());
            } catch (InvalidNameException e) {
                throw new IllegalArgumentException("cannot happen", e);
            }
        }

        public Map<String, String> toMap(final String baseDn, final String attrName) throws LdapException {
            ConnectionConfig connConfig = new ConnectionConfig(ldapUrl);
               connConfig.setConnectionInitializer(
                        new BindConnectionInitializer(
                                bindDn, new Credential(password)));
                ConnectionFactory cf = new DefaultConnectionFactory(connConfig);
            try {
                return searchThenMap(baseDn, attrName, cf);
            } catch (org.ldaptive.LdapException e) {
                throw new LdapException(e);
            }
        }

        private Map<String, String> searchThenMap(String baseDn, String attrName, ConnectionFactory cf) throws org.ldaptive.LdapException {
            SearchExecutor executor = new SearchExecutor();
            executor.setBaseDn(baseDn);
            SearchResult result = executor.search(cf, String.format("(%s=*)", attrName), attrName).getResult();
            return mapResponse(result.getEntries(), attrName);
        }

        public Map<String, String> mapResponse(final Collection<LdapEntry> searchResponseEntries, final String attrName) {
            return searchResponseEntries.stream()
                    .collect(Collectors.toMap(
                            this::toDotted,
                            ldapEntry -> value(ldapEntry, attrName)));
        }
    }
}
