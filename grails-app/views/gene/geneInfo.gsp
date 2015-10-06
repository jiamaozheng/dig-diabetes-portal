<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="t2dGenesCore"/>
    <r:require modules="core"/>
    <r:require modules="geneInfo"/>
    <r:require modules="crossMap"/>
    <r:layoutResources/>
    <%@ page import="dport.RestServerService" %>

    <link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

    <!-- jQuery UI CSS -->
    <link rel="stylesheet" type="text/css"
          href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/themes/smoothness/jquery-ui.css"/>

    <!-- Font Awesome CSS -->
    <link rel="stylesheet" type="text/css"
          href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css"/>

    <!-- jQuery JS -->
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>

    <!-- Bootstrap JS - for demo only, NOT REQUIRED FOR IGV -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

    <!-- jQuery UI CSS -->
    <link rel="stylesheet" type="text/css"
          href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/themes/redmond/jquery-ui.css"/>

    <!-- Google fonts -->
    <link rel="stylesheet" type="text/css" href='//fonts.googleapis.com/css?family=PT+Sans:400,700'>
    <link rel="stylesheet" type="text/css" href='//fonts.googleapis.com/css?family=Open+Sans'>

    <!-- Font Awesome CSS -->
    <link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css">

    <!-- IGV CSS -->
    <link rel="stylesheet" type="text/css" href="//igv.org/web/beta/igv-beta.css">

    <!-- jQuery JS -->
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

    <!-- jQuery UI JS -->
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>

    <!-- Vendor JS -->
    <script type="text/javascript" src="../js/lib/igv/vendor/inflate.js"></script>
    <script type="text/javascript" src="../js/lib/igv/vendor/zlib_and_gzip.min.js"></script>
    <script type="text/javascript" src="../js/lib/igv/vendor/jquery.mousewheel.js"></script>

    <!-- IGV JS -->
    <script type="text/javascript" src="../js/igv-create.js"></script>
    <script type="text/javascript" src="../js/userFeedback.js"></script>
    <script type="text/javascript" src="../js/popover.js"></script>
    <script type="text/javascript" src="../js/browser.js"></script>
    <script type="text/javascript" src="../js/ideogram.js"></script>
    <script type="text/javascript" src="../js/windowSizePanel.js"></script>
    <script type="text/javascript" src="../js/referenceFrame.js"></script>
    <script type="text/javascript" src="../js/igv-canvas.js"></script>
    <script type="text/javascript" src="../js/genome.js"></script>
    <script type="text/javascript" src="../js/trackView.js"></script>

    <script type="text/javascript" src="../js/rulerTrack.js"></script>

    <script type="text/javascript" src="../js/feature/featureTrack.js"></script>
    <script type="text/javascript" src="../js/igv-color.js"></script>
    <script type="text/javascript" src="../js/igv-utils.js"></script>
    <script type="text/javascript" src="../js/parseUtils.js"></script>
    <script type="text/javascript" src="../js/fasta.js"></script>
    <script type="text/javascript" src="../js/sequenceTrack.js"></script>
    <script type="text/javascript" src="../js/wigTrack.js"></script>
    <script type="text/javascript" src="../js/dataloader.js"></script>
    <script type="text/javascript" src="../js/igv-exts.js"></script>
    <script type="text/javascript" src="../js/igvxhr.js"></script>
    <script type="text/javascript" src="../js/binary.js"></script>

    <script type="text/javascript" src="../js/gtex/eqtlTrack.js"></script>
    <script type="text/javascript" src="../js/gtex/gtexReader.js"></script>
    <script type="text/javascript" src="../js/gtex/gtex.js"></script>
    <script type="text/javascript" src="../js/gtex/immvarReader.js"></script>

    <script type="text/javascript" src="../js/bam/coverageMap.js"></script>
    <script type="text/javascript" src="../js/bam/bamReader.js"></script>
    <script type="text/javascript" src="../js/bam/bgzf.js"></script>
    <script type="text/javascript" src="../js/bam/bamSource.js"></script>
    <script type="text/javascript" src="../js/bam/bamTrack.js"></script>
    <script type="text/javascript" src="../js/bam/bamAlignment.js"></script>
    <script type="text/javascript" src="../js/bam/bamAlignmentRow.js"></script>
    <script type="text/javascript" src="../js/bam/bamIndex.js"></script>

    <script type="text/javascript" src="../js/ga4gh/ga4ghAlignmentReader.js"></script>
    <script type="text/javascript" src="../js/ga4gh/ga4ghVariantReader.js"></script>
    <script type="text/javascript" src="../js/ga4gh/ga4ghHelper.js"></script>

    <script type="text/javascript" src="../js/intervalTree.js"></script>
    <script type="text/javascript" src="../js/igv-constants.js"></script>

    <script type="text/javascript" src="../js/feature/featureCache.js"></script>
    <script type="text/javascript" src="../js/feature/featureSource.js"></script>
    <script type="text/javascript" src="../js/feature/featureParsers.js"></script>
    <script type="text/javascript" src="../js/feature/tribble.js"></script>
    <script type="text/javascript" src="../js/feature/featureFileReader.js"></script>

    <script type="text/javascript" src="../js/encode/encode.js"></script>
    <script type="text/javascript" src="../js/cursor/cursorTrack.js"></script>
    <script type="text/javascript" src="../js/cursor/cursorModel.js"></script>
    <script type="text/javascript" src="../js/cursor/cursor-ideogram.js"></script>

    <script type="text/javascript" src="../js/karyo/karyo.js"></script>
    <script type="text/javascript" src="../js/gwas/t2dVariantSource.js"></script>
    <script type="text/javascript" src="../js/gwas/gwasTrack.js"></script>

    <script type="text/javascript" src="../js/feature/segParser.js"></script>
    <script type="text/javascript" src="../js/feature/segTrack.js"></script>
    <script type="text/javascript" src="../js/variant/vcfParser.js"></script>
    <script type="text/javascript" src="../js/variant/variant.js"></script>

    <script type="text/javascript" src="../js/bigwig/bufferedReader.js"></script>
    <script type="text/javascript" src="../js/bigwig/bwBPTree.js"></script>
    <script type="text/javascript" src="../js/bigwig/bwReader.js"></script>
    <script type="text/javascript" src="../js/bigwig/bwRPTree.js"></script>
    <script type="text/javascript" src="../js/bigwig/bwSource.js"></script>
    <script type="text/javascript" src="../js/bigwig/bwTotalSummary.js"></script>

    <script type="text/javascript" src="../js/trackCore.js"></script>

    <script type="text/javascript" src="../js/trackMenuPopupDialog.js"></script>

    <script type="text/javascript" src="../js/colorpicker.js"></script>
    <script type="text/javascript" src="../js/dialog.js"></script>
    <script type="text/javascript" src="../js/dataRangeDialog.js"></script>

    <script type="text/javascript" src="../js/set.js"></script>

    <script src="../js/oauth/google.js"></script>
    <style type="text/css">
    .igv-app-icon-container {
        border: none;
    }

    .igv-app-icon-container {
        border: none;
    }
    </style>

    <style type="text/css">

    #trackList {

        font-family: 'PT Sans', sans-serif;
        font-size: small;
        font-weight: 400;
    }

    div#trackList > div, div#trackList > h3 {

        color: #444;
        margin-left: 48px;
        margin-top: 4px;
        margin-bottom: 4px;

        padding-left: 32px;

        width: 300px;
    }

    div#trackList > div:hover,
    div#trackList > div:focus,
    div#trackList > div:active {
        cursor: pointer;
        color: white;
        background-color: rgba(49, 106, 246, 1);
    }


    </style>
    <g:set var="restServer" bean="restServerService"/>
