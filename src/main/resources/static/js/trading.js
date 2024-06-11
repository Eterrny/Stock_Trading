$(document).ready(function () {
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    const tariffs = {
        'premium': 0.005,
        'pro': 0.01,
        'novice': 0.03
    };

    let userCommissionRate;
    let userBalance = parseFloat($('#userBalance').text());

    $.get("/current-user-tariff", function (data) {
        userCommissionRate = parseFloat(data) % 100;
        // console.log("User commission rate: " + userCommissionRate);
    });

    function checkTradingHours() {
        const now = new Date();
        const utcHour = now.getUTCHours();
        const mskHour = (utcHour + 3) % 24;

        if (mskHour >= 10 && mskHour < 22) {
            $('#stocksDetails').show();
            $('#closedMessage').hide();
        } else {
            $('#stocksDetails').hide();
            $('#closedMessage').show();
        }
    }

    checkTradingHours();
    setInterval(checkTradingHours, 60000);

    window.sortTable = function(fieldName) {
        let tableBody = $('#stocksTableBody');
        let rows = tableBody.find('tr').get();
        let header = $('.tableHeader[data-field-sort="' + fieldName + '"]');
        let sortDirection = header.data('sort-direction');

        rows.sort(function(a, b) {
            let A = $(a).find('td').eq(getColumnIndex(fieldName)).text().toUpperCase();
            let B = $(b).find('td').eq(getColumnIndex(fieldName)).text().toUpperCase();

            if (A < B) {
                return (sortDirection === 'asc') ? -1 : 1;
            }
            if (A > B) {
                return (sortDirection === 'asc') ? 1 : -1;
            }
            return 0;
        });

        $.each(rows, function(index, row) {
            tableBody.append(row);
        });

        // Стрелка для текущего столбца
        let newSortDirection = (sortDirection === 'asc') ? 'desc' : 'asc';
        header.data('sort-direction', newSortDirection);
        // Обновить стрелки
        $('.arrowSpan[data-sort-direction="desc"]').show();
        $('.arrowSpan[data-sort-direction="asc"]').show();
        header.find('.arrowSpan').show();
        header.find('.arrowSpan[data-sort-direction="' + newSortDirection + '"]').hide();
    };

    function getColumnIndex(fieldName) {
        switch(fieldName) {
            case 'companyName':
                return 0;
            case 'buyPrice':
                return 1;
            case 'sellPrice':
                return 2;
            case 'availableQuantity':
                return 3;
            default:
                return 0;
        }
    }

    $('.buy-btn').click(function () {
        const company = $(this).data('company');
        const row = $('#stocksTableBody').find('tr:contains(' + company + ')');
        const quantityCell = row.find('td').eq(3);
        const availableQuantity = parseInt(quantityCell.text());
        $('#buyModal').show();
        $('#overlay').show();
        $('#companyName').text(company);
        $('#quantityInput').attr('max', availableQuantity).val(1);
        const buyPrice = parseFloat($(this).closest('tr').find('td').eq(1).text());
        updateTotalCost(buyPrice);
    });

    $('.max-set').click(function () {
        $('#quantityInput').val($('#quantityInput').attr('max'));
        const buyPrice = parseFloat($('#stocksTableBody').find('td:contains(' + $('#companyName').text() + ')').next().text());
        updateTotalCost(buyPrice);
    });

    $('#quantityInput').on('input', function () {
        let qty = $('#quantityInput');
        if (qty.val() < 1) {
            qty.val(1);
        }
        const buyPrice = parseFloat($('#stocksTableBody').find('td:contains(' + $('#companyName').text() + ')').next().text());
        updateTotalCost(buyPrice);
    });

    $('#closeModal').click(function () {
        $('#buyModal').hide();
        $('#overlay').hide();
    });

    $('#buyForm').submit(function (event) {
        event.preventDefault();
        const quantity = $('#quantityInput').val();
        const buyPrice = parseFloat($('#stocksTableBody').find('td:contains(' + $('#companyName').text() + ')').next().text());
        const totalCost = buyPrice * quantity * (1 + userCommissionRate);

        if (userBalance >= totalCost) {
            $.ajax({
                url: '/buy-stock',
                type: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                },
                data: {
                    company: $('#companyName').text(),
                    quantity: quantity
                },
                success: function (response) {
                    showSuccessMessage(response);
                    userBalance -= totalCost;
                    $('#userBalance').text(userBalance.toFixed(2));
                    updateTable($('#companyName').text(), quantity)
                    $('#buyModal').hide();
                    $('#overlay').hide();
                },
                error: function (xhr, status, error) {
                    showError(xhr.responseText);
                }
            });
        } else {
            showError('Insufficient balance to complete the purchase.')
        }
    });

    function updateTotalCost(buyPrice) {
        const quantity = $('#quantityInput').val();
        const totalCost = buyPrice * quantity * (1 + userCommissionRate);
        $('#totalCost').text(totalCost.toFixed(2));
    }

    function updateTable(companyName, quantitySold) {
        const row = $('#stocksTableBody').find('tr:contains(' + companyName + ')');
        const quantityCell = row.find('td').eq(3);
        let availableQuantity = parseInt(quantityCell.text());
        availableQuantity -= quantitySold;
        if (availableQuantity <= 0) {
            row.remove();
        } else {
            quantityCell.text(availableQuantity);
        }
    }

    function showError(message) {
        const errorAlert = $('#errorAlert');
        errorAlert.html('<strong>Error:</strong> ' + message);
        errorAlert.fadeIn().delay(3000).fadeOut();
    }

    function showSuccessMessage(message) {
        const successMessage = $('#success');
        successMessage.html(message);
        successMessage.fadeIn().delay(3000).fadeOut();
    }
});