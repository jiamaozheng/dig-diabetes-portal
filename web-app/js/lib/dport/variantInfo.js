var mpgSoftware = mpgSoftware || {};

(function () {
    "use strict";

    mpgSoftware.variantInfo = (function () {

        // this defines the maximum places to show after the decimal place the box data
        var precision = 10;
        // the upper breakpoints for p-value significance
        var significanceBoundaries = {
            genomeWide: 5e-8,
            locusWide: 5e-4,
            nominal: 5e-2
        };
        // provide the color assignments for the phenotypes
        // if a phenotype is undefined, a random one is generated
        var colorCodeArray = {
            'Bipolar disorder':"#f0b",
            'BMI':"#8bd",
            'Cholesterol':"#3cc",
            'Chronic kidney disease':"#90f",
            'Coronary artery disease':"#fd0",
            'Diastolic blood pressure': '#63c',
            'eGFR-creat (serum creatinine)':"#a00",
            'eGFR-cys (serum cystatin C)':"#9f0",
            'Fasting glucose':"#FA9",
            'Fasting insulin':"#f00",
            'HbA1c':"#3d3",
            'HDL cholesterol':"#0a6",
            'Height':"#9b8",
            'Hip circumference': '#1d5',
            'HOMA-B':"#C6F",
            'HOMA-IR':"#0a6",
            'LDL cholesterol':"#77f",
            'Major depressive disorder':"#952",
            'Microalbuminuria':"#060",
            'Proinsulin levels':"#F55",
            'Schizophrenia':"#f6f",
            'Systolic blood pressure': '#1ea',
            'Triglycerides':"#aaf",
            'Two-hour glucose':"#609",
            'Two-hour insulin':"#ab6",
            'Type 2 diabetes':"#000",
            'Urinary albumin-to-creatinine ratio':"#0a6",
            'Waist circumference': '#428',
            'Waist-hip ratio': '#ea7'
        };

        var setVariantTitleAndSummary = function (varId, dbsnpId, chrom, pos, gene, closestGene) {
            // load all the headers
            // variantTitle defaults to dbsnpId, or if that's undefined, then varId
            var variantTitle = dbsnpId || varId;
            $('#variantTitle').append(variantTitle);
            $("[data-textfield='variantName']").append(variantTitle);
            $('#variantTitleInAssociationStatistics').append(variantTitle);
            $('#effectOnCommonProteinsTitle').append(variantTitle);
            $('#exomeDataExistsTheMinorAlleleFrequency').append(variantTitle);
            $('#populationsHowCommonIs').append(variantTitle);
            $('#exploreSurroundingSequenceTitle').append(variantTitle);

            // load the summary text
            $("#chromosomeNumber").append(chrom);
            $("#coordinateNumber").append(pos);
            if(gene && gene != 'Outside') {
                $('#closestToGeneInfo').hide();
                $('#geneNumber').append('<a href=../../gene/geneInfo/'+gene+'>' + gene + '</a>.');
            } else if (closestGene) {
                $('#inGeneInfo').hide();
                $('#geneNumber').append('<a href=../../gene/geneInfo/'+closestGene+'>' + closestGene + '</a>.');
            } else {
                // we don't have any gene info, so remove those spans
                $('#inGeneInfo').hide();
                $('#closestToGeneInfo').hide();
            }
        };

        var displayTranscriptSummaries = function(transcripts, variantSummaryText) {
            var transcriptTemplate = document.getElementById('transcriptSummaryTemplate').innerHTML;
            Mustache.parse(transcriptTemplate);
            _.each(transcripts, function(transcriptData, transcriptKey) {
                // pull out the nucleotide data -- first get rid of all the lower case letters,
                // then split on /
                // note that the transcript may not have codon data--if this is the case, don't
                // do any processing. the template parsing will not display the nucleotide lines if
                // the nucleotide variables are null
                var referenceNucleotide= null;
                var variantNucleotide = null;
                if(transcriptData.Codons) {
                    var trimmedNucleotides = transcriptData.Codons.replace(/[a-z]/g, '').split('/');
                    referenceNucleotide = trimmedNucleotides[0];
                    variantNucleotide = trimmedNucleotides[1];
                }
                var thisTranscriptSummary = Mustache.render(transcriptTemplate, {
                    transcriptName: transcriptKey,
                    referenceNucleotide: referenceNucleotide,
                    variantNucleotide: variantNucleotide,
                    proteinChange: transcriptData.Protein_change,
                    consequence: variantSummaryText[transcriptData.MOST_DEL_SCORE]
                });
                $('#transcriptSummaries').append(thisTranscriptSummary);
            });
        };

        var initializePage = function(data, variantToSearch, traitInfoUrl, restServer, variantSummaryText) {
            // this call loads the data for the disease burden, 'how common is this variant', and IGV
            // viewer components
            if ( typeof data !== 'undefined')  {
                fillTheFields(data, variantToSearch, traitInfoUrl, restServer);
            }

            var args = _.flatten([{}, data.variant.variants[0]]);
            var variantObject = _.merge.apply(_, args);

            setVariantTitleAndSummary(variantObject.VAR_ID,
                                        variantObject.DBSNP_ID,
                                        variantObject.CHROM,
                                        variantObject.POS,
                                        variantObject.GENE,
                                        variantObject.CLOSEST_GENE);

            displayTranscriptSummaries(variantObject.TRANSCRIPT_ANNOT, variantSummaryText);

            $('[data-toggle="popover"]').popover();

            $(".pop-top").popover({placement : 'top'});
            $(".pop-right").popover({placement : 'right'});
            $(".pop-bottom").popover({placement : 'bottom'});
            $(".pop-left").popover({ placement : 'left'});
            $(".pop-auto").popover({ placement : 'auto'});

            loading.hide();
        };
        
        /**
         * This function fills in the association boxes for the primary phenotype
         */
        var fillPrimaryPhenotypeBoxes = function(phenotype, datasetList, variantId, variantAssociationStrings, dataUrl) {
            // make the ajax call to get the data for this phenotype
            $.ajax({
                cache: false,
                type: 'get',
                url: dataUrl,
                data: {
                    variantId: variantId,
                    phenotype: phenotype,
                    datasets: JSON.stringify(datasetList)
                },
                async: true
            }).done(function(data, textStatus, jqXHR) {
                loading.hide();
                // this is the translated phenotype
                var displayName = data.displayName;

                // groupedByDataset has as keys dataset names, mapping to arrays
                // that contain all of the data--need to go through each array, and pull
                // each property into a new object
                var groupedByDataset = _.chain(data.pVals).groupBy('dataset').omit('common').value();

                var processedDatasets = [];
                _.each(groupedByDataset, function(arrayOfValues) {
                    var thisDataset = {};
                    _.each(arrayOfValues, function(property) {
                        thisDataset.dataset = property.dataset;     // this is the same for every property

                        thisDataset[property.meaning] = property.count
                    });
                    processedDatasets.push(thisDataset);
                });

                processedDatasets = _.sortBy(processedDatasets, 'p_value');

                // all of the dataset objects are now processed and ordered correctly
                // call renderAPhenotype to get all of the HTML we need
                var generatedHTML = renderAPhenotype(displayName, processedDatasets, 'primary', variantAssociationStrings);
                var rowId = generatedHTML.rowId;
                var phenotypeRow = generatedHTML.renderedRow;
                var associations = generatedHTML.associationsArray;
                // it's apparently easiest to attach the row HTML followed by attaching the boxes to the row,
                // than it is to make a new object from the row HTML, attach the boxes to that, and then attach
                // the object to the document
                $('#primaryPhenotype').append(phenotypeRow);
                _.forEach(associations, function(association) {
                    $('#' + rowId + '> ul').append(association);
                });

            }).fail(function(jqXHR, textStatus, errorThrown) {
                loading.hide();
                core.errorReporter(jqXHR, errorThrown);
            });
        };

        var fillOtherPhenotypeBoxes = function(datasetList, variantId, variantAssociationStrings, dataUrl) {
            // make the ajax call to get the data for each phenotype
            _.forEach(datasetList, function(datasets, phenotype) {
                $.ajax({
                    cache: false,
                    type: 'get',
                    url: dataUrl,
                    data: {
                        variantId: variantId,
                        phenotype: phenotype,
                        datasets: JSON.stringify(_.values(datasets))
                    },
                    async: true
                }).done(function(data, textStatus, jqXHR) {
                    // this is the translated phenotype
                    var displayName = data.displayName;

                    // groupedByDataset has as keys dataset names, mapping to arrays
                    // that contain all of the data--need to go through each array, and pull
                    // each property into a new object
                    var groupedByDataset = _.chain(data.pVals).groupBy('dataset').omit('common').value();

                    var processedDatasets = [];
                    _.each(groupedByDataset, function(arrayOfValues) {
                        var thisDataset = {};
                        _.each(arrayOfValues, function(property) {
                            thisDataset.dataset = property.dataset;     // this is the same for every property

                            thisDataset[property.meaning] = property.count
                        });
                        processedDatasets.push(thisDataset);
                    });

                    processedDatasets = _.sortBy(processedDatasets, 'p_value');

                    // check to see if any dataset has at least a nominal signficance
                    // reject those that don't. If none do, then don't display anything
                    // for this phenotype
                    var areThereAnySignificantDatasets = _.some(processedDatasets, function(dataset) {
                        return dataset.p_value <= significanceBoundaries.nominal;
                    });
                    if( ! areThereAnySignificantDatasets ) {
                        // no dataset has a p-value that is at least nominally significant,
                        // so don't display anything for this phenotype
                        return;
                    }

                    // all of the dataset objects are now processed and ordered correctly
                    // call renderAPhenotype to get all of the HTML we need
                    var generatedHTML = renderAPhenotype(displayName, processedDatasets, 'secondary', variantAssociationStrings);
                    var rowId = generatedHTML.rowId;
                    var phenotypeRow = generatedHTML.renderedRow;
                    var associations = generatedHTML.associationsArray;
                    // it's apparently easiest to attach the row HTML followed by attaching the boxes to the row,
                    // than it is to make a new object from the row HTML, attach the boxes to that, and then attach
                    // the object to the document
                    $('#otherTraitsSection').append(phenotypeRow);
                    _.forEach(associations, function(association) {
                        $('#' + rowId + '> ul').append(association);
                    });

                }).fail(function(jqXHR, textStatus, errorThrown) {
                    core.errorReporter(jqXHR, errorThrown);
                });
            });
        };

        /**
         * data should have the following keys:
         * { dataset, pValue, maf, beta } -- maf and beta are optional
         * boxSize must be "primary" or "secondary"
         */
        var createABox = function(data, boxSize, variantAssociationStrings) {
            // compute text colors and text and attach to data object
            //      note: if maf/beta are undefined, need to add an empty string or nbsp
            // parse template
            // fill template
            // return resulting html
            var boxTemplate = document.getElementById('boxTemplate').innerHTML;
            Mustache.parse(boxTemplate);

            // this is all the string processing needed to come up with the right
            // display text and colors
            var templateData = {
                backgroundColor: function() {
                    var thisPval = this.p_value;
                    if( thisPval <= significanceBoundaries.genomeWide ) {
                        return '#006633';
                    } else if ( thisPval > significanceBoundaries.genomeWide && thisPval <= significanceBoundaries.locusWide ) {
                        return '#7AB317'
                    } else if ( thisPval <= significanceBoundaries.nominal ) {
                        return '#9ED54C';
                    } else {
                        // this is &nbsp;
                        return 'white';
                    }
                },
                boxClass: function() {
                    // todo: finish implementing
                    // start this as an array so we can use .join(' ') later,
                    // instead of having to worry about parsing spaces correctly
                    var classStringArray = []
                    switch(boxSize) {
                        case 'primary':
                            classStringArray.push('info-box', 'normal-info-box');
                            break;
                        case 'secondary':
                            classStringArray.push('info-box', 'small-info-box');
                            break;
                    }

                    if(this.p_value > significanceBoundaries.nominal) {
                        classStringArray.push('not-significant-box');
                    }

                    return classStringArray.join(' ');
                },
                datasetAndPValueTextColor: function() {
                    // changes based on the background color
                    var thisPval = this.p_value;
                    if ( thisPval <= significanceBoundaries.locusWide ) {
                        return 'white'
                    } else {
                        return 'black';
                    }
                },
                pValueText: function() {
                    return 'p = ' + UTILS.parseANumber(this.p_value, precision);
                },
                pValueSignificance: function() {
                    var thisPval = this.p_value;
                    if( thisPval <= significanceBoundaries.genomeWide ) {
                        return variantAssociationStrings.genomeSignificance;
                    } else if ( thisPval > significanceBoundaries.genomeWide && thisPval <= significanceBoundaries.locusWide ) {
                        return variantAssociationStrings.locusSignificance;
                    } else if ( thisPval <= significanceBoundaries.nominal ) {
                        return variantAssociationStrings.nominalSignificance;
                    } else {
                        // this is &nbsp;
                        return '\u00a0';
                    }
                },
                mafTextColor: function() {
                    // todo: implement
                    // may change based on background color
                    return 'white';
                },
                mafTextBackgroundColor: function() {
                    if(this.MAF) {
                        return '#0066ff';
                    }
                    return 'transparent';
                },
                mafText: function() {
                    if( this.MAF ) {
                        return 'MAF = ' + UTILS.parseANumber(this.MAF, precision);
                    }
                    // this is &nbsp;
                    return '\u00a0';
                },
                betaTextColor: function() {
                    // todo: implement
                    // may change based on background color
                    return 'white';
                },
                betaTextBackgroundColor: function() {
                    if( this.beta_value ) {
                        return this.beta_value >= 0 ? '#3399ff' : '#ff6600';
                    }
                    return 'transparent';
                },
                betaText: function() {
                    if( this.beta_value ) {
                        var effectArrow = this.beta_value >= 0 ? '↑' : '↓';
                        return effectArrow + 'Effect = beta: ' + UTILS.parseANumber(this.beta_value, precision);
                    }
                    // this is &nbsp;
                    return '\u00a0';
                }
            };

            var dataToPassIn = {};
            _.assign(dataToPassIn, data, templateData);
            var renderedBox = Mustache.render(boxTemplate, dataToPassIn);
            return renderedBox;
        };

        /**
         * phenotype is a string with the display name of the phenotype
         * dataArray is an array of the objects that will be passed to createABox
         * displayType must be "primary" or "secondary"
         */
        var renderAPhenotype = function(phenotype, dataArray, displaySize, variantAssociationStrings) {
            // generate all the boxes with calls to createABox
            // parse template
            // fill template with phenotype name and color
            // attach boxes to resulting html
            // add to document

            var associationsArray = [];
            _.each(dataArray, function(dataset) {
                associationsArray.push(createABox(dataset, displaySize, variantAssociationStrings));
            });
            // the phenotype is the display name, which may have spaces and/or parens,
            // so convert it to something that can work as an HTML id
            var rowId = phenotype.replace(/[\s()]/g, '') + 'Row';

            var phenotypeRowTemplate = document.getElementById('phenotypeTemplate').innerHTML;
            Mustache.parse(phenotypeRowTemplate);
            var data = {
                phenotypeName: phenotype,
                phenotypeColor: function() {
                    // if there's color defined for this phenotype, return that
                    // otherwise, return a random 3-character hex value to use as the color
                    if(colorCodeArray[phenotype]) {
                        return colorCodeArray[phenotype];
                    }
                    // 4095 = 0xfff, so this just gets a random value between 000 and fff
                    return '#' + Math.floor((Math.random()*4095)).toString(16);
                },
                rowClass: function() {
                    switch(displaySize) {
                        case 'primary':
                            return 'normal-info-box-holder';
                        case 'secondary':
                            return 'small-info-box-holder';
                    }
                    return '';
                },
                rowId: rowId
            };
            var renderedRow = Mustache.render(phenotypeRowTemplate, data);

            return {
                rowId: rowId,
                renderedRow: renderedRow,
                associationsArray: associationsArray
            };
        };

        // --------------------------------

        var delayedHowCommonIsPresentation = {},
            delayedCarrierStatusDiseaseRiskPresentation = {},
            delayedBurdenTestPresentation = {},
            delayedIgvLaunch = {},
            externalCalculateDiseaseBurden,
            externalizeCarrierStatusDiseaseRisk,
            externalVariantAssociationStatistics,
            externalizeShowHowCommonIsThisVariantAcrossEthnicities,
            describeImpactOfVariantOnProtein
            ;

        /**
         * Given an array of objects mapFromApi, pull out the objects with keys
         * included in listOfFieldNames and put them into a new object. Note that
         * this assumes there are no duplicate keys in mapFromApi.
         * @param mapFromApi
         * @param listOfFieldNames
         */
        var retrieveFieldsByName = function (mapFromApi, listOfFieldNames) {
            // this is a result of needing to merge an array of objects--_.merge
            // doesn't process arrays in the way we need it
            var args = _.flatten([{}, mapFromApi]);
            var mergedObject = _.merge.apply(_, args);
            var toReturn = _.pick(mergedObject, listOfFieldNames);
            return toReturn;
        };

        var describeImpactOfVariantOnProtein = function (variant, variantTitle, impactOnProtein) {
            $('#effectOfVariantOnProtein').append(privateMethods.variantGenerateProteinsChooser(variant, variantTitle, impactOnProtein));
            UTILS.verifyThatDisplayIsWarranted(variant["_13k_T2D_TRANSCRIPT_ANNOT"], $('#variationInfoEncodedProtein'), $('#puntOnNoncodingVariant'));
        };

        /**
        * We need to encapsulate a bunch of methods in order to retain control of everything that's going on.
        * Therefore define a function and surface only those methods that absolutely need to be public.
        *
        * @type {{showPercentageAcrossEthnicities: showPercentageAcrossEthnicities,
        * fillHowCommonIsUpBarChart: fillHowCommonIsUpBarChart,
        * fillCarrierStatusDiseaseRisk: fillCarrierStatusDiseaseRisk,
        * showEthnicityPercentageWithBarChart: showEthnicityPercentageWithBarChart,
        * showCarrierStatusDiseaseRisk: showCarrierStatusDiseaseRisk,
        * variantGenerateProteinsChooserTitle: variantGenerateProteinsChooserTitle,
        * variantGenerateProteinsChooser: variantGenerateProteinsChooser,
        * fillUpBarChart: fillUpBarChart,
        * fillDiseaseRiskBurdenTest: fillDiseaseRiskBurdenTest}}
        */
        var privateMethods = (function () {
            var calculateSearchRegion = function (data) {
                    var searchBand = 50000;// 50 kb
                    var returnValue = "";
                    if (data) {
                        var variant = {};
                        var i;
                        for (i = 0; i < data.length; i++) {
                            if (typeof data[i]["CHROM"] !== 'undefined') {
                                variant["CHROM"] = data[i]["CHROM"];
                            }
                            if (typeof data[i]["POS"] !== 'undefined') {
                                variant["POS"] = data[i]["POS"];
                            }
                        }
                        if ((typeof variant["CHROM"] !== 'undefined') &&
                            (typeof variant["POS"] !== 'undefined')) { // an't do anything without chromosome number and sequence position
                            var chromosomeIdentifier = variant["CHROM"];  // String
                            var position = variant["POS"];// number
                            var beginPosition = Math.max(0, position - searchBand);
                            var endPosition = position + searchBand;
                            returnValue = "chr" + chromosomeIdentifier + ":" + beginPosition + "-" + endPosition;
                        }
                    }
                    return returnValue;
                },
                
                fillHowCommonIsUpBarChart = function (freqInformation) {
                    if ((typeof freqInformation !== 'undefined') &&
                        (freqInformation.length > 0)) {
                        var dataForBarChart = [];
                        var summaryChart = (freqInformation.length < 10);
                        var extraSmallChart = (freqInformation.length < 4);
                        var chartHeight = (summaryChart) ? 250 : 550;
                        var chartHeight = (extraSmallChart) ? 80 : chartHeight;
                        var useSmallText = (summaryChart) ? 0 : 1;
                        for (var i = 0; i < freqInformation.length; i++) {
                            var cohort = freqInformation[i];
                            var cohortInfo = cohort.level;
                            var cohortFields = cohortInfo.split("^");
                            var displayableCohortName = "unknown ancestry";
                            var translatedCohortName = "unknown ancestry";
                            if (cohortFields.length > 5) {
                                translatedCohortName = cohortFields [5];
                                displayableCohortName = cohortFields [4];
                            }
                            dataForBarChart.push({
                                value: cohort.count * 100,
                                position: i,
                                barname: displayableCohortName,
                                barsubname: '',
                                barsubnamelink: '',
                                inbar: '',
                                descriptor: ('(' + translatedCohortName + ')')
                            })
                        }
                        var sortedDataForBarChart = dataForBarChart.sort(function (a, b) {
                            if (a.barname > b.barname) {
                                return 1;
                            }
                            if (a.barname < b.barname) {
                                return -1;
                            }
                            if (a.value > b.value) {
                                return 1;
                            }
                            if (a.value < b.value) {
                                return -1;
                            }

                            return 0;
                        });
                        for (var i = 0; i < sortedDataForBarChart.length; i++) {
                            sortedDataForBarChart[i].position = i;
                        }
                        var allAlleleValues = freqInformation.map(function (obj) {
                            return obj.count;
                        });

                        var roomForLabels = 120;
                        var maximumPossibleValue = (_.max(allAlleleValues ) * 150);

                        var labelSpacer = 10;

                        var margin = {top: 20, right: 20, bottom: 0, left: 40},
                            width = 800 - margin.left - margin.right,
                            height = chartHeight - margin.top - margin.bottom;

                        var commonBarChart = baget.barChart('howCommonIsChart')
                            .width(width)
                            .height(height)
                            .margin(margin)
                            .roomForLabels(roomForLabels)
                            .maximumPossibleValue(maximumPossibleValue)
                            .labelSpacer(labelSpacer)
                            .smallDescriptorText(useSmallText)
                            .customBarColoring(0)
                            .dataHanger("#howCommonIsChart", sortedDataForBarChart);
                        d3.select("#howCommonIsChart").call(commonBarChart.render);
                        return commonBarChart;
                    }

                },
                
                fillCarrierStatusDiseaseRisk = function (homozygCase, heterozygCase, nonCarrierCase, homozygControl, heterozygControl, nonCarrierControl, carrierStatusImpact) {
                    if ((typeof homozygCase !== 'undefined')) {
                        var data3 = [
                                {
                                    value: 1,
                                    position: 1,
                                    barname: carrierStatusImpact.casesTitle,
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: '(' + carrierStatusImpact.designationTotal + ' ' + (+nonCarrierCase) + ')',
                                    inset: 1
                                },
                                {
                                    value: homozygCase,
                                    position: 2,
                                    barname: ' ',
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: '',
                                    legendText: carrierStatusImpact.legendTextHomozygous
                                },
                                {
                                    value: heterozygCase,
                                    position: 3,
                                    barname: '  ',
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: '',
                                    legendText: carrierStatusImpact.legendTextHeterozygous
                                },
                                {
                                    value: nonCarrierCase - (homozygCase + heterozygCase),
                                    position: 4,
                                    barname: '   ',
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: '',
                                    legendText: carrierStatusImpact.legendTextNoncarrier
                                },
                                {
                                    value: 1,
                                    position: 6,
                                    barname: carrierStatusImpact.controlsTitle,
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: '(' + carrierStatusImpact.designationTotal + ' ' + (nonCarrierControl) + ')',
                                    inset: 1
                                },
                                {
                                    value: homozygControl,
                                    position: 7,
                                    barname: '    ',
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: ''
                                },
                                {
                                    value: heterozygControl,
                                    position: 8,
                                    barname: '     ',
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: ''
                                },
                                {
                                    value: nonCarrierControl - (homozygControl + heterozygControl),
                                    position: 9,
                                    barname: '      ',
                                    barsubname: '',
                                    barsubnamelink: '',
                                    inbar: '',
                                    descriptor: ''
                                }

                            ],
                            roomForLabels = 20,
                            maximumPossibleValue = (Math.max(homozygCase, heterozygCase, nonCarrierCase, homozygControl, heterozygControl, nonCarrierControl) * 1.5),
                            labelSpacer = 10;

                        var margin = {top: 140, right: 20, bottom: 0, left: 0},
                            width = 800 - margin.left - margin.right,
                            height = 520 - margin.top - margin.bottom;

                        var barChart = baget.barChart('carrierStatusDiseaseRiskChart')
                            .selectionIdentifier("#carrierStatusDiseaseRiskChart")
                            .width(width)
                            .height(height)
                            .margin(margin)
                            .roomForLabels(roomForLabels)
                            .maximumPossibleValue(10000)
                            .labelSpacer(labelSpacer)
                            .assignData(data3)
                            .integerValues(1)
                            .logXScale(1)
                            .customBarColoring(1)
                            .customLegend(1)
                            .dataHanger("#carrierStatusDiseaseRiskChart", data3);
                        d3.select("#carrierStatusDiseaseRiskChart").call(barChart.render);
                        return barChart;
                    }

                },

                showEthnicityPercentageWithBarChart = function (ethnicityPercentages) {
                    var retainBarchartPtr;

                    // We have everything we need to build the bar chart.  Store the functional reference in an object
                    // that we can call whenever we want
                    delayedHowCommonIsPresentation = {
                        barchartPtr: retainBarchartPtr,
                        launch: function () {

                            retainBarchartPtr = fillHowCommonIsUpBarChart(ethnicityPercentages);

                            return retainBarchartPtr;
                        },
                        removeBarchart: function () {
                            if ((typeof retainBarchartPtr !== 'undefined') &&
                                (typeof retainBarchartPtr.clear !== 'undefined')) {
                                retainBarchartPtr.clear('howCommonIsChart');
                            }
                        }

                    }
                },

                showCarrierStatusDiseaseRisk = function (OBSU, OBSA, HOMA, HETA, HOMU, HETU, carrierStatusImpact) {
                    var heta = 1, hetu = 1, totalCases = 1,
                        homa = 1, homu = 1, totalControls = 1,
                        retainBarchartPtr;

                    heta = HETA;
                    hetu = HETU;
                    homa = HOMA;
                    homu = HOMU;
                    totalCases = OBSA;
                    totalControls = OBSU;


                    delayedCarrierStatusDiseaseRiskPresentation = {
                        barchartPtr: retainBarchartPtr,
                        launch: function () {
                            d3.select('#carrierStatusDiseaseRiskChart').select('svg').remove();
                            retainBarchartPtr = fillCarrierStatusDiseaseRisk(homa,
                                heta,
                                totalCases,
                                homu,
                                hetu,
                                totalControls,
                                carrierStatusImpact);
                            return retainBarchartPtr;
                        },
                        removeBarchart: function () {
                            if ((typeof retainBarchartPtr !== 'undefined') &&
                                (typeof retainBarchartPtr.clear !== 'undefined')) {
                                retainBarchartPtr.clear('carrierStatusDiseaseRiskChart');
                            }
                        }

                    }

                },

                variantGenerateProteinsChooser = function (variant, title, impactOnProtein) {
                    var retVal = "";
                    if (variant.MOST_DEL_SCORE && variant.MOST_DEL_SCORE < 4) {
                        retVal += "<p>" + impactOnProtein.chooseOneTranscript + "</p>";
                        var allKeys = Object.keys(variant._13k_T2D_TRANSCRIPT_ANNOT);
                        for (var i = 0; i < allKeys.length; i++) {
                            var checked = (i == 0) ? ' checked ' : '';
                            var annotation = variant._13k_T2D_TRANSCRIPT_ANNOT[allKeys[i]];
                            retVal += ("<div class=\"radio-inline\">\n" +
                            "<label>\n" +
                            "<input " + checked + " class='transcript-radio' type='radio' name='transcript_check' id='transcript-" + allKeys[i] +
                            "' value='" + allKeys[i] + "' onclick='UTILS.variantInfoRadioChange(" +
                            "\"" + annotation['PolyPhen_SCORE'] + "\"," +
                            "\"" + annotation['SIFT_SCORE'] + "\"," +
                            "\"" + annotation['Condel_SCORE'] + "\"," +
                            "\"" + annotation['MOST_DEL_SCORE'] + "\"," +
                            "\"" + annotation['_13k_ANNOT_29_mammals_omega'] + "\"," +
                            "\"" + annotation['Protein_position'] + "\"," +
                            "\"" + annotation['Codons'] + "\"," +
                            "\"" + annotation['Protein_change'] + "\"," +
                            "\"" + annotation['PolyPhen_PRED'] + "\"," +
                            "\"" + annotation['Consequence'] + "\"," +
                            "\"" + annotation['Condel_PRED'] + "\"," +
                            "\"" + annotation['SIFT_PRED'] + "\"" +
                            ")' >\n" +
                            allKeys[i] + "\n" +
                            "</label>\n" +
                            "</div>\n");
                        }
                        if (allKeys.length > 0) {
                            var annotation = variant._13k_T2D_TRANSCRIPT_ANNOT[allKeys[0]];
                            UTILS.variantInfoRadioChange(annotation['PolyPhen_SCORE'],
                                annotation['SIFT_SCORE'],
                                annotation['Condel_SCORE'],
                                annotation['MOST_DEL_SCORE'],
                                annotation['_13k_ANNOT_29_mammals_omega'],
                                annotation['Protein_position'],
                                annotation['Codons'],
                                annotation['Protein_change'],
                                annotation['PolyPhen_PRED'],
                                annotation['Consequence'],
                                annotation['Condel_PRED'],
                                annotation['SIFT_PRED']);

                        }


                    }
                    return retVal;
                },
                fillUpBarChart = function (peopleWithDiseaseNumeratorString, peopleWithDiseaseDenominatorString, peopleWithoutDiseaseNumeratorString, peopleWithoutDiseaseDenominatorString, diseaseBurdenStrings) {
                    var peopleWithDiseaseDenominator,
                        peopleWithoutDiseaseDenominator,
                        peopleWithDiseaseNumerator,
                        peopleWithoutDiseaseNumerator,
                        calculatedPercentWithDisease,
                        calculatedPercentWithoutDisease,
                        proportionWithDiseaseDescriptiveString,
                        proportionWithoutDiseaseDescriptiveString;
                    if ((typeof peopleWithDiseaseDenominatorString !== 'undefined') &&
                        (typeof peopleWithoutDiseaseDenominatorString !== 'undefined')) {
                        peopleWithDiseaseDenominator = parseInt(peopleWithDiseaseDenominatorString);
                        peopleWithoutDiseaseDenominator = parseInt(peopleWithoutDiseaseDenominatorString);
                        peopleWithDiseaseNumerator = parseInt(peopleWithDiseaseNumeratorString);
                        peopleWithoutDiseaseNumerator = parseInt(peopleWithoutDiseaseNumeratorString);
                        if (( peopleWithDiseaseDenominator !== 0 ) &&
                            ( peopleWithoutDiseaseDenominator !== 0 )) {
                            calculatedPercentWithDisease = (100 * (peopleWithDiseaseNumerator / (2 * peopleWithDiseaseDenominator)));
                            calculatedPercentWithoutDisease = (100 * (peopleWithoutDiseaseNumerator / (2 * peopleWithoutDiseaseDenominator)));
                            proportionWithDiseaseDescriptiveString = "(" + peopleWithDiseaseNumerator + " out of " + peopleWithDiseaseDenominator + ")";
                            proportionWithoutDiseaseDescriptiveString = "(" + peopleWithoutDiseaseNumerator + " out of " + peopleWithoutDiseaseDenominator + ")";
                            var dataForBarChart = [
                                    {
                                        value: calculatedPercentWithDisease,
                                        barname: diseaseBurdenStrings.controlBarName,
                                        barsubname: diseaseBurdenStrings.controlBarSubName,
                                        barsubnamelink: '',
                                        inbar: '',
                                        descriptor: proportionWithDiseaseDescriptiveString
                                    },
                                    {
                                        value: calculatedPercentWithoutDisease,
                                        barname: diseaseBurdenStrings.caseBarName,
                                        barsubname: diseaseBurdenStrings.caseBarSubName,
                                        barsubnamelink: '',
                                        inbar: '',
                                        descriptor: proportionWithoutDiseaseDescriptiveString
                                    }
                                ],
                                roomForLabels = 120,
                                maximumPossibleValue = (Math.max(calculatedPercentWithDisease, calculatedPercentWithoutDisease) * 1.5),
                                labelSpacer = 10;

                            var margin = {top: 0, right: 20, bottom: 0, left: 70},
                                width = 800 - margin.left - margin.right,
                                height = 150 - margin.top - margin.bottom;


                            var barChart = baget.barChart('diseaseRiskChart')
                                .selectionIdentifier("#diseaseRiskChart")
                                .width(width)
                                .height(height)
                                .margin(margin)
                                .roomForLabels(roomForLabels)
                                .maximumPossibleValue(maximumPossibleValue)
                                .labelSpacer(labelSpacer)
                                .assignData(dataForBarChart)
                                .dataHanger("#diseaseRiskChart", dataForBarChart);
                            d3.select("#diseaseRiskChart").call(barChart.render);
                            return barChart;
                        }
                    }
                },

                fillDiseaseRiskBurdenTest = function (OBSU, OBSA, MINA, MINU, PVALUE, ORVALUE, rootVariantUrl, diseaseBurdenStrings) {
                    var mina = 0,
                        minu = 0,
                        totalUnaffected = 0,
                        totalAffected = 0,
                        pValue = 0,
                        retainBarchartPtr,
                        oddsRatio;

                    mina = MINA;
                    minu = MINU;
                    totalUnaffected = OBSU;
                    totalAffected = OBSA;
                    pValue = PVALUE;
                    oddsRatio = ORVALUE;

                    // variables for bar chart
                    var numeratorUnaffected,
                        denominatorUnaffected,
                        numeratorAffected,
                        denominatorAffected;
                    if ((totalUnaffected) && (totalAffected)) {
                        numeratorUnaffected = minu;
                        numeratorAffected = mina;
                        denominatorUnaffected = totalUnaffected;
                        denominatorAffected = totalAffected;
                        delayedBurdenTestPresentation = {
                            functionToRun: fillUpBarChart,
                            barchartPtr: retainBarchartPtr,
                            launch: function () {
                                retainBarchartPtr = fillUpBarChart(numeratorUnaffected, denominatorUnaffected, numeratorAffected, denominatorAffected, diseaseBurdenStrings);
                                if (pValue > 0) {
                                    var degreeOfSignificance = '';
                                    // TODO the p's below are piling up.  clean them out
                                    $('#describePValueInDiseaseRisk').append("<p class='slimDescription'>" + degreeOfSignificance + "</p>\n" +
                                        "<p  id='bhtMetaBurdenForDiabetes' class='slimAndTallDescription'>p=" + (pValue.toPrecision(3)) +
                                        diseaseBurdenStrings.diseaseBurdenPValueQ + "</p>");
                                    if (typeof oddsRatio !== 'undefined') {
                                        $('#describePValueInDiseaseRisk').append("<p  id='bhtOddsRatioForDiabetes' class='slimAndTallDescription'>OR=" +
                                            UTILS.realNumberFormatter(oddsRatio) + diseaseBurdenStrings.diseaseBurdenOddsRatioQ + "</p>");
                                    }
                                }
                                return retainBarchartPtr;
                            },
                            removeBarchart: function () {
                                if ((typeof retainBarchartPtr !== 'undefined') &&
                                    (typeof retainBarchartPtr.clear !== 'undefined')) {
                                    retainBarchartPtr.clear('diseaseRiskChart');
                                }
                            }
                        };
                    }
                };


            return {
                // public routines
                calculateSearchRegion: calculateSearchRegion,
                showEthnicityPercentageWithBarChart: showEthnicityPercentageWithBarChart,
                showCarrierStatusDiseaseRisk: showCarrierStatusDiseaseRisk,
                variantGenerateProteinsChooser: variantGenerateProteinsChooser,
                fillDiseaseRiskBurdenTest: fillDiseaseRiskBurdenTest
            }


        }());  // end of private methods


        /***
         * Here is the main publicly available method in this module, which ends up driving, directly or indirectly, most
         * of the rest of the JavaScript code in this file. This method gets executed after the Ajax calls returns with the data.
         *
         *
         * @param data
         * @param variantToSearch
         * @param traitsStudiedUrlRoot
         * @param restServerRoot
         * @param showGwas
         * @param showExchp
         * @param showExseq
         */
        var variantPosition;

        function fillTheFields(data, variantToSearch, traitsStudiedUrlRoot, restServerRoot) {
            var variantObj = data['variant'];
            var variant = variantObj['variants'][0];
            var prepareDelayedIgvLaunch = function (variant, restServerRoot) {
                /***
                 * store everything we need to launch IGV
                 */
                var regionforIgv = privateMethods.calculateSearchRegion(variant);
                return {
                    rememberRegion: regionforIgv,
                    launch: function () {
                        igvLauncher.launch("#myVariantDiv", regionforIgv, restServerRoot, [1, 1, 1, 0]);
                    }
                };
            };
            var prepareIgvLaunch = function (variant, restServerRoot) {
                return {
                    locus: privateMethods.calculateSearchRegion(variant),
                    server: restServerRoot
                };
            };
            var calculateDiseaseBurden = function (OBSU, OBSA, MINA, MINU, HOMA, HETA, HOMU, HETU, PVALUE, ORVALUE, variantTitle, diseaseBurdenStrings) {// disease burden
                var weHaveEnoughDataForRiskBurdenTest;
                weHaveEnoughDataForRiskBurdenTest = (!UTILS.nullSafetyTest([OBSU, OBSA, MINA, MINU]));
                UTILS.verifyThatDisplayIsWarranted(weHaveEnoughDataForRiskBurdenTest, $('#diseaseRiskExists'), $('#diseaseRiskNoExists'));
                if (weHaveEnoughDataForRiskBurdenTest) {
                    privateMethods.fillDiseaseRiskBurdenTest(OBSU, OBSA, MINA, MINU, PVALUE, ORVALUE, null, diseaseBurdenStrings);
                }
            };
            // externalize!
            // externalize!
            externalCalculateDiseaseBurden = calculateDiseaseBurden;
            var howCommonIsThisVariantAcrossEthnicities = function (ethnicityPercentages) {// how common is this allele across different ethnicities
                var weHaveEnoughDataToDescribeMinorAlleleFrequencies = ((typeof ethnicityPercentages !== 'undefined') && (typeof ethnicityPercentages[0] !== 'undefined'));
                if (weHaveEnoughDataToDescribeMinorAlleleFrequencies) {
                    weHaveEnoughDataToDescribeMinorAlleleFrequencies = (!UTILS.nullSafetyTest([ethnicityPercentages[0].count]));
                }
                UTILS.verifyThatDisplayIsWarranted(weHaveEnoughDataToDescribeMinorAlleleFrequencies, $('#howCommonIsExists'), $('#howCommonIsNoExists'));
                if (weHaveEnoughDataToDescribeMinorAlleleFrequencies) {
                    privateMethods.showEthnicityPercentageWithBarChart(ethnicityPercentages);
                }
            };
            externalizeShowHowCommonIsThisVariantAcrossEthnicities = howCommonIsThisVariantAcrossEthnicities;
            var showHowCarriersAreDistributed = function (OBSU, OBSA, HOMA, HETA, HOMU, HETU, carrierStatusImpact) {// case control data set characterization
                var weHaveEnoughDataToCharacterizeCaseControls;
                weHaveEnoughDataToCharacterizeCaseControls = (!UTILS.nullSafetyTest([OBSU, OBSA, HOMA, HETA, HOMU, HETU]));
                UTILS.verifyThatDisplayIsWarranted(weHaveEnoughDataToCharacterizeCaseControls, $('#carrierStatusExist'), $('#carrierStatusNoExist'));
                if (weHaveEnoughDataToCharacterizeCaseControls) {
                    privateMethods.showCarrierStatusDiseaseRisk(OBSU, OBSA, HOMA, HETA, HOMU, HETU, carrierStatusImpact);
                }
            };
            externalizeCarrierStatusDiseaseRisk = showHowCarriersAreDistributed;
            var oldDescribeImpactOfVariantOnProtein = function (variant, variantTitle, impactOnProtein) {
                $('#effectOfVariantOnProteinTitle').append(privateMethods.variantGenerateProteinsChooserTitle(variant, variantTitle, impactOnProtein));
                $('#effectOfVariantOnProtein').append(privateMethods.variantGenerateProteinsChooser(variant, variantTitle, impactOnProtein));
                UTILS.verifyThatDisplayIsWarranted(variant["_13k_T2D_TRANSCRIPT_ANNOT"], $('#variationInfoEncodedProtein'), $('#puntOnNoncodingVariant'));
            };


            /***
             * the following top-level routines do all the work in fillTheFields
             */
            delayedIgvLaunch = prepareDelayedIgvLaunch(variant, restServerRoot);
            variantPosition = prepareIgvLaunch(variant, restServerRoot);

        };

        var retrieveDelayedHowCommonIsPresentation = function () {
                return delayedHowCommonIsPresentation;
            },
            retrieveDelayedCarrierStatusDiseaseRiskPresentation = function () {
                return delayedCarrierStatusDiseaseRiskPresentation;
            },
            retrieveDelayedBurdenTestPresentation = function () {
                return delayedBurdenTestPresentation;
            },
            retrieveCalculateDiseaseBurden = function () {
                return externalCalculateDiseaseBurden;
            },
            retrieveCarrierStatusDiseaseRisk = function () {
                return externalizeCarrierStatusDiseaseRisk;
            },
            retrieveVariantAssociationStatistics = function () {
                return externalVariantAssociationStatistics;
            },
            retrieveHowCommonIsThisVariantAcrossEthnicities = function () {
                return externalizeShowHowCommonIsThisVariantAcrossEthnicities;
            },
            retrieveDescribeImpactOfVariantOnProtein = function () {
                return describeImpactOfVariantOnProtein;
            },

            retrieveDelayedIgvLaunch = function () {
                return delayedIgvLaunch;
            },
            retrieveVariantPosition = function () {
                return variantPosition;
            };

        var firstResponders = {
        };

        return {
            // private routines MADE PUBLIC FOR UNIT TESTING ONLY (find a way to do this in test mode only)

            // public routines
            retrieveDescribeImpactOfVariantOnProtein: retrieveDescribeImpactOfVariantOnProtein,
            retrieveCalculateDiseaseBurden: retrieveCalculateDiseaseBurden,
            retrieveCarrierStatusDiseaseRisk: retrieveCarrierStatusDiseaseRisk,
            retrieveVariantAssociationStatistics: retrieveVariantAssociationStatistics,
            retrieveDelayedHowCommonIsPresentation: retrieveDelayedHowCommonIsPresentation,
            retrieveDelayedCarrierStatusDiseaseRiskPresentation: retrieveDelayedCarrierStatusDiseaseRiskPresentation,
            retrieveDelayedBurdenTestPresentation: retrieveDelayedBurdenTestPresentation,
            retrieveHowCommonIsThisVariantAcrossEthnicities: retrieveHowCommonIsThisVariantAcrossEthnicities,
            retrieveDelayedIgvLaunch: retrieveDelayedIgvLaunch,
            retrieveFieldsByName: retrieveFieldsByName,
            retrieveVariantPosition: retrieveVariantPosition,
            describeImpactOfVariantOnProtein: describeImpactOfVariantOnProtein,
            // ---------------------------------------
            firstReponders: firstResponders,
            fillPrimaryPhenotypeBoxes: fillPrimaryPhenotypeBoxes,
            fillOtherPhenotypeBoxes: fillOtherPhenotypeBoxes,
            renderAPhenotype: renderAPhenotype,
            initializePage: initializePage
        }


    }());


})();

