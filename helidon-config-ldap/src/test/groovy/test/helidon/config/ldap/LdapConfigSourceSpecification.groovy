package test.helidon.config.ldap

import helidon.config.ldap.LdapConfigSource
import helidon.config.ldap.LdapConfigSourceBuilder
import io.helidon.config.Config
import io.helidon.config.ConfigException
import io.helidon.config.ConfigSources
import ldaptive.LdapException
import ldaptive.LdapTree
import spock.lang.Specification

class LdapConfigSourceSpecification extends Specification {
    private static final Map<String, String> ldapParams = [
            "uri": "ldap://ldap.forumsys.com"
            ,"bindDn": "cn=read-only-admin,dc=example,dc=com"
            ,"password": "password"
            ,"baseDn": "dc=example,dc=com"
            ,"attrName": "description"
            ]


    def "can build configuration from one-entry ldap tree"(){
        LdapTree ldapTree = Mock(LdapTree){
            toMap(_, _) >> ['com.example.newton': 'Newton']
        }
        when:
            LdapConfigSource source = new LdapConfigSource(LdapConfigSource
                    .builder(), ldapTree)
        then:
            Config.create(source).get("com.example").get('newton').asString().get() == 'Newton'
   }

    def "throw ConfigException when there is an error in ldap tree"(){
        LdapTree ldapTree = Mock(LdapTree){
            toMap(_, _) >> {throw new LdapException()}
        }
        when:

            Config.create(new LdapConfigSource(LdapConfigSource
                    .builder(), ldapTree))
        then:
            ConfigException exception = thrown()
    }

    def "test master"(){
        Config bootstrap = Config.create(
                ConfigSources.create(ldapParams)
                        .build());
        when:
            LdapConfigSourceBuilder builder = LdapConfigSource
                .builder()
                .config(bootstrap)
        then:
            builder.uri() == "ldap://ldap.forumsys.com"
            builder.password() == 'password'
            builder.bindDn() == "cn=read-only-admin,dc=example,dc=com"
            builder.baseDn() == "dc=example,dc=com"
            builder.attrName() == "description"
    }

    def "chained"(){
        LdapConfigSource configSource = LdapConfigSource
                .builder()
                .uri('uri')
                .bindDn('bindDn')
                .password('password')
                .attrName('attrName')
                .baseDn('baseDn')
                .build()
        expect:
            configSource.client.ldapUrl == 'uri'
            configSource.client.bindDn ==  'bindDn'
            configSource.client.password ==  'password'
            configSource.attrName == 'attrName'
            configSource.baseDn == 'baseDn'
    }
}

