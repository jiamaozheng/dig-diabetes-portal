package org.broadinstitute.mpg

import groovy.json.JsonSlurper
import org.apache.juli.logging.LogFactory
import org.broadinstitute.mpg.diabetes.BurdenService
import org.broadinstitute.mpg.diabetes.MetaDataService
import org.broadinstitute.mpg.diabetes.metadata.SampleGroup
import org.broadinstitute.mpg.diabetes.util.PortalConstants
import org.broadinstitute.mpg.locuszoom.PhenotypeBean
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.web.servlet.support.RequestContextUtils

class GeneController {

    RestServerService restServerService
    GeneManagementService geneManagementService
    SharedToolsService sharedToolsService
    MetaDataService metaDataService
    private static final log = LogFactory.getLog(this)
    SqlService sqlService
    BurdenService burdenService
    WidgetService widgetService

    /***
     * return partial matches as Json for the purposes of the twitter typeahead handler
     * @return
     */
    def index() {
        String partialMatches = geneManagementService.partialGeneMatchesUsingStringLists(params.query,27)
        response.setContentType("application/json")
        render ("${partialMatches}")
    }

    /***
     * We want to be able to do a type ahead in a gene name only field
     * @return
     */
    def geneOnlyTypeAhead() {
        String partialMatches = geneManagementService.partialGeneOnlyMatches(params.query,27)
        response.setContentType("application/json")
        render ("${partialMatches}")
    }


    /***
     * display all information about a gene. This call displays only the core of the page -- the data all come back
     * with the Jace on
     * @return
     */
    def geneInfo() {
        String locale = RequestContextUtils.getLocale(request)
        String geneToStartWith = params.id
        LinkedHashMap savedCols = params.findAll{ it.key =~ /^savedCol/ }
        LinkedHashMap savedRows = params.findAll{ it.key =~ /^savedRow/ }
        String newVandAColumnName = "custom significance"
        String newVandAColumnPValue
        String newDatasetName
        String newDatasetRowName = ""
        String phenotype = "T2D"

        if (params.phenotypeChooser){
            phenotype = params.phenotypeChooser
        }

        // capture requests related to a new column
        if (params.pValue) {
            newVandAColumnPValue =  params.pValue
        }
        if (params.columnName) {
            newVandAColumnName =  params.columnName
        }
        if (params.dataSetChooser) {
            newDatasetName =  params.dataSetChooser // data set^ P value property name
        }
        if (params.newRowName) {
            newDatasetRowName =  params.newRowName
        }
        String phenotypeList = this.metaDataService?.urlEncodedListOfPhenotypes();
        String regionSpecification = null

        // added test for unit test error
        if (geneToStartWith != null) {
            regionSpecification = this.geneManagementService?.getRegionSpecificationForGene(geneToStartWith, 100000)
        }

        List <LinkedHashMap<String,String>> columnInformation = []
        // if we have saved values then use them, otherwise add the defaults
        if (savedCols.size()>0){
            savedCols.each{String key, String value->
                List <String> listOfProperties = value.tokenize("^")
                if (listOfProperties.size()>2) {
                    columnInformation << [name:listOfProperties[0], value:listOfProperties[1], count:listOfProperties[2]]
                }
            }
        } else { // no saved columns -- provide some defaults
            columnInformation << [name:'total variants', value:'1', count:'0']
            columnInformation << [name:'nominal', value:'0.05', count:'0']
            columnInformation << [name:'locus-wide', value:'0.00005', count:'0']
            columnInformation << [name:'genome-wide', value:'0.00000005', count:'0']
        }
        if (newVandAColumnPValue){
            String filteredNumericPValue = newVandAColumnPValue.replaceAll(/\s*x\s*10/,"e") // take out any references to 10x, which would match the representation and the table, so I guess it's
                                                                                            // a fair mistake for a novice.   replace with a legal numerical format
            try {
                new BigDecimal(filteredNumericPValue)
                columnInformation << [name:"${newVandAColumnName}", value:"${filteredNumericPValue}", count:'0']
            } catch (e){
                columnInformation << [name:"${newVandAColumnName}", value:"1", count:'0'] // wrong value, but at least we don't crash
            }
        }
        List <LinkedHashMap<String,String>> sortedColumnInformation = columnInformation.sort{a,b-> (b.value as Float)<=>(a.value as Float)}

        List<PhenotypeBean> lzOptions = getHailPhenotypeMap()

        if (geneToStartWith)  {
            String  geneUpperCase =   geneToStartWith.toUpperCase()
            LinkedHashMap geneExtent = sharedToolsService.getGeneExpandedExtent(geneToStartWith)
            render (view: 'geneInfo', model:[show_gwas:sharedToolsService.getSectionToDisplay (SharedToolsService.TypeOfSection.show_gwas),
                                             show_exchp:sharedToolsService.getSectionToDisplay (SharedToolsService.TypeOfSection.show_exchp),
                                             show_exseq:sharedToolsService.getSectionToDisplay (SharedToolsService.TypeOfSection.show_exseq),
                                             phenotypeList: phenotypeList,
                                             regionSpecification: regionSpecification,
                                             geneName:geneUpperCase,
                                             geneExtentBegin:geneExtent.startExtent,
                                             geneExtentEnd:geneExtent.endExtent,
                                             geneChromosome:geneExtent.chrom,
                                             columnInformation:sortedColumnInformation,
                                             phenotype:phenotype,
                                             locale:locale,
                                             lzOptions:lzOptions
            ] )
        }
     }

