/*
 *
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved. *
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. *
 *
 * <OSCAR TEAM>
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
let ecgaProblems = [];
let ecgaMedications = [];
let efi_scores = [];

/*
	@field_name => Lower cased, with underscore
	@value => Value of the input

	Most of the values will be automatically valid, because there are set options in the inputs.
	However, for text inputs, the users can input anything, so they need to be manually verified.
*/
function isValidValue(field_name, value) {
	if (!value) {
		return false
	}

	if (field_name === 'moca') {
		try {
			const _value = parseInt(value)
			return _value >= 0 && _value <= 30
		} catch (e) {
			return false
		}
	}

	if (field_name === 'mini-cog') {
		try {
			const _value = parseInt(value)
			return _value >= 0 && _value <= 5
		} catch (e) {
			return false
		}
	}

	if (field_name === 'fast') {
		try {
			const _value = parseInt(value)
			return _value >= 0 && _value <= 7
		} catch (e) {
			return false
		}
	}

	if (field_name === 'falls_number') {
		try {
			const _value = parseInt(value)
			return _value >= 0 && _value <= 99
		} catch (e) {
			return false
		}
	}

	if (field_name === 'five_times_sit_to_stand_time') {
		try {
			const _value = parseInt(value)
			return _value >= 0 && _value <= 5
		} catch (e) {
			return false
		}
	}

	if (field_name === 'num_problem' || field_name === 'num_medication') {
		try {
			const _value = parseInt(value)
			return _value >= 0 && _value <= 99
		} catch (e) {
			return false
		}
	}

	return true
}

function getValue(type, field_name) {
	let value = undefined
	if (type === 'radio') {
		const $element = $("input[name='" + field_name + "']")
		if ($element.length === 0) {
			return null
		}
		value = $("input[name='" + field_name + "']:checked").val()
	} else if (field_name === 'fast') {
		value = $("select[name='" + field_name + "']").val()
	} else {
		value = $("input[name='" + field_name + "']").val()
	}
	if (value === undefined || value === '') {
		return null
	}
	return value;
}

function getMandatory() {
	const results = [];
	ECGA_MANDATORY.map(m => {
		const name = m.field_name.toLowerCase().replace(/\s/g, "_")
		let  $element = $("input[name='" + name + "']")
		if (name === 'fast') {	
			$element = $("#fast")	
		}

		if ($element.length === 0) {
		} else {
			const temp = Object.assign({}, m)
			temp.value = getValue(m.field_type.toLowerCase(), name)
			results.push(temp)
		}
	})
	return results;
}

function getIncompleteMandatory() {
	const mandatory = getMandatory();

	return mandatory.filter(r => r.value === null)
}

function getCompleteMandatory() {
	const mandatory = getMandatory();
	return mandatory.filter(r => r.value !== null)
}

function getInvalidMandatory() {
	const filled = getCompleteMandatory();
	return filled.filter(f => {
		const name = f.field_name.toLowerCase().replace(/\s/g, "_")
		return !isValidValue(name, f.value)
	})
}

function checkMandatory(options) {
	const NUM_MANDATORY = ECGA_MANDATORY.length;
	const filled = getCompleteMandatory();
	let empty = getIncompleteMandatory();
	let invalid = getInvalidMandatory();


	let isInvalid = false
	let isOverThreshold = false

	filled.map(f => {
		const name = f.field_name.toLowerCase().replace(/\s/g, "_")
		const $element = $('[data-field-name=' + name + ']');
		highlight($element[0], 'transparent')
	})

	invalid.map(f => {
		const name = f.field_name.toLowerCase().replace(/\s/g, "_")
		const $element = $('[data-field-name=' + name + ']');		
		highlight($element[0], 'red')
	})

	function handleEdgeCase() {	
		const moca = $('input[name="moca"]').val()	
		const mini_cog = $('input[name="mini-cog"]').val()
		if (moca) {	
			empty = empty.filter(e => e.field_name.toLowerCase() !== 'mini-cog')	
			invalid = invalid.filter(i => i.field_name.toLowerCase() !== 'mini-cog')	
		} else if (mini_cog) {	
			empty = empty.filter(e => e.field_name.toLowerCase() !== 'moca')	
			invalid = invalid.filter(i => i.field_name.toLowerCase() !== 'moca')				
		}	
	}	
	handleEdgeCase();

	if (invalid.length > 0) {
		isInvalid = true
	}

	const percent = (filled.length / NUM_MANDATORY) * 100
	const threshold = 80
	if (percent >= threshold) {
		isOverThreshold = true
		if (empty.length === 0) {}
	} else {
		highlightLabels(empty, 'lightpink')
	}

	if (options && options.showDialog === false) {
		return
	}

	let message = ''
	if (!isOverThreshold) {
		message += '<div>Please complete the eCGA mandatory fields.</div>'
	}
	if (isInvalid) {
		message += '<div>These values do not appear to be correct. Please check that you have entered the correct values.</div>'
	}
	if (message) {
		displayMessage('danger', message)
	}

	return isOverThreshold

}


