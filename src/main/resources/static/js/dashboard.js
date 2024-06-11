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
        return mskHour >= 10 && mskHour < 22;
    }

    window.sortTable = function (fieldName) {
        let tableBody = $('#stocksTableBody');
        let rows = tableBody.find('tr').get();
        let header = $('.tableHeader[data-field-sort="' + fieldName + '"]');
        let sortDirection = header.data('sort-direction');

        rows.sort(function (a, b) {
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

        $.each(rows, function (index, row) {
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
        switch (fieldName) {
            case 'companyName':
                return 0;
            case 'sellPrice':
                return 1;
            case 'availableQuantity':
                return 2;
            default:
                return 0;
        }
    }

    $('.sell-btn').click(function () {
        if (!checkTradingHours()) {
            showMessage("The trading platform is closed.<br>Working Hours: 10:00 - 22:00 (UTC+3)")
            return;
        }
        const company = $(this).data('company');
        const row = $('#stocksTableBody').find('tr:contains(' + company + ')');
        const quantityCell = row.find('td').eq(2);
        const availableQuantity = parseInt(quantityCell.text());
        $('#sellModal').show();
        $('#overlay').show();
        $('#stockName').text(company);
        $('#quantityInput').attr('max', availableQuantity).val(1);
        const sellPrice = parseFloat($(this).closest('tr').find('td').eq(1).text());
        updateTotalCost(sellPrice);
    });

    $('.max-set').click(function () {
        $('#quantityInput').val($('#quantityInput').attr('max'));
        const sellPrice = parseFloat($('#stocksTableBody').find('td:contains(' + $('#stockName').text() + ')').next().text());
        updateTotalCost(sellPrice);
    });

    $('#quantityInput').on('input', function () {
        let qty = $('#quantityInput');
        if (qty.val() < 1) {
            qty.val(1);
        }
        const sellPrice = parseFloat($('#stocksTableBody').find('td:contains(' + $('#stockName').text() + ')').next().text());
        updateTotalCost(sellPrice);
    });

    $('#closeModal').click(function () {
        $('#sellModal').hide();
        $('#overlay').hide();
    });

    $('#sellForm').submit(function (event) {
        event.preventDefault();
        const quantity = $('#quantityInput').val();
        const stockName = $('#stockName').text();

        const data = {
            stockName: stockName,
            quantity: quantity
        };

        $.ajax({
            url: '/dashboard/sell-stock',
            type: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: $.param(data),
            success: function (response) {
                showMessage(response);
                const sellPrice = parseFloat($('#stocksTableBody').find('td:contains(' + stockName + ')').next().text());
                const totalCost = sellPrice * quantity * (1 - userCommissionRate);
                userBalance += totalCost;
                $('#userBalance').text(userBalance.toFixed(2));
                updateTable(stockName, quantity);
                $('#sellModal').hide();
                $('#overlay').hide();
            },
            error: function (xhr, status, error) {
                showError(xhr.responseText);
            }
        });
    });


    function updateTotalCost(sellPrice) {
        const quantity = $('#quantityInput').val();
        const totalCost = sellPrice * quantity * (1 - userCommissionRate);
        $('#totalCost').text(totalCost.toFixed(2));
    }

    function updateTable(stockName, quantitySold) {
        const row = $('#stocksTableBody').find('tr:contains(' + stockName + ')');
        const quantityCell = row.find('td').eq(2);
        let availableQuantity = parseInt(quantityCell.text());
        availableQuantity -= quantitySold;
        if (availableQuantity <= 0) {
            row.remove();
        } else {
            $('#quantityInput').attr('max', availableQuantity)
            quantityCell.text(availableQuantity);
        }
    }

    function showError(message) {
        const errorAlert = $('#errorAlert');
        errorAlert.html('<strong>Error:</strong> ' + message);
        errorAlert.fadeIn().delay(3000).fadeOut();
    }

    function showMessage(message) {
        const successMessage = $('#success');
        successMessage.html(message);
        successMessage.fadeIn().delay(3000).fadeOut();
    }
});