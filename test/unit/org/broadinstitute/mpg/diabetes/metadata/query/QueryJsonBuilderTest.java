package org.broadinstitute.mpg.diabetes.metadata.query;

import junit.framework.TestCase;
import org.broadinstitute.mpg.diabetes.knowledgebase.result.Variant;
import org.broadinstitute.mpg.diabetes.knowledgebase.result.VariantBean;
import org.broadinstitute.mpg.diabetes.metadata.Property;
import org.broadinstitute.mpg.diabetes.metadata.parser.JsonParser;
import org.broadinstitute.mpg.diabetes.metadata.sort.PropertyListForQueryComparator;
import org.broadinstitute.mpg.diabetes.util.PortalConstants;
import org.broadinstitute.mpg.diabetes.util.PortalException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mduby on 8/27/15.
 */
public class QueryJsonBuilderTest extends TestCase {
    // instance variables
    QueryJsonBuilder queryJsonBuilder;
    JsonParser jsonParser;
    List<Property> propertyList;
    String jsonString;
    List<QueryFilter> filterList;
    String headerDataString = "";

    @Before
    public void setUp() throws Exception {
        this.queryJsonBuilder = QueryJsonBuilder.getQueryJsonBuilder();
        this.jsonParser = JsonParser.getService();
        InputStream inputStream = getClass().getResourceAsStream("../parser/metadata.json");
        this.jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        this.jsonParser.setJsonString(this.jsonString);

        // build map and populate it for the tests
        this.propertyList = new ArrayList<Property>();
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_CHROMOSOME));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_CLOSEST_GENE));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_CONSEQUENCE));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_DBSNP_ID));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_POSITION));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_VAR_ID));

        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_SG_MAF_82K));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_SG_MAF_SIGMA1));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_SG_EAF_GWAS_PGC));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_SG_EAF_GWAS_GIANT));