function highlightLabels(data, color) {
	return data.map(d => {
		const name = d.field_name.toLowerCase().replace(/\s/g, "_")
		const $element = $('[data-field-name=' + name + ']')
		if ($element.length > 0) {
			highlight($element[0], color)
		}
	})
}

function highlight(element, color) {
	if (element) {
		element.style.backgroundColor = color
	}

}

// Two ways to assign a label, for attribute, or the element exists inside label.
function getLabel(name) {
	let $label = $('label[for="' + name + ']')
	if ($label.length === 0) {
		const $element = $("input[name='" + name + "']")
		$label = $element.closest('label')
	}
	if (!$label || !$label.length === 0) {
		return null
	}
	return $label
}

function displayMessage(type, text) {
	alert = '<div class="alert alert-' + type + ' alert-static alert-dismissable fade in"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + text + '</div>';

	$('body').append(alert);

	//close alert if user dosen't do it manually
	setTimeout(function () {
		$(".alert").alert('close');
	}, 5000);

}

function saveForm() {
	const $form = $("#formeCARES");
	const url = $("#formeCARES").attr('action') + "?method=save";
	const jsonForm = $form.serializeJSON()
	jsonForm.medications = ecgaMedications
	jsonForm.problems = ecgaProblems
	var request = {
		formData: JSON.stringify(jsonForm)
	}
	return $.ajax({
		type: 'POST',
		url: url,
		dataType: "json",
		data: request,
	})
}


$('.save').click(function () {

	saveForm().done(function (data) {
		if (data.saved) {
			displayMessage('success', '<span class="glyphicon glyphicon-ok-circle"></span> All information entered on the eCGA has been saved.');
		} else {
			displayMessage('danger', '<span class="glyphicon glyphicon-times"></span> There was a problem during save. Please call support.');
		}
	}).fail(() => {
		displayMessage('danger', '<span class="glyphicon glyphicon-times"></span> There was a problem during save. Please call support.');
	});

});

$('.exit').click(function () {
	checkMandatory({
		showDialog: false
	})
	const emptyMandatory = getIncompleteMandatory();
	if (emptyMandatory.length > 0) {
		$('#myModal').modal('show');
	}

});

$('.print').click(function () {
	window.print();
});

$('.tt').tooltip({
	container: 'body'
});

$('circle').click(function () {
	if ($(this).data('demo')) {
		return false;
	}

	if ($(this).css('fill') === 'rgb(255, 255, 255)') {
		$(this).parent().next('.action-required').prop('checked', true);
		$(this).css({
			fill: 'black'
		});
	} else {
		$(this).parent().next('.action-required').prop('checked', false);
		$(this).css({
			fill: 'white'
		});
	}
});



function confirmExit() {
	return "All information entered on the eCGA has been saved. The eCGA is not complete, are you sure you want to close?";
}

$('#chartModal').on('shown.bs.modal', function () {
	renderGraph();
});

function renderGraph() {

	const line = efi_scores.map(s => {
		const date = s.date
		return [
			date.time, parseFloat(s.score)
		]
	})



	if (!line || !Array.isArray(line) || line.length === 0) {
		$("#chartModal .modal-body").html("<h5>The Frailty Index has not been calculated for this encounter. Please click Calculate Frailty Index.</h5>")
		return
	}

	$.jqplot('chart', [line], {
		animate: true,
		title: 'Trending Measure',
		axes: {
			xaxis: {
				renderer: $.jqplot.DateAxisRenderer
			}
		},
		series: [{
			lineWidth: 4,
			markerOptions: {
				style: 'filledCircle'
			}
		}]
	});
}