    /***
     * we've been asked to search, but we don't know what kind of string. Figure it out, and then
     * go to the right page.  gene info? Variant info? or punt, and re-display the main home page
     */
    def findTheRightDataPage () {
        String uncharacterizedString = params.id
        // Is our string a region?
        LinkedHashMap extractedNumbers =  restServerService.extractNumbersWeNeed(uncharacterizedString)
        if ((extractedNumbers)   &&
                (extractedNumbers["startExtent"])   &&
                (extractedNumbers["endExtent"])&&
                (extractedNumbers["chromosomeNumber"]) ){
            redirect(controller:'region',action:'regionInfo', params: [id: params.id])
            return
        }
        // It's not a region.  Is our string a gene?
        String possibleGene = params.id
        if (possibleGene){
            possibleGene = possibleGene.trim().toUpperCase()
        }
        Gene gene = Gene.retrieveGene(possibleGene)
        if (gene){
            redirect(controller:'gene',action:'geneInfo', params: [id: params.id])
            return
        }

        // KDUXTD-99: check to see if dbSnpId provided so that it gets past search box filter
        if (sharedToolsService.getRecognizedStringsOnly()!=0) {
            // once we have the variant database complete we can use this
            String inputString = params.id
            if (sqlService?.isDbSnpIdString(inputString)) {
                // search for the variant by dbSnpId and if found, return; if not found, drop down to below test (just in case for now)
                Variant variant = Variant.findByDbSnpId(inputString)
                if (variant) {
                    redirect(controller: 'variantInfo', action: 'variantInfo', params: [id: params.id])
                    return
                }
            }
        }

        // Is our string a variant?  Build an identifying string and test
        String canonicalVariant = sharedToolsService.createCanonicalVariantName(params.id)
        if (sharedToolsService.getRecognizedStringsOnly()!=0){ // once we have the variant database complete we can use this
            Variant variant = Variant.retrieveVariant(canonicalVariant)
            if (variant) {
                redirect(controller: 'variantInfo', action: 'variantInfo', params: [id: params.id])
                return
            } else {
                redirect(controller: 'home', action: 'portalHome', params: [id: params.id])
                return
            }
        } else {// for now we don't have to verify of variant's existence
            redirect(controller: 'variantInfo', action: 'variantInfo', params: [id: canonicalVariant])
            return
        }
        // this is an error condition -- we should never get here in the code
        log.error("why did we never finish parsing '${uncharacterizedString}'?")
        redirect(controller: 'home', action: 'portalHome', params: [id: params.id])
       return
    }

