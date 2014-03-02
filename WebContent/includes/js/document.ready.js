$(document).ready( function() {
//	console.log( $('#currentAction').val() );
	if ( $('#currentAction').val() == 'RECORD' ) {
		$(function () {
   			$('#tab a[href="#record-tab"]').tab('show');
  		});
	}
	else if ( $('#currentAction').val() == 'BALANCE' ) {
		$(function () {
   			$("#tab a[href='#balance-tab']").tab('show');
  		});
	}
	else if ( $('#currentAction').val() == 'monthBalanceChart' ) {
		$(function () {
   			$('#tab a[href="#month-balance-chart-tab"]').tab('show');
  		});
	}
	else if ( $('#currentAction').val() == 'CATEGORY_LIMIT' ) {
		$(function() {
			$('#tab a[href="#category-limit-tab"]').tab('show');
		});
	} 
	else if ( $('#currentAction').val() == 'CATEGORY_DETAIL_QUERY' ) {
		$(function() {
			console.log("HERE");
			$('#tab a[href="category-item-detail-tab"]').tab('show');
		});
	}
	
	console.log( $('#currentAction').val() );
});

function checkSelectedItem( item ) {
	if ( item == '自訂' ) {
		var itemName = this.prompt("請輸入自訂項目名稱:");
		$('#record-select-item').after('<input type="text" name="itemName" id="itemName" value=' + itemName + '>');
	}
	else if ( item == '無花費' ) {
		$('#amount').attr('value', 0 );
	}
	
	if ( item != '自訂' ) {
		$('#itemName').remove();
	}
}