package org.broadinstitute.mpg.diabetes.metadata.visitor;

import org.broadinstitute.mpg.diabetes.metadata.DataSet;
import org.broadinstitute.mpg.diabetes.metadata.MetaDataRoot;
import org.broadinstitute.mpg.diabetes.metadata.Property;
import org.broadinstitute.mpg.diabetes.util.PortalConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by balexand on 8/19/2015.
 */
public class PropertyPerExperimentVisitor implements DataSetVisitor {
    private String propertyName;
    private Boolean recursivelyDescendSampleGroups;
    private List<Property> propertyList = new ArrayList<Property>();

    public PropertyPerExperimentVisitor(String propertyName, Boolean recursivelyDescendSampleGroups){
        this.propertyName = propertyName;
        this.recursivelyDescendSampleGroups = recursivelyDescendSampleGroups;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void visit(DataSet dataSet) {
        if (dataSet.getType().equals(PortalConstants.TYPE_PROPERTY_KEY)) {
            if (dataSet.getName().equalsIgnoreCase(this.propertyName)){
                this.propertyList.add((Property)dataSet);

            }
        } else {
            for (DataSet child:dataSet.getAllChildren()){
                if (this.recursivelyDescendSampleGroups){ // if told to descend then we will always try
                    child.acceptVisitor(this);
                }  else { // if we shouldn't do send, make sure our sample group isn't a child of a sample group
                    if (child.getType().equals(PortalConstants.TYPE_SAMPLE_GROUP_KEY)){
                        if (!child.getParent().getType().equals(PortalConstants.TYPE_SAMPLE_GROUP_KEY)) {
                            child.acceptVisitor(this);
                        }
                    } else {
                        child.acceptVisitor(this);
                    }
                }
            }
        }

    }

}
