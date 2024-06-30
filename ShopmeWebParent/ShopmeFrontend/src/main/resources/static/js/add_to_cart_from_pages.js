



$(document).ready(function() {

	$(".addToCart").on("click", function(e) {
		e.preventDefault();
		addToCartFromPages($(this));

	});
	handleDefaultDetailLinkClick();

});


function handleDetailLinkClick(cssClass, modalId) {
	$(cssClass).on("click", function(e) {
		e.preventDefault();
		linkDetailURL = $(this).attr("href");
		$(modalId).modal("show").find(".modal-content").load(linkDetailURL);
	});
}

function handleDefaultDetailLinkClick() {
	handleDetailLinkClick(".link-detail", "#detailModal");
}


function addToCartFromPages(link) {

	var productId = link.attr("productId");
	var quantityInput = $("#quantity" + productId);;

	var newQuantity = parseInt(quantityInput.val()) + 1;

	productURL = link.attr("href");
	url = contextPath + productURL

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		if (response.includes("You must")) {
			showModalDialog("Shopping Cart", response);
			return false;
		} else {
			if (newQuantity <= 5) {
				quantityInput.val(newQuantity);
				showModalDialog("Shopping Cart", response);
				totalCartItem();

			} else {
				showWarningModal('Maximum quantity is 5');
			}
		}



	}).fail(function() {
		showErrorModal("Error while adding product to shopping cart.");
	});


}