    /***
     * Get the information for the variants and association tables on the gene info page
     * @return
     */
    def genepValueCounts() {
        String geneToStartWith = params.geneName
        String phenotype = params.phenotype
        String dataset = params.dataset
        List<String> colSignificances = sharedToolsService.convertAnHttpList(params."colNames[]")

        List<Float> significanceValues = []
        for(String significance in colSignificances){
            try {
                significanceValues << Float.parseFloat(significance)
            } catch (ex) {
                log.error("nonnumeric significance value (${significance}) in genepValueCounts action in GeneController")
            }
        }

        JSONObject jsonObject =  restServerService.combinedVariantCountByGeneNameAndPValue ( geneToStartWith.trim().toUpperCase(), dataset, significanceValues, phenotype )

        // attach the translated name so that the client has access to it for sorting in the table
        jsonObject.translatedName = g.message(code: "metadata." + jsonObject.dataset, default: jsonObject.dataset)

        render(status:200, contentType:"application/json") {
            jsonObject
        }
    }


    /***
     * get the information needed for the Variants across Continental Ancestry table on the gene info page
     * @return
     */
    def geneEthnicityCounts (){
        String geneToStartWith = params.geneName
        List<String> rowNames = sharedToolsService.convertAnHttpList(params."rowNames[]")
        List<String> colSignificances = sharedToolsService.convertAnHttpList(params."colNames[]")
        List <LinkedHashMap<String,String>> rowMaps = []
        if (!rowNames)  {

            rowMaps  = []
            List<SampleGroup> fullListOfSampleGroups = sharedToolsService.listOfTopLevelSampleGroups( "", "MAF", ["ExSeq","ExChip","GWAS", "WGS"])
            for (SampleGroup sampleGroup in fullListOfSampleGroups){
                rowMaps << ["dataset":"${sampleGroup.systemId}","technology":"${metaDataService.getTechnologyPerSampleGroup(sampleGroup.systemId)}"]
            }

        } else {
            for (String oneName in rowNames){
                rowMaps << ["dataset":oneName,"technology":"${metaDataService.getTechnologyPerSampleGroup(oneName)}"]
            }
        }
        List <LinkedHashMap<String,String>> uniqueRowMaps = rowMaps.unique{ LinkedHashMap a,LinkedHashMap b -> a.dataset <=> b.dataset }

        List<Float> significanceValues = []
        for(String significance in colSignificances){
            try {
                significanceValues << Float.parseFloat(significance)
            } catch (ex) {
                log.error("nonnumeric significance value (${significance}) in genepValueCounts action in GeneController")
            }
        }
        significanceValues = significanceValues.sort()

        List <LinkedHashMap<String,String>> numericBounds = []
        numericBounds << ["lowerValue":0.0f,"higherValue":1.0f]
        numericBounds << ["lowerValue":0.0f,"higherValue":1.0f]
        numericBounds << ["lowerValue":0.05f,"higherValue":1.0f]
        numericBounds << ["lowerValue":0.0005f,"higherValue":0.05f]
        numericBounds << ["lowerValue":0.0f,"higherValue":0.0005f]


        JSONObject jsonObject =  restServerService.combinedEthnicityTable ( geneToStartWith.trim().toUpperCase(), uniqueRowMaps, numericBounds)
        render(status:200, contentType:"application/json") {
            [ethnicityInfo:jsonObject]
        }
    }