</head>

<body>
<div id="rSpinner" class="dk-loading-wheel center-block" style="display:none">
    <img src="${resource(dir: 'images', file: 'ajax-loader.gif')}" alt="Loading"/>
</div>
<script>
    var loading = $('#spinner').show();
    $.ajax({
        cache: false,
        type: "post",
        url: "../geneInfoAjax",
        data: {geneName: '<%=geneName%>'},
        async: true,
        success: function (data) {
            var variantsAndAssociationsTableHeaders = {
                hdr1: '<g:message code="gene.variantassociations.table.colhdr.1" default="data type" />',
                hdr2: '<g:message code="gene.variantassociations.table.colhdr.2" default="sample size" />',
                hdr3: '<g:message code="gene.variantassociations.table.colhdr.3" default="total variants" />',
                hdr4: '<g:message code="gene.variantassociations.table.colhdr.4a" default="genome wide" />' +
                        '<g:helpText title="gene.variantassociations.table.colhdr.4.help.header" placement="top" body="gene.variantassociations.table.colhdr.4.help.text" qplacer="2px 0 0 6px"/>' +
                        '<g:message code="gene.variantassociations.table.colhdr.4b" default="genome wide" />',
                hdr5: '<g:message code="gene.variantassociations.table.colhdr.5a" default="locus wide" />' +
                        '<g:helpText title="gene.variantassociations.table.colhdr.5.help.header" placement="top" body="gene.variantassociations.table.colhdr.5.help.text" qplacer="2px 0 0 6px"/>' +
                        '<g:message code="gene.variantassociations.table.colhdr.5b" default="locus wide" />',
                hdr6: '<g:message code="gene.variantassociations.table.colhdr.6a" default="nominal" />' +
                        '<g:helpText title="gene.variantassociations.table.colhdr.6.help.header" placement="top" body="gene.variantassociations.table.colhdr.6.help.text" qplacer="2px 0 0 6px"/>' +
                        '<g:message code="gene.variantassociations.table.colhdr.6b" default="nominal" />'
            };
            var variantsAndAssociationsPhenotypeAssociations = {
                significantAssociations: '<g:message code="gene.variantassociations.significantAssociations" default="variants were associated with"  args="[geneName]"/>',
                noSignificantAssociationsExist: '<g:message code="gene.variantassociations.noSignificantAssociations" default="no significant associations"/>'
            };
            var biologicalHypothesisTesting = {
                question1explanation: '<g:message code="gene.biologicalhypothesis.question1.explanation" default="explanation" args="[geneName]"/>',
                question1insufficient: '<g:message code="gene.biologicalhypothesis.question1.insufficientdata" default="insufficient data"/>',
                question1nominal: '<g:message code="gene.biologicalhypothesis.question1.nominaldifference" default="nominal difference"/>',
                question1significant: '<g:message code="gene.biologicalhypothesis.question1.significantdifference" default="significant difference"/>',
                question1significantQ: '<g:helpText title="gene.biologicalhypothesis.question1.significance.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.biologicalhypothesis.question1.significance.help.text"/>'
            };
            var variantsAndAssociationsRowHelpText = {
                genomeWide: '<g:message code="gene.variantassociations.table.rowhdr.gwas" default="gwas"/>',
                genomeWideQ: '<g:helpText title="gene.variantassociations.table.rowhdr.gwas.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.variantassociations.table.rowhdr.gwas.help.text"/>',
                exomeChip: '<g:message code="gene.variantassociations.table.rowhdr.exomeChip" default="gwas"/>',
                exomeChipQ: '<g:helpText title="gene.variantassociations.table.rowhdr.exomeChip.help.header"  qplacer="2px 0 0 6px" placement="right" body="gene.variantassociations.table.rowhdr.exomeChip.help.text"/>',
                sigma: '<g:message code="gene.variantassociations.table.rowhdr.sigma" default="gwas"/>',
                sigmaQ: '<g:helpText title="gene.variantassociations.table.rowhdr.sigma.help.header"  qplacer="2px 0 0 6px" placement="right" body="gene.variantassociations.table.rowhdr.sigma.help.text"/>',
                exomeSequence: '<g:message code="gene.variantassociations.table.rowhdr.exomeSequence" default="gwas"/>',
                exomeSequenceQ: '<g:helpText title="gene.variantassociations.table.rowhdr.exomeSequence.help.header" qplacer="2px 0 0 6px" placement="right"  body="gene.variantassociations.table.rowhdr.exomeSequence.help.text"/>'
            };
            continentalAncestryText = {
                continentalAA: '<g:message code="gene.continentalancestry.title.rowhdr.AA" default="gwas"/>',
                continentalAAQ: '<g:helpText title="gene.continentalancestry.title.rowhdr.AA.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.title.rowhdr.AA.help.text"/>',
                continentalAAdatatype: '<g:message code="gene.continentalancestry.datatype.exomeSequence" default="exome sequence"/>' +
                        '<g:helpText title="gene.continentalancestry.datatype.exomeSequence.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.datatype.exomeSequence.help.text"/>',
                continentalEA: '<g:message code="gene.continentalancestry.title.rowhdr.EA" default="gwas"/>',
                continentalEAQ: '<g:helpText title="gene.continentalancestry.title.rowhdr.EA.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.title.rowhdr.EA.help.text"/>',
                continentalEAdatatype: '<g:message code="gene.continentalancestry.datatype.exomeSequence" default="exome sequence"/>',
                continentalSA: '<g:message code="gene.continentalancestry.title.rowhdr.SA" default="gwas"/>',
                continentalSAQ: '<g:helpText title="gene.continentalancestry.title.rowhdr.SA.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.title.rowhdr.SA.help.text"/>',
                continentalSAdatatype: '<g:message code="gene.continentalancestry.datatype.exomeSequence" default="exome sequence"/>',
                continentalEU: '<g:message code="gene.continentalancestry.title.rowhdr.EU" default="gwas"/>',
                continentalEUQ: '<g:helpText title="gene.continentalancestry.title.rowhdr.EU.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.title.rowhdr.EU.help.text"/>',
                continentalEUdatatype: '<g:message code="gene.continentalancestry.datatype.exomeSequence" default="exome sequence"/>',
                continentalHS: '<g:message code="gene.continentalancestry.title.rowhdr.HS" default="gwas"/>',
                continentalHSQ: '<g:helpText title="gene.continentalancestry.title.rowhdr.HS.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.title.rowhdr.HS.help.text"/>',
                continentalHSdatatype: '<g:message code="gene.continentalancestry.datatype.exomeSequence" default="exome sequence"/>',
                continentalEUchip: '<g:message code="gene.continentalancestry.title.rowhdr.chipEU" default="gwas"/>',
                continentalEUchipQ: '<g:helpText title="gene.continentalancestry.title.rowhdr.chipEU.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.title.rowhdr.chipEU.help.text"/>',
                continentalEUchipDatatype: '<g:message code="gene.continentalancestry.datatype.exomeChip" default="exome chip"/>' +
                        '<g:helpText title="gene.continentalancestry.datatype.exomeChip.help.header" qplacer="2px 0 0 6px" placement="right" body="gene.continentalancestry.datatype.exomeChip.help.text"/>'

            };
            mpgSoftware.geneInfo.fillTheGeneFields(data,
                    ${show_gwas},
                    ${show_exchp},
                    ${show_exseq},
                    '<g:createLink controller="region" action="regionInfo" />',
                    '<g:createLink controller="trait" action="traitSearch" />',
                    '<g:createLink controller="variantSearch" action="gene" />',
                    {variantsAndAssociationsTableHeaders: variantsAndAssociationsTableHeaders,
                        variantsAndAssociationsPhenotypeAssociations: variantsAndAssociationsPhenotypeAssociations,
                        biologicalHypothesisTesting: biologicalHypothesisTesting,
                        variantsAndAssociationsRowHelpText: variantsAndAssociationsRowHelpText,
                        continentalAncestryText: continentalAncestryText}
            );
            $('[data-toggle="popover"]').popover({
                animation: true,
                html: true,
                template: '<div class="popover" role="tooltip"><div class="arrow"></div><h5 class="popover-title"></h5><div class="popover-content"></div></div>'
            });
            $(".pop-top").popover({placement: 'top'});
            $(".pop-right").popover({placement: 'right'});
            $(".pop-bottom").popover({placement: 'bottom'});
            $(".pop-left").popover({ placement: 'left'});
            $(".pop-auto").popover({ placement: 'auto'});
            loading.hide();
        },
        error: function (jqXHR, exception) {
            loading.hide();
            core.errorReporter(jqXHR, exception);
        }
    });
