package test.helidon.config.ldap.ldaptive

import helidon.config.ldap.LdapEndpoint
import ldaptive.LdapTree
import org.ldaptive.LdapAttribute
import org.ldaptive.LdapEntry
import spock.lang.Specification

class LdapTreeSpecification extends Specification {

    def "map empty list to empty map"(){
        expect:
            new LdapTree.Default(new LdapEndpoint('','','')).mapResponse(Collections.emptyList(), 'attrname') == [:]
    }

    def "map single item list to single entry map"(){
        Collection<LdapEntry> entries = new ArrayList<LdapEntry>()
        LdapEntry entry = new LdapEntry()
        entry.setDn('cn=item, dc=example, dc=com')
        entry.addAttributes(new LdapAttribute('attrname', 'attrvalue'))
        entries.add(entry)

        expect:
            new LdapTree.Default(new LdapEndpoint('','','')).mapResponse(
                    entries, 'attrname') == ['com.example.item': 'attrvalue']
    }
}
