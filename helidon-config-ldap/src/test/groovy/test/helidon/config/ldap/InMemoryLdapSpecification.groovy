package test.helidon.config.ldap

import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import helidon.config.ldap.LdapEndpoint
import ldaptive.LdapTree
import spock.lang.Specification

class InMemoryLdapSpecification extends Specification {

    public static final NO_SCHEMA_REQUIRED = null
    public static final String bindDn = "cn=Directory Manager"
    public static final String PASSWORD = "password"
    public static final String BASE_DN = "dc=example,dc=com"

    protected static int ephemeralPort

    def setupSpec(){
        InMemoryDirectoryServerConfig config =
                new InMemoryDirectoryServerConfig(BASE_DN)
        config.addAdditionalBindCredentials(bindDn, PASSWORD)
        config.setSchema(NO_SCHEMA_REQUIRED)

        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config)
        ds.importFromLDIF(true, "src/test/resources/test-data.ldif")
        ds.startListening()
        ephemeralPort = ds.getListenPort()
    }

    def "read directory entries into map"() {
        LdapTree ldapTree = new LdapTree.Default(
                new LdapEndpoint('ldap://localhost:' + ephemeralPort, bindDn, PASSWORD)
        )
        expect:
            ldapTree.toMap(BASE_DN, "sn") == [
                'com.example.boyle': "Boyle",
                'com.example.curie': "Curie",
                'com.example.einstein': "Einstein",
                'com.example.euclid': "Euclid",
                'com.example.euler': "Euler",
                'com.example.galileo': "Galilei",
                'com.example.gauss': "Gauss",
                'com.example.jmacy': "training",
                'com.example.newton': "Newton",
                'com.example.nobel': "Nobel",
                'com.example.nogroup': "Group",
                'com.example.pasteur': "Pasteur",
                'com.example.read-only-admin': "Read Only Admin",
                'com.example.riemann': "Riemann",
                'com.example.tesla': "Tesla",
                'com.example.training': "training",
                'com.example.test': "Test"
            ]
    }
}