</script>

<div id="main">

<div class="container">

<div class="gene-info-container">
<div class="gene-info-view">

    <h1>
        <em><%=geneName%></em>
    </h1>



    <g:if test="${(geneName == "C19ORF80") ||
            (geneName == "PAM") ||
            (geneName == "HNF1A") ||
            (geneName == "SLC16A11") ||
            (geneName == "SLC30A8") ||
            (geneName == "WFS1")}">
        <div class="gene-summary">
            <div class="title"><g:message code="gene.header.geneSummary" default="Curated summary"/>
            <g:helpText title="gene.header.geneSummary.help.header" placement="right"
                        body="gene.header.geneSummary.help.text"/>
            </div>

            <div id="geneHolderTop" class="top">
                <script>
                    var contents = '<g:renderGeneSummary geneFile="${geneName}-top"></g:renderGeneSummary>';
                    $('#geneHolderTop').html(contents);
                </script>

            </div>

            <div class="bottom ishidden" id="geneHolderBottom" style="display: none;">
                <script>
                    var contents = '<g:renderGeneSummary geneFile="${geneName}-bottom"></g:renderGeneSummary>';
                    $('#geneHolderBottom').html(contents);
                    function toggleGeneDescr() {
                        if ($('#geneHolderBottom').is(':visible')) {
                            $('#geneHolderBottom').hide();
                            $('#gene-summary-expand').html('click to expand');
                        } else {
                            $('#geneHolderBottom').show();
                            $('#gene-summary-expand').html('click to collapse');
                        }
                    }
                </script>

            </div>
            <a class="boldlink" id="gene-summary-expand" onclick="toggleGeneDescr()">
                <g:message code="gene.header.clickToExpand" default="click to expand"/>
            </a>
        </div>
    </g:if>

    <p>
        <g:helpText title="gene.header.uniprotSummary.help.header" placement="right"
                    body="gene.header.uniprotSummary.help.text"/>
        <span id="uniprotSummaryGoesHere"></span>
    </p>


    <div class="accordion" id="accordion2">
        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion2"
                   href="#collapseOne" aria-expanded="true">
                    <h2><strong><g:message code="gene.variantassociations.title"
                                           default="Variants and associations"/></strong></h2>
                </a>
            </div>

            <div id="collapseOne" class="accordion-body collapse">
                <div class="accordion-inner">
                    <g:render template="variantsAndAssociations"/>
                </div>
            </div>
        </div>


        <g:if test="${1}">

            <div class="separator"></div>
            <g:render template="/widgets/gwasRegionSummary"
                      model="['phenotypeList': phenotypeList, 'regionSpecification': regionSpecification]"/>

        </g:if>
        <g:if test="${1}">

            <div class="separator"></div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion2"
                       href="#collapseIgv">
                        <h2><strong><g:message code="gene.igv.title" default="Explore variants with IGV"/></strong></h2>
                    </a>
                </div>

                <div id="collapseIgv" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="../trait/igvBrowser"/>
                    </div>
                </div>
            </div>

            <script>
                $('#accordion2').on('shown.bs.collapse', function (e) {
                    if (e.target.id === "collapseIgv") {
                        <g:renderT2dGenesSection>
                        %{--igvLauncher.launch("#myDiv", "${geneName}","${restServer.currentRestServer()}",[1,1,1,0]);--}%
                        </g:renderT2dGenesSection>

                    }

                });
                $('#collapseOne').collapse({hide: true})
            </script>

        </g:if>
        <script>
            $('#collapseOne').collapse({hide: true})
        </script>





        <g:if test="${show_exseq}">

            <div class="separator"></div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle  collapsed" data-toggle="collapse" data-parent="#accordion2"
                       href="#collapseTwo">
                        <h2><strong><g:message code="gene.continentalancestry.title"
                                               default="variation across continental ancestry"/></strong></h2>
                    </a>
                </div>

                <g:render template="variationAcrossContinents"/>

            </div>

        </g:if>

        <g:if test="${show_exseq}">

            <div class="separator"></div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle  collapsed" data-toggle="collapse" data-parent="#accordion2"
                       href="#collapseThree">
                        <h2><strong><g:message code="gene.biologicalhypothesis.title"
                                               default="variation across continental ancestry"/></strong></h2>
                    </a>
                </div>

                <div id="collapseThree" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="biologicalHypothesisTesting"/>
                    </div>
                </div>
            </div>

        </g:if>


        <div class="separator"></div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle  collapsed" data-toggle="collapse" data-parent="#accordion3"
                   href="#collapseFour">
                    <h2><strong><g:message code="gene.burdenTesting.title" default="Run a burden test"/></strong></h2>
                </a>
            </div>

            <div id="collapseFour" class="accordion-body collapse">
                <div class="accordion-inner">
                    <g:render template="burdenTest"/>
                </div>
            </div>
        </div>


        <div class="separator"></div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle  collapsed" data-toggle="collapse" data-parent="#accordion2"
                   href="#findOutMore">
                    <h2><strong><g:message code="gene.findoutmore.title" default="find out more"/></strong></h2>
                </a>
            </div>

            <div id="findOutMore" class="accordion-body collapse">
                <div class="accordion-inner">
                    <g:render template="findOutMore"/>
                </div>
            </div>
        </div>

    </div>
</div>
</div>
</div>

</div>

</body>
</html>