    /***
     * Returns the information necessary to fill the Biological Hypothesis Testing section of the
     * gene info page. NOTE: this is the only place in the entire application where we need to call
     * gene-info, which is otherwise a vestigial call from the old API.  We should probably find a way
     * to merge this call into the new API, or else embrace gene-info altogether, and be willing to use it
     * in other places as well.
     *
     * @return
     */
    def geneInfoAjax() {
        String geneToStartWith = params.geneName
        if (geneToStartWith)      {
            JSONObject jsonObject =  restServerService.retrieveGeneInfoByName (geneToStartWith.trim().toUpperCase())
            render(status:200, contentType:"application/json") {
                [geneInfo:jsonObject['gene-info']]
            }

        }
    }

    /***
     * This call supports the burden test on the gene info page
     * @return
     */
    def burdenTestAjax() {
        // log parameters received
        // Here are some example parameters, as they show up in the params variable
        // params.filterNum=="2" // value=id from burdenTestVariantSelectionOptionsAjax, or 0 if no selection was made (which is a legal choice)
        // params.mafOption=="1" // where 1->apply maf across all samples, 2->apply maf to each ancestry"
        // params.mafValue=="0.47" // where value= real number x (where 0 <= x <= 1), and x is the MAF you'll pass into the REST call"
        // params.geneName=="SLC30A8" // string representing gene name
        log.info("got parameters: " + params);

        // cast the parameters
        Boolean explicitlySelectSamples = false
        String geneName = params.geneName
        String dataSet = params.dataSet
        int variantFilterOptionId = (params.filterNum ? Integer.valueOf(params.filterNum) : 0);     // default to all variants if none given
        String burdenTraitFilterSelectedOption = (params.burdenTraitFilterSelectedOption ? params.burdenTraitFilterSelectedOption : PortalConstants.BURDEN_DEFAULT_PHENOTYPE_KEY);               // default ot t2d if none given
        int mafOption = (params.mafOption ? Integer.valueOf(params.mafOption) : 1);                 // 1 is default, 2 is different ancestries if specified
        Float mafValue = ((params.mafValue && !params.mafValue?.equals("NaN")) ? new Float(params.mafValue) : null);                      // null float if none specified

        // TODO - eventually create new bean to hold all the options and have smarts for double checking validity
        JSONObject result = this.burdenService.callBurdenTest(burdenTraitFilterSelectedOption, geneName, variantFilterOptionId, mafOption, mafValue, dataSet, explicitlySelectSamples);

        // send json response back
        render(status: 200, contentType: "application/json") {result}
    }



    def generateListOfVariantsFromFiltersAjax() {
        // log parameters received
        // Here are some example parameters, as they show up in the params variable
        // params.filterNum=="2" // value=id from burdenTestVariantSelectionOptionsAjax, or 0 if no selection was made (which is a legal choice)
        // params.mafOption=="1" // where 1->apply maf across all samples, 2->apply maf to each ancestry"
        // params.mafValue=="0.47" // where value= real number x (where 0 <= x <= 1), and x is the MAF you'll pass into the REST call"
        // params.geneName=="SLC30A8" // string representing gene name

        Boolean explicitlySelectSamples = false
        String geneName = params.geneName
        String dataSet = params.dataSet
        int variantFilterOptionId = (params.filterNum ? Integer.valueOf(params.filterNum) : 0);     // default to all variants if none given
        String burdenTraitFilterSelectedOption = (params.burdenTraitFilterSelectedOption ? params.burdenTraitFilterSelectedOption : PortalConstants.BURDEN_DEFAULT_PHENOTYPE_KEY);               // default ot t2d if none given
        int mafOption = (params.mafOption ? Integer.valueOf(params.mafOption) : 1);                 // 1 is default, 2 is different ancestries if specified
        Float mafValue = ((params.mafValue && !params.mafValue?.equals("NaN")) ? new Float(params.mafValue) : null);                      // null float if none specified

        // TODO - eventually create new bean to hold all the options and have smarts for double checking validity
        String codedVariantList = this.burdenService.generateListOfVariantsFromFilters(burdenTraitFilterSelectedOption, geneName, variantFilterOptionId, mafOption, mafValue, dataSet, explicitlySelectSamples);
//        String stringForVariantTable = "["+result.collect{org.broadinstitute.mpg.diabetes.knowledgebase.result.Variant v->"{\"name\":\"${v.getVariantId()}\"}"}.join(",")+"]"
        JsonSlurper slurper = new JsonSlurper()
        JSONObject sampleCallSpecifics = slurper.parseText(codedVariantList)
        // send json response back
        render(status: 200, contentType: "application/json") {variantInfo:sampleCallSpecifics}
    }







