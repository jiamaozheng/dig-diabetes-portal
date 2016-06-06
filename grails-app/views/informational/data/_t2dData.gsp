<script>
    var displaySelectedDataTypes = function() {
        var selectedDataType = $('#dataTypeSelector').val();

        if(selectedDataType == 'all') {
            $('.sampleGroup').show();
        } else {
            $('.sampleGroup[data-datatype="' + selectedDataType + '"]').show();
            $('.sampleGroup:not([data-datatype="' + selectedDataType + '"]').hide();
        }
    };

    $(document).ready(function() {
        // gather all the known data types
        var knownDataTypes = _.chain($('.sampleGroup')).map(function(sgPanel) {
            return $(sgPanel).attr('data-datatype');
        }).uniq().value();

        _.forEach(knownDataTypes, function(dataType) {
            var newOption = $('<option>').append(dataType).attr({value: dataType});
            $('#dataTypeSelector').append(newOption);
        });
    });
</script>

<div class="row" style="padding-top: 50px;">

    <div class="form-inline">
        <label>Data type:</label>
        <select id="dataTypeSelector" class="form-control" onchange="displaySelectedDataTypes()">
            <option value="all">Show all</option>
        </select>
        %{--<label>Case selection criteria:</label>--}%
        %{--<select class="form-control"><option>Show all</option><option>type 2 diabetes</option><option>coronary artery disease</option>--}%
        %{--</select>--}%

        %{--<div style="float:right; position:relative;">--}%
        %{--<label>Sort by:</label>--}%
        %{--<select class="form-control"><option>Sample number</option><option>Update date</option><option>coronary artery disease</option>--}%
        %{--</select></div>--}%
    </div>

    <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
        <g:each var="exp" in="${experiments}">
            <g:each var="sg" in="${exp.sampleGroups}">
                <div class="panel panel-default sampleGroup" data-datatype="${g.message(code: 'metadata.' + exp[0].technology)}">
                    <div class="panel-heading" role="tab" id="${sg[0].systemId}">
                        <p class="dataset-name">${ g.message(code: 'metadata.' + sg[0].getSystemId()) }
                            <span class="dataset-summary">
                            <span class="data-status-open-access">Open access</span> | %{-- {{ sequencing }} |--}% ${g.formatNumber(number: sg[0].getSubjectsNumber(), format: "###,###" )} | ${sg[0].getAncestry()}
                            </span>
                        </p>

                        <p class="dataset-info">
                            %{--<strong>Data status:</strong> {{ Open access }},--}%
                            <strong>Data type:</strong> ${g.message(code: 'metadata.' + exp[0].technology)}
                            %{--, <strong>Experiment type:</strong> {{ exome sequencing }}--}%<br />
                            <strong>Total number of samples:</strong> ${g.formatNumber(number: sg[0].getSubjectsNumber(), format: "###,###" )},
                            <strong>No. cases:</strong> ${g.formatNumber(number: sg[0].getCasesNumber(), format: "###,###" )},
                            <strong>No. controls:</strong> ${g.formatNumber(number: sg[0].getControlsNumber(), format: "###,###" )},
                            <strong>Ethnicity:</strong> ${sg[0].getAncestry()}
                        </p>
                        <h5 class="panel-title">
                            <a class="collapsed open-info" role="button" data-toggle="collapse" data-parent="#accordion" href="#${sg[0].systemId}Collapse" aria-expanded="true" aria-controls="${sg[0].systemId}Collapse">
                                Learn more >
                            </a>
                        </h5>
                    </div>
                    <div id="${sg[0].systemId}Collapse" class="panel-collapse collapse" role="tabpanel" aria-labelledby="${sg[0].systemId}">
                        <div class="panel-body">
                            <g:render template="data/${sg[0].systemId}" />
                        </div>
                    </div>
                </div>
            </g:each>
        </g:each>
    </div>
</div>