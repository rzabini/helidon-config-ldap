package ldaptive;

import helidon.config.ldap.LdapEndpoint;
import org.ldaptive.BindOperation;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.ldaptive.SearchScope;
import org.ldaptive.SimpleBindRequest;
import org.ldaptive.SingleConnectionFactory;
import org.ldaptive.filter.PresenceFilter;

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
            try (SingleConnectionFactory cf = new SingleConnectionFactory(ldapUrl)) {
                cf.initialize();
                new BindOperation(cf).execute(SimpleBindRequest.builder().dn(bindDn).password(password).build());

                return searchThenMap(baseDn, attrName, cf);
            } catch (org.ldaptive.LdapException e) {
                throw new LdapException(e);
            }
        }

        private Map<String, String> searchThenMap(String baseDn, String attrName, SingleConnectionFactory cf) throws org.ldaptive.LdapException {
            final SearchResponse searchResponse = new SearchOperation(cf)
                    .execute(SearchRequest.builder()
                            .dn(baseDn).scope(SearchScope.SUBTREE)
                            .filter(new PresenceFilter(attrName))
                            .returnAttributes(attrName).build());
            return mapResponse(searchResponse.getEntries(), attrName);
        }

        public Map<String, String> mapResponse(final Collection<LdapEntry> searchResponseEntries, final String attrName) {
            return searchResponseEntries.stream()
                    .collect(Collectors.toMap(
                            this::toDotted,
                            ldapEntry -> value(ldapEntry, attrName)));
        }
    }

}