$(document).ready(function ($) {
	audit();
	handleFallsNumber();
	const contextPath = $("#contextPath").html();
	const formId = $('input[name="formId"]').val();
	const demographicNo = $('input[name="demographicNo"]').val();
	const appointmentNo = $('input[name="appointmentNo"]').val();

    $("#ticklerModal").find(".modal-body").load(contextPath + "/formeCARES.do?method=getTickler&demographicNo=" + demographicNo);

	$(".tickler").on('click', function() {
        //populate tickler Encounter note field with unresolved data points.
        var note = ""
        const checked =  $('.action-required:checkbox:checked');

        if(checked.length > 0){
            note = ['eCGA Form Actions Required:'];
            checked.each(function(key, value){
                note.push("\t" + $(value).next().text().trim());
            })
        }

		const widget = $("#ticklerModal").modal("show");
		const comment = (note+"").replaceAll('  ', '').replaceAll('\n', '').replaceAll(',', '\n').trim();
		widget.find('.modal-title').text('Create Tickler');
		widget.find("input[name='message']").val("eCGA Form Actions Required");
		widget.find("textarea[name='comments']").val(comment);
	})

	$(".export").on('click', function() {
		console.log("Export")
		window.open(contextPath + "/formeCARES.do?method=export&demographicNo=" + demographicNo +"&formId=" + formId)
	})

	$('.date').datepicker({
		format: 'yyyy-mm-dd',
		autoclose: true
	});
	$(".date").datepicker("setDate", new Date());

	$(".closeWindowButton").on('click', function () {
		window.close();
	})

	getSavedData(contextPath, demographicNo, appointmentNo, formId).done((data) => {
		console.log('Load Data', data)
		for (let prop in data) {
			if (prop === 'efi_scores') {
				efi_scores = data[prop]
			}
			load(prop, data[prop])
		}
	})

	$(".save-tickler").on('click', function() {
        const $form = $("#ticklerAddForm");
        if(validateTickler($form)){
            saveTickler($form).done((data) => {
                console.log("Save Ticker", data);
                if(data.saved)
                {
                    displayMessage('success', 'Successfully saved Tickler')
                    $("input[name='ticklerId']").val(data.id);
                    $("#ticklerModal").modal("hide");
                    saveForm();
                }
                else
                {
                    displayMessage('danger', data.error)
                }
            }).fail(() => {
                console.log('failed')
                displayMessage('danger', 'Failed To Save Tickler')
            })
		}
    })

    $("#completed").change(function(){
        if($(this).val() === "true") {
            checkMandatory({
                showDialog: false
            })
           	const emptyMandatory = getIncompleteMandatory();
           	if (emptyMandatory.length > 0) {
           		displayMessage('danger', 'Mandatory fields missing. Cannot complete form.')
           		$(this).val("false");
           	} else {
           	    $('#myModal').modal('show');
           	}
        }
    })

	$(".calculate").click((e) => {
		e.preventDefault();
		saveForm();
		const isOverThreshold = checkMandatory();
		if (isOverThreshold) {
			calculate();
		}
	})

});

function getTickler(contextPath) {
	return $.get(contextPath + "/formeCARES.do?method=getTickler&demographicNo=" + demographicNo);
}

function createTickler(contextPath) {
	return $.get(contextPath + "/formeCARES.do?method=createTickler")
}

function saveTickler($form) {
	const contextPath = $("#contextPath").html();
    const jsonForm = $form.serializeJSON();
    var request = {
        tickler: JSON.stringify(jsonForm)
    }
    return $.ajax({
        type: 'POST',
        url: contextPath + "/formeCARES.do?method=createTickler",
        dataType: "json",
        data: request,
    })
}

function validateTickler($tickler) {
    var valid = true;
    $tickler.find("[required]").each(function(key,element){
        console.log($(element).val())
        console.log($(element).val().length < 1);
        if($(element).val().length < 1) {
            $(element).addClass("is-invalid");
            valid = false;
        }
        else {
            $(element).removeClass("is-invalid");
        }
    })
    return valid;
}

function getSavedData(contextPath, demographicNo, appointmentNo, formId) {
	return $.getJSON(contextPath + "/formeCARES.do?method=get&demographicNo=" + demographicNo + "&appointmentNo=" + appointmentNo +"&formId=" + formId)
}

function handleFallsNumber() {
	$('input[name="falls"]').on('change', function() {
		const value = $(this).val()
		if (value === '0') {
			$('input[name="falls_number"]').val(0)
		} else {
			$('input[name="falls_number"]').val('')
		}
	})	
}

