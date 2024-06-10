$(document).ready(function () {

    //$('.arrowSpan[data-sort-direction="desc"]').hide();

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

        // $('.tableHeader[data-field-sort]').data('sort-direction', 'asc');
        // if (sortDirection === 'asc') {
        //     $('.tableHeader[data-field-sort="' + fieldName + '"]').data('sort-direction', 'desc');
        // } else {
        //     $('.tableHeader[data-field-sort="' + fieldName + '"]').data('sort-direction', 'asc');
        // }
        //

        // // обновить стрелки
        // $('arrowSpan').show();
        // $('.arrowSpan[data-sort-direction="' + (sortDirection === 'asc' ? 'desc' : 'asc') + '"]').hide();

        // Стрелка для текущего столбца
        let newSortDirection = (sortDirection === 'asc') ? 'desc' : 'asc';
        header.data('sort-direction', newSortDirection);
        // Обновить стрелки
        $('.arrowSpan[data-sort-direction="desc"]').show();
        $('.arrowSpan[data-sort-direction="asc"]').show();
        header.find('.arrowSpan').show();
        header.find('.arrowSpan[data-sort-direction="' + newSortDirection + '"]').hide();


        // // Сбросить направление сортировки для всех заголовков
        // $('.tableHeader[data-field-sort]').data('sort-direction', 'asc');
        // // Установить правильное направление сортировки для текущего заголовка
        // header.data('sort-direction', (sortDirection === 'asc' ? 'desc' : 'asc'));
        //
        // // Обновить стрелки
        // $('.arrowSpan').hide();
        // header.find('.arrowSpan[data-sort-direction="' + header.data('sort-direction') + '"]').show();
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
        const availableQuantity = $(this).data('quantity');
        $('#buyModal').show();
        $('#overlay').show();
        $('#companyName').text(company);
        $('#quantityInput').attr('max', availableQuantity).val(1);
    });

    // $(document).on('click', '.max-set', function () {
    //     const availableQuantity = $(this).data('quantity');
    //     $('#quantityInput').val(availableQuantity);
    // });

    $('.max-set').click(function () {
        $('#quantityInput').val($('#quantityInput').attr('max'));
    });

    $('#closeModal').click(function () {
        $('#buyModal').hide();
        $('#overlay').hide();
    });

    $('#buyForm').submit(function (event) {
        event.preventDefault();
        $('#buyModal').hide();
        $('#overlay').hide();
        alert('Stock purchased successfully!');
    });
});