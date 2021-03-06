package org.broadinstitute.mpg

import grails.test.mixin.TestFor
import groovy.json.JsonSlurper
import org.broadinstitute.mpg.SharedToolsService
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(SharedToolsService)
class SharedToolsServiceUnitSpec extends Specification {




    void "test convertStringToArray"() {
        when:
        List <String> converted = service.convertStringToArray(inString)
        then:
        converted.size() == theSize
        where:
        inString            |       theSize
        "\"ABC\",\"def\""   |       2
        "123,def"           |       2
        "123_123"           |       1
        ""                  |       0

    }



    void "test  convertListToString{"() {
        when:
        String converted = service.convertListToString(inList)
        then:
        converted.size() == theSize
        where:
        inList              |       theSize
        ["s"]               |       3
        [""]                |       0
        ["","",""]          |       0
        ["a","",3]          |       7
    }






    void "test  convertMultilineToList"() {
        given:
        String string1 = """12321
cdsaed
""".toString()
        String string2 = """12321""".toString()
        String string3 = """12321


"ghg"

p
""".toString()
        when:
        List<String> list1 = service.convertMultilineToList(string1)
        List<String> list2 = service.convertMultilineToList(string2)
        List<String> list3 = service.convertMultilineToList(string3)
        then:
        list1.size() == 2
        list2.size() == 1
        list3.size() == 3
    }

    /***
     * let's see if we can make some legal Json given a list
     */
    void "test  createDistributedBurdenTestInput"() {
        when:
        List list = ["a","b"]
        String results = service.createDistributedBurdenTestInput(list)
        def userJson = new JsonSlurper().parseText(results )
        then:
        userJson.keySet().size() == 3
    }



    /***
     * Distinguish good chromosome specifications from bad?
     */
    void "test parseChromosome"() {
        when:
        String chr1 = service.parseChromosome('chr9')
        String chr2 = service.parseChromosome('chrX')
        String chr3 = service.parseChromosome('chrY')
        String chr4 = service.parseChromosome('chrZ')
        then:
        chr1=='9'
        chr2=='X'
        chr3=='Y'
        chr4==''
    }

}