function calculate() {

	let deficitSum = 0;
	let N = 0;
	let numProblems = 0;
	
	let ECGA_SCORE_FILTERED = ECGA_SCORE	
	function handleEdgeCase() {	
		const moca = $('input[name="moca"]').val()	
		const mini_cog = $('input[name="mini-cog"]').val()
		if (moca) {	
			ECGA_SCORE_FILTERED = ECGA_SCORE.filter(e => e.field_name.toLowerCase() !== 'mini-cog')	
		} else if (mini_cog) {	
			ECGA_SCORE_FILTERED = ECGA_SCORE.filter(e => e.field_name.toLowerCase() !== 'moca')	
		}	
	}	
	handleEdgeCase();

	ECGA_SCORE_FILTERED.map(d => {
		const name = d.field_name.toLowerCase().replace(/\s/g, "_")
		const field_type = d.field_type.toLowerCase();

		if (name === 'num_problem') {
			const $element = $("input[name='" + name + "']")
			const value = parseInt($element.val());
			if (value > 18) {
				numProblems += 18;
			} else {
				numProblems += value;
			}
			// N += 1;
		}

		if (name === 'num_medication') {
			const $element = $("input[name='" + name + "']")
			const value = parseInt($element.val());
			if(value == 0) {
				return;
			}
			console.log(name + ": ");
			if (value >= 8) {
				deficitSum += 1;
				console.log(1);
			} else if (value <= 7 && value >= 5) {
				deficitSum += 0.5;
				console.log(0.5);
			} else {
				deficitSum += 0;
				console.log(0);
			}
			// N += 1;
		}

		if (field_type === 'select') {
			const $element = $("select[name='" + name + "']:selected");
			const value = parseInt($element.val());
			if(! value) {
				return;
			}
			N += 1;
			console.log(name + ": ");
			switch(name) {
				case 'fast':
					if (value === 1 || value === 2) {
						deficitSum += 0;
						console.log(0);
					} else if (value === 3 || value === 4) {
						deficitSum += 0.5;
						console.log(0.5);
					} else {
						deficitSum += 1;
						console.log(1);
					}
					break;
			}
		}

		if (field_type === 'radio') {
			const $element = $("input[name='" + name + "']:checked")
			if ($element.length > 0) {
				const score = $element.data('score')
				if (score === undefined) {
					console.log("Undefined?", name)
				} else {
					deficitSum += score
					N += 1
				}
				console.log(name + ": ");
				console.log(score);
			}
		}

		if (field_type === 'textbox') {
			const $element = $("input[name='" + name + "']")
			const value = parseInt($element.val());
			console.log(name + ": ");
			if (value || value === 0) {
				N += 1;
				switch (name) {
					case 'mini-cog':
						if (value == 5) {
							deficitSum += 0;
							console.log(0);
						} else if (value == 4) {
							deficitSum += 0.33;
							console.log(0.33);
						} else if (value <= 3 && value >= 2) {
							deficitSum += 0.66;
							console.log(0.66);
						} else if (value <= 1) {
							deficitSum += 1;
							console.log(1);
						} else {
							deficitSum += 0;
							console.log(0);
						}
						break;
	
					case 'moca':
						if (value >= 25) {
							deficitSum += 0;
							console.log(0);
						} else if (value <= 24 && value >= 20) {
							deficitSum += 0.33;
							console.log(0.33);
						} else if (value <= 19 && value >= 11) {
							deficitSum += 0.66;
							console.log(0.66);
						} else if (value <= 10) {
							deficitSum += 1;
							console.log(1);
						} else {
							deficitSum += 0;
							console.log(0);
						}
						break;

					case 'falls_number':
						if (value === 1) {
							deficitSum += 0.5;
							console.log(0.5);
						} else if (value > 1) {
							deficitSum += 1;
							console.log(1);
						} else {
							deficitSum += 0;
							console.log(0);
						}

						break;
	
					case 'five_times_sit_to_stand_time':
						if (value <= 9) {
							deficitSum += 0;
							console.log(0);
						} else if (value <= 14 && value >= 10) {
							deficitSum += 0.5;
							console.log(0.5);
						} else {
							deficitSum += 1;
							console.log(1);
						}

						break;
				}

			}
			
		}
	})

	console.log("Problems: " + numProblems + " Deficit Score: " + deficitSum + " Deficits Selected: "
		+ N + " Percentage input: " + ((N/ECGA_SCORE.length)*100).toFixed(2));
	const score = (numProblems + deficitSum) / ( N + 18 )
	$('input[name="deficit_based_frailty_score"]').val(score); //.toFixed(2))
	
}

