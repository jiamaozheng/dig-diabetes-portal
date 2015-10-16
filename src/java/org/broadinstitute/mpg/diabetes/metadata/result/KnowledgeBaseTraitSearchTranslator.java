package org.broadinstitute.mpg.diabetes.metadata.result;

import org.broadinstitute.mpg.diabetes.knowledgebase.result.PropertyValue;
import org.broadinstitute.mpg.diabetes.knowledgebase.result.Variant;
import org.broadinstitute.mpg.diabetes.metadata.Phenotype;
import org.broadinstitute.mpg.diabetes.util.PortalConstants;
import org.broadinstitute.mpg.diabetes.util.PortalException;
import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONObject;

import java.util.List;

/**
 * Concrete class to translate from the new getData result API to the old trait-search result API
 *
 */
public class KnowledgeBaseTraitSearchTranslator implements KnowledgeBaseResultTranslator {

    /**
     * translate getData result into trait-search expected result format
     *
     * @param variantList
     * @return
     */
    public JSONObject translate(List<Variant> variantList) throws PortalException {
        // local variables
        JSONArray jsonArray = null;
        JSONObject rootObject = new JSONObject();
        JSONObject tempObject = null;

        // create a variants array and add it to root
        jsonArray = new JSONArray();
        rootObject.put(PortalConstants.JSON_VARIANTS_KEY, jsonArray);

        // for each row, add in a json object with property key/value pairs and add it to the json array
        for (Variant variant: variantList) {
            tempObject = this.getTraitSearchJsonObject(variant);
            jsonArray.add(tempObject);
        }

        // if got here, no error, so indicate
        rootObject.put(PortalConstants.JSON_ERROR_KEY, false);

        // add in the number of records
        rootObject.put(PortalConstants.JSON_NUMBER_RECORDS_KEY, variantList.size());

        // return
        return rootObject;
    }

    /**
     * translate a getData variant result into the trait-search result API type
     *
     * @param variant
     * @return
     * @throws PortalException
     */
    protected JSONObject getTraitSearchJsonObject(Variant variant) throws PortalException {
        // local variables
        JSONObject jsonObject = new JSONObject();

        // for each property, add in a key/value property to the json object
        for (PropertyValue propertyValue: variant.getPropertyValues()) {
            if (propertyValue.getProperty().getVariableType().equals(PortalConstants.OPERATOR_TYPE_FLOAT)) {
                try {
                    jsonObject.put(propertyValue.getProperty().getName(), (propertyValue.getValue() == null ? null : Float.valueOf(propertyValue.getValue()).floatValue()));
                } catch (NumberFormatException exception) {
                    jsonObject.put(propertyValue.getProperty().getName(), null);
                }

            } else if (propertyValue.getProperty().getVariableType().equals(PortalConstants.OPERATOR_TYPE_INTEGER)) {
                try {
                    jsonObject.put(propertyValue.getProperty().getName(), (propertyValue.getValue() == null ? null : Integer.valueOf(propertyValue.getValue()).intValue()));
                } catch (NumberFormatException exception) {
                    jsonObject.put(propertyValue.getProperty().getName(), null);
                }

            } else if (propertyValue.getProperty().getVariableType().equals(PortalConstants.OPERATOR_TYPE_STRING)) {
                jsonObject.put(propertyValue.getProperty().getName(), propertyValue.getValue());

            } else {
                throw new PortalException("Got property value with incorrect property value type: " + propertyValue.getProperty().getVariableType());
            }

            // if the property is a phenotype property, then also add in the phenotype name as well as a 'trait'
            if (propertyValue.getProperty().getPropertyType().equals(PortalConstants.TYPE_PHENOTYPE_PROPERTY_KEY)) {
                Phenotype phenotype = (Phenotype)propertyValue.getProperty().getParent();
                jsonObject.put(PortalConstants.KEY_PHENOTYPE_FOR_TRAIT_SEARCH, phenotype.getName());
            }
        }

        // return
        return jsonObject;
    }
}