        /***
     * Get the contents for the filter drop-down box on the burden test section of the gene info page
     * @return
     */
    def burdenTestVariantSelectionOptionsAjax() {
        JSONObject jsonObject = this.burdenService.getBurdenVariantSelectionOptions()

        // send json response back
        render(status: 200, contentType: "application/json") {jsonObject}
    }

    /**
     * returns a list of phenotypes to select from for the LZ plot display
     *
     * @return
     */
    public static List<PhenotypeBean> getHailPhenotypeMap() {
        // local variables
        List<PhenotypeBean> beanList = new ArrayList<PhenotypeBean>();

        // build the phenotype list
        beanList.add(new PhenotypeBean(key: "T2D", name: "T2D", description: "Type 2 Diabetes", defaultSelected: true));
        beanList.add(new PhenotypeBean(key: "BMI_adj_withincohort_invn", name: "BMI", description: "Body Mass Index", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "LDL_lipidmeds_divide.7_adjT2D_invn", name: "LDL", description: "LDL Cholesterol", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "HDL_adjT2D_invn", name: "HDL", description: "HDL Cholesterol", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "logfastingInsulin_adj_invn", name: "FI", description: "Fasting Insulin", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "fastingGlucose_adj_invn", name: "FG", description: "Fasting Glucose", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "HIP_adjT2D_invn", name: "HIP", description: "Hip Circumference", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "WC_adjT2D_invn", name: "WC", description: "Waist Circumference", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "WHR_adjT2D_invn", name: "WHR", description: "Waist Hip Ratio", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "TC_adjT2D_invn", name: "TC", description: "Total Cholesterol", defaultSelected: false));
        beanList.add(new PhenotypeBean(key: "TG_adjT2D_invn", name: "TG", description: "Triglycerides", defaultSelected: false));
        return beanList
    }

    /**
     * method to serve LZ requests in the format needed
     *
     * @return
     */
    def getLocusZoom() {
        // local variables
        String jsonReturn;
        String chromosome = params.chromosome;
//        chromosome = "22"
        String startString = params.start;
//        startString = "29737203"
        String endString = params.end;
//        endString = "29937203"
        String phenotype = params.phenotype;
        int startInteger;
        int endInteger;
        String errorJson = "{\"data\": {}, \"error\": true}";

        // get the covariate variants
        List<String> conditionVariants
        if (params.conditionVariantId) {
            conditionVariants = params.list("conditionVariantId")
            log.info("got covariates: " + conditionVariants)
        }

        // log
        log.info("got LZ request with params: " + params);

        // log start
        Date startTime = new Date();

        // if have all the information, call the widget service
        try {
            startInteger = Integer.parseInt(startString);
            endInteger = Integer.parseInt(endString);

            if (chromosome != null) {
                jsonReturn = widgetService.getVariantJsonForLocusZoomString(chromosome, startInteger, endInteger, null, phenotype, conditionVariants);
            } else {
                jsonReturn = errorJson;
            }

            // log
            log.info("got LZ result: " + jsonReturn);

            // log end
            Date endTime = new Date();
            log.info("LZ call returned in: " + (endTime?.getTime() - startTime?.getTime()) + " milliseconds");

        } catch (NumberFormatException exception) {
            log.error("got incorrect parameters for LZ call: " + params);
            jsonReturn = errorJson;
        }

        // return
        render(jsonReturn)
    }
}