function load(field_name, value) {

	const skip = {
		formId: true,
		demographicNo: true
	}

    if (field_name === 'incompleteFormExists') {
        if(value) {
            const $myModal = $("#myModal").modal("show");
            $myModal.find(".modal-body").html("<p>An incomplete eCGA form already exists. Start a new form anyway?</p>");
            $myModal.find("button.continueButton").text("Continue");
            $myModal.find("button.closeWindowButton").text("Exit");
        }
    }

	if (skip[field_name] === true) {
		return
	}

	if (isPatientInfo(field_name)) {
		return loadPatientInfo(field_name, value)
	}

	if (isUserInfo(field_name)) {
		return loadUserInfo(field_name, value)
	}

	if (field_name === 'problems' || field_name === 'medications') {
		loadInfo(field_name, value)
	}

	if (field_name === 'fast') {	
		loadSelect(field_name, value)
		completeForm(value);
	}

    if (field_name === 'completed') {
        loadSelect(field_name, value)
        completeForm(value)
    }

	if (field_name === 'education') {
		loadSelect(field_name, value)
		completeForm(value)
	}

	const data = getData(field_name)

	if (!data) {
		return
	}

	const field_type = data.field_type.toLowerCase()

	if (field_type === 'radio') {
		loadRadio(field_name, value)
	}
	if (field_type === 'textbox') {
		loadText(field_name, value)
	}
	if (field_type === 'textarea') {
		loadTextArea(field_name, value)
	}
	if (field_type ==='checkbox') {
		loadCheckbox(field_name, value)
	}
}

function completeForm(completed) {
    if(completed === "true") {
        $("fieldset").attr("disabled", true);
        $("input.save").attr("disabled", true);
        $("input.calculate").attr("disabled", true);
        $("input.tickler").attr("disabled", true);
    } else {
        $("fieldset").attr("disabled", false);
        $("input.save").attr("disabled", false);
        $("input.calculate").attr("disabled", false);
        $("input.tickler").attr("disabled", false);
    }
}

function loadSelect(field_name, value) {	
	const name = field_name.toLowerCase().replace(/\s/g, "_")	
	$("#" + name).val(value)	
}

function getData(field_name) {
	return ECGA_DICTIONARY_FILTERED.find(d => {
		const name = d.field_name.toLowerCase().replace(/\s/g, "_")
		return name === field_name
	})
}

function isPatientInfo(field_name) {
	return {
		patientFirstName: true,
		patientLastName: true,
		patientPHN: true,
		patientDOB: true,
		patientGender: true,
		patientAge: true,
		demographicNo: true
	} [field_name]
}

function isUserInfo(field_name) {
	return {
		userFirstName: true,
		userLastName: true,
		userFullName: true,
		userSignature: true
	} [field_name]
}

function loadUserInfo(field_name, value) {
	const $input = $('input[name="assessor"]');
	if (value) {
		$input.val(value)
	}
}

function loadInfo(field_name, value) {
	if (!Array.isArray(value)) {
		return
	}
	const num = value.filter(i => i.active === true).length
	if (field_name === 'problems') {
		$('input[name="num_problem"]').val(num)
	} else if (field_name === 'medications') {
		$('input[name="num_medication"]').val(num)
	}


	if (field_name === 'problems') {
		ecgaProblems = value
		loadProblems(field_name, value)
	} else if (field_name === 'medications') {
		ecgaMedications = value
		loadMedications(field_name, value)
	}
}