//        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_MINA_SIGMA1_T2D));
//        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_MINU_SIGMA1_T2D));
//        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_OR_FIRTH_SIGNA1_T2D));
//        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_P_VALUE_82K_T2D));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_BETA_GWAS_MAGIC_2HRG));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_P_VALUE_GWAS_DIAGRAM_T2D));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_P_VALUE_82K_T2D));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_BETA_13K_FG));
        this.propertyList.add((Property) this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_BETA_13K_HBA1C));

        // sort the list
        Collections.sort(this.propertyList, new PropertyListForQueryComparator());

        // build the filter list
        this.filterList = new ArrayList<QueryFilter>();
        this.filterList.add(new QueryFilterBean((Property)this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_PH_P_VALUE_GWAS_DIAGRAM_T2D), PortalConstants.OPERATOR_LESS_THAN_NOT_EQUALS, "1"));
        this.filterList.add(new QueryFilterBean((Property)this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_CHROMOSOME), PortalConstants.OPERATOR_EQUALS, "9"));
        this.filterList.add(new QueryFilterBean((Property)this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_POSITION), PortalConstants.OPERATOR_MORE_THAN_EQUALS, "21940000"));
        this.filterList.add(new QueryFilterBean((Property)this.jsonParser.getMapOfAllDataSetNodes().get(PortalConstants.PROPERTY_KEY_COMMON_POSITION), PortalConstants.OPERATOR_LESS_THAN_EQUALS, "22190000"));

        // set the header data
        this.headerDataString = "\"passback\": \"123abc\", \"entity\": \"variant\", \"page_number\": 0, \"page_size\": 100, \"limit\": 1000, \"count\": false, ";
    }

    @Test
    public void testGetCpropertiesString() {
        // local variables
        String compareString = "\"cproperty\": [ \"CHROM\" , \"CLOSEST_GENE\" , \"Consequence\" , \"DBSNP_ID\" , \"POS\" , \"VAR_ID\"], ";
        String generatedString = null;

        // generate the string
        generatedString = this.queryJsonBuilder.getCpropertiesString(this.propertyList);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
        assertEquals(compareString, generatedString);
    }

    @Test
    public void testGetDpropertiesString() {
        // local variables
        String compareString = "\"dproperty\" : {\"EAF\" : [ \"GWAS_PGC_mdv2\" , \"GWAS_GIANT_mdv2\"], \"MAF\" : [ \"ExChip_82k_mdv2\" , \"ExChip_SIGMA1_mdv2\"] } , ";
        String generatedString = null;

        // generate the string
        generatedString = this.queryJsonBuilder.getDpropertiesString(this.propertyList);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
        assertEquals(compareString, generatedString);

        // print for test copy
        System.out.println(generatedString);
    }

    @Test
    public void testGetFilterString() {
        // local variables
        StringBuilder builder = new StringBuilder();
        String referenceString = null;
        String generatedString = null;

        // build the reference string
        builder.append("\"filters\": [ {\"dataset_id\": \"GWAS_DIAGRAM_mdv2\", \"phenotype\": \"T2D\", \"operand\": \"P_VALUE\", \"operator\": \"LT\", \"value\": 1, \"operand_type\": \"FLOAT\"}, ");
        builder.append("{\"dataset_id\": \"blah\", \"phenotype\": \"blah\", \"operand\": \"CHROM\", \"operator\": \"EQ\", \"value\": \"9\", \"operand_type\": \"STRING\"}, ");
        builder.append("{\"dataset_id\": \"blah\", \"phenotype\": \"blah\", \"operand\": \"POS\", \"operator\": \"GTE\", \"value\": 21940000, \"operand_type\": \"INTEGER\"}, ");
        builder.append("{\"dataset_id\": \"blah\", \"phenotype\": \"blah\", \"operand\": \"POS\", \"operator\": \"LTE\", \"value\": 22190000, \"operand_type\": \"INTEGER\"} ] ");
        referenceString = builder.toString();

        // build the genererated string
        generatedString = this.queryJsonBuilder.getFilterString(this.filterList);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
        assertEquals(referenceString, generatedString);

        // print for test copy
        System.out.println(generatedString);
    }

    @Test
    public void testGetCovariateString() {
        // local variables
        StringBuilder builder = new StringBuilder();
        String referenceString = null;
        String generatedString = null;

        // create variant, add it to new covariate, then add to covariate
        Variant variant = new VariantBean();
        variant.setChromosome("22");
        variant.setPosition(29838203);
        variant.setReferenceAllele("A");
        variant.setAlternateAllele("T");
        Covariate covariate = new CovariateBean();
        covariate.setVariant(variant);
        List<Covariate> covariateList = new ArrayList<Covariate>();
        covariateList.add(covariate);

        // build the reference string
        builder.append("\"covariates\": [ {\"type\": \"variant\", \"chrom\": \"22\", \"pos\": 29838203, \"ref\": \"A\", \"alt\": \"T\"} ] ");
        referenceString = builder.toString();

        // build the genererated string
        generatedString = this.queryJsonBuilder.getCovariateString(covariateList);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
        assertEquals(referenceString, generatedString);

        // print for test copy
        System.out.println(generatedString);
    }

    @Test
    public void testGetPpropertiesString() {
        // local variables
        StringBuilder builder = new StringBuilder();
        String referenceString = null;
        String generatedString = null;

        // build the reference string
        builder.append("\"pproperty\" : {");
        builder.append("\"BETA\" : { \"ExSeq_13k_mdv2\" : [ \"FG\" , \"HBA1C\" ], \"GWAS_MAGIC_mdv2\" : [ \"2hrG\"]}");
        builder.append(", ");
        builder.append("\"P_VALUE\" : { \"ExChip_82k_mdv2\" : [ \"T2D\" ], \"GWAS_DIAGRAM_mdv2\" : [ \"T2D\"] } ");
        builder.append("} }, ");
//        builder.append("\"EAC_PH\" : {  \"ExSeq_13k_mdv2\" : [ \"FG\" , \"HBA1C\"] }  } } ,");
        referenceString = builder.toString();

        // build the generated string
        generatedString = this.queryJsonBuilder.getPpropertiesString(this.propertyList);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
        assertEquals(referenceString, generatedString);

        // print for test copy
        System.out.println(generatedString);
    }

    @Test
    public void testGetCovariatesSingleVariantString() {
        // local variables
        Covariate covariate = new CovariateBean();
        List<Covariate> covariateList = new ArrayList<Covariate>();
        Variant variant = new VariantBean();
        String expectedString = "\"covariates\": [{\"type\": \"variant\", \"chrom\": \"22\", \"pos\": 29838203, \"ref\": \"A\", \"alt\": \"T\"}] ";
        String resultString = null;

        // set the variant
        variant.setChromosome("22");
        variant.setPosition(29838203);
        variant.setReferenceAllele("A");
        variant.setAlternateAllele("T");

        // add to list
        covariate.setVariant(variant);
        covariateList.add(covariate);

        // get the string
        try {
            resultString = this.queryJsonBuilder.getCovariatesString(covariateList);

        } catch (PortalException exception) {
            fail("got exception; " + exception.getMessage());
        }

        // test
        assertNotNull(resultString);
        assertEquals(expectedString, resultString);
    }

    @Test
    public void testgetQueryJsonPayloadString() {
        // local variables
        StringBuilder builder = new StringBuilder();
        String referenceString = null;
        String generatedString = null;

        // build the generated string
        generatedString = this.queryJsonBuilder.getQueryJsonPayloadString(this.headerDataString, this.propertyList, null, this.filterList, null);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
        // TODO - tested in postman query; will need to do this using JSONObject
//        assertEquals(referenceString, generatedString);

        // print for test copy
        System.out.println(generatedString);
    }

     @Test
     public void testGetQueryJsonPayloadStringUsingQueryBean() {
        // local variables
        StringBuilder builder = new StringBuilder();
        String referenceString = null;
        String generatedString = null;

        // build the query bean
        GetDataQuery query = new GetDataQueryBean();
        for (Property property : this.propertyList) {
            query.addQueryProperty(property);
        }
        for (QueryFilter filter : this.filterList) {
            query.addFilterProperty(filter.getProperty(), filter.getOperator(), filter.getValue());
        }

        // build the generated string
        generatedString = this.queryJsonBuilder.getQueryJsonPayloadString(query);

        // test
        assertNotNull(generatedString);
        assertTrue(generatedString.length() > 0);
         // TODO - tested in postman query; will need to do this using JSONObject
//        assertEquals(referenceString, generatedString);

        // print for test copy
        System.out.println(generatedString);
    }
}