function loadProblems(field_name, value) {
	const $container = $(".problems-container");
	$container.empty();
	const list = document.createElement('ol')
	value.map(p => {
		const listItem = document.createElement('li')
		listItem.classList.add('info-listitem');
		listItem.innerHTML = '<span class="pd-none">Code: </span><strong>' + p.code + '</strong> <span class="pd-none">Description: </span><strong>' + (p.description || 'N/A') + '</strong> Date: ' + p.startdate
		list.appendChild(listItem)
		if (p.active === false) {
			listItem.classList.add('info-listitem-disabled')
		}

		const buttonContainer = document.createElement('div')
		buttonContainer.classList.add('pbtn-group')
		const disable = document.createElement('button')
		const enable = document.createElement('button')

		disable.classList.add("btn", "btn-default")
		enable.classList.add("btn", "btn-default")

		disable.innerHTML = 'Disable'
		enable.innerHTML = 'Enable'

		disable.addEventListener('click', (e) => {
			e.preventDefault();
			const data = ecgaProblems.find(ep => ep.id === p.id)
			data.active = false;
			loadInfo('problems', ecgaProblems)
		})
		enable.addEventListener('click', (e) => {
			e.preventDefault();
			const data = ecgaProblems.find(ep => ep.id === p.id)
			data.active = true;
			loadInfo('problems', ecgaProblems)
		})

		buttonContainer.appendChild(disable)
		buttonContainer.appendChild(enable)

		listItem.appendChild(buttonContainer)

		list.appendChild(listItem)
	})
	$container.html(list)
}

function loadMedications(field_name, value) {
	const $container = $(".medications-container");
	$container.empty();
	const list = document.createElement('ol')
	value.map(m => {
		const listItem = document.createElement('li')
		listItem.classList.add('info-listitem');
		if (m.active === false) {
			listItem.classList.add('info-listitem-disabled')
		}
		listItem.innerHTML = '<span class="pd-none">Prescription: </span><strong>' + m.prescription + '</strong> Date: ' + m.rxDate 

		const buttonContainer = document.createElement('div')
		const disable = document.createElement('button')
		disable.addEventListener('click', (e) => {
			e.preventDefault();
			const data = ecgaMedications.find(em => em.id === m.id)
			data.active = false;
			loadInfo('medications', ecgaMedications)
		})
		const enable = document.createElement('button')
		enable.addEventListener('click', (e) => {
			e.preventDefault();
			const data = ecgaMedications.find(em => em.id === m.id)
			data.active = true;
			loadInfo('medications', ecgaMedications)
		})
		disable.classList.add("btn", "btn-default")
		enable.classList.add("btn", "btn-default")

		disable.innerHTML = 'Disable'
		enable.innerHTML = 'Enable'

		buttonContainer.appendChild(disable)
		buttonContainer.appendChild(enable)
		buttonContainer.classList.add('pbtn-group')
		listItem.appendChild(buttonContainer)

		list.appendChild(listItem)
	})
	$container.html(list)
}

function loadText(field_name, value) {
	$("input[name='" + field_name + "']").val(value)
}

function loadTextArea(field_name, value) {
	$("textarea[name='" + field_name + "']").val(value)
}

function loadRadio(field_name, value) {
	$("input[name='" + field_name + "'][value='" + value + "']").prop('checked', true)
}

function loadPatientInfo(field_name, value) {
	$(".patient-info [data-field-name=" + field_name + "]").html(value)
}

/*
 * Only refresh if the parent window is formlist.jsp.
 */
function refreshParent() {
	if (window.opener != null && !window.opener.closed && window.opener.location.href.includes("formlist.jsp")) {
		window.opener.location.reload();
	}
}

window.addEventListener('beforeunload', function (e) {
	// If you prevent default behavior in Mozilla Firefox prompt will always be shown
	// Cancel the event
	e.preventDefault();
	refreshParent();
	// Chrome requires returnValue to be set
	e.returnValue = '';
});

function loadCheckbox(field_name, value) {
	if (value === 'on') {
		const $input = $('input[name="' + field_name + '"]').prop('checked', true)
		const $circle = $input.siblings("svg").find('circle')
		$circle.css({
			fill: 'black'
		});
	}
}


/*
 * Get all the elements and display the score value.
 */
function audit() {

	console.log("Active Elements: ");
	const elements = $("input[data-score]");
	let length = 0;
	let lastname = '';
	var activeElements = [];
	elements.each(function() {
		if($(this).attr('value')) {
			let name = $(this).attr('name');
			if(name !== lastname) {
				length++;
			}
			activeElements.push(name + " - " + $(this).attr('value') + ": " + $(this).data('score'));
			lastname = name;
		}
	})
	console.log(activeElements);
	/*
	 * 6 additional elements include:
	 * five_times_sit_to_stand_time
	 * falls_number
	 * fast
	 * moca | mini-cog
	 * problems
	 * medications
	 */
	console.log("Total Active Elements: " + (length + 6));

	console.log("Mandatory Elements: ");
	console.log(ECGA_SCORE);
	console.log("Total Mandatory Elements: " + ECGA_SCORE.length);

}